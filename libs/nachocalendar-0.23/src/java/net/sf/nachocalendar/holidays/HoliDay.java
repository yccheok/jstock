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
 * Created on Aug 18, 2004
 *
 */
package net.sf.nachocalendar.holidays;

import java.util.Date;

/**
 * Interface that must be implemented to represent a HoliDay.
 * and use the HoliDayModel
 * @author Ignacio Merani
 */
public interface HoliDay {
    /**
     * Getter for property name.
     *
     * @return Value of property name.
     */
    String getName();
    
    /**
     * Setter for property name.
     *
     * @param name
     *            New value of property name.
     */
    void setName(String name);
    
    /**
     * Getter for property date.
     *
     * @return Value of property date.
     */
    Date getDate();
    
    /**
     * Setter for property date.
     *
     * @param date
     *            New value of property date.
     */
    void setDate(Date date);
    
    /**
     * Getter for property recurrent.
     *
     * @return Value of property recurrent.
     */
    boolean isRecurrent();
    
    /**
     * Setter for property recurrent.
     *
     * @param recurrent
     *            New value of property recurrent.
     */
    void setRecurrent(boolean recurrent);
}
