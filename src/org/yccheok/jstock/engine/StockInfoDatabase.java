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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * This class is used as a offline database of a stock market. It is able to
 * provide code, symbol, industry and board information, based on user search
 * criteria. 
 * @author yccheok
 */
public class StockInfoDatabase {
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
     * Creates an instance of StockInfoDatabase, based on given list of stocks.
     *
     * @param stocks list of stocks
     */
    public StockInfoDatabase(List<Stock> stocks) {
        java.util.concurrent.locks.ReadWriteLock readWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
        reader = readWriteLock.readLock();
        writer = readWriteLock.writeLock();

        this.init(stocks);
    }

    // Initialize this stock info database, based on given list of stocks.
    private void init(List<Stock> stocks) {
        List<StockInfo> stockInfosWithSymbolAsString = new ArrayList<StockInfo>();

        for (Stock stock : stocks) {
            final Code code = stock.code;
            final Symbol symbol = stock.symbol;
            final Stock.Industry industry = stock.getIndustry();
            final Stock.Board board = stock.getBoard();

            StockInfo stockInfo = StockInfo.newInstance(code, symbol);
            StockInfoWithSymbolAsString stockInfoWithSymbolAsString = new StockInfoWithSymbolAsString(code, symbol);

            // Initialize stockInfos.
            stockInfos.add(stockInfo);
            stockInfosWithSymbolAsString.add(stockInfoWithSymbolAsString);

            // Initialize industryToStockInfos.
            List<StockInfo> _stockInfos = this.industryToStockInfos.get(industry);
            if (_stockInfos == null) {
                _stockInfos = new ArrayList<StockInfo>();
                this.industryToStockInfos.put(industry, _stockInfos);
            }
            _stockInfos.add(stockInfo);

            // Initialize boardToStockInfos.
            _stockInfos = this.boardToStockInfos.get(board);
            if (_stockInfos == null) {
                _stockInfos = new ArrayList<StockInfo>();
                this.boardToStockInfos.put(board, _stockInfos);
            }
            _stockInfos.add(stockInfo);

            // Initialize codeToStockInfo and symbolToStockInfos.
            codeToStockInfos.put(stockInfo.code, stockInfo);
            List<StockInfo> s = symbolToStockInfos.get(stockInfo.symbol);
            if (s == null) {
                s = new ArrayList<StockInfo>();
                symbolToStockInfos.put(stockInfo.symbol, s);
            }
            s.add(stockInfo);
        }

        // Initialize all search engines with correct list of stock info.
        this.symbolPinyinSearchEngine = Utils.isPinyinTSTSearchEngineRequiredForSymbol() ? new PinyinTSTSearchEngine<StockInfo>(stockInfosWithSymbolAsString) : null;
        this.symbolSearchEngine = new TSTSearchEngine<StockInfo>(stockInfosWithSymbolAsString);
        this.codeSearchEngine = new TSTSearchEngine<StockInfo>(stockInfos);
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
        reader.lock();
        try {
            List<StockInfo> _stockInfos  = this.codeSearchEngine.searchAll(string);
            if (_stockInfos.isEmpty()) {
                if (this.symbolPinyinSearchEngine != null) {
                    _stockInfos = this.symbolPinyinSearchEngine.searchAll(string);
                    if (_stockInfos.isEmpty()) {
                        _stockInfos = this.symbolSearchEngine.searchAll(string);
                    }
                } else {
                    _stockInfos = this.symbolSearchEngine.searchAll(string);
                }
            }
            return _stockInfos;
        } finally {
            reader.unlock();
        }
    }

    public List<StockInfo> greedySearchStockInfos(String string) {
        reader.lock();
        try {
            List<StockInfo> _stockInfos  = this.codeSearchEngine.searchAll(string);
            if (_stockInfos.isEmpty()) {
                if (this.symbolPinyinSearchEngine != null) {
                    _stockInfos = this.symbolPinyinSearchEngine.searchAll(string);
                }
            }
            _stockInfos.addAll(this.symbolSearchEngine.searchAll(string));
            
            // Add elements to al, including duplicates.
            // Use LinkedHashSet to preserve order.
            HashSet<StockInfo> hs = new LinkedHashSet<StockInfo>();
            hs.addAll(_stockInfos);
            _stockInfos.clear();
            _stockInfos.addAll(hs);
            
            return _stockInfos;
        } finally {
            reader.unlock();
        }
    }
    
    /**
     * Search best matched stock info based on given searched string. Code will
     * be searched first. If nothing has been found, we will search based on
     * Pinyin symbol (if any). If still nothing has been found, we will search
     * based on symbol.
     *
     * @param string the searched string
     * @return best matched stock info based on searched string. null if not
     * found
     */
    public StockInfo searchStockInfo(String string) {
        reader.lock();
        try {
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
        } finally {
            reader.unlock();
        }
    }

    /**
     * Returns list of stock info based on given industry. Since a new list will
     * be created each time, this method is rather time consuming.
     *
     * @param industry the industry
     * @return a list of stock info based on given industry
     */
    public List<StockInfo> getStockInfos(Stock.Industry industry) {
        reader.lock();
        try {
            final List<StockInfo> list = this.industryToStockInfos.get(industry);
            if(list == null) {
                return Collections.emptyList();
            }
            // Create a new list as StockInfoDatabase is a mutable class.
            return new ArrayList<StockInfo>(list);
        } finally {
            reader.unlock();
        }
    }

    /**
     * Returns list of stock info based on given board. Since a new list will be
     * created each time, this method is rather time consuming.
     *
     * @param board the board
     * @return a list of stock info based on given board
     */
    public List<StockInfo> getStockInfos(Stock.Board board) {
        reader.lock();
        try {
            final List<StockInfo> list = this.boardToStockInfos.get(board);
            if (list == null) {
                return Collections.emptyList();
            }
            // Create a new list as StockInfoDatabase is a mutable class.
            return new ArrayList<StockInfo>(list);
        } finally {
            reader.unlock();
        }
    }

    /**
     * Returns list of all the stock info of this database. Since a new list
     * will be created each time, this method is rather time consuming.
     *
     * @return list of all the stock info of this database
     */
    public List<StockInfo> getStockInfos() {
        reader.lock();
        try {
            // Construct a new list as StockInfoDatabase is a mutable class.
            return new ArrayList<StockInfo>(stockInfos);
        } finally {
            reader.unlock();
        }
    }
    
    /**
     * Remove all user defined stock info from this database.
     *
     * @return true if success
     */
    public boolean removeAllUserDefinedStockInfos() {
        writer.lock();
        try {
            List<StockInfo> _stockInfos = this.industryToStockInfos.get(Stock.Industry.UserDefined);
            if (_stockInfos == null) {
                return false;
            }
            // Use a new list. As the list items hold by industryToStockInfos will
            // be removed in for loop iteration.
            _stockInfos = new ArrayList<StockInfo>(_stockInfos);
            boolean result = true;
            for (StockInfo stockInfo : _stockInfos) {
                result &= this.removeUserDefinedStockInfo(stockInfo);
            }
            return result;
        } finally {
            writer.unlock();
        }
    }

    private boolean removeUserDefinedStockInfo(StockInfo stockInfo) {
        if (stockInfo == null) {
            throw new java.lang.IllegalArgumentException("Stock info cannot be null");
        }

        if (stockInfo.code.toString().trim().length() <= 0 || stockInfo.symbol.toString().trim().length() <= 0) {
            throw new java.lang.IllegalArgumentException("Stock info length cannot be 0");
        }

        final Stock.Industry industry = Stock.Industry.UserDefined;
        final Stock.Board board = Stock.Board.UserDefined;

        // Do call getStockInfos(Stock.Industry) and
        // getStockInfos(Stock.Board), which will give you heavy performance 
        // hit.
        final List<StockInfo> iStockInfos = this.industryToStockInfos.get(industry);
        final List<StockInfo> bStockInfos = this.boardToStockInfos.get(board);
        final List<StockInfo> sStockInfos = this.symbolToStockInfos.get(stockInfo.symbol);
                
        if (iStockInfos == null || false == iStockInfos.contains(stockInfo)) {
            return false;
        }

        if (bStockInfos == null || false == bStockInfos.contains(stockInfo)) {
            return false;
        }

        if (sStockInfos == null || false == sStockInfos.contains(stockInfo)) {
            return false;
        }
        
        if ((!(this.symbolSearchEngine instanceof TSTSearchEngine)) || (!(this.codeSearchEngine instanceof TSTSearchEngine)))
        {
            return false;
        }

        if (this.symbolPinyinSearchEngine != null) {
            if (!(this.symbolPinyinSearchEngine instanceof PinyinTSTSearchEngine)) {
                return false;
            }
        }

        this.stockInfos.remove(stockInfo);

        StockInfoWithSymbolAsString stockInfoWithSymbolAsString = new StockInfoWithSymbolAsString(stockInfo.code, stockInfo.symbol);

        // Update search engine.
        if (this.symbolPinyinSearchEngine != null) {
            ((PinyinTSTSearchEngine<StockInfo>)this.symbolPinyinSearchEngine).remove(stockInfoWithSymbolAsString);
        }
        ((TSTSearchEngine<StockInfo>)this.symbolSearchEngine).remove(stockInfoWithSymbolAsString);
        ((TSTSearchEngine<StockInfo>)this.codeSearchEngine).remove(stockInfo);

        // Update board and industry mapping.
        iStockInfos.remove(stockInfo);
        if (iStockInfos.isEmpty()) {
            this.industryToStockInfos.remove(industry);
        }

        bStockInfos.remove(stockInfo);
        if (bStockInfos.isEmpty()) {
            this.boardToStockInfos.remove(board);
        }

        sStockInfos.remove(stockInfo);
        if (sStockInfos.isEmpty()) {
            this.symbolToStockInfos.remove(stockInfo.symbol);
        }
        
        this.codeToStockInfos.remove(stockInfo.code);
                
        return true;
    }

    /**
     * Returns a list of non user defined stock info.
     *
     * @return a list of non user defined stock info
     */
    public List<StockInfo> getNonUserDefinedStockInfos() {
        reader.lock();
        try {
            List<StockInfo> _stockInfos = new ArrayList<StockInfo>(stockInfos);
            List<StockInfo> userDefinedStockInfos = this.industryToStockInfos.get(Stock.Industry.UserDefined);
            if (userDefinedStockInfos != null) {
                _stockInfos.removeAll(userDefinedStockInfos);
            }
            return _stockInfos;
        } finally {
            reader.unlock();
        }
    }

    /**
     * Returns a list of user defined stock info. Since a new list will be
     * created each time, this method is rather time consuming.
     *
     * @return a list of user defined stock info
     */
    public List<StockInfo> getUserDefinedStockInfos() {
        reader.lock();
        try {
            List<StockInfo> _stockInfos = this.industryToStockInfos.get(Stock.Industry.UserDefined);
            if (_stockInfos == null) {
                // Do not return Collections.emptyList(), as the returned list
                // need to be a mutable list.
                return new ArrayList<StockInfo>();
            }
            // Construct a new list as StockInfoDatabase is a mutable class.
            return new ArrayList<StockInfo>(_stockInfos);
        } finally {
            reader.unlock();
        }
    }

    /**
     * Adds in user defined stock info into this database.
     *
     * @param stockInfo the stock info
     * @return true if success
     */
    public boolean addUserDefinedStockInfo(StockInfo stockInfo) {
        writer.lock();
        try {
            return _addUserDefinedStockInfo(stockInfo);
        } finally {
            writer.unlock();
        }
    }

    private boolean _addUserDefinedStockInfo(StockInfo stockInfo) {
        if (stockInfo == null) {
            throw new java.lang.IllegalArgumentException("Stock info cannot be null");
        }

        if (stockInfo.code.toString().trim().length() <= 0 || stockInfo.symbol.toString().trim().length() <= 0) {
            throw new java.lang.IllegalArgumentException("Stock info length cannot be 0");
        }

        // We need to ensure there is no duplicated stock info being added.
        if (codeToStockInfos.containsKey(stockInfo.code)) {
            return false;
        }

        if ((!(this.symbolSearchEngine instanceof TSTSearchEngine)) || (!(this.codeSearchEngine instanceof TSTSearchEngine))) {
            return false;
        }

        if (this.symbolPinyinSearchEngine != null) {
            if (!(this.symbolPinyinSearchEngine instanceof PinyinTSTSearchEngine)) {
                return false;
            }
        }

        this.stockInfos.add(stockInfo);

        StockInfoWithSymbolAsString stockInfoWithSymbolAsString = new StockInfoWithSymbolAsString(stockInfo.code, stockInfo.symbol);

        // Update search engine.
        if (this.symbolPinyinSearchEngine != null) {
            ((PinyinTSTSearchEngine<StockInfo>)this.symbolPinyinSearchEngine).put(stockInfoWithSymbolAsString);
        }
        ((TSTSearchEngine<StockInfo>)this.symbolSearchEngine).put(stockInfoWithSymbolAsString);
        ((TSTSearchEngine<StockInfo>)this.codeSearchEngine).put(stockInfo);

        // Update board and industry mapping.
        final Stock.Industry industry = Stock.Industry.UserDefined;
        final Stock.Board board = Stock.Board.UserDefined;

        List<StockInfo> _stockInfos = this.industryToStockInfos.get(industry);
        if (_stockInfos == null) {
            _stockInfos = new ArrayList<StockInfo>();
            this.industryToStockInfos.put(industry, _stockInfos);
        }
        _stockInfos.add(stockInfo);

        _stockInfos = this.boardToStockInfos.get(board);
        if (_stockInfos == null) {
            _stockInfos = new ArrayList<StockInfo>();
            this.boardToStockInfos.put(board, _stockInfos);
        }
        _stockInfos.add(stockInfo);

        // Initialize codeToStockInfo and symbolToStockInfos.
        codeToStockInfos.put(stockInfo.code, stockInfo);
        List<StockInfo> s = symbolToStockInfos.get(stockInfo.symbol);
        if (s == null) {
            s = new ArrayList<StockInfo>();
            symbolToStockInfos.put(stockInfo.symbol, s);
        }
        s.add(stockInfo);

        return true;
    }

    /**
     * Returns true if this is an empty database.
     *
     * @return true if this is an empty database
     */
    public boolean isEmpty() {
        reader.lock();
        try {
            return this.stockInfos.isEmpty();
        } finally {
            reader.unlock();
        }
    }

    /**
     * Returns number of stock info in this database.
     *
     * @return number of stock info in this database
     */
    public int size() {
        reader.lock();
        try {
            return this.stockInfos.size();
        } finally {
            reader.unlock();
        }
    }

    /**
     * Converts code to symbol.
     *
     * @param code the code
     * @return symbol based on code to symbol conversion. null if the conversion
     * failed
     */
    public Symbol codeToSymbol(Code code) {
        reader.lock();
        try {
            StockInfo stockInfo = this.codeToStockInfo(code);
            return stockInfo != null ? stockInfo.symbol : null;
        } finally {
            reader.unlock();
        }
    }

    /**
     * Converts code to stock info.
     *
     * @param code the code
     * @return stock info based on code to stock info conversion. null if the
     * conversion failed
     */
    public StockInfo codeToStockInfo(Code code) {
        reader.lock();
        try {
            return codeToStockInfos.get(code);
        } finally {
            reader.unlock();
        }
    }

    /**
     * Converts symbol to list of code.
     *
     * @param symbol the symbol
     * @return list of code based on symbol to code conversion. Empty list will
     * be returned if failed
     */
    public List<StockInfo> symbolToStockInfos(Symbol symbol) {
        reader.lock();
        try {
            List<StockInfo> _stockInfos = symbolToStockInfos.get(symbol);
            if (_stockInfos == null) {
                return java.util.Collections.emptyList();
            }
            return _stockInfos;
        } finally {
            reader.unlock();
        }
    }

    /**
     * Returns list of all the stock board of this database. Since a new list
     * will be created each time, this method is rather time consuming.
     *
     * @return list of all the stock board of this database
     */
    public List<Stock.Board> getBoards() {
        reader.lock();
        try {
            // Construct a new list as StockInfoDatabase is a mutable class.
            return new ArrayList<Stock.Board>(this.boardToStockInfos.keySet());
        } finally {
            reader.unlock();
        }
    }

    /**
     * Returns list of all the stock industry of this database. Since a new list
     * will be created each time, this method is rather time consuming. 
     * 
     * @return list of all the stock industry of this database
     */
    public List<Stock.Industry> getIndustries() {
        reader.lock();
        try {
            // Construct a new list as StockInfoDatabase is a mutable class.
            return new ArrayList<Stock.Industry>(this.industryToStockInfos.keySet());
        } finally {
            reader.unlock();
        }
    }

    // Entire stock info of this database.
    private final List<StockInfo> stockInfos = new ArrayList<StockInfo>();
    // Stock industry to list of stock info mapping.
    private final Map<Stock.Industry, List<StockInfo>> industryToStockInfos = new EnumMap<Stock.Industry, List<StockInfo>>(Stock.Industry.class);
    // Stock board to list of stock info mapping.
    private final Map<Stock.Board, List<StockInfo>> boardToStockInfos = new EnumMap<Stock.Board, List<StockInfo>>(Stock.Board.class);

    // Symbol to list of stock info mapping.
    private transient Map<Symbol, List<StockInfo>> symbolToStockInfos = new HashMap<Symbol, List<StockInfo>>();
    // Code to stock info mapping.
    private transient Map<Code, StockInfo> codeToStockInfos = new HashMap<Code, StockInfo>();

    // Symbol String -> StockInfo (with its toString returns Symbol)
    private transient SearchEngine<StockInfo> symbolSearchEngine;
    // Pinyin String -> StockInfo (with its toString returns Symbol)
    private transient SearchEngine<StockInfo> symbolPinyinSearchEngine;
    // Code String -> StockInfo (with its toString returns Code)
    private transient SearchEngine<StockInfo> codeSearchEngine;

    // Reader and writer locks, so that we can have a thread safe mutable
    // stock info database.
    private transient java.util.concurrent.locks.Lock reader;
    private transient java.util.concurrent.locks.Lock writer;
}
