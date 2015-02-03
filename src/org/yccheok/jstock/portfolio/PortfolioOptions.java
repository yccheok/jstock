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

package org.yccheok.jstock.portfolio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.engine.currency.CurrencyPair;

/**
 *
 * @author yccheok
 */
public class PortfolioOptions {
    
    // Avoid using interface class, so that our gson serialization & 
    // deserialization can work correctly.
    public final HashMap<Code, Double> stockPrices = new HashMap<>();
    
    // Avoid using ExchangeRateLookup. As once we perform serialization on
    // ExchangeRateLookup, it is very difficult to evolve its design. We better
    // stick with simple data structure.
    public final HashMap<CurrencyPair, Double> exchangeRates = new HashMap<>();
    
    public final HashMap<Code, Currency> currencies = new HashMap<>();
    
    public long stockPricesTimeStamp = 0;
    public long exchangeRatesTimeStamp = 0;
    
    public volatile boolean stockPricesDirty = false;
    public volatile boolean exchangeRatesDirty = false;
    public volatile boolean currenciesDirty = false;
    
    private static final Log log = LogFactory.getLog(PortfolioOptions.class);
    
    public boolean save(File file) {
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        Gson gson = builder.create(); 
        String string = gson.toJson(this);
        
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            try {
                writer.write(string);
            } finally {
                writer.close();
            }
        } catch (IOException ex){
            log.error(null, ex);
            return false;
        }
        
        return true;
    }
}
