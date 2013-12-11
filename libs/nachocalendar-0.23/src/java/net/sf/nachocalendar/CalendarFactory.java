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
 * 2004-01-01    Class is now final and default constructor is private
 *
 * -------
 *
 * CalendarFactory.java
 *
 * Created on August 20, 2004, 11:48 PM
 */

package net.sf.nachocalendar;

import java.io.InputStream;

import net.sf.nachocalendar.components.CalendarPanel;
import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.components.DatePanel;
import net.sf.nachocalendar.components.DefaultDayRenderer;
import net.sf.nachocalendar.components.DefaultHeaderRenderer;
import net.sf.nachocalendar.customizer.DirectSetter;
import net.sf.nachocalendar.customizer.PropertiesCustomizer;
import net.sf.nachocalendar.customizer.PropertiesSetter;
import net.sf.nachocalendar.customizer.XMLCustomizer;

/** Factory with convenient methods to get components ready to use.
 * @author Ignacio Merani
 */
public final class CalendarFactory {
    private static PropertiesSetter setter;
    
    static {
        InputStream is = ClassLoader.getSystemResourceAsStream("nachocalendar.properties");
        if (is != null) {
            try {
                setter = new DirectSetter(new PropertiesCustomizer(is));
            } catch (Exception e) {
                // do nothing
            }
        }
        
        if (setter == null) {
            is = ClassLoader.getSystemResourceAsStream("nachocalendar.xml");
            if (is != null) {
                try {
                    setter = new DirectSetter(new XMLCustomizer(is));
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }
    /** Default constructor, declared as private. */
    public CalendarFactory() {
    }

    /** Returns a plain DateField.
     * @return a new DatePanel
     */
    public static DateField createDateField() {
        DateField retorno = new DateField();
        if (setter != null) {
            setter.customize(retorno);
        } else configureDateField(retorno);
        return retorno;
    }

    /** Utility method used to set a DateField.
     * @param df DateField to set
     */
    private static void configureDateField(DateField df) {
        df.setRenderer(new DefaultDayRenderer());
        df.setHeaderRenderer(new DefaultHeaderRenderer());
    }

    /** Creates a plain CalendarPanel.
     * @return a new CalendarPanel
     */
    public static CalendarPanel createCalendarPanel() {
        CalendarPanel retorno = new CalendarPanel();
        if (setter != null) {
            setter.customize(retorno);
        } else configureCalendarPanel(retorno); 
        return retorno;
    }

    /** Creates a plain CalendarPanel.
     * @param quantity quantity of months to show at once
     * @param orientation the orientation
     * @return a new CalendarPanel
     */
    public static CalendarPanel createCalendarPanel(int quantity, int orientation) {
        CalendarPanel retorno = new CalendarPanel(quantity, orientation);
        if (setter != null) {
            setter.customize(retorno);
        } else configureCalendarPanel(retorno);
        return retorno;
    }

    /** Creates a plain CalendarPanel.
     * @param quantity quantity of months to show at once
     * @return a new CalendarPanel
     */
    public static CalendarPanel createCalendarPanel(int quantity) {
        CalendarPanel retorno = new CalendarPanel(quantity,
            CalendarPanel.VERTICAL);
        if (setter != null) {
            setter.customize(retorno);
        } else configureCalendarPanel(retorno);
        return retorno;
    }

    /** Utility method used to set a CalendarPanel.
     * @param cp CalendarPanel to set
     */
    private static void configureCalendarPanel(CalendarPanel cp) {
        cp.setRenderer(new DefaultDayRenderer());
        cp.setHeaderRenderer(new DefaultHeaderRenderer());
    }

    /** Utility method used to configure a DatePanel.
     * @param dp DatePanel to set
     */
    private static void configureDatePanel(DatePanel dp) {
        dp.setHeaderRenderer(new DefaultHeaderRenderer());
        dp.setRenderer(new DefaultDayRenderer());
    }

    /** Creates a plain DatePanel.
     * @return a new DatePanel
     */
    public static DatePanel createDatePanel() {
        DatePanel retorno = new DatePanel();
        if (setter != null) {
            setter.customize(retorno);
        } else configureDatePanel(retorno);
        return retorno;
    }

    /** Creates a plain DatePanel.
     * @param showWeekNumbers true to show week numbers
     * @return a new DatePanel
     */
    public static DatePanel createDatePanel(boolean showWeekNumbers) {
        DatePanel retorno = new DatePanel(showWeekNumbers);
        if (setter != null) {
            setter.customize(retorno);
        } else configureDatePanel(retorno);
        return retorno;
    }
}
