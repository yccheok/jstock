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

package org.yccheok.jstock.engine.currency;

/**
 *
 * @author yccheok
 */
public class CurrencyPair extends org.yccheok.jstock.engine.Pair<Currency, Currency> {
    public static CurrencyPair create(String from, String to) {
        return create(Currency.valueOf(from), Currency.valueOf(to));
    }
    
    public static CurrencyPair create(Currency from, Currency to) {
        return new CurrencyPair(from, to);
    }
    
    public CurrencyPair(Currency from, Currency to) {        
        super(from, to);
        
        if (from == null || to == null) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    
    public Currency from() {
        return this.first;
    }
    
    public Currency to() {
        return this.second;
    }
}
