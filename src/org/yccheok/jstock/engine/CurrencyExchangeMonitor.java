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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class CurrencyExchangeMonitor {

    /**
     * Starts currency exchange monitoring activities. This method can be called
     * multiple times. start/stop/suspend/resume are being synchronized with
     * each others.
     */
    public synchronized void start() {
        if (has_started) {
            return;
        }
        executor.submit(currencyExchangeRunnable);
        has_started = true;
    }

    /**
     * Stop all currency exchange monitoring activities. Once it is stopped, it
     * cannot be started again. This method will only return once it is being
     * terminated completely. start/stop/suspend/resume are being synchronized
     * with each others.
     */
    public synchronized void stop() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(100, TimeUnit.DAYS);
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
        synchronized(currencyExchangeRunnable) {
            suspend = true;
        }
    }

    /**
     * Resume all ongoing monitoring activities. This method can be called
     * multiple times. start/stop/suspend/resume are being synchronized with
     * each others.
     */
    public synchronized void resume() {
        synchronized(currencyExchangeRunnable) {
            suspend = false;
            currencyExchangeRunnable.notify();
        }
    }

    private class CurrencyExchangeRunnable implements Runnable {
        @Override
        public void run() {
            while (!executor.isShutdown()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CurrencyExchangeMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }

                synchronized(this) {
                    while (suspend) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                            log.error(null, ex);
                            // Usually triggered by executor.shutdownNow
                            return;
                        }
                    }
                }
            }
        }
    }

    // Doesn't require volatile, as this variable is being accessed within
    // synchronized block.
    private boolean suspend = false;
    // Doesn't require volatile, as this variable is being accessed within
    // synchronized block.
    private boolean has_started = false;
    private final CurrencyExchangeRunnable currencyExchangeRunnable = new CurrencyExchangeRunnable();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Log log = LogFactory.getLog(CurrencyExchangeMonitor.class);
}
