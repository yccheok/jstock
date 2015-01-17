/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        readWriteLockForPeriodDuration = new java.util.concurrent.locks.ReentrantReadWriteLock();
        readerLockForPeriodDuration = readWriteLockForPeriodDuration.readLock();
        writerLockForPeriodDuration = readWriteLockForPeriodDuration.writeLock();

        this.DATABASE_SIZE = databaseSize;
        
        this.stockHistorySerializer = null;
    }
    
    public boolean addStockCode(final Code code) {
        writerLock.lock();

        try {
            // Should we perform lock downgrading?
            //
            /*
                 class CachedData {
                   Object data;
                   volatile boolean cacheValid;
                   ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

                   void processCachedData() {
                     rwl.readLock().lock();
                     if (!cacheValid) {
                        // upgrade lock manually
                        rwl.readLock().unlock();   // must unlock first to obtain writelock
                        rwl.writeLock().lock();
                        if (!cacheValid) { // recheck
                          data = ...
                          cacheValid = true;
                        }
                        // downgrade lock
                        rwl.readLock().lock();  // reacquire read without giving up write lock
                        rwl.writeLock().unlock(); // unlock write, still hold read
                     }

                     use(data);
                     rwl.readLock().unlock();
                   }
                 }
             */

            if (stockCodes.contains(code)) {
                return false;
            }

            boolean status = stockCodes.add(code);

            pool.execute(new StockHistoryRunnable(code));

            return status;
        } finally {
            writerLock.unlock();
        }
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

        writerLockForPeriodDuration.lock();
        try {
            this.duration = duration;
            this.period = null;
        } finally {
            writerLockForPeriodDuration.unlock();
        }
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        if (period == null)
        {
            throw new IllegalArgumentException("period cannot be null");
        }

        writerLockForPeriodDuration.lock();
        try {
            this.period = period;
            this.duration = null;
        } finally {
            writerLockForPeriodDuration.unlock();
        }
    }

    public class StockHistoryRunnable implements Runnable {
        public StockHistoryRunnable(Code code) {
            this.code = code;
            this.historyServer = null;
        }
        
        @Override
        public void run() {
            final Thread currentThread = Thread.currentThread();

            // Thread safety purpose.
            readerLockForPeriodDuration.lock();
            Duration duration;
            Period period;
            try {
                duration = StockHistoryMonitor.this.duration;
                period = StockHistoryMonitor.this.period;
            } finally {
                readerLockForPeriodDuration.unlock();
            }

            for (StockServerFactory factory : Factories.INSTANCE.getStockServerFactories(code)) {
                // Do not apply ExecutorService.isShutDown technique, to quit
                // from this loop. This is because pool variable isn't final.
                // We might be referring to the wrong pool. But checking for
                // Thread.isInterrupted isn't fool proof either, as the
                // interrupted flag might be cleared by other funtions. Fixing
                // this might be complicated. Till now, it hasn't gave us any
                // trouble. Hence, don't fix it if it isn't broken.
                if (currentThread.isInterrupted()) {
                    break;
                }

                StockHistoryServer history = null;

                if (period != null) {
                    history = factory.getStockHistoryServer(this.code, period);
                } else {
                    assert(duration != null);
                    history = factory.getStockHistoryServer(this.code, duration);
                }

                if (history != null) {
                    readerLock.lock();

                    try {
                        // Anyone try to stop us from publishing this history?
                        if (stockCodes.contains(code)) {
                            this.historyServer = history;

                            // Concurrent access problem may happen here. Two or more threads may read
                            // and modify histories. Although we can use new reader/writer lock pair to prevent,
                            // this will increase complexity.
                            //
                            // Hence, We choose to use "Double-Checked Locking", to avoid expensive of synchronized block.
                            //
                            // However, "Double-Checked Locking" is not guarantee to work well. Please refer to
                            // http://www.ibm.com/developerworks/java/library/j-jtp02244.html
                            // http://www.ibm.com/developerworks/library/j-jtp03304/
                            //
                            // Any how, we allow error tolerance here.
                            // The worst case is that, histories.size() >= StockHistoryMonitor.this.databaseSize.
                            // But that doesn't really matter.
                            //
                            boolean shouldUseSerializer = false;

                            if (histories.size() < StockHistoryMonitor.this.DATABASE_SIZE) {
                                synchronized (histories) {
                                    if(histories.size() < StockHistoryMonitor.this.DATABASE_SIZE) {
                                        histories.put(code, history);
                                    }
                                    else {
                                        shouldUseSerializer = true;
                                    }
                                }
                            }
                            else {
                                shouldUseSerializer = true;
                            }

                            if (shouldUseSerializer) {
                                if (StockHistoryMonitor.this.stockHistorySerializer != null) {
                                    if (period != null) {
                                        StockHistoryMonitor.this.stockHistorySerializer.save(history, period);
                                    } else {
                                        assert(duration != null);
                                        StockHistoryMonitor.this.stockHistorySerializer.save(history, duration);
                                    }
                                } else {
                                    log.error("Fail to perform serialization on stock history due to uninitialized serialization component.");
                                }
                            }
                        }
                    } finally {
                        readerLock.unlock();
                    }
                    
                    // Break from loop, as we already obtain the history.
                    break;
                }   // if (history != null)
            }   // for
            
            // We need to notify the listener. Whether the history is success or
            // fail.
            StockHistoryMonitor.this.notify(StockHistoryMonitor.this, this);
            
            // The purpose of stockCodes, is to ensure there are no 2 
            // StockHistoryRunnable with same codes in the queue. We already
            // finish this runnable. So, is time for us to uplift such
            // restriction so that next addStockCode can success.
            writerLock.lock();
            try {
                stockCodes.remove(code);
            } finally {
                writerLock.unlock();
            }            
        }
    
        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof StockHistoryRunnable)) {
                return false;
            }

            StockHistoryRunnable stockHistoryRunnable = (StockHistoryRunnable)o;

            return this.code.equals(stockHistoryRunnable.code);
        }
        
        @Override
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

        try {
            final ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor)pool);
            final int nThreads = threadPoolExecutor.getMaximumPoolSize();

            stockCodes.clear();

            histories.clear();

            threadPoolExecutor.shutdownNow();

            // pool is not valid any more. Discard it and re-create.
            pool = Executors.newFixedThreadPool(nThreads);
        } finally {
            writerLock.unlock();
        }
    }
    
    public boolean removeStockCode(Code code) {
        writerLock.lock();
        try {
            boolean status = stockCodes.remove(code);

            histories.remove(code);

            ((ThreadPoolExecutor)pool).remove(new StockHistoryRunnable(code));

            return status;
        } finally {
            writerLock.unlock();
        }
    }
    
    public StockHistoryServer getStockHistoryServer(Code code) {
        readerLock.lock();

        try {
            if (histories.containsKey(code))
            {
                final StockHistoryServer stockHistoryServer = histories.get(code);

                return stockHistoryServer;
            }
            else {
                final StockHistorySerializer shs = StockHistoryMonitor.this.stockHistorySerializer;
                if (shs != null) {
                    StockHistoryServer stockHistoryServer;

                    readerLockForPeriodDuration.lock();
                    Duration duration;
                    Period period;
                    try {
                        duration = StockHistoryMonitor.this.duration;
                        period = StockHistoryMonitor.this.period;
                    } finally {
                        readerLockForPeriodDuration.unlock();
                    }

                    if (period != null) {
                        stockHistoryServer = shs.load(code, period);
                    } else {
                        stockHistoryServer = shs.load(code, duration);
                    }

                    /* So that next time we won't read from the disk. */
                    if (stockHistoryServer != null && (this.DATABASE_SIZE > histories.size())) {
                        // Concurrent access problem may happen here. Two or more threads may read
                        // and modify histories. Although we can use new reader/writer lock pair to prevent,
                        // this will increase complexity.
                        //
                        // Hence, We choose to use "Double-Checked Locking", to avoid expensive of synchronized block.
                        //
                        // However, "Double-Checked Locking" is not guarantee to work well. Please refer to
                        // http://www.ibm.com/developerworks/java/library/j-jtp02244.html
                        // http://www.ibm.com/developerworks/library/j-jtp03304/
                        //
                        // Any how, we allow error tolerance here.
                        // The worst case is that, histories.size() >= StockHistoryMonitor.this.databaseSize.
                        // But that doesn't really matter.
                        //
                        synchronized (histories) {
                            if (stockHistoryServer != null && (this.DATABASE_SIZE > histories.size())) {
                                histories.put(code, stockHistoryServer);
                            }
                        }
                    }

                    return stockHistoryServer;
                }
                else {
                    log.error("Fail to retrieve stock history due to uninitialized serialization component.");
                }
            }

            return null;
        } finally {
             readerLock.unlock();
        }
    }
    
    public void setStockHistorySerializer(StockHistorySerializer stockHistorySerializer) {
        this.stockHistorySerializer = stockHistorySerializer;
    }
    
    // Never export out stockCodes information. They are being used internally.
    
    public void stop() {
        ThreadPoolExecutor threadPoolExecutor = null;
        writerLock.lock();       
        try {
            threadPoolExecutor = ((ThreadPoolExecutor)pool);
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
        } finally {
            writerLock.unlock();
        }

        // No unlock after awaitTermination, might cause deadlock.
        
        // How to wait for infinity?
        try {
            threadPoolExecutor.awaitTermination(100, TimeUnit.DAYS);
        }
        catch (InterruptedException exp) {
            log.error(null, exp);
        }
    }
    
    // The purpose of stockCodes, is to ensure there are no 2 StockHistoryRunnable
    // with same codes in the queue.
    private final java.util.List<Code> stockCodes = new java.util.ArrayList<Code>();
    private final java.util.Map<Code, StockHistoryServer> histories = new java.util.HashMap<Code, StockHistoryServer>();

    private final java.util.concurrent.locks.ReadWriteLock readWriteLock;
    private final java.util.concurrent.locks.Lock readerLock;
    private final java.util.concurrent.locks.Lock writerLock;

    private final java.util.concurrent.locks.ReadWriteLock readWriteLockForPeriodDuration;
    private final java.util.concurrent.locks.Lock readerLockForPeriodDuration;
    private final java.util.concurrent.locks.Lock writerLockForPeriodDuration;

    private Executor pool;
    
    // Prevent out of memory problem.
    private final int DATABASE_SIZE;
    
    private StockHistorySerializer stockHistorySerializer;

    // Either duration or period will always be null.
    private volatile Period period = Period.Years10;
    // This one is a mutable member. We just want to make our life easier. We
    // want to avoid stopping and creating a new StockHistoryMonitor, just
    // to make change on duration.
    private volatile Duration duration = null;

    private static final Log log = LogFactory.getLog(StockHistoryMonitor.class);     
}
