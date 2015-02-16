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
import java.util.Date;
import java.util.TimerTask;
import javax.swing.*;
import org.yccheok.jstock.internationalization.GUIBundle;


/**
 *
 * @author yccheok
 */
public class StockTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    private enum Alert {
        FallBelow,
        RiseAbove,
        NoAlert
    }
    
    /** Creates a new instance of StockTableCellRender */
    public StockTableCellRenderer(int horizontalAlignment) {
        super();
        this.setHorizontalAlignment(horizontalAlignment);
    }
    
    private void performCellBlinking(final Component cell, final double value, final double oldValue, final Color finalForegroundColor, final Color finalBackgroundColor, final AbstractTableModelWithMemory tableModel, final int modelRow, final int modelCol) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();

        if (value == oldValue) {
            cell.setForeground(finalForegroundColor);
            cell.setBackground(finalBackgroundColor);
            return;
        } else {
            cell.setForeground(jStockOptions.getAutoUpdateForegroundColor());
            cell.setBackground(jStockOptions.getAutoUpdateBackgroundColor());
        }                
        
        // in ms.
        final int scanningSpeed = JStock.instance().getJStockOptions().getScanningSpeed();
        // Cannot more than 5 seconds.
        int numberOfMillisecondsInTheFuture = Math.min(5000, scanningSpeed);
        Date timeToRun = new Date(System.currentTimeMillis()+numberOfMillisecondsInTheFuture);
        java.util.Timer timer = new java.util.Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tableModel.fireTableCellUpdated(modelRow, modelCol);
            }
        }, timeToRun);                                
    }
    
    private Color getBackgroundColor(int row, Alert alert) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();

        if (alert == Alert.FallBelow) {
            return jStockOptions.getFallBelowAlertBackgroundColor();
        } else if (alert == Alert.RiseAbove) {
            return jStockOptions.getRiseAboveAlertBackgroundColor();
        }

        if (row % 2 == 0) {
            return jStockOptions.getFirstRowBackgroundColor();
        }
        
        return jStockOptions.getSecondRowBackgroundColor();
    }

    private Color getNormalTextForegroundColor(Alert alert) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();

        if (alert == Alert.FallBelow) {
            return jStockOptions.getFallBelowAlertForegroundColor();
        } else if (alert == Alert.RiseAbove) {
            return jStockOptions.getRiseAboveAlertForegroundColor();
        }

        return jStockOptions.getNormalTextForegroundColor();
    }

    private Color getForegroundColor(double value, double ref, Alert alert) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean reverse = org.yccheok.jstock.engine.Utils.isFallBelowAndRiseAboveColorReverse();

        if (alert == Alert.FallBelow) {
            return jStockOptions.getFallBelowAlertForegroundColor();
        } else if (alert == Alert.RiseAbove) {
            return jStockOptions.getRiseAboveAlertForegroundColor();
        }

        if (value > ref) {
            if (reverse) {
                return jStockOptions.getLowerNumericalValueForegroundColor();
            } else {
                return jStockOptions.getHigherNumericalValueForegroundColor();
            }
        }
        else if (value < ref) {
            if (reverse) {
                return jStockOptions.getHigherNumericalValueForegroundColor();
            } else {
                return jStockOptions.getLowerNumericalValueForegroundColor();
            }
        }
        
        return jStockOptions.getNormalTextForegroundColor();
    }
    
    private Component getTableCellRendererComponentWithCellBlinking (
                            Component c, JTable table, Object color,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {        
        assert(isSelected == false);
        
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        
        AbstractTableModelWithMemory tableModel = (AbstractTableModelWithMemory)table.getModel();

        final int modelRow = table.convertRowIndexToModel(row);
        final double prevPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(PREV));
        final double lastPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(LAST));
        Alert alert = Alert.NoAlert;

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
                    alert = Alert.RiseAbove;
                }
            }

            if (fallBelow != null) {
                if (lastPrice <= fallBelow) {
                    alert = Alert.FallBelow;
                }
            }
        }

        if (table.getColumnName(column).equalsIgnoreCase(BUY)) {
            final int modelCol = tableModel.findColumn(BUY);
            
            final double buyPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(buyPrice));
            }
            
            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldBuyPrice = ((o == null) ? buyPrice : (Double)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, buyPrice, oldBuyPrice, this.getForegroundColor(buyPrice, prevPrice, alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        }                         
        else if (table.getColumnName(column).equalsIgnoreCase(SELL)) {
            final int modelCol = tableModel.findColumn(SELL);
            
            final double sellPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(sellPrice));
            }
            
            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldSellPrice = ((o == null) ? sellPrice : (Double)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, sellPrice, oldSellPrice, this.getForegroundColor(sellPrice, prevPrice, alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(LAST)) {
            final int modelCol = tableModel.findColumn(LAST);            

            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(lastPrice));
            }
            
            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldLastPrice = ((o == null) ? lastPrice : (Double)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, lastPrice, oldLastPrice, this.getForegroundColor(lastPrice, prevPrice, alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;          
        }      
        else if (table.getColumnName(column).equalsIgnoreCase(LOW)) {
            final int modelCol = tableModel.findColumn(LOW);
            
            final double lowPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(lowPrice));
            }
            
            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldLowPrice = ((o == null) ? lowPrice : (Double)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, lowPrice, oldLowPrice, this.getForegroundColor(lowPrice, prevPrice, alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(HIGH)) {
            final int modelCol = tableModel.findColumn(HIGH);
            
            final double highPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(highPrice));
            }
            
            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldHighPrice = ((o == null) ? highPrice : (Double)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, highPrice, oldHighPrice, this.getForegroundColor(highPrice, prevPrice, alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG)) {
            final int modelCol = tableModel.findColumn(CHG);
            
            final double changePrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(changePrice));
            }
            
            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldChangePrice = ((o == null) ? changePrice : (Double)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, changePrice, oldChangePrice, this.getForegroundColor(changePrice, 0.0, alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG_PERCENTAGE)) {
            final int modelCol = tableModel.findColumn(CHG_PERCENTAGE);
            
            final double changePricePercentage = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(changePricePercentage));
            }
            
            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final double oldChangePricePercentage = ((o == null) ? changePricePercentage : (Double)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, changePricePercentage, oldChangePricePercentage, this.getForegroundColor(changePricePercentage, 0.0, alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(VOL)) {
            final int modelCol = tableModel.findColumn(VOL);
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            final long volume = (Long)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            final long oldVolume = ((o == null) ? volume : (Long)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, volume, oldVolume, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(LVOL)) {
            final int modelCol = tableModel.findColumn(LVOL);
            
            final int lastVolume = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldLastVolume = ((o == null) ? lastVolume : (Integer)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, lastVolume, oldLastVolume, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        }
        else if (table.getColumnName(column).equalsIgnoreCase(BQTY)) {
            final int modelCol = tableModel.findColumn(BQTY);
            
            final int buyQuantity = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldBuyQuantity = ((o == null) ? buyQuantity : (Integer)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, buyQuantity, oldBuyQuantity, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(SQTY)) {
            final int modelCol = tableModel.findColumn(SQTY);
            
            final int sellQuantity = (Integer)tableModel.getValueAt(modelRow, modelCol);

            final Object o = tableModel.getOldValueAt(modelRow, modelCol);
            final int oldSellQuantity = ((o == null) ? sellQuantity : (Integer)o);
            tableModel.clearOldValueAt(modelRow, modelCol);
            this.performCellBlinking(c, sellQuantity, oldSellQuantity, getNormalTextForegroundColor(alert), getBackgroundColor(row, alert), tableModel, modelRow, modelCol);
            return c;
        }     
        else if (table.getColumnName(column).equalsIgnoreCase(RISE_ABOVE)) {
            final int modelCol = tableModel.findColumn(RISE_ABOVE);
            
            final Double riseAbove = (Double)tableModel.getValueAt(modelRow, modelCol);
            
            if (riseAbove != null) {            
                if (c instanceof JLabel) {
                    JLabel jLabel = (JLabel)c;
                    jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(riseAbove));
                }
            }

            c.setForeground(getNormalTextForegroundColor(alert));
            // No return.
        }
        else if (table.getColumnName(column).equalsIgnoreCase(FALL_BELOW)) {
            final int modelCol = tableModel.findColumn(FALL_BELOW);
            
            final Double fallBelow = (Double)tableModel.getValueAt(modelRow, modelCol);
            
            if (fallBelow != null) {            
                if (c instanceof JLabel) {
                    JLabel jLabel = (JLabel)c;
                    jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(fallBelow));
                }
            }

            c.setForeground(getNormalTextForegroundColor(alert));
            // No return.
        }        
        else if (table.getColumnName(column).equalsIgnoreCase(PREV)) {
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(prevPrice));
            }

            c.setForeground(getNormalTextForegroundColor(alert));
            // No return.
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

        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        
        if (!isSelected && jStockOptions.isEnableColorChange()) {
            return getTableCellRendererComponentWithCellBlinking(c, table, color, isSelected, hasFocus, row, column);
        }
        
        AbstractTableModelWithMemory tableModel = (AbstractTableModelWithMemory)table.getModel();

        final int modelRow = table.convertRowIndexToModel(row);
        final double prevPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(PREV));
        final double lastPrice = (Double)tableModel.getValueAt(modelRow, tableModel.findColumn(LAST));
        Alert alert = Alert.NoAlert;

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
                    alert = Alert.RiseAbove;
                }
            }

            if (fallBelow != null) {
                if (lastPrice <= fallBelow) {
                    alert = Alert.FallBelow;
                }
            }
        }

        if (table.getColumnName(column).equalsIgnoreCase(BUY)) {
            final int modelCol = tableModel.findColumn(BUY);
            
            final double buyPrice = (Double)tableModel.getValueAt(modelRow, modelCol);
            
            if (!isSelected) {
                c.setForeground(this.getForegroundColor(buyPrice, prevPrice, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(buyPrice));
            }
        }                         
        else if (table.getColumnName(column).equalsIgnoreCase(SELL)) {
            final int modelCol = tableModel.findColumn(SELL);
            
            final double sellPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (!isSelected) {
                c.setForeground(this.getForegroundColor(sellPrice, prevPrice, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(sellPrice));
            }            
        }
        else if (table.getColumnName(column).equalsIgnoreCase(OPEN)) {
            final int modelCol = tableModel.findColumn(OPEN);

            final double openPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (!isSelected) {
                c.setForeground(this.getForegroundColor(openPrice, prevPrice, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(openPrice));
            }            
        }
        else if (table.getColumnName(column).equalsIgnoreCase(LAST)) {
            if (!isSelected) {
                c.setForeground(this.getForegroundColor(lastPrice, prevPrice, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(lastPrice));
            }            
        }      
        else if (table.getColumnName(column).equalsIgnoreCase(LOW)) {
            final int modelCol = tableModel.findColumn(LOW);
            
            final double lowPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (!isSelected) {
                c.setForeground(this.getForegroundColor(lowPrice, prevPrice, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(lowPrice));
            }             
        } 
        else if (table.getColumnName(column).equalsIgnoreCase(HIGH)) {
            final int modelCol = tableModel.findColumn(HIGH);
            
            final double highPrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (!isSelected) {
                c.setForeground(this.getForegroundColor(highPrice, prevPrice, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(highPrice));
            }            
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG)) {
            final int modelCol = tableModel.findColumn(CHG);
            
            final double changePrice = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (!isSelected) {
                c.setForeground(this.getForegroundColor(changePrice, 0.0, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(changePrice));
            }            
        }
        else if (table.getColumnName(column).equalsIgnoreCase(CHG_PERCENTAGE)) {
            final int modelCol = tableModel.findColumn(CHG_PERCENTAGE);
            
            final double changePricePercentage = (Double)tableModel.getValueAt(modelRow, modelCol);

            if (!isSelected) {
                c.setForeground(this.getForegroundColor(changePricePercentage, 0.0, alert));
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(changePricePercentage));
            }             
        }
        else if (table.getColumnName(column).equalsIgnoreCase(RISE_ABOVE)) {
            final int modelCol = tableModel.findColumn(RISE_ABOVE);
            
            final Double riseAbove = (Double)tableModel.getValueAt(modelRow, modelCol);
            
            if (!isSelected) {
                c.setForeground(getNormalTextForegroundColor(alert));
            }            
            
            if (riseAbove != null) {
                if (c instanceof JLabel) {
                    JLabel jLabel = (JLabel)c;
                    jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(riseAbove));
                }             
            }
        }
        else if (table.getColumnName(column).equalsIgnoreCase(FALL_BELOW)) {
            final int modelCol = tableModel.findColumn(FALL_BELOW);
            
            final Double fallBelow = (Double)tableModel.getValueAt(modelRow, modelCol);
            
            if (!isSelected) {
                c.setForeground(getNormalTextForegroundColor(alert));
            }
            
            if (fallBelow != null) {
                if (c instanceof JLabel) {
                    JLabel jLabel = (JLabel)c;                
                    jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(fallBelow));
                }                
            }                         
        }
        else if (table.getColumnName(column).equalsIgnoreCase(PREV)) {
            if (!isSelected) {
                c.setForeground(getNormalTextForegroundColor(alert));           
            }
            
            if (c instanceof JLabel) {
                JLabel jLabel = (JLabel)c;
                jLabel.setText(org.yccheok.jstock.watchlist.Utils.toStockPrice(prevPrice));
            }
        }        
        else if (table.getColumnName(column).equalsIgnoreCase(INDICATOR)) {
            if (!isSelected) {
                c.setForeground(Color.BLUE);
            }
        } 
        else {
            if (!isSelected) {
                c.setForeground(getNormalTextForegroundColor(alert));           
            }
        }
        
        if (!isSelected) {
            c.setBackground(getBackgroundColor(row, alert));
        }

        return c;
    } 
    
    private static final String PREV;
    private static final String OPEN;
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
        OPEN = GUIBundle.getString("MainFrame_Open");
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
