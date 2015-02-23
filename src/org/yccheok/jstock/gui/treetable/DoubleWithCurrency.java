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

/**
 *
 * @author yccheok
 */
public class DoubleWithCurrency extends org.yccheok.jstock.engine.Pair<Double, Currency> implements Comparable<DoubleWithCurrency> {
    public static DoubleWithCurrency create(Double _double, Currency currency) {
        return new DoubleWithCurrency(_double, currency);
    }
    
    public DoubleWithCurrency(Double _double, Currency currency) {
        super(_double, currency);
    }
    
    public Double Double() {
        return this.first;
    }
    
    public Currency currency() {
        return this.second;
    }

    @Override
    public int compareTo(DoubleWithCurrency o) {
        return first.compareTo(o.first);
    }
}
