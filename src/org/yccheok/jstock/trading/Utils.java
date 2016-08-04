/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.text.DecimalFormat;

/**
 *
 * @author shuwnyuan
 */
public class Utils {
    // default to 2 decimal places
    public static String formatNumber(Double number) {
        return formatNumber(number, 2);
    }
    
    public static String formatNumber(Double number, int decimal) {
        String formatter = "0.";
        do {
            formatter = formatter.concat("0");
            decimal--;
        } while (decimal > 0);
        
        return new DecimalFormat(formatter).format(number);
    }
}
