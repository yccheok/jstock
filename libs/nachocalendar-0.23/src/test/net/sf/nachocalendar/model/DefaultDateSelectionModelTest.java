/*
 * Created on Jan 8, 2005
 * 
 * Project: NachoCalendar
 * 
 * DefaultDateSelectionModelTest.java
 */
package net.sf.nachocalendar.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

/**
 * @author Ignacio Merani
 *
 * 
 */
public class DefaultDateSelectionModelTest extends TestCase {
    private DateSelectionModel model;
    
    protected void setUp() throws Exception {
        model = new DefaultDateSelectionModel();
    }
    
    protected void tearDown() throws Exception {
        model = null;
    }
    
    public void testAddSelectionIntervalMultiple() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d3 = cal.getTime();
        model.addSelectionInterval(d1, d3);
        assertTrue(model.isSelectedDate(d1));
        assertTrue(model.isSelectedDate(d2));
        assertTrue(model.isSelectedDate(d3));
    }

    public void testAddSelectionIntervalSinglei() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d3 = cal.getTime();
        model.setSelectionMode(DateSelectionModel.SINGLE_INTERVAL_SELECTION);
        model.addSelectionInterval(d1, d3);
        assertTrue(model.isSelectedDate(d1));
        assertTrue(model.isSelectedDate(d2));
        assertTrue(model.isSelectedDate(d3));
    }
    
    public void testAddSelectionIntervalSingle() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d3 = cal.getTime();
        model.setSelectionMode(DateSelectionModel.SINGLE_SELECTION);
        model.addSelectionInterval(d1, d3);
        assertTrue(!model.isSelectedDate(d1));
        assertTrue(!model.isSelectedDate(d2));
        assertTrue(model.isSelectedDate(d3));
    }
    
    public void testRemoveSelectionInterval() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date d3 = cal.getTime();
        model.addSelectionInterval(d1, d3);
        model.removeSelectionInterval(d1, d2);
        assertTrue(!model.isSelectedDate(d1));
        assertTrue(!model.isSelectedDate(d2));
        assertTrue(model.isSelectedDate(d3));
    }


    public void testSetSelectedDate() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        model.setSelectedDate(d1);
        assertTrue(model.isSelectedDate(d1));
    }

    public void testSetSelectedDates() {
        Calendar cal = new GregorianCalendar();
        Object[] o = new Object[10];
        for (int i=0; i < o.length; i++) {
            o[i] = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        model.setSelectedDates(o);
        
        for (int i=0; i < o.length ; i++) {
            assertTrue(model.isSelectedDate((Date) o[i]));
        }
    }

}
