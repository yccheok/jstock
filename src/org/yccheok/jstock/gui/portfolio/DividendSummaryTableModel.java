/*
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
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui.portfolio;

import javax.swing.table.AbstractTableModel;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.Symbol;
import org.yccheok.jstock.file.CSVHelper;
import org.yccheok.jstock.file.GUIBundleWrapper;
import org.yccheok.jstock.file.GUIBundleWrapper.Language;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.Commentable;
import org.yccheok.jstock.portfolio.Dividend;
import org.yccheok.jstock.portfolio.DividendSummary;

/**
 *
 * @author yccheok
 */
public class DividendSummaryTableModel extends AbstractTableModel implements CommentableContainer, CSVHelper {

    public DividendSummaryTableModel(DividendSummary dividendSummary) {
        this.dividendSummary = dividendSummary;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        final Dividend dividend = dividendSummary.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return dividend.date.getCalendar().getTime();

            case 1:
                return dividend.stockInfo;
                
            case 2:
                return dividend.amount;
        }

        return null;
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }


    @Override
    public int getRowCount()
    {
        return dividendSummary.size();
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
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        switch(col) {
            case 0: {
                Dividend newDividend = dividendSummary.get(row).setDate(new SimpleDate((java.util.Date)value));
                dividendSummary.remove(row);
                dividendSummary.add(row, newDividend);
                fireTableCellUpdated(row, col);
                break;
            }

            case 1: {
                Dividend newDividend = dividendSummary.get(row).setStockInfo((StockInfo)value);
                dividendSummary.remove(row);
                dividendSummary.add(row, newDividend);
                fireTableCellUpdated(row, col);
                break;
            }
            
            case 2: {
                Dividend newDividend = dividendSummary.get(row).setAmount((Double)value);
                dividendSummary.remove(row);
                dividendSummary.add(row, newDividend);
                fireTableCellUpdated(row, col);
                break;
            }
        }
    }

    public void removeRow(int index) {
        dividendSummary.remove(index);
        this.fireTableRowsDeleted(index, index);
    }

    public int add(Dividend dividend) {
        dividendSummary.add(dividend);
        final int index = dividendSummary.size() - 1;
        this.fireTableRowsInserted(index, index);
        return index;        
    }
    
    public int addNewDividend() {
        return this.add(new Dividend(StockInfo.newInstance(Code.newInstance(""), Symbol.newInstance("")), 0.0, new SimpleDate()));
    }

    public Dividend getDividend(int index) {
        return dividendSummary.get(index);
    }

    @Override
    public Commentable getCommentable(int index) {
        return dividendSummary.get(index);
    }

    private static final String[] columnNames;
    private static final String[] languageIndependentColumnNames;
    private static final Class[] columnClasses = {
        java.util.Date.class,
        StockInfo.class,
        Double.class
    };
    private final DividendSummary dividendSummary;

    static {
        final String[] tmp = {
            GUIBundle.getString("PortfolioManagementJPanel_Date"),
            GUIBundle.getString("PortfolioManagementJPanel_Stock"),
            GUIBundle.getString("PortfolioManagementJPanel_Dividend")
        };
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(Language.INDEPENDENT);
        final String[] tmp2 = {
            guiBundleWrapper.getString("PortfolioManagementJPanel_Date"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Stock"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Dividend")
        };        
        columnNames = tmp;
        languageIndependentColumnNames = tmp2;
    }

    @Override
    public String getLanguageIndependentColumnName(int columnIndex) {
        return languageIndependentColumnNames[columnIndex];
    }
}
