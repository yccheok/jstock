/*
 * Utils.java
 *
 * Created on May 8, 2007, 10:20 PM
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

package org.yccheok.jstock.analysis;

import java.io.*;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    // Handle null case.
    public static boolean equals(Object oldValue, Object newValue) {
        if (oldValue == null) {
            return newValue == null;
        }        
        
        return oldValue.equals(newValue);
    }

    public static long getChecksum(String filename) {
        return getChecksum(new File(filename));
    }
    
    public static long getChecksum(File file) {
        FileInputStream stream = null;
        CheckedInputStream cis = null;
        try {
            // Compute Adler-32 checksum
            stream = new FileInputStream(file);
            cis = new CheckedInputStream(stream, new Adler32());
            byte[] tempBuf = new byte[128];
            while (cis.read(tempBuf) >= 0) {
            }
            long checksum = cis.getChecksum().getValue();
            return checksum;
        } catch (IOException ex) {
            log.error(null, ex);
        }
        finally {
            org.yccheok.jstock.file.Utils.close(cis);
            org.yccheok.jstock.file.Utils.close(stream);
        }
        return 0;
    }

    public static List<String> getFiles(List<String> l, String directory) {
        if (l == null) {
            l = new ArrayList<String>();
        }
        
        File file = new File(directory);
        
        if (file.isDirectory()) {
            String[] children = file.list();
            
            for (int i=0; i<children.length; i++) {
                getFiles(l, new File(file, children[i]).getAbsolutePath());
            }
        } else {
            if(file.isFile()) {
                l.add(directory);
            }
        }
        
        return l;
    }

    private static final Log log = LogFactory.getLog(Utils.class);
}
