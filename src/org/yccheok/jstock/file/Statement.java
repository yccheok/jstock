/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
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
        Unknown
    }

    public Statement(List<Atom> atoms) {
        this.atoms = new ArrayList<Atom>(atoms);

        ResourceBundle r = null;
        Type t = Type.Unknown;
        Locale l = Locale.ENGLISH;

        // Please revise Statement's construct code, when adding in new language.
        // So that its language guessing algorithm will work as it is.

        // Language guessing algorithm.
        Locale[] locales = {Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE, Locale.GERMAN};

        for (Locale locale : locales) {
            l = locale;
            r = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", locale);
            t = this.whatType(atoms, r);
            if (t != Type.Unknown) {
                break;
            }
        }

        this.type = t;
        if (this.type == Type.Unknown) {
            // Use default.
            this.guiResourceBundle = ResourceBundle.getBundle("org.yccheok.jstock.data.gui");
            this.dateFormat = DateFormat.getDateInstance();
        } else {
            // This is the best guess on what language file is being used.
            this.guiResourceBundle = r;
            this.dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, l);
        }

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
        if (o instanceof Double) {
            return (Double)o;
        }
        else if (o instanceof Integer) {
            Integer i = (Integer)o;
            Double d = (double)i.intValue();
            return d;
        }
        else if (o != null) {
            try {
                return Double.parseDouble(o.toString());
            }
            catch (NumberFormatException ex) {
                log.error(null, ex);
            }
        }
        return null;
    }

    public Type getType() {
        return this.type;
    }

    private Type whatType(List<Atom> atoms, ResourceBundle guiResourceBundle) {
        // Use if...if instead of if...else, as atoms may be having same size,
        // but their type may be different. Hence, we will just let them fall
        // through all size checking.
        final int size = atoms.size();
        if (size == 17)
        {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiResourceBundle.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiResourceBundle.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiResourceBundle.getString("MainFrame_Prev")) &&
            atoms.get(3).getType().equals(guiResourceBundle.getString("MainFrame_Open")) &&
            atoms.get(4).getType().equals(guiResourceBundle.getString("MainFrame_Last")) &&
            atoms.get(5).getType().equals(guiResourceBundle.getString("MainFrame_High")) &&
            atoms.get(6).getType().equals(guiResourceBundle.getString("MainFrame_Low")) &&
            atoms.get(7).getType().equals(guiResourceBundle.getString("MainFrame_Vol")) &&
            atoms.get(8).getType().equals(guiResourceBundle.getString("MainFrame_Chg")) &&
            atoms.get(9).getType().equals(guiResourceBundle.getString("MainFrame_ChgPercentage")) &&
            atoms.get(10).getType().equals(guiResourceBundle.getString("MainFrame_LVol")) &&
            atoms.get(11).getType().equals(guiResourceBundle.getString("MainFrame_Buy")) &&
            atoms.get(12).getType().equals(guiResourceBundle.getString("MainFrame_BQty")) &&
            atoms.get(13).getType().equals(guiResourceBundle.getString("MainFrame_Sell")) &&
            atoms.get(14).getType().equals(guiResourceBundle.getString("MainFrame_SQty")) &&
            atoms.get(15).getType().equals(guiResourceBundle.getString("MainFrame_FallBelow")) &&
            atoms.get(16).getType().equals(guiResourceBundle.getString("MainFrame_RiseAbove"))
            ) {
                return Type.RealtimeInfo;
            }
        }
        if (size == 18) {
            /* Wow! Beware, Stock will being translated into Code and Symbol */
            // GUIBundle.getString("PortfolioManagementJPanel_Stock")
            if (
            atoms.get(0).getType().equals(guiResourceBundle.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiResourceBundle.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(3).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(4).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(5).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_CurrentPrice")) &&
            atoms.get(6).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(7).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_CurrentValue")) &&
            atoms.get(8).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(9).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(10).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(11).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(12).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(13).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(14).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_NetPurchaseValue")) &&
            atoms.get(15).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(16).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(17).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementBuy;
            }
        }
        if (size == 19) {
            /* Wow! Beware, Stock will being translated into Code and Symbol */
            // GUIBundle.getString("PortfolioManagementJPanel_Stock")
            if (
            atoms.get(0).getType().equals(guiResourceBundle.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(guiResourceBundle.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_ReferenceDate")) &&
            atoms.get(3).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(4).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(5).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_SellingPrice")) &&
            atoms.get(6).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(7).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_SellingValue")) &&
            atoms.get(8).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(9).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(10).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(11).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(12).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(13).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(14).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(15).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_NetSellingValue")) &&
            atoms.get(16).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(17).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(18).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementSell;
            }
        }
        if (size == 2) {
            if (
            atoms.get(0).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Cash"))
            ) {
                return Type.PortfolioManagementDeposit;
            }
        }
        if (size == 4) {
            if (
            atoms.get(0).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(guiResourceBundle.getString("MainFrame_Code")) &&
            atoms.get(2).getType().equals(guiResourceBundle.getString("MainFrame_Symbol")) &&
            atoms.get(3).getType().equals(guiResourceBundle.getString("PortfolioManagementJPanel_Dividend"))
            ) {
                return Type.PortfolioManagementDividend;
            }
        }
        if (size == 18) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiResourceBundle.getString("IndicatorScannerJPanel_Indicator")) &&
            atoms.get(1).getType().equals(guiResourceBundle.getString("MainFrame_Code")) &&
            atoms.get(2).getType().equals(guiResourceBundle.getString("MainFrame_Symbol")) &&
            atoms.get(3).getType().equals(guiResourceBundle.getString("MainFrame_Prev")) &&
            atoms.get(4).getType().equals(guiResourceBundle.getString("MainFrame_Open")) &&
            atoms.get(5).getType().equals(guiResourceBundle.getString("MainFrame_Last")) &&
            atoms.get(6).getType().equals(guiResourceBundle.getString("MainFrame_High")) &&
            atoms.get(7).getType().equals(guiResourceBundle.getString("MainFrame_Low")) &&
            atoms.get(8).getType().equals(guiResourceBundle.getString("MainFrame_Vol")) &&
            atoms.get(9).getType().equals(guiResourceBundle.getString("MainFrame_Chg")) &&
            atoms.get(10).getType().equals(guiResourceBundle.getString("MainFrame_ChgPercentage")) &&
            atoms.get(11).getType().equals(guiResourceBundle.getString("MainFrame_LVol")) &&
            atoms.get(12).getType().equals(guiResourceBundle.getString("MainFrame_Buy")) &&
            atoms.get(13).getType().equals(guiResourceBundle.getString("MainFrame_BQty")) &&
            atoms.get(14).getType().equals(guiResourceBundle.getString("MainFrame_Sell")) &&
            atoms.get(15).getType().equals(guiResourceBundle.getString("MainFrame_SQty")) &&
            atoms.get(16).getType().equals(guiResourceBundle.getString("IndicatorScannerJPanel_MCapital")) &&
            atoms.get(17).getType().equals(guiResourceBundle.getString("IndicatorScannerJPanel_SIssued"))
            ) {
                return Type.StockIndicatorScanner;
            }
        }
        if (size == 6) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(guiResourceBundle.getString("StockHistory_Date")) &&
            atoms.get(1).getType().equals(guiResourceBundle.getString("StockHistory_Open")) &&
            atoms.get(2).getType().equals(guiResourceBundle.getString("StockHistory_High")) &&
            atoms.get(3).getType().equals(guiResourceBundle.getString("StockHistory_Low")) &&
            atoms.get(4).getType().equals(guiResourceBundle.getString("StockHistory_Close")) &&
            atoms.get(5).getType().equals(guiResourceBundle.getString("StockHistory_Volume"))
            ) {
                return Type.StockHistory;
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
     * @return the org.yccheok.jstock.data.gui language file used by this
     * statement.
     */
    public ResourceBundle getGUIResourceBundle() {
        return guiResourceBundle;
    }

    /**
     * @return the DateFormat used by this statement.
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    // So that we know which org.yccheok.jstock.data.gui language file we should
    // refer to.
    private final ResourceBundle guiResourceBundle;
    // So that we know which DateFormat we should refer to.
    private final DateFormat dateFormat;

    private final Type type;
    private final List<Atom> atoms;
    private final Map<String, Object> typeToValue = new HashMap<String, Object>();
    private static final Log log = LogFactory.getLog(Statement.class);
}
