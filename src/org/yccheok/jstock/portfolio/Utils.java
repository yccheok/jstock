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

package org.yccheok.jstock.portfolio;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.BuyPortfolioTreeTableModel;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.PortfolioManagementJPanel;
import org.yccheok.jstock.gui.SellPortfolioTreeTableModel;

/**
 *
 * @author yccheok
 */
public class Utils {
    
    // For XML to CSV migration usage.
    private static final class XMLPortfolio {
        public final BuyPortfolioTreeTableModel buyPortfolioTreeTableModel;
        public final SellPortfolioTreeTableModel sellPortfolioTreeTableModel;        
        public final DividendSummary dividendSummary;
        public final DepositSummary depositSummary;


        @SuppressWarnings( "deprecation" )
        private XMLPortfolio(BuyPortfolioTreeTableModel buyPortfolioTreeTableModel, SellPortfolioTreeTableModel sellPortfolioTreeTableModel, DividendSummary dividendSummary, DepositSummary depositSummary) {
            if (buyPortfolioTreeTableModel == null || sellPortfolioTreeTableModel == null || dividendSummary == null || depositSummary == null) {
                throw new java.lang.IllegalArgumentException();
            }
            this.buyPortfolioTreeTableModel = buyPortfolioTreeTableModel;
            this.sellPortfolioTreeTableModel = sellPortfolioTreeTableModel;
            this.dividendSummary = dividendSummary;
            this.depositSummary = depositSummary;
        }
        
        public static XMLPortfolio newInstance(BuyPortfolioTreeTableModel buyPortfolioTreeTableModel, SellPortfolioTreeTableModel sellPortfolioTreeTableModel, DividendSummary dividendSummary, DepositSummary depositSummary) {
            return new XMLPortfolio(buyPortfolioTreeTableModel, sellPortfolioTreeTableModel, dividendSummary, depositSummary);
        }
    }

    // Prevent from being instantiated.
    private Utils() {
    }

    private static final ThreadLocal <NumberFormat> unitsNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Instead of limiting currency decimal places to 2 only.
            return new DecimalFormat("#,##0.####");
        }
    };
    
    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> twoDecimalPlaceCurrencyNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            return new DecimalFormat("#,##0.00");
        }
    };
    
    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> threeDecimalPlaceCurrencyNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Instead of limiting currency decimal places to 2 only, we allow
            // them to float between 2 to 3, to avoid from losing precision.
            return new DecimalFormat("#,##0.00#");
        }
    };

    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> fourDecimalPlaceCurrencyNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Instead of limiting currency decimal places to 2 only, we allow
            // them to float between 2 to 4, to avoid from losing precision.
            return new DecimalFormat("#,##0.00##");
        }
    };
    
    // Use ThreadLocal to ensure thread safety
    private static final ThreadLocal <NumberFormat> quantityNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Instead of limiting currency decimal places to 0 only, we allow
            // them to float between 0 to 3, to avoid from losing precision.
            return new DecimalFormat("#,##0.###");
        }
    };

    // Use ThreadLocal to ensure thread safety
    private static final ThreadLocal <NumberFormat> exchangeRateNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Instead of limiting currency decimal places to 0 only, we allow
            // them to float between 0 to 6, to avoid from losing precision.
            return new DecimalFormat("#,##0.######");
        }
    };
    
    // Get the latest dividend. If there are more than 1 latest dividend, we will
    // simply pick up 1 of them. null if not found.
    public static Dividend getLatestDividend(DividendSummary dividendSummary, Code code) {
        Dividend latestDividend = null;
        for (int i = 0, ei = dividendSummary.size(); i < ei; i++) {
            Dividend dividend = dividendSummary.get(i);
            // We will only consider non-zero dividend.
            if (dividend.amount > 0.0 && dividend.stockInfo.code.equals(code)) {
                if (latestDividend == null) {
                    latestDividend = dividend;
                } else {
                    if (latestDividend.date.compareTo(dividend.date) < 0) {
                        latestDividend = dividend;
                    }
                }
            }
        }
        return latestDividend;
    }
    
    /**
     * Replace CSV line feed to system dependent line feed.
     * @param string the string
     * @return String with system dependent line feed
     */
    public static String replaceCSVLineFeedToSystemLineFeed(String string) {
        // OpenCSV is using "\n" as line feed.
        final String CSVLineFeed = "\n";
        final String systemLineFeed = System.getProperty("line.separator");
        return string.replaceAll(CSVLineFeed, systemLineFeed);
    }
    
    /**
     * Convert the value to exchange rate representation.
     *
     * @param value the value to be converted
     * @return exchange rate representation
     */
    public static String toExchangeRate(Object value) {
        return exchangeRateNumberFormat.get().format(value);
    }

    /**
     * Convert the value to exchange rate representation.
     *
     * @param value the value to be converted
     * @return exchange rate representation
     */
    public static String toExchangeRate(double value) {
        return exchangeRateNumberFormat.get().format(value);
    }
    
    /**
     * Convert the value to stock quantity representation.
     *
     * @param value the value to be converted
     * @return stock quantity representation
     */
    public static String toQuantity(Object value) {
        return quantityNumberFormat.get().format(value);
    }

    /**
     * Convert the value to stock quantity representation.
     *
     * @param value the value to be converted
     * @return stock quantity representation
     */
    public static String toQuantity(double value) {
        return quantityNumberFormat.get().format(value);
    }

    /**
     * Convert the value to currency representation (without symbol).
     *
     * @param value the value to be converted
     * @return currency representation (without symbol)
     */
    public static String toCurrency(DecimalPlaces decimalPlace, Object value) {
        if (decimalPlace == DecimalPlaces.Two) {
            return twoDecimalPlaceCurrencyNumberFormat.get().format(value);
        } else if (decimalPlace == DecimalPlaces.Three) {
            return threeDecimalPlaceCurrencyNumberFormat.get().format(value);
        }
        assert(decimalPlace == DecimalPlaces.Four);
        return fourDecimalPlaceCurrencyNumberFormat.get().format(value);
    }

    /**
     * Convert the value to currency representation (without symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (without symbol)
     */
    public static String toCurrency(DecimalPlaces decimalPlace, double value) {
        if (decimalPlace == DecimalPlaces.Two) {
            return twoDecimalPlaceCurrencyNumberFormat.get().format(value);
        } else if (decimalPlace == DecimalPlaces.Three) {
            return threeDecimalPlaceCurrencyNumberFormat.get().format(value);
        }
        assert(decimalPlace == DecimalPlaces.Four);
        return fourDecimalPlaceCurrencyNumberFormat.get().format(value);
    }
    
    /**
     * Convert the value to currency representation (with symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (with symbol)
     */
    public static String toCurrencyWithSymbol(DecimalPlaces decimalPlace, Object value) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        final Country country = jStockOptions.getCountry();
        return jStockOptions.getCurrencySymbol(country) + toCurrency(decimalPlace, value);
    }

    /**
     * Convert the value to currency representation (with symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (with symbol)
     */
    public static String toCurrencyWithSymbol(DecimalPlaces decimalPlace, double value) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        final Country country = jStockOptions.getCountry();
        return jStockOptions.getCurrencySymbol(country) + toCurrency(decimalPlace, value);
    }
    
    public static boolean isTransactionWithEqualStockCode(Transaction t0, Transaction t1) {
        final Code c0 = t0.getStock().code;
        final Code c1 = t1.getStock().code;
        
        return c0.equals(c1);
    }

    /**
     * Returns portfolio directory, based on given portfolio name. There is
     * chance where the returned directory doesn't exist. To verify against
     * existence, use <code>Utils.isPortfolioDirectory</code>.
     *
     * @param name portfolio name
     * @return portfolio directory, based on given portfolio name
     */
    public static String getPortfolioDirectory(String name) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return getPortfolioDirectory(jStockOptions.getCountry(), name);
    }

    public static String getPortfolioDirectory(Country country, String name) {
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "portfolios" + File.separator + name + File.separator;
    }
    
    /**
     * Creates empty portfolio for current selected country.
     *
     * @return true if empty portfolio creation success
     */
    public static boolean createEmptyPortfolio(String name) {
        final String directory = getPortfolioDirectory(name);

        // Note : Instead of creating multiple files, we will only create single 
        // file for space optimization purpose. This is important as we are 
        // going migrate to Android soon.

        //final File buyPortfolioFile = new File(directory + "buyportfolio.csv");
        //final File sellPortfolioFile = new File(directory + "sellportfolio.csv");
        //final File dividendSummaryFile = new File(directory + "dividendsummary.csv");
        //final File depositSummaryFile = new File(directory + "depositsummary.csv");        
        final File stockPricesFile = new File(directory + "stockprices.csv");
        
        // Do not allow to create empty portfolio, if the desired location already
        // contain portfolio files.
        //if (buyPortfolioFile.exists() || sellPortfolioFile.exists() || dividendSummaryFile.exists() || depositSummaryFile.exists() || stockPricesFile.exists()) {
        if (stockPricesFile.exists()) {
            return false;
        }
        
        if (false == org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory)) {
            return false;
        }
        
        //final Statements statements0 = Statements.newInstanceFromBuyPortfolioTreeTableModel(new BuyPortfolioTreeTableModelEx(), true);
        //final Statements statements1 = Statements.newInstanceFromSellPortfolioTreeTableModel(new SellPortfolioTreeTableModelEx(), true);
        //final Statements statements2 = Statements.newInstanceFromTableModel(new DividendSummaryTableModel(new DividendSummary()), true);
        //final Statements statements3 = Statements.newInstanceFromTableModel(new DepositSummaryTableModel(new DepositSummary()), true);
        final Statements statements4 = Statements.newInstanceFromStockPrices(java.util.Collections.<Code, Double> emptyMap(), 0);
        
        boolean status = true;
        //status = status & statements0.saveAsCSVFile(buyPortfolioFile);
        //status = status & statements1.saveAsCSVFile(sellPortfolioFile);
        //status = status & statements2.saveAsCSVFile(dividendSummaryFile);
        //status = status & statements3.saveAsCSVFile(depositSummaryFile);
        status = status & statements4.saveAsCSVFile(stockPricesFile);
        
        return status;
    }

    /**
     * Returns the current active portfolio directory, based on user selected
     * portfolio. There is chance where the returned directory doesn't exist. To
     * verify against existence, use <code>Utils.isPortfolioDirectory</code>.
     *
     * @return current active portfolio directory
     */
    public static String getPortfolioDirectory() {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return getPortfolioDirectory(jStockOptions.getPortfolioName());
    }

    // TODO : Remove this code after some time.
    /**
     * Due to historical reason, we are storing portfolio information in XML
     * files. Now, they are considered as obsolete. CSV files will become the
     * replacement. Returns true if the given file is a XML portfolio directory.
     *
     * @param file The <code>File</code> to be checked against
     * @return true if the given file is a XML portfolio directory
     */
    private static boolean isXMLPortfolioDirectory(File file) {
        if (false == file.isDirectory()) {
            // Returns false immediately if this is not a directory.
            return false;
        }
        String[] files = file.list();
        List<String> list = Arrays.asList(files);
        return list.contains("buyportfolio.xml") && list.contains("sellportfolio.xml") && list.contains("depositsummary.xml") && list.contains("dividendsummary.xml");
    }

    /**
     * Returns true if the given file is a CSV portfolio directory.
     *
     * @param file The <code>File</code> to be checked against
     * @return true if the given file is a CSV portfolio directory
     */
    private static boolean isCSVPortfolioDirectory(File file) {
        if (false == file.isDirectory()) {
            // Returns false immediately if this is not a directory.
            return false;
        }
        String[] files = file.list();
        List<String> list = Arrays.asList(files);
        //return list.contains("stockprices.csv") && list.contains("buyportfolio.csv") && list.contains("sellportfolio.csv") && list.contains("depositsummary.csv") && list.contains("dividendsummary.csv");
        return list.contains("stockprices.csv");
    }

    /**
     * Returns true if the given file is a CSV portfolio directory.
     *
     * @param file The <code>File</code> to be checked against
     * @return true if the given file is a CSV portfolio directory
     */
    private static boolean isPortfolioDirectory(File file) {
        return isCSVPortfolioDirectory(file);
    }

    public static String toUnits(Object value) {
        return unitsNumberFormat.get().format(value); 
    }
    
    public static String toUnits(double value) {
        return unitsNumberFormat.get().format(value); 
    }
    
    /**
     * Returns all available portfolio directories for current selected country.
     *
     * @return all available portfolio directories for current selected country
     */
    public static List<String> getPortfolioDirectories() {
        final List<String> names = Utils.getPortfolioNames();
        List<String> directories = new ArrayList<String>();
        for (String name : names) {
            directories.add(Utils.getPortfolioDirectory(name));
        }
        return directories;
    }    
    
    public static List<PortfolioInfo> getPortfolioInfos() {
        List<PortfolioInfo> portfolioInfos = new ArrayList<PortfolioInfo>();
        for (Country country : Country.values()) {
            final File file = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "portfolios" + File.separator);
            File[] children = file.listFiles();
            if (children == null) {
                // Either dir does not exist or is not a directory
                continue;
            } else {
                // Only seek for 1st level directory.
                for (File child : children) {
                    
                    File stockPricesFile = new File(child, "stockprices.csv");
                    int lines = org.yccheok.jstock.gui.Utils.numOfLines(stockPricesFile, true);
                    // Skip CSV header.
                    lines = lines - 1;
                    if (lines > 0) {
                        PortfolioInfo portfolioInfo = PortfolioInfo.newInstance(country, child.getName(), lines);
                        portfolioInfos.add(portfolioInfo);
                        continue;
                    } 

                    // We do not have buy record. Do we have sell record, dividend record or deposit record?
                    File sellPortfolioFile = new File(child, "sellportfolio.csv");
                    lines = org.yccheok.jstock.gui.Utils.numOfLines(sellPortfolioFile, true);
                    // Skip CSV header.
                    lines = lines - 1;
                    if (lines > 0) {
                        PortfolioInfo portfolioInfo = PortfolioInfo.newInstance(country, child.getName(), lines);
                        portfolioInfos.add(portfolioInfo);
                        continue;
                    }
                    
                    File dividendFile = new File(child, "dividendsummary.csv");
                    lines = org.yccheok.jstock.gui.Utils.numOfLines(dividendFile, true);
                    // Skip CSV header.
                    lines = lines - 1;
                    if (lines > 0) {
                        PortfolioInfo portfolioInfo = PortfolioInfo.newInstance(country, child.getName(), lines);
                        portfolioInfos.add(portfolioInfo);
                        continue;
                    }
                    
                    File depositFile = new File(child, "depositsummary.csv");
                    lines = org.yccheok.jstock.gui.Utils.numOfLines(depositFile, true);
                    // Skip CSV header.
                    lines = lines - 1;
                    if (lines > 0) {
                        PortfolioInfo portfolioInfo = PortfolioInfo.newInstance(country, child.getName(), lines);
                        portfolioInfos.add(portfolioInfo);
                        continue;
                    }                    
                }                
            }
        }
        return portfolioInfos;
    }
    
    public static List<String> getPortfolioNames() {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return getPortfolioNames(jStockOptions.getCountry());
    }
    
    /**
     * Returns all available portfolio names for current selected country.
     *
     * @return all available portfolio names for current selected country
     */
    public static List<String> getPortfolioNames(Country country) {
        List<String> portfolioNames = new ArrayList<String>();
        final File file = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "portfolios" + File.separator);
        File[] children = file.listFiles();
        if (children == null) {
            // Either dir does not exist or is not a directory
            return portfolioNames;
        } else {
            // Only seek for 1st level directory.
            for (File child : children) {
                if (isPortfolioDirectory(child)) {
                    portfolioNames.add(child.getName());
                }
            }
        }
        return portfolioNames;
    }
    
    // TODO : Remove this code after some time.
    private static List<String> getXMLPortfolioNames(String baseDirectory, Country country, boolean oldData) {
        List<String> portfolioNames = new ArrayList<String>();
        final File file = oldData ?
            new File(baseDirectory + country + File.separator + "config" + File.separator) :
            new File(baseDirectory + country + File.separator + "portfolios" + File.separator);
        
        File[] children = file.listFiles();
        if (children == null) {
            // Either dir does not exist or is not a directory
            return portfolioNames;
        } else {
            // Only seek for 1st level directory.
            for (File child : children) {
                if (isXMLPortfolioDirectory(child)) {
                    portfolioNames.add(child.getName());
                }
            }
        }
        return portfolioNames;
    }
    
    /**
     * Returns default portfolio name for all country.
     *
     * @return default portfolio name for all country
     */
    public static String getDefaultPortfolioName() {
        return "My Portfolio";
    }

    /**
     * Removes meaningless records from dividendSummary.
     *
     * @param dividendSummary The dividend summary
     */
    public static void removeMeaninglessRecords(DividendSummary dividendSummary) {
        for (int i = 0; i < dividendSummary.size(); i++) {
            Dividend dividend = dividendSummary.get(i);
            if (dividend.amount <= 0.0 || dividend.stockInfo.code.toString().length() <= 0) {
                // Remove meaningless record.
                dividendSummary.remove(dividend);
                i--;
            }
        }
    }

    /**
     * Removes meaningless records from depositSummary.
     *
     * @param depositSummary The deposit summary
     */
    public static void removeMeaninglessRecords(DepositSummary depositSummary) {
        for (int i = 0; i < depositSummary.size(); i++) {
            Deposit deposit = depositSummary.get(i);
            if (essentiallyEqual(deposit.getAmount(), 0.0)) {
                // Remove meaningless record.
                depositSummary.remove(deposit);
                i--;
            }
        }
    }

    /**
     * Returns total deposit from cash summary.
     *
     * @param cashSummary the cash summary
     * @return total deposit from cash summary
     */
    public static double getTotalDeposit(DepositSummary cashSummary) {
        final int size = cashSummary.size();
        double totalDeposit = 0.0;
        for (int i = 0; i < size; i++) {
            final Deposit deposit = cashSummary.get(i);
            final double amount = deposit.getAmount();
            if (definitelyGreaterThan(amount, 0.0)) {
                totalDeposit += amount;
            }
        }
        return totalDeposit;
    }

    /**
     * Returns total withdraw from cash summary.
     *
     * @param cashSummary the cash summary
     * @return total withdraw from cash summary
     */
    public static double getTotalWithdraw(DepositSummary cashSummary) {
        final int size = cashSummary.size();
        double totalWidthdraw = 0.0;
        for (int i = 0; i < size; i++) {
            final Deposit withdraw = cashSummary.get(i);
            final double amount = withdraw.getAmount();
            if (definitelyLessThan(amount, 0.0)) {
                totalWidthdraw += amount;
            }
        }
        return totalWidthdraw;
    }

    /**
     * Returns true if the two double values is essentially equal.
     *
     * @param a first double value
     * @param b second double value
     * @return true if the two double value is essentially equal
     */
    public static boolean essentiallyEqual(double a, double b)
    {
        return Math.abs(a - b) <= ( (Math.abs(a) > Math.abs(b) ? Math.abs(b) : Math.abs(a)) * EPSILON);
    }

    /**
     * Returns true if the a is definitely greater than b.
     *
     * @param a first double value
     * @param b second double value
     * @return true if the a is definitely greater than b
     */
    public static boolean definitelyGreaterThan(double a, double b)
    {
        return (a - b) > ( (Math.abs(a) < Math.abs(b) ? Math.abs(b) : Math.abs(a)) * EPSILON);
    }

    // TODO : Remove this code after some time.
    // The directory format should be "C:\Users\yccheok\.jstock\1.0.6\", or
    // temporary directory holding the extracted cloud files.
    public static boolean migrateXMLToCSVPortfolios(String srcBaseDirectory, String destBaseDirectory) {
        assert(srcBaseDirectory.endsWith(File.separator));
        assert(destBaseDirectory.endsWith(File.separator));
        
        boolean status = true;
        
        for (Country country : Country.values()) {
            List<String> names = getXMLPortfolioNames(srcBaseDirectory, country, false);
            final boolean oldData = names.size() <= 0;
            
            boolean localStatus = true;
            
            if (oldData) {
                final String oldDirectory = srcBaseDirectory + country + File.separator + "config" + File.separator;
                final String srcDirectory = oldDirectory;
                final String destDirectory = destBaseDirectory + country + File.separator + "portfolios" + File.separator + getDefaultPortfolioName() + File.separator;

                XMLPortfolio xmlPortfolio = getXMLPortfolio(srcDirectory);
                    
                PortfolioManagementJPanel.CSVPortfolio csvPortfolio = PortfolioManagementJPanel.CSVPortfolio.newInstance(
                    xmlPortfolio.buyPortfolioTreeTableModel.toBuyPortfolioTreeTableModelEx(), 
                    xmlPortfolio.sellPortfolioTreeTableModel.toSellPortfolioTreeTableModelEx(), 
                    xmlPortfolio.dividendSummary, 
                    xmlPortfolio.depositSummary);

                localStatus = PortfolioManagementJPanel.saveCSVPortfolio(destDirectory, csvPortfolio, 0);
                
                if (localStatus) {
                    deleteXMLPortfolio(srcDirectory);
                }  
            } else {
                for (String name : names) {
                    final String directory = srcBaseDirectory + country + File.separator + "portfolios" + File.separator + name + File.separator;
                    final String srcDirectory = directory;
                    final String destDirectory = destBaseDirectory + country + File.separator + "portfolios" + File.separator + name + File.separator;

                    XMLPortfolio xmlPortfolio = getXMLPortfolio(srcDirectory);

                    PortfolioManagementJPanel.CSVPortfolio csvPortfolio = PortfolioManagementJPanel.CSVPortfolio.newInstance(
                        xmlPortfolio.buyPortfolioTreeTableModel.toBuyPortfolioTreeTableModelEx(), 
                        xmlPortfolio.sellPortfolioTreeTableModel.toSellPortfolioTreeTableModelEx(), 
                        xmlPortfolio.dividendSummary, 
                        xmlPortfolio.depositSummary);

                    boolean _localStatus = PortfolioManagementJPanel.saveCSVPortfolio(destDirectory, csvPortfolio, 0);

                    if (_localStatus) {
                        deleteXMLPortfolio(srcDirectory);
                    }
                    
                    localStatus = localStatus & _localStatus;
                }   // for (String name : names)
            }   // if (oldData)
            
            if (localStatus) {
                // Delete legacy old folder.
                final String oldDirectory = srcBaseDirectory + country + File.separator + "config" + File.separator;
                deleteXMLPortfolio(oldDirectory);
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
    @SuppressWarnings( "deprecation" )
    private static XMLPortfolio getXMLPortfolio(String directory) {
        final File buyPortfolioFile;
        final File sellPortfolioFile;
        final File depositSummaryFile;
        final File dividendSummaryFile;

        // Determine the files to be loaded from disc.
        buyPortfolioFile = new File(directory + "buyportfolio.xml");
        sellPortfolioFile = new File(directory + "sellportfolio.xml");
        depositSummaryFile = new File(directory + "depositsummary.xml");
        dividendSummaryFile = new File(directory + "dividendsummary.xml");

        // Try to load files from disc.
        BuyPortfolioTreeTableModel buyPortfolioTreeTableModel = org.yccheok.jstock.gui.Utils.fromXML(BuyPortfolioTreeTableModel.class, buyPortfolioFile);
        SellPortfolioTreeTableModel sellPortfolioTreeTableModel = org.yccheok.jstock.gui.Utils.fromXML(SellPortfolioTreeTableModel.class, sellPortfolioFile);
        DepositSummary _depositSummary = org.yccheok.jstock.gui.Utils.fromXML(DepositSummary.class, depositSummaryFile);
        DividendSummary _dividendSummary = org.yccheok.jstock.gui.Utils.fromXML(DividendSummary.class, dividendSummaryFile);
        
        return XMLPortfolio.newInstance(
                buyPortfolioTreeTableModel != null ? buyPortfolioTreeTableModel : new BuyPortfolioTreeTableModel(), 
                sellPortfolioTreeTableModel != null ? sellPortfolioTreeTableModel : new SellPortfolioTreeTableModel(), 
                _dividendSummary != null ? _dividendSummary : new DividendSummary(), 
                _depositSummary != null ? _depositSummary : new DepositSummary());
    }
    
    // Never ever delete directory itself.
    private static boolean deleteXMLPortfolio(String directory) {
        final File buyPortfolioXMLFile = new File(directory + "buyportfolio.xml");
        final File sellPortfolioXMLFile = new File(directory + "sellportfolio.xml");
        final File depositSummaryXMLFile = new File(directory + "depositsummary.xml");
        final File dividendSummaryXMLFile = new File(directory + "dividendsummary.xml");

        buyPortfolioXMLFile.delete();
        sellPortfolioXMLFile.delete();
        depositSummaryXMLFile.delete();
        dividendSummaryXMLFile.delete();
        
        return true;
    }
    
    /**
     * Returns true if the a is definitely lesser than b.
     *
     * @param a first double value
     * @param b second double value
     * @return true if the a is definitely lesser than b
     */
    public static boolean definitelyLessThan(double a, double b)
    {
        return (b - a) > ( (Math.abs(a) < Math.abs(b) ? Math.abs(b) : Math.abs(a)) * EPSILON);
    }

    // 0.00000001 is a magic number. I have 0 idea what I should have for this
    // value.
    private static final double EPSILON = 0.00000001;
    
    private static final Log log = LogFactory.getLog(Utils.class);    
}
