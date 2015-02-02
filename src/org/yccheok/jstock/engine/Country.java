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

import javax.swing.ImageIcon;
import org.yccheok.jstock.engine.currency.Currency;

/**
 *
 * @author yccheok
 */
public enum Country {
    Australia("/images/16x16/au.png", "Australia", Currency.newInstance("AUD"), Currency.newInstance("AUD")),
    Austria("/images/16x16/at.png", "Austria", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    Belgium("/images/16x16/be.png", "Belgium", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    Brazil("/images/16x16/br.png", "Brazil", Currency.newInstance("BRL"), Currency.newInstance("BRL")),
    Canada("/images/16x16/ca.png", "Canada", Currency.newInstance("CAD"), Currency.newInstance("CAD")),
    China("/images/16x16/cn.png", "China", Currency.newInstance("CNY"), Currency.newInstance("CNY")),
    Czech("/images/16x16/cz.png", "Czech Republic", Currency.newInstance("CZK"), Currency.newInstance("CZK")),
    Denmark("/images/16x16/dk.png", "Denmark", Currency.newInstance("DKK"), Currency.newInstance("DKK")),
    France("/images/16x16/fr.png", "France", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    Germany("/images/16x16/de.png", "Germany", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    HongKong("/images/16x16/hk.png", "Hong Kong", Currency.newInstance("HKD"), Currency.newInstance("HKD")),
    Hungary("/images/16x16/hu.png", "Hungary", Currency.newInstance("HUF"), Currency.newInstance("HUF")),
    India("/images/16x16/in.png", "India", Currency.newInstance("INR"), Currency.newInstance("INR")),
    Indonesia("/images/16x16/id.png", "Indonesia", Currency.newInstance("IDR"), Currency.newInstance("IDR")),
    Israel("/images/16x16/il.png", "Israel", Currency.newInstance("ILS"), Currency.newInstance("ILS")),
    Italy("/images/16x16/it.png", "Italy", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    Korea("/images/16x16/kr.png", "Korea", Currency.newInstance("KPW"), Currency.newInstance("KPW")),
    Malaysia("/images/16x16/my.png", "Malaysia", Currency.newInstance("MYR"), Currency.newInstance("MYR")),
    Netherlands("/images/16x16/nl.png", "Netherlands", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    NewZealand("/images/16x16/nz.png", "New Zealand", Currency.newInstance("NZD"), Currency.newInstance("NZD")),
    Norway("/images/16x16/no.png", "Norway", Currency.newInstance("NOK"), Currency.newInstance("NOK")),
    Portugal("/images/16x16/pt.png", "Portugal", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    Singapore("/images/16x16/sg.png", "Singapore", Currency.newInstance("SGD"), Currency.newInstance("SGD")),
    Spain("/images/16x16/es.png", "Spain", Currency.newInstance("EUR"), Currency.newInstance("EUR")),
    Sweden("/images/16x16/se.png", "Sweden", Currency.newInstance("SEK"), Currency.newInstance("SEK")),
    Switzerland("/images/16x16/ch.png", "Switzerland", Currency.newInstance("CHF"), Currency.newInstance("CHF")),
    Taiwan("/images/16x16/tw.png", "Taiwan", Currency.newInstance("TWD"), Currency.newInstance("TWD")),
    UnitedKingdom("/images/16x16/gb.png", "United Kingdom", Currency.newInstance("GBX"), Currency.newInstance("GBP")),
    UnitedState("/images/16x16/us.png", "United States", Currency.newInstance("USD"), Currency.newInstance("USD"));
            
    Country(String fileName, String humanString, Currency stockCurrency, Currency localCurrency) {
        this.icon = new javax.swing.ImageIcon(this.getClass().getResource(fileName));
        this.humanString = humanString;
        this.stockCurrency = stockCurrency;
        this.localCurrency = localCurrency;
    }
    
    // For legacy reason, when generating path or operation, the following code
    // should be used :
    // String path = country.name() + File.separator
    //
    // However, we make a mistake by using
    // String path = country + File.separator
    //
    // That's why we avoid from override toString. Instead, we provide
    // toHumanReadableString
    //
    //@Override
    //public String toString() {
    //    return string;
    //}
    
    public final ImageIcon icon;
    public final String humanString; 
    public final Currency stockCurrency;
    public final Currency localCurrency;
}
