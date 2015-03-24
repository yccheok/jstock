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

package org.yccheok.jstock.gui.treetable;

import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.portfolio.DecimalPlace;
import org.yccheok.jstock.portfolio.DoubleWrapper;

/**
 *
 * @author yccheok
 */
public class DoubleWithCurrency extends org.yccheok.jstock.engine.Pair<Currency, DoubleWrapper> implements Comparable<DoubleWithCurrency> {
    public static DoubleWithCurrency create(Currency currency, DecimalPlace decimalPlace, double _double) {
        return new DoubleWithCurrency(currency, new DoubleWrapper(decimalPlace, _double));
    }
    
    public static DoubleWithCurrency create(Currency currency, DoubleWrapper doubleWrapper) {
        return new DoubleWithCurrency(currency, doubleWrapper);
    }
    
    public DoubleWithCurrency(Currency currency, DoubleWrapper doubleWrapper) {
        super(currency, doubleWrapper);
    }
    
    public Double Double() {
        return this.second.value;
    }
    
    public DecimalPlace decimalPlace() {
        return this.second.decimalPlace;
    }
    
    public Currency currency() {
        return this.first;
    }

    @Override
    public int compareTo(DoubleWithCurrency o) {
        return second.value.compareTo(o.second.value);
    }
}
