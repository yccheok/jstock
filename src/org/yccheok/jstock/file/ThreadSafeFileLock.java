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

package org.yccheok.jstock.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Pair;

/**
 *
 * Here's the reason why we didn't use Java's FileLock :
 *
 * File locks are held on behalf of the entire Java virtual machine. They are 
 * not suitable for controlling access to a file by multiple threads within the 
 * same virtual machine. File-lock objects are safe for use by multiple 
 * concurrent threads.
 * 
 * http://stackoverflow.com/questions/10531286/is-filelock-in-java-safe-across-multiple-threads-within-the-same-process-or-betw
 * 
 */

public class ThreadSafeFileLock {
    private ThreadSafeFileLock() {
    }
    
    /***************************************************************************
     * CRITICAL SECTION OF THE CODE.
     **************************************************************************/
    public static Lock getLock(File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException ex) {
            log.error(null, ex);
            return null;
        }
        
        Lock lock;
        synchronized(reentrantReadWriteLockMapMonitor) {
            lock = reentrantReadWriteLockMap.get(canonicalPath);
            if (lock == null) {
                ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
                AtomicInteger atomicInteger = new AtomicInteger(1);
                Pair<ReentrantReadWriteLock, AtomicInteger> pair = Pair.create(reentrantReadWriteLock, atomicInteger);
                lock = new Lock(pair, canonicalPath);
                reentrantReadWriteLockMap.put(canonicalPath, lock);
            } else {
                lock.reentrantReadWriteLock.second.incrementAndGet();
            }
        }
                
        return lock;
    }
    
    public static void releaseLock(Lock lock) {    
        synchronized(reentrantReadWriteLockMapMonitor) {
            int counter = lock.reentrantReadWriteLock.second.decrementAndGet();
            if (counter == 0) {
                reentrantReadWriteLockMap.remove(lock.canonicalPath);
            }
        } 
    }
    /***************************************************************************
     * CRITICAL SECTION OF THE CODE.
     **************************************************************************/
    
    /***************************************************************************
     * CRITICAL SECTION OF THE CODE.
     **************************************************************************/
    public static void lockRead(Lock lock) {
        lock.reentrantReadWriteLock.first.readLock().lock();
    }
    
    public static void unlockRead(Lock lock) {
        lock.reentrantReadWriteLock.first.readLock().unlock();
    }
    /***************************************************************************
     * CRITICAL SECTION OF THE CODE.
     **************************************************************************/

    /***************************************************************************
     * CRITICAL SECTION OF THE CODE.
     **************************************************************************/
    public static void lockWrite(Lock lock) {
        lock.reentrantReadWriteLock.first.writeLock().lock();
    }
    
    public static void unlockWrite(Lock lock) {
        lock.reentrantReadWriteLock.first.writeLock().unlock();
    }
    /***************************************************************************
     * CRITICAL SECTION OF THE CODE.
     **************************************************************************/
    
    public static class Lock {
        public final Pair<ReentrantReadWriteLock, AtomicInteger> reentrantReadWriteLock;
        public final String canonicalPath;
        
        public Lock(Pair<ReentrantReadWriteLock, AtomicInteger> reentrantReadWriteLock, String canonicalPath) {
            this.reentrantReadWriteLock = reentrantReadWriteLock;
            this.canonicalPath = canonicalPath;
        }
    }
    
    private static final Map<String, Lock> reentrantReadWriteLockMap = new HashMap<>();
    private static final Object reentrantReadWriteLockMapMonitor = new Object();
    
    private static final Log log = LogFactory.getLog(Statements.class);
}