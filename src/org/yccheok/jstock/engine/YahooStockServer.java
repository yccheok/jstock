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
        final StringBuffer stringBuffer = new StringBuffer(YAHOO_CSV_BASED_URL);

        final String _symbol;
        try {
            _symbol = java.net.URLEncoder.encode(symbol.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException("symbol.toString()=" + symbol.toString(), ex);
        } 
        
        stringBuffer.append(_symbol).append(YAHOO_STOCK_FORMAT);
        
        final String location = stringBuffer.toString();
        
        for(int retry=0; retry<NUM_OF_RETRY; retry++) {
            HttpMethod method = new GetMethod(location);                        

            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                httpClient.executeMethod(method);
                final String responde = method.getResponseBodyAsString();
                final List<Stock> stocks = YahooStockFormat.getInstance().parse(responde);
                
                if(stocks.size() == 1)
                    return stocks.get(0);
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
        
        throw new StockNotFoundException("Cannot get symbol=" + symbol);
    }

    public Stock getStock(Code code) throws StockNotFoundException {
        return getStock(Symbol.newInstance(code.toString()));
    }

    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        List<Stock> stocks = new ArrayList<Stock>();
        
        if(symbols.size() == 0) return stocks;
        
        final int time = symbols.size() / MAX_STOCK_PER_ITERATION;
        final int remainder = symbols.size() % MAX_STOCK_PER_ITERATION;
        
        for(int i=0; i<time; i++) {
            final int start = i * MAX_STOCK_PER_ITERATION;
            final int end = start + MAX_STOCK_PER_ITERATION;
            
            final StringBuffer stringBuffer = new StringBuffer(YAHOO_CSV_BASED_URL);
            final StringBuffer symbolBuffer = new StringBuffer();
            
            final int endLoop = end - 1;
            for(int j=start; j<endLoop; j++) {
                symbolBuffer.append(symbols.get(j)).append("+");
            }
            
            symbolBuffer.append(symbols.get(end - 1));
            final String _symbol;
            try {
                _symbol = java.net.URLEncoder.encode(symbolBuffer.toString(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new StockNotFoundException("symbolBuffer.toString()=" + symbolBuffer.toString(), ex);
            } 
        
            stringBuffer.append(_symbol).append(YAHOO_STOCK_FORMAT);
            
            final String location = stringBuffer.toString();
            
            boolean success = false;
            
            for(int retry=0; retry<NUM_OF_RETRY; retry++) {
                HttpMethod method = new GetMethod(location);                        

                try {
                    Utils.setHttpClientProxyFromSystemProperties(httpClient);
                    httpClient.executeMethod(method);
                    final String responde = method.getResponseBodyAsString();
                    System.out.println(responde);
                    
                    final List<Stock> tmpStocks = YahooStockFormat.getInstance().parse(responde);
                    if(tmpStocks.size() != MAX_STOCK_PER_ITERATION) {
                        if(retry == (NUM_OF_RETRY-1)) {
                            throw new StockNotFoundException();
                        }
                        continue;
                    }
                    
                    stocks.addAll(tmpStocks);
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
                
                success = true;
                break;
            }
            
            if(success == false)
                throw new StockNotFoundException("Inconsistent stock size (" + stocks.size() + ") and symbol size (" + symbols.size() + ")");
        }

        final int start = symbols.size() - remainder;        
        final int end = start + remainder;
        
        final StringBuffer stringBuffer = new StringBuffer(YAHOO_CSV_BASED_URL);
        final StringBuffer symbolBuffer = new StringBuffer();
        
        final int endLoop = end - 1;
        for(int i=start; i<endLoop; i++) {
            symbolBuffer.append(symbols.get(i)).append("+");
        }
        symbolBuffer.append(symbols.get(end-1));
                
        final String _symbol;
        try {
            _symbol = java.net.URLEncoder.encode(symbolBuffer.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException("symbolBuffer.toString()=" + symbolBuffer.toString(), ex);
        }
            
        stringBuffer.append(_symbol).append(YAHOO_STOCK_FORMAT);
                    
        final String location = stringBuffer.toString();
        
        System.out.println("LOCATION=" + location);
        
        for(int retry=0; retry<NUM_OF_RETRY; retry++) {
            HttpMethod method = new GetMethod(location);                        

            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                httpClient.executeMethod(method);
                final String responde = method.getResponseBodyAsString();
                System.out.println(responde);
                
                final List<Stock> tmpStocks = YahooStockFormat.getInstance().parse(responde);
                if(tmpStocks.size() != remainder) {
                    if(retry == (NUM_OF_RETRY-1)) {
                        throw new StockNotFoundException();
                    }                    
                    
                    continue;
                }
                
                stocks.addAll(tmpStocks);
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

       if(stocks.size() != symbols.size())
           throw new StockNotFoundException("Inconsistent stock size (" + stocks.size() + ") and symbol size (" + symbols.size() + ")");
       
        return stocks;
    }

    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        List<Symbol> symbols = new ArrayList<Symbol>();
        for(Code code : codes) {
            symbols.add(Symbol.newInstance(code.toString()));
        }
        
        return getStocksBySymbols(symbols);
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
        
        if(symbols.size() == 0) throw new StockNotFoundException();
        
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
    
    private static final int NUM_OF_RETRY = 3;
    
    // s = Symbol
    // n = Name
    // x = Stock Exchange
    // o = Open     <-- We are no longer using this one. It will not tally with change and change percentage
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
    private static final String YAHOO_STOCK_FORMAT = "&f=snxpl1hgsvsc1p2sk3sbsb6sasa5sd1t1";
    
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
