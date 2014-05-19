/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.portfolio;

import static org.junit.Assert.*;
import org.junit.Test;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.Symbol;
import org.yccheok.jstock.portfolio.Contract.ContractBuilder;

/**
 *
 * @author MichaelF
 */
public class SimpleBrokerTest {
    
    public SimpleBrokerTest() {
    }
    /**
     * Test of calculate method with just broker fee, of class SimpleBroker.
     */
    @Test
    public void testCalculate() {
        System.out.println("calculate");
        Code ms = new Code("Microsoft");
        Symbol ft = new Symbol("MSFT");
        Stock msft = new Stock.Builder(ms, ft).build();
        SimpleDate date = new SimpleDate(2014, 5, 18);
        ContractBuilder temp = new ContractBuilder(msft, date).quantity(100).price(39);
        Contract testContract = new Contract(temp);
        SimpleBroker testBroker = new SimpleBroker("test broker", 0.0, 20.0, 0.0);
        double expResult = testBroker.getMinimumRate();
        double result = testBroker.calculate(testContract);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    /**
     * Test of calculate method with broker fee based on percentage of trade, of class SimpleBroker.
     */
    @Test
    public void testCalculate2() {
        System.out.println("calculate");
        Code ms = new Code("Microsoft");
        Symbol ft = new Symbol("MSFT");
        Stock msft = new Stock.Builder(ms, ft).build();
        SimpleDate date = new SimpleDate(2014, 5, 18);
        ContractBuilder temp = new ContractBuilder(msft, date).quantity(100).price(39);
        Contract testContract = new Contract(temp);
        SimpleBroker testBroker = new SimpleBroker("test broker", 0.0, 0.0, 0.04);
        double expResult = 1.56;
        double result = testBroker.calculate(testContract);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
     /**
     * Test of calculate method with broker fee based on percentage of trade and minimum broker fee, of class SimpleBroker.
     */
    @Test
    public void testCalculate3() {
        System.out.println("calculate");
        Code ms = new Code("Microsoft");
        Symbol ft = new Symbol("MSFT");
        Stock msft = new Stock.Builder(ms, ft).build();
        SimpleDate date = new SimpleDate(2014, 5, 18);
        ContractBuilder temp = new ContractBuilder(msft, date).quantity(100).price(39);
        Contract testContract = new Contract(temp);
        SimpleBroker testBroker = new SimpleBroker("test broker", 0.0, 20.0, 0.04);
        double expResult = 21.56;
        double result = testBroker.calculate(testContract);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
     /**
     * Test of calculate method with broker fee based on percentage of trade and minimum broker fee, of class SimpleBroker.
     * Excepted test result is based off of bug report, this test should now fail since bug has been fixed.
     */
    @Test
    public void testCalculate4() {
        System.out.println("calculate");
        Code ms = new Code("Microsoft");
        Symbol ft = new Symbol("MSFT");
        Stock msft = new Stock.Builder(ms, ft).build();
        SimpleDate date = new SimpleDate(2014, 5, 18);
        ContractBuilder temp = new ContractBuilder(msft, date).quantity(100).price(39);
        Contract testContract = new Contract(temp);
        SimpleBroker testBroker = new SimpleBroker("test broker", 0.0, 20.0, 0.04);
        double expResult = 20.0;
        double result = testBroker.calculate(testContract);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
