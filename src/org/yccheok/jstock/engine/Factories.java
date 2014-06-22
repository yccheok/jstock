/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author yccheok
 */
public enum Factories {
    INSTANCE;
          
    public List<StockServerFactory> getStockServerFactories(Index index) {
        Country country = index.country;
        return getStockServerFactories(country);
    }
    
    public List<StockServerFactory> getStockServerFactories(Code code) {
        Country country = Utils.toCountry(code);
        return getStockServerFactories(country);
    }
    
    public List<StockServerFactory> getStockServerFactories(Country country) {
        List<StockServerFactory> list = map.get(country);
        if (list != null) {
            return java.util.Collections.unmodifiableList(list);
        }
        
        return java.util.Collections.emptyList();
    }
    
    public void updatePriceSource(Country country, final PriceSource priceSource) {
        final String name = priceSource.name().toLowerCase();
        
        final List<StockServerFactory> stockServerFactories = map.get(country);
        
        if (null == stockServerFactories) {
            return;
        }
        
        // It isn't possible to perform sorting directly on CopyOnWriteArrayList.
        // at java.util.concurrent.CopyOnWriteArrayList$COWIterator.set(CopyOnWriteArrayList.java:1049)
        final List<StockServerFactory> tmp = new ArrayList<StockServerFactory>(stockServerFactories);
        
        Collections.sort(tmp, new Comparator<StockServerFactory>() {

            @Override
            public int compare(StockServerFactory o1, StockServerFactory o2) {
                final String name1 = o1.getClass().getName().toLowerCase();
                final String name2 = o2.getClass().getName().toLowerCase();
                final boolean result1 = name1.contains(name);
                final boolean result2 = name2.contains(name);
                if (result1 && !result2) {
                    return -1;
                }
                
                if (!result1 && result2) {
                    return 1;
                }
                
                return 0;
            }
        });
        
        // Copy back to CopyOnWriteArrayList.
        for (int i = 0, ei = tmp.size(); i < ei; i++) {
            StockServerFactory f = tmp.get(i);
            if (f != stockServerFactories.get(i)) {
                stockServerFactories.set(i, f);
            }
        }
    }
    
    private static final Map<Country, List<StockServerFactory>> map = new EnumMap<Country, List<StockServerFactory>>(Country.class);
    
    static {
        final List<StockServerFactory> australiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> austriaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> belgiumList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> brazilList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> canadaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> chinaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> denmarkList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> franceList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> germanyList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> hongkongList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> indiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> indonesiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> israelList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> italyList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> koreaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> malaysiaList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> netherlandsList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> newZealandList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> norwayList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> portugalList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> singaporeList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> spainList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> swedenList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> switzerlandList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> taiwanList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> unitedKingdomList = new CopyOnWriteArrayList<StockServerFactory>();
        final List<StockServerFactory> unitedStateList = new CopyOnWriteArrayList<StockServerFactory>();

        australiaList.add(YahooStockServerFactory.newInstance());
        austriaList.add(YahooStockServerFactory.newInstance());
        austriaList.add(GoogleStockServerFactory.newInstance(false));
        belgiumList.add(YahooStockServerFactory.newInstance());
        brazilList.add(BrazilYahooStockServerFactory.newInstance());
        brazilList.add(GoogleStockServerFactory.newInstance(false));
        canadaList.add(YahooStockServerFactory.newInstance());
        chinaList.add(GoogleStockServerFactory.newInstance(true));
        chinaList.add(YahooStockServerFactory.newInstance());
        denmarkList.add(YahooStockServerFactory.newInstance());
        franceList.add(YahooStockServerFactory.newInstance());
        germanyList.add(YahooStockServerFactory.newInstance());
        hongkongList.add(YahooStockServerFactory.newInstance());
        indiaList.add(GoogleStockServerFactory.newInstance(true));
        indonesiaList.add(YahooStockServerFactory.newInstance());
        israelList.add(YahooStockServerFactory.newInstance());
        italyList.add(YahooStockServerFactory.newInstance());
        koreaList.add(YahooStockServerFactory.newInstance());
        malaysiaList.add(KLSEInfoStockServerFactory.newInstance());
        malaysiaList.add(YahooStockServerFactory.newInstance());
        netherlandsList.add(YahooStockServerFactory.newInstance());
        newZealandList.add(YahooStockServerFactory.newInstance());
        norwayList.add(YahooStockServerFactory.newInstance());
        portugalList.add(YahooStockServerFactory.newInstance());
        singaporeList.add(YahooStockServerFactory.newInstance());
        spainList.add(YahooStockServerFactory.newInstance());
        swedenList.add(YahooStockServerFactory.newInstance());
        switzerlandList.add(YahooStockServerFactory.newInstance());
        taiwanList.add(YahooStockServerFactory.newInstance());
        unitedKingdomList.add(GoogleStockServerFactory.newInstance(false));
        unitedKingdomList.add(YahooStockServerFactory.newInstance());
        unitedStateList.add(GoogleStockServerFactory.newInstance(false));
        unitedStateList.add(YahooStockServerFactory.newInstance());

        map.put(Country.Australia, australiaList);
        map.put(Country.Austria, austriaList);
        map.put(Country.Belgium, belgiumList);
        map.put(Country.Brazil, brazilList);
        map.put(Country.Canada, canadaList);
        map.put(Country.China, chinaList);
        map.put(Country.Denmark, denmarkList);
        map.put(Country.France, franceList);
        map.put(Country.Germany, germanyList);
        map.put(Country.HongKong, hongkongList);
        map.put(Country.India, indiaList);
        map.put(Country.Indonesia, indonesiaList);
        map.put(Country.Israel, israelList);
        map.put(Country.Italy, italyList);
        map.put(Country.Korea, koreaList);
        map.put(Country.Malaysia, malaysiaList);
        map.put(Country.Netherlands, netherlandsList);
        map.put(Country.NewZealand, newZealandList);
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
