/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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
import org.yccheok.jstock.internationalization.GUIBundle;


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

        if (value == oldValue) {
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

        if (value > ref) {
            return jStockOptions.getHigherNumericalValueForegroundColor();
        }
        else if (value < ref) {
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

        final int modelRow = table.convertRowIndexToModel(row);
        final double prevPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(PREV));
        final double lastPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(LAST));

        boolean alert = false;

        // Using lastPrice = 0 to compare against fall below and rise above
        // target price is meaningless. In normal condition, no stock price
        // shall fall until 0. When we get last price is 0, most probably
        // market is not opened yet.
        if (lastPrice > 0.0 && jStockOptions.isEnableColorAlert()) {
            final int riseAboveIndex = tableModel.findColumn(RISE_ABOVE);
            final int fallBelowIndex = tableModel.findColumn(FALL_BELOW);
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

        if (table.getColumnName(column).equalsIgnoreCase(BUY)) {
            final int modelCol = tableModel.findColumn(BUY);
            
            final double buyPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldBuyPrice = ((o == null) ? buyPrice : (Double)o);
            this.performCellBlinking(c, buyPrice, oldBuyPrice, this.getForegroundColor(buyPrice, prevPrice, alert), getBackgroundColor(row, alert));
            return c;
        }                         
        else if (table.getColumnName(column).equalsIgnoreCase(SELL)) {
            final int modelCol = tableModel.findColumn(SELL);
            
            final double sellPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldSellPrice = ((o == null) ? sellPrice : (Double)o);
            this.performCellBlinking(c, sellPrice, oldSellPrice, this.getForegroundColor(sellPrice, prevPrice, alert), getBackgroundColor(row, alert));
            return c;
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(LAST)) {
            final int modelCol = tableModel.findColumn(LAST);            

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldLastPrice = ((o == null) ? lastPrice : (Double)o);
            this.performCellBlinking(c, lastPrice, oldLastPrice, this.getForegroundColor(lastPrice, prevPrice, alert), getBackgroundColor(row, alert));
            return c;          
        }      
        else if (table.getColumnName(column).equalsIgnoreCase(LOW)) {
            final int modelCol = tableModel.findColumn(LOW);
            
            final double lowPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldLowPrice = ((o == null) ? lowPrice : (Double)o);
            this.performCellBlinking(c, lowPrice, oldLowPrice, this.getForegroundColor(lowPrice, prevPrice, alert), getBackgroundColor(row, alert));
            return c;
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(HIGH)) {
            final int modelCol = tableModel.findColumn(HIGH);
            
            final double highPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldHighPrice = ((o == null) ? highPrice : (Double)o);
            this.performCellBlinking(c, highPrice, oldHighPrice, this.getForegroundColor(highPrice, prevPrice, alert), getBackgroundColor(row, alert));
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG)) {
            final int modelCol = tableModel.findColumn(CHG);
            
            final double changePrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldChangePrice = ((o == null) ? changePrice : (Double)o);
            this.performCellBlinking(c, changePrice, oldChangePrice, this.getForegroundColor(changePrice, 0.0, alert), getBackgroundColor(row, alert));
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG_PERCENTAGE)) {
            final int modelCol = tableModel.findColumn(CHG_PERCENTAGE);
            
            final double changePricePercentage = (Double)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldChangePricePercentage = ((o == null) ? changePricePercentage : (Double)o);
            this.performCellBlinking(c, changePricePercentage, oldChangePricePercentage, this.getForegroundColor(changePricePercentage, 0.0, alert), getBackgroundColor(row, alert));
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(VOL)) {
            final int modelCol = tableModel.findColumn(VOL);
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            final long volume = (Long)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            final long oldVolume = ((o == null) ? volume : (Long)o);
            this.performCellBlinking(c, volume, oldVolume, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(LVOL)) {
            final int modelCol = tableModel.findColumn(LVOL);
            
            final int lastVolume = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldLastVolume = ((o == null) ? lastVolume : (Integer)o);
            this.performCellBlinking(c, lastVolume, oldLastVolume, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(BQTY)) {
            final int modelCol = tableModel.findColumn(BQTY);
            
            final int buyQuantity = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldBuyQuantity = ((o == null) ? buyQuantity : (Integer)o);
            this.performCellBlinking(c, buyQuantity, oldBuyQuantity, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(SQTY)) {
            final int modelCol = tableModel.findColumn(SQTY);
            
            final int sellQuantity = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldSellQuantity = ((o == null) ? sellQuantity : (Integer)o);
            this.performCellBlinking(c, sellQuantity, oldSellQuantity, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert));
            return c;
        }      
        else if (table.getColumnName(column).equalsIgnoreCase(INDICATOR)) {
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
        
        if (jStockOptions.isEnableColorChange()) {
            return getTableCellRendererComponentWithCellBlinking(c, table, color, isSelected, hasFocus, row, column);
        }
        
        AbstractTableModelWithMemory tableModel = (AbstractTableModelWithMemory)table.getModel();

        final int modelRow = table.convertRowIndexToModel(row);
        final double prevPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(PREV));
        final double lastPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(LAST));

        boolean alert = false;

        // Using lastPrice = 0 to compare against fall below and rise above
        // target price is meaningless. In normal condition, no stock price
        // shall fall until 0. When we get last price is 0, most probably
        // market is not opened yet.
        if (lastPrice > 0.0 && jStockOptions.isEnableColorAlert()) {
            final int riseAboveIndex = tableModel.findColumn(RISE_ABOVE);
            final int fallBelowIndex = tableModel.findColumn(FALL_BELOW);

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

        if (table.getColumnName(column).equalsIgnoreCase(BUY)) {
            final int modelCol = tableModel.findColumn(BUY);
            
            final double buyPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(buyPrice, prevPrice, alert));
        }                         
        else if (table.getColumnName(column).equalsIgnoreCase(SELL)) {
            final int modelCol = tableModel.findColumn(SELL);
            
            final double sellPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(sellPrice, prevPrice, alert));
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(LAST)) {
            c.setForeground(this.getForegroundColor(lastPrice, prevPrice, alert));
        }      
        else if (table.getColumnName(column).equalsIgnoreCase(LOW)) {
            final int modelCol = tableModel.findColumn(LOW);
            
            final double lowPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(lowPrice, prevPrice, alert));
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(HIGH)) {
            final int modelCol = tableModel.findColumn(HIGH);
            
            final double highPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(highPrice, prevPrice, alert));
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG)) {
            final int modelCol = tableModel.findColumn(CHG);
            
            final double changePrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(changePrice, 0.0, alert));
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG_PERCENTAGE)) {
            final int modelCol = tableModel.findColumn(CHG_PERCENTAGE);
            
            final double changePricePercentage = (Double)tableModel.getValueAt(modelRow, modelCol);

            c.setForeground(this.getForegroundColor(changePricePercentage, 0.0, alert));
        }
        else if (table.getColumnName(column).equalsIgnoreCase(INDICATOR)) {
            c.setForeground(Color.BLUE);
        }
        else {
            c.setForeground(getNormalTextForegroundColor(alert));
        }
        
        c.setBackground(getBackgroundColor(row, alert));

        return c;
    } 
    
    private static final String PREV;
    private static final String RISE_ABOVE;
    private static final String FALL_BELOW;
    private static final String LAST;
    private static final String BUY;
    private static final String SELL;
    private static final String LOW;
    private static final String HIGH;
    private static final String CHG;
    private static final String CHG_PERCENTAGE;
    private static final String VOL;
    private static final String LVOL;
    private static final String BQTY;
    private static final String SQTY;
    private static final String INDICATOR;

    static {
        PREV = GUIBundle.getString("MainFrame_Prev");
        RISE_ABOVE = GUIBundle.getString("MainFrame_RiseAbove");
        FALL_BELOW = GUIBundle.getString("MainFrame_FallBelow");
        LAST = GUIBundle.getString("MainFrame_Last");
        BUY = GUIBundle.getString("MainFrame_Buy");
        SELL = GUIBundle.getString("MainFrame_Sell");
        LOW = GUIBundle.getString("MainFrame_Low");
        HIGH = GUIBundle.getString("MainFrame_High");
        CHG = GUIBundle.getString("MainFrame_Chg");
        CHG_PERCENTAGE = GUIBundle.getString("MainFrame_ChgPercentage");
        VOL = GUIBundle.getString("MainFrame_Vol");
        LVOL = GUIBundle.getString("MainFrame_LVol");
        BQTY = GUIBundle.getString("MainFrame_BQty");
        SQTY = GUIBundle.getString("MainFrame_SQty");
        INDICATOR = GUIBundle.getString("IndicatorScannerJPanel_Indicator");
    }
}
