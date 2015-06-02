/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.util.List;
import org.yccheok.jstock.gui.JStock;

/**
 *
 * @author yccheok
 */
public class AjaxStockInfoSearchEngine implements SearchEngine<StockInfo> {    

    @Override
    public List<StockInfo> searchAll(String prefix) {
        Code code = Code.newInstance(prefix);
        Country country = Utils.toCountry(code);
        boolean searchRequired = false;
        
        if (isSupported(country)) {
            searchRequired = true;
        } else {
            /*******************************************************************
             * Hack & ugly code! We shouldn't access JStock in this class!
             ******************************************************************/
            country = JStock.instance().getJStockOptions().getCountry();
            if (isSupported(country)) {
                code = toDeepSearchCode(code, country);
                searchRequired = (code != null);    
            }
        }

        if (searchRequired) {
            assert(code != null);
            Stock stock = Utils.getStock(code);
            if (stock != null) {
                StockInfo stockInfo = StockInfo.newInstance(stock.code, stock.symbol);
                List<StockInfo> stockInfos = new ArrayList<>();
                stockInfos.add(stockInfo);
                return stockInfos;
            }
        }
        
        return java.util.Collections.emptyList();
    }

    // Only support Malaysia so far. This will burden our app performance.
    // Enhance this function with care.
    private Code toDeepSearchCode(Code code, Country country) {
        String s = code.toString().toUpperCase();
        
        switch (country) {
        case Malaysia:
            if (s.endsWith(".KL")) { 
                return code;
            } else if (s.endsWith(".K")) {
                s = s + "L";                    
            } else if (s.endsWith(".")) {
                s = s + "KL";
            } else if (s.matches("^[0-9]{4}[0-9A-Z]{2}$")) {
                s = s + ".KL";
            } else {
                return null;
            }
            return Code.newInstance(s);                
        }
        return null;
    }

    @Override
    public StockInfo search(String prefix) {
        final List<StockInfo> list = searchAll(prefix);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
    
    private boolean isSupported(Country country) {
        // Since UnitedState doesn't have stock code suffix, we do not want to
        // trigger search action, if user doesn't type "... .XX"
        return !(country == Country.UnitedState);
    }
}
