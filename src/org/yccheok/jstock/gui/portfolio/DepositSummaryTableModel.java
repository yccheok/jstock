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
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.file.CSVHelper;
import org.yccheok.jstock.file.GUIBundleWrapper;
import org.yccheok.jstock.file.GUIBundleWrapper.Language;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.Commentable;
import org.yccheok.jstock.portfolio.Deposit;
import org.yccheok.jstock.portfolio.DepositSummary;

/**
 *
 * @author yccheok
 */
public class DepositSummaryTableModel extends AbstractTableModel implements CommentableContainer, CSVHelper {

    public DepositSummaryTableModel(DepositSummary depositSummary) {
        this.depositSummary = depositSummary;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        final Deposit deposit = depositSummary.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return deposit.getDate().getCalendar().getTime();

            case 1:
                return deposit.getAmount();
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
        return depositSummary.size();
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
                Deposit newDeposit = depositSummary.get(row).setDate(new SimpleDate((java.util.Date)value));
                depositSummary.remove(row);
                depositSummary.add(row, newDeposit);
                fireTableCellUpdated(row, col);
                break;
            }

            case 1: {
                Deposit newDeposit = depositSummary.get(row).setAmount((Double)value);
                depositSummary.remove(row);
                depositSummary.add(row, newDeposit);
                fireTableCellUpdated(row, col);
                break;
            }
        }
    }

    public void removeRow(int index) {
        depositSummary.remove(index);
        this.fireTableRowsDeleted(index, index);
    }

    public int addNewDeposit() {
        depositSummary.add(new Deposit(0.0, new SimpleDate()));
        final int index = depositSummary.size() - 1;
        this.fireTableRowsInserted(index, index);
        return index;
    }

    public Deposit getDeposit(int index) {
        return depositSummary.get(index);
    }

    @Override
    public Commentable getCommentable(int index) {
        return depositSummary.get(index);
    }
    
    private static final String[] columnNames;
    // Unlike cNames, languageIndependentColumnNames is language independent.
    private static final String[] languageIndependentColumnNames;
    
    private static final Class[] columnClasses = {
        java.util.Date.class,
        Double.class
    };
    private final DepositSummary depositSummary;

    static {
        final String[] tmp = {
            GUIBundle.getString("PortfolioManagementJPanel_Date"),
            GUIBundle.getString("PortfolioManagementJPanel_Cash")
        };
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(Language.INDEPENDENT);
        final String[] tmp2 = {
            guiBundleWrapper.getString("PortfolioManagementJPanel_Date"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Cash")
        };        
        columnNames = tmp;
        languageIndependentColumnNames = tmp2;
    }

    @Override
    public String getLanguageIndependentColumnName(int columnIndex) {
        return languageIndependentColumnNames[columnIndex];
    }
}
