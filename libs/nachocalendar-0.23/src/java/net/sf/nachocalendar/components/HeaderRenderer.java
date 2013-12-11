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
 * HeaderRenderer.java
 *
 * Created on August 15, 2004, 7:24 PM
 */

package net.sf.nachocalendar.components;

import java.awt.Component;

/**
 * Interface that must be implemented by classes used to render.
 * Headers and Week Number
 * @author Ignacio Merani
 */
public interface HeaderRenderer {
    
    /**
     * Returns the component used to render the header.
     * @return Component to be used
     * @param isHeader true if is used for header, false if used for week number
     * @param isWorking true if it's a working day
     * @param value value to be show
     * @param panel panel where this component is showed
     */    
    Component getHeaderRenderer(HeaderPanel panel, Object value, boolean isHeader, boolean isWorking);
}
