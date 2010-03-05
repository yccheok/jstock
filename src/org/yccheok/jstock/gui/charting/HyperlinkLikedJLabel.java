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

package org.yccheok.jstock.gui.charting;

import java.awt.Cursor;
import javax.swing.JLabel;

/**
 * A JLabel, which user will feel it looks like and feels like a hyperlink.
 *
 * @author Hans Muller
 */
public class HyperlinkLikedJLabel extends JLabel {
    /**
     * Constructs a hyperlink liked JLabel.
     */
    public HyperlinkLikedJLabel() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelMouseEntered(evt);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelMouseExited(evt);
            }
        });
    }

    /**
     * Handles a 'mouse entered' event. Current cursor will be changed to hand.
     */
    private void jLabelMouseEntered(java.awt.event.MouseEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Handles a 'mouse exited' event. Current cursor will be reset.
     */
    private void jLabelMouseExited(java.awt.event.MouseEvent evt) {
        this.setCursor(Cursor.getDefaultCursor());
    }
}
