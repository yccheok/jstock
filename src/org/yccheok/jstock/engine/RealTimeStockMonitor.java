/*
 * RealTimeStockMonitor.java
 *
 * Created on April 24, 2007, 9:56 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
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
        if(maxThread <= 0 || numOfStockPerIteration <= 0 || delay <= 0) {
            throw new IllegalArgumentException("maxThread=" + maxThread + ",numOfStockPerIteration=" + numOfStockPerIteration + ",delay=" + delay);
        }
        
        this.maxThread = maxThread;
        this.numOfStockPerIteration = numOfStockPerIteration;
        this.delay = delay;
        
        this.stockServerFactories = new java.util.concurrent.CopyOnWriteArrayList<StockServerFactory>();
        this.stockCodes = new java.util.concurrent.CopyOnWriteArrayList<String>();
        this.stockMonitors = new java.util.ArrayList<StockMonitor>();
        
        stockCodesReadWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        stockCodesReaderLock = stockCodesReadWriteLock.readLock();
        stockCodesWriterLock = stockCodesReadWriteLock.writeLock();        
    }
    
    public synchronized boolean addStockServerFactory(StockServerFactory factory) {
        return stockServerFactories.add(factory);
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
    public synchronized boolean addStockCode(String code) {
        // Lock isn't required here. This is because increase the size of the 
        // list is not going to get IndexOutOfBoundException.
        if(stockCodes.contains(code)) return false;

        boolean status = stockCodes.add(code);

        start();

        return status;
    }
    
    public synchronized int getNumOfStockCode() {
        return stockCodes.size();
    }

    public synchronized String getStockCode(int index) {
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
            try {
                stockMonitor.join();
            }
            catch(java.lang.InterruptedException exp) {
                log.error("", exp);
            }            
        }
        
        return true;
    }
    
    // synchronized, to avoid addStockCode and removeStockCode at the same time
    public synchronized boolean removeStockCode(String code) {
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
            try {
                stockMonitor.join();
            }
            catch(java.lang.InterruptedException exp) {
                log.error("", exp);
            }
            
            log.info("After removing : current thread size=" + this.stockMonitors.size() + ",numOfMonitorRequired=" + numOfMonitorRequired);
        }

        return status;
    }    
    
    public synchronized void start() {
        // Do we need to remove any old thread?
        final int numOfMonitorRequired = this.getNumOfRequiredThread();
        
        assert(numOfMonitorRequired <= this.maxThread);
        
        for(int i=this.stockMonitors.size(); i<numOfMonitorRequired; i++) {
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
        for(StockMonitor stockMonitor : stockMonitors) {
            stockMonitor._stop();
            
            try {
                stockMonitor.join();
            }
            catch(java.lang.InterruptedException exp) {
                log.error("", exp);
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
        public StockMonitor(int index) {
            this.index = index;
            thread = this;
        }
        
        public void run() {
            final Thread thisThread = Thread.currentThread();
            
            /* Will advance by numOfStockPerIteration * maxThread */            
            final int step = numOfStockPerIteration * maxThread;
            
            while(thisThread == thread) {
                
                for(int currIndex = index; thisThread == thread; currIndex += step) {
                    ListIterator<String> listIterator = null;

                    // Acquire iterator in a safe way.
                    stockCodesReaderLock.lock();                
                    final int stockCodesSize = stockCodes.size();
                    if(currIndex < stockCodesSize)
                        listIterator = stockCodes.listIterator(currIndex);
                    stockCodesReaderLock.unlock();

                    if(listIterator != null) {
                        List<String> codes = new ArrayList<String>();

                        for(int i = 0; listIterator.hasNext() && i < numOfStockPerIteration && thisThread == thread; i++) {
                            codes.add(listIterator.next());
                        }

                        for(StockServerFactory factory : stockServerFactories)
                        {
                            final StockServer stockServer = factory.getStockServer();
                            
                            List<Stock> stocks = null;
                            try {
                                stocks = stockServer.getStockByCode(codes);                                
                            }
                            catch(StockNotFoundException exp) {
                                log.error(codes, exp);
                                // Try with another server.
                                continue;
                            }
                            
                            // Notify all the interested parties.
                            RealTimeStockMonitor.this.notify(RealTimeStockMonitor.this, stocks);
                            
                            break;
                        }   // for                    

                    }   // if(listIterator != null)
                    else {
                        break;
                    }
                }   // for(int currIndex = index; thisThread == thread; curIndex += step)
                
                try {
                    Thread.sleep(delay);
                }
                catch(java.lang.InterruptedException exp) {
                    log.error("index=" + index, exp);
                    break;
                }
            }
        }
        
        public void _stop() {
            thread = null;
            // Wake up from sleep.
            interrupt();
        }
        
        private final int index;
        private volatile Thread thread;
    }
    
    // Delay in ms
    private volatile long delay;
    private final int maxThread;
    // Number of stock to be monitored per iteration.
    private final int numOfStockPerIteration;
    private java.util.List<StockServerFactory> stockServerFactories;
    private java.util.List<String> stockCodes;
    private java.util.List<StockMonitor> stockMonitors;
    private final java.util.concurrent.locks.ReadWriteLock stockCodesReadWriteLock;
    private final java.util.concurrent.locks.Lock stockCodesReaderLock;
    private final java.util.concurrent.locks.Lock stockCodesWriterLock;
    
    private static final Log log = LogFactory.getLog(RealTimeStockMonitor.class);    
}
