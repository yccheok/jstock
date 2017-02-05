/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.internationalization;

import java.util.ResourceBundle;

/**
 *
 * @author shuwnyuan
 */
public class DriveWealthBundle {
    // The technique known as the initialization on demand holder idiom, 
    // is as lazy as possible.
    // http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
    private static class BundleHolder {
        private static final ResourceBundle bundle = ResourceBundle.getBundle("org.yccheok.jstock.data.drivewealth");
    }

    /**
     * No need to create an instance
     */
    private DriveWealthBundle() {
    }

    /**
     * Get the string defined in the property file.
     *
     * @param key the key for the desired string
     * @exception NullPointerException if <code>key</code> is <code>null</code>
     * @exception MissingResourceException if no object for the given key can be found
     * @exception ClassCastException if the object found for the given key is not a string
     * @return the string for the given key
     */
    public static String getString(String key) {
        return BundleHolder.bundle.getString(key);
    }
}

