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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author yccheok
 */
public abstract class AbstractYahooStockServer implements StockServer {
    protected abstract String getYahooCSVBasedURL();

    public AbstractYahooStockServer() {
    }
    
    private boolean isToleranceAllowed(int currSize, int expectedSize) {
        if (currSize >= expectedSize) {
            return true;
        }
        if (expectedSize <= 0) {
            return true;
        }
        //double result = 100.0 - ((double)(expectedSize - currSize) / (double)expectedSize * 100.0);
        //return (result >= STABILITY_RATE);
        return currSize > 0;
    }

    @Override
    public List<Stock> getStocks(List<Code> codes) throws StockNotFoundException {
        return _getStocks(codes);
    }

    @Override
    public Stock getStock(Code code) throws StockNotFoundException {
        return _getStock(code);
    }

    private List<Stock> _getStocks(List<Code> codes) throws StockNotFoundException {
        List<Stock> stocks = new ArrayList<Stock>();

        if (codes.isEmpty()) {
            return stocks;
        }

        final int time = codes.size() / MAX_STOCK_PER_ITERATION;
        final int remainder = codes.size() % MAX_STOCK_PER_ITERATION;

        for (int i = 0; i < time; i++) {
            final int start = i * MAX_STOCK_PER_ITERATION;
            final int end = start + MAX_STOCK_PER_ITERATION;

            final StringBuilder stringBuilder = new StringBuilder(getYahooCSVBasedURL());
            final StringBuilder codeBuilder = new StringBuilder();
            final List<Code> expectedCodes = new ArrayList<Code>();

            final int endLoop = end - 1;
            for (int j = start; j < endLoop; j++) {
                String codeString = null;

                try {
                    codeString = java.net.URLEncoder.encode(codes.get(j).toString(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new StockNotFoundException(null, ex);
                }
                codeBuilder.append(codeString).append("+");
                expectedCodes.add(codes.get(j));
            }

            String codeString = null;

            try {
                codeString = java.net.URLEncoder.encode(codes.get(end - 1).toString(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new StockNotFoundException(null, ex);
            }

            codeBuilder.append(codeString);
            expectedCodes.add(codes.get(end - 1));

            final String _code = codeBuilder.toString();

            stringBuilder.append(_code).append(YAHOO_STOCK_FORMAT);

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
                        assert(expectedCodes.size() == MAX_STOCK_PER_ITERATION);

                        final int currSize = tmpStocks.size();
                        final int expectedSize = expectedCodes.size();

                        if (this.isToleranceAllowed(currSize, expectedSize)) {
                            Set<Code> currCodes = new HashSet<Code>();
                            List<Stock> emptyStocks = new ArrayList<Stock>();

                            for (Stock stock : tmpStocks) {
                                currCodes.add(stock.code);                                
                            }

                            for (Code code : expectedCodes) {
                                if (currCodes.contains(code) == false) {
                                    emptyStocks.add(org.yccheok.jstock.engine.Utils.getEmptyStock(code, Symbol.newInstance(code.toString())));
                                }
                            }

                            tmpStocks.addAll(emptyStocks);
                            
                            // Will fall to stocks.addAll(tmpStocks);
                        }
                        else {
                            throw new StockNotFoundException("Expect " + expectedSize + " stock(s), but only receive " + currSize + " stock(s) from " + location);
                        }
                    }   // if (retry == (NUM_OF_RETRY-1))
                    else {
                        continue;
                    }
                }   // if (tmpStocks.size() != MAX_STOCK_PER_ITERATION)

                stocks.addAll(tmpStocks);

                success = true;
                break;
            }

            if (success == false) {
                throw new StockNotFoundException("Stock size (" + stocks.size() + ") inconsistent with code size (" + codes.size() + ")");
            }
        }

        final int start = codes.size() - remainder;
        final int end = start + remainder;

        final StringBuilder stringBuilder = new StringBuilder(getYahooCSVBasedURL());
        final StringBuilder codeBuilder = new StringBuilder();
        final List<Code> expectedCodes = new ArrayList<Code>();
        
        final int endLoop = end - 1;
        for (int i = start; i < endLoop; i++) {
            String codeString = null;

            try {
                codeString = java.net.URLEncoder.encode(codes.get(i).toString(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new StockNotFoundException("", ex);
            }

            codeBuilder.append(codeString).append("+");
            expectedCodes.add(codes.get(i));
        }

        String codeString = null;

        try {
            codeString = java.net.URLEncoder.encode(codes.get(end-1).toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException("", ex);
        }

        codeBuilder.append(codeString);
        expectedCodes.add(codes.get(end-1));

        final String _code = codeBuilder.toString();

        stringBuilder.append(_code).append(YAHOO_STOCK_FORMAT);

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
                    final int expectedSize = expectedCodes.size();

                    if (this.isToleranceAllowed(currSize, expectedSize)) {
                        Set<Code> currCodes = new HashSet<Code>();
                        List<Stock> emptyStocks = new ArrayList<Stock>();

                        for (Stock stock : tmpStocks) {
                            currCodes.add(stock.code);
                        }

                        for (Code code : expectedCodes) {
                            if (currCodes.contains(code) == false) {
                                emptyStocks.add(org.yccheok.jstock.engine.Utils.getEmptyStock(code, Symbol.newInstance(code.toString())));
                            }
                        }

                        tmpStocks.addAll(emptyStocks);
                        
                        // Will fall to stocks.addAll(tmpStocks);
                    }
                    else {
                        throw new StockNotFoundException("Expect " + expectedSize + " stock(s), but only receive " + currSize + " stock(s) from " + location);
                    }
                }   // if (retry == (NUM_OF_RETRY-1))
                else {
                    continue;
                }
            }   // if (tmpStocks.size() != remainder)

            stocks.addAll(tmpStocks);

            break;
        }

        if (stocks.size() != codes.size()) {
            throw new StockNotFoundException("Stock size (" + stocks.size() + ") inconsistent with code size (" + codes.size() + ")");
        }

        return stocks;
    }

    private Stock _getStock(Code code) throws StockNotFoundException {
        final StringBuilder stringBuilder = new StringBuilder(getYahooCSVBasedURL());

        final String _code;
        try {
            _code = java.net.URLEncoder.encode(code.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException(code.toString(), ex);
        }

        stringBuilder.append(_code).append(YAHOO_STOCK_FORMAT);

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

        throw new StockNotFoundException(code.toString());
    }

    // Yahoo server limit is 200. We shorter, to avoid URL from being too long.
    // Yahoo sometimes does complain URL for being too long.
    private static final int MAX_STOCK_PER_ITERATION = 180;
    private static final int NUM_OF_RETRY = 2;
    
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
    // b3 = Bid (Real-time) <-- We use b = Bid previously. However, most stocks return 0.
    // b6 = Bid Size        <-- We need to take special care on this, it may give us 1,234...
    // b2 = Ask (Real-time) <-- We use a = Ask previously. However, most stocks return 0.
    // a5 = Ask Size        <-- We need to take special care on this, it may give us 1,234...
    // d1 = Last Trade Date
    // t1 = Last Trade Time
    //
    // c6k2c1p2c -> Change (Real-time), Change Percent (Real-time), Change, Change in Percent, Change & Percent Change
    // "+1400.00","N/A - +4.31%",+1400.00,"+4.31%","+1400.00 - +4.31%"
    //
    // "MAERSKB.CO","AP MOELLER-MAERS-","Copenhagen",32500.00,33700.00,34200.00,33400.00,660,"+1200.00","N/A - +3.69%",33,33500.00,54,33700.00,96,"11/10/2008","10:53am"
    private static final String YAHOO_STOCK_FORMAT = "&f=snxspsosl1shsgsvsc1sp2sk3sb3sb6sb2sa5sd1t1";
}
