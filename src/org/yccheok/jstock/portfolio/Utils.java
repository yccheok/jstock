/*
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
 * Copyright (C) 2008 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.portfolio;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.gui.BuyPortfolioTreeTableModel;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.SellPortfolioTreeTableModel;

/**
 *
 * @author Owner
 */
public class Utils {
    
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

    public static String getPortfolioDirectory(String name) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + jStockOptions.getCountry() + File.separator + "portfolios" + File.separator + name + File.separator;
    }

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

	// Get current active portfolio directory.
    public static String getPortfoliosDirectory() {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        return getPortfolioDirectory(jStockOptions.getPortfolioName());
    }

    private static boolean isPortfolioDirectory(File file) {
        if (false == file.isDirectory()) {
            return false;
        }
        String[] files = file.list();
        List<String> list = Arrays.asList(files);
        return list.contains("buyportfolio.xml") && list.contains("sellportfolio.xml") && list.contains("depositsummary.xml") && list.contains("dividendsummary.xml");
    }

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
}
