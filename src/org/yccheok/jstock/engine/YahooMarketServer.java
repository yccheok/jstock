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
public class YahooMarketServer implements MarketServer {
    public YahooMarketServer(Country country) {
        // YahooMarketServer doesn't support Malaysia market at this moment.
        if (country == Country.Malaysia) {
            throw new java.lang.IllegalArgumentException("Malaysia market not supported");
        }
        
        this.indicies = Utils.getStockIndices(country);
        
        if (this.indicies.size() == 0) {
            throw new java.lang.IllegalArgumentException("Country=" + country);            
        }
        
        this.country = country;
        this.stockServer = new YahooStockServer(country);
        
        for (Index index : indicies) {
            codes.add(index.getCode());
            codeToIndexMap.put(index.getCode(), index);
        }
    }
    
    @Override
    public Market getMarket() {      
        try {
            return new YahooMarket();
        }
        catch (StockNotFoundException exp) {
            log.error("", exp);
        }
        
        return null;
    }
    
    public Country country() {
        return country;
    }
    
    private final class YahooMarket implements Market {
        private final Map<Index, Stock> map = new HashMap<Index, Stock>();
        
        public YahooMarket() throws StockNotFoundException {   
            List<Stock> stocks;
            
            try {
                stocks = stockServer.getStocksByCodes(codes);
            } catch (StockNotFoundException ex) {
                throw ex;
            }
        
            for (Stock stock : stocks) {
                map.put(codeToIndexMap.get(stock.getCode()), stock);
            }
        }
        
        @Override
        public double getIndex(Index index) {
            final Stock stock = map.get(index);
            if (stock == null) return 0.0;
            
            return stock.getLastPrice();
        }

        @Override
        public double getChange(Index index) {
            final Stock stock = map.get(index);
            if (stock == null) return 0.0;
            
            return stock.getChangePrice();
        }

        @Override
        public int getNumOfStockChange(ChangeType type) {
            return 0;
        }

        @Override
        public long getVolume() {
            long total = 0;
            
            for (Stock stock : map.values()) {
                total += stock.getVolume();
            }
            
            return total;
        }

        @Override
        public double getValue() {
            double total = 0;
            
            for (Stock stock : map.values()) {
                total += stock.getLastPrice();
            }
            
            return total;
        }

        @Override
        public Country getCountry() {
            return country;
        }        
    }
    
    private static final Log log = LogFactory.getLog(YahooMarketServer.class);
    
    private final Country country;
    private final List<Index> indicies;
    private final List<Code> codes = new ArrayList<Code>();
    private final StockServer stockServer;
    private final Map<Code, Index> codeToIndexMap = new HashMap<Code, Index>();
}
