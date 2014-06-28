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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.yccheok.jstock.gui.Pair;

/**
 *
 * @author yccheok
 */
public class CodeBucketLists {
    public CodeBucketLists(int maxBucketSize) {
        if (maxBucketSize <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        
        this.maxBucketSize = maxBucketSize;
        readWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        readerLock = readWriteLock.readLock();
        writerLock = readWriteLock.writeLock();                
        
    }
    
    public boolean isEmpty() {
        return bucketListsIndexMapping.isEmpty();
    }
    
    public List<Code> get(int index) {
        readerLock.lock();
        try {
            final int size = bucketListsIndexMapping.size();    
            if (index >= size) {
                return java.util.Collections.emptyList(); 
            }

            final String id = bucketListsIndexMapping.get(index); 
            final Integer index2 = basedIndexInfosIndexMapping.get(id);

            final Pair<String, Integer> info = basedIndexInfos.get(index2);
            
            final BucketList<Code> bucketList = bucketLists.get(id);

            final int basedIndex = info.second;
            final int i = index - basedIndex;
            
            return bucketList.get(i);
        } finally {
            readerLock.unlock();
        }
    }
    
    public int size() {
        return bucketListsIndexMapping.size();
    }
    
    public synchronized boolean add(Code code) {
        final String id = getStockServerFactoriesId(code);
        
        BucketList<Code> bucketList = bucketLists.get(id);
        
        if (bucketList == null) {
            bucketList = new BucketList<Code>(this.maxBucketSize);
            bucketLists.put(id, bucketList);
        }
        
        final int beforeSize = bucketList.size();
        
        final boolean status = bucketList.add(code);
        if (status == false) {
            // Duplicated.
            return status;
        }
        
        final int afterSize = bucketList.size();
        
        assert(afterSize >= 1);
        assert(afterSize >= beforeSize);
        
        if (afterSize == beforeSize) {
            return true;
        }
        
        final Integer basedIndexInfosIndex = basedIndexInfosIndexMapping.get(id);
        final int basedIndex;
        
        if (basedIndexInfosIndex == null) {
            // id not found.
            
            final int basedIndexInfosSize = basedIndexInfos.size();
            
            basedIndexInfosIndexMapping.put(id, basedIndexInfosSize);            
            
            if (basedIndexInfosSize == 0) {
                // ever first id in this CodeBucketLists.
                
                basedIndex = 0;
            } else {
                final Pair<String, Integer> previousBasedIndexInfo = basedIndexInfos.get(basedIndexInfosSize - 1);
                final BucketList<Code> previousBucketList = bucketLists.get(previousBasedIndexInfo.first);
                final int previousBucketListSize = previousBucketList.size();
                basedIndex = previousBasedIndexInfo.second + previousBucketListSize;
            }  
            
            basedIndexInfos.add(Pair.create(id, basedIndex));
            
        } else {
            basedIndex = basedIndexInfos.get(basedIndexInfosIndex).second;
            
            for (int i = (basedIndexInfosIndex + 1), ei = basedIndexInfos.size(); i < ei; i++) {
                final Pair<String, Integer> basedIndexInfo = basedIndexInfos.get(i);
                basedIndexInfos.set(i, Pair.create(basedIndexInfo.first, basedIndexInfo.second + 1));
            }
        }
        
        bucketListsIndexMapping.add(basedIndex, id);
        
        return true;
    }
    
    public synchronized void clear() {
        writerLock.lock();
        try {
            bucketLists.clear();
            bucketListsIndexMapping.clear();
            basedIndexInfosIndexMapping.clear();
            basedIndexInfos.clear();
        } finally {
            writerLock.unlock();
        }
    }
    
    public synchronized boolean remove(Code code) {
        writerLock.lock();
        try {        
            final String id = getStockServerFactoriesId(code);

            BucketList<Code> bucketList = bucketLists.get(id);

            if (bucketList == null) {
                // Nothing to be removed.
                return false;
            }

            final int beforeSize = bucketList.size();

            final boolean status = bucketList.remove(code);
            if (status == false) {
                // Nothing to be removed.
                return status;
            } 

            final int afterSize = bucketList.size();

            assert(afterSize >= 0);
            assert(afterSize <= beforeSize); 

            if (afterSize == beforeSize) {
                return true;
            }        

            final Integer basedIndexInfosIndex = basedIndexInfosIndexMapping.get(id);
            final int basedIndex = basedIndexInfos.get(basedIndexInfosIndex).second;

            for (int i = (basedIndexInfosIndex + 1), ei = basedIndexInfos.size(); i < ei; i++) {
                final Pair<String, Integer> basedIndexInfo = basedIndexInfos.get(i);
                basedIndexInfos.set(i, Pair.create(basedIndexInfo.first, basedIndexInfo.second - 1));
            }

            bucketListsIndexMapping.remove((int)basedIndex);

            if (afterSize == 0) {
                for (int i = (basedIndexInfosIndex + 1), ei = basedIndexInfos.size(); i < ei; i++) {
                    final Pair<String, Integer> basedIndexInfo = basedIndexInfos.get(i);
                    final String _id = basedIndexInfo.first;
                    int index = basedIndexInfosIndexMapping.get(_id);
                    index--;
                    basedIndexInfosIndexMapping.put(_id, index);
                }

                bucketLists.remove(id);
                int index = basedIndexInfosIndexMapping.remove(id);
                basedIndexInfos.remove(index);
            }

            return true;
        } finally {
            writerLock.unlock();
        }
    }
    
    // Someone had modified Factories singleton content. Rebuild code bucket
    // lists.
    public synchronized void rebuild() {
        final List<Code> codes = new ArrayList<Code>();
        
        for (int i = 0, ei = size(); i < ei; i++) {
            codes.addAll(get(i));
        }
        
        this.clear();
        
        for (Code code : codes) {
            this.add(code);
        }
    }
    
    private String getStockServerFactoriesId(Code code) {
        List<StockServerFactory> stockServerFactories = Factories.INSTANCE.getStockServerFactories(code);
        
        StringBuilder stringBuilder = new StringBuilder();
        for (StockServerFactory stockServerFactory : stockServerFactories) {
            stringBuilder.append(stockServerFactory.getId());
        }
        return stringBuilder.toString();
    }
    
    private final int maxBucketSize;
    
    /*        -----------------
     * "A" => | 0 | 1 | 2 | 3 |
     *        -----------------
     * "B" => | 4 |
     *        ---------
     * "C" => | 5 | 6 | 
     *        --------- 
     */
    private final Map<String, BucketList<Code>> bucketLists = new ConcurrentHashMap<String, BucketList<Code>>();
    
    /*
     * -------------------------------------------
     * | "A" | "A" | "A" | "A" | "B" | "C" | "C" |
     * -------------------------------------------
     */
    private final List<String> bucketListsIndexMapping = new java.util.concurrent.CopyOnWriteArrayList<String>();
    
    /*        
     * "A" => 0
     * "B" => 1
     * "C" => 2
     */    
    private final Map<String, Integer> basedIndexInfosIndexMapping = new ConcurrentHashMap<String, Integer>();
    
    /*
     * ----------------------------------
     * | ("A", 0) | ("B", 4) | ("C", 5) |
     * ----------------------------------
     */
    private final List<Pair<String, Integer>> basedIndexInfos = new java.util.concurrent.CopyOnWriteArrayList<Pair<String, Integer>>();
    
    private final java.util.concurrent.locks.ReadWriteLock readWriteLock;
    private final java.util.concurrent.locks.Lock readerLock;
    private final java.util.concurrent.locks.Lock writerLock;    
}
