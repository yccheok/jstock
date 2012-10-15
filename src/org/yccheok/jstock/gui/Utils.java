/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.GoogleService.CaptchaRequiredException;
import com.google.gdata.client.docs.*;
import com.google.gdata.data.docs.*;
import com.google.gdata.util.*;
import com.google.gdata.client.media.ResumableGDataFileUploader;
import com.google.gdata.client.uploader.FileUploadData;
import com.google.gdata.client.uploader.ProgressListener;
import com.google.gdata.client.uploader.ResumableHttpFileUploader;
import com.google.gdata.data.Link;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.MediaSource;
import com.thoughtworks.xstream.XStream;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
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
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.yccheok.jstock.engine.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
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
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
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
    public static boolean restartApplication(MainFrame mainFrame)
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

    // Get date information from Google server.
    public static java.util.Date getGoogleServerDate() {
        final String _time = org.yccheok.jstock.gui.Utils.getUUIDValue(org.yccheok.jstock.network.Utils.getURL(Type.GET_TIME), "time");
        if (_time == null) {
            return null;
        }
        try {
            final long time = Long.parseLong(_time);
            final Date date = new Date(time);
            return date;
        } catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        return null;
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
        assert(destDirectory.endsWith(File.separator));
        
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

                    final String destination =  destDirectory + zipEntry.getName();

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
    
    public static boolean createDirectoryIfDoesNotExist(String directory) {
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return Utils.getUserDataDirectory() + jStockOptions.getCountry() + File.separator + "history";
    }

    public static AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }

    /**
     * Migrates configuration data from version 1.0.5y to 1.0.6.
     *
     * @return true if migration success
     */
    public static boolean migrateFrom105yTo106() {
        // File (or directory) with old name
        final File oldDirectory = new File(System.getProperty("user.home") + File.separator + ".jstock" + File.separator + "1.0.5" + File.separator);

        // File (or directory) with new name
        final File newDirectory = new File(getUserDataDirectory());

        // Migrate already?
        if (newDirectory.isDirectory() && newDirectory.exists()) {
            return true;
        }

        // No 1.0.5y found?
        if (oldDirectory.isDirectory() == false || oldDirectory.exists() == false) {
            return true;
        }

        // Rename file (or directory)
        boolean status = oldDirectory.renameTo(newDirectory);

        return status;
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

    /**
     * Returns empty stock based on given stock info.
     *
     * @param stockInfo the stock info
     * @return empty stock based on given stock info
     */
    public static Stock getEmptyStock(StockInfo stockInfo) {
        return getEmptyStock(stockInfo.code, stockInfo.symbol);
    }

    /**
     * Returns empty stock based on given code and symbol.
     *
     * @param code the code
     * @param symbol the symbol
     * @return empty stock based on given code and symbol
     */
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
        }
        catch(org.jasypt.exceptions.EncryptionOperationNotPossibleException exp) {
            log.error(null, exp);
        }

        return "";
    }

    public static String getJStockUUID() {
        return "fe78440e-e0fe-4efb-881d-264a01be483c";
    }

    // Returns application name, used by Google Doc service.
    private static String getApplicationName() {
        return "JStock-" + APPLICATION_VERSION_ID;
    }
    
    // Remember to revise googleDocTitlePattern if we change the definition
    // of this method.
    private static String getGoogleDocTitle(long checksum, long date, int version) {
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

    public static CloudFile loadFromGoogleDoc(String username, String password) {
        CaptchaRespond captchaRespond = null;
        DocsService client = new DocsService(getApplicationName());
        do {
            try {
                if (captchaRespond == null) {
                    client.setUserCredentials(username, password);
                } else {
                    client.setUserCredentials(username, password, captchaRespond.logintoken, captchaRespond.logincaptcha);
                }
                break;
            } catch (CaptchaRequiredException ex) {
                log.error(null, ex);
                captchaRespond = Utils.getCapchaRespond(ex);
                if (captchaRespond == null) {
                    return null;
                }
            } catch (AuthenticationException ex) {
                log.error(null, ex);
                return null;
            }
        } while (true);

        // Login success. Let's find the cloud file.
        try {
            URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
            DocumentQuery query = new DocumentQuery(feedUri);
            // Get Everything
            DocumentListFeed allEntries = new DocumentListFeed();
            DocumentListFeed tempFeed = client.getFeed(query, DocumentListFeed.class);
            do {
                allEntries.getEntries().addAll(tempFeed.getEntries());
                Link nextLink = tempFeed.getNextLink();
                if ((nextLink == null) || (tempFeed.getEntries().isEmpty())) {
                  break;
                }
                tempFeed = client.getFeed(new URL(nextLink.getHref()), DocumentListFeed.class);
            } while (true);

            DocumentListEntry documentListEntry = null;

            long checksum = 0;
            long date = 0;
            int version = 0;
            
            for (DocumentListEntry entry : allEntries.getEntries()) {
                // Use title, not filename.
                final String title = entry.getTitle().getPlainText();
                if (title == null) {
                    // Do we really need to perform null checking?                    
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
                
                documentListEntry = entry;
                final File temp = File.createTempFile(Utils.getJStockUUID(), ".zip");
                downloadFile(client, documentListEntry, temp);
                return CloudFile.newInstance(temp, checksum, date, version);                
            }
        } catch (IOException ex) {
            log.error(null, ex);
            return null;
        } catch (ServiceException ex) {
            log.error(null, ex);
            return null;
        }
        return null;
    }
    
    private static void downloadFile(DocsService client, DocumentListEntry entry, File file)
        throws IOException, MalformedURLException, ServiceException {

        MediaContent mc = (MediaContent) entry.getContent();
        MediaSource ms = client.getMedia(mc);

        InputStream inStream = null;
        FileOutputStream outStream = null;

        try {
            inStream = ms.getInputStream();
            outStream = new FileOutputStream(file);

            int c;
            while ((c = inStream.read()) != -1) {
                outStream.write(c);
            }
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
        }
    }

    public static CloudFile loadFromCloud(String username, String password) {
        CaptchaRespond captchaRespond = null;
        do {
            final String url = "https://jstock-cloud.appspot.com/DownloadServlet";
            final PostMethod post = new PostMethod(url);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                org.yccheok.jstock.engine.Utils.setHttpClientProxyFromSystemProperties(httpClient);
                org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(httpClient);

                NameValuePair[] data = null;

                if (captchaRespond == null) {
                    data = new NameValuePair[] {
                        new NameValuePair("Email", username),
                        new NameValuePair("Passwd", password)
                    };
                }
                else {
                    data = new NameValuePair[] {
                        new NameValuePair("Email", username),
                        new NameValuePair("Passwd", password),
                        new NameValuePair("logintoken", captchaRespond.logintoken),
                        new NameValuePair("logincaptcha", captchaRespond.logincaptcha)
                    };
                }

                post.setRequestBody(data);
                // No ProxyAuth support yet. I do not know how to do so.
                httpClient.executeMethod(post);
                final Header header = post.getResponseHeader("Content-Type");
                if (header == null || header.getValue() == null) {
                    return null;
                }

                // Returns text/plain; charset=iso-8859-1
                if (true == header.getValue().contains("text/plain")) {
                    final String respond = post.getResponseBodyAsString();
                    if (respond == null) {
                        return null;
                    }
                    /* Captcha guess? */
                    captchaRespond = Utils.getCapchaRespond(respond);

                    if (captchaRespond == null) {
                        return null;
                    }
                    continue;
                }

                if (false == header.getValue().equalsIgnoreCase("application/octet-stream")) {
                    return null;
                }

                String _checksum = post.getResponseHeader("jstock-custom-checksum").getValue();
                String _date = post.getResponseHeader("jstock-custom-date").getValue();
                String _version = post.getResponseHeader("jstock-custom-version").getValue();
                if (_checksum == null || _date == null || _version == null) {
                    return null;
                }

                long checksum = Long.parseLong(_checksum);
                long date = Long.parseLong(_date);
                int version = Integer.parseInt(_version);

                inputStream = post.getResponseBodyAsStream();
                final File temp = File.createTempFile(Utils.getJStockUUID(), ".zip");
                temp.deleteOnExit();
                outputStream = new FileOutputStream(temp);
                byte buf[] = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                return CloudFile.newInstance(temp, checksum, date, version);
            }
            catch (FileNotFoundException ex) {
                log.error(null, ex);
                return null;
            }
            catch (IOException ex) {
                log.error(null, ex);
                return null;
            }
            catch (NumberFormatException ex) {
                log.error(null, ex);
                return null;
            }
            finally {
                close(outputStream);
                close(inputStream);
                post.releaseConnection();
            }
        } while (true);
    }

    private static class CaptchaRespond {
        public final String logintoken;
        public final String logincaptcha;
        public CaptchaRespond(String logintoken, String logincaptcha) {
            this.logintoken = logintoken;
            this.logincaptcha = logincaptcha;
        }
    }

    private static CaptchaRespond getCapchaRespond(CaptchaRequiredException captchaRequiredException) {
        final String CaptchaToken = captchaRequiredException.getCaptchaToken();
        final String CaptchaUrl = captchaRequiredException.getCaptchaUrl();
        try {
            URL url = new URL("https://www.google.com/accounts/" + CaptchaUrl);
            BufferedImage image = ImageIO.read(url);
            final CaptchaInputJDialog dialog = new CaptchaInputJDialog(MainFrame.getInstance(), image, true);
            // Possible deadlock?
            // SwingUtilities.invokeAndWait(new Runnable() {
            //    @Override
            //    public void run() {                        
            //        dialog.setLocationRelativeTo(MainFrame.getInstance());
            //        dialog.setVisible(true);
            //    }
            //});
            dialog.setLocationRelativeTo(MainFrame.getInstance());
            dialog.setVisible(true);
            if (dialog.getCaptcha() == null || dialog.getCaptcha().length() <= 0) {
                return null;
            }
            return new CaptchaRespond(CaptchaToken, dialog.getCaptcha());
        } catch (Exception exp) {
            log.error(null, exp);
            return null;
        }        
    }
    
    private static CaptchaRespond getCapchaRespond(String respond) {
        assert(respond != null);

        /* Handle Captcha. */
        final String[] res = respond.split("\\r?\\n");
        final Map<String, String> map = new HashMap<String, String>();
        for (String r : res) {
            final String[] v = r.split("=", 2);
            if (v.length == 2) {
                v[0] = v[0].trim();
                v[1] = v[1].trim();
                if (v[0].length() == 0 || v[1].length() == 0) {
                    continue;
                }
                map.put(v[0], v[1]);
            }
        }

        if (map.containsKey("CaptchaToken") && map.containsKey("CaptchaUrl")) {
            final String CaptchaToken = map.get("CaptchaToken");
            final String CaptchaUrl = map.get("CaptchaUrl");

            try {
                URL url = new URL("https://www.google.com/accounts/" + CaptchaUrl);
                BufferedImage image = ImageIO.read(url);
                final CaptchaInputJDialog dialog = new CaptchaInputJDialog(MainFrame.getInstance(), image, true);
                // Possible deadlock?
                // SwingUtilities.invokeAndWait(new Runnable() {
                //    @Override
                //    public void run() {                        
                //        dialog.setLocationRelativeTo(MainFrame.getInstance());
                //        dialog.setVisible(true);
                //    }
                //});
                dialog.setLocationRelativeTo(MainFrame.getInstance());
                dialog.setVisible(true);
                if (dialog.getCaptcha() == null || dialog.getCaptcha().length() <= 0) {
                    return null;
                }
                return new CaptchaRespond(CaptchaToken, dialog.getCaptcha());
            }
            catch (Exception exp) {
                log.error(null, exp);
                return null;
            }
        }
        return null;
    }

    private static class FileUploadProgressListener implements ProgressListener {

        private final CountDownLatch countDownLatch = new CountDownLatch(1);
        
        @Override
        public synchronized void progressChanged(ResumableHttpFileUploader uploader)
        {
            final String fileId = ((FileUploadData) uploader.getData()).getFileName();
            switch(uploader.getUploadState()) {
            case COMPLETE:
            case CLIENT_ERROR:
                countDownLatch.countDown();
                log.info(fileId + ": Completed");
                break;
            
            case IN_PROGRESS:
                log.info(fileId + ":" + String.format("%3.0f", uploader.getProgress() * 100) + "%");
                break;
        
            case NOT_STARTED:
                log.info(fileId + ":" + "Not Started");
                break;
            }
        }
        
        public void await() throws InterruptedException {
            countDownLatch.await();
        }
    }
    
    public static boolean saveToGoogleDoc(String username, String password, File file) {
        CaptchaRespond captchaRespond = null;
        DocsService client = new DocsService(getApplicationName());
        do {
            try {
                if (captchaRespond == null) {
                    client.setUserCredentials(username, password);
                } else {
                    client.setUserCredentials(username, password, captchaRespond.logintoken, captchaRespond.logincaptcha);
                }
                break;
            } catch (CaptchaRequiredException ex) {
                log.error(null, ex);
                captchaRespond = Utils.getCapchaRespond(ex);
                if (captchaRespond == null) {
                    return false;
                }
            } catch (AuthenticationException ex) {
                log.error(null, ex);
                return false;
            }
        } while (true);

        try {
            // Login success. Determine whether we need to perform NEW or UPDATE
            // operation.
            URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
            DocumentQuery query = new DocumentQuery(feedUri);
            // Get Everything
            DocumentListFeed allEntries = new DocumentListFeed();
            DocumentListFeed tempFeed = client.getFeed(query, DocumentListFeed.class);
            do {
                allEntries.getEntries().addAll(tempFeed.getEntries());
                Link nextLink = tempFeed.getNextLink();
                if ((nextLink == null) || (tempFeed.getEntries().isEmpty())) {
                  break;
                }
                tempFeed = client.getFeed(new URL(nextLink.getHref()), DocumentListFeed.class);
            } while (true);

            DocumentListEntry documentListEntry = null;

            for (DocumentListEntry entry : allEntries.getEntries()) {
                final String filename = entry.getFilename();
                if (filename == null) {
                    continue;
                }
                // Retrieve checksum, date and version information from filename.
                final Matcher matcher = googleDocTitlePattern.matcher(filename);
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
                    Long.parseLong(_checksum);
                    Long.parseLong(_date);
                    Integer.parseInt(_version);
                } catch (NumberFormatException ex) {
                    log.error(null, ex);
                    continue;
                }
                documentListEntry = entry;
                break;
            }

            final long checksum = org.yccheok.jstock.analysis.Utils.getChecksum(file);
            final long date = new Date().getTime();
            final int version = org.yccheok.jstock.gui.Utils.getApplicationVersionID();

            // Login success. Let's upload the cloud file.
            final int MAX_CONCURRENT_UPLOADS = 10;
            final int PROGRESS_UPDATE_INTERVAL = 1000;
            final int DEFAULT_CHUNK_SIZE = 10485760;

            // Create a listener
            FileUploadProgressListener listener = new FileUploadProgressListener();

            // Pool for handling concurrent upload tasks
            ExecutorService executor = Executors.newFixedThreadPool(MAX_CONCURRENT_UPLOADS);

            String contentType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();
            MediaFileSource mediaFile = new MediaFileSource(file, contentType);
        
            URL createUploadUrl = new URL("https://docs.google.com/feeds/upload/create-session/default/private/full?convert=false");
            ResumableGDataFileUploader uploader = null;
            if (documentListEntry == null) {
                // New file.
                uploader = new ResumableGDataFileUploader.Builder(client, createUploadUrl, mediaFile, null)
                .title(getGoogleDocTitle(checksum, date, version))
                .chunkSize(DEFAULT_CHUNK_SIZE).executor(executor)
                .trackProgress(listener, PROGRESS_UPDATE_INTERVAL)
                .build();
            } else {
                // Rename and overwrite.
                documentListEntry.setTitle(new PlainTextConstruct(getGoogleDocTitle(checksum, date, version)));
                uploader = new ResumableGDataFileUploader.Builder(client, createUploadUrl, mediaFile, documentListEntry)
                .title(getGoogleDocTitle(checksum, date, version))
                .chunkSize(DEFAULT_CHUNK_SIZE).executor(executor)
                .trackProgress(listener, PROGRESS_UPDATE_INTERVAL).requestType(ResumableGDataFileUploader.RequestType.UPDATE)
                .build();
            }
            uploader.start();

            // Wait for completion.
            listener.await();

            // Thread clean up.
            executor.shutdownNow();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);            
        } catch (java.net.MalformedURLException ex) {
            // Impossible.
            log.error(null, ex);
            return false;
        } catch (IOException ex) {
            log.error(null, ex);
            return false;            
        } catch (ServiceException ex) {
            log.error(null, ex);
            return false;
        } catch (InterruptedException ex) {
            log.error(null, ex);
            return false;            
        }

        return true;
    }
    
    public static boolean saveToCloud(String username, String password, File file) {
        CaptchaRespond captchaRespond = null;

        do {
            final String url = "https://jstock-cloud.appspot.com/UploadServlet";
            final PostMethod post = new PostMethod(url);
            try {
                org.yccheok.jstock.engine.Utils.setHttpClientProxyFromSystemProperties(httpClient);
                org.yccheok.jstock.gui.Utils.setHttpClientProxyCredentialsFromJStockOptions(httpClient);
                Part[] parts = null;

                if (captchaRespond == null) {
                    parts = new Part[]{
                        new StringPart("Email", username),
                        new StringPart("Passwd", password),
                        new StringPart("Date", new Date().getTime() + ""),
                        new StringPart("Checksum", org.yccheok.jstock.analysis.Utils.getChecksum(file) + ""),
                        new StringPart("Version", org.yccheok.jstock.gui.Utils.getApplicationVersionID() + ""),
                        new FilePart("file", file)
                    };
                }
                else {
                    parts = new Part[]{
                        new StringPart("Email", username),
                        new StringPart("Passwd", password),
                        new StringPart("Date", new Date().getTime() + ""),
                        new StringPart("Checksum", org.yccheok.jstock.analysis.Utils.getChecksum(file) + ""),
                        new StringPart("Version", org.yccheok.jstock.gui.Utils.getApplicationVersionID() + ""),
                        new StringPart("logintoken", captchaRespond.logintoken),
                        new StringPart("logincaptcha", captchaRespond.logincaptcha),
                        new FilePart("file", file)
                    };
                }
                post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));

                // No ProxyAuth support yet. I do not know how to do so.
                httpClient.executeMethod(post);
                final String respond = post.getResponseBodyAsString();
                if (respond == null) {
                    return false;
                }
                if (respond.equals("OK")) {
                    return true;
                }

                captchaRespond = Utils.getCapchaRespond(respond);

                if (captchaRespond == null) {
                    return false;
                }
            }
            catch (FileNotFoundException ex) {
                log.error(null, ex);
                return false;
            }
            catch (IOException ex) {
                log.error(null, ex);
                return false;
            }
            finally {
                post.releaseConnection();
            }
        } while (true);
    }

    public static boolean isCompatible(int applicationVersionID) {
        if (applicationVersionID == APPLICATION_VERSION_ID) {
            return true;
        }
        else if (applicationVersionID >= 1051 && applicationVersionID <= 1095) {
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

        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
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
            final int returnVal = chooser.showSaveDialog(MainFrame.getInstance());
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

                final int result = javax.swing.JOptionPane.showConfirmDialog(MainFrame.getInstance(), output, MessagesBundle.getString("question_title_replace_old"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
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
        int returnVal = chooser.showOpenDialog(MainFrame.getInstance());

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
                    final InputStream audioSrc = Utils.class.getResourceAsStream("/sounds/doorbell.wav");
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
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
            final int returnVal = chooser.showSaveDialog(MainFrame.getInstance());
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

                final int result = javax.swing.JOptionPane.showConfirmDialog(MainFrame.getInstance(), output, MessagesBundle.getString("question_title_replace_old"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
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
    
    public static boolean isDatabaseFilesInXML(int version) {
        // 1093 = 1.0.6r
        return version <= 1093;
    }
    
    public static boolean isWatchlistAndPortfolioFilesInXML(int version) {
        // 1090 = 1.0.6o
        return version <= 1090;
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
    
    private static final HanyuPinyinOutputFormat DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT = new HanyuPinyinOutputFormat();
    static {
        DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        DEFAULT_HANYU_PINYIN_OUTPUT_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    private static volatile List<String> NTPServers = null;

    // We will use this as directory name. Do not have space or special characters.
    private static final String APPLICATION_VERSION_STRING = "1.0.6";

    // For About box comparision on latest version purpose.
    // 1.0.6u
    // Remember to update isCompatible method.
    private static final int APPLICATION_VERSION_ID = 1096;

    private static Executor zombiePool = Executors.newFixedThreadPool(Utils.NUM_OF_THREADS_ZOMBIE_POOL);

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
