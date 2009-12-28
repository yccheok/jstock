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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class SingaporeYahooStockServer extends AbstractYahooStockServer {
    @Override
    protected String getYahooCSVBasedURL() {
        return "http://sg.finance.yahoo.com/d/quotes.csv?s=";
    }
    
    public SingaporeYahooStockServer(Country country) {
        super(country);
        baseURL = SingaporeYahooStockServer.servers.get(country);

        if (baseURL == null) {
            throw new java.lang.IllegalArgumentException("Illegal country as argument (" + country +")");
        }
    }

    @Override
    public Stock getStock(Code code) throws StockNotFoundException {
        final Code newCode = Utils.toYahooFormat(code, this.getCountry());
        return getStock(Symbol.newInstance(newCode.toString()));
    }

    @Override
    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        List<Symbol> symbols = new ArrayList<Symbol>();
        for (Code code : codes) {
            final Code newCode = Utils.toYahooFormat(code, this.getCountry());
            symbols.add(Symbol.newInstance(newCode.toString()));
        }

        return getStocksBySymbols(symbols);
    }

    // The returned URLs, shouldn't have any duplication with visited,
    // and they are unique. Although is more suitable that we use Set,
    // use List is more convinient for us to iterate.
    private List<URL> getURLs(String respond, List<URL> visited) {
        List<URL> urls = new ArrayList<URL>();

        final Pattern pattern = patterns.get(this.getCountry());
        final Matcher matcher = pattern.matcher(respond);

        while(matcher.find()){
            for(int j=1; j<=matcher.groupCount(); j++ ) {
                String string = matcher.group(j);

                try {
                    URL url = new URL(baseURL, string);

                    if((urls.contains(url) == false) && (visited.contains(url) == false)) {
                        urls.add(url);
                    }
                } catch (MalformedURLException ex) {
                    log.error("", ex);
                }
            }
        }

        return urls;
    }

    private Set<Symbol> getSymbols(String respond) {
        Set<Symbol> symbols = new HashSet<Symbol>();

        final Matcher matcher = symbolPattern.matcher(respond);

        while (matcher.find()){
            for (int j = 1; j <= matcher.groupCount(); j++ ) {
                final String string = matcher.group(j);
                // [2909595] Incorrect Hong Kong Database
                // https://sourceforge.net/tracker/?func=detail&aid=2909595&group_id=202896&atid=983418
                if (this.getCountry() == Country.HongKong && string.length() > "-OL.HK".length() && string.endsWith("-OL.HK")) {
                    symbols.add(Symbol.newInstance(string.substring(0, string.length() - "-OL.HK".length()) + ".HK"));
                }
                else {
                    symbols.add(Symbol.newInstance(string));
                }
            }
        }

        return symbols;
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
            }   // for (int retry = 0; retry < NUM_OF_RETRY; retry++)

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
    private static final Map<Country, Pattern> patterns = new HashMap<Country, Pattern>();
    private static final Log log = LogFactory.getLog(SingaporeYahooStockServer.class);

    private static final Pattern symbolPattern = Pattern.compile("<a\\s+href\\s*=[^>]+s=([^\">&]+)&d=t\"?>Quote");

    private static final int NUM_OF_RETRY = 2;

    static {
        try {
            servers.put(Country.Malaysia, new URL("http://sg.biz.yahoo.com/il/my/1"));
            servers.put(Country.Singapore, new URL("http://sg.biz.yahoo.com/il/si/1"));
            servers.put(Country.HongKong, new URL("http://sg.biz.yahoo.com/il/hk/1"));
            servers.put(Country.Indonesia, new URL("http://sg.biz.yahoo.com/il/jk/1"));
            servers.put(Country.Korea, new URL("http://sg.biz.yahoo.com/il/ks/1"));
            servers.put(Country.Taiwan, new URL("http://sg.biz.yahoo.com/il/tw/1"));
        }
        catch(MalformedURLException exp) {
            // Shouldn't happen.
            exp.printStackTrace();
        }

        // <a href="/il/my/l">
        patterns.put(Country.Malaysia, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/il\\/my\\/[^\\s\">]+)"));
        patterns.put(Country.Singapore, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/il\\/si\\/[^\\s\">]+)"));
        patterns.put(Country.HongKong, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/il\\/hk\\/[^\\s\">]+)"));
        patterns.put(Country.Indonesia, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/il\\/jk\\/[^\\s\">]+)"));
        patterns.put(Country.Korea, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/il\\/ks\\/[^\\s\">]+)"));
        patterns.put(Country.Taiwan, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/il\\/tw\\/[^\\s\">]+)"));
    }
}
