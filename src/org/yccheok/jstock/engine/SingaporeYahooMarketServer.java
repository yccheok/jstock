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
public class SingaporeYahooMarketServer implements MarketServer {
    public SingaporeYahooMarketServer(Country country) {
        if(country != Country.Malaysia && country != Country.Singapore) {
            throw new java.lang.IllegalArgumentException("Only Malaysia and Singapore market are supported");
        }

        this.indicies = Utils.getStockIndices(country);

        if(this.indicies.size() == 0) {
            throw new java.lang.IllegalArgumentException("Country=" + country);
        }

        this.country = country;
        this.stockServer = new SingaporeYahooStockServer(country);

        for(Index index : indicies) {
            symbols.add(index.getSymbol());
            symbolToIndexMap.put(index.getSymbol(), index);
        }
    }

    public Market getMarket() {
        try {
            return new SingaporeYahooMarket();
        }
        catch(StockNotFoundException exp) {
            log.error("", exp);
        }

        return null;
    }

    public Country country() {
        return country;
    }

    private final class SingaporeYahooMarket implements Market {
        private final Map<Index, Stock> map = new HashMap<Index, Stock>();

        public SingaporeYahooMarket() throws StockNotFoundException {
            List<Stock> stocks;

            try {
                stocks = stockServer.getStocksBySymbols(symbols);
            } catch (StockNotFoundException ex) {
                throw ex;
            }

            for(Stock stock : stocks) {
                map.put(symbolToIndexMap.get(stock.getSymbol()), stock);
            }
        }

        public double getIndex(Index index) {
            final Stock stock = map.get(index);
            if(stock == null) return 0.0;

            return stock.getLastPrice();
        }

        public double getChange(Index index) {
            final Stock stock = map.get(index);
            if(stock == null) return 0.0;

            return stock.getChangePrice();
        }

        public int getNumOfStockChange(ChangeType type) {
            return 0;
        }

        public long getVolume() {
            long total = 0;

            for(Stock stock : map.values()) {
                total += stock.getVolume();
            }

            return total;
        }

        public double getValue() {
            double total = 0;

            for(Stock stock : map.values()) {
                total += stock.getLastPrice();
            }

            return total;
        }

        public Country getCountry() {
            return country;
        }
    }

    private static final Log log = LogFactory.getLog(SingaporeYahooMarketServer.class);

    private final Country country;
    private final List<Index> indicies;
    private final List<Symbol> symbols = new ArrayList<Symbol>();
    private final StockServer stockServer;
    private final Map<Symbol, Index> symbolToIndexMap = new HashMap<Symbol, Index>();
}