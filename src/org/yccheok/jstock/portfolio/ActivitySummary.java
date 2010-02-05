/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yccheok.jstock.engine.SimpleDate;

/**
 *
 * @author yccheok
 */
public class ActivitySummary {

    public boolean add(SimpleDate date, Activity activity) {
        Activities activities = activitiesMap.get(date);
        if (activities == null) {
            activities = new Activities(date);
            activitiesMap.put(date, activities);
        }

        return activities.add(activity);
    }

    public int size() {
        return activitiesMap.size();
    }

    public Activities get(int index) {
        final Set<SimpleDate> key = activitiesMap.keySet();
        final List<SimpleDate> _simpleDates = new ArrayList<SimpleDate>(key);
        java.util.Collections.sort(_simpleDates);
        final SimpleDate simpleDate = _simpleDates.get(index);
        return activitiesMap.get(simpleDate);
    }

    public Collection<Activities> values() {
        return activitiesMap.values();
    }

    /* simpleDates shall be removed. It is still here and marked as transient, for xstream backward compatible purpose. */
    private transient List<SimpleDate> simpleDates = new ArrayList<SimpleDate>();
    
    private final Map<SimpleDate, Activities> activitiesMap = new HashMap<SimpleDate, Activities>();
}
