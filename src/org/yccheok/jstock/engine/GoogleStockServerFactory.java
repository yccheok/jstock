/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng Cheok <yccheok@yahoo.com>
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
 * Provides all Google servers, by using abstract factory pattern. Currently,
 * we only support MarketServer. For rest of the servers, we will either return
 * null or empty server.
 *
 * @author yccheok
 */
public class GoogleStockServerFactory implements StockServerFactory {

    private GoogleStockServerFactory(boolean useStockServer, boolean useMarketServer) {
        if (useStockServer) {
            stockServer = new GoogleStockServer();
        } else {
            stockServer = null;
        }        
        
        if (useMarketServer) {
             marketServer = new GoogleMarketServer();
        } else {
            marketServer = null;
        }
    }
    
    public static StockServerFactory newInstance(boolean useStockServer, boolean useMarketServer) {
        return new GoogleStockServerFactory(useStockServer, useMarketServer);
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
        try {
            return new GoogleStockHistoryServer(code);
        }
        catch (StockHistoryNotFoundException exp) {
            log.error(null, exp);
            return null;
        }
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
        try {
            return new GoogleStockHistoryServer(code, duration);
        }
        catch (StockHistoryNotFoundException exp) {
            log.error(null, exp);
            return null;
        }
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

    @Override
    public DividendServer getDividendServer() {
        return null;
    }
    
    private final StockServer stockServer;
    private final MarketServer marketServer;
    
    private static final Log log = LogFactory.getLog(GoogleStockServerFactory.class);
}
