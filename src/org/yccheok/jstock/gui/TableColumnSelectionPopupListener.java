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

package org.yccheok.jstock.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Owner
 */
public class TableColumnSelectionPopupListener extends MouseAdapter {
    private final List<String> columnNamesToBeIgnored;

    public TableColumnSelectionPopupListener(int menuStartIndex, String[] columnNamesToBeIgnored) {
        this.menuStartIndex = menuStartIndex;
        this.columnNamesToBeIgnored = java.util.Arrays.asList(columnNamesToBeIgnored);
    }

    public TableColumnSelectionPopupListener(int menuStartIndex) {
        this(menuStartIndex, new String[]{});
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            final Component component = e.getComponent();

            if(component instanceof javax.swing.table.JTableHeader) {
                javax.swing.table.JTableHeader jTableHeader = (javax.swing.table.JTableHeader)component;
                final JTable jTable1 = jTableHeader.getTable();
                getMyTableColumnSelectionPopupMenu(jTable1, e.getX()).show(component, e.getX(), e.getY());
            }
                
        }
    }
    
    private JPopupMenu getMyTableColumnSelectionPopupMenu(final JTable jTable1, final int mouseXLocation) {
        JPopupMenu popup = new JPopupMenu();
        TableModel tableModel = jTable1.getModel();
        final int col = tableModel.getColumnCount();
        
        for (int i = this.menuStartIndex; i < col; i++) {
            String name = tableModel.getColumnName(i);            
            
            // Do not display the menu with name which is ignored by user.
            if (columnNamesToBeIgnored.contains(name)) {
                continue;
            }

            boolean isVisible = true;
            
            try {
                TableColumn tableColumn = jTable1.getColumn(name);
            }
            catch(java.lang.IllegalArgumentException exp) {
                isVisible = false;
            }
            
            javax.swing.JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name, isVisible);
                        
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    String name = evt.getActionCommand();
                    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)evt.getSource();
                    if (menuItem.isSelected() == false) {
                        JTableUtilities.removeTableColumn(jTable1, name);
                    }
                    else {
                        TableColumnModel colModel = jTable1.getColumnModel();
                        int vColIndex = colModel.getColumnIndexAtX(mouseXLocation);
                        JTableUtilities.insertTableColumnFromModel(jTable1, name, vColIndex);
                    }
                }
            });
            
            popup.add(menuItem);            
        }
        
        return popup;
    }
    
    private int menuStartIndex;
}
