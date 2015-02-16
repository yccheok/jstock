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

package org.yccheok.jstock.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import net.sf.nachocalendar.table.DateRendererDecorator;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.portfolio.DepositSummaryTableModel;

/**
 *
 * @author yccheok
 */

// For us to decide background color.
public class DateRendererDecoratorEx extends DateRendererDecorator {
    public DateRendererDecoratorEx(TableCellRenderer renderer, DateFormat format) {
        super(renderer, format);
    }

    private Color getBackgroundColor(int row) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();

        if (row % 2 == 0) {
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

        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();

        c.setForeground(jStockOptions.getNormalTextForegroundColor());

        c.setBackground(getBackgroundColor(row));

        return c;
    }
}
