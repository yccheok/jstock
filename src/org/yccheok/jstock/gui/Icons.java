/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author yccheok
 */
public final class Icons {
    public static final Icon OK = new ImageIcon(Icons.class.getResource("/images/16x16/ok.png"));
    public static final Icon BUSY = new ImageIcon(Icons.class.getResource("/images/16x16/spinner.gif"));
    public static final Icon WARNING = new ImageIcon(Icons.class.getResource("/images/16x16/important.png"));
    public static final Icon ERROR = new ImageIcon(Icons.class.getResource("/images/16x16/stop.png"));
}
