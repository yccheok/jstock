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

package org.yccheok.jstock.engine.currency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yccheok
 */
public enum ExchangeRateLookup {
    INSTANCE;
    
    public ExchangeRate put(ExchangeRate exchageRate) {
        // Avoid out of memory.
        if (map.size() >= MAX_SIZE) {
            map.clear();
        }
        return map.put(exchageRate.currencyPair(), exchageRate);
    }
    
    public ExchangeRate get(CurrencyPair currencyPair) {
        return map.get(currencyPair);
    }
    
    private final int MAX_SIZE = 512;
    private final Map<CurrencyPair, ExchangeRate> map = new ConcurrentHashMap<>();
}
