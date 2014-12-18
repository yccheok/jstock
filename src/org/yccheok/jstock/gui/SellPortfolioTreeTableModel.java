/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.gui;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.yccheok.jstock.gui.treetable.SellPortfolioTreeTableModelEx;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.DecimalPlaces;
import org.yccheok.jstock.portfolio.DoubleWrapper;
/**
 *
 * @author yccheok
 */
@Deprecated
public class SellPortfolioTreeTableModel extends DeprecatedAbstractPortfolioTreeTableModel {
    
    // Names of the columns.
    private static final String[] cNames;

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
        cNames = tmp;
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
        return (transaction.getPrice() - transaction.getReferencePrice());
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
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();

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
                    return portfolio.getBroker();
                    
                case 11:
                    return portfolio.getClearingFee();
                    
                case 12:
                    return portfolio.getStampDuty();
                    
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
                    return ((Transaction)transactionSummary.getChildAt(0)).getStock().symbol;
                    
                case 2:
                    return transactionSummary.getQuantity();
                    
                case 3:
                    if (JStock.getInstance().getJStockOptions().isFourDecimalPlacesEnabled()) {
                        return new DoubleWrapper(DecimalPlaces.Four, this.getSellingPrice(transactionSummary));
                    } else {
                        return new DoubleWrapper(DecimalPlaces.Three, this.getSellingPrice(transactionSummary));   
                    }
                    
                case 4:
                    if (JStock.getInstance().getJStockOptions().isFourDecimalPlacesEnabled()) {
                        return new DoubleWrapper(DecimalPlaces.Four, this.getPurchasePrice(transactionSummary));
                    } else {
                        return new DoubleWrapper(DecimalPlaces.Three, this.getPurchasePrice(transactionSummary));   
                    }
                    
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
                    return transactionSummary.getBroker();
                    
                case 11:
                    return transactionSummary.getClearingFee();
                    
                case 12:
                    return transactionSummary.getStampDuty();
                    
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
                    return (transaction).getStock().symbol;

                case 1:
                    return transaction.getDate();
                    
                case 2:
                    return transaction.getQuantity();
                    
                case 3:
                    return transaction.getPrice();
                    
                case 4:
                    return transaction.getReferencePrice();
                    
                case 5:
                    return transaction.getTotal();
                    
                case 6:
                    return transaction.getReferenceTotal();
                    
                case 7:
                    return getGainLossPrice(transaction);
                    
                case 8:
                    return getGainLossValue(transaction);
                    
                case 9:
                    return getGainLossPercentage(transaction);
                    
                case 10:
                    return transaction.getBroker();
                    
                case 11:
                    return transaction.getClearingFee();
                    
                case 12:
                    return transaction.getStampDuty();
                    
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
    
    public SellPortfolioTreeTableModelEx toSellPortfolioTreeTableModelEx() {
        SellPortfolioTreeTableModelEx sellPortfolioTreeTableModelEx = new SellPortfolioTreeTableModelEx();
        sellPortfolioTreeTableModelEx.setRoot(this.getRoot());
        // Hacking. Pass TreeTableModel to portfolio, so that sorting would work.
        ((Portfolio)sellPortfolioTreeTableModelEx.getRoot()).setTreeTableModel(sellPortfolioTreeTableModelEx);

        return sellPortfolioTreeTableModelEx;
    }
        
    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getType() == Contract.Type.Sell);
    }

}
