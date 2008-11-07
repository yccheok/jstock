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

import java.util.List;

/**
 *
 * @author yccheok
 */
public class YahooStockServer implements StockServer {

    public Stock getStock(Symbol symbol) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Stock getStock(Code code) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Stock> getStocksBySymbols(List<Symbol> symbols) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Stock> getStocksByCodes(List<Code> codes) throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Stock> getAllStocks() throws StockNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
