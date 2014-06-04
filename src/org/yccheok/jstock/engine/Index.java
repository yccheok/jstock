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

/**
 *
 * @author yccheok
 */
public enum Index {
    // By referring to Bloomberg Android app.
    ASX(Country.Australia, "ASX 200", "S&P/ASX 200", Code.newInstance("^AXJO")),
    AORD(Country.Australia, "All Ordinaries", "All Ordinaries", Code.newInstance("^AORD")),
    ATX(Country.Austria, "ATX", "Austrian Traded Index", Code.newInstance("^ATX")),
    BFX(Country.Belgium, "BEL-20", "BEL-20", Code.newInstance("^BFX")),
    BVSP(Country.Brazil, "Bovespa", "Ibovespa Brasil Sao Paulo", Code.newInstance("^BVSP")),
    GSPTSE(Country.Canada, "TSX", "S&P/TSX Composite Index", Code.newInstance("^GSPTSE")),
    CSI300(Country.China, "CSI 300", "Shanghai Shenzhen CSI 300 Index", Code.newInstance("000300.SS")),
    SSEC(Country.China, "China Shanghai Composite", "China Shanghai Composite", Code.newInstance("000001.SS")),
    OMXC20CO(Country.Denmark, "KFX", "OMX Copenhagen 20 Index", Code.newInstance("OMXC20.CO")),
    FCHI(Country.France, "CAC 40", "CAC 40 Index", Code.newInstance("^FCHI")),
    DAX(Country.Germany, "DAX", "Deutsche Borse AG German Stock Index", Code.newInstance("^GDAXI")),
    HSI(Country.HongKong, "Hang Seng", "Hong Kong Hang Seng Index", Code.newInstance("^HSI")),
    BSESN(Country.India, "SENSEX", "S&P BSE India Sensex Index", Code.newInstance("^BSESN")),
    NSEI(Country.India, "NIFTY", "National Stock Exchange CNX Nifty Index", Code.newInstance("^NSEI")),
    JKSE(Country.Indonesia, "Jakarta Composite", "Jakarta Composite", Code.newInstance("^JKSE")),
    TA25(Country.Israel, "TA 25", "Tel Aviv 25 Index", Code.newInstance("T25.TA")),
    TA100(Country.Israel, "TA 100", "Tel Aviv 100 Index", Code.newInstance("^TA100")),
    FTSEMIB(Country.Italy, "MIB", "FTSE MIB Index", Code.newInstance("FTSEMIB.MI")),
    KS11(Country.Korea, "KOSPI", "Korea Stock Exchange KOSPI Index", Code.newInstance("^KS11")),
    KLSE(Country.Malaysia, "KLCI", "Kuala Lumpur Composite Index", Code.newInstance("^KLSE")),    
    AEX(Country.Netherlands, "AEX", "AEX-Index", Code.newInstance("^AEX")),
    NZSX50(Country.NewZealand, "NZX 50", "New Zealand Exchange 50 Gross Index", Code.newInstance("^NZ50")),
    OSEAX(Country.Norway, "OSEBX", "Oslo Stock Exchange Benchmark Index", Code.newInstance("^OSEAX")),
    PSI20(Country.Portugal, "PSI 20", "PSI 20 Index", Code.newInstance("PSI20.LS")),
    STI(Country.Singapore, "STI", "Straits Times Index", Code.newInstance("^STI")),
    SMSI(Country.Spain, "Madrid General", "Madrid General", Code.newInstance("^SMSI")),
    OMX30(Country.Sweden, "OMX 30", "OMX Stockholm 30 Index", Code.newInstance("^OMX")),
    SSMI(Country.Switzerland, "SMI", "Swiss Market Index", Code.newInstance("^SSMI")),
    TWII(Country.Taiwan, "TSEC", "Taiwan Stock Exchange Weighted Index", Code.newInstance("^TWII")),
    FTSE(Country.UnitedKingdom, "FTSE 100", "FTSE 100 Index", Code.newInstance("^FTSE")),
    DJI(Country.UnitedState, "DOW JONES", "Dow Jones Industrial Average", Code.newInstance("^DJI")),
    IXIC(Country.UnitedState, "NASDAQ", "NASDAQ Composite Index", Code.newInstance("^IXIC"));
    
    Index(Country country, String name, String longName, Code code) {
        this.country = country;
        this.name = name;
        this.longName = longName;
        this.code = code;
    }


    @Override
    public String toString() {
        return name;
    }
    
    public final String name;
    public final String longName;
    public final Code code;
    public final Country country;
}