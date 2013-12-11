/*
 *  NachoCalendar
 *
 * Project Info:  http://nachocalendar.sf.net
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * Changes
 * -------
 * 
 * 2005-01-09   Cleanups
 * 2004-12-28   convertToDate: Added null support.
 * 2004-12-21   Added isToday() funcion
 * 
 * -------
 *
 * CalendarUtils.java
 *
 * Created on October 22, 2004, 10:01 PM
 */

package net.sf.nachocalendar.components;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

/**
 * Collection of utilities.
 * @author  Ignacio Merani
 */
public final class CalendarUtils {
    private static DateFormat dateformat;
    private static Calendar calendar1, calendar2;
    private static ResourceBundle bundle = ResourceBundle.getBundle("net.sf.nachocalendar.language");
    static {
        dateformat = DateFormat.getDateInstance();
        calendar1 = new GregorianCalendar();
        calendar2 = new GregorianCalendar();
    }
    
    /** Creates a new instance of CalendarUtils */
    private CalendarUtils() {
    }
    
    /**
     * Converts Object to Date.
     * @param o Object to convert
     * @throws ParseException if something goes wrong
     * @return a Date
     */
    public static Date convertToDate(Object o) throws ParseException {
        if (o == null) return null;
        if (o instanceof Date) {
            return (Date) o;
        }
        if (o instanceof Timestamp) {
            return new Date(new java.sql.Date(((Timestamp)o).getTime()).getTime());
        }        
        if (o instanceof java.sql.Date) {
            return new Date(((java.sql.Date) o).getTime());
        }
        return dateformat.parse(o.toString());
    }
    
    private static synchronized boolean isSameDay() {
        if (calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR)) return false;
        if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)) return false;
        return true;
    }
    
    /**
     * Compares two dates.
     * @param d1 first date
     * @param d2 second date
     * @return true if both dates are the same day
     */
    public static synchronized boolean isSameDay(Date d1, Date d2) {
        calendar1.setTime(d1);
        calendar2.setTime(d2);
        return isSameDay();
    }
    
    /**
     * Compares provided date with the current Date.
     * @param date date to compare
     * @return true if is current day
     */
    public static synchronized boolean isToday(Date date) {
        calendar1.setTimeInMillis(System.currentTimeMillis());
        calendar2.setTime(date);
        return isSameDay();
    }
    
    /**
     * Returns a localized message.
     * @param key message key
     * @return
     */
    public static String getMessage(String key) {
        return bundle.getString(key);
    }
}
