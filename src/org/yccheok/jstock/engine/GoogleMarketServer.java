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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Retrieves stock market index information through Google Finance.
 *
 * @author yccheok
 */
public class GoogleMarketServer implements MarketServer {
    private final Country country;
    private final List<Index> indicies;
    private final List<Code> codes = new ArrayList<Code>();
    private final Map<Code, Index> codeToIndexMap = new HashMap<Code, Index>();

    private static final Log log = LogFactory.getLog(GoogleMarketServer.class);

    /**
     * Constructs a stock market index server based on country.
     *
     * @param country the country
     */
    public GoogleMarketServer(Country country) {
        this.country = country;
        this.indicies = Utils.getStockIndices(country);
        if (this.indicies.isEmpty()) {
            throw new java.lang.IllegalArgumentException(country.toString());
        }
        for (Index index : indicies) {
            codes.add(index.getCode());
            codeToIndexMap.put(index.getCode(), index);
        }
    }
    
    /**
     * Returns current stock market index information.
     *
     * @return current stock market index information
     */
    @Override
    public Market getMarket() {
        try {
            return new GoogleMarket();
        }
        catch (StockNotFoundException exp) {
            log.error(null, exp);
        }

        return null;
    }

    private final class GoogleMarket implements Market {
        private final Map<Index, Stock> map = new EnumMap<Index, Stock>(Index.class);
        // Will it be better if we make this as static?
        private final ObjectMapper mapper = new ObjectMapper();

        public GoogleMarket() throws StockNotFoundException {            
            try {
                // Use StringBuilder instead of StringBuffer. We do not concern
                // on thread safety.
                StringBuilder builder = new StringBuilder("http://www.google.com/finance/info?client=ig&q=");
                // Exception will be thrown from apache httpclient, if we do not
                // perform URL encoding.
                builder.append(java.net.URLEncoder.encode(Utils.toGoogleFormat(codes.get(0)).toString(), "UTF-8"));
                for (int i = 1, size = codes.size(); i < size; i++) {
                    builder.append(",");
                    builder.append(java.net.URLEncoder.encode(Utils.toGoogleFormat(codes.get(i)).toString(), "UTF-8"));
                }
                final String location = builder.toString();
                final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
                // Google returns "// [ { "id": ... } ]".
                // We need to turn them into "[ { "id": ... } ]".
                final List<Map> jsonArray = mapper.readValue(Utils.GoogleRespondToJSON(respond), List.class);
                final List<Stock> stocks = new ArrayList<Stock>();
                for (int i = 0, size = jsonArray.size(); i < size; i++) {
                    final Map<String, String> jsonObject = jsonArray.get(i);
                    final double l_curr = Double.parseDouble(jsonObject.get("l_cur").replaceAll("[^0-9\\.]", ""));
                    final double c = Double.parseDouble(jsonObject.get("c").replaceAll("[^0-9\\.\\-]", ""));
                    // We ignore changePricePercentage. GoogleMarket doesn't
                    // need to return this value.
                    final Stock stock = new Stock.Builder(codes.get(i), Symbol.newInstance(codes.get(i).toString())).lastPrice(l_curr).changePrice(c).build();
                    stocks.add(stock);
                }
                // Store the result for later query purpose.
                for (Stock stock : stocks) {
                    map.put(codeToIndexMap.get(stock.getCode()), stock);
                }
            } catch (UnsupportedEncodingException ex) {
                throw new StockNotFoundException(null, ex);
            } catch (IOException ex) {
                throw new StockNotFoundException(null, ex);
            } catch (Exception ex) {
                // Jackson library may cause runtime exception if there is error
                // in the JSON string.
                throw new StockNotFoundException(null, ex);
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
            // Sad. Google doesn't give us volume information yet.
            return 0;
        }

        @Override
        public double getValue() {
            return 0;
        }

        @Override
        public Country getCountry() {
            return country;
        }
    }
}
