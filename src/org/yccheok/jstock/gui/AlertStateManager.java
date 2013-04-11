/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import java.util.List;
import java.util.Set;
import org.yccheok.jstock.analysis.Indicator;
import org.yccheok.jstock.analysis.OperatorIndicator;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.Subject;

/**
 * For a stock which is under an indicator, every time we receive a stock tick,
 * there will be two states :-
 * (1) The stock hits the indicator.
 * (2) The stock doesn't hit the indicator.
 *
 * Most of the time, when a stock hits an indicator in n ticks, it will still hit
 * the indicator in n+1 ticks, with same parameters (e.g. same last price). If we try to
 * perform alert based on the condition :-
 * (1) When a stock hits an indicator in every tick.
 *
 * We will be getting far too many duplicated alert messages, which will not be client's
 * interest. The alert should be perform based on the following condition :-
 * (1) When a stock hits an indicator in every tick AND
 * (2) There is no alert for the particular stock and indicator at previous tick.
 *
 * AlertStateManager will be used to keep track the alert state, and inform its observer,
 * when a stock should be alerted. AlertStateManager will at least perform Indicator.isTriggered.
 *
 * @author yccheok
 */
public class AlertStateManager extends Subject<Indicator, Boolean> {
    private static class Key
    {
        private final Indicator indicator;

        private Key(Indicator indicator)
        {
            this.indicator = indicator;
        }

        public static Key newInstance(Indicator indicator) {
            return new Key(indicator);
        }

        @Override
        public int hashCode() {
            int result = 17;

            if (indicator instanceof OperatorIndicator)
            {
                result = 31 * result + ((OperatorIndicator)indicator).toString().hashCode();
            }

            if (indicator.getStock() != null)
            {
                result = 31 * result + indicator.getStock().code.hashCode();
            }

            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;

            if (!(o instanceof Key))
                return false;

            boolean result = true;
            final Indicator dest = ((Key)o).indicator;

            if (indicator instanceof OperatorIndicator &&  dest instanceof OperatorIndicator)
            {
                /* Our own special defination for OperatorIndicator's equals. Do
                 * not embedded this code into OperatorIndicator itself since
                 * it doesn't make sense from the point of OperatorIndicator view.
                 */
                final String string0 = ((OperatorIndicator)indicator).toString();
                final String string1 = ((OperatorIndicator)dest).toString();
                result = string0.equals(string1);
            }
            else
            {
                result = indicator != null ? indicator.equals(dest) : indicator == dest;
            }

            if (result == false) {
                return result;
            }

            if (indicator == null && dest == null) {
                return true;
            }

            if (indicator.getStock() != null && dest.getStock() != null)
            {
                result = indicator.getStock().code.equals(dest.getStock().code);
            }
            else
            {
                result = (indicator.getStock() == dest.getStock());
            }

            return result;
        }

        @Override
        public String toString() {
            return "" + indicator;
        }
    }

    /* Re-start from the initial state. */
    public synchronized void clearState()
    {
        alertRecords.clear();
        stockCodeToAlertRecords.clear();
    }

    /* Re-start a particular stock from the initial state. */
    public synchronized void clearState(Stock stock)
    {
        final Code code = stock.code;
        final java.util.Set<Key> keys = stockCodeToAlertRecords.remove(code);
        if (keys == null) {
            return;
        }

        for (Key key : keys) {
            this.alertRecords.remove(key);
        }
    }

    public synchronized void clearState(Indicator indicator, Stock stock)
    {
        final Code code = stock.code;
        final java.util.Set<Key> keys = stockCodeToAlertRecords.get(code);

        if (keys == null) {
            return;
        }

        /*
         * Having FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR, is to enable us
         * to remove a particular indicator from AlertStateManager, without the need
         * to call time-consuming getLastPriceFallBelowIndicator and
         * getLastPriceRiseAboveIndicator. In order for indicator to perform
         * correctly, we need to call indicator's mutable method (setStock).
         * However, since FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR are
         * sharable variables, we are not allowed to call setStock outside
         * synchronized block. We need to perfrom a hacking liked workaround
         * Within syncrhonized block, call getStock (To get old stock), setStock and
         * restore back old stock.
         */
        final Stock oldStock = indicator.getStock();
        indicator.setStock(stock);
        final Key key = Key.newInstance(indicator);
        keys.remove(key);
        alertRecords.remove(key);

        if (keys.size() <= 0) {
            stockCodeToAlertRecords.remove(code);
        }

        // Remember to reset back to old stock.
        indicator.setStock(oldStock);
    }

    private synchronized boolean add(Indicator indicator)
    {
        assert (indicator.getStock() != null);
        final Code code = indicator.getStock().code;
        final Key key = Key.newInstance(indicator);
        final boolean result0 = alertRecords.add(key);
        if (result0 == false) {
            return result0;
        }
        // result0 == true
        final Set<Key> keys = stockCodeToAlertRecords.get(code);
        if (keys != null) {
            final boolean result1 = keys.add(key);
            assert(result1);
            return result1;
        }
        final Set<Key> newKeys = new java.util.HashSet<Key>();
        newKeys.add(key);
        stockCodeToAlertRecords.put(code, newKeys);
        return true;
    }

    private synchronized boolean remove(Indicator indicator)
    {
        assert (indicator.getStock() != null);
        final Code code = indicator.getStock().code;
        final Key key = Key.newInstance(indicator);
        final boolean result0 = alertRecords.remove(key);
        if (result0 == false) {
            return result0;
        }
        // result0 == true
        final Set<Key> keys = stockCodeToAlertRecords.get(code);
        assert (keys != null);
        keys.remove(key);
        if (keys.size() <= 0) {
            stockCodeToAlertRecords.remove(code);
        }
        return true;
    }

    /*
     if writes are very infrequent w/ respect to reads, you could use
     CopyOnWriteArrayList (it has an addIfAbsent method). similarly, you could
     improve on your synchronized version by allowing concurrent reads using a
     ReadWriteLock (again, only a win if writes are fairly infrequent).
     otherwise, if writes are fairly frequent, your original example is probably
     your best bet. have you actually profiled your application to see if this
     is your bottleneck? if not, i highly recommend doing that first before
     using a more complicated concurrency structure. lastly, are you sure you
     need a List. you can get "random" access using a Map, and then you could
     use something like a ConcurrentHashMap.
    */
    public void alert(Indicator indicator)
    {
        final boolean result = indicator.isTriggered();
        
        if (result)
        {
            final boolean flag = this.add(indicator);

            if (flag)
            {
                this.notify(indicator, true);
            }
        }
        else
        {
            final boolean flag = this.remove(indicator);

            if (flag)
            {
                this.notify(indicator, false);
            }
        }
    }

    public void alert(List<? extends Indicator> indicators)
    {
        boolean result = true;

        for (Indicator indicator : indicators)
        {
            final boolean tmp = indicator.isTriggered();
            if (!tmp)
            {
                result = false;
                break;
            }
        }


        if (result)
        {
            for (Indicator indicator : indicators)
            {
                final boolean flag = this.add(indicator);

                if (flag)
                {
                    this.notify(indicator, true);
                }
            }
        }
        else
        {
            for (Indicator indicator : indicators)
            {
                final boolean flag = this.remove(indicator);

                if (flag)
                {
                    this.notify(indicator, false);
                }
            }
        }
    }

    private final java.util.Set<Key> alertRecords = new java.util.HashSet<Key>();
    private final java.util.Map<Code, java.util.Set<Key>> stockCodeToAlertRecords = new java.util.HashMap<Code, java.util.Set<Key>>();
}
