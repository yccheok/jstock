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
 * -------
 *
 * PropertiesSetter.java
 *
 * Created on Dec 19, 2005
 */
package net.sf.nachocalendar.customizer;

import net.sf.nachocalendar.components.CalendarPanel;
import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.components.DatePanel;

/**
 * Interface that must be implemented by
 * classes that set components properties.
 * @author Ignacio Merani
 *
 *
 */
public interface PropertiesSetter {

    /**
     * Customizes a Datefield.
     * 
     * @param datefield
     */
    public void customize(DateField datefield);

    /**
     * Customizes a CalendarPanel.
     * @param calendarpanel
     */
    public void customize(CalendarPanel calendarpanel);

    /**
     * Customizes a DatePanel.
     * @param datepanel
     */
    public void customize(DatePanel datepanel);

}