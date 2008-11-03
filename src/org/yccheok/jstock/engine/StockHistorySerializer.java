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

import com.thoughtworks.xstream.*;

/**
 *
 * @author yccheok
 */
public class StockHistorySerializer {
    
    /** Creates a new instance of StockHistorySerializer */
    public StockHistorySerializer(String directory) {
        org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory);
        
        this.directory = directory;
    }
    
    public boolean save(StockHistoryServer stockHistoryServer)
    {
        Calendar calendar = null;
        
        if((calendar = stockHistoryServer.getCalendar(0)) == null)
            return false;
        
        Stock stock = stockHistoryServer.getStock(calendar);
        
        File xStreamFile = new File(directory + File.separator + stock.getCode() + ".xml");        
        
        XStream xStream = new XStream();
        
        try {
            OutputStream outputStream = new FileOutputStream(xStreamFile);
            xStream.toXML(stockHistoryServer, outputStream);
            outputStream.close();        
        }
        catch(FileNotFoundException exp) {
            log.error("", exp);
            return false;
        }
        catch(IOException exp) {
            log.error("", exp);
            return false;
        }
        
        return true;
    }
    
    public StockHistoryServer load(Code code)
    {
        File xStreamFile = new File(directory + File.separator + code + ".xml");        

        XStream xStream = new XStream();
        
        StockHistoryServer stockHistoryServer = null;
        
        try {
            InputStream inputStream = new java.io.FileInputStream(xStreamFile);
            stockHistoryServer = (StockHistoryServer)xStream.fromXML(inputStream);        

            inputStream.close();
        }
        catch(FileNotFoundException exp) {
            log.error("", exp);
            return null;
        }
        catch(IOException exp) {
            log.error("", exp);
            return null;
        }
        catch(com.thoughtworks.xstream.core.BaseException exp) {
            log.error("", exp);
            return null;
        }
        
        return stockHistoryServer;
    }
    
    private final String directory;    
    private static final Log log = LogFactory.getLog(StockHistorySerializer.class);       
}
