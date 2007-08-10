/*
 * InputConnector.java
 *
 * Created on May 19, 2007, 7:36 PM
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

import org.jhotdraw.draw.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.xml.*;
import java.io.*;

/**
 *
 * @author yccheok
 */
public class InputConnector extends IndexedConnector {
    
    /** Creates a new instance of InputConnector */
    public InputConnector() {
    }
    
    public InputConnector(Figure owner, Locator l, int index) {                
        super(owner, l, index);
    } 
}
