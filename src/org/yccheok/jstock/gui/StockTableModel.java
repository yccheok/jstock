/*
 * StockTableModel.java
 *
 * Created on May 1, 2007, 12:44 AM
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

import java.util.*;
import javax.swing.table.*;
import org.yccheok.jstock.engine.*;

/**
 *
 * @author yccheok
 */
public class StockTableModel extends AbstractTableModelWithMemory {
    
    /** Creates a new instance of StockTableModel */
    public StockTableModel() {
        for(int i = 0; i < columnNames.length; i++) {
            columnNameMapping.put(columnNames[i], i);
        }
                
    }

    public Object getOldValueAt(int rowIndex, int columnIndex) {
        List<Object> stockInfo = oldTableModel.get(rowIndex);
        
        if(null == stockInfo) return null;
        
        return stockInfo.get(columnIndex);        
    }
    
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        List<Object> stockInfo = tableModel.get(rowIndex);
        return stockInfo.get(columnIndex);
    }
    
    public int getColumnCount()
    {
        return columnNames.length;
    }
    
    public int getRowCount()
    {
        return tableModel.size();
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int c) {
        return columnClasses[c];
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        throw new java.lang.UnsupportedOperationException();
    }
    
    public void updateStock(Stock stock) {
        Integer row = rowStockCodeMapping.get(stock.getCode());
        
        if(row != null) {
            oldTableModel.set(row, tableModel.get(row));
            tableModel.set(row, stockToList(stock));            
            stocks.set(row, stock);
            this.fireTableRowsUpdated(row, row);            
        }        
    }
    
    public void addStock(Stock stock) {
        Integer row = rowStockCodeMapping.get(stock.getCode());
        
        if(row == null) {
            tableModel.add(stockToList(stock));
            oldTableModel.add(null);
            stocks.add(stock);
            final int rowIndex = tableModel.size() - 1;
            rowStockCodeMapping.put(stock.getCode(), rowIndex);
            fireTableRowsInserted(rowIndex, rowIndex);
        }
    }
    
    public void clearAllStocks() {
        final int size = stocks.size();
        
        if(size == 0) return;
        
        tableModel.clear();
        oldTableModel.clear();
        stocks.clear();
        rowStockCodeMapping.clear();
            
        this.fireTableRowsDeleted(0, size - 1);
    }
    
    public void removeStock(Stock stock) {
        Integer row = rowStockCodeMapping.get(stock.getCode());
        
        if(row != null) {
            tableModel.remove(row);
            oldTableModel.remove(row);
            stocks.remove(row);
            rowStockCodeMapping.remove(stock.getCode());
            
            int size = stocks.size();
            for(int i=row; i<size; i++) {
                Stock s = stocks.get(i);
                rowStockCodeMapping.put(s.getCode(), i);
            }
            
            this.fireTableRowsDeleted(row, row);
        }
    }
    
    public Stock getStock(int row) {
        return stocks.get(row);
    }
    
    public List<Stock> getStocks() {
        return Collections.unmodifiableList(stocks);
    }
    
    public void removeRow(int row) {
        oldTableModel.remove(row);
        List<Object> list = tableModel.remove(row);
        stocks.remove(row);        
        // 0 is stock code.
        rowStockCodeMapping.remove(list.get(0));

        int size = stocks.size();
        for(int i=row; i<size; i++) {
            Stock s = stocks.get(i);
            rowStockCodeMapping.put(s.getCode(), i);
        }
        
        this.fireTableRowsDeleted(row, row);
    }
    
    private List<Object> stockToList(Stock stock) {
        List<Object> list = new ArrayList<Object>();
        list.add(stock.getCode());
        list.add(stock.getSymbol());
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
        
        return list;
    }
    
    public int findColumn(String columnName) {
        return columnNameMapping.get(columnName);
    }

    public int findRow(Stock stock) {
        Integer row = rowStockCodeMapping.get(stock.getCode());
        if(row != null) return row;
        
        return -1;
    }
    
    private List<List<Object>> tableModel = new ArrayList<List<Object>>();
    private List<List<Object>> oldTableModel = new ArrayList<List<Object>>();
    private List<Stock> stocks = new ArrayList<Stock>();
    // Used to get column by Name in fast way.
    private Map<String, Integer> columnNameMapping = new HashMap<String, Integer>();
    // Used to get row by Stock in fast way.
    private Map<String, Integer> rowStockCodeMapping = new HashMap<String, Integer>();
    private String[] columnNames =  {"Code",        "Symbol",       "Open",     "Last",        "High",         "Low",      "Vol",          "Chg",      "Chg (%)",      "L.Vol",        "Buy",      "B.Qty",        "Sell",         "S.Qty"};
    private Class[] columnClasses = {String.class, String.class, Double.class, Double.class, Double.class, Double.class, Integer.class, Double.class, Double.class, Integer.class, Double.class, Integer.class, Double.class, Integer.class};
    
}
