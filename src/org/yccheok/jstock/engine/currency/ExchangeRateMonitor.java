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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                List<ExchangeRate> exchangeRates = new ArrayList<>();
                Set<CurrencyPair> gbxCurrencyPairs = getGBXCurrencyPairs();
                
                for (Stock stock : stocks) {
                    final double lastPrice = stock.getLastPrice();
                    if (lastPrice > 0.0) {
                        CurrencyPair currencyPair = currencyPairMapping.get(stock.code);
                        if (currencyPair != null) {
                            ExchangeRate exchangeRate = new ExchangeRate(currencyPair, lastPrice);    
                            exchangeRates.add(exchangeRate);
                        }
                    }
                    
                    if (lastPrice > 0.0) {
                        for (CurrencyPair gbxCurrencyPair : gbxCurrencyPairs) {
                            final CurrencyPair realTimeMonitorCurrencyPair = toCurrencyPairForRealTimeStockMonitor(gbxCurrencyPair);
                            final Code realTimeMonitorCode = toCode(realTimeMonitorCurrencyPair);
                            if (stock.code.equals(realTimeMonitorCode)) {
                                final String from = gbxCurrencyPair.from().toString();
                                final String to = gbxCurrencyPair.to().toString();
                                double revisedLastPrice = lastPrice;
                                if (from.equals(GBX) && !to.equals(GBX)) {
                                    revisedLastPrice = lastPrice / 100.0;
                                } else if (!from.equals(GBX) && to.equals(GBX)) {
                                    revisedLastPrice = lastPrice * 100.0;
                                } else if (from.equals(GBX) && to.equals(GBX)) {
                                    revisedLastPrice = 1.0;
                                }
                                
                                ExchangeRate exchangeRate = new ExchangeRate(gbxCurrencyPair, revisedLastPrice);
                                exchangeRates.add(exchangeRate);
                            }
                        }
                    }
                }
                
                if (false == exchangeRates.isEmpty()) {
                    ExchangeRateMonitor.this.notify(ExchangeRateMonitor.this, exchangeRates);
                }
            }
            
        };
    }
    
    private Set<CurrencyPair> getGBXCurrencyPairs() {
        Set<CurrencyPair> currencyPairs = new HashSet<>();
        
        for (Map.Entry<Code, CurrencyPair> entry : currencyPairMapping.entrySet())
        {
            CurrencyPair currencyPair = entry.getValue();
            String from = currencyPair.from().toString();
            String to = currencyPair.to().toString();
            if (from.equals(GBX) || to.equals(GBX)) {
                currencyPairs.add(currencyPair);
            }
        }
        
        return currencyPairs;
    }
    
    private CurrencyPair toCurrencyPairForRealTimeStockMonitor(CurrencyPair currencyPair) {
        String from = currencyPair.from().toString();
        String to = currencyPair.to().toString();
        
        if (from.equals(GBX)) {
            from = "GBP";
        }
        
        if (to.equals(GBX)) {
            to = "GBP";
        }
        
        if (from.equals(currencyPair.from().toString()) && to.equals(currencyPair.to().toString())) {
            return currencyPair;
        }
        
        return CurrencyPair.create(from, to);
    }
    
    private Code toCode(CurrencyPair currencyPair) {
        final Currency from = currencyPair.from();
        final Currency to = currencyPair.to();
        final String fromCurrencyCode = from.toString();
        final String toCurrencyCode = to.toString();
        // Format used by Yahoo! Finance.
        return Code.newInstance(fromCurrencyCode + toCurrencyCode + "=X");
    }
    
    public synchronized boolean addCurrencyPair(CurrencyPair currencyPair) {
        final Code code = toCode(currencyPair);
                
        if (currencyPairMapping.containsKey(code)) {
            return false;
        }
        
        currencyPairMapping.put(code, currencyPair);
        
        final CurrencyPair realTimeMonitorCurrencyPair = toCurrencyPairForRealTimeStockMonitor(currencyPair);
        final Code realTimeMonitorCode = toCode(realTimeMonitorCurrencyPair);
        realTimeStockMonitor.addStockCode(realTimeMonitorCode);
        
        return true;
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
        if (null == currencyPairMapping.remove(code)) {
            return false;
        }

        final CurrencyPair realTimeMonitorCurrencyPair = toCurrencyPairForRealTimeStockMonitor(currencyPair);
        final Code realTimeMonitorCode = toCode(realTimeMonitorCurrencyPair);
        // Can we remove?
        if (currencyPairMapping.containsKey(realTimeMonitorCode)) {
            // Cannot remove first. Someone is requesting real GBP.
            return true;
        }
        
        return realTimeStockMonitor.removeStockCode(realTimeMonitorCode);
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
    
    private static final String GBX = "GBX";
    
    private final Map<Code, CurrencyPair> currencyPairMapping = new ConcurrentHashMap<>();
    private final RealTimeStockMonitor realTimeStockMonitor;
}
