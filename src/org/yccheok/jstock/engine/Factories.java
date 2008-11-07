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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yccheok
 */
public enum Factories {
    INSTANCE;
            
    public List<StockServerFactory> getStockServerFactories(Country country) {
        List<StockServerFactory> list = map.get(country);
        if(list != null) {
            return java.util.Collections.unmodifiableList(list);
        }
        
        return java.util.Collections.emptyList();
    }
    
    private static final Map<Country, List<StockServerFactory>> map = new HashMap<Country, List<StockServerFactory>>();
    
    static {
        final List<StockServerFactory> denmarkList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> franceList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> germanyList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> italyList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> malaysiaList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> norwayList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> spainList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> sweedenList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> unitedKingdomList = new ArrayList<StockServerFactory>();
        final List<StockServerFactory> unitedStateList = new ArrayList<StockServerFactory>();
        
        denmarkList.add(YahooDenmarkStockServerFactory.newInstance());
        franceList.add(YahooFranceStockServerFactory.newInstance());
        germanyList.add(YahooGermanyStockServerFactory.newInstance());
        italyList.add(YahooItalyStockServerFactory.newInstance());
        malaysiaList.add(CIMBStockServerFactory.newInstance("", ""));
        norwayList.add(YahooNorwayStockServerFactory.newInstance());
        spainList.add(YahooSpainStockServerFactory.newInstance());
        sweedenList.add(YahooSweedenStockServerFactory.newInstance());
        unitedKingdomList.add(YahooUKStockServerFactory.newInstance());
        unitedStateList.add(YahooUSStockServerFactory.newInstance());
        
        map.put(Country.Denmark, denmarkList);
        map.put(Country.France, franceList);
        map.put(Country.Germany, germanyList);
        map.put(Country.Italy, italyList);
        map.put(Country.Malaysia, malaysiaList);
        map.put(Country.Norway, norwayList);
        map.put(Country.Spain, spainList);
        map.put(Country.Sweeden, sweedenList);
        map.put(Country.UnitedKingdom, unitedKingdomList);
        map.put(Country.UnitedState, unitedStateList);
    }
}
