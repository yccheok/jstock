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
 * 2005-06-18   Added valueAdjusting methods
 * 2005-01-09   Cleanups
 * 
 * -------
 *
 * DateSelectionModel.java
 * 
 * Created on Dec 24, 2004
 * 
 */
package net.sf.nachocalendar.model;

import java.util.Date;

import net.sf.nachocalendar.event.DateSelectionListener;

/**
 * @author Ignacio Merani
 *
 * 
 */
public interface DateSelectionModel {

    /**
     * Used to support only one Date selected.
     */
    int SINGLE_SELECTION = 0;
    
    /**
     * Used to support only one Date interval.
     */
    int SINGLE_INTERVAL_SELECTION = 1;
    
    
    /**
     * Used to support multiple Date intevals.
     */
    int MULTIPLE_INTERVAL_SELECTION = 2;
    
    /** Adds the interval to the selection.
     * @param from starting date
     * @param to end date
     */
    void addSelectionInterval(Date from, Date to);

    /**
     *  Clears the current selection.
     */
    void clearSelection();

    /** 
     * Checks if provided date is selected.
     * @return true if date is selected
     * @param date Date to check.
     */
    boolean isSelectedDate(Date date);

    /** Checks if selection is empty.
     * @return true if selection is empty.
     */
    boolean isSelectionEmpty();
    
    /**
     * Returns the selection mode.
     * @return selection mode.
     */
    int getSelectionMode();
    
    /**
     * Sets the selection mode.
     * @param selectionMode the new selection mode.
     */
    void setSelectionMode(int selectionMode);

    /** Removes the interval from selection.
     * @param from starting date
     * @param to end date
     */
    void removeSelectionInterval(Date from, Date to);

    /**
     * Registers DateSelectionListener to receive events.
     * @param listener The listener to register.
     */
    void addDateSelectionListener(DateSelectionListener listener);

    /**
     * Removes DateSelectionListener from the list of listeners.
     * @param listener The listener to remove.
     */
    void removeDateSelectionListener(DateSelectionListener listener);
    
    /**
     * Returns the lead selection Date.
     * @return lead selection Date.
     */
    Date getLeadSelectionDate();
    
    /**
     * Sets the lead selection Date.
     * @param date lead selection Date.
     */
    void setLeadSelectionDate(Date date);
    
    /**
     * Returns the selected Date.
     * @return selected Date.
     */
    Object getSelectedDate();
    
    /**
     * Sets the selected Dates.
     * @return selected Dates.
     */
    Object[] getSelectedDates();
    
    /**
     * Sets the selected Date.
     * @param date selected Date.
     */
    void setSelectedDate(Object date);
   
    /**
     * Sets the selected Dates.
     * @param dates selected Dates.
     */
    void setSelectedDates(Object[] dates);
    
    /**
     * This attribute indicates that any
     * upcoming changes to the value of the 
     * model should be considered a single event.
     * 
     * @param b
     */
    void setValueIsAdjusting(boolean b);
    
    /**
     * Returns true if the current changes 
     * to the value property are part of a 
     * series of changes.
     * 
     * @return
     */
    boolean getValueIsAdjusting();
}
