/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.util.*;

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
        this.stockMonitors = new java.util.ArrayList<StockMonitor>();
        
        stockCodesReadWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        stockCodesReaderLock = stockCodesReadWriteLock.readLock();
        stockCodesWriterLock = stockCodesReadWriteLock.writeLock();        
    }
    
    public synchronized boolean setStockServerFactories(java.util.List<StockServerFactory> factories) {
        stockServerFactories.clear();
        return stockServerFactories.addAll(factories);
    }

    public synchronized StockServerFactory getStockServerFactory(int index) {
        return stockServerFactories.get(index);
    }
    
    public synchronized int getNumOfStockServerFactory() {
        return stockServerFactories.size();
    }
    
    public synchronized boolean removeStockServerFactory(StockServerFactory factory) {
        return stockServerFactories.remove(factory);
    }
    
    // synchronized, to avoid addStockCode and removeStockCode at the same time.
    // This method will start all the monitoring threads automatically.
    public synchronized boolean addStockCode(Code code) {
        // Lock isn't required here. This is because increase the size of the 
        // list is not going to get IndexOutOfBoundException.
        if (stockCodes.contains(code)) {
            return false;
        }

        boolean status = stockCodes.add(code);

        start();

        return status;
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
        stockCodes.clear();
        stockCodesWriterLock.unlock();   
        
        while(stockMonitors.size() > 0) {
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
                log.error("", exp);
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
        boolean status = stockCodes.remove(code);
        stockCodesWriterLock.unlock();

        // Do we need to remove any old thread?
        final int numOfMonitorRequired = this.getNumOfRequiredThread();

        if(this.stockMonitors.size() > numOfMonitorRequired) {
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
                log.error("", exp);
            }
             */
            
            log.info("After removing : current thread size=" + this.stockMonitors.size() + ",numOfMonitorRequired=" + numOfMonitorRequired);
        }

        return status;
    }    
    
    public synchronized void softStart() {
        for (StockMonitor stockMonitor : stockMonitors) {
            stockMonitor.softStart();
        }
    }

    public synchronized void softStop() {
        for (StockMonitor stockMonitor : stockMonitors) {
            stockMonitor.softStop();
        }
    }    
    
    public synchronized void start() {
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
        private volatile Status status = Status.Normal;
    
        public StockMonitor(int index) {
            this.index = index;
            thread = this;
        }
        
        private synchronized void softWait() throws InterruptedException {
            synchronized (this) {
                if (status == Status.Pause) {
                    while (status != Status.Resume) {
                        wait();
                    }

                    status = Status.Normal;
                }
            }
        }

        public synchronized void softStart() {
            if (status == Status.Pause) {
                status = Status.Resume;
                notify();
            }
        }

        public synchronized void softStop() {
            status = Status.Pause;
            notify();
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
                            softWait();
                        }
                        catch (InterruptedException exp) {
                            log.error(null, exp);
                            /* Exit the primary fail safe loop. */
                            thread = null;
                            break;
                        }

                        for (int currIndex = index; thisThread == thread; currIndex += step) {
                            ListIterator<Code> listIterator = null;

                            // Acquire iterator in a safe way.
                            stockCodesReaderLock.lock();
                            final int stockCodesSize = stockCodes.size();
                            if(currIndex < stockCodesSize) {
                                listIterator = stockCodes.listIterator(currIndex);
                            }
                            stockCodesReaderLock.unlock();

                            if (listIterator != null) {
                                List<Code> codes = new ArrayList<Code>();

                                for (int i = 0; listIterator.hasNext() && i < numOfStockPerIteration && thisThread == thread; i++) {
                                    codes.add(listIterator.next());
                                }

                                for (StockServerFactory factory : stockServerFactories)
                                {
                                    final StockServer stockServer = factory.getStockServer();

                                    List<Stock> stocks = null;
                                    try {
                                        stocks = stockServer.getStocksByCodes(codes);
                                    }
                                    catch (StockNotFoundException exp) {
                                        log.error(codes, exp);
                                        // Try with another server.
                                        continue;
                                    }

                                    if (thisThread != thread) {
                                        break;
                                    }

                                    // Notify all the interested parties.
                                    RealTimeStockMonitor.this.notify(RealTimeStockMonitor.this, stocks);

                                    break;
                                }   // for

                            }   // if (listIterator != null)
                            else {
                                break;
                            }
                        }   // for (int currIndex = index; thisThread == thread; curIndex += step)

                        try {
                            Thread.sleep(delay);
                        }
                        catch (java.lang.InterruptedException exp) {
                            log.error("index=" + index, exp);
                            /* Exit the primary fail safe loop. */
                            thread = null;                            
                            break;
                        }
                    }   /*  while (thisThread == thread) */
                }
                catch (Exception exp) {
                    log.error("Our thread just recover from unexpected error", exp);
                }	/* try */
            }	/* while (thisThread == thread) */
        }
        
        public void _stop() {
            thread = null;
            // Wake up from sleep.
            interrupt();
        }
        
        private final int index;
        private volatile Thread thread;
    }
    
    private enum Status {
        Pause,
        Resume,
        Normal
    };
        
    // Delay in ms
    private volatile long delay;
    private final int maxThread;
    // Number of stock to be monitored per iteration.
    private final int numOfStockPerIteration;
    private java.util.List<StockServerFactory> stockServerFactories;
    private java.util.List<Code> stockCodes;
    private java.util.List<StockMonitor> stockMonitors;
    private final java.util.concurrent.locks.ReadWriteLock stockCodesReadWriteLock;
    private final java.util.concurrent.locks.Lock stockCodesReaderLock;
    private final java.util.concurrent.locks.Lock stockCodesWriterLock;
    
    private static final Log log = LogFactory.getLog(RealTimeStockMonitor.class);    
}
