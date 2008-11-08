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
        symbolToCode = new java.util.HashMap<Symbol, Code>();
        codeToSymbol = new java.util.HashMap<Code, Symbol>();
        
        industryToCodes = new java.util.HashMap<Stock.Industry, List<Code>>();
        boardToCodes = new java.util.HashMap<Stock.Board, List<Code>>();
        industryToSymbols = new java.util.HashMap<Stock.Industry, List<Symbol>>();
        boardToSymbols = new java.util.HashMap<Stock.Board, List<Symbol>>();
            
        for(Stock.Industry industry : Stock.Industry.values()) {
            industryToCodes.put(industry, new ArrayList<Code>());
            industryToSymbols.put(industry, new ArrayList<Symbol>());
        }

        for(Stock.Board board : Stock.Board.values()) {
            boardToCodes.put(board, new ArrayList<Code>());
            boardToSymbols.put(board, new ArrayList<Symbol>());
        }
        
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
            
            industryToCodes.get(industry).add(code);
            boardToCodes.get(board).add(code);
            industryToSymbols.get(industry).add(symbol);
            boardToSymbols.get(board).add(symbol);
            
            tmpSymbols.add(symbol);
            tmpCodes.add(code);
        }
        
        symbolSearchEngine = new TSTSearchEngine<Symbol>(tmpSymbols);
        codeSearchEngine = new TSTSearchEngine<Code>(tmpCodes);        
    }
    
    public List<Symbol> searchStockSymbols(String symbol) {
        return symbolSearchEngine.searchAll(symbol);
    }
    
    public List<Code> searchStockCodes(String code) {
        return codeSearchEngine.searchAll(code);
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
        return symbolToCode.keySet();
    }

    public Set<Code> getCodes() {
        return codeToSymbol.keySet();
    }
    
    public List<Code> getCodes(Stock.Industry industry)
    {
        return industryToCodes.get(industry);
    }

    public List<Code> getCodes(Stock.Board board)
    {
        return boardToCodes.get(board);
    }

    public List<Symbol> getSymbols(Stock.Industry industry)
    {
        return industryToSymbols.get(industry);
    }

    public List<Symbol> getSymbols(Stock.Board board)
    {
        return boardToSymbols.get(board);
    }
    
    private Object readResolve() {
        List<Symbol> tmpSymbols = new ArrayList<Symbol>(symbolToCode.keySet());
        List<Code> tmpCodes = new ArrayList<Code>(codeToSymbol.keySet());
        
        symbolSearchEngine = new TSTSearchEngine<Symbol>(tmpSymbols);
        codeSearchEngine = new TSTSearchEngine<Code>(tmpCodes);         
        
        return this;
    }
    
    private Map<Symbol, Code> symbolToCode;
    private Map<Code, Symbol> codeToSymbol;

    private Map<Stock.Industry, List<Code>> industryToCodes;
    private Map<Stock.Board, List<Code>> boardToCodes;
    private Map<Stock.Industry, List<Symbol>> industryToSymbols;
    private Map<Stock.Board, List<Symbol>> boardToSymbols;

    private transient SearchEngine<Symbol> symbolSearchEngine;
    private transient SearchEngine<Code> codeSearchEngine;
}
