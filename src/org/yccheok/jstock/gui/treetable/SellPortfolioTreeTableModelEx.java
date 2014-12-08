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
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
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
public class SellPortfolioTreeTableModelEx extends AbstractPortfolioTreeTableModelEx {
    
    public SellPortfolioTreeTableModelEx() {
        super(Arrays.asList(columnNames));
    }
    
    // Names of the columns.
    private static final String[] columnNames;
    
    static {
        final String[] tmp = {
            GUIBundle.getString("PortfolioManagementJPanel_Stock"),
            GUIBundle.getString("PortfolioManagementJPanel_Date"),
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
    
    private double getGainLossPercentage(Portfolio portfolio) {
        if(portfolio.getReferenceTotal() == 0.0) return 0.0;
        
        return (portfolio.getTotal() - portfolio.getReferenceTotal()) / portfolio.getReferenceTotal() * 100.0;
    }
    
    public double getGainLossValue() {
        return getGainLossValue((Portfolio)getRoot());
    }
    
    private double getGainLossValue(Portfolio portfolio) {
        return portfolio.getTotal() - portfolio.getReferenceTotal();
    }
    
    public double getSellingValue() {
        return ((Portfolio)getRoot()).getTotal();
    }
    
    public double getNetSellingValue() {
        return ((Portfolio)getRoot()).getNetTotal();
    }

    public double getGainLossPercentage() {
        return getGainLossPercentage((Portfolio)getRoot());
    }
    
    public double getNetGainLossPercentage() {
        return getNetGainLossPercentage((Portfolio)getRoot());
    }

    public double getNetGainLossValue() {
        return getNetGainLossValue((Portfolio)getRoot());
    }
    
    private double getNetGainLossPercentage(Portfolio portfolio) {
        if (portfolio.getNetReferenceTotal() == 0.0) return 0.0;
        
        return (portfolio.getNetTotal() - portfolio.getNetReferenceTotal()) / portfolio.getNetReferenceTotal() * 100.0;
    }
    
    private double getNetGainLossValue(Portfolio portfolio) {
        return portfolio.getNetTotal() - portfolio.getNetReferenceTotal();
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
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        final boolean isPenceToPoundConversionEnabled = jStockOptions.isPenceToPoundConversionEnabled();

        if (node instanceof Portfolio) {
            final Portfolio portfolio = (Portfolio)node;
            
            switch(column) {
                case 0:
                    return GUIBundle.getString("PortfolioManagementJPanel_Sell");
        
                case 5:
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return portfolio.getNetTotal();
                        } else {
                            return portfolio.getTotal();
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return portfolio.getNetTotal() / 100.0;
                        } else {
                            return portfolio.getTotal() / 100.0;
                        }
                    }

                case 6:
                    // Total purchase value.
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return portfolio.getNetReferenceTotal();
                        } else {
                            return portfolio.getReferenceTotal();
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return portfolio.getNetReferenceTotal() / 100.0;
                        } else {
                            return portfolio.getReferenceTotal() / 100.0;
                        }
                    }

                case 7:
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return getNetGainLossValue(portfolio);
                        } else {
                            return getGainLossValue(portfolio);
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return getNetGainLossValue(portfolio) / 100.0;
                        } else {
                            return getGainLossValue(portfolio) / 100.0;
                        }
                    }

                case 8:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlaces.Two, getNetGainLossPercentage(portfolio));
                    } else {
                        return new DoubleWrapper(DecimalPlaces.Two, getGainLossPercentage(portfolio));
                    }
    
                case 9:
                    return portfolio.getBroker();
                    
                case 10:
                    return portfolio.getClearingFee();
                    
                case 11:
                    return portfolio.getStampDuty();
                    
                case 12:
                    return portfolio.getComment();
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
                        return new DoubleWrapper(DecimalPlaces.Four, getSellingPrice(transactionSummary));
                    } else {
                        return new DoubleWrapper(DecimalPlaces.Three, getSellingPrice(transactionSummary));   
                    }
                    
                case 4:
                    if (JStock.getInstance().getJStockOptions().isFourDecimalPlacesEnabled()) {
                        return new DoubleWrapper(DecimalPlaces.Four, getPurchasePrice(transactionSummary));
                    } else {
                        return new DoubleWrapper(DecimalPlaces.Three, getPurchasePrice(transactionSummary));   
                    }                    
                    
                case 5:
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return transactionSummary.getNetTotal();
                        } else {
                            return transactionSummary.getTotal();
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return transactionSummary.getNetTotal() / 100.0;
                        } else {
                            return transactionSummary.getTotal() / 100.0;
                        }
                    }
    
                case 6:
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return transactionSummary.getNetReferenceTotal();
                        } else {
                            return transactionSummary.getReferenceTotal();
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return transactionSummary.getNetReferenceTotal() / 100.0;
                        } else {
                            return transactionSummary.getReferenceTotal() / 100.0;
                        }
                    }
                       
                case 7:
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return getNetGainLossValue(transactionSummary);
                        } else {
                            return getGainLossValue(transactionSummary);
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return getNetGainLossValue(transactionSummary) / 100.0;
                        } else {
                            return getGainLossValue(transactionSummary) / 100.0;
                        }
                    }

                case 8:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlaces.Two, getNetGainLossPercentage(transactionSummary));
                    } else {
                        return new DoubleWrapper(DecimalPlaces.Two, getGainLossPercentage(transactionSummary));
                    }
                    
                case 9:
                    return transactionSummary.getBroker();
                    
                case 10:
                    return transactionSummary.getClearingFee();
                    
                case 11:
                    return transactionSummary.getStampDuty();
                    
                case 12:
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
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return transaction.getNetTotal();
                        } else {
                            return transaction.getTotal();
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return transaction.getNetTotal() / 100.0;
                        } else {
                            return transaction.getTotal() / 100.0;
                        }
                    }                    

                case 6:
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return transaction.getNetReferenceTotal();
                        } else {
                            return transaction.getReferenceTotal();
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return transaction.getNetReferenceTotal() / 100.0;
                        } else {
                            return transaction.getReferenceTotal() / 100.0;
                        }
                    }
                    
                case 7:
                    if (isPenceToPoundConversionEnabled == false) {
                        if (isFeeCalculationEnabled) {
                            return getNetGainLossValue(transaction);
                        } else {
                            return getGainLossValue(transaction);
                        }
                    } else {
                        if (isFeeCalculationEnabled) {
                            return getNetGainLossValue(transaction) / 100.0;
                        } else {
                            return getGainLossValue(transaction) / 100.0;
                        }
                    }
                                        
                case 8:
                    if (isFeeCalculationEnabled) {
                        return new DoubleWrapper(DecimalPlaces.Two, getNetGainLossPercentage(transaction));
                    } else {
                        return new DoubleWrapper(DecimalPlaces.Two, getGainLossPercentage(transaction));
                    }
                    
                case 9:
                    return transaction.getBroker();
                    
                case 10:
                    return transaction.getClearingFee();
                    
                case 11:
                    return transaction.getStampDuty();
                    
                case 12:
                    return transaction.getComment();
            }
        }
        
        return null;
    }
    
    @Override
    public boolean isValidTransaction(Transaction transaction) {
        return (transaction.getType() == Contract.Type.Sell);
    }

}
