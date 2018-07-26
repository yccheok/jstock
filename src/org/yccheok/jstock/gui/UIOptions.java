/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author yccheok
 */
public class UIOptions {
    public static final String LOAD_FROM_CLOUD_JDIALOG = "LOAD_FROM_CLOUD_JDIALOG";
    public static final String SAVE_TO_CLOUD_JDIALOG = "SAVE_TO_CLOUD_JDIALOG";
    
    public static final String NEW_BUY_TRANSACTION_JDIALOG = "NEW_BUY_TRANSACTION_JDIALOG";
    public static final String NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE = "NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE";
    public static final String NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE = "NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE";

    public static final String NEW_SELL_TRANSACTION_JDIALOG = "NEW_SELL_TRANSACTION_JDIALOG";
    public static final String NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE = "NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE";
    public static final String NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE = "NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE";
    
    public static final String DEPOSIT_SUMMARY_JDIALOG = "DEPOSIT_SUMMARY_JDIALOG";
    
    public static final String DIVIDEND_SUMMARY_JDIALOG = "DIVIDEND_SUMMARY_JDIALOG";
    
    public static final String AUTO_DIVIDEND_JDIALOG = "AUTO_DIVIDEND_JDIALOG";
    
    public static final String FAIR_USAGE_POLICY_JDIALOG = "FAIR_USAGE_POLICY_JDIALOG";
    
    private final Map<String, Dimension> sizes = new HashMap<>();
    
    public void setDimension(String key, Dimension dimension) {
        sizes.put(key, new Dimension(dimension));
    }
    
    public Dimension getDimension(String key) {
        Dimension dimension = sizes.get(key);
        if (dimension != null) {
            return new Dimension(dimension);
        }
        return dimension;
    }
}
