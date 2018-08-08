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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.DividendServer;
import org.yccheok.jstock.engine.Factories;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.StockServerFactory;
import org.yccheok.jstock.gui.Constants;
import org.yccheok.jstock.gui.JTableUtilities;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.PortfolioManagementJPanel;
import org.yccheok.jstock.gui.UIOptions;
import org.yccheok.jstock.gui.table.StockInfoRenderer;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.portfolio.Commentable;
import org.yccheok.jstock.portfolio.DecimalPlace;
import org.yccheok.jstock.portfolio.Dividend;
import org.yccheok.jstock.portfolio.DividendSummary;
import org.yccheok.jstock.portfolio.QuantityQuery;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;
import org.yccheok.jstock.portfolio.Utils;

/**
 *
 * @author yccheok
 */
public class DividendSummaryJDialog extends javax.swing.JDialog implements PropertyChangeListener {

    /** Creates new form DividendSummaryJDialog */
    public DividendSummaryJDialog(java.awt.Frame parent, boolean modal, DividendSummary dividendSummary, PortfolioManagementJPanel portfolioManagementJPanel) {
        super(parent, modal);
        this.portfolioManagementJPanel = portfolioManagementJPanel;
        // Clone another copy to avoid original copy from being corrupted.
        this.dividendSummary = new DividendSummary(dividendSummary);
        this.dividendSummaryAfterPressingOK = null;
        initComponents();
      
        // Hackish way to make Mac works.
        pack();    
        
        Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.DIVIDEND_SUMMARY_JDIALOG);
        if (dimension != null) {
            setSize(dimension);
        } else {     
            setSize(new java.awt.Dimension(416, 457));
        }
        
        setLocationRelativeTo(null);
        
        ((TableRowSorter)this.jTable1.getRowSorter()).setStringConverter(new TableStringConverter() {

            @Override
            public String toString(TableModel model, int row, int column) {
                if (model.getColumnClass(column) == StockInfo.class) {
                    return ((StockInfo)model.getValueAt(row, column)).symbol.toString();
                }
                
                return model.getValueAt(row, column).toString();
            }
        });
        
        // Sort by date, with latest comes first.
        final RowSorter<? extends TableModel> rowSorter = this.jTable1.getRowSorter();
        if (rowSorter != null) {
            rowSorter.toggleSortOrder(0);
            final List<? extends SortKey> sortKeys = rowSorter.getSortKeys();
            if (sortKeys.size() > 0) {
                if (sortKeys.get(0).getSortOrder() != javax.swing.SortOrder.DESCENDING) {
                    rowSorter.toggleSortOrder(0);        
                }
            }
        }        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                AutoDividendTask autoDividendTask = DividendSummaryJDialog.this.autoDividendTask;
                if (autoDividendTask != null) {
                    autoDividendTask.cancel(true);
                }
            }           
        });        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jXHeader1 = new org.jdesktop.swingx.JXHeader();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("DividendSummaryJDialog_DividendPayout")); // NOI18N
        setIconImage(null);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        jXHeader1.setDescription(bundle.getString("DividendSummaryJDialog_Description")); // NOI18N
        jXHeader1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32x32/money2.png"))); // NOI18N
        jXHeader1.setTitle(bundle.getString("DividendSummaryJDialog_Dividend")); // NOI18N
        getContentPane().add(jXHeader1, java.awt.BorderLayout.NORTH);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/apply.png"))); // NOI18N
        jButton1.setText(bundle.getString("DividendSummary_OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/button_cancel.png"))); // NOI18N
        jButton2.setText(bundle.getString("DividendSummary_Cancel")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel1.setLayout(new java.awt.BorderLayout(5, 5));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filenew.png"))); // NOI18N
        jButton3.setText(bundle.getString("DividendSummaryJDialog_New")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton3);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/editdelete.png"))); // NOI18N
        jButton4.setText(bundle.getString("DividendSummaryJDialog_Delete")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton4);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/auto-dividend.png"))); // NOI18N
        jButton5.setText(bundle.getString("DividendSummaryJDialog_AutoDividend...")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton5);

        jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(bundle.getString("DividendSummaryJDialog_TotalDividend")); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel4.add(jLabel1);

        jLabel2.setForeground(new java.awt.Color(0, 0, 255));
        jLabel2.setText(getDividendSummaryText());
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel2MouseExited(evt);
            }
        });
        jPanel4.add(jLabel2);

        jPanel1.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        jTable1.setModel(new DividendSummaryTableModel(this.dividendSummary));
        org.yccheok.jstock.gui.table.CurrencyRenderer currencyRenderer = new org.yccheok.jstock.gui.table.CurrencyRenderer();
        org.yccheok.jstock.gui.table.NonNegativeEmptyDoubleEditor currencyEditor = new org.yccheok.jstock.gui.table.NonNegativeEmptyDoubleEditor();
        currencyRenderer.setHorizontalAlignment(org.yccheok.jstock.gui.table.CurrencyRenderer.RIGHT);
        ((JTextField)currencyEditor.getComponent()).setHorizontalAlignment(SwingConstants.RIGHT);
        jTable1.setDefaultEditor(Double.class, currencyEditor);
        jTable1.setDefaultRenderer(Double.class, currencyRenderer);
        jTable1.setDefaultEditor(StockInfo.class, new org.yccheok.jstock.gui.table.StockInfoEditor(this.portfolioManagementJPanel.getStockInfosFromPortfolios()));
        jTable1.setDefaultRenderer(StockInfo.class, new StockInfoRenderer());

        // JTableCustomizer.setEditorForRow(jTable1, 1);
        // Do not use JTableCustomizer. We need to have our own implementation to decide 
        // row background color.
        JTableUtilities.setDateEditorAndRendererForRow(jTable1, 0);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.addMouseListener(new TableRowPopupListener());

        if (JStock.instance().getJStockOptions().useLargeFont()) {
            this.jTable1.setRowHeight((int)(this.jTable1.getRowHeight() * Constants.FONT_ENLARGE_FACTOR));
        }
        jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTable1PropertyChange(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(416, 457));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        clearAllTablesSelection();
    }//GEN-LAST:event_formMouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (KeyEvent.VK_DELETE == evt.getKeyCode()) {
            this.deleteSelectedDividend();
            this.jLabel2.setText(this.getDividendSummaryText());
            return;
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
        this.jLabel2.setText(this.getDividendSummaryText());
    }//GEN-LAST:event_jTable1PropertyChange

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Returns the active cell editor, which is null if the table is not
        // currently editing. 
        final TableCellEditor tableCellEditor = this.jTable1.getCellEditor();
        if (tableCellEditor != null) {
            // Save the value to table model.
            final boolean status = tableCellEditor.stopCellEditing();
            if (false == status) {
                // User enters an invalid value. Prevent user from closing
                // the dialog.
                return;
            }
        }
        this.dividendSummaryAfterPressingOK = this.dividendSummary;
        Utils.removeMeaninglessRecords(this.dividendSummaryAfterPressingOK);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dividendSummaryAfterPressingOK = null;
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.addNewDividend();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.deleteSelectedDividend();
        this.jLabel2.setText(this.getDividendSummaryText());
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        if (this.dividendSummary == null || this.dividendSummary.getTotal() <= 0.0) {
            return;
        }
        final JStock m = JStock.instance();
        final DividendSummaryBarChartJDialog dividendSummaryBarChartJDialog = new DividendSummaryBarChartJDialog(this, false, this.getDividendSummary());
        dividendSummaryBarChartJDialog.setVisible(true);
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseEntered
        if (this.dividendSummary == null || this.dividendSummary.getTotal() <= 0.0) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }//GEN-LAST:event_jLabel2MouseEntered

    private void jLabel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseExited
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLabel2MouseExited

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (transactionSummaries == null) {
            transactionSummaries = this.portfolioManagementJPanel.getTransactionSummariesFromPortfolios();
        }
        progressMonitor = new ProgressMonitor(DividendSummaryJDialog.this,
                                  GUIBundle.getString("DividendSummaryJDialog_AutoDividendCalculating..."),
                                  "", 0, transactionSummaries.size());
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(50);
        progressMonitor.setMillisToPopup(100);
        autoDividendTask = new AutoDividendTask();
        autoDividendTask.addPropertyChangeListener(this);
        autoDividendTask.execute();
        this.jButton5.setEnabled(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        JStock.instance().getUIOptions().setDimension(UIOptions.DIVIDEND_SUMMARY_JDIALOG, getSize());
    }//GEN-LAST:event_formWindowClosed

    /**
     * Returns dividend represented by this dialog.
     *
     * @return Dividend represented by this dialog
     */
    public DividendSummary getDividendSummary() {
        return this.dividendSummary;
    }

    /**
     * Returns dividend represented by this dialog after OK button is pressed.
     * If OK button is not being pressed, <code>null</code> will be returned.
     *
     * @return Dividend represented by this dialog. <code>null</code> will be
     * returned if OK button is not being pressed
     */
    public DividendSummary getDividendSummaryAfterPressingOK() {
        return this.dividendSummaryAfterPressingOK;
    }

    private String getDividendSummaryText() {
        if (this.dividendSummary != null) {
            return "<html><a href=\"\">" + org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(DecimalPlace.Three, this.dividendSummary.getTotal()) + "</a></html>";
        }
        return "";
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName() ) {
            if (progressMonitor == null) {
                return;
            }
           
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
            
            int total = transactionSummaries.size();
            int progressPercentage = total == 0 ? 0 : (int)(progress / (double)total * 100.);
            String template = GUIBundle.getString("DividendSummaryJDialog_Completed_template");
            String message = MessageFormat.format(template, progressPercentage);
            progressMonitor.setNote(message);
            if (progressMonitor.isCanceled() || autoDividendTask.isDone()) {
                if (progressMonitor.isCanceled()) {
                    autoDividendTask.cancel(true);
                } else {
                }
                jButton5.setEnabled(true);
            }
        }
    }

    private class TableRowPopupListener extends MouseAdapter {

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
                getMyJTablePopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public int addNewDividend(StockInfo stockInfo) {
        final int modelIndex = addNewDividend();
        ((DividendSummaryTableModel)jTable1.getModel()).setValueAt(stockInfo, modelIndex, 1);
        return modelIndex;
    }

    private int addNewDividend() {
        final int modelIndex = ((DividendSummaryTableModel)this.jTable1.getModel()).addNewDividend();

        clearAllTablesSelection();

        final int selectedViewIndex = jTable1.getRowSorter().convertRowIndexToView(modelIndex);
        jTable1.getSelectionModel().setSelectionInterval(selectedViewIndex, selectedViewIndex);
        JTableUtilities.scrollToVisible(jTable1, selectedViewIndex, 0);
        return modelIndex;
    }

    private void deleteSelectedDividend() {
        int rows[] = jTable1.getSelectedRows();

        final DividendSummaryTableModel dividendSummaryTableModel = (DividendSummaryTableModel)(jTable1.getModel());

        Arrays.sort(rows);

        for (int i = rows.length-1; i >= 0; i--) {
            int row = rows[i];

            if(row < 0) continue;

            final int modelIndex = jTable1.getRowSorter().convertRowIndexToModel(row);
            dividendSummaryTableModel.removeRow(modelIndex);
        }
    }

    private JPopupMenu getMyJTablePopupMenu() {
        final JPopupMenu popup = new JPopupMenu();

        javax.swing.JMenuItem menuItem = new JMenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("DividendSummary_New"), new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filenew.png")));

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewDividend();
            }
        });

        popup.add(menuItem);

        final Commentable commentable = getSelectedCommentable();
        final String text = this.getSelectedSecondColumnString();
        if (commentable != null && text != null) {
            popup.addSeparator();

            menuItem = new JMenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("DividendSummary_Note..."), new javax.swing.ImageIcon(getClass().getResource("/images/16x16/sticky.png")));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showCommentJDialog(commentable, MessageFormat.format(GUIBundle.getString("DividendSummaryJDialog_NoteFor_template"), text));
                }
            });

            popup.add(menuItem);
        }

        if (jTable1.getSelectedRowCount() >= 1) {
            popup.addSeparator();

            menuItem = new JMenuItem(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("DividendSummaryJDialog_Delete"), new javax.swing.ImageIcon(getClass().getResource("/images/16x16/editdelete.png")));

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    deleteSelectedDividend();
                    jLabel2.setText(getDividendSummaryText());
                }
            });

            popup.add(menuItem);
        }

        return popup;
    }

    private void showCommentJDialog(Commentable commentable, String title) {
        if (commentable == null) {
            // Nothing to be shown.
            return;
        }

        final JStock mainFrame = JStock.instance();
        CommentJDialog commentJDialog = new CommentJDialog(mainFrame, true, commentable);
        commentJDialog.setTitle(title);
        commentJDialog.setLocationRelativeTo(this);
        commentJDialog.setVisible(true);
    }
    
    private void clearAllTablesSelection() {
        this.jTable1.getSelectionModel().clearSelection();
    }

    private String getSelectedSecondColumnString() {
        if (this.jTable1.getSelectedRowCount() != 1) {
            return null;
        }

        int viewIndex = this.jTable1.convertColumnIndexToView(1);
        String string = ((StockInfo)jTable1.getValueAt(jTable1.getSelectedRow(), viewIndex)).symbol.toString();
        return string;
    }

    private Commentable getSelectedCommentable() {
        if (this.jTable1.getSelectedRowCount() != 1) {
            return null;
        }

        int viewIndex = this.jTable1.getSelectedRow();
        int index = this.jTable1.convertRowIndexToModel(viewIndex);
        return ((DividendSummaryTableModel)jTable1.getModel()).getDividend(index);
    }

    private class AutoDividendTask extends SwingWorker<Map<Code, List<Dividend>>, Void> {
        @Override
        public Map<Code, List<Dividend>> doInBackground() {
            final QuantityQuery quantityQuery = new QuantityQuery(DividendSummaryJDialog.this.transactionSummaries);
            Map<Code, List<Dividend>> result = new HashMap<Code, List<Dividend>>();

            int i = 0;
            for (TransactionSummary transactionSummary : transactionSummaries) {
                i++;
                
                if (isCancelled()) {
                    break;                    
                }
                
                final StockInfo stockInfo = ((Transaction)transactionSummary.getChildAt(0)).getStockInfo();
                final Code code = stockInfo.code;
                Dividend latestDividend = org.yccheok.jstock.portfolio.Utils.getLatestDividend(dividendSummary, code);                
                                
                List<Dividend> suggestedDividends = new ArrayList<>();
                for (StockServerFactory stockServerFactory : Factories.INSTANCE.getStockServerFactories(code)) {
                    if (isCancelled()) {
                        break;
                    }
                    
                    DividendServer dividendServer = stockServerFactory.getDividendServer();
                    if (dividendServer != null) {
                        List<Dividend> dividends = dividendServer.getDividends(code);
                        for (Dividend dividend : dividends) {                            
                            if (latestDividend != null) {
                                if (latestDividend.date.compareTo(dividend.date) >= 0) {
                                    continue;
                                }
                            }
                            
                            double total = dividend.amount * quantityQuery.getBalance(code, dividend.date);
                            if (total == 0) {
                                continue;
                            }
                            
                            final boolean shouldConvertPenceToPound = org.yccheok.jstock.portfolio.Utils.shouldConvertPenceToPound(portfolioManagementJPanel.getPortfolioRealTimeInfo(), dividend.stockInfo.code);
                            
                            if (shouldConvertPenceToPound) {
                                total = total / 100.0;
                            }

                            Dividend suggestedDividend = new Dividend(stockInfo, total, dividend.date);
                            suggestedDividends.add(suggestedDividend);
                        }
                    }
                }
                
                if (suggestedDividends.isEmpty() == false) {
                    result.put(code, suggestedDividends);
                }
                
                this.setProgress(i);
            }
            return result;
        }
 
        @Override
        public void done() {
            jButton5.setEnabled(true);
            progressMonitor.close();
            progressMonitor = null;
            if (false == this.isCancelled()) {
                try {
                    final Map<Code, List<Dividend>> dividends = get();
                    if (dividends.isEmpty()) {
                        JOptionPane.showMessageDialog(DividendSummaryJDialog.this, MessagesBundle.getString("info_message_no_dividend_found"), MessagesBundle.getString("info_title_no_dividend_found"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        AutoDividendJDialog autoDividendJDialog = new AutoDividendJDialog(JStock.instance(), true, dividends);
                        autoDividendJDialog.setLocationRelativeTo(DividendSummaryJDialog.this);
                        autoDividendJDialog.setVisible(true);
                        List<Dividend> dividendsAfterPressingOK = autoDividendJDialog.getDividendsAfterPressingOK();
                        if (dividendsAfterPressingOK != null) {
                            DividendSummaryTableModel dividendSummaryTableModel = (DividendSummaryTableModel)jTable1.getModel();
                            for (Dividend dividend : dividendsAfterPressingOK) {
                                dividendSummaryTableModel.add(dividend);
                            }
                            if (dividendsAfterPressingOK.isEmpty() == false) {
                                jLabel2.setText(getDividendSummaryText());
                                JOptionPane.showMessageDialog(DividendSummaryJDialog.this, 
                                    MessageFormat.format(MessagesBundle.getString("info_message_dividends_added_template"), dividendsAfterPressingOK.size()), 
                                    MessagesBundle.getString("info_title_dividends_added"), 
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error(null, ex);
                } 
            }
        }
    }
 
    private List<TransactionSummary> transactionSummaries = null;
    
    // Data structure hold by table.
    private DividendSummary dividendSummary;
    // Final dividend result after pressing OK button.
    private DividendSummary dividendSummaryAfterPressingOK = null;
    // We want to use getStocksFromPortfolios from PortfolioManagementJPanel.
    private final PortfolioManagementJPanel portfolioManagementJPanel;

    private ProgressMonitor progressMonitor = null;
    private AutoDividendTask autoDividendTask = null;
    
    private static final Log log = LogFactory.getLog(DividendSummaryJDialog.class);
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private org.jdesktop.swingx.JXHeader jXHeader1;
    // End of variables declaration//GEN-END:variables

}
