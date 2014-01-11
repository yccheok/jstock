/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class Statement {
    public enum Type {
        RealtimeInfo,
        PortfolioManagementBuy,
        PortfolioManagementSell,
        PortfolioManagementDeposit,
        PortfolioManagementDividend,
        StockIndicatorScanner,
        StockHistory,
        StockPrice,
        StockInfoDatabase,
        UserDefinedDatabase,
        StockNameDatabase,        
        WatchlistInfos,
        PortfolioInfos,
        Unknown
    }

    public static final class What {
        public final Type type;
        public final GUIBundleWrapper guiBundleWrapper;
                
        private What(Type type, GUIBundleWrapper guiBundleWrapper) {
            this.type = type;
            this.guiBundleWrapper = guiBundleWrapper;
        }
        
        public static What newInstance(Type type, GUIBundleWrapper guiBundleWrapper) {
            return new What(type, guiBundleWrapper);
        }
    }
    
    public Statement(List<Atom> atoms) {
        this.atoms = new ArrayList<Atom>(atoms);

        What what = whatAsAtoms(atoms);

        this.type = what.type;
        this.guiBundleWrapper = what.guiBundleWrapper;
        
        for (Atom atom : atoms) {
            final Object oldObject = typeToValue.put(atom.getType(), atom.getValue());
            if (null != oldObject)
            {
                // Give a warning message. Duplicated key situation shouldn't occur.
                log.error(oldObject + " is being replaced by " + atom.getValue() + " with type " + atom.getType());
            }
        }
    }
    
    public Object getValue(String type) {
        return typeToValue.get(type);
    }

    public String getValueAsString(String type) {
        Object o = typeToValue.get(type);
        if (o != null) {
            return o.toString();
        }
        return null;
    }

    public Double getValueAsDouble(String type) {
        Object o = typeToValue.get(type);
        // In our case, mostly is String.
        if (o instanceof String) {
            String s = (String)o;
            if (s.isEmpty() == false) {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException ex) {
                    log.error(null, ex);
                }
            }
        } else if (o instanceof Double) {
            return (Double)o;
        } else if (o instanceof Integer) {
            Integer i = (Integer)o;
            Double d = (double)i.intValue();
            return d;
        } else if (o != null) {
            String s = o.toString();
            if (s.isEmpty() == false) {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException ex) {
                    log.error(null, ex);
                }
            }
        }
        return null;
    }
    
    public Type getType() {
        return this.type;
    }
    
    private static What whatAsAtoms(List<Atom> atoms) {
        List<String> strings = new ArrayList<String>();
        for (Atom atom : atoms) {
            strings.add(atom.getType());
        }
        return what(strings);
    }
    
    /**
     * Returns type and GUI resource bundle, based on given CSV header string.
     * 
     * @param strings CSV header string
     * @return type and GUI resource bundle
     */
    public static What what(List<String> strings) {
        List<Atom> atoms = new ArrayList<Atom>();
        for (String string : strings) {
            Atom atom = new Atom("", string);
            atoms.add(atom);
        }
        
        Type t = Type.Unknown;
        GUIBundleWrapper r = null;
        for (GUIBundleWrapper.Language language : GUIBundleWrapper.Language.values()) {
            r = GUIBundleWrapper.newInstance(language);
            t = whatType(atoms, r);
            if (t != Type.Unknown) {
                break;
            }
        }
        return What.newInstance(t, r);
    }
    
    /**
     * Convert type to list of CSV header string.
     * 
     * @param type the type
     * @param guiBundleWrapper GUI resource bundle used to perform type to string
     * conversion
     * @return list of CSV header string
     */
    public static List<String> typeToStrings(Type type, GUIBundleWrapper guiBundleWrapper) {
  
        List<String> strings = new ArrayList<String>();
        if (type == Type.RealtimeInfo) {
            strings.add(guiBundleWrapper.getString("MainFrame_Code"));
            strings.add(guiBundleWrapper.getString("MainFrame_Symbol"));
            strings.add(guiBundleWrapper.getString("MainFrame_Prev"));
            strings.add(guiBundleWrapper.getString("MainFrame_Open"));
            strings.add(guiBundleWrapper.getString("MainFrame_Last"));
            strings.add(guiBundleWrapper.getString("MainFrame_High"));
            strings.add(guiBundleWrapper.getString("MainFrame_Low"));
            strings.add(guiBundleWrapper.getString("MainFrame_Vol"));
            strings.add(guiBundleWrapper.getString("MainFrame_Chg"));
            strings.add(guiBundleWrapper.getString("MainFrame_ChgPercentage"));
            strings.add(guiBundleWrapper.getString("MainFrame_LVol"));
            strings.add(guiBundleWrapper.getString("MainFrame_Buy"));
            strings.add(guiBundleWrapper.getString("MainFrame_BQty"));
            strings.add(guiBundleWrapper.getString("MainFrame_Sell"));
            strings.add(guiBundleWrapper.getString("MainFrame_SQty"));
            strings.add(guiBundleWrapper.getString("MainFrame_FallBelow"));
            strings.add(guiBundleWrapper.getString("MainFrame_RiseAbove"));
        } else if (type == Type.PortfolioManagementBuy) {
            strings.add(guiBundleWrapper.getString("MainFrame_Code"));
            strings.add(guiBundleWrapper.getString("MainFrame_Symbol"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Units"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_CurrentPrice"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_CurrentValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_NetPurchaseValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));            
        } else if (type == Type.PortfolioManagementSell) {
            strings.add(guiBundleWrapper.getString("MainFrame_Code"));
            strings.add(guiBundleWrapper.getString("MainFrame_Symbol"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Units"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingPrice"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseBroker"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseClearingFee"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseStampDuty"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_NetSellingValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));            
        } else if (type == Type.PortfolioManagementDeposit) {
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Cash"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));
        } else if (type == Type.PortfolioManagementDividend) {
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
            strings.add(guiBundleWrapper.getString("MainFrame_Code"));           
            strings.add(guiBundleWrapper.getString("MainFrame_Symbol"));  
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Dividend"));
            strings.add(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));
        } else if (type == Type.StockIndicatorScanner) {
            strings.add(guiBundleWrapper.getString("IndicatorScannerJPanel_Indicator"));
            strings.add(guiBundleWrapper.getString("MainFrame_Code"));
            strings.add(guiBundleWrapper.getString("MainFrame_Symbol"));
            strings.add(guiBundleWrapper.getString("MainFrame_Prev"));
            strings.add(guiBundleWrapper.getString("MainFrame_Open"));
            strings.add(guiBundleWrapper.getString("MainFrame_Last"));
            strings.add(guiBundleWrapper.getString("MainFrame_High"));
            strings.add(guiBundleWrapper.getString("MainFrame_Low"));
            strings.add(guiBundleWrapper.getString("MainFrame_Vol"));
            strings.add(guiBundleWrapper.getString("MainFrame_Chg"));
            strings.add(guiBundleWrapper.getString("MainFrame_ChgPercentage"));
            strings.add(guiBundleWrapper.getString("MainFrame_LVol"));
            strings.add(guiBundleWrapper.getString("MainFrame_Buy"));
            strings.add(guiBundleWrapper.getString("MainFrame_BQty"));
            strings.add(guiBundleWrapper.getString("MainFrame_Sell"));
            strings.add(guiBundleWrapper.getString("MainFrame_SQty"));
            strings.add(guiBundleWrapper.getString("IndicatorScannerJPanel_MCapital"));
            strings.add(guiBundleWrapper.getString("IndicatorScannerJPanel_SIssued"));            
        } else if (type == Type.StockHistory) {
            strings.add(guiBundleWrapper.getString("StockHistory_Date"));
            strings.add(guiBundleWrapper.getString("StockHistory_Open"));           
            strings.add(guiBundleWrapper.getString("StockHistory_High"));  
            strings.add(guiBundleWrapper.getString("StockHistory_Low"));             
            strings.add(guiBundleWrapper.getString("StockHistory_Close")); 
            strings.add(guiBundleWrapper.getString("StockHistory_Volume")); 
        } else if (type == Type.StockPrice) {
            strings.add(guiBundleWrapper.getString("MainFrame_Code")); 
            strings.add(guiBundleWrapper.getString("MainFrame_Last"));             
        } else if (type == Type.StockInfoDatabase) {
            strings.add(guiBundleWrapper.getString("MainFrame_Code")); 
            strings.add(guiBundleWrapper.getString("MainFrame_Symbol"));             
            strings.add(guiBundleWrapper.getString("MainFrame_Industry")); 
            strings.add(guiBundleWrapper.getString("MainFrame_Board"));            
        } else if (type == Type.UserDefinedDatabase) {
            strings.add(guiBundleWrapper.getString("MainFrame_Code")); 
            strings.add(guiBundleWrapper.getString("MainFrame_Symbol"));            
        } else if (type == Type.StockNameDatabase) {
            strings.add(guiBundleWrapper.getString("MainFrame_Code")); 
            strings.add(guiBundleWrapper.getString("MainFrame_Name"));            
        } else if (type == Type.WatchlistInfos) {
            strings.add(guiBundleWrapper.getString("WatchlistInfo_Country")); 
            strings.add(guiBundleWrapper.getString("WatchlistInfo_Name"));            
            strings.add(guiBundleWrapper.getString("WatchlistInfo_Size"));             
        } else if (type == Type.PortfolioInfos) {
            strings.add(guiBundleWrapper.getString("PortfolioInfo_Country")); 
            strings.add(guiBundleWrapper.getString("PortfolioInfo_Name"));            
            strings.add(guiBundleWrapper.getString("PortfolioInfo_Size")); 
        } else {
            assert(false);
        }
        return strings;
    }
    
    private static Type whatType(List<Atom> atoms, GUIBundleWrapper guiBundleWrapper) {
        // Use if...if instead of if...else, as atoms may be having same size,
        // but their type may be different. Hence, we will just let them fall
        // through all size checking.
        final int size = atoms.size();
        if (size == 17)
        {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("MainFrame_Prev")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("MainFrame_Open")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("MainFrame_Last")) &&
            atoms.get(5).getType().equals(guiBundleWrapper.getString("MainFrame_High")) &&
            atoms.get(6).getType().equals(guiBundleWrapper.getString("MainFrame_Low")) &&
            atoms.get(7).getType().equals(guiBundleWrapper.getString("MainFrame_Vol")) &&
            atoms.get(8).getType().equals(guiBundleWrapper.getString("MainFrame_Chg")) &&
            atoms.get(9).getType().equals(guiBundleWrapper.getString("MainFrame_ChgPercentage")) &&
            atoms.get(10).getType().equals(guiBundleWrapper.getString("MainFrame_LVol")) &&
            atoms.get(11).getType().equals(guiBundleWrapper.getString("MainFrame_Buy")) &&
            atoms.get(12).getType().equals(guiBundleWrapper.getString("MainFrame_BQty")) &&
            atoms.get(13).getType().equals(guiBundleWrapper.getString("MainFrame_Sell")) &&
            atoms.get(14).getType().equals(guiBundleWrapper.getString("MainFrame_SQty")) &&
            atoms.get(15).getType().equals(guiBundleWrapper.getString("MainFrame_FallBelow")) &&
            atoms.get(16).getType().equals(guiBundleWrapper.getString("MainFrame_RiseAbove"))
            ) {
                return Type.RealtimeInfo;
            }
        }
        if (size == 18) {
            /* Wow! Beware, Stock will being translated into Code and Symbol */
            // GUIBundle.getString("PortfolioManagementJPanel_Stock")
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(5).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_CurrentPrice")) &&
            atoms.get(6).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(7).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_CurrentValue")) &&
            atoms.get(8).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(9).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(10).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(11).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(12).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(13).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(14).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetPurchaseValue")) &&
            atoms.get(15).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(16).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(17).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementBuy;
            }
        }
        if (size == 19) {
            // Note, this code block is for legacy purpose. It should be removed
            // after a while. The new code block should have PortfolioManagementJPanel_Broker,
            // PortfolioManagementJPanel_ClearingFee, PortfolioManagementJPanel_StampDuty.
            // They are being introduced starting from 1.0.6x

            /* Wow! Beware, Stock will being translated into Code and Symbol */
            // GUIBundle.getString("PortfolioManagementJPanel_Stock")
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(5).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingPrice")) &&
            atoms.get(6).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(7).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingValue")) &&
            atoms.get(8).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(9).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(10).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(11).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(12).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(13).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(14).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(15).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetSellingValue")) &&
            atoms.get(16).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(17).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(18).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementSell;
            }
        }
        if (size == 20) {
            // Note, this code block is for legacy purpose. It should be removed
            // after a while. The new code block should have PortfolioManagementJPanel_Broker,
            // PortfolioManagementJPanel_ClearingFee, PortfolioManagementJPanel_StampDuty.
            // They are being introduced starting from 1.0.6x            
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(5).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingPrice")) &&
            atoms.get(6).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(7).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingValue")) &&
            atoms.get(8).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(9).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseFee")) &&
            atoms.get(10).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(11).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(12).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(13).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(14).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(15).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(16).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetSellingValue")) &&
            atoms.get(17).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(18).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(19).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementSell;
            }
        } 
        if (size == 22) {
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(5).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingPrice")) &&
            atoms.get(6).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(7).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingValue")) &&
            atoms.get(8).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(9).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseBroker")) &&
            atoms.get(10).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseClearingFee")) &&
            atoms.get(11).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseStampDuty")) &&
            atoms.get(12).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(13).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(14).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(15).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(16).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(17).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(18).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetSellingValue")) &&
            atoms.get(19).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(20).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(21).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementSell;
            }
        }         
        if (size == 2) {
            // Legacy CSV file handling. As in version <=1.0.6p, comment
            // is not being saved to CSV.
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Cash"))
            ) {
                return Type.PortfolioManagementDeposit;
            }
        }
        if (size == 3) {
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Cash")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementDeposit;
            }
        }        
        if (size == 4) {
            // Legacy CSV file handling. As in version <=1.0.6p, comment
            // is not being saved to CSV.          
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Dividend"))
            ) {
                return Type.PortfolioManagementDividend;
            }
        }
        if (size == 5) {           
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Dividend")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"))                    
            ) {
                return Type.PortfolioManagementDividend;
            }
        }        
        if (size == 18) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("IndicatorScannerJPanel_Indicator")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("MainFrame_Prev")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("MainFrame_Open")) &&
            atoms.get(5).getType().equals(guiBundleWrapper.getString("MainFrame_Last")) &&
            atoms.get(6).getType().equals(guiBundleWrapper.getString("MainFrame_High")) &&
            atoms.get(7).getType().equals(guiBundleWrapper.getString("MainFrame_Low")) &&
            atoms.get(8).getType().equals(guiBundleWrapper.getString("MainFrame_Vol")) &&
            atoms.get(9).getType().equals(guiBundleWrapper.getString("MainFrame_Chg")) &&
            atoms.get(10).getType().equals(guiBundleWrapper.getString("MainFrame_ChgPercentage")) &&
            atoms.get(11).getType().equals(guiBundleWrapper.getString("MainFrame_LVol")) &&
            atoms.get(12).getType().equals(guiBundleWrapper.getString("MainFrame_Buy")) &&
            atoms.get(13).getType().equals(guiBundleWrapper.getString("MainFrame_BQty")) &&
            atoms.get(14).getType().equals(guiBundleWrapper.getString("MainFrame_Sell")) &&
            atoms.get(15).getType().equals(guiBundleWrapper.getString("MainFrame_SQty")) &&
            atoms.get(16).getType().equals(guiBundleWrapper.getString("IndicatorScannerJPanel_MCapital")) &&
            atoms.get(17).getType().equals(guiBundleWrapper.getString("IndicatorScannerJPanel_SIssued"))
            ) {
                return Type.StockIndicatorScanner;
            }
        }
        if (size == 6) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("StockHistory_Date")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("StockHistory_Open")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("StockHistory_High")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("StockHistory_Low")) &&
            atoms.get(4).getType().equals(guiBundleWrapper.getString("StockHistory_Close")) &&
            atoms.get(5).getType().equals(guiBundleWrapper.getString("StockHistory_Volume"))
            ) {
                return Type.StockHistory;
            }
        }
        if (size == 2) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Last"))
            ) {
                return Type.StockPrice;
            }            
        }
        if (size == 4) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("MainFrame_Industry")) &&
            atoms.get(3).getType().equals(guiBundleWrapper.getString("MainFrame_Board"))
            ) {
                return Type.StockInfoDatabase;
            }            
        }     
        if (size == 2) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Symbol"))
            ) {
                return Type.UserDefinedDatabase;
            }            
        }
        if (size == 2) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("MainFrame_Name"))
            ) {
                return Type.StockNameDatabase;
            }            
        }    
        if (size == 3) {
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("WatchlistInfo_Country")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("WatchlistInfo_Name")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("WatchlistInfo_Size"))  
            ) {
                return Type.WatchlistInfos;
            }            
        }
        if (size == 3) {
            if (
            atoms.get(0).getType().equals(guiBundleWrapper.getString("PortfolioInfo_Country")) &&
            atoms.get(1).getType().equals(guiBundleWrapper.getString("PortfolioInfo_Name")) &&
            atoms.get(2).getType().equals(guiBundleWrapper.getString("PortfolioInfo_Size"))
            ) {
                return Type.PortfolioInfos;
            }            
        }
        return Type.Unknown;
    }

    public int size() {
        return atoms.size();
    }

    public Atom getAtom(int index) {
        return atoms.get(index);
    }

    /**
     * @return GUI resource bundle used by this statement.
     */
    public GUIBundleWrapper getGUIBundleWrapper() {
        return guiBundleWrapper;
    }


    private final GUIBundleWrapper guiBundleWrapper;
    private final Type type;
    private final List<Atom> atoms;
    private final Map<String, Object> typeToValue = new HashMap<String, Object>();
    private static final Log log = LogFactory.getLog(Statement.class);
}
