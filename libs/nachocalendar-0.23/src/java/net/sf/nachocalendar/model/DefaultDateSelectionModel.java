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
 * 2005-06-18   Implemented valueAdjusting methods
 * 2005-01-08   Cleanups
 * 
 * -------
 *
 * DefaultDateSelectionModel.java
 * 
 * Created on Dec 24, 2004
 * 
 */
package net.sf.nachocalendar.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.sf.nachocalendar.components.CalendarUtils;
import net.sf.nachocalendar.event.DateSelectionEvent;
import net.sf.nachocalendar.event.DateSelectionListener;

/**
 * 
 * Default implementation for {@link DateSelectionModel} interface.
 * @author Ignacio Merani
 * 
 *  
 */
public class DefaultDateSelectionModel implements DateSelectionModel {
    private DateSelectionModel model;
    private boolean isAdjusting, pendingEvent;

    /**
     *  Default constructor.
     */
    public DefaultDateSelectionModel() {
        model = new MultipleInterval();
    }

    /**
     * Utility field used by event firing mechanism.
     */
    private javax.swing.event.EventListenerList listenerList = null;

    /**
     * @see net.sf.nachocalendar.model.DateSelectionModel#addSelectionInterval(java.util.Date,
     *      java.util.Date)
     */
    public void addSelectionInterval(Date from, Date to) {
        model.addSelectionInterval(from, to);
        fireDateSelectionListenerValueChanged(new DateSelectionEvent(this));
    }

    /**
     * @see net.sf.nachocalendar.model.DateSelectionModel#clearSelection()
     */
    public void clearSelection() {
        model.clearSelection();
        fireDateSelectionListenerValueChanged(new DateSelectionEvent(this));
    }

    /**
     * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectedDate(java.util.Date)
     */
    public boolean isSelectedDate(Date date) {
        return model.isSelectedDate(date);
    }

    /**
     * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectionEmpty()
     */
    public boolean isSelectionEmpty() {
        return model.isSelectionEmpty();
    }

    /**
     * @see net.sf.nachocalendar.model.DateSelectionModel#removeSelectionInterval(java.util.Date,
     *      java.util.Date)
     */
    public void removeSelectionInterval(Date from, Date to) {
        model.removeSelectionInterval(from, to);
        fireDateSelectionListenerValueChanged(new DateSelectionEvent(this));
    }

    /**
     * Registers DateSelectionListener to receive events.
     * 
     * @param listener
     *            The listener to register.
     */
    public synchronized void addDateSelectionListener(
            net.sf.nachocalendar.event.DateSelectionListener listener) {
        if (listenerList == null) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(
                net.sf.nachocalendar.event.DateSelectionListener.class,
                listener);
    }

    /**
     * Removes DateSelectionListener from the list of listeners.
     * 
     * @param listener
     *            The listener to remove.
     */
    public synchronized void removeDateSelectionListener(
            net.sf.nachocalendar.event.DateSelectionListener listener) {
        listenerList.remove(
                net.sf.nachocalendar.event.DateSelectionListener.class,
                listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event
     *            The event to be fired
     */
    private void fireDateSelectionListenerValueChanged(
            net.sf.nachocalendar.event.DateSelectionEvent event) {
        if (isAdjusting) {
            pendingEvent = true;
            return;
        }
        if (listenerList == null)
            return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == net.sf.nachocalendar.event.DateSelectionListener.class) {
                ((net.sf.nachocalendar.event.DateSelectionListener) listeners[i + 1])
                        .valueChanged(event);
            }
        }
    }

    /**
     * @return Returns the selectionMode.
     */
    public int getSelectionMode() {
        return model.getSelectionMode();
    }

    /**
     * @param selectionMode
     *            The selectionMode to set.
     */
    public void setSelectionMode(int selectionMode) {
        switch (selectionMode) {
        case SINGLE_SELECTION:
            model = new SingleSelection();
            break;
        case SINGLE_INTERVAL_SELECTION:
            model = new SingleInterval();
            break;
        case MULTIPLE_INTERVAL_SELECTION:
        default:
            model = new MultipleInterval();
            break;
        }
    }

    /**
     *
     * 
     * @see net.sf.nachocalendar.model.DateSelectionModel#getLeadSelectionDate()
     */
    public Date getLeadSelectionDate() {
        return model.getLeadSelectionDate();
    }

    /**
     *
     * 
     * @see net.sf.nachocalendar.model.DateSelectionModel#setLeadSelectionDate(java.util.Date)
     */
    public void setLeadSelectionDate(Date date) {
        model.setLeadSelectionDate(date);
    }

    /**
     *
     * 
     * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDate()
     */
    public Object getSelectedDate() {
        return model.getSelectedDate();
    }

    /**
     *
     * 
     * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDates()
     */
    public Object[] getSelectedDates() {
        return model.getSelectedDates();
    }

    /**
     *
     * 
     * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDate(java.lang.Object)
     */
    public void setSelectedDate(Object date) {
        model.setSelectedDate(date);
        fireDateSelectionListenerValueChanged(new DateSelectionEvent(this));
    }

    /**
     * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDates(java.lang.Object[])
     */
    public void setSelectedDates(Object[] dates) {
        model.setSelectedDates(dates);
        fireDateSelectionListenerValueChanged(new DateSelectionEvent(this));
    }
    
    private static Object[] getDates(Date from, Date to) {
        List retorno = new ArrayList();
        Calendar cal = new GregorianCalendar();
        cal.setTime(from);
        while ((to.after(cal.getTime())) || (CalendarUtils.isSameDay(to, cal.getTime()))) {
            retorno.add(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Collections.sort(retorno);
        return retorno.toArray();
    }
    
    /* (non-Javadoc)
     * @see net.sf.nachocalendar.model.DateSelectionModel#setValueIsAdjusting(boolean)
     */
    public void setValueIsAdjusting(boolean b) {
        isAdjusting = b;
        if (!b && pendingEvent) {
            fireDateSelectionListenerValueChanged(new DateSelectionEvent(this));
            pendingEvent = false;
        }
    }

    /* (non-Javadoc)
     * @see net.sf.nachocalendar.model.DateSelectionModel#getValueIsAdjusting()
     */
    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }
    

    private static class SingleSelection implements DateSelectionModel {
        private Date selection;

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#addSelectionInterval(java.util.Date,
         *      java.util.Date)
         */
        public void addSelectionInterval(Date from, Date to) {
            selection = to;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#clearSelection()
         */
        public void clearSelection() {
            selection = null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectedDate(java.util.Date)
         */
        public boolean isSelectedDate(Date date) {
            if (selection == null)
                return false;
            if (date == null)
                return false;
            return CalendarUtils.isSameDay(date, selection);
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectionEmpty()
         */
        public boolean isSelectionEmpty() {
            return (selection == null);
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#removeSelectionInterval(java.util.Date,
         *      java.util.Date)
         */
        public void removeSelectionInterval(Date from, Date to) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#addDateSelectionListener(net.sf.nachocalendar.event.DateSelectionListener)
         */
        public void addDateSelectionListener(DateSelectionListener listener) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#removeDateSelectionListener(net.sf.nachocalendar.event.DateSelectionListener)
         */
        public void removeDateSelectionListener(DateSelectionListener listener) {
        }

        /**
         * @return
         */
        public int getSelectionMode() {
            return SINGLE_SELECTION;
        }

        /**
         * @param selectionMode
         */
        public void setSelectionMode(int selectionMode) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getLeadSelectionDate()
         */
        public Date getLeadSelectionDate() {
            return selection;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setLeadSelectionDate(java.util.Date)
         */
        public void setLeadSelectionDate(Date date) {
            selection = date;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDate()
         */
        public Object getSelectedDate() {
            return selection;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDates()
         */
        public Object[] getSelectedDates() {
            return new Object[] {selection};
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDate(java.lang.Object)
         */
        public void setSelectedDate(Object date) {
            if (date == null)
                return;
            try {
                selection = CalendarUtils.convertToDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDates(java.lang.Object[])
         */
        public void setSelectedDates(Object[] dates) {
            if (dates == null)
                return;
            if (dates.length == 0) {
                selection = null;
                return;
            }
            try {
                selection = CalendarUtils.convertToDate(dates[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /* (non-Javadoc)
         * @see net.sf.nachocalendar.model.DateSelectionModel#setValueIsAdjusting(boolean)
         */
        public void setValueIsAdjusting(boolean b) {
            // empty method
        }

        /* (non-Javadoc)
         * @see net.sf.nachocalendar.model.DateSelectionModel#getValueIsAdjusting()
         */
        public boolean getValueIsAdjusting() {
            // empty method
            return false;
        }

    }

    private static class SingleInterval implements DateSelectionModel {
        private Date from, to, lead;

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#addSelectionInterval(java.util.Date,
         *      java.util.Date)
         */
        public void addSelectionInterval(Date from, Date to) {
            if ((from == null) || (to == null)) {
                this.from = null;
                this.to = null;
                return;
            }
            if (from.after(to)) {
                this.from = to;
                this.to = from;
            } else {
                this.from = from;
                this.to = to;
            }
            lead = to;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#clearSelection()
         */
        public void clearSelection() {
            from = null;
            to = null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectedDate(java.util.Date)
         */
        public boolean isSelectedDate(Date date) {
            if ((from == null) || (to == null)) return false;
            if (date == null) return false;
            if (CalendarUtils.isSameDay(date,from) || CalendarUtils.isSameDay(date, to)) return true;
            if ((date.before(from)) || date.after(to))
                return false;
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectionEmpty()
         */
        public boolean isSelectionEmpty() {
            return ((from == null) || (to == null));
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#removeSelectionInterval(java.util.Date,
         *      java.util.Date)
         */
        public void removeSelectionInterval(Date from, Date to) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#addDateSelectionListener(net.sf.nachocalendar.event.DateSelectionListener)
         */
        public void addDateSelectionListener(DateSelectionListener listener) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#removeDateSelectionListener(net.sf.nachocalendar.event.DateSelectionListener)
         */
        public void removeDateSelectionListener(DateSelectionListener listener) {
        }

        /**
         * @return
         */
        public int getSelectionMode() {
            return SINGLE_INTERVAL_SELECTION;
        }

        /**
         * @param selectionMode
         */
        public void setSelectionMode(int selectionMode) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getLeadSelectionDate()
         */
        public Date getLeadSelectionDate() {
            return lead;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setLeadSelectionDate(java.util.Date)
         */
        public void setLeadSelectionDate(Date date) {
            lead = date;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDate()
         */
        public Object getSelectedDate() {
            return lead;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDates()
         */
        public Object[] getSelectedDates() {
            return getDates(from, to);
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDate(java.lang.Object)
         */
        public void setSelectedDate(Object date) {
            try {
                from = CalendarUtils.convertToDate(date);
                to = from;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDates(java.lang.Object[])
         */
        public void setSelectedDates(Object[] dates) {
            if ((dates == null) || (dates.length < 1)) {
                from = null;
                to = null;
                return;
            }
            try {
                from = CalendarUtils.convertToDate(dates[0]);
                to = CalendarUtils.convertToDate(dates[dates.length - 1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        /* (non-Javadoc)
         * @see net.sf.nachocalendar.model.DateSelectionModel#setValueIsAdjusting(boolean)
         */
        public void setValueIsAdjusting(boolean b) {
            // empty method
        }

        /* (non-Javadoc)
         * @see net.sf.nachocalendar.model.DateSelectionModel#getValueIsAdjusting()
         */
        public boolean getValueIsAdjusting() {
            // empty method
            return false;
        }
    }

    private static class MultipleInterval implements DateSelectionModel {
        private List selection;

        private Calendar calendar;

        private Date leadSelection;

        MultipleInterval() {
            selection = new ArrayList();
            calendar = new GregorianCalendar();
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#addSelectionInterval(java.util.Date,
         *      java.util.Date)
         */
        public void addSelectionInterval(Date from, Date to) {
            if ((from == null) || (to == null))
                return;
            Date d1 = null;
            Date d2 = null;
            if (from.before(to)) {
                d1 = from;
                d2 = to;
            } else {
                d1 = to;
                d2 = from;
            }

            calendar.setTime(d1);

            do {
                selection.add(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            } while ((d2.after(calendar.getTime()))
                    || (CalendarUtils.isSameDay(d2, calendar.getTime())));

            leadSelection = to;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#clearSelection()
         */
        public void clearSelection() {
            selection.clear();
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectedDate(java.util.Date)
         */
        public boolean isSelectedDate(Date date) {
            if (selection.isEmpty())
                return false;
            if (date == null)
                return false;
            Iterator it = selection.iterator();
            while (it.hasNext()) {
                Date d = (Date) it.next();
                if (CalendarUtils.isSameDay(d, date))
                    return true;
            }
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#isSelectionEmpty()
         */
        public boolean isSelectionEmpty() {
            return selection.isEmpty();
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#removeSelectionInterval(java.util.Date,
         *      java.util.Date)
         */
        public void removeSelectionInterval(Date from, Date to) {
            if ((from == null) || (to == null))
                return;
            if ((from == null) || (to == null))
                return;

            Object[] dates = getDates(from, to);
            
            for (int i = 0; i < dates.length; i++) {
                Iterator it = selection.iterator();
                Date d = (Date) dates[i];
                while (it.hasNext()) {
                    Date dd = (Date) it.next();
                    if (CalendarUtils.isSameDay(d, dd)) {
                        selection.remove(dd);
                        break;
                    }
                }
            }
            leadSelection = to;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#addDateSelectionListener(net.sf.nachocalendar.event.DateSelectionListener)
         */
        public void addDateSelectionListener(DateSelectionListener listener) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#removeDateSelectionListener(net.sf.nachocalendar.event.DateSelectionListener)
         */
        public void removeDateSelectionListener(DateSelectionListener listener) {
        }

        /**
         * @return
         */
        public int getSelectionMode() {
            return MULTIPLE_INTERVAL_SELECTION;
        }

        /**
         * @param selectionMode
         */
        public void setSelectionMode(int selectionMode) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getLeadSelectionDate()
         */
        public Date getLeadSelectionDate() {
            return leadSelection;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setLeadSelectionDate(java.util.Date)
         */
        public void setLeadSelectionDate(Date date) {
            leadSelection = date;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDate()
         */
        public Object getSelectedDate() {
            if (selection.isEmpty())
                return null;
            return leadSelection;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#getSelectedDates()
         */
        public Object[] getSelectedDates() {
            Collections.sort(selection);
            return selection.toArray();
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDate(java.lang.Object)
         */
        public void setSelectedDate(Object date) {
            selection.clear();
            if (date != null) {
                try {
                    selection.add(CalendarUtils.convertToDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sf.nachocalendar.model.DateSelectionModel#setSelectedDates(java.lang.Object[])
         */
        public void setSelectedDates(Object[] dates) {
            selection.clear();
            if ((dates == null) || (dates.length < 1))
                return;
            for (int i = 0; i < dates.length; i++) {
                try {
                    selection.add(CalendarUtils.convertToDate(dates[i]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        /* (non-Javadoc)
         * @see net.sf.nachocalendar.model.DateSelectionModel#setValueIsAdjusting(boolean)
         */
        public void setValueIsAdjusting(boolean b) {
            // empty method
        }

        /* (non-Javadoc)
         * @see net.sf.nachocalendar.model.DateSelectionModel#getValueIsAdjusting()
         */
        public boolean getValueIsAdjusting() {
            // empty method
            return false;
        }
    }

}
