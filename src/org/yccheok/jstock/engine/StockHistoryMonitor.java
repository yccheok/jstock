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
        
        readWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        readerLock = readWriteLock.readLock();
        writerLock = readWriteLock.writeLock();
        
        this.databaseSize = databaseSize;
        
        this.stockHistorySerializer = null;
    }
    
    public boolean addStockServerFactory(StockServerFactory factory) {
        return factories.add(factory);
    }
    
    public boolean addStockCode(final Code code) {
        writerLock.lock();
        
        if(stockCodes.contains(code)) {
            writerLock.unlock();
            return false;            
        }
        
        boolean status = stockCodes.add(code);
        
        pool.execute(new StockHistoryRunnable(code));

        writerLock.unlock();

        return status;
    }

    /**
     * @return the duration
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Duration duration) {
        if (duration == null)
        {
            throw new IllegalArgumentException("duration cannot be null");
        }

        this.duration = duration;
    }
    
    public class StockHistoryRunnable implements Runnable {
        public StockHistoryRunnable(Code code) {
            this.code = code;
            this.historyServer = null;
        }
        
        @Override
        public void run() {
            final Thread currentThread = Thread.currentThread();
            
            for(StockServerFactory factory : factories) {
                StockHistoryServer history = factory.getStockHistoryServer(this.code, duration);

                if(history != null) {
                    readerLock.lock();
                    
                    // Anyone try to stop us from publishing this history?
                    if(stockCodes.contains(code)) {
                        this.historyServer = history;

                        // Concurrent access problem may happen here. Two or more threads may read
                        // and modify histories. Although we can use reader/writer lock to prevent,
                        // this will increase complexity. Any how, we allow error tolerance here.
                        // The worst case is that, histories.size() >= StockHistoryMonitor.this.databaseSize.
                        // But that doesn't really matter.
                        //
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
                    
                    readerLock.unlock();
                    
                    break;
                }
               
                if(currentThread.isInterrupted())
                    break;
            }
            
            if(historyServer == null) {
                writerLock.lock();
        
                stockCodes.remove(code);
        
                writerLock.unlock();
            }
            
            // We need to notify the listener. Whether the history is success or
            // fail.
            StockHistoryMonitor.this.notify(StockHistoryMonitor.this, this);
        }
    
        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
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
    
    public void clearStockCodes() {        
        writerLock.lock();

        final ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor)pool);
        final int nThreads = threadPoolExecutor.getMaximumPoolSize();

        stockCodes.clear();
        
        histories.clear();                

        threadPoolExecutor.shutdownNow();  
        
        // pool is not valid any more. Discard it and re-create.
        pool = Executors.newFixedThreadPool(nThreads);

        writerLock.unlock();
    }
    
    public boolean removeStockCode(Code code) {
        writerLock.lock();
        
        boolean status = stockCodes.remove(code);
        
        histories.remove(code);                

        ((ThreadPoolExecutor)pool).remove(new StockHistoryRunnable(code));

        writerLock.unlock();

        return status;
    }
    
    public StockHistoryServer getStockHistoryServer(Code code) {
        readerLock.lock();

        if(histories.containsKey(code))
        {
            final StockHistoryServer stockHistoryServer = histories.get(code);

            readerLock.unlock();

            return stockHistoryServer;
        }
        else {
            if(StockHistoryMonitor.this.stockHistorySerializer != null) {
                StockHistoryServer stockHistoryServer = StockHistoryMonitor.this.stockHistorySerializer.load(code);
                
                /* So that next time we won't read from the disk. */
                if(stockHistoryServer != null && (this.databaseSize > histories.size())) {
                    // Concurrent access problem may happen here. Two or more threads may read
                    // and modify histories. Although we can use reader/writer lock to prevent,
                    // this will increase complexity. Any how, we allow error tolerance here.
                    // The worst case is that, histories.size() >= StockHistoryMonitor.this.databaseSize.
                    // But that doesn't really matter.
                    //
                    histories.put(code, stockHistoryServer);
                }

                readerLock.unlock();

                return stockHistoryServer;
            }
            else {
                log.error("Fail to retrieve stock history due to uninitialized serialization component.");                
            }
        }

        readerLock.unlock();
        
        return null;
    }
    
    public void setStockHistorySerializer(StockHistorySerializer stockHistorySerializer) {
        this.stockHistorySerializer = stockHistorySerializer;
    }
    
    // Never export out stockCodes information. They are being used internally.
    
    public void stop() {
        writerLock.lock();

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

        // pool is not valid any more. Discard it and re-create.
        pool = Executors.newFixedThreadPool(nThreads);

        writerLock.unlock();

        // No unlock after awaitTermination, might cause deadlock.
        
        // How to wait for infinity?
        try {
            threadPoolExecutor.awaitTermination(100, TimeUnit.DAYS);
        }
        catch(InterruptedException exp) {
            log.error("", exp);
        }
    }
    
    private java.util.List<StockServerFactory> factories = new java.util.concurrent.CopyOnWriteArrayList<StockServerFactory>();
    // Use internally. To ensure when we "remove" a code, we really remove it, even there is a runnable
    // in the middle of retrieving history information from the server.
    private java.util.List<Code> stockCodes = Collections.synchronizedList(new java.util.ArrayList<Code>());
    // We need to use concurrent map, due to the fact we need to export out the key set of this map.
    // We need to let the client have a safe way to iterate through the key set.
    private java.util.Map<Code, StockHistoryServer> histories = new java.util.concurrent.ConcurrentHashMap<Code, StockHistoryServer>();    
    private final java.util.concurrent.locks.ReadWriteLock readWriteLock;
    private final java.util.concurrent.locks.Lock readerLock;
    private final java.util.concurrent.locks.Lock writerLock;
    
    private Executor pool;
    
    // Prevent out of memory problem.
    private final int databaseSize;
    
    private StockHistorySerializer stockHistorySerializer;

    // This one is a mutable member. We just want to make our life easier. We
    // want to avoid stopping and creating a new StockHistoryMonitor, just
    // to make change on duration.
    private volatile Duration duration = Duration.getTodayDurationByYears(10);

    private static final Log log = LogFactory.getLog(StockHistoryMonitor.class);     
}
