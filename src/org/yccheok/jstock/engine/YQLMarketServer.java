/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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
 * Concrete implementation of YQL market server.
 */
public class YQLMarketServer extends AbstractYahooMarketServer {
    /**
     * Constructs an instance of YQL market server based on given country.
     *
     * @param country the country
     */
    public YQLMarketServer() {
        super();
    }

    /**
     * Return stock server based on given country. Be caution that, this
     * method will be consumed by parent constructor! On why it is dangerous to
     * consume overridden method within constructor, refer to
     * http://stackoverflow.com/questions/3404301/whats-wrong-with-overridable-method-calls-in-constructors
     * "Effective Java 2nd Edition, Item 17: Design and document for inheritance"
     *
     * @param country the country
     * @return stock server based on given country
     */
    @Override
    protected StockServer getStockServer() {
        // Do not store an instance of YQLStockServer to a member variable
        // during constructor call and return it. This is because this
        // overridden method will be consumed by the parent constructor of
        // YQLMarketServer. When parent constructor consumes getStockServer,
        // YQLMarketServer's constructor is not ready yet. A side effect is
        // that, null pointer will be returned to the parent.
        //
        // This is flaw in our design. But we will live with it at current
        // moment.
        return new YQLStockServer();
    }

}
