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
 * DataModel.java
 *
 * Created on August 12, 2004, 2:30 PM
 */

package net.sf.nachocalendar.model;

import java.util.Date;


/**
 * Model used to manage data asociated to dates. The implementation varies basically
 * in the way the data is aquired and the data type.
 *
 * @author Ignacio Merani
 */
public interface DataModel {
    
    /**
     * Checks if the date provided has data.
     * @param date Date to be checked
     * @return the data or null if it has not
     */    
    Object getData(Date date);
}
