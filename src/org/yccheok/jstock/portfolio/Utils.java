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

    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> currencyNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Instead of limiting currency decimal places to 2 only, we allow
            // them to float between 2 to 3, to avoid from losing precision.
            return new DecimalFormat("#,##0.00#");
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
    
    // Use ThreadLocal to ensure thread safety
    private static final ThreadLocal <NumberFormat> wealthHeaderNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            final java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();
            return numberFormat;
        }
    };
    
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
    public static String toCurrency(Object value) {
        return currencyNumberFormat.get().format(value);
    }

    /**
     * Convert the value to currency representation (without symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (without symbol)
     */
    public static String toCurrency(double value) {
        return currencyNumberFormat.get().format(value);
    }

    /**
     * Convert the value to currency representation (with symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (with symbol)
     */
    public static String toCurrencyWithSymbol(Object value) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        final Country country = jStockOptions.getCountry();
        return jStockOptions.getCurrencySymbol(country) + toCurrency(value);
    }

    /**
     * Convert the value to currency representation (with symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (with symbol)
     */
    public static String toCurrencyWithSymbol(double value) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        final Country country = jStockOptions.getCountry();
        return jStockOptions.getCurrencySymbol(country) + toCurrency(value);
    }

    /**
     * Convert the value to wealth header representation.
     *
     * @param value the value to be converted
     * @return wealth header representation
     */
    public static String toWealthHeader(Object value) {
        NumberFormat numberFormat = wealthHeaderNumberFormat.get();
        if (false == MainFrame.getInstance().getJStockOptions().isPenceToPoundConversionEnabled()) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
        }
        else {
            numberFormat.setMaximumFractionDigits(4);
            numberFormat.setMinimumFractionDigits(4);
        }        
        return numberFormat.format(value);
    }

    /**
     * Convert the value to wealth header representation.
     *
     * @param value the value to be converted
     * @return wealth header representation
     */
    public static String toWealthHeader(double value) {
        NumberFormat numberFormat = wealthHeaderNumberFormat.get();
        if (false == MainFrame.getInstance().getJStockOptions().isPenceToPoundConversionEnabled()) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
        }
        else {
            numberFormat.setMaximumFractionDigits(4);
            numberFormat.setMinimumFractionDigits(4);
        }        
        return numberFormat.format(value);
    }
    
    public static boolean isTransactionWithEqualStockCode(Transaction t0, Transaction t1) {
        final Code c0 = t0.getContract().getStock().getCode();
        final Code c1 = t1.getContract().getStock().getCode();
        
        return c0.equals(c1);
    }

    public static ClearingFee getDummyClearingFee(double clearingFee) {
        return new SimpleClearingFee("SimpleClearingfee", Double.MAX_VALUE, clearingFee, 0);
    }

    public static Broker getDummyBroker(double broker) {
        return new SimpleBroker("SimpleBroker", Double.MAX_VALUE, broker, 0);
    }

    public static StampDuty getDummyStampDuty(Contract contract, double stampDuty) {
        return new SimpleStampDuty("SimpleStampDuty", Double.MAX_VALUE, contract.getTotal(), stampDuty);
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
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + jStockOptions.getCountry() + File.separator + "portfolios" + File.separator + name + File.separator;
    }

    /**
     * Creates empty portfolio for current selected country.
     *
     * @return true if empty portfolio creation success
     */
    public static boolean createEmptyPortfolio(String name) {
        final String directory = getPortfolioDirectory(name);
        if (false == org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory)) {
            return false;
        }
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
        
        //final Statements statements0 = Statements.newInstanceFromBuyPortfolioTreeTableModel(new BuyPortfolioTreeTableModelEx(), true);
        //final Statements statements1 = Statements.newInstanceFromSellPortfolioTreeTableModel(new SellPortfolioTreeTableModelEx(), true);
        //final Statements statements2 = Statements.newInstanceFromTableModel(new DividendSummaryTableModel(new DividendSummary()), true);
        //final Statements statements3 = Statements.newInstanceFromTableModel(new DepositSummaryTableModel(new DepositSummary()), true);
        final Statements statements4 = Statements.newInstanceFromStockPrices(java.util.Collections.<Code, Double> emptyMap());
        
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
    
    /**
     * Returns all available portfolio names for current selected country.
     *
     * @return all available portfolio names for current selected country
     */
    public static List<String> getPortfolioNames() {
        List<String> portfolioNames = new ArrayList<String>();
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        final File file = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + jStockOptions.getCountry() + File.separator + "portfolios" + File.separator);
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
            if (dividend.getAmount() <= 0.0 || dividend.getStock().getCode().toString().length() <= 0) {
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

                localStatus = PortfolioManagementJPanel.saveCSVPortfolio(destDirectory, csvPortfolio);
                
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

                    boolean _localStatus = PortfolioManagementJPanel.saveCSVPortfolio(destDirectory, csvPortfolio);

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
