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

package org.yccheok.jstock.gui;

import java.util.EnumMap;
import java.util.Map;
import org.yccheok.jstock.engine.Country;

/**
 *
 * @author yccheok
 */
public enum Continent {
    Africa,
    America,
    Asia,
    Australia,    
    Europe;    

    public static Continent toContinent(Country country) {
        return map.get(country);
    }
    
    private static final Map<Country, Continent> map = new EnumMap<>(Country.class);
    static {
        map.put(Country.Argentina, America);
        map.put(Country.Australia, Australia);
        map.put(Country.Austria, Europe);
        map.put(Country.Belgium, Europe);        
        map.put(Country.Brazil, America);
        map.put(Country.Canada, America);
        map.put(Country.China, Asia);
        map.put(Country.Czech, Europe);
        map.put(Country.Denmark, Europe);
        map.put(Country.Finland, Europe);
        map.put(Country.France, Europe);
        map.put(Country.Germany, Europe);
        map.put(Country.HongKong, Asia);
        map.put(Country.Hungary, Europe);
        map.put(Country.India, Asia);
        map.put(Country.Indonesia, Asia);
        map.put(Country.Israel, Europe);
        map.put(Country.Italy, Europe);
        map.put(Country.Japan, Asia);
        map.put(Country.Korea, Asia);
        map.put(Country.Malaysia, Asia);
        map.put(Country.Mexico, America);
        map.put(Country.Netherlands, Europe);
        map.put(Country.NewZealand, Australia);
        map.put(Country.Norway, Europe);
        map.put(Country.Poland, Europe);
        map.put(Country.Portugal, Europe);
        map.put(Country.Russia, Asia);
        map.put(Country.SaudiArabia, Asia);
        map.put(Country.Singapore, Asia);
        map.put(Country.SouthAfrica, Africa);
        map.put(Country.Spain, Europe);
        map.put(Country.Sweden, Europe);
        map.put(Country.Switzerland, Europe);
        map.put(Country.Taiwan, Asia);
        map.put(Country.Thailand, Asia);
        map.put(Country.Turkey, Europe);
        map.put(Country.UnitedKingdom, Europe);
        map.put(Country.UnitedState, America);
    }
}