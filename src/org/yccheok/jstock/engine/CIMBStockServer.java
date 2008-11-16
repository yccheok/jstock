/*
 * CIMBStockServer.java
 *
 * Created on April 20, 2007, 12:15 AM
 *
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
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import java.io.*;
import java.util.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class CIMBStockServer extends Subject<CIMBStockServer, Integer> implements StockServer {
    
    /** Creates a new instance of CIMBStockServer */
    public CIMBStockServer() {
        // Empty username and password.
        this("", "");
    }

    public CIMBStockServer(String username, String password) {
        this.stockFormat = CIMBStockFormat.getInstance();
        
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        
        this.username = username;
        this.password = password;        
    }
    
    public Stock getStock(Symbol symbol) throws StockNotFoundException
    {
        String _symbol;

        try {
            _symbol = java.net.URLEncoder.encode(symbol.toString(), "UTF-8");
        }
        catch(java.io.UnsupportedEncodingException exp) {
            throw new StockNotFoundException("symbol=" + symbol, exp);
        }
                
        java.util.List<Stock> stocks = null;

        Thread currentThread = Thread.currentThread();
         
        for(String server : servers) {            
            if(currentThread.isInterrupted()) throw new StockNotFoundException("Thread has been interrupted");
        
            HttpMethod method = new GetMethod(server + "rtQuote.dll?GetStockGeneral&Key=" + _symbol);                        
            
            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                httpClient.executeMethod(method);
                final String responde = method.getResponseBodyAsString();

                stocks = stockFormat.parse(responde);

                if(stocks.size() != 1) {
                    log.error("Number of stock (" + stocks.size() + ") is not 1");
                    continue;
                }
            }
            catch(HttpException exp) {
                log.error("symbol=" + symbol, exp);
                continue;
            }
            catch(IOException exp) {
                log.error("symbol=" + symbol, exp);
                continue;
            }
            finally {
                method.releaseConnection();
            }
            
            break;
        }
        
        if(stocks == null) throw new StockNotFoundException("Cannot get symbol=" + symbol);
        
        return stocks.get(0);
    }
    
    public Stock getStock(Code code) throws StockNotFoundException
    {
        // The nice CIMB server are able to accept both code and name.
        return this.getStock(Symbol.newInstance(code.toString()));
    }
    
    public java.util.List<Stock> getStocksBySymbols(java.util.List<Symbol> symbols) throws StockNotFoundException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    
    public java.util.List<Stock> getStocksByCodes(java.util.List<Code> codes) throws StockNotFoundException
    {
        String _codes;
        StringBuffer _codesBuffer = new StringBuffer();        
        
        for(Code code : codes) {
            _codesBuffer.append(code);
            _codesBuffer.append("|");
        }
        
        _codes = _codesBuffer.toString();
        
        try {
            _codes = java.net.URLEncoder.encode(_codes, "UTF-8");
        }
        catch(java.io.UnsupportedEncodingException exp) {
            throw new StockNotFoundException("_codes=" + _codes, exp);
        }

        java.util.List<Stock> stocks = null;

        Thread currentThread = Thread.currentThread();

        for(String server : servers) {
            if(currentThread.isInterrupted()) throw new StockNotFoundException("Thread has been interrupted");

            /* ascending order */
            HttpMethod method = new GetMethod(server + "rtQuote.dll?GetStockInfoSortByCode&StockList=" + _codes + "&SortDesc=0");
            // method.getParams().setParameter("http.socket.timeout", new Integer(5000));

            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                httpClient.executeMethod(method);
                final String responde = method.getResponseBodyAsString();
                
                stocks = stockFormat.parse(responde);

                if(stocks.size() != codes.size()) {
                    log.error("Number of stock (" + stocks.size() + ") is not " + codes.size());
                    continue;                    
                }            
            }
            catch(HttpException exp) {
                log.error("", exp);
                continue;
            }
            catch(IOException exp) {
                log.error("", exp);
                continue;
            }
            finally {
                method.releaseConnection();
            }
            
            break;
        }
        
        if(stocks == null) throw new StockNotFoundException("Cannot get codes=" + codes);
        
        return stocks;
    }
    
    @Override
    public java.util.List<Stock> getAllStocks() throws StockNotFoundException
    {
        List<Stock> stocks = new ArrayList<Stock>();
        Set<Code> codes = new HashSet<Code>();        

        Thread currentThread = Thread.currentThread();
        
        server_label:
        for(String server : servers) {
            int from = 0, to = 80;
            final int increase = 70;
            
            if(currentThread.isInterrupted()) throw new StockNotFoundException("Thread has been interrupted");
            
            do {

                HttpMethod method = new GetMethod(server + "rtQuote.dll?GetStockSortByCode&From=" + from + "&To=" + to);

                try {
                    Utils.setHttpClientProxyFromSystemProperties(httpClient);
                    httpClient.executeMethod(method);
                    final String responde = method.getResponseBodyAsString();

                    List<Stock> tmpstocks = stockFormat.parse(responde);

                    if(tmpstocks.size() == 0) {
                        break;
                    }

                    for(Stock stock : tmpstocks) {

                        boolean firstTime = codes.add(stock.getCode());

                        if(firstTime == false) continue;

                        stocks.add(stock);
                        this.notify(this, stocks.size());
                    }
                }
                catch(HttpException exp) {
                    log.error("", exp);
                    continue server_label;
                }
                catch(IOException exp) {
                    log.error("", exp);
                    continue server_label;
                }
                finally {
                    method.releaseConnection();
                }

                from = from + increase;
                to = to + increase;

            }while(true);
            
            break;
        }
        
        if(stocks.size() == 0) {
            throw new StockNotFoundException("Empty data from server");
        }
        
        return stocks;
    }
    
    public int getNumOfServer() {
        return servers.length;
    }
    
    private final StockFormat stockFormat;
    private final HttpClient httpClient;
    private final String username;
    private final String password;
    
    private static final String[] servers = new String[] {
        "http://n2ntbfd01.itradecimb.com/",
        "http://n2ntbfd02.itradecimb.com/",
        "http://n2ntbfd03.itradecimb.com/",
        "http://n2ntbfd04.itradecimb.com/",
        "http://n2ntbfd05.itradecimb.com/",
        "http://n2ntbfd06.itradecimb.com/",
        "http://n2ntbfd07.itradecimb.com/",
        "http://n2ntbfd08.itradecimb.com/",
        "http://n2ntbfd09.itradecimb.com/",
        "http://n2ntbfd10.itradecimb.com/"
    };
    
    private static final Log log = LogFactory.getLog(CIMBStockServer.class);
}
