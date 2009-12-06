/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng CHEOK <yccheok@yahoo.com>
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
        List<Symbol> _symbols = this.industryToSymbols.get(Stock.Industry.UserDefined);
        if (_symbols == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Symbol>(_symbols);
    }

    public List<Code> getUserDefinedCode() {
        List<Code> _codes = this.industryToCodes.get(Stock.Industry.UserDefined);
        if (_codes == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Code>(_codes);
    }

    public boolean removeAllUserDefinedCodeAndSymbol() {
        List<Code> _codes = this.industryToCodes.get(Stock.Industry.UserDefined);
        if (_codes == null) {
            return false;
        }
        boolean result = true;
        _codes = new ArrayList<Code>(_codes);
        for (Code code : _codes) {
            final Symbol symbol = this.codeToSymbol(code);
            result &= this.removeUserDefinedCodeAndSymbol(code, symbol);
        }
        return result;
    }

    public boolean removeUserDefinedCodeAndSymbol(Code code, Symbol symbol) {
        if (symbol == null || code == null) {
            throw new java.lang.IllegalArgumentException("Code or symbol cannot be null");
        }

        if (symbol.toString().length() <= 0 || code.toString().length() <= 0) {
            throw new java.lang.IllegalArgumentException("Code or symbol length cannot be 0");
        }

        if (false == this.getCodes(Stock.Industry.UserDefined).contains(code) || false == this.getSymbols(Stock.Industry.UserDefined).contains(symbol)) {
            return false;
        }
        
        final Code _code = symbolToCode.get(symbol);
        
        // We should ensure the symbol which we intend to remove is inside the
        // database. In our case, the symbols which we wish to remove are coming from
        // MutableStockCodeAndSymbolDatabase's getUserDefinedSymbol. Although
        // this ensures code will never be null. Still, without null checking,
        // our code may break, if getUserDefinedSymbol is returning duplicated List
        // which is containing duplicated symbols.
        // Hence, it is better to leave null checking here.
        if (_code == null) {
            return false;
        }

        // Addtional check.
        if (_code.equals(code) == false) {
            return false;
        }

        final Symbol _symbol = codeToSymbol.get(code);

        if (_symbol == null) {
            return false;
        }

        // Addtional check.
        if (_symbol.equals(symbol) == false) {
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
        
        if ((!(symbolSearchEngine instanceof TSTSearchEngine)) || (!(codeSearchEngine instanceof TSTSearchEngine)))
        {
            return false;
        }
        
        symbols.remove(symbol);
        codes.remove(code);
        
        ((TSTSearchEngine<Symbol>)symbolSearchEngine).remove(symbol);
        ((TSTSearchEngine<Code>)codeSearchEngine).remove(code);
        
        symbolToCode.remove(symbol);
        codeToSymbol.remove(code);

        iCodes.remove(code);
        if (iCodes.size() <= 0) {
            industryToCodes.remove(industry);
        }

        bCodes.remove(code);
        if (bCodes.size() <= 0) {
            boardToCodes.remove(board);
        }

        iSymbols.remove(symbol);
        if (iSymbols.size() <= 0) {
            industryToSymbols.remove(industry);
        }

        bSymbols.remove(symbol);
        if (bSymbols.size() <= 0) {
            boardToSymbols.remove(board);
        }
        
        return true;        
    }
    
    // Not thread safe.
    public boolean addUserDefinedCodeAndSymbol(Code code, Symbol symbol) {
        if (symbol == null || code == null) {
            throw new java.lang.IllegalArgumentException("Code or symbol cannot be null");
        }
        
        if (symbol.toString().length() <= 0 || code.toString().length() <= 0) {
            throw new java.lang.IllegalArgumentException("Code or symbol length cannot be 0");
        }

        // We need to ensure there is no duplicated code or symbol being added.
        if (symbolToCode.containsKey(symbol) || codeToSymbol.containsKey(code)) {
            return false;
        }

        if((!(symbolSearchEngine instanceof TSTSearchEngine)) || (!(codeSearchEngine instanceof TSTSearchEngine))) {
            return false;
        }

        this.symbols.add(symbol);
        this.codes.add(code);

        ((TSTSearchEngine<Symbol>)symbolSearchEngine).put(symbol);
        ((TSTSearchEngine<Code>)codeSearchEngine).put(code);
        
        symbolToCode.put(symbol, code);
        codeToSymbol.put(code, symbol);
        
        final Stock.Industry industry = Stock.Industry.UserDefined;
        final Stock.Board board = Stock.Board.UserDefined;
        
        List<Code> _codes = industryToCodes.get(industry);
        if (_codes == null) {
            _codes = new ArrayList<Code>();
            industryToCodes.put(industry, _codes);
        }
        _codes.add(code);

        _codes = boardToCodes.get(board);
        if (_codes == null) {
            _codes = new ArrayList<Code>();
            boardToCodes.put(board, _codes);
        }
        _codes.add(code);

        List<Symbol> _symbols = industryToSymbols.get(industry);
        if (_symbols == null) {
            _symbols = new ArrayList<Symbol>();
            industryToSymbols.put(industry, _symbols);
        }
        _symbols.add(symbol);

        _symbols = boardToSymbols.get(board);
        if (_symbols == null) {
            _symbols = new ArrayList<Symbol>();
            boardToSymbols.put(board, _symbols);
        }
        _symbols.add(symbol);
        
        return true;
    }    
}
