/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.gui;

import java.text.DateFormat;
import org.junit.Test;
import static org.junit.Assert.*;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.SimpleDate;

/**
 *
 * @author Michael
 */
public class PortfolioManagementJPanelTest {
    
    public PortfolioManagementJPanelTest() {
    }

    /**
     * Test of DateFormat and display, of class PortfolioManagementJPanel. Country hard coded as Australia.
     * Test should pass.
     */
    @Test
    public void testDateFormat() {
        DateFormat dateFormat = DateFormat.getDateInstance();
        SimpleDate simpleDate = new SimpleDate(2014, 5, 2);
        String tempDate = dateFormat.format(simpleDate.getTime());
        tempDate = tempDate.replace(",", "");
        String[] dateComponents = tempDate.split(" ");
        String displayDate = dateComponents[1] + " " + dateComponents[0] + " " + dateComponents[2];
                
        Country country = Country.Australia;
        
        String expResult = "";
        String result = "";
        if (!country.toHumanReadableString().equals("United States"))
            {
                expResult = "2 Jun 2014";
                result = displayDate;
            }
        else
            {
                System.out.println(dateFormat.format(simpleDate.getTime()));
            }
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    /**
     * Test of DateFormat and display, of class PortfolioManagementJPanel. Country hard coded as United States.
     * Test should pass.
     */
    @Test
    public void testDateFormat2() {
        DateFormat dateFormat = DateFormat.getDateInstance();
        SimpleDate simpleDate = new SimpleDate(2014, 5, 2);
        String tempDate = dateFormat.format(simpleDate.getTime());
        tempDate = tempDate.replace(",", "");
        String[] dateComponents = tempDate.split(" ");
        String displayDate = dateComponents[1] + " " + dateComponents[0] + " " + dateComponents[2];
                
        Country country = Country.UnitedState;
        
        String expResult = "";
        String result = "";
        if (!country.toHumanReadableString().equals("United States"))
            {
                System.out.println(displayDate);
            }
        else
            {
                expResult = "Jun 2, 2014";
                result = dateFormat.format(simpleDate.getTime());
            }
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
