/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng Cheok <yccheok@yahoo.com>
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

import org.yccheok.jstock.gui.analysis.ObjectInspectorJPanel;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardModel;
import com.nexes.wizard.WizardPanelDescriptor;
import org.yccheok.jstock.gui.analysis.OperatorFigureCreationTool;
import org.jhotdraw.util.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.*;
import org.yccheok.jstock.engine.*;
import org.yccheok.jstock.analysis.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.gui.analysis.WizardSelectIndicatorDescriptor;
import org.yccheok.jstock.gui.analysis.WizardDownloadIndicatorDescriptor;
import org.yccheok.jstock.gui.analysis.WizardSelectInstallIndicatorMethodDescriptor;
import org.yccheok.jstock.gui.analysis.WizardSelectInstallIndicatorMethodJPanel;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;

/**
 * PertPanel.
 * 
 * @author Werner Randelshofer
 * @version 1.0 2006-07-15 Created.
 */
public class IndicatorPanel extends JPanel {
    private DrawingEditor editor;
    
    /** Creates new instance. */
    public IndicatorPanel() {    
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        initComponents();
        this.jPanel7.add(Utils.getBusyJXLayer((AutoCompleteJComboBox)this.jComboBox1));
        
        editor = new DefaultDrawingEditor();
        editor.add(view);
        
        addCreationButtonsTo(creationToolbar, editor);

        JPopupButton pb = new JPopupButton();
        pb.setItemFont(UIManager.getFont("MenuItem.font"));
        labels.configureToolBarButton(pb, "actions");
        pb.add(new GroupAction(editor));
        pb.add(new UngroupAction(editor));
        pb.addSeparator();
        pb.add(new BringToFrontAction(editor));
        pb.add(new SendToBackAction(editor));
        pb.addSeparator();
        pb.add(new SelectAllAction());
        pb.add(new SelectSameAction(editor));
        pb.addSeparator();
        pb.add(new ToggleGridAction(editor));

        JMenu m = new JMenu(labels.getString("zoom"));
        JRadioButtonMenuItem rbmi;
        ButtonGroup group = new ButtonGroup();
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.1, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.25, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.5, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.75, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 1.0, null)));
        rbmi.setSelected(true);
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 1.25, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 1.5, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 2, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 3, null)));
        group.add(rbmi);
        m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 4, null)));
        group.add(rbmi);
        pb.add(m);
        pb.setFocusable(false);
        creationToolbar.addSeparator();
        creationToolbar.add(pb);

        view.setDrawing(createDrawing());

        stockTask = null;
        
        initIndicatorProjectManager();
        initModuleProjectManager();
        // Must be done after project managers had been initialized.
        initListCellRenderer();
        createToolTipTextForJTabbedPane();
    }
    
    /**
     * Creates a new Drawing for this Project.
     */
    private Drawing createDrawing() {
        DefaultDrawing drawing = new IndicatorDefaultDrawing();
        
        return drawing;
    }
    
    public void setDrawing(Drawing d) {
        view.setDrawing(d);
    }

    public Drawing getDrawing() {
        return view.getDrawing();
    }

    public DrawingView getView() {
        return view;
    }

    public DrawingEditor getEditor() {
        return editor;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        toolButtonGroup = new javax.swing.ButtonGroup();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jComboBox1 = new AutoCompleteJComboBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        view = new org.jhotdraw.draw.DefaultDrawingView();
        jPanel1 = new javax.swing.JPanel();
        creationToolbar = new javax.swing.JToolBar();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        objectInspectorJPanel = new ObjectInspectorJPanel(new MutableStock(org.yccheok.jstock.engine.Utils.getEmptyStock(Code.newInstance(""), Symbol.newInstance(""))));

        jPanel10.setLayout(new java.awt.BorderLayout());

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        this.jList2.setModel(new DefaultListModel());
        this.jList2.addMouseListener(new JListPopupListener());
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        jPanel10.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jComboBox1.setEditable(true);
        jComboBox1.setPreferredSize(new java.awt.Dimension(150, 24));
        ((AutoCompleteJComboBox)jComboBox1).attachStockInfoObserver(this.getStockInfoObserver());
        ((AutoCompleteJComboBox)jComboBox1).attachDispObserver(this.getDispObserver());

        setLayout(new java.awt.BorderLayout());

        // Priority give to left component.
        jSplitPane1.setResizeWeight(0.9);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("IndicatorPanel_StockIndicator"))); // NOI18N
        jPanel4.setLayout(new java.awt.BorderLayout(5, 5));

        scrollPane.setViewportView(view);

        jPanel4.add(scrollPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        creationToolbar.setFloatable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(creationToolbar, gridBagConstraints);

        jPanel4.add(jPanel1, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(jPanel4);

        jSplitPane2.setDividerLocation(305);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setMinimumSize(new java.awt.Dimension(300, 261));
        jSplitPane2.setPreferredSize(new java.awt.Dimension(150, 368));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("IndicatorPanel_Database"))); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout(5, 5));

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 5));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filenew.png"))); // NOI18N
        jButton1.setText(bundle.getString("New...")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/filesave.png"))); // NOI18N
        jButton5.setText(bundle.getString("Save")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton5);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/download.png"))); // NOI18N
        jButton2.setText(bundle.getString("Install...")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        jPanel2.add(jPanel3, java.awt.BorderLayout.SOUTH);

        jPanel11.setLayout(new java.awt.BorderLayout());

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        this.jList1.setModel(new DefaultListModel());
        this.jList1.addMouseListener(new JListPopupListener());
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel11.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(bundle.getString("IndicatorPanel_AlertIndicator"), new javax.swing.ImageIcon(getClass().getResource("/images/16x16/bell.png")), jPanel11); // NOI18N

        jPanel2.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jSplitPane2.setTopComponent(jPanel2);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("IndicatorPanel_StockSampleData"))); // NOI18N
        jPanel5.setLayout(new java.awt.BorderLayout(5, 5));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/player_play.png"))); // NOI18N
        jButton4.setText(bundle.getString("IndicatorPanel_Simulate")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton4);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/stop.png"))); // NOI18N
        jButton6.setText(bundle.getString("IndicatorPanel_Stop")); // NOI18N
        jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton6);

        jPanel5.add(jPanel6, java.awt.BorderLayout.SOUTH);
        jPanel5.add(jPanel7, java.awt.BorderLayout.NORTH);
        jPanel5.add(objectInspectorJPanel, java.awt.BorderLayout.CENTER);

        jSplitPane2.setBottomComponent(jPanel5);

        jSplitPane1.setRightComponent(jSplitPane2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // Start button.
        this.jButton4.setEnabled(true);
        // Stop button.
        this.jButton6.setEnabled(false);     
        
        stop();
        
        JStock m = JStock.instance();
        m.setStatusBar(false, GUIBundle.getString("IndicatorPanel_SimulationStopped"));
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.Save(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        // When the user release the mouse button and completes the selection,
        // getValueIsAdjusting() becomes false        
        if (evt.getValueIsAdjusting()) {
            return;
        }

        final String projectName = (String)this.jList1.getSelectedValue();
        if (projectName == null) {
            this.listSelectionEx = null;
            return;
        }

        final IndicatorDefaultDrawing indicatorDefaultDrawing = this.alertIndicatorProjectManager.getIndicatorDefaultDrawing(projectName);
        boolean userCancel = false;
        if (indicatorDefaultDrawing != null) {
            if (this.promptToSaveSignificantEdits(this.listSelectionEx)) {
                this.setDrawing(indicatorDefaultDrawing);
            }
            else {
                userCancel = true;
            }
        }
        else {
            if (this.promptToSaveSignificantEdits(this.listSelectionEx)) {
                final String output = MessageFormat.format(MessagesBundle.getString("question_message_corrupted_file_remove_template"), projectName);
                if(JOptionPane.showConfirmDialog(this, output, MessagesBundle.getString("question_title_corrupted_file_remove"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    // Do not prompt "Are you sure you want to delete..." dialog box.
                    Delete(false);
                }
                else
                {
                    this.setDrawing(this.createDrawing());
                }
            }
            else {
                userCancel = true;
            }
        }

        if (!userCancel) {
            // Ensure jList1 and jList2 are mutually exclusive.
            // This must be done before we assign value to this.listSelectionEx,
            // as list selection listener of jList2 will overwrite this.listSelectionEx.
            // Also, clearSelection code cannot be placed before any code which depend
            // on the correctness of this.listSelectionEx.

            // Ensure jList1 and jList2 are mutually exclusive.
            //
            // When cancel is pressed, promptToSaveSignificantEdits has helped us
            // to determine correct selection. We need not to perform or clear selection
            // explicitly.
            this.jList2.clearSelection();

            // Whenever cancel happen, we need to make this.listSelectionEx remains
            // unchanged.
            this.listSelectionEx = ListSelectionEx.newInstance(this.jList1, projectName);
        }
    }//GEN-LAST:event_jList1ValueChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        final JStock m = JStock.instance();
        final IndicatorDefaultDrawing indicatorDefaultDrawing = (IndicatorDefaultDrawing)this.view.getDrawing();
        final OperatorIndicator operatorIndicator = indicatorDefaultDrawing.getOperatorIndicator();
        
        Stock stock = null;
        // Check, if stock information is required.
        if (operatorIndicator.isStockNeeded() || operatorIndicator.isStockHistoryServerNeeded()) {
            final Object o = ((ObjectInspectorJPanel)this.objectInspectorJPanel).getBean();
            final MutableStock mutableStock = (MutableStock)o;
            stock = mutableStock.getStock();
            if (stock.code.toString().equals("")) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_need_to_select_a_stock"), MessagesBundle.getString("warning_title_you_need_to_select_a_stock"), JOptionPane.WARNING_MESSAGE);
                this.jComboBox1.requestFocus();
                return;
            }
        }

        // Start button.
        this.jButton4.setEnabled(false);
        // Stop button.
        this.jButton6.setEnabled(true);

        final Thread thread = this.simulationThread;
        // Set null to stop the simulation thread.
        this.simulationThread = null;
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            }
            catch (InterruptedException exp) {
                log.error(null, exp);
            }
        }

        final Stock tmpStock = stock;
        simulationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                IndicatorPanel.this.simulate(tmpStock);
            }
        });
        
        simulationThread.start();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void New() {
        if (false == promptToSaveSignificantEdits()) {
            return;
        }
        if (this.jTabbedPane1.getSelectedIndex() == 0) {
            this.alertIndicatorNew();
        }
        else if (this.jTabbedPane1.getSelectedIndex() == 1) {
            this.moduleIndicatorNew();
        }
    }

    private static class ListSelectionEx {
        public final JList list;
        public final String projectName;
        private ListSelectionEx(JList list, String projectName) {
            if (list == null || projectName == null) {
                throw new IllegalArgumentException("Method arguments cannot be null");
            }
            this.list = list;
            this.projectName = projectName;
        }

        public static ListSelectionEx newInstance(JList list, String projectName) {
            return new ListSelectionEx(list, projectName);
        }
    }

    private boolean promptToSaveSignificantEdits(ListSelectionEx _listSelectionEx) {
        final boolean hasSignificantEdits = ((IndicatorDefaultDrawing)this.view.getDrawing()).hasSignificantEdits();
        if (false == hasSignificantEdits) {
            return true;
        }
        String output = MessagesBundle.getString("question_message_current_drawing_is_modified_save_it");
        if (_listSelectionEx != null) {
            /* There is a previous selected project. */
            output = MessageFormat.format(MessagesBundle.getString("question_message_current_drawing_is_modified_save_it_template"), _listSelectionEx.projectName);
        }
        // We have a unsaved drawing. Prompt user to save it.
        final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.instance(), output, MessagesBundle.getString("question_title_current_drawing_is_modified_save_it"), javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Do not use this.Save(false). this.Save(false) will only save the
            // project in current active tab and current selected project.
            // We just want to save current selected project, regardless current
            // active tab.
            // this.Save(false);
            if (_listSelectionEx != null) {
                // There is a previous selection.
                if (this.jList1 == _listSelectionEx.list) {
                    this._alertIndicatorSave(_listSelectionEx.projectName, false);
                }
                else if (this.jList2 == _listSelectionEx.list) {
                    this._moduleIndicatorSave(_listSelectionEx.projectName, false);
                }
            }
            else {
                // No choice. We will save to curent active tab, by prompting user
                // enter his desired project name.
                if (this.jTabbedPane1.getSelectedIndex() == 0) {
                    this._alertIndicatorSave(null, false);
                }
                else if (this.jTabbedPane1.getSelectedIndex() == 1) {
                    this._moduleIndicatorSave(null, false);
                }
            }
        }

        if (result == JOptionPane.CANCEL_OPTION) {
            final ListSelectionListener[] listSelectionListeners1 = this.jList1.getListSelectionListeners();
            final ListSelectionListener[] listSelectionListeners2 = this.jList2.getListSelectionListeners();
            try {
                for (ListSelectionListener listSelectionListener : listSelectionListeners1) {
                    this.jList1.removeListSelectionListener(listSelectionListener);
                }
                for (ListSelectionListener listSelectionListener : listSelectionListeners2) {
                    this.jList2.removeListSelectionListener(listSelectionListener);
                }
                // User chooses cancel. Select back previous project.
                if (_listSelectionEx != null) {
                    if (_listSelectionEx.list == this.jList1) {
                        this.jList1.setSelectedValue(_listSelectionEx.projectName, true);
                        this.jList2.clearSelection();
                    }
                    else {
                        assert(_listSelectionEx.list == this.jList2);
                        this.jList2.setSelectedValue(_listSelectionEx.projectName, true);
                        this.jList1.clearSelection();
                    }
                }
                else {
                    // User chooses cancel. Clear all the lists, since previously, there are
                    // no selection at all.
                    this.jList1.clearSelection();
                    this.jList2.clearSelection();
                }
            }
            finally {
                // Do it within finally block, just to be safe.
                for (ListSelectionListener listSelectionListener : listSelectionListeners1) {
                    this.jList1.addListSelectionListener(listSelectionListener);
                }
                for (ListSelectionListener listSelectionListener : listSelectionListeners2) {
                    this.jList2.addListSelectionListener(listSelectionListener);
                }
            }
        }

        return result != JOptionPane.CANCEL_OPTION;
    }

    // Return false, if user selects cancel. Else, return true.
    public boolean promptToSaveSignificantEdits() {
        final boolean hasSignificantEdits = ((IndicatorDefaultDrawing)this.view.getDrawing()).hasSignificantEdits();
        if (false == hasSignificantEdits) {
            return true;
        }
        String output = MessagesBundle.getString("question_message_current_drawing_is_modified_save_it");
        // Unlike Save, in promptToSaveSignificantEdits, we will try our best to save the selected project,
        // even it is not within our visible range (The jTabbedPane is not being selected)
        if (this.jList1.getSelectedValue() != null) {
            output = MessageFormat.format(MessagesBundle.getString("question_message_current_drawing_is_modified_save_it_template"), this.jList1.getSelectedValue());
        }
        else if (this.jList2.getSelectedValue() != null) {
            output = MessageFormat.format(MessagesBundle.getString("question_message_current_drawing_is_modified_save_it_template"), this.jList2.getSelectedValue());
        }
        // We have a unsaved drawing. Prompt user to save it.
        final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.instance(), output, MessagesBundle.getString("question_title_current_drawing_is_modified_save_it"), javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            // Do not use this.Save(false). this.Save(false) will only save the
            // project in current active tab and current selected project.
            // We just want to save current selected project, regardless current
            // active tab.
            // this.Save(false);
            if (this.jList1.getSelectedValue() != null) {
                this.alertIndicatorSave(false);
            }
            else if (this.jList2.getSelectedValue() != null) {
                this.moduleIndicatorSave(false);
            }
            else {
                // No choice. We will save to curent active tab, by prompting user
                // enter his desired project name.
                this.Save(false);
            }
        }
        return result != JOptionPane.CANCEL_OPTION;
    }
    
    private void alertIndicatorNew() {
        String projectName = null;

        while (true) {
            projectName = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_enter_new_alert_indicator_name"));

            if (projectName == null) {
                return;
            }

            projectName = projectName.trim();
            
            if (projectName.length() == 0) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_need_to_specific_alert_indicator_name"), MessagesBundle.getString("warning_title_you_need_to_specific_alert_indicator_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (this.alertIndicatorProjectManager.contains(projectName)) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_already_an_alert_indicator_with_same_name"), MessagesBundle.getString("warning_title_already_an_alert_indicator_with_same_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            IndicatorDefaultDrawing newDrawing = (IndicatorDefaultDrawing)createDrawing();
            if(this.alertIndicatorProjectManager.addProject(newDrawing, projectName))
            {
                this.syncJListWithIndicatorProjectManager(this.jList1, this.alertIndicatorProjectManager);
                // Select on the newly created project.
                this.jList1.setSelectedValue(projectName, true);
                return;
            }
            else
            {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_alert_indicator_name"), MessagesBundle.getString("warning_title_invalid_alert_indicator_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }
        }
    }

    private void moduleIndicatorNew() {
        String projectName = null;

        while (true) {
            projectName = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_enter_new_module_indicator_name"));

            if (projectName == null) {
                return;
            }

            projectName = projectName.trim();
            
            if (projectName.length() == 0) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_need_to_specific_module_indicator_name"), MessagesBundle.getString("warning_title_you_need_to_specific_module_indicator_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (this.moduleIndicatorProjectManager.contains(projectName)) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_already_a_module_indicator_with_same_name"), MessagesBundle.getString("warning_title_already_a_module_indicator_with_same_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            IndicatorDefaultDrawing newDrawing = (IndicatorDefaultDrawing)createDrawing();
            if(this.moduleIndicatorProjectManager.addProject(newDrawing, projectName))
            {
                this.syncJListWithIndicatorProjectManager(this.jList2, this.moduleIndicatorProjectManager);
                // Select on the newly created project.
                this.jList2.setSelectedValue(projectName, true);
                return;
            }
            else
            {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_module_indicator_name"), MessagesBundle.getString("warning_title_invalid_module_indicator_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }
        }
    }

    private void Save(boolean selectProjectAfterSave) {
        if (this.jTabbedPane1.getSelectedIndex() == 0) {
            this.alertIndicatorSave(selectProjectAfterSave);
        }
        else if (this.jTabbedPane1.getSelectedIndex() == 1) {
            this.moduleIndicatorSave(selectProjectAfterSave);
        }
    }

    private void alertIndicatorSave(boolean selectProjectAfterSave) {
        String projectName = (String)this.jList1.getSelectedValue();
        _alertIndicatorSave(projectName, selectProjectAfterSave);
    }

    private void _alertIndicatorSave(String projectName, boolean selectProjectAfterSave) {
        if (projectName == null) {
            // No project name selection. Prompt user to enter new project name.
            while (true) {
                projectName = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_enter_save_alert_indicator_name"));

                if (projectName == null) {
                    return;
                }

                projectName = projectName.trim();
                
                if (projectName.length() == 0) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_need_to_specific_alert_indicator_name"), MessagesBundle.getString("warning_title_you_need_to_specific_alert_indicator_name"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                if (alertIndicatorProjectManager.contains(projectName)) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_already_an_alert_indicator_with_same_name"), MessagesBundle.getString("warning_title_already_an_alert_indicator_with_same_name"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                IndicatorDefaultDrawing drawing = (IndicatorDefaultDrawing)view.getDrawing();

                if (this.alertIndicatorProjectManager.addProject(drawing, projectName))
                {
                    this.syncJListWithIndicatorProjectManager(this.jList1, this.alertIndicatorProjectManager);
                    final String output = MessageFormat.format(MessagesBundle.getString("info_message_file_saved_template"), projectName);
                    JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("info_title_file_saved"), JOptionPane.INFORMATION_MESSAGE);
                    if (selectProjectAfterSave) {
                        this.jList1.setSelectedValue(projectName, true);
                    }
                    return;
                }
                else
                {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_alert_indicator_name"), MessagesBundle.getString("warning_title_invalid_alert_indicator_name"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }
            }
        } else {
            // projectName is selected by user.
            final IndicatorDefaultDrawing drawing = (IndicatorDefaultDrawing)view.getDrawing();
            if (alertIndicatorProjectManager.addProject(drawing, projectName)) {
                // Just to ensure list cell renderer will be triggered.
                this.syncJListWithIndicatorProjectManager(this.jList1, this.alertIndicatorProjectManager);
                final String output = MessageFormat.format(MessagesBundle.getString("info_message_file_saved_template"), projectName);
                JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("info_title_file_saved"), JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_alert_indicator_name"), MessagesBundle.getString("warning_title_invalid_alert_indicator_name"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void moduleIndicatorSave(boolean selectProjectAfterSave) {
        String projectName = (String)this.jList2.getSelectedValue();
        this._moduleIndicatorSave(projectName, selectProjectAfterSave);
    }

    private void _moduleIndicatorSave(String projectName, boolean selectProjectAfterSave) {
        if (projectName == null) {
            while (true) {
                projectName = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_enter_save_module_indicator_name"));

                if (projectName == null) {
                    return;
                }

                projectName = projectName.trim();
                
                if (projectName.length() == 0) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_need_to_specific_module_indicator_name"), MessagesBundle.getString("warning_title_you_need_to_specific_module_indicator_name"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                if (moduleIndicatorProjectManager.contains(projectName)) {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_already_a_module_indicator_with_same_name"), MessagesBundle.getString("warning_title_already_a_module_indicator_with_same_name"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                IndicatorDefaultDrawing drawing = (IndicatorDefaultDrawing)view.getDrawing();

                if (this.moduleIndicatorProjectManager.addProject(drawing, projectName))
                {
                    this.syncJListWithIndicatorProjectManager(this.jList2, this.moduleIndicatorProjectManager);
                    final String output = MessageFormat.format(MessagesBundle.getString("info_message_file_saved_template"), projectName);
                    JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("info_title_file_saved"), JOptionPane.INFORMATION_MESSAGE);
                    if (selectProjectAfterSave) {
                        this.jList2.setSelectedValue(projectName, true);
                    }
                    return;
                }
                else
                {
                    JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_module_indicator_name"), MessagesBundle.getString("warning_title_invalid_module_indicator_name"), JOptionPane.WARNING_MESSAGE);
                    continue;
                }
            }
        } else {
            // projectName is selected by user.
            final IndicatorDefaultDrawing drawing = (IndicatorDefaultDrawing)view.getDrawing();
            if (this.moduleIndicatorProjectManager.addProject(drawing, projectName)) {
                // Just to ensure list cell renderer will be triggered.
                this.syncJListWithIndicatorProjectManager(this.jList2, this.moduleIndicatorProjectManager);
                final String output = MessageFormat.format(MessagesBundle.getString("info_message_file_saved_template"), projectName);
                JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("info_title_file_saved"), JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_module_indicator_name"), MessagesBundle.getString("warning_title_invalid_module_indicator_name"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void Export() {
        if (false == promptToSaveSignificantEdits()) {
            return;
        }
        if (this.jTabbedPane1.getSelectedIndex() == 0) {
            this.alertIndicatorExport();
        }
        else if (this.jTabbedPane1.getSelectedIndex() == 1) {
            this.moduleIndicatorExport();
        }
    }

    private void alertIndicatorExport() {
        final String projectName = (String)this.jList1.getSelectedValue();
        if (projectName == null) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_must_select_alert_indicator"), MessagesBundle.getString("warning_title_you_must_select_alert_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        final OperatorIndicator operatorIndicator = this.alertIndicatorProjectManager.getOperatorIndicator(projectName);
        if (operatorIndicator != null && operatorIndicator.getType() != this.alertIndicatorProjectManager.getPreferredOperatorIndicatorType()) {
            final String output = MessageFormat.format(MessagesBundle.getString("warning_message_you_may_not_export_invalid_indicator"), projectName);
            JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("warning_title_you_may_not_export_invalid_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        final File file = Utils.promptSaveZippedJFileChooser(projectName);
        if (file == null) {
            return;
        }
        this.alertIndicatorProjectManager.export(projectName, file);
    }

    private void moduleIndicatorExport() {
        final String projectName = (String)this.jList2.getSelectedValue();
        if (projectName == null) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_must_select_module_indicator"), MessagesBundle.getString("warning_title_you_must_select_module_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        final OperatorIndicator operatorIndicator = this.moduleIndicatorProjectManager.getOperatorIndicator(projectName);
        if (operatorIndicator != null && operatorIndicator.getType() != this.moduleIndicatorProjectManager.getPreferredOperatorIndicatorType()) {
            final String output = MessageFormat.format(MessagesBundle.getString("warning_message_you_may_not_export_invalid_indicator"), projectName);
            JOptionPane.showMessageDialog(this, output, MessagesBundle.getString("warning_title_you_may_not_export_invalid_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        final File file = Utils.promptSaveZippedJFileChooser(projectName);
        if (file == null) {
            return;
        }
        this.moduleIndicatorProjectManager.export(projectName, file);
    }

    private void Delete(boolean confirmationDialog) {
        if (this.jTabbedPane1.getSelectedIndex() == 0) {
            this.alertIndicatorDelete(confirmationDialog);
        }
        else if (this.jTabbedPane1.getSelectedIndex() == 1) {
            this.moduleIndicatorDelete(confirmationDialog);
        }
    }

    private void alertIndicatorDelete(boolean confirmationDialog) {
        final String projectName = (String)this.jList1.getSelectedValue();
        if (projectName == null) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_must_select_alert_indicator"), MessagesBundle.getString("warning_title_you_must_select_alert_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (confirmationDialog) {
            final String output = MessageFormat.format(MessagesBundle.getString("question_message_delete_template"), projectName);
            final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.instance(), output, MessagesBundle.getString("question_title_delete"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (result != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }
        this.alertIndicatorProjectManager.removeProject(projectName);
        this.syncJListWithIndicatorProjectManager(this.jList1, this.alertIndicatorProjectManager);
        if (this.alertIndicatorProjectManager.getNumOfProject() == 0) {
            this.setDrawing(this.createDrawing());
        }
    }

    private void moduleIndicatorDelete(boolean confirmationDialog) {
        final String projectName = (String)this.jList2.getSelectedValue();
        if (projectName == null) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_must_select_module_indicator"), MessagesBundle.getString("warning_title_you_must_select_module_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (confirmationDialog) {
            final String output = MessageFormat.format(MessagesBundle.getString("question_message_delete_template"), projectName);
            final int result = javax.swing.JOptionPane.showConfirmDialog(JStock.instance(), output, MessagesBundle.getString("question_title_delete"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (result != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }
        this.moduleIndicatorProjectManager.removeProject(projectName);
        this.syncJListWithIndicatorProjectManager(this.jList2, this.moduleIndicatorProjectManager);
        if (this.moduleIndicatorProjectManager.getNumOfProject() == 0) {
            this.setDrawing(this.createDrawing());
        }
    }

    private void Rename() {
        if (this.jTabbedPane1.getSelectedIndex() == 0) {
            this.alertIndicatorRename();
        }
        else if (this.jTabbedPane1.getSelectedIndex() == 1) {
            this.moduleIndicatorRename();
        }
    }

    private boolean isIndicatorDatabaseVisibleAndSelected() {
        final int select = this.jList1.getSelectedIndex();
        return this.jTabbedPane1.getSelectedIndex() == 0 && select >= 0 && select < this.alertIndicatorProjectManager.getNumOfProject();
    }

    private boolean isModuleDatabaseVisibleAndSelected() {
        final int select = this.jList2.getSelectedIndex();
        return this.jTabbedPane1.getSelectedIndex() == 1 && select >= 0 && select < this.moduleIndicatorProjectManager.getNumOfProject();
    }

    private void alertIndicatorRename() {
        final String oldProjectName = (String)this.jList1.getSelectedValue();
        if (oldProjectName == null) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_must_select_alert_indicator"), MessagesBundle.getString("warning_title_you_must_select_alert_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        assert(this.alertIndicatorProjectManager.contains(oldProjectName));

        String newProjectName = null;

        while (true) {
            newProjectName = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_enter_rename_alert_indicator_name"), oldProjectName);

            if (newProjectName == null) {
                return;
            }

            newProjectName = newProjectName.trim();
            
            if (newProjectName.length() == 0) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_need_to_specific_alert_indicator_name"), MessagesBundle.getString("warning_title_you_need_to_specific_alert_indicator_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (this.alertIndicatorProjectManager.contains(newProjectName)) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_already_an_alert_indicator_with_same_name"), MessagesBundle.getString("warning_title_already_an_alert_indicator_with_same_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (this.alertIndicatorProjectManager.renameProject(newProjectName, oldProjectName))
            {
                final DefaultListModel defaultListModel = (DefaultListModel)this.jList1.getModel();
                defaultListModel.setElementAt(newProjectName, this.jList1.getSelectedIndex());
                // sync may not work well for rename operation, as the method
                // may false thought we are deleting oldProjectName, and added
                // newProjectName. We will then perform wrong selection on the
                // project.
                //this.syncJListWithIndicatorProjectManager(this.jList1, this.alertIndicatorProjectManager);

                // Update the project.xml as well.
                IndicatorPanel.this.saveAlertIndicatorProjectManager();
                
                // Update selection info as well.
                if (this.listSelectionEx != null) {
                    this.listSelectionEx = ListSelectionEx.newInstance(this.listSelectionEx.list, newProjectName);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("error_message_unknown_error_during_renaming"), MessagesBundle.getString("error_title_unknown_error_during_renaming"), JOptionPane.ERROR_MESSAGE);
            }

            break;
        }
    }

    private void moduleIndicatorRename() {
        final String oldProjectName = (String)this.jList2.getSelectedValue();
        if (oldProjectName == null) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_must_select_module_indicator"), MessagesBundle.getString("warning_title_you_must_select_module_indicator"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        assert(this.moduleIndicatorProjectManager.contains(oldProjectName));

        String newProjectName = null;

        while (true) {
            newProjectName = JOptionPane.showInputDialog(this, MessagesBundle.getString("info_message_enter_rename_module_indicator_name"), oldProjectName);

            if (newProjectName == null) {
                return;
            }

            newProjectName = newProjectName.trim();
            
            if (newProjectName.length() == 0) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_you_need_to_specific_module_indicator_name"), MessagesBundle.getString("warning_title_you_need_to_specific_module_indicator_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (this.moduleIndicatorProjectManager.contains(newProjectName)) {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_already_a_module_indicator_with_same_name"), MessagesBundle.getString("warning_title_already_a_module_indicator_with_same_name"), JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (this.moduleIndicatorProjectManager.renameProject(newProjectName, oldProjectName))
            {
                final DefaultListModel defaultListModel = (DefaultListModel)this.jList2.getModel();
                defaultListModel.setElementAt(newProjectName, this.jList2.getSelectedIndex());
                // sync may not work well for rename operation, as the method
                // may false thought we are deleting oldProjectName, and added
                // newProjectName. We will then perform wrong selection on the
                // project.
                //this.syncJListWithIndicatorProjectManager(this.jList2, this.moduleIndicatorProjectManager);
                // Update the project.xml as well.
                IndicatorPanel.this.saveModuleIndicatorProjectManager();
            }
            else
            {
                JOptionPane.showMessageDialog(this, MessagesBundle.getString("error_message_unknown_error_during_renaming"), MessagesBundle.getString("error_title_unknown_error_during_renaming"), JOptionPane.ERROR_MESSAGE);
            }

            break;
        }
    }

    private ImageIcon getImageIcon(String imageIcon) {
        return new javax.swing.ImageIcon(getClass().getResource(imageIcon));
    }

    private JPopupMenu getJListPopupMenu() {
        final JPopupMenu popup = new JPopupMenu();
        javax.swing.JMenuItem menuItem = new JMenuItem(GUIBundle.getString("New..."), this.getImageIcon("/images/16x16/filenew.png"));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                New();
            }
        });
        popup.add(menuItem);
        menuItem = new JMenuItem(GUIBundle.getString("Save"), this.getImageIcon("/images/16x16/filesave.png"));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Save(true);
            }
        });
        popup.add(menuItem);
        popup.addSeparator();

        menuItem = new JMenuItem(GUIBundle.getString("Install..."), this.getImageIcon("/images/16x16/download.png"));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Install();
            }
        });
        popup.add(menuItem);

        if (isIndicatorDatabaseVisibleAndSelected() || isModuleDatabaseVisibleAndSelected()) {
            menuItem = new JMenuItem(GUIBundle.getString("Export..."), this.getImageIcon("/images/16x16/upload.png"));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Export();
                }
            });
            popup.add(menuItem);
            popup.addSeparator();
            menuItem = new JMenuItem(GUIBundle.getString("Rename..."), this.getImageIcon("/images/16x16/edit.png"));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Rename();
                }
            });
            popup.add(menuItem);
            menuItem = new JMenuItem(GUIBundle.getString("Delete"), this.getImageIcon("/images/16x16/editdelete.png"));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Delete(true);
                }
            });
            popup.add(menuItem);
        }
        return popup;
    }

    private class JListPopupListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {
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
                getJListPopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.New();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        // When the user release the mouse button and completes the selection,
        // getValueIsAdjusting() becomes false
       if (evt.getValueIsAdjusting()) {
            return;
        }

        String projectName = (String)this.jList2.getSelectedValue();
        if (projectName == null) {
            this.listSelectionEx = null;
            return;
        }

        IndicatorDefaultDrawing indicatorDefaultDrawing = this.moduleIndicatorProjectManager.getIndicatorDefaultDrawing(projectName);
        boolean userCancel = false;
        if (indicatorDefaultDrawing != null) {
            if (this.promptToSaveSignificantEdits(this.listSelectionEx)) {
                this.setDrawing(indicatorDefaultDrawing);
            }
            else {
                userCancel = true;
            }
        }
        else {
            if (this.promptToSaveSignificantEdits(this.listSelectionEx)) {
                final String output = MessageFormat.format(MessagesBundle.getString("question_message_corrupted_file_remove_template"), projectName);
                if(JOptionPane.showConfirmDialog(this, output, MessagesBundle.getString("question_title_corrupted_file_remove"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    // Do not prompt "Are you sure you want to delete..." dialog box.
                    Delete(false);
                }
                else
                {
                    this.setDrawing(this.createDrawing());
                }
            }
            else {
                userCancel = true;
            }
        }

        if (!userCancel) {
            // Ensure jList1 and jList2 are mutually exclusive.
            // This must be done before we assign value to this.listSelectionEx,
            // as list selection listener of jList1 will overwrite this.listSelectionEx.
            // Also, clearSelection code cannot be placed before any code which depend
            // on the correctness of this.listSelectionEx.

            // Ensure jList1 and jList2 are mutually exclusive.
            //
            // When cancel is pressed, promptToSaveSignificantEdits has helped us
            // to determine correct selection. We need not to perform or clear selection
            // explicitly.
            this.jList1.clearSelection();
            // Whenever cancel happen, we need to make this.listSelectionEx remains
            // unchanged.
            this.listSelectionEx = ListSelectionEx.newInstance(this.jList2, projectName);
        }
    }//GEN-LAST:event_jList2ValueChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Install();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void Install() {
        if (false == promptToSaveSignificantEdits()) {
            return;
        }
        final Wizard wizard = this.getWizardDialog();
        int ret = wizard.showModalDialog(650, 400, true);
        if (ret != Wizard.FINISH_RETURN_CODE) {
            final JList jList = this.getCurrentActiveJList();
            // Although cancel button is pressed, possible some indicators had
            // already installed from JStock server.
            //
            // Refresh JList after install from JStock server.
            this.syncJListWithIndicatorProjectManager(jList, this.getCurrentActiveIndicatorProjectManager());
            if (jList.getModel().getSize() > 0 && jList.getSelectedIndex() < 0) {
                jList.setSelectedIndex(0);
            }
            return;
        }
        final WizardModel wizardModel = wizard.getModel();
        final WizardPanelDescriptor wizardSelectInstallIndicatorMethodDescriptor = wizardModel.getPanelDescriptor(WizardSelectInstallIndicatorMethodDescriptor.IDENTIFIER);
        final WizardSelectInstallIndicatorMethodJPanel wizardSelectInstallIndicatorMethodJPanel = (WizardSelectInstallIndicatorMethodJPanel)wizardSelectInstallIndicatorMethodDescriptor.getPanelComponent();
        if (wizardSelectInstallIndicatorMethodJPanel.isLocalFileSelected()) {
            final File file = wizardSelectInstallIndicatorMethodJPanel.getSelectedFile();
            assert(file != null);
            if (this.jTabbedPane1.getSelectedIndex() == 0) {
                this.alertIndicatorInstall(file);
            }
            else if (this.jTabbedPane1.getSelectedIndex() == 1) {
                this.moduleIndicatorInstall(file);
            }
        }
        else
        {
            final JList jList = this.getCurrentActiveJList();
            // Refresh JList after install from JStock server.
            this.syncJListWithIndicatorProjectManager(jList, this.getCurrentActiveIndicatorProjectManager());
            if (jList.getModel().getSize() > 0 && jList.getSelectedIndex() < 0) {
                jList.setSelectedIndex(0);
            }
        }
    }

    private void alertIndicatorInstall(File file) {
        final IndicatorProjectManager.PreInstallStatus preInstallStatus = this.alertIndicatorProjectManager.getPreInstallStatus(file);
        if (preInstallStatus == IndicatorProjectManager.PreInstallStatus.Collision)
        {
            final String output = MessageFormat.format(MessagesBundle.getString("question_message_do_you_want_to_overwrite_template"), IndicatorProjectManager.getProjectName(file));
            if(JOptionPane.showConfirmDialog(this, output, MessagesBundle.getString("question_title_do_you_want_to_overwrite"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            {
                return;
            }
        }
        else if (preInstallStatus == IndicatorProjectManager.PreInstallStatus.Unsafe) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_alert_indicator_file"), MessagesBundle.getString("warning_title_invalid_alert_indicator_file"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (this.alertIndicatorProjectManager.install(file))
        {
            this.syncJListWithIndicatorProjectManager(this.jList1, this.alertIndicatorProjectManager);
            this.jList1.setSelectedValue(IndicatorProjectManager.getProjectName(file), true);
        }
        else
        {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_alert_indicator_file"), MessagesBundle.getString("warning_title_invalid_alert_indicator_file"), JOptionPane.WARNING_MESSAGE);
        }
    }

    private void moduleIndicatorInstall(File file) {
        final IndicatorProjectManager.PreInstallStatus preInstallStatus = this.moduleIndicatorProjectManager.getPreInstallStatus(file);
        if (preInstallStatus == IndicatorProjectManager.PreInstallStatus.Collision)
        {
            final String output = MessageFormat.format(MessagesBundle.getString("question_message_do_you_want_to_overwrite_template"), IndicatorProjectManager.getProjectName(file));
            if(JOptionPane.showConfirmDialog(this, output, MessagesBundle.getString("question_title_do_you_want_to_overwrite"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            {
                return;
            }
        }
        else if (preInstallStatus == IndicatorProjectManager.PreInstallStatus.Unsafe) {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_module_indicator_file"), MessagesBundle.getString("warning_title_invalid_module_indicator_file"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (this.moduleIndicatorProjectManager.install(file))
        {
            this.syncJListWithIndicatorProjectManager(this.jList2, this.moduleIndicatorProjectManager);
            this.jList2.setSelectedValue(IndicatorProjectManager.getProjectName(file), true);
        }
        else
        {
            JOptionPane.showMessageDialog(this, MessagesBundle.getString("warning_message_invalid_module_indicator_file"), MessagesBundle.getString("warning_title_invalid_module_indicator_file"), JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addCreationButtonsTo(JToolBar tb, final DrawingEditor editor) {
        // AttributeKeys for the entitie sets
        HashMap<AttributeKey,Object> attributes;
        
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.yccheok.jstock.data.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        
        //ToolBarButtonFactory.addSelectionToolTo(tb, editor);
        ButtonFactory.addSelectionToolTo(tb, editor, createDrawingActions(editor), createSelectionActions(editor));
        tb.addSeparator();
        
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes.put(AttributeKeys.TEXT_COLOR, Color.black);

        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.LogicalOperatorFigure", attributes), "createLogical", labels);
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.EqualityOperatorFigure", attributes), "createEquality", labels);
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.ArithmeticOperatorFigure", attributes), "createArithmetic", labels);
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.FunctionOperatorFigure", attributes), "createFunction", labels);
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.DoubleConstantOperatorFigure", attributes), "createDoubleConstant", labels);
        tb.addSeparator();
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.StockRelativeHistoryOperatorFigure", attributes), "createStockRelativeHistory", labels);
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.StockHistoryOperatorFigure", attributes), "createStockHistory", labels);
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.StockOperatorFigure", attributes), "createStock", labels);
        tb.addSeparator();
        //ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.DiodeOperatorFigure", attributes), "createDiode", labels);
        ButtonFactory.addToolTo(tb, editor, new OperatorFigureCreationTool("org.yccheok.jstock.gui.analysis.SinkOperatorFigure", attributes), "createSink", labels);
        tb.addSeparator();
        
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes.put(AttributeKeys.TEXT_COLOR, Color.black);
        attributes.put(AttributeKeys.FONT_BOLD, true);
        attributes.put(AttributeKeys.FILL_COLOR, new Color(255, 204, 0));
	ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new TextAreaFigure(), attributes), "edit.createText", drawLabels);
    }
    
    private static Collection<Action> createDrawingActions(DrawingEditor editor) {
        LinkedList<Action> a = new LinkedList<Action>();        
        return a;
    }
    
    private static Collection<Action> createSelectionActions(DrawingEditor editor) {
        LinkedList<Action> a = new LinkedList<Action>();
        a.add(new PropertiesAction(editor));
        a.add(null);
        a.add(new DeleteAction(editor));
        return a;
    }
    
    public void initAjaxProvider() {        
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

    // Shared code for getStockInfoObserver and getResultObserver.
    private void addStockInfoFromAutoCompleteJComboBox(StockInfo stockInfo) {
        final StockTask tmp = stockTask;
        if (tmp != null) {
            tmp.cancel(true);
        }

        stockTask = new StockTask(stockInfo.code, stockInfo.symbol);
        stockTask.execute();
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

    private class StockTask extends SwingWorker<Boolean, Stock> {        
        final Code code;
        final Symbol symbol;
        
        public StockTask(Code code, Symbol symbol) {
            this.code = code;
            this.symbol = symbol;
        }
        
        @Override
        public Boolean doInBackground() {
            Boolean success = false;
            Stock s = null;
            int tries = 0;
            
            JStock m = JStock.instance();

            if (m == null) {
                publish(s);
                return success; 
            }
            
            m.setStatusBar(true, GUIBundle.getString("IndicatorPanel_StockSampleDataRetrievingInProgress..."));
            
            while (!isCancelled() && !success) {
                for (StockServerFactory factory : Factories.INSTANCE.getStockServerFactories(code)) {
                    
                    StockServer server = factory.getStockServer();
                    
                    if (server == null) {
                        continue;
                    }
                    
                    s = server.getStock(code);
                    if (s != null) {
                        success = true;
                        break;
                    }

                    if (isCancelled()) {
                        break;
                    }
                }
                
                tries++;
                
                // We had tried NUM_OF_RETRY times, but still failed. Abort.
                if (tries >= NUM_OF_RETRY) {
                    break;
                }

            }
            
            publish(s);
            
            return success;
         }

        @Override
         protected void process(java.util.List<Stock> stocks) {
             for (Stock stock : stocks) {
                if (this.isCancelled()) {
                    return;
                }

                final JStock m = JStock.instance();
                 
                if (stock != null) {
                    Stock new_stock = stock;

                    // Special handling for China stock market.
                    // Also, sometimes for other countries, Yahoo will return
                    // empty string for their symbol. We will fix it through
                    // offline database.
                    if (org.yccheok.jstock.engine.Utils.isSymbolImmutable() || new_stock.symbol.toString().isEmpty()) {
                        final StockInfoDatabase stockInfoDatabase = m.getStockInfoDatabase();
                        if (stockInfoDatabase != null) {
                            final Symbol _symbol = stockInfoDatabase.codeToSymbol(new_stock.code);
                            if (_symbol != null) {
                                new_stock = new_stock.deriveStock(_symbol);
                            }
                        }
                    }

                    if (org.yccheok.jstock.engine.Utils.isNameImmutable()) {
                        final StockNameDatabase stockNameDatabase = m.getStockNameDatabase();
                        if (stockNameDatabase != null) {
                            final String _name = stockNameDatabase.codeToName(new_stock.code);
                            if (_name != null) {
                                new_stock = new_stock.deriveStock(_name);
                            }
                        }
                    }

                    ((ObjectInspectorJPanel)objectInspectorJPanel).setBean(new MutableStock(new_stock));

                    if (m != null) {
                        m.setStatusBar(false, GUIBundle.getString("IndicatorPanel_StockSampleDataRetrievedSuccess"));
                    }
                } else {
                    if (m != null) {
                        m.setStatusBar(false, GUIBundle.getString("IndicatorPanel_StockSampleDataRetrievedFailed"));
                    }
                }
             }
         }        
    }

    /**
     * Dettach all and stop Ajax threading activity in combo box. Once stop,
     * this combo box can no longer be reused.
     */
    public void dettachAllAndStopAutoCompleteJComboBox() {
        // We are no longer interest to receive any event from combo box.
        ((AutoCompleteJComboBox)this.jComboBox1).dettachAll();
        // Stop all threading activities in AutoCompleteJComboBox.
        ((AutoCompleteJComboBox)this.jComboBox1).stop();
    }

    private void stop()
    {
        final StockTask tmp = this.stockTask;

        if (tmp != null) {
            tmp.cancel(true);
            this.stockTask = null;
            log.info("Terminated stock task");
        }

        final Thread thread = this.simulationThread;
        // Set null to stop the simulation thread.
        this.simulationThread = null;
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            }
            catch (InterruptedException exp) {
                log.error(null, exp);
            }
            
            log.info("Terminated simulation thread");
        }        
    }

    // Due to the unknown problem in netbeans IDE, we will add in the tooltip
    // and icon seperately.
    private void createToolTipTextForJTabbedPane() {
        this.jTabbedPane1.setToolTipTextAt(0, GUIBundle.getString("IndicatorPanel_AlertIndicatorToolTip"));
        //this.jTabbedPane1.setToolTipTextAt(1, GUIBundle.getString("IndicatorPanel_ModuleIndicatorToolTip"));
    }

    // Run by worker thread only.
    private void simulate(final Stock stock) {
        JStock m = JStock.instance();

        // First, check whether there is a need to get history.
        final IndicatorDefaultDrawing indicatorDefaultDrawing = (IndicatorDefaultDrawing)this.view.getDrawing();
        final OperatorIndicator operatorIndicator = indicatorDefaultDrawing.getOperatorIndicator();
        final Duration historyDuration = operatorIndicator.getNeededStockHistoryDuration();
        final Thread currentThread = Thread.currentThread();

        // When stock is null, this means this indicator needs neither stock real-time information
        // nor stock history information.
        if (stock != null && operatorIndicator.isStockHistoryServerNeeded()) {
            m.setStatusBar(true, MessagesBundle.getString("info_message_stock_history_retrieving_in_progress..."));

            // Avoid from using old history monitor. Their duration are not the same.
            final Duration oldDuration = stockHistoryMonitor.getDuration();
            if (oldDuration == null || oldDuration.equals(historyDuration) == false)
            {
                this.initStockHistoryMonitor();
                this.stockHistoryMonitor.setDuration(historyDuration);
            }

            // Action!
            StockHistoryServer stockHistoryServer = this.stockHistoryMonitor.getStockHistoryServer(stock.code);

            if (stockHistoryServer == null) {
                final java.util.concurrent.CountDownLatch countDownLatch = new java.util.concurrent.CountDownLatch(1);
                final org.yccheok.jstock.engine.Observer<StockHistoryMonitor, StockHistoryMonitor.StockHistoryRunnable> observer = new org.yccheok.jstock.engine.Observer<StockHistoryMonitor, StockHistoryMonitor.StockHistoryRunnable>() {
                    @Override
                    public void update(StockHistoryMonitor monitor, StockHistoryMonitor.StockHistoryRunnable runnable)
                    {
                        if (runnable.getCode().equals(stock.code)) {
                            countDownLatch.countDown();
                        }
                    }
                };

                this.stockHistoryMonitor.attach(observer);
                this.stockHistoryMonitor.addStockCode(stock.code);
                try {
                    countDownLatch.await();
                }
                catch (java.lang.InterruptedException exp) {
                    log.error(null, exp);
                    return;
                }
                this.stockHistoryMonitor.dettach(observer);
                stockHistoryServer = this.stockHistoryMonitor.getStockHistoryServer(stock.code);
            }

            if (stockHistoryServer == null) {
                // Start button.
                this.jButton4.setEnabled(true);
                // Stop button.
                this.jButton6.setEnabled(false);
                m.setStatusBar(false, MessagesBundle.getString("info_message_history_not_found"));
                return;
            }

            if (currentThread.isInterrupted() || simulationThread != currentThread) {
                return;
            }

            m.setStatusBar(true, MessagesBundle.getString("info_message_stock_history_information_calculation_in_progress..."));
            operatorIndicator.setStockHistoryServer(stockHistoryServer);
        }   /* if(operatorIndicator.isStockHistoryServerNeeded()) { */
        
        if (currentThread.isInterrupted() || simulationThread != currentThread) {
            return;
        }
        
        m.setStatusBar(true, MessagesBundle.getString("info_message_real_time_stock_information_calculation_in_progress..."));
        
        operatorIndicator.preCalculate();
        
        if (currentThread.isInterrupted() || simulationThread != currentThread) {
            return;
        }
        
        m.setStatusBar(true, MessagesBundle.getString("info_message_final_calculation..."));
   
        long startTime = System.nanoTime();

        if (stock != null) {
            operatorIndicator.setStock(stock);
        }
        
        operatorIndicator.isTriggered();
   
        long estimatedTime = System.nanoTime() - startTime;

        if (currentThread.isInterrupted() || simulationThread != currentThread) {
            return;
        }

        final String output = MessageFormat.format(
                MessagesBundle.getString("info_message_simulation_done_with_time_taken_template"),
                ((double)estimatedTime / (double)1000000.0)
        );
        m.setStatusBar(false, output);

        // Start button.
        this.jButton4.setEnabled(true);
        // Stop button.
        this.jButton6.setEnabled(false);
    }
    
    public final void initIndicatorProjectManager() {
        File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "indicator" + File.separator + "project.xml");
        this.alertIndicatorProjectManager = org.yccheok.jstock.gui.Utils.fromXML(IndicatorProjectManager.class, f);
        if (this.alertIndicatorProjectManager != null) {
            log.info("alertIndicatorProjectManager loaded from " + f.toString() + " successfully.");
        }
        else {
            this.alertIndicatorProjectManager = new IndicatorProjectManager(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "indicator", OperatorIndicator.Type.AlertIndicator);
        }         
        
        // Cleanup unused files.
        Set<String> validFileNames = new HashSet<String>();
        for (int i = 0, size = this.alertIndicatorProjectManager.getNumOfProject(); i < size; i++) {
            String PROJECTNAME = this.alertIndicatorProjectManager.getProject(i).toUpperCase();
            validFileNames.add(PROJECTNAME + ".XML");
            validFileNames.add(PROJECTNAME + "-JHOTDRAW.XML");
        }
        validFileNames.add("PROJECT.XML");
        
        File[] files = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "indicator" + File.separator).listFiles();
        for (File file : files) {
            if (validFileNames.contains(file.getName().toUpperCase())) {
                continue;
            }
            file.delete();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final DefaultListModel defaultListModel = (DefaultListModel)IndicatorPanel.this.jList1.getModel();
                defaultListModel.clear();
                for (int i = 0; i < IndicatorPanel.this.alertIndicatorProjectManager.getNumOfProject(); i++) {
                    defaultListModel.addElement(IndicatorPanel.this.alertIndicatorProjectManager.getProject(i));
                }
                if (IndicatorPanel.this.jList1.getModel().getSize() > 0) {
                    // Select first element.
                    IndicatorPanel.this.jList1.setSelectedIndex(0);
                }
            }
        });
    }

    public final void initModuleProjectManager() {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "module" + File.separator + "project.xml");
        this.moduleIndicatorProjectManager = org.yccheok.jstock.gui.Utils.fromXML(IndicatorProjectManager.class, f);
        if (this.moduleIndicatorProjectManager != null) {
            log.info("moduleIndicatorProjectManager loaded from " + f.toString() + " successfully.");
        }
        else {
            this.moduleIndicatorProjectManager = new IndicatorProjectManager(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "module", OperatorIndicator.Type.ModuleIndicator);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final DefaultListModel defaultListModel = (DefaultListModel)jList2.getModel();
                defaultListModel.clear();
                for (int i = 0; i < IndicatorPanel.this.moduleIndicatorProjectManager.getNumOfProject(); i++) {
                    defaultListModel.addElement(IndicatorPanel.this.moduleIndicatorProjectManager.getProject(i));
                }
            }
        });
    }

    public boolean saveModuleIndicatorProjectManager() {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "module" + File.separator + "project.xml");
        return Utils.toXML(this.moduleIndicatorProjectManager, f);
    }

    public boolean saveAlertIndicatorProjectManager() {
        final File f = new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "indicator" + File.separator + "project.xml");
        return Utils.toXML(this.alertIndicatorProjectManager, f);
    }
    
    public IndicatorProjectManager getAlertIndicatorProjectManager() {
        return this.alertIndicatorProjectManager;
    }

    public IndicatorProjectManager getModuleIndicatorProjectManager() {
        return this.moduleIndicatorProjectManager;
    }

    public void initStockHistoryMonitor() {
        final StockHistoryMonitor oldStockHistoryMonitor = stockHistoryMonitor;
        if (oldStockHistoryMonitor != null) {            
            Utils.getZoombiePool().execute(new Runnable() {
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

        stockHistoryMonitor.setStockHistorySerializer(new StockHistorySerializer(Utils.getHistoryDirectory()));
    }

    private JList getCurrentActiveJList() {
        if (this.jTabbedPane1.getSelectedIndex() == 0) {
            return this.jList1;
        }
        else {
            assert(this.jTabbedPane1.getSelectedIndex() == 1);
            return this.jList2;
        }
    }

    private IndicatorProjectManager getCurrentActiveIndicatorProjectManager() {
        if (this.jTabbedPane1.getSelectedIndex() == 0) {
            return this.alertIndicatorProjectManager;
        }
        else {
            assert(this.jTabbedPane1.getSelectedIndex() == 1);
            return this.moduleIndicatorProjectManager;
        }
    }

    private Wizard getWizardDialog() {
        final JStock m = JStock.instance();
        final Wizard wizard = new Wizard(m);
        wizard.getDialog().setTitle(java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui").getString("IndicatorPanel_IndicatorInstallWizard"));
        wizard.getDialog().setResizable(false);
        WizardPanelDescriptor wizardSelectInstallIndicatorMethodDescriptor = new WizardSelectInstallIndicatorMethodDescriptor();
        wizard.registerWizardPanel(WizardSelectInstallIndicatorMethodDescriptor.IDENTIFIER, wizardSelectInstallIndicatorMethodDescriptor);
        wizard.setCurrentPanel(WizardSelectInstallIndicatorMethodDescriptor.IDENTIFIER);
        WizardPanelDescriptor wizardSelectIndicatorDescriptor = new WizardSelectIndicatorDescriptor(getCurrentActiveIndicatorProjectManager());
        wizard.registerWizardPanel(WizardSelectIndicatorDescriptor.IDENTIFIER, wizardSelectIndicatorDescriptor);
        WizardPanelDescriptor wizardDownloadIndicatorDescriptor = new WizardDownloadIndicatorDescriptor(getCurrentActiveIndicatorProjectManager());
        wizard.registerWizardPanel(WizardDownloadIndicatorDescriptor.IDENTIFIER, wizardDownloadIndicatorDescriptor);
        // Center to screen.
        wizard.getDialog().setLocationRelativeTo(null);
        wizard.getDialog().setSize(200, 200);
        return wizard;
    }

    private void initListCellRenderer() {
        assert(this.moduleIndicatorProjectManager != null);
        assert(this.alertIndicatorProjectManager != null);
        assert(this.jList1 != null);
        assert(this.jList2 != null);
        this.jList1.setCellRenderer(getListCellRenderer(alertIndicatorProjectManager));
        this.jList2.setCellRenderer(getListCellRenderer(moduleIndicatorProjectManager));
    }
    
    private ListCellRenderer getListCellRenderer(final IndicatorProjectManager projectManager) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (component != null && value != null) {
                    final OperatorIndicator operatorIndicator = projectManager.getOperatorIndicator(value.toString());
                    if (operatorIndicator != null && operatorIndicator.getType() != projectManager.getPreferredOperatorIndicatorType()) {
                        final Font oldFont = component.getFont();
                        component.setFont(oldFont.deriveFont(oldFont.getStyle() | Font.ITALIC));
                    }
                }
                return component;
            }
        };
    }

    private void syncJListWithIndicatorProjectManager(JList jList, IndicatorProjectManager indicatorProjectManager) {
        final String projectName = (String)jList.getSelectedValue();
        boolean isProjectNameBeingRemoved = false;
        int newSelection = -1;

        final ListModel listModel = jList.getModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            if (indicatorProjectManager.contains(listModel.getElementAt(i).toString()) == false) {
                // Remove from JList, as it is not found in indicator project manager.
                Object removedObject = ((DefaultListModel)listModel).remove(i);
                if (projectName.equals(removedObject)) {
                    isProjectNameBeingRemoved = true;
                    newSelection = i;
                }
                i--;
            }
        }
        for (int i = 0; i < indicatorProjectManager.getNumOfProject(); i++) {
            final String p = indicatorProjectManager.getProject(i);
            if (((DefaultListModel)listModel).contains(p) == false) {
                // Add to JList, as it is found in indicator project manager.
                ((DefaultListModel)listModel).addElement(p);
            }
        }

        if (!isProjectNameBeingRemoved) {
            // Ensure list cell renderer is being triggered.
            jList.setSelectedValue(projectName, true);
        }
        else {
            if (newSelection >= jList.getModel().getSize()) {
                // Select last row.
                jList.setSelectedIndex(jList.getModel().getSize() - 1);
            }
            else {
                jList.setSelectedIndex(newSelection);
            }
        }
    }

    private StockHistoryMonitor stockHistoryMonitor = null;

    private static final int HISTORY_MONITOR_MAX_THREAD = 1;

    private IndicatorProjectManager alertIndicatorProjectManager;
    private IndicatorProjectManager moduleIndicatorProjectManager;

    private static final Log log = LogFactory.getLog(IndicatorPanel.class);
    
    private StockTask stockTask;
    
    private volatile Thread simulationThread;

    private static final int NUM_OF_RETRY = 3;

    /* Hacking, in order to make when user select another project, we will able
     * to prompt user to save unsaved drawing.
     *
     * It is never easy to perform "Validate JList Before Selection Occur".
     * When we receive event triggering and perform validation, it is too late
     * to undo the selection.
     * That's why hacking comes into place :)
     */
    private ListSelectionEx listSelectionEx = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar creationToolbar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel objectInspectorJPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.ButtonGroup toolButtonGroup;
    private org.jhotdraw.draw.DefaultDrawingView view;
    // End of variables declaration//GEN-END:variables
    
}
