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

/* This code is being extracted out from JTable source code with the following
 * header.
 */

/*
 * @(#)JTable.java	1.292 08/05/30
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.yccheok.jstock.gui.table;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 *
 * @author yancheng
 */

/* Start modified by Cheok for jstock usage. */
// public class GenericEditor extends DefaultCellEditor {
public abstract class GenericEditor extends DefaultCellEditor {
/* End modified by Cheok for jstock usage. */

    Class[] argTypes = new Class[]{String.class};
    java.lang.reflect.Constructor constructor;
    Object value;

    public GenericEditor() {
        super(new JTextField());
        getComponent().setName("Table.editor");
    }

    /* Start modified by Cheok for jstock usage. */
    public abstract boolean validate(Object value);
    public abstract boolean isEmptyAllowed();
    /* End modified by Cheok for jstock usage. */

    @Override
    public boolean stopCellEditing() {
        String s = (String)super.getCellEditorValue();
        // Here we are dealing with the case where a user
        // has deleted the string value in a cell, possibly
        // after a failed validation. Return null, so that
        // they have the option to replace the value with
        // null or use escape to restore the original.
        // For Strings, return "" for backward compatibility.
        if ("".equals(s)) {
            if (isEmptyAllowed()) {
                if (constructor.getDeclaringClass() == String.class) {
                    value = s;
                }
                super.stopCellEditing();
            }
            else {
                ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
                return false;
            }
        }

        try {
            value = constructor.newInstance(new Object[]{s});
        }
        catch (Exception e) {
            ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
            return false;
        }

        /* Start modified by Cheok for jstock usage. */
        if (false == validate(value))
        {
            ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
            return false;
        }
        /* End modified by Cheok for jstock usage. */

        return super.stopCellEditing();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row, int column) {
        this.value = null;
        ((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
        try {
            Class type = table.getColumnClass(column);
            // Since our obligation is to produce a value which is
            // assignable for the required type it is OK to use the
            // String constructor for columns which are declared
            // to contain Objects. A String is an Object.
            if (type == Object.class) {
                type = String.class;
            }
            constructor = type.getConstructor(argTypes);
        }
	    catch (Exception e) {
            return null;
        }

        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }
}
