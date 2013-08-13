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

package org.yccheok.jstock.portfolio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;

/**
 *
 * @author yccheok
 */
public class Activities {
    public Activities(SimpleDate simpleDate)
    {
        this.simpleDate = simpleDate;
    }

    public SimpleDate getDate() {
        return this.simpleDate;
    }

    public boolean add(Activity activity) {
        final boolean status = this.activities.add(activity);
        java.util.Collections.sort(this.activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
        return status;
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

    public String toSummary() {
        Map<String, Double> datas = new HashMap<String, Double>();

        for (Activity activity : activities) {
            final Stock stock = (Stock)activity.get(Activity.Param.Stock);
            String key = (stock != null ? stock.symbol.toString() : "") + activity.getType();
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
            final String who = stock != null ? stock.symbol.toString() : "";
            final Activity.Type type = activity.getType();
            String key = who + type;
            Double d = datas.get(key);
            /* Must not be null due to first loop. */
            if (who.length() > 1) {
                message = message + who + " " + type.toString().toLowerCase() + " " + Utils.toCurrencyWithSymbol(DecimalPlaces.Three, d);
            }
            else {
                message = message + type.toString().toLowerCase() + " " + Utils.toCurrencyWithSymbol(DecimalPlaces.Three, d);
            }

            if (count < size) {
                message = message + "<br>";
            }
        }
        message = message + "";
        return message;
    }

    private final List<Activity> activities = new ArrayList<Activity>();
    private final SimpleDate simpleDate;
}
