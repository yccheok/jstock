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

package org.yccheok.jstock.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;
import javax.swing.SwingUtilities;
import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DateField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.DispType;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.StockInfoDatabase;
import org.yccheok.jstock.engine.Symbol;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.portfolio.BrokingFirm;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.Transaction;

/**
 *
 * @author  yccheok
 */
public class NewBuyTransactionJDialog extends javax.swing.JDialog {
    /** Creates new form NewBuyTransactionJDialog */
    public NewBuyTransactionJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        
        if (isFeeCalculationEnabled) {
            if (jStockOptions.getBrokingFirmSize() < 1) {
                initComponentsWithoutBrokerageFirm();
                
                Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE);
                if (dimension != null) {
                    setSize(dimension);
                }
            } else {
                initComponents();
                
                Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE);
                if (dimension != null) {
                    setSize(dimension);
                }                
            }
        } else {
            initComponentsWithFeeCalculationDisabled();
            
            Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.NEW_BUY_TRANSACTION_JDIALOG);
            if (dimension != null) {
                setSize(dimension);
            }             
        }
        
        this.jPanel1.add(Utils.getBusyJXLayer((AutoCompleteJComboBox)this.jComboBox1));
                
        if (shouldAutoCalculateBrokerFee())
        {
            this.jFormattedTextField3.setEditable(false);
            this.jFormattedTextField4.setEditable(false);
            this.jFormattedTextField5.setEditable(false);
        }
        
        initBrokerCostsComboBox();
        
        initAjaxProvider();
    }
    
    private void initComponentsWithoutBrokerageFirm() {

        jComboBox1 = new AutoCompleteJComboBox();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel3 = CalendarFactory.createDateField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = getCurrencyJFormattedTextField();
        jFormattedTextField2 = getCurrencyJFormattedTextField();
        jFormattedTextField3 = getCurrencyJFormattedTextField();
        jFormattedTextField4 = getCurrencyJFormattedTextField();
        jFormattedTextField5 = getCurrencyJFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField6 = getCurrencyJFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();

        jComboBox1.setEditable(true);
        jComboBox1.setPreferredSize(new java.awt.Dimension(110, 24));
        ((AutoCompleteJComboBox)jComboBox1).attachStockInfoObserver(this.getStockInfoObserver());
        ((AutoCompleteJComboBox)jComboBox1).attachDispObserver(this.getDispObserver());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("NewBuyTransactionJDialog_Buy")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewBuyTransactionJDialog_Transaction"))); // NOI18N

        jLabel2.setText(bundle.getString("NewSellTransactionJDialog_Symbol")); // NOI18N

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(100.0d, 0.001d, null, 100.0d));
        JSpinner.NumberEditor numberEditor = (JSpinner.NumberEditor)jSpinner1.getEditor();
        final DecimalFormat decimalFormat = numberEditor.getFormat();
        decimalFormat.setMaximumFractionDigits(4);
        numberEditor.getTextField().addMouseListener(getJFormattedTextFieldMouseListener());
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel4.setText(bundle.getString("NewBuyTransactionJDialog_Date")); // NOI18N

        jFormattedTextField1.setValue(new Double(0.0));
        jFormattedTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField1KeyTyped(evt);
            }
        });

        jFormattedTextField2.setEditable(false);
        jFormattedTextField2.setValue(new Double(0.0));

        jFormattedTextField3.setValue(new Double(0.0));
        jFormattedTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField3KeyTyped(evt);
            }
        });

        jFormattedTextField4.setValue(new Double(0.0));
        jFormattedTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField4KeyTyped(evt);
            }
        });

        jFormattedTextField5.setValue(new Double(0.0));
        jFormattedTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField5KeyTyped(evt);
            }
        });

        jLabel10.setFont(jLabel10.getFont().deriveFont(jLabel10.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel10.setText(bundle.getString("NewBuyTransactionJDialog_NetValue")); // NOI18N

        jFormattedTextField6.setEditable(false);
        jFormattedTextField6.setFont(jFormattedTextField6.getFont().deriveFont(jFormattedTextField6.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField6.setValue(new Double(0.0));

        jLabel1.setText(bundle.getString("NewBuyTransactionJDialog_Unit")); // NOI18N

        jLabel3.setText(bundle.getString("NewBuyTransactionJDialog_Price")); // NOI18N

        jLabel5.setText(bundle.getString("NewBuyTransactionJDialog_Value")); // NOI18N

        jLabel7.setText(bundle.getString("NewBuyTransactionJDialog_Broker")); // NOI18N

        jLabel8.setText(bundle.getString("NewBuyTransactionJDialog_Clearing")); // NOI18N

        jLabel9.setText(bundle.getString("NewBuyTransactionJDialog_StampDuty")); // NOI18N

        jLabel11.setText(bundle.getString("NewBuyTransactionJDialog_Code")); // NOI18N

        jTextField2.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jFormattedTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel11, jLabel2, jLabel3, jLabel4, jLabel5, jLabel7, jLabel8, jLabel9});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel9))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/apply.png"))); // NOI18N
        jButton1.setText(bundle.getString("NewBuyTransactionJDialog_OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/button_cancel.png"))); // NOI18N
        jButton2.setText(bundle.getString("NewBuyTransactionJDialog_Cancel")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewBuyTransactionJDialog_Stock"))); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/inbox.png"))); // NOI18N
        jPanel1.add(jLabel6);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();

    }  

     private void initBrokerCostsComboBox() {
         if (jComboBox2 == null) {
             return;
         }
         
         DefaultComboBoxModel model = new DefaultComboBoxModel();
         
         final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
         final java.util.List<BrokingFirm> brokerFirms = jStockOptions.getBrokingFirms();
         
         for (BrokingFirm brokingFirm : brokerFirms) {
             model.addElement(brokingFirm.getName());
         }             
         model.addElement(GUIBundle.getString("NewBuyTransactionJDialog_None"));
         
         jComboBox2.setModel(model);
         jComboBox2.addItemListener((java.awt.event.ItemEvent e) -> {
             int index = jComboBox2.getSelectedIndex();
             
             jStockOptions.setSelectedBrokingFirmIndex(index);
             update();
         });
         
         // Ensure valid selection.
         int index = jStockOptions.getSelectedBrokingFirmIndex();
         if (index < 0 || index >= model.getSize()) {
             index = model.getSize() - 1;
             jStockOptions.setSelectedBrokingFirmIndex(index);
         }
         
         jComboBox2.setSelectedIndex(index);
         jComboBox2.setPrototypeDisplayValue("a");
         
         BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
         jComboBox2.addPopupMenuListener(listener);
    }
     
    private void initComponentsWithFeeCalculationDisabled() {
        jComboBox1 = new AutoCompleteJComboBox();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel3 = CalendarFactory.createDateField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = getCurrencyJFormattedTextField();
        jFormattedTextField2 = getCurrencyJFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField6 = getCurrencyJFormattedTextField();
        
        jFormattedTextField3 = getCurrencyJFormattedTextField();
        jFormattedTextField4 = getCurrencyJFormattedTextField();
        jFormattedTextField5 = getCurrencyJFormattedTextField();

        jFormattedTextField3.setValue(new Double(0.0));
        jFormattedTextField4.setValue(new Double(0.0));
        jFormattedTextField5.setValue(new Double(0.0));

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();

        jComboBox1.setEditable(true);
        jComboBox1.setPreferredSize(new java.awt.Dimension(110, 24));
        ((AutoCompleteJComboBox)jComboBox1).attachStockInfoObserver(this.getStockInfoObserver());
        ((AutoCompleteJComboBox)jComboBox1).attachDispObserver(this.getDispObserver());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("NewBuyTransactionJDialog_Buy")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewBuyTransactionJDialog_Transaction"))); // NOI18N

        jLabel2.setText(bundle.getString("NewSellTransactionJDialog_Symbol")); // NOI18N

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(100.0d, 0.001d, null, 100.0d));
        JSpinner.NumberEditor numberEditor = (JSpinner.NumberEditor)jSpinner1.getEditor();
        final DecimalFormat decimalFormat = numberEditor.getFormat();
        decimalFormat.setMaximumFractionDigits(4);
        numberEditor.getTextField().addMouseListener(getJFormattedTextFieldMouseListener());
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel4.setText(bundle.getString("NewBuyTransactionJDialog_Date")); // NOI18N

        jFormattedTextField1.setValue(new Double(0.0));
        jFormattedTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField1KeyTyped(evt);
            }
        });

        jFormattedTextField2.setEditable(false);
        jFormattedTextField2.setValue(new Double(0.0));

        jLabel10.setFont(jLabel10.getFont().deriveFont(jLabel10.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel10.setText(bundle.getString("NewBuyTransactionJDialog_NetValue")); // NOI18N

        jFormattedTextField6.setEditable(false);
        jFormattedTextField6.setFont(jFormattedTextField6.getFont().deriveFont(jFormattedTextField6.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField6.setValue(new Double(0.0));

        jLabel1.setText(bundle.getString("NewBuyTransactionJDialog_Unit")); // NOI18N

        jLabel3.setText(bundle.getString("NewBuyTransactionJDialog_Price")); // NOI18N

        jLabel5.setText(bundle.getString("NewBuyTransactionJDialog_Value")); // NOI18N

        jLabel11.setText(bundle.getString("NewBuyTransactionJDialog_Code")); // NOI18N

        jTextField2.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel11, jLabel2, jLabel3, jLabel4, jLabel5});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/apply.png"))); // NOI18N
        jButton1.setText(bundle.getString("NewBuyTransactionJDialog_OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/button_cancel.png"))); // NOI18N
        jButton2.setText(bundle.getString("NewBuyTransactionJDialog_Cancel")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewBuyTransactionJDialog_Stock"))); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/inbox.png"))); // NOI18N
        jPanel1.add(jLabel6);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();

    }
    
    private boolean shouldAutoCalculateBrokerFee() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        
        return jStockOptions.getSelectedBrokingFirm() != null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new AutoCompleteJComboBox();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel3 = CalendarFactory.createDateField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = getCurrencyJFormattedTextField();
        jFormattedTextField2 = getCurrencyJFormattedTextField();
        jFormattedTextField3 = getCurrencyJFormattedTextField();
        jFormattedTextField4 = getCurrencyJFormattedTextField();
        jFormattedTextField5 = getCurrencyJFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField6 = getCurrencyJFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();

        jComboBox1.setEditable(true);
        jComboBox1.setPreferredSize(new java.awt.Dimension(110, 24));
        ((AutoCompleteJComboBox)jComboBox1).attachStockInfoObserver(this.getStockInfoObserver());
        ((AutoCompleteJComboBox)jComboBox1).attachDispObserver(this.getDispObserver());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("NewBuyTransactionJDialog_Buy")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewBuyTransactionJDialog_Transaction"))); // NOI18N

        jLabel2.setText(bundle.getString("NewSellTransactionJDialog_Symbol")); // NOI18N

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(100.0d, 0.001d, null, 100.0d));
        JSpinner.NumberEditor numberEditor = (JSpinner.NumberEditor)jSpinner1.getEditor();
        final DecimalFormat decimalFormat = numberEditor.getFormat();
        decimalFormat.setMaximumFractionDigits(4);
        numberEditor.getTextField().addMouseListener(getJFormattedTextFieldMouseListener());
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel4.setText(bundle.getString("NewBuyTransactionJDialog_Date")); // NOI18N

        jFormattedTextField1.setValue(new Double(0.0));
        jFormattedTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField1KeyTyped(evt);
            }
        });

        jFormattedTextField2.setEditable(false);
        jFormattedTextField2.setValue(new Double(0.0));

        jFormattedTextField3.setValue(new Double(0.0));
        jFormattedTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField3KeyTyped(evt);
            }
        });

        jFormattedTextField4.setValue(new Double(0.0));
        jFormattedTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField4KeyTyped(evt);
            }
        });

        jFormattedTextField5.setValue(new Double(0.0));
        jFormattedTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField5KeyTyped(evt);
            }
        });

        jLabel10.setFont(jLabel10.getFont().deriveFont(jLabel10.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel10.setText(bundle.getString("NewBuyTransactionJDialog_NetValue")); // NOI18N

        jFormattedTextField6.setEditable(false);
        jFormattedTextField6.setFont(jFormattedTextField6.getFont().deriveFont(jFormattedTextField6.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField6.setValue(new Double(0.0));

        jLabel1.setText(bundle.getString("NewBuyTransactionJDialog_Unit")); // NOI18N

        jLabel3.setText(bundle.getString("NewBuyTransactionJDialog_Price")); // NOI18N

        jLabel5.setText(bundle.getString("NewBuyTransactionJDialog_Value")); // NOI18N

        jLabel7.setText(bundle.getString("NewBuyTransactionJDialog_Broker")); // NOI18N

        jLabel8.setText(bundle.getString("NewBuyTransactionJDialog_Clearing")); // NOI18N

        jLabel9.setText(bundle.getString("NewBuyTransactionJDialog_StampDuty")); // NOI18N

        jLabel11.setText(bundle.getString("NewBuyTransactionJDialog_Code")); // NOI18N

        jTextField2.setEditable(false);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jFormattedTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel11, jLabel2, jLabel3, jLabel4, jLabel5, jLabel7, jLabel8, jLabel9});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel9))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/apply.png"))); // NOI18N
        jButton1.setText(bundle.getString("NewBuyTransactionJDialog_OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/button_cancel.png"))); // NOI18N
        jButton2.setText(bundle.getString("NewBuyTransactionJDialog_Cancel")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewBuyTransactionJDialog_Stock"))); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/inbox.png"))); // NOI18N
        jPanel1.add(jLabel6);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.transaction = null;
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    public void setTransaction(Transaction transaction) {
        this.stockInfo = transaction.getStockInfo();
        final Code code = stockInfo.code;
        final Symbol symbol = stockInfo.symbol;
        final Date date = transaction.getDate().getCalendar().getTime();
        final double quantity = transaction.getQuantity();
        final double price = transaction.getPrice();
        final double value = transaction.getTotal();
        final double brokerFee = transaction.getBroker();
        final double clearingFee = transaction.getClearingFee();
        final double stampDuty = transaction.getStampDuty();
        final double netValue = transaction.getNetTotal();

        this.transactionComment = transaction.getComment();
        this.jTextField2.setText(code.toString());
        this.jTextField1.setText(symbol.toString());
        ((DateField)jPanel3).setValue(date);
        this.jSpinner1.setValue(quantity);
        this.jFormattedTextField1.setValue(price);
        this.jFormattedTextField2.setValue(value);
        this.jFormattedTextField3.setValue(brokerFee);
        this.jFormattedTextField4.setValue(clearingFee);
        this.jFormattedTextField5.setValue(stampDuty);
        this.jFormattedTextField6.setValue(netValue);
    }
    
    private Transaction generateTransaction() {
        final DateField dateField = (DateField)jPanel3;
        
        final SimpleDate date = new SimpleDate((Date)dateField.getValue());
        final Contract.Type type = Contract.Type.Buy;
        final double unit = ((java.lang.Double)this.jSpinner1.getValue());
        final double price = ((Double)this.jFormattedTextField1.getValue());
        
        Contract.ContractBuilder builder = new Contract.ContractBuilder(new StockInfo(stockInfo.code, Symbol.newInstance(jTextField1.getText().trim())), date);
        
        Contract contract = builder.type(type).quantity(unit).price(price).build();

        final double brokerFeeValue = (Double)this.jFormattedTextField3.getValue();
        final double stampDutyValue = (Double)jFormattedTextField5.getValue();
        final double clearingFeeValue = (Double)this.jFormattedTextField4.getValue();        
        
        Transaction t = new Transaction(contract, brokerFeeValue, stampDutyValue, clearingFeeValue);
        t.setComment(transactionComment);

        return t;
    }
    
    private boolean isValidInput() {
        if (NewBuyTransactionJDialog.this.stockInfo == null) {
            do {
                // Perhaps user forgets to press enter? Let us help him to transfer
                // the stock to text field.
                final Object object = this.jComboBox1.getItemAt(0);
                if (object != null) {
                    // There is item(s) in this combo box. Let's transfer the 1st
                    // item into text field.
                    JStock m = (JStock)NewBuyTransactionJDialog.this.getParent();

                    if (m == null) {
                        // Break and fall back to warning message box pop up 
                        // code.                        
                        break;
                    }

                    final StockInfoDatabase stockInfoDatabase = m.getStockInfoDatabase();

                    if (stockInfoDatabase == null) {
                        // Database is not ready yet. Shall we pop up a warning to
                        // user?
                        log.info("Database is not ready yet.");
                        // Break and fall back to warning message box pop up 
                        // code.
                        break;
                    }

                    final String string = object.toString().trim();
                    StockInfo stockInfo = stockInfoDatabase.searchStockInfo(string);
                    assert(stockInfo != null);
                    Code code = stockInfo.code;
                    Symbol symbol = stockInfo.symbol;
                    assert(symbol != null);
                    assert(code != null);
                    
                    NewBuyTransactionJDialog.this.stockInfo = StockInfo.newInstance(code, symbol);
                    this.jTextField2.setText(code.toString());
                    this.jTextField1.setText(symbol.toString());
                    // text fields now contain necessary info. Don't proceed first.
                    // We want to let user sees it clearly, and let him to press
                    // OK again.
                    return false;
                }
                // Break and fall back to warning message box pop up 
                // code.
                break;                    
            } while (true);
        }

        // Has user key in any stock?
        if (NewBuyTransactionJDialog.this.stockInfo == null)
        {
            this.jComboBox1.requestFocus();
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_please_enter_stock_symbol"), MessagesBundle.getString("warning_title_please_enter_stock_symbol"), JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (this.jTextField1.getText().trim().length() <= 0) {
            this.jTextField1.setText(this.stockInfo.symbol.toString());
            return false;
        }
        
        if (this.jFormattedTextField1.getText().length() <= 0) {
            this.jFormattedTextField1.requestFocus();
            return false;
        }

        if (this.jFormattedTextField2.getText().length() <= 0) {
            this.jFormattedTextField2.requestFocus();
            return false;
        }

        if (this.jFormattedTextField3.getText().length() <= 0) {
            this.jFormattedTextField3.requestFocus();
            return false;
        }

        if (this.jFormattedTextField4.getText().length() <= 0) {
            this.jFormattedTextField4.requestFocus();
            return false;
        }

        if (this.jFormattedTextField5.getText().length() <= 0) {
            this.jFormattedTextField5.requestFocus();
            return false;
        }

        if (this.jFormattedTextField6.getText().length() <= 0) {
            this.jFormattedTextField6.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (isValidInput() == false) {
            return;
        }
        
        commitEdit();
        this.transaction = generateTransaction();
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void commitEdit() {
        try {
            jFormattedTextField1.commitEdit();
            jFormattedTextField2.commitEdit();
            jFormattedTextField3.commitEdit();
            jFormattedTextField4.commitEdit();
            jFormattedTextField5.commitEdit();
            jFormattedTextField6.commitEdit();
        } catch (ParseException ex) {
            log.error(null, ex);
        }
    }
    
    private void update() {
        SwingUtilities.invokeLater(new Runnable() {@Override public void run() {
            _update();
        }});
    }
    
    private void _update() {
        // Commit the value first before updating. This is to prevent
        // double rounding issue. We force the current value to
        // follow the formatter text field's.
        commitEdit();
        
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();

        if (isFeeCalculationEnabled && shouldAutoCalculateBrokerFee())
        {
            final BrokingFirm brokingFirm = JStock.instance().getJStockOptions().getSelectedBrokingFirm();
            
            final String name = jTextField1.getText();
            final double unit = (Double)jSpinner1.getValue();
            final double price = (Double)jFormattedTextField1.getValue();
            final DateField dateField = (DateField)jPanel3;
            final Date date = (Date)dateField.getValue();
            // Stock and date information is not important at this moment.
            Contract.ContractBuilder builder = new Contract.ContractBuilder(StockInfo.newInstance(Code.newInstance(name), Symbol.newInstance(name)), new SimpleDate(date));        
            Contract contract = builder.type(Contract.Type.Buy).quantity(unit).price(price).build();

            final double brokerFee = brokingFirm.brokerCalculate(contract);
            final double clearingFee = brokingFirm.clearingFeeCalculate(contract);
            final double stampDuty = brokingFirm.stampDutyCalculate(contract);
            jFormattedTextField3.setValue(brokerFee);
            jFormattedTextField4.setValue(clearingFee);
            jFormattedTextField5.setValue(stampDuty);
            jFormattedTextField2.setValue(price * (double)unit);                
            jFormattedTextField6.setValue(price * (double)unit + brokerFee + clearingFee + stampDuty);
            
            this.jFormattedTextField3.setEditable(false);
            this.jFormattedTextField4.setEditable(false);
            this.jFormattedTextField5.setEditable(false);
        } else {
            final double unit = (Double)jSpinner1.getValue();
            final double price = (Double)jFormattedTextField1.getValue();
            final double brokerFee = (Double)jFormattedTextField3.getValue();
            final double clearingFee = (Double)jFormattedTextField4.getValue();
            final double stampDuty = (Double)jFormattedTextField5.getValue();
            jFormattedTextField2.setValue(price * (double)unit); 
            if (isFeeCalculationEnabled) {
                jFormattedTextField6.setValue(price * (double)unit + brokerFee + clearingFee + stampDuty);
            } else {
                jFormattedTextField6.setValue(price * (double)unit);
            }
            
            this.jFormattedTextField3.setEditable(true);
            this.jFormattedTextField4.setEditable(true);
            this.jFormattedTextField5.setEditable(true);
        }
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

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        dettachAllAndStopAutoCompleteJComboBox();
        
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();        
        if (isFeeCalculationEnabled) {
            if (jStockOptions.getBrokingFirmSize() < 1) {
                JStock.instance().getUIOptions().setDimension(UIOptions.NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE, getSize());
            } else {
                JStock.instance().getUIOptions().setDimension(UIOptions.NEW_BUY_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE, getSize());              
            }
        } else {
            JStock.instance().getUIOptions().setDimension(UIOptions.NEW_BUY_TRANSACTION_JDIALOG, getSize());            
        }        
    }//GEN-LAST:event_formWindowClosed

    private void jFormattedTextField5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField5KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField5KeyTyped

    private void jFormattedTextField4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField4KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField4KeyTyped

    private void jFormattedTextField3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField3KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField3KeyTyped

    private void jFormattedTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField1KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField1KeyTyped

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        update();
    }//GEN-LAST:event_jSpinner1StateChanged
        
    private MouseListener getJFormattedTextFieldMouseListener() {
        MouseListener ml = new MouseAdapter()
        {
            @Override
            public void mousePressed(final MouseEvent e)
            {
                if (e.getClickCount() == 2) {
                    // Ignore double click.
                    return;
                }
                
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JTextField tf = (JTextField)e.getSource();
                        int offset = tf.viewToModel(e.getPoint());
                        tf.setCaretPosition(offset);
                    }
                });
            }
        };
        return ml;
    }
    
    private JFormattedTextField getCurrencyJFormattedTextField() {
        NumberFormat format= NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(4);
        NumberFormatter formatter= new NumberFormatter(format);
        formatter.setMinimum(0.0);
        formatter.setValueClass(Double.class);
        JFormattedTextField formattedTextField = new JFormattedTextField(formatter);
        formattedTextField.addMouseListener(getJFormattedTextFieldMouseListener());
        return formattedTextField;
    }

    public void setPrice(double price) { 
        this.jFormattedTextField1.setValue(price);
        update();
    }
    
    public void setJComboBoxEnabled(boolean enable) {
        this.jComboBox1.setEnabled(enable);
    }
    
    public void setStockInfo(StockInfo stockInfo) {
        if (stockInfo != null) {
            Symbol symbol = stockInfo.symbol;
            Code code = stockInfo.code;
            this.jTextField1.setText(symbol.toString());
            this.jTextField2.setText(code.toString());
            // So that the 1st character is being displayed.
            this.jTextField1.setCaretPosition(0);
            this.jTextField2.setCaretPosition(0);       
        } else {
            this.jTextField1.setText("");
            this.jTextField2.setText("");            
        }
        this.stockInfo = stockInfo;
    }
    
    public void setStockSelectionEnabled(boolean flag) {
        jComboBox1.setEnabled(flag);
    }
    
    private void initAjaxProvider() {
        JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        
        Country country = jStockOptions.getCountry();
        
        final AutoCompleteJComboBox autoCompleteJComboBox = ((AutoCompleteJComboBox)this.jComboBox1);

        if (country == Country.India) {
            autoCompleteJComboBox.setGreedyEnabled(true, Arrays.asList("N", "B"));
        } else if (country == Country.Japan) {
            autoCompleteJComboBox.setGreedyEnabled(false, java.util.Collections.<String>emptyList());
        } else {
            autoCompleteJComboBox.setGreedyEnabled(false, java.util.Collections.<String>emptyList());
        }
    }
    
    public void setStockInfoDatabase(StockInfoDatabase stockInfoDatabase) {
        ((AutoCompleteJComboBox)jComboBox1).setStockInfoDatabase(stockInfoDatabase);
    }
        
    public Transaction getTransaction() {
        return this.transaction;
    }

    private org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, DispType> getDispObserver() {
        return new org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, DispType>() {

            @Override
            public void update(AutoCompleteJComboBox subject, DispType dispType) {
                assert(dispType != null);
                final Code code = Code.newInstance(dispType.getDispCode());
                final Symbol symbol = Symbol.newInstance(dispType.getDispName());
                final StockInfo stockInfo = StockInfo.newInstance(code, symbol);

                addStockInfoFromAutoCompleteJComboBox(stockInfo);
            }
        };
    }

    private org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, StockInfo> getStockInfoObserver() {
        return new org.yccheok.jstock.engine.Observer<AutoCompleteJComboBox, StockInfo>() {
            @Override
            public void update(AutoCompleteJComboBox subject, StockInfo stockInfo) {
                assert(stockInfo != null);
                addStockInfoFromAutoCompleteJComboBox(stockInfo);
            }
        };
    }

    private void addStockInfoFromAutoCompleteJComboBox(StockInfo stockInfo) {
        NewBuyTransactionJDialog.this.stockInfo = stockInfo;
        NewBuyTransactionJDialog.this.jTextField1.setText(NewBuyTransactionJDialog.this.stockInfo.symbol.toString());
        NewBuyTransactionJDialog.this.jTextField2.setText(NewBuyTransactionJDialog.this.stockInfo.code.toString());
        // So that the 1st character is being displayed.
        NewBuyTransactionJDialog.this.jTextField1.setCaretPosition(0);
        NewBuyTransactionJDialog.this.jTextField2.setCaretPosition(0);
    }

    private static final Log log = LogFactory.getLog(NewBuyTransactionJDialog.class);
    
    private Transaction transaction = null;
    private String transactionComment = "";
    private StockInfo stockInfo = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JFormattedTextField jFormattedTextField6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
    
}
