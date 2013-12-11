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
 * YearChangeEvent.java
 *
 * Created on August 13, 2004, 5:00 PM
 */

package net.sf.nachocalendar.event;

import java.util.EventObject;

/**
 * Event fired when a year must be changed because a month under or overflows.
 * @author Ignacio Merani
 */
public class YearChangeEvent extends EventObject {
    
    /**
     * Creates a new instance of YearChangeEvent.
     * @param source source of the event
     */
    public YearChangeEvent(Object source) {
        super(source);
    }
    
}
