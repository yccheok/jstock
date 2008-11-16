/*
 * StockHistoryMonitor.java
 *
 * Created on May 1, 2007, 10:46 PM
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

import java.util.concurrent.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class StockHistoryMonitor extends Subject<StockHistoryMonitor, StockHistoryMonitor.StockHistoryRunnable> {
    
    /** Creates a new instance of StockHistoryMonitor */
    public StockHistoryMonitor(int nThreads) {
        // Default database size is 10.
        this(nThreads, 10);
    }

    public StockHistoryMonitor(int nThreads, int databaseSize) {
        pool = Executors.newFixedThreadPool(nThreads);
        
        stockCodesReadWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        stockCodesReaderLock = stockCodesReadWriteLock.readLock();
        stockCodesWriterLock = stockCodesReadWriteLock.writeLock();        
        
        this.databaseSize = databaseSize;
        
        this.stockHistorySerializer = null;
    }
    
    public synchronized boolean addStockServerFactory(StockServerFactory factory) {
        return factories.add(factory);
    }
    
    public synchronized int getNumOfStockServerFactory() {
        return factories.size();
    }
    
    public synchronized boolean removeStockServerFactory(StockServerFactory factory) {
        return factories.remove(factory);
    }

    public synchronized StockServerFactory getStockServerFactory(int index) {
        return factories.get(index);
    }
    
    public synchronized boolean addStockCode(final Code code) {
        stockCodesWriterLock.lock();
        
        if(stockCodes.contains(code)) {
            stockCodesWriterLock.unlock();
            return false;            
        }
        
        boolean status = stockCodes.add(code);
        stockCodesWriterLock.unlock();
        
        pool.execute(new StockHistoryRunnable(code));

        return status;
    }
    
    public class StockHistoryRunnable implements Runnable {
        public StockHistoryRunnable(Code code) {
            this.code = code;
            this.historyServer = null;
        }
        
        public void run() {
            final Thread currentThread = Thread.currentThread();
            
            for(StockServerFactory factory : factories) {
                StockHistoryServer history = factory.getStockHistoryServer(this.code);

                if(history != null) {
                    stockCodesReaderLock.lock();
                    
                    // Anyone try to stop us from publishing this history?
                    if(stockCodes.contains(code)) {
                        this.historyServer = history;
                        
                        if(histories.size() < StockHistoryMonitor.this.databaseSize) {                            
                            histories.put(code, history);
                        }
                        else {
                            if(StockHistoryMonitor.this.stockHistorySerializer != null) {
                                StockHistoryMonitor.this.stockHistorySerializer.save(history);
                            }
                            else {
                                log.error("Fail to perform serialization on stock history due to uninitialized serialization component.");
                            }
                        }
                    }
                    
                    stockCodesReaderLock.unlock();                                        
                    
                    break;
                }
               
                if(currentThread.isInterrupted())
                    break;
            }
            
            if(historyServer == null) {
                stockCodesWriterLock.lock();
        
                stockCodes.remove(code);
        
                stockCodesWriterLock.unlock();                
            }
            
            // We need to notify the listener. Whether the history is success or
            // fail.
            StockHistoryMonitor.this.notify(StockHistoryMonitor.this, this);
        }
    
        public int hashCode() {
            return code.hashCode();
        }

        public boolean equals(Object o) {
            if(!(o instanceof StockHistoryRunnable))
                return false;

            StockHistoryRunnable stockHistoryRunnable = (StockHistoryRunnable)o;

            return this.code.equals(stockHistoryRunnable.code);
        }
        
        public String toString() {
            return StockHistoryRunnable.class.getName() + "[code=" + code + "]";
        }
    
        public Code getCode() {
            return code;
        }

        public StockHistoryServer getStockHistoryServer() {
            return historyServer;
        }
        
        private final Code code;
        private StockHistoryServer historyServer;
    }
    
    public synchronized void clearStockCodes() {
        final ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor)pool);        
        final int nThreads = threadPoolExecutor.getMaximumPoolSize();        
        
        stockCodesWriterLock.lock();
        
        stockCodes.clear();
        
        histories.clear();
        
        stockCodesWriterLock.unlock();

        threadPoolExecutor.shutdownNow();  
        
        // pool is not valid any more. Discard it and re-create.
        pool = Executors.newFixedThreadPool(nThreads);        
    }
    
    // synchronized, to avoid addStockCode and removeStockCode at the same time
    public synchronized boolean removeStockCode(Code code) {
        stockCodesWriterLock.lock();
        
        boolean status = stockCodes.remove(code);
        
        histories.remove(code);
        
        stockCodesWriterLock.unlock();

        ((ThreadPoolExecutor)pool).remove(new StockHistoryRunnable(code));
        
        return status;
    }
    
    public synchronized StockHistoryServer getStockHistoryServer(Code code) {
        if(histories.containsKey(code))
            return histories.get(code);
        else {
            if(StockHistoryMonitor.this.stockHistorySerializer != null) {
                StockHistoryServer stockHistoryServer = StockHistoryMonitor.this.stockHistorySerializer.load(code);
                
                /* So that next time we won't read from the disk. */
                if(stockHistoryServer != null && (this.databaseSize > histories.size())) {
                    histories.put(code, stockHistoryServer);
                }
                
                return stockHistoryServer;
            }
            else {
                log.error("Fail to retrieve stock history due to uninitialized serialization component.");                
            }
        }
        
        return null;
    }

    public synchronized int getNumOfStockHistoryServer() {
        return histories.size();
    }
    
    public synchronized Set<Code> getCodes() {
        return histories.keySet();
    }
    
    public void setStockHistorySerializer(StockHistorySerializer stockHistorySerializer) {
        this.stockHistorySerializer = stockHistorySerializer;
    }
    
    // Never export out stockCodes information. They are being used internally.
    
    public synchronized void stop() {
        final ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor)pool);
        final int nThreads = threadPoolExecutor.getMaximumPoolSize();
        
        // Dangerous. Some users, do expect receive callback once they submit tasks into
        // monitor. However, if we are calling shutdownNow, user may not receive any
        // callback from those submitted tasks, which haven't started yet. Calling
        // shutdown() enables submitted tasks have chances to run once.
        //
        // threadPoolExecutor.shutdownNow();
        threadPoolExecutor.shutdown();
        threadPoolExecutor.purge();
        // How to wait for infinity?
        try {
            threadPoolExecutor.awaitTermination(100, TimeUnit.DAYS);
        }
        catch(InterruptedException exp) {
            log.error("", exp);
        }

        // pool is not valid any more. Discard it and re-create.
        pool = Executors.newFixedThreadPool(nThreads);
    }
    
    private java.util.List<StockServerFactory> factories = new java.util.concurrent.CopyOnWriteArrayList<StockServerFactory>();
    // Use internally. To ensure when we "remove" a code, we really remove it, even there is a runnable
    // in the middle of retrieving history information from the server.
    private java.util.List<Code> stockCodes = Collections.synchronizedList(new java.util.ArrayList<Code>());
    // We need to use concurrent map, due to the fact we need to export out the key set of this map.
    // We need to let the client have a safe way to iterate through the key set.
    private java.util.Map<Code, StockHistoryServer> histories = new java.util.concurrent.ConcurrentHashMap<Code, StockHistoryServer>();    
    private final java.util.concurrent.locks.ReadWriteLock stockCodesReadWriteLock;
    private final java.util.concurrent.locks.Lock stockCodesReaderLock;
    private final java.util.concurrent.locks.Lock stockCodesWriterLock;
    
    private Executor pool;
    
    // Prevent out of memory problem.
    private final int databaseSize;
    
    private StockHistorySerializer stockHistorySerializer;
    
    private static final Log log = LogFactory.getLog(StockHistoryMonitor.class);     
}
