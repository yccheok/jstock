/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        Collections.sort(this.stocks, new Comparator<Stock>() {
            @Override
            public int compare(Stock o1, Stock o2) {
                // Ensure symbols are in alphabetical order.
                return o1.getSymbol().toString().compareTo(o2.getSymbol().toString());
            }            
        });

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
        Stock stock = (Stock)value;
        _comboBox.setSelectedItem(stock.getSymbol());
        return _comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        int index = comboBox.getSelectedIndex();
        if (index >= stocks.size() || index < 0) {
            return null;
        }
        return stocks.get(index);
    }

    private final JComboBox comboBox;
    private final List<Stock> stocks;
}
