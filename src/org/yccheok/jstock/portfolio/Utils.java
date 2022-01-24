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

package org.yccheok.jstock.portfolio;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.engine.currency.CurrencyPair;
import org.yccheok.jstock.file.GUIBundleWrapper;
import org.yccheok.jstock.file.Statement;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;

/**
 *
 * @author yccheok
 */
public class Utils {

    // Prevent from being instantiated.
    private Utils() {
    }

    @Deprecated
    public static Pair<HashMap<Code, Double>, Long> getCSVStockPrices() {
        File stockPricesFile = new File(org.yccheok.jstock.portfolio.Utils.getStockPricesFilepath());
        
        final HashMap<Code, Double> stockPrices = new HashMap<>();
        
        Statements statements = Statements.newInstanceFromCSVFile(stockPricesFile);
        
        if (statements.getType() == Statement.Type.StockPrice) {
            final GUIBundleWrapper guiBundleWrapper = statements.getGUIBundleWrapper();
            
            for (int i = 0, ei = statements.size(); i < ei; i++) {
                Statement statement = statements.get(i);
                String codeStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Code"));
                Double price = statement.getValueAsDouble(guiBundleWrapper.getString("MainFrame_Last"));
                if (codeStr == null || price == null) {
                    continue;
                }
                
                Code code = Code.newInstance(codeStr);
                stockPrices.put(code, price);
            }
        }

        long _timestamp = 0;
        try {
            _timestamp = Long.parseLong(statements.getMetadatas().get("timestamp"));
        } catch (NumberFormatException ex) {
            log.error(null, ex);
        }
        
        return Pair.create(stockPrices, _timestamp);
    }

    public static boolean shouldConvertPenceToPound(Country country) {
        Currency stockCurrency = country.stockCurrency;
        return shouldConvertPenceToPound(stockCurrency);
    }

    public static boolean shouldConvertPenceToPound(PortfolioRealTimeInfo portfolioRealTimeInfo, Code code) {
        final Currency stockCurrency = getStockCurrency(portfolioRealTimeInfo, code);
        return shouldConvertPenceToPound(stockCurrency);
    }

    // Perhaps we should have a better naming.
    public static boolean shouldConvertPenceToPound(Currency currency) {
        if (currency == null) {
            return false;
        }

        return currency.isGBX() || currency.isZAC() || currency.isILA();
    }
    
    public static Currency getLocalCurrency() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        
        final Country country = jStockOptions.getCountry();
        final boolean currencyExchangeEnable = jStockOptions.isCurrencyExchangeEnable(country);
        final Currency localCurrency;
        if (currencyExchangeEnable) {
            final Country localCountry = jStockOptions.getLocalCurrencyCountry(country);
            localCurrency = localCountry.localCurrency;
        } else {
            localCurrency = null;
        }
        
        return localCurrency;
    }
        
    public static Currency getStockCurrency(PortfolioRealTimeInfo portfolioRealTimeInfo, Code code) {
        //////////////////////////////////////////
        // Get traded currency in this stock code.
        //////////////////////////////////////////
        final Currency stockCurrency;
        org.yccheok.jstock.engine.currency.Currency c = portfolioRealTimeInfo.currencies.get(code);
        if (c == null) {
            Country stockCountry = org.yccheok.jstock.engine.Utils.toCountry(code);
            stockCurrency = stockCountry.stockCurrency;
        } else {
            stockCurrency = c;
        }
        return stockCurrency;
    }
    
    public static double getExchangeRate(PortfolioRealTimeInfo portfolioRealTimeInfo, Currency localCurrency, Currency stockCurrency) {
        // Possible null.
        if (localCurrency == null) {
            if (stockCurrency.isGBX() || stockCurrency.isZAC()) {
                return 0.01;
            }
            return 1.0;
        }
        
        final double exchangeRate;
        if (stockCurrency.equals(localCurrency)) {
            exchangeRate = 1.0;
        } else {
            Double rate = portfolioRealTimeInfo.exchangeRates.get(CurrencyPair.create(stockCurrency, localCurrency));
            if (rate != null) {
                exchangeRate = rate;
            } else {
                if (stockCurrency.isGBX() || stockCurrency.isZAC()) {
                    exchangeRate = 0.01;    
                } else {
                    exchangeRate = 1.0;
                }
            }
        }

        return exchangeRate;
    }
    
    public static double getExchangeRate(PortfolioRealTimeInfo portfolioRealTimeInfo, Currency localCurrency, Code code) {
        final Currency stockCurrency = getStockCurrency(portfolioRealTimeInfo, code);
        
        return getExchangeRate(portfolioRealTimeInfo, localCurrency, stockCurrency);
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
    
    private static final ThreadLocal <NumberFormat> editThreeCurrencyNumberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Number of decimal places need to tally with threeDecimalPlaceCurrencyNumberFormat.
            // Do not use Grouping separator
            DecimalFormat decimalFormat = new DecimalFormat("0.###");
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
            return decimalFormat;
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
    public static String toCurrency(DecimalPlace decimalPlace, Object value) {
        if (decimalPlace == DecimalPlace.Two) {
            return twoDecimalPlaceCurrencyNumberFormat.get().format(value);
        } else if (decimalPlace == DecimalPlace.Three) {
            return threeDecimalPlaceCurrencyNumberFormat.get().format(value);
        }
        assert(decimalPlace == DecimalPlace.Four);
        return fourDecimalPlaceCurrencyNumberFormat.get().format(value);
    }

    /**
     * Convert the value to currency representation (without symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (without symbol)
     */
    public static String toCurrency(DecimalPlace decimalPlace, double value) {
        if (decimalPlace == DecimalPlace.Two) {
            return twoDecimalPlaceCurrencyNumberFormat.get().format(value);
        } else if (decimalPlace == DecimalPlace.Three) {
            return threeDecimalPlaceCurrencyNumberFormat.get().format(value);
        }
        assert(decimalPlace == DecimalPlace.Four);
        return fourDecimalPlaceCurrencyNumberFormat.get().format(value);
    }
    
    /**
     * Convert the value to currency representation (with symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (with symbol)
     */
    public static String toCurrencyWithSymbol(DecimalPlace decimalPlace, Object value) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final Country country = jStockOptions.getCountry();
        return jStockOptions.getCurrencySymbol(country) + toCurrency(decimalPlace, value);
    }

    /**
     * Convert the value to currency representation (with symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (with symbol)
     */
    public static String toCurrencyWithSymbol(DecimalPlace decimalPlace, double value) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final Country country = jStockOptions.getCountry();
        return jStockOptions.getCurrencySymbol(country) + toCurrency(decimalPlace, value);
    }
    
    public static boolean isTransactionWithEqualStockCode(Transaction t0, Transaction t1) {
        final Code c0 = t0.getStockInfo().code;
        final Code c1 = t1.getStockInfo().code;
        
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
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getPortfolioDirectory(jStockOptions.getCountry(), name);
    }

    public static String getPortfolioDirectory(Country country, String name) {
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "portfolios" + File.separator + name + File.separator;
    }
    
    private static String getPortfolioRealTimeInfoFilepath(Country country, String name) {
        final String portfolioDirectory = org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory(country, name);
        return portfolioDirectory + "portfolio-real-time-info.json";        
    }

    private static String getPortfolioRealTimeInfoFilepath(String name) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getPortfolioRealTimeInfoFilepath(jStockOptions.getCountry(), name);       
    }

    public static String getPortfolioRealTimeInfoFilepath() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getPortfolioRealTimeInfoFilepath(jStockOptions.getPortfolioName());       
    }
    
    @Deprecated
    private static String getStockPricesFilepath(Country country, String name) {
        final String portfolioDirectory = org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory(country, name);
        return getStockPricesFilepathViaDirectory(portfolioDirectory);
    }

    @Deprecated
    public static String getStockPricesFilepathViaDirectory(String portfolioDirectory) {
        return portfolioDirectory + "stockprices.csv";
    }
    
    @Deprecated
    private static String getStockPricesFilepath(String name) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getStockPricesFilepath(jStockOptions.getCountry(), name);       
    }

    @Deprecated
    public static String getStockPricesFilepath() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getStockPricesFilepath(jStockOptions.getPortfolioName());       
    }
    
    /**
     * Creates empty portfolio for current selected country.
     *
     * @return true if empty portfolio creation success
     */
    public static boolean createEmptyPortfolio(String name) {
        final String directory = getPortfolioDirectory(name);

        final File portfolioRealTimeInfoFile = new File(getPortfolioRealTimeInfoFilepath(name));
        final File stockPricesFile = new File(getStockPricesFilepath(name));
        
        if (portfolioRealTimeInfoFile.exists() || stockPricesFile.exists()) {
            return false;
        }
        
        if (false == org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory)) {
            return false;
        }
                
        PortfolioRealTimeInfo portfolioRealTimeInfo = new PortfolioRealTimeInfo();
        return portfolioRealTimeInfo.save(portfolioRealTimeInfoFile);
    }

    /**
     * Returns the current active portfolio directory, based on user selected
     * portfolio. There is chance where the returned directory doesn't exist. To
     * verify against existence, use <code>Utils.isPortfolioDirectory</code>.
     *
     * @return current active portfolio directory
     */
    public static String getPortfolioDirectory() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        return getPortfolioDirectory(jStockOptions.getPortfolioName());
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
        return list.contains("stockprices.csv") || list.contains("portfolio-real-time-info.json");
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
                    File portfolioRealTimeInfoFile = new File(child, "portfolio-real-time-info.json");
                    PortfolioRealTimeInfo portfolioRealTimeInfo = new PortfolioRealTimeInfo();
                    if (portfolioRealTimeInfo.load(portfolioRealTimeInfoFile)) {
                        final int lines = portfolioRealTimeInfo.stockPrices.size();
                        if (lines > 0) {
                            PortfolioInfo portfolioInfo = PortfolioInfo.newInstance(country, child.getName(), lines);
                            portfolioInfos.add(portfolioInfo);
                            continue;
                        }                        
                    } else {
                        // Legacy
                        File stockPricesFile = new File(child, "stockprices.csv");
                        int lines = org.yccheok.jstock.gui.Utils.numOfLines(stockPricesFile, true);
                        // Skip CSV header.
                        lines = lines - 1;
                        if (lines > 0) {
                            PortfolioInfo portfolioInfo = PortfolioInfo.newInstance(country, child.getName(), lines);
                            portfolioInfos.add(portfolioInfo);
                            continue;
                        }    
                    }                            

                    // We do not have buy record. Do we have sell record, dividend record or deposit record?
                    File sellPortfolioFile = new File(child, "sellportfolio.csv");
                    int lines = org.yccheok.jstock.gui.Utils.numOfLines(sellPortfolioFile, true);
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
    
    // This utility method is for XIRR calculation purpose.
    // The code is very similar to InvestmentFlowChartJDialog's initSummaries.
    // However, it doesn't have roiSummary and investSummary concept.
    public static ActivitySummary toActivitySummary(List<TransactionSummary> transactionSummaries, DividendSummary dividendSummary) {
        final ActivitySummary activitySummary = new ActivitySummary();
        
        final boolean isFeeCalculationEnabled = JStock.instance().getJStockOptions().isFeeCalculationEnabled();
        
        for (TransactionSummary transactionSummary : transactionSummaries) {
            final int count = transactionSummary.getChildCount();
            for (int i = 0; i < count; i++) {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);

                Contract.Type type = transaction.getType();
                final StockInfo stockInfo = transaction.getStockInfo();

                if (type == Contract.Type.Buy) {
                    final Activity activity = new Activity.Builder(Activity.Type.Buy, 
                            isFeeCalculationEnabled ? transaction.getNetTotal() : transaction.getTotal()).
                            put(Activity.Param.StockInfo, stockInfo).
                            put(Activity.Param.Quantity, transaction.getQuantity()).
                            build();
                    
                    activitySummary.add(transaction.getDate(), activity);
                } else if (type == Contract.Type.Sell) {
                    final Activity activity0 = new Activity.Builder(Activity.Type.Buy, 
                            isFeeCalculationEnabled ? transaction.getNetReferenceTotal() : transaction.getReferenceTotal()).
                            put(Activity.Param.StockInfo, stockInfo).
                            put(Activity.Param.Quantity, transaction.getQuantity()).
                            build();
                    final Activity activity1 = new Activity.Builder(Activity.Type.Sell, 
                            isFeeCalculationEnabled ? transaction.getNetTotal() : transaction.getTotal()).
                            put(Activity.Param.StockInfo, stockInfo).
                            put(Activity.Param.Quantity, transaction.getQuantity()).
                            build();
                    
                    activitySummary.add(transaction.getDate(), activity0);
                    activitySummary.add(transaction.getDate(), activity1);
                } else {
                    throw new java.lang.UnsupportedOperationException("Unsupported contract type " + type);
                }
            }
        }
        
        for (int i = 0, count = dividendSummary.size(); i < count; i++) {
            final Dividend dividend = dividendSummary.get(i);

            final Activity activity = new Activity.Builder(Activity.Type.Dividend, dividend.amount).
                    put(Activity.Param.StockInfo, dividend.stockInfo).build();
            
            activitySummary.add(dividend.date, activity);
        }
        
        activitySummary.ensureSorted();
        return activitySummary;
    }
            
    public static List<String> getPortfolioNames() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
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
        
        Collections.sort(portfolioNames);
        
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

    public static String toEditCurrency(DecimalPlace decimalPlace, double value) {
        if (decimalPlace == DecimalPlace.Two) {
            throw new java.lang.UnsupportedOperationException();
        } else if (decimalPlace == DecimalPlace.Three) {
            return editThreeCurrencyNumberFormat.get().format(value);
        }
        throw new java.lang.UnsupportedOperationException();
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
            if (definitelyLesserThan(amount, 0.0)) {
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
    
    /**
     * Returns true if the a is definitely lesser than b.
     *
     * @param a first double value
     * @param b second double value
     * @return true if the a is definitely lesser than b
     */
    public static boolean definitelyLesserThan(double a, double b)
    {
        return (b - a) > ( (Math.abs(a) < Math.abs(b) ? Math.abs(b) : Math.abs(a)) * EPSILON);
    }
    
    // 0.00000001 is a magic number. I have 0 idea what I should have for this
    // value.
    private static final double EPSILON = 0.00000001;
    
    private static final Log log = LogFactory.getLog(Utils.class);    
}
