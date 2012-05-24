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

package org.yccheok.jstock.gui.treetable;

import java.util.Arrays;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.yccheok.jstock.file.GUIBundleWrapper;
import org.yccheok.jstock.file.GUIBundleWrapper.Language;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;
import org.yccheok.jstock.internationalization.GUIBundle;
/**
 *
 * @author yccheok
 */
public class SellPortfolioTreeTableModelEx extends AbstractPortfolioTreeTableModelEx {
    
    public SellPortfolioTreeTableModelEx() {
        super(Arrays.asList(columnNames));
    }
    
    // Names of the columns.
    private static final String[] columnNames;
    // Unlike columnNames, LanguageIndependentColumnNames is language independent.
    private static final String[] languageIndependentColumnNames;
    
    static {
        final String[] tmp = {
            GUIBundle.getString("PortfolioManagementJPanel_Stock"),
            GUIBundle.getString("PortfolioManagementJPanel_Date"),
            GUIBundle.getString("PortfolioManagementJPanel_Units"),
            GUIBundle.getString("PortfolioManagementJPanel_SellingPrice"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchasePrice"),
            GUIBundle.getString("PortfolioManagementJPanel_SellingValue"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchaseValue"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossPrice"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossValue"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossPercentage"),
            GUIBundle.getString("PortfolioManagementJPanel_Broker"),
            GUIBundle.getString("PortfolioManagementJPanel_ClearingFee"),
            GUIBundle.getString("PortfolioManagementJPanel_StampDuty"),
            GUIBundle.getString("PortfolioManagementJPanel_NetSellingValue"),
            GUIBundle.getString("PortfolioManagementJPanel_NetGainLossValue"),
            GUIBundle.getString("PortfolioManagementJPanel_NetGainLossPercentage"),
            GUIBundle.getString("PortfolioManagementJPanel_Comment")
        };
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(Language.INDEPENDENT);
        final String[] tmp2 = {
            guiBundleWrapper.getString("PortfolioManagementJPanel_Stock"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Date"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Units"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_SellingPrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_SellingValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Broker"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetSellingValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Comment")
        };       
        columnNames = tmp;
        languageIndependentColumnNames = tmp2;
    }

    // Types of the columns.
    private static final Class[] cTypes = {
        TreeTableModel.class,
        org.yccheok.jstock.engine.SimpleDate.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        Double.class,
        String.class
    };
    
    @Override
    public int getColumnCount() {
        assert(columnNames.length == cTypes.length);
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int column) {
        assert(columnNames.length == cTypes.length);        
        return SellPortfolioTreeTableModelEx.cTypes[column];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public String getLanguageIndependentColumnName(int column) {
        return languageIndependentColumnNames[column];
    }
    
    private double getGainLossPercentage(Portfolio portfolio) {
        if(portfolio.getReferenceTotal() == 0.0) return 0.0;
        
        return (portfolio.getTotal() - portfolio.getReferenceTotal()) / portfolio.getReferenceTotal() * 100.0;
    }
    
    private double getGainLossValue(Portfolio portfolio) {
        return portfolio.getTotal() - portfolio.getReferenceTotal();
    }
    
    public double getNetSellingValue() {
        return ((Portfolio)getRoot()).getNetTotal();
    }

    public double getNetGainLossPercentage() {
        return getNetGainLossPercentage((Portfolio)getRoot());
    }

    public double getNetGainLossValue() {
        return getNetGainLossValue((Portfolio)getRoot());
    }
    
    private double getNetGainLossPercentage(Portfolio portfolio) {
        if(portfolio.getReferenceTotal() == 0.0) return 0.0;
        
        return (portfolio.getNetTotal() - portfolio.getReferenceTotal()) / portfolio.getReferenceTotal() * 100.0;
    }
    
    private double getNetGainLossValue(Portfolio portfolio) {
        return portfolio.getNetTotal() - portfolio.getReferenceTotal();
    }
    
    public double getSellingPrice(TransactionSummary transactionSummary) {
        if(transactionSummary.getQuantity() == 0.0) return 0.0;
        
        return transactionSummary.getTotal() / transactionSummary.getQuantity();
    }
    
    public double getPurchasePrice(TransactionSummary transactionSummary) {
        if(transactionSummary.getQuantity() == 0.0) return 0.0;
        
        return transactionSummary.getReferenceTotal() / transactionSummary.getQuantity();
    }
    
    public double getGainLossPrice(TransactionSummary transactionSummary) {
        if(transactionSummary.getQuantity() == 0.0) return 0.0;
        
        return ((transactionSummary.getTotal() - transactionSummary.getReferenceTotal()) / transactionSummary.getQuantity());        
    }
    
    public double getGainLossValue(TransactionSummary transactionSummary) {
        return transactionSummary.getTotal() - transactionSummary.getReferenceTotal();
    }

    public double getGainLossPercentage(TransactionSummary transactionSummary) {
        if(transactionSummary.getReferenceTotal() == 0.0) return 0.0;

        return (transactionSummary.getTotal() - transactionSummary.getReferenceTotal()) / transactionSummary.getReferenceTotal() * 100.0;
    }
    
    public double getNetGainLossValue(TransactionSummary transactionSummary) {
        return transactionSummary.getNetTotal() - transactionSummary.getReferenceTotal();
    }

    public double getNetGainLossPercentage(TransactionSummary transactionSummary) {
        if(transactionSummary.getReferenceTotal() == 0.0) return 0.0;

        return (transactionSummary.getNetTotal() - transactionSummary.getReferenceTotal()) / transactionSummary.getReferenceTotal() * 100.0;
    }
    
    private double getGainLossPrice(Transaction transaction) {
        return (transaction.getContract().getPrice() - transaction.getContract().getReferencePrice());
    }
    
    private double getGainLossValue(Transaction transaction) {
        return transaction.getTotal() - transaction.getReferenceTotal();
    }

    private double getGainLossPercentage(Transaction transaction) {
        if(transaction.getReferenceTotal() == 0.0) return 0.0;
        
        return (transaction.getTotal() - transaction.getReferenceTotal()) / transaction.getReferenceTotal() * 100.0;
    }

    private double getNetGainLossValue(Transaction transaction) {
        return transaction.getNetTotal() - transaction.getReferenceTotal();
    }

    private double getNetGainLossPercentage(Transaction transaction) {
        if(transaction.getReferenceTotal() == 0.0) return 0.0;
        
        return (transaction.getNetTotal() - transaction.getReferenceTotal()) / transaction.getReferenceTotal() * 100.0;
    }
    
    @Override
    public Object getValueAt(Object node, int column) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        if (node instanceof Portfolio) {
            final Portfolio portfolio = (Portfolio)node;
            
            switch(column) {
                case 0:
                    return GUIBundle.getString("PortfolioManagementJPanel_Sell");
        
                case 5:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return portfolio.getTotal();
                    }
                    else {
                        return portfolio.getTotal() / 100.0;
                    }

                case 6:
                    // Total purchase value.
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return portfolio.getReferenceTotal();
                    }
                    else {
                        return portfolio.getReferenceTotal() / 100.0;
                    }

                case 8:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return getGainLossValue(portfolio);
                    }
                    else {
                        return getGainLossValue(portfolio) / 100.0;
                    }

                case 9:
                    return getGainLossPercentage(portfolio);
    
                case 10:
                    return portfolio.getCalculatedBroker();
                    
                case 11:
                    return portfolio.getCalculatedClearingFee();
                    
                case 12:
                    return portfolio.getCalculatedStampDuty();
                    
                case 13:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return portfolio.getNetTotal();
                    }
                    else {
                        return portfolio.getNetTotal() / 100.0;
                    }

                case 14:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return getNetGainLossValue(portfolio);
                    }
                    else {
                        return getNetGainLossValue(portfolio) / 100.0;
                    }

                case 15:
                    return getNetGainLossPercentage(portfolio);

                case 16:
                    return portfolio.getComment();

                default:
                    return null;
            }
        }
   
        if (node instanceof TransactionSummary) {
            final TransactionSummary transactionSummary = (TransactionSummary)node;
            
            if (transactionSummary.getChildCount() <= 0) return null;
            
            switch(column) {
                case 0:
                    return ((Transaction)transactionSummary.getChildAt(0)).getContract().getStock().getSymbol();
                    
                case 2:
                    return transactionSummary.getQuantity();
                    
                case 3:
                    return getSellingPrice(transactionSummary);
                    
                case 4:
                    return getPurchasePrice(transactionSummary);
                    
                case 5:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return transactionSummary.getTotal();
                    }
                    else {
                        return transactionSummary.getTotal() / 100.0;
                    }
                    
                case 6:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return transactionSummary.getReferenceTotal();
                    }
                    else {
                        return transactionSummary.getReferenceTotal() / 100.0;
                    }
                    
                case 7:
                    return getGainLossPrice(transactionSummary);
                    
                case 8:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return getGainLossValue(transactionSummary);
                    }
                    else {
                        return getGainLossValue(transactionSummary) / 100.0;
                    }
                    
                case 9:
                    return getGainLossPercentage(transactionSummary);
                    
                case 10:
                    return transactionSummary.getCalculatedBroker();
                    
                case 11:
                    return transactionSummary.getCalculatdClearingFee();
                    
                case 12:
                    return transactionSummary.getCalculatedStampDuty();
                    
                case 13:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return transactionSummary.getNetTotal();
                    }
                    else {
                        return transactionSummary.getNetTotal() / 100.0;
                    }
                    
                case 14:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return getNetGainLossValue(transactionSummary);
                    }
                    else {
                        return getNetGainLossValue(transactionSummary) / 100.0;
                    }
                    
                case 15:
                    return getNetGainLossPercentage(transactionSummary);                    

                case 16:
                    return transactionSummary.getComment();
            }
        }
        
        if (node instanceof Transaction) {
            final Transaction transaction = (Transaction)node;
            
            switch(column) {
                case 0:
                    return (transaction).getContract().getStock().getSymbol();

                case 1:
                    return transaction.getContract().getDate();
                    
                case 2:
                    return transaction.getQuantity();
                    
                case 3:
                    return transaction.getContract().getPrice();
                    
                case 4:
                    return transaction.getContract().getReferencePrice();
                    
                case 5:
                    return transaction.getTotal();
                    
                case 6:
                    return transaction.getContract().getReferenceTotal();
                    
                case 7:
                    return getGainLossPrice(transaction);
                    
                case 8:
                    return getGainLossValue(transaction);
                    
                case 9:
                    return getGainLossPercentage(transaction);
                    
                case 10:
                    return transaction.getCalculatedBroker();
                    
                case 11:
                    return transaction.getCalculatdClearingFee();
                    
                case 12:
                    return transaction.getCalculatedStampDuty();
                    
                case 13:
                    return transaction.getNetTotal();
                    
                case 14:
                    return getNetGainLossValue(transaction);
                    
                case 15:
                    return getNetGainLossPercentage(transaction);                    

                case 16:
                    return transaction.getComment();
            }
        }
        
        return null;
    }
    
    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getContract().getType() == Contract.Type.Sell);
    }

}
