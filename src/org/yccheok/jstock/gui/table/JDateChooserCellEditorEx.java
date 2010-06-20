/*
 *  JDateChooserCellEditor.java  - A CellEditor for tables, using a JDateChooser
 *  Copyright (C) 2005 Kai Toedter
 *  kai@toedter.com
 *  www.toedter.com
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// Modified from JCalendar's JDateChooserCellEditor.java, to suit the need of
// JStock.

package org.yccheok.jstock.gui.table;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.yccheok.jstock.gui.Utils;

/**
 * A CellEditor for tables, using a JDateChooser.
 *
 * @author Kai Toedter
 * @version $LastChangedRevision: 100 $
 * @version $LastChangedDate: 2006-06-04 14:36:06 +0200 (So, 04 Jun 2006) $
 */
public class JDateChooserCellEditorEx extends AbstractCellEditor implements
		TableCellEditor, java.io.Serializable {

    private static final long serialVersionUID = -1279512020450996723L;

    private final JDateChooser dateChooser = Utils.getDefaultJDateChooser();

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {

        Date date = null;
        if (value instanceof Date)
        date = (Date) value;

        dateChooser.setDate(date);

        return dateChooser;
    }

    @Override
    public Object getCellEditorValue() {
        return dateChooser.getDate();
    }
}