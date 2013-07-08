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

/**
 * Provides all Google servers, by using abstract factory pattern. Currently,
 * we only support MarketServer. For rest of the servers, we will either return
 * null or empty server.
 *
 * @author yccheok
 */
public class GoogleStockServerFactory implements StockServerFactory {

    private GoogleStockServerFactory(Country country) {
        marketServer = new GoogleMarketServer(country);
        this.country = country;
        if (country == Country.India) {
            stockServer = new GoogleStockServer(country);
        } else {
            stockServer = null;
        }        
    }

    public Country getCountry() {
        return country;
    }
    
    /**
     * Returns GoogleStockServerFactory based on given country.
     *
     * @param country the country
     * @return GoogleStockServerFactory based on given country
     */
    public static StockServerFactory newInstance(Country country) {
        return new GoogleStockServerFactory(country);
    }

    /**
     * Returns stock server for this factory.
     *
     * @return stock server for this factory
     */
    @Override
    public StockServer getStockServer() {
        return stockServer;
    }

    /**
     * Returns stock history server for this factory based on given code. <code>
     * null</code> will be returned if fail.
     *
     * @param code the code
     * @return stock history server for this factory based on given code. <code>
     * null</code> will be returned if fail
     */
    @Override
    public StockHistoryServer getStockHistoryServer(Code code) {
        return null;
    }

    /**
     * Returns stock history server for this factory based on given code and 
     * duration. <code>null</code> will be returned if fail.
     * 
     * @param code the code
     * @param duration the duration
     * @return stock history server for this factory based on given code and 
     * duration. <code>null</code> will be returned if fail
     */
    @Override
    public StockHistoryServer getStockHistoryServer(Code code, Duration duration) {
        return null;
    }

    /**
     * Returns market server for this factory.
     *
     * @return market server for this factory
     */
    @Override
    public MarketServer getMarketServer() {
        return marketServer;
    }

    private final MarketServer marketServer;
    private final StockServer stockServer;
    private final Country country;
}
