/*
 * CIMBStockServerFactory.java
 *
 * Created on May 1, 2007, 11:10 PM
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class CIMBStockServerFactory implements StockServerFactory {
    
    /** Creates a new instance of CIMBStockServerFactory */
    private CIMBStockServerFactory(String username, String password) {
        this.username = username;
        this.password = password;
        stockServer = new CIMBStockServer(username, password);
        marketServer = new CIMBMarketServer();
    }
    
    public static StockServerFactory newInstance(String usename, String password) {
        return new CIMBStockServerFactory(usename, password);
    }
    
    @Override
    public StockServer getStockServer()
    {        
        return stockServer;
    }
    
    @Override
    public StockHistoryServer getStockHistoryServer(Code code)
    {
        try {
            return new CIMBStockHistoryServer(username, password, code);
        }
        catch(StockHistoryNotFoundException exp) {
            log.error("", exp);
            return null;
        }
    }
    
    public MarketServer getMarketServer() {
        return marketServer;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public StockHistoryServer getStockHistoryServer(Code code, org.yccheok.jstock.engine.Duration duration) {
        return getStockHistoryServer(code);
    }

    private final String username;
    private final String password;
    private final StockServer stockServer;
    private final MarketServer marketServer;
    private static final Log log = LogFactory.getLog(CIMBStockServer.class);
}
