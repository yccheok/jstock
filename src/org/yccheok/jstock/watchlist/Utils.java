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
import java.util.Collections;
import java.util.List;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
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
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getWatchlistDirectory(jStockOptions.getCountry(), name);
    }

    public static String getWatchlistDirectory(Country country, String name) {
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "watchlist" + File.separator + name + File.separator;
    }
    
    public static String getWatchlistDirectory() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
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
    
    public static List<String> getWatchlistNames() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getWatchlistNames(jStockOptions.getCountry());
    }
    
    /**
     * Returns all available watchlist names for current selected country.
     *
     * @return all available watchlist names for current selected country
     */
    public static List<String> getWatchlistNames(Country country) {
        List<String> watchlistNames = new ArrayList<String>();        
        final File file = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "watchlist" + File.separator);
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
        
        Collections.sort(watchlistNames);
        
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
    
    /**
     * Creates empty watchlist for current selected country.
     *
     * @return true if empty watchlist creation success
     */
    public static boolean createEmptyWatchlist(String name) {
        final String directory = getWatchlistDirectory(name);
        
        // Do not allow to create empty watchlist, if the desired location already
        // contain watchlist files.
        if (new File(directory + "realtimestock.csv").exists()) {
            return false;
        }
        
        if (false == org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory)) {
            return false;
        }

        final StockTableModel stockTableModel = new StockTableModel();
        
        JStock.CSVWatchlist csvWatchlist = JStock.CSVWatchlist.newInstance(stockTableModel);

        return JStock.saveCSVWatchlist(directory, csvWatchlist);
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
                    int lines = org.yccheok.jstock.gui.Utils.numOfLines(realTimeStockFile, true);
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
