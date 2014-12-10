/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.alert;

import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;

/**
 *
 * @author yccheok
 */
public enum SMSLimiter {
    INSTANCE;
    private long epoch = System.currentTimeMillis()/1000;
    private java.util.concurrent.atomic.AtomicInteger atomic = new java.util.concurrent.atomic.AtomicInteger();

    public boolean isSMSAllowed() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        final int max = jStockOptions.getMaxSMSPerDay();

        if (max <= 0) {
            // Ignore. User does not expect any number of SMS limit.
            return true;
        }

        // Is it still the same day?
        long current = System.currentTimeMillis()/1000;
        final long secondsInADay = 60 * 60 * 24;
        if (epoch / secondsInADay != current / secondsInADay) {
            // The next day.
            epoch = current;
            atomic.lazySet(0);
        }

        return atomic.get() < max;
    }

    public void inc() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        final int max = jStockOptions.getMaxSMSPerDay();

        if (max <= 0) {
            // Ignore. User does not expect any number of SMS limit.
            return;
        }
        atomic.incrementAndGet();
    }
}
