/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class BrazilYahooStockServer extends Subject<BrazilYahooStockServer, Integer> implements StockServer {
    public BrazilYahooStockServer(Country country) {
        this.country = country;
        baseURL = BrazilYahooStockServer.servers.get(country);

        if(baseURL == null) {
            throw new java.lang.IllegalArgumentException("Illegal country as argument (" + country +")");
        }
    }

    @Override
    public Stock getStock(Symbol symbol) throws StockNotFoundException {
        final StringBuilder stringBuilder = new StringBuilder(YAHOO_CSV_BASED_URL);

        final String _symbol;
        try {
            _symbol = java.net.URLEncoder.encode(symbol.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException("symbol.toString()=" + symbol.toString(), ex);
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

        throw new StockNotFoundException("Cannot get symbol=" + symbol);
    }

    @Override
    public Stock getStock(Code code) throws StockNotFoundException {
        return getStock(Symbol.newInstance(code.toString()));
    }

    private boolean isToleranceAllowed(int currSize, int expectedSize) {
        if(currSize >= expectedSize) return true;
        if(expectedSize <= 0) return true;

        double result = 100.0 - ((double)(expectedSize - currSize) / (double)expectedSize * 100.0);
        return (result >= STABILITY_RATE);
    }

    @Override
    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        List<Stock> stocks = new ArrayList<Stock>();

        if(symbols.size() == 0) return stocks;

        final int time = symbols.size() / MAX_STOCK_PER_ITERATION;
        final int remainder = symbols.size() % MAX_STOCK_PER_ITERATION;

        for(int i=0; i<time; i++) {
            final int start = i * MAX_STOCK_PER_ITERATION;
            final int end = start + MAX_STOCK_PER_ITERATION;

            final StringBuilder stringBuilder = new StringBuilder(YAHOO_CSV_BASED_URL);
            final StringBuilder symbolBuilder = new StringBuilder();
            final List<Symbol> expectedSymbols = new ArrayList<Symbol>();

            final int endLoop = end - 1;
            for(int j=start; j<endLoop; j++) {
                String symbolString = null;

                try {
                    symbolString = java.net.URLEncoder.encode(symbols.get(j).toString(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new StockNotFoundException("", ex);
                }

                symbolBuilder.append(symbolString).append("+");
                expectedSymbols.add(symbols.get(j));
            }

            String symbolString = null;

            try {
                symbolString = java.net.URLEncoder.encode(symbols.get(end - 1).toString(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new StockNotFoundException("", ex);
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
                    if(retry == (NUM_OF_RETRY-1)) {
                        // throw new StockNotFoundException();

                        assert(expectedSymbols.size() == MAX_STOCK_PER_ITERATION);

                        final int currSize = tmpStocks.size();
                        final int expectedSize = expectedSymbols.size();

                        if (this.isToleranceAllowed(currSize, expectedSize)) {
                            List<Symbol> currSymbols = new ArrayList<Symbol>();
                            List<Stock> emptyStocks = new ArrayList<Stock>();

                            for(Stock stock : tmpStocks) {
                                currSymbols.add(stock.getSymbol());
                            }

                            for(Symbol symbol : expectedSymbols) {
                                if(currSymbols.contains(symbol) == false) {
                                    emptyStocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(Code.newInstance(symbol.toString()), symbol));
                                }
                            }

                            tmpStocks.addAll(emptyStocks);
                        }
                        else {
                            throw new StockNotFoundException("Expected stock size=" + expectedSize + ", Current stock size=" + currSize + ", Request=" + location);
                        }
                    }   // if(retry == (NUM_OF_RETRY-1))
                    continue;
                }   // if(tmpStocks.size() != MAX_STOCK_PER_ITERATION)

                stocks.addAll(tmpStocks);

                success = true;
                break;
            }

            if (success == false) {
                throw new StockNotFoundException("Inconsistent stock size (" + stocks.size() + ") and symbol size (" + symbols.size() + ")");
            }
        }

        final int start = symbols.size() - remainder;
        final int end = start + remainder;

        final StringBuilder stringBuilder = new StringBuilder(YAHOO_CSV_BASED_URL);
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
            if(tmpStocks.size() != remainder) {
                if(retry == (NUM_OF_RETRY-1)) {
                    // throw new StockNotFoundException();

                    final int currSize = tmpStocks.size();
                    final int expectedSize = expectedSymbols.size();

                    if(this.isToleranceAllowed(currSize, expectedSize)) {
                        List<Symbol> currSymbols = new ArrayList<Symbol>();
                        List<Stock> emptyStocks = new ArrayList<Stock>();

                        for(Stock stock : tmpStocks) {
                            currSymbols.add(stock.getSymbol());
                        }

                        for(Symbol symbol : expectedSymbols) {
                            if(currSymbols.contains(symbol) == false) {
                                emptyStocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(Code.newInstance(symbol.toString()), symbol));
                            }
                        }

                        tmpStocks.addAll(emptyStocks);
                    }
                    else {
                        throw new StockNotFoundException("Expected stock size=" + expectedSize + ", Current stock size=" + currSize + ", Request=" + location);
                    }
                }   // if(retry == (NUM_OF_RETRY-1))

                continue;
            }   // if(tmpStocks.size() != remainder)

            stocks.addAll(tmpStocks);

            break;
        }

        if (stocks.size() != symbols.size()) {
           throw new StockNotFoundException("Inconsistent stock size (" + stocks.size() + ") and symbol size (" + symbols.size() + ")");
        }

        return stocks;
    }

    @Override
    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        List<Symbol> symbols = new ArrayList<Symbol>();
        for(Code code : codes) {
            symbols.add(Symbol.newInstance(code.toString()));
        }

        return getStocksBySymbols(symbols);
    }


    private Set<Symbol> getSymbols(String respond) {
        Set<Symbol> symbols = new HashSet<Symbol>();

        final Matcher matcher = symbolPattern.matcher(respond);

        while (matcher.find()){

            String group = matcher.group();

            for (int j = 1; j <= matcher.groupCount(); j++ ) {
                final String string = matcher.group(j);
                symbols.add(Symbol.newInstance(string));
            }
        }

        return symbols;
    }

    // The returned URLs, shouldn't have any duplication with visited,
    // and they are unique. Although is more suitable that we use Set,
    // use List is more convinient for us to iterate.
    private List<URL> getURLs(String respond, List<URL> visited) {
        List<URL> urls = new ArrayList<URL>();

        final Matcher matcher = urlPattern.matcher(respond);

        while (matcher.find()){
            for (int j = 1; j <= matcher.groupCount(); j++) {
                // Not sure why. Modify "/q/cp?s=%5EBVSP&amp;c=1" => "/q/cp?s=%5EBVSP&c=1"
                final String tmp = matcher.group(j);
                final String string = StringEscapeUtils.unescapeHtml(tmp);
                try {
                    URL url = new URL(baseURL, string);

                    if ((urls.contains(url) == false) && (visited.contains(url) == false)) {
                        urls.add(url);
                    }
                } catch (MalformedURLException ex) {
                    log.error(null, ex);
                }
            }
        }

        return urls;
    }

    @Override
    public List<Stock> getAllStocks() throws StockNotFoundException {
        List<URL> visited = new ArrayList<URL>();

        // Use Set, for safety purpose to avoid duplication.
        Set<Symbol> symbols = new HashSet<Symbol>();

        visited.add(baseURL);

        for (int i = 0; i < visited.size(); i++) {
            final String location = visited.get(i).toString();

            for (int retry = 0; retry < NUM_OF_RETRY; retry++) {
                final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
                if (respond == null) {
                    continue;
                }
                List<URL> urls = getURLs(respond, visited);
                Set<Symbol> tmpSymbols = getSymbols(respond);

                // getURLs already ensure URLs are unique.
                visited.addAll(urls);
                symbols.addAll(tmpSymbols);

                break;
            }   // for(int retry=0; retry<NUM_OF_RETRY; retry++)

            this.notify(this, symbols.size());
        }

        if (symbols.size() == 0) {
            throw new StockNotFoundException();
        }

        final List<Symbol> _symbols = new ArrayList<Symbol>(symbols);
        return getStocksBySymbols(_symbols);
    }

    private final Country country;
    private final URL baseURL;

    private static final Map<Country, URL> servers = new HashMap<Country, URL>();

    //<a href="/q/cp?s=^BVSP&c=1">Último</a>
    //      become ==>
    //<a href="/q/cp?s=%5EBVSP&amp;c=1">Último</a>
    private static final Pattern urlPattern = Pattern.compile("<a\\s+href\\s*=\\s*\"(/q/cp\\?s=%5EBVSP&amp;c=\\d+)\">");
    private static final Log log = LogFactory.getLog(BrazilYahooStockServer.class);

    //<tr>
    //  <td class="yfnc_tabledata1"><b><a href="/q?s=ALLL11.SA">ALLL11.SA</a></b></td>
    //  <td class="yfnc_tabledata1"><small>ALL AMER LAT-UNT     N2</small></td>
    //  <td class="yfnc_tabledata1" align="center"><b>15.30 </b> <nobr><small>dez 11</small></nobr></td>
    //  <td class="yfnc_tabledata1" align="center"><img width="10" height="14" border="0" src="http://us.i1.yimg.com/us.yimg.com/i/us/fi/03rd/up_g.gif" alt="Up"> <b style="color:#008800;">0.60 (4.08%)</b></td>
    //  <td class="yfnc_tabledata1" align="right">4,552,400</td>
    //</tr>
    private static final Pattern symbolPattern = Pattern.compile("<b><a\\s+href\\s*=\"/q\\?s=([^\"]+)\">[^<]+</a></b></td>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // Yahoo server limit is 200. We shorter, to avoid URL from being too long.
    // Yahoo sometimes does complain URL for being too long.
    private static final int MAX_STOCK_PER_ITERATION = 180;
    private static final String YAHOO_CSV_BASED_URL = "http://br.finance.yahoo.com/d/quotes.csv?s=";

    // Yahoo server's result is not stable. If we request for 100 stocks, it may only
    // return 99 stocks to us. We allow stability rate in %. Higher rate means more
    // stable.
    private static final double STABILITY_RATE = 90.0;

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

    static {
        // We try to perform search from Yahoo Finance by using the following parameters :
        try {
            servers.put(Country.Brazil, new URL("http://br.finance.yahoo.com/q/cp?s=%5EBVSP&c=0"));
        }
        catch(MalformedURLException exp) {
            // Shouldn't happen.
            exp.printStackTrace();
        }
    }
}
