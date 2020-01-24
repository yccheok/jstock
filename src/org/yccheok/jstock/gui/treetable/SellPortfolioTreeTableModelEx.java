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
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.PortfolioManagementJPanel;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.DecimalPlace;
import org.yccheok.jstock.portfolio.DoubleWrapper;
import org.yccheok.jstock.portfolio.PortfolioRealTimeInfo;
/**
 *
 * @author yccheok
 */
public class SellPortfolioTreeTableModelEx extends AbstractPortfolioTreeTableModelEx {
    
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
    
    public SellPortfolioTreeTableModelEx() {
        super(Arrays.asList(columnNames));
    }
    
    // Names of the columns.
    private static final String[] columnNames;
    
    static {
        final String[] tmp = {
            GUIBundle.getString("PortfolioManagementJPanel_Stock"),
            GUIBundle.getString("PortfolioManagementJPanel_Code"),
            GUIBundle.getString("PortfolioManagementJPanel_Date"),
            GUIBundle.getString("PortfolioManagementJPanel_ReferenceDate"),
            GUIBundle.getString("PortfolioManagementJPanel_Units"),
            GUIBundle.getString("PortfolioManagementJPanel_SellingPrice"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchasePrice"),
            GUIBundle.getString("PortfolioManagementJPanel_SellingValue"),
            GUIBundle.getString("PortfolioManagementJPanel_PurchaseValue"),
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
    private static final Class[] cTypes = {
        TreeTableModel.class,
        Code.class,
        org.yccheok.jstock.engine.SimpleDate.class,
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
    
    public double getGainLossPercentage(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();

        double referenceTotal = portfolio.getReferenceTotal(localCurrency);
        if (referenceTotal == 0.0) {
            return 0.0;
        }
        
        return (portfolio.getTotal(localCurrency) - referenceTotal) / referenceTotal * 100.0;
    }
    
    public double getGainLossValue(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        return portfolio.getTotal(localCurrency) - portfolio.getReferenceTotal(localCurrency);
    }
    
    public double getSellingValue(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        return portfolio.getTotal(localCurrency);
    }
    
    public double getNetSellingValue(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        return portfolio.getNetTotal(localCurrency);
    }

    public double getNetGainLossPercentage(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();

        double netReferenceTotal = portfolio.getNetReferenceTotal(localCurrency);
        if (netReferenceTotal == 0.0) {
            return 0.0;
        }
        
        return (portfolio.getNetTotal(localCurrency) - netReferenceTotal) / netReferenceTotal * 100.0;
    }
    
    public double getNetGainLossValue(Currency localCurrency) {
        Portfolio portfolio = (Portfolio)getRoot();
        return portfolio.getNetTotal(localCurrency) - portfolio.getNetReferenceTotal(localCurrency);
    }
    
    public double getSellingPrice(TransactionSummary transactionSummary) {
        if(transactionSummary.getQuantity() == 0.0) return 0.0;
        
        return transactionSummary.getTotal() / transactionSummary.getQuantity();
    }
    
    public double getPurchasePrice(TransactionSummary transactionSummary) {
        if(transactionSummary.getQuantity() == 0.0) return 0.0;
        
        return transactionSummary.getReferenceTotal() / transactionSummary.getQuantity();
    }
    
    public double getGainLossValue(TransactionSummary transactionSummary) {
        return transactionSummary.getTotal() - transactionSummary.getReferenceTotal();
    }

    public double getGainLossPercentage(TransactionSummary transactionSummary) {
        if(transactionSummary.getReferenceTotal() == 0.0) return 0.0;

        return (transactionSummary.getTotal() - transactionSummary.getReferenceTotal()) / transactionSummary.getReferenceTotal() * 100.0;
    }
    
    public double getNetGainLossValue(TransactionSummary transactionSummary) {
        return transactionSummary.getNetTotal() - transactionSummary.getNetReferenceTotal();
    }

    public double getNetGainLossPercentage(TransactionSummary transactionSummary) {
        if (transactionSummary.getNetReferenceTotal() == 0.0) return 0.0;

        return (transactionSummary.getNetTotal() - transactionSummary.getNetReferenceTotal()) / transactionSummary.getNetReferenceTotal() * 100.0;
    }
    
    public double getGainLossPrice(Transaction transaction) {
        if (transaction.getQuantity() == 0.0) return 0.0;
        
        return ((transaction.getTotal() - transaction.getReferenceTotal()) / transaction.getQuantity());        
    }
    
    public double getGainLossValue(Transaction transaction) {
        return transaction.getTotal() - transaction.getReferenceTotal();
    }

    public double getGainLossPercentage(Transaction transaction) {
        if (transaction.getReferenceTotal() == 0.0) return 0.0;
        
        return (transaction.getTotal() - transaction.getReferenceTotal()) / transaction.getReferenceTotal() * 100.0;
    }

    public double getNetGainLossValue(Transaction transaction) {
        return transaction.getNetTotal() - transaction.getNetReferenceTotal();
    }

    public double getNetGainLossPercentage(Transaction transaction) {
        if (transaction.getNetReferenceTotal() == 0.0) return 0.0;
        
        return (transaction.getNetTotal() - transaction.getNetReferenceTotal()) / transaction.getNetReferenceTotal() * 100.0;
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
                    return GUIBundle.getString("PortfolioManagementJPanel_Sell");
        
                case 7:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(decimalPlace, portfolio.getNetTotal(localCurrency));
                    } else {
                        return new DoubleWrapper(decimalPlace, portfolio.getTotal(localCurrency));
                    }

                case 8:
                    // Total purchase value.
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(decimalPlace, portfolio.getNetReferenceTotal(localCurrency));
                    } else {
                        return new DoubleWrapper(decimalPlace, portfolio.getReferenceTotal(localCurrency));
                    }

                case 9:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(decimalPlace, getNetGainLossValue(localCurrency));
                    } else {
                        return new DoubleWrapper(decimalPlace, getGainLossValue(localCurrency));
                    }


                case 10:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlace.Two, getNetGainLossPercentage(localCurrency));
                    } else {
                        return new DoubleWrapper(DecimalPlace.Two, getGainLossPercentage(localCurrency));
                    }
    
                case 11:
                    return new DoubleWrapper(decimalPlace, portfolio.getBroker());
                    
                case 12:
                    return new DoubleWrapper(decimalPlace, portfolio.getClearingFee());
                    
                case 13:
                    return new DoubleWrapper(decimalPlace, portfolio.getStampDuty());
                    
                case 14:
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
                    
                case 4:
                    return transactionSummary.getQuantity();
                    
                case 5:
                    return new DoubleWrapper(decimalPlace, getSellingPrice(transactionSummary));
                    
                case 6:
                    return new DoubleWrapper(decimalPlace, getPurchasePrice(transactionSummary));
                    
                case 7:
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
    
                case 8:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getNetReferenceTotal());
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getReferenceTotal());
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getNetReferenceTotal() / 100.0);
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transactionSummary.getReferenceTotal() / 100.0);
                        }
                    }
                       
                case 9:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getNetGainLossValue(transactionSummary));
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getGainLossValue(transactionSummary));
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getNetGainLossValue(transactionSummary) / 100.0);
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getGainLossValue(transactionSummary) / 100.0);
                        }
                    }

                case 10:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlace.Two, getNetGainLossPercentage(transactionSummary));
                    } else {
                        return new DoubleWrapper(DecimalPlace.Two, getGainLossPercentage(transactionSummary));
                    }
                    
                case 11:
                    return new DoubleWrapper(decimalPlace, transactionSummary.getBroker());
                    
                case 12:
                    return new DoubleWrapper(decimalPlace, transactionSummary.getClearingFee());
                    
                case 13:
                    return new DoubleWrapper(decimalPlace, transactionSummary.getStampDuty());
                    
                case 14:
                    return transactionSummary.getComment();
            }
        }
        
        if (node instanceof Transaction) {
            final Transaction transaction = (Transaction)node;
            
            final Code code = transaction.getStockInfo().code;
            
            final boolean shouldConvertPenceToPound = org.yccheok.jstock.portfolio.Utils.shouldConvertPenceToPound(portfolioRealTimeInfo, code);
            
            final boolean shouldDisplayCurrencyForValue = this.portfolioManagementJPanel.shouldDisplayCurrencyForValue(code);
            
            final Currency stockCurrency = shouldDisplayCurrencyForValue ? org.yccheok.jstock.portfolio.Utils.getStockCurrency(portfolioRealTimeInfo, code) : null;

            switch(column) {
                case 0:
                    return (transaction).getStockInfo().symbol;

                case 1:
                    return (transaction).getStockInfo().code;
                    
                case 2:
                    return transaction.getDate();

                case 3:
                    return transaction.getReferenceDate();
                    
                case 4:
                    return transaction.getQuantity();
                    
                case 5:
                    return new DoubleWrapper(decimalPlace, transaction.getPrice());
                    
                case 6:
                    return new DoubleWrapper(decimalPlace, transaction.getReferencePrice());
                    
                case 7:
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

                case 8:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getNetReferenceTotal());
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getReferenceTotal());
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getNetReferenceTotal() / 100.0);
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, transaction.getReferenceTotal() / 100.0);
                        }
                    }
                    
                case 9:
                    if (shouldConvertPenceToPound == false) {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getNetGainLossValue(transaction));
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getGainLossValue(transaction));
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getNetGainLossValue(transaction) / 100.0);
                        } else {
                            return DoubleWithCurrency.create(stockCurrency, decimalPlace, getGainLossValue(transaction) / 100.0);
                        }
                    }
                                        
                case 10:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlace.Two, getNetGainLossPercentage(transaction));
                    } else {
                        return new DoubleWrapper(DecimalPlace.Two, getGainLossPercentage(transaction));
                    }
                    
                case 11:
                    return new DoubleWrapper(decimalPlace, transaction.getBroker());
                    
                case 12:
                    return new DoubleWrapper(decimalPlace, transaction.getClearingFee());
                    
                case 13:
                    return new DoubleWrapper(decimalPlace, transaction.getStampDuty());
                    
                case 14:
                    return transaction.getComment();
            }
        }
        
        return null;
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
    
    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getType() == Contract.Type.Sell);
    }

}
