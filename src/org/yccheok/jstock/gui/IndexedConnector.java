/*
 * IndexedConnector.java
 *
 * Created on May 23, 2007, 12:52 AM
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
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;
import java.io.*;

/**
 *
 * @author yccheok
 */
public class IndexedConnector extends LocatorConnector {
    
    /** Creates a new instance of IndexedConnector */
    public IndexedConnector() {
    }
    
    public IndexedConnector(Figure owner, Locator l, int index) {                
        super(owner, l);
        this.index = index;
        numOfConnection = 0;
    }
    
    public void read(DOMInput in) throws IOException {
        super.read(in);
        index = in.getAttribute("index", 0);
        numOfConnection = in.getAttribute("numOfConnection", 0);
    }
    
    public void write(DOMOutput out) throws IOException {
        super.write(out);
        out.addAttribute("index", index);
        out.addAttribute("numOfConnection", numOfConnection);
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setNumOfConnection(int numOfConnection) {
        this.numOfConnection = numOfConnection;        
    }
    
    public int getNumOfConnection() {
        return numOfConnection;
    }
    
    private int index;
    private int numOfConnection;
}