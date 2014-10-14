/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2014 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class RealTimeStockMonitor extends Subject<RealTimeStockMonitor, java.util.List<Stock>> {
 
    /** Creates a new instance of RealTimeStockMonitor */
    public RealTimeStockMonitor(int maxThread, int maxBucketSize, long delay) {
        if (maxThread <= 0 || maxBucketSize <= 0 || delay <= 0) {
            throw new IllegalArgumentException("maxThread : " + maxThread + ", maxBucketSize : " + maxBucketSize + ", delay : " + delay);
        }
        
        this.maxThread = maxThread;
        this.delay = delay;
        
        this.codeBucketLists = new CodeBucketLists(maxBucketSize);
        
        this.stockMonitors = new java.util.ArrayList<StockMonitor>();
    }
    
    // synchronized, to avoid addStockCode and removeStockCode at the same time.
    public synchronized boolean addStockCode(Code code) {
        return codeBucketLists.add(code);
    }

    /**
     * Returns true if there is no stock code in this monitor.
     * 
     * @return true if there is no stock code in this monitor
     */
    public synchronized boolean isEmpty() {
        return codeBucketLists.isEmpty();
    }

    public synchronized boolean clearStockCodes() {
        codeBucketLists.clear();
        
        while (stockMonitors.size() > 0) {
            StockMonitor stockMonitor = stockMonitors.remove(stockMonitors.size() - 1);
            stockMonitor._stop();          
        }
        
        return true;
    }
    
    // synchronized, to avoid addStockCode and removeStockCode at the same time
    public synchronized boolean removeStockCode(Code code) {
        boolean status = codeBucketLists.remove(code);

        // Do we need to remove any old thread?
        final int numOfMonitorRequired = this.getNumOfRequiredThread();

        if (this.stockMonitors.size() > numOfMonitorRequired) {            
            StockMonitor stockMonitor = stockMonitors.remove(stockMonitors.size() - 1);
            stockMonitor._stop();
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
            
            StockMonitor stockMonitor = new StockMonitor(i);
            stockMonitors.add(stockMonitor);
            stockMonitor.start();
            
            log.info("After adding : current thread size=" + this.stockMonitors.size() + ",numOfMonitorRequired=" + numOfMonitorRequired);
        }
    }
    
    public synchronized void rebuild() {
        this.codeBucketLists.rebuild();
                
        this.startNewThreadsIfNecessary();
        
        final int numOfMonitorRequired = this.getNumOfRequiredThread();
        
        assert(numOfMonitorRequired <= this.maxThread);
        
        while (this.stockMonitors.size() > numOfMonitorRequired) {
            StockMonitor stockMonitor = stockMonitors.remove(stockMonitors.size() - 1);
            stockMonitor._stop();
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
        final int numOfThreadRequired = codeBucketLists.size();
        
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
            
            /* Will advance by maxThread */            
            final int step = maxThread;


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
                            final List<Code> codes = codeBucketLists.get(currIndex);

                            if (codes.isEmpty()) {
                                break;
                            }
                            
                            final int size = codes.size();
                            fail += size;
                            
                            List<Code> zeroPriceCodes = codes;
                            final Set<Code> nonZeroPriceCodes = new HashSet<Code>();
                            final List<Stock> stocks = new ArrayList<Stock>();
                            
                            for (StockServerFactory factory : Factories.INSTANCE.getStockServerFactories(codes.get(0)))
                            {
                                final StockServer stockServer = factory.getStockServer();

                                if (stockServer == null) {
                                    continue;
                                }

                                List<Stock> tmpStocks = null;
                                try {
                                    tmpStocks = stockServer.getStocks(zeroPriceCodes);
                                } catch (StockNotFoundException exp) {
                                    if (thisThread != thread) {
                                        break;    
                                    }

                                    log.error(zeroPriceCodes, exp);
                                    // Try with another server.
                                    continue;
                                }

                                if (thisThread != thread) {
                                    break;
                                }

                                zeroPriceCodes = getZeroPriceCodes(tmpStocks, stocks, nonZeroPriceCodes);
                                if (zeroPriceCodes.isEmpty()) {
                                    break;
                                }
                            }   /* for (StockServerFactory factory : Factories.INSTANCE.getStockServerFactories(codes.get(0))) */

                            // Notify all the interested parties.
                            if (thisThread == thread && nonZeroPriceCodes.isEmpty() == false) {
                                if (stocks.size() < codes.size()) {
                                    for (Code code : codes) {
                                        if (false == nonZeroPriceCodes.contains(code)) {
                                            stocks.add(org.yccheok.jstock.engine.Utils.getEmptyStock(code, Symbol.newInstance(code.toString())));
                                        }
                                    }
                                }
                                
                                pass += size;
                                fail -= size;
                                totalScanned = pass + fail;  
                                
                                RealTimeStockMonitor.this.notify(RealTimeStockMonitor.this, stocks);
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
                }   /* try */
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
    
    private List<Code> getZeroPriceCodes(List<Stock> stocks, List<Stock> nonZeroPriceStocks, Set<Code> nonZeroPriceCodes) {
        assert(stocks != null);
        assert(nonZeroPriceStocks != null);
        assert(nonZeroPriceCodes != null);
        
        List<Code> zeroPriceCodes = null;
        
        for (Stock stock : stocks) {
            if (nonZeroPriceCodes.contains(stock.code)) {
                continue;
            }
                
            if (stock.getLastPrice() == 0.0 && stock.getOpenPrice() == 0.0) {
                if (zeroPriceCodes == null) {
                    zeroPriceCodes = new ArrayList<Code>();
                }
                
                zeroPriceCodes.add(stock.code);
            } else {
                nonZeroPriceCodes.add(stock.code);
                nonZeroPriceStocks.add(stock);
            }
        }
        
        if (zeroPriceCodes == null) {
            return java.util.Collections.emptyList();
        }
        
        return zeroPriceCodes;
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

    private final CodeBucketLists codeBucketLists;
    
    private final java.util.List<StockMonitor> stockMonitors;
    
    private static final Log log = LogFactory.getLog(RealTimeStockMonitor.class);    
}
