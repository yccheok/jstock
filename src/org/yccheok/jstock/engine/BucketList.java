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
    }    
    
    public int size() {
        final int size = 
            (buckets.size() / maxBucketSize) + 
            (((buckets.size() % maxBucketSize) == 0) ? 0 : 1);        
        return size;
    }
    
    public List<E> get(int index) {        
        ListIterator<E> listIterator = null;
        final int size = size();
        if (index < size) {
            listIterator = buckets.listIterator(index * maxBucketSize);
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
    
    public boolean add(E e) {
        if (bucketsIndexMapping.containsKey(e)) {
            return false;
        }
        
        boolean status = buckets.add(e);
        bucketsIndexMapping.put(e, buckets.size() - 1);
        
        return status;

    }
    
    private final int maxBucketSize;
    private List<E> buckets = new ArrayList<E>();
    private Map<E, Integer> bucketsIndexMapping = new HashMap<E, Integer>();
}
