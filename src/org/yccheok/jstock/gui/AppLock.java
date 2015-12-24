/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// http://jimlife.wordpress.com/2008/07/21/java-application-make-sure-only-singleone-instance-running-with-file-lock-ampampampampamp-shutdownhook/
public class AppLock {
    private static File f;
    private static FileChannel channel;
    private static FileLock lock;
    
    public static boolean lock() {
        try {
            String directory = Utils.getUserDataDirectory();
            String filename = "jstock.lock";
            Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory);
            f = new File(directory + filename);
            // Do we need these code?
            //if (f.exists()) {
            //    f.delete();
            //}
            channel = new RandomAccessFile(f, "rw").getChannel();
            lock = channel.tryLock();
            if(lock == null) {
                channel.close();
                return false;
            }
        } catch (FileNotFoundException ex) {            
            log.error(null, ex);
        } catch (IOException ex) {
            log.error(null, ex);
        }
        return true;
    }
    
    public static void unlock() {
        // release and delete file lock
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                f.delete();
            }
        } catch(IOException e) {
            log.error(null, e);
        }        
    }
    
    private static final Log log = LogFactory.getLog(AppLock.class);
}
