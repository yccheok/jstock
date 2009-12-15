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
 * Copyright (C) 2008 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class BrazilYahooStockServerFactory implements StockServerFactory {

    private BrazilYahooStockServerFactory(Country country) {
        this.country = country;
        stockServer = new BrazilYahooStockServer(country);
        marketServer = new BrazilYahooMarketServer(country);
    }

    public static StockServerFactory newInstance(Country country) {
        return new BrazilYahooStockServerFactory(country);
    }

    @Override
    public StockServer getStockServer() {
        return stockServer;
    }

    @Override
    public StockHistoryServer getStockHistoryServer(Code code) {
        try {
            return new BrazilYahooStockHistoryServer(country, code);
        }
        catch(StockHistoryNotFoundException exp) {
            log.error("", exp);
            return null;
        }
    }

    @Override
    public StockHistoryServer getStockHistoryServer(Code code, org.yccheok.jstock.engine.Duration duration) {
        try {
            return new BrazilYahooStockHistoryServer(country, code, duration);
        }
        catch(StockHistoryNotFoundException exp) {
            log.error(null, exp);
            return null;
        }
    }

    @Override
    public MarketServer getMarketServer() {
        return marketServer;
    }

    private final StockServer stockServer;
    private final MarketServer marketServer;
    private final Country country;

    private static final Log log = LogFactory.getLog(BrazilYahooStockServerFactory.class);
}