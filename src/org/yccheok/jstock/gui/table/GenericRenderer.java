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
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.portfolio.CommentableContainer;

/**
 *
 * @author yccheok
 */
public class GenericRenderer extends DefaultTableCellRenderer {

    protected Color getBackgroundColor(int row) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();

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

        /* Opps! Design Flawn Here! */
        TableModel tableModel = table.getModel();
        if (tableModel instanceof CommentableContainer)
        {
            CommentableContainer commentableContainer = (CommentableContainer)tableModel;
            int index = table.convertRowIndexToModel(row);
            String comment = commentableContainer.getCommentable(index).getComment();
            if (comment.length() > 0) {
                ((JComponent) c).setToolTipText(org.yccheok.jstock.gui.Utils.toHTML(comment));
            }
            else {
                ((JComponent) c).setToolTipText(null);
            }
        }

        if (isSelected || hasFocus) {
            return c;
        }

        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();

        c.setForeground(jStockOptions.getNormalTextForegroundColor());

        c.setBackground(getBackgroundColor(row));

        return c;
    }
}
