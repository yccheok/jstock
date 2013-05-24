/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.watchlist;

import org.yccheok.jstock.engine.Country;

/**
 *
 * @author yccheok
 */
public class WatchlistInfo {
    public final Country country;
    public final String name;
    public final int size;
    
    private WatchlistInfo(Country country, String name, int size) {
        this.country = country;
        this.name = name;
        this.size = size;
    }
    
    public static WatchlistInfo newInstance(Country country, String name, int size) {
        return new WatchlistInfo(country, name, size);
    }
    
    public WatchlistInfo setCountry(Country country) {
        return new WatchlistInfo(country, this.name, this.size);
    }
    
    public WatchlistInfo setName(String name) {
        return new WatchlistInfo(this.country, name, this.size);
    }
    
    public WatchlistInfo setSize(int size) {
        return new WatchlistInfo(this.country, this.name, size);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + country.hashCode();
        result = 31 * result + name.toLowerCase().hashCode();
        // Do not compare size.
        
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof WatchlistInfo)) {
            return false;
        }
        
        WatchlistInfo watchlistInfo = (WatchlistInfo)o;
        // Do not compare size.
        return this.country.equals(watchlistInfo.country) && this.name.toLowerCase().equals(watchlistInfo.name.toLowerCase());
    }
    
    @Override
    public String toString() {
        return WatchlistInfo.class.getSimpleName() + "[" + country + ", " + name + ", " + size + "]";
    }    
}
