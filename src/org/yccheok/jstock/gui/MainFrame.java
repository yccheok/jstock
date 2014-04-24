/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2014 Yan Cheng Cheok <yccheok@yahoo.com>
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

import org.yccheok.jstock.gui.charting.ChartJDialog;
import org.yccheok.jstock.gui.charting.ChartJDialogOptions;
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
import javax.swing.*;
import java.text.MessageFormat;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.yccheok.jstock.alert.SMSLimiter;
import org.yccheok.jstock.analysis.Indicator;
import org.yccheok.jstock.analysis.OperatorIndicator;
import org.yccheok.jstock.engine.ResultType;
import org.yccheok.jstock.engine.Stock.Board;
import org.yccheok.jstock.engine.Stock.Industry;
import org.yccheok.jstock.file.Atom;
import org.yccheok.jstock.file.GUIBundleWrapper;
import org.yccheok.jstock.file.Statement;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.charting.DynamicChart;
import org.yccheok.jstock.gui.portfolio.PortfolioJDialog;
import org.yccheok.jstock.gui.table.NonNegativeDoubleEditor;
import org.yccheok.jstock.gui.watchlist.WatchlistJDialog;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.network.ProxyDetector;
import org.yccheok.jstock.portfolio.PortfolioInfo;
import org.yccheok.jstock.watchlist.WatchlistInfo;

/**
 *
 * @author  doraemon
 */
public class MainFrame extends javax.swing.JFrame {

    public static final class CSVWatchlist {
        public final TableModel tableModel;
        
        private CSVWatchlist(TableModel tableModel) {
            if (tableModel == null) {
                throw new java.lang.IllegalArgumentException();
            }
            this.tableModel = tableModel;
        }
        
        public static CSVWatchlist newInstance(TableModel tableModel) {
            return new CSVWatchlist(tableModel);
        }
    }
    
    // Comment out, to avoid annoying log messages during debugging.
    static { System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); }
    
    /** Creates new form MainFrame */

    // Private constructor is sufficient to suppress unauthorized calls to the constructor
    private MainFrame()
    {
    }

    /**
     * Initialize this MainFrame based on the JStockOptions.
     */
    private void init() {

        try {
            UIManager.setLookAndFeel(getJStockOptions().getLooknFeel());
        }
        catch (java.lang.ClassNotFoundException exp) {
            log.error(null, exp);
        }
        catch (java.lang.InstantiationException exp) {
            log.error(null, exp);
        }
        catch (java.lang.IllegalAccessException exp) {
            log.error(null, exp);
        }
        catch (javax.swing.UnsupportedLookAndFeelException exp) {
            log.error(null, exp);
        }

        initComponents();

        createLookAndFeelMenuItem();
        createCountryMenuItem();

        createStockIndicatorEditor();
        createIndicatorScannerJPanel();
        createPortfolioManagementJPanel();

        createIconsAndToolTipTextForJTabbedPane();

        this.createSystemTrayIcon();

        this.initPreloadDatabase(false);
        this.initExtraDatas();
        this.initStatusBar();
        this.initMarketJPanel();
        this.initTableHeaderToolTips();
        this.initMyJXStatusBarExchangeRateLabelMouseAdapter();
        this.initMyJXStatusBarCountryLabelMouseAdapter();
        this.initMyJXStatusBarImageLabelMouseAdapter();
        this.initStockInfoDatabaseMeta();
        this.initDatabase(true);
        this.initAjaxProvider();
        this.initMarketThread();
        this.initLatestNewsTask();
        this.initKLSEInfoStockServerFactoryThread();
        this.initCurrencyExchangeMonitor();
        this.initRealTimeStockMonitor();
        this.initWatchlist();
        this.initAlertStateManager();
        this.initDynamicCharts();
        this.initDynamicChartVisibility();
        this.initStockHistoryMonitor();
        this.initOthersStockHistoryMonitor();
        this.initBrokingFirmLogos();
        this.initGUIOptions();
        this.initChartJDialogOptions();
        this.initLanguageMenuItemsSelection();        
        this.initJXLayerOnJComboBox();
        this.initKeyBindings();
        
        // Turn to the last viewed page.
        final int lastSelectedPageIndex = this.getJStockOptions().getLastSelectedPageIndex();
        if (this.jTabbedPane1.getTabCount() > lastSelectedPageIndex) {
            this.jTabbedPane1.setSelectedIndex(lastSelectedPageIndex);
        }

        // setSelectedIndex will not always trigger jTabbedPane1StateChanged,
        // if the selected index is same as current page index. However, we are
        // expecting jTabbedPane1StateChanged to suspend/resume
        // PortfolioManagementJPanel's RealtTimeStockMonitor and MainFrame's
        // CurrencyExchangeMonitor, in order to preserve network resource. Hence,
        // we need to call handleJTabbedPaneStateChanged explicitly.
        handleJTabbedPaneStateChanged(this.jTabbedPane1);

        // Restore previous size and location.
        JStockOptions.BoundsEx boundsEx = jStockOptions.getBoundsEx();
        if (boundsEx == null) {
            // First time. Maximize it.
            this.setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            if ((boundsEx.extendedState & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                this.setExtendedState(Frame.MAXIMIZED_BOTH);
            } else {
                this.setBounds(boundsEx.bounds);
            }
        }
        
        installShutdownHook();
        
        // Spain no longer supported. Sad...
        if (this.jStockOptions.getCountry() == Country.Spain) {
            JOptionPane.showMessageDialog(
                null, 
                MessagesBundle.getString("info_message_spain_not_supported"), 
                MessagesBundle.getString("info_title_spain_not_supported"), 
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void requestFocusOnJComboBox() {
        this.jComboBox1.getEditor().getEditorComponent().requestFocus();
    }

    private void initKeyBindings() {
        KeyStroke watchlistNavigationKeyStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK);
        KeyStroke portfolioNavigationKeyStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(watchlistNavigationKeyStroke, "watchlistNavigation");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(portfolioNavigationKeyStroke, "portfolioNavigation");
        getRootPane().getActionMap().put("watchlistNavigation", new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                watchlistNavigation();
            }
        });
        getRootPane().getActionMap().put("portfolioNavigation", new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                portfolioNavigation();
            }
        });        
    }
    
    private void watchlistNavigation() {
        if (this.getSelectedComponent() != this.jPanel8) {
            // The page is not active. Make it active.
            MainFrame.this.jTabbedPane1.setSelectedIndex(0);
            return;
        }
        
        final java.util.List<String> watchlistNames = org.yccheok.jstock.watchlist.Utils.getWatchlistNames();
        final int size = watchlistNames.size();
        if (size <= 1) {
            // Nothing to navigate.
            return;
        }
        final String currentWatchlistName = this.getJStockOptions().getWatchlistName();
        
        int index = 0;
        
        for (; index < size; index++) {
            if (watchlistNames.get(index).equals(currentWatchlistName)) {
                index++;
                if (index >= size) index = 0;
                break;
            }
        }
        this.selectActiveWatchlist(watchlistNames.get(index));
    }
    
    private void portfolioNavigation() {
        if (this.getSelectedComponent() != this.portfolioManagementJPanel) {
            // The page is not active. Make it active.
            MainFrame.this.jTabbedPane1.setSelectedIndex(3);
            return;
        }
        
        final java.util.List<String> portfolioNames = org.yccheok.jstock.portfolio.Utils.getPortfolioNames();
        final int size = portfolioNames.size();
        if (size <= 1) {
            // Nothing to navigate.
            return;
        }        
        final String currentPortfolioName = this.getJStockOptions().getPortfolioName();
        
        int index = 0;        
        for (; index < size; index++) {
            if (portfolioNames.get(index).equals(currentPortfolioName)) {
                index++;
                if (index >= size) index = 0;
                break;
            }
        }
        this.selectActivePortfolio(portfolioNames.get(index));
    }
    
    // Register a hook to save app settings when quit via the app menu.
    // This is in Mac OSX only.   
    // http://sourceforge.net/tracker/?func=detail&aid=3490453&group_id=202896&atid=983418
    private void installShutdownHook() {
        if (Utils.isMacOSX()) {
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    if (isFormWindowClosedCalled) {
                        AppLock.unlock();
                        return;
                    }
                    
                    formWindowClosed(null);
                    
                    AppLock.unlock();
                }
            };
            Runtime.getRuntime().addShutdownHook(new Thread(runner, "Window Prefs Hook"));
        } else {
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    AppLock.unlock();
                }
            };
            Runtime.getRuntime().addShutdownHook(new Thread(runner, "Window Prefs Hook"));
        }
    }
    
    /**
     * Initialize language menu items so that correct item is being selected
     * according to current default locale.
     */
    private void initLanguageMenuItemsSelection() {
        // Please revise Statement's construct code, when adding in new language.
        // So that its language guessing algorithm will work as it is.

        final Locale defaultLocale = Locale.getDefault();
        if (Utils.isTraditionalChinese(defaultLocale)) {
            this.jRadioButtonMenuItem4.setSelected(true);
        } else if (Utils.isSimplifiedChinese(defaultLocale)) {
            this.jRadioButtonMenuItem2.setSelected(true);
        } else if (defaultLocale.getLanguage().equals(Locale.GERMAN.getLanguage())) {
            this.jRadioButtonMenuItem3.setSelected(true);
        } else if (defaultLocale.getLanguage().equals(Locale.ITALIAN.getLanguage())) {
            this.jRadioButtonMenuItem5.setSelected(true);
        } else {
            this.jRadioButtonMenuItem1.setSelected(true);
        }
    }

    /**
     * MainFrameHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to MainFrameHolder.INSTANCE, not before.
     */
    private static class MainFrameHolder {
        private final static MainFrame INSTANCE = new MainFrame();
    }

    /**
     * Returns MainFrame as singleton.
     * 
     * @return MainFrame as singleton
     */
    public static MainFrame getInstance() {
        return MainFrameHolder.INSTANCE;
    }

    // Install JXLayer around JComboBox.
    // It is used to display busy indicator.
    private void initJXLayerOnJComboBox() {
        // Add the layer as usual combo box.
        jPanel1.add(Utils.getBusyJXLayer((AutoCompleteJComboBox)this.jComboBox1));
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
        jComboBox1 = new AutoCompleteJComboBox();
        jPanel6 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenu10 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem6 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenu8 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();

        jComboBox1.setEditable(true);
        jComboBox1.setPreferredSize(new java.awt.Dimension(150, 24));
        ((AutoCompleteJComboBox)this.jComboBox1).attachStockInfoObserver(getStockInfoObserver());
        ((AutoCompleteJComboBox)this.jComboBox1).attachResultObserver(getResultObserver());
        ((AutoCompleteJComboBox)this.jComboBox1).attachMatchObserver(getMatchObserver());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("MainFrame_Application_Title")); // NOI18N
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
        jTable1.setFont(jTable1.getFont().deriveFont(jTable1.getFont().getStyle() | java.awt.Font.BOLD, jTable1.getFont().getSize()+1));
        jTable1.setModel(new StockTableModel());
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        this.jTable1.setDefaultRenderer(Number.class, new StockTableCellRenderer(SwingConstants.RIGHT));
        this.jTable1.setDefaultRenderer(Double.class, new StockTableCellRenderer(SwingConstants.RIGHT));
        this.jTable1.setDefaultRenderer(Object.class, new StockTableCellRenderer(SwingConstants.LEFT));

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

        jLabel1.setText(bundle.getString("MainFrame_Stock")); // NOI18N
        jPanel1.add(jLabel1);

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

        jPanel8.add(jPanel10, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab(bundle.getString("MainFrame_Title"), jPanel8); // NOI18N

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridLayout(2, 1));
        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        jMenu3.setText(bundle.getString("MainFrame_File")); // NOI18N

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/project_open.png"))); // NOI18N
        jMenuItem2.setText(bundle.getString("MainFrame_Open...")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filesave.png"))); // NOI18N
        jMenuItem9.setText(bundle.getString("MainFrame_SaveAs...")); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);
        jMenu3.add(jSeparator7);

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
        jMenu3.add(jSeparator8);

        jMenuItem1.setText(bundle.getString("MainFrame_Exit")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuBar2.add(jMenu3);

        jMenu5.setText(bundle.getString("MainFrame_Edit")); // NOI18N

        jMenuItem4.setText(bundle.getString("MainFrame_AddStocks...")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem4);

        jMenuItem7.setText(bundle.getString("MainFrame_ClearAllStocks")); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem7);
        jMenu5.add(jSeparator4);

        jMenuItem15.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem15.setText(bundle.getString("MainFrame_RefreshStockPrices")); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jMenuBar2.add(jMenu5);

        jMenu6.setText(bundle.getString("MainFrame_Country")); // NOI18N
        jMenuBar2.add(jMenu6);

        jMenu10.setText(bundle.getString("MainFrame_Language")); // NOI18N

        buttonGroup3.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText(Locale.ENGLISH.getDisplayLanguage(Locale.getDefault()));
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu10.add(jRadioButtonMenuItem1);

        buttonGroup3.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText(Locale.SIMPLIFIED_CHINESE.getDisplayName(Locale.getDefault()));
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu10.add(jRadioButtonMenuItem2);

        buttonGroup3.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setText(Locale.TRADITIONAL_CHINESE.getDisplayName(Locale.getDefault()));
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        jMenu10.add(jRadioButtonMenuItem4);

        buttonGroup3.add(jRadioButtonMenuItem6);
        jRadioButtonMenuItem6.setText(Locale.FRENCH.getDisplayLanguage(Locale.getDefault()));
        jRadioButtonMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem6ActionPerformed(evt);
            }
        });
        jMenu10.add(jRadioButtonMenuItem6);

        buttonGroup3.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setText(Locale.GERMAN.getDisplayLanguage(Locale.getDefault()));
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        jMenu10.add(jRadioButtonMenuItem3);

        buttonGroup3.add(jRadioButtonMenuItem5);
        jRadioButtonMenuItem5.setText(Locale.ITALIAN.getDisplayLanguage(Locale.getDefault()));
        jRadioButtonMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem5ActionPerformed(evt);
            }
        });
        jMenu10.add(jRadioButtonMenuItem5);

        jMenuBar2.add(jMenu10);

        jMenu7.setText(bundle.getString("MainFrame_Database")); // NOI18N

        jMenuItem8.setText(bundle.getString("MainFrame_StockDatabase...")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem8);

        jMenuBar2.add(jMenu7);

        jMenu9.setText(bundle.getString("MainFrame_Watchlist")); // NOI18N
        jMenu9.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu9MenuSelected(evt);
            }
        });
        jMenuBar2.add(jMenu9);

        jMenu8.setText(bundle.getString("MainFrame_Portfolio")); // NOI18N
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

        jMenu1.setText(bundle.getString("MainFrame_Options")); // NOI18N

        jMenuItem6.setText(bundle.getString("MainFrame_Options...")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar2.add(jMenu1);

        jMenu4.setText(bundle.getString("MainFrame_LooknFeel")); // NOI18N
        jMenuBar2.add(jMenu4);

        jMenu2.setText(bundle.getString("MainFrame_Help")); // NOI18N

        jMenuItem3.setText(bundle.getString("MainFrame_OnlineHelp")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem16.setText(bundle.getString("MainFrame_KeyboardShortcuts")); // NOI18N
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem16);

        jMenuItem12.setText(bundle.getString("MainFrame_Calculator")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem12);
        jMenu2.add(jSeparator6);

        jMenuItem13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/smile2.png"))); // NOI18N
        jMenuItem13.setText(bundle.getString("MainFrame_DonateToJStock")); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuItem14.setText(bundle.getString("MainFrame_ContributeToJStock")); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem14);
        jMenu2.add(jSeparator5);

        jMenuItem5.setText(bundle.getString("MainFrame_About...")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar2.add(jMenu2);

        jMenu11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/android-small.png"))); // NOI18N
        jMenu11.setText(bundle.getString("MainFrame_Android")); // NOI18N
        jMenu11.setFont(jMenu11.getFont().deriveFont(jMenu11.getFont().getStyle() | java.awt.Font.BOLD));

        jMenuItem17.setText(bundle.getString("MainFrame_DownloadJStockAndroid")); // NOI18N
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem17);

        jMenuBar2.add(jMenu11);

        setJMenuBar(jMenuBar2);

        setSize(new java.awt.Dimension(952, 478));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if (this.getStockInfoDatabase() == null) {
            javax.swing.JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("info_message_we_havent_connected_to_stock_server"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("info_title_we_havent_connected_to_stock_server"), javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StockJDialog stockJDialog = new StockJDialog(this, true);
        stockJDialog.setLocationRelativeTo(this);
        stockJDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        this.clearAllStocks();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private boolean openAsCSVFile(File file) {
        final Statements statements = Statements.newInstanceFromCSVFile(file);
        return this.openAsStatements(statements, file);
    }

    public boolean openAsStatements(Statements statements, File file) {
        assert(statements != null);
        
        final GUIBundleWrapper guiBundleWrapper = statements.getGUIBundleWrapper();
        
        if (statements.getType() == Statement.Type.RealtimeInfo) {
            final int size = statements.size();
            for (int i = 0; i < size; i++) {
                final org.yccheok.jstock.file.Statement statement = statements.get(i);
                final String codeStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Code"));
                final String symbolStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Symbol"));
                final Double fallBelowDouble = statement.getValueAsDouble(guiBundleWrapper.getString("MainFrame_FallBelow"));
                final Double riseAboveDouble = statement.getValueAsDouble(guiBundleWrapper.getString("MainFrame_RiseAbove"));
                if (codeStr.length() > 0 && symbolStr.length() > 0) {
                    final Stock stock = Utils.getEmptyStock(Code.newInstance(codeStr), Symbol.newInstance(symbolStr));
                    final StockAlert stockAlert = new StockAlert().setFallBelow(fallBelowDouble).setRiseAbove(riseAboveDouble);
                    this.addStockToTable(stock, stockAlert);
                    realTimeStockMonitor.addStockCode(Code.newInstance(codeStr));
                }
            }
            realTimeStockMonitor.startNewThreadsIfNecessary();
            realTimeStockMonitor.refresh();
        } else if (statements.getType() == Statement.Type.StockIndicatorScanner) {
            // Some users request of having Stock Watchlist able to load stocks
            // saved from Stock Indicators Scanner.
            final int size = statements.size();
            for (int i = 0; i < size; i++) {
                final org.yccheok.jstock.file.Statement statement = statements.get(i);
                final String codeStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Code"));
                final String symbolStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Symbol"));
                if (codeStr.length() > 0 && symbolStr.length() > 0) {
                    final Stock stock = Utils.getEmptyStock(Code.newInstance(codeStr), Symbol.newInstance(symbolStr));
                    this.addStockToTable(stock);
                    realTimeStockMonitor.addStockCode(Code.newInstance(codeStr));
                }
            }
            realTimeStockMonitor.startNewThreadsIfNecessary();
            realTimeStockMonitor.refresh();
        } else if (statements.getType() == Statement.Type.PortfolioManagementBuy || statements.getType() == Statement.Type.PortfolioManagementSell || statements.getType() == Statement.Type.PortfolioManagementDeposit || statements.getType() == Statement.Type.PortfolioManagementDividend) {
            /* Open using other tabs. */
            return this.portfolioManagementJPanel.openAsStatements(statements, file);
        } else {
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

    private void handleJTabbedPaneStateChanged(JTabbedPane pane) {
        // MainFrame
        if (pane.getSelectedComponent() == this.jPanel8) {
            this.jMenuItem2.setEnabled(true);   // Load
            this.jMenuItem9.setEnabled(true);   // Save
            this.jMenuItem4.setEnabled(true);   // Add Stocks...
            this.jMenuItem7.setEnabled(true);   // Clear All Stocks...
            this.jMenuItem15.setEnabled(true);  // Refresh Stock Prices
            
            requestFocusOnJComboBox();
        }
        else if (pane.getSelectedComponent() == this.indicatorPanel) {
            this.jMenuItem2.setEnabled(false);  // Load
            this.jMenuItem9.setEnabled(false);  // Save
            this.jMenuItem4.setEnabled(false);  // Add Stocks...
            this.jMenuItem7.setEnabled(false);  // Clear All Stocks...
            this.jMenuItem15.setEnabled(false); // Refresh Stock Prices
        }
        else if(pane.getSelectedComponent() == this.indicatorScannerJPanel) {
            this.jMenuItem2.setEnabled(false);  // Load
            this.jMenuItem9.setEnabled(true);   // Save
            this.jMenuItem4.setEnabled(false);  // Add Stocks...
            this.jMenuItem7.setEnabled(false);  // Clear All Stocks...
            this.jMenuItem15.setEnabled(true);  // Refresh Stock Prices            
        }
        else if(pane.getSelectedComponent() == this.portfolioManagementJPanel) {
            this.jMenuItem2.setEnabled(true);   // Load
            this.jMenuItem9.setEnabled(true);   // Save
            this.jMenuItem4.setEnabled(false);  // Add Stocks...
            this.jMenuItem7.setEnabled(false);  // Clear All Stocks...
            this.jMenuItem15.setEnabled(true);  // Refresh Stock Prices            
        }   
        
        if (this.isStatusBarBusy == false) {
            this.setStatusBar(false, this.getBestStatusBarMessage());
        }
    }

    // Policy : Each pane should have their own real time stock monitoring.
    //
    //          Each pane should share history monitoring with main frame, 
    //          for optimized history retrieving purpose.
    //
    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        JTabbedPane pane = (JTabbedPane)evt.getSource();
        handleJTabbedPaneStateChanged(pane);
    }//GEN-LAST:event_jTabbedPane1StateChanged


    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        OptionsJDialog optionsJDialog = new OptionsJDialog(this, true);
        optionsJDialog.setLocationRelativeTo(this);
        optionsJDialog.set(jStockOptions);
        optionsJDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    /**
     * Returns JStock options of this main frame.
     * @return JStock options of this main frame
     */
    public JStockOptions getJStockOptions() {
        return this.jStockOptions;
    }

    /**
     * Returns the chart dialog options of this main frame.
     * @return the chart dialog options of this main frame
     */
    public ChartJDialogOptions getChartJDialogOptions() {
        return this.chartJDialogOptions;
    }

    /**
     * Save the entire application settings.
     */
    public void save() {
        // Save the last viewed page.
        this.getJStockOptions().setLastSelectedPageIndex(this.jTabbedPane1.getSelectedIndex());

        // Save current window size and position.
        JStockOptions.BoundsEx boundsEx = new JStockOptions.BoundsEx(this.getBounds(), this.getExtendedState());
        this.getJStockOptions().setBoundsEx(boundsEx);
        
        jStockOptions.setApplicationVersionID(Utils.getApplicationVersionID());

        this.saveJStockOptions();
        this.saveGUIOptions();
        this.saveChartJDialogOptions();
        this.saveBrokingFirmLogos();
        this.saveWatchlist();
        this.indicatorPanel.saveAlertIndicatorProjectManager();
        this.indicatorPanel.saveModuleIndicatorProjectManager();
        this.portfolioManagementJPanel.savePortfolio();
    }

    /**
     * Dettach all and stop Ajax threading activity in combo box. Once stop, 
     * this combo box can no longer be reused.
     */
    private void dettachAllAndStopAutoCompleteJComboBox() {
        // We are no longer interest to receive any event from combo box.
        ((AutoCompleteJComboBox)this.jComboBox1).dettachAll();
        // Stop all threading activities in AutoCompleteJComboBox.
        ((AutoCompleteJComboBox)this.jComboBox1).stop();
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
        isFormWindowClosedCalled = true;
        
        try {
            ExecutorService _stockInfoDatabaseMetaPool = this.stockInfoDatabaseMetaPool;
            this.stockInfoDatabaseMetaPool = null;
        
            _stockInfoDatabaseMetaPool.shutdownNow();
            
            // Always be the first statement. As no matter what happen, we must
            // save all the configuration files.
            this.save();

            if (this.needToSaveUserDefinedDatabase) {
                // We are having updated user database in memory.
                // Save it to disk.
                this.saveUserDefinedDatabaseAsCSV(jStockOptions.getCountry(), stockInfoDatabase);
            }

            // Hide the icon immediately.
            TrayIcon _trayIcon = trayIcon;
            if (_trayIcon != null) {
                SystemTray.getSystemTray().remove(_trayIcon);
                trayIcon = null;
            }
            
            dettachAllAndStopAutoCompleteJComboBox();
            this.indicatorPanel.dettachAllAndStopAutoCompleteJComboBox();
            
            log.info("latestNewsTask stop...");

            if (this.latestNewsTask != null)
            {
                this.latestNewsTask.cancel(true);
            }

            _stockInfoDatabaseMetaPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            
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
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
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
        updateDynamicChart(null);
    }//GEN-LAST:event_formMouseClicked

    private void updateDynamicChart(Stock stock) {
        assert(java.awt.EventQueue.isDispatchThread());

        DynamicChart dynamicChart = stock != null ? this.dynamicCharts.get(stock.code) : MainFrame.EMPTY_DYNAMIC_CHART;
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
        if (KeyEvent.VK_DELETE == evt.getKeyCode()) {
            this.deteleSelectedTableRow();
            return;
        }
        
        if (evt.isActionKey()) {
            int[] rows = MainFrame.this.jTable1.getSelectedRows();
            
            if (rows.length == 1) {
                int row = rows[0];
                
                final StockTableModel tableModel = (StockTableModel)jTable1.getModel();
                final int modelIndex = jTable1.convertRowIndexToModel(row);
                final Stock stock = tableModel.getStock(modelIndex);
                this.updateDynamicChart(stock);
            } else {
                this.updateDynamicChart(null);
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (this.indicatorPanel.promptToSaveSignificantEdits()) {
            this.dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // Use local variable to ensure thread safety.
        final StockInfoDatabase stock_info_database = this.stockInfoDatabase;

        if (stock_info_database == null) {
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("info_message_there_are_no_database_ready_yet"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("info_title_there_are_no_database_ready_yet"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // stockDatabaseJDialog will be calling mutable methods of
        // stock_info_database.
        StockDatabaseJDialog stockDatabaseJDialog = new StockDatabaseJDialog(this, stock_info_database, true);
        stockDatabaseJDialog.setSize(540, 540);
        stockDatabaseJDialog.setLocationRelativeTo(this);
        stockDatabaseJDialog.setVisible(true); 

        if (stockDatabaseJDialog.getResult() != null) {
            assert(stockDatabaseJDialog.getResult() == this.stockInfoDatabase);
            this.stockInfoDatabase = stockDatabaseJDialog.getResult();
            ((AutoCompleteJComboBox)jComboBox1).setStockInfoDatabase(this.stockInfoDatabase);
            indicatorPanel.setStockInfoDatabase(this.stockInfoDatabase);
            log.info("saveStockCodeAndSymbolDatabase...");
            saveDatabase();
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
                        status = this.saveAsCSVFile(file, false);
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
                    status = this.portfolioManagementJPanel.saveAsCSVFile(fileEx, false);
                }
                else if (Utils.getFileExtension(fileEx.file).equals("xls"))
                {
                    status = this.portfolioManagementJPanel.saveAsExcelFile(fileEx.file, false);
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
        this.jMenu8.removeAll();
        final java.util.List<String> portfolioNames = org.yccheok.jstock.portfolio.Utils.getPortfolioNames();
        final String currentPortfolioName = this.getJStockOptions().getPortfolioName();
        final javax.swing.ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
        for (String portfolioName : portfolioNames) {
            final JMenuItem mi = (JRadioButtonMenuItem) jMenu8.add(new JRadioButtonMenuItem(portfolioName));
            buttonGroup.add(mi);
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
        final JMenuItem mi = new JMenuItem(GUIBundle.getString("MainFrame_MultiplePortolio..."), this.getImageIcon("/images/16x16/calc.png"));
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

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        Utils.launchWebBrowser(org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.HELP_HTML));
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenu9MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu9MenuSelected
        this.jMenu9.removeAll();        
        final java.util.List<String> watchlistNames = org.yccheok.jstock.watchlist.Utils.getWatchlistNames();
        final String currentWatchlistName = this.getJStockOptions().getWatchlistName();
        final javax.swing.ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
        for (String watchlistName : watchlistNames) {
            final JMenuItem mi = (JRadioButtonMenuItem)this.jMenu9.add(new JRadioButtonMenuItem(watchlistName));
            buttonGroup.add(mi);
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final String s = ((JRadioButtonMenuItem)e.getSource()).getText();
                    if (false == s.equals(currentWatchlistName)) {
                        MainFrame.this.selectActiveWatchlist(s);
                    }
                }

            });
            mi.setSelected(watchlistName.equals(currentWatchlistName));
        }

        this.jMenu9.addSeparator();
        final JMenuItem mi = new JMenuItem(GUIBundle.getString("MainFrame_MultipleWatchlist..."), this.getImageIcon("/images/16x16/stock_timezone.png"));
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multipleWatchlists();
            }

        });
        this.jMenu9.add(mi);
    }//GEN-LAST:event_jMenu9MenuSelected

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        try {
            Runtime.getRuntime().exec("calc");
        } catch (IOException ex) {
            log.error(null, ex);
            // External program not found. Use our own calculator.
            org.yccheok.jstock.gui.portfolio.Calc calc = new org.yccheok.jstock.gui.portfolio.Calc(this, false);
            calc.setVisible(true);
        }
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        if (false == org.yccheok.jstock.gui.Utils.hasSpecifiedLanguageFile(this.jStockOptions.getLocale())) {
            // User is currently using default langauge. English is our default
            // langauge. Hence, do nothing and return early. This is because we 
            // want to avoid from having the following locale.
            //
            // Locale(ENGLISH, FRANCE)
            //
            // This will yield incorrect behavior during currency formatting.
            // We prefer to have
            //
            // Locale(FRANCE, FRANCE)
            //
            // English language will be displayed still, as we do not have 
            // FRANCE language file yet.
            //
            return;
        }
        
        // Avoid from Confirm Dialog to pop up when user change to same language (i.e. english)
        if (false == this.jStockOptions.getLocale().getLanguage().equals(Locale.ENGLISH.getLanguage())) {
            // Do not suprise user with sudden restart. Ask for their permission to do so.
            final int result = JOptionPane.showConfirmDialog(this, MessagesBundle.getString("question_message_restart_now"), MessagesBundle.getString("question_title_restart_now"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                final Locale locale = new Locale(Locale.ENGLISH.getLanguage(), Locale.getDefault().getCountry(), Locale.getDefault().getVariant());
                this.jStockOptions.setLocale(locale);
                org.yccheok.jstock.gui.Utils.restartApplication(this);
            } // return to the previous selection if the user press "no" in the dialog
            else {
                if (Utils.isTraditionalChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem4.setSelected(true);
                } else if (Utils.isSimplifiedChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem2.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.GERMAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem3.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ITALIAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem5.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.FRENCH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem6.setSelected(true);
                }
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        // Avoid from Confirm Dialog to pop up when user change to same language (i.e. simplified chinese)
        if (false == Utils.isSimplifiedChinese(this.jStockOptions.getLocale())) {
            // Do not suprise user with sudden restart. Ask for their permission to do so.
            final int result = JOptionPane.showConfirmDialog(this, MessagesBundle.getString("question_message_restart_now"), MessagesBundle.getString("question_title_restart_now"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                String country = Locale.TRADITIONAL_CHINESE.getCountry().equals(Locale.getDefault().getCountry()) ? Locale.SIMPLIFIED_CHINESE.getCountry() : Locale.getDefault().getCountry();
                final Locale locale = new Locale(Locale.SIMPLIFIED_CHINESE.getLanguage(), country, Locale.getDefault().getVariant());
                this.jStockOptions.setLocale(locale);
                org.yccheok.jstock.gui.Utils.restartApplication(this);
            } // return to the previous selection if the user press "no" in the dialog
            else {
                if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ENGLISH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem1.setSelected(true);
                } else if (Utils.isTraditionalChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem4.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.GERMAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem3.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ITALIAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem5.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.FRENCH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem6.setSelected(true);
                }
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        // Avoid from Confirm Dialog to pop up when user change to same language (i.e. german)
        if (false == this.jStockOptions.getLocale().getLanguage().equals(Locale.GERMAN.getLanguage())) {
            // Do not suprise user with sudden restart. Ask for their permission to do so.
            final int result = JOptionPane.showConfirmDialog(this, MessagesBundle.getString("question_message_restart_now"), MessagesBundle.getString("question_title_restart_now"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                final Locale locale = new Locale(Locale.GERMAN.getLanguage(), Locale.getDefault().getCountry(), Locale.getDefault().getVariant());
                this.jStockOptions.setLocale(locale);
                org.yccheok.jstock.gui.Utils.restartApplication(this);
            }// return to the previous selection if the user press "no" in the dialog
            else {
                if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ENGLISH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem1.setSelected(true);
                } else if (Utils.isTraditionalChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem4.setSelected(true);
                } else if (Utils.isSimplifiedChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem2.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ITALIAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem5.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.FRENCH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem6.setSelected(true);
                }
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
        // Avoid from Confirm Dialog to pop up when user change to same language (i.e. german)
        if (false == Utils.isTraditionalChinese(this.jStockOptions.getLocale())) {
            // Do not suprise user with sudden restart. Ask for their permission to do so.
            final int result = JOptionPane.showConfirmDialog(this, MessagesBundle.getString("question_message_restart_now"), MessagesBundle.getString("question_title_restart_now"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                // Unline simplified chinese, we will not use Locale.getDefault().getCountry().
                // Instead, we will be using Locale.TRADITIONAL_CHINESE.getCountry().
                final Locale locale = new Locale(Locale.TRADITIONAL_CHINESE.getLanguage(), Locale.TRADITIONAL_CHINESE.getCountry(), Locale.getDefault().getVariant());
                this.jStockOptions.setLocale(locale);
                org.yccheok.jstock.gui.Utils.restartApplication(this);
            }// return to the previous selection if the user press "no" in the dialog
            else {
                if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ENGLISH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem1.setSelected(true);
                } else if (Utils.isSimplifiedChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem2.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.GERMAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem3.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ITALIAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem5.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.FRENCH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem6.setSelected(true);
                }
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        Utils.launchWebBrowser(org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.DONATE_HTML));
    }//GEN-LAST:event_jMenuItem13ActionPerformed
    
    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        Utils.launchWebBrowser(org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.CONTRIBUTE_HTML));
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jRadioButtonMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem5ActionPerformed
        // Avoid from Confirm Dialog to pop up when user change to same language (i.e. german)
        if (false == this.jStockOptions.getLocale().getLanguage().equals(Locale.ITALIAN.getLanguage())) {
            // Do not suprise user with sudden restart. Ask for their permission to do so.
            final int result = JOptionPane.showConfirmDialog(this, MessagesBundle.getString("question_message_restart_now"), MessagesBundle.getString("question_title_restart_now"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                final Locale locale = new Locale(Locale.ITALIAN.getLanguage(), Locale.getDefault().getCountry(), Locale.getDefault().getVariant());
                this.jStockOptions.setLocale(locale);
                org.yccheok.jstock.gui.Utils.restartApplication(this);
            }// return to the previous selection if the user press "no" in the dialog
            else {
                if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ENGLISH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem1.setSelected(true);
                } else if (Utils.isTraditionalChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem4.setSelected(true);
                } else if (Utils.isSimplifiedChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem2.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.GERMAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem3.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.FRENCH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem6.setSelected(true);
                }
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem5ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        refreshAllRealTimeStockMonitors();
        refreshCurrencyExchangeMonitor();
        // Refresh stock index as well.
        this.initMarketThread();
        
        // Only update UI when there is at least one stock.
        if (this.getStocks().isEmpty() == false) {
            this.setStatusBar(true, GUIBundle.getString("MainFrame_RefreshStockPrices..."));
            refreshPriceInProgress = true;
        }
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        Utils.launchWebBrowser(org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.HELP_KEYBOARD_SHORTCUTS_HTML));
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        Utils.launchWebBrowser(org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.ANDROID_HTML));
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jRadioButtonMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem6ActionPerformed
        // Avoid from Confirm Dialog to pop up when user change to same language (i.e. german)
        if (false == this.jStockOptions.getLocale().getLanguage().equals(Locale.FRENCH.getLanguage())) {
            // Do not suprise user with sudden restart. Ask for their permission to do so.
            final int result = JOptionPane.showConfirmDialog(this, MessagesBundle.getString("question_message_restart_now"), MessagesBundle.getString("question_title_restart_now"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                final Locale locale = new Locale(Locale.FRENCH.getLanguage(), Locale.getDefault().getCountry(), Locale.getDefault().getVariant());
                this.jStockOptions.setLocale(locale);
                org.yccheok.jstock.gui.Utils.restartApplication(this);
            }// return to the previous selection if the user press "no" in the dialog
            else {
                if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ENGLISH.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem1.setSelected(true);
                } else if (Utils.isTraditionalChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem4.setSelected(true);
                } else if (Utils.isSimplifiedChinese(this.jStockOptions.getLocale())) {
                    this.jRadioButtonMenuItem2.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.ITALIAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem5.setSelected(true);
                } else if (this.jStockOptions.getLocale().getLanguage().compareTo(Locale.GERMAN.getLanguage()) == 0) {
                    this.jRadioButtonMenuItem3.setSelected(true);
                }
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem6ActionPerformed
    
    /**
     * Activate specified watchlist.
     *
     * @param watchlist Watchlist name
     */
    public void selectActiveWatchlist(String watchlist) {
        assert(SwingUtilities.isEventDispatchThread());
        // Save current watchlist.
        MainFrame.this.saveWatchlist();
        // Save current GUI options.
        // Do not call MainFrame.this.saveGUIOptions() (Pay note on the underscore)
        // , as that will save portfolio's and indicator scanner's as well.
        MainFrame.this._saveGUIOptions();
        // And switch to new portfolio.
        MainFrame.this.getJStockOptions().setWatchlistName(watchlist);
        MainFrame.this.initWatchlist();
        // I guess user wants to watch the current active watchlist right now.
        // We will help him to turn to the stock watchlist page.
        MainFrame.this.jTabbedPane1.setSelectedIndex(0);

        // No matter how, just stop progress bar, and display best message.
        this.setStatusBar(false, this.getBestStatusBarMessage());
    }

    /**
     * Activate specified portfolio.
     *
     * @param portfolio Portfolio name
     */
    public void selectActivePortfolio(String portfolio) {
        assert(SwingUtilities.isEventDispatchThread());
        // Save current portfolio.
        MainFrame.this.portfolioManagementJPanel.savePortfolio();
        // Save current GUI options.
        MainFrame.this.portfolioManagementJPanel.saveGUIOptions();
        // And switch to new portfolio.
        MainFrame.this.getJStockOptions().setPortfolioName(portfolio);
        MainFrame.this.portfolioManagementJPanel.initPortfolio();
        // I guess user wants to watch the current active portfolio right now.
        // We will help him to turn to the portfolio page.
        MainFrame.this.jTabbedPane1.setSelectedIndex(3);
        
        MainFrame.this.portfolioManagementJPanel.updateTitledBorder();

        // No matter how, just stop progress bar, and display best message.
        this.setStatusBar(false, this.getBestStatusBarMessage());
    }

    private void multipleWatchlists() {
        WatchlistJDialog watchlistJDialog = new WatchlistJDialog(this, true);
        watchlistJDialog.setLocationRelativeTo(this);
        watchlistJDialog.setVisible(true);
    }

    private void multiplePortfolios() {
        PortfolioJDialog portfolioJDialog = new PortfolioJDialog(this, true);
        portfolioJDialog.setLocationRelativeTo(this);
        portfolioJDialog.setVisible(true);
    }
    
    private static boolean saveAsCSVFile(CSVWatchlist csvWatchlist, File file, boolean languageIndependent) {
        final org.yccheok.jstock.file.Statements statements = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(csvWatchlist.tableModel, languageIndependent);
        assert(statements != null);
        return statements.saveAsCSVFile(file);
    }    
    
    private boolean saveAsCSVFile(File file, boolean languageIndependent) {
        final TableModel tableModel = jTable1.getModel();
        CSVWatchlist csvWatchlist = CSVWatchlist.newInstance(tableModel);
        return saveAsCSVFile(csvWatchlist, file, languageIndependent);
    }

    private boolean saveAsExcelFile(File file) {
        final TableModel tableModel = jTable1.getModel();
        final org.yccheok.jstock.file.Statements statements = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(tableModel, false);
        assert(statements != null);
        return statements.saveAsExcelFile(file, GUIBundle.getString("MainFrame_Title"));
    }

    /**
     * Returns the best JStockOptions, based on command line argument.
     */
    private static JStockOptions getJStockOptions(String args[]) {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "options.xml");
        JStockOptions jStockOptions = Utils.fromXML(JStockOptions.class, f);
        if (jStockOptions == null) {
            // JStockOptions's file not found. Perhaps this is the first time we
            // run JStock.
            jStockOptions = new JStockOptions();
        }
            
        for (String arg : args) {
            final String[] tokens = arg.split("=");
            if (tokens.length != 2) {
                continue;
            }
            final String compare = tokens[0].trim();
            if (compare.equalsIgnoreCase("-country")) {
                final String countryStr = tokens[1].trim();
                try {
                    final Country country = Country.valueOf(countryStr);
                    if (country == Country.China) {
                        // Has user switch to this country before?
                        if (false == Utils.isFileOrDirectoryExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country)) {
                            // First time switching to this country. Use the settings from
                            // command line argument.
                            jStockOptions.setCountry(country);
                            final Locale locale = new Locale(Locale.SIMPLIFIED_CHINESE.getLanguage(), Locale.getDefault().getCountry(), Locale.getDefault().getVariant());
                            jStockOptions.setLocale(locale);
                        }
                    }
                } catch(IllegalArgumentException ex) {
                    log.error(null, ex);
                }
            }
        }

        return jStockOptions;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {        
        if (false == AppLock.lock()) {
            final int choice = JOptionPane.showOptionDialog(null, 
                    MessagesBundle.getString("warning_message_running_2_jstock"),
                    MessagesBundle.getString("warning_title_running_2_jstock"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[] {MessagesBundle.getString("yes_button_running_2_jstock"), MessagesBundle.getString("no_button_running_2_jstock")}, 
                    MessagesBundle.getString("no_button_running_2_jstock"));
            if (choice != JOptionPane.YES_OPTION) {
                System.exit(0);
                return;
            }
        }        

        // As ProxyDetector is affected by system properties
        // http.proxyHost, we are forced to initialized ProxyDetector right here,
        // before we manually change the system properties according to
        // JStockOptions.
        ProxyDetector.getInstance();      

        Utils.setDefaultLookAndFeel();

        final String[] _args = args;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame mainFrame = MainFrame.getInstance();
                final JStockOptions jStockOptions = getJStockOptions(_args);
                // This global effect, should just come before anything else, 
                // after we get an instance of JStockOptions.
                Locale.setDefault(jStockOptions.getLocale());
                
                // We need to first assign jStockOptions to mainFrame, as during
                // Utils.migrateXMLToCSVPortfolios, we will be accessing mainFrame's
                // jStockOptions.
                mainFrame.initJStockOptions(jStockOptions);
                
                mainFrame.init();
                mainFrame.setVisible(true);
                mainFrame.updateDividerLocation();
                mainFrame.requestFocusOnJComboBox();
            }
        });
    }

    // Restore the last saved divider location for portfolio management panel.
    private void updateDividerLocation() {
        this.portfolioManagementJPanel.updateDividerLocation();
    }
    
    private void clearAllStocks() {
        if (stockCodeHistoryGUI != null) {
            stockCodeHistoryGUI.clear();
        }
        if (realTimeStockMonitor != null) {
            realTimeStockMonitor.clearStockCodes();
        }
        if (stockHistoryMonitor != null) {
            stockHistoryMonitor.clearStockCodes();
        }
        final StockTableModel tableModel = (StockTableModel)jTable1.getModel();                                 
        this.initAlertStateManager();

        if (java.awt.EventQueue.isDispatchThread()) {
            tableModel.clearAllStocks();
            updateDynamicChart(null);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tableModel.clearAllStocks();
                    updateDynamicChart(null);
                }                
            });
        }

        if (stockCodeHistoryGUI != null) {
            if (stockCodeHistoryGUI.isEmpty()) {
                if (this.stockInfoDatabase != null) {
                    this.setStatusBar(false, this.getBestStatusBarMessage());
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
            stockCodeHistoryGUI.remove(stock.code);
            realTimeStockMonitor.removeStockCode(stock.code);
            stockHistoryMonitor.removeStockCode(stock.code);
            tableModel.removeRow(modelIndex);
            this.alertStateManager.clearState(stock);
        }            
        
        this.updateDynamicChart(null);

        if (stockCodeHistoryGUI.isEmpty()) {
            if (this.stockInfoDatabase != null) {
                this.setStatusBar(false, this.getBestStatusBarMessage());
            }
        }
    }

    /**
     * Set the exchange rate value on status bar.
     *
     * @param exchangeRate the exchange rate value. null to reset
     */
    public void setStatusBarExchangeRate(final Double exchangeRate) {
        if (SwingUtilities.isEventDispatchThread()) {
            statusBar.setExchangeRate(exchangeRate);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    statusBar.setExchangeRate(exchangeRate);
                }
            });
        }
    }

    /**
     * Set the visibility of exchange rate label on status bar.
     *
     * @param visible true to make the exchange rate label visible. Else false
     */
    public void setStatusBarExchangeRateVisible(final boolean visible) {
        if (SwingUtilities.isEventDispatchThread()) {
            statusBar.setExchangeRateVisible(visible);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    statusBar.setExchangeRateVisible(visible);
                }
            });
        }
    }

    /**
     * Set the tool tip text of exchange rate label on status bar.
     *
     * @param text the tool tip text
     */
    public void setStatusBarExchangeRateToolTipText(final String text) {
        if (SwingUtilities.isEventDispatchThread()) {
            statusBar.setExchangeRateToolTipText(text);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    statusBar.setExchangeRateToolTipText(text);
                }
            });
        }
    }

    /**
     * Update the status bar.
     *
     * @param progressBar true to make progress bar busy. Else false
     * @param mainMessage message on the left
     */
    public void setStatusBar(final boolean progressBar, final String mainMessage) {
        if (SwingUtilities.isEventDispatchThread())
        {
            isStatusBarBusy = progressBar;
            statusBar.setProgressBar(progressBar);
            statusBar.setMainMessage(mainMessage);
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    isStatusBarBusy = progressBar;
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
        if (lafClassName == null) {
            return;
        }
        try {
            UIManager.setLookAndFeel(lafClassName);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (java.lang.ClassNotFoundException exp) {
            log.error(null, exp);
        }
        catch (java.lang.InstantiationException exp) {
            log.error(null, exp);
        }
        catch (java.lang.IllegalAccessException exp) {
            log.error(null, exp);
        }
        catch (javax.swing.UnsupportedLookAndFeelException exp) {
            log.error(null, exp);
        }
        
        this.jStockOptions.setLookNFeel(lafClassName);
        
        for (Enumeration<AbstractButton> e = this.buttonGroup1.getElements() ; e.hasMoreElements() ;) {
            AbstractButton button = e.nextElement();
            javax.swing.JRadioButtonMenuItem m = (javax.swing.JRadioButtonMenuItem)button;
            ChangeLookAndFeelAction a = (ChangeLookAndFeelAction)m.getActionListeners()[0];
                        
            if (a.getLafClassName().equals(lafClassName)) {
                m.setSelected(true);
                break;                   
            }
        }
        
        // Sequence are important. The AutoCompleteJComboBox itself should have the highest
        // priority.
        ((AutoCompleteJComboBox)jComboBox1).setStockInfoDatabase(this.stockInfoDatabase);
        this.indicatorPanel.setStockInfoDatabase(this.stockInfoDatabase);
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
        jTabbedPane1.addTab(GUIBundle.getString("IndicatorPanel_Title"), indicatorPanel);
    }

    private void createIndicatorScannerJPanel() {
        this.indicatorScannerJPanel = new IndicatorScannerJPanel();                
        jTabbedPane1.addTab(GUIBundle.getString("IndicatorScannerJPanel_Title"), indicatorScannerJPanel);
        jTabbedPane1.addChangeListener(indicatorScannerJPanel);
    }
    
    // Due to the unknown problem in netbeans IDE, we will add in the tooltip
    // and icon seperately.
    private void createIconsAndToolTipTextForJTabbedPane() {
        this.jTabbedPane1.setIconAt(0, this.getImageIcon("/images/16x16/stock_timezone.png"));
        this.jTabbedPane1.setIconAt(1, this.getImageIcon("/images/16x16/color_line.png"));
        this.jTabbedPane1.setIconAt(2, this.getImageIcon("/images/16x16/find.png"));
        this.jTabbedPane1.setIconAt(3, this.getImageIcon("/images/16x16/calc.png"));
        this.jTabbedPane1.setToolTipTextAt(0, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_WatchYourFavoriteStockMovement"));
        this.jTabbedPane1.setToolTipTextAt(1, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_CustomizeYourOwnStockIndicatorForAlertPurpose"));
        this.jTabbedPane1.setToolTipTextAt(2, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_ScanThroughTheEntireStockMarketSoThatYouWillBeInformedWhatToSellOrBuy"));
        this.jTabbedPane1.setToolTipTextAt(3, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_ManageYourRealTimePortfolioWhichEnableYouToTrackBuyAndSellRecords"));
    }
      
    public void createCountryMenuItem() {
        java.util.List<Country> countries = new ArrayList<Country>(Arrays.asList(Country.values()));
        // Czech is only for currency exchange purpose.
        countries.remove(Country.Czech);

        for (final Country country : countries) {
            // Ugly fix on spelling mistake.
            final JMenuItem mi;
            mi = (JRadioButtonMenuItem) jMenu6.add(new JRadioButtonMenuItem(country.toHumanReadableString(), country.getIcon()));

            buttonGroup2.add(mi);
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainFrame.this.changeCountry(country);
                }                
            });
            
            if (jStockOptions.getCountry() == country) {
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
            
            if (currentlaf != null) {
                if (lafInfo[i].getClassName().equals(currentlaf.getClass().getName()))
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
        if (Utils.isWindows7() || Utils.isWindows8()) {
            return new javax.swing.ImageIcon(getClass().getResource("/images/128x128/chart.png")).getImage();
        }
        return new javax.swing.ImageIcon(getClass().getResource("/images/16x16/chart.png")).getImage();
    }
    
    private void createSystemTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            final Image image;
            if (Utils.isWindows7() || Utils.isWindows8()) {
                image = new javax.swing.ImageIcon(getClass().getResource("/images/128x128/chart.png")).getImage();
            } else {
                image = new javax.swing.ImageIcon(getClass().getResource("/images/16x16/chart.png")).getImage();
            }

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
            MenuItem defaultItem = new MenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_Exit"));
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);

            trayIcon = new TrayIcon(image, GUIBundle.getString("MainFrame_Application_Title"), popup);

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
                JOptionPane.showMessageDialog(MainFrame.this, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("warning_message_trayicon_could_not_be_added"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("warning_title_trayicon_could_not_be_added"), JOptionPane.WARNING_MESSAGE);
            }

        } else {
            //  System Tray is not supported
            trayIcon = null;
            JOptionPane.showMessageDialog(MainFrame.this, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("warning_message_system_tray_is_not_supported"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("warning_title_system_tray_is_not_supported"), JOptionPane.WARNING_MESSAGE);
        }        
    }
    
    private void initTableHeaderToolTips() {
        JTableHeader header = jTable1.getTableHeader();
    
        ColumnHeaderToolTips tips = new ColumnHeaderToolTips();

        tips.setToolTip(jTable1.getColumn(GUIBundle.getString("MainFrame_FallBelow")), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_AlertUserWhenLastPriceFallBelowOrEqualToSpecifiedValue"));
        tips.setToolTip(jTable1.getColumn(GUIBundle.getString("MainFrame_RiseAbove")), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_AlertUserWhenLastPriceFallAboveOrEqualToSpecifiedValue"));

        header.addMouseMotionListener(tips);        
    }
    
    /* Save everything to disc, before perform uploading. */
    public void commitBeforeSaveToCloud() {
        // Previously, we will store the entire stockcodeandsymboldatabase.xml
        // to cloud server if stockcodeandsymboldatabase.xml is containing
        // user defined database. Due to our server is running out of space, we will
        // only store UserDefined pair. user-defined-database.xml will be only
        // used for cloud storage purpose.
        //
        // The following code is trying to extract user defined database out from
        // StockCodeAndSymbolDatabase. The code shall be removed after a while, as 
        // this operation will be done in saveDatabase method. It is here merely 
        // for backward compatible purpose.
        //
        // We should instead iterate to every Country to save accordingly. However,
        // it is too time consuming. We will make assumption that user is most
        // interested in his current viewing country. Hence, for existing user,
        // he will be having risk of losing other countries UserDefined codes.
        final Country country = jStockOptions.getCountry();
        if (false == Utils.isFileOrDirectoryExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "user-defined-database.xml")) {
            final StockInfoDatabase stock_info_database = this.stockInfoDatabase;
            if (stock_info_database != null) {
                saveUserDefinedDatabaseAsCSV(country, stock_info_database);
            }
        }

        /* These codes are very similar to clean up code during application
         * exit.
         */
                
        jStockOptions.setApplicationVersionID(Utils.getApplicationVersionID());        

        this.saveJStockOptions();
        
        this.saveGUIOptions();
        this.saveChartJDialogOptions();
        this.saveBrokingFirmLogos();
        this.saveWatchlist();
        this.indicatorPanel.saveAlertIndicatorProjectManager();
        this.indicatorPanel.saveModuleIndicatorProjectManager();
        this.portfolioManagementJPanel.savePortfolio();
        
        // In Linux, "My Portfolio" and "my portfolio" are 2 different folders.
        // However, we cannot commit such folders to cloud. This will cause
        // problem in Windows. This code was introduced since 1.0.7c. We should
        // remove it after a while, as we do have 2557 changeset to prevent such
        // incident in Linux.
        solveCaseSensitiveFoldersIssue();
        
        saveWatchlistAndPortfolioInfos();
    }

    private void solveCaseSensitiveFoldersIssue() {
        final Country currentCountry = this.jStockOptions.getCountry();
        final String currentWatchlist = this.jStockOptions.getWatchlistName();
        final String currentPortfolio = this.jStockOptions.getPortfolioName();
        
        ////////////////////////////////////////////////////////////////////////
        // WATCHLIST
        ////////////////////////////////////////////////////////////////////////
        for (Country country : Country.values()) {
            java.util.List<String> watchlistNames = org.yccheok.jstock.watchlist.Utils.getWatchlistNames(country);
            Map<String, java.util.List<String>> watchlistNamesMap = new HashMap<String, java.util.List<String>>();
            java.util.List<java.util.List<String>> duplicatedNames = new ArrayList<java.util.List<String>>();
            Set<String> lowerCaseNames = new HashSet<String>();
            
            for (String watchlistName : watchlistNames) {
                String lowerCaseWatchlistName = watchlistName.toLowerCase();
                lowerCaseNames.add(lowerCaseWatchlistName);
                
                java.util.List<String> names = watchlistNamesMap.get(lowerCaseWatchlistName);
                if (names == null) {
                    names = new ArrayList<String>();
                    watchlistNamesMap.put(lowerCaseWatchlistName, names);
                }
                
                names.add(watchlistName);
                if (names.size() > 1) {
                    duplicatedNames.add(names);
                }
            }
            
            for (java.util.List<String> names : duplicatedNames) {
                int counter = 0;
                boolean originalNameUsed = false;
                for (int i = 0, ei = names.size(); i < ei; i++) {
                    final String originalName = names.get(i);
                    if (currentCountry == country && currentWatchlist.equals(originalName)) {
                        originalNameUsed = true;
                        continue;
                    }
                    
                    String newName = originalName;
                    if (originalNameUsed || i < (ei - 1)) {
                        // Cannot use the original name.
                        newName = originalName + counter++;
                        while (lowerCaseNames.contains(newName.toLowerCase())) {
                            newName = originalName + counter++;    
                        }
                        lowerCaseNames.add(newName.toLowerCase());
                    } else {
                        // Use original name.
                        originalNameUsed = true;
                    }
                    
                    String originalDirectory = org.yccheok.jstock.watchlist.Utils.getWatchlistDirectory(country, originalName);
                    String newDirectory = org.yccheok.jstock.watchlist.Utils.getWatchlistDirectory(country, newName);
                    
                    if (false == originalDirectory.equalsIgnoreCase(newDirectory)) {
                        new File(originalDirectory).renameTo(new File(newDirectory));
                    }
                }
            }
        }
        
        ////////////////////////////////////////////////////////////////////////
        // PORTFOLIO
        ////////////////////////////////////////////////////////////////////////        
        for (Country country : Country.values()) {
            java.util.List<String> portfolioNames = org.yccheok.jstock.portfolio.Utils.getPortfolioNames(country);
            Map<String, java.util.List<String>> portfolioNamesMap = new HashMap<String, java.util.List<String>>();
            java.util.List<java.util.List<String>> duplicatedNames = new ArrayList<java.util.List<String>>();
            Set<String> lowerCaseNames = new HashSet<String>();

            for (String portfolioName : portfolioNames) {
                String lowerCasePortfolioName = portfolioName.toLowerCase();
                lowerCaseNames.add(lowerCasePortfolioName);

                java.util.List<String> names = portfolioNamesMap.get(lowerCasePortfolioName);
                if (names == null) {
                    names = new ArrayList<String>();
                    portfolioNamesMap.put(lowerCasePortfolioName, names);
                }

                names.add(portfolioName);
                if (names.size() > 1) {
                    duplicatedNames.add(names);
                }
            }

            for (java.util.List<String> names : duplicatedNames) {
                int counter = 0;
                boolean originalNameUsed = false;
                for (int i = 0, ei = names.size(); i < ei; i++) {
                    final String originalName = names.get(i);
                    if (currentCountry == country && currentPortfolio.equals(originalName)) {
                        originalNameUsed = true;
                        continue;
                    }

                    String newName = originalName;
                    if (originalNameUsed || i < (ei - 1)) {
                        // Cannot use the original name.
                        newName = originalName + counter++;
                        while (lowerCaseNames.contains(newName.toLowerCase())) {
                            newName = originalName + counter++;    
                        }
                        lowerCaseNames.add(newName.toLowerCase());
                    } else {
                        // Use original name.
                        originalNameUsed = true;
                    }

                    String originalDirectory = org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory(country, originalName);
                    String newDirectory = org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory(country, newName);

                    if (false == originalDirectory.equalsIgnoreCase(newDirectory)) {
                        new File(originalDirectory).renameTo(new File(newDirectory));
                    }
                }
            }
        }        
    }
    
    // Only call this function after you had saved all the watchlists and
    // portfolios.
    private boolean saveWatchlistAndPortfolioInfos() {
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "android") == false)
        {
            return false;
        }

        java.util.List<WatchlistInfo> watchlistInfos = org.yccheok.jstock.watchlist.Utils.getWatchlistInfos();
        java.util.List<PortfolioInfo> portfolioInfos = org.yccheok.jstock.portfolio.Utils.getPortfolioInfos();
        File watchlistInfosFile = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "android" + File.separator + "watchlistinfos.csv");
        File portfolioInfosFile = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "android" + File.separator + "portfolioinfos.csv");
        boolean result0 = Statements.newInstanceFromWatchlistInfos(watchlistInfos).saveAsCSVFile(watchlistInfosFile);
        boolean result1 = Statements.newInstanceFromPortfolioInfos(portfolioInfos).saveAsCSVFile(portfolioInfosFile);
        return result0 && result1;
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

        // Need to read user-defined-database.xml.
        // The user-defined-database.xml is extracted from cloud
        // freshly.
        this.initDatabase(true);
        this.initAjaxProvider();
        this.initMarketThread();
        this.initMarketJPanel();
        this.initStockHistoryMonitor();
        this.initOthersStockHistoryMonitor();
        this.initCurrencyExchangeMonitor();
        // Initialize real time monitor must come before initialize real time
        // stocks. We need to submit real time stocks to real time stock monitor.
        // Hence, after we load real time stocks from file, real time stock monitor
        // must be ready (initialized).
        this.initRealTimeStockMonitor();
        this.initWatchlist();
        this.initAlertStateManager();
        this.initDynamicCharts();
        this.initDynamicChartVisibility();

        for (Enumeration<AbstractButton> e = this.buttonGroup2.getElements() ; e.hasMoreElements() ;) {
            AbstractButton button = e.nextElement();
            javax.swing.JRadioButtonMenuItem m = (javax.swing.JRadioButtonMenuItem)button;
            
            if(m.getText().equals(jStockOptions.getCountry().toHumanReadableString())) {
                m.setSelected(true);
                break;
            }
        }

        if (null != this.indicatorPanel) {
            this.indicatorPanel.initIndicatorProjectManager();
            this.indicatorPanel.initModuleProjectManager();
        }

        // I will try to reload the GUI settings for Stock Watchlist and Stock
        // Indicator Scanner. I hope that the sudden change in GUI will not give
        // user a shock.
        this.initGUIOptions();
        this.indicatorScannerJPanel.initGUIOptions();
    }

    private void changeCountry(Country country) {
        if (country == null) {
            return;
        }

        if (jStockOptions.getCountry() == country) {
            return;
        }

        if (needToSaveUserDefinedDatabase) {
            // We are having updated user database in memory.
            // Save it to disk.
            this.saveUserDefinedDatabaseAsCSV(country, stockInfoDatabase);
        }

        /* Save the GUI look. */
        saveGUIOptions();

        /* Need to save chart dialog options? */
        
        saveWatchlist();
        this.portfolioManagementJPanel.savePortfolio();

        // Spain no longer supported. Sad...
        if (country == Country.Spain) {
            JOptionPane.showMessageDialog(
                null, 
                MessagesBundle.getString("info_message_spain_not_supported"), 
                MessagesBundle.getString("info_title_spain_not_supported"), 
                JOptionPane.INFORMATION_MESSAGE
            );
        }        
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

        this.initDatabase(true);
        this.initAjaxProvider();
        this.initMarketThread();
        this.initMarketJPanel();
        this.initStockHistoryMonitor();
        this.initOthersStockHistoryMonitor();
        this.initCurrencyExchangeMonitor();
        // Initialize real time monitor must come before initialize real time
        // stocks. We need to submit real time stocks to real time stock monitor.
        // Hence, after we load real time stocks from file, real time stock monitor
        // must be ready (initialized).
        this.initRealTimeStockMonitor();
        this.initWatchlist();
        this.initAlertStateManager();
        this.initDynamicCharts();
        // this.initDynamicChartVisibility();

        for (Enumeration<AbstractButton> e = this.buttonGroup2.getElements() ; e.hasMoreElements() ;) {
            AbstractButton button = e.nextElement();
            javax.swing.JRadioButtonMenuItem m = (javax.swing.JRadioButtonMenuItem)button;
             
            // Ugly fix on spelling mistake.    
            if (country == Country.UnitedState && m.getText().equals(country.toString() + "s")) {
                m.setSelected(true);
                break;                
            }
            
            if (m.getText().equals(country.toString())) {
                m.setSelected(true);
                break;                   
            }
        }        
    }

    private MouseAdapter getMyJXStatusBarExchangeRateLabelMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Popup dialog to select currency exchange option.
                    OptionsJDialog optionsJDialog = new OptionsJDialog(MainFrame.this, true);
                    optionsJDialog.setLocationRelativeTo(MainFrame.this);
                    optionsJDialog.set(jStockOptions);
                    optionsJDialog.select(GUIBundle.getString("OptionsJPanel_Wealth"));
                    optionsJDialog.setVisible(true);
                }
            }
        };
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
                if (e.getClickCount() == 2) {
                    
                    // Make sure no other task is running.
                    // Use local variable to be thread safe.
                    final DatabaseTask task = MainFrame.this.databaseTask;
                    if (task != null) {
                        if (task.isDone() == true) {
                            // Task is done. But, does it success?
                            boolean success = false;
                            // Some developers suggest that check for isCancelled before calling get
                            // to avoid CancellationException. Others suggest that just perform catch
                            // on all Exceptions. I will do it both.
                            if (task.isCancelled() == false) {
                                try {
                                    success = task.get();
                                } catch (InterruptedException ex) {
                                    log.error(null, ex);
                                } catch (ExecutionException ex) {
                                    log.error(null, ex);
                                } catch (CancellationException ex) {
                                    log.error(null, ex);
                                }
                            }
                            if (success == false) {
                                // Fail. Automatically reload database for user. Need not to prompt them message.
                                // As, they do not have any database right now.
                                MainFrame.this.initDatabase(true);
                                
                            } else {
                                final int result = JOptionPane.showConfirmDialog(MainFrame.this, MessagesBundle.getString("question_message_perform_server_reconnecting"), MessagesBundle.getString("question_title_perform_server_reconnecting"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if (result == JOptionPane.YES_OPTION) {
                                    MainFrame.this.initDatabase(false);
                                }
                            }
                        }
                        else {
                            // There is task still running. Ask user whether he wants
                            // to stop it.
                            final int result = JOptionPane.showConfirmDialog(MainFrame.this, MessagesBundle.getString("question_message_cancel_server_reconnecting"), MessagesBundle.getString("question_title_cancel_server_reconnecting"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            
                            if (result == JOptionPane.YES_OPTION)
                            {                            
                                synchronized (MainFrame.this.databaseTaskMonitor)
                                {
                                    MainFrame.this.databaseTask.cancel(true);
                                    MainFrame.this.databaseTask = null;
                                }
                                
                                setStatusBar(false, GUIBundle.getString("MainFrame_NetworkError"));
                                statusBar.setImageIcon(getImageIcon("/images/16x16/network-error.png"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_DoubleClickedToTryAgain"));
                            }
                        }
                    }
                    else {
                        // User cancels databaseTask explicitly. (Cancel while
                        // JStock is fetching database from server). Let's read
                        // from disk.
                        initDatabase(true);
                    }
                            
                }
            }
        };
    }

    public StockInfoDatabase getStockInfoDatabase() {
        return this.stockInfoDatabase;
    }

    public StockNameDatabase getStockNameDatabase() {
        return stockNameDatabase;
    }
    
    public java.util.List<StockServerFactory> getStockServerFactories() {
        return Factories.INSTANCE.getStockServerFactories(this.jStockOptions.getCountry());
    }
    
    public java.util.List<Stock> getStocks() {
        final StockTableModel tableModel = (StockTableModel)jTable1.getModel();
        return tableModel.getStocks();
    }
    
    // Should we synchronized the jTable1, or post the job to GUI event dispatch
    // queue?
    public void addStockToTable(final Stock stock, final StockAlert alert) {
        final JTable _jTable1 = this.jTable1;
        if (java.awt.EventQueue.isDispatchThread()) {
            final StockTableModel tableModel = (StockTableModel)_jTable1.getModel();
            tableModel.addStock(stock, alert);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final StockTableModel tableModel = (StockTableModel)_jTable1.getModel();
                    tableModel.addStock(stock, alert);                    
                }
            });
        }
    }

    public void addStockToTable(final Stock stock) {
        final JTable _jTable1 = this.jTable1;
        if (java.awt.EventQueue.isDispatchThread()) {
            final StockTableModel tableModel = (StockTableModel)_jTable1.getModel();
            tableModel.addStock(stock);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final StockTableModel tableModel = (StockTableModel)_jTable1.getModel();
                    tableModel.addStock(stock);                    
                }
            });            
        }
    }

    // Only will return true if the selected stock is the one and only one.
    private boolean isStockBeingSelected(final Stock stock) {
        int[] rows = MainFrame.this.jTable1.getSelectedRows();
            
        if (rows.length == 1) {
            final int row = rows[0];
            final StockTableModel tableModel = (StockTableModel)jTable1.getModel();
            final int modelIndex = jTable1.convertRowIndexToModel(row);
            if (stock.code.equals(tableModel.getStock(modelIndex).code)) {
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
                        final String template = GUIBundle.getString("MainFrame_FallBelow_template");
                        message = MessageFormat.format(template, stock.symbol, lastPrice, price);
                    }
                    else
                    {
                        final String template = GUIBundle.getString("MainFrame_RiseAbove_template");
                        message = MessageFormat.format(template, stock.symbol, lastPrice, price);
                    }

                    if (jStockOptions.isPopupMessage()) {
                        displayPopupMessage(stock.symbol.toString(), message);

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
                        final String template = GUIBundle.getString("MainFrame_FallBelow_template");
                        title = MessageFormat.format(template, stock.symbol, lastPrice, price);
                    }
                    else
                    {
                        final String template = GUIBundle.getString("MainFrame_RiseAbove_template");
                        title = MessageFormat.format(template, stock.symbol, lastPrice, price);
                    }

                    final String message = title + "\n(JStock)";

                    try {
                        String email = Utils.decrypt(jStockOptions.getEmail());
                        final String CCEmail = Utils.decrypt(jStockOptions.getCCEmail());
                        GoogleMail.Send(email, Utils.decrypt(jStockOptions.getEmailPassword()), email + "@gmail.com", CCEmail, title, message);
                    } catch (AddressException exp) {
                        log.error(null, exp);
                    } catch (MessagingException exp) {
                        log.error(null, exp);
                    }
                }
            };

            try {
                emailAlertPool.submit(r);
            }
            catch (java.util.concurrent.RejectedExecutionException exp) {
                log.error(null, exp);
            }
        }   /* if(jStockOptions.isSendEmail()) */

        if (this.jStockOptions.isSMSEnabled()) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    if (((OperatorIndicator)indicator).getName().equalsIgnoreCase("fallbelow"))
                    {
                        final String template = GUIBundle.getString("MainFrame_FallBelow_template");
                        message = MessageFormat.format(template, stock.symbol, lastPrice, price);
                    }
                    else
                    {
                        final String template = GUIBundle.getString("MainFrame_RiseAbove_template");
                        message = MessageFormat.format(template, stock.symbol, lastPrice, price);
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

    /**
     * Highlight stock at row <code>modelRow</code>, by making it selected and
     * visible.
     * 
     * @param modelRow row respected to stock model
     */
    private void highlightStock(int modelRow) {
        if (modelRow < 0) {
            return;
        }
        final int row = this.jTable1.convertRowIndexToView(modelRow);
        // Make it selected.
        this.jTable1.getSelectionModel().setSelectionInterval(row, row);
        // and visible.
        JTableUtilities.scrollToVisible(this.jTable1, row, 0);
    }

    /**
     * Add user defined stock info.
     *
     * @param stockInfo the stock info
     * @return true if success
     */
    public boolean addUserDefinedStockInfo(StockInfo stockInfo) {
        StockInfoDatabase stock_info_database = this.stockInfoDatabase;
        if (stock_info_database == null) {
            return false;
        }
        boolean result = stock_info_database.addUserDefinedStockInfo(stockInfo);
        if (result) {
            needToSaveUserDefinedDatabase = true;
        }
        return result;
    }

    private org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, MatchType> getMatchObserver() {
        return new org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, MatchType>() {

            @Override
            public void update(AutoCompleteJComboBox subject, MatchType matchType) {
                assert(matchType != null);
                Code code = matchType.getCode();
                final Symbol symbol = Symbol.newInstance(matchType.n);
                final StockInfo stockInfo = StockInfo.newInstance(code, symbol);

                addStockInfoFromAutoCompleteJComboBox(stockInfo);

                // Remember to update our offline database as well.
                addUserDefinedStockInfo(stockInfo);
            }
        };
    }
    
    private org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, ResultType> getResultObserver() {
        return new org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, ResultType>() {

            @Override
            public void update(AutoCompleteJComboBox subject, ResultType resultType) {
                assert(resultType != null);
                // Symbol from Yahoo means Code in JStock.
                final Code code = Code.newInstance(resultType.symbol);
                // Name from Yahoo means Symbol in JStock.
                final Symbol symbol = Symbol.newInstance(resultType.name);
                final StockInfo stockInfo = StockInfo.newInstance(code, symbol);

                addStockInfoFromAutoCompleteJComboBox(stockInfo);

                // Remember to update our offline database as well.
                addUserDefinedStockInfo(stockInfo);
            }
        };
    }

    private org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, StockInfo> getStockInfoObserver() {
        return new org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, StockInfo>() {
            @Override
            public void update(AutoCompleteJComboBox subject, StockInfo stockInfo) {
                assert(stockInfo != null);
                addStockInfoFromAutoCompleteJComboBox(stockInfo);
            }   // public void update(AutoCompleteJComboBox subject, StockInfo stockInfo)
        };
    }

    // Shared code for getStockInfoObserver and getResultObserver.
    private void addStockInfoFromAutoCompleteJComboBox(StockInfo stockInfo) {
        // When user try to enter a stock, and the stock is already in
        // the table, the stock shall be highlighted. Stock will be
        // selected and the table shall be scrolled to be visible.
        final StockTableModel tableModel = (StockTableModel)MainFrame.this.jTable1.getModel();

        final Stock emptyStock = Utils.getEmptyStock(stockInfo);
        
        // First add the empty stock, so that the user will not have wrong perspective that
        // our system is slow.
        addStockToTable(emptyStock);
        int row = tableModel.findRow(emptyStock);
        realTimeStockMonitor.addStockCode(stockInfo.code);
        realTimeStockMonitor.startNewThreadsIfNecessary();
        realTimeStockMonitor.refresh();
        
        MainFrame.this.highlightStock(row);
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
        final StockHistoryServer stockHistoryServer = stockHistoryMonitor.getStockHistoryServer(stock.code);

        if (stockHistoryServer == null) {
            if (stockCodeHistoryGUI.add(stock.code) && stockHistoryMonitor.addStockCode(stock.code)) {                                
                final String template = GUIBundle.getString("MainFrame_LookingForHistory_template");
                final String message = MessageFormat.format(template, stock.symbol, stockCodeHistoryGUI.size());
                setStatusBar(true, message);
            }
        }
        else {
            ChartJDialog chartJDialog = new ChartJDialog(MainFrame.this, stock.symbol + " (" + stock.code + ")", false, stockHistoryServer);
            chartJDialog.setVisible(true);                            
        }        
    }
    
    private JPopupMenu getMyJTablePopupMenu() {
        final JPopupMenu popup = new JPopupMenu();
        final TableModel tableModel = jTable1.getModel();            
        
        javax.swing.JMenuItem menuItem = new JMenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_History..."), this.getImageIcon("/images/16x16/strokedocker.png"));
        
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
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
            menuItem = new JMenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_Buy..."), this.getImageIcon("/images/16x16/inbox.png"));

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
                    // Say no to : portfolioManagementJPanel.showNewBuyTransactionJDialog(stock.symbol, stock.getLastPrice(), false);
                    portfolioManagementJPanel.showNewBuyTransactionJDialog(stock, stock.getLastPrice(), false);
                }
            });  
            
             //Company news
            popup.add(menuItem);            
            
            popup.addSeparator();
            
            menuItem = new JMenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_News"), this.getImageIcon("/images/16x16/internet.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    final int row = jTable1.getSelectedRow();
                    final int modelIndex = jTable1.getRowSorter().convertRowIndexToModel(row);
                    final Stock stock = ((StockTableModel)tableModel).getStock(modelIndex);
                    
                    String coTicker = stock.code.toString();
                    String coName = stock.symbol.toString();
                    
                    CompanyNews coNews = new CompanyNews(coTicker, coName);
                    coNews.getCoNews();
                    coNews.setVisible(true);  
                }
            });
         
            
            popup.add(menuItem);            
            
            popup.addSeparator();
        }                
        
        menuItem = new JMenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_Delete"), this.getImageIcon("/images/16x16/editdelete.png"));
        
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
        // 2 seconds.
        private static final long MIN_DELAY = 2000;
        private static final long MIN_DELAY_COUNTER = 3;
        private int minDelayCounter = 0;
        
        public MarketRunnable() {
        }
        
        @Override
        public void run() {
            final Thread currentThread = Thread.currentThread();

            final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories();
            final java.util.List<Index> is = org.yccheok.jstock.engine.Utils.getStockIndices(jStockOptions.getCountry());
            final int is_size = is.size();
            
            // Do not rely on isInterrupted flag only. The flag can be cleared by 3rd party easily.
            // Check for current thread as well.
            while (!currentThread.isInterrupted()  && (marketThread == currentThread)) {
                
                int fail = is_size;
                
                for (StockServerFactory factory : stockServerFactories) {
                    MarketServer marketServer = factory.getMarketServer();
                    
                    if (marketServer == null) {
                        continue;
                    }
                    
                    java.util.List<Market> markets = marketServer.getMarkets(is);
                    
                                        
                    if (marketThread != currentThread) {
                        break;
                    }

                    final int market_size = markets.size();

                    // Very strict rule.
                    if (market_size != is_size) {
                        continue;
                    }

                    fail -= market_size;

                    // Notify all the interested parties.
                    update(markets);

                    break;
                }
                
                try {
                    if (fail == 0) {
                        Thread.sleep(jStockOptions.getScanningSpeed());
                    } else {
                        if (minDelayCounter < MIN_DELAY_COUNTER) {
                            // Sleep as little as possible, to get the 1st reading
                            // as soon as possible. MIN_DELAY_COUNTER is used to avoid
                            // from getting our CPU and network too busy.
                            minDelayCounter++;
                            Thread.sleep(MIN_DELAY);
                        } else {
                            Thread.sleep(jStockOptions.getScanningSpeed());
                        }
                    }
                } catch (java.lang.InterruptedException exp) {
                    log.error(null, exp);
                    break;
                }
            }   // while
        }            
    }

    private static boolean saveStockNameDatabaseAsCSV(Country country, StockNameDatabase stockNameDatabase) {
        final File stockNameDatabaseCSVFile = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "stock-name-database.csv");
        final Statements statements = Statements.newInstanceFromStockNameDatabase(stockNameDatabase);
        boolean result = statements.saveAsCSVFile(stockNameDatabaseCSVFile);
        return result;
    }
    
    private static boolean saveStockInfoDatabaseAsCSV(Country country, StockInfoDatabase stockInfoDatabase) {
        final File stockInfoDatabaseCSVFile = org.yccheok.jstock.engine.Utils.getStockInfoDatabaseFile(country);
        final Statements statements = Statements.newInstanceFromStockInfoDatabase(stockInfoDatabase);
        boolean result = statements.saveAsCSVFile(stockInfoDatabaseCSVFile);
        return result;
    }
    
    private boolean saveUserDefinedDatabaseAsCSV(Country country, StockInfoDatabase stockInfoDatabase) {
        // Previously, we will store the entire stockcodeandsymboldatabase.xml
        // to cloud server if stockcodeandsymboldatabase.xml is containing
        // user defined code. Due to our server is running out of space, we will
        // only store UserDefined pair. user-defined-database.xml will be only
        // used for cloud storage purpose.
        final java.util.List<Pair<Code, Symbol>> pairs = getUserDefinedPair(stockInfoDatabase);
        // pairs can be empty. When it is empty, try to delete the file.
        // If deletion fail, we need to overwrite the file to reflect this.
        final File userDefinedDatabaseCSVFile = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "user-defined-database.csv");
        if (pairs.isEmpty()) {            
            if (userDefinedDatabaseCSVFile.delete() == true) {
                return true;
            }
        }
        final Statements statements = Statements.newInstanceFromUserDefinedDatabase(pairs);
        boolean result = statements.saveAsCSVFile(userDefinedDatabaseCSVFile);
        this.needToSaveUserDefinedDatabase = false;
        return result;
    }
    
    private java.util.List<Pair<Code, Symbol>> loadUserDefinedDatabaseFromCSV(Country country) {
        final File userDefinedDatabaseCSVFile = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "user-defined-database.csv");
        
        Statements statements = Statements.newInstanceFromCSVFile(userDefinedDatabaseCSVFile);
        if (statements.getType() != Statement.Type.UserDefinedDatabase) {
            return new ArrayList<Pair<Code, Symbol>>();
        }        
        java.util.List<Pair<Code, Symbol>> pairs = new ArrayList<Pair<Code, Symbol>>();
        for (int i = 0, ei = statements.size(); i < ei; i++) {
            Statement statement = statements.get(i);
            Atom atom0 = statement.getAtom(0);
            Atom atom1 = statement.getAtom(1);
            Code code = Code.newInstance(atom0.getValue().toString());
            Symbol symbol = Symbol.newInstance(atom1.getValue().toString());
            
            pairs.add(new Pair<Code, Symbol>(code, symbol));
        }
        return pairs;
    }
    
    private StockNameDatabase loadStockNameDatabaseFromCSV(Country country) {
        final File stockNameDatabaseCSVFile = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "stock-name-database.csv");
        
        Statements statements = Statements.newInstanceFromCSVFile(stockNameDatabaseCSVFile);
        if (statements.getType() != Statement.Type.StockNameDatabase) {
            return null;
        }        
        java.util.List<Stock> stocks = new ArrayList<Stock>();
        for (int i = 0, ei = statements.size(); i < ei; i++) {
            Statement statement = statements.get(i);
            Atom atom0 = statement.getAtom(0);
            Atom atom1 = statement.getAtom(1);
            Code code = Code.newInstance(atom0.getValue().toString());
            String name = atom1.getValue().toString();
            
            // Symbol doesn't matter. Just provide a dummy value for it.
            Stock stock = new Stock.Builder(code, Symbol.newInstance(code.toString())).name(name).build();
            stocks.add(stock);
        }
        return new StockNameDatabase(stocks);
    }
    
    private StockInfoDatabase loadStockInfoDatabaseFromCSV(Country country) {
        final File stockInfoDatabaseCSVFile = org.yccheok.jstock.engine.Utils.getStockInfoDatabaseFile(country);
        
        Statements statements = Statements.newInstanceFromCSVFile(stockInfoDatabaseCSVFile);
        if (statements.getType() != Statement.Type.StockInfoDatabase) {
            return null;
        }
        java.util.List<Stock> stocks = new ArrayList<Stock>();
        for (int i = 0, ei = statements.size(); i < ei; i++) {
            Statement statement = statements.get(i);
            Atom atom0 = statement.getAtom(0);
            Atom atom1 = statement.getAtom(1);
            Atom atom2 = statement.getAtom(2);
            Atom atom3 = statement.getAtom(3);
            
            Code code = Code.newInstance(atom0.getValue().toString());
            Symbol symbol = Symbol.newInstance(atom1.getValue().toString());
            Industry industry = Industry.Unknown;
            Board board = Board.Unknown;
            try {
                industry = Industry.valueOf(atom2.getValue().toString());
            } catch (Exception exp) {
                log.error(null, exp);
            }
            try {
                board = Board.valueOf(atom3.getValue().toString());
            } catch (Exception exp) {
                log.error(null, exp);
            }
            
            Stock stock = new Stock.Builder(code, symbol).board(board).industry(industry).build();
            stocks.add(stock);
        }
        return new StockInfoDatabase(stocks);
    }
    
    // Task to initialize both stockInfoDatabase and stockNameDatabase.
    private class DatabaseTask extends SwingWorker<Boolean, Void> {
        private boolean readFromDisk = true;

        public DatabaseTask(boolean readFromDisk)
        {
            this.readFromDisk = readFromDisk;
        }
        
        @Override
        protected void done() {
            // The done Method: When you are informed that the SwingWorker
            // is done via a property change or via the SwingWorker object's
            // done method, you need to be aware that the get methods can
            // throw a CancellationException. A CancellationException is a
            // RuntimeException, which means you do not need to declare it
            // thrown and you do not need to catch it. Instead, you should
            // test the SwingWorker using the isCancelled method before you
            // use the get method.
            if (this.isCancelled()) {
                // Cancelled by user explicitly. Do not perform any GUI update.
                // No pop-up message.
                return;
            }

            boolean success = false;
            
            try {
                success = get();
            } catch (InterruptedException exp) {
                log.error(null, exp);
            } catch (java.util.concurrent.ExecutionException exp) {
                log.error(null, exp);
            } catch (CancellationException ex) {
                // Not sure. Some developers suggest to catch this exception as
                // well instead of checking on isCancelled. I will do it both.
                log.error(null, ex);
            }
           
            if (success) {
                setStatusBar(false, getBestStatusBarMessage());
                statusBar.setImageIcon(getImageIcon("/images/16x16/network-transmit-receive.png"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_Connected"));
            }
            else {
                setStatusBar(false, GUIBundle.getString("MainFrame_NetworkError"));
                statusBar.setImageIcon(getImageIcon("/images/16x16/network-error.png"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_DoubleClickedToTryAgain"));
            }
       }
       
        @Override
        public Boolean doInBackground() {
            final Country country = jStockOptions.getCountry();
            
            Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database");

            if (this.readFromDisk)
            {
                StockInfoDatabase tmp_stock_info_database = loadStockInfoDatabaseFromCSV(country);
                if (tmp_stock_info_database == null) {
                    // Perhaps we are having a corrupted database. We will 
                    // restore from database.zip.
                    initPreloadDatabase(true);        
                    
                    tmp_stock_info_database = loadStockInfoDatabaseFromCSV(country);
                }

                // StockNameDatabase is an optional item.
                final StockNameDatabase tmp_name_database;
                if (org.yccheok.jstock.engine.Utils.isNameImmutable()) {
                    tmp_name_database = MainFrame.this.loadStockNameDatabaseFromCSV(country);
                } else {
                    tmp_name_database = null;
                }

                // After time consuming operation, check whether we should 
                // cancel.
                if (this.isCancelled()) {
                    return false;
                }
                
                if (tmp_stock_info_database != null && false == tmp_stock_info_database.isEmpty()) {
                    // Yes. We need to integrate "user-defined-database.csv" into tmp_stock_info_database
                    final java.util.List<Pair<Code, Symbol>> pairs = loadUserDefinedDatabaseFromCSV(country);
                    
                    boolean addUserDefinedStockInfoSuccessAtLeastOnce = false;
                    
                    if (pairs.isEmpty() == false) {
                        // Remove the old user defined database. Legacy stockcodeandsymboldatabase.xml
                        // may contain user defined codes.
                        tmp_stock_info_database.removeAllUserDefinedStockInfos();
                        
                        // Insert with new user defined code.
                        for (Pair<Code, Symbol> pair : pairs) {
                            if (tmp_stock_info_database.addUserDefinedStockInfo(StockInfo.newInstance(pair.first, pair.second))) {
                                addUserDefinedStockInfoSuccessAtLeastOnce = true;
                            }
                        }
                    }

                    if (false == addUserDefinedStockInfoSuccessAtLeastOnce) {
                        // user-defined-database.csv is no longer needed.
                        new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "user-defined-database.csv").delete();
                    }

                    // Prepare proper synchronization for us to change country.
                    synchronized (MainFrame.this.databaseTaskMonitor)
                    {
                        if (this.isCancelled()) {
                            return false;
                        }
                        
                        MainFrame.this.stockInfoDatabase = tmp_stock_info_database;
                        MainFrame.this.stockNameDatabase = tmp_name_database;
                        // Register the auto complete JComboBox with latest database.
                        ((AutoCompleteJComboBox)MainFrame.this.jComboBox1).setStockInfoDatabase(MainFrame.this.stockInfoDatabase);
                        MainFrame.this.indicatorPanel.setStockInfoDatabase(MainFrame.this.stockInfoDatabase);

                        return true;
                    }                
                }   // if (tmp_stock_info_database != null && false == tmp_stock_info_database.isEmpty())
            }   // if(this.readFromDisk)
                    
            // When we fall here, we either fail to read from disk or user
            // explicitly doesn't allow us to read from disk. Let's perform
            // networking stuff.
            //
            // For networking stuff, we will try on JStock static server.

            final String location = org.yccheok.jstock.engine.Utils.getStocksCSVFileLocation(country);
            // Try to download the CSV file.
            final File file = Utils.downloadAsTempFile(location);
            // Is download success?
            if (file == null) {
                return false;
            }
            // Try to parse the CSV file.
            final java.util.List<Stock> stocks = org.yccheok.jstock.engine.Utils.getStocksFromCSVFile(file);
            // Is the stocks good enough?
            if (false == stocks.isEmpty()) {
                final Pair<StockInfoDatabase, StockNameDatabase> stockDatabase = org.yccheok.jstock.engine.Utils.toStockDatabase(stocks, country);

                // After time consuming operation, check whether we should
                // cancel.
                if (this.isCancelled()) {
                    return false;
                }

                // Save to disk.
                MainFrame.saveStockInfoDatabaseAsCSV(country, stockDatabase.first);
                if (stockDatabase.second != null) {
                    MainFrame.saveStockNameDatabaseAsCSV(country, stockDatabase.second);
                }
                
                // Yes. We need to integrate "user-defined-database.csv" into tmp_stock_info_database
                final java.util.List<Pair<Code, Symbol>> pairs = loadUserDefinedDatabaseFromCSV(country);
                
                if (pairs.isEmpty() == false) {
                    // Insert with new user defined code.
                    for (Pair<Code, Symbol> pair : pairs) {
                        stockDatabase.first.addUserDefinedStockInfo(StockInfo.newInstance(pair.first, pair.second));
                    }
                }

                // Prepare proper synchronization for us to change country.
                synchronized (MainFrame.this.databaseTaskMonitor)
                {
                    if (this.isCancelled()) {
                        return false;
                    }

                    MainFrame.this.stockInfoDatabase = stockDatabase.first;
                    MainFrame.this.stockNameDatabase = stockDatabase.second;

                    // Register the auto complete JComboBox with latest database.
                    ((AutoCompleteJComboBox)MainFrame.this.jComboBox1).setStockInfoDatabase(MainFrame.this.stockInfoDatabase);
                    MainFrame.this.indicatorPanel.setStockInfoDatabase(MainFrame.this.stockInfoDatabase);

                    return true;
                }
            }
            
            return false;
        }
    }

    private void initMyJXStatusBarExchangeRateLabelMouseAdapter() {
        final MouseAdapter mouseAdapter = this.getMyJXStatusBarExchangeRateLabelMouseAdapter();
        this.statusBar.addExchangeRateLabelMouseListener(mouseAdapter);
    }

    private void initMyJXStatusBarCountryLabelMouseAdapter() {
        final MouseAdapter mouseAdapter = this.getMyJXStatusBarCountryLabelMouseAdapter();
        this.statusBar.addCountryLabelMouseListener(mouseAdapter);
    }
    
    private void initMyJXStatusBarImageLabelMouseAdapter() {
        final MouseAdapter mouseAdapter = this.getMyJXStatusBarImageLabelMouseAdapter();
        this.statusBar.addImageLabelMouseListener(mouseAdapter);
    }

    /**
     * Initializes currency exchange monitor.
     */
    public void initCurrencyExchangeMonitor() {
        final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories();
        this.portfolioManagementJPanel.initCurrencyExchangeMonitor(stockServerFactories);
    }

    private void initRealTimeStockMonitor() {
        final RealTimeStockMonitor oldRealTimeStockMonitor = realTimeStockMonitor;
        if (oldRealTimeStockMonitor != null) {            
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

        this.indicatorScannerJPanel.initRealTimeStockMonitor(stockServerFactories);
        this.portfolioManagementJPanel.initRealTimeStockMonitor(stockServerFactories);
    }

    // Only call after initJStockOptions.
    private void initBrokingFirmLogos() {
        final int size = jStockOptions.getBrokingFirmSize();

        for (int i=0; i<size; i++) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "logos" + File.separator + i + ".png"));

                jStockOptions.getBrokingFirm(i).setLogo(bufferedImage);
            } catch (IOException exp) {
                log.error(null, exp);
            }
        }
    }

    private void initGUIOptions() {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "mainframe.xml");
        GUIOptions guiOptions = Utils.fromXML(GUIOptions.class, f);

        if (guiOptions == null)
        {
            // When user launches JStock for first time, we will help him to
            // turn off the following column(s), as we feel those information
            // is redundant. If they wish to view those information, they have
            // to turn it on explicitly.
            JTableUtilities.removeTableColumn(jTable1, GUIBundle.getString("MainFrame_Open"));
            return;
        }

        if (guiOptions.getJTableOptionsSize() <= 0)
        {
            // When user launches JStock for first time, we will help him to
            // turn off the following column(s), as we feel those information
            // is redundant. If they wish to view those information, they have
            // to turn it on explicitly.
            JTableUtilities.removeTableColumn(jTable1, GUIBundle.getString("MainFrame_Open"));
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
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config") == false)
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

    /**
     * Initialize chart dialog options.
     */
    private void initChartJDialogOptions() {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "chartjdialogoptions.xml");
        final ChartJDialogOptions tmp = Utils.fromXML(ChartJDialogOptions.class, f);
        if (tmp == null) {
            this.chartJDialogOptions = new ChartJDialogOptions();
        }
        else {
            this.chartJDialogOptions = tmp;
            log.info("chartJDialogOptions loaded from " + f.toString() + " successfully.");
        }
    }

    /**
     * Initialize JStock options.
     */
    private void initJStockOptions(JStockOptions jStockOptions) {
        this.jStockOptions = jStockOptions;

        /* Hard core fix. */
        if (this.jStockOptions.getScanningSpeed() == 0) {
            this.jStockOptions.setScanningSpeed(5000);
        }
                
        final String proxyHost = this.jStockOptions.getProxyServer();
        final int proxyPort = this.jStockOptions.getProxyPort();
        
        if ((proxyHost.length() > 0) && (org.yccheok.jstock.engine.Utils.isValidPortNumber(proxyPort))) {
            System.getProperties().put("http.proxyHost", proxyHost);
            System.getProperties().put("http.proxyPort", "" + proxyPort);
        }
        else {
            System.getProperties().remove("http.proxyHost");
            System.getProperties().remove("http.proxyPort");
        }

        for (Country country : Country.values()) {
            final Class c = this.jStockOptions.getPrimaryStockServerFactoryClass(country);
            if (c == null) {
                continue;
            }
            Factories.INSTANCE.updatePrimaryStockServerFactory(country, c);
        }
    }   

    public void updatePrimaryStockServerFactory(Country country, Class<? extends StockServerFactory> c) {
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

    private void initWatchlist() {
        // This is new watchlist. Reset last update date.
        this.timestamp = 0;
        initCSVWatchlist();
    }
    
    private boolean initCSVWatchlist() {  
        java.util.List<String> availableWatchlistNames = org.yccheok.jstock.watchlist.Utils.getWatchlistNames();
        // Do we have any watchlist for this country?
        if (availableWatchlistNames.size() <= 0) {
            // If not, create an empty watchlist.
            org.yccheok.jstock.watchlist.Utils.createEmptyWatchlist(org.yccheok.jstock.watchlist.Utils.getDefaultWatchlistName());
            availableWatchlistNames = org.yccheok.jstock.watchlist.Utils.getWatchlistNames();
        }
        assert(availableWatchlistNames.isEmpty() == false);

        // Is user selected watchlist name within current available watchlist names?
        if (false == availableWatchlistNames.contains(jStockOptions.getWatchlistName())) {
            // Nope. Reset user selected watchlist name to the first available name.
            jStockOptions.setWatchlistName(availableWatchlistNames.get(0));
        }
        
        // openAsCSVFile will "append" stocks. We need to clear previous stocks
        // explicitly.
        
        // Clear the previous data structures.
        clearAllStocks();
        
        File realTimeStockFile = org.yccheok.jstock.watchlist.Utils.getWatchlistFile(org.yccheok.jstock.watchlist.Utils.getWatchlistDirectory());
        return this.openAsCSVFile(realTimeStockFile);
    }
    
    public static boolean saveCSVWatchlist(String directory, CSVWatchlist csvWatchlist) {
        assert(directory.endsWith(File.separator));
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory) == false)
        {
            return false;
        } 
        return MainFrame.saveAsCSVFile(csvWatchlist, org.yccheok.jstock.watchlist.Utils.getWatchlistFile(directory), true);
    }
    
    private boolean saveCSVWathclist() {
        final String directory = org.yccheok.jstock.watchlist.Utils.getWatchlistDirectory();
        final TableModel tableModel = jTable1.getModel();
        CSVWatchlist csvWatchlist = CSVWatchlist.newInstance(tableModel);
        return MainFrame.saveCSVWatchlist(directory, csvWatchlist);
    }

    private boolean saveWatchlist() {
        return this.saveCSVWathclist();
    }
    
    private boolean saveBrokingFirmLogos() {
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "logos") == false)
        {
            return false;
        }
        
        if (Utils.deleteDir(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "logos", false) == false) {
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
            catch (java.io.IOException exp) {
                log.error(null, exp);
            }
        }
        
        return true;
    }

    private boolean saveDatabase() {
        final Country country = jStockOptions.getCountry();
            
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database") == false)
        {
            return false;
        }

        // Use local variable to ensure thread safety.
        final StockInfoDatabase stock_info_database = this.stockInfoDatabase;
        final StockNameDatabase name_database = this.stockNameDatabase;

        boolean b0 = true;

        if (name_database != null) {
            final Statements statements = Statements.newInstanceFromStockNameDatabase(name_database);
            final File stockNameDatabaseCSVFile = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "stock-name-database.csv");
            b0 = statements.saveAsCSVFile(stockNameDatabaseCSVFile);
        }

        if (stock_info_database == null)
        {
            return false;
        }

        // This could happen when OutOfMemoryException happen while fetching stock database information
        // from the server.
        if (stock_info_database.isEmpty())
        {
            log.info("Database was corrupted.");
            return false;
        }

        final boolean b1 = saveUserDefinedDatabaseAsCSV(country, stock_info_database);

        // For optimization purpose.
        // symbol_database will always contain UserDefined code and non-UserDefined 
        // code. As we may always recover UserDefined code from
        // user-defined-database.xml, we will not save the duplicated information.
        //
        // We will only do it, if stock-info-database.xml is not available,
        // which is very unlikely. Because during application startup, we will
        // always check the existance of stock-info-database.xml.
        boolean b2 = true;
        final File f = org.yccheok.jstock.engine.Utils.getStockInfoDatabaseFile(country);
        if (f.exists() == false) {
            b2 = saveStockInfoDatabaseAsCSV(country, stock_info_database);
        }

        return b0 && b1 && b2;
    }

    private static java.util.List<Pair<Code, Symbol>> getUserDefinedPair(StockInfoDatabase stockInfoDatabase) {
        java.util.List<Pair<Code, Symbol>> pairs = new ArrayList<Pair<Code, Symbol>>();
        java.util.List<StockInfo> stockInfos = stockInfoDatabase.getUserDefinedStockInfos();
        for (StockInfo stockInfo : stockInfos) {
            pairs.add(new Pair(stockInfo.code, stockInfo.symbol));
        }
        return pairs;
    }

    /**
     * Save chart dialog options to disc.
     * @return <tt>true</tt> if saving operation is success
     */
    private boolean saveChartJDialogOptions() {
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config") == false)
        {
            return false;
        }

        File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "chartjdialogoptions.xml");
        return org.yccheok.jstock.gui.Utils.toXML(this.chartJDialogOptions, f);
    }

    /**
     * Save JStock options to disc.
     * @return <tt>true</tt> if saving operation is success
     */
    private boolean saveJStockOptions() {
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config") == false)
        {
            return false;
        }
        
        File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "options.xml");
        return org.yccheok.jstock.gui.Utils.toXML(this.jStockOptions, f);
    }

    private void removeOldHistoryData(Country country) {
        // We do not want "yesterday" history record. We will remove 1 day old files.
        org.yccheok.jstock.gui.Utils.deleteAllOldFiles(new File(Utils.getHistoryDirectory(country)), 1);
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
        final StockHistoryMonitor oldStockHistoryMonitor = stockHistoryMonitor;
        if (oldStockHistoryMonitor != null) {            
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
        
        this.stockHistoryMonitor = new StockHistoryMonitor(HISTORY_MONITOR_MAX_THREAD);
        
        final java.util.List<StockServerFactory> stockServerFactories = getStockServerFactories();
        stockHistoryMonitor.setStockServerFactories(stockServerFactories);
        
        stockHistoryMonitor.attach(this.stockHistoryMonitorObserver);

        final Country country = jStockOptions.getCountry();

        removeOldHistoryData(country);

        StockHistorySerializer stockHistorySerializer = new StockHistorySerializer(Utils.getHistoryDirectory());

        stockHistoryMonitor.setStockHistorySerializer(stockHistorySerializer);

        stockHistoryMonitor.setDuration(Duration.getTodayDurationByYears(jStockOptions.getHistoryDuration()));
    }

    // Determine whether we should make use of KLSEInfoStockServerFactory.
    private void initKLSEInfoStockServerFactoryThread()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String remove = org.yccheok.jstock.gui.Utils.getUUIDValue(org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.OPTIONS), "remove_klse_info_stock_server_factory");
                if (remove != null && remove.equals("1"))
                {
                    Factories.INSTANCE.removeKLSEInfoStockServerFactory();
                }
            }            
        };
        
        this.klseInfoStockServerFactoryThread = new Thread(runnable);
        this.klseInfoStockServerFactoryThread.start();
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
            final LatestNewsTask oldLatestNewsTask = latestNewsTask;
            if (oldLatestNewsTask != null) {                
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
        final Thread oldMarketThread = marketThread;
        if (oldMarketThread != null) {
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
    
    private void initAjaxProvider() {
        Country country = this.jStockOptions.getCountry();
        
        final AutoCompleteJComboBox autoCompleteJComboBox = ((AutoCompleteJComboBox)this.jComboBox1);

        if (country == Country.India) {
            autoCompleteJComboBox.setAjaxProvider(AjaxServiceProvider.Google, Arrays.asList("NSE", "BOM"));
            autoCompleteJComboBox.setGreedyEnabled(true, Arrays.asList("N", "B"));
        } else {
            autoCompleteJComboBox.setAjaxProvider(AjaxServiceProvider.Yahoo, java.util.Collections.<String>emptyList());
            autoCompleteJComboBox.setGreedyEnabled(false, java.util.Collections.<String>emptyList());
        }
        
        this.indicatorPanel.initAjaxProvider();
    }
    
    private void initStockInfoDatabaseMeta() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Read existing stock-info-database-meta.json
                final Map<Country, Long> localStockInfoDatabaseMeta = Utils.loadStockInfoDatabaseMeta(Utils.getStockInfoDatabaseMetaFile());
                
                final String location = org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.STOCK_INFO_DATABASE_META);
                
                final String json = Utils.downloadAsString(location);
                
                final Map<Country, Long> latestStockInfoDatabaseMeta = Utils.loadStockInfoDatabaseMeta(json);

                final Map<Country, Long> successStockInfoDatabaseMeta = new EnumMap<Country, Long>(Country.class);
                
                boolean needToInitDatabase = false;
                
                // Build up list of stock-info-database.csv that needed to be
                // updated.
                for (Map.Entry<Country, Long> entry : latestStockInfoDatabaseMeta.entrySet()) {
                    if (Thread.currentThread().isInterrupted() || stockInfoDatabaseMetaPool == null) {
                        break;
                    }
                    
                    Country country = entry.getKey();
                    Long latest = entry.getValue();
                    Long local = localStockInfoDatabaseMeta.get(country);
                                        
                    if (false == latest.equals(local)) {
                        final String stocksCSVFileLocation = org.yccheok.jstock.engine.Utils.getStocksCSVFileLocation(country);
                        final File file = Utils.downloadAsTempFile(stocksCSVFileLocation);
                        if (file != null) {                            
                            final java.util.List<Stock> stocks = org.yccheok.jstock.engine.Utils.getStocksFromCSVFile(file);
                            
                            if (false == stocks.isEmpty()) {
                                final Pair<StockInfoDatabase, StockNameDatabase> stockDatabase = org.yccheok.jstock.engine.Utils.toStockDatabase(stocks, country);
                                MainFrame.saveStockInfoDatabaseAsCSV(country, stockDatabase.first);
                                if (stockDatabase.second != null) {
                                    MainFrame.saveStockNameDatabaseAsCSV(country, stockDatabase.second);
                                }
                                successStockInfoDatabaseMeta.put(country, latest);
                                
                                if (country == jStockOptions.getCountry()) {
                                    needToInitDatabase = true;
                                }
                            }
                        }
                    }
                }
                
                if (successStockInfoDatabaseMeta.isEmpty()) {
                    return;
                }
                
                // Retain old meta value.
                for (Map.Entry<Country, Long> entry : localStockInfoDatabaseMeta.entrySet()) {
                    Country country = entry.getKey();
                    Long old = entry.getValue();
                    
                    if (false == successStockInfoDatabaseMeta.containsKey(country)) {
                        successStockInfoDatabaseMeta.put(country, old);
                    }
                }
                
                Utils.saveStockInfoDatabaseMeta(Utils.getStockInfoDatabaseMetaFile(), successStockInfoDatabaseMeta);
                
                if (needToInitDatabase) {
                    initDatabase(true);
                }
            }           
        };
        
        stockInfoDatabaseMetaPool.execute(runnable);
    }
    
    private void initDatabase(boolean readFromDisk) {
        // Update GUI state.
        this.setStatusBar(true, GUIBundle.getString("MainFrame_ConnectingToStockServerToRetrieveStockInformation..."));
        this.statusBar.setImageIcon(getImageIcon("/images/16x16/network-connecting.png"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_Connecting..."));

        // Stop any on-going activities.
        // Entire block will be synchronized, as we do not want to hit by more
        // than 1 databaseTask running.
        synchronized (this.databaseTaskMonitor)
        {
            if (this.databaseTask != null)
            {
                this.databaseTask.cancel(true);
                this.stockInfoDatabase = null;
                this.stockNameDatabase = null;
                ((AutoCompleteJComboBox)this.jComboBox1).setStockInfoDatabase(null);
                this.indicatorPanel.setStockInfoDatabase(null);
            }
            
            this.databaseTask = new DatabaseTask(readFromDisk);
            this.databaseTask.execute();
        }

        // We may hold a large database previously. Invoke garbage collector to perform cleanup.
        System.gc();
    }
        
    private void update(RealTimeStockMonitor monitor, final java.util.List<Stock> stocks) { 
        // We need to ignore symbol names given by stock server. Replace them
        // with database's.
        final boolean isSymbolImmutable = org.yccheok.jstock.engine.Utils.isSymbolImmutable();                
        for (int i = 0, size = stocks.size(); i < size; i++) {
            final Stock stock = stocks.get(i);
            Stock new_stock = stock;
            // Sometimes server goes crazy by returning empty symbol.
            if (isSymbolImmutable || new_stock.symbol.toString().isEmpty()) {                
                // Use local variable to ensure thread safety.
                final StockInfoDatabase stock_info_database = this.stockInfoDatabase;
                //final StockNameDatabase name_database = this.stockNameDatabase;
                
                if (stock_info_database != null) {
                    final Symbol symbol = stock_info_database.codeToSymbol(stock.code);
                    if (symbol != null) {
                        new_stock = new_stock.deriveStock(symbol);
                    } else {
                        // Shouldn't be null. Let's give some warning on this.
                        log.error("Wrong stock code " + stock.code + " given by stock server.");
                    }
                } else {
                    // stockCodeAndSymbolDatabase is not ready yet. Use the information
                    // from stock table.
                    final StockTableModel tableModel = (StockTableModel)jTable1.getModel();
                    final int row = tableModel.findRow(stock);
                    if (row >= 0) {
                        final Symbol symbol = tableModel.getStock(row).symbol;
                        new_stock = new_stock.deriveStock(symbol);
                    }
                }   // if (symbol_database != null)

                // Doesn't matter, as we do not need to show "name" in table.
                // Need not to perform derive for speed optimization.
                //if (org.yccheok.jstock.engine.Utils.isNameImmutable()) {
                //    if (name_database != null) {
                //        final String name = name_database.codeToName(stock.code);
                //        if (name != null) {
                //            new_stock = new_stock.deriveStock(name);
                //        } else {
                //            // Shouldn't be null. Let's give some warning on this.
                //            log.error("Wrong stock code " + stock.code + " given by stock server.");
                //        }
                //    } else {
                //        // stockNameDatabase is not ready yet. Use the information
                //        // from stock table.
                //        final StockTableModel tableModel = (StockTableModel)jTable1.getModel();
                //        final int row = tableModel.findRow(stock);
                //        if (row >= 0) {
                //            final String name = tableModel.getStock(row).getName();
                //            new_stock = new_stock.deriveStock(name);
                //        }
                //    }
                //}

                if (stock != new_stock) {
                    stocks.set(i, new_stock);
                }
            }   // if (isSymbolImmutable || new_stock.symbol.toString().isEmpty())
        }   // for (int i = 0, size = stocks.size(); i < size; i++)
        
        // Update status bar with current time string.
        this.timestamp = System.currentTimeMillis();
        ((StockTableModel)jTable1.getModel()).setTimestamp(this.timestamp);
        
        MainFrame.getInstance().updateStatusBarWithLastUpdateDateMessageIfPossible();

        // Do it in GUI event dispatch thread. Otherwise, we may face deadlock.
        // For example, we lock the jTable, and try to remove the stock from the
        // real time monitor. While we wait for the real time monitor to complete,
        // real time monitor will call this function and, be locked at function
        // updateStockToTable.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Stock stock : stocks) {                    
                    updateStockToTable(stock);
                    if (isStockBeingSelected(stock)) {
                        MainFrame.this.updateDynamicChart(stock);
                    }
                }               
            }
        });

        // Dynamic charting. Intraday trader might love this.
        for (Stock stock : stocks) {
            final Code code = stock.code;
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
                dynamicChart.addPriceObservation(stock.getTimestamp(), stock.getLastPrice());
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
                dynamicChart.addPriceObservation(stock.getTimestamp(), stock.getLastPrice());
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

    public void updateStatusBarWithLastUpdateDateMessageIfPossible() {
        if (this.refreshPriceInProgress) {
            // Stop refresh price in progress message.
            this.setStatusBar(false, getBestStatusBarMessage());
            this.refreshPriceInProgress = false;
            return;
        }
        
        if (this.isStatusBarBusy) {
            return;
        }
        
        if (this.getSelectedComponent() != this.jPanel8 && this.getSelectedComponent() != this.portfolioManagementJPanel) {
            return;
        }
        
        this.setStatusBar(false, getBestStatusBarMessage());
    }
    
    // Connected
    // [My Watchlist] Last update: Connected
    // [My Watchlist] Last update: 10:40AM
    public String getBestStatusBarMessage() {        
        final String currentName;
        final long _timestamp;
        // MainFrame
        if (this.getSelectedComponent() == this.jPanel8) {
            currentName = this.getJStockOptions().getWatchlistName();
            _timestamp = this.timestamp;
        } else if (this.getSelectedComponent() == this.portfolioManagementJPanel) {
            currentName = this.getJStockOptions().getPortfolioName();
            _timestamp = this.portfolioManagementJPanel.getTimestamp();
        } else {
            return GUIBundle.getString("MainFrame_Connected");
        }
        
        if (_timestamp == 0) {
            return MessageFormat.format(GUIBundle.getString("MainFrame_Connected_template"), currentName);            
        }

        Date date = new Date(_timestamp);
        String time;
        if (Utils.isToday(_timestamp)) {
            time = Utils.getTodayLastUpdateTimeFormat().format(date);
        } else {
            time = Utils.getOtherDayLastUpdateTimeFormat().format(date);
        }

        return MessageFormat.format(GUIBundle.getString("MainFrame_LastUpdate_template"), currentName, time);
    }
    
    private void update(final java.util.List<Market> markets) {
        assert(markets.isEmpty() == false);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               marketJPanel.update(markets);
            }
        });
    }
    
    public void update(StockHistoryMonitor monitor, final StockHistoryMonitor.StockHistoryRunnable runnable) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Code code = runnable.getCode();
                Symbol symbol = null;

                // Use local variable to ensure thread safety.
                final StockInfoDatabase stock_info_database = MainFrame.this.stockInfoDatabase;
                // Is the database ready?
                if (stock_info_database != null) {
                    // Possible null if we are trying to get index history.
                    symbol = stock_info_database.codeToSymbol(code);
                }
                final boolean shouldShowGUI = MainFrame.this.stockCodeHistoryGUI.remove(code);
               
                if (stockCodeHistoryGUI.isEmpty()) {
                    if (runnable.getStockHistoryServer() != null) {
                        final String template = GUIBundle.getString("MainFrame_HistorySuccess_template");
                        final String message = MessageFormat.format(template, (symbol != null ? symbol : code));
                        setStatusBar(false, message);
                    }
                    else {
                        final String template = GUIBundle.getString("MainFrame_HistoryFailed_template");
                        final String message = MessageFormat.format(template, (symbol != null ? symbol : code));
                        setStatusBar(false, message);
                    }
                }
                else {
                    if (runnable.getStockHistoryServer() != null) {
                        final String template = GUIBundle.getString("MainFrame_HistorySuccessStillWaitingForHistoryTotal_template");
                        final String message = MessageFormat.format(template, (symbol != null ? symbol : code), stockCodeHistoryGUI.size());
                        setStatusBar(true, message);
                    }
                    else {
                        final String template = GUIBundle.getString("MainFrame_HistoryFailedStillWaitingForHistoryTotal_template");
                        final String message = MessageFormat.format(template, (symbol != null ? symbol : code), stockCodeHistoryGUI.size());
                        setStatusBar(true, message);
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
            
            if (rows.length == 1) {
                int row = rows[0];
                
                StockTableModel tableModel = (StockTableModel)jTable1.getModel();
                int modelIndex = jTable1.convertRowIndexToModel(row);
                Stock stock = tableModel.getStock(modelIndex);
                updateDynamicChart(stock);
            } else {
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
                if (jTable1.getSelectedRowCount() > 0) {
                    getMyJTablePopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
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
        if (trayIcon == null) {
            return;
        }

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
            Utils.deleteDir(Utils.getHistoryDirectory(country), false);
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
        if (this.marketJPanel != null) {
            jPanel2.remove(marketJPanel);            
        }
        
        this.marketJPanel = new MarketJPanel(jStockOptions.getCountry());
        jPanel2.add(marketJPanel);
        jPanel2.revalidate();
    }

    private void initPreloadDatabase(boolean overWrite) {
        /* No overwrite. */
        Utils.extractZipFile("database" + File.separator + "database.zip", overWrite);
    }

    private class LatestNewsTask extends SwingWorker<Void, String> {
        // Delay first update checking for 20 seconds.
        private static final int SHORT_DELAY = 20 * 1000;

        private volatile CountDownLatch doneSignal;       

        @Override
        protected void done() {
        }

        @Override
        protected void process(java.util.List<String> messages) {
            boolean show = false;

            for (String message : messages)
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
                final java.util.Map<String, String> map = Utils.getUUIDValue(org.yccheok.jstock.network.Utils.getURL(org.yccheok.jstock.network.Utils.Type.NEWS_INFORMATION_TXT));
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
    
    public void initDynamicChartVisibility() {
        jPanel10.setVisible(this.jStockOptions.isDynamicChartVisible());
    }

    private void initDynamicCharts()
    {
        dynamicCharts.clear();
    }

    private void initStatusBar()
    {
        final String message = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_ConnectingToStockServerToRetrieveStockInformation...");
        final ImageIcon icon = getImageIcon("/images/16x16/network-connecting.png");
        final String iconMessage = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("MainFrame_Connecting...");
        
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

                    final DynamicChart dynamicChart = MainFrame.this.dynamicCharts.get(stock.code);
                    if (dynamicChart == null) {
                        return;
                    }
                    Symbol symbol = null;
                    // Use local variable to ensure thread safety.
                    final StockInfoDatabase stock_info_database = MainFrame.this.stockInfoDatabase;
                    // Is the database ready?
                    if (stock_info_database != null) {
                        // Possible null if we are trying to get index history.
                        symbol = stock_info_database.codeToSymbol(stock.code);
                    }
                    final String template = GUIBundle.getString("MainFrame_IntradayMovementTemplate");
                    final String message = MessageFormat.format(template, symbol == null ? stock.symbol : symbol);
                    dynamicChart.showNewJDialog(MainFrame.this, message);
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
    
    private void refreshCurrencyExchangeMonitor() {
        this.portfolioManagementJPanel.refreshCurrencyExchangeMonitor();
    }
    
    public void refreshAllRealTimeStockMonitors() {
        RealTimeStockMonitor _realTimeStockMonitor = this.realTimeStockMonitor;
        if (_realTimeStockMonitor != null) {
            _realTimeStockMonitor.refresh();
        }
        this.indicatorScannerJPanel.refreshRealTimeStockMonitor();
        this.portfolioManagementJPanel.refreshRealTimeStockMonitor();
    }
    
    private TrayIcon trayIcon;
    
    private static final Log log = LogFactory.getLog(MainFrame.class);
        
    private MyJXStatusBar statusBar = new MyJXStatusBar();
    private boolean isStatusBarBusy = false;
    
    // A set of stock history which we need to display GUI on them, when user request explicitly.
    private java.util.Set<Code> stockCodeHistoryGUI = new java.util.HashSet<Code>();
    
    private volatile StockInfoDatabase stockInfoDatabase = null;
    // StockNameDatabase is an optional item.
    private volatile StockNameDatabase stockNameDatabase = null;
    
    private RealTimeStockMonitor realTimeStockMonitor = null;
    private StockHistoryMonitor stockHistoryMonitor = null;

    private DatabaseTask databaseTask = null;
    private final Object databaseTaskMonitor = new Object();

    private LatestNewsTask latestNewsTask = null;
    private volatile Thread marketThread = null;
    private Thread klseInfoStockServerFactoryThread = null;   
    private JStockOptions jStockOptions;
    private ChartJDialogOptions chartJDialogOptions;
    
    private IndicatorPanel indicatorPanel;
    private IndicatorScannerJPanel indicatorScannerJPanel;
    private PortfolioManagementJPanel portfolioManagementJPanel;

    private final AlertStateManager alertStateManager = new AlertStateManager();
    private ExecutorService emailAlertPool = Executors.newFixedThreadPool(1);
    private ExecutorService smsAlertPool = Executors.newFixedThreadPool(1);
    private ExecutorService systemTrayAlertPool = Executors.newFixedThreadPool(1);
    private volatile ExecutorService stockInfoDatabaseMetaPool = Executors.newFixedThreadPool(1);
            
    private final org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, java.util.List<Stock>> realTimeStockMonitorObserver = this.getRealTimeStockMonitorObserver();
    private final org.yccheok.jstock.engine.Observer<StockHistoryMonitor, StockHistoryMonitor.StockHistoryRunnable> stockHistoryMonitorObserver = this.getStockHistoryMonitorObserver();
    private final org.yccheok.jstock.engine.Observer<Indicator, Boolean> alertStateManagerObserver = this.getAlertStateManagerObserver();

    private final javax.swing.ImageIcon smileIcon = this.getImageIcon("/images/16x16/smile.png");
    private final javax.swing.ImageIcon smileGrayIcon = this.getImageIcon("/images/16x16/smile-gray.png");

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
    
    private static final int HISTORY_MONITOR_MAX_THREAD = 4;

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

    // Do we need to save user defined database when we switch country or close
    // this application?
    private volatile boolean needToSaveUserDefinedDatabase = false;

    private volatile boolean isFormWindowClosedCalled = false;
    
    // The last time when we receive stock price update.
    private long timestamp = 0;
    private boolean refreshPriceInProgress = false;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
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
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    
}
