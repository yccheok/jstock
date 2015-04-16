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

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yccheok
 */
public class Industry implements Comparable<Industry> {
    private Industry(String industry) {
        this.industry = industry;
    }
    
    public static Industry valueOf(String industry) {
        if (industry == null) {
            throw new java.lang.IllegalArgumentException("industry cannot be null");
        }
        
        industry = industry.trim();
        
        if (industry.isEmpty()) {
            throw new java.lang.IllegalArgumentException("industry cannot be empty");
        }
        
        Industry result = map.get(industry);
        if (result == null) {
            final Industry instance = new Industry(industry);
            result = map.putIfAbsent(industry, instance);
            if (result == null) {
                result = instance;
            }
        }
        
        assert(result != null);
        return result;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + industry.hashCode();
        
        return result;
    }
    
    @Override
    public int compareTo(Industry o) {
        return this.industry.compareTo(o.industry);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Industry)) {
            return false;
        }
        
        return this.industry.equals(((Industry)o).industry);
    }
    
    @Override
    public String toString() {
        return industry;
    }
    
    public String name() {
        return industry;
    }
    
    private final String industry;
    
    // Avoid using interface. We want it to be fast!
    private static final ConcurrentHashMap<String, Industry> map = new ConcurrentHashMap<>();
    
    // Common used industry.
    public static final Industry Unknown = Industry.valueOf("Unknown");
    public static final Industry UserDefined = Industry.valueOf("UserDefined");
}