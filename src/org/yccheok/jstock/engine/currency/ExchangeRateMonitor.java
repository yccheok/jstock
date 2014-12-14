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

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Observer;
import org.yccheok.jstock.engine.RealTimeStockMonitor;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.Subject;

/**
 *
 * @author yccheok
 */
public class ExchangeRateMonitor extends Subject<ExchangeRateMonitor, List<ExchangeRate>> {

    /** Creates a new instance of ExchangeRateMonitor */
    public ExchangeRateMonitor(int maxThread, int maxBucketSize, long delay) {
        realTimeStockMonitor = new RealTimeStockMonitor(maxThread, maxBucketSize, delay);
        
        realTimeStockMonitor.attach(getRealTimeStockMonitorObserver());
    }
    
    private Observer<RealTimeStockMonitor, List<Stock>> getRealTimeStockMonitorObserver() {
        return new Observer<RealTimeStockMonitor, List<Stock>>() {
            
            @Override
            public void update(RealTimeStockMonitor subject, List<Stock> stocks) {
                List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
                for (Stock stock : stocks) {
                    CurrencyPair currencyPair = currencyPairMapping.get(stock.code);
                    if (currencyPair != null) {
                        double lastPrice = stock.getLastPrice();
                        if (lastPrice > 0.0) {
                            ExchangeRate exchangeRate = new ExchangeRate(currencyPair, lastPrice);    
                            exchangeRates.add(exchangeRate);
                        }
                    }
                }
                
                if (false == exchangeRates.isEmpty()) {
                    ExchangeRateMonitor.this.notify(ExchangeRateMonitor.this, exchangeRates);
                }
            }
            
        };
    }
    
    private Code toCode(CurrencyPair currencyPair) {
        final Currency from = currencyPair.from();
        final Currency to = currencyPair.to();
        final String fromCurrencyCode = from.getCurrencyCode();
        final String toCurrencyCode = to.getCurrencyCode();
        // Format used by Yahoo! Finance.
        return Code.newInstance(fromCurrencyCode + toCurrencyCode + "=X");
    }
    
    public synchronized boolean addCurrencyPair(CurrencyPair currencyPair) {
        final Code code = toCode(currencyPair);
        
        if (realTimeStockMonitor.addStockCode(code)) {
            currencyPairMapping.put(code, currencyPair);
            return true;
        }
        return false;
    }
    
    public synchronized boolean isEmpty() {
        return realTimeStockMonitor.isEmpty();
    }
    
    public synchronized boolean clearCurrencyPairs() {
        currencyPairMapping.clear();        
        return realTimeStockMonitor.clearStockCodes();
    }
    
    public synchronized boolean removeCurrencyPair(CurrencyPair currencyPair) {
        final Code code = toCode(currencyPair);
        currencyPairMapping.remove(code);
        return realTimeStockMonitor.removeStockCode(code);
    } 
    
    public synchronized void resume() {
        realTimeStockMonitor.resume();
    }

    public synchronized void suspend() {
        realTimeStockMonitor.suspend();
    } 
    
    public synchronized void startNewThreadsIfNecessary() {
        realTimeStockMonitor.startNewThreadsIfNecessary();
    }
    
    public synchronized void rebuild() {
        realTimeStockMonitor.rebuild();
    }
    
    public synchronized void stop() {
        realTimeStockMonitor.stop();
    }
    
    public synchronized void refresh() {
        realTimeStockMonitor.refresh();
    }
    
    public synchronized long getDelay() {
        return realTimeStockMonitor.getDelay();
    }
    
    public synchronized void setDelay(int delay) {
        realTimeStockMonitor.setDelay(delay);
    }
    
    public int getTotalScanned() {
        return realTimeStockMonitor.getTotalScanned();
    }
    
    private final Map<Code, CurrencyPair> currencyPairMapping = new ConcurrentHashMap<Code, CurrencyPair>();
    private final RealTimeStockMonitor realTimeStockMonitor;
}
