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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;
import javax.swing.SwingUtilities;
import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DateField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.Symbol;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.BrokingFirm;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.DecimalPlace;
import org.yccheok.jstock.portfolio.Transaction;

/**
 *
 * @author  yccheok
 */
public class NewSellTransactionJDialog extends javax.swing.JDialog {
    /** Creates new form NewSellTransactionJDialog */
    public NewSellTransactionJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        
        if (isFeeCalculationEnabled) {
            if (jStockOptions.getBrokingFirmSize() < 1) {
                initComponentsWithoutBrokerageFirm();
                
                Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE);
                if (dimension != null) {
                    setSize(dimension);
                }                
            } else {
                initComponents();
                
                Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE);
                if (dimension != null) {
                    setSize(dimension);
                }                
            }
        } else {
            initComponentsWithFeeCalculationDisabled();
            
            Dimension dimension = JStock.instance().getUIOptions().getDimension(UIOptions.NEW_SELL_TRANSACTION_JDIALOG);
            if (dimension != null) {
                setSize(dimension);
            }             
        }
        
        if (shouldAutoCalculateBrokerFee())
        {
            this.jFormattedTextField4.setEditable(false);
            this.jFormattedTextField5.setEditable(false);
            this.jFormattedTextField7.setEditable(false);
        }
        
        initBrokerCostsComboBox();
    }

    private void initComponentsWithoutBrokerageFirm() {
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel3 = CalendarFactory.createDateField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = getCurrencyJFormattedTextField(false);
        jFormattedTextField2 = getCurrencyJFormattedTextField(false);
        jFormattedTextField3 = getCurrencyJFormattedTextField(false);
        jFormattedTextField4 = getCurrencyJFormattedTextField(false);
        jFormattedTextField5 = getCurrencyJFormattedTextField(false);
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField6 = getPercentageJFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextField7 = getCurrencyJFormattedTextField(false);
        jLabel11 = new javax.swing.JLabel();
        jFormattedTextField8 = getCurrencyJFormattedTextField(true);
        jLabel12 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("NewSellTransactionJDialog_Sell")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewSellTransactionJDialog_Transaction"))); // NOI18N

        jTextField1.setEditable(false);

        jLabel2.setText(bundle.getString("NewBuyTransactionJDialog_Symbol")); // NOI18N

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

        jFormattedTextField3.setEditable(false);
        jFormattedTextField3.setValue(new Double(0.0));

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
        jLabel10.setText(bundle.getString("NewSellTransactionJDialog_Profit%")); // NOI18N

        jFormattedTextField6.setEditable(false);
        jFormattedTextField6.setFont(jFormattedTextField6.getFont().deriveFont(jFormattedTextField6.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField6.setValue(new Double(0.0));

        jLabel1.setText(bundle.getString("NewSellTransactionJDialog_Unit")); // NOI18N

        jLabel3.setText(bundle.getString("NewSellTransactionJDialog_Price")); // NOI18N

        jLabel5.setText(bundle.getString("NewSellTransactionJDialog_Value")); // NOI18N

        jLabel7.setText(bundle.getString("NewSellTransactionJDialog_BuyCost")); // NOI18N

        jLabel8.setText(bundle.getString("NewSellTransactionJDialog_Broker")); // NOI18N

        jLabel9.setText(bundle.getString("NewSellTransactionJDialog_Clearing")); // NOI18N

        jFormattedTextField7.setValue(new Double(0.0));
        jFormattedTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField7KeyTyped(evt);
            }
        });

        jLabel11.setText(bundle.getString("NewSellTransactionJDialog_StampDuty")); // NOI18N

        jFormattedTextField8.setEditable(false);
        jFormattedTextField8.setFont(jFormattedTextField8.getFont().deriveFont(jFormattedTextField8.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField8.setValue(new Double(0.0));

        jLabel12.setFont(jLabel12.getFont().deriveFont(jLabel12.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel12.setText(bundle.getString("NewSellTransactionJDialog_Profit$")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jFormattedTextField7, jFormattedTextField8, jPanel3, jSpinner1, jTextField1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel11, jLabel12, jLabel2, jLabel3, jLabel4, jLabel5, jLabel7, jLabel8, jLabel9});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jFormattedTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jFormattedTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/apply.png"))); // NOI18N
        jButton1.setText(bundle.getString("NewSellTransactionJDialog_OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/button_cancel.png"))); // NOI18N
        jButton2.setText(bundle.getString("NewSellTransactionJDialog_Cancel")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewSellTransactionJDialog_Stock"))); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/outbox.png"))); // NOI18N
        jPanel1.add(jLabel6);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/idea.png"))); // NOI18N
        jButton3.setText(bundle.getString("NewSellTransactionJDialog_BestPrice")); // NOI18N
        jButton3.setToolTipText("Suggest the best selling price to break even.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();
    }
               
     private void initBrokerCostsComboBox() {
         if (jComboBox1 == null) {
             return;
         }
         
         DefaultComboBoxModel model = new DefaultComboBoxModel();
         
         final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
         final java.util.List<BrokingFirm> brokerFirms = jStockOptions.getBrokingFirms();
         
         for (BrokingFirm brokingFirm : brokerFirms) {
             model.addElement(brokingFirm.getName());
         }             
         model.addElement(GUIBundle.getString("NewSellTransactionJDialog_None"));
         
         jComboBox1.setModel(model);
         jComboBox1.addItemListener((java.awt.event.ItemEvent e) -> {
             int index = jComboBox1.getSelectedIndex();
             
             jStockOptions.setSelectedBrokingFirmIndex(index);
             update();
         });
         
         // Ensure valid selection.
         int index = jStockOptions.getSelectedBrokingFirmIndex();
         if (index < 0 || index >= model.getSize()) {
             index = model.getSize() - 1;
             jStockOptions.setSelectedBrokingFirmIndex(index);
         }
         
         jComboBox1.setSelectedIndex(index);
         jComboBox1.setPrototypeDisplayValue("a");
         
         BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
         jComboBox1.addPopupMenuListener(listener);
    }

    private void setMaxSellQuantity(double maxSellQuantity) {
        SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)jSpinner1.getModel();
        spinnerNumberModel.setMaximum(maxSellQuantity);
    }

    private boolean shouldAutoCalculateBrokerFee() {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        
        return jStockOptions.getSelectedBrokingFirm() != null;
    }
    
    private void initComponentsWithFeeCalculationDisabled() {
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel3 = CalendarFactory.createDateField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = getCurrencyJFormattedTextField(false);
        jFormattedTextField2 = getCurrencyJFormattedTextField(false);
        jFormattedTextField3 = getCurrencyJFormattedTextField(false);
        
        jFormattedTextField4 = getCurrencyJFormattedTextField(false);
        jFormattedTextField5 = getCurrencyJFormattedTextField(false);
        jFormattedTextField7 = getCurrencyJFormattedTextField(false);
        jFormattedTextField4.setValue(new Double(0.0));
        jFormattedTextField5.setValue(new Double(0.0));
        jFormattedTextField7.setValue(new Double(0.0));

        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField6 = getPercentageJFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jFormattedTextField8 = getCurrencyJFormattedTextField(true);
        jLabel12 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("NewSellTransactionJDialog_Sell")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewSellTransactionJDialog_Transaction"))); // NOI18N

        jTextField1.setEditable(false);

        jLabel2.setText(bundle.getString("NewBuyTransactionJDialog_Symbol")); // NOI18N

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

        jFormattedTextField3.setEditable(false);
        jFormattedTextField3.setValue(new Double(0.0));

        jLabel10.setFont(jLabel10.getFont().deriveFont(jLabel10.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel10.setText(bundle.getString("NewSellTransactionJDialog_Profit%")); // NOI18N

        jFormattedTextField6.setEditable(false);
        jFormattedTextField6.setFont(jFormattedTextField6.getFont().deriveFont(jFormattedTextField6.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField6.setValue(new Double(0.0));

        jLabel1.setText(bundle.getString("NewSellTransactionJDialog_Unit")); // NOI18N

        jLabel3.setText(bundle.getString("NewSellTransactionJDialog_Price")); // NOI18N

        jLabel5.setText(bundle.getString("NewSellTransactionJDialog_Value")); // NOI18N

        jLabel7.setText(bundle.getString("NewSellTransactionJDialog_BuyCost")); // NOI18N

        jFormattedTextField8.setEditable(false);
        jFormattedTextField8.setFont(jFormattedTextField8.getFont().deriveFont(jFormattedTextField8.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField8.setValue(new Double(0.0));

        jLabel12.setFont(jLabel12.getFont().deriveFont(jLabel12.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel12.setText(bundle.getString("NewSellTransactionJDialog_Profit$")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel12)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField6, jFormattedTextField8, jPanel3, jSpinner1, jTextField1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel12, jLabel2, jLabel3, jLabel4, jLabel5, jLabel7});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jFormattedTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/apply.png"))); // NOI18N
        jButton1.setText(bundle.getString("NewSellTransactionJDialog_OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/button_cancel.png"))); // NOI18N
        jButton2.setText(bundle.getString("NewSellTransactionJDialog_Cancel")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewSellTransactionJDialog_Stock"))); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/outbox.png"))); // NOI18N
        jPanel1.add(jLabel6);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/idea.png"))); // NOI18N
        jButton3.setText(bundle.getString("NewSellTransactionJDialog_BestPrice")); // NOI18N
        jButton3.setToolTipText("Suggest the best selling price to break even.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel3 = CalendarFactory.createDateField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = getCurrencyJFormattedTextField(false);
        jFormattedTextField2 = getCurrencyJFormattedTextField(false);
        jFormattedTextField3 = getCurrencyJFormattedTextField(false);
        jFormattedTextField4 = getCurrencyJFormattedTextField(false);
        jFormattedTextField5 = getCurrencyJFormattedTextField(false);
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField6 = getPercentageJFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextField7 = getCurrencyJFormattedTextField(false);
        jLabel11 = new javax.swing.JLabel();
        jFormattedTextField8 = getCurrencyJFormattedTextField(true);
        jLabel12 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("NewSellTransactionJDialog_Sell")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewSellTransactionJDialog_Transaction"))); // NOI18N

        jTextField1.setEditable(false);

        jLabel2.setText(bundle.getString("NewBuyTransactionJDialog_Symbol")); // NOI18N

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

        jFormattedTextField3.setEditable(false);
        jFormattedTextField3.setValue(new Double(0.0));

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
        jLabel10.setText(bundle.getString("NewSellTransactionJDialog_Profit%")); // NOI18N

        jFormattedTextField6.setEditable(false);
        jFormattedTextField6.setFont(jFormattedTextField6.getFont().deriveFont(jFormattedTextField6.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField6.setValue(new Double(0.0));

        jLabel1.setText(bundle.getString("NewSellTransactionJDialog_Unit")); // NOI18N

        jLabel3.setText(bundle.getString("NewSellTransactionJDialog_Price")); // NOI18N

        jLabel5.setText(bundle.getString("NewSellTransactionJDialog_Value")); // NOI18N

        jLabel7.setText(bundle.getString("NewSellTransactionJDialog_BuyCost")); // NOI18N

        jLabel8.setText(bundle.getString("NewSellTransactionJDialog_Broker")); // NOI18N

        jLabel9.setText(bundle.getString("NewSellTransactionJDialog_Clearing")); // NOI18N

        jFormattedTextField7.setValue(new Double(0.0));
        jFormattedTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextField7KeyTyped(evt);
            }
        });

        jLabel11.setText(bundle.getString("NewSellTransactionJDialog_StampDuty")); // NOI18N

        jFormattedTextField8.setEditable(false);
        jFormattedTextField8.setFont(jFormattedTextField8.getFont().deriveFont(jFormattedTextField8.getFont().getStyle() | java.awt.Font.BOLD));
        jFormattedTextField8.setValue(new Double(0.0));

        jLabel12.setFont(jLabel12.getFont().deriveFont(jLabel12.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel12.setText(bundle.getString("NewSellTransactionJDialog_Profit$")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jFormattedTextField7, jFormattedTextField8, jPanel3, jSpinner1, jTextField1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel11, jLabel12, jLabel2, jLabel3, jLabel4, jLabel5, jLabel7, jLabel8, jLabel9});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jFormattedTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jFormattedTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField2, jFormattedTextField3, jFormattedTextField4, jFormattedTextField5, jFormattedTextField6, jPanel3, jSpinner1, jTextField1});

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/apply.png"))); // NOI18N
        jButton1.setText(bundle.getString("NewSellTransactionJDialog_OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/button_cancel.png"))); // NOI18N
        jButton2.setText(bundle.getString("NewSellTransactionJDialog_Cancel")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("NewSellTransactionJDialog_Stock"))); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/outbox.png"))); // NOI18N
        jPanel1.add(jLabel6);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/idea.png"))); // NOI18N
        jButton3.setText(bundle.getString("NewSellTransactionJDialog_BestPrice")); // NOI18N
        jButton3.setToolTipText("Suggest the best selling price to break even.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        resultSellTransactions.clear();
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    public void setBuyTransactions(List<Transaction> transactions) {
        // Ensure we only perform batch update.
        if (this.sellTransaction != null) {
            throw new java.lang.UnsupportedOperationException("You cannot sell transaction and edit transaction at the same time");
        }

        buyTransactions.clear();
        buyTransactions.addAll(transactions);

        final StockInfo _stockInfo = transactions.get(0).getStockInfo();
        final Symbol symbol = _stockInfo.symbol;
        final Date date = java.util.Calendar.getInstance().getTime();

        JStock mainFrame = JStock.instance();
        double price = mainFrame.getPortfolioManagementJPanel().getStockPrice(_stockInfo.code);

        this.jTextField1.setText(symbol.toString());
        // So that the 1st character is being displayed.
        this.jTextField1.setCaretPosition(0);
        ((DateField)jPanel3).setValue(date);
        
        double quantity = 0.0;
        for (Transaction transaction : transactions) {
            quantity += transaction.getQuantity();
        }        
        this.jSpinner1.setValue(quantity);
        
        this.jFormattedTextField1.setValue(price);

        // Limit maximum sell quantity.
        setMaxSellQuantity(quantity);

        this.stockInfo = _stockInfo;
        this.type = Contract.Type.Sell;

        updateBuyValueAfterSpinner(quantity);
        update();
    }

    private void updateBuyValueAfterSpinner(double spinnerQuantity) {
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        
        double sellQuantity = spinnerQuantity;
        
        // Edit
        if (this.sellTransaction != null) {
            if (isFeeCalculationEnabled) {
                // Do not use "sellQuantity * this.sellTransaction.getNetReferenceTotal() / this.sellTransaction.getQuantity();"
                // It will create too many decimal places.
                this.buyValue = sellQuantity * this.sellTransaction.getReferencePrice() + this.sellTransaction.getReferenceBroker() + this.sellTransaction.getReferenceClearingFee() + this.sellTransaction.getReferenceStampDuty();
            } else {
                this.buyValue = sellQuantity * this.sellTransaction.getReferencePrice();
            }
            // Return early.
            return;
        }
        
        double _buyValue = 0.0;
        for (Transaction buyTransaction : buyTransactions) {
            if (sellQuantity >= buyTransaction.getQuantity()) {
                if (isFeeCalculationEnabled) {
                    _buyValue += buyTransaction.getNetTotal();
                } else {
                    _buyValue += buyTransaction.getTotal();
                }
                sellQuantity -= buyTransaction.getQuantity();
            } else {
                if (org.yccheok.jstock.portfolio.Utils.definitelyGreaterThan(sellQuantity, 0)) {
                    if (isFeeCalculationEnabled) {
                        // Do not use getNetPrice, as it might return bad value
                        // like 1.333... which is difficult to display in UI.
                        _buyValue += (buyTransaction.getPrice() * sellQuantity) + 
                                getGoodCurrencyDouble(buyTransaction.getBroker(), sellQuantity, buyTransaction.getQuantity()) +
                                getGoodCurrencyDouble(buyTransaction.getClearingFee(), sellQuantity, buyTransaction.getQuantity()) +
                                getGoodCurrencyDouble(buyTransaction.getStampDuty(), sellQuantity, buyTransaction.getQuantity());
                    } else {
                        _buyValue += (buyTransaction.getPrice() * sellQuantity);
                    }
                }
                sellQuantity = 0;
                break;
            }
        }        
        this.buyValue = _buyValue;
    }
    
    // Instead of returning 1.3333..., we will return 1.333
    private double getGoodCurrencyDouble(double value, double sellQuantity, double buyQuantity) {
        assert(sellQuantity <= buyQuantity);
        assert(org.yccheok.jstock.portfolio.Utils.definitelyGreaterThan(sellQuantity, 0));
        assert(org.yccheok.jstock.portfolio.Utils.definitelyGreaterThan(buyQuantity, 0));

        if (org.yccheok.jstock.portfolio.Utils.essentiallyEqual(sellQuantity, buyQuantity)) {
            return value;
        }
        
        // Do not use Utils.toCurrency(DecimalPlaces.Three, ...
        // Double.parseDouble is not able to handle it well when localization is in french.
        //
        // Use Three instead of Four. The idea is, shorter is better.
        // Is it good to use isFourDecimalPlacesEnabled right here???
        final String text = org.yccheok.jstock.portfolio.Utils.toEditCurrency(DecimalPlace.Three, sellQuantity /  buyQuantity * value);
        return Double.parseDouble(text);
    }
    
    private BigDecimal getGoodCurrencyBigDecimal(double value, double sellQuantity, double buyQuantity) {
        return new BigDecimal(getGoodCurrencyDouble(value, sellQuantity, buyQuantity));
    }
    
    public void setSellTransaction(Transaction transaction) {
        assert(transaction.getType() == Contract.Type.Sell);

        // Ensure we only edit.
        if (this.buyTransactions.size() > 0) {
            throw new java.lang.UnsupportedOperationException("You cannot sell transaction and edit transaction at the same time");
        }

        if (transaction.getType() != Contract.Type.Sell) {
            throw new java.lang.UnsupportedOperationException("You can only edit sell transaction");
        }

        this.sellTransaction = transaction;

        final StockInfo _stockInfo = transaction.getStockInfo();
        final Symbol symbol = _stockInfo.symbol;
        final Date date = transaction.getDate().getCalendar().getTime();
        final double quantity = transaction.getQuantity();
        final double price = transaction.getPrice();
        final double value = transaction.getTotal();
        final double brokerFee = transaction.getBroker();
        final double clearingFee = transaction.getClearingFee();
        final double stampDuty = transaction.getStampDuty();
        
        this.jTextField1.setText(symbol.toString());
        ((DateField)jPanel3).setValue(date);
        this.jSpinner1.setValue(quantity);
        this.jFormattedTextField1.setValue(price);
        this.jFormattedTextField2.setValue(value);
        this.jFormattedTextField4.setValue(brokerFee);
        this.jFormattedTextField5.setValue(clearingFee);
        this.jFormattedTextField7.setValue(stampDuty);

        this.stockInfo = transaction.getStockInfo();
        this.type = Contract.Type.Sell;
        
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();
        if (isFeeCalculationEnabled) {
            this.buyValue = transaction.getNetReferenceTotal();
        } else {
            this.buyValue = transaction.getReferenceTotal();
        }

        update();
    }
    
    private List<Transaction> generateTransactions() {
        if (this.sellTransaction != null && this.buyTransactions.size() > 0) {
            throw new java.lang.UnsupportedOperationException("You cannot sell transaction and edit transaction at the same time");
        }

        final List<Transaction> transactions = new ArrayList<Transaction>();

        if (this.sellTransaction != null)
        {
            // Editing...
            
            final DateField dateField = (DateField)jPanel3;
            final SimpleDate date = new SimpleDate((Date)dateField.getValue());
            final double unit = ((java.lang.Double)this.jSpinner1.getValue());
            final double price = ((Double)this.jFormattedTextField1.getValue());
            Contract.ContractBuilder builder = new Contract.ContractBuilder(this.sellTransaction.getStockInfo(), date);
            final Contract oldContract = this.sellTransaction.getContract();
            final Contract contract = builder.type(type).quantity(unit).price(price).referencePrice(oldContract.getReferencePrice())
                    .referenceBroker(oldContract.getReferenceBroker())
                    .referenceClearingFee(oldContract.getReferenceClearingFee())
                    .referenceStampDuty(oldContract.getReferenceStampDuty())
                    .referenceDate(oldContract.getReferenceDate()).build();
                
            final double brokerFeeValue = (Double)this.jFormattedTextField4.getValue();
            final double stampDutyValue = (Double)jFormattedTextField7.getValue();
            final double clearingFeeValue = (Double)this.jFormattedTextField5.getValue();

            Transaction t = new Transaction(contract, brokerFeeValue, stampDutyValue, clearingFeeValue);
            t.setComment(this.sellTransaction.getComment());
            transactions.add(t);
        }
        else {
            final DateField dateField = (DateField)jPanel3;
            final SimpleDate date = new SimpleDate((Date)dateField.getValue());
            final double unit = ((java.lang.Double)this.jSpinner1.getValue());
            final double price = ((Double)this.jFormattedTextField1.getValue());
            final double brokerFee = (Double)this.jFormattedTextField4.getValue();
            final double stampDuty = (Double)jFormattedTextField7.getValue();
            final double clearingFee = (Double)this.jFormattedTextField5.getValue();

            // Use BigDecimal, for precision purpose. Please refer to Effective Java,
            // Item 48: Avoid float and double if exact answers are required            
            BigDecimal quantity = new BigDecimal(unit);
            boolean shouldBreakInNextRound = false;
            
            int i = 0;
            BigDecimal totalBrokerFee = new BigDecimal(0);
            BigDecimal totalStampDuty = new BigDecimal(0);
            BigDecimal totalClearingFee = new BigDecimal(0);

            
            for (Transaction buyTransaction : this.buyTransactions) {
                i++;
                
                if (shouldBreakInNextRound) {
                    break;                    
                }
                
                double realTransactionQuantity = 0.0;
                if (quantity.doubleValue() >= buyTransaction.getQuantity()) {
                    realTransactionQuantity = buyTransaction.getQuantity();
                    quantity = quantity.subtract(new BigDecimal(buyTransaction.getQuantity()));
                } else {
                    realTransactionQuantity = quantity.doubleValue();
                    quantity = BigDecimal.ZERO;
                    shouldBreakInNextRound = true;
                    
                    // 0 quantity. Break early.
                    if (false == org.yccheok.jstock.portfolio.Utils.definitelyGreaterThan(realTransactionQuantity, 0)) {
                        break;
                    }
                }

                Contract.ContractBuilder builder = new Contract.ContractBuilder(buyTransaction.getStockInfo(), date);
                final double referenceBroker = buyTransaction.getBroker();
                final double referenceClearingFee = buyTransaction.getClearingFee();
                final double referenceStampDuty = buyTransaction.getStampDuty();
                final double goodReferenceBroker = getGoodCurrencyDouble(referenceBroker, realTransactionQuantity, buyTransaction.getQuantity());
                final double goodReferenceClearingFee = getGoodCurrencyDouble(referenceClearingFee, realTransactionQuantity, buyTransaction.getQuantity());
                final double goodReferenceStampDuty = getGoodCurrencyDouble(referenceStampDuty, realTransactionQuantity, buyTransaction.getQuantity());
                
                final Contract contract = builder.type(type).quantity(realTransactionQuantity).price(price)
                        .referencePrice(buyTransaction.getPrice())
                        .referenceBroker(goodReferenceBroker)
                        .referenceClearingFee(goodReferenceClearingFee)
                        .referenceStampDuty(goodReferenceStampDuty)
                        .referenceDate(buyTransaction.getDate()).build();
                
                // Do not use the following code, as our objective is
                // 1) Sum of individual transactions' fee must be equal to 
                // transaction summary's fee.
                // 2) Three decimal places.
                // 
                //final double brokerFeeValue = brokerFee * realTransactionQuantity / unit;
                //final double stampDutyValue = stampDuty * realTransactionQuantity / unit;
                //final double clearingFeeValue = clearingFee * realTransactionQuantity / unit;
                       
                // Use BigDecimal, for precision purpose. Please refer to Effective Java,
                // Item 48: Avoid float and double if exact answers are required                
                BigDecimal brokerFeeBigDecimal;
                BigDecimal stampDutyBigDecimal;
                BigDecimal clearingFeeBigDecimal;
                // Is this last item?
                if (i != buyTransactions.size()) {                    
                    brokerFeeBigDecimal = getGoodCurrencyBigDecimal(brokerFee, realTransactionQuantity, unit);
                    stampDutyBigDecimal = getGoodCurrencyBigDecimal(stampDuty, realTransactionQuantity, unit);
                    clearingFeeBigDecimal = getGoodCurrencyBigDecimal(clearingFee, realTransactionQuantity, unit);
                    
                    totalBrokerFee = totalBrokerFee.add(brokerFeeBigDecimal);
                    totalStampDuty = totalStampDuty.add(stampDutyBigDecimal);
                    totalClearingFee = totalClearingFee.add(clearingFeeBigDecimal);
                } else {
                    // This is the last item.
                    brokerFeeBigDecimal = new BigDecimal(brokerFee).subtract(totalBrokerFee).max(BigDecimal.ZERO);
                    stampDutyBigDecimal = new BigDecimal(stampDuty).subtract(totalStampDuty).max(BigDecimal.ZERO);
                    clearingFeeBigDecimal = new BigDecimal(clearingFee).subtract(totalClearingFee).max(BigDecimal.ZERO);
                }
                
                // Do not pass in brokerFee, stampDuty and clearingFee.
                Transaction t = new Transaction(contract, brokerFeeBigDecimal.doubleValue(), stampDutyBigDecimal.doubleValue(), clearingFeeBigDecimal.doubleValue());
                t.setComment(buyTransaction.getComment());
                transactions.add(t);
            }
        }

        return transactions;
    }
    
    private boolean isValidInput() {
        
        if(this.jFormattedTextField1.getText().length() <= 0) {
            this.jFormattedTextField1.requestFocus();
            return false;
        }

        if(this.jFormattedTextField2.getText().length() <= 0) {
            this.jFormattedTextField2.requestFocus();
            return false;
        }

        if(this.jFormattedTextField4.getText().length() <= 0) {
            this.jFormattedTextField4.requestFocus();
            return false;
        }

        if(this.jFormattedTextField5.getText().length() <= 0) {
            this.jFormattedTextField5.requestFocus();
            return false;
        }

        if(this.jFormattedTextField7.getText().length() <= 0) {
            this.jFormattedTextField7.requestFocus();
            return false;
        }

        if(this.jFormattedTextField6.getText().length() <= 0) {
            this.jFormattedTextField6.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(isValidInput() == false) {
            return;
        }
        
        commitEdit();
        this.resultSellTransactions = generateTransactions();
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            Contract contract = builder.type(Contract.Type.Sell).quantity(unit).price(price).build();

            final double brokerFee = brokingFirm.brokerCalculate(contract);
            final double clearingFee = brokingFirm.clearingFeeCalculate(contract);
            final double stampDuty = brokingFirm.stampDutyCalculate(contract);
            jFormattedTextField4.setValue(brokerFee);
            jFormattedTextField5.setValue(clearingFee);
            jFormattedTextField7.setValue(stampDuty);

            double sellValue = price * (double)unit;
            if (isFeeCalculationEnabled) {
                sellValue = sellValue - brokerFee - clearingFee - stampDuty;
            }                
            final double totalCost = NewSellTransactionJDialog.this.buyValue;
            final double netProfit = sellValue - totalCost;
            final double netProfitPercentage = (totalCost == 0.0) ? 0.0 : netProfit / totalCost * 100.0;

            jFormattedTextField2.setValue(sellValue);
            jFormattedTextField3.setValue(totalCost);
            jFormattedTextField6.setValue(netProfitPercentage);
            jFormattedTextField6.setForeground(Utils.getColor(netProfitPercentage, 0.0));
            jFormattedTextField8.setValue(netProfit);
            jFormattedTextField8.setForeground(Utils.getColor(netProfit, 0.0));
            
            this.jFormattedTextField4.setEditable(false);
            this.jFormattedTextField5.setEditable(false);
            this.jFormattedTextField7.setEditable(false);
        } else {
            final double unit = (Double)jSpinner1.getValue();
            final double price = (Double)jFormattedTextField1.getValue();
            final double brokerFee = (Double)jFormattedTextField4.getValue();
            final double clearingFee = (Double)jFormattedTextField5.getValue();
            final double stampDuty = (Double)jFormattedTextField7.getValue();

            double sellValue = price * unit;
            if (isFeeCalculationEnabled) {
                sellValue = sellValue - brokerFee - clearingFee - stampDuty;
            }
            final double totalCost = NewSellTransactionJDialog.this.buyValue;
            final double netProfit = sellValue - totalCost;
            final double netProfitPercentage = (totalCost == 0.0) ? 0.0 : netProfit / totalCost * 100.0;

            jFormattedTextField2.setValue(sellValue);
            jFormattedTextField3.setValue(totalCost);
            jFormattedTextField6.setValue(netProfitPercentage);
            jFormattedTextField6.setForeground(Utils.getColor(netProfitPercentage, 0.0));
            jFormattedTextField8.setValue(netProfit);
            jFormattedTextField8.setForeground(Utils.getColor(netProfit, 0.0));
            
            this.jFormattedTextField4.setEditable(true);
            this.jFormattedTextField5.setEditable(true);
            this.jFormattedTextField7.setEditable(true);
        }
    }
    
    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        // TODO add your handling code here: 
        updateBuyValueAfterSpinner((Double)jSpinner1.getValue());
        update();
    }//GEN-LAST:event_jSpinner1StateChanged

    private void setPrice(double price) {
        this.jFormattedTextField1.setValue(price);
        update();
    }

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		// TODO add your handling code here:
    	this.setPrice(this.suggestBestSellingPrice());
	}//GEN-LAST:event_jButton3ActionPerformed

    private void jFormattedTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField1KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField1KeyTyped

    private void jFormattedTextField4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField4KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField4KeyTyped

    private void jFormattedTextField5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField5KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField5KeyTyped

    private void jFormattedTextField7KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField7KeyTyped
        update();
    }//GEN-LAST:event_jFormattedTextField7KeyTyped

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final boolean isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled();        
        if (isFeeCalculationEnabled) {
            if (jStockOptions.getBrokingFirmSize() < 1) {
                JStock.instance().getUIOptions().setDimension(UIOptions.NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE, getSize());
            } else {
                JStock.instance().getUIOptions().setDimension(UIOptions.NEW_SELL_TRANSACTION_JDIALOG_WITH_FEE_AND_BROKERAGE, getSize());              
            }
        } else {
            JStock.instance().getUIOptions().setDimension(UIOptions.NEW_SELL_TRANSACTION_JDIALOG, getSize());            
        }  
    }//GEN-LAST:event_formWindowClosed
    
    private void commitEdit() {
        try {
            jFormattedTextField1.commitEdit();
            jFormattedTextField2.commitEdit();
            jFormattedTextField3.commitEdit();
            jFormattedTextField4.commitEdit();
            jFormattedTextField5.commitEdit();
            jFormattedTextField6.commitEdit();
            jFormattedTextField7.commitEdit();
            jFormattedTextField8.commitEdit();
        } catch (ParseException ex) {
            log.error(null, ex);
        }
    }

    private JFormattedTextField getPercentageJFormattedTextField() {
        NumberFormat format= NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        NumberFormatter formatter= new NumberFormatter(format);        
        formatter.setValueClass(Double.class);
        JFormattedTextField formattedTextField= new JFormattedTextField(formatter);        
        return formattedTextField;
    }
    
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
    
    private JFormattedTextField getCurrencyJFormattedTextField(boolean isNegativeAllowed) {
        NumberFormat format= NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(4);
        NumberFormatter formatter= new NumberFormatter(format);
        
        if (isNegativeAllowed == false)
            formatter.setMinimum(0.0);
        else
            formatter.setMinimum(null);
        
        formatter.setValueClass(Double.class);
        JFormattedTextField formattedTextField= new JFormattedTextField(formatter);
        formattedTextField.addMouseListener(getJFormattedTextFieldMouseListener());
        return formattedTextField;
    }
    
    public List<Transaction> getTransactions() {
        return this.resultSellTransactions;
    }

    public double suggestBestSellingPrice() {
        final double expectedProfitPercentage = JStock.instance().getJStockOptions().getExpectedProfitPercentage();
        
        final double unit = (Double)jSpinner1.getValue();
        
        if (unit <= 0.0) {
            return 0.0;
        }
        
        final double brokerFee = (Double)jFormattedTextField4.getValue();
        final double clearingFee = (Double)jFormattedTextField5.getValue();
        final double stampDuty = (Double)jFormattedTextField7.getValue();
                
        final double _buyValue = this.buyValue;
        final double totalCost = _buyValue + brokerFee + clearingFee + stampDuty;
        
        final double bestSellingValue = (1.0 + expectedProfitPercentage / 100.0) * totalCost;
        final double bestPrice = bestSellingValue / (double)unit;

        JStock mainFrame = JStock.instance();
        double currentPrice = mainFrame.getPortfolioManagementJPanel().getStockPrice(stockInfo.code);

        return bestPrice > currentPrice ? bestPrice : currentPrice;
    }
    
    private static final Log log = LogFactory.getLog(NewSellTransactionJDialog.class);

    /* For edit purpose on sell transaction */
    private Transaction sellTransaction = null;
    /* For single selling, batch selling purpose. */
    private List<Transaction> buyTransactions = new ArrayList<Transaction>();
    private List<Transaction> resultSellTransactions = new ArrayList<Transaction>();

    private StockInfo stockInfo;        // immutable.
    // private SimpleDate date;         // mutable
    private Contract.Type type;         // immutable
    // private int quantity;            // mutable for edit. immutable for batch update.
    // private double price;            // mutable
    private double buyValue = 0.0;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JFormattedTextField jFormattedTextField6;
    private javax.swing.JFormattedTextField jFormattedTextField7;
    private javax.swing.JFormattedTextField jFormattedTextField8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    // End of variables declaration//GEN-END:variables
    
}
