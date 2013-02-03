/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.*;

/**
 *
 * @author yccheok
 */

// The Date data structure from java.util is almost useless. We will define our
// own simple data structure.
public class SimpleDate implements java.lang.Comparable<SimpleDate> {
    
    /** Creates a new instance of SimpleDate */
    public SimpleDate(int year, int month, int date) {
        this.year = year;
        this.month = month;
        this.date = date;
    }

    public SimpleDate() {
        this(Calendar.getInstance());
    }

    public SimpleDate(java.util.Calendar calendar) {        
        this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
    }
    
    public SimpleDate(java.util.Date date) {        
        Calendar container = Calendar.getInstance();
        container.setTime(date);
        
        this.year = container.get(Calendar.YEAR);
        this.month = container.get(Calendar.MONTH);
        this.date = container.get(Calendar.DATE);
    }
    
    public SimpleDate(SimpleDate simpleDate) {
        this.year = simpleDate.year;
        this.month = simpleDate.month;
        this.date = simpleDate.date;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + year;
        result = 31 * result + month;
        result = 31 * result + date;
        
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof SimpleDate))
            return false;
        
        SimpleDate simpleDate = (SimpleDate)o;
        
        return this.year == simpleDate.year && this.month == simpleDate.month && this.date == simpleDate.date;
    }
    
    public Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        org.yccheok.jstock.engine.Utils.resetCalendarTime(calendar);
        return calendar;
    }

    public Date getTime() {
        return this.getCalendar().getTime();
    }

    public int getYear() {
        return year;
    }
    
    public int getMonth() {
        return month;
    }
    
    public int getDate() {
        return date;
    }
    
    @Override
    public int compareTo(SimpleDate simpleDate) {
        final int yearDiff = year - simpleDate.year;
        if (yearDiff != 0) {
            return yearDiff;
        }
        
        final int monthDiff = month - simpleDate.month;
        if (monthDiff != 0) {
            return monthDiff;
        }
        
        return date - simpleDate.date;
    }
    
    @Override
    public String toString() {
        return SimpleDate.class.getName() + "[year=" + year + ",month=" + month + ",date=" + date + "]";
    }
    
    private final int year;   /* ? */
    private final int month;  /* 0 ~ 11 */
    private final int date;   /* 1 ~ 31 */
}
