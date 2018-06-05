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

package org.yccheok.jstock.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Symbol;
import org.yccheok.jstock.gui.treetable.SellPortfolioTreeTableModelEx;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.Dividend;
import org.yccheok.jstock.portfolio.DividendSummary;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.PortfolioRealTimeInfo;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;

/**
 *
 * @author  yccheok
 */
public class SellPortfolioChartJDialog extends javax.swing.JDialog {
    
    private static final String[] cNames = {
        GUIBundle.getString("SellPortfolioTreeTableModel_GainValue"),
        GUIBundle.getString("SellPortfolioTreeTableModel_LossValue"),
        GUIBundle.getString("SellPortfolioTreeTableModel_GainPercentage"),
        GUIBundle.getString("SellPortfolioTreeTableModel_LossPercentage"),
        GUIBundle.getString("SellPortfolioTreeTableModel_Dividend"),
        GUIBundle.getString("SellPortfolioTreeTableModel_SellingValue"),
        GUIBundle.getString("SellPortfolioTreeTableModel_PurchaseValue"),
        GUIBundle.getString("SellPortfolioTreeTableModel_Units"),
        GUIBundle.getString("SellPortfolioTreeTableModel_Broker"),
        GUIBundle.getString("SellPortfolioTreeTableModel_StampDuty"),
        GUIBundle.getString("SellPortfolioTreeTableModel_ClearingFee")
    };
    
    /** Creates new form SellPortfolioChartJDialog */
    public SellPortfolioChartJDialog(java.awt.Frame parent, boolean modal, SellPortfolioTreeTableModelEx portfolioTreeTableModel, PortfolioRealTimeInfo portfolioRealTimeInfo, DividendSummary dividendSummary) {
        super(parent, GUIBundle.getString("SellPortfolioChartJDialog_SellSummary"), modal);

        this.initCodeToTotalDividend(dividendSummary);

        initComponents();
        
        this.portfolioTreeTableModel = portfolioTreeTableModel;
        this.portfolioRealTimeInfo = portfolioRealTimeInfo;
        
        final JFreeChart freeChart;
        final int lastSelectedSellPortfolioChartIndex = JStock.instance().getJStockOptions().getLastSelectedSellPortfolioChartIndex();
        if (lastSelectedSellPortfolioChartIndex < this.jComboBox1.getItemCount() && lastSelectedSellPortfolioChartIndex < cNames.length && lastSelectedSellPortfolioChartIndex >= 0) {            
            freeChart = createChart(cNames[lastSelectedSellPortfolioChartIndex]);
            // Put it in next queue, so that it won't trigger jComBox1's event
            // when this.chartPanel is not ready yet.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jComboBox1.setSelectedIndex(lastSelectedSellPortfolioChartIndex);
                }
            });
            
        } else {
            freeChart = createChart(cNames[0]);
        }                

        org.yccheok.jstock.charting.Utils.applyChartTheme(freeChart);

        chartPanel = new ChartPanel(freeChart, true, true, true, true, true);
        
        getContentPane().add(chartPanel, java.awt.BorderLayout.CENTER);        
    }

    private void initCodeToTotalDividend(DividendSummary dividendSummary)
    {
        final int size = dividendSummary.size();
        for (int i = 0; i < size; i++) {
            Dividend dividend = dividendSummary.get(i);
            Code code = dividend.stockInfo.code;
            Double value = this.codeToTotalDividend.get(code);
            if (value != null) {
                double total = value + dividend.amount;
                this.codeToTotalDividend.put(code, total);
            }
            else {
                this.codeToTotalDividend.put(code, dividend.amount);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        final JStockOptions jStockOptions = org.yccheok.jstock.gui.JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        Set<String> excludedStrings = new HashSet<String>();
        excludedStrings.add(GUIBundle.getString("SellPortfolioTreeTableModel_Broker"));
        excludedStrings.add(GUIBundle.getString("SellPortfolioTreeTableModel_StampDuty"));
        excludedStrings.add(GUIBundle.getString("SellPortfolioTreeTableModel_ClearingFee"));
        for(String cName : this.cNames) {
            if (isFeeCalculationEnabled == false) {
                if (excludedStrings.contains(cName)) {
                    continue;
                }
            }
            this.jComboBox1.addItem(cName);
        }
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox1);

        jPanel1.add(jPanel2, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(750, 600));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private static class DataEx implements Comparable<DataEx> {
        public final String data;
        public final double value;
        
        private DataEx(String data, double value) {
            this.data = data;
            this.value = value;
        }
        
        public static DataEx newInstance(String data, double value) {
            return new DataEx(data, value);
        }

        @Override
        public int compareTo(DataEx o) {
            return Double.compare(o.value, this.value);
        }
    }
    
    
    private JFreeChart createChart(String name) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        
        final Portfolio portfolio = (Portfolio)portfolioTreeTableModel.getRoot();
        final int count = portfolio.getChildCount();
        DefaultPieDataset data = new DefaultPieDataset();
        final List<DataEx> dataExs = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            TransactionSummary transactionSummary = (TransactionSummary)portfolio.getChildAt(i);
            
            if (transactionSummary.getChildCount() <= 0) {
                continue;
            }
            
            Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
            final Symbol symbol = transaction.getStockInfo().symbol;
            final Code code = transaction.getStockInfo().code;

            final boolean shouldConvertPenceToPound = org.yccheok.jstock.portfolio.Utils.shouldConvertPenceToPound(portfolioRealTimeInfo, code);
            
            /* Should use reflection technology. */
            if(name.equals(cNames[0])) {
                if (shouldConvertPenceToPound == false) {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), portfolioTreeTableModel.getNetGainLossValue(transactionSummary)));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), portfolioTreeTableModel.getGainLossValue(transactionSummary)));
                    }
                } else {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), portfolioTreeTableModel.getNetGainLossValue(transactionSummary) / 100.0));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), portfolioTreeTableModel.getGainLossValue(transactionSummary) / 100.0));
                    }                    
                }
            } else if(name.equals(cNames[1])) {
                if (shouldConvertPenceToPound == false) {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), -portfolioTreeTableModel.getNetGainLossValue(transactionSummary)));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), -portfolioTreeTableModel.getGainLossValue(transactionSummary)));
                    }
                } else {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), -portfolioTreeTableModel.getNetGainLossValue(transactionSummary) / 100.0));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), -portfolioTreeTableModel.getGainLossValue(transactionSummary) / 100.0));
                    }
                }
            } else if(name.equals(cNames[2])) {
                if (isFeeCalculationEnabled) {
                    dataExs.add(DataEx.newInstance(symbol.toString(), portfolioTreeTableModel.getNetGainLossPercentage(transactionSummary)));
                } else {
                    dataExs.add(DataEx.newInstance(symbol.toString(), portfolioTreeTableModel.getGainLossPercentage(transactionSummary)));
                }
            } else if(name.equals(cNames[3])) {
                if (isFeeCalculationEnabled) {
                    dataExs.add(DataEx.newInstance(symbol.toString(), -portfolioTreeTableModel.getNetGainLossPercentage(transactionSummary)));
                } else {
                    dataExs.add(DataEx.newInstance(symbol.toString(), -portfolioTreeTableModel.getGainLossPercentage(transactionSummary)));
                }
            } else if(name.equals(cNames[4])) {
                Double value = this.codeToTotalDividend.get(code);
                if (value != null) {
                    if (value.doubleValue() > 0.0) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), this.codeToTotalDividend.get(code)));
                    }
                }
            } else if(name.equals(cNames[5])) {
                if (shouldConvertPenceToPound == false) {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getNetTotal()));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getTotal()));
                    }
                } else {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getNetTotal() / 100.0));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getTotal() / 100.0));
                    }                    
                }
            } else if(name.equals(cNames[6])) {
                if (shouldConvertPenceToPound == false) {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getNetReferenceTotal()));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getReferenceTotal()));
                    }
                } else {
                    if (isFeeCalculationEnabled) {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getNetReferenceTotal() / 100.0));
                    } else {
                        dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getReferenceTotal() / 100.0));
                    }                    
                }
            } else if(name.equals(cNames[7])) {
                dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getQuantity()));                
            } else if(name.equals(cNames[8])) {
                dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getBroker()));                
            } else if(name.equals(cNames[9])) {
                dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getStampDuty()));                
            } else if(name.equals(cNames[10])) {
                dataExs.add(DataEx.newInstance(symbol.toString(), transactionSummary.getClearingFee()));                
            }            
        }
        
        Collections.sort(dataExs);
        
        for (DataEx dataEx : dataExs) {
            // Selling value can be negative sometimes.
            if (dataEx.value <= 0) {
                continue;
            }
            
            data.setValue(dataEx.data, dataEx.value);
        }
        
        // create a chart...
        return ChartFactory.createPieChart(name, data, true, true, true);        
    }
    
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        String selected = ((javax.swing.JComboBox)evt.getSource()).getSelectedItem().toString();
        final int selectedIndex = ((javax.swing.JComboBox)evt.getSource()).getSelectedIndex();
        JStock.instance().getJStockOptions().setLastSelectedSellPortfolioChartIndex(selectedIndex);
        final JFreeChart freeChart = this.createChart(selected);
        org.yccheok.jstock.charting.Utils.applyChartTheme(freeChart);
        chartPanel.setChart(freeChart);
    }//GEN-LAST:event_jComboBox1ActionPerformed
    
    private final SellPortfolioTreeTableModelEx portfolioTreeTableModel;
    private final PortfolioRealTimeInfo portfolioRealTimeInfo;
    private final ChartPanel chartPanel;
    private final Map<Code, Double> codeToTotalDividend = new HashMap<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    
}
