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

package org.yccheok.jstock.gui;

/**
 * Adjust popup for combo box, so that horizontal scrollbar will not display.
 * http://forums.oracle.com/forums/thread.jspa?messageID=8037483&#8037483
 * http://www.camick.com/java/source/BoundsPopupMenuListener.java
 *
 * Update : According to https://forums.oracle.com/forums/thread.jspa?messageID=9789603#9789603
 * , the above techniques is longer workable.
 * =========================================================================
 * 6u25 changed when popupMenuWillBecomeVisible is called: it is now called 
 * before the list is created so you can add items in that method and still 
 * have the list size correctly.
 * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4743225
 * So for your workaround: either it isn't needed anymore or you need to add 
 * an extra hierarchy listener to check when the list is actually added.
 * =========================================================================
 * 
 * I use a quick hack from
 * http://javabyexample.wisdomplug.com/java-concepts/34-core-java/59-tips-and-tricks-for-jtree-jlist-and-jcombobox-part-i.html
 * 
 * @author yccheok
 */
public interface JComboBoxPopupAdjustable {
    
    /**
     * Set the combo box popup width.
     * 
     * @param popupWidth the combo box popup width
     */
    public void setPopupWidth(int popupWidth);
}
