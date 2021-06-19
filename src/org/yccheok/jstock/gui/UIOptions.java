/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2018 Yan Cheng Cheok <yccheok@yahoo.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
    public static final String DIVIDEND_SUMMARY_BAR_CHART_JDIALOG = "DIVIDEND_SUMMARY_BAR_CHART_JDIALOG";
    
    public static final String AUTO_DIVIDEND_JDIALOG = "AUTO_DIVIDEND_JDIALOG";
    
    public static final String FAIR_USAGE_POLICY_JDIALOG = "FAIR_USAGE_POLICY_JDIALOG";
    
    public static final String RENAME_STOCK_JDIALOG = "RENAME_STOCK_JDIALOG";
    
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
