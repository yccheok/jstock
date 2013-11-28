/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng Cheok <yccheok@yahoo.com>
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

/**
 *
 * @author yccheok
 */
public class MatchType {
    /* Ticker */
    public final String t;
    
    /* Name */
    public final String n;
    
    /* Stock Exchange */
    public final String e;
    
    public final String id;
    
    public MatchType(String t, String n, String e, String id) {
        this.t = t;
        this.n = n;
        this.e = e;
        this.id = id;
    }
    
    public Code getCode() {
        if (this.e.equals("NSE")) {
            return Code.newInstance(this.t + ".N");
        } else if (this.e.equals("BSE")) {
            return Code.newInstance(this.t + ".B");
        }
        return Code.newInstance(this.e + ":" + this.t);
    }
    
    public MatchType deriveWithT(String t) {
        return new MatchType(t, this.n, this.e, this.id);
    }

    public MatchType deriveWithN(String n) {
        return new MatchType(this.t, n, this.e, this.id);
    }

    public MatchType deriveWithE(String e) {
        return new MatchType(this.t, this.n, e, this.id);
    }
    
    @Override
    public String toString() {
        return t;
    }    
}
