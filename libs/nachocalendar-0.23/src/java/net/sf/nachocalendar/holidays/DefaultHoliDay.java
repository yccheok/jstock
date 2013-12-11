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
 * DefaultHoliDay.java
 *
 * Created on August 12, 2004, 10:07 AM
 */

package net.sf.nachocalendar.holidays;

import java.io.Serializable;
import java.util.Date;

/**
 * Default implementation for holidays.
 * @author Ignacio Merani
 */
public class DefaultHoliDay implements Serializable, HoliDay {
    
    /**
     * Holds value of property name.
     */
    private String name;
    
    /**
     * Holds value of property date.
     */
    private Date date;
    
    /**
     * Holds value of property description.
     */
    private String description;
    
    /**
     * Holds value of property recurrent.
     */
    private boolean recurrent;
    
    /** Creates a new instance of HoliDay. */
    public DefaultHoliDay() {
    }
    
    /**
     * Getter for property name.
     *
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Setter for property name.
     *
     * @param name
     *            New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Getter for property date.
     *
     * @return Value of property date.
     */
    public Date getDate() {
        if (date != null) return (Date) date.clone();
        return null;
    }
    
    /**
     * Setter for property date.
     *
     * @param date
     *            New value of property date.
     */
    public void setDate(Date date) {
        if (date == null) throw new IllegalArgumentException("Illegal Date");
        this.date = (Date) date.clone();
    }
    
    /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Setter for property description.
     *
     * @param description
     *            New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property recurrent.
     *
     * @return Value of property recurrent.
     */
    public boolean isRecurrent() {
        return this.recurrent;
    }
    
    /**
     * Setter for property recurrent.
     *
     * @param recurrent
     *            New value of property recurrent.
     */
    public void setRecurrent(boolean recurrent) {
        this.recurrent = recurrent;
    }
    
    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        return name;
    }
}
