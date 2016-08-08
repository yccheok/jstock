/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author shuwnyuan
 */
public class Utils {
    
    // Prevent from being instantiated.
    private Utils() {
    }
    
    public static String monetaryFormat (Double number) {
        return monetaryFormat(number, false);
    }

    public static String monetaryFormat (Double number, boolean displayCurrency) {
        DecimalFormat formatter;
        
        if (displayCurrency == true) {
            Locale locale = new Locale("en", "US");
            formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
            String symbol = formatter.getCurrency().getSymbol();
            formatter.setNegativePrefix("-" + symbol);
            formatter.setNegativeSuffix("");
        } else {
            formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
            // no currency symbol
            formatter.setNegativePrefix("-");
            formatter.setNegativeSuffix("");
            formatter.setPositivePrefix("");
            formatter.setPositiveSuffix("");
        }
        
        return formatter.format(number);
    }

    public static String formatNumber(Double number, int decimal) {
        String formatter = "###,###,###,###,###,###,##0.";
        do {
            formatter = formatter.concat("0");
            decimal--;
        } while (decimal > 0);
        
        return new DecimalFormat(formatter).format(number);
    }
}
