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
 * 
 * -------
 * 
 * DateFieldTableEditor.java
 * 
 * Created on 28/12/2004
 * 
 */
package net.sf.nachocalendar.table;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DateField;

/**
 * @author Ignacio Merani
 *
 */
public class DateFieldTableEditor extends AbstractCellEditor implements
        TableCellEditor {

    private DateField datefield;
    
    /**
     * Default constructor.
     */
    public DateFieldTableEditor() {
        super();
        datefield = CalendarFactory.createDateField();
    }
    
    /** Constructor specifying the DateField to use.
     * @param datefield DateField to use
     */
    public DateFieldTableEditor(DateField datefield) {
        super();
        this.datefield = datefield;
    }
    
    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int col) {
        datefield.setValue(value);
        return datefield;
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return datefield.getValue();
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) evt;
            return (me.getClickCount() > 1);
        }
        return super.isCellEditable(evt);
    }
}
