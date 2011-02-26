/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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
 * A pair which carries code and symbol information of a stock. The reason we
 * do not inherited from Pair class is that, we do not like getFirst method name
 * and getSecond name, which is less readable.
 * @author yccheok
 */
public class StockInfo {
    /**
     * Code of this stock info.
     */
    public final Code code;
    /**
     * Symbol of this stock info.
     */
    public final Symbol symbol;

    /**
     * Constructs an instance of stock info.
     * @param code the code
     * @param symbol the symbol
     */
    public StockInfo(Code code, Symbol symbol) {
        if (code == null) {
            throw new java.lang.IllegalArgumentException("code cannot be null");
        }
        if (symbol == null) {
            throw new java.lang.IllegalArgumentException("symbol cannot be null");
        }
        this.code = code;
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof StockInfo))
            return false;

        StockInfo stockInfo = (StockInfo)o;
        return this.code.equals(stockInfo.code) && this.symbol.equals(stockInfo.symbol);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + this.code.hashCode();
        hash = 43 * hash + this.symbol.hashCode();
        return hash;
    }
}
