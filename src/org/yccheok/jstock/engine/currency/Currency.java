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

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yccheok
 */
public class Currency implements Comparable<Currency> {
    private Currency(String currency) {
        assert(currency != null);

        this.currency = currency;
    }

    public static Currency valueOfWithVerification(String currency) {
        if (currency.equals(GBX) || currency.equals(ZAC) || currency.equals(ILA)) {
            // Pence sterling is a subdivision of Pound sterling, the currency
            // for the United Kingdom. Stocks are often traded in pence rather
            // than pounds and stock exchages often use GBX to indicate that
            // this is the case for the give stock rather than use the ISO 4217
            // currency symbol GBP for pounds sterling.
            return valueOf(currency);
        }

        // Possible throw java.lang.IllegalArgumentException
        java.util.Currency c = java.util.Currency.getInstance(currency);
        assert(c != null);

        return valueOf(currency);
    }

    public static Currency valueOf(String currency) {
        if (currency == null) {
            throw new java.lang.IllegalArgumentException("currency cannot be null");
        }

        currency = currency.trim();

        if (currency.isEmpty()) {
            throw new java.lang.IllegalArgumentException("currency cannot be empty");
        }

        Currency result = map.get(currency);
        if (result == null) {
            final Currency instance = new Currency(currency);
            result = map.putIfAbsent(currency, instance);
            if (result == null) {
                result = instance;
            }
        }

        assert(result != null);
        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + currency.hashCode();

        return result;
    }

    @Override
    public int compareTo(Currency o) {
        return this.currency.compareTo(o.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Currency)) {
            return false;
        }

        return this.currency.equals(((Currency)o).currency);
    }

    @Override
    public String toString() {
        return currency;
    }

    public String name() {
        return currency;
    }

    public boolean isGBX() {
        return currency.equals(GBX);
    }

    public boolean isZAC() {
        return currency.equals(ZAC);
    }

    public boolean isILA() {
        return currency.equals(ILA);
    }

    public static final String GBX = "GBX";
    public static final String ZAC = "ZAC";
    public static final String ILA = "ILA";

    public static final String GBP = "GBP";
    public static final String ZAR = "ZAR";
    public static final String ILS = "ILS";

    private final String currency;

    // Avoid using interface. We want it to be fast!
    private static final ConcurrentHashMap<String, Currency> map = new ConcurrentHashMap<>();
}