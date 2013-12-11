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
 * 2004-10-01    Class is now final and default constructor is private
 *
 * -------
 *
 * TaskCalendarFactory.java
 *
 * Created on August 18, 2004, 10:49 PM
 */

package net.sf.nachocalendar.tasks;

import java.util.Locale;

import javax.swing.text.DateFormatter;

import net.sf.nachocalendar.components.CalendarPanel;
import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.components.DatePanel;
import net.sf.nachocalendar.components.DefaultDayRenderer;
import net.sf.nachocalendar.components.DefaultHeaderRenderer;
import net.sf.nachocalendar.components.FormatSymbols;

/**
 * Factory class used to obtain objects customized to show Tasks.
 * 
 * @author Ignacio Merani
 */
public final class TaskCalendarFactory {
    private static DateFormatter requiredFormatter = new DateFormatter(); // setup
                                                                          // a
                                                                          // default
                                                                          // formatter

    private static Locale requiredLocale = new java.util.Locale(""); // setup a
                                                                     // default
                                                                     // locale

    /** Default constructor. */
    private TaskCalendarFactory() {

    }

    /**
     * Returns a formatted DateField.
     * 
     * @return a new formatted DatePanel
     */
    public static DateField createDateField(String formatter, Locale userLocale) {

        requiredLocale = userLocale;
        requiredFormatter = new DateFormatter(new java.text.SimpleDateFormat(
                formatter, userLocale));
        new FormatSymbols(requiredFormatter, requiredLocale);
        DateField retorno = new DateField(requiredFormatter);
        configureDateField(retorno);
        return retorno;
    }

    /**
     * Returns a new DateField customized to show Tasks.
     * 
     * @return a new DateField
     */
    public static DateField createDateField() {
        DateField retorno = new DateField();
        configureDateField(retorno);
        return retorno;
    }

    private static void configureDateField(DateField df) {
        df.setModel(new TaskDataModel());
        df.setRenderer(new TaskDecorator(new DefaultDayRenderer()));
        df.setHeaderRenderer(new DefaultHeaderRenderer());
    }

    /**
     * Returns a new CalendarPanel customized to show Tasks.
     * 
     * @return a new CalendarPanel
     */
    public static CalendarPanel createCalendarPanel() {
        CalendarPanel retorno = new CalendarPanel();
        configureCalendarPanel(retorno);
        return retorno;
    }

    /**
     * Returns a new CalendarPanel customized to show Tasks.
     * 
     * @param quantity
     *            quantity of months to show
     * @param orientation
     *            the orientation
     * @return a new CalendarPanel
     */
    public static CalendarPanel createCalendarPanel(int quantity,
            int orientation) {
        CalendarPanel retorno = new CalendarPanel(quantity, orientation);
        configureCalendarPanel(retorno);
        return retorno;
    }

    /**
     * Returns a new CalendarPanel customized to show Tasks.
     * 
     * @param quantity
     *            quantity of months to show
     * @return a new CalendarPanel
     */
    public static CalendarPanel createCalendarPanel(int quantity) {
        CalendarPanel retorno = new CalendarPanel(quantity,
                CalendarPanel.VERTICAL);
        configureCalendarPanel(retorno);
        return retorno;
    }

    private static void configureCalendarPanel(CalendarPanel cp) {
        cp.setModel(new TaskDataModel());
        cp.setRenderer(new TaskDecorator(new DefaultDayRenderer()));
        cp.setHeaderRenderer(new DefaultHeaderRenderer());
    }

    /**
     * Returns a new DatePanel customized to show Tasks.
     * 
     * @return a new DatePanel
     */
    public static DatePanel createDatePanel() {
        DatePanel retorno = new DatePanel();
        configureDatePanel(retorno);
        return retorno;
    }

    /**
     * Returns a new DatePanel customized to show Tasks.
     * 
     * @param showWeekNumbers
     *            true to show week numbers
     * @return a new DatePanel
     */
    public static DatePanel createDatePanel(boolean showWeekNumbers) {
        DatePanel retorno = new DatePanel(showWeekNumbers);
        configureDatePanel(retorno);
        return retorno;
    }

    private static void configureDatePanel(DatePanel dp) {
        dp.setModel(new TaskDataModel());
        dp.setRenderer(new TaskDecorator(new DefaultDayRenderer()));
        dp.setHeaderRenderer(new DefaultHeaderRenderer());
    }
}
