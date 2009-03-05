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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import org.yccheok.jstock.engine.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.analysis.Connection;
import org.yccheok.jstock.analysis.DoubleConstantOperator;
import org.yccheok.jstock.analysis.EqualityOperator;
import org.yccheok.jstock.analysis.Indicator;
import org.yccheok.jstock.analysis.OperatorIndicator;
import org.yccheok.jstock.analysis.SinkOperator;
import org.yccheok.jstock.analysis.StockOperator;

/**
 *
 * @author yccheok
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    public static java.awt.Image getScaledImage(Image image, int maxWidth, int maxHeight) {
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        
        final int imgWidth = image.getWidth(null);
        final int imgHeight = image.getHeight(null);
                        
        final int preferredWidth = Math.min(imgWidth, maxWidth);
        final int preferredHeight = Math.min(imgHeight, maxHeight);
        
        final double scaleX = (double)preferredWidth / (double)imgWidth;
        final double scaleY = (double)preferredHeight / (double)imgHeight;
        
        final double bestScale = Math.min(scaleX, scaleY);
    
        return image.getScaledInstance((int)((double)imgWidth * bestScale), (int)((double)imgHeight * bestScale), Image.SCALE_SMOOTH);
    }
    
    // This method returns true if the specified image has transparent pixels
    private static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }
    
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
    
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        
        return cm.hasAlpha();
    }
    
    // This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
    
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
    
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);
    
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
    
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
    
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
    
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
    
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
    
        return bimage;
    }
    
    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir, boolean deleteRoot) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]), true);
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        if(deleteRoot) {
            return dir.delete();
        }
        
        return true;
    }

    public static boolean createCompleteDirectoryHierarchyIfDoesNotExist(String directory) {
        return createCompleteDirectoryHierarchyIfDoesNotExist(new File(directory));
    }
    
    private static boolean createCompleteDirectoryHierarchyIfDoesNotExist(File f) {
        if(f == null) return true;
                
        if(false == createCompleteDirectoryHierarchyIfDoesNotExist(f.getParentFile())) {
            return false;
        }
        
        String path = null;
        
        try {
            path = f.getCanonicalPath();
        } catch (IOException ex) {
            log.error("", ex);
            return false;
        }
        
        return createDirectoryIfDoesNotExist(path);
    }
    
    public static boolean isFileOrDirectoryExist(String fileOrDirectory) {
        java.io.File f = new java.io.File(fileOrDirectory);
        return f.exists();
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
    
    public static String getUserDataDirectory() {
        return System.getProperty("user.home") + File.separator + ".jstock" + File.separator + getApplicationVersionString() + File.separator;
    }
    
    public static Color getColor(double price, double referencePrice) {
        if(price < referencePrice) {
            return JStockOptions.DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR;
        }
        
        if(price > referencePrice) {
            return JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR;
        }
        
        return JStockOptions.DEFAULT_NORMAL_TEXT_FOREGROUND_COLOR;
    }
    
    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }
    
    public static Stock getEmptyStock(Code code, Symbol symbol) {
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

    public static void deleteAllOldFiles(File dir, int days) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                deleteAllOldFiles(new File(dir, children[i]), days);
            }

            // Delete empty directory
            if(dir.list().length == 0)
            {
                dir.delete();
            }
        } else {
            final long today = System.currentTimeMillis();
            final long timeStamp = dir.lastModified();

            final long difMil = today - timeStamp;
            final long milPerDay = 1000*60*60*24;
            final long d = difMil / milPerDay;

            if(d >= days)
            {
                dir.delete();
            }
        }
    }

    public static String getApplicationVersionString() {
        return APPLICATION_VERSION_STRING;
    }

    public static String getLatestNewsLocation(long newsVersion)
    {
        return "http://jstock.sourceforge.net/news/" + APPLICATION_VERSION_STRING + "/" + newsVersion + ".html";
    }

    // If you try to call this method at different time with same source, the
    // resultant encrypted string will be different. The best part is, it is still
    // able to be decrypted back to the original source.
    public static String encrypt(String source)
    {
        if (source.length() <= 0)
            return "";

        org.jasypt.encryption.pbe.PBEStringEncryptor pbeStringEncryptor = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
        pbeStringEncryptor.setPassword(getJStockUUID());
        return pbeStringEncryptor.encrypt(source);
    }

    public static String decrypt(String source)
    {
        if (source.length() <= 0)
            return "";

        org.jasypt.encryption.pbe.PBEStringEncryptor pbeStringEncryptor = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
        pbeStringEncryptor.setPassword(getJStockUUID());
        try {
            return pbeStringEncryptor.decrypt(source);
        }
        catch(org.jasypt.exceptions.EncryptionOperationNotPossibleException exp) {
            log.error(null, exp);
        }

        return "";
    }

    public static String getJStockUUID() {
        return "fe78440e-e0fe-4efb-881d-264a01be483c";
    }

    public static boolean isWindows() {
        String windowsString = "Windows";
        String osName = System.getProperty("os.name");

        if (osName == null) return false;

        return osName.regionMatches(true, 0, windowsString, 0, windowsString.length());
    }

    public static Executor getZoombiePool()
    {
        return zombiePool;
    }

    public static Indicator getLastPriceRiseAboveIndicator(double lastPrice)
    {
        final StockOperator stockOperator = new StockOperator();
        stockOperator.setType(StockOperator.Type.LastPrice);
        final DoubleConstantOperator doubleConstantOperator = new DoubleConstantOperator();
        doubleConstantOperator.setConstant(lastPrice);
        final EqualityOperator equalityOperator = new EqualityOperator();
        equalityOperator.setEquality(EqualityOperator.Equality.GreaterOrEqual);
        final SinkOperator sinkOperator = new SinkOperator();

        final Connection stockToEqualityConnection = new Connection();
        final Connection doubleConstantToEqualityConnection = new Connection();
        final Connection equalityToSinkConnection = new Connection();

        stockOperator.addOutputConnection(stockToEqualityConnection, 0);
        equalityOperator.addInputConnection(stockToEqualityConnection, 0);

        doubleConstantOperator.addOutputConnection(doubleConstantToEqualityConnection, 0);
        equalityOperator.addInputConnection(doubleConstantToEqualityConnection, 1);

        equalityOperator.addOutputConnection(equalityToSinkConnection, 0);
        sinkOperator.addInputConnection(equalityToSinkConnection, 0);

        final OperatorIndicator operatorIndicator = new OperatorIndicator();
        operatorIndicator.setName("RiseAbove");
        operatorIndicator.add(stockOperator);
        operatorIndicator.add(doubleConstantOperator);
        operatorIndicator.add(equalityOperator);
        operatorIndicator.add(sinkOperator);

        assert(operatorIndicator.isValid());
        operatorIndicator.preCalculate();

        return operatorIndicator;
    }

    public static Indicator getLastPriceFallBelowIndicator(double lastPrice)
    {
        final StockOperator stockOperator = new StockOperator();
        stockOperator.setType(StockOperator.Type.LastPrice);
        final DoubleConstantOperator doubleConstantOperator = new DoubleConstantOperator();
        doubleConstantOperator.setConstant(lastPrice);
        final EqualityOperator equalityOperator = new EqualityOperator();
        equalityOperator.setEquality(EqualityOperator.Equality.LesserOrEqual);
        final SinkOperator sinkOperator = new SinkOperator();

        final Connection stockToEqualityConnection = new Connection();
        final Connection doubleConstantToEqualityConnection = new Connection();
        final Connection equalityToSinkConnection = new Connection();

        stockOperator.addOutputConnection(stockToEqualityConnection, 0);
        equalityOperator.addInputConnection(stockToEqualityConnection, 0);

        doubleConstantOperator.addOutputConnection(doubleConstantToEqualityConnection, 0);
        equalityOperator.addInputConnection(doubleConstantToEqualityConnection, 1);

        equalityOperator.addOutputConnection(equalityToSinkConnection, 0);
        sinkOperator.addInputConnection(equalityToSinkConnection, 0);

        final OperatorIndicator operatorIndicator = new OperatorIndicator();
        operatorIndicator.setName("FallBelow");
        operatorIndicator.add(stockOperator);
        operatorIndicator.add(doubleConstantOperator);
        operatorIndicator.add(equalityOperator);
        operatorIndicator.add(sinkOperator);

        assert(operatorIndicator.isValid());
        operatorIndicator.preCalculate();
        
        return operatorIndicator;
    }

    public static void setDefaultLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (java.lang.ClassNotFoundException exp) {
            log.error(null, exp);
        }
        catch (java.lang.InstantiationException exp) {
            log.error(null, exp);
        }
        catch (java.lang.IllegalAccessException exp) {
            log.error(null, exp);
        }
        catch (javax.swing.UnsupportedLookAndFeelException exp) {
            log.error(null, exp);
        }
    }

	// We will use this as directory name. Do not have space or special characters.
    private static final String APPLICATION_VERSION_STRING = "1.0.3";

    private static Executor zombiePool = Executors.newFixedThreadPool(Utils.NUM_OF_THREADS_ZOMBIE_POOL);

    private static final int NUM_OF_THREADS_ZOMBIE_POOL = 4;

    private static final Log log = LogFactory.getLog(Utils.class);
}
