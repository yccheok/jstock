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

package org.yccheok.jstock.gui;

import org.yccheok.jstock.gui.treetable.*;
import org.yccheok.jstock.portfolio.*;
import org.yccheok.jstock.engine.*;
import java.util.*;

/**
 *
 * @author Owner
 */
public class PortfolioTreeTableModel extends AbstractTreeTableModel {
    private java.util.Map<String, Double> stockLastPrice = new java.util.HashMap<String, Double>();
    
    private Portfolio portfolio;
    
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
        "Stamp Duty", 
        "Clearing Fee", 
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
       
    public PortfolioTreeTableModel(Portfolio portfolio) {
        super(portfolio) ;
        
        this.portfolio = portfolio;
        
        
        
        
                Contract contract = new Contract(
                        Utils.getEmptyStock("1234", "pbbank"), 
                        new SimpleDate(2008, 0, 31), 
                        Contract.Type.Buy, 
                        1000, 
                        2.00);
                
                Broker broker = new SimpleBroker("SimpleBroker", 1000, 100, 2.0);
                
                StampDuty stampDuty = new SimpleStampDuty("SimpleStampDuty", 1000, 100, 2.0);
               
                ClearingFee clearingFee = new SimpleClearingFee("SimpleClearingfee", 1000, 100, 2.0);
                        
                Transaction transaction = new Transaction(contract, broker, stampDuty, clearingFee);        
                
                portfolio.addTransaction(transaction);
    }

    public PortfolioTreeTableModel() {
        this(new Portfolio()) ;
    }
    
    public void updateStockLastPrice(org.yccheok.jstock.engine.Stock stock, double lastPrice) {
        stockLastPrice.put(stock.getCode(), lastPrice);
    }
            
    public int getColumnCount() {
        assert(cNames.length == cTypes.length);
        return cNames.length;
    }

    @Override
    @SuppressWarnings("static-access")
    public Class getColumnClass(int column) {
        return this.cTypes[column];
    }
    
    public String getColumnName(int column) {
        return cNames[column];        
    }

    private double getStockCurrentValue(Transaction transaction) {
        final String code = transaction.getContract().getStock().getCode();
        final Double lastPrice = this.stockLastPrice.get(code);

        if(lastPrice == null) return 0.0;
        
        return lastPrice * transaction.getQuantity();        
    }
    
    private double getStockCurrentValue(TransactionSummary transactionSummary) {
        final String code = transactionSummary.getTransaction(0).getContract().getStock().getCode();
        final Double lastPrice = this.stockLastPrice.get(code);

        if(lastPrice == null) return 0.0;
        
        return lastPrice * transactionSummary.getQuantity();
    }
    
    private double getStockCurrentPrice(Transaction transaction) {
        final String code = transaction.getContract().getStock().getCode();
        final Double lastPrice = this.stockLastPrice.get(code);

        if(lastPrice == null) return 0.0;
        
        return lastPrice;        
    }
    
    private double getStockCurrentPrice(TransactionSummary transactionSummary) {
        final String code = transactionSummary.getTransaction(0).getContract().getStock().getCode();
        final Double lastPrice = this.stockLastPrice.get(code);

        if(lastPrice == null) return 0.0;
        
        return lastPrice;
    }
    
    private double getStockGainLossValue(Portfolio portfolio) {
        return getStockCurrentValue(portfolio) - portfolio.getTotal();
    }
    
    private double getStockGainLossPercentage(Portfolio portfolio) {
        return (getStockCurrentValue(portfolio) - portfolio.getTotal()) / portfolio.getTotal() * 100.0;        
    }
    
    private double getStockNetGainLossValue(Portfolio portfolio) {
        return getStockCurrentValue(portfolio) - portfolio.getNetTotal();
    }
    
    private double getStockNetGainLossPercentage(Portfolio portfolio) {
        return (getStockCurrentValue(portfolio) - portfolio.getNetTotal()) / portfolio.getNetTotal() * 100.0;        
    }
    
    private double getStockCurrentValue(Portfolio portfolio) {
        final int portfolioSize = portfolio.getSize();
        
        double result = 0.0;
        
        for(int i=0; i<portfolioSize; i++) {
            final TransactionSummary transactionSummary = portfolio.getTransactionSummary(i);
            assert(transactionSummary.getSize() > 0);
            
            final String code = transactionSummary.getTransaction(0).getContract().getStock().getCode();
            final Double lastPrice = this.stockLastPrice.get(code);
            
            if(lastPrice == null) continue;
            
            final int quantity = transactionSummary.getQuantity();
            
            result += (lastPrice * quantity);            
        }
        
        return result;
    }
    
    public Object getValueAt(Object node, int column) {
        if(node instanceof Portfolio) {
            final Portfolio portfolio = (Portfolio)node;
            
            switch(column) {
                case 0:
                    return "Portfolio";
        
                case 5:
                    return portfolio.getTotal();
                    
                case 6:
                    return getStockCurrentValue(portfolio);
                    
                case 8:
                    return this.getStockGainLossValue(portfolio);
                    
                case 9:
                    return this.getStockGainLossPercentage(portfolio);
    
                case 14:
                    return this.getStockNetGainLossValue(portfolio);
                    
                case 15:
                    return this.getStockNetGainLossPercentage(portfolio);
                                             
                default:
                    return null;
            }
        }
   
        if(node instanceof TransactionSummary) {
            final TransactionSummary transactionSummary = (TransactionSummary)node;
            
            switch(column) {
                case 0:
                    return transactionSummary.getTransaction(0).getContract().getStock().getSymbol();
                    
                case 2:
                    return transactionSummary.getQuantity();
                    
                case 3:
                    return transactionSummary.getTotal() / transactionSummary.getQuantity();
                    
                case 4:
                    return this.getStockCurrentPrice(transactionSummary);
                    
                case 5:
                    return transactionSummary.getTotal();
                    
                case 6:
                    return this.getStockCurrentValue(transactionSummary);
            }
        }
        
        if(node instanceof Transaction) {
            final Transaction transaction = (Transaction)node;
            
            switch(column) {
                case 1:
                    return transaction.getContract().getDate();
                    
                case 2:
                    return transaction.getQuantity();
            }
        }
        
	return null; 
    }

    public Object getChild(Object parent, int index) {
        if(parent instanceof TreeTableable) {
            final TreeTableable treeTableable = (TreeTableable)parent;
            
            return treeTableable.getChild(index);
        }
        
        return null;
    }

    public int getChildCount(Object parent) {
        if(parent instanceof TreeTableable) {
            final TreeTableable treeTableable = (TreeTableable)parent;
            return treeTableable.getSize();
        }
        
        return 0;
    }

    public boolean addTransaction(Transaction transaction) {
        if(portfolio.addTransaction(transaction)) {
            Object[] children = { transaction };
            int[] index = { this.getIndexOfChild(transaction.getParent(), transaction) };
           		
            final TreeTableable[] path = transaction.getPath();
            fireTreeStructureChanged(this, path, null,
					 null);
                             
            this.fireTreeNodesInserted(
                    (Object)portfolio, 
                    (Object[])transaction.getPath(), 
                    index,
                    children);
            
            return true;
        }
        
        return false;
    }
}
