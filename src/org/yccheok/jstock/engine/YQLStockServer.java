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
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Concrete implementation of YQL stock server.
 */
public class YQLStockServer implements StockServer {

    /**
     * Returns stock based on given symbol.
     * @param symbol the symbol
     * @return stock based on given symbol
     * @throws StockNotFoundException if stock is not found based on given
     * symbol
     */
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

    /**
     * Returns stock based on given code.
     * @param code the code
     * @return stock based on given code
     * @throws StockNotFoundException if stock is not found based on given
     * symbol
     */
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

    /**
     * Returns list of stocks based on given list of symbols. The length of
     * the returned stock list will be equal to the given symbol list.
     *
     * @param symbols list of symbols
     * @return list of stocks based on given list of symbols
     * @throws StockNotFoundException if list of stocks is not found based on
     * given list of symbols
     */
    @Override
    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        List<Code> codes = new ArrayList<Code>();
        for (Symbol symbol : symbols) {
            codes.add(Code.newInstance(symbol.toString()));
        }
        return getStocksByCodes(codes);
    }

    /**
     * Returns list of stocks based on given list of codes. The length of
     * the returned stock list will be equal to the given code list.
     *
     * @param codes list of codes
     * @return list of stocks based on given list of codes
     * @throws StockNotFoundException if list of stocks is not found based on
     * given list of codes
     */
    @Override
    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        // Generate a list of queries to be sent over to YQL server.
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
            // Preparing the query.
            final String request = tmp.toString();
            // Send the query to YQL server one by one.
            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(request);
            // Ensure we are getting valid JSON respond.
            final String json = Utils.YahooRespondToJSON(respond);
            boolean success_at_least_one = false;
            try {
                final Holder value = mapper.readValue(json, Holder.class);
                List<QuoteType> quotes = value.query.results.quote;
                for (QuoteType quote : quotes) {
                    // symbol (will be used as Code in JStock) and Name from
                    // YQL must be non-empty.
                    if (quote.symbol == null || quote.symbol.trim().isEmpty()) {
                        continue;
                    }
                    // Is OK to have empty name.
                    // Sometimes Yahoo server goes crazy by returning empty name.
                    String Name = "";
                    if (quote.Name != null && quote.Name.trim().isEmpty() == false) {
                        // We are so lucky! Yahoo returns us something.
                        Name = quote.Name.trim();
                    }
                    final Stock stock = new Stock.Builder(Code.newInstance(quote.symbol.trim()), Symbol.newInstance(Name)).
                            prevPrice(Utils.parseDouble(quote.PreviousClose)).
                            lastPrice(Utils.parseDouble(quote.LastTradePriceOnly)).
                            openPrice(Utils.parseDouble(quote.Open)).
                            highPrice(Utils.parseDouble(quote.DaysHigh)).
                            lowPrice(Utils.parseDouble(quote.DaysLow)).
                            volume(Utils.parseLong(quote.Volume)).
                            changePrice(Utils.parseDouble(quote.Change)).
                            changePricePercentage(Utils.parseDouble(quote.PercentChange)).
                            buyPrice(Utils.parseDouble(quote.BidRealtime)).
                            sellPrice(Utils.parseDouble(quote.AskRealtime)).
                            build();
                    stocks.add(stock);
                    success_at_least_one = true;
                }
            } catch (Exception ex) {
                log.error(null, ex);
            }
            
            if (false == success_at_least_one) {
                // Never success. Try with Holder2.
                try {
                    final Holder2 value = mapper.readValue(json, Holder2.class);
                    QuoteType quote = value.query.results.quote;
                    // symbol (will be used as Code in JStock) and Name from
                    // YQL must be non-empty.
                    if (quote.symbol == null || quote.symbol.trim().isEmpty()) {
                        continue;
                    }
                    // Is OK to have empty name.
                    // Sometimes Yahoo server goes crazy by returning empty name.
                    String Name = "";
                    if (quote.Name != null && quote.Name.trim().isEmpty() == false) {
                        // We are so lucky! Yahoo returns us something.
                        Name = quote.Name.trim();
                    }
                    final Stock stock = new Stock.Builder(Code.newInstance(quote.symbol.trim()), Symbol.newInstance(Name)).
                            prevPrice(Utils.parseDouble(quote.PreviousClose)).
                            lastPrice(Utils.parseDouble(quote.LastTradePriceOnly)).
                            openPrice(Utils.parseDouble(quote.Open)).
                            highPrice(Utils.parseDouble(quote.DaysHigh)).
                            lowPrice(Utils.parseDouble(quote.DaysLow)).
                            volume(Utils.parseLong(quote.Volume)).
                            changePrice(Utils.parseDouble(quote.Change)).
                            changePricePercentage(Utils.parseDouble(quote.PercentChange)).
                            buyPrice(Utils.parseDouble(quote.BidRealtime)).
                            sellPrice(Utils.parseDouble(quote.AskRealtime)).
                            build();
                    stocks.add(stock);
                } catch (Exception ex) {
                    log.error(null, ex);
                }
            }   //  if (false == success_at_least_one)
        }   // for (String query : queries)

        // Are we getting enough stocks from YQL?
        if (isToleranceAllowed(stocks.size(), codes.size())) {
            List<Code> currentCodes = new ArrayList<Code>();
            for (Stock stock : stocks) {
                currentCodes.add(stock.getCode());
            }

            for (Code code : codes) {
                if (currentCodes.contains(code) == false) {
                    // If we are not getting enough stocks from YQL, we need
                    // to append empty stock into the returned result manually.
                    stocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(code, Symbol.newInstance(code.toString())));
                }
            }
        }

        // Ensure number of stocks matches with what user has requested.
        if (stocks.size() != codes.size()) {
            throw new StockNotFoundException("Stock size (" + stocks.size() + ") inconsistent with code size (" + codes.size() + ")");
        }

        return stocks;
    }

    /**
     * Returns list of all stocks in this stock server.
     * @return list of all stocks in this stock server
     * @throws StockNotFoundException if the operation fails
     */
    @Override
    public List<Stock> getAllStocks() throws StockNotFoundException {
        throw new StockNotFoundException();
    }

    // If I request currSize number of stock, and YQLStockServer only return
    // expectedSize number of stock, is it allowable?
    private boolean isToleranceAllowed(int currSize, int expectedSize) {
        if (currSize >= expectedSize) {
            return true;
        }
        if (expectedSize <= 0) {
            return true;
        }
        double result = 100.0 - ((double)(expectedSize - currSize) / (double)expectedSize * 100.0);
        return (result >= STABILITY_RATE);
    }

    // Will it be better if we make this as static?
    private final ObjectMapper mapper = new ObjectMapper();

    // Yahoo server limit is 200. We shorter, to avoid URL from being too long.
    // Yahoo sometimes does complain URL for being too long.
    private static final int MAX_STOCK_PER_ITERATION = 180;

    // Yahoo server's result is not stable. If we request for 100 stocks, it may only
    // return 99 stocks to us. We allow stability rate in %. Higher rate means more
    // stable.
    private static final double STABILITY_RATE = 90.0;

    private static final String baseURL = "http://query.yahooapis.com/v1/public/yql?q=";

    private static final Log log = LogFactory.getLog(YQLStockServer.class);

    /**
     * Type used to hold JSON result from Yahoo server.
     * Quote information will be return as array of JSON objects.
     */
    private static class Holder {
        public final QueryType query = null;
    }

    private static class QueryType {
        public final String count = null;
        public final String created = null;
        public final String lang = null;
        public final ResultsType results = null;
    }

    private static class ResultsType {
        public final List<QuoteType> quote = null;
    }

    private static class QuoteType {
        public final String symbol = null;
        public final String Name = null;
        public final String PreviousClose = null;
        public final String LastTradePriceOnly = null;
        public final String Open = null;
        public final String DaysHigh = null;
        public final String DaysLow = null;
        public final String Volume = null;
        public final String Change = null;
        public final String PercentChange = null;
        public final String BidRealtime = null;
        public final String AskRealtime = null;
    }

    /**
     * Type used to hold JSON result from Yahoo server.
     * Quote information will be return as single JSON object.
     */
    private static class Holder2 {
        public final QueryType2 query = null;
    }

    private static class QueryType2 {
        public final String count = null;
        public final String created = null;
        public final String lang = null;
        public final ResultsType2 results = null;
    }

    private static class ResultsType2 {
        public final QuoteType quote = null;
    }
}
