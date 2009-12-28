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
public class BrazilYahooStockServer extends AbstractYahooStockServer {
    @Override
    protected String getYahooCSVBasedURL() {
        return "http://br.finance.yahoo.com/d/quotes.csv?s=";
    }

    @Override
    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException
    {
        final List<Stock> tmp = super.getStocksBySymbols(symbols);
        final List<Stock> stocks = new ArrayList<Stock>();
        for (Stock stock : tmp) {
            // For Brazil Stock Market, change "Stock    Name" to "Stock Name".
            final String name = longSpacePattern.matcher(stock.getName()).replaceAll("").trim();
            final String symbol = longSpacePattern.matcher(stock.getSymbol().toString()).replaceAll("").trim();
            final Stock s = new Stock(
                    stock.getCode(),
                    Symbol.newInstance(symbol),
                    name,
                    stock.getBoard(),
                    stock.getIndustry(),
                    stock.getPrevPrice(),
                    stock.getOpenPrice(),
                    stock.getLastPrice(),
                    stock.getHighPrice(),
                    stock.getLowPrice(),
                    stock.getVolume(),
                    stock.getChangePrice(),
                    stock.getChangePricePercentage(),
                    stock.getLastVolume(),
                    stock.getBuyPrice(),
                    stock.getBuyQuantity(),
                    stock.getSellPrice(),
                    stock.getSellQuantity(),
                    stock.getSecondBuyPrice(),
                    stock.getSecondBuyQuantity(),
                    stock.getSecondSellPrice(),
                    stock.getSecondSellQuantity(),
                    stock.getThirdBuyPrice(),
                    stock.getThirdBuyQuantity(),
                    stock.getThirdSellPrice(),
                    stock.getThirdSellQuantity(),
                    stock.getCalendar()
                );
            stocks.add(s);
        }
        return stocks;
    }

    @Override
    public Stock getStock(Symbol symbol) throws StockNotFoundException
    {
        final Stock tmp = super.getStock(symbol);
        // For Brazil Stock Market, change "Stock    Name" to "Stock Name".
        final String name = longSpacePattern.matcher(tmp.getName()).replaceAll("").trim();
        final String _symbol = longSpacePattern.matcher(tmp.getSymbol().toString()).replaceAll("").trim();
        final Stock stock = new Stock(
                tmp.getCode(),
                Symbol.newInstance(_symbol),
                name,
                tmp.getBoard(),
                tmp.getIndustry(),
                tmp.getPrevPrice(),
                tmp.getOpenPrice(),
                tmp.getLastPrice(),
                tmp.getHighPrice(),
                tmp.getLowPrice(),
                tmp.getVolume(),
                tmp.getChangePrice(),
                tmp.getChangePricePercentage(),
                tmp.getLastVolume(),
                tmp.getBuyPrice(),
                tmp.getBuyQuantity(),
                tmp.getSellPrice(),
                tmp.getSellQuantity(),
                tmp.getSecondBuyPrice(),
                tmp.getSecondBuyQuantity(),
                tmp.getSecondSellPrice(),
                tmp.getSecondSellQuantity(),
                tmp.getThirdBuyPrice(),
                tmp.getThirdBuyQuantity(),
                tmp.getThirdSellPrice(),
                tmp.getThirdSellQuantity(),
                tmp.getCalendar()
            );
        return stock;
    }

    public BrazilYahooStockServer(Country country) {
        super(country);
        baseURL = BrazilYahooStockServer.servers.get(country);

        if (baseURL == null) {
            throw new java.lang.IllegalArgumentException("Illegal country as argument (" + country +")");
        }
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
            }   // for(int retry = 0; retry < NUM_OF_RETRY; retry++)

            this.notify(this, symbols.size());
        }

        if (symbols.size() == 0) {
            throw new StockNotFoundException();
        }

        final List<Symbol> _symbols = new ArrayList<Symbol>(symbols);
        return getStocksBySymbols(_symbols);
    }

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

    private static final int NUM_OF_RETRY = 2;

    private static final Pattern longSpacePattern = Pattern.compile("\\s\\s+");
    
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
