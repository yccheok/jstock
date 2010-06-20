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

import java.awt.*;
import java.text.DateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.table.*;
//import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.yccheok.jstock.gui.table.DateRendererDecoratorEx;
import org.yccheok.jstock.gui.table.JDateChooserCellEditorEx;
import org.yccheok.jstock.internationalization.GUIBundle;

public class JTableUtilities {

    /** Creates a new instance of JTableUtilities */
    private JTableUtilities() {
    }

    // Resize table column. User will still able to adjust the width manually.
    public static void makeTableColumnWidthFit(JTable jTable, int col, int margin) {
        makeTableColumnWidthFit(jTable, col, margin, false);
    }

    public static void makeTableColumnWidthFit(JTable jTable, int col, int margin, boolean locking) {
        // strategy - get max width for cells in column and
        // make that the preferred width
        TableColumnModel columnModel = jTable.getColumnModel();
        int maxwidth = 0;

        for (int row=0; row<jTable.getRowCount(); row++) {
            TableCellRenderer rend = jTable.getCellRenderer(row, col);
            Object value = jTable.getValueAt (row, col);
            Component comp = rend.getTableCellRendererComponent (
                jTable,
                value,
                false,
                false,
                row,
                col);

            maxwidth = Math.max (comp.getPreferredSize().width + margin, maxwidth);
        } // for row

        TableColumn column = columnModel.getColumn (col);
        TableCellRenderer headerRenderer = column.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = jTable.getTableHeader().getDefaultRenderer();
        }
        Object headerValue = column.getHeaderValue();
        Component headerComp = headerRenderer.getTableCellRendererComponent (
            jTable,
            headerValue,
            false,
            false,
            0,
            col);

        maxwidth = Math.max (maxwidth, headerComp.getPreferredSize().width + margin);        
        column.setPreferredWidth (maxwidth);
        if (locking) {
            // User will not able to adjust the width manually.
            column.setMinWidth(maxwidth);
            column.setMaxWidth(maxwidth);
        }
    }

    public static void setJTableOptions(JTable jTable, GUIOptions.JTableOptions jTableOptions)
    {
        final Locale locale = jTableOptions.getLocale();
        
    	// Remove unwanted column. MUST BE DONE FIRST!
        for (int i = 0; i < jTable.getColumnCount(); i++) {
            final String name = jTable.getColumnName(i);

            // In to avoid the following situation.
            // 1. GUIOptions is being saved when we are in Chinese locale.
            // 2. Application is restarted in English locale.
            // 3. We are trying to compare English wording with Chinese wording.
            //
            // Before performing comparison, we shall convert name, to the locale
            // of table options.
            final String key = getKey(name, Locale.getDefault());

            // Ensure correct resource file is being loaded.
            // When ResourceBundle.getBundle(..., locale) is being called, the
            // system will try to search in the following sequence.
            // 1. gui_<locale>.properties.
            // 2. gui_<default_locale>.properties.
            // 3. gui.properties.
            final Locale oldLocale = Locale.getDefault();
            Locale.setDefault(locale);
            try {
                final ResourceBundle bundle = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", locale);
                final String translated_name = key == null ? name : bundle.getString(key);
                Locale.setDefault(oldLocale);

                /* Remove any unwanted columns. */
                if (jTableOptions.contains(translated_name) == false)
                {
                    removeTableColumn(jTable, name);
                    i--;
                }
            }
            finally {
                Locale.setDefault(oldLocale);
            }
        }

        final int optionsCount = jTableOptions.getColumnSize();
        final int tableCount = jTable.getColumnCount();

        int target = 0;
        /* Sort the columns according to user preference. */
        for (int i = 0; i < optionsCount; i++) {
            final String name = jTableOptions.getColumnName(i);
            final String key = getKey(name, locale);
            final String translated_name = key == null ? name : GUIBundle.getString(key);

            int index = -1;
            for (int j = 0; j < tableCount; j++) {
                if (jTable.getColumnName(j).equals(translated_name))
                {
                    /* Restore width. */
                    jTable.getColumn(translated_name).setPreferredWidth(jTableOptions.getColumnWidth(i));

                    index = j;
                    break;
                }
            }

            if (index >= 0)
            {
                jTable.moveColumn(index, target++);
            }
        }      
    }

    public static void removeTableColumn(JTable jTable, Object identifier) {
        jTable.removeColumn(jTable.getColumn(identifier));

    }

    

    public static void insertTableColumnFromModel(JTable jTable, Object value, int targetColumn) {        

        boolean isVisible = true;

        

        try {

            TableColumn tableColumn = jTable.getColumn(value);

        }

        catch(java.lang.IllegalArgumentException exp) {

            isVisible = false;

        }

        

        if(isVisible) return;

                

        TableModel tableModel = jTable.getModel();

        final int modelIndex = getModelColumnIndex(jTable, value);

        Class c = tableModel.getColumnClass(modelIndex);

        TableColumn tableColumn = new javax.swing.table.TableColumn(modelIndex, 0, jTable.getDefaultRenderer(c), jTable.getDefaultEditor(c));        

        jTable.addColumn(tableColumn);

        makeTableColumnWidthFit(jTable, jTable.getColumnCount() - 1, 5);

        jTable.moveColumn(jTable.getColumnCount() - 1, targetColumn);

    }

    

    public static int getModelColumnIndex(JTable jTable, Object value) {

        TableModel tableModel = jTable.getModel();

        

        if(tableModel instanceof StockTableModel) {

            return ((StockTableModel)tableModel).findColumn(value.toString());

        }

        

        try {

            TableColumn tableColumn = jTable.getColumn(value);

            return tableColumn.getModelIndex();

        }   // Anti-pattern. We are depending on the exception throwing. Bad!

        catch(java.lang.IllegalArgumentException exp) {            

            final int columnCount = tableModel.getColumnCount();

            for(int col=0; col<columnCount; col++) {

                String s = tableModel.getColumnName(col);

                

                if(s.equals(value))

                    return col;

            }

        }

        

        return -1;

    }      
    
    public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        
        JViewport viewport = (JViewport)table.getParent();
    
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    
        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();
    
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);
    
        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }

    /** Sets the editor/renderer for Date objects to provided JTable, for the specified column.
     * @param table JTable to set up
     * @param row Column to apply
     */
    public static void setDateEditorAndRendererForRow(JTable table, int row) {
        final TableColumn column = table.getColumnModel().getColumn(row);
        // SwingX's. Pretty but buggy.
        //column.setCellEditor(new DatePickerCellEditor());
        column.setCellEditor(new JDateChooserCellEditorEx());
        final DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        column.setCellRenderer(new DateRendererDecoratorEx(column.getCellRenderer(), format));
    }

    /**
     * Get the key for a given string and locale.
     *
     * @param string the string for the desired key
     * @param locale the locale for the desired key
     * @return the key for a given string and locale
     */
    private static String getKey(String string, Locale locale) {
        if (string2KeyMap.containsKey(locale)) {
            final Map<String, String> string2Key = string2KeyMap.get(locale);
            return string2Key.get(string);
        }

        final Map<String, String> string2Key = new HashMap<String, String>();

        // Ensure correct resource file is being loaded.
        // When ResourceBundle.getBundle(..., locale) is being called, the
        // system will try to search in the following sequence.
        // 1. gui_<locale>.properties.
        // 2. gui_<default_locale>.properties.
        // 3. gui.properties.
        final Locale oldLocale = Locale.getDefault();
        Locale.setDefault(locale);
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle("org.yccheok.jstock.data.gui", locale);

            final Enumeration<String> enumeration = bundle.getKeys();
            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                string2Key.put(bundle.getString(key), key);
            }

            string2KeyMap.put(locale, string2Key);
        }
        finally {
            Locale.setDefault(oldLocale);
        }
        return string2Key.get(string);
    }

    private static final Map<Locale, Map<String, String>> string2KeyMap = new HashMap<Locale, Map<String, String>>();
}

