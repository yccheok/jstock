/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.gui;

import java.text.SimpleDateFormat;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;

/**
 *
 * @author yccheok
 */
public class SellPortfolioTreeTableModel extends AbstractPortfolioTreeTableModel {
    
    // Names of the columns.
    private static final String[]  cNames = {
        "Stock", 
        "Date",
        "Units",
        "Selling Price",
        "Purchase Price",        
        "Selling Value", 
        "Purchase Value",
        "Gain/Loss Price",
        "Gain/Loss Value",
        "Gain/Loss %",
        "Broker", 
        "Clearing Fee",        
        "Stamp Duty",  
        "Net Selling Value",
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
    
    @Override
    public int getColumnCount() {
        assert(cNames.length == cTypes.length);
        return cNames.length;
    }

    @Override
    public Class getColumnClass(int column) {
        assert(cNames.length == cTypes.length);        
        return SellPortfolioTreeTableModel.cTypes[column];
    }

    @Override
    public String getColumnName(int column) {
        return cNames[column];
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
        if(node instanceof Portfolio) {
            final Portfolio portfolio = (Portfolio)node;
            
            switch(column) {
                case 0:
                    return "Sell";
        
                case 5:
                    return portfolio.getTotal();
                    
                case 6:
                    // Total purchase value.
                    return portfolio.getReferenceTotal();
                    
                case 8:
                    return getGainLossValue(portfolio);
                    
                case 9:
                    return getGainLossPercentage(portfolio);
    
                case 10:
                    return portfolio.getCalculatedBroker();
                    
                case 11:
                    return portfolio.getCalculatedClearingFee();
                    
                case 12:
                    return portfolio.getCalculatedStampDuty();
                    
                case 13:
                    return portfolio.getNetTotal();
                    
                case 14:
                    return getNetGainLossValue(portfolio);
                    
                case 15:
                    return getNetGainLossPercentage(portfolio);
                                             
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
                    return getSellingPrice(transactionSummary);
                    
                case 4:
                    return getPurchasePrice(transactionSummary);
                    
                case 5:
                    return transactionSummary.getTotal();
                    
                case 6:
                    return transactionSummary.getReferenceTotal();
                    
                case 7:
                    return getGainLossPrice(transactionSummary);
                    
                case 8:
                    return getGainLossValue(transactionSummary);
                    
                case 9:
                    return getGainLossPercentage(transactionSummary);
                    
                case 10:
                    return transactionSummary.getCalculatedBroker();
                    
                case 11:
                    return transactionSummary.getCalculatdClearingFee();
                    
                case 12:
                    return transactionSummary.getCalculatedStampDuty();
                    
                case 13:
                    return transactionSummary.getNetTotal();
                    
                case 14:
                    return getNetGainLossValue(transactionSummary);
                    
                case 15:
                    return getNetGainLossPercentage(transactionSummary);                    
                    
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
                    
            }
        }
        
	return null; 
    }
    
    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getContract().getType() == Contract.Type.Sell);
    }

}
