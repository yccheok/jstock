/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng CHEOK <yccheok@yahoo.com>
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

/**
 *
 * @author yccheok
 */
public abstract class AbstractYahooStockServer extends Subject<AbstractYahooStockServer, Integer> implements StockServer {
    protected abstract String getYahooCSVBasedURL();

    public AbstractYahooStockServer(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return this.country;
    }

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

    @Override
    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        List<Symbol> symbols = new ArrayList<Symbol>();
        for (Code code : codes) {
            final Code newCode = Utils.toYahooFormat(code, this.country);
            symbols.add(Symbol.newInstance(newCode.toString()));
        }
        return getStocksBySymbols(symbols);
    }

    @Override
    public Stock getStock(Code code) throws StockNotFoundException {
        final Code newCode = Utils.toYahooFormat(code, this.country);
        return getStock(Symbol.newInstance(newCode.toString()));
    }

    @Override
    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        List<Stock> stocks = new ArrayList<Stock>();

        if (symbols.isEmpty()) {
            return stocks;
        }

        final int time = symbols.size() / MAX_STOCK_PER_ITERATION;
        final int remainder = symbols.size() % MAX_STOCK_PER_ITERATION;

        for (int i = 0; i < time; i++) {
            final int start = i * MAX_STOCK_PER_ITERATION;
            final int end = start + MAX_STOCK_PER_ITERATION;

            final StringBuilder stringBuilder = new StringBuilder(getYahooCSVBasedURL());
            final StringBuilder symbolBuilder = new StringBuilder();
            final List<Symbol> expectedSymbols = new ArrayList<Symbol>();

            final int endLoop = end - 1;
            for (int j = start; j < endLoop; j++) {
                String symbolString = null;

                try {
                    symbolString = java.net.URLEncoder.encode(symbols.get(j).toString(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new StockNotFoundException(null, ex);
                }
                symbolBuilder.append(symbolString).append("+");
                expectedSymbols.add(symbols.get(j));
            }

            String symbolString = null;

            try {
                symbolString = java.net.URLEncoder.encode(symbols.get(end - 1).toString(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new StockNotFoundException(null, ex);
            }

            symbolBuilder.append(symbolString);
            expectedSymbols.add(symbols.get(end - 1));

            final String _symbol = symbolBuilder.toString();

            stringBuilder.append(_symbol).append(YAHOO_STOCK_FORMAT);

            final String location = stringBuilder.toString();

            boolean success = false;

            for (int retry = 0; retry < NUM_OF_RETRY; retry++) {
                final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);

                if (respond == null) {
                    continue;
                }

                final List<Stock> tmpStocks = YahooStockFormat.getInstance().parse(respond);
                if (tmpStocks.size() != MAX_STOCK_PER_ITERATION) {
                    if (retry == (NUM_OF_RETRY - 1)) {
                        assert(expectedSymbols.size() == MAX_STOCK_PER_ITERATION);

                        final int currSize = tmpStocks.size();
                        final int expectedSize = expectedSymbols.size();

                        if (this.isToleranceAllowed(currSize, expectedSize)) {
                            List<Symbol> currSymbols = new ArrayList<Symbol>();
                            List<Stock> emptyStocks = new ArrayList<Stock>();

                            for (Stock stock : tmpStocks) {
                                currSymbols.add(stock.getSymbol());
                            }

                            for (Symbol symbol : expectedSymbols) {
                                if (currSymbols.contains(symbol) == false) {
                                    emptyStocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(Code.newInstance(symbol.toString()), symbol));
                                }
                            }

                            tmpStocks.addAll(emptyStocks);
                        }
                        else {
                            throw new StockNotFoundException("Expect " + expectedSize + " stock(s), but only receive " + currSize + " stock(s) from " + location);
                        }
                    }   // if (retry == (NUM_OF_RETRY-1))
                    continue;
                }   // if (tmpStocks.size() != MAX_STOCK_PER_ITERATION)

                stocks.addAll(tmpStocks);

                success = true;
                break;
            }

            if (success == false) {
                throw new StockNotFoundException("Stock size (" + stocks.size() + ") inconsistent with symbol size (" + symbols.size() + ")");
            }
        }

        final int start = symbols.size() - remainder;
        final int end = start + remainder;

        final StringBuilder stringBuilder = new StringBuilder(getYahooCSVBasedURL());
        final StringBuilder symbolBuilder = new StringBuilder();
        final List<Symbol> expectedSymbols = new ArrayList<Symbol>();

        final int endLoop = end - 1;
        for (int i = start; i < endLoop; i++) {
            String symbolString = null;

            try {
                symbolString = java.net.URLEncoder.encode(symbols.get(i).toString(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new StockNotFoundException("", ex);
            }

            symbolBuilder.append(symbolString).append("+");
            expectedSymbols.add(symbols.get(i));
        }

        String symbolString = null;

        try {
            symbolString = java.net.URLEncoder.encode(symbols.get(end-1).toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException("", ex);
        }

        symbolBuilder.append(symbolString);
        expectedSymbols.add(symbols.get(end-1));

        final String _symbol = symbolBuilder.toString();

        stringBuilder.append(_symbol).append(YAHOO_STOCK_FORMAT);

        final String location = stringBuilder.toString();

        for (int retry = 0; retry < NUM_OF_RETRY; retry++) {
            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
            if (respond == null) {
                continue;
            }
            final List<Stock> tmpStocks = YahooStockFormat.getInstance().parse(respond);
            if (tmpStocks.size() != remainder) {
                if (retry == (NUM_OF_RETRY - 1)) {
                    final int currSize = tmpStocks.size();
                    final int expectedSize = expectedSymbols.size();

                    if (this.isToleranceAllowed(currSize, expectedSize)) {
                        List<Symbol> currSymbols = new ArrayList<Symbol>();
                        List<Stock> emptyStocks = new ArrayList<Stock>();

                        for (Stock stock : tmpStocks) {
                            currSymbols.add(stock.getSymbol());
                        }

                        for (Symbol symbol : expectedSymbols) {
                            if (currSymbols.contains(symbol) == false) {
                                emptyStocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(Code.newInstance(symbol.toString()), symbol));
                            }
                        }

                        tmpStocks.addAll(emptyStocks);
                    }
                    else {
                        throw new StockNotFoundException("Expect " + expectedSize + " stock(s), but only receive " + currSize + " stock(s) from " + location);
                    }
                }   // if (retry == (NUM_OF_RETRY-1))

                continue;
            }   // if (tmpStocks.size() != remainder)

            stocks.addAll(tmpStocks);

            break;
        }

        if (stocks.size() != symbols.size()) {
            throw new StockNotFoundException("Stock size (" + stocks.size() + ") inconsistent with symbol size (" + symbols.size() + ")");
        }

        return stocks;
    }

    @Override
    public Stock getStock(Symbol symbol) throws StockNotFoundException {
        final StringBuilder stringBuilder = new StringBuilder(getYahooCSVBasedURL());

        final String _symbol;
        try {
            _symbol = java.net.URLEncoder.encode(symbol.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException(symbol.toString(), ex);
        }

        stringBuilder.append(_symbol).append(YAHOO_STOCK_FORMAT);

        final String location = stringBuilder.toString();

        for (int retry = 0; retry < NUM_OF_RETRY; retry++) {
            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
            if (respond == null) {
                continue;
            }
            final List<Stock> stocks = YahooStockFormat.getInstance().parse(respond);

            if (stocks.size() == 1) {
                return stocks.get(0);
            }

            break;
        }

        throw new StockNotFoundException(symbol.toString());
    }

    // Yahoo server limit is 200. We shorter, to avoid URL from being too long.
    // Yahoo sometimes does complain URL for being too long.
    private static final int MAX_STOCK_PER_ITERATION = 180;
    private static final int NUM_OF_RETRY = 2;

    // Yahoo server's result is not stable. If we request for 100 stocks, it may only
    // return 99 stocks to us. We allow stability rate in %. Higher rate means more
    // stable.
    private static final double STABILITY_RATE = 90.0;
    
    // Update on 19 March 2009 : We cannot assume certain parameters will always
    // be float. They may become integer too. For example, in the case of Korea
    // Stock Market, Previous Close is in integer. We shall apply string quote
    // protection method too on them.
    //
    // Here are the index since 19 March 2009 :
    // (0) Symbol
    // (1) Name
    // (2) Stock Exchange
    // (3) Symbol
    // (4) Previous Close
    // (5) Symbol
    // (6) Open
    // (7) Symbol
    // (8) Last Trade
    // (9) Symbol
    // (10) Day's high
    // (11) Symbol
    // (12) Day's low
    // (13) Symbol
    // (14) Volume
    // (15) Symbol
    // (16) Change
    // (17) Symbol
    // (18) Change Percent
    // (19) Symbol
    // (20) Last Trade Size
    // (21) Symbol
    // (22) Bid
    // (23) Symbol
    // (24) Bid Size
    // (25) Symbol
    // (26) Ask
    // (27) Symbol
    // (28) Ask Size
    // (29) Symbol
    // (30) Last Trade Date
    // (31) Last Trade Time.
    //
    // s = Symbol
    // n = Name
    // x = Stock Exchange
    // o = Open             <-- Although we will keep this value in our stock data structure, we will not show
    //                          it to clients. As some stock servers unable to retrieve open price.
    // p = Previous Close
    // l1 = Last Trade (Price Only)
    // h = Day's high
    // g = Day's low
    // v = Volume           <-- We need to take special care on this, it may give us 1,234. This will
    //                          make us difficult to parse csv file. The only workaround is to make integer
    //                          in between two string literal (which will always contains "). By using regular
    //                          expression, we will manually remove the comma.
    // c1 = Change
    // p2 = Change Percent
    // k3 = Last Trade Size <-- We need to take special care on this, it may give us 1,234...
    // b = Bid
    // b6 = Bid Size        <-- We need to take special care on this, it may give us 1,234...
    // a = Ask
    // a5 = Ask Size        <-- We need to take special care on this, it may give us 1,234...
    // d1 = Last Trade Date
    // t1 = Last Trade Time
    //
    // c6k2c1p2c -> Change (Real-time), Change Percent (Real-time), Change, Change in Percent, Change & Percent Change
    // "+1400.00","N/A - +4.31%",+1400.00,"+4.31%","+1400.00 - +4.31%"
    //
    // "MAERSKB.CO","AP MOELLER-MAERS-","Copenhagen",32500.00,33700.00,34200.00,33400.00,660,"+1200.00","N/A - +3.69%",33,33500.00,54,33700.00,96,"11/10/2008","10:53am"
    private static final String YAHOO_STOCK_FORMAT = "&f=snxspsosl1shsgsvsc1sp2sk3sbsb6sasa5sd1t1";

    private final Country country;
}
