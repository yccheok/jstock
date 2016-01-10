/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2016 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class Utils {
    private Utils() {
    }
    

    /**
     * Performs close operation on Closeable stream, without the need of
     * writing cumbersome try...catch block.
     *
     * @param closeable The closeable stream.
     */
    public static void close(Closeable closeable) {
        // Instead of returning boolean, we will just simply swallow any
        // exception silently. This is because this method will usually be
        // invoked within finally block. If we are having control statement
        // (return, break, continue) within finally block, a lot of surprise may
        // happen.
        // http://stackoverflow.com/questions/48088/returning-from-a-finally-block-in-java
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException ex) {
                log.error(null, ex);
            }
        }
    }
    

    /**
     * Performs close operation on ZIP input stream, without the need of
     * writing cumbersome try...catch block.
     *
     * @param zipInputStream The ZIP input stream.
     */
    public static void closeEntry(ZipInputStream zipInputStream) {
        // Instead of returning boolean, we will just simply swallow any
        // exception silently. This is because this method will usually be
        // invoked within finally block. If we are having control statement
        // (return, break, continue) within finally block, a lot of surprise may
        // happen.
        // http://stackoverflow.com/questions/48088/returning-from-a-finally-block-in-java
        if (null != zipInputStream) {
            try {
                zipInputStream.closeEntry();
            } catch (IOException ex) {
                log.error(null, ex);
            }
        }
    }


    /**
     * Performs close operation on ZIP output stream, without the need of
     * writing cumbersome try...catch block.
     *
     * @param zipOutputStream The ZIP input stream.
     * @return Returns false if there is an exception during close operation.
     * Otherwise returns true.
     */
    public static void closeEntry(ZipOutputStream zipOutputStream) {
        if (null != zipOutputStream) {
            try {
                zipOutputStream.closeEntry();
            } catch (IOException ex) {
                log.error(null, ex);
            }
        }
    }
    
    private static final Log log = LogFactory.getLog(Utils.class);
}
