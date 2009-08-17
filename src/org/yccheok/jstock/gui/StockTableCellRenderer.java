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

package org.yccheok.jstock.gui;

import java.awt.*;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class StockTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    /** Creates a new instance of StockTableCellRender */
    public StockTableCellRenderer() {
        super();
    }
    
    private void performCellBlinking(final Component cell, final double value, final double oldValue, final Color finalForegroundColor, final Color finalBackgroundColor) {                
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        if(value == oldValue) {
            cell.setForeground(finalForegroundColor);
            cell.setBackground(finalBackgroundColor);
            return;
        }
        else {
            cell.setForeground(jStockOptions.getAutoUpdateForegroundColor());
            cell.setBackground(jStockOptions.getAutoUpdateBackgroundColor());
        }                
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                cell.setForeground(finalForegroundColor);
                cell.setBackground(finalBackgroundColor);
            }
        });                                
    }
    
    private Color getBackgroundColor(int row, boolean alert) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        if (alert) {
            return jStockOptions.getAlertBackgroundColor();
        }

        if(row % 2 == 0) {
            return jStockOptions.getFirstRowBackgroundColor();
        }
        
        return jStockOptions.getSecondRowBackgroundColor();
    }

    private Color getNormalTextForegroundColor(boolean alert) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        if (alert) {
            return jStockOptions.getAlertForegroundColor();
        }

        return jStockOptions.getNormalTextForegroundColor();
    }

    private Color getForegroundColor(double value, double ref, boolean alert) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        if (alert) {
            return jStockOptions.getAlertForegroundColor();
        }

        if(value > ref) {
            return jStockOptions.getHigherNumericalValueForegroundColor();
        }
        else if(value < ref) {
            return jStockOptions.getLowerNumericalValueForegroundColor();
        }
        
        return jStockOptions.getNormalTextForegroundColor();
    }
    
    private Component getTableCellRendererComponentWithCellBlinking (
                            Component c, JTable table, Object color,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {        
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        
        AbstractTableModelWithMemory tableModel = (AbstractTableModelWithMemory)table.getModel();

        final double openPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Open"));
        boolean alert = false;

        if (jStockOptions.isEnableColorAlert()) {
            final int riseAboveIndex = tableModel.findColumn("Rise Above");
            final int fallBelowIndex = tableModel.findColumn("Fall Below");
            final double lastPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Last"));
            final Double riseAbove = riseAboveIndex >= 0 ? (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), riseAboveIndex) : null;
            final Double fallBelow = fallBelowIndex >= 0 ? (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), fallBelowIndex) : null;

            if (riseAbove != null) {
                if (lastPrice >= riseAbove) {
                    alert = true;
                }
            }

            if (fallBelow != null) {
                if (lastPrice <= fallBelow) {
                    alert = true;
                }
            }
        }

        if(table.getColumnName(column).equalsIgnoreCase("buy"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Buy"); 
            
            final double buyPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldBuyPrice = ((o == null) ? buyPrice : (Double)o);
            this.performCellBlinking(c, buyPrice, oldBuyPrice, this.getForegroundColor(buyPrice, openPrice, alert), getBackgroundColor(row, alert));
            return c;
        }                         
        else if(table.getColumnName(column).equalsIgnoreCase("sell"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Sell"); 
            
            final double sellPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldSellPrice = ((o == null) ? sellPrice : (Double)o);
            this.performCellBlinking(c, sellPrice, oldSellPrice, this.getForegroundColor(sellPrice, openPrice, alert), getBackgroundColor(row, alert));
            return c;
        } 
        else if(table.getColumnName(column).equalsIgnoreCase("last"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Last"); 
            
            final double lastPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldLastPrice = ((o == null) ? lastPrice : (Double)o);
            this.performCellBlinking(c, lastPrice, oldLastPrice, this.getForegroundColor(lastPrice, openPrice, alert), getBackgroundColor(row, alert));
            return c;          
        }      
        else if(table.getColumnName(column).equalsIgnoreCase("low"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Low"); 
            
            final double lowPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldLowPrice = ((o == null) ? lowPrice : (Double)o);
            this.performCellBlinking(c, lowPrice, oldLowPrice, this.getForegroundColor(lowPrice, openPrice, alert), getBackgroundColor(row, alert));
            return c;
        } 
        else if(table.getColumnName(column).equalsIgnoreCase("high"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("High"); 
            
            final double highPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldHighPrice = ((o == null) ? highPrice : (Double)o);
            this.performCellBlinking(c, highPrice, oldHighPrice, this.getForegroundColor(highPrice, openPrice, alert), getBackgroundColor(row, alert));
            return c;
        }
        else if(table.getColumnName(column).equalsIgnoreCase("chg"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Chg"); 
            
            final double changePrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldChangePrice = ((o == null) ? changePrice : (Double)o);
            this.performCellBlinking(c, changePrice, oldChangePrice, this.getForegroundColor(changePrice, 0.0, alert), getBackgroundColor(row, alert));
            return c;
        }
        else if(table.getColumnName(column).equalsIgnoreCase("chg (%)"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Chg (%)"); 
            
            final double changePricePercentage = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldChangePricePercentage = ((o == null) ? changePricePercentage : (Double)o);
            this.performCellBlinking(c, changePricePercentage, oldChangePricePercentage, this.getForegroundColor(changePricePercentage, 0.0, alert), getBackgroundColor(row, alert));
            return c;
        }
        else if(table.getColumnName(column).equalsIgnoreCase("vol"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Vol"); 
            
            final int volume = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldVolume = ((o == null) ? volume : (Integer)o);
            this.performCellBlinking(c, volume, oldVolume, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        }
        else if(table.getColumnName(column).equalsIgnoreCase("l.vol"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("L.Vol"); 
            
            final int lastVolume = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldLastVolume = ((o == null) ? lastVolume : (Integer)o);
            this.performCellBlinking(c, lastVolume, oldLastVolume, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        }
        else if(table.getColumnName(column).equalsIgnoreCase("b.qty"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("B.Qty"); 
            
            final int buyQuantity = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldBuyQuantity = ((o == null) ? buyQuantity : (Integer)o);
            this.performCellBlinking(c, buyQuantity, oldBuyQuantity, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        } 
        else if(table.getColumnName(column).equalsIgnoreCase("s.qty"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("S.Qty"); 
            
            final int sellQuantity = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldSellQuantity = ((o == null) ? sellQuantity : (Integer)o);
            this.performCellBlinking(c, sellQuantity, oldSellQuantity, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        }      
        else if(table.getColumnName(column).equalsIgnoreCase("indicator"))
        {
            c.setForeground(Color.BLUE);
        }
        else {
            c.setForeground(getNormalTextForegroundColor(alert));
        }
        
        c.setBackground(getBackgroundColor(row, alert));

        return c;        
    }    
    
    @Override
    public Component getTableCellRendererComponent(
                            JTable table, Object color,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        Component c = super.getTableCellRendererComponent(table, color, isSelected, hasFocus, row, column);

        if(isSelected) return c;

        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        
        if(jStockOptions.isEnableColorChange()) {
            return getTableCellRendererComponentWithCellBlinking(c, table, color, isSelected, hasFocus, row, column);
        }
        
        AbstractTableModelWithMemory tableModel = (AbstractTableModelWithMemory)table.getModel();

        double openPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Open"));       

        boolean alert = false;

        if (jStockOptions.isEnableColorAlert()) {
            final int riseAboveIndex = tableModel.findColumn("Rise Above");
            final int fallBelowIndex = tableModel.findColumn("Fall Below");

            final double lastPrice = (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), tableModel.findColumn("Last"));
            final Double riseAbove = riseAboveIndex >= 0 ? (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), riseAbove) : null;
            final Double fallBelow = fallBelowIndex >= 0 ? (Double)tableModel.getValueAt(table.convertRowIndexToModel(row), fallBelow) : null;

            if (riseAbove != null) {
                if (lastPrice >= riseAbove) {
                    alert = true;
                }
            }

            if (fallBelow != null) {
                if (lastPrice <= fallBelow) {
                    alert = true;
                }
            }
        }

        if(table.getColumnName(column).equalsIgnoreCase("buy"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Buy"); 
            
            final double buyPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(buyPrice, openPrice, alert));
        }                         
        else if(table.getColumnName(column).equalsIgnoreCase("sell"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Sell"); 
            
            final double sellPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(sellPrice, openPrice, alert));
        } 
        else if(table.getColumnName(column).equalsIgnoreCase("last"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Last"); 
            
            final double lastPrice = (Double)tableModel.getValueAt(modelRow, modelCol);
            
            c.setForeground(this.getForegroundColor(lastPrice, openPrice, alert));
        }      
        else if(table.getColumnName(column).equalsIgnoreCase("low"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Low"); 
            
            final double lowPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(lowPrice, openPrice, alert));
        } 
        else if(table.getColumnName(column).equalsIgnoreCase("high"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("High"); 
            
            final double highPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(highPrice, openPrice, alert));
        }
        else if(table.getColumnName(column).equalsIgnoreCase("chg"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Chg"); 
            
            final double changePrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(changePrice, 0.0, alert));
        }
        else if(table.getColumnName(column).equalsIgnoreCase("chg (%)"))
        {
            final int modelRow = table.convertRowIndexToModel(row);
            final int modelCol = tableModel.findColumn("Chg (%)"); 
            
            final double changePricePercentage = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(changePricePercentage, 0.0, alert));
        }
        else if(table.getColumnName(column).equalsIgnoreCase("indicator"))
        {
            c.setForeground(Color.BLUE);
        }
        else {
            c.setForeground(getNormalTextForegroundColor(alert));
        }
        
        c.setBackground(getBackgroundColor(row, alert));

        return c;
    } 
    
    private static final Log log = LogFactory.getLog(StockTableCellRenderer.class);    
}
