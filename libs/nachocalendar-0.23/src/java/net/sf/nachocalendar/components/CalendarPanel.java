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
 *  2005-03-30   Added circular scroll
 *  2005-03-25   Added moon print
 *  2005-03-23   Centers the panel of today
 *  2005-01-08   Cleanups
 *  2005-01-02   Added property change support.
 *  2004-12-24   Reimplemented layout functions.
 *               Added keyboard navigation.
 *               Reimplemented selection model, now supports multiple selection   
 *  2004-12-12   Reimplemented the constructor.
 *  2004-10-22   setEnabled(boolean b) overriden, now works
 *  2004-10-17   Added keylistener support.
 *  2004-10-01   Checked with checkstyle
 *
 * -------
 *
 * CalendarPanel.java
 *
 * Created on August 12, 2004, 4:05 PM
 */

package net.sf.nachocalendar.components;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.nachocalendar.event.DateSelectionEvent;
import net.sf.nachocalendar.event.DateSelectionListener;
import net.sf.nachocalendar.model.DataModel;
import net.sf.nachocalendar.model.DateSelectionModel;
import net.sf.nachocalendar.model.DefaultDateSelectionModel;

/**
 * Panel used to show many months at once.
 * @author Ignacio Merani
 */
public class CalendarPanel extends JPanel implements ChangeListener {
    private boolean antiAliased;
    private KeyListener klistener;
    private MouseListener mlistener;
    private DateSelectionListener listlistener;
    private DateSelectionModel dateSelectionModel;
    private DataModel datamodel;
    private HeaderRenderer headerrenderer;
    private DayRenderer dayrenderer;
    private Calendar navigation, calendar;
    private Date minDate, maxDate;
    private int minimalDaysInFirstWeek;
    private boolean printMoon;
    protected boolean eternalScroll;
    private boolean showToday;
    private int middle; //used to display the panel in the middle!

    
    /** Array with the panels. */
    private MonthPanel[] months;
    
    /** Orientation. */
    private int orientation;
    
    /** Scroll Position. */
    private int scrollPosition;
    
    /**
     * Horizontal orientation.
     */
    public static final int HORIZONTAL = 0;
    
    /** Vertical orientation. */
    public static final int VERTICAL = 1;
    
    /** Left Position.*/
    public static final int LEFT = 0;
    
    /** Right Position. */
    public static final int RIGHT = 1;
    
    /** Up Position. */
    public static final int UP = 0;
    
    /** Down Position. */
    public static final int DOWN = 1;
    
    /** Scroll Bar. */
    private JScrollBar scroll;
    
    /** Year Scroller. */
    private YearScroller ys;
    
    /** Today Button */
    private JButton today;
    
    /** Array representing the working days. */
    private boolean[] workingdays = {false, true, true, true, true, true, true };
    
    /** Calendar used to calc dates. */
    private Calendar cal;
    
    private int showingyear, quantity;
    private Date date;
    private JPanel abajo;
    private boolean showWeekNumber;
    
    /** Utility field holding list of ChangeListeners. */
    private transient java.util.ArrayList changeListenerList;
    
    /** Holds value of property yearPosition.  */
    private int yearPosition;
    
    /** Default constructor, constructs a vertical panel with 3 months. */
    public CalendarPanel() {
        this(3, VERTICAL);
    }
    
    /**
     * Constructs a panel with 3 months and the provided orientation.
     * @param quantity quantity of months to show at once
     */
    public CalendarPanel(int quantity) {
        this(quantity, VERTICAL);
    }
    
    /**
     * Constructrs a panel with 3 months, Vertical.
     * 
     * @param showWeekNumbers
     */
    public CalendarPanel(boolean showWeekNumbers) {
        this(3, VERTICAL, showWeekNumbers);
    }
    
    /**
     * Constructs a panel with the provided quantity and orientation.
     * @param quantity months to show at once
     * @param orientation orientation
     */
    public CalendarPanel(int quantity, int orientation) {
        this(quantity, orientation, true);
    }
    
    //TODO: LeO: parameterized correctly
    public CalendarPanel(int quantity, int orientation, boolean showWeekNumber) {
      this(quantity, orientation, showWeekNumber, true);
    }
    
    /**
     * Creates a new instance of CalendarPanel.
     * @param showWeekNumber true to show the week numbers
     * @param quantity months to show at once
     * @param orientation the orientation
     */
    public CalendarPanel(int quantity, int orientation, boolean showWeekNumber, boolean eternalScroll) {
        if (quantity < 1) {
            quantity = 1;
        }
        if (quantity > 12) {
            quantity = 12;
        }
        this.quantity = quantity;
        this.showWeekNumber = showWeekNumber;
        this.orientation = orientation;
        navigation = new GregorianCalendar();
        calendar = new GregorianCalendar();
        dateSelectionModel = new DefaultDateSelectionModel();
        ys = new YearScroller();
        middle=quantity/2;
        this.eternalScroll = eternalScroll;
        initScroll();
        today = new JButton(CalendarUtils.getMessage("today"));
        today.setVisible(false);
        cal = new GregorianCalendar();
        setLayout(new BorderLayout());
        
        createListeners();
        dateSelectionModel.addDateSelectionListener(listlistener);
        setQuantity(quantity);
        
        if (orientation == VERTICAL) {
            layoutVertical();
        } else {
            layoutHorizontal();
        }
        
        initDisplayPanel();
        setValue(new Date());
        setRenderer(new DefaultDayRenderer());
        setHeaderRenderer(new DefaultHeaderRenderer());

    }

    /**
     * init the ScrollBar with the Parameters required!
     */
    private void initScroll() {
      int or = (orientation == HORIZONTAL ? Adjustable.HORIZONTAL : Adjustable.VERTICAL);
      scroll = new JScrollBar(or);
      scroll.setMaximum(17 - quantity);
      scroll.setBlockIncrement(3);
      scroll.setUnitIncrement(1);
      BoundedRangeModel model = new DefaultBoundedRangeModel() {
          public void setValue(int value) {
              super.setValue(value);
              if (eternalScroll) {
                  if (value < getMinimum()) {
                      goPreviousYear();
                  }
                  if (value > (getMaximum() - getExtent())) {
                      goNextYear();
                  }
              }
                  
          }
      };
      model.setMaximum(12 + 5  - quantity);
      model.setMinimum(0);
      model.setExtent(5);
      model.setValue(0);
      scroll.setModel(model);
    }
    
    private void goNextYear() {
        ys.setYear(ys.getYear() + 1);
        setShowingYear(ys.getYear());
        scroll.setValue(0);
    }
    
    private void goPreviousYear() {
        ys.setYear(ys.getYear() - 1);
        setShowingYear(ys.getYear());
        scroll.setValue(scroll.getMaximum() - scroll.getModel().getExtent());
    }

    /**
     * Displays the panel right after the start
     * 
     * @param quantity
     * @param eternalScroll
     */
    private void initDisplayPanel() {
      int displayRange= cal.get(Calendar.MONTH); //first we have to retrieve the month
      setShowingYear(cal.get(Calendar.YEAR)); //next we set the year (and change the cal)

        if (0 < displayRange  && (displayRange-middle) < 10){
          scroll.setValue(displayRange -middle);
        } else if (displayRange < quantity) {
          scroll.setValue(displayRange);
        } else {
          scroll.setValue(displayRange - quantity -middle);
        }
    }
    
    /** Utility method used during initialization. */
    private void createListeners() {
        scroll.addAdjustmentListener(new AdjustmentListener() {
          public void adjustmentValueChanged(AdjustmentEvent e) {
              setShowingMonth(e.getValue());
          }
        });
      
        ys.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setShowingYear(ys.getYear());
            }
        });
        
        today.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            cal.setTime(new Date());
            initDisplayPanel();
          }
        });
        
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!isEnabled()) return;
                int q = e.getWheelRotation();
                int value = scroll.getValue();
                value += q;
                if (value < 0) {
                    value = 0;
                    if (eternalScroll) {
                        goPreviousYear();
                        return;
                    }
                }
                if (value > 11) {
                    value = 11;
                    if (eternalScroll) {
                        goNextYear();
                        return;
                    }
                }
                scroll.setValue(value);
            }
        });
        
        // listener to pass events
        klistener = new KeyListener() {
            public void keyPressed(KeyEvent e) {
                boolean changed = false;
                int keycode = e.getKeyCode();
                navigation.setTime(calendar.getTime());
                if ((keycode == KeyEvent.VK_LEFT) || (keycode == 226)) {
                    navigation.add(Calendar.DAY_OF_YEAR, -1);
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_RIGHT) || (keycode == 227)) {
                    navigation.add(Calendar.DAY_OF_YEAR, 1);
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_UP) || (keycode == 224)) {
                    navigation.add(Calendar.DAY_OF_YEAR, -7);
                    changed = true;
                }

                if ((keycode == KeyEvent.VK_DOWN) || (keycode == 225)) {
                    navigation.add(Calendar.DAY_OF_YEAR, 7);
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_PAGE_UP)) {
                    navigation.add(Calendar.MONTH, -1);
                    scroll.setValue(navigation.get(Calendar.MONTH));
                    changed = true;
                }
                if ((keycode == KeyEvent.VK_PAGE_DOWN)) {
                    navigation.add(Calendar.MONTH, 1);
                    scroll.setValue(navigation.get(Calendar.MONTH));
                    changed = true;
                }
                if (changed) {
                    if (!isShowing(navigation.getTime())) {
                        showMonth(navigation.getTime());
                    }
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
                    refreshSelection();
                    repaint();
                    //monthpanel.repaint();
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
        

        /*monthlistener = new MonthChangeListener() {
            public void monthIncreased(MonthChangeEvent e) {
                setDate(e.getDate());
            }
            
            public void monthDecreased(MonthChangeEvent e) {
                setDate(e.getDate());
            }
        };*/
                
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
                /*fireActionListenerActionPerformed(new ActionEvent(this, 0,
                        "clicked"));*/
                dateSelectionModel.setValueIsAdjusting(false);
                refreshSelection();
                repaint();
            }
        };
        
        // listener para la seleccion
        listlistener = new DateSelectionListener() {
            public void valueChanged(DateSelectionEvent e) {
                for (int i=0; i < months.length; i++) {
                    DayPanel[] daypanels = months[i].getDaypanels();
                    for (int j=0; j < daypanels.length; j++) {
                        if (dateSelectionModel.isSelectedDate(daypanels[j].getDate())) {
                            daypanels[j].setSelected(true);
                        } else daypanels[j].setSelected(false);
                    }
                }
                repaint();
                fireChangeListenerStateChanged(new ChangeEvent(CalendarPanel.this));
            }
        };
    }
    
    /** 
     * Changes the orientation (horizontal or vertical).
     * @param orientation the new orientation
     */
    public void setOrientation(int orientation) {
        if (orientation == this.orientation) {
            return;
        }
        int old = this.orientation;
        this.orientation = orientation;
        removeAll();
        if (orientation == VERTICAL) {
            layoutVertical();
        } else {
            layoutHorizontal();
        }
        firePropertyChange("orientation", old, orientation);
    }
    
    /** Method used to layout vertical. */
    private void layoutVertical() {
        scroll.setOrientation(Adjustable.VERTICAL);
        JPanel centro = new JPanel(new GridLayout(months.length, 1));
        for (int i = 0; i < months.length; i++) {
            centro.add(months[i]);
        }
        add(centro, BorderLayout.CENTER);
        layoutScrollAndYear();
    }
    
    /** Method used to layout horizontal. */
    private void layoutHorizontal() {
        scroll.setOrientation(Adjustable.HORIZONTAL);
        JPanel centro = new JPanel(new GridLayout(1, months.length));
        for (int i = 0; i < months.length; i++) {
            centro.add(months[i]);
        }
        add(centro, BorderLayout.CENTER);
        layoutScrollAndYear();
    }
    
    /** 
     * Registers ChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(javax.swing.event.ChangeListener listener) {
        if (listener == null) return;
        if (changeListenerList == null) {
            changeListenerList = new java.util.ArrayList();
        }
        changeListenerList.add(listener);
    }
    
    /** 
     * Removes ChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(javax.swing.event.ChangeListener listener) {
        if (changeListenerList != null) {
            changeListenerList.remove(listener);
        }
    }
    
    /** 
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void fireChangeListenerStateChanged(javax.swing.event.ChangeEvent event) {
        java.util.ArrayList list;
        synchronized (this) {
            if (changeListenerList == null) {
                return;
            }
            list = (java.util.ArrayList) changeListenerList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((javax.swing.event.ChangeListener) list.get(i)).stateChanged(event);
        }
    }
    
    /** 
     * Event fired when the selected day changes.
     * @param e event fired
     */
    public void stateChanged(ChangeEvent e) {
        fireChangeListenerStateChanged(e);
    }
    
    /** 
     * Sets the month shown.
     * @param month month to show
     */
    private void setShowingMonth(int month) {
        Calendar cal = new GregorianCalendar(showingyear, month, 1);
        for (int i = 0; i < months.length; i++) {
            //cal.setTime(months[i].getMonth());
            //cal.add(Calendar.MONTH, cal.get(Calendar.MONTH) - month + i);
            months[i].setMonth(cal.getTime());
            /*months[i].setSelectionOffset((month + i) * 42);
            months[i].setSelectionOffset(cal.get(Calendar.MONTH));*/
            cal.add(Calendar.MONTH, 1);
        }
        minDate = months[0].getMinDate();
        maxDate = months[months.length - 1].getMaxDate();
        refreshSelection();
    }
    
    /** 
     * Sets the year shown.
     * @param year the year to show
     */
    private void setShowingYear(int year) {
        showingyear = year;
        for (int i = 0; i < months.length; i++) {
            cal.setTime(months[i].getMonth());
            cal.set(Calendar.YEAR, year);
            months[i].setMonth(cal.getTime());
        }
        refreshSelection();
    }
    
    /** 
     * Getter for property date.
     * @return Value of property date.
     */
    public Date getDate() {
        return date;
    }
    
    /** 
     * Setter for property date.
     * @param date New value of property date.
     */
    public void setDate(Date date) {
        if (date == null) return;
        this.date = date;
        cal.setTime(date);
        if (!isShowing(date)) {
            showMonth(date);
        }
        dateSelectionModel.setSelectedDate(date);
        refreshSelection();
        repaint();
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
        boolean[] old = this.workingdays;
        this.workingdays = workingdays;
        for (int i = 0; i < months.length; i++) {
            months[i].setWorkingdays(workingdays);
        }
        firePropertyChange("workingDays", old, workingdays);
    }
    
    /** 
     * Getter for property renderer.
     * @return Value of property renderer.
     */
    public DayRenderer getRenderer() {
        return months[0].getRenderer();
    }
    
    /** 
     * Setter for property renderer.
     * @param renderer New value of property renderer.
     */
    public void setRenderer(DayRenderer renderer) {
        dayrenderer = renderer;
        for (int i = 0; i < months.length; i++) {
            months[i].setRenderer(renderer);
        }
    }
    
    /** 
     * Getter for property model.
     * @return Value of property model.
     */
    public DataModel getModel() {
        return months[0].getModel();
    }
    
    /** 
     * Setter for property model.
     * @param model New value of property model.
     */
    public void setModel(DataModel model) {
        datamodel = model;
        for (int i = 0; i < months.length; i++) {
            months[i].setModel(model);
        }
    }
    
    /** 
     * Getter for property firstDayOfWeek.
     * @return Value of property firstDayOfWeek.
     */
    public int getFirstDayOfWeek() {
        return months[0].getFirstDayOfWeek();
    }
    
    /** 
     * Setter for property firstDayOfWeek.
     * @param firstDayOfWeek New value of property firstDayOfWeek.
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if ((firstDayOfWeek == Calendar.SUNDAY) || (firstDayOfWeek == Calendar.MONDAY)) {
            int old = months[0].getFirstDayOfWeek();
            if (firstDayOfWeek == months[0].getFirstDayOfWeek()) return;
            for (int i = 0; i < months.length; i++) {
                months[i].setFirstDayOfWeek(firstDayOfWeek);
            }
            refreshSelection();
            repaint();
            firePropertyChange("firstDayOfWeek", old, firstDayOfWeek);
        }
    }
    
    /** Refreshes the display of this month. */
    public void refresh() {
        for (int i = 0; i < months.length; i++) {
            months[i].refresh();
        }
    }
    
    /** 
     * Getter for property headerRenderer.
     * @return Value of property headerRenderer.
     */
    public HeaderRenderer getHeaderRenderer() {
        return months[0].getHeaderRenderer();
    }
    
    /** 
     * Setter for property headerRenderer.
     * @param headerRenderer New value of property headerRenderer.
     */
    public void setHeaderRenderer(HeaderRenderer headerRenderer) {
        headerrenderer = headerRenderer;
        for (int i = 0; i < months.length; i++) {
            months[i].setHeaderRenderer(headerRenderer);
        }
    }
    
    /** 
     * Getter for property orientation.
     * @return Value of property orientation.
     */
    public int getOrientation() {
        return orientation;
    }
    
    /** 
     * Getter for property scrollPosition.
     * @return Value of property scrollPosition.
     */
    public int getScrollPosition() {
        return scrollPosition;
    }
    
    /** 
     * Setter for property scrollPosition.
     * @param scrollPosition New value of property scrollPosition.
     */
    public void setScrollPosition(int scrollPosition) {
        if (scrollPosition == this.scrollPosition) {
            return;
        }
        int old = this.scrollPosition;
        this.scrollPosition = scrollPosition;
        if (orientation == HORIZONTAL) {
            remove(abajo);
        } else {
            remove(ys);
            remove(scroll);
        }
        layoutScrollAndYear();
        firePropertyChange("scrollPosition", old, scrollPosition);
    }
    
    /** 
     * Getter for property yearPosition.
     * @return Value of property yearPosition.
     */
    public int getYearPosition() {
        return this.yearPosition;
    }
    
    /** 
     * Setter for property yearPosition.
     * @param yearPosition New value of property yearPosition.
     */
    public void setYearPosition(int yearPosition) {
        if (yearPosition == this.yearPosition) {
            return;
        }
        int old = this.yearPosition;
        this.yearPosition = yearPosition;
        if (orientation == HORIZONTAL) {
            remove(abajo);
        } else {
            remove(ys);
            remove(scroll);
        }
        layoutScrollAndYear();
        firePropertyChange("yearPosition", old, yearPosition);
    }
    
    /** Utility method to layout the scrolls. */
    private void layoutScrollAndYear() {
        if (orientation == VERTICAL) {
            if (yearPosition == UP) {
                add(ys, BorderLayout.NORTH);
                add(today, BorderLayout.SOUTH);
            } else {
                add(ys, BorderLayout.SOUTH);
                add(today, BorderLayout.NORTH);
            }
            
            if (scrollPosition == LEFT) {
                add(scroll, BorderLayout.WEST);
            } else {
                add(scroll, BorderLayout.EAST);
            }
        } else {
            abajo = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weighty = 1;
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.BOTH;
            
            if (yearPosition == LEFT) {
                gbc.weightx = 0;
                abajo.add(ys, gbc);
                gbc.weightx = 1;
                abajo.add(scroll, gbc);
                gbc.weightx = 0;
                abajo.add(today, gbc);
            } else {
                gbc.weightx = 0;
                abajo.add(today, gbc);
                gbc.weightx = 1;
                abajo.add(scroll, gbc);
                gbc.weightx = 0;
                abajo.add(ys, gbc);
            }
            
            if (scrollPosition == UP) {
                add(abajo, BorderLayout.NORTH);
            } else {
                add(abajo, BorderLayout.SOUTH);
            }
            
        }
    }
    
    /** 
     * Getter for property quantity.
     * @return Value of property quantity.
     */
    public int getQuantity() {
        return months.length;
    }
    
    /** 
     * Setter for property quantity.
     * @param quantity New value of property quantity.
     */
    public void setQuantity(int quantity) {
        if (quantity < 1) {
            quantity = 1;
        }
        if (quantity > 12) {
            quantity = 12;
        }
        if ((months != null) && (months.length == quantity)) {
            return;
        }
        int old = this.quantity;
        months = new MonthPanel[quantity];
        for (int i = 0; i < months.length; i++) {
            months[i] = new MonthPanel(showWeekNumber);
            months[i].showTitle(true);
            months[i].setModel(datamodel);
            months[i].setRenderer(dayrenderer);
            months[i].setHeaderRenderer(headerrenderer);
            months[i].setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
            months[i].setDay(new Date());
            DayPanel[] daypanels = months[i].getDaypanels();
            for (int j=0; j < daypanels.length; j++) {
                daypanels[j].addKeyListener(klistener);
                daypanels[j].addMouseListener(mlistener);
            }
        }
        int oldor = orientation;
        orientation = -1;
        setOrientation(oldor);
        this.quantity = quantity;
        scroll.setMaximum(12 + 5  - quantity);
        if (old != 0) firePropertyChange("quantity", old, quantity);
    }
    
    /**
     * Registers KeyListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addKeyListener(java.awt.event.KeyListener listener) {
        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(java.awt.event.KeyListener.class, listener);
    }
    
    /**
     * Removes KeyListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeKeyListener(java.awt.event.KeyListener listener) {
        listenerList.remove(java.awt.event.KeyListener.class, listener);
    }
    
    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void fireKeyListenerKeyTyped(java.awt.event.KeyEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==java.awt.event.KeyListener.class) {
                ((java.awt.event.KeyListener)listeners[i+1]).keyTyped(event);
            }
        }
    }
    
    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void fireKeyListenerKeyPressed(java.awt.event.KeyEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==java.awt.event.KeyListener.class) {
                ((java.awt.event.KeyListener)listeners[i+1]).keyPressed(event);
            }
        }
    }
    
    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void fireKeyListenerKeyReleased(java.awt.event.KeyEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==java.awt.event.KeyListener.class) {
                ((java.awt.event.KeyListener)listeners[i+1]).keyReleased(event);
            }
        }
    }
    
    /**
     * Enables or disables the component
     * @param enabled true for enabling
     */
    public void setEnabled(boolean enabled) {
        for (int i=0; i < months.length; i++) {
            months[i].setEnabled(enabled);
        }
        scroll.setEnabled(enabled);
        ys.setEnabled(enabled);
        repaint();
       // this.enabled = enabled;
        super.setEnabled(enabled);
    }
    
    /**
     * Getter for enabled property
     * @return true if it's enabled
     */
    public boolean isEnabled() {
        return scroll.isEnabled();
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
        boolean old = this.antiAliased;
        this.antiAliased = antiAliased;
        for (int i=0; i < months.length; i++) {
            months[i].setAntiAliased(antiAliased);
        }
        firePropertyChange("antiAliased", old, antiAliased);
    }
    /**
     * @return Returns the selectionMode.
     */
    public int getSelectionMode() {
        return dateSelectionModel.getSelectionMode();
    }
    /**
     * @param selectionMode The selectionMode to set.
     */
    public void setSelectionMode(int selectionMode) {
        int old = dateSelectionModel.getSelectionMode();
        dateSelectionModel.setSelectionMode(selectionMode);
        refreshSelection();
        firePropertyChange("selectionMode", old, selectionMode);
    }
    
    private void refreshSelection() {
        for (int i=0; i < months.length; i++) {
            DayPanel[] daypanels = months[i].getDaypanels();
            for (int j=0; j < daypanels.length; j++) {
                if (!daypanels[j].isEnabled()) {
                    daypanels[j].setSelected(false);
                    continue;
                }
                daypanels[j].setSelected(dateSelectionModel.isSelectedDate(daypanels[j].getDate()));
            }
        }
    }
    
    private boolean isShowing(Date date) {
        //if (minDate == null) return false;
        if (date.before(minDate)) return false;
        //if (maxDate == null) return false;
        if (date.after(maxDate)) return false;
        return true;
    }
    
    private void showMonth(Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        if (ys.getYear() != cal.get(Calendar.YEAR)) {
            ys.setYear(cal.get(Calendar.YEAR));
            setShowingYear(ys.getYear());
        }
        if (isShowing(d)) return;
        
        int month = cal.get(Calendar.MONTH);
        int middle = (quantity / 2);
        int show = 0;
        if (month < scroll.getValue()) {
            show = month;
        } else {
            show = month - quantity + 1;
        }
        if (show < 0) {
            show = 0;
        }
        if (show > (11 - quantity + middle)) {
            show = (11 - quantity + middle);
        }
        scroll.setValue(show);
    }
    
    /** Returns the selected date.
     * @return Selected Date
     */
    public Object getValue() {
        return dateSelectionModel.getSelectedDate();
    }
    
    /** Return the selected dates as an Array.
     * @return Selected Dates
     */
    public Object[] getValues() {
        return dateSelectionModel.getSelectedDates();
    }
    
    /** Sets the selected date.
     * @param date Date to select
     */
    public void setValue(Object date) {
        try {
            setDate(CalendarUtils.convertToDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    /** Set the selected dates.
     * @param dates Array with the Dates
     */
    public void setValues(Object[] dates) {
        dateSelectionModel.setSelectedDates(dates);
        refreshSelection();
        repaint();
        showMonth((Date) dateSelectionModel.getSelectedDates()[0]);
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
            this.dateSelectionModel.removeDateSelectionListener(listlistener);
            this.dateSelectionModel = dateSelectionModel;
            dateSelectionModel.addDateSelectionListener(listlistener);
        }
    }
    
    /**
     * Specifies how many days should the first week of the year contain <br>
     * If not specified, i.e. 0, then no changes from the defaults are done
     * 
     * @param number
     */
    public void  setMinimalDaysInFirstWeek(int number) {
      if (months[0].getMinimalDaysInFirstWeek() != number) {
        for (int i = 0; i < months.length; i++) {
          months[i].setMinimalDaysInFirstWeek (number);
        }
        refreshSelection();
        repaint();
        firePropertyChange("minimalDaysInFirstWeek", minimalDaysInFirstWeek, number);
        minimalDaysInFirstWeek = number;
      }
    }
    
    /**
     * @return how many weekdays does the first week of the year have
     */
    public int getMinimalDaysInFirstWeek() {
      return months[0].getMinimalDaysInFirstWeek();
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
        for (int i = 0; i < months.length; i++) {
          months[i].setPrintMoon(printMoon);
        }
        refreshSelection();
        repaint();
        firePropertyChange("printMoon", printMoon, !printMoon);
      }
    }

    
    public void setTodayCaption(String caption) {
        if (caption == null) {
            today.setText(CalendarUtils.getMessage("today"));
        } else {
            today.setText(caption);
        }
    }
    
    
    /**
     * @return Returns the eternalScroll.
     */
    public boolean isEternalScroll() {
        return eternalScroll;
    }
    /**
     * @param eternalScroll The eternalScroll to set.
     */
    public void setEternalScroll(boolean eternalScroll) {
        this.eternalScroll = eternalScroll;
    }
    /**
     * @return Returns the showToday.
     */
    public boolean isShowToday() {
        return showToday;
    }
    /**
     * @param showToday The showToday to set.
     */
    public void setShowToday(boolean showToday) {
        today.setVisible(showToday);
        repaint();
        this.showToday = showToday;
    }
    
    
}
