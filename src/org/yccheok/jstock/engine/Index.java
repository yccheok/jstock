/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2008 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

/**
 *
 * @author yccheok
 */
public enum Index {
    KLSE("KLSE", Symbol.newInstance("KLSE")),
    Second("Second Board", Symbol.newInstance("Second")),
    Mesdaq("Mesdaq", Symbol.newInstance("Mesdaq")),
    DHI("Dow Jones Industrial Average", Symbol.newInstance("^DHI")),
    IXIC("NASDAQ COMPOSITE", Symbol.newInstance("^IXIC")),       
    DAX("DAX", Symbol.newInstance("^GDAXI")),               
    OMXSPI("Stockholm General", Symbol.newInstance("^OMXSPI")),
    OMXC20CO("OMX Copenhagen 20", Symbol.newInstance("OMXC20.CO")),
    OSEAX("OSE All Share", Symbol.newInstance("^OSEAX")),           
    SPMIB("S&P/MIB", Symbol.newInstance("^SPMIB")),
    SMSI("Madrid General", Symbol.newInstance("^SMSI")),
    FTSE("FTSE 100", Symbol.newInstance("^FTSE")),
    FCHI("CAC 40", Symbol.newInstance("^FCHI"));
        
    Index(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }
    
    public Symbol getSymbol() {
        return symbol;
    }
    
    @Override
    public String toString() {
        return name;
    }
        
    private String name;
    private Symbol symbol;
}
