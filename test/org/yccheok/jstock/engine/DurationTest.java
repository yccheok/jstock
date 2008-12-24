/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.engine;

import junit.framework.TestCase;

/**
 *
 * @author yccheok
 */
public class DurationTest extends TestCase {
    
    public DurationTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getStartDate method, of class Duration.
     */
    public void testGetStartDate() {
        System.out.println("getStartDate");
        Duration instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        SimpleDate expResult = new SimpleDate(2008, 12, 25);
        SimpleDate result = instance.getStartDate();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndDate method, of class Duration.
     */
    public void testGetEndDate() {
        System.out.println("getEndDate");
        Duration instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        SimpleDate expResult = new SimpleDate(2008, 12, 31);
        SimpleDate result = instance.getEndDate();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDurationInDays method, of class Duration.
     */
    public void testGetDurationInDays() {
        System.out.println("getDurationInDays");
        Duration instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 25));
        long expResult = 0L;
        long result = instance.getDurationInDays();
        assertEquals(expResult, result);

        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 26));
        expResult = 1L;
        result = instance.getDurationInDays();
        assertEquals(expResult, result);

        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2009, 1, 1));
        expResult = 7L;
        result = instance.getDurationInDays();
        assertEquals(expResult, result);
    }

    /**
     * Test of isContains method, of class Duration.
     */
    public void testIsContains() {
        System.out.println("isContains");
        Duration duration = new Duration(new SimpleDate(2008, 12, 26), new SimpleDate(2008, 12, 30));
        Duration instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        boolean expResult = true;
        boolean result = instance.isContains(duration);
        assertEquals(expResult, result);

        duration = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        expResult = true;
        result = instance.isContains(duration);
        assertEquals(expResult, result);

        duration = new Duration(new SimpleDate(2008, 12, 24), new SimpleDate(2008, 12, 28));
        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        expResult = false;
        result = instance.isContains(duration);
        assertEquals(expResult, result);

        duration = new Duration(new SimpleDate(2008, 11, 24), new SimpleDate(2008, 11, 28));
        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        expResult = false;
        result = instance.isContains(duration);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTodayDurationByYears method, of class Duration.
     */
    public void testGetTodayDurationByYears() {
        System.out.println("getTodayDurationByYears");
        int durationInYears = 1;

        java.util.Calendar end = java.util.Calendar.getInstance();
        java.util.Calendar start = java.util.Calendar.getInstance();
        start.add(java.util.Calendar.YEAR, -durationInYears);

        Duration expResult = new Duration(start, end);
        Duration result = Duration.getTodayDurationByYears(durationInYears);
        assertEquals(expResult, result);
    }

    /**
     * Test of getUnionDuration method, of class Duration.
     */
    public void testGetUnionDuration() {
        System.out.println("getUnionDuration");
        Duration duration = new Duration(new SimpleDate(2008, 12, 24), new SimpleDate(2008, 12, 28));
        Duration instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        Duration expResult = new Duration(new SimpleDate(2008, 12, 24), new SimpleDate(2008, 12, 31));
        Duration result = instance.getUnionDuration(duration);
        assertEquals(expResult, result);

        duration = new Duration(new SimpleDate(2007, 12, 24), new SimpleDate(2007, 12, 28));
        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        expResult = new Duration(new SimpleDate(2007, 12, 24), new SimpleDate(2008, 12, 31));
        result = instance.getUnionDuration(duration);
        assertEquals(expResult, result);

        duration = new Duration(new SimpleDate(2008, 12, 28), new SimpleDate(2008, 12, 29));
        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 26));
        expResult = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 29));
        result = instance.getUnionDuration(duration);
        assertEquals(expResult, result);

        duration = new Duration(new SimpleDate(2008, 12, 31), new SimpleDate(2008, 12, 31));
        instance = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 25));
        expResult = new Duration(new SimpleDate(2008, 12, 25), new SimpleDate(2008, 12, 31));
        result = instance.getUnionDuration(duration);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTodayDurationByDays method, of class Duration.
     */
    public void testGetTodayDurationByDays() {
        System.out.println("getTodayDurationByDays");
        int durationInDays = 900;

        java.util.Calendar end = java.util.Calendar.getInstance();
        java.util.Calendar start = java.util.Calendar.getInstance();
        start.add(java.util.Calendar.DAY_OF_YEAR, -durationInDays);

        Duration expResult = new Duration(start, end);

        Duration result = Duration.getTodayDurationByDays(durationInDays);
        assertEquals(expResult, result);
    }
}
