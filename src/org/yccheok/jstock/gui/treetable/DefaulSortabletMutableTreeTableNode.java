/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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
 
package org.yccheok.jstock.gui.treetable;

// Note, the following code is just copy-n-pasted from original SwingX.
// You may get it from http://java.net/projects/swingx/sources/svn/content/trunk/swingx-core/src/main/java/org/jdesktop/swingx/treetable/DefaultMutableTreeTableNode.java?rev=4171
// Or google for that.

/**
 *
 * @author yccheok
 */
public class DefaulSortabletMutableTreeTableNode extends AbstractSortableTreeTableNode {
    /**
     * 
     */
    public DefaulSortabletMutableTreeTableNode() {
        super();
    }

    /**
     * @param userObject
     */
    public DefaulSortabletMutableTreeTableNode(Object userObject) {
        super(userObject);
    }

    /**
     * @param userObject
     * @param allowsChildren
     */
    public DefaulSortabletMutableTreeTableNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(int column) {
        return getUserObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable(int column) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValueAt(Object aValue, int column) {
        setUserObject(aValue);
    }    
}
