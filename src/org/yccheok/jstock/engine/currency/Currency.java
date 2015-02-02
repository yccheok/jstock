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
public class Currency {
    private Currency(String currency) {
        this.currency = currency;
    }
    
    public static Currency newInstance(String currency) {
        if (currency.equals(GBX)) {
            // Pence sterling is a subdivision of Pound sterling, the currency 
            // for the United Kingdom. Stocks are often traded in pence rather 
            // than pounds and stock exchages often use GBX to indicate that 
            // this is the case for the give stock rather than use the ISO 4217 
            // currency symbol GBP for pounds sterling.
            return new Currency(currency);
        }
        
        // Possible throw java.lang.IllegalArgumentException
        java.util.Currency.getInstance(currency);
        
        return new Currency(currency);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + currency.hashCode();
        
        return result;
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
    
    private static final String GBX = "GBX";
    
    private final String currency;
}