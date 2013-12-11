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
 *  2004-10-01   Checked with checkstyle
 *
 * -------
 *
 * DayRenderer.java
 *
 * Created on August 15, 2004, 11:12 AM
 */

package net.sf.nachocalendar.components;

import java.awt.Component;
import java.util.Date;

/**
 * Interface that must be implemented by classes used to render Days.
 * @author Ignacio Merani
 */
public interface DayRenderer {
    
    /**
     * Returns a component configured to render the Day.
     * @return component to be used
     * @param daypanel Daypanel to be renderer
     * @param day current day
     * @param data current data
     * @param selected true if it's selected
     * @param working true if it's a working day
     * @param enabled true if it's enabled
     */    
    Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled);
}
