/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class YahooStockServerFactory implements StockServerFactory {

    private YahooStockServerFactory(Country country) {
        this.country = country;
        stockServer = new YahooStockServer(country);
        marketServer = new YahooMarketServer(country);
        dividendServer = new YahooDividendServer();
    }
    
    public static StockServerFactory newInstance(Country country) {
        return new YahooStockServerFactory(country);
    }

    @Override
    public StockServer getStockServer() {
        return stockServer;
    }

    @Override
    public StockHistoryServer getStockHistoryServer(Code code) {
        try {
            return new YahooStockHistoryServer(country, code);
        }
        catch (StockHistoryNotFoundException exp) {
            log.error(null, exp);
            return null;
        }
    }

    @Override
    public StockHistoryServer getStockHistoryServer(Code code,org.yccheok.jstock.engine.Duration duration) {
        try {
            return new YahooStockHistoryServer(country, code, duration);
        }
        catch (StockHistoryNotFoundException exp) {
            log.error(null, exp);
            return null;
        }
    }
    
    @Override
    public MarketServer getMarketServer() {
        return marketServer;
    }
    
    public Country getCountry() {
        return country;
    }
    
    @Override
    public DividendServer getDividendServer() {
        return dividendServer;
    }
    
    private final StockServer stockServer;
    private final MarketServer marketServer;
    private final Country country;
    private final DividendServer dividendServer;
    
    private static final Log log = LogFactory.getLog(YahooStockServerFactory.class);
}
