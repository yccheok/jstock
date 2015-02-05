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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.engine.currency.CurrencyPair;

/**
 *
 * @author yccheok
 */
public class PortfolioRealTimeInfo {    
    // Avoid using interface class, so that our gson serialization & 
    // deserialization can work correctly.
    
    public final ConcurrentHashMap<Code, Double> stockPrices = new ConcurrentHashMap<>();
    
    public final ConcurrentHashMap<CurrencyPair, Double> exchangeRates = new ConcurrentHashMap<>();
    
    public final ConcurrentHashMap<Code, Currency> currencies = new ConcurrentHashMap<>();
    
    public long stockPricesTimestamp = 0;
    public long exchangeRatesTimestamp = 0;
    
    public transient volatile boolean stockPricesDirty = false;
    public transient volatile boolean exchangeRatesDirty = false;
    public transient volatile boolean currenciesDirty = false;
    
    private static final Log log = LogFactory.getLog(PortfolioRealTimeInfo.class);
    
    public PortfolioRealTimeInfo() {
    }
    
    public PortfolioRealTimeInfo(PortfolioRealTimeInfo portfolioRealTimeInfo) {
        copy(portfolioRealTimeInfo);
    }
    
    private void copy(PortfolioRealTimeInfo portfolioRealTimeInfo) {
        stockPrices.clear();
        exchangeRates.clear();
        currencies.clear();
        
        stockPrices.putAll(portfolioRealTimeInfo.stockPrices);
        exchangeRates.putAll(portfolioRealTimeInfo.exchangeRates);
        currencies.putAll(portfolioRealTimeInfo.currencies);
        
        stockPricesTimestamp = portfolioRealTimeInfo.stockPricesTimestamp;
        exchangeRatesTimestamp = portfolioRealTimeInfo.exchangeRatesTimestamp;

        // I don't know what to do with dirty flags at this moment.
    }
    
    public boolean load(File file) {
        assert(file != null);

        if (false == file.isFile()) {
            return false;
        }
        
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();        
        
        PortfolioRealTimeInfo portfolioRealTimeInfo = null;
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));            
            try {
                portfolioRealTimeInfo = gson.fromJson(reader, PortfolioRealTimeInfo.class);
            } finally {
                reader.close();
            }
        } catch (IOException ex){
            log.error(null, ex);
        } catch (com.google.gson.JsonSyntaxException ex) {
            log.error(null, ex);
        } 
        
        if (portfolioRealTimeInfo == null) {
            return false;
        }
        
        copy(portfolioRealTimeInfo);
        
        return true;
    }
    
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
