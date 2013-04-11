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

import java.util.*;
import org.yccheok.jstock.engine.*;
import org.yccheok.jstock.analysis.*;
import org.yccheok.jstock.file.CSVHelper;
import org.yccheok.jstock.file.GUIBundleWrapper;
import org.yccheok.jstock.file.GUIBundleWrapper.Language;
import org.yccheok.jstock.internationalization.GUIBundle;

/**
 *
 * @author yccheok
 */
public class IndicatorTableModel extends AbstractTableModelWithMemory implements CSVHelper {
    
    /** Creates a new instance of IndicatorTableModel */
    public IndicatorTableModel() {
        for (int i = 0; i < columnNames.length; i++) {
            columnNameMapping.put(columnNames[i], i);
        }        
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }
    
    @Override
    public int getRowCount()
    {
        return tableModel.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        List<Object> indicatorInfo = tableModel.get(rowIndex);
        return indicatorInfo.get(columnIndex);        
    }
    
    @Override
    public Object getOldValueAt(int rowIndex, int columnIndex) {
        List<Object> indicatorInfo = oldTableModel.get(rowIndex);
        
        if (null == indicatorInfo) return null;
        
        return indicatorInfo.get(columnIndex);        
    }
    
    @Override    
    public void clearOldValueAt(int rowIndex, int columnIndex) {
        List<Object> indicatorInfo = oldTableModel.get(rowIndex);
        
        if (null == indicatorInfo) return;
        
        indicatorInfo.set(columnIndex, null);
    }
    
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class getColumnClass(int c) {
        return columnClasses[c];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        throw new java.lang.UnsupportedOperationException();
    }
    
    public void addIndicator(Indicator indicator) {
        Integer row = rowIndicatorMapping.get(getIndicatorKey(indicator));
        
        if (row == null) {
            tableModel.add(indicatorToList(indicator));
            oldTableModel.add(null);
            indicators.add(indicator);
            final int rowIndex = tableModel.size() - 1;
            rowIndicatorMapping.put(getIndicatorKey(indicator), rowIndex);
            fireTableRowsInserted(rowIndex, rowIndex);
        }
        else {
            oldTableModel.set(row, tableModel.get(row));
            tableModel.set(row, indicatorToList(indicator));
            indicators.set(row, indicator);
            this.fireTableRowsUpdated(row, row);            
        }
    }
    
    public void removeIndicator(Indicator indicator) {
        Integer row = rowIndicatorMapping.get(getIndicatorKey(indicator));
        
        if (row != null) {
            int int_row = row;
            tableModel.remove(int_row);
            oldTableModel.remove(int_row);
            indicators.remove(int_row);
            rowIndicatorMapping.remove(getIndicatorKey(indicator));
            
            int size = indicators.size();
            for (int i = int_row; i < size; i++) {
                Indicator otherIndicator = indicators.get(i);
                rowIndicatorMapping.put(getIndicatorKey(otherIndicator), i);
            }
            
            this.fireTableRowsDeleted(int_row, int_row);
        }
    }
    
    private List<Object> indicatorToList(Indicator indicator) {
        List<Object> list = new ArrayList<Object>();
        list.add(indicator.toString());
        
        final Stock stock = indicator.getStock();
        assert(stock != null);
        
        list.add(stock.code);
        list.add(stock.symbol);
        list.add(stock.getPrevPrice());
        list.add(stock.getOpenPrice());
        list.add(stock.getLastPrice());
        list.add(stock.getHighPrice());
        list.add(stock.getLowPrice());
        list.add(stock.getVolume());
        list.add(stock.getChangePrice());
        list.add(stock.getChangePricePercentage());
        list.add(stock.getLastVolume());
        list.add(stock.getBuyPrice());
        list.add(stock.getBuyQuantity());
        list.add(stock.getSellPrice());
        list.add(stock.getSellQuantity());
        
        if(stock instanceof StockEx) {
            StockEx stockEx = (StockEx)stock;
            list.add(stockEx.getMarketCapital());
            list.add(stockEx.getSharesIssued());
        }
        else {
            list.add(0);
            list.add(0);
        }
        
        return list;
    }
    
    public Indicator getIndicator(int row) {
        return indicators.get(row);
    }
    
    public void removeAll() {
        final int size = tableModel.size();
        
        if(size == 0) return;
        
        tableModel.clear();
        oldTableModel.clear();
        indicators.clear();
        rowIndicatorMapping.clear();
        this.fireTableRowsDeleted(0, size - 1);
    }
    
    public void removeRow(int row) {
        List<Object> list = tableModel.remove(row);
        oldTableModel.remove(row);
        indicators.remove(row);        
        // 0 is indicator, 1 is stock code.
        rowIndicatorMapping.remove(list.get(0).toString() + list.get(1).toString());

        int size = indicators.size();
        for(int i=row; i<size; i++) {
            Indicator indicator = indicators.get(i);
            rowIndicatorMapping.put(getIndicatorKey(indicator), i);
        }
        
        this.fireTableRowsDeleted(row, row);
    }
    
    private String getIndicatorKey(Indicator indicator) {
        // Stock shouldn't be null.
        assert(indicator.getStock() != null);
        
        return indicator.toString() + indicator.getStock().code;
    }
    
    @Override
    public int findColumn(String columnName) {
        Integer integer = columnNameMapping.get(columnName);
        if (integer == null) {
            return -1;
        }
        return integer;
    }

    public int findRow(Indicator indicator) {
        Integer row = rowIndicatorMapping.get(getIndicatorKey(indicator));
        if (row != null) return row;
        
        return -1;
    }
    
    private List<List<Object>> tableModel = new ArrayList<List<Object>>();
    private List<List<Object>> oldTableModel = new ArrayList<List<Object>>();
    private List<Indicator> indicators = new ArrayList<Indicator>();
    // Used to get column by Name in fast way.
    private Map<String, Integer> columnNameMapping = new HashMap<String, Integer>();
    // Used to get row by Stock in fast way.
    private Map<String, Integer> rowIndicatorMapping = new HashMap<String, Integer>();
    private static final String[] columnNames;
    private static final String[] languageIndependentColumnNames;    
    private static final Class[] columnClasses = {
        String.class,
        Code.class,
        Symbol.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        // TODO: CRITICAL LONG BUG REVISED NEEDED.
        Long.class,
        Double.class,
        Double.class,
        Integer.class,
        Double.class,
        Integer.class,
        Double.class,
        Integer.class,
        Long.class,
        Long.class
    };

    static {
        final String[] tmp = {
            GUIBundle.getString("IndicatorScannerJPanel_Indicator"),
            GUIBundle.getString("MainFrame_Code"),
            GUIBundle.getString("MainFrame_Symbol"),
            GUIBundle.getString("MainFrame_Prev"),
            GUIBundle.getString("MainFrame_Open"),
            GUIBundle.getString("MainFrame_Last"),
            GUIBundle.getString("MainFrame_High"),
            GUIBundle.getString("MainFrame_Low"),
            GUIBundle.getString("MainFrame_Vol"),
            GUIBundle.getString("MainFrame_Chg"),
            GUIBundle.getString("MainFrame_ChgPercentage"),
            GUIBundle.getString("MainFrame_LVol"),
            GUIBundle.getString("MainFrame_Buy"),
            GUIBundle.getString("MainFrame_BQty"),
            GUIBundle.getString("MainFrame_Sell"),
            GUIBundle.getString("MainFrame_SQty"),
            GUIBundle.getString("IndicatorScannerJPanel_MCapital"),
            GUIBundle.getString("IndicatorScannerJPanel_SIssued")
        };
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(Language.INDEPENDENT);        
        final String[] tmp2 = {
            guiBundleWrapper.getString("IndicatorScannerJPanel_Indicator"),
            guiBundleWrapper.getString("MainFrame_Code"),
            guiBundleWrapper.getString("MainFrame_Symbol"),
            guiBundleWrapper.getString("MainFrame_Prev"),
            guiBundleWrapper.getString("MainFrame_Open"),
            guiBundleWrapper.getString("MainFrame_Last"),
            guiBundleWrapper.getString("MainFrame_High"),
            guiBundleWrapper.getString("MainFrame_Low"),
            guiBundleWrapper.getString("MainFrame_Vol"),
            guiBundleWrapper.getString("MainFrame_Chg"),
            guiBundleWrapper.getString("MainFrame_ChgPercentage"),
            guiBundleWrapper.getString("MainFrame_LVol"),
            guiBundleWrapper.getString("MainFrame_Buy"),
            guiBundleWrapper.getString("MainFrame_BQty"),
            guiBundleWrapper.getString("MainFrame_Sell"),
            guiBundleWrapper.getString("MainFrame_SQty"),
            guiBundleWrapper.getString("IndicatorScannerJPanel_MCapital"),
            guiBundleWrapper.getString("IndicatorScannerJPanel_SIssued")
        };
        
        columnNames = tmp;
        languageIndependentColumnNames = tmp2;
    }

    @Override
    public String getLanguageIndependentColumnName(int columnIndex) {
        return languageIndependentColumnNames[columnIndex];
    }
}
