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
 * 2005-03-25   Added Moonphase painting
 * 2005-03-25   fixed correct Numbering of Weeks (at least for European standard)
 * 2004-12-21   Added antialiasing support
 * 2004-12-15   Removed ActionListener
 * 2004-12-14   MonthPanel now extends JComponent
 * 2004-10-24   removed all references to ButtonGroup
 * 2004-10-23   added multiple selection. Changed Calendar to GregorianCalendar
 * 2004-10-22   setEnabled(boolean b) overriden, now works
 * 2004-10-17   Added keylistener support.
 * 2004-10-17   Added keylistener to children, to pass events up.
 * 2004-10-01   Checked with checkstyle.
 *
 * -------
 *
 * MonthPanel.java
 *
 * Created on August 11, 2004, 12:07 AM
 */

package net.sf.nachocalendar.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.nachocalendar.model.DataModel;

/**
 * Class for displaying a month. It allows to select a day, can be combined
 * with another month.
 * 
 * @author Ignacio Merani
 */
public class MonthPanel extends JComponent {
    //private int year;
    private DayPanel[][] paneles;
    private final int rows = 6;
    private final int cols = 7;
    private int tcols, firstday, showingmonth, showingyear;
    private Calendar calendar, check, navigation;
    private String[] months, days;
    private HeaderPanel[] weeks;
    private Date date;
    private Collection changelisteners;
    private boolean[] workingdays;
    private DayRenderer renderer;
    private HeaderPanel[] headers;
    private boolean showWeekNumber, showtitle;
    private JPanel centro;
    private JLabel title;
    private static final int[] MONDAYFIRST = {0, 2, 3, 4, 5, 6, 7, 1};
    private static final int[] SUNDAYFIRST = {0, 1, 2, 3, 4, 5, 6, 7};
    private static final int[] RESTAMONDAY = {0, 7, 1, 2, 3, 4, 5, 6};
    private int minimalDaysInFirstWeek;
    private int[] dayorder;
    private DayPanel[] daypanels;
    private boolean antiAliased;
    private boolean printMoon;
    
    /**
     * Default value for working days.
     */
    private static final boolean[] DEFAULTWORKING = {false, true, true, true, true,
    true, true };
    
    /**
     * Holds value of property model.
     */
    private DataModel model;
    
    /**
     * Holds value of property headerRenderer.
     */
    private HeaderRenderer headerRenderer;
    
    /**
     * Default constructor, it uses default values for everything.
     */
    public MonthPanel() {
        this(false);
    }
    
    /**
     * Full constructor for MonthPanel. Can be especifyed workingdays, the
     * showing of week numbers and can be passed a shared buttongroup for using
     * with other MonthPanels
     * @param showWeekNumber true for showing week numbers
     */
    public MonthPanel(boolean showWeekNumber) {
        this.showWeekNumber = showWeekNumber;
        daypanels = new DayPanel[42];
        calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        check = new GregorianCalendar();
        check.set(Calendar.HOUR, 0);
        check.set(Calendar.MINUTE, 0);
        check.set(Calendar.MILLISECOND, 0);
        navigation = new GregorianCalendar();
        navigation.set(Calendar.HOUR, 0);
        navigation.set(Calendar.MINUTE, 0);
        navigation.set(Calendar.MILLISECOND, 0);
        
        firstday = calendar.getFirstDayOfWeek();
        setFocusable(true);
        
        if (firstday == Calendar.MONDAY) {
            dayorder = MONDAYFIRST;
        } else {
            dayorder = SUNDAYFIRST;
        }
        
        centro = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        add(centro, BorderLayout.CENTER);
        
        if (showWeekNumber) {
            tcols = cols + 1;
        } else {
            tcols = cols;
        }
        headers = new HeaderPanel[tcols];
        centro.setLayout(new GridLayout(rows + 1, tcols));
        
        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);
        title.setVisible(false);
        changelisteners = new ArrayList();
        paneles = new DayPanel[rows][cols];
        weeks = new HeaderPanel[rows];
        
        DateFormatSymbols symbols = new DateFormatSymbols();
        days = symbols.getShortWeekdays();
        for (int i = 1; i < days.length; i++) {
            days[i] = days[i].substring(0, 1).toUpperCase() + days[i].substring(1).toLowerCase();
        }
        
        months = symbols.getMonths();
        for (int i = 0; i < months.length - 1; i++) {
            months[i] = months[i].substring(0, 1).toUpperCase() + months[i].substring(1).toLowerCase();
        }
        
        // Cargo la cabecera de los dias
        
        for (int i = 0; i < tcols; i++) {
            headers[i] = new HeaderPanel(headerRenderer);
            centro.add(headers[i]);
        }
        
        setHeaders();
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < tcols; j++) {
                if ((j == 0) && showWeekNumber) {
                    weeks[i] = new HeaderPanel(headerRenderer);
                    centro.add(weeks[i]);
                    weeks[i].setValue(days[i]);
                } else {
                    int jj = j;
                    if (showWeekNumber) {
                        jj = j - 1;
                    }
                    paneles[i][jj] = new DayPanel(renderer, index);
                    daypanels[index] = paneles[i][jj];
                    index++;
                    centro.add(paneles[i][jj]);
                }
            }
        }
        
        setRenderer(new DefaultDayRenderer());
        setHeaderRenderer(new DefaultHeaderRenderer());
        setWorkingdays(getDefaultWorking());
    }
    
    private void setHeaders() {
        for (int i = 0; i < tcols; i++) {
            if (showWeekNumber) {
                headers[i].setValue(days[dayorder[i]]);
            } else {
                headers[i].setValue(days[dayorder[i + 1]]);
            }
        }
    }
    
    /**
     * Sets the current selected day. If the component is showing another
     * month, the showing month is changed
     * @param d new Date
     */
    public void setDay(Date d) {
        setDay(d, true);
    }
    
    /**
     * Sets the current showing month.
     * @param d new Date to get the month
     */
    public void setMonth(Date d) {
        setDay(d, false);
    }
    
    /**
     * Returns the currently selected month.
     * @return selected month
     */
    public Date getMonth() {
        return date;
    }
    
    /**
     * Sets the showing of the title.
     * @param show true for showing the title
     */
    public void showTitle(boolean show) {
        showtitle = show;
        title.setVisible(show);
        doLayout();
    }
    
    private void setDay(Date d, boolean select) {
        if (d == null) return;
        date = d;
        boolean update = ((showingyear != calendar.get(Calendar.YEAR)) || (showingmonth != calendar.get(Calendar.MONTH)));
        calendar.setTime(d);
        showingmonth = calendar.get(Calendar.MONTH);
        showingyear = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.DAY_OF_YEAR, -1 * (calendar.get(Calendar.DAY_OF_MONTH) - 1));
        if (calendar.getFirstDayOfWeek() == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1 * (calendar.get(Calendar.DAY_OF_WEEK) - 1));
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, -1 * (RESTAMONDAY[calendar.get(Calendar.DAY_OF_WEEK)] - 1));
        }
        
        for (int i=0; i < daypanels.length; i++) {
            if (update) {
                Date temp = calendar.getTime();
                daypanels[i].setDate(temp);
                if (model != null) {
                    daypanels[i].setData(model.getData(temp));
                }
                if (calendar.get(Calendar.MONTH) == showingmonth) {
                    daypanels[i].setEnabled(true);
                } else daypanels[i].setEnabled(false);
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        
        // actualizo el titulo
        if (showtitle) {
            title.setText(months[showingmonth] + " " + showingyear);
        }
        
        // actualizo semanas
        if (showWeekNumber) {
          if (minimalDaysInFirstWeek != 0)
            calendar.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
          
          calendar.setTime(d);
          //for retrieving the correct Weeknumber we have to set the calendar back to Monday
          //and then retrieve the Weeknumbers
          int backToWeekStart=0;
          int value = calendar.get(Calendar.DAY_OF_WEEK);
          if (value == Calendar.SUNDAY) {
            backToWeekStart = -6;
          } else if (value == Calendar.MONDAY) { 
            backToWeekStart = 0;
          } else if (value == Calendar.TUESDAY) { 
            backToWeekStart = -1;
          } else if (value == Calendar.WEDNESDAY) { 
            backToWeekStart = -2;
          } else if (value == Calendar.THURSDAY) { 
            backToWeekStart = -3;
          } else if (value == Calendar.FRIDAY) { 
            backToWeekStart = -4;
          } else if (value == Calendar.SATURDAY) { 
            backToWeekStart = -5;
          }
          
          if (calendar.getFirstDayOfWeek() == Calendar.MONDAY) {
            calendar.add(Calendar.DATE, backToWeekStart );
          } else {
            calendar.add(Calendar.DATE, backToWeekStart - 1 );
          }
          
          
          for (int i = 0; i < weeks.length; i++) {
            weeks[i].setValue(Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR)));
            calendar.add(Calendar.DATE,7); //jump to next week
          }
        }
        
        repaint();
        fireChangeEvent(new ChangeEvent(this));
    }
    
    /**
     * Refreshes the showing of this component.
     */
    public void refresh() {
        for (int i = 0; i < paneles.length; i++) {
            for (int j = 0; j < paneles[i].length; j++) {
                if (model != null) {
                    paneles[i][j].setData(model.getData(paneles[i][j].getDate()));
                    paneles[i][j].setPrintMoon(printMoon);
                }
            }
        }
        repaint();
    }
    
    /**
     * Returns the currently selected day.
     * @return selected Date
     */
    public Date getDay() {
        return date;
    }
    
    /**
     * Event fired every date selection change.
     * @param e event fired
     */
    protected void fireChangeEvent(ChangeEvent e) {
        Iterator it = changelisteners.iterator();
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(e);
        }
    }
    
    /**
     * Getter for property model.
     *
     * @return Value of property model.
     */
    public DataModel getModel() {
        return this.model;
    }
    
    /**
     * Setter for property model.
     *
     * @param model
     *            New value of property model.
     */
    public void setModel(DataModel model) {
        this.model = model;
    }
    
    /**
     * Getter for property renderer.
     * @return Value of property renderer.
     */
    public DayRenderer getRenderer() {
        return renderer;
    }
    
    /**
     * Setter for property renderer.
     * @param renderer New value of property renderer.
     */
    public void setRenderer(DayRenderer renderer) {
        this.renderer = renderer;
        for (int i = 0; i < paneles.length; i++) {
            for (int j = 0; j < paneles[i].length; j++) {
                paneles[i][j].setRenderer(renderer);
            }
        }
    }
    
    /**
     * Getter for property headerRenderer.
     * @return Value of property headerRenderer.
     */
    public HeaderRenderer getHeaderRenderer() {
        return this.headerRenderer;
    }
    
    /**
     * Setter for property headerRenderer.
     * @param headerRenderer New value of property headerRenderer.
     */
    public void setHeaderRenderer(HeaderRenderer headerRenderer) {
        this.headerRenderer = headerRenderer;
        for (int i = 0; i < headers.length; i++) {
            headers[i].setRenderer(headerRenderer);
        }
        if (showWeekNumber) {
            for (int i = 0; i < weeks.length; i++) {
                weeks[i].setRenderer(headerRenderer);
            }
        }
    }
    
    /**
     * Specifies how many days should the first week of the year contain <br>
     * If not specified, i.e. 0, then no changes from the defaults are done
     * 
     * @param number
     */
    public void  setMinimalDaysInFirstWeek(int number) {
      if (minimalDaysInFirstWeek != number) {
        minimalDaysInFirstWeek = number;
        setHeaders();
        setMonth(getMonth());
      }
    }
    
    /**
     * @return how many weekdays does the first week of the year have
     */
    public int getMinimalDaysInFirstWeek() {
      return minimalDaysInFirstWeek;
    }
    /**
     * Getter for property workingdays.
     * @return Value of property workingdays.
     */
    public boolean[] getWorkingdays() {
        return this.workingdays;
    }
    
    /**
     * Setter for property workingdays.
     * @param workingdays New value of property workingdays.
     */
    public void setWorkingdays(boolean[] workingdays) {
        if (workingdays == null) return;
        this.workingdays = workingdays;
        for (int i = 0; i < paneles.length; i++) {
            for (int j = 0; j < paneles[i].length; j++) {
                paneles[i][j].setWorking(workingdays[dayorder[j + 1] - 1]);
            }
        }
    }
    
    
    /**
     * Getter for property firstDayOfWeek.
     * @return Value of property firstDayOfWeek.
     */
    public int getFirstDayOfWeek() {
        return calendar.getFirstDayOfWeek();
    }
    
    /**
     * Setter for property firstDayOfWeek.
     * @param firstDayOfWeek New value of property firstDayOfWeek.
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if ((firstDayOfWeek == Calendar.MONDAY) || (firstDayOfWeek == Calendar.SUNDAY)) {
            calendar.setFirstDayOfWeek(firstDayOfWeek);
            check.setFirstDayOfWeek(firstDayOfWeek);
            if (firstDayOfWeek == Calendar.SUNDAY) {
                dayorder = SUNDAYFIRST;
            } else dayorder = MONDAYFIRST;
            setHeaders();
            setMonth(getMonth());
        }
        setWorkingdays(getWorkingdays());
    }
    
    /**
     * @return Returns the defaultWorking.
     */
    public static boolean[] getDefaultWorking() {
        return DEFAULTWORKING;
    }
    
    /**
     * Enables or disables the component
     * @param b true for enabling
     */
    public void setEnabled(boolean b) {
        for (int i=0; i < daypanels.length; i++) {
            daypanels[i].setComponentEnabled(b);
        }
    }
    
    /**
     * Getter for enabled property
     * @return true if it's enabled
     */
    public boolean isEnabled() {
        return daypanels[0].isComponentEnabled();
    }
    
    /**
     * @return Returns the antiAliased.
     */
    public boolean isAntiAliased() {
        return antiAliased;
    }
    
    /**
     * @param antiAliased The antiAliased to set.
     */
    public void setAntiAliased(boolean antiAliased) {
        this.antiAliased = antiAliased;
        for (int i=0; i < daypanels.length; i++) {
            daypanels[i].setAntiAliased(antiAliased);
        }
        
        for (int i=0; i < headers.length; i++) {
            headers[i].setAntiAliased(antiAliased);
        }
        
        if (showWeekNumber) {
            for (int i=0; i < weeks.length; i++) {
                weeks[i].setAntiAliased(antiAliased);
            }
        }
        repaint();
    }
    /**
     * @return Returns the daypanels.
     */
    protected DayPanel[] getDaypanels() {
        return daypanels;
    }
    /**
     * @param daypanels The daypanels to set.
     */
    protected void setDaypanels(DayPanel[] daypanels) {
        this.daypanels = daypanels;
    }
    
    public Date getMinDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) * -1) + 1);
        return cal.getTime();
    }
    
    public Date getMaxDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) * -1) + 1);
        cal.add(Calendar.MONTH,1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }
    
    /**
     * @return Returns the printMoon.
     */
    public boolean isPrintMoon() {
      return printMoon;
    }
    
    /**
     * @param printMoon The printMoon to set.
     */
    public void setPrintMoon(boolean printMoon) {
      if (this.printMoon != printMoon) {
        
        this.printMoon = printMoon;
        for (int i = 0; i < paneles.length; i++) {
          for (int j = 0; j < paneles[i].length; j++) {
            paneles[i][j].setPrintMoon(printMoon);
          }
        }
      }
    }
    
}
