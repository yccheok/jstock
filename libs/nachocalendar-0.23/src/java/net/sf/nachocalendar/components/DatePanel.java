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
 *  2005-06-18   Fixed ChangeListener problem
 *  2005-01-08   Cleanups
 *  2005-01-02   Fixed startup bug
 *               Added property change support
 *  2004-12-24   Reimplemented selection model, now supports multiple selection
 *               Added keyboard navigation
 *  2004-12-21   Added antialiasing support
 *  2004-12-11   setFirsDayOfWeek now works correctly
 *  2004-10-22   setEnabled(boolean b) overriden, now works
 *  2004-10-18   Added mousewheellistener
 *  2004-10-13   Added workingDays
 *  2004-10-01   Checked with checkstyle
 *
 * -------
 *
 * DatePanel.java
 *
 * Created on August 13, 2004, 8:11 PM
 */

package net.sf.nachocalendar.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.nachocalendar.event.DateSelectionEvent;
import net.sf.nachocalendar.event.DateSelectionListener;
import net.sf.nachocalendar.event.MonthChangeEvent;
import net.sf.nachocalendar.event.YearChangeEvent;
import net.sf.nachocalendar.event.YearChangeListener;
import net.sf.nachocalendar.model.DataModel;
import net.sf.nachocalendar.model.DateSelectionModel;
import net.sf.nachocalendar.model.DefaultDateSelectionModel;

/**
 * Panel for selecting a day. It has custom components to change the displaying
 * month and year
 * 
 * @author Ignacio Merani
 */
public class DatePanel extends JPanel {
    private MonthPanel monthpanel;

    private Calendar calendar, navigation;

    private MonthScroller monthscroller;

    private YearScroller yearscroller;

    private boolean antiAliased;

    private DateSelectionModel dateSelectionModel;

    private MouseListener mlistener;

    private KeyListener klistener;
    
    private boolean printMoon;
    
    private JButton today;

    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient java.util.ArrayList changeListenerList;

    /**
     * Holds value of property workingDays.
     */
    private boolean[] workingDays;
    
    /**
     * Utility field holding list of ActionListeners.
     */
    private transient java.util.ArrayList actionListenerList;

    /**
     * Default constructor. It creates a DatePanel with default values
     */

    public DatePanel() {
        monthpanel = new MonthPanel();
        init();
    }

    /**
     * Creates a DatePanel and let specify the showing of week numbers.
     * 
     * @param showWeekNumbers
     *            true for showing week numbers
     */
    public DatePanel(boolean showWeekNumbers) {
        monthpanel = new MonthPanel(showWeekNumbers);
        init();
    }

    private void init() {
        setRenderer(new DefaultDayRenderer());
        setHeaderRenderer(new DefaultHeaderRenderer());
        workingDays = new boolean[7];
        dateSelectionModel = new DefaultDateSelectionModel();
        monthscroller = new MonthScroller();
        yearscroller = new YearScroller();
        calendar = new GregorianCalendar();
        navigation = new GregorianCalendar();
        add(monthpanel);
        setDate(calendar.getTime());
        setFocusable(true);
        today = new JButton(CalendarUtils.getMessage("today"));
        today.setVisible(false);
        JPanel arriba = new JPanel(new GridLayout(1, 2));
        setLayout(new BorderLayout());

        arriba.add(monthscroller);

        arriba.add(yearscroller);
        
        add(arriba, BorderLayout.NORTH);
        add(monthpanel, BorderLayout.CENTER);
        add(today, BorderLayout.SOUTH);
        
        addListeners();
        DayPanel[] daypanels = monthpanel.getDaypanels();
        for (int i = 0; i < daypanels.length; i++) {
            daypanels[i].addMouseListener(mlistener);
            daypanels[i].addKeyListener(klistener);
        }
        monthpanel.setMonth(getDate());
    }

    private void addListeners() {
        monthscroller.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateMonth();
            }
        });

        yearscroller.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateYear();
            }
        });

        monthscroller.addYearChangeListener(new YearChangeListener() {
            public void yearIncreased(YearChangeEvent e) {
                yearscroller.setYear(yearscroller.getYear() + 1);
                updateYear();
            }

            public void yearDecreased(YearChangeEvent e) {
                yearscroller.setYear(yearscroller.getYear() - 1);
                updateYear();
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!isEnabled())
                    return;
                int q = e.getWheelRotation();
                for (int i = 0; i < Math.abs(q); i++) {
                    if (q > 0) {
                        monthscroller.nextMonth();
                    } else
                        monthscroller.previousMonth();
                }
            }
        });

        dateSelectionModel.addDateSelectionListener(new DateSelectionListener() {

                    public void valueChanged(DateSelectionEvent e) {
                        refreshSelection();
                        fireChangeListenerStateChanged(new ChangeEvent(DatePanel.this));
                    }

                });

        mlistener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DayPanel dp = (DayPanel) e.getSource();
                if (!dp.isEnabled() || !dp.isComponentEnabled())
                    return;
                dateSelectionModel.setValueIsAdjusting(true);
                if (!e.isControlDown()) {
                    dateSelectionModel.clearSelection();
                    if (e.isShiftDown()) {
                        dateSelectionModel.addSelectionInterval(
                                dateSelectionModel.getLeadSelectionDate(), dp
                                        .getDate());
                    } else
                        dateSelectionModel.addSelectionInterval(dp.getDate(),
                                dp.getDate());
                } else {
                    if (e.isShiftDown()) {
                        dateSelectionModel.addSelectionInterval(
                                dateSelectionModel.getLeadSelectionDate(), dp
                                        .getDate());
                    } else {
                        if (dateSelectionModel.isSelectedDate(dp.getDate())) {
                            dateSelectionModel.removeSelectionInterval(dp
                                    .getDate(), dp.getDate());
                        } else
                            dateSelectionModel.addSelectionInterval(dp
                                    .getDate(), dp.getDate());
                    }
                }
                dateSelectionModel.setLeadSelectionDate(dp.getDate());
                repaint();
                dp.requestFocus();
                calendar.setTime(dp.getDate());
                dateSelectionModel.setValueIsAdjusting(false);
                fireActionListenerActionPerformed(new ActionEvent(this, 0,
                        "clicked"));
            }
        };

        klistener = new KeyListener() {
            public void keyPressed(KeyEvent e) {
                boolean changed = false;
                int keycode = e.getKeyCode();
                navigation.setTime(calendar.getTime());
                if ((keycode == KeyEvent.VK_LEFT) || (keycode == 226)) {
                    int month = navigation.get(Calendar.MONTH);
                    navigation.add(Calendar.DAY_OF_YEAR, -1);
                    if (month != navigation.get(Calendar.MONTH)) {
                        fireMonthChangeListenerMonthDecreased(new MonthChangeEvent(
                                this, navigation.getTime()));
                        setDate(navigation.getTime());
                    }
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_RIGHT) || (keycode == 227)) {
                    int month = navigation.get(Calendar.MONTH);
                    navigation.add(Calendar.DAY_OF_YEAR, 1);
                    if (month != navigation.get(Calendar.MONTH)) {
                        fireMonthChangeListenerMonthIncreased(new MonthChangeEvent(
                                this, navigation.getTime()));
                        setDate(navigation.getTime());
                    }
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_UP) || (keycode == 224)) {
                    int month = navigation.get(Calendar.MONTH);
                    navigation.add(Calendar.DAY_OF_YEAR, -7);
                    if (month != navigation.get(Calendar.MONTH)) {
                        fireMonthChangeListenerMonthDecreased(new MonthChangeEvent(
                                this, navigation.getTime()));
                        setDate(navigation.getTime());
                    }
                    changed = true;
                }

                if ((keycode == KeyEvent.VK_DOWN) || (keycode == 225)) {
                    int month = navigation.get(Calendar.MONTH);
                    navigation.add(Calendar.DAY_OF_YEAR, 7);
                    if (month != navigation.get(Calendar.MONTH)) {
                        fireMonthChangeListenerMonthIncreased(new MonthChangeEvent(
                                this, navigation.getTime()));
                        setDate(navigation.getTime());
                    }
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_PAGE_UP)) {
                    navigation.add(Calendar.MONTH, -1);
                    fireMonthChangeListenerMonthIncreased(new MonthChangeEvent(
                            this, navigation.getTime()));
                    setDate(navigation.getTime());
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_PAGE_DOWN)) {
                    navigation.add(Calendar.MONTH, 1);
                    fireMonthChangeListenerMonthIncreased(new MonthChangeEvent(
                            this, navigation.getTime()));
                    setDate(navigation.getTime());
                    changed = true;
                }
                if (changed) {
                    if ((!e.isControlDown()) && (!e.isShiftDown())) {
                        dateSelectionModel.clearSelection();
                        if (e.isShiftDown()) {
                            dateSelectionModel.addSelectionInterval(
                                    dateSelectionModel.getLeadSelectionDate(),
                                    navigation.getTime());
                        } else
                            dateSelectionModel.addSelectionInterval(navigation
                                    .getTime(), navigation.getTime());
                    } else {
                        if (e.isShiftDown()) {
                            dateSelectionModel.addSelectionInterval(
                                    dateSelectionModel.getLeadSelectionDate(),
                                    navigation.getTime());
                        } else {
                            if (dateSelectionModel.isSelectedDate(navigation
                                    .getTime())) {
                                dateSelectionModel.removeSelectionInterval(
                                        navigation.getTime(), navigation
                                                .getTime());
                            } else
                                dateSelectionModel.addSelectionInterval(
                                        navigation.getTime(), navigation
                                                .getTime());
                        }
                    }
                    dateSelectionModel.setLeadSelectionDate(navigation
                            .getTime());
                    calendar.setTime(navigation.getTime());
                    monthpanel.repaint();
                }
                fireKeyListenerKeyPressed(e);
            }

            public void keyReleased(KeyEvent e) {
                fireKeyListenerKeyReleased(e);
            }

            public void keyTyped(KeyEvent e) {
                fireKeyListenerKeyTyped(e);
            }
        };

        today.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Calendar cal = Calendar.getInstance();
                setDate(new Date());
                /*monthscroller.setMonth(cal.get(Calendar.MONTH));
                yearscroller.setYear(cal.get(Calendar.YEAR));
                monthpanel.setMonth(new Date());
                monthpanel.setDay(new Date());*/
            }
        });
    }

    private void updateMonth() {
        int month = calendar.get(Calendar.MONTH);
        calendar.add(Calendar.MONTH, monthscroller.getMonth() - month);
        monthpanel.setMonth(calendar.getTime());
        refreshSelection();
    }

    private void updateYear() {
        int year = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.YEAR, yearscroller.getYear() - year);
        monthpanel.setMonth(calendar.getTime());
        refreshSelection();
    }

    /**
     * Sets the selected date. If the date is in other month, the month is also
     * changed.
     * 
     * @param d
     *            new date
     */
    public void setDate(Date d) {
        calendar.setTime(d);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (year != yearscroller.getYear()) {
            yearscroller.setYear(year);
        }

        if (month != monthscroller.getMonth()) {
            monthscroller.setMonth(month);
        }

        monthpanel.setDay(d);
        dateSelectionModel.setSelectedDate(d);
        refreshSelection();
    }

    /**
     * Returns the selected date.
     * 
     * @return selected date
     */
    public Date getDate() {
        return calendar.getTime();
    }

    /**
     * Getter for property renderer.
     * 
     * @return Value of property renderer.
     */
    public DayRenderer getRenderer() {
        return monthpanel.getRenderer();
    }

    /**
     * Setter for property renderer.
     * 
     * @param renderer
     *            New value of property renderer.
     */
    public void setRenderer(DayRenderer renderer) {
        monthpanel.setRenderer(renderer);
    }

    /**
     * Registers ChangeListener to receive events.
     * 
     * @param listener
     *            The listener to register.
     */
    public synchronized void addChangeListener(
            javax.swing.event.ChangeListener listener) {
        if (changeListenerList == null) {
            changeListenerList = new java.util.ArrayList();
        }
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     * 
     * @param listener
     *            The listener to remove.
     */
    public synchronized void removeChangeListener(
            javax.swing.event.ChangeListener listener) {
        if (changeListenerList != null) {
            changeListenerList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireChangeListenerStateChanged(
            javax.swing.event.ChangeEvent event) {
        java.util.ArrayList list;
        synchronized (this) {
            if (changeListenerList == null) {
                return;
            }
            list = (java.util.ArrayList) changeListenerList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((javax.swing.event.ChangeListener) list.get(i))
                    .stateChanged(event);
        }
    }

    /**
     * Getter for property model.
     * 
     * @return Value of property model.
     */
    public DataModel getModel() {
        return monthpanel.getModel();
    }

    /**
     * Setter for property model.
     * 
     * @param model
     *            New value of property model.
     */
    public void setModel(DataModel model) {
        monthpanel.setModel(model);
    }

    /**
     * Getter for property firstDayOfWeek.
     * 
     * @return Value of property firstDayOfWeek.
     */
    public int getFirstDayOfWeek() {
        return monthpanel.getFirstDayOfWeek();
    }

    /**
     * Setter for property firstDayOfWeek.
     * 
     * @param firstDayOfWeek
     *            New value of property firstDayOfWeek.
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if ((firstDayOfWeek == Calendar.MONDAY)
                || (firstDayOfWeek == Calendar.SUNDAY)) {
            if (monthpanel.getFirstDayOfWeek() == firstDayOfWeek) return;
            int old = monthpanel.getFirstDayOfWeek();
            monthpanel.setFirstDayOfWeek(firstDayOfWeek);
            refreshSelection();
            repaint();
            firePropertyChange("firstDayOfWeek", old, firstDayOfWeek);
        }
    }

    /**
     * Refreshes the showing of this component.
     */
    public void refresh() {
        monthpanel.refresh();
    }

    /**
     * Getter for property headerRenderer.
     * 
     * @return Value of property headerRenderer.
     */
    public HeaderRenderer getHeaderRenderer() {
        return monthpanel.getHeaderRenderer();
    }

    /**
     * Setter for property headerRenderer.
     * 
     * @param headerRenderer
     *            New value of property headerRenderer.
     */
    public void setHeaderRenderer(HeaderRenderer headerRenderer) {
        monthpanel.setHeaderRenderer(headerRenderer);
    }

    /**
     * Getter for property value.
     * 
     * @return Value of property value.
     */
    public Object getValue() {
        return dateSelectionModel.getSelectedDate();
    }

    /**
     * Setter for property value.
     * 
     * @param value
     *            New value of property value.
     */
    public void setValue(Object value) {
        try {
            setDate(CalendarUtils.convertToDate(value));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the selected Dates.
     * @param values values to set
     */
    public void setValues(Object[] values) {
        dateSelectionModel.setSelectedDates(values);
        refreshSelection();
    }

    /**
     * Getter for property workingDays.
     * 
     * @return Value of property workingDays.
     */
    public boolean[] getWorkingDays() {
        boolean[] retorno = new boolean[workingDays.length];
        for (int i = 0; i < workingDays.length; i++) {
            retorno[i] = workingDays[i];
        }
        return retorno;
    }

    /**
     * Setter for property workingDays.
     * 
     * @param workingDays
     *            New value of property workingDays.
     */
    public void setWorkingDays(boolean[] workingDays) {
        if (workingDays == null) return;
        boolean[] old = monthpanel.getWorkingdays();
        monthpanel.setWorkingdays(workingDays);
        for (int i = 0; i < workingDays.length && i < this.workingDays.length; i++) {
            this.workingDays[i] = workingDays[i];
        }
        firePropertyChange("workingDays", old, workingDays);
    }

    /**
     * Registers KeyListener to receive events.
     * 
     * @param listener
     *            The listener to register.
     */
    public synchronized void addKeyListener(java.awt.event.KeyListener listener) {
        if (listenerList == null) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(java.awt.event.KeyListener.class, listener);
    }

    /**
     * Removes KeyListener from the list of listeners.
     * 
     * @param listener
     *            The listener to remove.
     */
    public synchronized void removeKeyListener(
            java.awt.event.KeyListener listener) {
        listenerList.remove(java.awt.event.KeyListener.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireKeyListenerKeyTyped(java.awt.event.KeyEvent event) {
        if (listenerList == null)
            return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == java.awt.event.KeyListener.class) {
                ((java.awt.event.KeyListener) listeners[i + 1]).keyTyped(event);
            }
        }
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireKeyListenerKeyPressed(java.awt.event.KeyEvent event) {
        if (listenerList == null)
            return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == java.awt.event.KeyListener.class) {
                ((java.awt.event.KeyListener) listeners[i + 1])
                        .keyPressed(event);
            }
        }
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireKeyListenerKeyReleased(java.awt.event.KeyEvent event) {
        if (listenerList == null)
            return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == java.awt.event.KeyListener.class) {
                ((java.awt.event.KeyListener) listeners[i + 1])
                        .keyReleased(event);
            }
        }
    }

    /**
     * Enables or disables the component.
     * 
     * @param enabled
     *            true for enabling
     */
    public void setEnabled(boolean enabled) {
        monthpanel.setEnabled(enabled);
        monthscroller.setEnabled(enabled);
        yearscroller.setEnabled(enabled);
        super.setEnabled(enabled);
        repaint();
    }

    /**
     * Getter for enabled property.
     * 
     * @return true if it's enabled
     */
    public boolean isEnabled() {
        return monthpanel.isEnabled();
    }

    /**
     * Returns a Object collection with the selected dates.
     * 
     * @return selected dates or null
     */
    public Object[] getValues() {
        return dateSelectionModel.getSelectedDates();
    }

    /**
     * Sets the selection mode.
     * 
     * @param mode
     *            the new mode
     */
    public void setSelectionMode(int mode) {
        int old = dateSelectionModel.getSelectionMode();
        dateSelectionModel.setSelectionMode(mode);
        refreshSelection();
        firePropertyChange("selectionMode", old, mode);
    }

    /**
     * Returns the current selection mode.
     * 
     * @return selection mode
     */
    public int getSelectionMode() {
        return dateSelectionModel.getSelectionMode();
    }

    /**
     * Registers ActionListener to receive events.
     * 
     * @param listener
     *            The listener to register.
     */
    public synchronized void addActionListener(
            java.awt.event.ActionListener listener) {
        if (actionListenerList == null) {
            actionListenerList = new java.util.ArrayList();
        }
        actionListenerList.add(listener);
    }

    /**
     * Removes ActionListener from the list of listeners.
     * 
     * @param listener
     *            The listener to remove.
     */
    public synchronized void removeActionListener(
            java.awt.event.ActionListener listener) {
        if (actionListenerList != null) {
            actionListenerList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireActionListenerActionPerformed(
            java.awt.event.ActionEvent event) {
        java.util.ArrayList list;
        synchronized (this) {
            if (actionListenerList == null)
                return;
            list = (java.util.ArrayList) actionListenerList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((java.awt.event.ActionListener) list.get(i))
                    .actionPerformed(event);
        }
    }

    /**
     * Registers MonthChangeListener to receive events.
     * 
     * @param listener
     *            The listener to register.
     */
    public synchronized void addMonthChangeListener(
            net.sf.nachocalendar.event.MonthChangeListener listener) {
        if (listenerList == null) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(net.sf.nachocalendar.event.MonthChangeListener.class,
                listener);
    }

    /**
     * Removes MonthChangeListener from the list of listeners.
     * 
     * @param listener
     *            The listener to remove.
     */
    public synchronized void removeMonthChangeListener(
            net.sf.nachocalendar.event.MonthChangeListener listener) {
        listenerList.remove(
                net.sf.nachocalendar.event.MonthChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireMonthChangeListenerMonthIncreased(
            net.sf.nachocalendar.event.MonthChangeEvent event) {
        if (listenerList == null)
            return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == net.sf.nachocalendar.event.MonthChangeListener.class) {
                ((net.sf.nachocalendar.event.MonthChangeListener) listeners[i + 1])
                        .monthIncreased(event);
            }
        }
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireMonthChangeListenerMonthDecreased(
            net.sf.nachocalendar.event.MonthChangeEvent event) {
        if (listenerList == null)
            return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == net.sf.nachocalendar.event.MonthChangeListener.class) {
                ((net.sf.nachocalendar.event.MonthChangeListener) listeners[i + 1])
                        .monthDecreased(event);
            }
        }
    }

    private void refreshSelection() {
        DayPanel[] daypanels = monthpanel.getDaypanels();
        for (int i = 0; i < daypanels.length; i++) {
            if (!daypanels[i].isEnabled()) {
                daypanels[i].setSelected(false);
                continue;
            }
            daypanels[i].setSelected(dateSelectionModel
                    .isSelectedDate(daypanels[i].getDate()));
        }
    }

    /**
     * @return Returns the dateSelectionModel.
     */
    public DateSelectionModel getDateSelectionModel() {
        return dateSelectionModel;
    }
    
    /**
     * @param dateSelectionModel The dateSelectionModel to set.
     */
    public void setDateSelectionModel(DateSelectionModel dateSelectionModel) {
        if (dateSelectionModel != null) {
            this.dateSelectionModel = dateSelectionModel;
        }
    }
    
    /**
     * @return Returns the antiAliased.
     */
    public boolean isAntiAliased() {
        return antiAliased;
    }

    /**
     * @param antiAliased
     *            The antiAliased to set.
     */
    public void setAntiAliased(boolean antiAliased) {
        boolean old = this.antiAliased;
        this.antiAliased = antiAliased;
        monthpanel.setAntiAliased(antiAliased);
        firePropertyChange("antiAliased", old, antiAliased);
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
        monthpanel.setPrintMoon(printMoon);
        repaint();
        this.printMoon = printMoon;
    }
    
    /**
     * Sets the Today button visibility.
     * @param show
     */
    public void setShowToday(boolean show) {
        today.setVisible(show);
        repaint();
    }
    
    /**
     * Returns the Today button visibility.
     * @return
     */
    public boolean getShowToday() {
        return today.isVisible();
    }
    
    /**
     * Sets the today button text.
     * @param caption
     */
    public void setTodayCaption(String caption) {
        if (caption == null) {
            today.setText(CalendarUtils.getMessage("today"));
        } else today.setText(caption);
    }
    
    /**
     * Returns the today button text.
     * @return
     */
    public String getTodayCaption() {
        return today.getText();
    }
    
    
}
