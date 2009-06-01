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
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import java.text.SimpleDateFormat;
import org.jdesktop.swingx.treetable.*;
import org.yccheok.jstock.portfolio.*;
import javax.swing.tree.TreePath;
import org.yccheok.jstock.engine.Code;

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
    private static final String[]  cNames = {
        "Stock", 
        "Date",
        "Units",
        "Purchase Price",
        "Current Price",        
        "Purchase Value", 
        "Current Value",
        "Gain/Loss Price",
        "Gain/Loss Value",
        "Gain/Loss %",
        "Broker", 
        "Clearing Fee",        
        "Stamp Duty",  
        "Net Purchase Value",
        "Net Gain/Loss Value",
        "Net Gain/Loss %"
    };

    // Types of the columns.
    private static final Class[]  cTypes = { 
        TreeTableModel.class,
        org.yccheok.jstock.engine.SimpleDate.class,
        Integer.class,
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
        Double.class
    };
    
    public boolean updateStockLastPrice(org.yccheok.jstock.engine.Stock stock) {
        boolean status = false;

        if (stock.getLastPrice() > 0.0) {
            stockPrice.put(stock.getCode(), stock.getLastPrice());
        }
        else {
            stockPrice.put(stock.getCode(), stock.getOpenPrice());
        }

        final Portfolio portfolio = (Portfolio)getRoot();
        final int count = portfolio.getChildCount();
        
        TransactionSummary transactionSummary = null;
        
        for(int i=0; i<count; i++) {
            transactionSummary = (TransactionSummary)portfolio.getChildAt(i);
            
            assert(transactionSummary.getChildCount() > 0);
            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
            
            if(true == transaction.getContract().getStock().getCode().equals(stock.getCode())) {
                break;
            }
        }
        
        if(null == transactionSummary) return status;
        
        final int num = transactionSummary.getChildCount();
        
        for(int i=0; i<num; i++) {
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
                        
            this.modelSupport.fireChildChanged(new TreePath(getPathToRoot(transaction)), i, transaction);
            
            status = true;
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
    
    private double getCurrentValue(Portfolio portfolio) {
        final int count = portfolio.getChildCount();
        
        double result = 0.0;
        
        for(int i=0; i<count; i++) {
            Object o = portfolio.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
                    
            assert(transactionSummary.getChildCount() > 0);            

            final Code code = ((Transaction)transactionSummary.getChildAt(0)).getContract().getStock().getCode();
            
            final Double price = this.stockPrice.get(code);
            
            if (price == null) continue;
            
            final int quantity = transactionSummary.getQuantity();
            
            result += (price * quantity);
        }
        
        return result;
    }
    
    public double getPurchasePrice(TransactionSummary transactionSummary) {
        if(transactionSummary.getQuantity() == 0.0) return 0.0;
        
        return transactionSummary.getTotal() / transactionSummary.getQuantity();
    }
    
    public double getGainLossPrice(TransactionSummary transactionSummary) {
        if(transactionSummary.getQuantity() == 0.0) return 0.0;
        
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
        if(node instanceof Portfolio) {
            final Portfolio portfolio = (Portfolio)node;
            
            switch(column) {
                case 0:
                    return "Buy";
        
                case 5:
                    return portfolio.getTotal();
                    
                case 6:
                    return getCurrentValue(portfolio);
                    
                case 8:
                    return this.getGainLossValue(portfolio);
                    
                case 9:
                    return this.getGainLossPercentage(portfolio);
    
                case 10:
                    return portfolio.getCalculatedBroker();
                    
                case 11:
                    return portfolio.getCalculatedClearingFee();
                    
                case 12:
                    return portfolio.getCalculatedStampDuty();
                    
                case 13:
                    return portfolio.getNetTotal();
                    
                case 14:
                    return this.getNetGainLossValue(portfolio);
                    
                case 15:
                    return this.getNetGainLossPercentage(portfolio);
                                             
                default:
                    return null;
            }
        }
   
        if(node instanceof TransactionSummary) {
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
                    return transactionSummary.getTotal();
                    
                case 6:
                    return this.getCurrentValue(transactionSummary);
                    
                case 7:
                    return this.getGainLossPrice(transactionSummary);
                    
                case 8:
                    return this.getGainLossValue(transactionSummary);
                    
                case 9:
                    return this.getGainLossPercentage(transactionSummary);
                    
                case 10:
                    return transactionSummary.getCalculatedBroker();
                    
                case 11:
                    return transactionSummary.getCalculatdClearingFee();
                    
                case 12:
                    return transactionSummary.getCalculatedStampDuty();
                    
                case 13:
                    return transactionSummary.getNetTotal();
                    
                case 14:
                    return this.getNetGainLossValue(transactionSummary);
                    
                case 15:
                    return this.getNetGainLossPercentage(transactionSummary);                    
                    
            }
        }
        
        if(node instanceof Transaction) {
            final Transaction transaction = (Transaction)node;
            
            switch(column) {
                case 0:
                    return (transaction).getContract().getStock().getSymbol();

                case 1:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                    return simpleDateFormat.format(transaction.getContract().getDate().getCalendar().getTime());
                    
                case 2:
                    return transaction.getQuantity();
                    
                case 3:
                    return transaction.getContract().getPrice();
                    
                case 4:
                    return this.getCurrentPrice(transaction);
                    
                case 5:
                    return transaction.getTotal();
                    
                case 6:
                    return this.getCurrentValue(transaction);
                    
                case 7:
                    return this.getCurrentPrice(transaction) - (transaction.getTotal() / transaction.getQuantity());
                    
                case 8:
                    return this.getCurrentValue(transaction) - transaction.getTotal();
                    
                case 9:
                    return this.getGainLossPercentage(transaction);
                    
                case 10:
                    return transaction.getCalculatedBroker();
                    
                case 11:
                    return transaction.getCalculatdClearingFee();
                    
                case 12:
                    return transaction.getCalculatedStampDuty();
                    
                case 13:
                    return transaction.getNetTotal();
                    
                case 14:
                    return this.getNetGainLossValue(transaction);
                    
                case 15:
                    return this.getNetGainLossPercentage(transaction);                    
                    
            }
        }
        
	return null; 
    }

    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getContract().getType() == Contract.Type.Buy);
    }
}
