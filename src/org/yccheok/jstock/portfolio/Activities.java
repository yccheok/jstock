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

package org.yccheok.jstock.portfolio;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;

/**
 *
 * @author yccheok
 */
public class Activities implements java.lang.Comparable<Activities> {
    public Activities(SimpleDate simpleDate)
    {
        this.simpleDate = simpleDate;
    }

    public SimpleDate getDate() {
        return this.simpleDate;
    }

    public boolean add(Activity activity) {
        needEvaluation = true;
        return this.activities.add(activity);
    }

    public int size() {
        return this.activities.size();
    }

    public Activity get(int index) {
        return this.activities.get(index);
    }

    public List<Activity.Type> getTypes() {
        List<Activity.Type> types = new ArrayList<Activity.Type>();
        for (Activity activity : activities) {
            if (false == types.contains(activity.getType())) {
                types.add(activity.getType());
            }
        }
        java.util.Collections.sort(types);
        return types;
    }

    public double getNetAmount() {
        if (false == needEvaluation) {
            return netAmount;
        }

        netAmount = 0.0;

        for (Activity activity : activities) {
            switch(activity.getType()) {
                case Buy:
                    netAmount = netAmount - activity.getAmount();
                    break;
                case Sell:
                    netAmount = netAmount + activity.getAmount();
                    break;
                case Deposit:
                    netAmount = netAmount + activity.getAmount();
                    break;
                case Dividend:
                    netAmount = netAmount + activity.getAmount();
                    break;
            }
        }

        needEvaluation = false;

        return netAmount;
    }

    public String toSummary() {
        Map<String, Double> datas = new HashMap<String, Double>();

        for (Activity activity : activities) {
            final Stock stock = (Stock)activity.get(Activity.Param.Stock);
            String key = (stock != null ? stock.getSymbol().toString() : "") + activity.getType();
            Double d = datas.get(key);
            if (d != null) {
                double total = d.doubleValue() + activity.getAmount();
                datas.put(key, total);
            }
            else {
                datas.put(key, activity.getAmount());
            }
        }

        String message = "";
        int count = 0;
        final int size = activities.size();
        for (Activity activity : activities) {
            count++;
            final Stock stock = (Stock)activity.get(Activity.Param.Stock);
            final String who = stock != null ? stock.getSymbol().toString() : "";
            final Activity.Type type = activity.getType();
            String key = who + type;
            Double d = datas.get(key);
            /* Must not be null due to first loop. */
            if (who.length() > 1) {
                message = message + who + " " + type.toString().toLowerCase() + " " + Utils.currencyNumberFormat(d);
            }
            else {
                message = message + type.toString().toLowerCase() + " " + Utils.currencyNumberFormat(d);
            }

            if (count < size) {
                message = message + "<br>";
            }
        }
        message = message + "";
        return message;
    }

    @Override
    public int compareTo(Activities o) {
        return this.simpleDate.compareTo(o.simpleDate);
    }

    private double netAmount = 0.0;
    private volatile boolean needEvaluation = true;

    SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy");
    //static {
    //    currencyFormat.setMaximumFractionDigits(2);
    //    currencyFormat.setMinimumFractionDigits(2);
    //}
    private final List<Activity> activities = new ArrayList<Activity>();
    private final SimpleDate simpleDate;
}
