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
import java.util.Calendar;
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
public class YahooStockServer extends AbstractYahooStockServer {
    @Override
    protected String getYahooCSVBasedURL() {
        return "http://finance.yahoo.com/d/quotes.csv?s=";
    }
    
    public YahooStockServer(Country country) {
        super(country);
        baseURL = YahooStockServer.servers.get(country);
        
        if (baseURL == null) {
            throw new java.lang.IllegalArgumentException("Illegal country as argument (" + country +")");
        }        
    }
    
    // The returned URLs, shouldn't have any duplication with visited,
    // and they are unique. Although is more suitable that we use Set,
    // use List is more convinient for us to iterate.
    private List<URL> getURLs(String respond, List<URL> visited) {
        List<URL> urls = new ArrayList<URL>();

        final Matcher matcher = urlPattern.matcher(respond);
        
        while (matcher.find()){
            for (int j = 1; j <= matcher.groupCount(); j++) {
                final String string = matcher.group(j);

                try {
                    URL url = new URL(baseURL, string);
                    
                    if ((urls.contains(url) == false) && (visited.contains(url) == false)) {
                        urls.add(url);
                    }
                } catch (MalformedURLException ex) {
                    log.error("", ex);
                }                                
            }
        }
        
        return urls;
    }
    
    @Override
    public List<Stock> getAllStocks() throws StockNotFoundException {
        List<URL> visited = new ArrayList<URL>();
        
        List<Stock> stocks = new ArrayList<Stock>();

        // Use Set, for safety purpose to avoid duplication.
        Set<Code> codes = new HashSet<Code>();

        visited.add(baseURL);

        for (int i = 0; i < visited.size(); i++) {
            final String location = visited.get(i).toString();

            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);

            if (respond == null) {
                continue;
            }

            final List<Stock> tmpStocks = getStocks(respond);
            final List<URL> urls = getURLs(respond, visited);

            for (Stock stock : tmpStocks) {
                if (codes.add(stock.getCode())) {
                    stocks.add(stock);
                }
            }

            for (URL url : urls) {
                if (visited.contains(url)) {
                    continue;
                }
                visited.add(url);
            }

            notify(this, stocks.size());
        }

        return stocks;
    }

    private List<Stock> getStocks(String respond) {
        List<Stock> stocks = new ArrayList<Stock>();
        // In order to avoid duplicated stock's code.
        Set<Code> codes = new HashSet<Code>();

        // A hacking way to prevent
        // <a href="http://uk.finance.yahoo.com/q?s=SCRIA.ST+NCCA.ST+SHBB.ST+ELUXA.ST+LATOA.ST+VOLVA.ST+SCVA.ST+SKFA.ST+SEBC.ST+DV-BTA-1.ST+MTGB.ST+63054.ST+37341.ST+HOLMA.ST+TEL2A.ST+62893.ST+RATOA.ST+585671.ST+">View Quotes for All Above Symbols</a>
        // going into our database. Although we had placed some restriction on the
        // symbol maximum length, it may be some exceptional case sometimes.
        // For example : <a href="http://uk.finance.yahoo.com/q?s=SCRIA.ST+NCCA.ST+">View Quotes for All Above Symbols</a>
        final Pattern nonSymbolPattern = Pattern.compile(Pattern.quote("View Quotes for All Above Symbols"), Pattern.CASE_INSENSITIVE);

        final Matcher matcher = stockAndBoardPattern.matcher(respond);
        while (matcher.find()){
            for (int i = 1; i <= matcher.groupCount(); i += 3) {
                String c = matcher.group(i);
                if (c == null) {
                    continue;
                }

                String s = matcher.group(i + 1);
                if (s == null) {
                    continue;
                }

                String b = matcher.group(i + 2);
                if (b == null) {
                    continue;
                }

                c = c.trim();
                s = s.trim();

                // Change Parken Sport &amp; Entertainment A/S to
                // Parken Sport & Entertainment A/S
                s = StringEscapeUtils.unescapeHtml(s);

                if (nonSymbolPattern.matcher(s).find()) {
                    continue;
                }

                // Enum name is not allowed to have space in between.
                b = b.trim().replaceAll("\\s+", "");

                final Code code = Code.newInstance(c);
                final Symbol symbol = Symbol.newInstance(s);
                Stock.Board board = null;

                if (codes.add(code) == false) {
                    // The stock with same code had been added previously. We
                    // do not want any stock with duplicated code.
                    continue;
                }

                // Since '-' character is not allowed in enum, we need some sort
                // of hacking.
                if (b.equals("Virt-X")) {
                    b = "Virt_X";
                }

                try {
                    board = Stock.Board.valueOf(b);
                }
                catch (java.lang.IllegalArgumentException exp) {
                    log.error(null, exp);
                    board = Stock.Board.Unknown;
                }

                final Stock stock = new Stock(
                        code,
                        symbol,
                        "",
                        board,
                        Stock.Industry.Unknown,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0,
                        0.0,
                        0.0,
                        0,
                        0.0,
                        0,
                        0.0,
                        0,
                        0.0,
                        0,
                        0.0,
                        0,
                        0.0,
                        0,
                        0.0,
                        0,
                        Calendar.getInstance()
                        );
                stocks.add(stock);
            }
        }

        return stocks;
    }

    private final URL baseURL;
    
    private static final Map<Country, URL> servers = new HashMap<Country, URL>();
    private static final Pattern urlPattern = Pattern.compile("<a\\s+href\\s*=\\s*\"?(/uk/index.php.+?)\"?>", Pattern.CASE_INSENSITIVE);
    private static final Log log = LogFactory.getLog(YahooStockServer.class);

    /*
    <tr>
    <td nowrap="" class="yfnc_h">
     <a href="http://uk.finance.yahoo.com/q?s=VWS.CO">Vestas Wind Systems A/S</a>
    </td>
    <td nowrap="" align="center" class="yfnc_h">
     VWS.CO</td>
    <td nowrap="" align="center" class="yfnc_h">
     DK0010268606</td>
    <td nowrap="" align="center" class="yfnc_h">
     Copenhagen</td>
    <td nowrap="" align="center" class="yfnc_h">
     Stock</td>
    <td nowrap="" align="center" class="yfnc_h">
     <b>269.50<small>DKK</small> </b> <small>11:50</small></td>
    <td nowrap="" align="center" class="yfnc_h">
       <b style="color: rgb(204, 0, 0);"> -4.09%</b> </td>
    <td nowrap="" align="right" class="yfnc_h">
     1,373,728</td>
    <td nowrap="" align="center" class="yfnc_h">
     <img height="10" border="0" width="10" src="http://us.i1.yimg.com/us.yimg.com/i/us/fi/gr/track_trns_1.gif"/>
    <a href="http://uk.finedit.yahoo.com/ec?.intl=uk&amp;.src=quote&amp;.portfover=1&amp;.done=http://uk.finance.yahoo.com/&amp;.cancelPage=http://uk.biz.yahoo.com/quote_lookup.html&amp;.sym=VWS.CO&amp;.nm=Vestas+Wind+Systems+A%2FS">Add</a>
    </td>
    </tr>
     */
    // <a href="http://uk.finance.yahoo.com/q?s=RBS.L">Royal Bank Of Scotland</a>
    // First group is stock code, second group is stock symbol, 3rd group is board.
    // Board information is located at the fourth column of the table.
    // Pattern.DOTALL means for '.', we want to match everything including newline.
    //
    // Take note that, we need to avoid this kind of URLs sometimes :
    // <font face="arial" size="-1"><a href="http://uk.finance.yahoo.com/q?s=SCRIA.ST+NCCA.ST+SHBB.ST+ELUXA.ST+LATOA.ST+VOLVA.ST+SCVA.ST+SKFA.ST+SEBC.ST+DV-BTA-1.ST+MTGB.ST+63054.ST+37341.ST+HOLMA.ST+TEL2A.ST+62893.ST+RATOA.ST+585671.ST+">View Quotes for All Above Symbols</a></font>
    // We use "Limiting Repetition" to distinguish them out of normal stock code. We
    // assume the longest stock code length will <= 64.
    private static final Pattern stockAndBoardPattern = Pattern.compile("<a\\s+href\\s*=[^>]+s=([^\">]{1,64})\"?>(.+?)</a>.*?</td>.*?<td[^>]*>.*?</td>.*?<td[^>]*>.*?</td>.*?<td[^>]*>(.*?)</td>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    static {
        // We try to perform search from Yahoo Finance by using the following parameters :
        // Name or Symbol : **
        // Type : Stocks
        // Market : Depends on selected country.
        try {
            servers.put(Country.Australia, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=AX&sub=Look+Up"));
            servers.put(Country.Austria, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=VI&sub=Look+Up"));
            servers.put(Country.Belgium, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=BR&sub=Look+Up"));
            servers.put(Country.Canada, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=TO&sub=Look+Up"));
            servers.put(Country.Denmark, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=CO&sub=Look+Up"));
            servers.put(Country.France, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=PA&sub=Look+Up"));
            servers.put(Country.Germany, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=GER&sub=Look+Up"));
            servers.put(Country.India, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=IN&sub=Look+Up"));
            servers.put(Country.Italy, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=MI&sub=Look+Up"));
            servers.put(Country.Netherlands, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=AS&sub=Look+Up"));
            servers.put(Country.Norway, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=OL&sub=Look+Up"));
            servers.put(Country.Portugal, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=LI&sub=Look+Up"));
            servers.put(Country.Spain, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=ESP&sub=Look+Up"));
            servers.put(Country.Sweden, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=ST&sub=Look+Up"));
            servers.put(Country.Switzerland, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=SWI&sub=Look+Up"));
            servers.put(Country.UnitedKingdom, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=L&sub=Look+Up"));
            servers.put(Country.UnitedState, new URL("http://uk.finsearch.yahoo.com/uk/index.php?s=uk_sort&nm=**&tp=S&r=US&sub=Look+Up"));
        }
        catch(MalformedURLException exp) {
            // Shouldn't happen.
            exp.printStackTrace();
        }
    }
}
