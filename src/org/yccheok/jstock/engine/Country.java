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

import javax.swing.ImageIcon;

/**
 *
 * @author yccheok
 */
public enum Country {
    Australia("/images/16x16/au.png", "Australia"),
    Austria("/images/16x16/at.png", "Austria"),
    Belgium("/images/16x16/be.png", "Belgium"),
    Brazil("/images/16x16/br.png", "Brazil"),
    Canada("/images/16x16/ca.png", "Canada"),
    China("/images/16x16/cn.png", "China"),
    Czech("/images/16x16/cz.png", "Czech Republic"),
    Denmark("/images/16x16/dk.png", "Denmark"),
    France("/images/16x16/fr.png", "France"),
    Germany("/images/16x16/de.png", "Germany"),
    HongKong("/images/16x16/hk.png", "Hong Kong"),
    Hungary("/images/16x16/hu.png", "Hungary"),
    India("/images/16x16/in.png", "India"),
    Indonesia("/images/16x16/id.png", "Indonesia"),
    Israel("/images/16x16/il.png", "Israel"),
    Italy("/images/16x16/it.png", "Italy"),
    Korea("/images/16x16/kr.png", "Korea"),
    Malaysia("/images/16x16/my.png", "Malaysia"),
    Netherlands("/images/16x16/nl.png", "Netherlands"),
    NewZealand("/images/16x16/nz.png", "New Zealand"),
    Norway("/images/16x16/no.png", "Norway"),
    Portugal("/images/16x16/pt.png", "Portugal"),
    Singapore("/images/16x16/sg.png", "Singapore"),
    Spain("/images/16x16/es.png", "Spain"),            
    Sweden("/images/16x16/se.png", "Sweden"),
    Switzerland("/images/16x16/ch.png", "Switzerland"),
    Taiwan("/images/16x16/tw.png", "Taiwan"),
    UnitedKingdom("/images/16x16/gb.png", "United Kingdom"),
    UnitedState("/images/16x16/us.png", "United States");
            
    Country(String fileName, String humanReadableString) {
        this.icon = new javax.swing.ImageIcon(this.getClass().getResource(fileName));
        this.humanReadableString = humanReadableString;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public String toHumanReadableString() {
        return humanReadableString;
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
    
    private ImageIcon icon;
    private final String humanReadableString;    
}
