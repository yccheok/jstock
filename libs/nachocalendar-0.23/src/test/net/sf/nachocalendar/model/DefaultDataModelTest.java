/*
 * Created on Jan 8, 2005
 * 
 * Project: NachoCalendar
 * 
 * DefaultDataModelTest.java
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
public class DefaultDataModelTest extends TestCase {
    private DefaultDataModel model;
    
    protected void setUp() throws Exception {
        model = new DefaultDataModel();
    }
    
    protected void tearDown() throws Exception {
        model = null;
    }
    
    public void testRemoveData() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date d2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date d3 = cal.getTime();
        assertEquals(model.getSize(), 0);
        model.addData(d1, this);
        assertEquals(model.getSize(), 1);
        model.addData(d2, this);
        assertEquals(model.getSize(), 2);
        model.addData(d3, this);
        assertEquals(model.getSize(), 3);
        model.removeData(d1);
        assertEquals(model.getSize(), 2);
        model.removeData(d2);
        assertEquals(model.getSize(), 1);
        model.removeData(d3);
        assertEquals(model.getSize(), 0);
    }

    public void testClear() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date d2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date d3 = cal.getTime();
        assertEquals(model.getSize(), 0);
        model.addData(d1, "1");
        model.addData(d2, "2");
        model.addData(d3, "3");
        model.clear();
        assertEquals(model.getSize(), 0);
    }

    public void testGetData() {
        Calendar cal = new GregorianCalendar();
        Date d1 = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date d2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date d3 = cal.getTime();
        assertEquals(model.getSize(), 0);
        model.addData(d1, "Dia 1");
        assertEquals(model.getSize(), 1);
        model.addData(d2, "Dia 2");
        assertEquals(model.getSize(), 2);
        model.addData(d3, "Dia 3");
        assertEquals(model.getSize(), 3);
        String s1 = (String) model.getData(d1);
        String s2 = (String) model.getData(d2);
        String s3 = (String) model.getData(d3);
        
        assertEquals("Dia 1", s1);
        assertEquals("Dia 2", s2);
        assertEquals("Dia 3", s3);
    }
}
