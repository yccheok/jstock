/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.Symbol;
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
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            final Dividend dividend = dividendSummary.get(i);
            final Stock stock = dividend.getStock();
            if (false == this.symbolToCode.containsValue(stock.getCode())) {
                this.symbolToCode.put(stock.getSymbol(), stock.getCode());
                strings.add(stock.getSymbol().toString());
            }
        }

        java.util.Collections.sort(strings);

        for (String string : strings) {
            this.jComboBox1.addItem(string);
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
            if (this.jComboBox1.getSelectedIndex() != 0) {
                final Code code = this.symbolToCode.get(Symbol.newInstance(this.jComboBox1.getSelectedItem().toString()));
                if (false == dividend.getStock().getCode().equals(code)) {
                    continue;
                }
            }

            final int year = dividend.getDate().getYear();
            if (m.containsKey(year)) {
                m.put(year, m.get(year) + dividend.getAmount());
            }
            else {
                m.put(year, dividend.getAmount());
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

        jPanel1.setLayout(new java.awt.BorderLayout(5, 5));

        jComboBox1.setModel(getComboBoxModel());
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox1, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-700)/2, (screenSize.height-500)/2, 700, 500);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        this.chartPanel.setChart(this.createBarChart(this.createDataset()));
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private final Map<Symbol, Code> symbolToCode = new java.util.HashMap<Symbol, Code>();
    private final ChartPanel chartPanel;
    private final DividendSummary dividendSummary;
    private static final NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance();
    static {
        // 0 decimal place, to save up some display area.
        currencyFormat.setMaximumFractionDigits(0);
        currencyFormat.setMinimumFractionDigits(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
