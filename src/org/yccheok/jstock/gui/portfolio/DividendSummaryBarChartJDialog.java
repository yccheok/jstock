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

package org.yccheok.jstock.gui.portfolio;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ComboBoxModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.UIOptions;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.Dividend;
import org.yccheok.jstock.portfolio.DividendSummary;

/**
 *
 * @author yccheok
 */
public class DividendSummaryBarChartJDialog extends javax.swing.JDialog {

    /** Creates new form DividendSummaryBarChartJDialog */
    public DividendSummaryBarChartJDialog(java.awt.Dialog parent, boolean modal, DividendSummary dividendSummary) {
        super(parent, modal);
        
        initComponents();
        
        Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.DIVIDEND_SUMMARY_BAR_CHART_JDIALOG);
        if (dimension != null) {
            setSize(dimension);
        }
        
        /* Sequence is important.
         * (1) Initialize dividendSummary.
         * (2) Initialize combo box.
         * (3) Initialize chart.
         */
        this.dividendSummary = dividendSummary;
        this.initJComboBox();
        final JFreeChart freeChart = this.createBarChart(this.createDataset());
        chartPanel = new ChartPanel(freeChart, true, true, true, true, true);
        getContentPane().add(chartPanel, java.awt.BorderLayout.CENTER);
    }

    /** Creates new form DividendSummaryBarChartJDialog */
    public DividendSummaryBarChartJDialog(java.awt.Frame parent, boolean modal, DividendSummary dividendSummary) {
        super(parent, modal);
        
        initComponents();
        
        Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.DIVIDEND_SUMMARY_BAR_CHART_JDIALOG);
        if (dimension != null) {
            setSize(dimension);
        }
        
        /* Sequence is important.
         * (1) Initialize dividendSummary.
         * (2) Initialize combo box.
         * (3) Initialize chart.
         */
        this.dividendSummary = dividendSummary;
        this.initJComboBox();
        final JFreeChart freeChart = this.createBarChart(this.createDataset());
        chartPanel = new ChartPanel(freeChart, true, true, true, true, true);
        getContentPane().add(chartPanel, java.awt.BorderLayout.CENTER);
    }

    private void initJComboBox() {
        final int size = dividendSummary.size();
        Set<Code> codes = new HashSet<Code>();
        for (int i = size - 1; i >= 0; i--) {
            final Dividend dividend = dividendSummary.get(i);
            final StockInfo stockInfo = dividend.stockInfo;      
            // We do not perform duplication detection through stock info
            // comparison. This is because starting from version 1.0.6s, we enable
            // user to change buy transaction's stock symbol. Hence, user may
            // have dividend records with same stock code, but different stock
            // symbol. We will loop from backward, and take the first detected 
            // code.
            if (false == codes.add(stockInfo.code)) {
                continue;
            }

            stockInfos.add(stockInfo);
        }

        // Ensure symbols are in alphabetical order.
        java.util.Collections.sort(stockInfos, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                return ((StockInfo)o1).symbol.toString().compareTo(((StockInfo)o2).symbol.toString());
            }

        });

        for (StockInfo stockInfo : stockInfos) {
            this.jComboBox1.addItem(stockInfo.symbol.toString());
        }
    }

    private ComboBoxModel getComboBoxModel() {
        return new javax.swing.DefaultComboBoxModel(new String[] { GUIBundle.getString("DividendSummaryBarChartJDialog_AllStock(s)") });
    }

    private CategoryDataset createDataset() {
        /* Year to Dividend */
        final Map<Integer, Double> m = new HashMap<Integer, Double>();
        final int size = this.dividendSummary.size();

        for (int i = 0; i < size; i++) {
            final Dividend dividend = this.dividendSummary.get(i);

            // There might be newly added empty records in dividendSummary.
            // Ignore them.
            if (dividend.amount <= 0) {
                continue;
            }

            int selectedIndex = this.jComboBox1.getSelectedIndex();
            if (selectedIndex != 0) {
                // selectedIndex - 1, as the first item in combo box is "All Stock(s)".
                final Code code = this.stockInfos.get(selectedIndex - 1).code;
                if (false == dividend.stockInfo.code.equals(code)) {
                    continue;
                }
            }

            final int year = dividend.date.getYear();
            if (m.containsKey(year)) {
                m.put(year, m.get(year) + dividend.amount);
            }
            else {
                m.put(year, dividend.amount);
            }
        }

        List<Integer> list = new ArrayList<Integer>(m.keySet());
        java.util.Collections.sort(list);

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int year : list) {
            final String category = "" + year;
            final String series = this.jComboBox1.getSelectedItem().toString();
            dataset.addValue(m.get(year), series, category);
        }

        return dataset;
    }

    private JFreeChart createBarChart(CategoryDataset dataset) {
        final int size = ((DefaultCategoryDataset)dataset).getColumnCount();
        double total = 0.0;
        for (int i = 0; i < size; i++) {
            total += ((DefaultCategoryDataset)dataset).getValue(0, i).doubleValue();
        }

        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final String currencySymbol = jStockOptions.getCurrencySymbol(jStockOptions.getCountry());
        // Use apostrophes to escape currencySymbol. If currencySymbol contains
        // apostrophes, we may need to escape those by doubling them.
        final NumberFormat currencyFormat = new DecimalFormat("'" + currencySymbol.replace("'", "''") + "'#,##0");

        final String title = MessageFormat.format(org.yccheok.jstock.internationalization.GUIBundle.getString("DividendSummaryBarChartJDialog_DividendByYear_template"), this.jComboBox1.getSelectedItem(), currencyFormat.format(total));
        final String domain_label = org.yccheok.jstock.internationalization.GUIBundle.getString("DividendSummaryBarChartJDialog_Year");
        final String range_label = org.yccheok.jstock.internationalization.GUIBundle.getString("DividendSummaryBarChartJDialog_Dividend");
        // create the chart...
        final JFreeChart freeChart = ChartFactory.createBarChart(
            title,                      // chart title
            domain_label,               // domain axis label
            range_label,                // range axis label
            dataset,                    // data
            PlotOrientation.VERTICAL,   // orientation
            false,                      // include legend
            true,                       // tooltips?
            false                       // URLs?
        );
        org.yccheok.jstock.charting.Utils.applyChartTheme(freeChart);
        NumberAxis rangeAxis1 = (NumberAxis) ((CategoryPlot)freeChart.getPlot()).getRangeAxis();
        rangeAxis1.setNumberFormatOverride(currencyFormat);
        return freeChart;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("DividendSummaryBarChartJDialog_DividendByYear")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout(5, 5));

        jComboBox1.setModel(getComboBoxModel());
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox1, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(700, 500));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        this.chartPanel.setChart(this.createBarChart(this.createDataset()));
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        JStock.instance().getUIOptions().setDimension(UIOptions.DIVIDEND_SUMMARY_BAR_CHART_JDIALOG, getSize());
    }//GEN-LAST:event_formWindowClosed

    private final List<StockInfo> stockInfos = new ArrayList<StockInfo>();
    private final ChartPanel chartPanel;
    private final DividendSummary dividendSummary;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
