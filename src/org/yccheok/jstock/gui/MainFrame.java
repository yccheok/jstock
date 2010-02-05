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

package org.yccheok.jstock.gui;

import org.yccheok.jstock.alert.GoogleMail;
import org.yccheok.jstock.alert.GoogleCalendar;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import org.yccheok.jstock.engine.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.help.*;
import javax.swing.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.yccheok.jstock.alert.SMSLimiter;
import org.yccheok.jstock.analysis.Indicator;
import org.yccheok.jstock.analysis.OperatorIndicator;
import org.yccheok.jstock.file.Statement;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.dynamicchart.DynamicChart;
import org.yccheok.jstock.gui.portfolio.PortfolioJDialog;
import org.yccheok.jstock.gui.table.NonNegativeDoubleEditor;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.network.ProxyDetector;

/**
 *
 * @author  doraemon
 */
public class MainFrame extends javax.swing.JFrame {

    // Comment out, to avoid annoying log messages during debugging.
    //static { System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); }
    
    /** Creates new form MainFrame */

    // Private constructor is sufficient to suppress unauthorized calls to the constructor
    private MainFrame()
    {
    }

    private void init() {
        /* Workaround to solve JXTreeTable look n feel cannot be changed on the fly. */
        initJStockOptions();

        try {
            UIManager.setLookAndFeel(getJStockOptions().getLooknFeel());
        }
        catch(java.lang.ClassNotFoundException exp) {
            log.error("", exp);
        }
        catch(java.lang.InstantiationException exp) {
            log.error("", exp);
        }
        catch(java.lang.IllegalAccessException exp) {
            log.error("", exp);
        }
        catch(javax.swing.UnsupportedLookAndFeelException exp) {
            log.error("", exp);
        }

        initComponents();

        createLookAndFeelMenuItem();
        createCountryMenuItem();

        createStockIndicatorEditor();
        createIndicatorScannerJPanel();
        createPortfolioManagementJPanel();
        createChatJPanel();

        createIconsAndToolTipTextForJTabbedPane();

        this.createSystemTrayIcon();

        this.initPreloadDatabase();
        this.initChatDatas();
        this.initExtraDatas();
        this.initStatusBar();
        this.initMarketJPanel();
        this.initUsernameAndPassword();
        this.initTableHeaderToolTips();
        this.initjComboBox1EditorComponentKeyListerner();
        this.initMyJXStatusBarCountryLabelMouseAdapter();
        this.initMyJXStatusBarImageLabelMouseAdapter();
        this.initStockCodeAndSymbolDatabase(true);
        this.initMarketThread();
        this.initLatestNewsTask();
        this.initRealTimeStockMonitor();
        this.initRealTimeStocks();
        this.initAlertStateManager();
        this.initDynamicCharts();
        this.initStockHistoryMonitor();
        this.initOthersStockHistoryMonitor();
        this.initBrokingFirmLogos();
        this.initGUIOptions();

        // Turn to the last viewed page.
        this.jTabbedPane1.setSelectedIndex(this.getJStockOptions().getLastSelectedPageIndex());
    }

    /**
     * MainFrameHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to MainFrameHolder.INSTANCE, not before.
     */
    private static class MainFrameHolder {
        private final static MainFrame INSTANCE = new MainFrame();
    }

    public static MainFrame getInstance() {
        return MainFrameHolder.INSTANCE;
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
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel6 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new AutoCompleteJComboBox();
        jPanel10 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel27 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel21 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel20 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel22 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel28 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel36 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel31 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel18 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel24 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel23 = new org.yccheok.jstock.gui.GradientJLabel(new Color(111, 148, 182), new Color(184, 207, 229), (float)0.9);
        jLabel25 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("JStock - Stock Market Software");
        setFont(new java.awt.Font("Tahoma", 0, 12));
        setIconImage(getMyIconImage());
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel6.setLayout(new java.awt.BorderLayout(5, 5));
        this.jPanel6.add(statusBar, java.awt.BorderLayout.SOUTH);
        getContentPane().add(jPanel6, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel8.setLayout(new java.awt.BorderLayout(5, 5));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jTable1.setModel(new StockTableModel());
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        this.jTable1.setDefaultRenderer(Number.class, new StockTableCellRenderer());
        this.jTable1.setDefaultRenderer(Double.class, new StockTableCellRenderer());
        this.jTable1.setDefaultRenderer(Object.class, new StockTableCellRenderer());

        this.jTable1.setDefaultEditor(Double.class, new NonNegativeDoubleEditor());

        this.jTable1.getModel().addTableModelListener(this.getTableModelListener());

        this.jTable1.getTableHeader().addMouseListener(new TableColumnSelectionPopupListener(1));
        this.jTable1.addMouseListener(new TableRowPopupListener());
        this.jTable1.addKeyListener(new TableKeyEventListener());
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel8.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Stock");
        jPanel1.add(jLabel1);

        jComboBox1.setEditable(true);
        jComboBox1.setPreferredSize(new java.awt.Dimension(150, 24));
        this.jComboBox1.getEditor().getEditorComponent().addKeyListener(jComboBox1EditorComponentKeyAdapter);
        jPanel1.add(jComboBox1);

        jPanel8.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel10.setPreferredSize(new java.awt.Dimension(328, 170));
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(170, 160));
        jPanel3.setBorder(new org.jdesktop.swingx.border.DropShadowBorder(true));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel10.add(jPanel3);
        EMPTY_DYNAMIC_CHART.getChartPanel().addMouseListener(dynamicChartMouseAdapter);
        jPanel3.add(EMPTY_DYNAMIC_CHART.getChartPanel(), java.awt.BorderLayout.CENTER);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(new org.jdesktop.swingx.border.DropShadowBorder(true));
        jPanel7.setLayout(new java.awt.GridLayout(3, 7, 2, 2));

        jLabel27.setBackground(new java.awt.Color(184, 207, 229));
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jPanel7.add(jLabel27);

        jLabel21.setBackground(new java.awt.Color(184, 207, 229));
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("3rd Buy");
        jLabel21.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jLabel21.setFocusTraversalPolicyProvider(true);
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel7.add(jLabel21);

        jLabel20.setBackground(new java.awt.Color(184, 207, 229));
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("2nd Buy");
        jLabel20.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jLabel20.setFocusTraversalPolicyProvider(true);
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel7.add(jLabel20);

        jLabel22.setBackground(new java.awt.Color(184, 207, 229));
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("1st Buy");
        jLabel22.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jLabel22.setFocusTraversalPolicyProvider(true);
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel7.add(jLabel22);

        jLabel28.setBackground(new java.awt.Color(184, 207, 229));
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("1st Sell");
        jLabel28.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel7.add(jLabel28);

        jLabel36.setBackground(new java.awt.Color(184, 207, 229));
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("2nd Sell");
        jLabel36.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel7.add(jLabel36);

        jLabel31.setBackground(new java.awt.Color(184, 207, 229));
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("3rd Sell");
        jLabel31.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel7.add(jLabel31);

        jLabel18.setBackground(new java.awt.Color(184, 207, 229));
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Price");
        jPanel7.add(jLabel18);

        jLabel24.setBackground(new java.awt.Color(255, 255, 204));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setOpaque(true);
        jPanel7.add(jLabel24);

        jLabel33.setBackground(new java.awt.Color(255, 255, 204));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setOpaque(true);
        jPanel7.add(jLabel33);

        jLabel19.setBackground(new java.awt.Color(255, 255, 204));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setOpaque(true);
        jPanel7.add(jLabel19);

        jLabel32.setBackground(new java.awt.Color(255, 255, 204));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setOpaque(true);
        jPanel7.add(jLabel32);

        jLabel35.setBackground(new java.awt.Color(255, 255, 204));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setOpaque(true);
        jPanel7.add(jLabel35);

        jLabel37.setBackground(new java.awt.Color(255, 255, 204));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setOpaque(true);
        jPanel7.add(jLabel37);

        jLabel23.setBackground(new java.awt.Color(184, 207, 229));
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Qty");
        jPanel7.add(jLabel23);

        jLabel25.setBackground(new java.awt.Color(255, 255, 204));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setOpaque(true);
        jPanel7.add(jLabel25);

        jLabel34.setBackground(new java.awt.Color(255, 255, 204));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setOpaque(true);
        jPanel7.add(jLabel34);

        jLabel26.setBackground(new java.awt.Color(255, 255, 204));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setOpaque(true);
        jPanel7.add(jLabel26);

        jLabel29.setBackground(new java.awt.Color(255, 255, 204));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setOpaque(true);
        jPanel7.add(jLabel29);

        jLabel30.setBackground(new java.awt.Color(255, 255, 204));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setOpaque(true);
        jPanel7.add(jLabel30);

        jLabel38.setBackground(new java.awt.Color(255, 255, 204));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setOpaque(true);
        jPanel7.add(jLabel38);

        jPanel10.add(jPanel7);

        jPanel8.add(jPanel10, java.awt.BorderLayout.SOUTH);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        jTabbedPane1.addTab(bundle.getString("MainFrame_Title"), jPanel8); // NOI18N

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridLayout(2, 1));
        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        jMenu3.setText("File");

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/project_open.png"))); // NOI18N
        jMenuItem2.setText("Open...");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filesave.png"))); // NOI18N
        jMenuItem9.setText("Save As...");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);
        jMenu3.add(jSeparator2);

        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/download_from_cloud.png"))); // NOI18N
        jMenuItem11.setText(bundle.getString("MainFrame_OpenFromCloud...")); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/upload_to_cloud.png"))); // NOI18N
        jMenuItem10.setText(bundle.getString("MainFrame_SaveToCloud...")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);
        jMenu3.add(jSeparator3);

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuBar2.add(jMenu3);

        jMenu5.setText("Edit");

        jMenuItem4.setText("Add Stocks...");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem4);

        jMenuItem7.setText("Clear All Stocks");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem7);

        jMenuBar2.add(jMenu5);

        jMenu6.setText("Country");
        jMenuBar2.add(jMenu6);

        jMenu7.setText("Database");

        jMenuItem8.setText("Stock Database...");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem8);

        jMenuBar2.add(jMenu7);

        jMenu8.setText("Portfolio");
        jMenu8.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu8MenuSelected(evt);
            }
        });
        jMenuBar2.add(jMenu8);

        jMenu1.setText("Options");

        jMenuItem6.setText("Options...");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar2.add(jMenu1);

        jMenu4.setText("Look n Feel");
        jMenuBar2.add(jMenu4);

        jMenu2.setText("Help");

        jMenuItem3.setText("Help");
        javax.help.HelpBroker hb = this.getHelpBroker();
        if(hb != null) {
            this.jMenuItem3.addActionListener(
                new CSH.DisplayHelpFromSource(hb)
            );
        }
        jMenu2.add(jMenuItem3);
        jMenu2.add(jSeparator1);

        jMenuItem12.setText(bundle.getString("MainFrame_JStockHome")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem12);

        jMenuItem5.setText("About...");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar2.add(jMenu2);

        setJMenuBar(jMenuBar2);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-952)/2, (screenSize.height-478)/2, 952, 478);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
// TODO add your handling code here:
        if (this.getStockCodeAndSymbolDatabase() == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "We haven't connected to stock server.", "Not Connected", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StockJDialog stockJDialog = new StockJDialog(this, true);
        stockJDialog.setLocationRelativeTo(this);
        stockJDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
// TODO add your handling code here:
        this.clearAllStocks();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private boolean openAsCSVFile(File file) {
        final Statements statements = Statements.newInstanceFromCSVFile(file);
        return this.openAsStatements(statements, file);
    }

    public boolean openAsStatements(Statements statements, File file) {
        if (statements == null) {
            return false;
        }
        
        if (statements.getType() == Statement.Type.RealtimeInfo) {
            final int size = statements.size();
            for (int i = 0; i < size; i++) {
                final org.yccheok.jstock.file.Statement statement = statements.get(i);
                final String codeStr = statement.getValueAsString(GUIBundle.getString("MainFrame_Code"));
                final String symbolStr = statement.getValueAsString(GUIBundle.getString("MainFrame_Symbol"));
                final Double fallBelowDouble = statement.getValueAsDouble(GUIBundle.getString("MainFrame_FallBelow"));
                final Double riseAboveDouble = statement.getValueAsDouble(GUIBundle.getString("MainFrame_RiseAbove"));
                if (codeStr.length() > 0 && symbolStr.length() > 0) {
                    final Stock stock = Utils.getEmptyStock(Code.newInstance(codeStr), Symbol.newInstance(symbolStr));
                    final StockAlert stockAlert = new StockAlert().setFallBelow(fallBelowDouble).setRiseAbove(riseAboveDouble);
                    this.addStockToTable(stock, stockAlert);
                    realTimeStockMonitor.addStockCode(Code.newInstance(codeStr));
                }
            }
        }
        else if (statements.getType() == Statement.Type.PortfolioManagementBuy || statements.getType() == Statement.Type.PortfolioManagementSell || statements.getType() == Statement.Type.PortfolioManagementDeposit || statements.getType() == Statement.Type.PortfolioManagementDividend) {
            /* Open using other tabs. */
            return this.portfolioManagementJPanel.openAsStatements(statements, file);
        }
        else {
            return false;
        }
        return true;
    }
    
    private boolean openAsExcelFile(File file) {
        final java.util.List<Statements> statementsList = Statements.newInstanceFromExcelFile(file);
        boolean status = statementsList.size() > 0;
        for (Statements statements : statementsList) {
            status = status & this.openAsStatements(statements, file);
        }
        return status;
    }

    public RealTimeStockMonitor getRealTimeStockMonitor() {
        return realTimeStockMonitor;
    }
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    // TODO add your handling code here:
        final File file = Utils.promptOpenCSVAndExcelJFileChooser();
        if (file == null) {
            return;
        }
        boolean status = true;
        if (Utils.getFileExtension(file).equals("xls")) {
            if (this.getSelectedComponent() == this.jPanel8) {
                status = this.openAsExcelFile(file);
            }
            else if (this.getSelectedComponent() == this.portfolioManagementJPanel) {
                status = this.portfolioManagementJPanel.openAsExcelFile(file);
            }
            else {
                assert(false);
            }
        }
        else if(Utils.getFileExtension(file).equals("csv")) {
            if (this.getSelectedComponent() == this.jPanel8) {
                status = this.openAsCSVFile(file);
            }
            else if (this.getSelectedComponent() == this.portfolioManagementJPanel) {
                status = this.portfolioManagementJPanel.openAsCSVFile(file);
            }
            else {
                assert(false);
            }
        }
        else {
            assert(false);
        }

        if (false == status) {
            final String output = MessageFormat.format(MessagesBundle.getString("error_message_bad_file_format_template"), file.getName());
            JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("error_title_bad_file_format"), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    
    // Policy : Each pane should have their own real time stock monitoring.
    //
    //          Each pane should share history monitoring with main frame, 
    //          for optimized history retrieving purpose.
    //
    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        JTabbedPane pane = (JTabbedPane)evt.getSource();
        // MainFrame
        if (pane.getSelectedComponent() == this.jPanel8) {
            this.jMenuItem2.setEnabled(true);   // Load
            this.jMenuItem9.setEnabled(true);   // Save

            if (this.portfolioManagementJPanel != null) {
                this.portfolioManagementJPanel.softStop();
                log.info("Stop portfolio monitor.");
            }
        }
        else if (pane.getSelectedComponent() == this.indicatorPanel) {
            this.jMenuItem2.setEnabled(false);  // Load
            this.jMenuItem9.setEnabled(false);  // Save

            if (this.portfolioManagementJPanel != null) {
                this.portfolioManagementJPanel.softStop();
                log.info("Stop portfolio monitor.");
            }
        }
        else if(pane.getSelectedComponent() == this.indicatorScannerJPanel) {
            this.jMenuItem2.setEnabled(false);  // Load
            this.jMenuItem9.setEnabled(true);   // Save

            if (this.portfolioManagementJPanel != null) {
                this.portfolioManagementJPanel.softStop();
                log.info("Stop portfolio monitor.");
            }
        }
        else if(pane.getSelectedComponent() == this.portfolioManagementJPanel) {
            this.jMenuItem2.setEnabled(true);   // Load
            this.jMenuItem9.setEnabled(true);   // Save

            if (this.portfolioManagementJPanel != null) {
                this.portfolioManagementJPanel.softStart();
                log.info("Start portfolio monitor.");
            }
        }
        else if (pane.getSelectedComponent() == this.chatJPanel) {
            this.jMenuItem2.setEnabled(false);  // Save
            this.jMenuItem9.setEnabled(false);  // Load

            if (this.portfolioManagementJPanel != null) {
                this.portfolioManagementJPanel.softStop();
                log.info("Stop portfolio monitor.");
            }
        }

        if (pane.getSelectedComponent() == this.chatJPanel)
        {
            if (timer != null)
            {
                timer.stop();
                timer = null;
            }

            // Ensure at the end, we are using smile icon.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MainFrame.this.jTabbedPane1.setIconAt(4, smileIcon);
                }
            });
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged


    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
// TODO add your handling code here
        OptionsJDialog optionsJDialog = new OptionsJDialog(this, true);
        optionsJDialog.setLocationRelativeTo(this);
        optionsJDialog.set(jStockOptions);
        optionsJDialog.setVisible(true);		
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    public JStockOptions getJStockOptions() {
        return jStockOptions;
    }

    // windowClosing
    // Invoked when the user attempts to close the window from the window's system menu.
    //
    // windowClosed
    // Invoked when a window has been closed as the result of calling dispose on the window.
    //
    /* Dangerous! We didn't perform proper clean up, because we do not want
     * to give user perspective that our system is slow. But, is it safe
     * to do so?
     */
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        try {
            log.info("Widnow is closed...");

            //log.info("stop indicator scanner panel...");
            //this.indicatorScannerJPanel.stop();

            //log.info("stop indicator panel...");
            //this.indicatorPanel.stop();

            log.info("saveJStockOptions...");
            // Save the last viewed page.
            this.getJStockOptions().setLastSelectedPageIndex(this.jTabbedPane1.getSelectedIndex());
            this.saveJStockOptions();

            log.info("saveGUIOptions...");
            this.saveGUIOptions();

            log.info("saveBrokingFirmLogos...");
            this.saveBrokingFirmLogos();

            log.info("saveRealTimeStocks...");
            this.saveRealTimeStocks();

            log.info("saveIndicatorProjectManager...");
            this.indicatorPanel.saveAlertIndicatorProjectManager();

            log.info("saveModuleProjectManager...");
            this.indicatorPanel.saveModuleIndicatorProjectManager();

            log.info("savePortfolio...");
            this.portfolioManagementJPanel.savePortfolio();

            log.info("latestNewsTask stop...");
            if(this.latestNewsTask != null)
            {
                this.latestNewsTask.cancel(true);
            }

            //log.info("stockCodeAndSymbolDatabaseTask stop...");
            //stockCodeAndSymbolDatabaseTask._stop();

            //try {
            //    stockCodeAndSymbolDatabaseTask.get();
            //}
            //catch(InterruptedException exp) {
            //    log.error("", exp);
            //}
            //catch(java.util.concurrent.ExecutionException exp) {
            //    log.error("", exp);
            //}

            //log.info("marketThread stop...");
            //marketThread.interrupt();

            //try {
            //    marketThread.join();
            //}
            //catch(InterruptedException exp) {
            //    log.error("", exp);
            //}

            //log.info("realTimeStockMonitor stop...");
            //realTimeStockMonitor.stop();
            //log.info("stockHistoryMonitor stop...");
            //stockHistoryMonitor.stop();

            this.chatJPanel.stopChatServiceManager();

            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
            }

            // We suppose to call shutdownAll to clean up all network resources.
            // However, that will cause Exception in other threads if they are still using httpclient.
            // Exception in thread "Thread-4" java.lang.IllegalStateException: Connection factory has been shutdown.
            //
            // MultiThreadedHttpConnectionManager.shutdownAll();

            log.info("Widnow is closed.");
        }
        catch (Exception exp) {
            log.error("Unexpected error while trying to quit application", exp);
        }

        // All the above operations are done within try block, to ensure
        // System.exit(0) will always be called.
        //
        // Final clean up.
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        new AboutJDialog(this, true).setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
// TODO add your handling code here:
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
// TODO add your handling code here:
        // Calling setVisible(false) will cause modal dialog box to be unblocked
        // for JDialog.setVisible(true). This will happen in Linux system where
        // user are allowed to minimize window even there is a modal JDialog box
        // We have no solution at current moment.
        //

        if (Utils.isWindows())
        {
            this.setVisible(false);
        }
    }//GEN-LAST:event_formWindowIconified

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified
// TODO add your handling code here:
    }//GEN-LAST:event_formWindowDeiconified

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        this.jTable1.getSelectionModel().clearSelection();
        this.indicatorScannerJPanel.clearTableSelection();
        this.portfolioManagementJPanel.clearTableSelection();
        updateBuyerSellerInformation(null);
        updateDynamicChart(null);
    }//GEN-LAST:event_formMouseClicked

    private void updateDynamicChart(Stock stock) {
        assert(java.awt.EventQueue.isDispatchThread());

        DynamicChart dynamicChart = stock != null ? this.dynamicCharts.get(stock.getCode()) : MainFrame.EMPTY_DYNAMIC_CHART;
        if (dynamicChart == null) {
            dynamicChart = MainFrame.EMPTY_DYNAMIC_CHART;
        }

        if (java.util.Arrays.asList(jPanel3.getComponents()).contains(dynamicChart.getChartPanel()))
        {
            return;
        }

        this.jPanel3.removeAll();
        this.jPanel3.add(dynamicChart.getChartPanel(), java.awt.BorderLayout.CENTER);
        this.jPanel3.validate();

        // Not sure why. validate itself is not enough to perform update. We
        // must call repaint as well.
        dynamicChart.getChartPanel().repaint();
        dynamicChart.getChartPanel().removeMouseListener(dynamicChartMouseAdapter);
        dynamicChart.getChartPanel().addMouseListener(dynamicChartMouseAdapter);
    }

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
// TODO add your handling code here:
        if(KeyEvent.VK_DELETE == evt.getKeyCode()) {
            this.deteleSelectedTableRow();
            return;
        }
        
        if(evt.isActionKey()) {
            int[] rows = MainFrame.this.jTable1.getSelectedRows();
            
            if(rows.length == 1) {
                int row = rows[0];
                
                StockTableModel tableModel = (StockTableModel)jTable1.getModel();
                int modelIndex = jTable1.convertRowIndexToModel(row);
                Stock stock = tableModel.getStock(modelIndex);
                updateBuyerSellerInformation(stock);
                this.updateDynamicChart(stock);
            }
            else {
            	updateBuyerSellerInformation(null);
                this.updateDynamicChart(null);
            }

            return;
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
// TODO add your handling code here:
        log.info("Widnow is closing.");     
    }//GEN-LAST:event_formWindowClosing

	private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
	// TODO add your handling code here:
    	if (this.stockCodeAndSymbolDatabase == null) {
        	JOptionPane.showMessageDialog(this, "There are no database ready yet.", "Database not ready", JOptionPane.INFORMATION_MESSAGE);
        	return;
    	}
    
    	StockDatabaseJDialog stockDatabaseJDialog = new StockDatabaseJDialog(this, stockCodeAndSymbolDatabase, true);
    	stockDatabaseJDialog.setSize(540, 540);
    	stockDatabaseJDialog.setLocationRelativeTo(this);
    	stockDatabaseJDialog.setVisible(true); 
    
    	if(stockDatabaseJDialog.getMutableStockCodeAndSymbolDatabase() != null) {
        	this.stockCodeAndSymbolDatabase = stockDatabaseJDialog.getMutableStockCodeAndSymbolDatabase().toStockCodeAndSymbolDatabase();
        	((AutoCompleteJComboBox)jComboBox1).setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);
        	indicatorPanel.setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);        
        
        	log.info("saveStockCodeAndSymbolDatabase...");
        	saveStockCodeAndSymbolDatabase();        
    	}
	}//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        String suggestedFileName = "";

        if (this.getSelectedComponent() == this.jPanel8) {
            suggestedFileName = GUIBundle.getString("MainFrame_Title");
        }
        else if (this.getSelectedComponent() == this.indicatorScannerJPanel) {
            suggestedFileName = GUIBundle.getString("IndicatorScannerJPanel_Title");
        }
        else if (this.getSelectedComponent() == this.portfolioManagementJPanel) {
            suggestedFileName = GUIBundle.getString("PortfolioManagementJPanel_Title");
        }
        else {
            assert(false);
        }

        boolean status = true;
        File file = null;
        if (this.getSelectedComponent() == this.jPanel8 || this.getSelectedComponent() == this.indicatorScannerJPanel) {
            file = Utils.promptSaveCSVAndExcelJFileChooser(suggestedFileName);
            if (file != null) {                
                if (Utils.getFileExtension(file).equals("csv"))
                {
                    if (this.getSelectedComponent() == this.jPanel8) {
                        status = this.saveAsCSVFile(file);
                    }
                    else if (this.getSelectedComponent() == this.indicatorScannerJPanel) {
                        status = this.indicatorScannerJPanel.saveAsCSVFile(file);
                    }
                    else {
                        assert(false);
                    }
                }
                else if (Utils.getFileExtension(file).equals("xls"))
                {
                    if (this.getSelectedComponent() == this.jPanel8) {
                        status = this.saveAsExcelFile(file);
                    }
                    else if (this.getSelectedComponent() == this.indicatorScannerJPanel) {
                        status = this.indicatorScannerJPanel.saveAsExcelFile(file);
                    }
                    else {
                        assert(false);
                    }
                }
            }
        }
        else if (this.getSelectedComponent() == this.portfolioManagementJPanel) {
            final Utils.FileEx fileEx = Utils.promptSavePortfolioCSVAndExcelJFileChooser(suggestedFileName);
            if (fileEx != null) {
                file = fileEx.file;
                if (Utils.getFileExtension(fileEx.file).equals("csv"))
                {
                    status = this.portfolioManagementJPanel.saveAsCSVFile(fileEx);
                }
                else if (Utils.getFileExtension(fileEx.file).equals("xls"))
                {
                    status = this.portfolioManagementJPanel.saveAsExcelFile(fileEx.file);
                }
            }
        }
        else {
            assert(false);
        }
        if (false == status)
        {
            // file will never become null, if status had been changed from true
            // to false.
            assert(file != null);
            final String output = MessageFormat.format(MessagesBundle.getString("error_message_nothing_to_be_saved_template"), file.getName());
            JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("error_title_nothing_to_be_saved"), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenu8MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu8MenuSelected
        // TODO add your handling code here:
        this.jMenu8.removeAll();
        final java.util.List<String> portfolioNames = org.yccheok.jstock.portfolio.Utils.getPortfolioNames();
        final String currentPortfolioName = this.getJStockOptions().getPortfolioName();
        for (String portfolioName : portfolioNames) {
            final JMenuItem mi = (JRadioButtonMenuItem) jMenu8.add(new JRadioButtonMenuItem(portfolioName));
            buttonGroup3.add(mi);
            mi.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    final String s = ((JRadioButtonMenuItem)e.getSource()).getText();
                    if (false == s.equals(currentPortfolioName)) {
                        MainFrame.this.selectActivePortfolio(s);
                    }
                }

            });
            mi.setSelected(portfolioName.equals(currentPortfolioName));
        }

        jMenu8.addSeparator();
        final JMenuItem mi = new JMenuItem(GUIBundle.getString("MainFrame_MultiplePortolio"));
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                multiplePortfolios();
            }

        });
        jMenu8.add(mi);
    }//GEN-LAST:event_jMenu8MenuSelected

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        final LoadFromCloudJDialog dialog = new LoadFromCloudJDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        final SaveToCloudJDialog dialog = new SaveToCloudJDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        Utils.launchWebBrowser("http://jstock.sourceforge.net/?utm_source=jstock&utm_medium=help_menu");
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    public void selectActivePortfolio(String portfolio) {
        // Save current portfolio.
        MainFrame.this.portfolioManagementJPanel.savePortfolio();
        // And switch to new portfolio.
        MainFrame.this.getJStockOptions().setPortfolioName(portfolio);
        MainFrame.this.portfolioManagementJPanel.initPortfolio();
    }

    private void multiplePortfolios() {
        PortfolioJDialog portfolioJDialog = new PortfolioJDialog(this, true);
        portfolioJDialog.setLocationRelativeTo(this);
        portfolioJDialog.setVisible(true);
    }

    private boolean saveAsCSVFile(File file) {
        final TableModel tableModel = jTable1.getModel();
        final org.yccheok.jstock.file.Statements statements = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(tableModel);
        if (statements == null) {
            return false;
        }
        return statements.saveAsCSVFile(file);
    }

    private boolean saveAsExcelFile(File file) {
        final TableModel tableModel = jTable1.getModel();
        final org.yccheok.jstock.file.Statements statements = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(tableModel);
        if (statements == null) {
            return false;
        }
        return statements.saveAsExcelFile(file, GUIBundle.getString("MainFrame_Title"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* This ugly code shall be removed in next few release. */
        if (false == Utils.migrateFrom104jTo105()) {
            final int choice = JOptionPane.showConfirmDialog(null,
                "JStock unable to read previous 1.0.4j portfolio and settings, continue?\n\nPress \"Yes\" to continue, BUT all your data will lost.\n\nOr, Press \"No\", restart your machine and try again.",
                "JStock unable to read previous 1.0.4j portfolio and settings",
                JOptionPane.YES_NO_OPTION);
            log.error("Migration from 1.0.4j to 1.0.5 fail.");

            if (choice != JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }

        // As ProxyDetector is affected by system properties
        // http.proxyHost, we are forced to initialized ProxyDetector right here,
        // before we manually change the system properties according to
        // JStockOptions.
        ProxyDetector.getInstance();      
        
        Utils.setDefaultLookAndFeel();

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame mainFrame = MainFrame.getInstance();
                mainFrame.init();
                mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                mainFrame.setVisible(true);

                final LookAndFeel lnf = UIManager.getLookAndFeel();
                // User choose not to use default native look and feel. Let's make user happy by giving
                // what they want.
                if (false == lnf.getClass().getName().equals(mainFrame.getJStockOptions().getLooknFeel())) {
                    log.info("User prefer not to use native " + lnf.getClass().getName());
                    mainFrame.setLookAndFeel(mainFrame.getJStockOptions().getLooknFeel());
                }
            }
        });
    }
    
    private void clearAllStocks() {
        assert(java.awt.EventQueue.isDispatchThread());
        
        StockTableModel tableModel = (StockTableModel)jTable1.getModel();            

        if(stockCodeHistoryGUI != null) stockCodeHistoryGUI.clear();
        if(realTimeStockMonitor != null) realTimeStockMonitor.clearStockCodes();
        if(stockHistoryMonitor != null) stockHistoryMonitor.clearStockCodes();
        tableModel.clearAllStocks();     
        this.initAlertStateManager();

        updateBuyerSellerInformation(null);
        this.updateDynamicChart(null);

        if(stockCodeHistoryGUI != null) {
            if(stockCodeHistoryGUI.size() == 0) {
                if(this.stockCodeAndSymbolDatabase != null) {
                    statusBar.setProgressBar(false);
                    statusBar.setMainMessage("Connected");
                }
            }        
        }
    }
    
    // Should we synchronized the jTable1, or post the job at GUI event dispatch
    // queue?    
    private void deteleSelectedTableRow() {
        assert(java.awt.EventQueue.isDispatchThread());
        
        StockTableModel tableModel = (StockTableModel)jTable1.getModel();

        int rows[] = jTable1.getSelectedRows();

        Arrays.sort(rows);

        for (int i = rows.length-1; i >= 0; i--) {
            int row = rows[i];

            if (row < 0) {
                continue;
            }
            final int modelIndex = jTable1.getRowSorter().convertRowIndexToModel(row);
            Stock stock = tableModel.getStock(modelIndex);
            stockCodeHistoryGUI.remove(stock.getCode());
            realTimeStockMonitor.removeStockCode(stock.getCode());
            stockHistoryMonitor.removeStockCode(stock.getCode());
            tableModel.removeRow(modelIndex);
            this.alertStateManager.clearState(stock);
        }            
        
        updateBuyerSellerInformation(null);
        this.updateDynamicChart(null);

        if (stockCodeHistoryGUI.size() == 0) {
            if (this.stockCodeAndSymbolDatabase != null) {
                statusBar.setProgressBar(false);
                statusBar.setMainMessage("Connected");
            }
        }
    }
    
    public void setStatusBar(final boolean progressBar, final String mainMessage) {
        if (SwingUtilities.isEventDispatchThread())
        {
            statusBar.setProgressBar(progressBar);
            statusBar.setMainMessage(mainMessage);
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    statusBar.setProgressBar(progressBar);
                    statusBar.setMainMessage(mainMessage);
                }
            });
        }
    }
    
    class ChangeLookAndFeelAction extends AbstractAction {
        MainFrame mainFrame;
        String lafClassName;
  
        protected ChangeLookAndFeelAction(MainFrame mainFrame, String lafClassName) {
            super("ChangeTheme");
            this.mainFrame = mainFrame;
            this.lafClassName = lafClassName;
        }
  
        public String getLafClassName() {
            return lafClassName;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            mainFrame.setLookAndFeel(lafClassName);
        }
    }
  
    public void setLookAndFeel(String lafClassName) {
        /* Backward compataible purpose. Old JStockOptions may contain null in this field. */
        if(lafClassName == null)
            return;
        
        try {
            UIManager.setLookAndFeel(lafClassName);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch(java.lang.ClassNotFoundException exp) {
            log.error("", exp);
        }
        catch(java.lang.InstantiationException exp) {
            log.error("", exp);
        }
        catch(java.lang.IllegalAccessException exp) {
            log.error("", exp);
        }
        catch(javax.swing.UnsupportedLookAndFeelException exp) {
            log.error("", exp);
        }
        
        this.jStockOptions.setLookNFeel(lafClassName);
        
        for (Enumeration<AbstractButton> e = this.buttonGroup1.getElements() ; e.hasMoreElements() ;) {
            AbstractButton button = e.nextElement();
            javax.swing.JRadioButtonMenuItem m = (javax.swing.JRadioButtonMenuItem)button;
            ChangeLookAndFeelAction a = (ChangeLookAndFeelAction)m.getActionListeners()[0];
                        
            if(a.getLafClassName().equals(lafClassName)) {
                m.setSelected(true);
                break;                   
            }
        }
        
        // Sequence are important. The AutoCompleteJComboBox itself should have the highest
        // priority.
        ((AutoCompleteJComboBox)jComboBox1).setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);
        this.indicatorPanel.setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);
        initjComboBox1EditorComponentKeyListerner();
        this.indicatorPanel.initjComboBox1EditorComponentKeyListerner();
    }

    private void createChatJPanel() {
        chatJPanel = new org.yccheok.jstock.chat.ChatJPanel();
        jTabbedPane1.addTab("Market Chit Chat", chatJPanel);
        if (jStockOptions.isChatEnabled())
        {
            chatJPanel.startChatServiceManager();
        }
    }

    public PortfolioManagementJPanel getPortfolioManagementJPanel() {
        return this.portfolioManagementJPanel;
    }

    private void createPortfolioManagementJPanel() {
        portfolioManagementJPanel = new PortfolioManagementJPanel();        
        jTabbedPane1.addTab(GUIBundle.getString("PortfolioManagementJPanel_Title"), portfolioManagementJPanel);
    }
    
    private void createStockIndicatorEditor() {
        indicatorPanel = new IndicatorPanel();                
        jTabbedPane1.addTab("Stock Indicator Editor", indicatorPanel);
    }

    private void createIndicatorScannerJPanel() {
        this.indicatorScannerJPanel = new IndicatorScannerJPanel();                
        jTabbedPane1.addTab(GUIBundle.getString("IndicatorScannerJPanel_Title"), indicatorScannerJPanel);
        jTabbedPane1.addChangeListener(indicatorScannerJPanel);
    }
    
    // Due to the unknown problem in netbeans IDE, we will add in the tooltip
    // and icon seperately.
    private void createIconsAndToolTipTextForJTabbedPane() {
        this.jTabbedPane1.setIconAt(0, this.getImageIcon("/images/16x16/strokedocker.png"));
        this.jTabbedPane1.setIconAt(1, this.getImageIcon("/images/16x16/color_line.png"));
        this.jTabbedPane1.setIconAt(2, this.getImageIcon("/images/16x16/find.png"));
        this.jTabbedPane1.setIconAt(3, this.getImageIcon("/images/16x16/calc.png"));
        this.jTabbedPane1.setIconAt(4, this.getImageIcon("/images/16x16/smile.png"));
        this.jTabbedPane1.setToolTipTextAt(0, "Watch your favorite stock movement in real time");
        this.jTabbedPane1.setToolTipTextAt(1, "Customize your own stock indicator for alert purpose");
        this.jTabbedPane1.setToolTipTextAt(2, "Scan through the entire stock market so that you will be informed what to sell or buy");
        this.jTabbedPane1.setToolTipTextAt(3, "Manage your real time portfolio, which enable you to track buy and sell records");
        this.jTabbedPane1.setToolTipTextAt(4, "Chit chat with other JStock users regarding the hottest stock market news");
    }
      
    public void createCountryMenuItem() {
        final Country[] countries = Country.values();

        for(Country country : countries) {
            final JMenuItem mi = (JRadioButtonMenuItem) jMenu6.add(new JRadioButtonMenuItem(country.toString(), country.getIcon()));
            buttonGroup2.add(mi);
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final Country selectedCountry = Country.valueOf(mi.getText());
                    MainFrame.this.changeCountry(selectedCountry);
                }                
            });
            
            if(jStockOptions.getCountry() == country) {
                ((JRadioButtonMenuItem) mi).setSelected(true);
            }
        }
    }
            
    public void createLookAndFeelMenuItem() {
        LookAndFeel currentlaf = UIManager.getLookAndFeel();
        
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();

        for(int i=0; i<lafInfo.length; i++) {
            JMenuItem mi = (JRadioButtonMenuItem) jMenu4.add(new JRadioButtonMenuItem(lafInfo[i].getName()));
            buttonGroup1.add(mi);
            mi.addActionListener(new ChangeLookAndFeelAction(this, lafInfo[i].getClassName()));
            
            if(currentlaf != null) {
                if(lafInfo[i].getClassName().equals(currentlaf.getClass().getName()))
                {
                    ((JRadioButtonMenuItem) mi).setSelected(true);
                }
            }
        }
    }
  
    private javax.swing.event.TableModelListener getTableModelListener() {
        return new javax.swing.event.TableModelListener() {
            @Override
           public void tableChanged(javax.swing.event.TableModelEvent e) {
                int firstRow = e.getFirstRow();
                int lastRow = e.getLastRow();
                int mColIndex = e.getColumn();
                
                switch (e.getType()) {
                    case javax.swing.event.TableModelEvent.INSERT:
                        break;
                        
                  case javax.swing.event.TableModelEvent.UPDATE:
                    break;

                  case javax.swing.event.TableModelEvent.DELETE:
                    break;                        
                }
            }            
        };
    }

    private Image getMyIconImage()
    {
        return new javax.swing.ImageIcon(getClass().getResource("/images/16x16/chart.png")).getImage();
    }
    
    private void createSystemTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = new javax.swing.ImageIcon(getClass().getResource("/images/16x16/chart.png")).getImage();

            MouseListener mouseListener = new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON1) {
                        MainFrame.this.setVisible(true);
                        MainFrame.this.setState(Frame.NORMAL);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {               
                }

                @Override
                public void mouseExited(MouseEvent e) {            
                }

                @Override
                public void mousePressed(MouseEvent e) {             
                }

                @Override
                public void mouseReleased(MouseEvent e) {              
                }
            };

            ActionListener exitListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainFrame.this.setVisible(false);
                    MainFrame.this.dispose();
                }
            };

            PopupMenu popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("Exit");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);

            trayIcon = new TrayIcon(image, "JStock - Stock Market Software", popup);

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };

            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);
            trayIcon.addMouseListener(mouseListener);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                trayIcon = null;
                JOptionPane.showMessageDialog(MainFrame.this, "TrayIcon could not be added.", "System Tray Not Supported", JOptionPane.WARNING_MESSAGE);
            }

        } else {
            //  System Tray is not supported

            trayIcon = null;
            JOptionPane.showMessageDialog(MainFrame.this, "System tray is not supported. You may not get stock alert notify.", "System Tray Not Supported", JOptionPane.WARNING_MESSAGE);
        }        
    }
    
    private void initTableHeaderToolTips() {
        JTableHeader header = jTable1.getTableHeader();
    
        ColumnHeaderToolTips tips = new ColumnHeaderToolTips();

        tips.setToolTip(jTable1.getColumn("Fall Below"), "Alert user when last price fall below or equal to specified value");
        tips.setToolTip(jTable1.getColumn("Rise Above"), "Alert user when last price rise above or equal to specified value");

        header.addMouseMotionListener(tips);        
    }
    
    private void updateBuyerSellerInformation(Stock stock) {
        assert(java.awt.EventQueue.isDispatchThread());

        if(stock == null) {
            jLabel24.setText("");
            jLabel33.setText("");
            jLabel19.setText("");
            jLabel32.setText("");
            jLabel35.setText("");
            jLabel37.setText("");
            jLabel25.setText("");
            jLabel34.setText("");
            jLabel26.setText("");
            jLabel29.setText("");
            jLabel30.setText("");
            jLabel38.setText("");
            return;
        }
        
        final double prevPrice = stock.getPrevPrice();
        
        jLabel24.setText("" + stock.getThirdBuyPrice()); jLabel24.setForeground(Utils.getColor(stock.getThirdBuyPrice(), prevPrice));
        jLabel33.setText("" + stock.getSecondBuyPrice()); jLabel33.setForeground(Utils.getColor(stock.getSecondBuyPrice(), prevPrice));
        jLabel19.setText("" + stock.getBuyPrice()); jLabel19.setForeground(Utils.getColor(stock.getBuyPrice(), prevPrice));
        jLabel32.setText("" + stock.getSellPrice()); jLabel32.setForeground(Utils.getColor(stock.getSellPrice(), prevPrice));
        jLabel35.setText("" + stock.getSecondSellPrice()); jLabel35.setForeground(Utils.getColor(stock.getSecondSellPrice(), prevPrice));
        jLabel37.setText("" + stock.getThirdSellPrice()); jLabel37.setForeground(Utils.getColor(stock.getThirdSellPrice(), prevPrice));
        jLabel25.setText("" + stock.getThirdBuyQuantity());
        jLabel34.setText("" + stock.getSecondBuyQuantity());
        jLabel26.setText("" + stock.getBuyQuantity());
        jLabel29.setText("" + stock.getSellQuantity());
        jLabel30.setText("" + stock.getSecondSellQuantity());
        jLabel38.setText("" + stock.getThirdSellQuantity());
    }
    
    /* Save everything to disc, before perform uploading. */
    public void commitBeforeUploadToCloud() {
        /* These codes are very similar to clean up code during application
         * exit.
         */
        this.saveJStockOptions();
        this.saveGUIOptions();
        this.saveBrokingFirmLogos();
        this.saveRealTimeStocks();
        this.indicatorPanel.saveAlertIndicatorProjectManager();
        this.indicatorPanel.saveModuleIndicatorProjectManager();
        this.portfolioManagementJPanel.savePortfolio();
    }

    /* Reload after downloading from cloud. Take note that we must reload
     * JStockOptions before and outside this method, due to insensitive data
     * requirement.
     */
    public void reloadAfterDownloadFromCloud() {
        /* These codes are very similar to clean up code during changing country.
         */
        MainFrame.this.statusBar.setCountryIcon(jStockOptions.getCountry().getIcon(), jStockOptions.getCountry().toString());

        // Here is the dirty trick here. We let our the 'child' panels perform
        // cleanup/ initialization first before initStockCodeAndSymbolDatabase.
        // This is because all child panels and stock symbol database task do
        // interact with status bar. However, We are only most interest in stock symbol
        // database, as it will be the most busy. Hence, we let the stock symbol
        // database to be the last, so that its interaction will overwrite the others.
        this.portfolioManagementJPanel.initPortfolio();
        this.indicatorScannerJPanel.stop();
        this.indicatorScannerJPanel.clear();
        this.chatJPanel.stopChatServiceManager();
        if (jStockOptions.isChatEnabled())
        {
            this.chatJPanel.startChatServiceManager();
        }

        this.initStockCodeAndSymbolDatabase(true);
        this.initMarketThread();
        this.initMarketJPanel();
        this.initStockHistoryMonitor();
        this.initOthersStockHistoryMonitor();
        // Initialize real time monitor must come before initialize real time
        // stocks. We need to submit real time stocks to real time stock monitor.
        // Hence, after we load real time stocks from file, real time stock monitor
        // must be ready (initialized).
        this.initRealTimeStockMonitor();
        this.initRealTimeStocks();
        this.initAlertStateManager();
        this.initDynamicCharts();

        for (Enumeration<AbstractButton> e = this.buttonGroup2.getElements() ; e.hasMoreElements() ;) {
            AbstractButton button = e.nextElement();
            javax.swing.JRadioButtonMenuItem m = (javax.swing.JRadioButtonMenuItem)button;

            if(m.getText().equals(jStockOptions.getCountry().toString())) {
                m.setSelected(true);
                break;
            }
        }

        if (null != this.indicatorPanel) {
            this.indicatorPanel.initIndicatorProjectManager();
            this.indicatorPanel.initModuleProjectManager();
        }
    }

    private void changeCountry(Country country) {
        if (country == null) {
            return;
        }
        if (jStockOptions.getCountry() == country) {
            return;
        }

        /* Save the GUI look. */
        saveGUIOptions();

        saveRealTimeStocks();
        this.portfolioManagementJPanel.savePortfolio();        
        
        jStockOptions.setCountry(country);
        MainFrame.this.statusBar.setCountryIcon(country.getIcon(), country.toString());

        // Here is the dirty trick here. We let our the 'child' panels perform
        // cleanup/ initialization first before initStockCodeAndSymbolDatabase.
        // This is because all child panels and stock symbol database task do
        // interact with status bar. However, We are only most interest in stock symbol
        // database, as it will be the most busy. Hence, we let the stock symbol
        // database to be the last, so that its interaction will overwrite the others.
        this.portfolioManagementJPanel.initPortfolio();
        this.indicatorScannerJPanel.stop();
        this.indicatorScannerJPanel.clear();
        this.chatJPanel.stopChatServiceManager();
        if (jStockOptions.isChatEnabled())
        {
            this.chatJPanel.startChatServiceManager();
        }

        this.initStockCodeAndSymbolDatabase(true);
        this.initMarketThread();
        this.initMarketJPanel();
        this.initStockHistoryMonitor();
        this.initOthersStockHistoryMonitor();
        // Initialize real time monitor must come before initialize real time
        // stocks. We need to submit real time stocks to real time stock monitor.
        // Hence, after we load real time stocks from file, real time stock monitor
        // must be ready (initialized).
        this.initRealTimeStockMonitor();
        this.initRealTimeStocks();
        this.initAlertStateManager();
        this.initDynamicCharts();

        for (Enumeration<AbstractButton> e = this.buttonGroup2.getElements() ; e.hasMoreElements() ;) {
            AbstractButton button = e.nextElement();
            javax.swing.JRadioButtonMenuItem m = (javax.swing.JRadioButtonMenuItem)button;
                        
            if(m.getText().equals(country.toString())) {
                m.setSelected(true);
                break;                   
            }
        }
        
    }
    
    private MouseAdapter getMyJXStatusBarCountryLabelMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    CountryJDialog countryJDialog = new CountryJDialog(MainFrame.this, true);
                    countryJDialog.setLocationRelativeTo(MainFrame.this);
                    countryJDialog.setCountry(jStockOptions.getCountry());
                    countryJDialog.setVisible(true);
                    
                    final Country country = countryJDialog.getCountry();
                    changeCountry(country);
                }
            }
        };
    }
    
    private MouseAdapter getMyJXStatusBarImageLabelMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    
                    // Make sure no same task is running.
                    if (stockCodeAndSymbolDatabaseTask != null) {
                        if (stockCodeAndSymbolDatabaseTask.isDone() == true) {
                            final int result = JOptionPane.showConfirmDialog(MainFrame.this, MessagesBundle.getString("question_message_perform_server_reconnecting"), MessagesBundle.getString("question_title_perform_server_reconnecting"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (result == JOptionPane.YES_OPTION)
                            {
                                initStockCodeAndSymbolDatabase(false);
                            }
                        }
                        else {
                            final int result = JOptionPane.showConfirmDialog(MainFrame.this, MessagesBundle.getString("question_message_cancel_server_reconnecting"), MessagesBundle.getString("question_title_cancel_server_reconnecting"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            
                            if (result == JOptionPane.YES_OPTION)
                            {                            
                                synchronized(stockCodeAndSymbolDatabaseTask)
                                {
                                    stockCodeAndSymbolDatabaseTask._stop();
                                    stockCodeAndSymbolDatabaseTask = null;
                                }
                                
                                statusBar.setMainMessage("Network error");
                                statusBar.setImageIcon(getImageIcon("/images/16x16/network-error.png"), "Double cliked to try again");
                                statusBar.setProgressBar(false);                                
                            }
                        }
                    }
                    else {
                        initStockCodeAndSymbolDatabase(true);
                    }
                            
                }
            }
        };
    }

    private KeyAdapter getjComboBox1EditorComponentKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    String stock = MainFrame.this.jComboBox1.getEditor().getItem().toString();
                    
                    if (stock.length() > 0) {
                        Code code = stockCodeAndSymbolDatabase.searchStockCode(stock);
                        Symbol symbol = null;
                            
                        if (code != null) {
                            symbol = stockCodeAndSymbolDatabase.codeToSymbol(code);
                            // First add the empty stock, so that the user will not have wrong perspective that
                            // our system is slow.
                            addStockToTable(Utils.getEmptyStock(code, symbol));
                            realTimeStockMonitor.addStockCode(code);                                                                
                        }
                        else {
                            symbol = stockCodeAndSymbolDatabase.searchStockSymbol(stock);
                                
                            if (symbol != null) {
                                code = stockCodeAndSymbolDatabase.symbolToCode(symbol);
                                // Shouldn't be null. This is because symbol is obtained directly
                                // from database. Even later user modifies symbol or code through Database -> Stock Database...,
                                // it shouldn't have any side effect.
                                assert(code != null);
                                addStockToTable(Utils.getEmptyStock(code, symbol));
                                // We allow user to modify user defined type stock code and symbol.
                                // 1st rule is, modified code cannot crash with existing stock's code or stock's symbol.
                                // 2nd rule is, modified symbol cannot crash with existing stock's code or stock's symbol.
                                //
                                // Please consider the following senario :
                                // (1) A stock code "6012.KL" and symbol "MAXIS" already added.
                                // (2) Users modify "MAXIS" to "MAXIS SDN BHD".
                                // (3) Users try to add "6012.KL". RealTimeStockMonitor will prevent this from happen,
                                //     as it detectes there is a duplicated code.
                                // (4) Users try to add "MAXIS SDN BHD". RealTimeStockMonitor will prevent this from happen,
                                //     as it detectes there is a duplicated code, regardless the value of symbol.
                                //
                                // Please consider another senario :
                                // (1) A stock code "6012.KL" and symbol "MAXIS" already added.
                                // (2) Users modify "6012.KL" to "6013.KL".
                                // (3) Users try to add "6013.KL". RealTimeStockMonitor will allow.
                                //     At the end, there are two rows with same symbol "MAXIS", but different codes.
                                // (4) Users try to add "MAXIS". RealTimeStockMonitor will allow.
                                //     At the end, there are two rows with same symbol "MAXIS", but different codes.
                                realTimeStockMonitor.addStockCode(stockCodeAndSymbolDatabase.symbolToCode(symbol));
                            }
                        }
                    }   // if(stock.length() > 0)
                    else {
                        final String lastEnteredString = ((AutoCompleteJComboBox)MainFrame.this.jComboBox1).getLastEnteredString();
                        
                        if (lastEnteredString.length() < 1) {
                            return;
                        }
                        
                        if (MainFrame.this.stockCodeAndSymbolDatabaseTask != null)
                        {
                            if (MainFrame.this.stockCodeAndSymbolDatabaseTask.isDone() == false)
                            {
                                JOptionPane.showMessageDialog(MainFrame.this, lastEnteredString + " is not found in database\nPlease wait till database finished downloaded.", "Database downloading in progress", JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }
                        }
                        
                        JOptionPane.showMessageDialog(MainFrame.this, lastEnteredString + " is not found in database.\nYou may either add \"" + lastEnteredString + "\" manually through \"Database\" menu,\n" +
                                "or you may get the latest database from stock server, by double click on " +
                                "bottom right network icon.", "Database outdated", JOptionPane.INFORMATION_MESSAGE);
                        
                        //final int result = JOptionPane.showConfirmDialog(MainFrame.this, lastEnteredString + " is not found in database\nDo you want to perform reconnecting to stock server?\nThis may take several minutes to several hours. (depending on your network connection)", "Database outdated", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        //if(result == JOptionPane.YES_OPTION)
                        //{
                        //    initStockCodeAndSymbolDatabase(false);
                        //}                            
                    }
                    
                }   // if(KeyEvent.VK_ENTER == e.getKeyCode())

            }   /* public void keyReleased(KeyEvent e) */
        };
    }

    public StockCodeAndSymbolDatabase getStockCodeAndSymbolDatabase() {
        return stockCodeAndSymbolDatabase;
    }
    
    public java.util.List<StockServerFactory> getStockServerFactories() {
        return getStockServerFactories(this.jStockOptions.getCountry());
    }

    private java.util.List<StockServerFactory> getStockServerFactories(Country country) {
        return Factories.INSTANCE.getStockServerFactories(country);
    }
    
    public java.util.List<Stock> getStocks() {
        StockTableModel tableModel = (StockTableModel)jTable1.getModel();
        return tableModel.getStocks();
    }
    
    // Should we synchronized the jTable1, or post the job to GUI event dispatch
    // queue?
    public void addStockToTable(final Stock stock, final StockAlert alert) {
        assert(java.awt.EventQueue.isDispatchThread());
        
        StockTableModel tableModel = (StockTableModel)jTable1.getModel();
        tableModel.addStock(stock, alert);
    }

    public void addStockToTable(final Stock stock) {
        assert(java.awt.EventQueue.isDispatchThread());

        StockTableModel tableModel = (StockTableModel)jTable1.getModel();
        tableModel.addStock(stock);
    }

    // Only will return true if the selected stock is the one and only one.
    private boolean isStockBeingSelected(final Stock stock) {
        int[] rows = MainFrame.this.jTable1.getSelectedRows();
            
        if(rows.length == 1) {
            int row = rows[0];
                
            StockTableModel tableModel = (StockTableModel)jTable1.getModel();
            int modelIndex = jTable1.convertRowIndexToModel(row);
            if(stock.getCode().equals(tableModel.getStock(modelIndex).getCode()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    // Return one and only one selected stock. Otherwise null.
    private Stock getSelectedStock() {
        int[] rows = MainFrame.this.jTable1.getSelectedRows();

        if(rows.length == 1) {
            int row = rows[0];

            StockTableModel tableModel = (StockTableModel)jTable1.getModel();
            int modelIndex = jTable1.convertRowIndexToModel(row);
            return tableModel.getStock(modelIndex);
        }

        return null;
    }

    private void updateStockToTable(final Stock stock) {
        StockTableModel tableModel = (StockTableModel)jTable1.getModel();
        tableModel.updateStock(stock);
    }

    private void update(final Indicator indicator, Boolean result)
    {
        final boolean flag = result;

        if (flag == false) {
            return;
        }

        final StockTableModel stockTableModel = (StockTableModel)MainFrame.this.jTable1.getModel();
        final Stock stock = indicator.getStock();        
        final Double price = ((OperatorIndicator)indicator).getName().equalsIgnoreCase("fallbelow") ? stockTableModel.getFallBelow(stock) : stockTableModel.getRiseAbove(stock);
	final double lastPrice = stock.getLastPrice();

        // Using lastPrice = 0 to compare against fall below and rise above
        // target price is meaningless. In normal condition, no stock price
        // shall fall until 0. When we get last price is 0, most probably
        // market is not opened yet.
        if (lastPrice <= 0.0) {
            return;
        }

        if(this.jStockOptions.isPopupMessage()) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {                    
                    String message = "";

                    if (((OperatorIndicator)indicator).getName().equalsIgnoreCase("fallbelow"))
                    {
                        message = stock.getSymbol() + " (" + lastPrice + ") fall below " + price;
                    }
                    else
                    {
                        message = stock.getSymbol() + " (" + lastPrice + ") rise above " + price;
                    }

                    if (jStockOptions.isPopupMessage()) {
                        displayPopupMessage(stock.getSymbol().toString(), message);

                        if (jStockOptions.isSoundEnabled()) {
                            /* Non-blocking. */
                            Utils.playAlertSound();
                        }

                        try {
                            Thread.sleep(jStockOptions.getAlertSpeed() * 1000);
                        }
                        catch (InterruptedException exp) {
                            log.error(null, exp);
                        }
                    }
                }
            };

            try {
                systemTrayAlertPool.submit(r);
            }
            catch (java.util.concurrent.RejectedExecutionException exp) {
                log.error(null, exp);
            }
        }   /* if(this.jStockOptions.isPopupMessage()) */

        // Sound alert hasn't been submitted to pop up message pool.
        if (jStockOptions.isPopupMessage() == false && jStockOptions.isSoundEnabled()) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (jStockOptions.isSoundEnabled()) {
                        /* Non-blocking. */
                        Utils.playAlertSound();

                        try {
                            Thread.sleep(jStockOptions.getAlertSpeed() * 1000);
                        }
                        catch (InterruptedException exp) {
                            log.error(null, exp);
                        }
                    }
                }
            };

            try {
                systemTrayAlertPool.submit(r);
            }
            catch (java.util.concurrent.RejectedExecutionException exp) {
                log.error(null, exp);
            }
        }   /* if(this.jStockOptions.isSoundEnabled()) */

        if (this.jStockOptions.isSendEmail()) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    String title = "";

                    if (((OperatorIndicator)indicator).getName().equalsIgnoreCase("fallbelow"))
                    {
                        title = stock.getSymbol() + " (" + lastPrice + ") fall below " + price;
                    }
                    else
                    {
                        title = stock.getSymbol() + " (" + lastPrice + ") rise above " + price;
                    }

                    final String message = title + "\n(JStock)";

                    try {
                        String email = Utils.decrypt(jStockOptions.getEmail());
                        GoogleMail.Send(email, Utils.decrypt(jStockOptions.getEmailPassword()), email + "@gmail.com", title, message);
                    } catch (AddressException exp) {
                        log.error("", exp);
                    } catch (MessagingException exp) {
                        log.error("", exp);
                    }
                }
            };

            try {
                emailAlertPool.submit(r);
            }
            catch(java.util.concurrent.RejectedExecutionException exp) {
                log.error("", exp);
            }
        }   /* if(jStockOptions.isSendEmail()) */

        if (this.jStockOptions.isSMSEnabled()) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    String message = "";
					
                    if (((OperatorIndicator)indicator).getName().equalsIgnoreCase("fallbelow"))
                    {
                        message = stock.getSymbol() + " (" + lastPrice + ") fall below " + price;
                    }
                    else
                    {
                        message = stock.getSymbol() + " (" + lastPrice + ") rise above " + price;
                    }

                    final String username = Utils.decrypt(jStockOptions.getGoogleCalendarUsername());
                    if (SMSLimiter.INSTANCE.isSMSAllowed()) {
                        final boolean status = GoogleCalendar.SMS(username, Utils.decrypt(jStockOptions.getGoogleCalendarPassword()), message);
                        if (status) {
                            SMSLimiter.INSTANCE.inc();
                        }
                    }
                }
            };

            try {
                smsAlertPool.submit(r);
            }
            catch(java.util.concurrent.RejectedExecutionException exp) {
                log.error(null, exp);
            }
        }   /* if(jStockOptions.isSMSEnabled()) */
    }

    private org.yccheok.jstock.engine.Observer<Indicator, Boolean> getAlertStateManagerObserver() {
        return new org.yccheok.jstock.engine.Observer<Indicator, Boolean>() {

            @Override
            public void update(Indicator subject, Boolean arg) {
                MainFrame.this.update(subject, arg);
            }

        };
    }

    // This is the workaround to overcome Erasure by generics. We are unable to make MainFrame to
    // two observers at the same time.
    private org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, java.util.List<Stock>> getRealTimeStockMonitorObserver() {
        return new org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, java.util.List<Stock>>() {
            @Override
            public void update(RealTimeStockMonitor monitor, java.util.List<Stock> stocks)
            {
                MainFrame.this.update(monitor, stocks);
            }
        };
    }

    private org.yccheok.jstock.engine.Observer<StockHistoryMonitor, StockHistoryMonitor.StockHistoryRunnable> getStockHistoryMonitorObserver() {
        return new org.yccheok.jstock.engine.Observer<StockHistoryMonitor, StockHistoryMonitor.StockHistoryRunnable>() {
            @Override
            public void update(StockHistoryMonitor monitor, StockHistoryMonitor.StockHistoryRunnable runnable)
            {
                MainFrame.this.update(monitor, runnable);
            }
        };
    }
    
    // Asynchronous call. Must be called by event dispatch thread.
    public void displayHistoryChart(Stock stock) {
        final StockHistoryServer stockHistoryServer = stockHistoryMonitor.getStockHistoryServer(stock.getCode());

        if(stockHistoryServer == null) {
            if(stockCodeHistoryGUI.add(stock.getCode()) && stockHistoryMonitor.addStockCode(stock.getCode())) {
                statusBar.setProgressBar(true);
                statusBar.setMainMessage("Looking for " + stock.getSymbol() + " history. Still waiting for history total " + stockCodeHistoryGUI.size() + "...");                            
            }
        }
        else {
            ChartJDialog chartJDialog = new ChartJDialog(MainFrame.this, stock.getSymbol() + " (" + stock.getCode() + ")", false, stockHistoryServer);
            chartJDialog.setVisible(true);                            
        }        
    }
    
    private JPopupMenu getMyJTablePopupMenu() {
        final JPopupMenu popup = new JPopupMenu();
        final TableModel tableModel = jTable1.getModel();            
        
        javax.swing.JMenuItem menuItem = new JMenuItem("History...", this.getImageIcon("/images/16x16/strokedocker.png"));
        
        menuItem.addActionListener(new ActionListener() {
            @Override
        	public void actionPerformed(ActionEvent evt) {
                if (MainFrame.this.stockCodeAndSymbolDatabase == null)
                {
                    javax.swing.JOptionPane.showMessageDialog(MainFrame.this, "We haven't connected to stock server.", "Not Connected", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int rows[] = jTable1.getSelectedRows();
                final StockTableModel tableModel = (StockTableModel)jTable1.getModel();

                for (int row : rows) {
                    final int modelIndex = jTable1.getRowSorter().convertRowIndexToModel(row);
                    Stock stock = tableModel.getStock(modelIndex);
                    displayHistoryChart(stock);
                }
            }
        });
                
        popup.add(menuItem);
        
        popup.addSeparator();        
        
        if (jTable1.getSelectedRowCount() == 1) {
            menuItem = new JMenuItem("Buy...", this.getImageIcon("/images/16x16/inbox.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    final int row = jTable1.getSelectedRow();
                    final int modelIndex = jTable1.getRowSorter().convertRowIndexToModel(row);
                    final Stock stock = ((StockTableModel)tableModel).getStock(modelIndex);

                    // We have a real nasty bug here. We retrieve stock information through stock code.
                    // When we receive stock information, we update all its particular information, including stock
                    // symbol. Here is the catch, the latest updated stock symbol (stock.getSymbol), may not be the
                    // same as stock symbol found in stock database. If we pass the stock symbol which is not found
                    // in stock database to portfolio, something can go wrong. This is because portfolio rely heavily
                    // on symbol <-> code conversion. Hence, instead of using stock.getSymbol, we prefer to get the
                    // symbol out from stock database. This marks the close of the following reported bugs :
                    //
                    // [2800598] buyportfolio.xml file not updated with code symbol
                    // [2790218] User unable to add new buy transaction in Spain
                    //
                    // Say no to : portfolioManagementJPanel.showNewBuyTransactionJDialog(stock.getSymbol(), stock.getLastPrice(), false);
                    portfolioManagementJPanel.showNewBuyTransactionJDialog(stock, stock.getLastPrice(), false);
                }
            });  
            
            popup.add(menuItem);            
            
            popup.addSeparator();
        }                
        
        menuItem = new JMenuItem("Delete", this.getImageIcon("/images/16x16/editdelete.png"));
        
        menuItem.addActionListener(new ActionListener() {
            @Override
        	public void actionPerformed(ActionEvent evt) {
                    MainFrame.this.deteleSelectedTableRow();
            }
        });
            
        popup.add(menuItem);
        
        return popup;
    }
    
    private class MarketRunnable implements Runnable {
        public MarketRunnable() {
        }
        
        @Override
        public void run() {
            final Thread currentThread = Thread.currentThread();

            // Do not rely on isInterrupted flag only. The flag can be cleared by 3rd party easily.
            // Check for current thread as well.
            while (!currentThread.isInterrupted()  && (marketThread == Thread.currentThread())) {
                final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories();
                for (StockServerFactory factory : stockServerFactories) {
                    MarketServer server = factory.getMarketServer();
                    
                    final Market market = server.getMarket();
                    
                    if(market != null) {
                        update(market);
                        break;
                    }
                }
                
                try {
                    Thread.sleep(jStockOptions.getScanningSpeed());
                }
                catch(InterruptedException exp) {
                    log.error(null, exp);
                    break;
                }
            }
        }        
    }
    
    private class StockCodeAndSymbolDatabaseTask extends SwingWorker<Boolean, Integer> implements org.yccheok.jstock.engine.Observer<StockServer, Integer>{
        private volatile boolean runnable = true;
        private boolean readFromDisk = true;
        
        public StockCodeAndSymbolDatabaseTask(boolean readFromDisk)
        {
            this.readFromDisk = readFromDisk;
        }
        
        public void _stop() {
            runnable = false;
        }
        
        @Override
        protected void done() {
            boolean success = false;
            
            try {
                success = get();

            }
            catch(InterruptedException exp) {
                log.error("", exp);
            }
            catch(java.util.concurrent.ExecutionException exp) {
                log.error("", exp);
            }
           
            // If we are asked to stop explicitly, do not perform any update
            // on GUI.
            if(runnable)
            {
                if(success) {
                    statusBar.setMainMessage("Connected");
                    statusBar.setImageIcon(getImageIcon("/images/16x16/network-transmit-receive.png"), "Connected");
                    statusBar.setProgressBar(false);                    
                }
                else {
                    statusBar.setMainMessage("Network error");
                    statusBar.setImageIcon(getImageIcon("/images/16x16/network-error.png"), "Double cliked to try again");
                    statusBar.setProgressBar(false);
                }
            }
       }
       
        @Override
        public Boolean doInBackground() {
            final Country country = jStockOptions.getCountry();
            
            Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database");
            final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "stockcodeandsymboldatabase.xml");
            
            if(this.readFromDisk)
            {                                
                // We try to first load from disk. The information may be outdated,
                // but it is far more better than letting user to wait for several
                // hours.
                final StockCodeAndSymbolDatabase tmp = org.yccheok.jstock.gui.Utils.fromXML(StockCodeAndSymbolDatabase.class, f);
                
                if (tmp != null) {
                    log.info("Stock code and symbol database loaded from " + f.toString() + " successfully.");            
                
                    // Prepare proper synchronization for us to change country.
                    synchronized(StockCodeAndSymbolDatabaseTask.this)
                    {
                        if (runnable)
                        {
                            stockCodeAndSymbolDatabase = tmp;

                            // Register the auto complete JComboBox with latest database.
                            ((AutoCompleteJComboBox)jComboBox1).setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);
                            indicatorPanel.setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);
                            
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }                
                }
            }   // if(this.readFromDisk)
            
            // When we fall here, we either fail to read from disk or user
            // explicitly doesn't allow us to read from disk. Let's perform
            // networking stuff.
            //
            Boolean success = false;
            int tries = 0;            
            final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories(country);
            StockCodeAndSymbolDatabase tmp = null;
            
            while (!isCancelled() && !success && runnable) {
                for (StockServerFactory factory : stockServerFactories) {

                    StockServer stockServer = factory.getStockServer();
                    
                    if(stockServer instanceof Subject)
                    {
                        ((Subject<StockServer, Integer>)stockServer).attach(StockCodeAndSymbolDatabaseTask.this);
                    }
                    
                    try {                        
                        tmp = new StockCodeAndSymbolDatabase(stockServer);
                        
                        // Prepare proper synchronization for us to change country.
                        synchronized(this)
                        {
                            if(runnable)
                            {
                                stockCodeAndSymbolDatabase = tmp;
                                
                                // Register the auto complete JComboBox with latest database.
                                ((AutoCompleteJComboBox)jComboBox1).setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);
                                indicatorPanel.setStockCodeAndSymbolDatabase(stockCodeAndSymbolDatabase);
                                
                                success = true;
                            }
                        }                        
                        break;
                    }
                    catch(StockNotFoundException exp) {
                        log.error("", exp);
                    }
                    finally {
                        if(stockServer instanceof Subject)
                        {
                            ((Subject<StockServer, Integer>)stockServer).dettach(StockCodeAndSymbolDatabaseTask.this);
                        }
                    }
                    
                    if(isCancelled() || !runnable) {
                        break;
                    }
                }
                
                tries++;
                
                // We had tried NUM_OF_RETRY times, but still failed. Abort.
                if(tries >= NUM_OF_RETRY) break;

            }
             
            if(success == true)
            {
                org.yccheok.jstock.gui.Utils.toXML(tmp, f);
            }
            
            return success;
        }

        @Override
        public void update(StockServer subject, Integer arg) {
            publish(arg);
        }

        @Override
        protected void process(java.util.List<Integer> chunks) {
            if(runnable)
            {
                int max = 0;
                for(Integer integer : chunks) {
                    if(max < integer.intValue())
                        max = integer.intValue();
                }
                
                statusBar.setMainMessage(max + " stock(s) has been downloaded so far ...");
                statusBar.setImageIcon(getImageIcon("/images/16x16/network-connecting.png"), "Connecting...");
                statusBar.setProgressBar(true);                    
            }
        }
    }
    
    private void initMyJXStatusBarCountryLabelMouseAdapter() {
        final MouseAdapter mouseAdapter = this.getMyJXStatusBarCountryLabelMouseAdapter();
        this.statusBar.addCountryLabelMouseListener(mouseAdapter);
    }
    
    private void initMyJXStatusBarImageLabelMouseAdapter() {
        final MouseAdapter mouseAdapter = this.getMyJXStatusBarImageLabelMouseAdapter();
        this.statusBar.addImageLabelMouseListener(mouseAdapter);
    }
    
    private void initjComboBox1EditorComponentKeyListerner() {
        KeyListener[] listeners = this.jComboBox1.getEditor().getEditorComponent().getKeyListeners();
        
        for(KeyListener listener : listeners) {
            if(listener == jComboBox1EditorComponentKeyAdapter) {
                return;
            }
        }
        
        // Bug in Java 6. Most probably this listener had been removed during look n feel updating, reassign!
        this.jComboBox1.getEditor().getEditorComponent().addKeyListener(jComboBox1EditorComponentKeyAdapter);
        log.info("Reassign key adapter to combo box");
    }
    
    private void initRealTimeStockMonitor() {
        if (realTimeStockMonitor != null) {
            final RealTimeStockMonitor oldRealTimeStockMonitor = realTimeStockMonitor;
            zombiePool.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("Prepare to shut down " + oldRealTimeStockMonitor + "...");
                    oldRealTimeStockMonitor.clearStockCodes();
                    oldRealTimeStockMonitor.dettachAll();
                    oldRealTimeStockMonitor.stop();
                    log.info("Shut down " + oldRealTimeStockMonitor + " peacefully.");
                }
            });
        }
        
        realTimeStockMonitor = new RealTimeStockMonitor(4, 20, jStockOptions.getScanningSpeed());
        
        final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories();
        realTimeStockMonitor.setStockServerFactories(stockServerFactories);

        realTimeStockMonitor.attach(this.realTimeStockMonitorObserver);
        
        this.indicatorScannerJPanel.initRealTimeStockMonitor(Collections.unmodifiableList(stockServerFactories));
        this.portfolioManagementJPanel.initRealTimeStockMonitor(Collections.unmodifiableList(stockServerFactories));
    }

    // Only call after initJStockOptions.
    private void initBrokingFirmLogos() {
        final int size = jStockOptions.getBrokingFirmSize();

        for(int i=0; i<size; i++) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "logos" + File.separator + i + ".png"));

                jStockOptions.getBrokingFirm(i).setLogo(bufferedImage);
            } catch (IOException exp) {
                log.error("", exp);
            }
        }
    }

    private void initGUIOptions() {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "mainframe.xml");
        GUIOptions guiOptions = Utils.fromXML(GUIOptions.class, f);

        if (guiOptions == null)
        {
            return;
        }

        if (guiOptions.getJTableOptionsSize() <= 0)
        {
            return;
        }

        /* Set Table Settings */
        JTableUtilities.setJTableOptions(jTable1, guiOptions.getJTableOptions(0));
    }

    private void saveGUIOptions() {
        _saveGUIOptions();
        this.indicatorScannerJPanel.saveGUIOptions();
        this.portfolioManagementJPanel.saveGUIOptions();
    }
    
    private boolean _saveGUIOptions() {
        if(Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config") == false)
        {
            return false;
        }
        
        final GUIOptions.JTableOptions jTableOptions = new GUIOptions.JTableOptions();
        
        final int count = this.jTable1.getColumnCount();
        for (int i = 0; i < count; i++) {
            final String name = this.jTable1.getColumnName(i);
            final TableColumn column = jTable1.getColumnModel().getColumn(i);
            jTableOptions.addColumnOption(GUIOptions.JTableOptions.ColumnOption.newInstance(name, column.getWidth()));
        }
        
        final GUIOptions guiOptions = new GUIOptions();
        guiOptions.addJTableOptions(jTableOptions);
        
        File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "mainframe.xml");
        return Utils.toXML(guiOptions, f);
    }

    private void initJStockOptions() {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "options.xml");
        this.jStockOptions = Utils.fromXML(JStockOptions.class, f);
        
        if (jStockOptions == null) {
            jStockOptions = new JStockOptions();
        }
        else {
            log.info("jstockOptions loaded from " + f.toString() + " successfully.");
        }
        /* Hard core fix. */
        if (jStockOptions.getScanningSpeed() == 0) {
            jStockOptions.setScanningSpeed(1000);
        }
                
        final String proxyHost = jStockOptions.getProxyServer();
        final int proxyPort = jStockOptions.getProxyPort();
        
        if ((proxyHost.length() > 0) && (org.yccheok.jstock.engine.Utils.isValidPortNumber(proxyPort))) {
            System.getProperties().put("http.proxyHost", proxyHost);
            System.getProperties().put("http.proxyPort", "" + proxyPort);
        }
        else {
            System.getProperties().remove("http.proxyHost");
            System.getProperties().remove("http.proxyPort");
        }

        for (Country country : Country.values()) {
            final Class c = jStockOptions.getPrimaryStockServerFactoryClass(country);
            if (c == null) {
                continue;
            }
            Factories.INSTANCE.updatePrimaryStockServerFactory(country, c);
        }
    }   

    public void updatePrimaryStockServerFactory(Country country, Class c) {
        // Same. Nothing to be updated.
        if (c == jStockOptions.getPrimaryStockServerFactoryClass(country)) {
            return;
        }

        jStockOptions.addPrimaryStockServerFactoryClass(country, c);
        Factories.INSTANCE.updatePrimaryStockServerFactory(country, c);

        realTimeStockMonitor.setStockServerFactories(this.getStockServerFactories());
        stockHistoryMonitor.setStockServerFactories(this.getStockServerFactories());

        this.indicatorScannerJPanel.updatePrimaryStockServerFactory(Collections.unmodifiableList(this.getStockServerFactories()));
        this.portfolioManagementJPanel.updatePrimaryStockServerFactory(Collections.unmodifiableList(this.getStockServerFactories()));
        this.indicatorPanel.updatePrimaryStockServerFactory(Collections.unmodifiableList(this.getStockServerFactories()));
    }

    private void initRealTimeStocks() {
        final Country country = jStockOptions.getCountry();
        final File f0 = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "config" + File.separator + "realtimestock.xml");
        java.util.List<Stock> s = Utils.fromXML(java.util.List.class, f0);
        if (s != null) {
            log.info("Real time stocks loaded from " + f0.toString() + " successfully.");
        }
       
        File f1 = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "config" + File.separator + "realtimestockalert.xml");
        java.util.List<StockAlert> a = Utils.fromXML(java.util.List.class, f1);
        if (a != null) {
            log.info("Real time stocks' alert loaded from " + f1.toString() + " successfully.");
        }

        clearAllStocks();
        
        if(s != null) {            
            final java.util.List<Stock> stocks = s;
            final java.util.List<StockAlert> alerts = a;
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (alerts == null || (alerts.size() != stocks.size()))
                    {
                        for(Stock stock : stocks) {
                            final Stock emptyStock = Utils.getEmptyStock(stock.getCode(), stock.getSymbol());
                            MainFrame.this.addStockToTable(emptyStock);
                            realTimeStockMonitor.addStockCode(emptyStock.getCode());
                        }
                    }
                    else
                    {
                        final int size = stocks.size();
                        for(int i = 0; i < size; i++) {
                            final Stock stock = stocks.get(i);
                            final StockAlert alert = alerts.get(i);
                            final Stock emptyStock = Utils.getEmptyStock(stock.getCode(), stock.getSymbol());
                            MainFrame.this.addStockToTable(emptyStock, alert);
                            realTimeStockMonitor.addStockCode(emptyStock.getCode());
                        }
                    }
                }
            });
        }
    } 
    
    private javax.help.HelpBroker getHelpBroker() {
        final String hsName = "jhelpset.hs";        
        HelpSet hs = null;
        
        try {
            ClassLoader cl = MainFrame.class.getClassLoader();
            URL hsURL = HelpSet.findHelpSet(cl, hsName);
            hs = new HelpSet(null, hsURL);
        } catch (Exception exp) {
            log.error("", exp);
            return null;
        }

        return hs.createHelpBroker();
    }

    private boolean saveRealTimeStocks() {
        final Country country = jStockOptions.getCountry();

        if(Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "config") == false)
        {
            return false;
        }
        
        final File f0 = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "config" + File.separator + "realtimestock.xml");
        final boolean status0 = Utils.toXML(((StockTableModel)this.jTable1.getModel()).getStocks(), f0);
        if (status0 == false) {
            return status0;
        }

        final File f1 = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "config" + File.separator + "realtimestockalert.xml");
        return Utils.toXML(((StockTableModel)this.jTable1.getModel()).getAlerts(), f1);
    }
    
    private boolean saveBrokingFirmLogos() {
        if(Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "logos") == false)
        {
            return false;
        }
        
        if(Utils.deleteDir(new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "logos"), false) == false) {
            return false;
        }
        
        final int size = this.jStockOptions.getBrokingFirmSize();

        for(int i=0; i<size; i++) {
            final Image image = jStockOptions.getBrokingFirm(i).getLogo();
            
            if(image == null) continue;
            
            File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "logos" + File.separator + i + ".png");
                       
            try {
                ImageIO.write(Utils.toBufferedImage(image), "png", f);
            }
            catch(java.io.IOException exp) {
                log.error("", exp);
            }
        }
        
        return true;
    }

    private boolean saveStockCodeAndSymbolDatabase() {
        final Country country = jStockOptions.getCountry();
            
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database") == false)
        {
            return false;
        }
        
        if (stockCodeAndSymbolDatabase == null)
        {
            return false;
        }

        // This could happen when OutOfMemoryException happen while fetching stock database information
        // from the server.
        if (stockCodeAndSymbolDatabase.getCodes().size() <= 0)
        {
            log.info("Database was corrupted.");
            return false;
        }

        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "stockcodeandsymboldatabase.xml");
        return Utils.toXML(this.stockCodeAndSymbolDatabase, f);
    }
    
    private boolean saveJStockOptions() {
        if(Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config") == false)
        {
            return false;
        }
        
        File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "options.xml");
        return org.yccheok.jstock.gui.Utils.toXML(jStockOptions, f);
    }

    private void removeOldHistoryData(Country country) {
        // We do not want "yesterday" history record. We will remove 1 day old files.
        org.yccheok.jstock.gui.Utils.deleteAllOldFiles(new File(Utils.getUserDataDirectory() + country + File.separator + "history"), 1);
    }

    private void initAlertStateManager() {
        alertStateManager.clearState();
        alertStateManager.attach(alertStateManagerObserver);
    }

    private void initOthersStockHistoryMonitor()
    {
        final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories();

        this.indicatorPanel.initStockHistoryMonitor(Collections.unmodifiableList(stockServerFactories));
        this.indicatorScannerJPanel.initStockHistoryMonitor(Collections.unmodifiableList(stockServerFactories));
    }

	// Do not combine initOthersStockHistoryMonitor with initStockHistoryMonitor. We need to be able to update
	// only MainFrame's history monitor, when we change the history duration option. Other's history monitors
	// are not affected.
    private void initStockHistoryMonitor() {
        if(stockHistoryMonitor != null) {
            final StockHistoryMonitor oldStockHistoryMonitor = stockHistoryMonitor;
            zombiePool.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("Prepare to shut down " + oldStockHistoryMonitor + "...");
                    oldStockHistoryMonitor.clearStockCodes();
                    oldStockHistoryMonitor.dettachAll();
                    oldStockHistoryMonitor.stop();
                    log.info("Shut down " + oldStockHistoryMonitor + " peacefully.");
                }
            });
        }
        
        this.stockHistoryMonitor = new StockHistoryMonitor(NUM_OF_THREADS_HISTORY_MONITOR);
        
        final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories();
        stockHistoryMonitor.setStockServerFactories(stockServerFactories);
        
        stockHistoryMonitor.attach(this.stockHistoryMonitorObserver);

        final Country country = jStockOptions.getCountry();

        removeOldHistoryData(country);

        stockHistorySerializer = new StockHistorySerializer(Utils.getUserDataDirectory() + country + File.separator + "history");
        
        stockHistoryMonitor.setStockHistorySerializer(stockHistorySerializer);

        stockHistoryMonitor.setDuration(Duration.getTodayDurationByYears(jStockOptions.getHistoryDuration()));
    }
    
    private void initUsernameAndPassword() {
    }

    public void initLatestNewsTask()
    {
        if (jStockOptions.isAutoUpdateNewsEnabled() == true)
        {
            if (latestNewsTask == null)
            {
                latestNewsTask = new LatestNewsTask();
                latestNewsTask.execute();
            }
        }
        else
        {
            if (latestNewsTask != null) {
                final LatestNewsTask oldLatestNewsTask = latestNewsTask;
                zombiePool.execute(new Runnable() {
                    @Override
                    public void run() {
                        log.info("Prepare to shut down " + oldLatestNewsTask + "...");
                        oldLatestNewsTask.cancel(true);
                        log.info("Shut down " + oldLatestNewsTask + " peacefully.");
                    }
                });

                latestNewsTask = null;
            }
        }
    }
    
    private void initMarketThread() {
        if(marketThread != null) {
            final Thread oldMarketThread = marketThread;
            zombiePool.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("Prepare to shut down market thread " + oldMarketThread + "...");
                    oldMarketThread.interrupt();
                    try {
                        oldMarketThread.join();
                    } catch (InterruptedException ex) {
                        log.error(null, ex);
                    }
                    log.info("Shut down market thread " + oldMarketThread + " peacefully.");
                }
            });            
        }
        
        this.marketThread = new Thread(new MarketRunnable());
        this.marketThread.start();
    }
    
    private void initStockCodeAndSymbolDatabase(boolean readFromDisk) {
        // Stop any on-going activities.
        if (this.stockCodeAndSymbolDatabaseTask != null)
        {
            synchronized(this.stockCodeAndSymbolDatabaseTask)
            {
                this.stockCodeAndSymbolDatabaseTask._stop();
                stockCodeAndSymbolDatabase = null;
                ((AutoCompleteJComboBox)jComboBox1).setStockCodeAndSymbolDatabase(null);
                indicatorPanel.setStockCodeAndSymbolDatabase(null);                
            }
        }

        this.setStatusBar(true, "Connecting to stock server to retrieve stock information ...");
        statusBar.setImageIcon(getImageIcon("/images/16x16/network-connecting.png"), "Connecting ...");

        // We may hold a large database previously. Invoke garbage collector to perform cleanup.
        System.gc();
        
        stockCodeAndSymbolDatabaseTask = new StockCodeAndSymbolDatabaseTask(readFromDisk);
        stockCodeAndSymbolDatabaseTask.execute();
    }
    
    public void update(RealTimeStockMonitor monitor, final java.util.List<Stock> stocks) {
        // Do it in GUI event dispatch thread. Otherwise, we may face deadlock.
        // For example, we lock the jTable, and try to remove the stock from the
        // real time monitor. While we wait for the real time monitor to complete,
        // real time monitor will call this function and, be locked at function
        // updateStockToTable.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for(Stock stock : stocks) {
                    updateStockToTable(stock);
                    if(isStockBeingSelected(stock)) {
                        MainFrame.this.updateBuyerSellerInformation(stock);
                        MainFrame.this.updateDynamicChart(stock);
                    }
                }               
            }
        });

        // Dynamic charting. Intraday trader might love this.
        for (Stock stock : stocks) {
            final Code code = stock.getCode();
            DynamicChart dynamicChart = this.dynamicCharts.get(code);
            if (dynamicChart == null) {
                // Not found. Try to create a new dynamic chart.
                if (this.dynamicCharts.size() <= MainFrame.MAX_DYNAMIC_CHART_SIZE) {
                    dynamicChart = new DynamicChart();
                    this.dynamicCharts.put(code, dynamicChart);
                }
                else {
                    // Full already. Shall we remove?
                    if (this.isStockBeingSelected(stock)) {
                        Set<Code> codes = this.dynamicCharts.keySet();
                        for (Code c : codes) {
                            // Random remove. We do not care who is being removed.
                            this.dynamicCharts.remove(c);
                            if (this.dynamicCharts.size() <= MainFrame.MAX_DYNAMIC_CHART_SIZE) {
                                // Remove success.
                                break;
                            }
                        }
                        dynamicChart = new DynamicChart();
                        this.dynamicCharts.put(code, dynamicChart);
                    }
                }
            }   /* if (dynamicChart == null) */

            // Still null?
            if (dynamicChart == null) {
                // This usually indicate that dynamic chart list is full, and
                // no one is selecting this particular stock.
                continue;
            }

            if (this.isStockBeingSelected(stock)) {
                dynamicChart.addPriceObservation(stock.getCalendar().getTime(), stock.getLastPrice());
                final Stock s = stock;
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainFrame.this.updateDynamicChart(s);
                    }
                });
            }
            else {
                // Although no one is watching at us, we still need to perform notification.
                // Weird?
                dynamicChart.addPriceObservation(stock.getCalendar().getTime(), stock.getLastPrice());
            }
        }   /* for (Stock stock : stocks) */

        // No alert is needed. Early return.
        if ((jStockOptions.isSMSEnabled() == false) && (jStockOptions.isPopupMessage() == false) && (jStockOptions.isSoundEnabled() == false) && (jStockOptions.isSendEmail() == false)) {
            return;
        }

        final StockTableModel stockTableModel = (StockTableModel)jTable1.getModel();

        for (Stock stock : stocks) {
            final Double fallBelow = stockTableModel.getFallBelow(stock);            
            if (fallBelow != null) {
                final Indicator indicator = Utils.getLastPriceFallBelowIndicator(fallBelow);
                indicator.setStock(stock);
                alertStateManager.alert(indicator);
            }
            else {
                /*
                 * Having FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR, is to enable us
                 * to remove a particular indicator from AlertStateManager, without the need
                 * to call time-consuming getLastPriceFallBelowIndicator and
                 * getLastPriceRiseAboveIndicator. In order for indicator to perform
                 * correctly, we need to call indicator's mutable method (setStock).
                 * However, since FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR are
                 * sharable variables, we are not allowed to call setStock outside
                 * synchronized block. We need to perfrom a hacking liked workaround
                 * Within syncrhonized block, call getStock (To get old stock), setStock and
                 * restore back old stock.
                 */
                alertStateManager.clearState(FALL_BELOW_INDICATOR, stock);
            }

            final Double riseAbove = stockTableModel.getRiseAbove(stock);
            if (riseAbove != null) {
                final Indicator indicator = Utils.getLastPriceRiseAboveIndicator(riseAbove);
                indicator.setStock(stock);
                alertStateManager.alert(indicator);
            }
            else {
                /*
                 * Having FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR, is to enable us
                 * to remove a particular indicator from AlertStateManager, without the need
                 * to call time-consuming getLastPriceFallBelowIndicator and
                 * getLastPriceRiseAboveIndicator. In order for indicator to perform
                 * correctly, we need to call indicator's mutable method (setStock).
                 * However, since FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR are
                 * sharable variables, we are not allowed to call setStock outside
                 * synchronized block. We need to perfrom a hacking liked workaround
                 * Within syncrhonized block, call getStock (To get old stock), setStock and
                 * restore back old stock.
                 */
                alertStateManager.clearState(RISE_ABOVE_INDICATOR, stock);
            }
        }
    }

    private void update(final Market market) {
        // We are only interested in current selected country.
        if (market.getCountry() != this.jStockOptions.getCountry()) {
            return;
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               marketJPanel.update(market);
            }
        });
    }
    
    public void update(StockHistoryMonitor monitor, final StockHistoryMonitor.StockHistoryRunnable runnable) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Code code = runnable.getCode();
                // Possible null if we are trying to get index history.
                Symbol symbol = MainFrame.this.stockCodeAndSymbolDatabase.codeToSymbol(code);
                final boolean shouldShowGUI = MainFrame.this.stockCodeHistoryGUI.remove(code);
               
                if (stockCodeHistoryGUI.size() == 0) {
                    statusBar.setProgressBar(false);

                    if (runnable.getStockHistoryServer() != null) {
                        statusBar.setMainMessage((symbol != null ? symbol : code) + " history success.");
                    }
                    else {
                        statusBar.setMainMessage((symbol != null ? symbol : code) + " history failed.");
                    }
                }
                else {
                    if (runnable.getStockHistoryServer() != null) {
                        statusBar.setMainMessage((symbol != null ? symbol : code) + " history success. Still waiting for history total " + stockCodeHistoryGUI.size() + " ...");
                    }
                    else {
                        statusBar.setMainMessage((symbol != null ? symbol : code) + " history failed. Still waiting for history total " + stockCodeHistoryGUI.size() + " ...");
                    }
                }
               
                if ((runnable.getStockHistoryServer() != null) && shouldShowGUI) {
                    ChartJDialog chartJDialog = new ChartJDialog(MainFrame.this, (symbol != null ? symbol : code) + " (" + code + ")", false, runnable.getStockHistoryServer());
                    chartJDialog.setVisible(true);
                }
           } 
        });
    }
    
    private ImageIcon getImageIcon(String imageIcon) {
        return new javax.swing.ImageIcon(getClass().getResource(imageIcon));
    }
    
    private class TableRowPopupListener extends MouseAdapter {
        
        @Override
        public void mouseClicked(MouseEvent evt) {
            int[] rows = MainFrame.this.jTable1.getSelectedRows();
            
            if(rows.length == 1) {
                int row = rows[0];
                
                StockTableModel tableModel = (StockTableModel)jTable1.getModel();
                int modelIndex = jTable1.convertRowIndexToModel(row);
                Stock stock = tableModel.getStock(modelIndex);
                updateBuyerSellerInformation(stock);
                updateDynamicChart(stock);
            }
            else {
                updateBuyerSellerInformation(null);
                updateDynamicChart(null);
                
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if(jTable1.getSelectedColumn() != -1)
                    getMyJTablePopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private static class ColumnHeaderToolTips extends MouseMotionAdapter {

        // Current column whose tooltip is being displayed.
        // This variable is used to minimize the calls to setToolTipText().
        TableColumn curCol;
    
        // Maps TableColumn objects to tooltips
        java.util.Map<TableColumn, String> tips = new HashMap<TableColumn, String>();
    
        // If tooltip is null, removes any tooltip text.
        public void setToolTip(TableColumn col, String tooltip) {
            if (tooltip == null) {
                tips.remove(col);
            } else {
                tips.put(col, tooltip);
            }
        }
    
        @Override
        public void mouseMoved(MouseEvent evt) {
            TableColumn col = null;
            JTableHeader header = (JTableHeader)evt.getSource();
            JTable table = header.getTable();
            TableColumnModel colModel = table.getColumnModel();
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
    
            // Return if not clicked on any column header
            if (vColIndex >= 0) {
                col = colModel.getColumn(vColIndex);
            }
    
            if (col != curCol) {
                header.setToolTipText((String)tips.get(col));
                curCol = col;
            }
        }
    }
    
    public void displayPopupMessage(final String caption, final String message) {
        if(trayIcon == null) return;

        if (SwingUtilities.isEventDispatchThread())
        {
            trayIcon.displayMessage(caption, message, TrayIcon.MessageType.INFO);
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    trayIcon.displayMessage(caption, message, TrayIcon.MessageType.INFO);
                }
            });
        }
    }
    
    public StockHistorySerializer getStockHistorySerializer()
    {
        return stockHistorySerializer;
    }
    
    public IndicatorProjectManager getAlertIndicatorProjectManager()
    {
        return this.indicatorPanel.getAlertIndicatorProjectManager();
    }
    
    public void updateScanningSpeed(int speed) {
        this.realTimeStockMonitor.setDelay(speed);
        indicatorScannerJPanel.updateScanningSpeed(speed);
    }

    public void updateHistoryDuration(Duration historyDuration) {
        final Duration oldDuration = stockHistoryMonitor.getDuration();

        if (oldDuration.isContains(historyDuration))
        {
            this.stockHistoryMonitor.setDuration(historyDuration);
            return;
        }

        // The history files that we are going to read, their duration are
        // too short compared to historyDuration. The easiest way to overcome
        // this problem is to remove them all.
        log.info("We are going to remove all history files, due to new duration " + historyDuration + " is not within old duration " + oldDuration);

        Country[] countries = Country.values();
        for (Country country : countries)
        {
            Utils.deleteDir(new File(Utils.getUserDataDirectory() + country + File.separator + "history"), false);
        }

        // Avoid from using old history monitor. History monitor contains their own memory data.
        // Since their duration are no longer valid, the memory data are no longer valid too.
        //
        this.initStockHistoryMonitor();

    }

    public void repaintTable() {
        Component c = getSelectedComponent();
        
        if(c instanceof IndicatorScannerJPanel) {
            indicatorScannerJPanel.repaintTable();
        }
        else if(c instanceof IndicatorPanel) {
            
        }
        else {
            this.jTable1.repaint();
        }
    }
    
    private void initMarketJPanel() {
        if(this.marketJPanel != null) {
            jPanel2.remove(marketJPanel);            
        }
        
        this.marketJPanel = new MarketJPanel(jStockOptions.getCountry());
        jPanel2.add(marketJPanel);
        jPanel2.revalidate();
    }

    private void initPreloadDatabase() {
        /* No overwrite. */
        Utils.extractZipFile("database" + File.separator + "database.zip", false);
    }

    private class LatestNewsTask extends SwingWorker<Void, String> {
        // Delay first update checking for the 20 seconds
        private static final int SHORT_DELAY = 20 * 1000;

        private volatile CountDownLatch doneSignal;       

        @Override
        protected void done() {
        }

        @Override
        protected void process(java.util.List<String> messages) {
            boolean show = false;

            for(String message : messages)
            {
                AutoUpdateNewsJDialog dialog = new AutoUpdateNewsJDialog(MainFrame.this, true);
                dialog.setNews(message);
                dialog.setVisible(true);
                show = true;
            }

            if (show)
            {
                doneSignal.countDown();
            }
        }

        @Override
        protected Void doInBackground() {
            while (!isCancelled())
            {
                try {
                    Thread.sleep(SHORT_DELAY);
                } catch (InterruptedException ex) {
                    log.info(null, ex);
                    break;
                }
                final java.util.Map<String, String> map = Utils.getUUIDValue(org.yccheok.jstock.engine.Utils.getJStockStaticServer() + "news_information/index.txt");
                final String newsID = MainFrame.this.getJStockOptions().getNewsID();
                if (newsID.equals(map.get("news_id"))) {
                    // Seen before. Quit.
                    break;
                }
                final String location = map.get("news_url");
                if (location == null) {
                    break;
                }
                doneSignal = new CountDownLatch(1);
                final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
                if (respond == null)
                {                    
                    break;
                }
                if (respond.indexOf(Utils.getJStockUUID()) < 0)
                {
                    break;
                }
                publish(respond);
                try {
                    doneSignal.await();
                } catch (InterruptedException ex) {
                    log.info(null, ex);
                    break;
                }
                // Update jStockOptions, will make this while loop break
                // in next iteration.
                jStockOptions.setNewsID(map.get("news_id"));
            }

            return null;
        }
    }

    public Component getSelectedComponent() {
        return this.jTabbedPane1.getSelectedComponent();
    }

    public void flashChatTabIfNeeded()
    {
        if (jStockOptions.isChatFlashNotificationEnabled() == false) {
            return;
        }

        if (this.getSelectedComponent() == this.chatJPanel) {
            return;
        }

        if (timer != null) {
            return;
        }

        timer = new javax.swing.Timer(TIMER_DELAY, this.getTimerActionListener());
        timer.setInitialDelay(0);
        timer.start();
    }

    public void stopChatServiceManager()
    {
        this.chatJPanel.stopChatServiceManager();
    }

    public void startChatServiceManager()
    {
        this.chatJPanel.startChatServiceManager();
    }

    public void updateChatJPanelUIAccordingToOptions()
    {
        this.chatJPanel.updateUIAccordingToOptions();
    }

    private void initChatDatas() {
        /* No overwrite. */
        Utils.extractZipFile("chat" + File.separator + "chat.zip", false);
    }

    // Unlike a JButton, setIcon() does not add an icon to the text label. 
    // Rather, in a radio button, the method is used to customize the icons used
    // to depict its state. However, by using the HTML capabilities in a label, 
    // it is possible to add an icon to the label without affecting the 
    // state-depicting icons.
    // We need to have image files being extracted outside executable jar file.
    private void initExtraDatas() {
        /* No overwrite. */
        Utils.extractZipFile("extra" + File.separator + "extra.zip", false);
    }

    private ActionListener getTimerActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainFrame.this.jTabbedPane1.getIconAt(4) == MainFrame.this.smileIcon)
                {
                   MainFrame.this.jTabbedPane1.setIconAt(4, smileGrayIcon);
                }
                else
                {
                    MainFrame.this.jTabbedPane1.setIconAt(4, smileIcon);
                }
            }
        };
    }

    public boolean isChatLogin() {
        return this.chatJPanel.isLogin();
    }

    public boolean changeChatPassword(String newPassword) {
        return this.chatJPanel.changePassword(newPassword);
    }

    private void initDynamicCharts()
    {
        dynamicCharts.clear();
    }

    private void initStatusBar()
    {
        final String message = "Connecting to stock server to retrieve stock information ...";
        final ImageIcon icon = getImageIcon("/images/16x16/network-connecting.png");
        final String iconMessage = "Connecting...";
        
        statusBar.setMainMessage(message)
                .setImageIcon(icon, iconMessage)
                .setCountryIcon(jStockOptions.getCountry().getIcon(), jStockOptions.getCountry().toString());
    }

    private MouseAdapter getDynamicChartMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final Stock stock = getSelectedStock();
                    if (stock == null) {
                        return;
                    }

                    final DynamicChart dynamicChart = MainFrame.this.dynamicCharts.get(stock.getCode());
                    if (dynamicChart == null) {
                        return;
                    }

                    final Symbol symbol = MainFrame.this.getStockCodeAndSymbolDatabase().codeToSymbol(stock.getCode());
                    dynamicChart.showNewJDialog(MainFrame.this, symbol + " Intraday Movement");
                }
            }
            // Shall we provide visualize mouse move over effect, so that user
            // knows this is a clickable component?
            /*
            private final LineBorder lineBorder = new LineBorder(Color.WHITE);
            private Border oldBorder = null;

            @Override
            public void mouseEntered(MouseEvent e) {
                JPanel jPanel = (JPanel)e.getComponent();
                Border old = jPanel.getBorder();
                if (old != lineBorder) {
                    oldBorder = old;
                }
                jPanel.setBorder(lineBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JPanel jPanel = (JPanel)e.getComponent();
                jPanel.setBorder(oldBorder);
            }
            */
        };
    }

    private class TableKeyEventListener extends java.awt.event.KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent e) {
            MainFrame.this.jTable1.getSelectionModel().clearSelection();
        }
    }

    private TrayIcon trayIcon;
    
    private static final Log log = LogFactory.getLog(MainFrame.class);
        
    private MyJXStatusBar statusBar = new MyJXStatusBar();

    // A set of stock history which we need to display GUI on them, when user request explicitly.
    private java.util.Set<Code> stockCodeHistoryGUI = new java.util.HashSet<Code>();
    
    private volatile StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase = null;
    private RealTimeStockMonitor realTimeStockMonitor = null;
    private StockHistoryMonitor stockHistoryMonitor = null;
    private StockCodeAndSymbolDatabaseTask stockCodeAndSymbolDatabaseTask = null;
    private LatestNewsTask latestNewsTask = null;
    private Thread marketThread = null;
    private StockHistorySerializer stockHistorySerializer = null;        
    private JStockOptions jStockOptions;

    // As workaround to overcome the bug, when new look n feel being applied during runtime, the original
    // KeyListner for ComboBoxEditor will be removed.
    private final KeyListener jComboBox1EditorComponentKeyAdapter = getjComboBox1EditorComponentKeyAdapter();
    
    private IndicatorPanel indicatorPanel;
    private IndicatorScannerJPanel indicatorScannerJPanel;
    private PortfolioManagementJPanel portfolioManagementJPanel;
    private org.yccheok.jstock.chat.ChatJPanel chatJPanel;

    private final AlertStateManager alertStateManager = new AlertStateManager();
    private ExecutorService emailAlertPool = Executors.newFixedThreadPool(1);
    private ExecutorService smsAlertPool = Executors.newFixedThreadPool(1);
    private ExecutorService systemTrayAlertPool = Executors.newFixedThreadPool(1);

    private org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, java.util.List<Stock>> realTimeStockMonitorObserver = this.getRealTimeStockMonitorObserver();
    private org.yccheok.jstock.engine.Observer<StockHistoryMonitor, StockHistoryMonitor.StockHistoryRunnable> stockHistoryMonitorObserver = this.getStockHistoryMonitorObserver();
    private org.yccheok.jstock.engine.Observer<Indicator, Boolean> alertStateManagerObserver = this.getAlertStateManagerObserver();

    private final javax.swing.ImageIcon smileIcon = this.getImageIcon("/images/16x16/smile.png");
    private final javax.swing.ImageIcon smileGrayIcon = this.getImageIcon("/images/16x16/smile-gray.png");
    private javax.swing.Timer timer = null;
    private static final int TIMER_DELAY = 500;

    private Executor zombiePool = Utils.getZoombiePool();
    
    private MarketJPanel marketJPanel;

    // Use ConcurrentHashMap, enable us able to read and write using different
    // threads.
    private java.util.Map<Code, DynamicChart> dynamicCharts = new java.util.concurrent.ConcurrentHashMap<Code, DynamicChart>();
    // We have 720 (6 * 60 * 2) points per chart, based on 10 seconds per points, with maximum 2 hours.
    // By having maximum 10 charts, we shall not face any memory problem.
    private static final int MAX_DYNAMIC_CHART_SIZE = 10;
    private static final DynamicChart EMPTY_DYNAMIC_CHART = new DynamicChart();
    private final MouseAdapter dynamicChartMouseAdapter = getDynamicChartMouseAdapter();
    
    private static final int NUM_OF_RETRY = 3;
    private static final int NUM_OF_THREADS_HISTORY_MONITOR = 4;

    /*
     * Having FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR, is to enable us
     * to remove a particular indicator from AlertStateManager, without the need
     * to call time-consuming getLastPriceFallBelowIndicator and
     * getLastPriceRiseAboveIndicator. In order for indicator to perform
     * correctly, we need to call indicator's mutable method (setStock).
     * However, since FALL_BELOW_INDICATOR and RISE_ABOVE_INDICATOR are
     * sharable variables, we are not allowed to call setStock outside
     * synchronized block. We need to perfrom a hacking liked workaround
     * Within syncrhonized block, call getStock (To get old stock), setStock and 
     * restore back old stock.
     */
    private static final Indicator FALL_BELOW_INDICATOR = Utils.getLastPriceFallBelowIndicator(0.0);
    private static final Indicator RISE_ABOVE_INDICATOR = Utils.getLastPriceRiseAboveIndicator(0.0);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    
}
