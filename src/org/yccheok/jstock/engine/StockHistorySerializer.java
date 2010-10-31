/*
 * StockHistorySerializer.java
 *
 * Created on June 9, 2007, 2:18 AM
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

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author yccheok
 */
public class StockHistorySerializer {
    
    /** Creates a new instance of StockHistorySerializer */
    public StockHistorySerializer(String directory) {
        this(directory, true);
    }

    public StockHistorySerializer(String directory, boolean writeEnable)
    {
        org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory);

        this.directory = directory;

        this.writeEnable = writeEnable;
    }

    public boolean save(StockHistoryServer stockHistoryServer)
    {
        if (!writeEnable)
            return false;

        Calendar calendar = null;
        
        if ((calendar = stockHistoryServer.getCalendar(0)) == null)
            return false;
        
        Stock stock = stockHistoryServer.getStock(calendar);

        return org.yccheok.jstock.gui.Utils.toXML(stockHistoryServer, directory + File.separator + stock.getCode() + ".xml");
    }
    
    public StockHistoryServer load(Code code)
    {
        File xStreamFile = new File(directory + File.separator + code + ".xml");
        return org.yccheok.jstock.gui.Utils.fromXML(StockHistoryServer.class, xStreamFile);
    }
    
    private final String directory;
    private final boolean writeEnable;
    private static final Log log = LogFactory.getLog(StockHistorySerializer.class);       
}
