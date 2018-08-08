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

import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Observer;
import org.yccheok.jstock.engine.RealTimeStockMonitor;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.Subject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    
    private Observer<RealTimeStockMonitor, RealTimeStockMonitor.Result> getRealTimeStockMonitorObserver() {
        return new Observer<RealTimeStockMonitor, RealTimeStockMonitor.Result>() {
            @Override
            public void update(RealTimeStockMonitor subject, RealTimeStockMonitor.Result result) {
                List<ExchangeRate> exchangeRates = new ArrayList<>();
                Set<CurrencyPair> centOrPenceCurrencyPairs = getCentOrPenceCurrencyPairs();

                for (Stock stock : result.stocks) {
                    final double lastPrice = stock.getLastPrice();
                    if (lastPrice > 0.0) {
                        CurrencyPair currencyPair = currencyPairMapping.get(stock.code);
                        if (currencyPair != null) {
                            ExchangeRate exchangeRate = new ExchangeRate(currencyPair, lastPrice);
                            exchangeRates.add(exchangeRate);
                        }
                    }

                    if (lastPrice > 0.0) {
                        for (CurrencyPair centOrPenceCurrencyPair : centOrPenceCurrencyPairs) {
                            final CurrencyPair realTimeMonitorCurrencyPair = toCurrencyPairForRealTimeStockMonitor(centOrPenceCurrencyPair);
                            final Code realTimeMonitorCode = toCode(realTimeMonitorCurrencyPair);
                            if (stock.code.equals(realTimeMonitorCode)) {
                                double revisedLastPrice = lastPrice;
                                boolean toIsCentOrPence = centOrPenceCurrencyPair.to().isGBX() || centOrPenceCurrencyPair.to().isZAC() || centOrPenceCurrencyPair.to().isILA();
                                boolean fromIsCentOrPence = centOrPenceCurrencyPair.from().isGBX() || centOrPenceCurrencyPair.from().isZAC() || centOrPenceCurrencyPair.from().isILA();

                                if (fromIsCentOrPence && !toIsCentOrPence) {
                                    revisedLastPrice = lastPrice / 100.0;
                                } else if (!fromIsCentOrPence && toIsCentOrPence) {
                                    revisedLastPrice = lastPrice * 100.0;
                                } else if (fromIsCentOrPence && toIsCentOrPence) {
                                    revisedLastPrice = lastPrice;
                                }

                                ExchangeRate exchangeRate = new ExchangeRate(centOrPenceCurrencyPair, revisedLastPrice);
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
    
    private Set<CurrencyPair> getCentOrPenceCurrencyPairs() {
        Set<CurrencyPair> currencyPairs = new HashSet<>();
        
        for (Map.Entry<Code, CurrencyPair> entry : currencyPairMapping.entrySet())
        {
            CurrencyPair currencyPair = entry.getValue();
            if (currencyPair.from().isGBX() || currencyPair.from().isZAC()|| currencyPair.from().isILA() || currencyPair.to().isGBX() || currencyPair.to().isZAC() || currencyPair.to().isILA()) {
                currencyPairs.add(currencyPair);
            }
        }
        
        return currencyPairs;
    }
    
    private CurrencyPair toCurrencyPairForRealTimeStockMonitor(CurrencyPair currencyPair) {
        String from = currencyPair.from().name();
        String to = currencyPair.to().name();
        
        if (currencyPair.from().isGBX()) {
            from = Currency.GBP;
        } else if (currencyPair.from().isZAC()) {
            from = Currency.ZAR;
        } else if (currencyPair.from().isILA()) {
            from = Currency.ILS;
        }
        
        if (currencyPair.to().isGBX()) {
            to = Currency.GBP;
        } else if (currencyPair.to().isZAC()) {
            to = Currency.ZAR;
        } else if (currencyPair.to().isILA()) {
            to = Currency.ILS;
        }
        
        if (from.equals(currencyPair.from().name()) && to.equals(currencyPair.to().name())) {
            return currencyPair;
        }
        
        return CurrencyPair.create(from, to);
    }
    
    private Code toCode(CurrencyPair currencyPair) {
        final Currency from = currencyPair.from();
        final Currency to = currencyPair.to();
        final String fromCurrencyCode = from.name();
        final String toCurrencyCode = to.name();
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
    
    public synchronized void setDelay(int delay) {
        realTimeStockMonitor.setDelay(delay);
    }
    
    private final Map<Code, CurrencyPair> currencyPairMapping = new ConcurrentHashMap<>();
    private final RealTimeStockMonitor realTimeStockMonitor;
}
