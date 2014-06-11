/*
 * StockHistoryNotFoundException.java
 *
 * Created on April 22, 2007, 1:33 AM
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

package org.yccheok.jstock.engine;

/**
 *
 * @author yccheok
 */
public class StockHistoryNotFoundException extends Exception {
    
    /** Creates a new instance of StockHistoryNotFoundException */
    public StockHistoryNotFoundException() {
        super();
    }

    public StockHistoryNotFoundException(String message) {
        super(message);
    }

    public StockHistoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public StockHistoryNotFoundException(Throwable cause) {
        super(cause);
    }     
}
