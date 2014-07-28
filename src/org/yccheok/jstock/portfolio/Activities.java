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
import java.util.List;
import org.yccheok.jstock.engine.SimpleDate;

/**
 *
 * @author yccheok
 */
public class Activities {
    public Activities(SimpleDate simpleDate)
    {
        this.simpleDate = simpleDate;
    }

    public void ensureSorted() {
        java.util.Collections.sort(this.activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
    }
    
    public SimpleDate getDate() {
        return this.simpleDate;
    }

    public boolean add(Activity activity) {
        final boolean status = this.activities.add(activity);

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

    private final List<Activity> activities = new ArrayList<Activity>();
    private final SimpleDate simpleDate;
}
