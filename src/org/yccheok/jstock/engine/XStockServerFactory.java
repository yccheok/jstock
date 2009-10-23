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

/**
 *
 * @author yccheok
 */
public class XStockServerFactory implements StockServerFactory {

    private XStockServerFactory() {
    }

    public static StockServerFactory newInstance() {
        return new XStockServerFactory();
    }

    @Override
    public StockServer getStockServer() {
        return stockServer;
    }

    @Override
    public StockHistoryServer getStockHistoryServer(Code code) {
        return null;
    }

    @Override
    public StockHistoryServer getStockHistoryServer(Code code, Duration duration) {
        return null;
    }

    @Override
    public MarketServer getMarketServer() {
        return marketServer;
    }

    private final StockServer stockServer = new XStockServer();
    private final MarketServer marketServer = new XMarketServer();
}
