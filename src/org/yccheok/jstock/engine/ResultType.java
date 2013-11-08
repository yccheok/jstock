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
public class ResultType {
    /**
     * The symbol.
     */
    public final String symbol;
    /**
     * The name.
     */
    public final String name;
    /**
     * The stock exchange.
     */
    public final String exch;
    /**
     * The stock type.
     */
    public final String type;
    /**
     * The stock exchange displayed name.
     */
    public final String exchDisp;
    /**
     * The stock type displayed name.
     */
    public final String typeDisp;

    /**
     * Creates a new instance of ResultType, with the specified symbol and
     * name.
     *
     * @param symbol The symbol
     * @param name The name
     */
    public ResultType(String symbol, String name) {
        this(symbol, name, null, null, null, null);
    }

    /**
     * Default constructor. Must have, in order for jackson to work
     * properly.
     */
    public ResultType() {
        this(null, null, null, null, null, null);
    }

    private ResultType(String symbol, String name, String exch, String type, String exchDisp, String typeDisp) {
        this.symbol = symbol;
        this.name = name;
        this.exch = exch;
        this.type = type;
        this.exchDisp = exchDisp;
        this.typeDisp = typeDisp;
    }

    public ResultType deriveWithSymbol(String symbol) {
        return new ResultType(symbol, this.name, this.exch, this.type, this.exchDisp, this.typeDisp);
    }

    public ResultType deriveWithName(String name) {
        return new ResultType(this.symbol, name, this.exch, this.type, this.exchDisp, this.typeDisp);
    }

    @Override
    public String toString() {
        return symbol;
    }
}
