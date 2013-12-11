/*
 *  NachoCalendar
 *
 * Project Info:  http://nachocalendar.sf.net
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * Changes
 * -------
 *  2005-6-18   Fixed DateRendererDecorator constructor
 * -------
 * 
 * JTableCustomizer.java
 * 
 * Created on 29/12/2004
 *
 */
package net.sf.nachocalendar.table;

import java.text.DateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 * @author Ignacio Merani
 *
 */
public final class JTableCustomizer {
    
    /**
     * Constructor declared as privete to prevent instantiation.
     */
    private JTableCustomizer() {
    }
    
    /** Sets the default editor/renderer for Date objets to provided JTable.
     * @param table JTable to set up
     */
    public static void setDefaultEditor(JTable table) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        setDefaultEditor(table, format);
    }
    
    /** Sets the default editor/renderer for Date objets to provided JTable.
     * @param table JTable to set up
     * @param format Format to use
     */
    public static void setDefaultEditor(JTable table, DateFormat format) {
        table.setDefaultEditor(Date.class, new DateFieldTableEditor());
        table.setDefaultRenderer(Date.class, new DateRendererDecorator(table.getDefaultRenderer(Date.class), format));
    }
    
    /** Sets the editor/renderer for Date objects to provided JTable, for the specified column.
     * @param table JTable to set up
     * @param row Column to apply
     */
    public static void setEditorForRow(JTable table, int row) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        setEditorForRow(table, row, format);
    }
    
    /** Sets the editor/renderer for Date objects to provided JTable, for the specified column.
     * @param table JTable to set up
     * @param row Column to apply
     * @param format Format to use
     */
    public static void setEditorForRow(JTable table, int row, DateFormat format) {
        TableColumn column = table.getColumnModel().getColumn(row);
        column.setCellEditor(new DateFieldTableEditor());
        column.setCellRenderer(new DateRendererDecorator(column.getCellRenderer(), format));
    }
}
