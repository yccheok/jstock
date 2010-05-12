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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.StockTableModel;

/**
 *
 * @author yccheok
 */
public class Utils {

    /**
     * Prevent from being instantiated.
     */
    private Utils() {
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

    /**
     * Returns true if the given file is a watchlist directory.
     *
     * @param file The <code>File</code> to be checked against
     * @return true if the given file is a watchlist directory
     */
    private static boolean isWatchlistDirectory(File file) {
        if (false == file.isDirectory()) {
            return false;
        }
        String[] files = file.list();
        List<String> list = Arrays.asList(files);
        return list.contains("realtimestock.xml") && list.contains("realtimestockalert.xml");
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
        if (new File(directory + "realtimestock.xml").exists() || new File(directory + "realtimestockalert.xml").exists()) {
            return false;
        }

        final StockTableModel stockTableModel = new StockTableModel();

        return
        org.yccheok.jstock.gui.Utils.toXML(stockTableModel.getStocks(), directory + "realtimestock.xml") &&
        org.yccheok.jstock.gui.Utils.toXML(stockTableModel.getAlerts(), directory + "realtimestockalert.xml");
    }
}
