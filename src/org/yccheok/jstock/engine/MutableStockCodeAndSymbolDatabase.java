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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author yccheok
 */
public class MutableStockCodeAndSymbolDatabase extends StockCodeAndSymbolDatabase {
    public MutableStockCodeAndSymbolDatabase(StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase) {
        super(stockCodeAndSymbolDatabase);
    }
    
    public StockCodeAndSymbolDatabase toStockCodeAndSymbolDatabase() {
        StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase = new StockCodeAndSymbolDatabase(this);
        return stockCodeAndSymbolDatabase;
    }
    
    // Not thread safe.
    // getUserDefinedSymbol shall return list with non-duplicated symbols. If not,
    // we shall consider this method as buggy method. Please refer to tracker record :
    // [ 2617022 ] NPE When Invoking Stock Database Dialog
    public List<Symbol> getUserDefinedSymbol() {
        List<Symbol> symbols = this.industryToSymbols.get(Stock.Industry.UserDefined);
        if (symbols == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Symbol>(symbols);
    }
    
    public boolean removeUserDefinedSymbol(Symbol symbol) {        
        if(symbol == null) {
            throw new java.lang.IllegalArgumentException("Code or symbol cannot be null");
        }
        
        final Code code = symbolToCode.get(symbol);
        
        // We should ensure the symbol which we intend to remove is inside the
        // database. In our case, the symbols which we wish to remove are coming from
        // MutableStockCodeAndSymbolDatabase's getUserDefinedSymbol. Although
        // this ensures code will never be null. Still, without null checking,
        // our code may break, if getUserDefinedSymbol is returning duplicated List
        // which is containing duplicated symbols.
        // Hence, it is better to leave null checking here.
        if (code == null) {
            return false;
        }

        final Stock.Industry industry = Stock.Industry.UserDefined;
        final Stock.Board board = Stock.Board.UserDefined;
        
        final List<Code> iCodes = industryToCodes.get(industry);
        final List<Code> bCodes = boardToCodes.get(board);
        final List<Symbol> iSymbols = industryToSymbols.get(industry);
        final List<Symbol> bSymbols = boardToSymbols.get(board);

        if (iCodes == null || bCodes == null || iSymbols == null || bSymbols == null) {
            return false;
        }
        
        Code searchedCode = this.codeSearchEngine.search(code.toString());
        Symbol searchedSymbol = this.symbolSearchEngine.search(symbol.toString());
        
        if (searchedCode == null) return false;
        if (searchedSymbol == null) return false;
        
        if (!searchedCode.equals(code)) {
            return false;
        }
        
        if (!searchedSymbol.equals(symbol)) {
            return false;
        }  
        
        if ((!(symbolSearchEngine instanceof TSTSearchEngine)) || (!(codeSearchEngine instanceof TSTSearchEngine)))
        {
            return false;
        }
        
        ((TSTSearchEngine<Symbol>)symbolSearchEngine).remove(symbol.toString());
        ((TSTSearchEngine<Code>)codeSearchEngine).remove(code.toString());
        
        symbolToCode.remove(symbol);
        codeToSymbol.remove(code);

        iCodes.remove(code);
        if(iCodes.size() <= 0) industryToCodes.remove(industry);

        bCodes.remove(code);
        if(bCodes.size() <= 0) boardToCodes.remove(board);

        iSymbols.remove(symbol);
        if(iSymbols.size() <= 0) industryToSymbols.remove(industry);

        bSymbols.remove(symbol);
        if(bSymbols.size() <= 0) boardToSymbols.remove(board);
        
        return true;        
    }
    
    // Not thread safe.
    public boolean addUserDefinedSymbol(Symbol symbol) {
        if (symbol == null) {
            throw new java.lang.IllegalArgumentException("Code or symbol cannot be null");
        }
        
        if (symbol.toString().length() <= 0) {
            throw new java.lang.IllegalArgumentException("Code or symbol length cannot be 0");
        }

        final Code code = Code.newInstance(symbol.toString());

        // We need to ensure there is no duplicated code or symbol being added.
        if (symbolToCode.containsKey(symbol) || codeToSymbol.containsKey(code)) {
            return false;
        }

        if((!(symbolSearchEngine instanceof TSTSearchEngine)) || (!(codeSearchEngine instanceof TSTSearchEngine)))
        {
            return false;
        }
        
        ((TSTSearchEngine<Symbol>)symbolSearchEngine).put(symbol.toString(), symbol);
        ((TSTSearchEngine<Code>)codeSearchEngine).put(code.toString(), code);
        
        symbolToCode.put(symbol, code);
        codeToSymbol.put(code, symbol);
        
        final Stock.Industry industry = Stock.Industry.UserDefined;
        final Stock.Board board = Stock.Board.UserDefined;
        
        List<Code> codes = industryToCodes.get(industry);
        if (codes == null) {
            codes = new ArrayList<Code>();
            industryToCodes.put(industry, codes);                
        }
        codes.add(code);

        codes = boardToCodes.get(board);
        if (codes == null) {
            codes = new ArrayList<Code>();
            boardToCodes.put(board, codes);                
        }
        codes.add(code);

        List<Symbol> symbols = industryToSymbols.get(industry);
        if (symbols == null) {
            symbols = new ArrayList<Symbol>();
            industryToSymbols.put(industry, symbols);                
        }
        symbols.add(symbol);

        symbols = boardToSymbols.get(board);
        if (symbols == null) {
            symbols = new ArrayList<Symbol>();
            boardToSymbols.put(board, symbols);                
        }
        symbols.add(symbol);
        
        return true;
    }    
}
