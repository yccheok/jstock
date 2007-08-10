/*
 * Utils.java
 *
 * Created on May 26, 2007, 9:37 AM
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

import org.yccheok.jstock.engine.*;
import java.util.*;

/**
 *
 * @author yccheok
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    public Utils() {
    }
    
    public static boolean createDirectoryIfDoesNotExist(String directory) {
        java.io.File f = new java.io.File(directory);
        
        if(f.exists() == false) {
            if(f.mkdir())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        
        return true;
    }
    
    public static Stock getEmptyStock(String code, String symbol) {
        return new Stock(   code,
                            symbol,
                            "",
                            Stock.Board.Unknown,
                            Stock.Industry.Unknown,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0,
                            0.0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            Calendar.getInstance()                                        
                            );                
    }    
}
