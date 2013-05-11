/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.watchlist;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.StockAlert;
import org.yccheok.jstock.gui.StockTableModel;

/**
 *
 * @author yccheok
 */
public class Utils {
    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> stockPriceNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            return new DecimalFormat("0.00##");
        }
    };
    
    // For XML to CSV migration usage.
    private static final class XMLWatchlist {
        public final StockTableModel stockTableModel;
        
        private XMLWatchlist(StockTableModel stockTableModel) {
            if (stockTableModel == null) {
                throw new java.lang.IllegalArgumentException();
            }
            this.stockTableModel = stockTableModel;
        }
        
        public static XMLWatchlist newInstance(StockTableModel stockTableModel) {
            return new XMLWatchlist(stockTableModel);
        }
    }    
    
    /**
     * Prevent from being instantiated.
     */
    private Utils() {
    }

    public static File getWatchlistFile(String directory) {
        return new File(directory  + "realtimestock.csv");
    }
    
    /**
     * Returns watchlist directory, based on given watchlist name. There is
     * chance where the returned directory doesn't exist. To verify against
     * existence, use <code>Utils.isWatchlistDirectory</code>.
     *
     * @param name watchlist name
     * @return watchlist directory, based on given watchlist name
     */
    public static String getWatchlistDirectory(String name) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + jStockOptions.getCountry() + File.separator + "watchlist" + File.separator + name + File.separator;
    }

    public static String getWatchlistDirectory() {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return getWatchlistDirectory(jStockOptions.getWatchlistName());
    }
    
    /**
     * Returns true if the given file is a watchlist directory.
     *
     * @param file The <code>File</code> to be checked against
     * @return true if the given file is a watchlist directory
     */
    private static boolean isXMLWatchlistDirectory(File file) {
        if (false == file.isDirectory()) {
            return false;
        }
        String[] files = file.list();
        List<String> list = Arrays.asList(files);
        return list.contains("realtimestock.xml") && list.contains("realtimestockalert.xml");
    }

    private static boolean isWatchlistDirectory(File file) {
        if (false == file.isDirectory()) {
            return false;
        }
        String[] files = file.list();
        List<String> list = Arrays.asList(files);
        return list.contains("realtimestock.csv");
    }
    
    private static List<String> getXMLWatchlistNames(String baseDirectory, Country country, boolean oldData) {
        List<String> watchlistNames = new ArrayList<String>();
        final File file = oldData ?
            new File(baseDirectory + country + File.separator + "config" + File.separator) :
            new File(baseDirectory + country + File.separator + "watchlist" + File.separator);
        
        File[] children = file.listFiles();
        if (children == null) {
            // Either dir does not exist or is not a directory
            return watchlistNames;
        } else {
            // Only seek for 1st level directory.
            for (File child : children) {
                if (isXMLWatchlistDirectory(child)) {
                    watchlistNames.add(child.getName());
                }
            }
        }
        return watchlistNames;
    }
    
    /**
     * Returns all available watchlist names for current selected country.
     *
     * @return all available watchlist names for current selected country
     */
    public static List<String> getWatchlistNames() {
        List<String> watchlistNames = new ArrayList<String>();
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        final File file = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + jStockOptions.getCountry() + File.separator + "watchlist" + File.separator);
        File[] children = file.listFiles();
        if (children == null) {
            // Either dir does not exist or is not a directory
            return watchlistNames;
        } else {
            // Only seek for 1st level directory.
            for (File child : children) {
                if (isWatchlistDirectory(child)) {
                    watchlistNames.add(child.getName());
                }
            }
        }
        return watchlistNames;
    }


    /**
     * Returns default watchlist name for all country.
     *
     * @return default watchlist name for all country
     */
    public static String getDefaultWatchlistName() {
        return "My Watchlist";
    }
    
    private static XMLWatchlist getXMLWatchlist(String directory) {
        final File realTimeStockFile;
        final File realTimeStockAlertFile;
        
        // Determine the files to be loaded from disc.
        realTimeStockFile = new File(directory + "realtimestock.xml");
        realTimeStockAlertFile = new File(directory + "realtimestockalert.xml");

        // Try to load files from disc.
        java.util.List<Stock> stocks = org.yccheok.jstock.gui.Utils.fromXML(java.util.List.class, realTimeStockFile);
        java.util.List<StockAlert> alerts = org.yccheok.jstock.gui.Utils.fromXML(java.util.List.class, realTimeStockAlertFile);
        final StockTableModel stockTableModel = new StockTableModel();
        
        if (stocks != null && alerts != null) {
            if (alerts.size() != stocks.size())
            {
                for (Stock stock : stocks) {
                    final Stock emptyStock = org.yccheok.jstock.gui.Utils.getEmptyStock(stock.code, stock.symbol);
                    stockTableModel.addStock(emptyStock);
                }
            }
            else
            {
                final int size = stocks.size();
                for(int i = 0; i < size; i++) {
                    final Stock stock = stocks.get(i);
                    final StockAlert alert = alerts.get(i);
                    final Stock emptyStock = org.yccheok.jstock.gui.Utils.getEmptyStock(stock.code, stock.symbol);
                    stockTableModel.addStock(emptyStock, alert);
                }
            }      
        }
        
        return XMLWatchlist.newInstance(stockTableModel);
    }
    

    // TODO : Remove this code after some time.
    // The directory format should be "C:\Users\yccheok\.jstock\1.0.6\", or
    // temporary directory holding the extracted cloud files.
    public static boolean migrateXMLToCSVWatchlists(String srcBaseDirectory, String destBaseDirectory) {
        assert(srcBaseDirectory.endsWith(File.separator));
        assert(destBaseDirectory.endsWith(File.separator));
        
        boolean status = true;
        
        for (Country country : Country.values()) {
            List<String> names = getXMLWatchlistNames(srcBaseDirectory, country, false);
            final boolean oldData = names.size() <= 0;
            
            boolean localStatus = true;
            
            if (oldData) {
                final String oldDirectory = srcBaseDirectory + country + File.separator + "config" + File.separator;
                final String srcDirectory = oldDirectory;
                final String destDirectory = destBaseDirectory + country + File.separator + "watchlist" + File.separator + getDefaultWatchlistName() + File.separator;
                
                XMLWatchlist xmlPortfolio = getXMLWatchlist(srcDirectory);
                
                MainFrame.CSVWatchlist csvWatchlist = MainFrame.CSVWatchlist.newInstance(xmlPortfolio.stockTableModel);
                
                localStatus = MainFrame.saveCSVWatchlist(destDirectory, csvWatchlist);
                
                if (localStatus) {
                    deleteXMLWatchlist(srcDirectory);
                }
            } else {

                for (String name : names) {
                    final String directory = srcBaseDirectory + country + File.separator + "watchlist" + File.separator + name + File.separator;
                    final String srcDirectory = directory;
                    final String destDirectory = destBaseDirectory + country + File.separator + "watchlist" + File.separator + name + File.separator;

                    XMLWatchlist xmlPortfolio = getXMLWatchlist(srcDirectory);

                    MainFrame.CSVWatchlist csvWatchlist = MainFrame.CSVWatchlist.newInstance(xmlPortfolio.stockTableModel);

                    boolean _localStatus = MainFrame.saveCSVWatchlist(destDirectory, csvWatchlist);

                    if (_localStatus) {
                        deleteXMLWatchlist(directory);
                    }

                    localStatus = localStatus & _localStatus;
                }
            }   // if (oldData)
            
            if (localStatus) {
                // Delete legacy old folder.
                final String oldDirectory = srcBaseDirectory + country + File.separator + "config" + File.separator;
                deleteXMLWatchlist(oldDirectory);
                File dir = new File(oldDirectory);
                if (dir.isDirectory()) {
                    if (dir.list().length == 0) {
                        dir.delete();
                    }
                }
            }
            
            status = status & localStatus;
        }   // for (Country country : Country.values())
        
        return status;
    }
    
    // TODO : Remove this code after some time.
    // Never ever delete directory itself.
    private static boolean deleteXMLWatchlist(String directory) {
        final File realTimeStockFile = new File(directory  + "realtimestock.xml");
        final File realTimeStockAlertFile = new File(directory  + "realtimestockalert.xml");        

        realTimeStockFile.delete();
        realTimeStockAlertFile.delete();
        
        return true;
    }
    
    /**
     * Creates empty watchlist for current selected country.
     *
     * @return true if empty watchlist creation success
     */
    public static boolean createEmptyWatchlist(String name) {
        final String directory = getWatchlistDirectory(name);
        if (false == org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory)) {
            return false;
        }

        // Do not allow to create empty watchlist, if the desired location already
        // contain watchlist files.
        if (new File(directory + "realtimestock.csv").exists()) {
            return false;
        }

        final StockTableModel stockTableModel = new StockTableModel();
        
        MainFrame.CSVWatchlist csvWatchlist = MainFrame.CSVWatchlist.newInstance(stockTableModel);

        return MainFrame.saveCSVWatchlist(directory, csvWatchlist);
    }
    
    public static List<WatchlistInfo> getWatchlistInfos() {
        List<WatchlistInfo> watchlistInfos = new ArrayList<WatchlistInfo>();
        for (Country country : Country.values()) {
            final File file = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "watchlist" + File.separator);
            File[] children = file.listFiles();
            if (children == null) {
                // Either dir does not exist or is not a directory
                continue;
            } else {
                // Only seek for 1st level directory.
                for (File child : children) {
                    File realTimeStockFile = new File(child, "realtimestock.csv");
                    int lines = org.yccheok.jstock.gui.Utils.numOfLines(realTimeStockFile);
                    // Skip CSV header.
                    lines = lines - 1;
                    if (lines > 0) {
                        WatchlistInfo watchlistInfo = WatchlistInfo.newInstance(country, child.getName(), lines);
                        watchlistInfos.add(watchlistInfo);
                    }
                }                
            }
        }
        return watchlistInfos;
    }

    /**
     * Convert the value to stock price representation.
     *
     * @param value the value to be converted
     * @return stock price representation
     */
    public static String toStockPrice(double value) {
        return stockPriceNumberFormat.get().format(value);
    }    
}
