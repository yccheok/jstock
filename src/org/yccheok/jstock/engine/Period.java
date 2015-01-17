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

package org.yccheok.jstock.engine;

import java.util.Calendar;

public enum Period {
    // Order is important. Less days come first.
    Days7(Calendar.DATE, 7),
    Month1(Calendar.MONTH, 1),
    Months3(Calendar.MONTH, 3),
    Months6(Calendar.MONTH, 6),
    Year1(Calendar.YEAR, 1),
    Years5(Calendar.YEAR, 5),
    Years10(Calendar.YEAR, 10);

    Period(int field, int count) {
        this.field = field;
        this.count = count;
    }

    public long getStartTimestamp(long endTimestamp) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTimestamp);
        calendar.add(this.field, -count);
        return calendar.getTimeInMillis();
    }

    private final int field;
    private final int count;
}
