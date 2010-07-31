/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.util.*;
import org.yccheok.jstock.engine.Stock.Industry;

/**
 *
 * @author yccheok
 */
public class StockCodeAndSymbolDatabase {

    /** 
     * Initializes a newly created {@code StockCodeAndSymbolDatabase} object so 
     * that it contains the available stock codes and symbols information for
     * a stock market. The information is being retrieved from stock server.
     *
     * @param stockServer Stock server which provides stock information
     * @throws StockNotFoundException If fail to retrieve stock information from
     *         stock server
     */
    public StockCodeAndSymbolDatabase(StockServer stockServer) throws StockNotFoundException {
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
     * Initializes a newly created {@code StockCodeAndSymbolDatabase} object so
     * that it contains the available stock codes and symbols information for
     * a stock market. The information is being retrieved from list of stocks.
     *
     * @param stocks List of stocks which provides stock information
     */
    public StockCodeAndSymbolDatabase(List<Stock> stocks) {
        this.init(stocks);
    }

    /**
     * Initializes this {@code StockCodeAndSymbolDatabase} based on given list
     * of stocks.
     *
     * @param stocks List of stocks which provides stock information
     */
    private void init(List<Stock> stocks) {
        for (Stock stock : stocks) {
            Symbol symbol = stock.getSymbol();
            Code code = stock.getCode();
            Stock.Industry industry = stock.getIndustry();
            Stock.Board board = stock.getBoard();

            if (symbol.toString().length() == 0 || code.toString().length() == 0) {
                continue;
            }

            this.codes.add(code);
            this.symbols.add(symbol);
            this.symbolToCode.put(symbol, code);
            this.codeToSymbol.put(code, symbol);

            List<Code> _codes = this.industryToCodes.get(industry);
            if (_codes == null) {
                _codes = new ArrayList<Code>();
                this.industryToCodes.put(industry, _codes);
            }
            _codes.add(code);

            _codes = this.boardToCodes.get(board);
            if (_codes == null) {
                _codes = new ArrayList<Code>();
                this.boardToCodes.put(board, _codes);
            }
            _codes.add(code);

            List<Symbol> _symbols = this.industryToSymbols.get(industry);
            if(_symbols == null) {
                _symbols = new ArrayList<Symbol>();
                this.industryToSymbols.put(industry, _symbols);
            }
            _symbols.add(symbol);

            _symbols = this.boardToSymbols.get(board);
            if (_symbols == null) {
                _symbols = new ArrayList<Symbol>();
                this.boardToSymbols.put(board, _symbols);
            }
            _symbols.add(symbol);
        }

        // Initialize all search engines.
        this.symbolPinyinSearchEngine = Utils.isPinyinTSTSearchEngineRequiredForSymbol() ? new PinyinTSTSearchEngine<Symbol>(this.symbols) : null;
        this.symbolSearchEngine = new TSTSearchEngine<Symbol>(this.symbols);
        this.codeSearchEngine = new TSTSearchEngine<Code>(this.codes);
    }

    /**
     * Initializes a newly created {@code StockCodeAndSymbolDatabase} object so
     * that it contains same stock information as argument; in other words, the
     * newly created database is a copy of the argument database.
     *
     * @param src A {@code StockCodeAndSymbolDatabase}
     */
    public StockCodeAndSymbolDatabase(StockCodeAndSymbolDatabase src) {
        this.symbolToCode.putAll(src.symbolToCode);
        this.codeToSymbol.putAll(src.codeToSymbol);

        deepCopy(this.industryToCodes, src.industryToCodes);
        deepCopy(this.boardToCodes, src.boardToCodes);
        deepCopy(this.industryToSymbols, src.industryToSymbols);
        deepCopy(this.boardToSymbols, src.boardToSymbols);
        
        this.symbols = new ArrayList<Symbol>(src.symbols);
        this.codes = new ArrayList<Code>(src.codes);
        
        // Initialize all search engines.
        this.symbolPinyinSearchEngine = Utils.isPinyinTSTSearchEngineRequiredForSymbol() ? new PinyinTSTSearchEngine<Symbol>(this.symbols) : null;
        this.symbolSearchEngine = new TSTSearchEngine<Symbol>(this.symbols);
        this.codeSearchEngine = new TSTSearchEngine<Code>(this.codes);
    }
    
    @SuppressWarnings("unchecked")
    private void deepCopy(Map dest, Map src) {
        Set set = src.keySet();
        for(Object o : set) {
            List srcList = (List)src.get(o);            
            List destList = new ArrayList(srcList);
            dest.put(o, destList);
        }
    }
    
    public List<Symbol> searchStockSymbols(String symbol) {
        if (this.symbolPinyinSearchEngine != null) {
            // Can symbol is pinyin?
            final List<Symbol> _symbols = this.symbolPinyinSearchEngine.searchAll(symbol);
            if (_symbols.isEmpty()) {
                // Nope. Perform deeper search.
                return Collections.unmodifiableList(this.symbolSearchEngine.searchAll(symbol));
            }
            else {
                return Collections.unmodifiableList(_symbols);
            }
        }
        // Need not to involve pinyin search engine.
        return Collections.unmodifiableList(this.symbolSearchEngine.searchAll(symbol));
    }
    
    public List<Code> searchStockCodes(String code) {
        return Collections.unmodifiableList(this.codeSearchEngine.searchAll(code));
    }
    
    public Symbol searchStockSymbol(String symbol) {
        if (this.symbolPinyinSearchEngine != null) {
            // Can symbol is pinyin?
            final Symbol _symbol = this.symbolPinyinSearchEngine.search(symbol);
            if (_symbol == null) {
                // Nope. Perform deeper search.
                return this.symbolSearchEngine.search(symbol);
            }
            else {
                return _symbol;
            }
        }
        // Need not to involve pinyin search engine.
        return this.symbolSearchEngine.search(symbol);
    }
    
    public Code searchStockCode(String code) {
        return this.codeSearchEngine.search(code);
    }
    
    public Symbol codeToSymbol(Code code) {
        return this.codeToSymbol.get(code);
    }

    public Code symbolToCode(Symbol symbol) {
        return this.symbolToCode.get(symbol);
    }

    public List<Symbol> getSymbols() {
        // Do not return Set by Collections.unmodifiableSet(codeToSymbol.keySet())
        // We need to ensure by applying a single index on the data structures
        // returned by getCodes and getSymbols, we should able to get code and
        // symbol which are associated with the same stock.    
        return Collections.unmodifiableList(this.symbols);
    }

    public List<Code> getCodes() {
        // Do not return Set by Collections.unmodifiableSet(codeToSymbol.keySet())
        // We need to ensure by applying a single index on the data structures
        // returned by getCodes and getSymbols, we should able to get code and
        // symbol which are associated with the same stock.
        return Collections.unmodifiableList(this.codes);
    }
    
    public List<Code> getCodes(Stock.Industry industry)
    {
        final List<Code> list = this.industryToCodes.get(industry);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);        
    }

    public List<Code> getCodes(Stock.Board board)
    {
        final List<Code> list = this.boardToCodes.get(board);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);        
    }

    public List<Symbol> getSymbols(Stock.Industry industry)
    {
        final List<Symbol> list = this.industryToSymbols.get(industry);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    public List<Symbol> getSymbols(Stock.Board board)
    {
        final List<Symbol> list = this.boardToSymbols.get(board);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);        
    }
    
    public Set<Stock.Industry> getIndustries() {
        return Collections.unmodifiableSet(this.industryToCodes.keySet());
    }
    
    public Set<Stock.Board> getBoards() {                
        return Collections.unmodifiableSet(this.boardToCodes.keySet());
    }
    
    private Object readResolve() {
        /* For backward compatible */
        if (this.symbols == null || this.symbols.size() <= 0) {
            if (this.symbols == null) {
            	this.symbols = new ArrayList<Symbol>();
            }

            // During iteration for symbols and codes, we must use key set from
            // same data structure (either industryToCodes or industryToSymbols).
            // We need to ensure by applying a single index on the data structures
            // returned by getCodes and getSymbols, we should able to get code and
            // symbol which are associated with the same stock.
            //
            // Hence, do not use industryToSymbols.keySet.
            for (Industry industry : this.industryToCodes.keySet())
            {
                this.symbols.addAll(this.industryToSymbols.get(industry));
            }
        }

        /* For backward compatible */
        if (this.codes == null || this.codes.size() <= 0) {
            if (this.codes == null) {
            	this.codes = new ArrayList<Code>();
            }
            
            // See the above comment on why we need to use industryToCodes.keySet.
            for (Industry industry : industryToCodes.keySet())
            {
                this.codes.addAll(this.industryToCodes.get(industry));
            }
        }

        // Initialize all search engines.
        this.symbolPinyinSearchEngine = Utils.isPinyinTSTSearchEngineRequiredForSymbol() ? new PinyinTSTSearchEngine<Symbol>(this.symbols) : null;
        this.symbolSearchEngine = new TSTSearchEngine<Symbol>(this.symbols);
        this.codeSearchEngine = new TSTSearchEngine<Code>(this.codes);

        return this;
    }

    protected final Map<Symbol, Code> symbolToCode = new HashMap<Symbol, Code>();
    protected final Map<Code, Symbol> codeToSymbol = new HashMap<Code, Symbol>();

    protected final Map<Stock.Industry, List<Code>> industryToCodes = new EnumMap<Stock.Industry, List<Code>>(Stock.Industry.class);
    protected final Map<Stock.Board, List<Code>> boardToCodes = new EnumMap<Stock.Board, List<Code>>(Stock.Board.class);
    protected final Map<Stock.Industry, List<Symbol>> industryToSymbols = new EnumMap<Stock.Industry, List<Symbol>>(Stock.Industry.class);
    protected final Map<Stock.Board, List<Symbol>> boardToSymbols = new EnumMap<Stock.Board, List<Symbol>>(Stock.Board.class);

    protected List<Symbol> symbols = new ArrayList<Symbol>();
    protected List<Code> codes = new ArrayList<Code>();

    protected transient SearchEngine<Symbol> symbolSearchEngine;
    protected transient SearchEngine<Symbol> symbolPinyinSearchEngine;
    protected transient SearchEngine<Code> codeSearchEngine;
}
