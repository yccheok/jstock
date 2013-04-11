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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to provide stock code to stock name mapping, as some
 * stock server doesn't return good stock name. For example, China users wish
 * to view stock name in Chinese. However, Yahoo! Stock Server returns stock
 * name in English. In this case, we need to retrieve stock name from
 * <code>StockNameDatabase</code>.
 */
public class StockNameDatabase {
    /**
     * Initializes a newly created {@code StockNameDatabase} object so
     * that it contains stock code to stock name mapping information. The
     * information is being retrieved from list of stocks.
     *
     * @param stocks List of stocks which provides stock information
     */
    public StockNameDatabase(List<Stock> stocks) {
        for (Stock stock : stocks) {
            codeToName.put(stock.code, stock.getName());
        }
    }

    public Map<Code, String> getCodeToName() {
        return java.util.Collections.unmodifiableMap(codeToName);
    }
    
    /**
     * Returns name based on the code.
     * 
     * @param code The code
     * @return name based on the code. <code>null</code> will be returned if
     * no match found
     */
    public String codeToName(Code code) {
        return codeToName.get(code);
    }

    private final Map<Code, String> codeToName = new HashMap<Code, String>();
}
