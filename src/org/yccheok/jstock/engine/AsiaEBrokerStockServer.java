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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yccheok
 */
public class AsiaEBrokerStockServer implements StockServer {

    @Override
    public Stock getStock(Symbol symbol) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Stock getStock(Code code) throws StockNotFoundException {
        List<Code> codes = new ArrayList<Code>();
        codes.add(code);
        // No need perform checking. As getStocksByCodes will always make sure
        // returned list will same size as codes. If not, StockNotFoundException
        // will be thrown.
        return getStocksByCodes(codes).get(0);
    }

    @Override
    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        final int size = codes.size();
        if (size <= 0) {
            throw new IllegalArgumentException("Codes cannot be 0 length");
        }
        initServers();
        // 7 = length of 1234.KL. Multiple 7 with a 2 factor.
        final StringBuilder stringBuilder = new StringBuilder(size * 14);
        for (Code code : codes) {
            stringBuilder.append(code.toString()).append(",");
        }
        // -1 to remove tailing ,
        final String form_data = String.format(STOCK_DETAILS_FORM_DATA, size, stringBuilder.substring(0, stringBuilder.length() - 1));
        int server_index = -1;
        for (String server : servers) {
            ++server_index;
            if (Thread.currentThread().isInterrupted()) {
                throw new StockNotFoundException("Thread has been interrupted");
            }
            final String response = org.yccheok.jstock.gui.Utils.getPOSTResponseBodyAsStringBasedOnProxyAuthOption(server + RESOURCE, form_data);
            if (response == null) {
                continue;
            }
            List<Stock> stocks = AsiaEBrokerStockFormat.getInstance().parse(response);
            if (stocks.size() == 0) {
                continue;
            }
            if (stocks.size() == codes.size()) {
                // Sort the best server.
                if (bestServerAlreadySorted == false) {
                    synchronized(servers_lock) {
                        if (bestServerAlreadySorted == false) {
                            bestServerAlreadySorted = true;
                            String tmp = servers.get(0);
                            servers.set(0, servers.get(server_index));
                            servers.set(server_index, tmp);
                        }
                    }
                }
                return stocks;
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
            if (stocks.size() == codes.size()) {
                // Sort the best server.
                if (bestServerAlreadySorted == false) {
                    synchronized(servers_lock) {
                        if (bestServerAlreadySorted == false) {
                            bestServerAlreadySorted = true;
                            String tmp = servers.get(0);
                            servers.set(0, servers.get(server_index));
                            servers.set(server_index, tmp);
                        }
                    }
                }
                return stocks;
            }
        }
        throw new StockNotFoundException("Fail to retrieve multiple codes");
    }

    @Override
    public List<Stock> getAllStocks() throws StockNotFoundException {
        final List<Stock> stocks = new ArrayList<Stock>();
        // Avoid duplication.
        final List<Code> codes = new ArrayList<Code>();

        for (String server : servers) {
            final String response = org.yccheok.jstock.gui.Utils.getPOSTResponseBodyAsStringBasedOnProxyAuthOption(server + RESOURCE, ALL_STOCKS_FORM_DATA);
            if (response == null) {
                continue;
            }
            final String[] tokens = response.split("\r\n|\r|\n");
            for (String token : tokens) {
                String[] token_elements = token.split(",");
                // 7040PA.KL,MMM-PA.KL
                if (token_elements.length < 2) {
                    continue;
                }
                final Code code = Code.newInstance(token_elements[0]);
                final Symbol symbol = Symbol.newInstance(token_elements[1]);
                if (codes.contains(code)) {
                    continue;
                }
                codes.add(code);
                stocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(code, symbol));
            }
        }
        return stocks;
    }

    private void initServers() {
        // Already initialized. Return early.
        if (this.servers != null) {
            return;
        }
        synchronized(servers_lock) {
            // Already initialized. Return early.
            if (this.servers != null) {
                return;
            }

            this.servers = Utils.getAsiaEBrokerStockServers();
        }
    }

    private static final String RESOURCE = "/%5bvUpJYKw4QvGRMBmhATUxRwv4JrU9aDnwNEuangVyy6OuHxi2YiY=%5dImage?";
    private static final String STOCK_DETAILS_FORM_DATA = "[SORT]=0,1,0,10,%d,0,KL,0&[FIELD]=33,38,51,58,68,88,78,98,99,101,56,57,69,70,71,72,89,90,91,92,59,60,61,62,79,80,81,82&[LIST]=%s";
    private static final String ALL_STOCKS_FORM_DATA = "[FAST]=KL&[SECTOR]=10&[FIELD]=33,38";

    // Do not initialize servers in constructor. Initialization will be time
    // consuming since we need to connect to sourceforge to retrieve server
    // information. If most of the time taken up in constructor, our GUI will
    // be slow to show up.
    // Only initialize it when we need it.
    private List<String> servers;
    private final Object servers_lock = new Object();
    // We had already discover the best server. Please take note that,
    // synchronized is required during best server sorting. Hence, we will
    // use this flag to help us only perform sorting once.
    private volatile boolean bestServerAlreadySorted = false;
}
