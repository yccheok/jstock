/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng CHEOK <yccheok@yahoo.com>
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
 
package org.yccheok.jstock.file;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 *
 * @author yccheok
 */
public class GUIBundleWrapper {
    public enum Language {
        // INDEPENDENT must come first. This is important, especially when we 
        // want to loop through Language. The first member we want to access is
        // INDEPENDENT.
        //
        // DEFAULT will always be the last.
        INDEPENDENT,
        ENGLISH,
        SIMPLIFIED_CHINESE,
        GERMAN,
        TRADITIONAL_CHINESE,
        ITALIAN,
        DEFAULT,
    };
    
    // So that we know which org.yccheok.jstock.data.gui language file we should
    // refer to.
    private final ResourceBundle guiBundleWrapper;
    private static volatile Map<String, String> map = null;
    
    public static GUIBundleWrapper newInstance(Language language) {
        return new GUIBundleWrapper(language);
    }
    
    private GUIBundleWrapper(Language type) {
        if (type == Language.INDEPENDENT) {
            guiBundleWrapper = null;
        } else if (type == Language.ENGLISH) {
            guiBundleWrapper = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", Locale.ENGLISH);
        } else if (type == Language.SIMPLIFIED_CHINESE) {
            guiBundleWrapper = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", Locale.SIMPLIFIED_CHINESE);
        } else if (type == Language.GERMAN) {
            guiBundleWrapper = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", Locale.GERMAN);
        } else if (type == Language.TRADITIONAL_CHINESE) { 
            guiBundleWrapper = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", Locale.TRADITIONAL_CHINESE);
        } else if (type == Language.ITALIAN) {
            guiBundleWrapper = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", Locale.ITALIAN);
        } else {
            assert(type == Language.DEFAULT);
            guiBundleWrapper = ResourceBundle.getBundle("org.yccheok.jstock.data.gui");
        }
    }
    
    public String getString(String key) {
        if (guiBundleWrapper == null) {
            return getLanguageIndependentString(key);
        }
        return guiBundleWrapper.getString(key);
    }
    
    private static void initMap() {
        if (GUIBundleWrapper.map != null) {
            return;
        }
        
        Map<String, String> _map = new HashMap<String, String>();
        _map.put("MainFrame_Stock", "Stock");
        _map.put("MainFrame_Code", "Code");
        _map.put("MainFrame_Symbol", "Symbol");
        _map.put("MainFrame_Prev", "Prev");
        _map.put("MainFrame_Open", "Open");        
        _map.put("MainFrame_Last", "Last");
        _map.put("MainFrame_High", "High");
        _map.put("MainFrame_Low", "Low");
        _map.put("MainFrame_Vol", "Vol");
        _map.put("MainFrame_Chg", "Chg");
        _map.put("MainFrame_ChgPercentage", "Chg (%)");
        _map.put("MainFrame_LVol", "L.Vol");
        _map.put("MainFrame_Buy", "Buy");
        _map.put("MainFrame_BQty", "B.Qty");
        _map.put("MainFrame_Sell", "Sell");
        _map.put("MainFrame_SQty", "S.Qty");
        _map.put("MainFrame_FallBelow", "Fall Below");
        _map.put("MainFrame_RiseAbove", "Rise Above");
        
        // For stock info.
        _map.put("MainFrame_Industry", "Industry");
        _map.put("MainFrame_Board", "Board");
        // For stock name.
        _map.put("MainFrame_Name", "Name");
        
        _map.put("PortfolioManagementJPanel_Stock", "Stock");      
        _map.put("PortfolioManagementJPanel_Date", "Date");
        _map.put("PortfolioManagementJPanel_Units", "Units");    
        _map.put("PortfolioManagementJPanel_PurchasePrice", "Purchase Price");
        _map.put("PortfolioManagementJPanel_CurrentPrice", "Current Price");
        _map.put("PortfolioManagementJPanel_PurchaseValue", "Purchase Value");
        
        // PortfolioManagementJPanel_PurchaseFee is obsolete. It is replaced by
        // PortfolioManagementJPanel_PurchaseBroker, PortfolioManagementJPanel_PurchaseClearingFee
        // and PortfolioManagementJPanel_PurchaseStampDuty. All works related to
        // PortfolioManagementJPanel_PurchaseFee may be removed, after some 
        // time.
        _map.put("PortfolioManagementJPanel_PurchaseFee", "Purchase Fee");
        _map.put("PortfolioManagementJPanel_PurchaseBroker", "Purchase Broker");
        _map.put("PortfolioManagementJPanel_PurchaseClearingFee", "Purchase Clearing Fee");
        _map.put("PortfolioManagementJPanel_PurchaseStampDuty", "Purchase Stamp Duty");
        
        _map.put("PortfolioManagementJPanel_CurrentValue", "Current Value");
        _map.put("PortfolioManagementJPanel_GainLossPrice", "Gain/Loss Price");
        _map.put("PortfolioManagementJPanel_GainLossValue", "Gain/Loss Value");
        _map.put("PortfolioManagementJPanel_GainLossPercentage", "Gain/Loss %");
        _map.put("PortfolioManagementJPanel_Broker", "Broker");
        _map.put("PortfolioManagementJPanel_ClearingFee", "Clearing Fee");
        _map.put("PortfolioManagementJPanel_StampDuty", "Stamp Duty");
        _map.put("PortfolioManagementJPanel_NetPurchaseValue", "Net Purchase Value");
        _map.put("PortfolioManagementJPanel_NetGainLossValue", "Net Gain/Loss Value");
        _map.put("PortfolioManagementJPanel_NetGainLossPercentage", "Net Gain/Loss %");
        _map.put("PortfolioManagementJPanel_Comment", "Comment");
        
        _map.put("PortfolioManagementJPanel_ReferenceDate", "Purchase Date");
        _map.put("PortfolioManagementJPanel_SellingPrice", "Selling Price");        
        _map.put("PortfolioManagementJPanel_SellingValue", "Selling Value");          
        _map.put("PortfolioManagementJPanel_NetSellingValue", "Net Selling Value");
        
        _map.put("PortfolioManagementJPanel_Cash", "Cash");
        
        _map.put("PortfolioManagementJPanel_Dividend", "Dividend");
        
        _map.put("IndicatorScannerJPanel_Indicator", "Indicator");                
        _map.put("IndicatorScannerJPanel_MCapital", "M.Capital");
        _map.put("IndicatorScannerJPanel_SIssued", "S.Issued");

        _map.put("StockHistory_Date", "Date");                
        _map.put("StockHistory_Open", "Open");
        _map.put("StockHistory_High", "High");
        _map.put("StockHistory_Low", "Low");                
        _map.put("StockHistory_Close", "Close");
        _map.put("StockHistory_Volume", "Volume");
        
        // We need not to have WatchlistInfo_... and PortfolioInfo_... string
        // in language files. As users from different localization will not save 
        // those information explicitly. System will only save those info as
        // Language.INDEPENDENT.
        _map.put("WatchlistInfo_Country", "Country");
        _map.put("WatchlistInfo_Name", "Watchlist Name");
        _map.put("WatchlistInfo_Size", "Size");

        _map.put("PortfolioInfo_Country", "Country");
        _map.put("PortfolioInfo_Name", "Portfolio Name");
        _map.put("PortfolioInfo_Size", "Size");
        
        //company news related
        _map.put("MainFrame_News", "News");
        _map.put("News_Time", "Time");
        _map.put("News_Title", "Title (Click for story)");
        
        GUIBundleWrapper.map = _map;
    }
    
    private static String getLanguageIndependentString(String key) {
        initMap();
        assert(GUIBundleWrapper.map.containsKey(key));
        String s = GUIBundleWrapper.map.get(key);
        if (s == null) {
            throw new java.lang.IllegalArgumentException(key);
        }
        return s;
    }
}
