/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jdesktop.swingx.treetable.*;
import org.yccheok.jstock.portfolio.*;
import javax.swing.tree.TreePath;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.internationalization.GUIBundle;

/**
 *
 * @author Owner
 */
public class BuyPortfolioTreeTableModel extends AbstractPortfolioTreeTableModel {
    // Can be either stock last price or open price. If stock last price is 0
    // at current moment (Usually, this means no transaction has been done on
    // that day), open price will be applied.
    private java.util.Map<Code, Double> stockPrice = new java.util.HashMap<Code, Double>();
    
    public double getLastPrice(Code code) {
        Object price = stockPrice.get(code);
        if(price == null) return 0.0;
                
        return (Double)price;
    }
    
    // Names of the columns.
    private static final String[]  cNames;

    static {
        final String[] tmp = {
            GUIBundle.getString("PortfolioManagementJPanel_Stock"),
            GUIBundle.getString("PortfolioManagementJPanel_Date"),
            GUIBundle.getString("PortfolioManagementJPanel_Units"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchasePrice"),
            GUIBundle.getString("PortfolioManagementJPanel_CurrentPrice"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchaseValue"),
            GUIBundle.getString("PortfolioManagementJPanel_CurrentValue"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossPrice"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossValue"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossPercentage"),
            GUIBundle.getString("PortfolioManagementJPanel_Broker"),
            GUIBundle.getString("PortfolioManagementJPanel_ClearingFee"),
            GUIBundle.getString("PortfolioManagementJPanel_StampDuty"),
            GUIBundle.getString("PortfolioManagementJPanel_NetPurchaseValue"),
            GUIBundle.getString("PortfolioManagementJPanel_NetGainLossValue"),
            GUIBundle.getString("PortfolioManagementJPanel_NetGainLossPercentage"),
            GUIBundle.getString("PortfolioManagementJPanel_Comment")
        };
        cNames = tmp;
    }

    // Types of the columns.
    private static final Class[]  cTypes = { 
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

    /**
     * Performs stock splitting, or reverse splitting aka merging, on selected
     * stock info, if ratio is less than 1.
     *
     * @param stockInfo the stock info
     * @param ratio splitting ratio, or reverse splitting ratio if less than 1
     * @return true if success
     */
    public boolean split(StockInfo stockInfo, double ratio) {
        boolean status = false;

        final Portfolio portfolio = (Portfolio)getRoot();
        final int count = portfolio.getChildCount();

        TransactionSummary transactionSummary = null;

        for (int i = 0; i < count; i++) {
            transactionSummary = (TransactionSummary)portfolio.getChildAt(i);

            assert(transactionSummary.getChildCount() > 0);

            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);

            if (true == transaction.getContract().getStock().getCode().equals(stockInfo.code)) {
                break;
            }
        }

        if (null == transactionSummary) return status;

        final int num = transactionSummary.getChildCount();

        for (int i = 0; i < num; i++) {
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
            double quantity = transaction.getQuantity() * ratio;
            // Remember to adjust the purchase price as well.
            double price = transaction.getContract().getPrice() / ratio;
            this.editTransaction(transaction.deriveWithQuantity(quantity).deriveWithPrice(price), transaction);
            status = true;
        }

        return status;
    }

    public boolean updateStockLastPrice(org.yccheok.jstock.engine.Stock stock) {
        final Portfolio portfolio = (Portfolio)getRoot();
        final int count = portfolio.getChildCount();
        
        TransactionSummary transactionSummary = null;
        
        for (int i = 0; i < count; i++) {
            transactionSummary = (TransactionSummary)portfolio.getChildAt(i);
            
            assert(transactionSummary.getChildCount() > 0);
            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
            
            if (true == transaction.getContract().getStock().getCode().equals(stock.getCode())) {
                break;
            }
        }

        boolean status = false;
        
        if (null == transactionSummary) {
            // This stock is not found in transaction records. Returns false
            // early.
            return status;
        }
        
        final int num = transactionSummary.getChildCount();

        if (num == 0) {
            return status;
        }

        status = true;

        // Only update stockPrice map if this stock is found in transaction
        // records.
        if (stock.getLastPrice() > 0.0) {
            stockPrice.put(stock.getCode(), stock.getLastPrice());
        }
        else {
            stockPrice.put(stock.getCode(), stock.getPrevPrice());
        }

        for (int i = 0; i < num; i++) {
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
                        
            this.modelSupport.fireChildChanged(new TreePath(getPathToRoot(transaction)), i, transaction);
        }
                
        fireTreeTableNodeChanged(transactionSummary);
        fireTreeTableNodeChanged(getRoot());
                
        return status;
    }
    
    private double getCurrentValue(Transaction transaction) {
        final Code code = transaction.getContract().getStock().getCode();
        final Double price = this.stockPrice.get(code);

        if (price == null) return 0.0;
        
        return price * transaction.getQuantity();
    }
    
    public double getCurrentValue(TransactionSummary transactionSummary) {
        final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
        final Code code = transaction.getContract().getStock().getCode();
        
        final Double price = this.stockPrice.get(code);

        if (price == null) return 0.0;
        
        return price * transactionSummary.getQuantity();
    }
    
    private double getCurrentPrice(Transaction transaction) {
        final Code code = transaction.getContract().getStock().getCode();
        
        final Double price = this.stockPrice.get(code);

        if (price == null) return 0.0;
        
        return price;
    }
    
    public double getCurrentPrice(TransactionSummary transactionSummary) {
        final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
        final Code code = transaction.getContract().getStock().getCode();

        final Double price = this.stockPrice.get(code);

        if (price == null) return 0.0;
        
        return price;
    }
    
    private double getGainLossValue(Portfolio portfolio) {
        return getCurrentValue(portfolio) - portfolio.getTotal();
    }
    
    private double getGainLossPercentage(Portfolio portfolio) {
        if(portfolio.getTotal() == 0) return 0.0;
        
        return (getCurrentValue(portfolio) - portfolio.getTotal()) / portfolio.getTotal() * 100.0;        
    }

    public double getGainLossPercentage(TransactionSummary transactionSummary) {
        if(transactionSummary.getTotal() == 0) return 0.0;
        
        return (getCurrentValue(transactionSummary) - transactionSummary.getTotal()) / transactionSummary.getTotal() * 100.0;        
    }
    
    public double getNetGainLossValue(TransactionSummary transactionSummary) {
        return getCurrentValue(transactionSummary) - transactionSummary.getNetTotal();
    }
    
    public double getNetGainLossPercentage(TransactionSummary transactionSummary) {
        if(transactionSummary.getTotal() == 0) return 0.0;
        
        return (getCurrentValue(transactionSummary) - transactionSummary.getNetTotal()) / transactionSummary.getNetTotal() * 100.0;        
    }
    
    private double getGainLossPercentage(Transaction transaction) {
        if(transaction.getTotal() == 0) return 0.0;
        
        return (getCurrentValue(transaction) - transaction.getTotal()) / transaction.getTotal() * 100.0;        
    }
    
    private double getNetGainLossValue(Transaction transaction) {
        return getCurrentValue(transaction) - transaction.getNetTotal();
    }
    
    private double getNetGainLossPercentage(Transaction transaction) {
        if(transaction.getNetTotal() == 0) return 0.0;
        
        return (getCurrentValue(transaction) - transaction.getNetTotal()) / transaction.getNetTotal() * 100.0;        
    }
    
    public double getNetGainLossValue() {
        return getNetGainLossValue((Portfolio)getRoot());
    }
    
    private double getNetGainLossValue(Portfolio portfolio) {
        return getCurrentValue(portfolio) - portfolio.getNetTotal();
    }

    public double getNetGainLossPercentage() {
        return getNetGainLossPercentage((Portfolio)getRoot());
    }
    
    private double getNetGainLossPercentage(Portfolio portfolio) {
        if(portfolio.getNetTotal() == 0) return 0.0;
        
        return (getCurrentValue(portfolio) - portfolio.getNetTotal()) / portfolio.getNetTotal() * 100.0;        
    }
    
    public double getCurrentValue() {
        return this.getCurrentValue((Portfolio)getRoot());        
    }

    public double getNetPurchaseValue() {
        return ((Portfolio)getRoot()).getNetTotal();
    }

    private double getCurrentValue(Portfolio portfolio) {
        final int count = portfolio.getChildCount();
        
        double result = 0.0;
        
        for (int i = 0; i < count; i++) {
            Object o = portfolio.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
                    
            assert(transactionSummary.getChildCount() > 0);            

            final Code code = ((Transaction)transactionSummary.getChildAt(0)).getContract().getStock().getCode();
            
            final Double price = this.stockPrice.get(code);
            
            if (price == null) continue;
            
            final double quantity = transactionSummary.getQuantity();
            
            result += (price * quantity);
        }
        
        return result;
    }
    
    public double getPurchasePrice(TransactionSummary transactionSummary) {
        if (transactionSummary.getQuantity() == 0.0) {
            return 0.0;
        }
        
        return transactionSummary.getTotal() / transactionSummary.getQuantity();
    }
    
    public double getGainLossPrice(TransactionSummary transactionSummary) {
        if (transactionSummary.getQuantity() == 0.0) {
            return 0.0;
        }
        
        return this.getCurrentPrice(transactionSummary) - (transactionSummary.getTotal() / transactionSummary.getQuantity());        
    }
    
    public double getGainLossValue(TransactionSummary transactionSummary) {
        return this.getCurrentValue(transactionSummary) - transactionSummary.getTotal();        
    }

    @Override
    public int getColumnCount() {
        assert(cNames.length == cTypes.length);
        return cNames.length;
    }

    @Override
    public Class getColumnClass(int column) {
        return BuyPortfolioTreeTableModel.cTypes[column];
    }

    @Override
    public String getColumnName(int column) {
        return cNames[column];  
    }

    @Override
    public Object getValueAt(Object node, int column) {
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();

        if (node instanceof Portfolio) {
            final Portfolio portfolio = (Portfolio)node;
            
            switch(column) {
                case 0:
                    return GUIBundle.getString("PortfolioManagementJPanel_Buy");
        
                case 5:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return portfolio.getTotal();
                    }
                    else {
                        return portfolio.getTotal() / 100.0;
                    }
                    
                case 6:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return getCurrentValue(portfolio);
                    }
                    else {
                        return getCurrentValue(portfolio) / 100.0;
                    }
                    
                case 8:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return this.getGainLossValue(portfolio);
                    }
                    else {
                        return this.getGainLossValue(portfolio) / 100.00;
                    }
                    
                case 9:
                    return this.getGainLossPercentage(portfolio);
    
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
                        return this.getNetGainLossValue(portfolio);
                    }
                    else {
                        return this.getNetGainLossValue(portfolio) / 100.0;
                    }
                    
                case 15:
                    return this.getNetGainLossPercentage(portfolio);

                case 16:
                    return portfolio.getComment();

                default:
                    return null;
            }
        }
   
        if (node instanceof TransactionSummary) {
            final TransactionSummary transactionSummary = (TransactionSummary)node;
            
            if(transactionSummary.getChildCount() <= 0) return null;
            
            switch(column) {
                case 0:
                    return ((Transaction)transactionSummary.getChildAt(0)).getContract().getStock().getSymbol();
                    
                case 2:
                    return transactionSummary.getQuantity();
                    
                case 3:
                    return this.getPurchasePrice(transactionSummary);
                    
                case 4:
                    return this.getCurrentPrice(transactionSummary);
                    
                case 5:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return transactionSummary.getTotal();
                    }
                    else {
                        return transactionSummary.getTotal() / 100.0;
                    }
                    
                case 6:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return this.getCurrentValue(transactionSummary);
                    }
                    else {
                        return this.getCurrentValue(transactionSummary) / 100.0;
                    }
                    
                case 7:
                    return this.getGainLossPrice(transactionSummary);
                    
                case 8:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return this.getGainLossValue(transactionSummary);
                    }
                    else {
                        return this.getGainLossValue(transactionSummary) / 100.0;
                    }
                    
                case 9:
                    return this.getGainLossPercentage(transactionSummary);
                    
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
                        return this.getNetGainLossValue(transactionSummary);
                    }
                    else {
                        return this.getNetGainLossValue(transactionSummary) / 100.0;
                    }
                    
                case 15:
                    return this.getNetGainLossPercentage(transactionSummary);                    

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
                    return this.getCurrentPrice(transaction);
                    
                case 5:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return transaction.getTotal();
                    }
                    else {
                        return transaction.getTotal() / 100.0;
                    }
                    
                case 6:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return this.getCurrentValue(transaction);
                    }
                    else {
                        return this.getCurrentValue(transaction) / 100.0;
                    }
                    
                case 7:
                    return this.getCurrentPrice(transaction) - (transaction.getTotal() / transaction.getQuantity());
                    
                case 8:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return this.getCurrentValue(transaction) - transaction.getTotal();
                    }
                    else {
                        return (this.getCurrentValue(transaction) - transaction.getTotal()) / 100.0;
                    }
                    
                case 9:
                    return this.getGainLossPercentage(transaction);
                    
                case 10:
                    return transaction.getCalculatedBroker();
                    
                case 11:
                    return transaction.getCalculatdClearingFee();
                    
                case 12:
                    return transaction.getCalculatedStampDuty();
                    
                case 13:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return transaction.getNetTotal();
                    }
                    else {
                        return transaction.getNetTotal() / 100.0;
                    }
                    
                case 14:
                    if (jStockOptions.isPenceToPoundConversionEnabled() == false) {
                        return this.getNetGainLossValue(transaction);
                    }
                    else {
                        return this.getNetGainLossValue(transaction) / 100.0;
                    }
                    
                case 15:
                    return this.getNetGainLossPercentage(transaction);                    

                case 16:
                    return transaction.getComment();
            }
        }
        
		return null; 
    }

    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getContract().getType() == Contract.Type.Buy);
    }

    private Object readResolve() {
        // Remove all invalid records found in stockPrice. This is caused by
        // old bug introduced in updateStockLastPrice.

        final Portfolio portfolio = (Portfolio)getRoot();
        final int count = portfolio.getChildCount();
        final Set<Code> set = new HashSet<Code>();

        for (int i = 0; i < count; i++) {
            TransactionSummary transactionSummary = (TransactionSummary)portfolio.getChildAt(i);

            assert(transactionSummary.getChildCount() > 0);

            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);

            set.add(transaction.getContract().getStock().getCode());
        }

        // http://stackoverflow.com/questions/1884889/iterating-over-and-removing-from-a-map
        Iterator<Code> it = stockPrice.keySet().iterator();
        while (it.hasNext()) {
            if (!set.contains(it.next())) {
                // This stock is not found in transaction records. Remove it.
                it.remove();
            }
        }

        return this;
    }
}
