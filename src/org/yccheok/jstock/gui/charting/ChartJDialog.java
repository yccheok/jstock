/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui.charting;

import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.io.File;
import org.yccheok.jstock.engine.*;

import java.util.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.*;
import org.yccheok.jstock.charting.TechnicalAnalysis;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;

/**
 *
 * @author  yccheok
 */
public class ChartJDialog extends javax.swing.JDialog {
    public enum TA {
        SMA,
        CCI,
        RSI,
        EMA,
        MFI
    }

    public enum Mode {
        PriceVolume,
        Candlestick
    }

    public enum Zoom {
        Days7,
        Month1,
        Months3,
        Months6,
        Year1,
        All
    }

    private Mode getCurrentMode() {
        final String selected = this.jComboBox1.getSelectedItem().toString();

        if (selected.equals("Price Volume")) {
            return Mode.PriceVolume;
        }
        else if(selected.equals("Candlestick")) {
            return Mode.Candlestick;
        }
        assert(false);
        return null;
    }

    /** Creates new form ChartJDialog */
    public ChartJDialog(java.awt.Frame parent, String title, boolean modal, StockHistoryServer stockHistoryServer) {
        super(parent, title, modal);
                
        initComponents();
        
        this.stockHistoryServer = stockHistoryServer;
        
        this.priceTimeSeries = this.getPriceTimeSeries(stockHistoryServer);
        this.priceDataset = new TimeSeriesCollection(this.priceTimeSeries);
        this.priceOHLCDataset = this.getOHLCDataset(stockHistoryServer);
        this.volumeDataset = this.getVolumeDataset(stockHistoryServer);      
        
        this.priceVolumeChart = this.createPriceVolumeChart(stockHistoryServer);
        
        this.chartPanel = new ChartPanel(this.priceVolumeChart, true, true, true, true, true);

        final org.jdesktop.jxlayer.JXLayer<ChartPanel> layer = new org.jdesktop.jxlayer.JXLayer<ChartPanel>(this.chartPanel);
        this.chartLayerUI = new ChartLayerUI<ChartPanel>(this);
        layer.setUI(this.chartLayerUI);

        getContentPane().add(layer, java.awt.BorderLayout.CENTER);

        // Handle resize.
        this.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e) {
                ChartJDialog.this.chartLayerUI.updateTraceInfos();
            }
        });

        /* Update the high low labels. */
        this.updateHighLowJLabels();

        /* Restores previous chart dialog options. */
        this.loadChartJDialogOptions();
    }

    /**
     * Restores previous chart dialog options.
     */
    private void loadChartJDialogOptions() {
        final ChartJDialogOptions chartJDialogOptions = MainFrame.getInstance().getChartJDialogOptions();
        /* Are we in price volume or candlestick chart? */
        this.changeMode(chartJDialogOptions.getMode());
    }

    public StockHistoryServer getStockHistoryServer() {
        return this.stockHistoryServer;
    }

    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jLabel10 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jLabel11 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jLabel12 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jLabel13 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jLabel14 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem3 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem4 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem5 = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jCheckBoxMenuItem16 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem17 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem18 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem19 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem20 = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jCheckBoxMenuItem21 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem22 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem23 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem24 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem25 = new javax.swing.JCheckBoxMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jCheckBoxMenuItem6 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem7 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem8 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem9 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem10 = new javax.swing.JCheckBoxMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jCheckBoxMenuItem11 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem12 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem13 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem14 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem15 = new javax.swing.JCheckBoxMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 2, 5));
        jPanel4.setLayout(new java.awt.BorderLayout(5, 5));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 160));
        jPanel2.setPreferredSize(new java.awt.Dimension(120, 33));

        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 0, 5));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        jLabel1.setText(bundle.getString("ChartJDialog_High")); // NOI18N
        jPanel1.add(jLabel1);

        jLabel3.setText(bundle.getString("ChartJDialog_Low")); // NOI18N
        jPanel1.add(jLabel3);

        jPanel3.setLayout(new java.awt.GridLayout(2, 1, 0, 5));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setForeground(JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR);
        jPanel3.add(jLabel2);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setForeground(JStockOptions.DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR);
        jPanel3.add(jLabel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel2, java.awt.BorderLayout.WEST);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Price Volume", "Candlestick" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel5.add(jComboBox1);

        jPanel4.add(jPanel5, java.awt.BorderLayout.EAST);

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        jLabel9.setText("<html> <a href=\"\">7 Days</a> </html>");
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel9);

        jLabel10.setText("<html>\n<a href=\"\">1 Month</a>\n</html>");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel10);

        jLabel11.setText("<html>\n<a href=\"\">3 Months</a>\n</html>");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel11);

        jLabel12.setText("<html>\n<a href=\"\">6 Months</a>\n</html>");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel12);

        jLabel13.setText("<html>\n<a href=\"\">1 Year</a>\n</html>");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel13);

        jLabel14.setText("<html>\n    <a href=\"\">All</a>\n</html>");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel14);

        jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jMenu1.setText("File");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filesave.png"))); // NOI18N
        jMenuItem1.setText("Save As...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Technical Analysis");

        jMenu3.setText(bundle.getString("ChartJDialog_SMA")); // NOI18N
        jMenu3.setToolTipText(bundle.getString("ChartJDialog_SimpleMovingAverage")); // NOI18N

        jCheckBoxMenuItem1.setText(bundle.getString("ChartJDialog_20Days")); // NOI18N
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem1);

        jCheckBoxMenuItem2.setText(bundle.getString("ChartJDialog_30Days")); // NOI18N
        jCheckBoxMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem2);

        jCheckBoxMenuItem3.setText(bundle.getString("ChartJDialog_50Days")); // NOI18N
        jCheckBoxMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem3);

        jCheckBoxMenuItem4.setText(bundle.getString("ChartJDialog_100Days")); // NOI18N
        jCheckBoxMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem4);

        jCheckBoxMenuItem5.setText(bundle.getString("ChartJDialog_200Days")); // NOI18N
        jCheckBoxMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem5);
        jMenu3.add(jSeparator2);

        jMenuItem3.setText(bundle.getString("ChartJDialog_Custom...")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenu2.add(jMenu3);

        jMenu6.setText(bundle.getString("ChartJDialog_EMA")); // NOI18N
        jMenu6.setToolTipText(bundle.getString("ChartJDialog_ExponentialMovingAverage")); // NOI18N

        jCheckBoxMenuItem16.setText(bundle.getString("ChartJDialog_20Days")); // NOI18N
        jCheckBoxMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem16ActionPerformed(evt);
            }
        });
        jMenu6.add(jCheckBoxMenuItem16);

        jCheckBoxMenuItem17.setText(bundle.getString("ChartJDialog_30Days")); // NOI18N
        jCheckBoxMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem17ActionPerformed(evt);
            }
        });
        jMenu6.add(jCheckBoxMenuItem17);

        jCheckBoxMenuItem18.setText(bundle.getString("ChartJDialog_50Days")); // NOI18N
        jCheckBoxMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem18ActionPerformed(evt);
            }
        });
        jMenu6.add(jCheckBoxMenuItem18);

        jCheckBoxMenuItem19.setText(bundle.getString("ChartJDialog_100Days")); // NOI18N
        jCheckBoxMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem19ActionPerformed(evt);
            }
        });
        jMenu6.add(jCheckBoxMenuItem19);

        jCheckBoxMenuItem20.setText(bundle.getString("ChartJDialog_200Days")); // NOI18N
        jCheckBoxMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem20ActionPerformed(evt);
            }
        });
        jMenu6.add(jCheckBoxMenuItem20);
        jMenu6.add(jSeparator5);

        jMenuItem5.setText(bundle.getString("ChartJDialog_Custom...")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem5);

        jMenu2.add(jMenu6);

        jMenu7.setText(bundle.getString("ChartJDialog_MFI")); // NOI18N
        jMenu7.setToolTipText(bundle.getString("ChartJDialog_MoneyFlowIndex")); // NOI18N

        jCheckBoxMenuItem21.setText(bundle.getString("ChartJDialog_20Days")); // NOI18N
        jCheckBoxMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem21ActionPerformed(evt);
            }
        });
        jMenu7.add(jCheckBoxMenuItem21);

        jCheckBoxMenuItem22.setText(bundle.getString("ChartJDialog_30Days")); // NOI18N
        jCheckBoxMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem22ActionPerformed(evt);
            }
        });
        jMenu7.add(jCheckBoxMenuItem22);

        jCheckBoxMenuItem23.setText(bundle.getString("ChartJDialog_50Days")); // NOI18N
        jCheckBoxMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem23ActionPerformed(evt);
            }
        });
        jMenu7.add(jCheckBoxMenuItem23);

        jCheckBoxMenuItem24.setText(bundle.getString("ChartJDialog_100Days")); // NOI18N
        jCheckBoxMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem24ActionPerformed(evt);
            }
        });
        jMenu7.add(jCheckBoxMenuItem24);

        jCheckBoxMenuItem25.setText(bundle.getString("ChartJDialog_200Days")); // NOI18N
        jCheckBoxMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem25ActionPerformed(evt);
            }
        });
        jMenu7.add(jCheckBoxMenuItem25);
        jMenu7.add(jSeparator6);

        jMenuItem6.setText(bundle.getString("ChartJDialog_Custom...")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem6);

        jMenu2.add(jMenu7);

        jMenu5.setText(bundle.getString("ChartJDialog_RSI")); // NOI18N
        jMenu5.setToolTipText(bundle.getString("ChartJDialog_RelativeStrengthIndex")); // NOI18N

        jCheckBoxMenuItem6.setText(bundle.getString("ChartJDialog_20Days")); // NOI18N
        jCheckBoxMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem6ActionPerformed(evt);
            }
        });
        jMenu5.add(jCheckBoxMenuItem6);

        jCheckBoxMenuItem7.setText(bundle.getString("ChartJDialog_30Days")); // NOI18N
        jCheckBoxMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem7ActionPerformed(evt);
            }
        });
        jMenu5.add(jCheckBoxMenuItem7);

        jCheckBoxMenuItem8.setText(bundle.getString("ChartJDialog_50Days")); // NOI18N
        jCheckBoxMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem8ActionPerformed(evt);
            }
        });
        jMenu5.add(jCheckBoxMenuItem8);

        jCheckBoxMenuItem9.setText(bundle.getString("ChartJDialog_100Days")); // NOI18N
        jCheckBoxMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem9ActionPerformed(evt);
            }
        });
        jMenu5.add(jCheckBoxMenuItem9);

        jCheckBoxMenuItem10.setText(bundle.getString("ChartJDialog_200Days")); // NOI18N
        jCheckBoxMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jCheckBoxMenuItem10);
        jMenu5.add(jSeparator3);

        jMenuItem9.setText(bundle.getString("ChartJDialog_Custom...")); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem9);

        jMenu2.add(jMenu5);

        jMenu4.setText(bundle.getString("ChartJDialog_CCI")); // NOI18N
        jMenu4.setToolTipText(bundle.getString("ChartJDialog_CommodityChannelIndex")); // NOI18N

        jCheckBoxMenuItem11.setText(bundle.getString("ChartJDialog_20Days")); // NOI18N
        jCheckBoxMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem11ActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckBoxMenuItem11);

        jCheckBoxMenuItem12.setText(bundle.getString("ChartJDialog_30Days")); // NOI18N
        jCheckBoxMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem12ActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckBoxMenuItem12);

        jCheckBoxMenuItem13.setText(bundle.getString("ChartJDialog_50Days")); // NOI18N
        jCheckBoxMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem13ActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckBoxMenuItem13);

        jCheckBoxMenuItem14.setText(bundle.getString("ChartJDialog_100Days")); // NOI18N
        jCheckBoxMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem14ActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckBoxMenuItem14);

        jCheckBoxMenuItem15.setText(bundle.getString("ChartJDialog_200Days")); // NOI18N
        jCheckBoxMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem15ActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckBoxMenuItem15);
        jMenu4.add(jSeparator4);

        jMenuItem4.setText(bundle.getString("ChartJDialog_Custom...")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenu2.add(jMenu4);

        jMenuItem8.setText(bundle.getString("ChartJDialog_Stochastic...")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);
        jMenu2.add(jSeparator1);

        jMenuItem2.setText(bundle.getString("ChartJDialog_ClearAll")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        jMenu8.setText(bundle.getString("ChartJDialog_Help")); // NOI18N

        jMenuItem7.setText(bundle.getString("ChartJDialog_TechnicalAnalysisTutorial")); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem7);

        jMenuBar1.add(jMenu8);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-750)/2, (screenSize.height-600)/2, 750, 600);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Changes the mode (price volume or candlestick chart) of this chart dialog.
     * @param the mode (price volume or candlestick chart) of this chart dialog to be changed.
     */
    private void changeMode(Mode mode) {
        if (mode == Mode.PriceVolume) {
            if (this.priceVolumeChart == null) {
                this.priceVolumeChart = this.createPriceVolumeChart(stockHistoryServer);
            }
            chartPanel.setChart(this.priceVolumeChart);
            this.chartLayerUI.updateTraceInfos();
            /* Update high low labels as well. */
            this.updateHighLowJLabels();
            /* Reset all day labels. */
            this.resetAllDayLabels();
        }
        else if (mode == Mode.Candlestick) {
            if (this.candlestickChart == null) {
                this.candlestickChart = this.createCandlestickChart(stockHistoryServer);
            }
            chartPanel.setChart(this.candlestickChart);
            this.chartLayerUI.updateTraceInfos();
            /* Update high low labels as well. */
            this.updateHighLowJLabels();
            /* Reset all day labels. */
            this.resetAllDayLabels();
        }

        if (this.getCurrentMode() != mode) {
            if (mode == Mode.PriceVolume) {
                this.jComboBox1.setSelectedIndex(0);
            }
            else if (mode == Mode.Candlestick) {
                this.jComboBox1.setSelectedIndex(1);
            }
        }

        /* Remember the setting. */
        MainFrame.getInstance().getChartJDialogOptions().setMode(mode);
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        changeMode(this.getCurrentMode());
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        assert(this.stockHistoryServer.getNumOfCalendar() > 0);
        final Stock stock = this.stockHistoryServer.getStock(this.stockHistoryServer.getCalendar(0));
        final File file = org.yccheok.jstock.gui.Utils.promptSaveCSVAndExcelJFileChooser(stock.getCode().toString());

        if (file != null) {
            if (org.yccheok.jstock.gui.Utils.getFileExtension(file).equals("csv"))
            {
                final Statements statements = Statements.newInstanceFromStockHistoryServer(stockHistoryServer);
                statements.saveAsCSVFile(file);
            }
            else if (org.yccheok.jstock.gui.Utils.getFileExtension(file).equals("xls"))
            {
                final Statements statements = Statements.newInstanceFromStockHistoryServer(stockHistoryServer);
                statements.saveAsExcelFile(file, stock.getCode().toString());
            }
            else
            {
                assert(false);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateSMA(20, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void jCheckBoxMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem2ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateSMA(30, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem2ActionPerformed

    private void jCheckBoxMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem3ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateSMA(50, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem3ActionPerformed

    private void jCheckBoxMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem4ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateSMA(100, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem4ActionPerformed

    private void jCheckBoxMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem5ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateSMA(200, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem5ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < this.activeTAExs.size(); i++) {
            final TAEx taEx = this.activeTAExs.get(i);
            if (taEx.ta == TA.SMA) {
                this.updateSMA((Integer)taEx.parameter, false);
                i--;
            }
            else if (taEx.ta == TA.RSI) {
                this.updateRSI((Integer)taEx.parameter, false);
                i--;
            }
            else if (taEx.ta == TA.CCI) {
                this.updateCCI((Integer)taEx.parameter, false);
                i--;
            }
            else if (taEx.ta == TA.EMA) {
                this.updateEMA((Integer)taEx.parameter, false);
                i--;
            }
            else if (taEx.ta == TA.MFI) {
                this.updateMFI((Integer)taEx.parameter, false);
                i--;
            }
            else {
                assert(false);
            }
        }
        this.jCheckBoxMenuItem1.setSelected(false);
        this.jCheckBoxMenuItem2.setSelected(false);
        this.jCheckBoxMenuItem3.setSelected(false);
        this.jCheckBoxMenuItem4.setSelected(false);
        this.jCheckBoxMenuItem5.setSelected(false);

        this.jCheckBoxMenuItem6.setSelected(false);
        this.jCheckBoxMenuItem7.setSelected(false);
        this.jCheckBoxMenuItem8.setSelected(false);
        this.jCheckBoxMenuItem9.setSelected(false);
        this.jCheckBoxMenuItem10.setSelected(false);

        this.jCheckBoxMenuItem11.setSelected(false);
        this.jCheckBoxMenuItem12.setSelected(false);
        this.jCheckBoxMenuItem13.setSelected(false);
        this.jCheckBoxMenuItem14.setSelected(false);
        this.jCheckBoxMenuItem15.setSelected(false);

        this.jCheckBoxMenuItem16.setSelected(false);
        this.jCheckBoxMenuItem17.setSelected(false);
        this.jCheckBoxMenuItem18.setSelected(false);
        this.jCheckBoxMenuItem19.setSelected(false);
        this.jCheckBoxMenuItem20.setSelected(false);

        this.jCheckBoxMenuItem21.setSelected(false);
        this.jCheckBoxMenuItem22.setSelected(false);
        this.jCheckBoxMenuItem23.setSelected(false);
        this.jCheckBoxMenuItem24.setSelected(false);
        this.jCheckBoxMenuItem25.setSelected(false);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
       do {
            final String days_string = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_please_enter_number_of_days_for_moving_average"));
            if (days_string == null) {
                return;
            }
            try {
                final int days = Integer.parseInt(days_string);
                if (days <= 0) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                this.updateSMA(days, true);
                return;
            }
            catch (java.lang.NumberFormatException exp) {
                log.error(null, exp);
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                continue;
            }
        } while(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jCheckBoxMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem6ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateRSI(20, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem6ActionPerformed

    private void jCheckBoxMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem7ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateRSI(30, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem7ActionPerformed

    private void jCheckBoxMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem8ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateRSI(50, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem8ActionPerformed

    private void jCheckBoxMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem9ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateRSI(100, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem9ActionPerformed

    private void jCheckBoxMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem10ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateRSI(200, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem10ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
       do {
            final String days_string = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_please_enter_number_of_days_for_moving_average"));
            if (days_string == null) {
                return;
            }
            try {
                final int days = Integer.parseInt(days_string);
                if (days <= 0) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                this.updateRSI(days, true);
                return;
            }
            catch (java.lang.NumberFormatException exp) {
                log.error(null, exp);
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                continue;
            }
        } while(true);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jCheckBoxMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem11ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateCCI(20, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem11ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
      do {
            final String days_string = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_please_enter_number_of_days_for_moving_average"));
            if (days_string == null) {
                return;
            }
            try {
                final int days = Integer.parseInt(days_string);
                if (days <= 0) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                this.updateCCI(days, true);
                return;
            }
            catch (java.lang.NumberFormatException exp) {
                log.error(null, exp);
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                continue;
            }
        } while(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jCheckBoxMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem15ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateCCI(200, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem15ActionPerformed

    private void jCheckBoxMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem14ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateCCI(100, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem14ActionPerformed

    private void jCheckBoxMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem13ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateCCI(50, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem13ActionPerformed

    private void jCheckBoxMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem12ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateCCI(30, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem12ActionPerformed

    private void jCheckBoxMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem16ActionPerformed
        // TODO add your handling code here:
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateEMA(20, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem16ActionPerformed

    private void jCheckBoxMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem17ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateEMA(30, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem17ActionPerformed

    private void jCheckBoxMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem18ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateEMA(50, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem18ActionPerformed

    private void jCheckBoxMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem19ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateEMA(100, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem19ActionPerformed

    private void jCheckBoxMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem20ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        updateEMA(200, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem20ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
       do {
            final String days_string = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_please_enter_number_of_days_for_moving_average"));
            if (days_string == null) {
                return;
            }
            try {
                final int days = Integer.parseInt(days_string);
                if (days <= 0) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                this.updateEMA(days, true);
                return;
            }
            catch (java.lang.NumberFormatException exp) {
                log.error(null, exp);
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                continue;
            }
        } while(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        org.yccheok.jstock.gui.Utils.launchWebBrowser("http://jstock.sourceforge.net/ma_indicator.html?utm_source=jstock&utm_medium=chart_dialog");
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jCheckBoxMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem21ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateMFI(20, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem21ActionPerformed

    private void jCheckBoxMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem22ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateMFI(30, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem22ActionPerformed

    private void jCheckBoxMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem23ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateMFI(50, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem23ActionPerformed

    private void jCheckBoxMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem24ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateMFI(100, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem24ActionPerformed

    private void jCheckBoxMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem25ActionPerformed
        final JCheckBoxMenuItem menu = (JCheckBoxMenuItem)evt.getSource();
        this.updateMFI(200, menu.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem25ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
       do {
            final String days_string = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_please_enter_number_of_days_for_moving_average"));
            if (days_string == null) {
                return;
            }
            try {
                final int days = Integer.parseInt(days_string);
                if (days <= 0) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                this.updateMFI(days, true);
                return;
            }
            catch (java.lang.NumberFormatException exp) {
                log.error(null, exp);
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                continue;
            }
        } while(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        StochasticOptionsJDialog stochasticOptionsJDialog = new StochasticOptionsJDialog(MainFrame.getInstance(), true);
        stochasticOptionsJDialog.setLocationRelativeTo(this);
        stochasticOptionsJDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        this.zoom(Zoom.Days7);
    }//GEN-LAST:event_jLabel9MouseClicked

    /**
     * Calculate and update high low value labels, according to current displayed
     * time range. This is a time consuming method, and shall be called by
     * user thread.
     */
    private void _updateHighLowJLabels() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChartJDialog.this.jLabel2.setText("");
                ChartJDialog.this.jLabel4.setText("");
            }
        });

        final ValueAxis valueAxis = this.getPlot().getDomainAxis();
        final Range range = valueAxis.getRange();
        final long lowerBound = (long)range.getLowerBound();
        final long upperBound = (long)range.getUpperBound();

        // Perform binary search, to located day in price time series, which
        // is equal or lesser than upperBound.
        int low = 0;
        int high = this.priceTimeSeries.getItemCount() - 1;
        long best_dist = Long.MAX_VALUE;
        int best_mid = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            final Day day = (Day)this.priceTimeSeries.getDataItem(mid).getPeriod();
            long v = day.getFirstMillisecond();

            if (v > upperBound) {
                high = mid - 1;
            }
            else if (v < upperBound) {
                low = mid + 1;
                long dist = upperBound - v;
                if (dist < best_dist) {
                    best_dist = dist;
                    best_mid = mid;
                }
            }
            else {
                best_dist = 0;
                best_mid = mid;
                break;
            }
        }

        if (best_mid < 0) {
            return;
        }

        double high_last_price = Double.NEGATIVE_INFINITY;
        double low_last_price = Double.MAX_VALUE;
        for (int i = best_mid; i >= 0; i--) {
            final TimeSeriesDataItem item = this.priceTimeSeries.getDataItem(i);
            final long time = ((Day)item.getPeriod()).getFirstMillisecond();
            if (time < lowerBound) {
                break;
            }
            double value = (Double)item.getValue();
            if (value == 0.0) {
                /* Market closed during that time. Ignore. */
                continue;
            }
            if (high_last_price < value) {
                high_last_price = value;
            }
            if (low_last_price > value) {
                low_last_price = value;
            }
        }

        final double h = high_last_price;
        final double l = low_last_price;
        if (high_last_price >= low_last_price) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ChartJDialog.this.jLabel2.setText(org.yccheok.jstock.gui.Utils.stockPriceDecimalFormat(h));
                    ChartJDialog.this.jLabel4.setText(org.yccheok.jstock.gui.Utils.stockPriceDecimalFormat(l));
                }
            });
        }
    }

    /**
     * Reset all day labels back to plain font.
     */
    private void resetAllDayLabels() {
        JLabel[] labels = {jLabel9, jLabel10, jLabel11, jLabel12, jLabel13, jLabel14 };
        for (JLabel label : labels) {
            final Font oldFont = label.getFont();
            final Font font = new Font(oldFont.getFontName(), oldFont.getStyle() & ~Font.BOLD, oldFont.getSize());
            label.setFont(font);
        }
    }

    /**
     * Calculate and update high low value labels, according to current displayed
     * time range. This method will return immediately, as the calculating and
     * updating task by performed by user thread.
     */
    private void updateHighLowJLabels() {
        updateHighLowLabelsPool.execute(new Runnable() {
            @Override
            public void run() {
                ChartJDialog.this._updateHighLowJLabels();
            }
        });
    }

    /**
     * Zoom in to this chart according to zoom information, and update day labels
     * as well.
     * @param zoom zoom information
     */
    private void zoom(Zoom z) {
        switch(z) {
        case Days7:
            this._zoom(Calendar.DATE, -7);
            /* Reset first. */
            this.resetAllDayLabels();
            this.jLabel9.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel9.getFont()));
            break;

        case Month1:
            this._zoom(Calendar.MONTH, -1);
            /* Reset first. */
            this.resetAllDayLabels();
            this.jLabel10.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel10.getFont()));
            break;

        case Months3:
            this._zoom(Calendar.MONTH, -3);
            /* Reset first. */
            this.resetAllDayLabels();
            this.jLabel11.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel11.getFont()));
            break;

        case Months6:
            this._zoom(Calendar.MONTH, -6);
            /* Reset first. */
            this.resetAllDayLabels();
            this.jLabel12.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel12.getFont()));
            break;

        case Year1:
            this._zoom(Calendar.YEAR, -1);
            /* Reset first. */
            this.resetAllDayLabels();
            this.jLabel13.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel13.getFont()));
            break;

        case All:
            this.chartPanel.restoreAutoBounds();
            /* Reset first. */
            this.resetAllDayLabels();
            /* Bold the target. */
            this.jLabel14.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel14.getFont()));
            break;
        }
    }

    /**
     * Zoom in to this chart with specific amount of time.
     * @param field the calendar field.
     * @param amount the amount of date or time to be added to the field.
     */
    private void _zoom(int field, int amount) {
        this.chartPanel.restoreAutoBounds();

        final int itemCount = this.priceTimeSeries.getItemCount();
        final Day day = (Day)this.priceTimeSeries.getDataItem(itemCount - 1).getPeriod();
        // Candle stick takes up half day space.
        // Volume price chart's volume information takes up whole day space.
        final long end = day.getFirstMillisecond() + (this.getCurrentMode() == Mode.Candlestick ? (1000 * 60 * 60 * 12) : (1000 * 60 * 60 * 24 - 1));
        final Calendar calendar = Calendar.getInstance();
        // -1. Calendar's month is 0 based but JFreeChart's month is 1 based.
        calendar.set(day.getYear(), day.getMonth() - 1, day.getDayOfMonth(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(field, amount);
        // Candle stick takes up half day space.
        // Volume price chart's volume information does not take up any space.
        final long start = Math.max(0, calendar.getTimeInMillis() - (this.getCurrentMode() == Mode.Candlestick ? (1000 * 60 * 60 * 12) : 0));
        final ValueAxis valueAxis = this.getPlot().getDomainAxis();
        valueAxis.setRange(start, end);

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double max_volume = Double.MIN_VALUE;
        for (int i = itemCount - 1; i >= 0; i--) {
            final TimeSeriesDataItem item = this.priceTimeSeries.getDataItem(i);
            final Day d = (Day)item.getPeriod();
            if (d.getFirstMillisecond() < start) {
                break;
            }
            final DefaultHighLowDataset defaultHighLowDataset = (DefaultHighLowDataset)this.priceOHLCDataset;
            final double high = defaultHighLowDataset.getHighValue(0, i);
            final double low = defaultHighLowDataset.getLowValue(0, i);
            final double volume = defaultHighLowDataset.getVolumeValue(0, i);

            if (max < high) {
                max = high;
            }
            if (min > low) {
                min = low;
            }
            if (max_volume < volume) {
                max_volume = volume;
            }
        }

        if (min > max) {
            return;
        }

        final ValueAxis rangeAxis = this.getPlot().getRangeAxis();
        final Range rangeAxisRange = rangeAxis.getRange();
        // Increase each side by 1%
        double tolerance = 0.01 * (max - min);
        // The tolerance must within range [0.01, 1.0]
        tolerance = Math.min(Math.max(0.01, tolerance), 1.0);
        // The range must within the original chart range.
        min = Math.max(rangeAxisRange.getLowerBound(), min - tolerance);
        max = Math.min(rangeAxisRange.getUpperBound(), max + tolerance);

        this.getPlot().getRangeAxis().setRange(min, max);

        if (this.getPlot().getRangeAxisCount() > 1) {
            final double volumeUpperBound = this.getPlot().getRangeAxis(1).getRange().getUpperBound();
            final double suggestedVolumneUpperBound = max_volume * 4;
            // To prevent over zoom-in.
            if (suggestedVolumneUpperBound < volumeUpperBound) {
                this.getPlot().getRangeAxis(1).setRange(0, suggestedVolumneUpperBound);
            }
        }
    }

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        this.zoom(Zoom.Month1);
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        this.zoom(Zoom.Months3);
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        this.zoom(Zoom.Months6);
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        this.zoom(Zoom.Year1);
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        this.zoom(Zoom.All);
    }//GEN-LAST:event_jLabel14MouseClicked
   
    /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createPriceVolumeChart(StockHistoryServer stockHistoryServer) {
        final int num = stockHistoryServer.getNumOfCalendar();
        final Stock stock = stockHistoryServer.getStock(stockHistoryServer.getCalendar(num - 1));

        final String title = stock.getName();

        final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
        timeAxis.setLowerMargin(0.02);                  // reduce the default margins
        timeAxis.setUpperMargin(0.02);
        
        final NumberAxis rangeAxis1 = new NumberAxis(GUIBundle.getString("ChartJDialog_Price"));
        rangeAxis1.setAutoRangeIncludesZero(false);     // override default
        rangeAxis1.setLowerMargin(0.40);                // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("00.00");
        rangeAxis1.setNumberFormatOverride(format);

        XYPlot plot = new XYPlot(this.priceDataset, timeAxis, rangeAxis1, null);

        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
        renderer1.setBaseToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
            )
        );
        plot.setRenderer(0, renderer1);

        final NumberAxis rangeAxis2 = new NumberAxis("Volume");
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(1, rangeAxis2);
        plot.setDataset(1, this.volumeDataset);
        plot.mapDatasetToRangeAxis(1, 1);

        XYBarRenderer renderer2 = new XYBarRenderer(0.20);
        renderer2.setBaseToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")
            )
        );
        plot.setRenderer(1, renderer2);
        
        CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(timeAxis);
        cplot.add(plot, 3);
        cplot.setGap(8.0);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, cplot, true);
        org.yccheok.jstock.charting.Utils.applyChartTheme(chart);

        // Handle zooming event.
        chart.addChangeListener(this.getChartChangeListner());

        return chart;
    }

    private TimeSeries getPriceTimeSeries(StockHistoryServer stockHistoryServer) {

        // create dataset 1...
        TimeSeries series1 = new TimeSeries(GUIBundle.getString("ChartJDialog_Price"));
        final int num = stockHistoryServer.getNumOfCalendar();
        
        for(int i = 0; i < num; i++) {
            final Calendar c = stockHistoryServer.getCalendar(i);
            final Stock s = stockHistoryServer.getStock(c);
            series1.add(new Day(c.getTime()), s.getLastPrice());
        }
        return series1;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private IntervalXYDataset getVolumeDataset(StockHistoryServer stockHistoryServer) {

        // create dataset 2...
        TimeSeries series1 = new TimeSeries(GUIBundle.getString("ChartJDialog_Volume"));

        final int num = stockHistoryServer.getNumOfCalendar();
        
        for(int i = 0; i < num; i++) {
            final Calendar c = stockHistoryServer.getCalendar(i);
            final Stock s = stockHistoryServer.getStock(c);
            series1.add(new Day(c.getTime()), s.getVolume());
        }

        return new TimeSeriesCollection(series1);
    }
    
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The dataset.
     */
    private JFreeChart createCandlestickChart(StockHistoryServer stockHistoryServer) {
        final int num = stockHistoryServer.getNumOfCalendar();
        final Stock stock = stockHistoryServer.getStock(stockHistoryServer.getCalendar(num - 1));
        
        final String title = stock.getName();
        
        final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
        final NumberAxis valueAxis = new NumberAxis(GUIBundle.getString("ChartJDialog_Price"));
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setUpperMargin(0.0);
        valueAxis.setLowerMargin(0.0);
        XYPlot plot = new XYPlot(this.priceOHLCDataset, timeAxis, valueAxis, null);

        final CandlestickRenderer candlestickRenderer = new CandlestickRenderer();
        plot.setRenderer(candlestickRenderer);
        
        // Give good width when zoom in, but too slow in calculation.
        ((CandlestickRenderer)plot.getRenderer()).setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);

        CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(timeAxis);
        cplot.add(plot, 3);
        cplot.setGap(8.0);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, cplot, true);

        org.yccheok.jstock.charting.Utils.applyChartTheme(chart);

        // Handle zooming event.
        chart.addChangeListener(this.getChartChangeListner());

        return chart;        
    }
    
    /**
     * Creates a sample high low dataset.
     *
     * @return a sample high low dataset.
     */
    public OHLCDataset getOHLCDataset(StockHistoryServer stockHistoryServer) {

        final int num = stockHistoryServer.getNumOfCalendar();
        
        Date[] date = new Date[num];
        double[] high = new double[num];
        double[] low = new double[num];
        double[] open = new double[num];
        double[] close = new double[num];
        double[] volume = new double[num];
        
        for(int i = 0; i < num; i++) {
            final Calendar c = stockHistoryServer.getCalendar(i);
            final Stock s = stockHistoryServer.getStock(c);
            
            date[i] = s.getCalendar().getTime();
            high[i] = s.getHighPrice();
            low[i] = s.getLowPrice();
            open[i] = s.getOpenPrice();
            close[i] = s.getLastPrice();
            volume[i] = s.getVolume();
        }
        
        return new DefaultHighLowDataset(GUIBundle.getString("ChartJDialog_Price"), date, high, low, open,
                close, volume);
    }

    // For Candlestick chart usage.
    private int getIndex(int days, Map<Integer, Integer> map) {
        if (map.containsKey(days)) {
            return map.get(days);
        }
        int max = 0;    // 0, reserve for price and volume.
                        // Price and volume are using same index for Candlestick.
        for (Integer index : map.values()) {
            if (index > max) {
                max = index;
            }
        }
        return max + 1;
    }

    private String getEMAKey(int days) {
        return days + "d EMA";
    }

    private String getMovingAverageKey(int days) {
        return days + "d SMA";
    }

    private String getRSIKey(int days) {
        return days + "d RSI";
    }

    private String getCCIKey(int days) {
        return days + "d CCI";
    }

    private String getMFIKey(int days) {
        return days + "d MFI";
    }

    private void updateCCI(int days, boolean show) {
        /* To simplify our work, we will update candle stick chart as well. */
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(stockHistoryServer);
        }

        final TAEx taEx = TAEx.newInstance(TA.CCI, new Integer(days));

        if (show) {
            final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
            final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();

            if (price_volume_ta_map.containsKey(taEx) == false) {
                final XYDataset dataset = org.yccheok.jstock.charting.TechnicalAnalysis.createCCI(this.stockHistoryServer, getCCIKey(days), days);
                NumberAxis rangeAxis1 = new NumberAxis(GUIBundle.getString("ChartJDialog_CCI"));
                rangeAxis1.setAutoRangeIncludesZero(false);     // override default
                rangeAxis1.setLowerMargin(0.40);                // to leave room for volume bars
                DecimalFormat format = new DecimalFormat("0");
                rangeAxis1.setNumberFormatOverride(format);

                final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
                timeAxis.setLowerMargin(0.02);                  // reduce the default margins
                timeAxis.setUpperMargin(0.02);

                XYPlot plot = new XYPlot(dataset, timeAxis, rangeAxis1, null);

                XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
                renderer1.setBaseToolTipGenerator(
                    new StandardXYToolTipGenerator(
                        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
                    )
                );
                plot.setRenderer(0, renderer1);
                price_volume_ta_map.put(taEx, plot);
            }

            if (candlestick_ta_map.containsKey(taEx) == false) {
                try {
                    /* Not sure why. I cannot make priceVolumeChart and candlestickChart sharing the same
                     * plot. If not, this will inhibit incorrect zooming behavior.
                     */
                    candlestick_ta_map.put(taEx, (XYPlot)price_volume_ta_map.get(taEx).clone());
                } catch (CloneNotSupportedException ex) {
                    log.error(null, ex);
                }
            }
    
            final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
            final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

            if (price_volume_ta != null) cplot0.add(price_volume_ta, 1);    // weight is 1.
            if (candlestick_ta != null) cplot1.add(candlestick_ta, 1);      // weight is 1.
            org.yccheok.jstock.charting.Utils.applyChartTheme(this.priceVolumeChart);
            org.yccheok.jstock.charting.Utils.applyChartTheme(this.candlestickChart);
        }
        else {
            final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
            final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();
            final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
            final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

            if (price_volume_ta != null) cplot0.remove(price_volume_ta);
            if (candlestick_ta != null) cplot1.remove(candlestick_ta);
        }

        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
        }
    }

    private void updateMFI(int days, boolean show) {
        /* To simplify our work, we will update candle stick chart as well. */
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(stockHistoryServer);
        }

        final TAEx taEx = TAEx.newInstance(TA.MFI, new Integer(days));

        if (show) {
            final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
            final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();

            if (price_volume_ta_map.containsKey(taEx) == false) {
                final XYDataset dataset = org.yccheok.jstock.charting.TechnicalAnalysis.createMFI(this.stockHistoryServer, getMFIKey(days), days);
                NumberAxis rangeAxis1 = new NumberAxis(GUIBundle.getString("ChartJDialog_MFI"));
                rangeAxis1.setAutoRangeIncludesZero(false);     // override default
                rangeAxis1.setLowerMargin(0.40);                // to leave room for volume bars
                DecimalFormat format = new DecimalFormat("0");
                rangeAxis1.setNumberFormatOverride(format);

                final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
                timeAxis.setLowerMargin(0.02);                  // reduce the default margins
                timeAxis.setUpperMargin(0.02);

                XYPlot plot = new XYPlot(dataset, timeAxis, rangeAxis1, null);

                XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
                renderer1.setBaseToolTipGenerator(
                    new StandardXYToolTipGenerator(
                        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
                    )
                );
                plot.setRenderer(0, renderer1);
                price_volume_ta_map.put(taEx, plot);
            }

            if (candlestick_ta_map.containsKey(taEx) == false) {
                try {
                    /* Not sure why. I cannot make priceVolumeChart and candlestickChart sharing the same
                     * plot. If not, this will inhibit incorrect zooming behavior.
                     */
                    candlestick_ta_map.put(taEx, (XYPlot)price_volume_ta_map.get(taEx).clone());
                } catch (CloneNotSupportedException ex) {
                    log.error(null, ex);
                }
            }

            final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
            final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

            if (price_volume_ta != null) cplot0.add(price_volume_ta, 1);    // weight is 1.
            if (candlestick_ta != null) cplot1.add(candlestick_ta, 1);      // weight is 1.
            org.yccheok.jstock.charting.Utils.applyChartTheme(this.priceVolumeChart);
            org.yccheok.jstock.charting.Utils.applyChartTheme(this.candlestickChart);
        }
        else {
            final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
            final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();
            final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
            final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

            if (price_volume_ta != null) cplot0.remove(price_volume_ta);
            if (candlestick_ta != null) cplot1.remove(candlestick_ta);
        }

        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
        }
    }

    private void updateRSI(int days, boolean show) {
        /* To simplify our work, we will update candle stick chart as well. */
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(stockHistoryServer);
        }

        final TAEx taEx = TAEx.newInstance(TA.RSI, new Integer(days));

        if (show) {
            final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
            final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();

            if (price_volume_ta_map.containsKey(taEx) == false) {
                final XYDataset dataset = org.yccheok.jstock.charting.TechnicalAnalysis.createRSI(this.stockHistoryServer, getRSIKey(days), days);
                NumberAxis rangeAxis1 = new NumberAxis(GUIBundle.getString("ChartJDialog_RSI"));
                rangeAxis1.setAutoRangeIncludesZero(false);     // override default
                rangeAxis1.setLowerMargin(0.40);                // to leave room for volume bars
                DecimalFormat format = new DecimalFormat("0");
                rangeAxis1.setNumberFormatOverride(format);

                final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
                timeAxis.setLowerMargin(0.02);                  // reduce the default margins
                timeAxis.setUpperMargin(0.02);

                XYPlot plot = new XYPlot(dataset, timeAxis, rangeAxis1, null);

                XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
                renderer1.setBaseToolTipGenerator(
                    new StandardXYToolTipGenerator(
                        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
                    )
                );
                plot.setRenderer(0, renderer1);
                price_volume_ta_map.put(taEx, plot);
            }

            if (candlestick_ta_map.containsKey(taEx) == false) {
                try {
                    /* Not sure why. I cannot make priceVolumeChart and candlestickChart sharing the same
                     * plot. If not, this will inhibit incorrect zooming behavior.
                     */
                    candlestick_ta_map.put(taEx, (XYPlot)price_volume_ta_map.get(taEx).clone());
                } catch (CloneNotSupportedException ex) {
                    log.error(null, ex);
                }
            }
            
            final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
            final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

            if (price_volume_ta != null) cplot0.add(price_volume_ta, 1);    // weight is 1.
            if (candlestick_ta != null) cplot1.add(candlestick_ta, 1);      // weight is 1.
            org.yccheok.jstock.charting.Utils.applyChartTheme(this.priceVolumeChart);
            org.yccheok.jstock.charting.Utils.applyChartTheme(this.candlestickChart);
        }
        else {
            final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
            final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();
            final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
            final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

            if (price_volume_ta != null) cplot0.remove(price_volume_ta);
            if (candlestick_ta != null) cplot1.remove(candlestick_ta);
        }

        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
        }
    }

    private void updateEMA(int days, boolean show) {
        /* To simplify our work, we will update candle stick chart as well. */
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(stockHistoryServer);
        }

        final TAEx taEx = TAEx.newInstance(TA.EMA, new Integer(days));
        if (show) {
            TimeSeries timeSeries = null;
            XYDataset dataSet = null;
            final Integer days_integer = days;
            if (false == time_series_exponential_moving_average_map.containsKey(days_integer)) {
                timeSeries = TechnicalAnalysis.createEMA(stockHistoryServer, this.getEMAKey(days), days);
                dataSet = new TimeSeriesCollection(timeSeries);
                // Do not put everything into map. We will out of memory.
                if (this.time_series_exponential_moving_average_map.size() < MAX_MAP_SIZE) {
                    time_series_exponential_moving_average_map.put(days_integer, timeSeries);
                }
            }
            else {
                timeSeries = time_series_exponential_moving_average_map.get(days_integer);
                dataSet =  new TimeSeriesCollection(timeSeries);
            }

            {
                if (this.activeTAExs.contains(taEx) == false)
                {
                    // Avoid duplication.
                    ((TimeSeriesCollection)this.priceDataset).addSeries(timeSeries);
                }
            }
            {
                if (this.activeTAExs.contains(taEx) == false)
                {
                    // Avoid duplication.
                    final Plot main_plot = (Plot)((CombinedDomainXYPlot)this.candlestickChart.getPlot()).getSubplots().get(0);
                    final XYPlot plot = (XYPlot) main_plot;
                    final int index = getIndex(days, this.xydata_index_map);
                    this.xydata_index_map.put(days, index);
                    plot.setDataset(index, dataSet);
                    plot.setRenderer(index, new StandardXYItemRenderer());
                }
            }
        }
        else {
            {
                final TimeSeries ts = ((TimeSeriesCollection)this.priceDataset).getSeries(this.getEMAKey(days));
                if (ts != null) {
                    ((TimeSeriesCollection)this.priceDataset).removeSeries(ts);
                }
            }
            {
                final Plot main_plot = (Plot)((CombinedDomainXYPlot)this.candlestickChart.getPlot()).getSubplots().get(0);
                final XYPlot plot = (XYPlot)main_plot;
                final Integer integer = this.xydata_index_map.get(days);
                if (integer != null) {
                    final int index = integer;
                    plot.setDataset(index, null);
                }
            }
        }

        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
        }
    }

    /**
     * Returns chart change listener, which will be responsible for handling
     * zooming event.
     *
     * @return chart change listener, which will be responsible for handling
     * zooming event
     */
    private ChartChangeListener getChartChangeListner() {
        return new ChartChangeListener() {
            @Override
            public void chartChanged(ChartChangeEvent event) {
                // Is weird. This works well for zoom-in. When we add new CCI or
                // RIS. This event function will be triggered too. However, the
                // returned draw area will always be the old draw area, unless
                // you move your move over.
                // Even I try to capture event.getType() == ChartChangeEventType.NEW_DATASET
                // also doesn't work.
                if (event.getType() == ChartChangeEventType.GENERAL) {
                    ChartJDialog.this.chartLayerUI.updateTraceInfos();
                    // Re-calculating high low value.
                    ChartJDialog.this.updateHighLowJLabels();
                }
            }
        };
    }

    /**
     * Returns the current main plot of this chart dialog.
     *
     * @return the current main plot of this chart dialog
     */
    public XYPlot getPlot() {
        final JFreeChart chart = this.chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        final XYPlot plot = (XYPlot) cplot.getSubplots().get(0);
        return plot;
    }

    /**
     * Returns the plot at the specified position in this chart dialog.
     *
     * @param index index of the plot to return
     * @return the plot at the specified position in this chart dialog
     */
    public XYPlot getPlot(int index) {
        final JFreeChart chart = this.chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        final XYPlot plot = (XYPlot) cplot.getSubplots().get(index);
        return plot;
    }

    /**
     * Returns the number of plots in this chart dialog.
     *
     * @return the number of plots in this chart dialog
     */
    public int getPlotSize() {
        final JFreeChart chart = this.chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        return cplot.getSubplots().size();
    }

    private void updateSMA(int days, boolean show) {
        /* To simplify our work, we will update candle stick chart as well. */
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(stockHistoryServer);
        }

        final TAEx taEx = TAEx.newInstance(TA.SMA, new Integer(days));
        if (show) {
            TimeSeries timeSeries = null;
            XYDataset dataSet = null;
            final Integer days_integer = days;
            if (false == time_series_moving_average_map.containsKey(days_integer)) {
                timeSeries = MovingAverage.createMovingAverage(
                    priceTimeSeries, getMovingAverageKey(days), days, days
                );
                dataSet = new TimeSeriesCollection(timeSeries);
                // Do not put everything into map. We will out of memory.
                if (this.time_series_moving_average_map.size() < MAX_MAP_SIZE) {
                    time_series_moving_average_map.put(days_integer, timeSeries);
                }
            }
            else {
                timeSeries = time_series_moving_average_map.get(days_integer);
                dataSet =  new TimeSeriesCollection(timeSeries);
            }

            {
                if (this.activeTAExs.contains(taEx) == false)
                {
                    // Avoid duplication.
                    ((TimeSeriesCollection)this.priceDataset).addSeries(timeSeries);
                }                
            }
            {
                if (this.activeTAExs.contains(taEx) == false)
                {
                    // Avoid duplication.
                    final Plot main_plot = (Plot)((CombinedDomainXYPlot)this.candlestickChart.getPlot()).getSubplots().get(0);
                    final XYPlot plot = (XYPlot) main_plot;
                    final int index = getIndex(days, this.xydata_index_map);
                    this.xydata_index_map.put(days, index);
                    plot.setDataset(index, dataSet);
                    plot.setRenderer(index, new StandardXYItemRenderer());
                }
            }
        }
        else {
            {
                final TimeSeries ts = ((TimeSeriesCollection)this.priceDataset).getSeries(this.getMovingAverageKey(days));
                if (ts != null) {
                    ((TimeSeriesCollection)this.priceDataset).removeSeries(ts);
                }
            }
            {
                final Plot main_plot = (Plot)((CombinedDomainXYPlot)this.candlestickChart.getPlot()).getSubplots().get(0);
                final XYPlot plot = (XYPlot) main_plot;
                final Integer integer = this.xydata_index_map.get(days);
                if (integer != null) {
                    final int index = integer;
                    plot.setDataset(index, null);
                }
            }
        }

        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
        }
    }

    public static class TAEx {
        private TAEx(TA ta, Object parameter) {
            if (ta == null || parameter == null) {
                throw new IllegalArgumentException("Method arguments cannot be null");
            }
            this.ta = ta;
            this.parameter = parameter;
        }

        public static TAEx newInstance(TA ta, Object parameter) {
            return new TAEx(ta, parameter);
        }

        /**
         * Get the technical analysis type.
         * @return the technical analysis type
         */
        public TA getTA() {
            return this.ta;
        }

        /**
         * Get the technical analysis parameter
         * @return the technical analysis parameter.
         */
        public Object getParameter() {
            return this.parameter;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if(!(o instanceof TAEx)) {
                return false;
            }
            TAEx taEx = (TAEx)o;
            return  (this.ta == null ? taEx.ta == null : this.ta.equals(taEx.ta)) &&
                    (this.parameter == null ? taEx.parameter == null : this.parameter.equals(taEx.parameter));
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (this.ta != null ? this.ta.hashCode() : 0);
            hash = 53 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
            return hash;
        }

        private final TA ta;
        private final Object parameter;
    }

    private final StockHistoryServer stockHistoryServer;
    private final ChartPanel chartPanel;
    private final Map<Integer, TimeSeries> time_series_moving_average_map = new HashMap<Integer, TimeSeries>();
    private final Map<Integer, TimeSeries> time_series_exponential_moving_average_map = new HashMap<Integer, TimeSeries>();
    /* Days to series index. (For main plot only) */
    private final Map<Integer, Integer> xydata_index_map = new HashMap<Integer, Integer>();
    private final List<TAEx> activeTAExs = new ArrayList<TAEx>();
    private static final int MAX_MAP_SIZE = 20;

    private final Map<TAEx, XYPlot> price_volume_ta_map = new HashMap<TAEx, XYPlot>();
    /* Not sure why. I cannot make priceVolumeChart and candlestickChart sharing the same
     * plot. If not, this will inhibit incorrect zooming behavior.
     */
    private final Map<TAEx, XYPlot> candlestick_ta_map = new HashMap<TAEx, XYPlot>();

    private final XYDataset priceDataset;
    private final IntervalXYDataset volumeDataset;
    private final TimeSeries priceTimeSeries;
    private final OHLCDataset priceOHLCDataset;
    private JFreeChart priceVolumeChart;
    private JFreeChart candlestickChart;

    /**
     * Thread pool, used to hold threads to update high low labels.
     */
    private final Executor updateHighLowLabelsPool = Executors.newFixedThreadPool(1);

    /* Overlay layer. */
    private final ChartLayerUI<ChartPanel> chartLayerUI;

    private static final Log log = LogFactory.getLog(ChartJDialog.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem10;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem11;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem12;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem13;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem14;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem15;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem16;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem17;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem18;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem19;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem20;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem21;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem22;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem23;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem24;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem25;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem3;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem4;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem5;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem6;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem7;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem8;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    // End of variables declaration//GEN-END:variables
    
}
