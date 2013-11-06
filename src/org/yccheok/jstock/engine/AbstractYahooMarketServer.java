/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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
 *
 * @author yccheok
 */
public abstract class AbstractYahooMarketServer implements MarketServer {
    
    protected abstract StockServer getStockServer();
    
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
    
    @Override
    public java.util.List<Market> getMarkets(java.util.List<Index> indices) {
        assert(false == indices.isEmpty());
        
        StockServer stockServer = AbstractYahooMarketServer.this.getStockServer();
        
        List<Code> codes = new ArrayList<Code>();
        for (Index index : indices) {
            codes.add(index.code);
        }
        
        List<Stock> stocks = null;
        try {
            stocks = stockServer.getStocks(codes);
        } catch (StockNotFoundException e) {
            log.error(null, e);
            return java.util.Collections.emptyList();
        }
        
        Map<Code, Stock> map = new HashMap<Code, Stock>();
        for (Stock stock : stocks) {
            map.put(stock.code, stock);
        }
        
        List<Market> markets = new ArrayList<Market>();
        for (Index index : indices) {
            Stock stock = map.get(index.code);
            if (stock != null) {
                Market market = Market.newInstance(stock, index);
                markets.add(market);
            }
        }
        
        return markets;
    }
    
    private static final Log log = LogFactory.getLog(AbstractYahooMarketServer.class);
}
