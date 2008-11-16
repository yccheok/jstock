/*
 * StockCodeAndSymbolDatabase.java
 *
 * Created on April 21, 2007, 6:13 PM
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

import java.util.*;

/**
 *
 * @author yccheok
 */
public class StockCodeAndSymbolDatabase {
    
    /** Creates a new instance of StockCodeAndSymbolDatabase */
    public StockCodeAndSymbolDatabase(StockServer stockServer) throws StockNotFoundException {
        List<Stock> stocks = null;
        
        try {
            stocks = stockServer.getAllStocks();
        }
        catch(StockNotFoundException exp) {
            throw exp;
        }
        List<Symbol> tmpSymbols = new ArrayList<Symbol>();        
        List<Code> tmpCodes = new ArrayList<Code>();
        
        for(Stock stock : stocks)
        {
            Symbol symbol = stock.getSymbol();
            Code code = stock.getCode();
            Stock.Industry industry = stock.getIndustry();
            Stock.Board board = stock.getBoard();
            
            if(symbol.toString().length() == 0 || code.toString().length() == 0) continue;
             
            symbolToCode.put(symbol, code);
             
            codeToSymbol.put(code, symbol);
            
            List<Code> codes = industryToCodes.get(industry);
            if(codes == null) {
                codes = new ArrayList<Code>();
                industryToCodes.put(industry, codes);                
            }
            codes.add(code);

            codes = boardToCodes.get(board);
            if(codes == null) {
                codes = new ArrayList<Code>();
                boardToCodes.put(board, codes);                
            }
            codes.add(code);

            List<Symbol> symbols = industryToSymbols.get(industry);
            if(symbols == null) {
                symbols = new ArrayList<Symbol>();
                industryToSymbols.put(industry, symbols);                
            }
            symbols.add(symbol);
            
            symbols = boardToSymbols.get(board);
            if(symbols == null) {
                symbols = new ArrayList<Symbol>();
                boardToSymbols.put(board, symbols);                
            }
            symbols.add(symbol);
            
            tmpSymbols.add(symbol);
            tmpCodes.add(code);
        }
        
        symbolSearchEngine = new TSTSearchEngine<Symbol>(tmpSymbols);
        codeSearchEngine = new TSTSearchEngine<Code>(tmpCodes);        
    }
    
    public StockCodeAndSymbolDatabase(StockCodeAndSymbolDatabase src) {
        this.symbolToCode.putAll(src.symbolToCode);
        this.codeToSymbol.putAll(src.codeToSymbol);

        /*
        this.industryToCodes.putAll(src.industryToCodes);
        this.boardToCodes.putAll(src.boardToCodes);
        this.industryToSymbols.putAll(src.industryToSymbols);
        this.boardToSymbols.putAll(src.boardToSymbols);
        */
        deepCopy(this.industryToCodes, src.industryToCodes);
        deepCopy(this.boardToCodes, src.boardToCodes);
        deepCopy(this.industryToSymbols, src.industryToSymbols);
        deepCopy(this.boardToSymbols, src.boardToSymbols);
        
        symbolSearchEngine = new TSTSearchEngine<Symbol>(new ArrayList<Symbol>(symbolToCode.keySet()));
        codeSearchEngine = new TSTSearchEngine<Code>(new ArrayList<Code>(codeToSymbol.keySet()));        
    }
        
    private void deepCopy(Map dest, Map src) {
        Set set = src.keySet();
        for(Object o : set) {
            List srcList = (List)src.get(o);
            List destList = new ArrayList(srcList);
            dest.put(o, destList);
        }
    }
    
    public List<Symbol> searchStockSymbols(String symbol) {
        return Collections.unmodifiableList(symbolSearchEngine.searchAll(symbol));
    }
    
    public List<Code> searchStockCodes(String code) {
        return Collections.unmodifiableList(codeSearchEngine.searchAll(code));
    }
    
    public Symbol searchStockSymbol(String symbol) {
        return symbolSearchEngine.search(symbol);
    }
    
    public Code searchStockCode(String code) {
        return codeSearchEngine.search(code);
    }
    
    public Symbol codeToSymbol(Code code) {
        return codeToSymbol.get(code);   
    }

    public Code symbolToCode(Symbol symbol) {
        return symbolToCode.get(symbol);
    }

    public Set<Symbol> getSymbols() {
        return Collections.unmodifiableSet(symbolToCode.keySet());
    }

    public Set<Code> getCodes() {
        return Collections.unmodifiableSet(codeToSymbol.keySet());
    }
    
    public List<Code> getCodes(Stock.Industry industry)
    {
        final List<Code> list = industryToCodes.get(industry);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);        
    }

    public List<Code> getCodes(Stock.Board board)
    {
        final List<Code> list = boardToCodes.get(board);        
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);        
    }

    public List<Symbol> getSymbols(Stock.Industry industry)
    {
        final List<Symbol> list = industryToSymbols.get(industry);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    public List<Symbol> getSymbols(Stock.Board board)
    {
        final List<Symbol> list = boardToSymbols.get(board);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);        
    }
    
    public Set<Stock.Industry> getIndustries() {
        return Collections.unmodifiableSet(industryToCodes.keySet());
    }
    
    public Set<Stock.Board> getBoards() {                
        return Collections.unmodifiableSet(boardToCodes.keySet());
    }
    
    private Object readResolve() {
        List<Symbol> tmpSymbols = new ArrayList<Symbol>(symbolToCode.keySet());
        List<Code> tmpCodes = new ArrayList<Code>(codeToSymbol.keySet());
        
        symbolSearchEngine = new TSTSearchEngine<Symbol>(tmpSymbols);
        codeSearchEngine = new TSTSearchEngine<Code>(tmpCodes);         
        
        return this;
    }
        
    protected Map<Symbol, Code> symbolToCode = new HashMap<Symbol, Code>();
    protected Map<Code, Symbol> codeToSymbol = new HashMap<Code, Symbol>();

    protected Map<Stock.Industry, List<Code>> industryToCodes = new HashMap<Stock.Industry, List<Code>>();
    protected Map<Stock.Board, List<Code>> boardToCodes = new HashMap<Stock.Board, List<Code>>();
    protected Map<Stock.Industry, List<Symbol>> industryToSymbols = new HashMap<Stock.Industry, List<Symbol>>();
    protected Map<Stock.Board, List<Symbol>> boardToSymbols = new HashMap<Stock.Board, List<Symbol>>();

    protected transient SearchEngine<Symbol> symbolSearchEngine;
    protected transient SearchEngine<Code> codeSearchEngine;
}
