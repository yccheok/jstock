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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Retrieves stock market index information through Google Finance.
 *
 * @author yccheok
 */
public class GoogleMarketServer implements MarketServer {
    
    @Override
    public Market getMarket(Index index) {
        List<Index> indices = new ArrayList<Index>();
        indices.add(index);
        java.util.List<Market> markets = getMarkets(indices);
        if (markets.size() == 1) {
            return markets.get(0);
        }
        return null;
    }
    
    public java.util.List<Market> getMarkets(java.util.List<Index> indices) {
        
        Map<Code, Index> map = new HashMap<Code, Index>();
        List<Code> codes = new ArrayList<Code>();

        for (Index index : indices) {
            map.put(index.code, index);
            codes.add(index.code);
        }

        List<Market> markets = new ArrayList<Market>();
        
        List<Stock> stocks;
        try {
            stocks = googleStockServer.getStocks(codes);
        } catch (StockNotFoundException e) {
            log.error(null, e);
            return markets;
        }
        
        for (Stock stock : stocks) {
            final Market market = Market.newInstance(stock, map.get(stock.code));
            markets.add(market);
        }
        
        return markets;
    }

    private final GoogleStockServer googleStockServer = new GoogleStockServer();
    private static final Log log = LogFactory.getLog(GoogleMarketServer.class);
}
