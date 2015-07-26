/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboPopup;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.jxlayer.JXLayer;
import org.yccheok.jstock.analysis.Connection;
import org.yccheok.jstock.analysis.DoubleConstantOperator;
import org.yccheok.jstock.analysis.EqualityOperator;
import org.yccheok.jstock.analysis.Indicator;
import org.yccheok.jstock.analysis.OperatorIndicator;
import org.yccheok.jstock.analysis.SinkOperator;
import org.yccheok.jstock.analysis.StockOperator;
import org.yccheok.jstock.engine.*;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.network.Utils.Type;

/**
 *
 * @author yccheok
 */
public class Utils {
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    public static void updateFactoriesPriceSource() {
        for (Country country : Country.values()) {
            final PriceSource priceSource = JStock.instance().getJStockOptions().getPriceSource(country);
            Factories.INSTANCE.updatePriceSource(country, priceSource);
        }  
    }
    
    /**
     * Returns true if there are specified language files designed for this
     * locale. As in Java, when there are no specified language files for a 
     * locale, a default language file will be used.
     * 
     * @param locale the locale
     * @return true if there are specified language files designed for this
     * locale
     */
    public static boolean hasSpecifiedLanguageFile(Locale locale) {
        // Please revise Statement's construct code, when adding in new language.
        // So that its language guessing algorithm will work as it is.        
        if (Utils.isTraditionalChinese(locale)) {
            return true;
        } else if (Utils.isSimplifiedChinese(locale)) {
            return true;
        } else if (locale.getLanguage().equals(Locale.GERMAN.getLanguage())) {
            return true;
        } else if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
            return true;
        } else if (locale.getLanguage().equals(Locale.ITALIAN.getLanguage())) {
            return true;
        } else if (locale.getLanguage().equals(Locale.FRENCH.getLanguage())) {
            return true;
        }
        return false;
    }
    
    /**
     * Adjust popup for combo box, so that horizontal scrollbar will not display.
     * http://forums.oracle.com/forums/thread.jspa?messageID=8037483&#8037483
     * http://www.camick.com/java/source/BoundsPopupMenuListener.java
     *
     * Update : According to https://forums.oracle.com/forums/thread.jspa?messageID=9789603#9789603
     * , the above techniques is longer workable.
     * =========================================================================
     * 6u25 changed when popupMenuWillBecomeVisible is called: it is now called 
     * before the list is created so you can add items in that method and still 
     * have the list size correctly.
     * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4743225
     * So for your workaround: either it isn't needed anymore or you need to add 
     * an extra hierarchy listener to check when the list is actually added.
     * =========================================================================
     * 
     * I use a quick hack from
     * http://javabyexample.wisdomplug.com/java-concepts/34-core-java/59-tips-and-tricks-for-jtree-jlist-and-jcombobox-part-i.html
     * 
     * @param comboBox The combo box
     */
    public static void adjustPopupWidth(JComboBox comboBox) {
        if (comboBox.getItemCount() == 0) return;
        Object comp = comboBox.getAccessibleContext().getAccessibleChild(0);
        if (!(comp instanceof BasicComboPopup)) {
            return;
        }
        BasicComboPopup popup = (BasicComboPopup)comp;
        JList list = popup.getList();
        JScrollPane scrollPane = getScrollPane(popup);

        // Just to be paranoid enough.
        if (list == null || scrollPane == null) {
            return;
        }

        //  Determine the maximimum width to use:
        //  a) determine the popup preferred width
        //  b) ensure width is not less than the scroll pane width
        int popupWidth = list.getPreferredSize().width
                        + 5  // make sure horizontal scrollbar doesn't appear
                        + getScrollBarWidth(comboBox, scrollPane);
        Dimension scrollPaneSize = scrollPane.getPreferredSize();
        //popupWidth = Math.max(popupWidth, scrollPaneSize.width);
        // Use comboBox.getSize(), since we realize under Linux's Java 6u25,
        // After expanding, scrollPane.getPreferredSize() will return expanded
        // size in the 2nd round, although no expand is required.
        popupWidth = Math.max(popupWidth, comboBox.getSize().width);
        
        //  Adjust the width
        scrollPaneSize.width = popupWidth;
        scrollPane.setPreferredSize(scrollPaneSize);
        scrollPane.setMaximumSize(scrollPaneSize);
        
        // The above workaround is no longer working. Use the below hack code!
        if (comboBox instanceof JComboBoxPopupAdjustable) {
            ((JComboBoxPopupAdjustable)comboBox).setPopupWidth(popupWidth);
        }
    }

    /*
     *  I can't find any property on the scrollBar to determine if it will be
     *  displayed or not so use brute force to determine this.
     */
    private static int getScrollBarWidth(JComboBox comboBox, JScrollPane scrollPane) {
        int scrollBarWidth = 0;
        if (comboBox.getItemCount() > comboBox.getMaximumRowCount()) {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            scrollBarWidth = vertical.getPreferredSize().width;
        }
        return scrollBarWidth;
    }

    /*
     *  Get the scroll pane used by the popup so its bounds can be adjusted
     */
    private static JScrollPane getScrollPane(BasicComboPopup popup) {
        JList list = popup.getList();
        Container c = SwingUtilities.getAncestorOfClass(JScrollPane.class, list);
        return (JScrollPane)c;
    }

    /**
     * Restart the application.
     *
     * There are some important aspects to have in mind for this code:
     * + The application's main class must be in a jar file. mainFrame
     *   must be an instance of any class inside the same jar file (could be the
     *   main class too).
     * + The called java VM will be the same that the application is currently
     *   running on.
     * + There is no special error checking: the java VM may return an error like
     *   class not found or jar not found, and it will not be caught by the code
     *   posted above.
     *
     * The function will never return if it doesn't catch an error. It would be
     * a good practice to close all the handlers that could conflict with the
     * 'duplicate' new application before calling restartApplication(). There
     * will be a small time (which depends on many factors) where both
     * applications will be running at the same time.
     *
     * @param mainFrame One and only one mainFrame
     * @return true if restart success
     */
    public static boolean restartApplication(JStock mainFrame)
    {
        String javaBin = System.getProperty("java.home") + "/bin/javaw";
        File jarFile;
        try {
            jarFile = new File
            (mainFrame.getClass().getProtectionDomain()
            .getCodeSource().getLocation().toURI());
        }
        catch(Exception e) {
            log.error(null, e);
            return false;
        }

        /* is it a jar file or exe file? */
        if (!jarFile.getName().endsWith(".jar") && !jarFile.getName().endsWith(".exe")) {
            //no, it's a .class probably
            return false;
        }

        String toExec[] = null;

        if (jarFile.getName().endsWith(".exe")) {
            toExec = new String[] { jarFile.getPath() };
        }
        else {
            toExec = new String[] { javaBin, "-jar", jarFile.getPath() };
        }
     
        // Before launching new JStock, save all the application settings.
        mainFrame.save();

        // Before launching new JStock, remember to remove app lock.
        AppLock.unlock();
        
        try {
            Process p = Runtime.getRuntime().exec(toExec);
        }
        catch(Exception e) {
            log.error(null, e);
            return false;
        }

        // And close the old's if new JStock launched successfully.
        mainFrame.setVisible(false);
        mainFrame.dispose();
        
        System.exit(0);

        return true;
    }

    // Get timestamp (in ms) information from Google server. Returns 0 if
    // invalid.
    public static long getGoogleServerTimestamp() {
        final String _time = org.yccheok.jstock.gui.Utils.getUUIDValue(org.yccheok.jstock.network.Utils.getURL(Type.GET_TIME), "time");
        if (_time == null) {
            return 0;
        }
        try {
            final long time = Long.parseLong(_time);
            return time;
        } catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        return 0;
    }

    public static String toEndWithFileSeperator(String string) {
        if (string.endsWith(File.separator)) {
            return string;
        }
        return string + File.separator;
    }
    
    public static boolean extractZipFile(String zipFilePath, boolean overwrite) {
        return extractZipFile(new File(zipFilePath), overwrite);
    }

    public static boolean extractZipFile(File zipFilePath, boolean overwrite) {
        return extractZipFile(zipFilePath, Utils.getUserDataDirectory(), overwrite);
    }
    
    public static boolean extractZipFile(File zipFilePath, String destDirectory, boolean overwrite) {        
        InputStream inputStream = null;
        ZipInputStream zipInputStream = null;
        boolean status = true;

        try {
            inputStream = new FileInputStream(zipFilePath);

            zipInputStream = new ZipInputStream(inputStream);
            final byte[] data = new byte[1024];

            while (true) {
                ZipEntry zipEntry = null;
                FileOutputStream outputStream = null;

                try {
                    zipEntry = zipInputStream.getNextEntry();

                    if (zipEntry == null) {
                        break;
                    }

                    final String destination;
                    if (destDirectory.endsWith(File.separator)) {
                        destination =  destDirectory + zipEntry.getName();
                    } else {
                        destination =  destDirectory + File.separator + zipEntry.getName();
                    }

                    if (overwrite == false) {
                        if (Utils.isFileOrDirectoryExist(destination)) {
                            continue;
                        }
                    }

                    if (zipEntry.isDirectory()) {
                        Utils.createCompleteDirectoryHierarchyIfDoesNotExist(destination);
                    } else {
                        final File file = new File(destination);
                        // Ensure directory is there before we write the file.
                        Utils.createCompleteDirectoryHierarchyIfDoesNotExist(file.getParentFile());

                        int size = zipInputStream.read(data);

                        if (size > 0) {
                            outputStream = new FileOutputStream(destination);

                            do {
                                outputStream.write(data, 0, size);
                                size = zipInputStream.read(data);
                            } while (size >= 0);
                        }
                    }
                } catch (IOException exp) {
                    log.error(null, exp);
                    status = false;
                    break;
                } finally {
                    close(outputStream);
                    closeEntry(zipInputStream);
                }

            }   // while(true)
        } catch (IOException exp) {
            log.error(null, exp);
            status = false;
        } finally {
            close(zipInputStream);
            close(inputStream);
        }
        return status;
    }

    // A value obtained from server, to ensure all JStock's users are getting
    // same value for same key.
    // Note that, key "id" is a reserved word.
    public static String getUUIDValue(String url, String key) {
        final org.yccheok.jstock.gui.Utils.InputStreamAndMethod inputStreamAndMethod = org.yccheok.jstock.gui.Utils.getResponseBodyAsStreamBasedOnProxyAuthOption(url);

        if (inputStreamAndMethod.inputStream == null) {
            inputStreamAndMethod.method.releaseConnection();
            return null;
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStreamAndMethod.inputStream);
        }
        catch (IOException exp) {
            log.error(null, exp);
            return null;
        }
        catch (IllegalArgumentException exp) {
            log.error(null, exp);
            return null;
        }
        finally {
            close(inputStreamAndMethod.inputStream);
            inputStreamAndMethod.method.releaseConnection();
        }
        final String _id = properties.getProperty("id");
        if (_id == null) {
            log.info("UUID not found");
            return null;
        }

        final String id = org.yccheok.jstock.gui.Utils.decrypt(_id);
        if (id.equals(org.yccheok.jstock.gui.Utils.getJStockUUID()) == false) {
            log.info("UUID doesn't match");
            return null;
        }

        final String value = properties.getProperty(key);
        if (value == null) {
            log.info("Value not found");
            return null;
        }
        return value;
    }

    public static Map<String, String> getUUIDValue(String url) {
        Map<String, String> map = new HashMap<String, String>();
        final org.yccheok.jstock.gui.Utils.InputStreamAndMethod inputStreamAndMethod = org.yccheok.jstock.gui.Utils.getResponseBodyAsStreamBasedOnProxyAuthOption(url);

        if (inputStreamAndMethod.inputStream == null) {
            inputStreamAndMethod.method.releaseConnection();
            return java.util.Collections.emptyMap();
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStreamAndMethod.inputStream);
        }
        catch (IOException exp) {
            log.error(null, exp);
            return java.util.Collections.emptyMap();
        }
        catch (IllegalArgumentException exp) {
            log.error(null, exp);
            return java.util.Collections.emptyMap();
        }
        finally {
            close(inputStreamAndMethod.inputStream);
            inputStreamAndMethod.method.releaseConnection();
        }
        final String _id = properties.getProperty("id");
        if (_id == null) {
            log.info("UUID not found");
            return java.util.Collections.emptyMap();
        }

        final String id = org.yccheok.jstock.gui.Utils.decrypt(_id);
        if (id.equals(org.yccheok.jstock.gui.Utils.getJStockUUID()) == false) {
            log.info("UUID doesn't match");
            return java.util.Collections.emptyMap();
        }

        for (Object key : properties.keySet()) {
            // "id" is a reserved word. Ignore it!
            if (key != null && !key.equals("id")) {
                map.put(key.toString(), properties.getProperty(key.toString()));
            }
        }
        return map;
    }

    /**
     * Returns <code>ZipEntry</code> which is usable in both Linux and Windows.
     *
     * @param zipEntryName zip entry name
     * @return <code>ZipEntry</code> which is usable in both Linux and Windows
     */
    public static ZipEntry getZipEntry(String zipEntryName) {
        // Linux will not able to recognized File.seperator from Windows.
        // Change all to "/", which will be recognized by both Linux and Windows.
        return new ZipEntry(zipEntryName.replace(File.separator, "/"));
    }
    
    private static List<String> getNTPServers()
    {
        // The list is obtained from Windows Vista, Internet Time Server List itself.
        // The complete server list can be obtained from http://tf.nist.gov/tf-cgi/servers.cgi
        final List<String> defaultServer = java.util.Collections.unmodifiableList(java.util.Arrays.asList("time-a.nist.gov", "time-b.nist.gov", "time-nw.nist.gov"));
        List<String> servers = Utils.NTPServers;
        if (servers != null) {
            // We already have the server list.
            return servers;
        }

        final String server = getUUIDValue(org.yccheok.jstock.network.Utils.getURL(Type.NTP_SERVER_TXT), "server");
        if (server != null) {
            String[] s = server.split(",");
            if (s.length > 0) {
                List<String> me = java.util.Collections.unmodifiableList(java.util.Arrays.asList(s));
                // Save it! So that we need not to ask for server list again next time.
                Utils.NTPServers = me;
                return me;
            }
        }
        
        // Use default servers, so that we need not to ask for server list again next time.
        Utils.NTPServers = defaultServer;
        return defaultServer;
    }

    public static void launchWebBrowser(String address) {
        if (Desktop.isDesktopSupported())
        {
            final Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE))
            {
                URL url = null;
                String string = address;
                try {
                    url = new URL(string);
                } catch (MalformedURLException ex) {
                    return;
                }
                try {
                    desktop.browse(url.toURI());
                }
                catch (URISyntaxException ex) {
                }
                catch (IOException ex) {
                }
            }
        }
    }

    public static void launchWebBrowser(javax.swing.event.HyperlinkEvent evt) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(evt.getEventType())) {
            URL url = evt.getURL();
            if (Desktop.isDesktopSupported())
            {
                final Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE))
                {
                    if (url == null) {
                        // www.yahoo.com considered an invalid URL. Hence, evt.getURL() returns null.
                        String string = "http://" + evt.getDescription();
                        try {
                            url = new URL(string);
                        } catch (MalformedURLException ex) {
                            return;
                        }
                    }
                    try {
                        desktop.browse(url.toURI());
                    }
                    catch (URISyntaxException ex) {
                    }
                    catch (IOException ex) {
                    }
                }
            }
        }
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
    
    // This code is being picked from Google Guava Library.
    // http://code.google.com/p/guava-libraries/source/browse/guava/src/com/google/common/io/Files.java
    
    /**
    * Atomically creates a new directory somewhere beneath the system's
    * temporary directory (as defined by the {@code java.io.tmpdir} system
    * property), and returns its name.
    *
    * <p>Use this method instead of {@link File#createTempFile(String, String)}
    * when you wish to create a directory, not a regular file.  A common pitfall
    * is to call {@code createTempFile}, delete the file and create a
    * directory in its place, but this leads a race condition which can be
    * exploited to create security vulnerabilities, especially when executable
    * files are to be written into the directory.
    *
    * <p>This method assumes that the temporary volume is writable, has free
    * inodes and free blocks, and that it will not be called thousands of times
    * per second.
    *
    * @return the newly-created directory
    * @throws IllegalStateException if the directory could not be created
    */
    public static File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
          File tempDir = new File(baseDir, baseName + counter);
          if (tempDir.mkdir()) {
            return tempDir;
          }
        }
        throw new IllegalStateException("Failed to create directory within "
            + TEMP_DIR_ATTEMPTS + " attempts (tried "
            + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
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
        if (deleteRoot) {
            return dir.delete();
        }
        
        return true;
    }

    public static boolean deleteDir(String dir, boolean deleteRoot) {        
        return deleteDir(new File(dir), deleteRoot);
    }
    
    public static boolean createCompleteDirectoryHierarchyIfDoesNotExist(String directory) {
        return createCompleteDirectoryHierarchyIfDoesNotExist(new File(directory));
    }
    
    private static boolean createCompleteDirectoryHierarchyIfDoesNotExist(File f) {
        if (f == null) return true;
                
        if (false == createCompleteDirectoryHierarchyIfDoesNotExist(f.getParentFile())) {
            return false;
        }
        
        final String path = f.getAbsolutePath();
        
        return createDirectoryIfDoesNotExist(path);
    }
    
    private static boolean createDirectoryIfDoesNotExist(String directory) {
        java.io.File f = new java.io.File(directory);
        
        if (f.exists() == false) {
            if (f.mkdir())
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
    
    public static boolean isFileOrDirectoryExist(String fileOrDirectory) {
        java.io.File f = new java.io.File(fileOrDirectory);
        return f.exists();
    }
    
    /**
     * Returns user data directory.
     * @return user data directory
     */
    public static String getUserDataDirectory() {
        return System.getProperty("user.home") + File.separator + ".jstock" + File.separator + getApplicationVersionString() + File.separator;
    }

    /**
     * Returns cached history files directory.
     * @return cached history files directory
     */
    public static String getHistoryDirectory() {
        return getHistoryDirectory(JStock.instance().getJStockOptions().getCountry());
    }

    public static String getHistoryDirectory(Country country) {
        return Utils.getUserDataDirectory() + country + File.separator + "history";
    }
    
    public static AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }

    public static Color getColor(double price, double referencePrice) {
        final boolean reverse = org.yccheok.jstock.engine.Utils.isFallBelowAndRiseAboveColorReverse();
        if (price < referencePrice) {
            if (reverse) {
                return JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR;
            } else {
                return JStockOptions.DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR;
            }
        }
        
        if (price > referencePrice) {
            if (reverse) {
                return JStockOptions.DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR;
            } else {
                return JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR;
            }
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

    public static String getAboutBoxVersionString() {
        return ABOUT_BOX_VERSION_STRING;
    }
    
    public static String getApplicationVersionString() {
        return APPLICATION_VERSION_STRING;
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
        if (source.length() <= 0) {
            return "";
        }

        org.jasypt.encryption.pbe.PBEStringEncryptor pbeStringEncryptor = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
        pbeStringEncryptor.setPassword(getJStockUUID());
        try {
            return pbeStringEncryptor.decrypt(source);
        } catch (org.jasypt.exceptions.EncryptionOperationNotPossibleException exp) {
            log.error(null, exp);
        }

        return "";
    }

    public static String getJStockUUID() {
        return "fe78440e-e0fe-4efb-881d-264a01be483c";
    }

    // Returns application name, used by Google Doc service.
    private static String getCloudApplicationName() {
        return "JStock-" + CLOUD_FILE_VERSION_ID;
    }
    
    // Remember to revise googleDocTitlePattern if we change the definition
    // of this method.
    private static String getGoogleDriveTitle(long checksum, long date, int version) {
        return "jstock-" + getJStockUUID() + "-checksum=" + checksum + "-date=" + date + "-version=" + version + ".zip";
    }
    
    /**
     * Returns true if the given locale is simplified chinese.
     *
     * @param locale the locale
     * @return true if the given locale is simplified chinese
     */
    public static boolean isSimplifiedChinese(Locale locale) {
        // I assume every country in this world is using simplified chinese,
        // except Taiwan (Locale.TRADITIONAL_CHINESE.getCountry). But, how
        // about Hong Kong? Note that, we cannot just simply compare by using
        // Locale.getLanguage, as both Locale.SIMPLIFIED_CHINESE.getLanguage
        // and Locale.TRADITIONAL_CHINESE.getLanguage are having same value.
        return locale.getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage()) && !locale.getCountry().equals(Locale.TRADITIONAL_CHINESE.getCountry());
    }

    /**
     * Returns true if given locale is traditional chinese.
     * 
     * @param locale the locale
     * @return true if given locale is traditional chinese
     */
    public static boolean isTraditionalChinese(Locale locale) {
        // I assume every country in this world is using simplified chinese,
        // except Taiwan (Locale.TRADITIONAL_CHINESE.getCountry). But, how
        // about Hong Kong? Note that, we cannot just simply compare by using
        // Locale.getLanguage, as both Locale.SIMPLIFIED_CHINESE.getLanguage
        // and Locale.TRADITIONAL_CHINESE.getLanguage are having same value.
        return locale.getLanguage().equals(Locale.TRADITIONAL_CHINESE.getLanguage()) && locale.getCountry().equals(Locale.TRADITIONAL_CHINESE.getCountry());
    }

    public static boolean isMacOSX() {
        return org.jdesktop.swingx.util.OS.isMacOSX();
    }
    
    public static boolean isWindows7() {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        return "Windows 7".equals(osName) && "6.1".equals(osVersion);        
    }
    
    public static boolean isWindows8() {
        String osVersion = System.getProperty("os.version");
        return "6.2".equals(osVersion);        
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

        assert(operatorIndicator.getType() == OperatorIndicator.Type.AlertIndicator);
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

        assert(operatorIndicator.getType() == OperatorIndicator.Type.AlertIndicator);
        operatorIndicator.preCalculate();
        
        return operatorIndicator;
    }

    public static String setDefaultLookAndFeel() {
        try {
            String className = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(className);
            return className;
        } catch (java.lang.ClassNotFoundException | java.lang.InstantiationException | java.lang.IllegalAccessException | javax.swing.UnsupportedLookAndFeelException exp) {
            log.error(null, exp);
        }
        return null;
    }

    public static class CloudFile {
        public final File file;
        public final long checksum;
        public final long date;
        public final int version;
        private CloudFile(File file, long checksum, long date, int version) {
            this.file = file;
            this.checksum = checksum;
            this.date = date;
            this.version = version;
        }

        public static CloudFile newInstance(File file, long checksum, long date, int version) {
            return new CloudFile(file, checksum, date, version);
        }
    }

    private static class GoogleCloudFile {
        public final com.google.api.services.drive.model.File file;
        public final long checksum;
        public final long date;
        public final int version;
        private GoogleCloudFile(com.google.api.services.drive.model.File file, long checksum, long date, int version) {
            this.file = file;
            this.checksum = checksum;
            this.date = date;
            this.version = version;
        }

        public static GoogleCloudFile newInstance(com.google.api.services.drive.model.File file, long checksum, long date, int version) {
            return new GoogleCloudFile(file, checksum, date, version);
        }
    }

    private static GoogleCloudFile searchFromGoogleDrive(Drive drive, String qString) {
        try {
            Files.List request = drive.files().list().setQ(qString);
            
            do {                
                FileList fileList = request.execute();
                
                long checksum = 0;
                long date = 0;
                int version = 0;
                com.google.api.services.drive.model.File file = null;

                for (com.google.api.services.drive.model.File f : fileList.getItems()) {

                    final String title = f.getTitle();

                    if (title == null || f.getDownloadUrl() == null || f.getDownloadUrl().length() <= 0) {
                        continue;
                    }

                    // Retrieve checksum, date and version information from filename.
                    final Matcher matcher = googleDocTitlePattern.matcher(title);
                    String _checksum = null;
                    String _date = null;
                    String _version = null;
                    if (matcher.find()){
                        if (matcher.groupCount() == 3) {
                            _checksum = matcher.group(1);
                            _date = matcher.group(2);
                            _version = matcher.group(3);
                        }
                    }
                    if (_checksum == null || _date == null || _version == null) {
                        continue;
                    }

                    try {
                        checksum = Long.parseLong(_checksum);
                        date = Long.parseLong(_date);
                        version = Integer.parseInt(_version);
                    } catch (NumberFormatException ex) {
                        log.error(null, ex);
                        continue;
                    }  

                    file = f;

                    break;
                }

                if (file != null) {
                    return GoogleCloudFile.newInstance(file, checksum, date, version);
                }
                
                request.setPageToken(fileList.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() > 0);
        } catch (IOException ex) {
            log.error(null, ex);
            return null;
        }
        return null;
    }

    private static CloudFile _loadFromGoogleDrive(Credential credential, String qString) {
        Drive drive = org.yccheok.jstock.google.Utils.getDrive(credential);
        
        GoogleCloudFile googleCloudFile = searchFromGoogleDrive(drive, qString);
        
        if (googleCloudFile == null) {
            return null;
        }
        
        final com.google.api.services.drive.model.File file = googleCloudFile.file;
        final long checksum = googleCloudFile.checksum;
        final long date = googleCloudFile.date;
        final int version = googleCloudFile.version;
        
        HttpResponse resp = null;
        InputStream inputStream = null;
        java.io.File outputFile = null;
        OutputStream outputStream = null;
        
        try {
            resp = drive.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
            inputStream = resp.getContent();
            outputFile = java.io.File.createTempFile(Utils.getJStockUUID(), ".zip");
            outputFile.deleteOnExit();
            outputStream = new FileOutputStream(outputFile);
            
            int read = 0;
            byte[] bytes = new byte[1024];
         
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }            
        } catch (IOException ex) {
            log.error(null, ex);
        } finally {
            Utils.close(outputStream);
            Utils.close(inputStream);
            if (resp != null) {
                try {
                    resp.disconnect();
                } catch (IOException ex) {
                    log.error(null, ex);
                }
            }
        }
        
        if (outputFile == null) {
            return null;
        }
        
        return CloudFile.newInstance(outputFile, checksum, date, version);
    }
    
    public static CloudFile loadFromGoogleDrive(Credential credential) {
        // 25 is based on experiment. Might changed by Google in the future.
        final String titleName = ("jstock-" + Utils.getJStockUUID() + "-checksum=").substring(0, 25);        
        final String qString = "title contains '" + titleName + "' and trashed = false and 'appdata' in parents";
        return _loadFromGoogleDrive(credential, qString);
    }

    // Legacy. Shall be removed after a while...
    public static CloudFile loadFromLegacyGoogleDrive(Credential credential) {
        // 25 is based on experiment. Might changed by Google in the future.
        final String titleName = ("jstock-" + Utils.getJStockUUID() + "-checksum=").substring(0, 25);
        final String qString = "title contains '" + titleName + "' and trashed = false and not 'appdata' in parents";
        return _loadFromGoogleDrive(credential, qString);
    }

    public static boolean saveToGoogleDrive(Credential credential, File file) {
        // 25 is based on experiment. Might changed by Google in the future.
        final String titleName = ("jstock-" + Utils.getJStockUUID() + "-checksum=").substring(0, 25);        
        final String qString = "title contains '" + titleName + "' and trashed = false and 'appdata' in parents";        
        return _saveToGoogleDrive(credential, file, qString, "appdata");
    }
    
    public static boolean saveToLegacyGoogleDrive(Credential credential, File file) {
        // 25 is based on experiment. Might changed by Google in the future.
        final String titleName = ("jstock-" + Utils.getJStockUUID() + "-checksum=").substring(0, 25);
        final String qString = "title contains '" + titleName + "' and trashed = false and not 'appdata' in parents";       
        return _saveToGoogleDrive(credential, file, qString, null);
    }
    
    private static boolean _saveToGoogleDrive(Credential credential, File file, String qString, String folder) {
        Drive drive = org.yccheok.jstock.google.Utils.getDrive(credential);
        
        // Should we new or replace?
        
        GoogleCloudFile googleCloudFile = searchFromGoogleDrive(drive, qString);
        
        final long checksum = org.yccheok.jstock.analysis.Utils.getChecksum(file);
        final long date = new Date().getTime();
        final int version = org.yccheok.jstock.gui.Utils.getCloudFileVersionID();
        final String title = getGoogleDriveTitle(checksum, date, version);    

        if (googleCloudFile == null) {
            String id = null;
            if (folder != null) {
                com.google.api.services.drive.model.File appData;
                try {
                    appData = drive.files().get(folder).execute();
                    id = appData.getId();
                } catch (IOException ex) {
                    log.error(null, ex);
                    return false;
                }
            }
            return null != insertFile(drive, title, id, file);
        } else {
            final com.google.api.services.drive.model.File oldFile = googleCloudFile.file;
            return null != updateFile(drive, oldFile.getId(), title, file);
        }
    }
    
    /**
     * Insert new file.
     *
     * @param service Drive API service instance.
     * @param title Title of the file to insert, including the extension.
     * @param parentId Optional parent folder's ID.
     * @param mimeType MIME type of the file to insert.
     * @param filename Filename of the file to insert.
     * @return Inserted file metadata if successful, {@code null} otherwise.
     */
    private static com.google.api.services.drive.model.File insertFile(Drive service, String title, String parentId, java.io.File fileContent) {
        // File's metadata.
        com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
        body.setTitle(title);

        // Set the parent folder.
        if (parentId != null && parentId.length() > 0) {
            body.setParents(
                Arrays.asList(new ParentReference().setId(parentId)));
        }

        // File's content.
        FileContent mediaContent = new FileContent("", fileContent);
        try {
            com.google.api.services.drive.model.File file = service.files().insert(body, mediaContent).execute();
            return file;
        } catch (IOException e) {
            log.error(null, e);
            return null;
        }
    }

    /**
     * Update an existing file's metadata and content.
     *
     * @param service Drive API service instance.
     * @param fileId ID of the file to update.
     * @param newTitle New title for the file.
     * @param newFilename Filename of the new content to upload.
     * @return Updated file metadata if successful, {@code null} otherwise.
     */
    private static com.google.api.services.drive.model.File updateFile(Drive service, String fileId, String newTitle, java.io.File fileContent) {
        try {
            // First retrieve the file from the API.
            com.google.api.services.drive.model.File file = service.files().get(fileId).execute();

            // File's new metadata.
            file.setTitle(newTitle);

            FileContent mediaContent = new FileContent("", fileContent);
            
            // http://stackoverflow.com/questions/23707388/unable-update-file-store-in-appdata-scope-500-internal-server-error

            // Send the request to the API.
            com.google.api.services.drive.model.File updatedFile = service.files().update(fileId, file, mediaContent).execute();

            return updatedFile;
        } catch (IOException e) {
            log.error(null, e);
            return null;
        }
    }

    public static boolean isCloudFileCompatible(int cloudFileVersionId) {
        if (cloudFileVersionId == CLOUD_FILE_VERSION_ID) {
            return true;
        }
        else if (cloudFileVersionId >= 1051 && cloudFileVersionId <= (CLOUD_FILE_VERSION_ID - 1)) {
            return true;
        }
      
        return false;
    }

    /**
     * Get response body through non-standard POST method.
     * Please refer to <url>http://stackoverflow.com/questions/1473255/is-jakarta-httpclient-sutitable-for-the-following-task/1473305#1473305</url>
     *
     * @param uri For example, http://X/%5bvUpJYKw4QvGRMBmhATUxRwv4JrU9aDnwNEuangVyy6OuHxi2YiY=%5dImage?
     * @param formData For example, [SORT]=0,1,0,10,5,0,KL,0&[FIELD]=33,38,51
     * @return the response body. null if fail.
     */
    public static String getPOSTResponseBodyAsStringBasedOnProxyAuthOption(String uri, String formData) {
        org.yccheok.jstock.engine.Utils.setHttpClientProxyFromSystemProperties(httpClient);
        org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(httpClient);

        final PostMethod method = new PostMethod(uri);
        final RequestEntity entity;
        try {
            entity = new StringRequestEntity(formData, "application/x-www-form-urlencoded", "UTF-8");
        } catch (UnsupportedEncodingException exp) {
            log.error(null, exp);
            return null;
        }
        method.setRequestEntity(entity);
        method.setContentChunked(false);

        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        String respond = null;
        try {
            if (jStockOptions.isProxyAuthEnabled()) {
                /* WARNING : This chunck of code block is not tested! */
                method.setFollowRedirects(false);
                httpClient.executeMethod(method);

                int statuscode = method.getStatusCode();
                if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                    (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                    (statuscode == HttpStatus.SC_SEE_OTHER) ||
                    (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                    //Make new Request with new URL
                    Header header = method.getResponseHeader("location");
                    /* WARNING : Correct method to redirect? Shall we use POST? How about form data? */
                    HttpMethod RedirectMethod = new GetMethod(header.getValue());
                    // I assume it is OK to release method for twice. (The second
                    // release will happen in finally block). We shouldn't have an
                    // unreleased method, before executing another new method.
                    method.releaseConnection();
                    // Do RedirectMethod within try-catch-finally, so that we can have a
                    // exception free way to release RedirectMethod connection.
                    // #2836422
                    try {
                        httpClient.executeMethod(RedirectMethod);
                        respond = RedirectMethod.getResponseBodyAsString();
                    }
                    catch (HttpException exp) {
                        log.error(null, exp);
                        return null;
                    }
                    catch (IOException exp) {
                        log.error(null, exp);
                        return null;
                    }
                    finally {
                        RedirectMethod.releaseConnection();
                    }
                }
                else {
                    respond = method.getResponseBodyAsString();
                } // if statuscode = Redirect
            }
            else {
                httpClient.executeMethod(method);
                respond = method.getResponseBodyAsString();
            } //  if jStockOptions.isProxyAuthEnabled()
        }
        catch (HttpException exp) {
            log.error(null, exp);
            return null;
        }
        catch (IOException exp) {
            log.error(null, exp);
            return null;
        }
        finally {
            method.releaseConnection();
        }
        return respond;
    }

    /**
     * Request server response by sending request together without agent info.
     * 
     * @param request the request
     * @return server response. null if fail.
     */
    public static String getResponseBodyAsStringBasedOnProxyAuthOption(String request) {
        return _getResponseBodyAsStringBasedOnProxyAuthOption(httpClient, request);
    }
    
    /**
     * Request server response by sending request together with agent info.
     * 
     * @param request the request
     * @return server response. null if fail.
     */
    public static String getResponseBodyAsStringBasedOnProxyAuthOptionWithAgentInfo(String request) {
        return _getResponseBodyAsStringBasedOnProxyAuthOption(httpClientWithAgentInfo, request);        
    }
    
    // We prefer to have this method in gui package instead of engine. This is because it requires
    // access to JStockOptions.
    // Returns null if fail.
    private static String _getResponseBodyAsStringBasedOnProxyAuthOption(HttpClient client, String request) {
        org.yccheok.jstock.engine.Utils.setHttpClientProxyFromSystemProperties(client);
        org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(client);

        final HttpMethod method = new GetMethod(request);
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        String respond = null;
        try {
            if (jStockOptions.isProxyAuthEnabled()) {
                method.setFollowRedirects(false);
                client.executeMethod(method);

                int statuscode = method.getStatusCode();
                if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                    (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                    (statuscode == HttpStatus.SC_SEE_OTHER) ||
                    (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                    //Make new Request with new URL
                    Header header = method.getResponseHeader("location");
                    HttpMethod RedirectMethod = new GetMethod(header.getValue());
                    // I assume it is OK to release method for twice. (The second
                    // release will happen in finally block). We shouldn't have an
                    // unreleased method, before executing another new method.
                    method.releaseConnection();
                    // Do RedirectMethod within try-catch-finally, so that we can have a
                    // exception free way to release RedirectMethod connection.
                    // #2836422
                    try {
                        client.executeMethod(RedirectMethod);
                        respond = RedirectMethod.getResponseBodyAsString();
                    }
                    catch (HttpException exp) {
                        log.error(null, exp);
                        return null;
                    }
                    catch (IOException exp) {
                        log.error(null, exp);
                        return null;
                    }
                    finally {
                        RedirectMethod.releaseConnection();
                    }
                }
                else {
                    respond = method.getResponseBodyAsString();
                } // if statuscode = Redirect
            }
            else {
                client.executeMethod(method);
                respond = method.getResponseBodyAsString();
            } //  if jStockOptions.isProxyAuthEnabled()
        }
        catch (HttpException exp) {
            log.error(null, exp);
            return null;
        }
        catch (IOException exp) {
            log.error(null, exp);
            return null;
        }
        finally {
            method.releaseConnection();
        }
        return respond;
    }

    public static class InputStreamAndMethod {
        public final InputStream inputStream;
        public final HttpMethod method;
        public InputStreamAndMethod(InputStream inputStream, HttpMethod method) {
            this.inputStream = inputStream;
            this.method = method;
        }
    }

    // Unlike getResponseBodyAsStringBasedOnProxyAuthOption, method must be closed
    // explicitly by caller. If not, the returned input stream will not be valid.
    // The returned InputStreamAndMethod and InputStreamAndMethod.method will always be non-null.
    //
    // InputStreamAndMethod.inputStream will be null if we fail to get any respond.
    //
    // We must always remember to close InputStreamAndMethod.method, after finish
    // reading InputStreamAndMethod.inputStream.
    public static InputStreamAndMethod getResponseBodyAsStreamBasedOnProxyAuthOption(String request) {
        org.yccheok.jstock.engine.Utils.setHttpClientProxyFromSystemProperties(httpClient);
        org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(httpClient);

        final GetMethod method = new GetMethod(request);
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        InputStreamAndMethod inputStreamAndMethod = null;
        InputStream respond = null;
        HttpMethod methodToClosed = method;

        try {
            if (jStockOptions.isProxyAuthEnabled()) {
                method.setFollowRedirects(false);
                httpClient.executeMethod(method);

                int statuscode = method.getStatusCode();
                if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                    (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                    (statuscode == HttpStatus.SC_SEE_OTHER) ||
                    (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                    //Make new Request with new URL
                    Header header = method.getResponseHeader("location");
                    GetMethod RedirectMethod = new GetMethod(header.getValue());
                    methodToClosed = RedirectMethod;
                    method.releaseConnection();
                    // Do RedirectMethod within try-catch-finally, so that we can have a
                    // exception free way to release RedirectMethod connection.
                    // #2836422                    
                    try {
                        httpClient.executeMethod(RedirectMethod);
                        respond = RedirectMethod.getResponseBodyAsStream();
                    }
                    catch (HttpException exp) {
                        log.error(null, exp);
                    }
                    catch (IOException exp) {
                        log.error(null, exp);
                    }
                }
                else {
                    methodToClosed = method;
                    respond = method.getResponseBodyAsStream();
                } // if statuscode = Redirect
            }
            else {
                methodToClosed = method;
                httpClient.executeMethod(method);
                respond = method.getResponseBodyAsStream();
            } //  if jStockOptions.isProxyAuthEnabled()
        }
        catch (HttpException exp) {
            log.error(null, exp);
        }
        catch (IOException exp) {
            log.error(null, exp);
        }
        finally {
            inputStreamAndMethod = new InputStreamAndMethod(respond, methodToClosed);
        }

        return inputStreamAndMethod;
    }

    // We prefer to have this method in gui package instead of engine. This is because it requires
    // access to JStockOptions.
    private static void setHttpClientProxyCredentialsFromJStockOptions(HttpClient httpClient) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        if (jStockOptions.isProxyAuthEnabled() == false) {
            httpClient.getState().clearCredentials();
        }
        else {
            httpClient.getState().setProxyCredentials(AuthScope.ANY, jStockOptions.getCredentials());
        }
    }

    /*
     * Get the extension of a file.
     */
    public static String getFileExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /*
     * Get the extension of a file.
     */
    public static String getFileExtension(File f) {
        return getFileExtension(f.getName());
    }

    public static ApplicationInfo getLatestApplicationInfo()
    {
        final String request = org.yccheok.jstock.network.Utils.getURL(Type.VERSION_INFORMATION_TXT);

        final org.yccheok.jstock.gui.Utils.InputStreamAndMethod inputStreamAndMethod = org.yccheok.jstock.gui.Utils.getResponseBodyAsStreamBasedOnProxyAuthOption(request);

        if (inputStreamAndMethod.inputStream == null) {
            inputStreamAndMethod.method.releaseConnection();
            return null;
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStreamAndMethod.inputStream);
        }
        catch (IOException exp) {
            log.error(null, exp);
            return null;
        }
        catch (IllegalArgumentException exp) {
            log.error(null, exp);
            return null;

        }
        finally {
            close(inputStreamAndMethod.inputStream);
            inputStreamAndMethod.method.releaseConnection();
        }

        final String applicationVersionID = properties.getProperty("applicationVersionID");
        final String windowsDownloadLink = properties.getProperty("windowsDownloadLink");
        final String linuxDownloadLink = properties.getProperty("linuxDownloadLink");
        final String macDownloadLink = properties.getProperty("macDownloadLink");
        final String solarisDownloadLink = properties.getProperty("solarisDownloadLink");
        if (applicationVersionID == null || windowsDownloadLink == null || linuxDownloadLink == null || macDownloadLink == null || solarisDownloadLink == null) {
            return null;
        }

        final int version;
       	try {
            version = Integer.parseInt(applicationVersionID);
        }
        catch (NumberFormatException exp) {
            log.error(null, exp);
            return null;
        }
        return new ApplicationInfo(version, windowsDownloadLink, linuxDownloadLink, macDownloadLink, solarisDownloadLink);
    }

    public static int getApplicationVersionID() {
        return Utils.APPLICATION_VERSION_ID;
    }
    
    public static int getCloudFileVersionID() {
        return Utils.CLOUD_FILE_VERSION_ID;
    }

    public static String toHTML(String plainText) {
        plainText = plainText.replace(System.getProperty("line.separator"), "<br>");
        return "<html><head></head><body>" + plainText + "</body></html>";
    }

    @SuppressWarnings("unchecked")
    public static <A> A fromXML(Class c, Reader reader) {
        // Don't ever try to use DomDriver. They are VERY slow.
        XStream xStream = new XStream();

        try {
            Object object = xStream.fromXML(reader);
            if (c.isInstance(object)) {
                return (A)object;
            }
        }
        catch (Exception exp) {
            log.error(null, exp);
        }
        finally {
            /* The caller shall close reader explicitly. */
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <A> A fromXML(Class<A> c, File file) {
        // Don't ever try to use DomDriver. They are VERY slow.
        XStream xStream = new XStream();
        InputStream inputStream = null;
        Reader reader = null;

        try {
            inputStream = new java.io.FileInputStream(file);
            reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            Object object = xStream.fromXML(reader);

            if (c.isInstance(object)) {
                return (A)object;
            }
        }
        catch (Exception exp) {
            log.error(null, exp);
        }
        finally {
            close(reader);
            close(inputStream);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <A> A fromXML(Class<A> c, String filePath) {
        return fromXML(c, new File(filePath));
    }

    public static boolean toXML(Object object, File file) {
        XStream xStream = new XStream();
        OutputStream outputStream = null;
        Writer writer = null;

        try {
            outputStream = new FileOutputStream(file);
            writer = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"));
            xStream.toXML(object, writer);
        }
        catch (Exception exp) {
            log.error(null, exp);
            return false;
        }
        finally {
            close(writer);
            close(outputStream);
        }

        return true;
    }

    public static boolean toXML(Object object, String filePath) {
        return toXML(object, new File(filePath));
    }

    public static File getStockInfoDatabaseMetaFile() {
        return new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "stock-info-database-meta.json");
    }
    
    public static String getExtraDataDirectory() {
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "extra" + File.separator;
    }

    public static String toHTMLFileSrcFormat(String fileName) {
        try {
            return new File(fileName).toURI().toURL().toString();
        } catch (MalformedURLException ex) {
            log.error(null, ex);
        }
        // http://www.exampledepot.com/egs/javax.swing/checkbox_AddIcon.html
        return "file:" + fileName;
    }

   public static class FileEx {
       public final File file;
       public final org.yccheok.jstock.file.Statement.Type type;
       public FileEx(File file, org.yccheok.jstock.file.Statement.Type type) {
           this.file = file;
           this.type = type;
       }
   }

    // Calling to this method will affect state of JStockOptions.
    // Returns null if no file being selected.
    public static FileEx promptSavePortfolioCSVAndExcelJFileChooser(final String suggestedFileName) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final JFileChooser chooser = new JFileChooser(jStockOptions.getLastFileIODirectory());
        final FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Documents (*.csv)", "csv");
        final FileNameExtensionFilter xlsFilter = new FileNameExtensionFilter("Microsoft Excel (*.xls)", "xls");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(csvFilter);
        chooser.addChoosableFileFilter(xlsFilter);        

        final org.yccheok.jstock.gui.file.PortfolioSelectionJPanel portfolioSelectionJPanel = new org.yccheok.jstock.gui.file.PortfolioSelectionJPanel();
        chooser.setAccessory(portfolioSelectionJPanel);
        chooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final boolean flag = ((FileNameExtensionFilter)evt.getNewValue()).equals(csvFilter);
                portfolioSelectionJPanel.setEnabled(flag);
                chooser.setSelectedFile(chooser.getFileFilter().getDescription().equals(csvFilter.getDescription()) ? new File(portfolioSelectionJPanel.getSuggestedFileName()) : new File(suggestedFileName));
            }
            
        });
        portfolioSelectionJPanel.addJRadioButtonsActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.setSelectedFile(new File(portfolioSelectionJPanel.getSuggestedFileName()));
            }

        });
        final java.util.Map<String, FileNameExtensionFilter> map = new HashMap<String, FileNameExtensionFilter>();
        map.put(csvFilter.getDescription(), csvFilter);
        map.put(xlsFilter.getDescription(), xlsFilter);

        final FileNameExtensionFilter filter = map.get(jStockOptions.getLastSavedFileNameExtensionDescription());
        if (filter != null) {
            chooser.setFileFilter(filter);
        }

        // Only enable portfolioSelectionJPanel, if CSV is being selected.
        portfolioSelectionJPanel.setEnabled(chooser.getFileFilter().getDescription().equals(csvFilter.getDescription()));
        chooser.setSelectedFile(chooser.getFileFilter().getDescription().equals(csvFilter.getDescription()) ? new File(portfolioSelectionJPanel.getSuggestedFileName()) : new File(suggestedFileName));

        while (true) {
            final int returnVal = chooser.showSaveDialog(JStock.instance());
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            File file = chooser.getSelectedFile();
            if (file == null) {
                return null;
            }

            // Ensure the saved file is in correct extension. If user provide correct
            // file extension explicitly, leave it as is. If not, mutate the filename.
            final String extension = Utils.getFileExtension(file);
            if (extension.equals("csv") == false && extension.equals("xls") == false) {
                if (chooser.getFileFilter().getDescription().equals(csvFilter.getDescription())) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                else if (chooser.getFileFilter().getDescription().equals(xlsFilter.getDescription())) {
                    file = new File(file.getAbsolutePath() + ".xls");
                }
                else {
                    // Impossible.
                    return null;
                }
            }

            if (file.exists()) {
                final String output = MessageFormat.format(MessagesBundle.getString("question_message_replace_old_template"), file.getName());

                final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.instance(), output, MessagesBundle.getString("question_title_replace_old"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                if (result != javax.swing.JOptionPane.YES_OPTION) {
                    continue;
                }
            }

            final String parent = chooser.getSelectedFile().getParent();
            if (parent != null) {
                jStockOptions.setLastFileIODirectory(parent);
            }

            if (Utils.getFileExtension(file).equals("csv")) {
                jStockOptions.setLastFileNameExtensionDescription(csvFilter.getDescription());                
            }
            else if (Utils.getFileExtension(file).equals("xls")) {
                jStockOptions.setLastFileNameExtensionDescription(xlsFilter.getDescription());                
            }
            else {
                // Impossible.
                return null;
            }
            
            return new FileEx(file, portfolioSelectionJPanel.getType());
        }
    }

    // This method returns the selected radio button in a button group
    public static JRadioButton getSelection(ButtonGroup group) {
        for (Enumeration e = group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if (b.getModel() == group.getSelection()) {
                return b;
            }
        }
        return null;
    }

    private static File promptOpenJFileChooser(FileNameExtensionFilter... fileNameExtensionFilters) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final JFileChooser chooser = new JFileChooser(jStockOptions.getLastFileIODirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
            chooser.addChoosableFileFilter(fileNameExtensionFilter);
        }
        final java.util.Map<String, FileNameExtensionFilter> map = new HashMap<String, FileNameExtensionFilter>();
        for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
            map.put(fileNameExtensionFilter.getDescription(), fileNameExtensionFilter);
        }
        final FileNameExtensionFilter filter = map.get(jStockOptions.getLastSavedFileNameExtensionDescription());
        if (filter != null) {
            chooser.setFileFilter(filter);
        }
        int returnVal = chooser.showOpenDialog(JStock.instance());

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();
        if (file == null || !file.exists()) {
            return null;
        }
        final String parent = chooser.getSelectedFile().getParent();
        if (parent != null) {
            jStockOptions.setLastFileIODirectory(parent);
        }
        final String extension = Utils.getFileExtension(file);
        for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
            final String[] extensions = fileNameExtensionFilter.getExtensions();
            if (extensions.length <= 0) {
                continue;
            }
            if (extension.equals(extensions[0])) {
                jStockOptions.setLastFileNameExtensionDescription(fileNameExtensionFilter.getDescription());
                return file;
            }
        }
        return null;
    }

    public static void playAlertSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    clip.addLineListener(new LineListener() {
                        @Override
                        public void update(LineEvent event) {
                            if (event.getType() == LineEvent.Type.STOP) {
                                event.getLine().close();
                            }
                        }
                    });
                    final InputStream audioSrc = Utils.class.getResourceAsStream("/assets/sounds/doorbell.wav");
                    // http://stackoverflow.com/questions/5529754/java-io-ioexception-mark-reset-not-supported
                    // Add buffer for mark/reset support.
                    final InputStream bufferedIn = new BufferedInputStream(audioSrc);                    
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                   log.error(null, e);
                }
            }
        }).start();
    }

    // Calling to this method will affect state of JStockOptions.
    // Returns null if no file being selected.
    public static File promptOpenCSVAndExcelJFileChooser() {
        final FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Documents (*.csv)", "csv");
        final FileNameExtensionFilter xlsFilter = new FileNameExtensionFilter("Microsoft Excel (*.xls)", "xls");
        return promptOpenJFileChooser(csvFilter, xlsFilter);
    }

    public static File promptOpenZippedJFileChooser() {
        final FileNameExtensionFilter zippedFilter = new FileNameExtensionFilter("Zipped Files (*.zip)", "zip");
        return promptOpenJFileChooser(zippedFilter);
    }

    public static String stockPriceDecimalFormat(Object value) {
        // 0.1   -> "0.10"
        // 0.01  -> "0.01"
        // 0.001 -> "0.001"
        final DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return decimalFormat.format(value);
    }

    public static String stockPriceDecimalFormat(double value) {
        // 0.1   -> "0.10"
        // 0.01  -> "0.01"
        // 0.001 -> "0.001"
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return decimalFormat.format(value);
    }

    private static File promptSaveJFileChooser(String suggestedFileName, FileNameExtensionFilter... fileNameExtensionFilters) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final JFileChooser chooser = new JFileChooser(jStockOptions.getLastFileIODirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
            chooser.addChoosableFileFilter(fileNameExtensionFilter);
        }
        chooser.setSelectedFile(new File(suggestedFileName));
        final java.util.Map<String, FileNameExtensionFilter> map = new HashMap<String, FileNameExtensionFilter>();
        for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
            map.put(fileNameExtensionFilter.getDescription(), fileNameExtensionFilter);
        }
        final FileNameExtensionFilter filter = map.get(jStockOptions.getLastSavedFileNameExtensionDescription());
        if (filter != null) {
            chooser.setFileFilter(filter);
        }
        while (true) {
            final int returnVal = chooser.showSaveDialog(JStock.instance());
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            File file = chooser.getSelectedFile();
            if (file == null) {
                return null;
            }
            // Ensure the saved file is in correct extension. If user provide correct
            // file extension explicitly, leave it as is. If not, mutate the filename.
            final String extension = Utils.getFileExtension(file);
            boolean found = false;
            root:
            for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
                String[] extensions = fileNameExtensionFilter.getExtensions();
                for (String e : extensions) {
                    if (e.equals(extension)) {
                        found = true;
                        break root;
                    }
                }
            }
            if (!found) {
                for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
                    String[] extensions = fileNameExtensionFilter.getExtensions();
                    if (extensions.length <= 0) {
                        continue;
                    }
                    final String e = extensions[0];
                    if (chooser.getFileFilter().getDescription().equals(fileNameExtensionFilter.getDescription())) {
                        if (e.startsWith(".")) {
                            file = new File(file.getAbsolutePath() + e);
                        }
                        else {
                            file = new File(file.getAbsolutePath() + "." + e);
                        }
                        break;
                    }
                }
            }
            if (file.exists()) {
                final String output = MessageFormat.format(MessagesBundle.getString("question_message_replace_old_template"), file.getName());

                final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.instance(), output, MessagesBundle.getString("question_title_replace_old"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                if (result != javax.swing.JOptionPane.YES_OPTION) {
                    continue;
                }
            }

            final String parent = chooser.getSelectedFile().getParent();
            if (parent != null) {
                jStockOptions.setLastFileIODirectory(parent);
            }
            final String e = Utils.getFileExtension(file);
            for (FileNameExtensionFilter fileNameExtensionFilter : fileNameExtensionFilters) {
                String[] extensions = fileNameExtensionFilter.getExtensions();
                if (extensions.length <= 0) {
                    continue;
                }
                if (e.equals(extensions[0])) {
                    jStockOptions.setLastFileNameExtensionDescription(fileNameExtensionFilter.getDescription());
                    break;
                }
            }
            return file;
        }
    }

    public static File promptSaveZippedJFileChooser(String suggestedFileName) {
        final FileNameExtensionFilter zippedFilter = new FileNameExtensionFilter("Zipped Files (*.zip)", "zip");
        return promptSaveJFileChooser(suggestedFileName, zippedFilter);
    }

    /**
     * Get a new bold version of specified font, with rest of specified font
     * attributes remained the same.
     * 
     * @param font specified font
     * @return a new bold version of specified font
     */
    public static Font getBoldFont(Font font) {
        return font.deriveFont(font.getStyle() | Font.BOLD);
    }

    // Calling to this method will affect state of JStockOptions.
    // Returns null if no file being selected.
    public static File promptSaveCSVAndExcelJFileChooser(String suggestedFileName) {
        final FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Documents (*.csv)", "csv");
        final FileNameExtensionFilter xlsFilter = new FileNameExtensionFilter("Microsoft Excel (*.xls)", "xls");
        return promptSaveJFileChooser(suggestedFileName, csvFilter, xlsFilter);
    }

    /**
     * Performs close operation on ZIP output stream, without the need of
     * writing cumbersome try...catch block.
     *
     * @param zipOutputStream The ZIP input stream.
     * @return Returns false if there is an exception during close operation.
     * Otherwise returns true.
     */
    public static boolean closeEntry(ZipOutputStream zipOutputStream) {
        if (null != zipOutputStream) {
            try {
                zipOutputStream.closeEntry();
            } catch (IOException ex) {
                log.error(null, ex);
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a JXLayer with busy indicator, which wraps around the auto
     * complete combo box.
     *
     * @param autoCompleteJComboBox the auto complete combo box
     * @return a JXLayer with busy indicator, which wraps around the auto
     * complete combo box
     */
    public static JXLayer<JComboBox> getBusyJXLayer(AutoCompleteJComboBox autoCompleteJComboBox) {
        // Wrap combo box.
        final JXLayer<JComboBox> layer = new JXLayer<JComboBox>(autoCompleteJComboBox);
        // Set our LayerUI.
        JComboBoxLayerUI jComboBoxLayerUI = new JComboBoxLayerUI();
        layer.setUI(jComboBoxLayerUI);
        autoCompleteJComboBox.attachBusyObserver(jComboBoxLayerUI);
        return layer;
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
     * Returns username in email format if possible. The default used email is
     * GMail. Returns null if conversion is not possible.
     * 
     * @param username the username
     * @return username in email format if possible. The default used email is
     * GMail. Returns null if conversion is not possible
     */
    public static String toEmailIfPossible(String username) {
        if (false == org.apache.commons.validator.EmailValidator.getInstance().isValid(username)) {
            // The default email is gmail.
            username = username + "@gmail.com";
            if (false == org.apache.commons.validator.EmailValidator.getInstance().isValid(username)) {
                return null;
            }
        }
        return username;
    }
    
    /**
     * Returns number of lines in a file.
     * 
     * @param file The file
     * @return number of lines in a file
     */
    public static int numOfLines(File file, boolean skipMetadata) {
        int line = 0;
        int metaLineNumber = 0;
        
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(file));
            
            if (skipMetadata) {
                String nextLine = lnr.readLine();
                // Metadata handling.
                while (nextLine != null) {
                    String[] tokens = nextLine.split("=", 2);
                    if (tokens.length == 2) {
                        String key = tokens[0].trim();
                        if (key.length() > 0) {
                            // Is OK for value to be empty.
                            metaLineNumber++;
                            nextLine = lnr.readLine();
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }            
            }

            lnr.skip(Long.MAX_VALUE);
            line = lnr.getLineNumber();
        } catch (IOException ex) {
            log.error(null, ex);
        } finally {
            close(lnr);
        }
        
        return line - metaLineNumber;
    }

    public static String downloadAsString(String location) {    
        final Utils.InputStreamAndMethod inputStreamAndMethod = Utils.getResponseBodyAsStreamBasedOnProxyAuthOption(location);
        if (inputStreamAndMethod.inputStream == null) {
            inputStreamAndMethod.method.releaseConnection();
            return null;
        }
        try {
            java.util.Scanner s = new java.util.Scanner(inputStreamAndMethod.inputStream, "UTF-8").useDelimiter("\\A");
            return s.hasNext() ? s.next() : null;        
        } finally {
            close(inputStreamAndMethod.inputStream);
            inputStreamAndMethod.method.releaseConnection();
        }
    }
    
    /**
     * Performs download and save the download as temporary file.
     * 
     * @param location Download URL location
     * @return The saved temporary file if download success. <code>null</code>
     * if failed.
     */
    public static File downloadAsTempFile(String location) {
        final Utils.InputStreamAndMethod inputStreamAndMethod = Utils.getResponseBodyAsStreamBasedOnProxyAuthOption(location);
        if (inputStreamAndMethod.inputStream == null) {
            inputStreamAndMethod.method.releaseConnection();
            return null;
        }
        // Write to temp file.
        OutputStream out = null;
        File temp = null;
        try {
            // Create temp file.
            temp = File.createTempFile(Utils.getJStockUUID(), null);
            // Delete temp file when program exits.
            temp.deleteOnExit();

            out = new FileOutputStream(temp);

            // Transfer bytes from the ZIP file to the output file
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStreamAndMethod.inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Success!
            return temp;
        } catch (IOException ex) {
            log.error(null, ex);
        } finally {
            close(out);
            close(inputStreamAndMethod.inputStream);
            inputStreamAndMethod.method.releaseConnection();
        }
        return null;
    }

    /**
     * Returns list of Han Yu Pin Yin's prefix of every characters. If the
     * character is an alphabet or numerical, the original character will be
     * used. If there is any error occur during conversion, that particular
     * character will be ignored.
     *
     * @param chinese String to be converted
     * @return List of Han Yu Pin Yin's prefix of every characters.
     */
    public static List<String> toHanyuPinyin(String chinese) {
        // Is this an empty string?
        if (chinese.isEmpty()) {
            return new ArrayList<String>();
        }

        // Use StringBuilder instead of String during processing for speed
        // optimization.
        List<StringBuilder> stringBuilders = null;

        for (int i = 0, length = chinese.length(); i < length; i++) {
            final char c = chinese.charAt(i);

            String[] pinyins = null;
            final java.util.Set<Character> set = new java.util.HashSet<Character>();
            // Is this Chinese character?
            if (CharUtils.isAscii(c)) {
                if (CharUtils.isAsciiAlphanumeric(c)) {
                    // We are only interested in 'abc' and '123'.
                    set.add(c);
                }
            } else {
                // This is possible a Chinese character.
                try {
                    pinyins = PinyinHelper.toHanyuPinyinStringArray(c, DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT);
                    if (pinyins != null) {
                        for (String pinyin : pinyins) {
                            set.add(pinyin.charAt(0));
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination ex) {
                    log.error(null, ex);
                    // No. This is not Chinese character.
                    // Just ignore the error. Continue for the rest of characters.
                    // return new ArrayList<String>();
                }            
            }
            final List<StringBuilder> tmps = stringBuilders;
            stringBuilders = new ArrayList<StringBuilder>();

            if (tmps == null) {
                // This will be the first converted character.
                for (Character character : set) {
                    final StringBuilder me = new StringBuilder();
                    me.append(character);
                    stringBuilders.add(me);
                }
            } else {
                for (Character character : set) {
                    for (StringBuilder tmp : tmps) {
                        final StringBuilder me = new StringBuilder();
                        me.append(tmp);
                        me.append(character);
                        stringBuilders.add(me);
                    }
                }
            }
        }

        List<String> result = new ArrayList<String>();
        // Do we have any converted characters?
        if (stringBuilders != null) {
            for (StringBuilder stringBuilder : stringBuilders) {
                result.add(stringBuilder.toString());
            }
        }

        return result;
    }

    /**
     * Returns default currency symbol, regardless what country we are in right
     * now.
     *
     * @return Default currency symbol, regardless what country we are in right
     * now.
     */
    public static String getDefaultCurrencySymbol() {
        return "$";
    }
    
    /**
     * Returns common used date format, which will be used by Statements. We need
     * common used date format, as we need to perform data exchange across
     * different platforms.
     * 
     * @return common used date format
     */
    public static DateFormat getCommonDateFormat() {
        return commonDateFormat.get();
    }
    
    public static boolean isToday(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(timestamp);
        int _date = calendar.get(Calendar.DATE);
        int _month = calendar.get(Calendar.MONTH);
        int _year = calendar.get(Calendar.YEAR);
        return date == _date && month == _month && year == _year;
    }
    
    private static Gson getGsonForStockInfoDatabaseMeta() {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                new TypeToken<EnumMap<Country, Long>>() {}.getType(),
                new EnumMapInstanceCreator<Country, Long>(Country.class))                
                .create();
        
        return gson;
    }
    
    public static Map<Country, Long> loadStockInfoDatabaseMeta(String json) {
        final Gson gson = getGsonForStockInfoDatabaseMeta();
        
        Map<Country, Long> stockInfoDatabaseMeta = null;
        
        try {
            stockInfoDatabaseMeta = gson.fromJson(json, new TypeToken<EnumMap<Country, Long>>(){}.getType());
        } catch (Exception ex) {
            log.error(null, ex);
        }
        
        if (stockInfoDatabaseMeta == null) {
            return java.util.Collections.emptyMap();
        }
        
        return stockInfoDatabaseMeta;
    }
    
    public static Map<Country, Long> loadStockInfoDatabaseMeta(File stockInfoDatabaseMetaFile) {
        final Gson gson = getGsonForStockInfoDatabaseMeta();
                
        Map<Country, Long> stockInfoDatabaseMeta = null;

        try {
            //If the constructor throws an exception, the finally block will NOT execute
            //BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stockInfoDatabaseMetaFile), "UTF-8"));            
            try {
                stockInfoDatabaseMeta = gson.fromJson(reader, new TypeToken<EnumMap<Country, Long>>(){}.getType());
            } finally {
                //no need to check for null
                //any exceptions thrown here will be caught by 
                //the outer catch block
                reader.close();
            }
        } catch (IOException ex){
            log.error(null, ex);
        } catch (com.google.gson.JsonSyntaxException ex) {
            log.error(null, ex);
        }            

        if (stockInfoDatabaseMeta == null) {
            return java.util.Collections.emptyMap();
        }
        
        return stockInfoDatabaseMeta;
    }
    
    public static Font getRobotoLightFont() {
        if (ROBOTO_LIGHT_FONT == null) {
            ROBOTO_LIGHT_FONT = _getRobotoLightFont();
        }
        return ROBOTO_LIGHT_FONT;
    }
    
    private static Font _getRobotoLightFont() {
        InputStream inputStream = Utils.class.getResourceAsStream("/assets/fonts/Roboto-Light.ttf");
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            if (font != null) {
                return font;
            }
        } catch (FontFormatException ex) {
            log.error(null, ex);
        } catch (IOException ex) {
            log.error(null, ex);
        } finally {
            close(inputStream);
        }
        Font oldLabelFont = UIManager.getFont("Label.font");
        return oldLabelFont;
    }
    
    public static boolean saveStockInfoDatabaseMeta(File stockInfoDatabaseMetaFile, Map<Country, Long> stockInfoDatabaseMeta) {
        final Gson gson = getGsonForStockInfoDatabaseMeta();
        String string = gson.toJson(stockInfoDatabaseMeta);
        
        try {
            //If the constructor throws an exception, the finally block will NOT execute
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stockInfoDatabaseMetaFile), "UTF-8"));
            try {
                writer.write(string);
            } finally {
                writer.close();
            }
        } catch (IOException ex){
            log.error(null, ex);
            return false;
        }
        
        return true;        
    }
    
    /**
     * Returns time format used in status bar.
     * 
     * @return time format used in status bar
     */
    public static DateFormat getTodayLastUpdateTimeFormat() {
        return todayLastUpdateTimeFormat.get();
    }

    public static DateFormat getOtherDayLastUpdateTimeFormat() {
        return otherDayLastUpdateTimeFormat.get();
    }
    
    private static class EnumMapInstanceCreator<K extends Enum<K>, V> implements
            InstanceCreator<EnumMap<K, V>> {
        private final Class<K> enumClazz;

        public EnumMapInstanceCreator(final Class<K> enumClazz) {
            super();
            this.enumClazz = enumClazz;
        }

        @Override
        public EnumMap<K, V> createInstance(final java.lang.reflect.Type type) {
            return new EnumMap<K, V>(enumClazz);
        }
    }
    
    /**
     * Represents latest application information. This is being used for
     * application upgrading.
     */
    public static class ApplicationInfo
    {
        /**
         * ID to represent application version.
         */
        public final int applicationVersionID;
        /**
         * URL link to download Windows application version <code>applicationVersionID</code>
         */
        public final String windowsDownloadLink;
        /**
         * URL link to download Linux application version <code>applicationVersionID</code>
         */
        public final String linuxDownloadLink;
        /**
         * URL link to download Mac application version <code>applicationVersionID</code>
         */
        public final String macDownloadLink;
        /**
         * URL link to download Solaris application version <code>applicationVersionID</code>
         */
        public final String solarisDownloadLink;

        /**
         * Constructs application information object.
         *
         * @param applicationVersionID ID to represent application version
         * @param windowsDownloadLink URL link to download Windows application
         * @param linuxDownloadLink URL link to download Linux application
         * @param macDownloadLink URL link to download Mac application
         * @param solarisDownloadLink URL link to download Solaris application
         */
        public ApplicationInfo(int applicationVersionID, String windowsDownloadLink, String linuxDownloadLink, String macDownloadLink, String solarisDownloadLink) {
            this.applicationVersionID = applicationVersionID;
            this.windowsDownloadLink = windowsDownloadLink;
            this.linuxDownloadLink = linuxDownloadLink;
            this.macDownloadLink = macDownloadLink;
            this.solarisDownloadLink = solarisDownloadLink;
        }
    }

    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <DateFormat> commonDateFormat = new ThreadLocal <DateFormat>() {
        @Override protected DateFormat initialValue() {
            // We will use a fixed date format (Locale.English), so that it will be
            // easier for Android to process.
            //
            // "Sep 5, 2011"    -   Locale.ENGLISH
            // "2011-9-5"       -   Locale.SIMPLIFIED_CHINESE
            // "2011/9/5"       -   Locale.TRADITIONAL_CHINESE
            // 05.09.2011       -   Locale.GERMAN
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
            return dateFormat;
        }
    };

    private static final ThreadLocal<DateFormat> todayLastUpdateTimeFormat = new ThreadLocal <DateFormat>() {
        @Override protected DateFormat initialValue() {
            return new SimpleDateFormat("h:mm a");
        }
    };

    private static final ThreadLocal<DateFormat> otherDayLastUpdateTimeFormat = new ThreadLocal <DateFormat>() {
        @Override protected DateFormat initialValue() {
            return DateFormat.getDateInstance(DateFormat.SHORT);
        }
    };
    
    public static Font ROBOTO_LIGHT_FONT = null;
            
    private static final HanyuPinyinOutputFormat DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT = new HanyuPinyinOutputFormat();
    static {
        DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    private static volatile List<String> NTPServers = null;

    // We will use this as directory name. Do not have space or special characters.
    private static final String APPLICATION_VERSION_STRING = "1.0.7";
    
    // 1.0.7e
    // Remember to update isCloudFileCompatible method.
    private static final int CLOUD_FILE_VERSION_ID = 1107;

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private static final String ABOUT_BOX_VERSION_STRING = "1.0.7.9";

    // 1.0.7.9
    // For About box comparision on latest version purpose.
    private static final int APPLICATION_VERSION_ID = 1138;
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    private static final Executor zombiePool = Executors.newFixedThreadPool(Utils.NUM_OF_THREADS_ZOMBIE_POOL);

    private static final int NUM_OF_THREADS_ZOMBIE_POOL = 4;

    private static final HttpClient httpClient;
    private static final HttpClient httpClientWithAgentInfo;
    
    /** Maximum loop count when creating temp directories. */
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    static {
        MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
        multiThreadedHttpConnectionManager.getParams().setMaxTotalConnections(128);
        multiThreadedHttpConnectionManager.getParams().setDefaultMaxConnectionsPerHost(128);
        httpClient = new HttpClient(multiThreadedHttpConnectionManager);
        // To prevent cookie warnings.
        httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
        httpClient.getParams().setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.BROWSER_COMPATIBILITY);
        multiThreadedHttpConnectionManager.getParams().setMaxConnectionsPerHost(httpClient.getHostConfiguration(), 128);

    }
    static {
        MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
        multiThreadedHttpConnectionManager.getParams().setMaxTotalConnections(128);
        multiThreadedHttpConnectionManager.getParams().setDefaultMaxConnectionsPerHost(128);
        httpClientWithAgentInfo = new HttpClient(multiThreadedHttpConnectionManager);
        // Provide agent information, as requested by KLSEInfo owner.
        httpClientWithAgentInfo.getParams().setParameter(HttpMethodParams.USER_AGENT, "JStock-1.0.6o");
        // To prevent cookie warnings.
        httpClientWithAgentInfo.getParams().setParameter("http.protocol.single-cookie-header", true);
        httpClientWithAgentInfo.getParams().setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.BROWSER_COMPATIBILITY);
        multiThreadedHttpConnectionManager.getParams().setMaxConnectionsPerHost(httpClientWithAgentInfo.getHostConfiguration(), 128);    
    }
    
    // http://stackoverflow.com/questions/1360113/is-java-regex-thread-safe
    private static final Pattern googleDocTitlePattern = Pattern.compile("jstock-" + getJStockUUID() +  "-checksum=([0-9]+)-date=([0-9]+)-version=([0-9]+)\\.zip", Pattern.CASE_INSENSITIVE);
        
    private static final Log log = LogFactory.getLog(Utils.class);
}
