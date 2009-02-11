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
 * Copyright (C) 2008 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author yccheok
 */
public class Duration {
    public Duration (SimpleDate startDate, SimpleDate endDate)
    {
        if (startDate.compareTo(endDate) > 0)
        {
            throw new java.lang.IllegalArgumentException("startDate " + startDate + " should not after endDate " + endDate);
        }

        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Duration(Date startDate, Date endDate)
    {
        this(new SimpleDate(startDate), new SimpleDate(endDate));
    }

    public Duration(Calendar start, Calendar end) {
        this(start.getTime(), end.getTime());
    }

    public SimpleDate getStartDate()
    {
        return startDate;
    }

    public SimpleDate getEndDate()
    {
        return endDate;
    }

    public long getDurationInDays()
    {
        if (durationInDays < 0)
        {
            durationInDays = Utils.getDifferenceInDays(startDate.getCalendar(), endDate.getCalendar());
        }

        return durationInDays;
    }

    public boolean isContains(Duration duration)
    {
        return (this.startDate.compareTo(duration.startDate) <= 0) && (this.endDate.compareTo(duration.endDate) >= 0);
    }

    public static Duration getTodayDurationByYears(int durationInYears)
    {
        if (durationInYears < 0)
        {
            throw new java.lang.IllegalArgumentException("durationInYears must be a non-negative number");
        }

        final Calendar calendar = Calendar.getInstance();
        final SimpleDate end = new SimpleDate(calendar);
        calendar.add(Calendar.YEAR, -durationInYears);
        final SimpleDate start = new SimpleDate(calendar);
        return new Duration(start, end);
    }

    public Duration getUnionDuration(Duration duration)
    {
        final SimpleDate start = this.startDate.compareTo(duration.startDate) <= 0 ? this.startDate : duration.startDate;
        final SimpleDate end = this.endDate.compareTo(duration.endDate) >= 0 ? this.endDate : duration.endDate;

        return new Duration(start, end);
    }

    public static Duration getTodayDurationByDays(int durationInDays)
    {
        if (durationInDays < 0)
        {
            throw new java.lang.IllegalArgumentException("durationInDays must be a non-negative number");
        }
        
        final Calendar calendar = Calendar.getInstance();
        final SimpleDate end = new SimpleDate(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, -durationInDays);
        final SimpleDate start = new SimpleDate(calendar);
        return new Duration(start, end);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + startDate.hashCode();
        result = 31 * result + startDate.hashCode();

        return result;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if(!(o instanceof Duration))
            return false;

        Duration duration = (Duration)o;

        return this.startDate.equals(duration.startDate) && this.endDate.equals(duration.endDate);
    }

    @Override
    public String toString() {
        return Duration.class.getName() + "[startDate=" + startDate + ",endDate=" + endDate + "]";
    }

    // Cache
    private long durationInDays = -1;
    private final SimpleDate startDate;
    private final SimpleDate endDate;
}
