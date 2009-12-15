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
    KLSE("KLSE", Code.newInstance("^KLSE")),
    Second("Second Board", Code.newInstance("Second")),
    Mesdaq("Mesdaq", Code.newInstance("Mesdaq")),
    STI("Straits Times Index", Code.newInstance("^STI")),
    DJI("Dow Jones Industrial Average", Code.newInstance("^DJI")),
    IXIC("Nasdaq Composite", Code.newInstance("^IXIC")),
    DAX("DAX", Code.newInstance("^GDAXI")),
    OMXSPI("Stockholm General", Code.newInstance("^OMXSPI")),
    OMXC20CO("OMX Copenhagen 20", Code.newInstance("OMXC20.CO")),
    OSEAX("OSE All Share", Code.newInstance("^OSEAX")),
    SPMIB("S&P/MIB", Code.newInstance("^SPMIB")),
    SMSI("Madrid General", Code.newInstance("^SMSI")),
    FTSE("FTSE 100", Code.newInstance("^FTSE")),
    FCHI("CAC 40", Code.newInstance("^FCHI")),
    BSESN("BSE SENSEX", Code.newInstance("^BSESN")),
    NSEI("S&P CNX NIFTY", Code.newInstance("^NSEI")),
    AORD("All Ordinaries", Code.newInstance("^AORD")),
    ATX("ATX", Code.newInstance("^ATX")),
    BFX("BEL-20", Code.newInstance("^BFX")),
    GSPTSE("S&P TSX Composite", Code.newInstance("^GSPTSE")),
    HSI("Hang Seng", Code.newInstance("^HSI")),
    JKSE("Jakarta Composite", Code.newInstance("^JKSE")),
    KS11("Seoul Composite", Code.newInstance("^KS11")),
    AEX("AEX", Code.newInstance("^AEX")),
    PSI20("PSI 20", Code.newInstance("^PSI20")),
    TWII("TSEC weighted index", Code.newInstance("^TWII")),
    SSMI("Swiss Market", Code.newInstance("^SSMI")),
    BVSP("Bovespa", Code.newInstance("^BVSP"));

    Index(String name, Code code) {
        this.name = name;
        this.code = code;
    }
    
    public Code getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return name;
    }
        
    private String name;
    private Code code;
}
