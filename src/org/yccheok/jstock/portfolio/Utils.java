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

package org.yccheok.jstock.portfolio;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.gui.BuyPortfolioTreeTableModel;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.SellPortfolioTreeTableModel;

/**
 *
 * @author yccheok
 */
public class Utils {

    // Prevent from being instantiated.
    private Utils() {
    }

    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> numberFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            return new DecimalFormat("#,##0.00");
        }
    };

    /**
     * Convert the value to currency representation (without symbol).
     *
     * @param value the value to be converted
     * @return currency representation (without symbol)
     */
    public static String toCurrency(Object value) {
        return numberFormat.get().format(value);
    }

    /**
     * Convert the value to currency representation (without symbol).
     * 
     * @param value the value to be converted
     * @return currency representation (without symbol)
     */
    public static String toCurrency(double value) {
        return numberFormat.get().format(value);
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

        // Do not allow to create empty portfolio, if the desired location already
        // contain portfolio files.
        if (new File(directory + "buyportfolio.xml").exists() || new File(directory + "depositsummary.xml").exists() ||
            new File(directory + "sellportfolio.xml").exists() || new File(directory + "dividendsummary.xml").exists()) {
            return false;
        }
        
        final BuyPortfolioTreeTableModel buyPortfolioTreeTableModel = new BuyPortfolioTreeTableModel();
        final SellPortfolioTreeTableModel sellPortfolioTreeTableModel = new SellPortfolioTreeTableModel();
        final DepositSummary depositSummary = new DepositSummary();
        final DividendSummary dividendSummary = new DividendSummary();

        return
        org.yccheok.jstock.gui.Utils.toXML(buyPortfolioTreeTableModel, directory + "buyportfolio.xml") &&
        org.yccheok.jstock.gui.Utils.toXML(sellPortfolioTreeTableModel, directory + "sellportfolio.xml") &&
        org.yccheok.jstock.gui.Utils.toXML(depositSummary, directory + "depositsummary.xml") &&
        org.yccheok.jstock.gui.Utils.toXML(dividendSummary, directory + "dividendsummary.xml");
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
        return getPortfolioDirectory(jStockOptions.getPortfolioName(jStockOptions.getCountry()));
    }

    /**
     * Returns true if the given file is a portfolio directory.
     *
     * @param file The <code>File</code> to be checked against
     * @return true if the given file is a portfolio directory
     */
    private static boolean isPortfolioDirectory(File file) {
        if (false == file.isDirectory()) {
            return false;
        }
        String[] files = file.list();
        List<String> list = Arrays.asList(files);
        return list.contains("buyportfolio.xml") && list.contains("sellportfolio.xml") && list.contains("depositsummary.xml") && list.contains("dividendsummary.xml");
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
}
