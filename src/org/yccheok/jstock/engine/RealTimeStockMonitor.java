/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.yccheok.jstock.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class RealTimeStockMonitor extends Subject<RealTimeStockMonitor, java.util.List<Stock>> {
    
    /** Creates a new instance of RealTimeStockMonitor */
    public RealTimeStockMonitor(int maxThread, int numOfStockPerIteration, long delay) {
        if (maxThread <= 0 || numOfStockPerIteration <= 0 || delay <= 0) {
            throw new IllegalArgumentException("maxThread : " + maxThread + ", numOfStockPerIteration : " + numOfStockPerIteration + ", delay : " + delay);
        }
        
        this.maxThread = maxThread;
        this.numOfStockPerIteration = numOfStockPerIteration;
        this.delay = delay;
        
        this.stockServerFactories = new java.util.concurrent.CopyOnWriteArrayList<StockServerFactory>();
        this.stockCodes = new java.util.concurrent.CopyOnWriteArrayList<Code>();
        this.rowStockCodeMapping = new HashMap<Code, Integer>();
        this.stockMonitors = new java.util.ArrayList<StockMonitor>();
        
        stockCodesReadWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        stockCodesReaderLock = stockCodesReadWriteLock.readLock();
        stockCodesWriterLock = stockCodesReadWriteLock.writeLock();        
    }
    
    public synchronized void setStockServerFactories(java.util.List<StockServerFactory> factories) {
        // Do not use deep copy. If not, Factories's removeKLSEInfoStockServerFactory 
        // effect won't propagate to here.
        //stockServerFactories.clear();
        //return stockServerFactories.addAll(factories);
        stockServerFactories = factories;
    }
    
    // synchronized, to avoid addStockCode and removeStockCode at the same time.
    // This method will start all the monitoring threads automatically.
    public synchronized boolean addStockCode(Code code) {
        // Lock isn't required here. This is because increase the size of the 
        // list is not going to get IndexOutOfBoundException.
        if (rowStockCodeMapping.containsKey(code)) {
            return false;
        }

        boolean status = stockCodes.add(code);
        rowStockCodeMapping.put(code, stockCodes.size() - 1);
        
        return status;
    }

    /**
     * Returns true if there is no stock code in this monitor.
     * 
     * @return true if there is no stock code in this monitor
     */
    public synchronized boolean isEmpty() {
        return stockCodes.isEmpty();
    }

    public synchronized int getNumOfStockCode() {
        return stockCodes.size();
    }

    public synchronized Code getStockCode(int index) {
        return stockCodes.get(index);
    }
    
    public synchronized boolean clearStockCodes() {
        // This is to ensure we are able to get the correct StockCodes size,
        // and able to retrieve Iterator in a safe way without getting 
        // IndexOutOfBoundException.
        stockCodesWriterLock.lock();
        try {
            stockCodes.clear();
            rowStockCodeMapping.clear();
        } finally {
            stockCodesWriterLock.unlock();
        }
        
        while (stockMonitors.size() > 0) {
            StockMonitor stockMonitor = stockMonitors.remove(stockMonitors.size() - 1);
            stockMonitor._stop();
            /*
             * Unlike stop(), no need to explicitly wait for the thread to dead. Let it dead
             * naturally. However, is it safe to do so?
             *            
            try {
                stockMonitor.join();
            }
            catch(java.lang.InterruptedException exp) {
                log.error(null, exp);
            } 
             */           
        }
        
        return true;
    }
    
    // synchronized, to avoid addStockCode and removeStockCode at the same time
    public synchronized boolean removeStockCode(Code code) {
        // This is to ensure we are able to get the correct StockCodes size,
        // and able to retrieve Iterator in a safe way without getting 
        // IndexOutOfBoundException.
        stockCodesWriterLock.lock();
        boolean status = false;
        try {
            Integer row = rowStockCodeMapping.get(code);
            if (row == null) {
                return status;
            }
            status = (null != stockCodes.remove((int)row));
            rowStockCodeMapping.remove(code);
        } finally {
            stockCodesWriterLock.unlock();
        }

        // Do we need to remove any old thread?
        final int numOfMonitorRequired = this.getNumOfRequiredThread();

        if (this.stockMonitors.size() > numOfMonitorRequired) {
            log.info("After removing : current thread size=" + this.stockMonitors.size() + ",numOfMonitorRequired=" + numOfMonitorRequired);

            StockMonitor stockMonitor = stockMonitors.remove(stockMonitors.size() - 1);
            stockMonitor._stop();
            
            /*
             * Unlike stop(), no need to explicitly wait for the thread to dead. Let it dead
             * naturally. However, is it safe to do so?
             * 
            try {
                stockMonitor.join();
            }
            catch(java.lang.InterruptedException exp) {
                log.error(null, exp);
            }
             */
            
            log.info("After removing : current thread size=" + this.stockMonitors.size() + ",numOfMonitorRequired=" + numOfMonitorRequired);
        }

        return status;
    }    
    
    public synchronized void resume() {
        for (StockMonitor stockMonitor : stockMonitors) {
            stockMonitor._resume();
        }
    }

    public synchronized void suspend() {
        for (StockMonitor stockMonitor : stockMonitors) {
            stockMonitor._suspend();
        }
    }    
    
    // Previously, this is a private method, and is being called automatically
    // just after addCode. However, there's a problem when
    //
    // 1) I have 17 codes to be added
    // 2) After I add the first code, thread started.
    // 3) The thread thoughts only 1 code added, and performs server query.
    // 4) Rest of the 16 codes are added then
    // 5) Perform refresh() call from external.
    // 6) 1 code result returned. The thread enters long sleep since everything
    //    is success.
    //
    // To avoid such problem, we will only start new thread explicitly, after we
    // have added all the codes. We will no longer start new thread, in the
    // middle of adding new code.
    public synchronized void startNewThreadsIfNecessary() {
        // Do we need to remove any old thread?
        final int numOfMonitorRequired = this.getNumOfRequiredThread();
        
        assert(numOfMonitorRequired <= this.maxThread);
        
        for (int i = this.stockMonitors.size(); i < numOfMonitorRequired; i++) {
            log.info("Before adding : current thread size=" + this.stockMonitors.size() + ",numOfMonitorRequired=" + numOfMonitorRequired);
            
            StockMonitor stockMonitor = new StockMonitor(i * numOfStockPerIteration);
            stockMonitors.add(stockMonitor);
            stockMonitor.start();
            
            log.info("After adding : current thread size=" + this.stockMonitors.size() + ",numOfMonitorRequired=" + numOfMonitorRequired);
        }
    }
    
    // Stop all the monitoring thread. Once this had been stopped, all the
    // previous monitoring thread will be removed.
    public synchronized void stop() {
        for (StockMonitor stockMonitor : stockMonitors) {
            stockMonitor._stop();
            
            try {
                stockMonitor.join();
            }
            catch (java.lang.InterruptedException exp) {
                log.error(null, exp);
            }            
        }
        
        stockMonitors.clear();
    }
    
    // Trigger stock monitor to fetch stock price immediately. Although I am not
    // sure whether we should make this method as synchronized, but it should
    // be no harm to do so. Performance shouldn't be an issue, as this is not a
    // frequent accessed method.
    public synchronized void refresh() {
        for (StockMonitor stockMonitor : stockMonitors) {
            stockMonitor.refresh();
        }     
    }
    
    public synchronized long getDelay() {
        return this.delay;
    }
    
    public synchronized void setDelay(int delay) {
        this.delay = delay;
    }
    
    private int getNumOfRequiredThread() {
        final int numOfThreadRequired = 
            (stockCodes.size() / numOfStockPerIteration) + 
            (((stockCodes.size() % numOfStockPerIteration) == 0) ? 0 : 1);
        
        return Math.min(numOfThreadRequired, maxThread);
    }
    
    private class StockMonitor extends Thread {
        
        public StockMonitor(int index) {
            this.index = index;
            thread = this;
        }

        // Use name with underscore, in order to avoid naming crashing with
        // Thread's.
        private synchronized void _wait() throws InterruptedException {
            while (suspend) {
                wait();
            }
        }

        // Use name with underscore, in order to avoid naming crashing with
        // Thread's.
        public synchronized void _resume() {
            suspend = false;
            notify();
        }

        // Use name with underscore, in order to avoid naming crashing with
        // Thread's.
        public synchronized void _suspend() {
            suspend = true;
        }    
        
        @Override
        public void run() {
            final Thread thisThread = Thread.currentThread();
            
            /* Will advance by numOfStockPerIteration * maxThread */            
            final int step = numOfStockPerIteration * maxThread;


            while (thisThread == thread) {                
                // Fail safe. So that middle in the code, if there is a unexpected exception being thrown, our thread
                // still remain alive. Take note that, entering exception block for each loop is expensive. Hence, we have double
                // loop strategy.
                try {
                    while (thisThread == thread) {
                        try {
                            _wait();
                        } catch (InterruptedException exp) {
                            log.error(null, exp);
                            // If this monitor is suspended, refresh will not work, unless
                            // resume is being called first. This is for simplicity purpose.
                            // During _wait(), when InterruptedException being thrown, I have
                            // no idea whether it is caused by thread termination, or refresh.
                            // By placing "if (suspend)" constraint, I can more or less sure
                            // InterruptedException during _wait() is caused by thread
                            // termination. But, I can never 100% sure after resume() is called,
                            // the thread is returned from wait(). So, problem still arise,
                            // if the sequence is _resume() -> refresh() -> returned from _wait().
                            // That's why we have "if (isRefresh == false)" check in exception
                            // handling.
                            if (isRefresh == false) {
                                /* Exit the primary fail safe loop. */
                                thread = null;
                                break;
                            }
                        }

                        int pass = 0;
                        int fail = 0;
                        
                        for (int currIndex = index; thisThread == thread; currIndex += step) {
                            ListIterator<Code> listIterator = null;

                            // Acquire iterator in a safe way.
                            stockCodesReaderLock.lock();
                            try {
                                final int stockCodesSize = stockCodes.size();
                                if (currIndex < stockCodesSize) {
                                    listIterator = stockCodes.listIterator(currIndex);
                                }
                            } finally {
                                stockCodesReaderLock.unlock();
                            }

                            if (listIterator != null) {
                                List<Code> codes = new ArrayList<Code>();

                                for (int i = 0; listIterator.hasNext() && i < numOfStockPerIteration && thisThread == thread; i++) {
                                    codes.add(listIterator.next());
                                }

                                final int size = codes.size();
                                fail += size;
                                for (StockServerFactory factory : stockServerFactories)
                                {
                                    final StockServer stockServer = factory.getStockServer();

                                    List<Stock> stocks = null;
                                    try {
                                        stocks = stockServer.getStocks(codes);
                                    } catch (StockNotFoundException exp) {
                                        log.error(codes, exp);
                                        // Try with another server.
                                        continue;
                                    }

                                    if (thisThread != thread) {
                                        break;
                                    }

                                    pass += size;
                                    fail -= size;
                                    totalScanned = pass + fail;

                                    // Notify all the interested parties.
                                    RealTimeStockMonitor.this.notify(RealTimeStockMonitor.this, stocks);

                                    break;
                                }   // for

                            }   // if (listIterator != null)
                            else {
                                break;
                            }
                        }   // for (int currIndex = index; thisThread == thread; curIndex += step)

                        totalScanned = pass + fail;

                        try {
                            if (fail == 0) {
                                Thread.sleep(delay);
                            } else {
                                if (minDelayCounter < MIN_DELAY_COUNTER) {
                                    // Sleep as little as possible, to get the 1st reading
                                    // as soon as possible. MIN_DELAY_COUNTER is used to avoid
                                    // from getting our CPU and network too busy.
                                    minDelayCounter++;
                                    Thread.sleep(MIN_DELAY);
                                } else {
                                    Thread.sleep(delay);
                                }
                            }
                        } catch (java.lang.InterruptedException exp) {
                            log.error("index=" + index, exp);
                            if (isRefresh == false) {
                                /* Exit the primary fail safe loop. */
                                thread = null;                            
                                break;
                            } else {
                                // User has requested to perform refresh explicitly.
                                // Clear isRefresh flag, and continue with rest
                                // of the work.
                                isRefresh = false;
                            }
                        }
                    }   /*  while (thisThread == thread) */
                }
                catch (Exception exp) {
                    log.error("Our thread just recover from unexpected error", exp);
                }   /* try */
            }	/* while (thisThread == thread) */
        }
        
        // Need synchronized, to ensure refresh is mutually exclusive with
        // _wait.
        public synchronized void refresh() {
            if (suspend) {
                // If this monitor is suspended, refresh will not work, unless
                // resume is being called first. This is for simplicity purpose.
                // During _wait(), when InterruptedException being thrown, I have
                // no idea whether it is caused by thread termination, or refresh.
                // By placing "if (suspend)" constraint, I can more or less sure
                // InterruptedException during _wait() is caused by thread
                // termination. But, I can never 100% sure after resume() is called,
                // the thread is returned from wait(). So, problem still arise,
                // if the sequence is _resume() -> refresh() -> returned from _wait().
                // That's why we have "if (isRefresh == false)" check in exception
                // handling.
                return;
            }
            
            isRefresh = true;
            minDelayCounter = 0;
            totalScanned = 0;
            interrupt();
        }
        
        public void _stop() {
            thread = null;
            // Wake up from sleep.
            interrupt();
        }
        
        public int getTotalScanned() {
            return totalScanned;
        }
        
        // Doesn't require volatile, as this variable is being accessed within
        // synchronized block.
        private boolean suspend = false;        
        private volatile boolean isRefresh = false;
        private volatile int minDelayCounter = 0;
        private volatile int totalScanned = 0;
        private final int index;
        private volatile Thread thread;
    }
        
    public int getTotalScanned() {
        int totalScanned = 0;
        for (StockMonitor stockMonitor : stockMonitors) {
            totalScanned += stockMonitor.getTotalScanned();
        }
        return totalScanned;
    }
    
    // Delay in ms
    private volatile long delay;
    
    // 5 seconds.
    private static final long MIN_DELAY = 5000;
    private static final long MIN_DELAY_COUNTER = 3;
    
    private final int maxThread;
    // Number of stock to be monitored per iteration.
    private final int numOfStockPerIteration;
    private java.util.List<StockServerFactory> stockServerFactories;
    private final java.util.List<Code> stockCodes;
    // Used for duplication check. We avoid using stockCodes.constains, as the
    // complexity is O(n).
    private final java.util.HashMap<Code, Integer> rowStockCodeMapping;
    private final java.util.List<StockMonitor> stockMonitors;
    private final java.util.concurrent.locks.ReadWriteLock stockCodesReadWriteLock;
    private final java.util.concurrent.locks.Lock stockCodesReaderLock;
    private final java.util.concurrent.locks.Lock stockCodesWriterLock;
    
    private static final Log log = LogFactory.getLog(RealTimeStockMonitor.class);    
}
