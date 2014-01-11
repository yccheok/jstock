/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui;

import java.io.File;
import org.yccheok.jstock.engine.Code;

/**
 *
 * @author yccheok
 */
public class BackwardCompatible {
    public static final String toGoogleCodeIfPossible(String code) {
        String string = code.toString().trim().toUpperCase();
        final int string_length = string.length();
        
        if (string.endsWith(".NS") && string_length > ".NS".length()) {
            string = string.substring(0, string_length - ".NS".length());
            String newString = org.yccheok.jstock.engine.Utils.toGoogleFormatThroughAutoComplete(string, "NSE");
            System.out.println("newString : " + newString);
            if (false == newString.endsWith(".NS")) {
                 return newString + ".N";
            }
        }
        
        return code;
    }
    
    public static boolean needToPerformBackwardCompatible(File file) {
        String name = file.getName();
        if (name.contains("realtimestock.csv") || name.contains("buyportfolio.csv") || name.contains("sellportfolio.csv") || name.contains("dividendsummary.csv")) {
            System.out.println("needToPerformBackwardCompatible : " + file);
            return true;            
        }
        return false;
    }
    
    public static boolean needToHandleMetadata(File file) {
        String name = file.getName();
        if (name.contains("buyportfolio.csv") || name.contains("sellportfolio.csv")) {
            System.out.println("needToHandleMetadata : " + name);
            return true;            
        }
        return false;
    }    
}
