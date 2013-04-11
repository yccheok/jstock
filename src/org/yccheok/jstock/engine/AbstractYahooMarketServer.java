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
import java.util.EnumMap;
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
    private final StockServer stockServer;
    private final Country country;
    private final List<Index> indicies;
    private final List<Code> codes = new ArrayList<Code>();
    private final Map<Code, Index> codeToIndexMap = new HashMap<Code, Index>();

    private static final Log log = LogFactory.getLog(AbstractYahooMarketServer.class);

    protected abstract StockServer getStockServer(Country country);

    public AbstractYahooMarketServer(Country country) {
        this.country = country;
        this.stockServer = getStockServer(country);       
        /* Hack on Malaysia Market! The format among Yahoo and CIMB are difference. */
        if (country == Country.Malaysia) {
            final List<Index> tmp = new ArrayList<Index>();
            for (Index index : Utils.getStockIndices(country)) {
                if (index.code.toString().startsWith("^")) {
                    tmp.add(index);
                }
            }
            this.indicies = java.util.Collections.unmodifiableList(tmp);
        }
        else {
            this.indicies = Utils.getStockIndices(country);
        }
        if (this.indicies.isEmpty()) {
            throw new java.lang.IllegalArgumentException(country.toString());
        }
        for (Index index : indicies) {
            codes.add(index.code);
            codeToIndexMap.put(index.code, index);
        }
    }

    @Override
    public Market getMarket() {
        try {
            return new YahooMarket();
        }
        catch (StockNotFoundException exp) {
            log.error(null, exp);
        }

        return null;
    }

    private final class YahooMarket implements Market {
        private final Map<Index, Stock> map = new EnumMap<Index, Stock>(Index.class);
        
        public YahooMarket() throws StockNotFoundException {
            List<Stock> stocks;

            try {
                stocks = AbstractYahooMarketServer.this.stockServer.getStocks(codes);
            } catch (StockNotFoundException ex) {
                throw ex;
            }

            for (Stock stock : stocks) {
                map.put(codeToIndexMap.get(stock.code), stock);
            }
        }

        @Override
        public double getIndex(Index index) {
            final Stock stock = map.get(index);
            if (stock == null) {
                return 0.0;
            }
            return stock.getLastPrice();
        }

        @Override
        public double getChange(Index index) {
            final Stock stock = map.get(index);
            if (stock == null) {
                return 0.0;
            }
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
}
