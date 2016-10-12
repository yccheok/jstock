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
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.gui.BoundsPopupMenuListener;

/**
 *
 * @author yccheok
 */
public class StockInfoEditor extends DefaultCellEditor {
    public StockInfoEditor(List<StockInfo> stockInfos) {
        super(new JComboBox());
        comboBox = (JComboBox) this.getComponent();
        this.stockInfos = new ArrayList<StockInfo>(stockInfos);
        Collections.sort(this.stockInfos, new Comparator<StockInfo>() {
            @Override
            public int compare(StockInfo o1, StockInfo o2) {
                // Ensure symbols are in alphabetical order.
                return o1.symbol.toString().compareTo(o2.symbol.toString());
            }            
        });

        for (StockInfo stockInfo : this.stockInfos) {
            comboBox.addItem(stockInfo.symbol);
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
        StockInfo stockInfo = (StockInfo)value;
        _comboBox.setSelectedItem(stockInfo.symbol);
        
         BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
         _comboBox.addPopupMenuListener(listener);

        return _comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        int index = comboBox.getSelectedIndex();
        if (index >= stockInfos.size() || index < 0) {
            return null;
        }
        return stockInfos.get(index);
    }

    private final JComboBox comboBox;
    private final List<StockInfo> stockInfos;
}
