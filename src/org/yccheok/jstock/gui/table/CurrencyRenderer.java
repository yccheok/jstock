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

package org.yccheok.jstock.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.portfolio.DepositSummaryTableModel;

/**
 *
 * @author yancheng
 */
public class CurrencyRenderer extends DefaultTableCellRenderer {
    private NumberFormat format = null;

    public CurrencyRenderer() { 
        super();
    }

    @Override
    public void setValue(Object value) {
        if (format == null) {
            format = java.text.NumberFormat.getCurrencyInstance();
        }
        setText((value == null) ? "" : format.format(value));
    }

    private Color getBackgroundColor(int row) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        if(row % 2 == 0) {
            return jStockOptions.getFirstRowBackgroundColor();
        }

        return jStockOptions.getSecondRowBackgroundColor();
    }

    @Override
    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TableModel tableModel = table.getModel();
        if (tableModel instanceof DepositSummaryTableModel)
        {
            DepositSummaryTableModel depositSummaryTableModel = (DepositSummaryTableModel)tableModel;
            int index = table.convertRowIndexToModel(row);
            String comment = depositSummaryTableModel.getDeposit(index).getComment();
            if (comment.length() > 0) {
                ((JComponent) c).setToolTipText(org.yccheok.jstock.gui.Utils.toHTML(comment));
            }
            else {
                ((JComponent) c).setToolTipText(null);
            }
        }

        if(isSelected || hasFocus) return c;

        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        c.setForeground(jStockOptions.getNormalTextForegroundColor());

        c.setBackground(getBackgroundColor(row));

        return c;
    }
}
