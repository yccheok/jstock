/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2008 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import java.io.IOException;
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
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class YahooStockServer implements StockServer {
    public YahooStockServer(Country country) {
        this.country = country;
        baseURL = YahooStockServer.servers.get(country);
        
        if(baseURL == null) {
            throw new java.lang.IllegalArgumentException("Illegal country as argument (" + country +")");
        }
        
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
    }
    
    public Stock getStock(Symbol symbol) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");        
    }

    public Stock getStock(Code code) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        List<Stock> stocks = new ArrayList<Stock>();
        
        final int time = symbols.size() / MAX_STOCK_PER_ITERATION;
        final int remainder = symbols.size() % MAX_STOCK_PER_ITERATION;
        
        for(int i=0; i<time; i++) {
            final int start = i * MAX_STOCK_PER_ITERATION;
            final int end = start + MAX_STOCK_PER_ITERATION;
            
            final StringBuffer stringBuffer = new StringBuffer(YAHOO_CSV_BASED_URL);
            
            for(int j=start; j<end-1; j++) {
                stringBuffer.append(symbols.get(j)).append("+");
            }
            
            stringBuffer.append(symbols.get(end-1)).append(YAHOO_STOCK_FORMAT);
            
            final String location = stringBuffer.toString();
            
            for(int retry=0; retry<3; retry++) {
                HttpMethod method = new GetMethod(location);                        

                try {
                    Utils.setHttpClientProxyFromSystemProperties(httpClient);
                    httpClient.executeMethod(method);
                    final String responde = method.getResponseBodyAsString();
                    System.out.println(responde);
                }
                catch(HttpException exp) {
                    log.error("location=" + location, exp);                
                    continue;
                }
                catch(IOException exp) {
                    log.error("location=" + location, exp);
                    continue;
                }
                finally {
                    method.releaseConnection();
                }
                
                break;
            }
        }
        
        final StringBuffer stringBuffer = new StringBuffer(YAHOO_CSV_BASED_URL);

        final int start = time * MAX_STOCK_PER_ITERATION;
        final int end = start + remainder;
        
        for(int i=start; i<end-1; i++) {
            stringBuffer.append(symbols.get(i)).append("+");
        }

        stringBuffer.append(symbols.get(end-1)).append(YAHOO_STOCK_FORMAT);
            
       final String location = stringBuffer.toString();

        for(int retry=0; retry<3; retry++) {
            HttpMethod method = new GetMethod(location);                        

            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                httpClient.executeMethod(method);
                final String responde = method.getResponseBodyAsString();
                System.out.println(responde);
            }
            catch(HttpException exp) {
                log.error("location=" + location, exp);                
                continue;
            }
            catch(IOException exp) {
                log.error("location=" + location, exp);
                continue;
            }
            finally {
                method.releaseConnection();
            }

            break;
        }

        return stocks;
    }

    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Set<Symbol> getSymbols(String responde) {
        Set<Symbol> symbols = new HashSet<Symbol>();
        
        final Matcher matcher = symbolPattern.matcher(responde);
        
        while(matcher.find()){
            for(int j=1; j<=matcher.groupCount(); j++ ) {
                final String string = matcher.group(j);
                symbols.add(Symbol.newInstance(string));
            }
        }
        
        return symbols;
    }
    
    // The returned URLs, shouldn't have any duplication with visited,
    // and they are unique. Although is more suitable that we use Set,
    // use List is more convinient for us to iterate.
    private List<URL> getURLs(String responde, List<URL> visited) {   
        List<URL> urls = new ArrayList<URL>();
        
        final Pattern pattern = patterns.get(country);
        final Matcher matcher = pattern.matcher(responde);
        
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
    
    public List<Stock> getAllStocks() throws StockNotFoundException {
        List<Stock> stocks = new ArrayList<Stock>();
        List<URL> visited = new ArrayList<URL>();
        
        // Use Set, for safety purpose to avoid duplication.
        Set<Symbol> symbols = new HashSet<Symbol>();

        visited.add(baseURL);
        
        for(int i=0; i<visited.size(); i++) {
            final String location = visited.get(i).toString();
            
            for(int retry=0; retry<3; retry++) {
                HttpMethod method = new GetMethod(location);                        

                try {
                    Utils.setHttpClientProxyFromSystemProperties(httpClient);
                    httpClient.executeMethod(method);
                    final String responde = method.getResponseBodyAsString();
                    List<URL> urls = getURLs(responde, visited);
                    Set<Symbol> tmpSymbols = getSymbols(responde);

                    // getURLs already ensure URLs are unique.
                    visited.addAll(urls);
                    symbols.addAll(tmpSymbols);
                }
                catch(HttpException exp) {
                    log.error("location=" + location, exp);                
                    continue;
                }
                catch(IOException exp) {
                    log.error("location=" + location, exp);                
                    continue;
                }
                finally {
                    method.releaseConnection();
                }
                
                break;
            }   // for(int retry=0; retry<3; retry++)
        }
        
        final List<Symbol> _symbols = new ArrayList<Symbol>(symbols);
        return getStocksBySymbols(_symbols);
    }

    public static void main(String[] args) throws StockNotFoundException {
        YahooStockServer server = new YahooStockServer(Country.Denmark);
        server.getAllStocks();
    }
    
    private final Country country;
    private final URL baseURL;
    private HttpClient httpClient;
    
    private static final Map<Country, URL> servers = new HashMap<Country, URL>();
    private static final Map<Country, Pattern> patterns = new HashMap<Country, Pattern>();
    private static final Log log = LogFactory.getLog(YahooStockServer.class);
    
    private static final Pattern symbolPattern = Pattern.compile("<a\\s+href\\s*=[^>]+s=([^\">]+)\"?>Quote");
    
    // Yahoo server limit.
    private static final int MAX_STOCK_PER_ITERATION = 200;
    private static final String YAHOO_CSV_BASED_URL = "http://finance.yahoo.com/d/quotes.csv?s=";
    private static final String YAHOO_STOCK_FORMAT = "&f=nxol1hgvc6k2k3b3b6b2a5d1";
    
    static {
        try {
            servers.put(Country.Denmark, new URL("http://uk.biz.yahoo.com/p/dk/cpi/index.html"));
            servers.put(Country.France, new URL("http://uk.biz.yahoo.com/p/fr/cpi/index.html"));
            servers.put(Country.Germany, new URL("http://uk.biz.yahoo.com/p/de/cpi/index.html"));
            servers.put(Country.Italy, new URL("http://uk.biz.yahoo.com/p/it/cpi/index.html"));
            servers.put(Country.Norway, new URL("http://uk.biz.yahoo.com/p/no/cpi/index.html"));
            servers.put(Country.Spain, new URL("http://uk.biz.yahoo.com/p/es/cpi/index.html"));
            servers.put(Country.Sweeden, new URL("http://uk.biz.yahoo.com/p/se/cpi/index.html"));
            servers.put(Country.UnitedKingdom, new URL("http://uk.biz.yahoo.com/p/uk/cpi/index.html"));
            servers.put(Country.UnitedState, new URL("http://uk.biz.yahoo.com/p/us/cpi/index.html"));
        }
        catch(MalformedURLException exp) {
            // Shouldn't happen.
            exp.printStackTrace();
        }
        
        // <a href="/p/us/cpi/cpim0.html">
        patterns.put(Country.Denmark, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/dk\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.France, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/fr\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.Germany, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/de\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.Italy, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/it\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.Norway, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/no\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.Spain, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/es\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.Sweeden, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/se\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.UnitedKingdom, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/uk\\/cpi\\/[^\\s]+\\.html)"));
        patterns.put(Country.UnitedState, Pattern.compile("<a\\s+href\\s*=\\s*\"?(\\/p\\/us\\/cpi\\/[^\\s]+\\.html)"));
    }
}
