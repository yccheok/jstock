/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.engine;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.network.Utils.Type;

/**
 *
 * @author yccheok
 */
public class CurrencyExchangeMonitor extends Subject<CurrencyExchangeMonitor, Double> {

    /**
     * Constructs an instance of CurrencyExchangeMonitor.
     * 
     * @param fromCountry convert from this country's currency
     * @param toCountry convert to this country's currency
     */
    public CurrencyExchangeMonitor(Country fromCountry, Country toCountry) {
        this.fromCountry = fromCountry;
        this.toCountry = toCountry;
    }

    /**
     * Starts currency exchange monitoring activities. This method can be called
     * multiple times. start/stop/suspend/resume are being synchronized with
     * each others.
     */
    public synchronized void start() {
        if (has_started) {
            return;
        }
        currencyExchangeThread.start();
        has_started = true;
    }

    public synchronized void refresh()
    {
        synchronized(currencyExchangeThread) {
            if (suspend) {
                // If this monitor is suspended, refresh will not work, unless
                // resume is being called first.
                return;
            }
            isRefresh = true;
            this.currencyExchangeThread.interrupt();
        }
    }
    
    /**
     * Stop all currency exchange monitoring activities. Once it is stopped, it
     * cannot be started again. This method will only return once it is being
     * terminated completely. start/stop/suspend/resume are being synchronized
     * with each others.
     */
    public synchronized void stop() {
        this.currencyExchangeThread._stop();
        try {
            this.currencyExchangeThread.join();
        } catch (InterruptedException ex) {
            log.error(null, ex);
        }
    }

    /**
     * Temporary suspend all ongoing monitoring activities. This method can be
     * called multiple times. start/stop/suspend/resume are being synchronized
     * with each others.
     */
    public synchronized void suspend() {
        synchronized(currencyExchangeThread) {
            suspend = true;
        }
    }

    /**
     * Resume all ongoing monitoring activities. This method can be called
     * multiple times. start/stop/suspend/resume are being synchronized with
     * each others.
     */
    public synchronized void resume() {
        synchronized(currencyExchangeThread) {
            suspend = false;
            currencyExchangeThread.notify();
        }
    }

    /**
     * @return the fromCountry's currency
     */
    public String getFromCurrency() {
        return countryToCurrencyCode.get(fromCountry);
    }

    /**
     * @return the toCountry's currency
     */
    public String getToCurrency() {
        return countryToCurrencyCode.get(toCountry);
    }
    
    /**
     * @return the fromCountry
     */
    public Country getFromCountry() {
        return fromCountry;
    }

    /**
     * @return the toCountry
     */
    public Country getToCountry() {
        return toCountry;
    }

    // CurrencyExchangeRunnable is doing 2 things:
    //
    // (1) Keep trying to fill in countryToCurrencyCode with latest
    // country -> currency information, until it successes at least once.
    // (2) Fetch the latest currency exchange information, according to given
    // fromCountry and toCountry.
    private class CurrencyExchangeThread extends Thread {

        private volatile boolean isRunnable = true;
        
        public void _stop() {
            isRunnable = false;
            // Wake up from sleep.
            interrupt();
        }

        @Override
        public void run() {
            while (isRunnable) {
                // Execute the while loop within try block to ensure fail safe.
                try {
                    while (isRunnable) {
                        synchronized(this) {
                            while (suspend) {
                                try {
                                    wait();
                                } catch (InterruptedException ex) {
                                    log.error(null, ex);
                                    if (isRefresh == false) {
                                        isRunnable = false; 
                                        break;
                                    }
                                }
                            }
                        }

                        // Let's do the job.

                        // Use fromCountry?
                        for (StockServerFactory factory : Factories.INSTANCE.getStockServerFactories(fromCountry)) {
                            final StockServer stockServer = factory.getStockServer();
                            
                            if (stockServer == null) {
                                continue;
                            }
                            
                            Stock stock = null;
                            try {
                                stock = stockServer.getStock(getCode());
                            } catch (StockNotFoundException ex) {
                                log.error(null, ex);
                                // Try with another server.
                                continue;
                            }

                            if (stock.getLastPrice() > 0.0) {
                                // Ensure we are always having a valid exchange rate.
                                exchangeRate = stock.getLastPrice();
                                // Inform listeners.
                                CurrencyExchangeMonitor.this.notify(CurrencyExchangeMonitor.this, exchangeRate);
                                // Done.
                                break;
                            }
                        }   // for

                        try {
                            Thread.sleep(org.yccheok.jstock.gui.MainFrame.getInstance().getJStockOptions().getScanningSpeed());
                        } catch (InterruptedException ex) {
                            log.error(null, ex);
                            if (isRefresh == false) {
                                /* Exit the primary fail safe loop. */
                                isRunnable = false;                            
                                break;
                            } else {
                                // User has requested to perform refresh explicitly.
                                // Clear isRefresh flag, and continue with rest
                                // of the work.
                                isRefresh = false;
                            }
                        }
                        
                    }   // while (!executor.isShutdown())
                } catch (Exception exp) {
                    // Our thread just recover from unexpected error.
                    log.error(null, exp);
                }
            }   //  while (!executor.isShutdown())
        }
    }

    /**
     * Returns currency code of this monitor.
     *
     * @return currency code of this monitor
     */
    public Code getCode() {
        final String fromCurrencyCode = countryToCurrencyCode.get(this.getFromCountry());
        final String toCurrencyCode = countryToCurrencyCode.get(this.getToCountry());
        // Format used by Yahoo! Finance.
        return Code.newInstance(fromCurrencyCode + toCurrencyCode + "=X");
    }

    /**
     * Returns exchange rate of this monitor.
     *
     * @return exchange rate of this monitor
     */
    public double getExchangeRate() {
        return exchangeRate;
    }

    // This map, is used to convert a country to its latest assiciated currency.
    // This map will at most be updated by currencyExchangeRunnable once, from
    // CURRENCY_CODE_TXT's server. Even we are unable to obtain information
    // from CURRENCY_CODE_TXT's server, this map itself still able to provide
    // default information.
    private static final Map<Country, String> countryToCurrencyCode =  new EnumMap<Country, String>(Country.class);

    static {
        countryToCurrencyCode.put(Country.Australia, "AUD");
        countryToCurrencyCode.put(Country.Austria, "EUR");
        countryToCurrencyCode.put(Country.Belgium, "EUR");
        countryToCurrencyCode.put(Country.Brazil, "BRL");
        countryToCurrencyCode.put(Country.Canada, "CAD");
        countryToCurrencyCode.put(Country.China, "CNY");
        countryToCurrencyCode.put(Country.Czech, "CZK");
        countryToCurrencyCode.put(Country.Denmark, "DKK");
        countryToCurrencyCode.put(Country.France, "EUR");
        countryToCurrencyCode.put(Country.Germany, "EUR");
        countryToCurrencyCode.put(Country.HongKong, "HKD");
        countryToCurrencyCode.put(Country.India, "INR");
        countryToCurrencyCode.put(Country.Indonesia, "IDR");
        countryToCurrencyCode.put(Country.Israel, "ILS");
        countryToCurrencyCode.put(Country.Italy, "EUR");
        countryToCurrencyCode.put(Country.Korea, "KPW");
        countryToCurrencyCode.put(Country.Malaysia, "MYR");
        countryToCurrencyCode.put(Country.Netherlands, "EUR");
        countryToCurrencyCode.put(Country.NewZealand, "NZD");
        countryToCurrencyCode.put(Country.Norway, "NOK");
        countryToCurrencyCode.put(Country.Portugal, "EUR");
        countryToCurrencyCode.put(Country.Singapore, "SGD");
        countryToCurrencyCode.put(Country.Spain, "EUR");
        countryToCurrencyCode.put(Country.Sweden, "SEK");
        countryToCurrencyCode.put(Country.Switzerland, "CHF");
        countryToCurrencyCode.put(Country.Taiwan, "TWD");
        countryToCurrencyCode.put(Country.UnitedKingdom, "GBP");
        countryToCurrencyCode.put(Country.UnitedState, "USD");
    }

    /**
     * Convert from this country's currency.
     */
    private final Country fromCountry;

    /**
     * Convert to this country's currency.
     */
    private final Country toCountry;

    // Use volatile, to make assignment operation on double atomic.
    private volatile double exchangeRate = 1.0;

    // Doesn't require volatile, as this variable is being accessed within
    // synchronized block.
    private boolean suspend = false;
    private volatile boolean isRefresh = false;
    // Doesn't require volatile, as this variable is being accessed within
    // synchronized block.
    private boolean has_started = false;
    // Note that, we no longer using executor. As, to implement refresh, we need 
    // to perform interruption. It is easier to perform interruption on Thread, 
    // rather on Future returned from executor.
    private final CurrencyExchangeThread currencyExchangeThread = new CurrencyExchangeThread();
    private static final Log log = LogFactory.getLog(CurrencyExchangeMonitor.class);
}
