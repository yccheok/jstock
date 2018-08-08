/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui;

import java.io.File;
import org.yccheok.jstock.engine.Country;

/**
 *
 * @author yccheok
 */
public class BackwardCompatible {
    public static String toGoogleCodeIfPossible(String code) {
        final String string = code.toString().trim().toUpperCase();
        final int string_length = string.length();
        
        if (string.endsWith(".NS") && string_length > ".NS".length()) {
            final String s = string.substring(0, string_length - ".NS".length());
            String newString = org.yccheok.jstock.engine.Utils.toGoogleFormatThroughAutoComplete(s, "NSE");
            if (newString != null) {
                return newString + ".N";
            }
        }

        if (string.endsWith(".FI") && string_length > ".FI".length()) {
            final String s = string.substring(0, string_length - ".FI".length());
            return s + ".HE";
        }

        if (string.endsWith(".RU") && string_length > ".RU".length()) {
            final String s = string.substring(0, string_length - ".ME".length());
            return s + ".ME";
        }

        return code;
    }
    
    public static boolean needToPerformBackwardCompatible(File file) {
        String name = file.getName();
        if (name.contains("realtimestock.csv") || name.contains("buyportfolio.csv") || name.contains("sellportfolio.csv") || name.contains("dividendsummary.csv")) {
            return true;            
        }
        return false;
    }
    
    public static boolean needToHandleMetadata(File file) {
        String name = file.getName();
        if (name.contains("buyportfolio.csv") || name.contains("sellportfolio.csv")) {
            return true;            
        }
        return false;
    }   
    
    public static void removeGoogleCodeDatabaseIfNecessary() {
        final int versionCode = JStock.instance().getJStockOptions().getApplicationVersionID();

        if (versionCode <= 0) {
            // First time. Should be no previous old database.
            return;
        }

        if (versionCode >= 1164) {
            return;
        }

        new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + SQLite.DATABASE_GOOGLE_CODE).delete();

        JStock.instance().getJStockOptions().setGoogleCodeDatabaseMetaTimestamp(Country.Malaysia, 0);
    }    
}
