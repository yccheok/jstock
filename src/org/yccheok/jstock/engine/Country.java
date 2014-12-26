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

package org.yccheok.jstock.engine;

import java.util.Currency;
import javax.swing.ImageIcon;

/**
 *
 * @author yccheok
 */
public enum Country {
    Australia("/images/16x16/au.png", "Australia", Currency.getInstance("AUD")),
    Austria("/images/16x16/at.png", "Austria", Currency.getInstance("EUR")),
    Belgium("/images/16x16/be.png", "Belgium", Currency.getInstance("EUR")),
    Brazil("/images/16x16/br.png", "Brazil", Currency.getInstance("BRL")),
    Canada("/images/16x16/ca.png", "Canada", Currency.getInstance("CAD")),
    China("/images/16x16/cn.png", "China", Currency.getInstance("CNY")),
    Czech("/images/16x16/cz.png", "Czech Republic", Currency.getInstance("CZK")),
    Denmark("/images/16x16/dk.png", "Denmark", Currency.getInstance("DKK")),
    France("/images/16x16/fr.png", "France", Currency.getInstance("EUR")),
    Germany("/images/16x16/de.png", "Germany", Currency.getInstance("EUR")),
    HongKong("/images/16x16/hk.png", "Hong Kong", Currency.getInstance("HKD")),
    Hungary("/images/16x16/hu.png", "Hungary", Currency.getInstance("HUF")),
    India("/images/16x16/in.png", "India", Currency.getInstance("INR")),
    Indonesia("/images/16x16/id.png", "Indonesia", Currency.getInstance("IDR")),
    Israel("/images/16x16/il.png", "Israel", Currency.getInstance("ILS")),
    Italy("/images/16x16/it.png", "Italy", Currency.getInstance("EUR")),
    Korea("/images/16x16/kr.png", "Korea", Currency.getInstance("KPW")),
    Malaysia("/images/16x16/my.png", "Malaysia", Currency.getInstance("MYR")),
    Netherlands("/images/16x16/nl.png", "Netherlands", Currency.getInstance("EUR")),
    NewZealand("/images/16x16/nz.png", "New Zealand", Currency.getInstance("NZD")),
    Norway("/images/16x16/no.png", "Norway", Currency.getInstance("NOK")),
    Portugal("/images/16x16/pt.png", "Portugal", Currency.getInstance("EUR")),
    Singapore("/images/16x16/sg.png", "Singapore", Currency.getInstance("SGD")),
    Spain("/images/16x16/es.png", "Spain", Currency.getInstance("EUR")),            
    Sweden("/images/16x16/se.png", "Sweden", Currency.getInstance("SEK")),
    Switzerland("/images/16x16/ch.png", "Switzerland", Currency.getInstance("CHF")),
    Taiwan("/images/16x16/tw.png", "Taiwan", Currency.getInstance("TWD")),
    UnitedKingdom("/images/16x16/gb.png", "United Kingdom", Currency.getInstance("GBP")),
    UnitedState("/images/16x16/us.png", "United States", Currency.getInstance("USD"));
            
    Country(String fileName, String humanReadableString, Currency currency) {
        this.icon = new javax.swing.ImageIcon(this.getClass().getResource(fileName));
        this.humanReadableString = humanReadableString;
        this.currency = currency;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public String toHumanReadableString() {
        return humanReadableString;
    }
    
    public Currency getCurrency() {
        return currency;
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
    
    private final ImageIcon icon;
    private final String humanReadableString; 
    private final Currency currency;
}
