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

package org.yccheok.jstock.gui.treetable;

import java.util.Arrays;
import org.jdesktop.swingx.treetable.*;
import org.yccheok.jstock.portfolio.*;
import javax.swing.tree.TreePath;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.PortfolioManagementJPanel;
import org.yccheok.jstock.internationalization.GUIBundle;

/**
 *
 * @author yccheok
 */
public class BuyPortfolioTreeTableModelEx extends AbstractPortfolioTreeTableModelEx {
    
    // Avoid NPE.
    private PortfolioRealTimeInfo portfolioRealTimeInfo = new PortfolioRealTimeInfo();
    private PortfolioManagementJPanel portfolioManagementJPanel = null;

    public void bind(PortfolioRealTimeInfo portfolioRealTimeInfo) {
        this.portfolioRealTimeInfo = portfolioRealTimeInfo;
        final Portfolio portfolio = (Portfolio)getRoot();
        portfolio.bind(portfolioRealTimeInfo);
    }

    public void bind(PortfolioManagementJPanel portfolioManagementJPanel) {
        this.portfolioManagementJPanel = portfolioManagementJPanel;
    }
    
    public BuyPortfolioTreeTableModelEx() {
        super(Arrays.asList(columnNames));
    }
    
    // Names of the columns.
    private static final String[] columnNames;   
    
    static {
        final String[] tmp = {
            GUIBundle.getString("PortfolioManagementJPanel_Stock"),
            GUIBundle.getString("PortfolioManagementJPanel_Code"),
            GUIBundle.getString("PortfolioManagementJPanel_Date"),
            GUIBundle.getString("PortfolioManagementJPanel_Units"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchasePrice"),
            GUIBundle.getString("PortfolioManagementJPanel_CurrentPrice"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchaseValue"),
            GUIBundle.getString("PortfolioManagementJPanel_CurrentValue"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossValue"),
            GUIBundle.getString("PortfolioManagementJPanel_GainLossPercentage"),
            GUIBundle.getString("PortfolioManagementJPanel_Broker"),
            GUIBundle.getString("PortfolioManagementJPanel_ClearingFee"),
            GUIBundle.getString("PortfolioManagementJPanel_StampDuty"),
            GUIBundle.getString("PortfolioManagementJPanel_Comment")
        };      
        columnNames = tmp;
    }

    // Types of the columns.
    private static final Class[]  cTypes = { 
        TreeTableModel.class,
        Code.class,
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

            if (true == transaction.getStockInfo().code.equals(stockInfo.code)) {
                break;
            }
        }

        if (null == transactionSummary) return status;

        final int num = transactionSummary.getChildCount();

        for (int i = 0; i < num; i++) {
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
            double quantity = transaction.getQuantity() * ratio;
            // Remember to adjust the purchase price as well.
            double price = transaction.getPrice() / ratio;
            this.editTransaction(transaction.deriveWithQuantity(quantity).deriveWithPrice(price), transaction);
            status = true;
        }

        return status;
    }

    public void refreshRoot() {
        fireTreeTableNodeChanged(getRoot());
    }
    
    public boolean refresh(Code code) {
        final Portfolio portfolio = (Portfolio)getRoot();
        final int count = portfolio.getChildCount();
        
        TransactionSummary transactionSummary = null;
        
        // Possible to have index out of bound exception, as mutable operation
        // may occur in between by another thread. But it should be fine at this
        // moment, as this method will only be consumed by RealTimeStockMonitor,
        // and it is fail safe.
        for (int i = 0; i < count; i++) {
            TransactionSummary ts = (TransactionSummary)portfolio.getChildAt(i);
            
            assert(ts.getChildCount() > 0);
            
            final Transaction transaction = (Transaction)ts.getChildAt(0);
            
            if (true == transaction.getStockInfo().code.equals(code)) {
                transactionSummary = ts;
                break;
            }
        }
        
        if (null == transactionSummary) {
            return false;
        }
        
        final int num = transactionSummary.getChildCount();

        if (num == 0) {
            return false;
        }

        for (int i = 0; i < num; i++) {
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
                        
            this.modelSupport.fireChildChanged(new TreePath(getPathToRoot(transaction)), i, transaction);
        }
                
        fireTreeTableNodeChanged(transactionSummary);
        fireTreeTableNodeChanged(getRoot());
                
        return true;        
    }
    
    public double getCurrentValue(Transaction transaction) {
        final Code code = transaction.getStockInfo().code;
        final Double price = this.portfolioRealTimeInfo.stockPrices.get(code);

        if (price == null) return 0.0;
        
        return price * transaction.getQuantity();
    }
    
    public double getCurrentValue(TransactionSummary transactionSummary) {
        final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
        final Code code = transaction.getStockInfo().code;
        
        final Double price = this.portfolioRealTimeInfo.stockPrices.get(code);

        if (price == null) return 0.0;
        
        return price * transactionSummary.getQuantity();
    }
    
    public double getCurrentPrice(Transaction transaction) {
        final Code code = transaction.getStockInfo().code;
        
        final Double price = this.portfolioRealTimeInfo.stockPrices.get(code);

        if (price == null) return 0.0;
        
        return price;
    }
    
    public double getCurrentPrice(TransactionSummary transactionSummary) {
        final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
        final Code code = transaction.getStockInfo().code;

        final Double price = this.portfolioRealTimeInfo.stockPrices.get(code);

        if (price == null) return 0.0;
        
        return price;
    }
    
    public double getGainLossValue(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        return getCurrentValue(localCurrency) - portfolio.getTotal(localCurrency);
    }
    
    public double getGainLossPercentage(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        
        double total = portfolio.getTotal(localCurrency);
        if (total == 0.0) {
            return 0.0;
        }
        
        return (getCurrentValue(localCurrency) - total) / total * 100.0;        
    }

    public double getGainLossPercentage(TransactionSummary transactionSummary) {
        if(transactionSummary.getTotal() == 0) return 0.0;
        
        return (getCurrentValue(transactionSummary) - transactionSummary.getTotal()) / transactionSummary.getTotal() * 100.0;        
    }
    
    public double getNetGainLossValue(TransactionSummary transactionSummary) {
        return getCurrentValue(transactionSummary) - transactionSummary.getNetTotal();
    }
    
    public double getNetGainLossPercentage(TransactionSummary transactionSummary) {
        if (transactionSummary.getTotal() == 0) return 0.0;
        
        return (getCurrentValue(transactionSummary) - transactionSummary.getNetTotal()) / transactionSummary.getNetTotal() * 100.0;        
    }
    
    public double getGainLossPercentage(Transaction transaction) {
        if (transaction.getTotal() == 0) return 0.0;
        
        return (getCurrentValue(transaction) - transaction.getTotal()) / transaction.getTotal() * 100.0;        
    }
    
    public double getNetGainLossValue(Transaction transaction) {
        return getCurrentValue(transaction) - transaction.getNetTotal();
    }
    
    public double getNetGainLossPercentage(Transaction transaction) {
        if (transaction.getNetTotal() == 0) return 0.0;
        
        return (getCurrentValue(transaction) - transaction.getNetTotal()) / transaction.getNetTotal() * 100.0;        
    }
    
    public double getNetGainLossValue(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        return getCurrentValue(localCurrency) - portfolio.getNetTotal(localCurrency);
    }
    
    public double getNetGainLossPercentage(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        
        double netTotal = portfolio.getNetTotal(localCurrency);
        if (netTotal == 0.0) {
            return 0.0;
        }
        
        return (getCurrentValue(localCurrency) - netTotal) / netTotal * 100.0;        
    }

    public double getCurrentValue(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        
        final int count = portfolio.getChildCount();
        
        double result = 0.0;
        
        for (int i = 0; i < count; i++) {
            Object o = portfolio.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
                    
            assert(transactionSummary.getChildCount() > 0);            
                        
            double currentValue = this.getCurrentValue(transactionSummary);
            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
            final Code code = transaction.getStockInfo().code;
            
            final double exchangeRate = org.yccheok.jstock.portfolio.Utils.getExchangeRate(portfolioRealTimeInfo, localCurrency, code);
                                    
            result += (currentValue * exchangeRate);
        }
        
        return result;

    }

    public double getNetPurchaseValue(Currency localCurrency) {
        return ((Portfolio)getRoot()).getNetTotal(localCurrency);
    }

    public double getPurchaseValue(Currency localCurrency) {
        return ((Portfolio)getRoot()).getTotal(localCurrency);
    }
    
    public double getPurchasePrice(TransactionSummary transactionSummary) {
        if (transactionSummary.getQuantity() == 0.0) {
            return 0.0;
        }
        
        return transactionSummary.getTotal() / transactionSummary.getQuantity();
    }
    
    public double getGainLossPrice(Transaction transaction) {
        return this.getCurrentPrice(transaction) - transaction.getPrice();
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

    public double getGainLossValue(Transaction transaction) {
        return getCurrentValue(transaction) - transaction.getTotal();
    }
    
    @Override
    public int getColumnCount() {
        assert(columnNames.length == cTypes.length);
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int column) {
        return BuyPortfolioTreeTableModelEx.cTypes[column];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];  
    }
    
    @Override
    public Object getValueAt(Object node, int column) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        final DecimalPlace decimalPlace = JStock.instance().getJStockOptions().getDecimalPlace();
        
        if (node instanceof Portfolio) {
            final Currency localCurrency = org.yccheok.jstock.portfolio.Utils.getLocalCurrency();

            final Portfolio portfolio = (Portfolio)node;
            
            switch(column) {
                case 0:
                    return GUIBundle.getString("PortfolioManagementJPanel_Buy");
        
                case 6:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(decimalPlace, portfolio.getNetTotal(localCurrency));
                    } else {
                        return new DoubleWrapper(decimalPlace, portfolio.getTotal(localCurrency));
                    }
                    
                case 7:
                    return new DoubleWrapper(decimalPlace, getCurrentValue(localCurrency));
                    
                case 8:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(decimalPlace, this.getNetGainLossValue(localCurrency));
                    } else {
                        return new DoubleWrapper(decimalPlace, this.getGainLossValue(localCurrency));
                    }                        
                    
                case 9:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlace.Two, this.getNetGainLossPercentage(localCurrency));
                    } else {
                        return new DoubleWrapper(DecimalPlace.Two, this.getGainLossPercentage(localCurrency));
                    }
    
                case 10:
                    return new DoubleWrapper(decimalPlace, portfolio.getBroker());
                    
                case 11:
                    return new DoubleWrapper(decimalPlace, portfolio.getClearingFee());
                    
                case 12:
                    return new DoubleWrapper(decimalPlace, portfolio.getStampDuty());
                    
                case 13:
                    return portfolio.getComment();
            }
        }
   
        if (node instanceof TransactionSummary) {
            final TransactionSummary transactionSummary = (TransactionSummary)node;
            
            if (transactionSummary.getChildCount() <= 0) return null;
            
            final Code code = ((Transaction)transactionSummary.getChildAt(0)).getStockInfo().code;
            
            final boolean shouldConvertPenceToPound = org.yccheok.jstock.portfolio.Utils.shouldConvertPenceToPound(portfolioRealTimeInfo, code);
            
            final boolean shouldDisplayCurrencyForValue = this.portfolioManagementJPanel.shouldDisplayCurrencyForValue(code);
            
            final Currency stockCurrency = shouldDisplayCurrencyForValue ? org.yccheok.jstock.portfolio.Utils.getStockCurrency(portfolioRealTimeInfo, code) : null;
            
            switch(column) {
                case 0:
                    return ((Transaction)transactionSummary.getChildAt(0)).getStockInfo().symbol;

                case 1:
                    return ((Transaction)transactionSummary.getChildAt(0)).getStockInfo().code;
                                        
                case 3:
                    return transactionSummary.getQuantity();
                    
                case 4:
                    return new DoubleWrapper(decimalPlace, this.getPurchasePrice(transactionSummary));
                    
                case 5:
                    return new DoubleWrapper(decimalPlace, this.getCurrentPrice(transactionSummary));
                    
                case 6:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getNetTotal());
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getTotal());
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getNetTotal() / 100.0);
                        } else {                        
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getTotal() / 100.0);
                        }
                    }
                    
                case 7:
                    if (shouldConvertPenceToPound == false) {
                        return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getCurrentValue(transactionSummary));
                    } else {
                        return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getCurrentValue(transactionSummary) / 100.0);
                    }                    
                    
                case 8:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getNetGainLossValue(transactionSummary));
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getGainLossValue(transactionSummary));
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getNetGainLossValue(transactionSummary) / 100.0);
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getGainLossValue(transactionSummary) / 100.0);
                        }
                    }
                    
                case 9:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlace.Two, this.getNetGainLossPercentage(transactionSummary));
                    } else {
                        return new DoubleWrapper(DecimalPlace.Two, this.getGainLossPercentage(transactionSummary));
                    }
                    
                case 10:
                    return new DoubleWrapper(decimalPlace, transactionSummary.getBroker());
                    
                case 11:
                    return new DoubleWrapper(decimalPlace, transactionSummary.getClearingFee());
                    
                case 12:
                    return new DoubleWrapper(decimalPlace, transactionSummary.getStampDuty());
                    
                case 13:
                    return transactionSummary.getComment();                    
            }
        }
        
        if (node instanceof Transaction) {
            final Transaction transaction = (Transaction)node;
            
            final Code code = transaction.getStockInfo().code;
            
            final boolean shouldConvertPenceToPound = org.yccheok.jstock.portfolio.Utils.shouldConvertPenceToPound(portfolioRealTimeInfo, code);
            
            final boolean shouldDisplayCurrencyInfoForValue = this.portfolioManagementJPanel.shouldDisplayCurrencyForValue(code);
            
            final Currency stockCurrency = shouldDisplayCurrencyInfoForValue ? org.yccheok.jstock.portfolio.Utils.getStockCurrency(portfolioRealTimeInfo, code) : null;

            switch(column) {
                case 0:
                    return (transaction).getStockInfo().symbol;

                case 1:
                    return (transaction).getStockInfo().code;
                                        
                case 2:
                    return transaction.getDate();
                    
                case 3:
                    return transaction.getQuantity();
                    
                case 4:
                    return transaction.getPrice();
                    
                case 5:
                    return this.getCurrentPrice(transaction);
                    
                case 6:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getNetTotal());
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getTotal());
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getNetTotal() / 100.0);
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getTotal() / 100.0);
                        }
                    }
                    
                case 7:
                    if (shouldConvertPenceToPound == false) {
                        return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getCurrentValue(transaction));
                    } else {
                        return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getCurrentValue(transaction) / 100.0);
                    }
                    
                case 8:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getNetGainLossValue(transaction));
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getGainLossValue(transaction));
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getNetGainLossValue(transaction) / 100.0);
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, this.getGainLossValue(transaction) / 100.0);
                        }
                    }
                    
                case 9:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlace.Two, this.getNetGainLossPercentage(transaction));
                    } else {
                        return new DoubleWrapper(DecimalPlace.Two, this.getGainLossPercentage(transaction));
                    }
                    
                case 10:
                    return new DoubleWrapper(decimalPlace, transaction.getBroker());
                    
                case 11:
                    return new DoubleWrapper(decimalPlace, transaction.getClearingFee());
                    
                case 12:
                    return new DoubleWrapper(decimalPlace, transaction.getStampDuty());
                    
                case 13:
                    return transaction.getComment();
            }
        }
        
        return null; 
    }

    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getType() == Contract.Type.Buy);
    }
}
