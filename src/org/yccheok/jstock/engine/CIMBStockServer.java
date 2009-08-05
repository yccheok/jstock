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
        
        this.username = username;
        this.password = password;        
    }
    
    @Override
    public Stock getStock(Symbol symbol) throws StockNotFoundException
    {
        initServers();

        String _symbol;

        try {
            _symbol = java.net.URLEncoder.encode(symbol.toString(), "UTF-8");
        }
        catch(java.io.UnsupportedEncodingException exp) {
            throw new StockNotFoundException("symbol=" + symbol, exp);
        }
                
        java.util.List<Stock> stocks = null;

        Thread currentThread = Thread.currentThread();

        final HttpClient httpClient = new HttpClient();

        int index = 0;

        for (String server : servers) {
            if (currentThread.isInterrupted()) {
				throw new StockNotFoundException("Thread has been interrupted");
			}
        
            HttpMethod method = new GetMethod(server + "rtQuote.dll?GetStockGeneral&Key=" + _symbol);                        
            
            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(httpClient);

				final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(httpClient, method);

                stocks = stockFormat.parse(respond);

                if (stocks.size() != 1) {
                    log.error("Number of stock (" + stocks.size() + ") is not 1");
                    continue;
                }

               // Sort the best server.
                if (bestServerAlreadySorted == false) {
                    synchronized(servers) {
                        if (bestServerAlreadySorted == false) {
                            bestServerAlreadySorted = true;
                            String tmp = servers.get(0);
                            servers.set(0, servers.get(index));
                            servers.set(index, tmp);
                        }
                    }
                }
            }
            catch (HttpException exp) {
                log.error("symbol=" + symbol, exp);
                continue;
            }
            catch (IOException exp) {
                log.error("symbol=" + symbol, exp);
                continue;
            }
            finally {
                method.releaseConnection();
                index++;
            }
            
            break;
        }
        
        if(stocks == null) throw new StockNotFoundException("Cannot get symbol=" + symbol);
        
        return stocks.get(0);
    }
    
    @Override
    public Stock getStock(Code code) throws StockNotFoundException
    {
        // The nice CIMB server are able to accept both code and name.
        final Code newCode = Utils.toCIMBFormat(code, Country.Malaysia);

        return this.getStock(Symbol.newInstance(newCode.toString()));
    }
    
    @Override
    public java.util.List<Stock> getStocksBySymbols(java.util.List<Symbol> symbols) throws StockNotFoundException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    
    @Override
    public java.util.List<Stock> getStocksByCodes(java.util.List<Code> codes) throws StockNotFoundException
    {
        initServers();

        String _codes;
        StringBuffer _codesBuffer = new StringBuffer();        
        
        for (Code code : codes) {
            final Code newCode = Utils.toCIMBFormat(code, Country.Malaysia);
            _codesBuffer.append(newCode);
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

        final HttpClient httpClient = new HttpClient();

        int index = 0;

        for (String server : servers) {
            if (currentThread.isInterrupted()) {
                throw new StockNotFoundException("Thread has been interrupted");
            }

            /* ascending order */
            HttpMethod method = new GetMethod(server + "rtQuote.dll?GetStockInfoSortByCode&StockList=" + _codes + "&SortDesc=0");
            // method.getParams().setParameter("http.socket.timeout", new Integer(5000));

            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(httpClient);
				final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(httpClient, method);

                
                stocks = stockFormat.parse(respond);

                //if(stocks.size() != codes.size()) {
                //    log.error("Number of stock (" + stocks.size() + ") is not " + codes.size());
                //    continue;
                //}
                // For CIMB server, if we pass one good code and one bad code to it. It will only return
                // stock of the good code. Hence, we can easily faill into case stocks.size() != codes.size()
                // Hence, it is must more preferable that we create our own empty stock for those bad code.
                if (stocks.size() != codes.size()) {
                    if (stocks.size() <= 0) {
                        // All bad codes. Retry.
                        log.error("Number of stock (" + stocks.size() + ") is not " + codes.size());
                        continue;
                    }

                    for (Code code : codes) {
                        if (stocks.size() >= codes.size()) {
                            break;
                        }

                        boolean found = false;
                        for (Stock stock : stocks) {
                            if (stock.getCode().equals(code)) {
                                found = true;
                                break;
                            }
                        }
                        if (found != true) {
                        	// Create fake stock if we haven't do so.
                            stocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(code, Symbol.newInstance(code.toString())));
                        }
                    }   /* for (Code code : codes) */
                }   /* if (stocks.size() != codes.size()) */

               // Sort the best server.
                if (bestServerAlreadySorted == false) {
                    synchronized(servers) {
                        if (bestServerAlreadySorted == false) {
                            bestServerAlreadySorted = true;
                            String tmp = servers.get(0);
                            servers.set(0, servers.get(index));
                            servers.set(index, tmp);
                        }
                    }
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
                index++;
            }
            
            break;
        }

        // Is it necessary to perform null check?
        if (stocks == null) throw new StockNotFoundException("Cannot get codes=" + codes);
        
        if (stocks.size() != codes.size()) throw new StockNotFoundException("Number of stock (" + stocks.size() + ") is not " + codes.size());
        
        return stocks;
    }
    
    @Override
    public java.util.List<Stock> getAllStocks() throws StockNotFoundException
    {
        initServers();

        List<Stock> stocks = new ArrayList<Stock>();
        Set<Code> codes = new HashSet<Code>();        

        Thread currentThread = Thread.currentThread();

        final HttpClient httpClient = new HttpClient();
        
        server_label:
        for (String server : servers) {
            int from = 0, to = 80;
            final int increase = 70;
            
            if (currentThread.isInterrupted()) {
				throw new StockNotFoundException("Thread has been interrupted");
			}
            
            do {

                HttpMethod method = new GetMethod(server + "rtQuote.dll?GetStockSortByCode&From=" + from + "&To=" + to);

                try {
                    Utils.setHttpClientProxyFromSystemProperties(httpClient);
                    org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(httpClient);
					final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(httpClient, method);


                    List<Stock> tmpstocks = stockFormat.parse(respond);

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
        initServers();
        return servers.size();
    }

    private void initServers() {
        // Already initialized. Return early.
        if (this.servers != null) {
            return;
        }

        synchronized(this) {
            // Already initialized. Return early.
            if (this.servers != null) {
                return;
            }

            this.servers = Utils.getCIMBStockServers();
        }
    }

    private final StockFormat stockFormat;
    private final String username;
    private final String password;

    // Do not initialize servers in constructor. Initialization will be time
    // consuming since we need to connect to sourceforge to retrieve server
    // information. If most of the time taken up in constructor, our GUI will
    // be slow to show up.
    // Only initialize it when we need it.
    private List<String> servers;
    // We had already discover the best server. Please take note that,
    // synchronized is required during best server sorting. Hence, we will
    // use this flag to help us only perform sorting once.
    private volatile boolean bestServerAlreadySorted = false;
    
    private static final Log log = LogFactory.getLog(CIMBStockServer.class);
}
