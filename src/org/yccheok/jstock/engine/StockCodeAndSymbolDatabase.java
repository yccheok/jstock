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
        symbolToCode = new java.util.HashMap<String, String>();
        codeToSymbol = new java.util.HashMap<String, String>();
        
        industryToCodes = new java.util.HashMap<Stock.Industry, List<String>>();
        boardToCodes = new java.util.HashMap<Stock.Board, List<String>>();
        industryToSymbols = new java.util.HashMap<Stock.Industry, List<String>>();
        boardToSymbols = new java.util.HashMap<Stock.Board, List<String>>();
            
        for(Stock.Industry industry : Stock.Industry.values()) {
            industryToCodes.put(industry, new ArrayList<String>());
            industryToSymbols.put(industry, new ArrayList<String>());
        }

        for(Stock.Board board : Stock.Board.values()) {
            boardToCodes.put(board, new ArrayList<String>());
            boardToSymbols.put(board, new ArrayList<String>());
        }
        
        List<Stock> stocks = null;
        
        try {
            stocks = stockServer.getAllStock();
        }
        catch(StockNotFoundException exp) {
            throw exp;
        }
        List<String> tmpSymbols = new ArrayList<String>();        
        List<String> tmpCodes = new ArrayList<String>();
        
        for(Stock stock : stocks)
        {
            String symbol = stock.getSymbol();
            String code = stock.getCode();
            Stock.Industry industry = stock.getIndustry();
            Stock.Board board = stock.getBoard();
            
            if(symbol.length() == 0 || code.length() == 0) continue;
             
            symbolToCode.put(symbol, code);
             
            codeToSymbol.put(code, symbol);
            
            industryToCodes.get(industry).add(code);
            boardToCodes.get(board).add(code);
            industryToSymbols.get(industry).add(symbol);
            boardToSymbols.get(board).add(symbol);
            
            tmpSymbols.add(symbol);
            tmpCodes.add(code);
        }
        
        symbolSearchEngine = new TSTStringSearchEngine(tmpSymbols);
        codeSearchEngine = new TSTStringSearchEngine(tmpCodes);        
    }
    
    public List<String> searchStockSymbols(String symbol) {
        return symbolSearchEngine.searchAll(symbol);
    }
    
    public List<String> searchStockCodes(String code) {
        return codeSearchEngine.searchAll(code);
    }
    
    public String searchStockSymbol(String symbol) {
        return symbolSearchEngine.search(symbol);
    }
    
    public String searchStockCode(String code) {
        return codeSearchEngine.search(code);
    }
    
    public String codeToSymbol(String code) {
        return codeToSymbol.get(code);   
    }

    public String symbolToCode(String symbol) {
        return symbolToCode.get(symbol);
    }

    public Set<String> getSymbols() {
        return symbolToCode.keySet();
    }

    public Set<String> getCodes() {
        return codeToSymbol.keySet();
    }
    
    public List<String> getCodes(Stock.Industry industry)
    {
        return industryToCodes.get(industry);
    }

    public List<String> getCodes(Stock.Board board)
    {
        return boardToCodes.get(board);
    }

    public List<String> getSymbols(Stock.Industry industry)
    {
        return industryToSymbols.get(industry);
    }

    public List<String> getSymbols(Stock.Board board)
    {
        return boardToSymbols.get(board);
    }
    
    Map<String, String> symbolToCode;
    Map<String, String> codeToSymbol;

    Map<Stock.Industry, List<String>> industryToCodes;
    Map<Stock.Board, List<String>> boardToCodes;
    Map<Stock.Industry, List<String>> industryToSymbols;
    Map<Stock.Board, List<String>> boardToSymbols;

    StringSearchEngine symbolSearchEngine;
    StringSearchEngine codeSearchEngine;
}
