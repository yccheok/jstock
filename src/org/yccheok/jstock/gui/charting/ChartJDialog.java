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

package org.yccheok.jstock.gui.charting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import org.yccheok.jstock.engine.*;

import java.util.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
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
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.*;
import org.yccheok.jstock.charting.ChartData;
import org.yccheok.jstock.charting.MACD;
import org.yccheok.jstock.charting.TechnicalAnalysis;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.charting.ChartJDialog.TAEx;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.network.Utils;

/**
 *
 * @author  yccheok
 */
public class ChartJDialog extends javax.swing.JFrame implements WindowListener {
    public enum TA {
        SMA,
        EMA,
        MACD,
        RSI,
        MFI,        
        CCI,        
    }

    public enum Type {
        PriceVolume,
        Candlestick
    }

    public enum Interval {
        Daily,
        Weekly,
        Monthly
    }
    
    public enum Zoom {
        Days7,
        Month1,
        Months3,
        Months6,
        Year1,
        Year5,
        Year10,
        All
    }

    private Interval getCurrentInterval() {
        return this.currentInverval;
    }
    
    private Type getCurrentType() {
        return this.currentType;
    }
    
    /** Creates new form ChartJDialog */
    public ChartJDialog(java.awt.Frame parent, String title, boolean modalNotUsed, StockHistoryServer stockHistoryServer) {                
        super(title);
        
        this.parent = parent;
        this.parent.addWindowListener(this);
        
        this.setIconImage(parent.getIconImage());
                
        initComponents();
        initKeyBindings(); 

        // Must initialized first before any other operations. Our objective
        // is to show this chart as fast as possible. Hence, we will pass in
        // null first, till we sure what are we going to create.
        this.chartPanel = new ChartPanel(null, true, true, true, true, true);
        this.stockHistoryServer = stockHistoryServer;

        final ChartJDialogOptions chartJDialogOptions = JStock.instance().getChartJDialogOptions();
        this.changeInterval(chartJDialogOptions.getInterval());
        
        // Yellow box and chart resizing (#2969416)
        //
        // paradoxoff :
        // If the available size for a ChartPanel exceeds the dimensions defined
        // by the maximumDrawWidth and maximumDrawHeight attributes, the
        // ChartPanel will draw the chart in a Rectangle defined by the maximum
        // sizes and then scale it to fill the available size.
        // All you need to do is to avoid that scaling by using sufficiently
        // large values for the maximumDrawWidth and maximumDrawHeight, e. g.
        // by using the Dimension returned from
        // Toolkit.getDefaultToolkit().getScreenSize()
        // http://www.jfree.org/phpBB2/viewtopic.php?f=3&t=30059
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.chartPanel.setMaximumDrawWidth((int)Math.round(dimension.getWidth()));
        this.chartPanel.setMaximumDrawHeight((int)Math.round(dimension.getHeight()));
 
        // Make chartPanel able to receive key event.
        // So that we may use arrow key to move around yellow information box.
        this.chartPanel.setFocusable(true);
        this.chartPanel.requestFocus();

        final org.jdesktop.jxlayer.JXLayer<ChartPanel> layer = new org.jdesktop.jxlayer.JXLayer<ChartPanel>(this.chartPanel);
        this.chartLayerUI = new ChartLayerUI<ChartPanel>(this);
        layer.setUI(this.chartLayerUI);

        // Call after JXLayer has been initialized. changeType assumes JXLayer 
        // is ready.
        this.changeType(chartJDialogOptions.getType());

        getContentPane().add(layer, java.awt.BorderLayout.CENTER);

        // Handle resize.
        this.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e) {
                ChartJDialog.this.chartLayerUI.updateTraceInfos();
            }
        });
        
        // Update GUI.
        if (JStock.instance().getJStockOptions().getChartTheme() == JStockOptions.ChartTheme.Light) {
            this.jRadioButtonMenuItem3.setSelected(true);
        } else {
            this.jRadioButtonMenuItem4.setSelected(true);
        }
    }

    public void windowActivated(WindowEvent e) {
       // this only works becuase AutoRequestFocus is false, so this stays on
       // top, but looses focus 
       this.toFront();
    }
    @Override
    public void windowDeactivated(WindowEvent e) {
       // JFrame is set to AlwaysOnTop = true at design time. So this is only
       // useful on first deactivation. After that it is meaningless. But without
       // initial AlwaysOnTop, it would not receive focus because autoRequestFocus
       // is false.
       this.setAlwaysOnTop(false);
    }
    @Override
    public void windowIconified(WindowEvent e) {
       // when main app goes away, this child window should also go away 
       this.setVisible(false);
    }
    @Override
    public void windowDeiconified(WindowEvent e) {
       // when main app comes back, this child window should also come back
       this.setVisible(true);
    }
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e) {}
    @Override
    public void windowOpened(WindowEvent e) {}
    
    /**
     * Build menu items for TA.
     */
    private void buildTAMenuItems() {
        final int[] days = {14, 28, 50, 100, 200};
        final MACD.Period[] macd_periods = {MACD.Period.newInstance(12, 26, 9)};
        
        // day_keys, week_keys and month_keys should be having same length as
        // days.        
        final String[] day_keys = {"ChartJDialog_14Days", "ChartJDialog_28Days", "ChartJDialog_50Days", "ChartJDialog_100Days", "ChartJDialog_200Days" };
        final String[] week_keys = {"ChartJDialog_14Weeks", "ChartJDialog_28Weeks", "ChartJDialog_50Weeks", "ChartJDialog_100Weeks", "ChartJDialog_200Weeks" };
        final String[] month_keys = {"ChartJDialog_14Months", "ChartJDialog_28Months", "ChartJDialog_50Months", "ChartJDialog_100Months", "ChartJDialog_200Months" };
        assert(days.length == day_keys.length);
        assert(days.length == week_keys.length);
        assert(days.length == week_keys.length);
        // macd_day_keys, macd_week_keys and macd_month_keys should be having 
        // same length as macd_periods.
        final String[] macd_day_keys = {"ChartJDialog_12_26_9Days"};
        final String[] macd_week_keys = {"ChartJDialog_12_26_9Weeks"};
        final String[] macd_month_keys = {"ChartJDialog_12_26_9Months"};
        assert(macd_periods.length == macd_day_keys.length);
        assert(macd_periods.length == macd_week_keys.length);
        assert(macd_periods.length == macd_month_keys.length);
        
        String[] keys = null;
        String[] macd_keys = null;
        if (this.getCurrentInterval() == Interval.Daily) {
            keys = day_keys;
            macd_keys = macd_day_keys;
        } else if (this.getCurrentInterval() == Interval.Weekly) {
            keys = week_keys;
            macd_keys = macd_week_keys;
        } else if (this.getCurrentInterval() == Interval.Monthly) {
            keys = month_keys;
            macd_keys = macd_month_keys;
        } else {
            assert(false);
        }
        final TA[] tas = TA.values();
        final String[] ta_keys = {"ChartJDialog_SMA", "ChartJDialog_EMA", "ChartJDialog_MACD", "ChartJDialog_RSI", "ChartJDialog_MFI", "ChartJDialog_CCI" };
        final String[] ta_tip_keys = {"ChartJDialog_SimpleMovingAverage", "ChartJDialog_ExponentialMovingAverage", "ChartJDialog_MovingAverageConvergenceDivergence", "ChartJDialog_RelativeStrengthIndex", "ChartJDialog_MoneyFlowIndex", "ChartJDialog_CommodityChannelIndex" };
        final String[] custom_message_keys = {
            "info_message_please_enter_number_of_days_for_SMA",
            "info_message_please_enter_number_of_days_for_EMA",
            "dummy",
            "info_message_please_enter_number_of_days_for_RSI",
            "info_message_please_enter_number_of_days_for_MFI",
            "info_message_please_enter_number_of_days_for_CCI"
        };
        final Map<TA, Set<Object>> m = new EnumMap<TA, Set<Object>>(TA.class);
        final int taExSize = JStock.instance().getChartJDialogOptions().getTAExSize();
        for (int i = 0; i < taExSize; i++) {
            final TAEx taEx = JStock.instance().getChartJDialogOptions().getTAEx(i);
            if (m.containsKey(taEx.getTA()) == false) {
                m.put(taEx.getTA(), new HashSet<Object>());
            }
            m.get(taEx.getTA()).add(taEx.getParameter());
        }

        for (int i = 0, length = tas.length; i < length; i++) {
            final TA ta = tas[i];
            javax.swing.JMenu menu = new javax.swing.JMenu();
            menu.setText(GUIBundle.getString(ta_keys[i])); // NOI18N
            menu.setToolTipText(GUIBundle.getString(ta_tip_keys[i])); // NOI18N

            if (ta == TA.MACD) {
                for (int j = 0, length2 = macd_periods.length; j < length2; j++) {
                    final int _j = j;
                    final javax.swing.JCheckBoxMenuItem item = new javax.swing.JCheckBoxMenuItem();
                    item.setText(GUIBundle.getString(macd_keys[j]));
                    if (m.containsKey(ta)) {
                        if (m.get(ta).contains(macd_periods[_j])) {
                            item.setSelected(true);
                        }
                    }
                    item.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (ta == TA.MACD) {
                                updateMACD(macd_periods[_j], item.isSelected());
                            }
                        }   
                    });
                    menu.add(item);
                }
            } else {
                for (int j = 0, length2 = days.length; j < length2; j++) {
                    final int _j = j;
                    final javax.swing.JCheckBoxMenuItem item = new javax.swing.JCheckBoxMenuItem();
                    item.setText(GUIBundle.getString(keys[j])); // NOI18N
                    if (m.containsKey(ta)) {
                        if (m.get(ta).contains(days[_j])) {
                            item.setSelected(true);
                        }
                    }
                    item.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (ta == TA.SMA) {
                                updateSMA(days[_j], item.isSelected());
                            }
                            else if (ta == TA.EMA) {
                                updateEMA(days[_j], item.isSelected());
                            }
                            else if (ta == TA.MFI) {
                                updateMFI(days[_j], item.isSelected());
                            }
                            else if (ta == TA.RSI) {
                                updateRSI(days[_j], item.isSelected());
                            }
                            else if (ta == TA.CCI) {
                                updateCCI(days[_j], item.isSelected());
                            }
                        }
                    });
                    menu.add(item);
                }   // for
            }   // if (ta == TA.MACD)
            
            menu.add(new javax.swing.JSeparator());
            javax.swing.JMenuItem item = new javax.swing.JMenuItem();
            item.setText(GUIBundle.getString("ChartJDialog_Custom...")); // NOI18N
            final int _i = i;
            item.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (ta == TA.MACD) {
                        showMACDCustomDialog();
                    } else {
                        do {                        
                            final String days_string = JOptionPane.showInputDialog(ChartJDialog.this, MessagesBundle.getString(custom_message_keys[_i]));
                            if (days_string == null) {
                                return;
                            }
                            try {
                                final int days = Integer.parseInt(days_string);
                                if (days <= 0) {
                                    JOptionPane.showMessageDialog(ChartJDialog.this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                                    continue;
                                }
                                ChartJDialog.this.updateTA(ta, days, true);
                                return;
                            }
                            catch (java.lang.NumberFormatException exp) {
                                log.error(null, exp);
                                JOptionPane.showMessageDialog(ChartJDialog.this, MessagesBundle.getString("info_message_number_of_days_required"), MessagesBundle.getString("info_title_number_of_days_required"), JOptionPane.WARNING_MESSAGE);
                                continue;
                            }
                        } while(true);
                    }
                }
            });
            menu.add(item);

            // TEMP DISABLE MACD
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (ta != TA.MACD) {
                this.jMenu2.add(menu);
            }
        }   // for
        this.jMenu2.add(new javax.swing.JSeparator());
        javax.swing.JMenuItem item = new javax.swing.JMenuItem();
        item.setText(GUIBundle.getString("ChartJDialog_ClearAll")); // NOI18N
        item.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChartJDialog.this.clearAll();
            }
        });
        this.jMenu2.add(item);
    }

    private void updateTA(TA ta, int days, boolean show) {
        if (ta == TA.SMA) {
            updateSMA(days, show);
        }
        else if (ta == TA.EMA) {
            updateEMA(days, show);
        }
        else if (ta == TA.MFI) {
            updateMFI(days, show);
        }
        else if (ta == TA.RSI) {
            updateRSI(days, show);
        }
        else if (ta == TA.CCI) {
            updateCCI(days, show);
        }
    }

    /**
     * Restores previous chart dialog options.
     */
    private void loadChartJDialogOptions() {
        final ChartJDialogOptions chartJDialogOptions = JStock.instance().getChartJDialogOptions();
        /* Zoom in to which days? */
        ChartJDialog.this.zoom(chartJDialogOptions.getZoom());
        /* Restore TA information. */
        final int TAExSize = chartJDialogOptions.getTAExSize();
        for (int i = 0; i < TAExSize; i++) {
            final TAEx taEx = chartJDialogOptions.getTAEx(i);
            final TA ta = taEx.getTA();
            if (ta == TA.MACD) {
                final MACD.Period period = (MACD.Period)taEx.getParameter();
                ChartJDialog.this.updateMACD(period, true);                
            } else {
                final Integer day = (Integer)taEx.getParameter();
                ChartJDialog.this.updateTA(ta, day, true);
            }
        }
    }

    public List<ChartData> getChartDatas() {
        return Collections.unmodifiableList(chartDatas);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
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
        jLabel5 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jLabel6 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jLabel14 = new org.yccheok.jstock.gui.charting.HyperlinkLikedJLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
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

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setForeground(JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR);
        jPanel3.add(jLabel2);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
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

        jComboBox1.setModel(getComboBoxModel());
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel5.add(jComboBox1);

        jPanel4.add(jPanel5, java.awt.BorderLayout.EAST);

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        jLabel9.setText(bundle.getString("ChartJDialog_7Days")); // NOI18N
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel9);

        jLabel10.setText(bundle.getString("ChartJDialog_1Month")); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel10);

        jLabel11.setText(bundle.getString("ChartJDialog_3Months")); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel11);

        jLabel12.setText(bundle.getString("ChartJDialog_6Months")); // NOI18N
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel12);

        jLabel13.setText(bundle.getString("ChartJDialog_1Year")); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel13);

        jLabel5.setText(bundle.getString("ChartJDialog_5Years")); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel5);

        jLabel6.setText(bundle.getString("ChartJDialog_10Years")); // NOI18N
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel6);

        jLabel14.setText(bundle.getString("ChartJDialog_All")); // NOI18N
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel14);

        jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jMenu1.setText(bundle.getString("ChartJDialog_File")); // NOI18N

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filesave.png"))); // NOI18N
        jMenuItem1.setText(bundle.getString("ChartJDialog_SaveAs...")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("ChartJDialog_TechnicalAnalysis")); // NOI18N
        jMenu2.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu2MenuSelected(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        jMenu3.setText(bundle.getString("ChartJDialog_Type")); // NOI18N

        buttonGroup1.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText(bundle.getString("ChartJDialog_PriceVolume")); // NOI18N
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem1);

        buttonGroup1.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText(bundle.getString("ChartJDialog_Candlestick")); // NOI18N
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem2);

        jMenuBar1.add(jMenu3);

        jMenu4.setText(bundle.getString("ChartJDialog_Theme")); // NOI18N

        buttonGroup2.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setSelected(true);
        jRadioButtonMenuItem3.setText(bundle.getString("ChartJDialog_Light")); // NOI18N
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonMenuItem3);

        buttonGroup2.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setText(bundle.getString("ChartJDialog_Dark")); // NOI18N
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonMenuItem4);

        jMenuBar1.add(jMenu4);

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

        setSize(new java.awt.Dimension(750, 600));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void initKeyBindings() {
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            // close the frame when the user presses escape
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }; 

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }
    
    private ComboBoxModel getComboBoxModel() {
        return new javax.swing.DefaultComboBoxModel(new String[] { 
            GUIBundle.getString("ChartJDialog_Daily"),
            GUIBundle.getString("ChartJDialog_Weekly"),
            GUIBundle.getString("ChartJDialog_Monthly")
        });
    }

    private void changeInterval(Interval interval) {
        if (interval == getCurrentInterval()) {
            // Same as current interval. Nothing to do. Returns early.
            return;
        }
        
        if (interval == Interval.Weekly) {
            this.chartDatas = org.yccheok.jstock.charting.Utils.getWeeklyChartData(this.stockHistoryServer);
        } else if (interval == Interval.Monthly) {
            this.chartDatas = org.yccheok.jstock.charting.Utils.getMonthlyChartData(this.stockHistoryServer);
        } else {
            assert(interval == Interval.Daily);
            this.chartDatas = org.yccheok.jstock.charting.Utils.getDailyChartData(this.stockHistoryServer);
        }
        
        this.priceTimeSeries = this.getPriceTimeSeries(this.chartDatas);
        this.priceDataset = new TimeSeriesCollection(this.priceTimeSeries);
        this.priceOHLCDataset = this.getOHLCDataset(this.chartDatas);
        this.volumeDataset = this.getVolumeDataset(this.chartDatas);
        // Throw away all the old data.
        this.priceVolumeChart = null;
        this.candlestickChart = null;
        changeType(JStock.instance().getChartJDialogOptions().getType());
        // and throw away all the old TAs.
        time_series_moving_average_map.clear();
        time_series_exponential_moving_average_map.clear();
        xydata_index_map.clear();
        activeTAExs.clear();
        price_volume_ta_map.clear();
        candlestick_ta_map.clear();
        // We need to rebuild TA menus.
        this.jMenu2.removeAll();

        /* Remember the setting. */
        JStock.instance().getChartJDialogOptions().setInterval(interval);
        /* Remember current interval. */
        this.currentInverval = interval;

        // Update the GUI.
        // Only done this after "Remember current interval".
        // As this will trigger actionPerformed event. We will use currentInverval
        // to prevent it from re-entering this method.
        if (interval == Interval.Daily) {
            this.jComboBox1.setSelectedItem(GUIBundle.getString("ChartJDialog_Daily"));
        }
        else if(interval == Interval.Weekly) {
            this.jComboBox1.setSelectedItem(GUIBundle.getString("ChartJDialog_Weekly"));
        }
        else if(interval == Interval.Monthly) {
            this.jComboBox1.setSelectedItem(GUIBundle.getString("ChartJDialog_Monthly"));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {                        
                        loadChartJDialogOptions();
                    }
                });
            }
        }).start();
    }

    /**
     * Changes the type (price volume or candlestick chart) of this chart dialog.
     * @param the type (price volume or candlestick chart) of this chart dialog to be changed.
     */
    private void changeType(Type type) {
        if (type == this.getCurrentType()) {
            if (this.priceVolumeChart == null && type == Type.PriceVolume) {
                // Do not returns early! Chart creation is needed.
            } else if (this.candlestickChart == null && type == Type.Candlestick) {
                // Do not returns early! Chart creation is needed.
            } else {
                // Same as current type. Nothing to do. Returns early.
                return;
            }
        }

        if (type == Type.PriceVolume) {
            if (this.priceVolumeChart == null) {
                this.priceVolumeChart = this.createPriceVolumeChart(this.priceDataset, this.volumeDataset);
            }
            chartPanel.setChart(this.priceVolumeChart);
            // Make chartPanel able to receive key event.
            // So that we may use arrow key to move around yellow information box.
            chartPanel.requestFocus();
            if (this.chartLayerUI != null) {
                this.chartLayerUI.updateTraceInfos();
            }
        } else if (type == Type.Candlestick) {
            if (this.candlestickChart == null) {
                this.candlestickChart = this.createCandlestickChart(this.priceOHLCDataset);
            }
            chartPanel.setChart(this.candlestickChart);
            // Make chartPanel able to receive key event.
            // So that we may use arrow key to move around yellow information box.
            chartPanel.requestFocus();            
            if (this.chartLayerUI != null) {
                this.chartLayerUI.updateTraceInfos();
            }
        }

        /* Remember the setting. */
        JStock.instance().getChartJDialogOptions().setType(type);
        /* Remember current interval. */
        this.currentType = type;

        /* Update the GUI. */
        if (type == Type.PriceVolume) {
            this.jRadioButtonMenuItem1.setSelected(true);
        }
        else if (type == Type.Candlestick) {
            this.jRadioButtonMenuItem2.setSelected(true);
        }
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        final String selected = this.jComboBox1.getSelectedItem().toString();
        Interval interval = Interval.Daily;
        if (selected.equals(GUIBundle.getString("ChartJDialog_Daily"))) {
            interval = Interval.Daily;
        }
        else if(selected.equals(GUIBundle.getString("ChartJDialog_Weekly"))) {
            interval = Interval.Weekly;
        }
        else if(selected.equals(GUIBundle.getString("ChartJDialog_Monthly"))) {
            interval = Interval.Monthly;
        }
        this.changeInterval(interval);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        assert(this.stockHistoryServer.size() > 0);
        final Stock stock = this.stockHistoryServer.getStock(this.stockHistoryServer.getTimestamp(0));
        final File file = org.yccheok.jstock.gui.Utils.promptSaveCSVAndExcelJFileChooser(stock.code.toString());

        if (file != null) {
            if (org.yccheok.jstock.gui.Utils.getFileExtension(file).equals("csv"))
            {
                final Statements statements = Statements.newInstanceFromStockHistoryServer(stockHistoryServer, false);
                statements.saveAsCSVFile(file);
            }
            else if (org.yccheok.jstock.gui.Utils.getFileExtension(file).equals("xls"))
            {
                final Statements statements = Statements.newInstanceFromStockHistoryServer(stockHistoryServer, false);
                statements.saveAsExcelFile(file, stock.code.toString());
            }
            else
            {
                assert(false);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * Clear all technical analysis information.
     */
    private void clearAll() {
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
            else if (taEx.ta == TA.MACD) {
                this.updateMACD((MACD.Period)taEx.parameter, false);
                i--;                
            }
            else {
                assert(false);
            }
        }
        // Update menus.
        for (int i = 0, count = jMenu2.getItemCount(); i < count; i++) {
            final JMenuItem item = jMenu2.getItem(i);
            if (item instanceof JMenu) {
                final JMenu m = (JMenu)item;
                for (int j = 0, count2 = m.getItemCount(); j < count2; j++) {
                    JMenuItem item2 = m.getItem(j);
                    if (item2 instanceof javax.swing.JCheckBoxMenuItem) {
                        m.getItem(j).setSelected(false);
                    }
                }
            }

        }
    }

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        org.yccheok.jstock.gui.Utils.launchWebBrowser(Utils.getURL(org.yccheok.jstock.network.Utils.Type.MA_INDICATOR_HTML));
    }//GEN-LAST:event_jMenuItem7ActionPerformed

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

        double high_price = -Double.MAX_VALUE;
        double low_price = Double.MAX_VALUE;
        final DefaultHighLowDataset defaultHighLowDataset = (DefaultHighLowDataset)this.priceOHLCDataset;
        for (int i = best_mid; i >= 0; i--) {
            final TimeSeriesDataItem item = this.priceTimeSeries.getDataItem(i);
            final long time = ((Day)item.getPeriod()).getFirstMillisecond();
            if (time < lowerBound) {
                break;
            }
            
            final double _high_price = defaultHighLowDataset.getHighValue(0, i);
            final double _low_price = defaultHighLowDataset.getLowValue(0, i);
            final double _last_price = defaultHighLowDataset.getCloseValue(0, i);

            high_price = Math.max(high_price, _high_price);

            // Prevent bad data.
            if (_low_price > 0) {
                low_price = Math.min(low_price, _low_price);
            } else {
                if (_high_price > 0) {
                    low_price = Math.min(low_price, _high_price);
                }
                if (_last_price > 0) {
                    low_price = Math.min(low_price, _last_price);
                }
            }
        }

        final double h = high_price;
        final double l = low_price;
        if (high_price >= low_price) {
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
        JLabel[] labels = {jLabel9, jLabel10, jLabel11, jLabel12, jLabel13, jLabel14, jLabel5, jLabel6};
        for (JLabel label : labels) {
            final Font oldFont = label.getFont();
            // Reset BOLD attribute.
            final Font font = oldFont.deriveFont(oldFont.getStyle() & ~Font.BOLD);
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

        case Year5:
            this._zoom(Calendar.YEAR, -5);
            /* Reset first. */
            this.resetAllDayLabels();
            this.jLabel5.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel5.getFont()));
            break;

        case Year10:
            this._zoom(Calendar.YEAR, -10);
            /* Reset first. */
            this.resetAllDayLabels();
            this.jLabel6.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel6.getFont()));
            break;

        case All:
            this.chartPanel.restoreAutoBounds();
            /* Reset first. */
            this.resetAllDayLabels();
            /* Bold the target. */
            this.jLabel14.setFont(org.yccheok.jstock.gui.Utils.getBoldFont(this.jLabel14.getFont()));
            break;
        }
        JStock.instance().getChartJDialogOptions().setZoom(z);
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
        final long end = day.getFirstMillisecond() + (this.getCurrentType() == Type.Candlestick ? (1000 * 60 * 60 * 12) : (1000 * 60 * 60 * 24 - 1));
        final Calendar calendar = Calendar.getInstance();
        // -1. Calendar's month is 0 based but JFreeChart's month is 1 based.
        calendar.set(day.getYear(), day.getMonth() - 1, day.getDayOfMonth(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(field, amount);
        // Candle stick takes up half day space.
        // Volume price chart's volume information does not take up any space.
        final long start = Math.max(0, calendar.getTimeInMillis() - (this.getCurrentType() == Type.Candlestick ? (1000 * 60 * 60 * 12) : 0));
        final ValueAxis valueAxis = this.getPlot().getDomainAxis();

        if (priceTimeSeries.getItemCount() > 0) {
            if (start < priceTimeSeries.getTimePeriod(0).getFirstMillisecond()) {
                // To prevent zoom-out too much.
                // This happens when user demands for 10 years zoom, where we
                // are only having 5 years data.
                return;
            }
        }
        
        valueAxis.setRange(start, end);

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double max_volume = Double.MIN_VALUE;
        final DefaultHighLowDataset defaultHighLowDataset = (DefaultHighLowDataset)this.priceOHLCDataset;
        
        for (int i = itemCount - 1; i >= 0; i--) {
            final TimeSeriesDataItem item = this.priceTimeSeries.getDataItem(i);
            final Day d = (Day)item.getPeriod();
            if (d.getFirstMillisecond() < start) {
                break;
            }
            
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

    private void jMenu2MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu2MenuSelected
        if (this.jMenu2.getItemCount() <= 0) {
            this.buildTAMenuItems();
        }
    }//GEN-LAST:event_jMenu2MenuSelected

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        this.changeType(Type.PriceVolume);
        this.zoom(JStock.instance().getChartJDialogOptions().getZoom());
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        this.changeType(Type.Candlestick);
        this.zoom(JStock.instance().getChartJDialogOptions().getZoom());
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        this.zoom(Zoom.Year5);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        this.zoom(Zoom.Year10);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        JStock.instance().getJStockOptions().setChartTheme(JStockOptions.ChartTheme.Light);
        if (priceVolumeChart != null) {
            org.yccheok.jstock.charting.Utils.applyChartThemeEx(priceVolumeChart);
        }
        if (candlestickChart != null) {
            org.yccheok.jstock.charting.Utils.applyChartThemeEx(candlestickChart);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
        JStock.instance().getJStockOptions().setChartTheme(JStockOptions.ChartTheme.Dark);
        if (priceVolumeChart != null) {
            org.yccheok.jstock.charting.Utils.applyChartThemeEx(priceVolumeChart);
        }
        if (candlestickChart != null) {
            org.yccheok.jstock.charting.Utils.applyChartThemeEx(candlestickChart);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // To avoid memory leak.
        parent.removeWindowListener(this);
        this.dispose();
    }//GEN-LAST:event_formWindowClosing
   
    /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createPriceVolumeChart(XYDataset priceDataset, XYDataset volumeDataset) {
        final String title = getBestStockName();

        final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
        timeAxis.setLowerMargin(0.02);                  // reduce the default margins
        timeAxis.setUpperMargin(0.02);
        
        final NumberAxis rangeAxis1 = new NumberAxis(GUIBundle.getString("ChartJDialog_Price"));
        rangeAxis1.setAutoRangeIncludesZero(false);     // override default
        rangeAxis1.setLowerMargin(0.40);                // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("0.00#");
        rangeAxis1.setNumberFormatOverride(format);

        XYPlot plot = new XYPlot(priceDataset, timeAxis, rangeAxis1, null);

        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
        renderer1.setDefaultToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
            )
        );
        plot.setRenderer(0, renderer1);

        final NumberAxis rangeAxis2 = new NumberAxis("Volume");
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(1, rangeAxis2);
        plot.setDataset(1, volumeDataset);
        plot.mapDatasetToRangeAxis(1, 1);

        XYBarRenderer renderer2 = new XYBarRenderer(0.20);
        renderer2.setDefaultToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")
            )
        );
        plot.setRenderer(1, renderer2);
        
        CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(timeAxis);
        cplot.add(plot, 1);
        cplot.setGap(8.0);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, cplot, true);
        org.yccheok.jstock.charting.Utils.applyChartThemeEx(chart);

        // Only do it after applying chart theme.
        org.yccheok.jstock.charting.Utils.setPriceSeriesPaint(renderer1);
        org.yccheok.jstock.charting.Utils.setVolumeSeriesPaint(renderer2);
        
        // Handle zooming event.
        chart.addChangeListener(this.getChartChangeListner());

        return chart;
    }

    private TimeSeries getPriceTimeSeries(List<ChartData> chartDatas) {
        // create dataset 1...
        TimeSeries series1 = new TimeSeries(GUIBundle.getString("ChartJDialog_Price"));
        
        for (ChartData chartData : chartDatas) {
            series1.addOrUpdate(new Day(new Date(chartData.timestamp)), chartData.lastPrice);
        }
        return series1;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset getVolumeDataset(List<ChartData> chartDatas) {

        // create dataset 2...
        TimeSeries series1 = new TimeSeries(GUIBundle.getString("ChartJDialog_Volume"));

        for (ChartData chartData : chartDatas) {
            series1.addOrUpdate(new Day(new Date(chartData.timestamp)), chartData.volume);
        }

        return new TimeSeriesCollection(series1);
    }
    
    private String getBestStockName() {
        final int num = stockHistoryServer.size();
        final Stock stock = stockHistoryServer.getStock(stockHistoryServer.getTimestamp(num - 1));

        if (org.yccheok.jstock.engine.Utils.isNameImmutable()) {
            final StockNameDatabase stockNameDatabase = JStock.instance().getStockNameDatabase();
            if (stockNameDatabase != null) {
                final String name = stockNameDatabase.codeToName(stock.code);
                if (name != null) {
                    return name;
                }
            }
        }

        final String name = stock.getName();
        // For unknown reason, server may return us empty stock name.
        if (name.isEmpty()) {
            final Symbol symbol = stock.symbol;
            if (false == symbol.toString().isEmpty()) {
                // Luckly. The symbol is not empty. Use it as replacement to
                // name.
                return symbol.toString();
            }
            // Symbol from server is empty. We need to ask help from offline
            // database.
            final StockInfoDatabase stockInfoDatabase = JStock.instance().getStockInfoDatabase();
            if (stockInfoDatabase != null) {
                final Symbol s = stockInfoDatabase.codeToSymbol(stock.code);
                if (s != null) {
                    // Use symbol as replacement if possible.
                    return s.toString();
                }
            }
            // If not, we will just apply code as replacement.
            return stock.code.toString();
        }

        return name;
    }

    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The dataset.
     */
    private JFreeChart createCandlestickChart(OHLCDataset priceOHLCDataset) {
        final String title = getBestStockName();
        
        final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
        final NumberAxis valueAxis = new NumberAxis(GUIBundle.getString("ChartJDialog_Price"));
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setUpperMargin(0.0);
        valueAxis.setLowerMargin(0.0);
        XYPlot plot = new XYPlot(priceOHLCDataset, timeAxis, valueAxis, null);

        final CandlestickRenderer candlestickRenderer = new CandlestickRenderer();
        plot.setRenderer(candlestickRenderer);
        
        // Give good width when zoom in, but too slow in calculation.
        ((CandlestickRenderer)plot.getRenderer()).setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);

        CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(timeAxis);
        cplot.add(plot, 3);
        cplot.setGap(8.0);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, cplot, true);

        org.yccheok.jstock.charting.Utils.applyChartThemeEx(chart);

        // Handle zooming event.
        chart.addChangeListener(this.getChartChangeListner());

        return chart;        
    }
    
    /**
     * Creates a sample high low dataset.
     *
     * @return a sample high low dataset.
     */
    private OHLCDataset getOHLCDataset(List<ChartData> chartDatas) {

        final int size = chartDatas.size();
        
        Date[] date = new Date[size];
        double[] high = new double[size];
        double[] low = new double[size];
        double[] open = new double[size];
        double[] close = new double[size];
        double[] volume = new double[size];

        int i = 0;
        for(ChartData chartData : chartDatas) {
            date[i] = new Date(chartData.timestamp);
            high[i] = chartData.highPrice;
            low[i] = chartData.lowPrice;
            open[i] = chartData.openPrice;
            close[i] = chartData.lastPrice;
            volume[i] = chartData.volume;
            i++;
        }
        
        return new DefaultHighLowDataset(GUIBundle.getString("ChartJDialog_Price"), date, high, low, open,
                close, volume);
    }

    // For Candlestick chart usage.
    private int getIndex(TAEx taEx, Map<TAEx, Integer> map) {
        if (map.containsKey(taEx)) {
            return map.get(taEx);
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
        Interval interval = this.getCurrentInterval();
        String c = "d";
        if (interval == Interval.Weekly) {
            c = "w";
        } else if (interval == Interval.Monthly) {
            c = "m";
        }
        return days + c + " EMA";
    }

    private String getSMAKey(int days) {
        Interval interval = this.getCurrentInterval();
        String c = "d";
        if (interval == Interval.Weekly) {
            c = "w";
        } else if (interval == Interval.Monthly) {
            c = "m";
        }
        return days + c + " SMA";
    }

    private String getRSIKey(int days) {
        Interval interval = this.getCurrentInterval();
        String c = "d";
        if (interval == Interval.Weekly) {
            c = "w";
        } else if (interval == Interval.Monthly) {
            c = "m";
        }
        return days + c + " RSI";
    }

    private String getMACDKey(MACD.Period period) {
        Interval interval = this.getCurrentInterval();
        String c = "d";
        if (interval == Interval.Weekly) {
            c = "w";
        } else if (interval == Interval.Monthly) {
            c = "m";
        }
        return "(" + period.fastPeriod + "," + period.slowPeriod + "," + period.period + ")";
    }
    
    private String getCCIKey(int days) {
        Interval interval = this.getCurrentInterval();
        String c = "d";
        if (interval == Interval.Weekly) {
            c = "w";
        } else if (interval == Interval.Monthly) {
            c = "m";
        }
        return days + c + " CCI";
    }

    private String getMFIKey(int days) {
        Interval interval = this.getCurrentInterval();
        String c = "d";
        if (interval == Interval.Weekly) {
            c = "w";
        } else if (interval == Interval.Monthly) {
            c = "m";
        }
        return days + c + " MFI";
    }

    private void updateCCI(int days, boolean show) {
        if (this.priceVolumeChart == null) {
            this.priceVolumeChart = this.createPriceVolumeChart(this.priceDataset, this.volumeDataset);
        }
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(this.priceOHLCDataset);
        }

        final TAEx taEx = TAEx.newInstance(TA.CCI, new Integer(days));

        if (show) {
            if (price_volume_ta_map.containsKey(taEx) == false) {
                final XYDataset dataset = org.yccheok.jstock.charting.TechnicalAnalysis.createCCI(this.chartDatas, getCCIKey(days), days);
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
                renderer1.setDefaultToolTipGenerator(
                    new StandardXYToolTipGenerator(
                        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
                    )
                );
                plot.setRenderer(0, renderer1);
                org.yccheok.jstock.charting.Utils.setPriceSeriesPaint(renderer1);
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

            if (this.activeTAExs.contains(taEx) == false)
            {
                // Avoid duplication.
                final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
                final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);


                final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
                final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();
                if (price_volume_ta != null) cplot0.add(price_volume_ta, 1);    // weight is 1.
                if (candlestick_ta != null) cplot1.add(candlestick_ta, 1);      // weight is 1.
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.priceVolumeChart);
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.candlestickChart);
            }
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
            JStock.instance().getChartJDialogOptions().add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
            JStock.instance().getChartJDialogOptions().remove(taEx);
        }
    }

    private void updateMFI(int days, boolean show) {
        if (this.priceVolumeChart == null) {
            this.priceVolumeChart = this.createPriceVolumeChart(this.priceDataset, this.volumeDataset);
        }
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(this.priceOHLCDataset);
        }

        final TAEx taEx = TAEx.newInstance(TA.MFI, new Integer(days));

        if (show) {
            if (price_volume_ta_map.containsKey(taEx) == false) {
                final XYDataset dataset = org.yccheok.jstock.charting.TechnicalAnalysis.createMFI(this.chartDatas, getMFIKey(days), days);
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
                renderer1.setDefaultToolTipGenerator(
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

            if (this.activeTAExs.contains(taEx) == false)
            {
                // Avoid duplication.
                final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
                final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

                final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
                final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();
                if (price_volume_ta != null) cplot0.add(price_volume_ta, 1);    // weight is 1.
                if (candlestick_ta != null) cplot1.add(candlestick_ta, 1);      // weight is 1.
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.priceVolumeChart);
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.candlestickChart);
            }
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
            JStock.instance().getChartJDialogOptions().add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
            JStock.instance().getChartJDialogOptions().remove(taEx);
        }
    }

    private void updateRSI(int days, boolean show) {
        if (this.priceVolumeChart == null) {
            this.priceVolumeChart = this.createPriceVolumeChart(this.priceDataset, this.volumeDataset);
        }
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(this.priceOHLCDataset);
        }

        final TAEx taEx = TAEx.newInstance(TA.RSI, new Integer(days));

        if (show) {
            if (price_volume_ta_map.containsKey(taEx) == false) {
                final XYDataset dataset = org.yccheok.jstock.charting.TechnicalAnalysis.createRSI(this.chartDatas, getRSIKey(days), days);
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
                renderer1.setDefaultToolTipGenerator(
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

            if (this.activeTAExs.contains(taEx) == false)
            {
                // Avoid duplication.
                final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
                final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);
                final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
                final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();

                if (price_volume_ta != null) cplot0.add(price_volume_ta, 1);    // weight is 1.
                if (candlestick_ta != null) cplot1.add(candlestick_ta, 1);      // weight is 1.
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.priceVolumeChart);
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.candlestickChart);
            }
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
            JStock.instance().getChartJDialogOptions().add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
            JStock.instance().getChartJDialogOptions().remove(taEx);
        }
    }

    private void showMACDCustomDialog() {        
        System.out.println("showMACDCustomDialog");
    }
    
    // VERY BUGGY CODE STILL! :p
    private void updateMACD(MACD.Period period, boolean show) {
        if (this.priceVolumeChart == null) {
            this.priceVolumeChart = this.createPriceVolumeChart(this.priceDataset, this.volumeDataset);
        }
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(this.priceOHLCDataset);
        }
        
        final TAEx taEx = TAEx.newInstance(TA.MACD, period);
        if (show) {
            if (price_volume_ta_map.containsKey(taEx) == false) {                
                final MACD.ChartResult macdChartResult = org.yccheok.jstock.charting.TechnicalAnalysis.createMACD(this.chartDatas, getMACDKey(period), period);

                // MACD!
                NumberAxis rangeAxis1 = new NumberAxis(GUIBundle.getString("ChartJDialog_MACD"));
                rangeAxis1.setAutoRangeIncludesZero(false);     // override default
                rangeAxis1.setLowerMargin(0.40);                // to leave room for volume bars
                DecimalFormat format = new DecimalFormat("0.00#");
                rangeAxis1.setNumberFormatOverride(format);

                final ValueAxis timeAxis = new DateAxis(GUIBundle.getString("ChartJDialog_Date"));
                timeAxis.setLowerMargin(0.02);                  // reduce the default margins
                timeAxis.setUpperMargin(0.02);

                XYPlot plot = new XYPlot(macdChartResult.outMACD, timeAxis, rangeAxis1, null);

                XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
                renderer1.setDefaultToolTipGenerator(
                    new StandardXYToolTipGenerator(
                        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
                    )
                );
                plot.setRenderer(0, renderer1);
                org.yccheok.jstock.charting.Utils.setPriceSeriesPaint(renderer1);
                
                // MACD SIGNAL!
                plot.setDataset(1, macdChartResult.outMACDSignal);
                XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
                renderer2.setDefaultToolTipGenerator(
                    new StandardXYToolTipGenerator(
                        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00#")
                    )
                );                
                plot.setRenderer(1, renderer2);
                
                // VOLUME!
                //plot.setRangeAxis(1, rangeAxis1);
                plot.setDataset(2, macdChartResult.outMACDHist);
                //plot.mapDatasetToRangeAxis(1, 1);

                XYBarRenderer renderer3 = new XYBarRenderer(0.20);
                
                renderer3.setDefaultToolTipGenerator(
                    new StandardXYToolTipGenerator(
                        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")
                    )
                );
                plot.setRenderer(2, renderer3);                
                

                
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
            
            if (this.activeTAExs.contains(taEx) == false)
            {
                // Avoid duplication.
                final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
                final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);


                final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
                final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();
                if (price_volume_ta != null) cplot0.add(price_volume_ta, 1);    // weight is 1.
                if (candlestick_ta != null) cplot1.add(candlestick_ta, 1);      // weight is 1.
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.priceVolumeChart);
                org.yccheok.jstock.charting.Utils.applyChartThemeEx(this.candlestickChart);
            }            
        } else {
            final CombinedDomainXYPlot cplot0 = (CombinedDomainXYPlot)this.priceVolumeChart.getPlot();
            final CombinedDomainXYPlot cplot1 = (CombinedDomainXYPlot)this.candlestickChart.getPlot();
            final XYPlot price_volume_ta = price_volume_ta_map.get(taEx);
            final XYPlot candlestick_ta = candlestick_ta_map.get(taEx);

            if (price_volume_ta != null) cplot0.remove(price_volume_ta);
            if (candlestick_ta != null) cplot1.remove(candlestick_ta);            
        }
        
        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
            JStock.instance().getChartJDialogOptions().add(taEx);
        } else if (!show) {
            this.activeTAExs.remove(taEx);
            JStock.instance().getChartJDialogOptions().remove(taEx);
        }        
    }
    
    private void updateEMA(int days, boolean show) {
        if (this.priceVolumeChart == null) {
            this.priceVolumeChart = this.createPriceVolumeChart(this.priceDataset, this.volumeDataset);
        }
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(this.priceOHLCDataset);
        }

        final TAEx taEx = TAEx.newInstance(TA.EMA, new Integer(days));
        if (show) {
            TimeSeries timeSeries = null;
            XYDataset dataSet = null;
            final Integer days_integer = days;
            if (false == time_series_exponential_moving_average_map.containsKey(days_integer)) {
                timeSeries = TechnicalAnalysis.createEMA(this.chartDatas, this.getEMAKey(days), days);
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
                    final int index = getIndex(taEx, this.xydata_index_map);
                    this.xydata_index_map.put(taEx, index);
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
                final Integer integer = this.xydata_index_map.get(taEx);
                if (integer != null) {
                    final int index = integer;
                    plot.setDataset(index, null);
                }
            }
        }

        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
            JStock.instance().getChartJDialogOptions().add(taEx);
        } else if (!show) {
            this.activeTAExs.remove(taEx);
            JStock.instance().getChartJDialogOptions().remove(taEx);
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
        if (this.priceVolumeChart == null) {
            this.priceVolumeChart = this.createPriceVolumeChart(this.priceDataset, this.volumeDataset);
        }
        if (this.candlestickChart == null) {
            this.candlestickChart = this.createCandlestickChart(this.priceOHLCDataset);
        }
        
        final TAEx taEx = TAEx.newInstance(TA.SMA, new Integer(days));
        if (show) {
            TimeSeries timeSeries = null;
            XYDataset dataSet = null;
            final Integer days_integer = days;
            if (false == time_series_moving_average_map.containsKey(days_integer)) {
                timeSeries = TechnicalAnalysis.createSMA(this.chartDatas, this.getSMAKey(days), days);
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
                    final int index = getIndex(taEx, this.xydata_index_map);
                    this.xydata_index_map.put(taEx, index);
                    plot.setDataset(index, dataSet);
                    plot.setRenderer(index, new StandardXYItemRenderer());
                }
            }
        }
        else {
            {
                final TimeSeries ts = ((TimeSeriesCollection)this.priceDataset).getSeries(this.getSMAKey(days));
                if (ts != null) {
                    ((TimeSeriesCollection)this.priceDataset).removeSeries(ts);
                }
            }
            {
                final Plot main_plot = (Plot)((CombinedDomainXYPlot)this.candlestickChart.getPlot()).getSubplots().get(0);
                final XYPlot plot = (XYPlot) main_plot;
                final Integer integer = this.xydata_index_map.get(taEx);
                if (integer != null) {
                    final int index = integer;
                    plot.setDataset(index, null);
                }
            }
        }

        if (show && this.activeTAExs.contains(taEx) == false) {
            this.activeTAExs.add(taEx);
            JStock.instance().getChartJDialogOptions().add(taEx);
        }
        else if (!show) {
            this.activeTAExs.remove(taEx);
            JStock.instance().getChartJDialogOptions().remove(taEx);
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

    private Interval currentInverval = null;
    private Type currentType = null;

    private final StockHistoryServer stockHistoryServer;
    /* Derived based on stockHistoryServer. */
    private List<ChartData> chartDatas;

    private final ChartPanel chartPanel;
    private final Map<Integer, TimeSeries> time_series_moving_average_map = new HashMap<Integer, TimeSeries>();
    private final Map<Integer, TimeSeries> time_series_exponential_moving_average_map = new HashMap<Integer, TimeSeries>();
    /* Days to series index. (For main plot only) */
    private final Map<TAEx, Integer> xydata_index_map = new HashMap<TAEx, Integer>();
    private final List<TAEx> activeTAExs = new ArrayList<TAEx>();
    private static final int MAX_MAP_SIZE = 20;

    private final Map<TAEx, XYPlot> price_volume_ta_map = new HashMap<TAEx, XYPlot>();
    /* Not sure why. I cannot make priceVolumeChart and candlestickChart sharing the same
     * plot. If not, this will inhibit incorrect zooming behavior.
     */
    private final Map<TAEx, XYPlot> candlestick_ta_map = new HashMap<TAEx, XYPlot>();

    private XYDataset priceDataset;
    private XYDataset volumeDataset;
    private TimeSeries priceTimeSeries;
    private OHLCDataset priceOHLCDataset;
    private JFreeChart priceVolumeChart;
    private JFreeChart candlestickChart;

    /**
     * Thread pool, used to hold threads to update high low labels.
     */
    private final Executor updateHighLowLabelsPool = Executors.newFixedThreadPool(1);

    /* Overlay layer. */
    private final ChartLayerUI<ChartPanel> chartLayerUI;

    /* To avoid memory leak. */
    private final java.awt.Frame parent;
    
    private static final Log log = LogFactory.getLog(ChartJDialog.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    // End of variables declaration//GEN-END:variables
    
}
