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

/**
 *
 * @author yccheok
 */
public class ExchangeRate extends org.yccheok.jstock.engine.Pair<CurrencyPair, Double> {

    public ExchangeRate(CurrencyPair currencyPair, double rate) {
        super(currencyPair, rate);
    }
    
    public double rate() {
        return this.second;
    }
    
    public CurrencyPair currencyPair() {
        return this.first;
    }
}
