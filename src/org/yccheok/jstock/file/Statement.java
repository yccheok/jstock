/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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
import org.yccheok.jstock.internationalization.GUIBundle;

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
        Unknown
    }

    public Statement(List<Atom> atoms) {
        this.atoms = new ArrayList<Atom>(atoms);
        this.type = this.whatType(atoms);
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

    public Type getType() {
        return this.type;
    }

    private Type whatType(List<Atom> atoms) {
        if (atoms.size() == 16)
        {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(GUIBundle.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(GUIBundle.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(GUIBundle.getString("MainFrame_Open")) &&
            atoms.get(3).getType().equals(GUIBundle.getString("MainFrame_Last")) &&
            atoms.get(4).getType().equals(GUIBundle.getString("MainFrame_High")) &&
            atoms.get(5).getType().equals(GUIBundle.getString("MainFrame_Low")) &&
            atoms.get(6).getType().equals(GUIBundle.getString("MainFrame_Vol")) &&
            atoms.get(7).getType().equals(GUIBundle.getString("MainFrame_Chg")) &&
            atoms.get(8).getType().equals(GUIBundle.getString("MainFrame_ChgPercentage")) &&
            atoms.get(9).getType().equals(GUIBundle.getString("MainFrame_LVol")) &&
            atoms.get(10).getType().equals(GUIBundle.getString("MainFrame_Buy")) &&
            atoms.get(11).getType().equals(GUIBundle.getString("MainFrame_BQty")) &&
            atoms.get(12).getType().equals(GUIBundle.getString("MainFrame_Sell")) &&
            atoms.get(13).getType().equals(GUIBundle.getString("MainFrame_SQty")) &&
            atoms.get(14).getType().equals(GUIBundle.getString("MainFrame_FallBelow")) &&
            atoms.get(15).getType().equals(GUIBundle.getString("MainFrame_RiseAbove"))
            ) {
                return Type.RealtimeInfo;
            }
        }
        else if (atoms.size() == 18) {
            /* Wow! Beware, Stock will being translated into Code and Symbol */
            // GUIBundle.getString("PortfolioManagementJPanel_Stock")
            if (
            atoms.get(0).getType().equals(GUIBundle.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(GUIBundle.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(3).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(4).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(5).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_CurrentPrice")) &&
            atoms.get(6).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(7).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_CurrentValue")) &&
            atoms.get(8).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(9).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(10).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(11).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(12).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(13).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(14).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_NetPurchaseValue")) &&
            atoms.get(15).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(16).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(17).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementBuy;
            }
        }
        else if (atoms.size() == 18) {
            /* Wow! Beware, Stock will being translated into Code and Symbol */
            // GUIBundle.getString("PortfolioManagementJPanel_Stock")
            if (
            atoms.get(0).getType().equals(GUIBundle.getString("MainFrame_Code")) &&
            atoms.get(1).getType().equals(GUIBundle.getString("MainFrame_Symbol")) &&
            atoms.get(2).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(3).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Units")) &&
            atoms.get(4).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_SellingPrice")) &&
            atoms.get(5).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_PurchasePrice")) &&
            atoms.get(6).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_SellingValue")) &&
            atoms.get(7).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_PurchaseValue")) &&
            atoms.get(8).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_GainLossPrice")) &&
            atoms.get(9).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_GainLossValue")) &&
            atoms.get(10).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_GainLossPercentage")) &&
            atoms.get(11).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Broker")) &&
            atoms.get(12).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_ClearingFee")) &&
            atoms.get(13).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_StampDuty")) &&
            atoms.get(14).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_NetSellingValue")) &&
            atoms.get(15).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_NetGainLossValue")) &&
            atoms.get(16).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_NetGainLossPercentage")) &&
            atoms.get(17).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Comment"))
            ) {
                return Type.PortfolioManagementSell;
            }
        }
        else if (atoms.size() == 2) {
            if (
            atoms.get(0).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Cash"))
            ) {
                return Type.PortfolioManagementDeposit;
            }
        }
        else if (atoms.size() == 4) {
            if (
            atoms.get(0).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Date")) &&
            atoms.get(1).getType().equals(GUIBundle.getString("MainFrame_Code")) &&
            atoms.get(2).getType().equals(GUIBundle.getString("MainFrame_Symbol")) &&
            atoms.get(3).getType().equals(GUIBundle.getString("PortfolioManagementJPanel_Dividend"))
            ) {
                return Type.PortfolioManagementDividend;
            }
        }
        else if (atoms.size() == 17) {
            /* Wow! */
            if (
            atoms.get(0).getType().equals(GUIBundle.getString("IndicatorScannerJPanel_Indicator")) &&
            atoms.get(1).getType().equals(GUIBundle.getString("MainFrame_Code")) &&
            atoms.get(2).getType().equals(GUIBundle.getString("MainFrame_Symbol")) &&
            atoms.get(3).getType().equals(GUIBundle.getString("MainFrame_Open")) &&
            atoms.get(4).getType().equals(GUIBundle.getString("MainFrame_Last")) &&
            atoms.get(5).getType().equals(GUIBundle.getString("MainFrame_High")) &&
            atoms.get(6).getType().equals(GUIBundle.getString("MainFrame_Low")) &&
            atoms.get(7).getType().equals(GUIBundle.getString("MainFrame_Vol")) &&
            atoms.get(8).getType().equals(GUIBundle.getString("MainFrame_Chg")) &&
            atoms.get(9).getType().equals(GUIBundle.getString("MainFrame_ChgPercentage")) &&
            atoms.get(10).getType().equals(GUIBundle.getString("MainFrame_LVol")) &&
            atoms.get(11).getType().equals(GUIBundle.getString("MainFrame_Buy")) &&
            atoms.get(12).getType().equals(GUIBundle.getString("MainFrame_BQty")) &&
            atoms.get(13).getType().equals(GUIBundle.getString("MainFrame_Sell")) &&
            atoms.get(14).getType().equals(GUIBundle.getString("MainFrame_SQty")) &&
            atoms.get(15).getType().equals(GUIBundle.getString("IndicatorScannerJPanel_MCapital")) &&
            atoms.get(16).getType().equals(GUIBundle.getString("IndicatorScannerJPanel_SIssued"))
            ) {
                return Type.StockIndicatorScanner;
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

    private final Type type;
    private final List<Atom> atoms;
    private final Map<String, Object> typeToValue = new HashMap<String, Object>();
    private static final Log log = LogFactory.getLog(Statement.class);
}
