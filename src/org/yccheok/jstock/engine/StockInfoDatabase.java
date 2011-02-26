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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used as a offline database of a stock market. It is able to
 * provide code, symbol, industry and board information, based on user search
 * criteria. 
 * @author yccheok
 */
public class StockInfoDatabase {
    // A stock info which its toString will return code.
    // Having a correct implementation of toString is important as :
    // 1) Our search engine build the key index through toString.
    // 2) Our auto complete combo box display its drop down list items based on
    //    toString.
    private static final class StockInfoWithCodeAsString extends StockInfo {
        public StockInfoWithCodeAsString(Code code, Symbol symbol) {
            super(code, symbol);
        }

        @Override
        public String toString() {
            return this.code.toString();
        }
    }

    // A stock info which its toString will return symbol.
    // Having a correct implementation of toString is important as :
    // 1) Our search engine build the key index through toString.
    // 2) Our auto complete combo box display its drop down list items based on
    //    toString.
    private static final class StockInfoWithSymbolAsString extends StockInfo {
        public StockInfoWithSymbolAsString(Code code, Symbol symbol) {
            super(code, symbol);
        }

        @Override
        public String toString() {
            return this.symbol.toString();
        }
    }

    /**
     * Creates an instance of StockInfoDatabase, based on given stock server.
     * We will get all stocks from the stock server, and build a stock info
     * database based on the stocks.
     * 
     * @param stockServer the stock server
     * @throws StockNotFoundException if stock is not found for the given stock
     * server
     */
    public StockInfoDatabase(StockServer stockServer) throws StockNotFoundException {
        List<Stock> stocks = null;

        try {
            stocks = stockServer.getAllStocks();
        }
        catch (StockNotFoundException exp) {
            throw exp;
        }

        this.init(stocks);
    }

    /**
     * Creates an instance of StockInfoDatabase, based on given list of stocks.
     *
     * @param stocks list of stocks
     */
    public StockInfoDatabase(List<Stock> stocks) {
        this.init(stocks);
    }

    // Initialize this stock info database, based on given list of stocks.
    private void init(List<Stock> stocks) {
        List<StockInfo> stockInfosWithCodeAsString = new ArrayList<StockInfo>();
        List<StockInfo> stockInfosWithSymbolAsString = new ArrayList<StockInfo>();

        for (Stock stock : stocks) {
            stockInfosWithCodeAsString.add(new StockInfoWithCodeAsString(stock.getCode(), stock.getSymbol()));
            stockInfosWithSymbolAsString.add(new StockInfoWithSymbolAsString(stock.getCode(), stock.getSymbol()));
        }

        // Initialize all search engines with correct list of stock info.
        this.symbolPinyinSearchEngine = Utils.isPinyinTSTSearchEngineRequiredForSymbol() ? new PinyinTSTSearchEngine<StockInfo>(stockInfosWithCodeAsString) : null;
        this.symbolSearchEngine = new TSTSearchEngine<StockInfo>(stockInfosWithSymbolAsString);
        this.codeSearchEngine = new TSTSearchEngine<StockInfo>(stockInfosWithCodeAsString);
    }

    /**
     * Search for list of stock info based on given searched string. Code will
     * be searched first. If nothing has been found, we will search based on
     * Pinyin symbol (if any). If still nothing has been found, we will search
     * based on symbol.
     *
     * @param string the searched string
     * @return list of stock info based on given searched string
     */
    public List<StockInfo> searchStockInfos(String string) {
        List<StockInfo> stockInfos  = this.codeSearchEngine.searchAll(string);
        if (stockInfos.isEmpty()) {
            if (this.symbolPinyinSearchEngine != null) {
                stockInfos = this.symbolPinyinSearchEngine.searchAll(string);
                if (stockInfos.isEmpty()) {
                    stockInfos = this.symbolSearchEngine.searchAll(string);
                }
            } else {
                stockInfos = this.symbolSearchEngine.searchAll(string);
            }
        }
        return Collections.unmodifiableList(stockInfos);
    }

    /**
     * Search best matched stock info based on given searched string. Code will
     * be searched first. If nothing has been found, we will search based on
     * Pinyin symbol (if any). If still nothing has been found, we will search
     * based on symbol.
     *
     * @param string the searched string
     * @return best matched stock info based on searched string
     */
    public StockInfo searchStockInfo(String string) {
        StockInfo stockInfo = this.codeSearchEngine.search(string);
        if (null == stockInfo) {
            if (this.symbolPinyinSearchEngine != null) {
                stockInfo = this.symbolPinyinSearchEngine.search(string);
                if (null == stockInfo) {
                    stockInfo = this.symbolSearchEngine.search(string);
                }
            } else {
                stockInfo = this.symbolSearchEngine.search(string);
            }
        }
        return stockInfo;
    }

    // Symbol String -> StockInfo (with its toString returns Symbol)
    private transient SearchEngine<StockInfo> symbolSearchEngine;
    // Pinyin String -> StockInfo (with its toString returns Symbol)
    private transient SearchEngine<StockInfo> symbolPinyinSearchEngine;
    // Code String -> StockInfo (with its toString returns Code)
    private transient SearchEngine<StockInfo> codeSearchEngine;
}
