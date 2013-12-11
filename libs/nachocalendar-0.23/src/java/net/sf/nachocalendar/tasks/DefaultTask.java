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
 * DefaultTask.java
 *
 * Created on August 18, 2004, 9:40 PM
 */

package net.sf.nachocalendar.tasks;

import java.io.Serializable;
import java.util.Date;

/**
 * Default implementation of the Task interface.
 * @author Ignacio Merani
 */
public class DefaultTask implements Task, Serializable {
    private Date date;
    private String name;
    
    /** Creates a new instance of DefaultTask. */
    public DefaultTask() {
    }
    
    /**
     * Returns the Date of this task.
     * @return Returns the date.
     */
    public Date getDate() {
        return (Date) date.clone();
    }
    /**
     * Sets the date of this task.
     * @param date The date to set.
     */
    public void setDate(Date date) {
        if (date != null) {
            this.date = (Date) date.clone();
        } else this.date = null;
    }
    /**
     * Returns the name of this task.
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of this task.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns a String representing this object.
     * @return String representing this object.
     */    
    public String toString() {
        return name;
    }
}
