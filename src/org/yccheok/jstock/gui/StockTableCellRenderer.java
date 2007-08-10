/*
 * StockTableCellRender.java
 *
 * Created on May 1, 2007, 5:43 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author yccheok
 */
public class StockTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    /** Creates a new instance of StockTableCellRender */
    public StockTableCellRenderer() {
        super();
    }
    
    public Component getTableCellRendererComponent(
                            JTable table, Object color,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        Component c = super.getTableCellRendererComponent(table, color, isSelected, hasFocus, row, column);

        if(isSelected) return c;

        java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        AbstractTableModel tableModel = (AbstractTableModel)table.getModel();

        double openPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Open"));

        c.setForeground(Color.BLACK);

        if(table.getColumnName(column).equalsIgnoreCase("buy"))
        {
            double buyPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Buy"));

            if(buyPrice < openPrice) {
                c.setForeground(lowerColor);
            }
            else if(buyPrice > openPrice) {
                c.setForeground(higherColor);
            }
        }                         
        else if(table.getColumnName(column).equalsIgnoreCase("sell"))
        {
            double sellPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Sell"));

            if(sellPrice < openPrice) {
                c.setForeground(lowerColor);
            }
            else if(sellPrice > openPrice) {
                c.setForeground(higherColor);
            }
        } 
        else if(table.getColumnName(column).equalsIgnoreCase("last"))
        {
            double lastPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Last"));

            if(lastPrice < openPrice) {
                c.setForeground(lowerColor);
            }
            else if(lastPrice > openPrice) {
                c.setForeground(higherColor);
            }
        }      
        else if(table.getColumnName(column).equalsIgnoreCase("low"))
        {
            double lowPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Low"));

            if(lowPrice < openPrice) {
                c.setForeground(lowerColor);
            }
            else if(lowPrice > openPrice) {
                c.setForeground(higherColor);
            }
        } 
        else if(table.getColumnName(column).equalsIgnoreCase("high"))
        {
            double highPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("High"));

            if(highPrice < openPrice) {
                c.setForeground(lowerColor);
            }
            else if(highPrice > openPrice) {
                c.setForeground(higherColor);
            }
        }
        else if(table.getColumnName(column).equalsIgnoreCase("chg"))
        {
            double changePrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Chg"));

            if(changePrice < 0.0) {
                c.setForeground(lowerColor);
            }
            else if(changePrice > 0.0) {
                c.setForeground(higherColor);
            }
        }
        else if(table.getColumnName(column).equalsIgnoreCase("chg (%)"))
        {
            double changePricePercentage = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Chg (%)"));

            if(changePricePercentage < 0.0) {
                c.setForeground(lowerColor);
            }
            else if(changePricePercentage > 0.0) {
                c.setForeground(higherColor);
            }
        }
        else if(table.getColumnName(column).equalsIgnoreCase("indicator"))
        {
            c.setForeground(Color.BLUE);
        }
        
        if(row % 2 == 0) {
            c.setBackground(Color.WHITE);
        }
        else {
            c.setBackground(highLighter);
        }

        return this;
    }
    
    private static java.awt.Color higherColor = new java.awt.Color(50, 150, 0);
    private static java.awt.Color lowerColor = new java.awt.Color(200, 0, 50);
    private static java.awt.Color highLighter = new java.awt.Color(255, 255, 204);    
}
