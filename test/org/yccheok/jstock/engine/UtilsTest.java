/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.engine;

import junit.framework.TestCase;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;

/**
 *
 * @author yccheok
 */
public class UtilsTest extends TestCase {
    
    public UtilsTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MainFrame.getInstance().initJStockOptions(new JStockOptions());
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of toCompleteUnitedStatesGoogleFormat method, of class Utils.
     */
    public void testToCompleteUnitedStatesGoogleFormat() {
        System.out.println("toCompleteUnitedStatesGoogleFormat");
        
        Code code = Code.newInstance("MCD");
        String result = Utils.toCompleteUnitedStatesGoogleFormat(code);
        assertEquals("NYSE:MCD", result);
        
        // Test for cache.
        code = Code.newInstance("MCD");
        result = Utils.toCompleteUnitedStatesGoogleFormat(code);
        assertEquals("NYSE:MCD", result);        
        
        code = Code.newInstance("ENVS");
        result = Utils.toCompleteUnitedStatesGoogleFormat(code);
        // Not LON:ENVS
        assertEquals("OTCMKTS:ENVS", result);        
        
    }
    
}
