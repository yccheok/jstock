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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import org.yccheok.jstock.engine.Stock;

/**
 *
 * @author yccheok
 */
public class StockEditor extends DefaultCellEditor {
    public StockEditor(List<Stock> stocks) {
        super(new JComboBox());
        comboBox = (JComboBox) this.getComponent();
        this.stocks = new ArrayList<Stock>(stocks);
        for (Stock stock : this.stocks) {
            comboBox.addItem(stock.getSymbol());
        }
    }

    //Implement the one method defined by TableCellEditor.
    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        JComboBox _comboBox = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        currentStock = (Stock)value;
        _comboBox.setSelectedItem(currentStock.getSymbol());
        return _comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        int index = comboBox.getSelectedIndex();
        return stocks.get(index);
    }

    private final JComboBox comboBox;
    private Stock currentStock;
    private List<Stock> stocks = new ArrayList<Stock>();
}
