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
 * 2004-10-01   Checked with checkstyle
 *
 * -------
 *
 * DefaultDataModel.java
 *
 * Created on August 12, 2004, 2:34 PM
 */

package net.sf.nachocalendar.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.nachocalendar.event.DataChangeEvent;

/**
 * Default implementation for DataModel. It has a collection
 * to contain the data and convenient accesor methods
 *
 * @author Ignacio Merani
 */
public class DefaultDataModel implements DataModel {
    private HashMap data, mindata;
    private Calendar cal, check;
    private int currentmonth;
    
    /**
     * Utility field holding list of DataModelListeners.
     */
    private transient java.util.ArrayList dataModelListenerList;
    
    /** Creates a new instance of DefaultDataModel. */
    public DefaultDataModel() {
        cal = Calendar.getInstance();
        check = Calendar.getInstance();
        data = new HashMap();
        mindata = new HashMap();
    }
    
    private void changeMonth(int month) {
        currentmonth = month;
        mindata.clear();
        Iterator it = data.keySet().iterator();
        while (it.hasNext()) {
            Date d = (Date) it.next();
            check.setTime(d);
            if (check.get(Calendar.MONTH) == month) {
                mindata.put(d, data.get(d));
            }
        }
    }
    /**
     * Adds new data to the Collection.
     * @param date the new date
     * @param o the new data
     */
    public void addData(Date date, Object o) {
        data.put(date, o);
        fireDataModelListenerDataChanged(new DataChangeEvent(o, date));
        currentmonth = -1;
    }
    
    /**
     * Removes the provided data from the Collection.
     * @param date date to be removed
     */
    public void removeData(Date date) {
        Object o = data.remove(date);
        currentmonth = -1;
        if (o != null) {
            fireDataModelListenerDataChanged(new DataChangeEvent(o, date));
        }
    }
    
    /**
     * Returns the quantity of data in the Collection.
     * @return the size of the Collection
     */
    public int getSize() {
        return data.size();
    }
    
    /**
     * Returns a Collection with the data.
     * @return Collection with the data
     */
    public Map getAll() {
        return (Map) data.clone();
    }
    
    /**
     * Removes all data from the Collection.
     */
    public void clear() {
        data.clear();
    }
    
    /**
     * Checks if the date provided has data.
     * @param date Date to be checked
     * @return the data or null if it has not
     */
    public Object getData(Date date) {
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        if (month != currentmonth) {
            changeMonth(month);
        }
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Iterator it = mindata.keySet().iterator();
        while (it.hasNext()) {
            Date d = (Date) it.next();
            if (compareDates(year, month, day, d)) {
                return mindata.get(d);
            }
        }
        return null;
    }
    
    private boolean compareDates(int year, int month, int day, Date d) {
        check.setTime(d);
        if (day != check.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        if (month != check.get(Calendar.MONTH)) {
            return false;
        }
        if (year != check.get(Calendar.YEAR)) {
            return false;
        }
        return true;
    }
    
    /**
     * Registers DataModelListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addDataModelListener(net.sf.nachocalendar.event.DataModelListener listener) {
        if (dataModelListenerList == null) {
            dataModelListenerList = new java.util.ArrayList();
        }
        dataModelListenerList.add(listener);
    }
    
    /**
     * Removes DataModelListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeDataModelListener(net.sf.nachocalendar.event.DataModelListener listener) {
        if (dataModelListenerList != null) {
            dataModelListenerList.remove(listener);
        }
    }
    
    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void fireDataModelListenerDataChanged(net.sf.nachocalendar.event.DataChangeEvent event) {
        java.util.ArrayList list;
        synchronized (this) {
            if (dataModelListenerList == null) {
                return;
            }
            list = (java.util.ArrayList) dataModelListenerList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((net.sf.nachocalendar.event.DataModelListener) list.get(i)).dataChanged(event);
        }
    }
}
