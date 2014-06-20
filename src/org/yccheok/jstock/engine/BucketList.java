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
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author yccheok
 */
public class BucketList<E> {
    public BucketList(int maxBucketSize) {
        if (maxBucketSize <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        
        this.maxBucketSize = maxBucketSize;
        bucketsReadWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        bucketsReaderLock = bucketsReadWriteLock.readLock();
        bucketsWriterLock = bucketsReadWriteLock.writeLock();                
    }    
    
    public int size() {
        final int bucketsSize = buckets.size();
        
        final int size = 
            (bucketsSize / maxBucketSize) + 
            (((bucketsSize % maxBucketSize) == 0) ? 0 : 1);
        
        return size;
    }
    
    public List<E> get(int index) {        
        ListIterator<E> listIterator = null;
        
        bucketsReaderLock.lock();
        try {
            final int size = size();

            if (index >= 0 && index < size) {
                listIterator = buckets.listIterator(index * maxBucketSize);
            }
        } finally {
            bucketsReaderLock.unlock();
        }
        
        if (listIterator != null) {
            List<E> list = new ArrayList<E>();
            for (int i = 0; listIterator.hasNext() && i < maxBucketSize; i++) {
                list.add(listIterator.next());
            }
            return list;
        }
        
        return java.util.Collections.emptyList();
    }
    
    // synchronized, to avoid add and remove at the same time.
    public synchronized boolean add(E e) {
        // Lock isn't required here. This is because increase the size of the 
        // list is not going to get IndexOutOfBoundException.        
        if (bucketsIndexMapping.containsKey(e)) {
            return false;
        }
        
        boolean status = buckets.add(e);
        bucketsIndexMapping.put(e, buckets.size() - 1);
        
        return status;

    }
    
    // synchronized, to avoid add and remove at the same time
    public synchronized boolean remove(E e) {
        // This is to ensure we are able to get the correct StockCodes size,
        // and able to retrieve Iterator in a safe way without getting 
        // IndexOutOfBoundException.
        bucketsWriterLock.lock();        
        boolean status = false;
        
        try {
            Integer row = bucketsIndexMapping.get(e);

            if (row == null) {
                return status;
            }

            status = (null != buckets.remove((int)row));            
            bucketsIndexMapping.remove(e);

            for (int i = row, ei = buckets.size(); i < ei; i++) {
                bucketsIndexMapping.put(buckets.get(i), i);
            }
        } finally {
            bucketsWriterLock.unlock();
        }
        
        return status;
    }    
    
    private final int maxBucketSize;
    private final List<E> buckets = new java.util.concurrent.CopyOnWriteArrayList<E>();
    private final Map<E, Integer> bucketsIndexMapping = new HashMap<E, Integer>();
    private final java.util.concurrent.locks.ReadWriteLock bucketsReadWriteLock;
    private final java.util.concurrent.locks.Lock bucketsReaderLock;
    private final java.util.concurrent.locks.Lock bucketsWriterLock;    
}
