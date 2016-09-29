/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static String formatNumber (Double number, int decimal) {
        String formatter = "###,###,###,###,###,###,##0.";
        do {
            formatter = formatter.concat("#");
            decimal--;
        } while (decimal > 0);
        
        return new DecimalFormat(formatter).format(number);
    }

    public static String formatNumberNoComma (Double number, int decimal) {
        String formatter = "#.";
        do {
            formatter = formatter.concat("#");
            decimal--;
        } while (decimal > 0);

        return new DecimalFormat(formatter).format(number);
    }
    
    public static Double formattedNumtoDouble (String formattedNum) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        
        Double result = null;
        try {
            Number number = formatter.parse(formattedNum);
            result = number.doubleValue();
        } catch (ParseException ex) {
            System.out.println("[formattedNumtoDouble]  Can't parse formatted number: " + formattedNum);
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    public static boolean validateNumber (String numberS) {
        if (numberS == null || numberS.isEmpty()) {
            return false;
        }

        boolean valid = false;
        try {
            if (Double.parseDouble(numberS) > 0) {
                valid = true;
            }
        } catch (NumberFormatException ex) {
            System.out.println("[validateNumber]  NOT number format: " + numberS);
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valid;
    }
    
    public static String utcDateToLocal (String utcDate) {
        String localDate = null;
        
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = utcFormat.parse(utcDate);

            SimpleDateFormat localFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
            localDate = localFormat.format(date);
        } catch (ParseException ex) {
            System.out.println("[utcDateToLocal] parse date error: " + utcDate);
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return localDate;
    }
    
    
    
}
