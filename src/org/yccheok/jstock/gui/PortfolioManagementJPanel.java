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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.*;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.*;
import org.yccheok.jstock.engine.*;
import org.yccheok.jstock.engine.currency.CurrencyPair;
import org.yccheok.jstock.engine.currency.ExchangeRate;
import org.yccheok.jstock.engine.currency.ExchangeRateMonitor;
import org.yccheok.jstock.file.GUIBundleWrapper;
import org.yccheok.jstock.file.Statement;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.Utils.FileEx;
import org.yccheok.jstock.gui.charting.InvestmentFlowChartJDialog;
import org.yccheok.jstock.gui.portfolio.CommentJDialog;
import org.yccheok.jstock.gui.portfolio.DepositSummaryJDialog;
import org.yccheok.jstock.gui.portfolio.DepositSummaryTableModel;
import org.yccheok.jstock.gui.portfolio.DividendSummaryBarChartJDialog;
import org.yccheok.jstock.gui.portfolio.DividendSummaryJDialog;
import org.yccheok.jstock.gui.portfolio.DividendSummaryTableModel;
import org.yccheok.jstock.gui.portfolio.SplitJDialog;
import org.yccheok.jstock.gui.portfolio.ToolTipHighlighter;
import org.yccheok.jstock.gui.treetable.AbstractPortfolioTreeTableModelEx;
import org.yccheok.jstock.gui.treetable.BuyPortfolioTreeTableModelEx;
import org.yccheok.jstock.gui.treetable.SellPortfolioTreeTableModelEx;
import org.yccheok.jstock.gui.treetable.SortableTreeTable;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.portfolio.*;

/**
 *
 * @author  Owner
 */
public class PortfolioManagementJPanel extends javax.swing.JPanel {
       
    public static final class CSVPortfolio {
        public final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel;
        public final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel;        
        public final DividendSummary dividendSummary;
        public final DepositSummary depositSummary;


        private CSVPortfolio(BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel, SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel, DividendSummary dividendSummary, DepositSummary depositSummary) {
            this.buyPortfolioTreeTableModel = buyPortfolioTreeTableModel;
            this.sellPortfolioTreeTableModel = sellPortfolioTreeTableModel;
            this.dividendSummary = dividendSummary;            
            this.depositSummary = depositSummary;
        }
        
        public static CSVPortfolio newInstance(BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel, SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel, DividendSummary dividendSummary, DepositSummary depositSummary) {
            return new CSVPortfolio(buyPortfolioTreeTableModel, sellPortfolioTreeTableModel, dividendSummary, depositSummary);
        }
    }
    
    /** Creates new form PortfoliioJPanel */
    public PortfolioManagementJPanel() {
        initComponents();        
        
        this.initPortfolio();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        buyTreeTable = new SortableTreeTable(new BuyPortfolioTreeTableModelEx());
        // We need to have a hack way, to have "Comment" in the model, but not visible to user.
        // So that our ToolTipHighlighter can work correctly.
        // setVisible should be called after JXTreeTable has been constructed. This is to avoid
        // initGUIOptions from calling JTable.removeColumn
        // ToolTipHighlighter will not work correctly if we tend to hide column view by removeColumn.
        // We need to hide the view by using TableColumnExt.setVisible.
        // Why? Don't ask me. Ask SwingX team.
        ((TableColumnExt)buyTreeTable.getColumn(GUIBundle.getString("PortfolioManagementJPanel_Comment"))).setVisible(false);
        jScrollPane2 = new javax.swing.JScrollPane();
        sellTreeTable = new SortableTreeTable(new SellPortfolioTreeTableModelEx());

        // We need to have a hack way, to have "Comment" in the model, but not visible to user.
        // So that our ToolTipHighlighter can work correctly.
        // setVisible should be called after JXTreeTable has been constructed. This is to avoid
        // initGUIOptions from calling JTable.removeColumn
        // ToolTipHighlighter will not work correctly if we tend to hide column view by removeColumn.
        // We need to hide the view by using TableColumnExt.setVisible.
        // Why? Don't ask me. Ask SwingX team.
        ((TableColumnExt)sellTreeTable.getColumn(GUIBundle.getString("PortfolioManagementJPanel_Comment"))).setVisible(false);
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Portfolio Management"));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 5));

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText(getShareLabel());
        jPanel3.add(jLabel1);

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jPanel3.add(jLabel2);

        jLabel3.setText(getCashLabel());
        jPanel3.add(jLabel3);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jPanel3.add(jLabel4);

        jPanel4.add(jPanel3, java.awt.BorderLayout.WEST);

        jLabel5.setText(getPaperProfitLabel());
        jPanel5.add(jLabel5);

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | java.awt.Font.BOLD));
        jPanel5.add(jLabel6);

        jLabel7.setText(getRealizedProfitLabel());
        jPanel5.add(jLabel7);

        jLabel8.setFont(jLabel8.getFont().deriveFont(jLabel8.getFont().getStyle() | java.awt.Font.BOLD));
        jPanel5.add(jLabel8);

        jPanel4.add(jPanel5, java.awt.BorderLayout.EAST);

        jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PortfolioManagementJPanel_Buy"))); // NOI18N

        buyTreeTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        buyTreeTable.setRootVisible(true);
        // this must be before any sort instructions or get funny results
        buyTreeTable.setAutoCreateColumnsFromModel(false);

        buyTreeTable.addMouseListener(new BuyTableRowPopupListener());
        buyTreeTable.addKeyListener(new TableKeyEventListener());

        org.jdesktop.swingx.decorator.Highlighter highlighter0 = org.jdesktop.swingx.decorator.HighlighterFactory.createSimpleStriping(new Color(245, 245, 220));
        buyTreeTable.addHighlighter(highlighter0);
        buyTreeTable.addHighlighter(new ToolTipHighlighter());

        initTreeTableDefaultRenderer(buyTreeTable);

        // Not sure why. Without this code, sorting won't work just after you resize 
        // table header.
        JTableHeader oldBuyTableHeader = buyTreeTable.getTableHeader();
        JXTableHeader newBuyTableHeader = new JXTableHeader(oldBuyTableHeader.getColumnModel());
        buyTreeTable.setTableHeader(newBuyTableHeader);

        // We need to have a hack way, to have "Comment" in the model, but not visible to user.
        // So that our ToolTipHighlighter can work correctly.
        buyTreeTable.getTableHeader().addMouseListener(new TableColumnSelectionPopupListener(1, new String[]{GUIBundle.getString("PortfolioManagementJPanel_Comment")}));
        buyTreeTable.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                buyTreeTableValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(buyTreeTable);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PortfolioManagementJPanel_Sell"))); // NOI18N

        sellTreeTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        sellTreeTable.setRootVisible(true);
        // this must be before any sort instructions or get funny results
        sellTreeTable.setAutoCreateColumnsFromModel(false);

        sellTreeTable.addMouseListener(new SellTableRowPopupListener());
        sellTreeTable.addKeyListener(new TableKeyEventListener());

        org.jdesktop.swingx.decorator.Highlighter highlighter1 = org.jdesktop.swingx.decorator.HighlighterFactory.createSimpleStriping(new Color(245, 245, 220));
        sellTreeTable.addHighlighter(highlighter1);
        sellTreeTable.addHighlighter(new ToolTipHighlighter());

        initTreeTableDefaultRenderer(sellTreeTable);

        // Not sure why. Without this code, sorting won't work just after you resize 
        // table header.
        JTableHeader oldSellTableHeader = sellTreeTable.getTableHeader();
        JXTableHeader newSellTableHeader = new JXTableHeader(oldSellTableHeader.getColumnModel());
        sellTreeTable.setTableHeader(newSellTableHeader);

        // We need to have a hack way, to have "Comment" in the model, but not visible to user.
        // So that our ToolTipHighlighter can work correctly.
        sellTreeTable.getTableHeader().addMouseListener(new TableColumnSelectionPopupListener(1, new String[]{GUIBundle.getString("PortfolioManagementJPanel_Comment")}));
        sellTreeTable.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                sellTreeTableValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(sellTreeTable);

        jSplitPane1.setRightComponent(jScrollPane2);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/inbox.png"))); // NOI18N
        jButton1.setText(bundle.getString("PortfolioManagementJPanel_Buy...")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/outbox.png"))); // NOI18N
        jButton3.setText(bundle.getString("PortfolioManagementJPanel_Sell...")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/money.png"))); // NOI18N
        jButton4.setText(bundle.getString("PortfolioManagementJPanel_Cash...")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/money2.png"))); // NOI18N
        jButton5.setText(bundle.getString("PortfolioManagementJPanel_Dividen...")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton5);

        add(jPanel2, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        List<Stock> stocks = getSelectedStocks();
        if (stocks.size() == 1) {
            this.showNewBuyTransactionJDialog(stocks.get(0), this.getStockPrice(stocks.get(0).code), true);
        } else {
            this.showNewBuyTransactionJDialog(null, 0.0, true);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Define our own renderers, so that we may have a consistent decimal places.
    private void initTreeTableDefaultRenderer(JXTreeTable treeTable) {
        final TableCellRenderer doubleOldTableCellRenderer = treeTable.getDefaultRenderer(Double.class);

        treeTable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = doubleOldTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                final String UNITS = GUIBundle.getString("PortfolioManagementJPanel_Units");
                // Do not manipulate display for "Units". We do not want 100
                // "units" displayed as 100.00 "units".
                if (false == UNITS.equals(table.getColumnName(column))) {
                    if (value != null) {
                        if (c instanceof JLabel) {
                            // Re-define the displayed value.
                            
                            // Ugly hacking.
                            if (value instanceof DoubleWrapper) {
                                DoubleWrapper v = (DoubleWrapper)value;
                                ((JLabel)c).setText(org.yccheok.jstock.portfolio.Utils.toCurrency(v.decimalPlace, v.value));
                            } else {
                                ((JLabel)c).setText(org.yccheok.jstock.portfolio.Utils.toCurrency(DecimalPlaces.Four, value));
                            }
                        }
                    }
                } else {
                    if (value != null) {
                        if (c instanceof JLabel) {
                            ((JLabel)c).setText(org.yccheok.jstock.portfolio.Utils.toUnits(value));
                        }
                    }                    
                }
                return c;
            }
        });

        treeTable.setDefaultRenderer(SimpleDate.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);                
                if (value != null) {
                    if (c instanceof JLabel) {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        SimpleDate simpleDate = (SimpleDate)value;
                        ((JLabel)c).setText(dateFormat.format(simpleDate.getTime()));
                    }
                }
                return c;
            }
        });
    }

    private boolean isValidTreeTableNode(TreeTableModel treeTableModel, Object node) {
        boolean result = false;
        
        final Object root = treeTableModel.getRoot();
        
        if (node instanceof TreeTableNode) {
            TreeTableNode ttn = (TreeTableNode) node;

            while (!result && ttn != null) {
                result = ttn == root;

                ttn = ttn.getParent();
            }
        }

        return result;
    }

    private String getSelectedFirstColumnString(JXTreeTable treeTable) {
        final TreePath[] treePaths = treeTable.getTreeSelectionModel().getSelectionPaths();

        if (treePaths == null) {
            return null;
        }

        if (treePaths.length == 1) {
            return treePaths[0].getLastPathComponent().toString();
        }

        return null;
    }

    private Commentable getSelectedCommentable(JXTreeTable treeTable) {
        final TreePath[] treePaths = treeTable.getTreeSelectionModel().getSelectionPaths();

        if(treePaths == null) {
            return null;
        }

        if(treePaths.length == 1) {
            if(treePaths[0].getLastPathComponent() instanceof Commentable) {
                return (Commentable)treePaths[0].getLastPathComponent();
            }
        }

        return null;
    }

    public boolean openAsExcelFile(File file) {
        final java.util.List<Statements> statementsList = Statements.newInstanceFromExcelFile(file);
        boolean status = true;
        for (Statements statements : statementsList) {
            status = status & this.openAsStatements(statements, file);
        }
        return status;
    }

    public boolean openAsCSVFile(File file) {
        final Statements statements = Statements.newInstanceFromCSVFile(file);
        return this.openAsStatements(statements, file);
    }

    public boolean openAsStatements(Statements statements, File file) {
        assert(statements != null);
        
        if (statements.getType() == Statement.Type.PortfolioManagementBuy || statements.getType() == Statement.Type.PortfolioManagementSell || statements.getType() == Statement.Type.PortfolioManagementDeposit || statements.getType() == Statement.Type.PortfolioManagementDividend) {
            final GUIBundleWrapper guiBundleWrapper = statements.getGUIBundleWrapper();
            // We will use a fixed date format (Locale.English), so that it will be
            // easier for Android to process.
            //
            // "Sep 5, 2011"    -   Locale.ENGLISH
            // "2011-9-5"       -   Locale.SIMPLIFIED_CHINESE
            // "2011/9/5"       -   Locale.TRADITIONAL_CHINESE
            // 05.09.2011       -   Locale.GERMAN
            //
            // However, for backward compatible purpose (Able to read old CSV),
            // we perform a for loop to determine the best date format.
            DateFormat dateFormat = null;
            final int size = statements.size();
            switch(statements.getType()) {
                case PortfolioManagementBuy:
                {
                    final List<Transaction> transactions = new ArrayList<Transaction>();

                    for (int i = 0; i < size; i++) {
                        final Statement statement = statements.get(i);
                        final String _code = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Code"));
                        final String _symbol = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Symbol"));
                        final String _date = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
                        final Double units = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_Units"));
                        final Double purchasePrice = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice"));
                        final Double broker = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker"));
                        final Double clearingFee = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee"));
                        final Double stampDuty = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty"));
                        final String _comment = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));

                        Stock stock = null;
                        if (_code.length() > 0 && _symbol.length() > 0) {
                            stock = org.yccheok.jstock.engine.Utils.getEmptyStock(Code.newInstance(_code), Symbol.newInstance(_symbol));
                        }
                        else {
                            log.error("Unexpected empty stock. Ignore");
                            // stock is null.
                            continue;
                        }
                        Date date = null;

                        if (dateFormat == null) {
                            // However, for backward compatible purpose (Able to read old CSV),
                            // we perform a for loop to determine the best date format.
                            // For the latest CSV, it should be Locale.ENGLISH.
                            Locale[] locales = {Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE, Locale.GERMAN, Locale.TRADITIONAL_CHINESE, Locale.ITALIAN};
                            for (Locale locale : locales) {
                                dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                                try {
                                    date = dateFormat.parse((String)_date);
                                } catch (ParseException exp) {
                                    log.error(null, exp);
                                    date = null;
                                    dateFormat = null;
                                    continue;
                                }
                                // We had found our best dateFormat. Early break.
                                break;
                            }
                        } else {
                            // We already determine our best dateFormat.
                            try {
                                date = dateFormat.parse((String)_date);
                            } catch (ParseException exp) {
                                log.error(null, exp);
                            }
                        }

                        if (date == null) {
                            log.error("Unexpected wrong date. Ignore");
                            continue;
                        }

                        // Shall we continue to ignore, or shall we just return false to
                        // flag an error?
                        if (units == null) {
                            log.error("Unexpected wrong units. Ignore");
                            continue;
                        }
                        if (purchasePrice == null || broker == null || clearingFee == null || stampDuty == null) {
                            log.error("Unexpected wrong purchasePrice/broker/clearingFee/stampDuty. Ignore");
                            continue;
                        }

                        final SimpleDate simpleDate = new SimpleDate(date);
                        final Contract.Type type = Contract.Type.Buy;
                        final Contract.ContractBuilder builder = new Contract.ContractBuilder(stock, simpleDate);
                        final Contract contract = builder.type(type).quantity(units).price(purchasePrice).build();
                        final Transaction t = new Transaction(contract, broker, stampDuty, clearingFee);
                        t.setComment(org.yccheok.jstock.portfolio.Utils.replaceCSVLineFeedToSystemLineFeed(_comment));
                        transactions.add(t);
                    }

                    // We allow empty portfolio.
                    //if (transactions.size() <= 0) {
                    //    return false;
                    //}

                    // Is there any exsiting displayed data?
                    if (this.getBuyTransactionSize() > 0) {
                        final String output = MessageFormat.format(MessagesBundle.getString("question_message_load_file_for_buy_portfolio_template"), file.getName());
                        final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.getInstance(), output, MessagesBundle.getString("question_title_load_file_for_buy_portfolio"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                        if (result != javax.swing.JOptionPane.YES_OPTION) {
                            // Assume success.
                            return true;
                        }
                    }
                    this.buyTreeTable.setTreeTableModel(new BuyPortfolioTreeTableModelEx());
                    
                    Map<String, String> metadatas = statements.getMetadatas();
                    for (Transaction transaction : transactions) {
                        final Code code = transaction.getStock().code;
                        TransactionSummary transactionSummary = this.addBuyTransaction(transaction);
                        if (transactionSummary != null) {
                            String comment = metadatas.get(code.toString());
                            if (comment != null) {
                                transactionSummary.setComment(org.yccheok.jstock.portfolio.Utils.replaceCSVLineFeedToSystemLineFeed(comment));
                            }
                        }
                    }

                    // Only shows necessary columns.
                    initGUIOptions();

                    expandTreeTable(this.buyTreeTable);
                    
                    updateRealTimeStockMonitorAccordingToBuyPortfolioTreeTableModel();                                        
                }
                break;

                case PortfolioManagementSell:
                {
                    final List<Transaction> transactions = new ArrayList<Transaction>();

                    for (int i = 0; i < size; i++) {
                        final Statement statement = statements.get(i);
                        final String _code = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Code"));
                        final String _symbol = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Symbol"));
                        final String _referenceDate = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate"));
                        
                        // Legacy file handling. PortfolioManagementJPanel_PurchaseBroker, PortfolioManagementJPanel_PurchaseClearingFee,
                        // and PortfolioManagementJPanel_PurchaseStampDuty are introduced starting from 1.0.6x
                        Double purchaseBroker = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseBroker"));
                        if (purchaseBroker == null) {
                            // Legacy file handling. PortfolioManagementJPanel_PurchaseFee is introduced starting from 1.0.6s
                            purchaseBroker = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseFee"));
                            if (purchaseBroker == null) {
                                purchaseBroker = new Double(0.0);
                            }
                        }
                        Double purchaseClearingFee = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseClearingFee"));
                        if (purchaseClearingFee == null) {
                            purchaseClearingFee = new Double(0.0);                            
                        }
                        Double purchaseStampDuty = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseStampDuty"));
                        if (purchaseStampDuty == null) {
                            purchaseStampDuty = new Double(0.0);                            

                        }                        
                        final String _date = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
                        final Double units = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_Units"));
                        final Double sellingPrice =  statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_SellingPrice"));
                        final Double purchasePrice = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice"));
                        final Double broker = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_Broker"));
                        final Double clearingFee = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee"));
                        final Double stampDuty = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty"));
                        final String _comment = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));

                        Stock stock = null;
                        if (_code.length() > 0 && _symbol.length() > 0) {
                            stock = org.yccheok.jstock.engine.Utils.getEmptyStock(Code.newInstance(_code), Symbol.newInstance(_symbol));
                        }
                        else {
                            log.error("Unexpected empty stock. Ignore");
                            // stock is null.
                            continue;
                        }

                        Date date = null;
                        Date referenceDate = null;

                        if (dateFormat == null) {
                            // However, for backward compatible purpose (Able to read old CSV),
                            // we perform a for loop to determine the best date format.
                            // For the latest CSV, it should be Locale.ENGLISH.
                            Locale[] locales = {Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE, Locale.GERMAN, Locale.TRADITIONAL_CHINESE, Locale.ITALIAN};
                            for (Locale locale : locales) {
                                dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                                try {
                                    date = dateFormat.parse((String)_date);
                                    referenceDate = dateFormat.parse((String)_referenceDate);
                                } catch (ParseException exp) {
                                    log.error(null, exp);
                                    date = null;
                                    referenceDate = null;
                                    dateFormat = null;
                                    continue;
                                }
                                // We had found our best dateFormat. Early break.
                                break;
                            }
                        } else {
                            // We already determine our best dateFormat.
                            try {
                                date = dateFormat.parse((String)_date);
                                referenceDate = dateFormat.parse((String)_referenceDate);
                            } catch (ParseException exp) {
                                log.error(null, exp);
                            }
                        }

                        if (date == null || referenceDate == null) {
                            log.error("Unexpected wrong date/referenceDate. Ignore");
                            continue;
                        }
                        // Shall we continue to ignore, or shall we just return false to
                        // flag an error?
                        if (units == null) {
                            log.error("Unexpected wrong units. Ignore");
                            continue;
                        }
                        if (purchasePrice == null || broker == null || clearingFee == null || stampDuty == null || sellingPrice == null) {
                            log.error("Unexpected wrong purchasePrice/broker/clearingFee/stampDuty/sellingPrice. Ignore");
                            continue;
                        }
                        
                        final SimpleDate simpleDate = new SimpleDate(date);
                        final SimpleDate simpleReferenceDate = new SimpleDate(referenceDate);
                        final Contract.Type type = Contract.Type.Sell;
                        final Contract.ContractBuilder builder = new Contract.ContractBuilder(stock, simpleDate);
                        final Contract contract = builder.type(type).quantity(units).price(sellingPrice).referencePrice(purchasePrice).referenceDate(simpleReferenceDate)
                                .referenceBroker(purchaseBroker)
                                .referenceClearingFee(purchaseClearingFee)
                                .referenceStampDuty(purchaseStampDuty)
                                .build();
                        final Transaction t = new Transaction(contract, broker, stampDuty, clearingFee);
                        t.setComment(org.yccheok.jstock.portfolio.Utils.replaceCSVLineFeedToSystemLineFeed(_comment));
                        transactions.add(t);
                    }   // for
                    
                    // We allow empty portfolio.
                    //if (transactions.size() <= 0) {
                    //    return false;
                    //}

                    // Is there any exsiting displayed data?
                    if (this.getSellTransactionSize() > 0) {
                        final String output = MessageFormat.format(MessagesBundle.getString("question_message_load_file_for_sell_portfolio_template"), file.getName());
                        final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.getInstance(), output, MessagesBundle.getString("question_title_load_file_for_sell_portfolio"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                        if (result != javax.swing.JOptionPane.YES_OPTION) {
                            // Assume success.
                            return true;
                        }
                    }
                    this.sellTreeTable.setTreeTableModel(new SellPortfolioTreeTableModelEx());                    
                    
                    Map<String, String> metadatas = statements.getMetadatas();

                    for (Transaction transaction : transactions) {
                        final Code code = transaction.getStock().code;
                        TransactionSummary transactionSummary = this.addSellTransaction(transaction);
                        if (transactionSummary != null) {
                            String comment = metadatas.get(code.toString());
                            if (comment != null) {
                                transactionSummary.setComment(org.yccheok.jstock.portfolio.Utils.replaceCSVLineFeedToSystemLineFeed(comment));
                            }
                        }
                    }

                    // Only shows necessary columns.
                    initGUIOptions();
                    
                    expandTreeTable(this.sellTreeTable);
                }
                break;

                case PortfolioManagementDeposit:
                {
                    final List<Deposit> deposits = new ArrayList<Deposit>();

                    for (int i = 0; i < size; i++) {
                        Date date = null;
                        final Statement statement = statements.get(i);
                        final String object0 = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
                        assert(object0 != null);

                        if (dateFormat == null) {
                            // However, for backward compatible purpose (Able to read old CSV),
                            // we will perform a for loop to determine the best date format.
                            // For the latest CSV, it should be Locale.ENGLISH.
                            Locale[] locales = {Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE, Locale.GERMAN, Locale.TRADITIONAL_CHINESE, Locale.ITALIAN};
                            for (Locale locale : locales) {
                                dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                                try {
                                    date = dateFormat.parse(object0);
                                } catch (ParseException exp) {
                                    log.error(null, exp);
                                    date = null;
                                    dateFormat = null;
                                    continue;
                                }
                                // We had found our best dateFormat. Early break.
                                break;
                            }
                        } else {
                            // We already determine our best dateFormat.
                            try {
                                date = dateFormat.parse(object0);
                            } catch (ParseException exp) {
                                log.error(null, exp);
                            }
                        }

                        // Shall we continue to ignore, or shall we just return false to
                        // flag an error?
                        if (date == null) {
                            log.error("Unexpected wrong date. Ignore");
                            continue;
                        }
                        final Double cash = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_Cash"));
                        // Shall we continue to ignore, or shall we just return false to
                        // flag an error?
                        if (cash == null) {
                            log.error("Unexpected wrong cash. Ignore");
                            continue;
                        }
                        final Deposit deposit = new Deposit(cash, new SimpleDate(date));
                        
                        final String comment = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));
                        if (comment != null) {
                            // Possible to be null. As in version <=1.0.6p, comment
                            // is not being saved to CSV.
                            deposit.setComment(org.yccheok.jstock.portfolio.Utils.replaceCSVLineFeedToSystemLineFeed(comment));
                        }

                        deposits.add(deposit);
                    }

                    // We allow empty portfolio.
                    //if (deposits.size() <= 0) {
                    //    return false;
                    //}

                    // Is there any exsiting displayed data?
                    if (this.depositSummary.size() > 0) {
                        final String output = MessageFormat.format(MessagesBundle.getString("question_message_load_file_for_cash_deposit_template"), file.getName());
                        final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.getInstance(), output, MessagesBundle.getString("question_title_load_file_for_cash_deposit"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                        if (result != javax.swing.JOptionPane.YES_OPTION) {
                            // Assume success.
                            return true;
                        }                        
                    }

                    this.depositSummary = new DepositSummary();
                    
                    for (Deposit deposit : deposits) {
                        depositSummary.add(deposit);
                    }
                }
                break;

                case PortfolioManagementDividend:
                {
                    final List<Dividend> dividends = new ArrayList<Dividend>();

                    for (int i = 0; i < size; i++) {
                        Date date = null;
                        StockInfo stockInfo = null;
                        final Statement statement = statements.get(i);
                        final String object0 = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Date"));
                        assert(object0 != null);
                        
                        if (dateFormat == null) {
                            // However, for backward compatible purpose (Able to read old CSV),
                            // we will perform a for loop to determine the best date format.
                            // For the latest CSV, it should be Locale.ENGLISH.
                            Locale[] locales = {Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE, Locale.GERMAN, Locale.TRADITIONAL_CHINESE, Locale.ITALIAN};
                            for (Locale locale : locales) {
                                dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                                try {
                                    date = dateFormat.parse(object0);
                                } catch (ParseException exp) {
                                    log.error(null, exp);
                                    date = null;
                                    dateFormat = null;
                                    continue;
                                }
                                // We had found our best dateFormat. Early break.
                                break;
                            }
                        } else {
                            // We already determine our best dateFormat.
                            try {
                                date = dateFormat.parse(object0);
                            } catch (ParseException exp) {
                                log.error(null, exp);
                            }
                        }

                        // Shall we continue to ignore, or shall we just return false to
                        // flag an error?
                        if (date == null) {
                            log.error("Unexpected wrong date. Ignore");
                            continue;
                        }
                        final Double dividend = statement.getValueAsDouble(guiBundleWrapper.getString("PortfolioManagementJPanel_Dividend"));
                        // Shall we continue to ignore, or shall we just return false to
                        // flag an error?
                        if (dividend == null) {
                            log.error("Unexpected wrong dividend. Ignore");
                            continue;
                        }
                        final String codeStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Code"));
                        final String symbolStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Symbol"));
                        if (codeStr.isEmpty() == false && symbolStr.isEmpty() == false) {
                            stockInfo = StockInfo.newInstance(Code.newInstance(codeStr), Symbol.newInstance(symbolStr));
                        } else {
                            log.error("Unexpected wrong stock. Ignore");
                            // stock is null.
                            continue;
                        }
                        
                        assert(stockInfo != null);
                        assert(dividend != null);
                        assert(date != null);
                        
                        final Dividend d = new Dividend(stockInfo, dividend, new SimpleDate(date));
                        
                        final String comment = statement.getValueAsString(guiBundleWrapper.getString("PortfolioManagementJPanel_Comment"));
                        if (comment != null) {
                            // Possible to be null. As in version <=1.0.6p, comment
                            // is not being saved to CSV.
                            d.setComment(org.yccheok.jstock.portfolio.Utils.replaceCSVLineFeedToSystemLineFeed(comment));
                        }
                        
                        dividends.add(d);
                    }

                    // We allow empty portfolio.
                    //if (dividends.size() <= 0) {
                    //    return false;
                    //}                    

                    if (this.dividendSummary.size() > 0) {
                        final String output = MessageFormat.format(MessagesBundle.getString("question_message_load_file_for_dividend_template"), file.getName());
                        final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.getInstance(), output, MessagesBundle.getString("question_title_load_file_for_dividend"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                        if (result != javax.swing.JOptionPane.YES_OPTION) {
                            // Assume success.
                            return true;
                        }                        
                    }

                    this.dividendSummary = new DividendSummary();
                    
                    for (Dividend dividend : dividends) {
                        dividendSummary.add(dividend);
                    }
                }
                break;

                default:
                    assert(false);
            }
            this.updateWealthHeader();
        }
        else if (statements.getType() == Statement.Type.RealtimeInfo) {
            /* Open using other tabs. */
            return JStock.getInstance().openAsStatements(statements, file);
        }
        else {
            return false;
        }
        return true;
    }

    private List<Stock> getSelectedStocks() {
        List<Stock> stocks0 = this.getSelectedStocks(buyTreeTable);
        List<Stock> stocks1 = this.getSelectedStocks(sellTreeTable);
        Set<Code> c = new HashSet<Code>();
        List<Stock> stocks = new ArrayList<Stock>();

        for (Stock stock : stocks0) {
            if (c.contains(stock.code) == false) {
                c.add(stock.code);
                stocks.add(stock);
            }
        }

        for (Stock stock : stocks1) {
            if (c.contains(stock.code) == false) {
                c.add(stock.code);
                stocks.add(stock);
            }
        }

        return Collections.unmodifiableList(stocks);
    }
    
    public double getStockPrice(Code code) {
        final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        return buyPortfolioTreeTableModel.getStockPrice(code);
    }

    private void showNewSellTransactionJDialog(List<Transaction> buyTransactions) {
        final JStock mainFrame = JStock.getInstance();        
        NewSellTransactionJDialog newSellTransactionJDialog = new NewSellTransactionJDialog(mainFrame, true);
        if (buyTransactions.size() > 1) {
            final String template = GUIBundle.getString("PortfolioManagementJPanel_BatchSell_template");
            newSellTransactionJDialog.setTitle(MessageFormat.format(template, newSellTransactionJDialog.getTitle(), buyTransactions.size()));
        }
        newSellTransactionJDialog.setLocationRelativeTo(this);
        newSellTransactionJDialog.setBuyTransactions(buyTransactions);       
        newSellTransactionJDialog.setVisible(true);
        
        final List<Transaction> newSellTransactions = newSellTransactionJDialog.getTransactions();
        
        for (int i = 0; i < newSellTransactions.size(); i++) {
            Transaction newSellTransaction = newSellTransactions.get(i);
            Transaction buyTransaction = buyTransactions.get(i);

            final double remain = buyTransaction.getQuantity() - newSellTransaction.getQuantity();
            
            assert(remain >= 0);
            
            addSellTransaction(newSellTransaction);
            
            final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
            
            if (remain <= 0) {
                portfolioTreeTableModel.removeTransaction(buyTransaction);
            } else {
                final double newBroker = buyTransaction.getBroker() - newSellTransaction.getReferenceBroker();
                final double newStampDuty = buyTransaction.getStampDuty() - newSellTransaction.getReferenceStampDuty();
                final double newClearingFee = buyTransaction.getClearingFee() - newSellTransaction.getReferenceClearingFee();
                
                this.editBuyTransaction(
                        buyTransaction.deriveWithQuantity(remain).deriveWithBroker(newBroker).deriveWithStampDuty(newStampDuty).deriveWithClearingFee(newClearingFee), 
                        buyTransaction);
            }                        
        }

        updateWealthHeader();
    }
    
    private void showEditTransactionJDialog(Transaction transaction) {
        final JStock mainFrame = JStock.getInstance();

        if (transaction.getType() == Contract.Type.Buy) {
            NewBuyTransactionJDialog newTransactionJDialog = new NewBuyTransactionJDialog(mainFrame, true);
            newTransactionJDialog.setStockSelectionEnabled(false);
            newTransactionJDialog.setTransaction(transaction);
            final String template = GUIBundle.getString("PortfolioManagementJPanel_EditBuy_template");
            newTransactionJDialog.setTitle(MessageFormat.format(template, transaction.getStock().symbol));
            newTransactionJDialog.setLocationRelativeTo(this);
            newTransactionJDialog.setVisible(true);

            final Transaction newTransaction = newTransactionJDialog.getTransaction();
            if (newTransaction != null) {
                this.editBuyTransaction(newTransaction, transaction);
                updateWealthHeader();
            }        
        }
        else {
            assert(transaction.getType() == Contract.Type.Sell);
            
            NewSellTransactionJDialog newTransactionJDialog = new NewSellTransactionJDialog(mainFrame, true);
            newTransactionJDialog.setSellTransaction(transaction);
            final String template = GUIBundle.getString("PortfolioManagementJPanel_EditSell_template");
            newTransactionJDialog.setTitle(MessageFormat.format(template, transaction.getStock().symbol));
            newTransactionJDialog.setLocationRelativeTo(this);
            newTransactionJDialog.setVisible(true);

            List<Transaction> transactions = newTransactionJDialog.getTransactions();
            for (Transaction newTransaction : transactions) {
                this.editSellTransaction(newTransaction, transaction);
                updateWealthHeader();
            }                    
        }
    }
    
    public void showNewBuyTransactionJDialog(Stock stock, double lastPrice, boolean JComboBoxEnabled) {

        final JStock mainFrame = JStock.getInstance();

        final StockInfoDatabase stockInfoDatabase = mainFrame.getStockInfoDatabase();
        
        if (stockInfoDatabase == null) {
            javax.swing.JOptionPane.showMessageDialog(this, MessagesBundle.getString("info_message_we_havent_connected_to_stock_server"), MessagesBundle.getString("info_title_we_havent_connected_to_stock_server"), javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        NewBuyTransactionJDialog newTransactionJDialog = new NewBuyTransactionJDialog(mainFrame, true);
        newTransactionJDialog.setLocationRelativeTo(this);
        newTransactionJDialog.setStock(stock);
        newTransactionJDialog.setPrice(lastPrice);
        newTransactionJDialog.setJComboBoxEnabled(JComboBoxEnabled);
        newTransactionJDialog.setStockInfoDatabase(stockInfoDatabase);

        // If we are not in portfolio page, we shall provide user a hint, so that
        // user will know this transaction will go into which portfolio, without
        // having to click on the Portfolio drop-down menu.
        if (mainFrame.getSelectedComponent() != this) {
            final JStockOptions jStockOptions = mainFrame.getJStockOptions();
            final String title = newTransactionJDialog.getTitle() + " (" + jStockOptions.getPortfolioName() + ")";
            newTransactionJDialog.setTitle(title);
        }

        newTransactionJDialog.setVisible(true);
        
        final Transaction transaction = newTransactionJDialog.getTransaction();
        if (transaction != null) {
            this.addBuyTransaction(transaction);
            updateWealthHeader();
        }
    }
    
    public void clearTableSelection() {
        buyTreeTable.getSelectionModel().clearSelection();
        sellTreeTable.getSelectionModel().clearSelection();
    }
    
    private void deleteSelectedTreeTableRow(org.jdesktop.swingx.JXTreeTable treeTable) {
        final AbstractPortfolioTreeTableModelEx portfolioTreeTableModel = (AbstractPortfolioTreeTableModelEx)treeTable.getTreeTableModel();
        final TreePath[] treePaths = treeTable.getTreeSelectionModel().getSelectionPaths();
        
        if (treePaths == null) {
            return;
        }
        
        for (TreePath treePath : treePaths) {
            final Object o = treePath.getLastPathComponent();
            if (portfolioTreeTableModel.getRoot() == o) {
                continue;
            }
            final MutableTreeTableNode mutableTreeTableNode = (MutableTreeTableNode)o;
            if (isValidTreeTableNode(portfolioTreeTableModel, mutableTreeTableNode) == false) {
                //???
                portfolioTreeTableModel.fireTreeTableNodeChanged(mutableTreeTableNode);
                continue;
            }
                        
            if (o instanceof Transaction) {
                portfolioTreeTableModel.removeTransaction((Transaction)o);                
            }
            else if (o instanceof TransactionSummary) {
                portfolioTreeTableModel.removeTransactionSummary((TransactionSummary)o);
            }
        }        
    }
    
    private void deteleSelectedTreeTableRow() {
        deleteSelectedTreeTableRow(this.buyTreeTable);
        deleteSelectedTreeTableRow(this.sellTreeTable);
        
        updateWealthHeader();
    }
    
    private void buyTreeTableValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_buyTreeTableValueChanged
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (buyTreeTable.getSelectedRowCount() > 0) {
                    sellTreeTable.clearSelection();
                }
            }
        });
    }//GEN-LAST:event_buyTreeTableValueChanged

    private void sellTreeTableValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_sellTreeTableValueChanged
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (sellTreeTable.getSelectedRowCount() > 0) {
                    buyTreeTable.clearSelection();
                }
            }
        });
    }//GEN-LAST:event_sellTreeTableValueChanged

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        this.clearTableSelection();
    }//GEN-LAST:event_formMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        final List<Stock> stocks = this.getSelectedStocks(buyTreeTable);
        if (stocks.size() != 1) {
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("info_message_you_need_to_select_only_single_stock_from_buy_portfolio_to_perform_sell_transaction"), java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/messages").getString("info_title_you_need_to_select_only_single_stock_from_buy_portfolio_to_perform_sell_transaction"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Transaction> transactions = this.getSelectedTransactions(buyTreeTable);
        this.showNewSellTransactionJDialog(transactions);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        showDepositSummaryJDialog();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        showDividendSummaryJDialog();
    }//GEN-LAST:event_jButton5ActionPerformed

    // When transaction summary being selected, we assume all its transactions are being selected.
    // This is most of the users intention too, I guess.
    private List<Transaction> getSelectedTransactions(JXTreeTable treeTable) {
        final TreePath[] treePaths = treeTable.getTreeSelectionModel().getSelectionPaths();
        List<Transaction> transactions = new ArrayList<Transaction>();

        if(treePaths == null) {
            return Collections.unmodifiableList(transactions);
        }

        for (TreePath treePath : treePaths) {
            final Object o = treePath.getLastPathComponent();
            if (o instanceof Transaction) {
                final Transaction transaction = (Transaction)o;

                if (transactions.contains(transaction) == false) {
                    transactions.add(transaction);
                }
            }
            else if (o instanceof TransactionSummary) {
                final TransactionSummary transactionSummary = (TransactionSummary)o;
                final int count = transactionSummary.getChildCount();
                for (int i = 0; i < count; i++) {
                    final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);

                    if (transactions.contains(transaction) == false) {
                        transactions.add(transaction);
                    }
                }
            }
        }
        
        return Collections.unmodifiableList(transactions);
    }
    
    private boolean isOnlyTreeTableRootBeingSelected(JXTreeTable treeTable) {
        if(treeTable.getSelectedRowCount() != 1) return false;
        
        final TreePath[] treePaths = treeTable.getTreeSelectionModel().getSelectionPaths();
        
        final Object o = treePaths[0].getLastPathComponent();

        final AbstractPortfolioTreeTableModelEx portfolioTreeTableModel = (AbstractPortfolioTreeTableModelEx)treeTable.getTreeTableModel();
        
        return (portfolioTreeTableModel.getRoot() == o);
    }

    private void showDividendSummaryJDialog() {
        final JStock mainFrame = JStock.getInstance();
        DividendSummaryJDialog dividendSummaryJDialog = new DividendSummaryJDialog(mainFrame, true, this.getDividendSummary(), this);
        dividendSummaryJDialog.setLocationRelativeTo(this);

        List<Stock> stocks = this.getSelectedStocks();
        if (stocks.size() == 1) {
            dividendSummaryJDialog.addNewDividend(org.yccheok.jstock.engine.StockInfo.newInstance(stocks.get(0)));
        }
        dividendSummaryJDialog.setVisible(true);

        final DividendSummary _dividendSummary = dividendSummaryJDialog.getDividendSummaryAfterPressingOK();
        if (_dividendSummary != null) {
            this.dividendSummary = _dividendSummary;
            updateWealthHeader();
        }
    }

    private void showSplitOrMergeJDialog(StockInfo stockInfo) {
        final JStock mainFrame = JStock.getInstance();
        SplitJDialog splitOrMergeJDialog = new SplitJDialog(mainFrame, true, stockInfo);
        splitOrMergeJDialog.pack();
        splitOrMergeJDialog.setLocationRelativeTo(this);
        splitOrMergeJDialog.setVisible(true);

        if (splitOrMergeJDialog.getRatio() == null) {
            return;
        }

        double ratio = splitOrMergeJDialog.getRatio();
        final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        // Perform splitting. (Or merging)
        portfolioTreeTableModel.split(stockInfo, ratio);
        // Update the wealth. The value of wealth shall remain unchanged.
        this.updateWealthHeader();
    }

    private void showDepositSummaryJDialog() {
        final JStock mainFrame = JStock.getInstance();
        DepositSummaryJDialog depositSummaryJDialog = new DepositSummaryJDialog(mainFrame, true, this.getDepositSummary());
        depositSummaryJDialog.setLocationRelativeTo(this);
        depositSummaryJDialog.setVisible(true);

        final DepositSummary _depositSummary = depositSummaryJDialog.getDepositSummaryAfterPressingOK();
        if (_depositSummary != null) {
            this.depositSummary = _depositSummary;
            updateWealthHeader();
        }
    }
    
    private void showCommentJDialog(Commentable commentable, String title) {
        if (commentable == null) {
            // Nothing to be shown.
            return;
        }

        final JStock mainFrame = JStock.getInstance();
        CommentJDialog commentJDialog = new CommentJDialog(mainFrame, true, commentable);
        commentJDialog.setTitle(title);
        commentJDialog.setLocationRelativeTo(this);
        commentJDialog.setVisible(true);
    }

    /**
     * @return the depositSummary
     */
    public DepositSummary getDepositSummary() {
        return depositSummary;
    }

    /**
     * @return the dividendSummary
     */
    public DividendSummary getDividendSummary() {
        return dividendSummary;
    }

    private class TableKeyEventListener extends java.awt.event.KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent e) {
            PortfolioManagementJPanel.this.clearTableSelection();
        }
    }

    private class BuyTableRowPopupListener extends MouseAdapter {
        
        @Override
        public void mouseClicked(MouseEvent evt) {
            if(evt.getClickCount() == 2) {
                final List<Transaction> transactions = getSelectedTransactions(buyTreeTable);
                if (transactions.size() == 1) {
                    PortfolioManagementJPanel.this.showEditTransactionJDialog(transactions.get(0));
                }
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
                getBuyTreeTablePopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private class SellTableRowPopupListener extends MouseAdapter {
        
        @Override
        public void mouseClicked(MouseEvent evt) {
            if(evt.getClickCount() == 2) {
                final List<Transaction> transactions = getSelectedTransactions(sellTreeTable);
                if (transactions.size() == 1) {
                    PortfolioManagementJPanel.this.showEditTransactionJDialog(transactions.get(0));
                }
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
                final JPopupMenu popupMenu = getSellTreeTablePopupMenu();
                if(popupMenu != null)
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    private ImageIcon getImageIcon(String imageIcon) {
        return new javax.swing.ImageIcon(getClass().getResource(imageIcon));
    }
    
    private void showBuyPortfolioChartJDialog() {
        final JStock m = JStock.getInstance();
        final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        BuyPortfolioChartJDialog buyPortfolioChartJDialog = new BuyPortfolioChartJDialog(m, false, buyPortfolioTreeTableModel, this.getDividendSummary());
        buyPortfolioChartJDialog.setVisible(true);                                    
    }

    private void showChashFlowChartJDialog() {
        final JStock m = JStock.getInstance();
        InvestmentFlowChartJDialog cashFlowChartJDialog = new InvestmentFlowChartJDialog(m, false, this);
        cashFlowChartJDialog.setVisible(true);
    }

    private void showSellPortfolioChartJDialog() {
        final JStock m = JStock.getInstance();
        final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel = (SellPortfolioTreeTableModelEx)sellTreeTable.getTreeTableModel();
        SellPortfolioChartJDialog sellPortfolioChartJDialog = new SellPortfolioChartJDialog(m, false, sellPortfolioTreeTableModel, this.getDividendSummary());
        sellPortfolioChartJDialog.setVisible(true);                                    
    }
    
    private JPopupMenu getSellTreeTablePopupMenu() {                
        final List<Transaction> transactions = getSelectedTransactions(this.sellTreeTable);

        JPopupMenu popup = new JPopupMenu();

        JMenuItem menuItem = null;

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagementJPanel_Cash..."), this.getImageIcon("/images/16x16/money.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showDepositSummaryJDialog();
            }
        });

        popup.add(menuItem);

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Dividend..."), this.getImageIcon("/images/16x16/money2.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showDividendSummaryJDialog();
            }
        });

        popup.add(menuItem);

        popup.addSeparator();
        
        if (transactions.size() == 1) {
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Edit..."), this.getImageIcon("/images/16x16/edit.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    PortfolioManagementJPanel.this.showEditTransactionJDialog(transactions.get(0));
                }
            });            

            popup.add(menuItem);
        }

        final Commentable commentable = getSelectedCommentable(this.sellTreeTable);
        final String tmp = getSelectedFirstColumnString(this.sellTreeTable);
        if (commentable != null && tmp != null) {
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Note..."), this.getImageIcon("/images/16x16/sticky.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    final String template = GUIBundle.getString("PortfolioManagementJPanel_NoteFor_template");
                    final String title = MessageFormat.format(template, tmp);
                    PortfolioManagementJPanel.this.showCommentJDialog(commentable, title);
                }
            });

            popup.add(menuItem);

            popup.addSeparator();
        }
        else if (transactions.size() == 1) {
            popup.addSeparator();
        }

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_InvestmentChart..."), this.getImageIcon("/images/16x16/graph.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showChashFlowChartJDialog();
            }
        });

        popup.add(menuItem);

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagementJPanel_DividendChart"), this.getImageIcon("/images/16x16/chart.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PortfolioManagementJPanel.this.showDividendSummaryBarChartJDialog();
            }
        });

        popup.add(menuItem);

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Summary..."), this.getImageIcon("/images/16x16/pie_chart.png"));
        
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PortfolioManagementJPanel.this.showSellPortfolioChartJDialog();
            }
        });

        popup.add(menuItem);        
        
        if(isOnlyTreeTableRootBeingSelected(sellTreeTable) == false && (sellTreeTable.getSelectedRow() > 0)) {
            final JStock m = JStock.getInstance();
                                
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_History..."), this.getImageIcon("/images/16x16/strokedocker.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    List<Stock> stocks = getSelectedStocks(sellTreeTable);

                    for(Stock stock : stocks) {
                        m.displayHistoryChart(stock);
                    }
                }
            });
                        
            popup.add(menuItem);
            popup.addSeparator();
            
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Delete"), this.getImageIcon("/images/16x16/editdelete.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    PortfolioManagementJPanel.this.deteleSelectedTreeTableRow();
                }
            });

            popup.add(menuItem);
        }
        
        return popup;
    }

    private void showDividendSummaryBarChartJDialog() {
        final JStock m = JStock.getInstance();
        final DividendSummaryBarChartJDialog dividendSummaryBarChartJDialog = new DividendSummaryBarChartJDialog(m, false, this.getDividendSummary());
        dividendSummaryBarChartJDialog.setVisible(true);
    }

    private JPopupMenu getBuyTreeTablePopupMenu() {                
        JPopupMenu popup = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Buy..."), this.getImageIcon("/images/16x16/inbox.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                List<Stock> stocks = getSelectedStocks();
                if (stocks.size() == 1) {
                    PortfolioManagementJPanel.this.showNewBuyTransactionJDialog(stocks.get(0), PortfolioManagementJPanel.this.getStockPrice(stocks.get(0).code), true);
                }
                else {
                    PortfolioManagementJPanel.this.showNewBuyTransactionJDialog(org.yccheok.jstock.engine.Utils.getEmptyStock(Code.newInstance(""), Symbol.newInstance("")), 0.0, true);
                }
            }
        });

        popup.add(menuItem);

        final List<Transaction> transactions = getSelectedTransactions(this.buyTreeTable);
        final List<Stock> stocks = this.getSelectedStocks(this.buyTreeTable);

        if (transactions.size() > 0 && stocks.size() == 1) {
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Sell..."), this.getImageIcon("/images/16x16/outbox.png"));
            
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    PortfolioManagementJPanel.this.showNewSellTransactionJDialog(transactions);
                }
            });            
            
            popup.add(menuItem);  
        }       

        popup.addSeparator();

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagementJPanel_Cash..."), this.getImageIcon("/images/16x16/money.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showDepositSummaryJDialog();
            }
        });

        popup.add(menuItem);

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Dividend..."), this.getImageIcon("/images/16x16/money2.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showDividendSummaryJDialog();
            }
        });

        popup.add(menuItem);

        popup.addSeparator();

        boolean needToAddSeperator = false;

        if (transactions.size() == 1) {
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Edit..."), this.getImageIcon("/images/16x16/edit.png"));
            
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    PortfolioManagementJPanel.this.showEditTransactionJDialog(transactions.get(0));
                }
            });            
            
            popup.add(menuItem);
            needToAddSeperator = true;
        }       

        final Commentable commentable = getSelectedCommentable(this.buyTreeTable);
        final String tmp = getSelectedFirstColumnString(this.buyTreeTable);
        if (commentable != null && tmp != null) {
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Note..."), this.getImageIcon("/images/16x16/sticky.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    final String template = GUIBundle.getString("PortfolioManagementJPanel_NoteFor_template");
                    final String title = MessageFormat.format(template, tmp);
                    PortfolioManagementJPanel.this.showCommentJDialog(commentable, title);
                }
            });

            popup.add(menuItem);
            needToAddSeperator = true;
        }

        // Split or merge only allowed, if there is one and only one stock
        // being selected.
        final List<Stock> selectedStocks = this.getSelectedStocks();
        if (selectedStocks.size() == 1) {
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagementJPanel_SplitOrMerge"), this.getImageIcon("/images/16x16/merge.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    PortfolioManagementJPanel.this.showSplitOrMergeJDialog(StockInfo.newInstance(selectedStocks.get(0)));
                }
            });

            popup.add(menuItem);
            needToAddSeperator = true;
        }

        if (needToAddSeperator) {
            popup.addSeparator();
        }

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_InvestmentChart..."), this.getImageIcon("/images/16x16/graph.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showChashFlowChartJDialog();
            }
        });

        popup.add(menuItem);

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagementJPanel_DividendChart"), this.getImageIcon("/images/16x16/chart.png"));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PortfolioManagementJPanel.this.showDividendSummaryBarChartJDialog();
            }
        });

        popup.add(menuItem);

        menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Summary..."), this.getImageIcon("/images/16x16/pie_chart.png"));
        
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PortfolioManagementJPanel.this.showBuyPortfolioChartJDialog();
            }
        });

        popup.add(menuItem);                
        
        if (isOnlyTreeTableRootBeingSelected(buyTreeTable) == false && (buyTreeTable.getSelectedRow() > 0)) {
            final JStock m = JStock.getInstance();
                                
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_History..."), this.getImageIcon("/images/16x16/strokedocker.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    List<Stock> stocks = getSelectedStocks(buyTreeTable);

                    for(Stock stock : stocks) {
                        m.displayHistoryChart(stock);
                    }
                }
            });
                        
            popup.add(menuItem);
            popup.addSeparator();
            
            menuItem = new JMenuItem(GUIBundle.getString("PortfolioManagement_Delete"), this.getImageIcon("/images/16x16/editdelete.png"));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    PortfolioManagementJPanel.this.deteleSelectedTreeTableRow();
                }
            });

            popup.add(menuItem);
        }
        
        return popup;
    }

    private void editSellTransaction(Transaction newTransaction, Transaction oldTransaction) {
        assert(newTransaction.getType() == Contract.Type.Sell);
        assert(oldTransaction.getType() == Contract.Type.Sell);
        
        final SellPortfolioTreeTableModelEx portfolioTreeTableModel = (SellPortfolioTreeTableModelEx)sellTreeTable.getTreeTableModel();
        portfolioTreeTableModel.editTransaction(newTransaction, oldTransaction);        
    }
    
    private void editBuyTransaction(Transaction newTransaction, Transaction oldTransaction) {
        assert(newTransaction.getType() == Contract.Type.Buy);
        assert(oldTransaction.getType() == Contract.Type.Buy);
        
        final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        portfolioTreeTableModel.editTransaction(newTransaction, oldTransaction);        
    }

    private Set<Country> getBuyTransactionCountries() {
        Set<Country> countries = new HashSet<>();
        final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        final Portfolio buyPortfolio = (Portfolio) buyPortfolioTreeTableModel.getRoot();

        for (int i = 0, count = buyPortfolio.getChildCount(); i < count; i++) {
            TransactionSummary transactionSummary = (TransactionSummary)buyPortfolio.getChildAt(i);
            Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
            Stock stock = transaction.getStock();
            Country country = org.yccheok.jstock.engine.Utils.toCountry(stock.code);
            countries.add(country);
        }
        
        return countries;
    }
    
    private int getBuyTransactionSize() {
        final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        return portfolioTreeTableModel.getTransactionSize();
    }

    private int getSellTransactionSize() {
        final SellPortfolioTreeTableModelEx portfolioTreeTableModel = (SellPortfolioTreeTableModelEx)sellTreeTable.getTreeTableModel();
        return portfolioTreeTableModel.getTransactionSize();
    }

    private TransactionSummary addBuyTransaction(Transaction transaction) {
        assert(transaction.getType() == Contract.Type.Buy);
        
        final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        TransactionSummary transactionSummary = portfolioTreeTableModel.addTransaction(transaction);

        // This is to prevent NPE, during initPortfolio through constructor.
        // Information will be pumped in later to realTimeStockMonitor, through 
        // initRealTimeStockMonitor.
        if (this.realTimeStockMonitor != null) {
            this.realTimeStockMonitor.addStockCode(transaction.getStock().code);
            this.realTimeStockMonitor.startNewThreadsIfNecessary();
            this.realTimeStockMonitor.refresh();
        }
        
        return transactionSummary;
    }

    public List<TransactionSummary> getTransactionSummariesFromPortfolios() {
        final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel = (SellPortfolioTreeTableModelEx)sellTreeTable.getTreeTableModel();
        final Portfolio buyPortfolio = (Portfolio) buyPortfolioTreeTableModel.getRoot();
        final Portfolio sellPortfolio = (Portfolio) sellPortfolioTreeTableModel.getRoot();
        List<TransactionSummary> summaries = new ArrayList<TransactionSummary>();

        for (int i = 0, count = buyPortfolio.getChildCount(); i < count; i++) {
            summaries.add((TransactionSummary)buyPortfolio.getChildAt(i));
        }
        
        for (int i = 0, count = sellPortfolio.getChildCount(); i < count; i++) {
            summaries.add((TransactionSummary)sellPortfolio.getChildAt(i));
        }

        return summaries;
    }

    public List<StockInfo> getStockInfosFromPortfolios() {
        final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
        final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel = (SellPortfolioTreeTableModelEx)sellTreeTable.getTreeTableModel();
        final Portfolio buyPortfolio = (Portfolio) buyPortfolioTreeTableModel.getRoot();
        final Portfolio sellPortfolio = (Portfolio) sellPortfolioTreeTableModel.getRoot();

        Set<Code> codes = new HashSet<Code>();
        List<StockInfo> stockInfos = new ArrayList<StockInfo>();

        final int count = buyPortfolio.getChildCount();
        TransactionSummary transactionSummary = null;
        for (int i = 0; i < count; i++) {
            transactionSummary = (TransactionSummary)buyPortfolio.getChildAt(i);

            assert(transactionSummary.getChildCount() > 0);

            Transaction transaction = (Transaction)transactionSummary.getChildAt(0);

            Stock stock = transaction.getStock();

            if (codes.contains(stock.code) == false) {
                codes.add(stock.code);
                stockInfos.add(StockInfo.newInstance(stock));
            }
        }

        final int count2 = sellPortfolio.getChildCount();
        transactionSummary = null;
        for (int i = 0; i < count2; i++) {
            transactionSummary = (TransactionSummary)sellPortfolio.getChildAt(i);

            assert(transactionSummary.getChildCount() > 0);

            Transaction transaction = (Transaction)transactionSummary.getChildAt(0);

            Stock stock = transaction.getStock();

            if (codes.contains(stock.code) == false) {
                codes.add(stock.code);
                stockInfos.add(StockInfo.newInstance(stock));
            }
        }

        return stockInfos;
    }

    private TransactionSummary addSellTransaction(Transaction transaction) {
        assert(transaction.getType() == Contract.Type.Sell);
        
        final SellPortfolioTreeTableModelEx portfolioTreeTableModel = (SellPortfolioTreeTableModelEx)sellTreeTable.getTreeTableModel();
        return portfolioTreeTableModel.addTransaction(transaction);
    }
    
    private void updateRealTimeStockMonitorAccordingToBuyPortfolioTreeTableModel() {
        if (this.realTimeStockMonitor == null) {
            return;
        }
        
        final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
                
        if (portfolioTreeTableModel != null) {
            this.buyTreeTable.setTreeTableModel(portfolioTreeTableModel);
            
            Portfolio portfolio = (Portfolio)portfolioTreeTableModel.getRoot();
            final int count = portfolio.getChildCount();
            
            for (int i = 0; i < count; i++) {
                TransactionSummary transactionSummary = (TransactionSummary)portfolio.getChildAt(i);
                
                if (transactionSummary.getChildCount() <= 0) continue;
                
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);

                this.realTimeStockMonitor.addStockCode(transaction.getStock().code);
            }
            this.realTimeStockMonitor.startNewThreadsIfNecessary();
            this.realTimeStockMonitor.refresh();
        }
        
    }
    
    private List<Stock> getSelectedStocks(JXTreeTable treeTable) {
        final TreePath[] treePaths = treeTable.getTreeSelectionModel().getSelectionPaths();
        List<Stock> stocks = new ArrayList<Stock>();
        Set<Code> c = new HashSet<Code>();
        
        if (treePaths == null) {
            return Collections.unmodifiableList(stocks);
        }
        
        for (TreePath treePath : treePaths) {
            final Object lastPathComponent = treePath.getLastPathComponent();
            
            if (lastPathComponent instanceof TransactionSummary) {
                final TransactionSummary transactionSummary = (TransactionSummary)lastPathComponent;
                assert(transactionSummary.getChildCount() > 0);
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
                final Stock stock = transaction.getStock();
                final Code code = stock.code;
                
                if(c.contains(code)) continue;
                
                stocks.add(stock);
                c.add(code);
            }
            else if (lastPathComponent instanceof Transaction) {
                final Transaction transaction = (Transaction)lastPathComponent;
                final Stock stock = transaction.getStock();
                final Code code = stock.code;
                
                if(c.contains(code)) continue;
                
                stocks.add(stock);
                c.add(code);
            }                        
        }
        
        return Collections.unmodifiableList(stocks);
    }

    // Initialize portfolios through CSV files. This is the preferable method,
    // as it works well under Desktop platform and Android platform.
    private boolean initCSVPortfolio() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        
        List<String> availablePortfolioNames = org.yccheok.jstock.portfolio.Utils.getPortfolioNames();
        // Do we have any portfolio for this country?
        if (availablePortfolioNames.size() <= 0) {
            // If not, create an empty portfolio.
            org.yccheok.jstock.portfolio.Utils.createEmptyPortfolio(org.yccheok.jstock.portfolio.Utils.getDefaultPortfolioName());
            availablePortfolioNames = org.yccheok.jstock.portfolio.Utils.getPortfolioNames();
        }
        assert(availablePortfolioNames.isEmpty() == false);

        // Is user selected portfolio name within current available portfolio names?
        if (false == availablePortfolioNames.contains(jStockOptions.getPortfolioName())) {
            // Nope. Reset user selected portfolio name to the first available name.
            jStockOptions.setPortfolioName(availablePortfolioNames.get(0));
        }
        
        // Clear the previous data structures.
        PortfolioManagementJPanel.this.buyTreeTable.setTreeTableModel(new BuyPortfolioTreeTableModelEx());
        PortfolioManagementJPanel.this.sellTreeTable.setTreeTableModel(new SellPortfolioTreeTableModelEx());
        PortfolioManagementJPanel.this.depositSummary = new DepositSummary();
        PortfolioManagementJPanel.this.dividendSummary = new DividendSummary();
            
        final File buyPortfolioFile = new File(org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory() + "buyportfolio.csv");
        final File sellPortfolioFile = new File(org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory() + "sellportfolio.csv");
        final File depositSummaryFile = new File(org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory() + "depositsummary.csv");
        final File dividendSummaryFile = new File(org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory() + "dividendsummary.csv");

        if (openAsCSVFile(buyPortfolioFile) == false) {
            // If CSV file is not there, consider this as empty record. This is
            // because in createEmptyPortfolio, we only create stockprices.csv,
            // for space and speed optimization purpose.            
            if (buyPortfolioFile.exists()) {
                // Either CSV format is corrupted.
                return false;
            }
        }
        if (openAsCSVFile(sellPortfolioFile) == false) {
            // If CSV file is not there, consider this as empty record. This is
            // because in createEmptyPortfolio, we only create stockprices.csv,
            // for space and speed optimization purpose.            
            if (sellPortfolioFile.exists()) {
                // Either CSV format is corrupted.
                return false;
            }
        }
        if (openAsCSVFile(dividendSummaryFile) == false) {
            // If CSV file is not there, consider this as empty record. This is
            // because in createEmptyPortfolio, we only create stockprices.csv,
            // for space and speed optimization purpose.            
            if (dividendSummaryFile.exists()) {
                // Either CSV format is corrupted.
                return false;
            }
        }        
        if (openAsCSVFile(depositSummaryFile) == false) {
            // If CSV file is not there, consider this as empty record. This is
            // because in createEmptyPortfolio, we only create stockprices.csv,
            // for space and speed optimization purpose.            
            if (depositSummaryFile.exists()) {
                // Either CSV format is corrupted.
                return false;
            }
        }
        
        this.timestamp = initCSVStockPrices();
        
        refershGUIAfterInitPortfolio(
                (BuyPortfolioTreeTableModelEx)PortfolioManagementJPanel.this.buyTreeTable.getTreeTableModel(), 
                (SellPortfolioTreeTableModelEx)PortfolioManagementJPanel.this.sellTreeTable.getTreeTableModel(), 
                this.dividendSummary,
                this.depositSummary);

        return true;
    }

    // Different among refreshGUIAfterOptionsJDialog and refreshGUIAfterFeeCalculationEnabledOptionsChanged
    // is that, refreshGUIAfterOptionsJDialog doesn't touch on column visibility.
    public void refreshGUIAfterOptionsJDialog() {
        if (SwingUtilities.isEventDispatchThread()) {
            _refreshGUIAfterOptionsJDialog();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _refreshGUIAfterOptionsJDialog();
                }
            });            
        }        
    }
    
    private void _refreshGUIAfterOptionsJDialog() {
        this.buyTreeTable.repaint();
        this.sellTreeTable.repaint();
        this.updateWealthHeader();        
    }
    
    public void refreshGUIAfterFeeCalculationEnabledOptionsChanged() {
        if (SwingUtilities.isEventDispatchThread()) {
            _refreshGUIAfterFeeCalculationEnabledOptionsChanged();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _refreshGUIAfterFeeCalculationEnabledOptionsChanged();
                }
            });            
        }        
    }
    
    public void _refreshGUIAfterFeeCalculationEnabledOptionsChanged() {
        this.buyTreeTable.repaint();
        this.sellTreeTable.repaint();
        this.updateWealthHeader();
        
        // Add/ remove columns based on user option.
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        JTable[] tables = {this.sellTreeTable, this.buyTreeTable};
        String[] names = { 
            GUIBundle.getString("PortfolioManagementJPanel_Broker"),
            GUIBundle.getString("PortfolioManagementJPanel_ClearingFee"),            
            GUIBundle.getString("PortfolioManagementJPanel_StampDuty")
        };
        
        for (JTable table : tables) {
            for (String name : names) {
                if (jStockOptions.isFeeCalculationEnabled()) {
                    final int columnCount = table.getColumnCount();
                    JTableUtilities.insertTableColumnFromModel(table, name, columnCount);                      
                } else {
                  JTableUtilities.removeTableColumn(table, name);
                }
            }
        }        
    }
    
    private void _refershGUIAfterInitPortfolio(
            final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel,
            final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel,
            final DividendSummary _dividendSummary,
            final DepositSummary _depositSummary          
            ) {
        // Without "if" checking, tree expand won't work. Weird!
        if (PortfolioManagementJPanel.this.buyTreeTable.getTreeTableModel() != buyPortfolioTreeTableModel) {
            PortfolioManagementJPanel.this.buyTreeTable.setTreeTableModel(buyPortfolioTreeTableModel);
        }
        
        // Without "if" checking, tree expand won't work. Weird!
        if (PortfolioManagementJPanel.this.sellTreeTable.getTreeTableModel() != sellPortfolioTreeTableModel) {
            PortfolioManagementJPanel.this.sellTreeTable.setTreeTableModel(sellPortfolioTreeTableModel);
        }
        PortfolioManagementJPanel.this.depositSummary = _depositSummary;
        PortfolioManagementJPanel.this.dividendSummary = _dividendSummary;

        PortfolioManagementJPanel.this.updateRealTimeStockMonitorAccordingToBuyPortfolioTreeTableModel();

        PortfolioManagementJPanel.this.updateWealthHeader();

        // Give user preferred GUI look. We do it here, because the entire table model is being changed.
        PortfolioManagementJPanel.this.initGUIOptions();

        PortfolioManagementJPanel.this.updateTitledBorder();

        // Every country is having different currency symbol. Remember to
        // refresh the currency symbol after we change the country.
        PortfolioManagementJPanel.this.refreshCurrencySymbol();
        
        expandTreeTable(this.buyTreeTable);
        expandTreeTable(this.sellTreeTable);
    }
    
    private void expandTreeTable(JXTreeTable treeTable) {
        // Due to bug in JXTreeTable, expandRow sometimes just won't work. Here
        // is the hacking which makes it works.
        if (treeTable.isExpanded(0)) {
            treeTable.collapseRow(0);
        }
                
        // Expand the trees.
        treeTable.expandRow(0);      
    }
    
    private void refershGUIAfterInitPortfolio(
            final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel,
            final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel,
            final DividendSummary _dividendSummary,
            final DepositSummary _depositSummary) {
        if (SwingUtilities.isEventDispatchThread()) {
            _refershGUIAfterInitPortfolio(buyPortfolioTreeTableModel, sellPortfolioTreeTableModel, _dividendSummary, _depositSummary);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _refershGUIAfterInitPortfolio(buyPortfolioTreeTableModel, sellPortfolioTreeTableModel, _dividendSummary, _depositSummary);
                }
            });            
        }
    }
    
    public final void initPortfolio() {
        // This is new portfolio. Reset last update date.
        this.timestamp = 0;
        this.initCSVPortfolio();
    }

    public void updateTitledBorder() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        if (SwingUtilities.isEventDispatchThread()) {
            final TitledBorder titledBorder = (TitledBorder)PortfolioManagementJPanel.this.jPanel1.getBorder();
            titledBorder.setTitle(jStockOptions.getPortfolioName());
            // So that title will refresh immediately.
            PortfolioManagementJPanel.this.jPanel1.repaint();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final TitledBorder titledBorder = (TitledBorder)PortfolioManagementJPanel.this.jPanel1.getBorder();
                    titledBorder.setTitle(jStockOptions.getPortfolioName());
                    // So that title will refresh immediately.
                    PortfolioManagementJPanel.this.jPanel1.repaint();
                }
            });
        }
    }

    public static boolean saveCSVPortfolio(String directory, CSVPortfolio csvPortfolio, long timestamp) {
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory) == false)
        {
            return false;
        }
        
        assert(directory.endsWith(File.separator));
        
        final File buyPortfolioFile = new File(directory + "buyportfolio.csv");
        final File sellPortfolioFile = new File(directory + "sellportfolio.csv");
        final File dividendSummaryFile = new File(directory + "dividendsummary.csv");
        final File depositSummaryFile = new File(directory + "depositsummary.csv");

        final FileEx buyPortfolioFileEx = new FileEx(buyPortfolioFile, org.yccheok.jstock.file.Statement.Type.PortfolioManagementBuy);
        final FileEx sellPortfolioFileEx = new FileEx(sellPortfolioFile, org.yccheok.jstock.file.Statement.Type.PortfolioManagementSell);
        final FileEx dividendSummaryFileEx = new FileEx(dividendSummaryFile, org.yccheok.jstock.file.Statement.Type.PortfolioManagementDividend);
        final FileEx depositSummaryFileEx = new FileEx(depositSummaryFile, org.yccheok.jstock.file.Statement.Type.PortfolioManagementDeposit);

        if (false == saveAsCSVFile(csvPortfolio, buyPortfolioFileEx, true)) {            
            
            final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel = csvPortfolio.buyPortfolioTreeTableModel;
            // org.yccheok.jstock.file.Statements is not good in handling empty 
            // case. Let us handle it seperately.
            int count = buyPortfolioTreeTableModel.getRoot().getChildCount();
            if (count > 0) {
                buyPortfolioFileEx.file.delete();
                // Is not empty, but we fail to save it for unknown reason.
                return false;
            }
        }
        
        if (false == saveAsCSVFile(csvPortfolio, sellPortfolioFileEx, true)) {            
            final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel = csvPortfolio.sellPortfolioTreeTableModel;
            
            // org.yccheok.jstock.file.Statements is not good in handling empty 
            // case. Let us handle it seperately.
            int count = sellPortfolioTreeTableModel.getRoot().getChildCount();
            if (count > 0) {
                sellPortfolioFileEx.file.delete();
                buyPortfolioFileEx.file.delete();
                // Is not empty, but we fail to save it for unknown reason.
                return false;
            }
        }

        if (false == saveAsCSVFile(csvPortfolio, dividendSummaryFileEx, true)) {            
            // org.yccheok.jstock.file.Statements is not good in handling empty 
            // case. Let us handle it seperately.
            int count = csvPortfolio.dividendSummary.size();
            if (count > 0) {
                dividendSummaryFileEx.file.delete();
                depositSummaryFileEx.file.delete();
                sellPortfolioFileEx.file.delete();
                buyPortfolioFileEx.file.delete();
                
                // Is not empty, but we fail to save it for unknown reason.
                return false;
            }
        }

        if (false == saveAsCSVFile(csvPortfolio, depositSummaryFileEx, true)) {
            // org.yccheok.jstock.file.Statements is not good in handling empty 
            // case. Let us handle it seperately.
            int count = csvPortfolio.depositSummary.size();
            if (count > 0) {
                depositSummaryFileEx.file.delete();
                sellPortfolioFileEx.file.delete();
                buyPortfolioFileEx.file.delete();                
                // Is not empty, but we fail to save it for unknown reason.
                return false;
            }
        }
        
        return saveCSVStockPrices(directory, csvPortfolio.buyPortfolioTreeTableModel, timestamp);
    }
    
    private boolean saveCSVPortfolio() {
        return saveCSVPortfolio(
            org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory(), 
            CSVPortfolio.newInstance((BuyPortfolioTreeTableModelEx)this.buyTreeTable.getTreeTableModel(), (SellPortfolioTreeTableModelEx)this.sellTreeTable.getTreeTableModel(), this.dividendSummary, this.depositSummary),
            timestamp
        );
    }

    private long initCSVStockPrices() {
        final File stockPricesFile = new File(org.yccheok.jstock.portfolio.Utils.getPortfolioDirectory() + "stockprices.csv");
        
        final Map<Code, Double> stockPrices = new LinkedHashMap<Code, Double>();
        
        Statements statements = Statements.newInstanceFromCSVFile(stockPricesFile);
        
        if (statements.getType() == Statement.Type.StockPrice) {
            final GUIBundleWrapper guiBundleWrapper = statements.getGUIBundleWrapper();
            
            for (int i = 0, ei = statements.size(); i < ei; i++) {
                Statement statement = statements.get(i);
                String codeStr = statement.getValueAsString(guiBundleWrapper.getString("MainFrame_Code"));
                Double price = statement.getValueAsDouble(guiBundleWrapper.getString("MainFrame_Last"));
                if (codeStr == null || price == null) {
                    continue;
                }
                
                Code code = Code.newInstance(codeStr);
                stockPrices.put(code, price);
            }
        }
        
        final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
 
        // Initialization.
        for (Map.Entry<Code, Double> entry : stockPrices.entrySet()) {
            Code code = entry.getKey();
            Double price = entry.getValue();
            portfolioTreeTableModel.updateStockLastPrice(code, price);
        }

        long _timestamp = 0;
        try {
            _timestamp = Long.parseLong(statements.getMetadatas().get("timestamp"));
        } catch (NumberFormatException ex) {
            log.error(null, ex);
        }
        
        return _timestamp;
    }
    
    private static boolean saveCSVStockPrices(String directory, BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModelEx, long timestamp) {
        assert(directory.endsWith(File.separator));
        
        // Ensure our stock prices data structure doesn't contain too less or
        // too much information. stockPrices might still contain redundant
        // information, as we do not update stockPrices immediately during
        // transaction summary deletion.
        Map<Code, Double> stockPrices = buyPortfolioTreeTableModelEx.getStockPrices();
        Map<Code, Double> goodStockPrices = new HashMap<Code, Double>();
        final Portfolio portfolio = (Portfolio)buyPortfolioTreeTableModelEx.getRoot();
        final int count = portfolio.getChildCount();
        for (int i = 0; i < count; i++) {
            TransactionSummary transactionSummary = (TransactionSummary)portfolio.getChildAt(i);
            assert(transactionSummary.getChildCount() > 0);            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
            final Code code = transaction.getStock().code;
            final Double price = stockPrices.get(code);
            if (price == null) {
                goodStockPrices.put(code, 0.0);
            } else {
                goodStockPrices.put(code, price);
            }
        }
        
        Statements statements = Statements.newInstanceFromStockPrices(goodStockPrices, timestamp);
        
        final File stockPricesFile = new File(directory + "stockprices.csv");
        
        return statements.saveAsCSVFile(stockPricesFile);
    }

    public boolean savePortfolio() {
        return saveCSVPortfolio();
    }

    /**
     * Initializes currency exchange monitor.
     */
    public void initCurrencyExchangeMonitor() {
        final JStock mainFrame = JStock.getInstance();
        final JStockOptions jStockOptions = mainFrame.getJStockOptions();

        final Country fromCountry = jStockOptions.getCountry();
        final Country toCountry = jStockOptions.getLocalCurrencyCountry(fromCountry);

        // Should we show the exchange rate label on status bar?
        mainFrame.setStatusBarExchangeRateVisible(jStockOptions.isCurrencyExchangeEnable(fromCountry));

        final ExchangeRateMonitor oldExchangeRateMonitor = this.exchangeRateMonitor;
        if (oldExchangeRateMonitor != null) {            
            Utils.getZoombiePool().execute(new Runnable() {
                @Override
                public void run() {
                    log.info("Prepare to shut down " + oldExchangeRateMonitor + "...");
                    oldExchangeRateMonitor.dettachAll();
                    oldExchangeRateMonitor.stop();
                    log.info("Shut down " + oldExchangeRateMonitor + " peacefully.");
                }
            });
        }

        this.exchangeRateMonitor = new ExchangeRateMonitor(
            Constants.EXCHANGE_RATE_MONITOR_MAX_THREAD, 
            Constants.EXCHANGE_RATE_MONITOR_MAX_STOCK_SIZE_PER_SCAN,
            jStockOptions.getScanningSpeed());
        
        this.exchangeRateMonitor.attach(exchangeRateMonitorObserver);

        Set<Country> countries = this.getBuyTransactionCountries();
        for (Country country : countries) {
            CurrencyPair currencyPair = new CurrencyPair(country.getCurrency(), toCountry.getCurrency());
            this.exchangeRateMonitor.addCurrencyPair(currencyPair);
        }
        
        // We will display the currency exchange rate, only if there is 1 
        // currency pair.
        if (countries.size() == 1) {
            // Update the tool tip text.
            Currency fromCurrency = countries.iterator().next().getCurrency();
            Currency toCurrency = toCountry.getCurrency();
            final String text = MessageFormat.format(GUIBundle.getString("MyJXStatusBar_CurrencyExchangeRateFor"), fromCurrency.toString(), toCurrency.toString());
            mainFrame.setStatusBarExchangeRateToolTipText(text);
        }

        // Everything is new. So, reset the displayed text first.
        mainFrame.setStatusBarExchangeRate(null);

        if (jStockOptions.isCurrencyExchangeEnable(fromCountry)) {
            // Start immediately.
            this.exchangeRateMonitor.startNewThreadsIfNecessary();
        }

        // Before returning, update wealth header immediately.
        this.updateWealthHeader();
    }

    public void initRealTimeStockMonitor() {
        final RealTimeStockMonitor oldRealTimeStockMonitor = realTimeStockMonitor;
        if (oldRealTimeStockMonitor != null) {            
            Utils.getZoombiePool().execute(new Runnable() {
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
        
        realTimeStockMonitor = new RealTimeStockMonitor(
                Constants.REAL_TIME_STOCK_MONITOR_MAX_THREAD, 
                Constants.REAL_TIME_STOCK_MONITOR_MAX_STOCK_SIZE_PER_SCAN, 
                JStock.getInstance().getJStockOptions().getScanningSpeed());
        
        realTimeStockMonitor.attach(this.realTimeStockMonitorObserver);
        
        updateRealTimeStockMonitorAccordingToBuyPortfolioTreeTableModel();
    }

    private org.yccheok.jstock.engine.Observer<ExchangeRateMonitor, List<ExchangeRate>> getExchangeRateMonitorObserver() {
        return new org.yccheok.jstock.engine.Observer<ExchangeRateMonitor, List<ExchangeRate>>() {
            @Override
            public void update(ExchangeRateMonitor subject, java.util.List<ExchangeRate> arg) {
                JStock.getInstance().setStatusBarExchangeRate(arg);
                updateWealthHeader();
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
                PortfolioManagementJPanel.this.update(monitor, stocks);
            }
        };
    }
    
    private void update(RealTimeStockMonitor monitor, final java.util.List<Stock> stocks) {
        final BuyPortfolioTreeTableModelEx portfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)buyTreeTable.getTreeTableModel();
 
        for (Stock stock : stocks) {
            if (false == portfolioTreeTableModel.updateStockLastPrice(stock)) {
                this.realTimeStockMonitor.removeStockCode(stock.code);
            }
        }
        
        updateWealthHeader();
        
        // Update status bar with current time string.
        this.timestamp = System.currentTimeMillis();
        
        JStock.getInstance().updateStatusBarWithLastUpdateDateMessageIfPossible();     
    }  

    public long getTimestamp() {
        return this.timestamp;
    }
    
    private void initGUIOptions() {
        File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "portfoliomanagementjpanel.xml");
        final GUIOptions guiOptions = Utils.fromXML(GUIOptions.class, f);

        if (guiOptions == null)
        {
            return;
        }

        if (guiOptions.getJTableOptionsSize() <= 1)
        {
            return;
        }

        final org.jdesktop.swingx.JXTreeTable[] treeTables = {buyTreeTable, sellTreeTable};

        /* Set Table Settings */
        for (int tableIndex = 0; tableIndex < treeTables.length; tableIndex++) {
            final JXTreeTable treeTable = treeTables[tableIndex];
            final javax.swing.table.JTableHeader jTableHeader = treeTable.getTableHeader();
            final JTable jTable = jTableHeader.getTable();
            JTableUtilities.setJTableOptions(jTable, guiOptions.getJTableOptions(tableIndex));
        }

        // Do we have the divider location option?
        if (guiOptions.getDividerLocationSize() > 0) {
            // Yes. Remember the divider location.
            // It will be used in updateDividerLocation later.
            this.dividerLocation = guiOptions.getDividerLocation(0);
        }
    }

    // Also, be aware: Calling setDividerLocation(double) before the JSplitPane
    // is visible will not work correctly.
    // Workaround for bug : http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6528446
    public void updateDividerLocation() {
        if (this.dividerLocation > 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Have the code in AWT event dispatching thread, in order
                    // to ensure MainFrame is already shown on the screen.
                    jSplitPane1.setDividerLocation(dividerLocation);
                }
            });
        }
    }

    public boolean saveGUIOptions() {
        if (Utils.createCompleteDirectoryHierarchyIfDoesNotExist(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config") == false)
        {
            return false;
        }

        final GUIOptions guiOptions = new GUIOptions();

        final org.jdesktop.swingx.JXTreeTable[] treeTables = {buyTreeTable, sellTreeTable};

        for (org.jdesktop.swingx.JXTreeTable treeTable : treeTables)
        {
            final javax.swing.table.JTableHeader jTableHeader = treeTable.getTableHeader();
            final JTable jTable = jTableHeader.getTable();
            final GUIOptions.JTableOptions jTableOptions = new GUIOptions.JTableOptions();
            
            final int count = jTable.getColumnCount();
            for (int i = 0; i < count; i++) {
                final String name = jTable.getColumnName(i);
                final TableColumn column = jTable.getColumnModel().getColumn(i);
                jTableOptions.addColumnOption(GUIOptions.JTableOptions.ColumnOption.newInstance(name, column.getWidth()));
            }

            guiOptions.addJTableOptions(jTableOptions);
        }

        guiOptions.addDividerLocation(jSplitPane1.getDividerLocation());
        
        File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "portfoliomanagementjpanel.xml");
        return org.yccheok.jstock.gui.Utils.toXML(guiOptions, f);
    }

    public boolean saveAsExcelFile(File file, boolean languageIndependent) {
        org.yccheok.jstock.file.Statements.StatementsEx statementsEx0, statementsEx1, statementsEx2, statementsEx3;
        Statements statements0 = org.yccheok.jstock.file.Statements.newInstanceFromBuyPortfolioTreeTableModel((BuyPortfolioTreeTableModelEx)this.buyTreeTable.getTreeTableModel(), languageIndependent);
        Statements statements1 = org.yccheok.jstock.file.Statements.newInstanceFromSellPortfolioTreeTableModel((SellPortfolioTreeTableModelEx)this.sellTreeTable.getTreeTableModel(), languageIndependent);
        Statements statements2 = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(new DividendSummaryTableModel(this.dividendSummary), languageIndependent);
        Statements statements3 = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(new DepositSummaryTableModel(this.depositSummary), languageIndependent);
        
        statementsEx0 = new org.yccheok.jstock.file.Statements.StatementsEx(statements0, GUIBundle.getString("PortfolioManagementJPanel_BuyPortfolio"));
        statementsEx1 = new org.yccheok.jstock.file.Statements.StatementsEx(statements1, GUIBundle.getString("PortfolioManagementJPanel_SellPortfolio"));
        statementsEx2 = new org.yccheok.jstock.file.Statements.StatementsEx(statements2, GUIBundle.getString("PortfolioManagementJPanel_DividendPortfolio"));
        statementsEx3 = new org.yccheok.jstock.file.Statements.StatementsEx(statements3, GUIBundle.getString("PortfolioManagementJPanel_CashDepositPortfolio"));
        List<org.yccheok.jstock.file.Statements.StatementsEx> statementsExs = Arrays.asList(statementsEx0, statementsEx1, statementsEx2, statementsEx3);
        return Statements.saveAsExcelFile(file, statementsExs);
    }

    private static boolean saveAsCSVFile(CSVPortfolio csvPortfolio, Utils.FileEx fileEx, boolean languageIndependent) {
        org.yccheok.jstock.file.Statements statements = null;
        if (fileEx.type == org.yccheok.jstock.file.Statement.Type.PortfolioManagementBuy) {
            // For buy portfolio, need not save metadata information, as we have
            // seperate "stockprices.csv" to handle it. However, I am not really
            // sure that whether seperating them is a good idea.
            statements = org.yccheok.jstock.file.Statements.newInstanceFromBuyPortfolioTreeTableModel(csvPortfolio.buyPortfolioTreeTableModel, languageIndependent);
        }
        else if (fileEx.type == org.yccheok.jstock.file.Statement.Type.PortfolioManagementSell) {
            statements = org.yccheok.jstock.file.Statements.newInstanceFromSellPortfolioTreeTableModel(csvPortfolio.sellPortfolioTreeTableModel, languageIndependent);
        }
        else if (fileEx.type == org.yccheok.jstock.file.Statement.Type.PortfolioManagementDividend) {
            statements = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(new DividendSummaryTableModel(csvPortfolio.dividendSummary), languageIndependent);
        }
        else if (fileEx.type == org.yccheok.jstock.file.Statement.Type.PortfolioManagementDeposit) {
            statements = org.yccheok.jstock.file.Statements.newInstanceFromTableModel(new DepositSummaryTableModel(csvPortfolio.depositSummary), languageIndependent);
        }
        // Use metadata to store TransactionSummary's comment.
        return statements.saveAsCSVFile(fileEx.file);
    }
    
    public boolean saveAsCSVFile(Utils.FileEx fileEx, boolean languageIndependent) {
        CSVPortfolio csvPortfolio = CSVPortfolio.newInstance(
                    (BuyPortfolioTreeTableModelEx)this.buyTreeTable.getTreeTableModel(), 
                    (SellPortfolioTreeTableModelEx)this.sellTreeTable.getTreeTableModel(), 
                    this.dividendSummary, 
                    this.depositSummary);
        return saveAsCSVFile(csvPortfolio, fileEx, languageIndependent);
    }

    public double getCurrencyExchangeRate() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        
        // Get the currency exchange rate.
        double exchangeRate = 1.0;
        if (jStockOptions.isCurrencyExchangeEnable(jStockOptions.getCountry())) {
            final CurrencyExchangeMonitor _currencyExchangeMonitor = this.currencyExchangeMonitor;
            if (_currencyExchangeMonitor != null) {
                exchangeRate = _currencyExchangeMonitor.getExchangeRate();
            }
        }
        return exchangeRate;
    }
    
    private void updateWealthHeader() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        final boolean isPenceToPoundConversionEnabled = jStockOptions.isPenceToPoundConversionEnabled();
        
        final BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel = (BuyPortfolioTreeTableModelEx)this.buyTreeTable.getTreeTableModel();
        final SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel = (SellPortfolioTreeTableModelEx)this.sellTreeTable.getTreeTableModel();
      
        final double exchangeRate = getCurrencyExchangeRate();

        final double share;
        final double cash;
        final double paperProfit;
        final double realizedProfit;
        if (false == isPenceToPoundConversionEnabled) {
            if (isFeeCalculationEnabled) {
                share = exchangeRate * buyPortfolioTreeTableModel.getCurrentValue();
                cash = exchangeRate * (sellPortfolioTreeTableModel.getNetSellingValue() - ((Portfolio)sellPortfolioTreeTableModel.getRoot()).getNetReferenceTotal() - buyPortfolioTreeTableModel.getNetPurchaseValue() + this.getDepositSummary().getTotal() + this.getDividendSummary().getTotal());
                paperProfit = exchangeRate * buyPortfolioTreeTableModel.getNetGainLossValue();
                realizedProfit = exchangeRate * sellPortfolioTreeTableModel.getNetGainLossValue();
            } else {
                share = exchangeRate * buyPortfolioTreeTableModel.getCurrentValue();
                cash = exchangeRate * (sellPortfolioTreeTableModel.getSellingValue() - ((Portfolio)sellPortfolioTreeTableModel.getRoot()).getReferenceTotal() - buyPortfolioTreeTableModel.getPurchaseValue() + this.getDepositSummary().getTotal() + this.getDividendSummary().getTotal());
                paperProfit = exchangeRate * buyPortfolioTreeTableModel.getGainLossValue();
                realizedProfit = exchangeRate * sellPortfolioTreeTableModel.getGainLossValue();                
            }
        } else {
            if (isFeeCalculationEnabled) {
                share = (exchangeRate * buyPortfolioTreeTableModel.getCurrentValue()) / 100.0;
                cash = exchangeRate * (sellPortfolioTreeTableModel.getNetSellingValue() / 100.0 - ((Portfolio)sellPortfolioTreeTableModel.getRoot()).getNetReferenceTotal() / 100.0 - buyPortfolioTreeTableModel.getNetPurchaseValue() / 100.0 + this.getDepositSummary().getTotal() + this.getDividendSummary().getTotal());
                paperProfit = (exchangeRate * buyPortfolioTreeTableModel.getNetGainLossValue()) / 100.0;
                realizedProfit = (exchangeRate * sellPortfolioTreeTableModel.getNetGainLossValue()) / 100.0;
            } else {
                share = (exchangeRate * buyPortfolioTreeTableModel.getCurrentValue()) / 100.0;
                cash = exchangeRate * (sellPortfolioTreeTableModel.getSellingValue() / 100.0 - ((Portfolio)sellPortfolioTreeTableModel.getRoot()).getReferenceTotal() / 100.0 - buyPortfolioTreeTableModel.getPurchaseValue() / 100.0 + this.getDepositSummary().getTotal() + this.getDividendSummary().getTotal());
                paperProfit = (exchangeRate * buyPortfolioTreeTableModel.getGainLossValue()) / 100.0;
                realizedProfit = (exchangeRate * sellPortfolioTreeTableModel.getGainLossValue()) / 100.0;                
            }
        }

        final double paperProfitPercentage;
        final double realizedProfitPercentage;

        if (isFeeCalculationEnabled) {
            paperProfitPercentage = buyPortfolioTreeTableModel.getNetGainLossPercentage();
            realizedProfitPercentage = sellPortfolioTreeTableModel.getNetGainLossPercentage();
        } else {
            paperProfitPercentage = buyPortfolioTreeTableModel.getGainLossPercentage();
            realizedProfitPercentage = sellPortfolioTreeTableModel.getGainLossPercentage();            
        }
        
        final String _share = org.yccheok.jstock.portfolio.Utils.toCurrency(DecimalPlaces.Two, share);
        final String _cash = org.yccheok.jstock.portfolio.Utils.toCurrency(DecimalPlaces.Two, cash);
        final String _paperProfit = org.yccheok.jstock.portfolio.Utils.toCurrency(DecimalPlaces.Two, paperProfit);
        final String _paperProfitPercentage = org.yccheok.jstock.portfolio.Utils.toCurrency(DecimalPlaces.Two, paperProfitPercentage);
        final String _realizedProfit = org.yccheok.jstock.portfolio.Utils.toCurrency(DecimalPlaces.Two, realizedProfit);
        final String _realizedProfitPercentage = org.yccheok.jstock.portfolio.Utils.toCurrency(DecimalPlaces.Two, realizedProfitPercentage);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabel2.setText(_share);
                jLabel4.setText(_cash);
                jLabel6.setText(_paperProfit + " (" + _paperProfitPercentage + "%)");
                jLabel8.setText(_realizedProfit + " (" + _realizedProfitPercentage + "%)");
                jLabel2.setForeground(Utils.getColor(share, 0.0));
                jLabel4.setForeground(Utils.getColor(cash, 0.0));
                jLabel6.setForeground(Utils.getColor(paperProfit, 0.0));
                jLabel8.setForeground(Utils.getColor(realizedProfit, 0.0));
           }
        });
    }
    
    public void resumeRealTimeStockMonitor() {
        if (realTimeStockMonitor == null) {
            return;
        }    
        realTimeStockMonitor.resume();
    }
    
    public void suspendRealTimeStockMonitor() {
        if (realTimeStockMonitor == null) {
            return;
        }        
        realTimeStockMonitor.suspend();
    }

    /**
     * Refresh all the labels with latest currency symbol.
     */
    public void refreshCurrencySymbol() {
        jLabel1.setText(getShareLabel());
        jLabel3.setText(getCashLabel());
        jLabel5.setText(getPaperProfitLabel());
        jLabel7.setText(getRealizedProfitLabel());
    }

    private String getShareLabel() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        return MessageFormat.format(
            GUIBundle.getString("PortfolioManagementJPanel_ShareLabel"),
            jStockOptions.getCurrencySymbol(jStockOptions.getCountry())
        );
    }
    
    private String getCashLabel() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        return MessageFormat.format(
            GUIBundle.getString("PortfolioManagementJPanel_CashLabel_template"),
            jStockOptions.getCurrencySymbol(jStockOptions.getCountry())
        );
    }
    
    private String getPaperProfitLabel() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        return MessageFormat.format(
            GUIBundle.getString("PortfolioManagementJPanel_PaperProfitLabel_template"),
            jStockOptions.getCurrencySymbol(jStockOptions.getCountry())
        );
    }
    
    private String getRealizedProfitLabel() {
        final JStockOptions jStockOptions = JStock.getInstance().getJStockOptions();
        return MessageFormat.format(
            GUIBundle.getString("PortfolioManagementJPanel_RealizedProfitLabel_template"),
            jStockOptions.getCurrencySymbol(jStockOptions.getCountry())
        );
    }

    public void refreshCurrencyExchangeMonitor() {
        CurrencyExchangeMonitor _currencyExchangeMonitor = this.currencyExchangeMonitor;
        if (_currencyExchangeMonitor != null) {
            _currencyExchangeMonitor.refresh();
        }
    }
    
    public void refreshRealTimeStockMonitor() {
        RealTimeStockMonitor _realTimeStockMonitor = this.realTimeStockMonitor;
        if (_realTimeStockMonitor != null) {
            _realTimeStockMonitor.refresh();
        }
    }
    
    public void rebuildRealTimeStockMonitor() {
        RealTimeStockMonitor _realTimeStockMonitor = this.realTimeStockMonitor;
        if (_realTimeStockMonitor != null) {
            _realTimeStockMonitor.rebuild();
        }
    }
    
    private static final Log log = LogFactory.getLog(PortfolioManagementJPanel.class);

    private int dividerLocation = -1;

    // Data structure.
    private DepositSummary depositSummary = new DepositSummary();
    private DividendSummary dividendSummary = new DividendSummary();

    private RealTimeStockMonitor realTimeStockMonitor = null;
    private ExchangeRateMonitor exchangeRateMonitor = null;

    private final org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, List<Stock>> realTimeStockMonitorObserver = this.getRealTimeStockMonitorObserver();
    private final org.yccheok.jstock.engine.Observer<ExchangeRateMonitor, List<ExchangeRate>> exchangeRateMonitorObserver = this.getExchangeRateMonitorObserver();

    private long timestamp = 0;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.yccheok.jstock.gui.treetable.SortableTreeTable buyTreeTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private org.yccheok.jstock.gui.treetable.SortableTreeTable sellTreeTable;
    // End of variables declaration//GEN-END:variables

}
