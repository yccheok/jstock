/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public void updatePrimaryStockServerFactory(Country country, Class c) {
        final List<StockServerFactory> stockServerFactories = map.get(country);
        int index = 0;
        for (StockServerFactory stockServerFactory : stockServerFactories) {
            if (stockServerFactory.getClass().equals(c)) {
                StockServerFactory tmp = stockServerFactories.get(0);
                stockServerFactories.set(0, stockServerFactory);
                stockServerFactories.set(index, tmp);
                break;
            }
            index++;
        }
    }

    private static final Map<Country, List<StockServerFactory>> map = new HashMap<Country, List<StockServerFactory>>();
    
    static {
        final List<StockServerFactory> australiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> austriaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> belgiumList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> canadaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> denmarkList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> franceList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> germanyList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> hongkongList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> indiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> indonesiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> italyList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> koreaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> malaysiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> netherlandsList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> norwayList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> portugalList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> singaporeList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> spainList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> swedenList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> switzerlandList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> taiwanList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> unitedKingdomList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> unitedStateList = new CopyOnWriteArrayList<StockServerFactory>();

        australiaList.add(YahooStockServerFactory.newInstance(Country.Australia));
        austriaList.add(YahooStockServerFactory.newInstance(Country.Austria));
        belgiumList.add(YahooStockServerFactory.newInstance(Country.Belgium));
        canadaList.add(YahooStockServerFactory.newInstance(Country.Canada));
        denmarkList.add(YahooStockServerFactory.newInstance(Country.Denmark));
        franceList.add(YahooStockServerFactory.newInstance(Country.France));
        germanyList.add(YahooStockServerFactory.newInstance(Country.Germany));
        hongkongList.add(SingaporeYahooStockServerFactory.newInstance(Country.HongKong));
        indiaList.add(YahooStockServerFactory.newInstance(Country.India));
        indonesiaList.add(SingaporeYahooStockServerFactory.newInstance(Country.Indonesia));
        italyList.add(YahooStockServerFactory.newInstance(Country.Italy));
        koreaList.add(SingaporeYahooStockServerFactory.newInstance(Country.Korea));
        malaysiaList.add(XStockServerFactory.newInstance());
        malaysiaList.add(SingaporeYahooStockServerFactory.newInstance(Country.Malaysia));
        netherlandsList.add(YahooStockServerFactory.newInstance(Country.Netherlands));
        norwayList.add(YahooStockServerFactory.newInstance(Country.Norway));
        portugalList.add(YahooStockServerFactory.newInstance(Country.Portugal));
        singaporeList.add(SingaporeYahooStockServerFactory.newInstance(Country.Singapore));
        spainList.add(YahooStockServerFactory.newInstance(Country.Spain));
        swedenList.add(YahooStockServerFactory.newInstance(Country.Sweden));
        switzerlandList.add(YahooStockServerFactory.newInstance(Country.Switzerland));
        taiwanList.add(SingaporeYahooStockServerFactory.newInstance(Country.Taiwan));
        unitedKingdomList.add(YahooStockServerFactory.newInstance(Country.UnitedKingdom));
        unitedStateList.add(YahooStockServerFactory.newInstance(Country.UnitedState));

        map.put(Country.Australia, australiaList);
        map.put(Country.Austria, austriaList);
        map.put(Country.Belgium, belgiumList);
        map.put(Country.Canada, canadaList);
        map.put(Country.Denmark, denmarkList);
        map.put(Country.France, franceList);
        map.put(Country.Germany, germanyList);
        map.put(Country.HongKong, hongkongList);
        map.put(Country.India, indiaList);
        map.put(Country.Indonesia, indonesiaList);
        map.put(Country.Italy, italyList);
        map.put(Country.Korea, koreaList);
        map.put(Country.Malaysia, malaysiaList);
        map.put(Country.Netherlands, netherlandsList);
        map.put(Country.Norway, norwayList);
        map.put(Country.Portugal, portugalList);
        map.put(Country.Singapore, singaporeList);
        map.put(Country.Spain, spainList);
        map.put(Country.Sweden, swedenList);
        map.put(Country.Switzerland, switzerlandList);
        map.put(Country.Taiwan, taiwanList);
        map.put(Country.UnitedKingdom, unitedKingdomList);
        map.put(Country.UnitedState, unitedStateList);
    }
}
