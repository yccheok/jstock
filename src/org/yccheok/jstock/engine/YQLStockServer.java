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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation of YQL stock server.
 *
 * @author yccheok
 */
public class YQLStockServer implements StockServer {

    @Override
    public Stock getStock(Symbol symbol) throws StockNotFoundException {
        List<Symbol> symbols = new ArrayList<Symbol>();
        symbols.add(symbol);
        List<Stock> stocks = getStocksBySymbols(symbols);
        if (stocks.size() == 1) {
            return stocks.get(0);
        }
        throw new StockNotFoundException();
    }

    @Override
    public Stock getStock(Code code) throws StockNotFoundException {
        List<Code> codes = new ArrayList<Code>();
        codes.add(code);
        List<Stock> stocks = getStocksByCodes(codes);
        if (stocks.size() == 1) {
            return stocks.get(0);
        }
        throw new StockNotFoundException();
    }

    @Override
    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        List<Code> codes = new ArrayList<Code>();
        for (Symbol symbol : symbols) {
            codes.add(Code.newInstance(symbol.toString()));
        }
        return getStocksByCodes(codes);
    }

    @Override
    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        StringBuilder builder = new StringBuilder("select symbol,Name,PreviousClose,LastTradePriceOnly,Open,DaysHigh,DaysLow,Volume,Change,PercentChange,BidRealtime,AskRealtime from yahoo.finance.quotes where symbol in (");
        List<Stock> stocks = new ArrayList<Stock>();
        List<String> queries = new ArrayList<String>();
        int count = 0;
        for (int i = 0, size = codes.size(); i < size; i++) {
            Code code = codes.get(i);
            count++;
            if (count >= size) {
                builder.append("\"");
                builder.append(code);
                builder.append("\")");
                try {
                    StringBuilder tmp = new StringBuilder(java.net.URLEncoder.encode(builder.toString(), "UTF-8"));
                    tmp.append("&format=json&env=http%3A%2F%2Fdatatables.org%2Falltables.env&callback=");
                    queries.add(tmp.toString());
                } catch (UnsupportedEncodingException ex) {
                    log.error(null, ex);
                }
            } else if (count >= MAX_STOCK_PER_ITERATION) {
                builder.append("\"");
                builder.append(code);
                builder.append("\")");
                try {
                    StringBuilder tmp = new StringBuilder(java.net.URLEncoder.encode(builder.toString(), "UTF-8"));
                    tmp.append("&format=json&env=http%3A%2F%2Fdatatables.org%2Falltables.env&callback=");
                    queries.add(tmp.toString());
                } catch (UnsupportedEncodingException ex) {
                    log.error(null, ex);
                }
                builder = new StringBuilder("select symbol,Name,PreviousClose,LastTradePriceOnly,Open,DaysHigh,DaysLow,Volume,Change,PercentChange,BidRealtime,AskRealtime from yahoo.finance.quotes where symbol in (");
                count = 0;
            } else {
                 builder.append("\"");
                 builder.append(code);
                 builder.append("\",");
            }
        }

        for (String query : queries) {
            StringBuilder tmp = new StringBuilder(baseURL);
            tmp.append(query);
            final String request = tmp.toString();
            // TODO add code to parse request into json.
        }
        
        throw new StockNotFoundException();
    }

    @Override
    public List<Stock> getAllStocks() throws StockNotFoundException {
        throw new StockNotFoundException();
    }

    // Yahoo server limit is 200. We shorter, to avoid URL from being too long.
    // Yahoo sometimes does complain URL for being too long.
    private static final int MAX_STOCK_PER_ITERATION = 180;

    private static final String baseURL = "http://query.yahooapis.com/v1/public/yql?q=";

    private static final Log log = LogFactory.getLog(YQLStockServer.class);

    /**
     * Type used to hold JSON result from Yahoo server.
     */
    private static class Holder {
        public final QueryType query = null;
    }

    private static class QueryType {
        public final String count = null;
        public final String created = null;
        public final String lang = null;
        public final ResultsType result = null;
    }

    private static class ResultsType {
        public final List<QuoteType> quote = null;
    }

    private static class QuoteType {
        public static final String symbol = null;
        public static final String Name = null;
        public static final String PreviousClose = null;
        public static final String LastTradePriceOnly = null;
        public static final String Open = null;
        public static final String DaysHigh = null;
        public static final String DaysLow = null;
        public static final String Volume = null;
        public static final String Change = null;
        public static final String PercentChange = null;
        public static final String BidRealtime = null;
        public static final String AskRealtime = null;
    }
}
