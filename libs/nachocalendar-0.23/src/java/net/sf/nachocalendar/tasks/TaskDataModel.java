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
 * TaskDataModel.java
 *
 * Created on August 18, 2004, 9:39 PM
 */

package net.sf.nachocalendar.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.sf.nachocalendar.model.DefaultDataModel;

/**
 * Implementation of the interface DataModel, created to manage
 * tasks.
 * @author Ignacio Merani
 */
public class TaskDataModel extends DefaultDataModel {
    
    /** Creates a new instance of TaskDataModel. */
    public TaskDataModel() {
    }
    
    /**
     * Adds a new Task to the model.
     * @param task the task to be added
     */    
    public void addTask(Task task) {
        Date d = task.getDate();
        if (d == null) {
            return;
        }
        Object o = getData(d);
        Collection col = (Collection) o;
        if (col == null) {
            col = new ArrayList();
            addData(d, col);
        }
        col.add(task);
    }
    
    /**
     * Returns a Collection with all the tasks related to a provided Date.
     * @param date the Date requested
     * @return Collection with values or null
     */    
    public Collection getTasks(Date date) {
        Object o = getData(date);
        if (o == null) {
            return null;
        }
        if (o instanceof Collection) {
            return (Collection) o;
        }
        return null;
    }
    
    /**
     * Removes a task from the model.
     * @param task task to be removed
     */    
    public void removeTask(Task task) {
        Collection col = getTasks(task.getDate());
        if (col != null) {
            col.remove(task);
            if (col.size() == 0) {
                removeData(task.getDate());
            }
        }
    }
}
