/*

 * JTableUtilities.java

 *

 * Created on April 3, 2007, 12:28 AM

 *

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

 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>

 */



package org.yccheok.jstock.gui;



import java.awt.*;

import java.text.DateFormat;
import javax.swing.*;

import javax.swing.table.*;
import net.sf.nachocalendar.table.DateFieldTableEditor;
import org.yccheok.jstock.gui.table.DateRendererDecoratorEx;



/**

 *

 * @author doraemon

 */

public class JTableUtilities {

    

    /** Creates a new instance of JTableUtilities */

    private JTableUtilities() {

    }



    public static void makeTableColumnWidthFit(JTable jTable, int col, int margin) {

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

	if (headerRenderer == null)

            headerRenderer = jTable.getTableHeader().getDefaultRenderer();

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

    }

    public static void setJTableOptions(JTable jTable, GUIOptions.JTableOptions jTableOptions)
    {
    	// Remove unwanted column. MUST BE DONE FIRST!
        for (int i = 0; i < jTable.getColumnCount(); i++) {
            final String name = jTable.getColumnName(i);

            /* Remove any unwanted columns. */
            if (jTableOptions.contains(name) == false)
            {
                removeTableColumn(jTable, name);
                i--;
            }
        }

        final int optionsCount = jTableOptions.getColumnSize();
        final int tableCount = jTable.getColumnCount();

        int target = 0;
        /* Sort the columns according to user preference. */
        for (int i = 0; i < optionsCount; i++) {
            final String name = jTableOptions.getColumnName(i);
            int index = -1;
            for (int j = 0; j < tableCount; j++) {
                if (jTable.getColumnName(j).equals(name))
                {
                    /* Restore width. */
                    jTable.getColumn(name).setPreferredWidth(jTableOptions.getColumnWidth(i));

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
    public static void setDateEditorForRow(JTable table, int row) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        setDateEditorForRow(table, row, format);
    }

    /** Sets the editor/renderer for Date objects to provided JTable, for the specified column.
     * @param table JTable to set up
     * @param row Column to apply
     * @param format Format to use
     */
    private static void setDateEditorForRow(JTable table, int row, DateFormat format) {
        TableColumn column = table.getColumnModel().getColumn(row);
        column.setCellEditor(new DateFieldTableEditor());
        column.setCellRenderer(new DateRendererDecoratorEx(column.getCellRenderer(), format));
    }
}

