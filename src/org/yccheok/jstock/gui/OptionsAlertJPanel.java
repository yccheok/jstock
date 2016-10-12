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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.alert.GoogleMail;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;

/**
 *
 * @author  yccheok
 */
public class OptionsAlertJPanel extends javax.swing.JPanel implements JStockOptionsObserver {
    
    /** Creates new form OptionsAlertJPanel */
    public OptionsAlertJPanel() {
        initComponents();
        initCredentialEx();
    }
    
    private void signOut() {
        this.credentialEx = null;
        jCheckBox2.setSelected(false);
        JStock.instance().getJStockOptions().setSendEmail(jCheckBox2.isSelected());
        jTextField1.setText("");
        org.yccheok.jstock.google.Utils.logoutGmail();
        updateGUIState();
    }
    
    private void signIn() {
        jCheckBox2.setEnabled(false);
        
        SwingWorker swingWorker = new SwingWorker<Pair<Pair<Credential, String>, Boolean>, Void>() {

            @Override
            protected Pair<Pair<Credential, String>, Boolean> doInBackground() throws Exception {
                final Pair<Pair<Credential, String>, Boolean> pair = org.yccheok.jstock.google.Utils.authorizeGmail();
                return pair;
            }
            
            @Override
            public void done() {
                Pair<Pair<Credential, String>, Boolean> pair = null;
                
                try {
                    pair = this.get();
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(OptionsAlertJPanel.this, ex.getMessage(), GUIBundle.getString("OptionsAlertJPanel_Alert"), JOptionPane.ERROR_MESSAGE);
                    log.error(null, ex);
                } catch (ExecutionException ex) {
                    Throwable throwable = ex.getCause();
                    if (throwable instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
                        com.google.api.client.googleapis.json.GoogleJsonResponseException ge = (com.google.api.client.googleapis.json.GoogleJsonResponseException)throwable;
                        for (GoogleJsonError.ErrorInfo errorInfo : ge.getDetails().getErrors()) {
                            if ("insufficientPermissions".equals(errorInfo.getReason())) {
                                org.yccheok.jstock.google.Utils.logoutGmail();
                                break;
                            }
                        }                        
                    }
                    
                    JOptionPane.showMessageDialog(OptionsAlertJPanel.this, ex.getMessage(), GUIBundle.getString("OptionsAlertJPanel_Alert"), JOptionPane.ERROR_MESSAGE);
                    log.error(null, ex);
                }                
                
                if (pair != null) {
                    credentialEx = pair.first;
                } else {
                    jCheckBox2.setSelected(false);
                    JStock.instance().getJStockOptions().setSendEmail(jCheckBox2.isSelected());
                }
                
                updateGUIState();                
            }
        };
        
        swingWorker.execute();
    }

    private void initCredentialEx() {
        SwingWorker swingWorker = new SwingWorker<Pair<Credential, String>, Void>() {

            @Override
            protected Pair<Credential, String> doInBackground() throws Exception {
                final Pair<Credential, String> pair = org.yccheok.jstock.google.Utils.authorizeGmailOffline();
                return pair;
            }
            
            @Override
            public void done() { 
                Pair<Credential, String> pair = null;
                
                try {
                    pair = this.get();
                } catch (InterruptedException ex) {
                    log.error(null, ex);
                } catch (ExecutionException ex) {
                    Throwable throwable = ex.getCause();
                    if (throwable instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
                        com.google.api.client.googleapis.json.GoogleJsonResponseException ge = (com.google.api.client.googleapis.json.GoogleJsonResponseException)throwable;
                        for (GoogleJsonError.ErrorInfo errorInfo : ge.getDetails().getErrors()) {
                            if ("insufficientPermissions".equals(errorInfo.getReason())) {
                                org.yccheok.jstock.google.Utils.logoutGmail();
                                break;
                            }
                        }
                        
                    }
                    
                    log.error(null, ex);
                }
                
                if (pair != null) {
                    credentialEx = pair;
                    jCheckBox2.setSelected(JStock.instance().getJStockOptions().isSendEmail());
                }
                
                updateGUIState();
            }
        };
        
        swingWorker.execute();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jXHeader1 = new org.jdesktop.swingx.JXHeader();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(24, 0), new java.awt.Dimension(24, 0), new java.awt.Dimension(24, 32767));
        jButton2 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jPanel5 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        jXHeader1.setDescription(bundle.getString("OptionsAlertJPanel_description")); // NOI18N
        jXHeader1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32x32/bell.png"))); // NOI18N
        jXHeader1.setTitle(bundle.getString("OptionsAlertJPanel_Alert")); // NOI18N
        add(jXHeader1, java.awt.BorderLayout.NORTH);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("OptionsAlertJPanel_Email"))); // NOI18N

        jCheckBox2.setText(bundle.getString("OptionsAlertJPanel_SendMessageToEmails")); // NOI18N
        jCheckBox2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox2ItemStateChanged(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/mail_send.png"))); // NOI18N
        jButton1.setText(bundle.getString("OptionsAlertJPanel_TestEmail")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/spinner.gif"))); // NOI18N

        jLabel11.setText(bundle.getString("OptionsAlertJPanel_CCCopy")); // NOI18N
        jLabel11.setEnabled(false);

        jTextField1.setEnabled(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel12.setFont(jLabel12.getFont().deriveFont(jLabel12.getFont().getSize()-1f));
        jLabel12.setText(bundle.getString("OptionsAlertJPanel_EmailExample")); // NOI18N
        jLabel12.setEnabled(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("OptionsAlertJPanel_GmalAccount"))); // NOI18N
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanel2.add(filler1);

        jLabel1.setBackground(new java.awt.Color(140, 196, 116));
        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+1));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("username@email.com");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jLabel1.setOpaque(true);
        jPanel2.add(jLabel1);
        jPanel2.add(filler3);

        jButton2.setText(bundle.getString("OptionsAlertJPanel_SignOut")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);
        jPanel2.add(filler2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("OptionsAlertJPanel_System"))); // NOI18N

        jCheckBox1.setText(bundle.getString("OptionsAlertJPanel_ShowAMessage")); // NOI18N
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox4.setText(bundle.getString("OptionsAlertJPanel_PlayingAlertSound")); // NOI18N
        jCheckBox4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBox4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox4ItemStateChanged(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/sound.png"))); // NOI18N
        jButton3.setText(bundle.getString("OptionsAlertJPanel_TestSound")); // NOI18N
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jCheckBox4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jCheckBox1))
                .addGap(134, 134, 134))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox4)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        this.testEmailSwingWorker = getTestEmailSwingWorker();
        this.updateGUIState();
        this.jButton1.requestFocus();
        this.testEmailSwingWorker.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox2ItemStateChanged
        updateGUIState();
        
        if (jCheckBox2.isSelected()) {
            if (this.credentialEx == null) {
                signIn();
            }
        }
    }//GEN-LAST:event_jCheckBox2ItemStateChanged

    private void jCheckBox4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox4ItemStateChanged
        updateGUIState();
    }//GEN-LAST:event_jCheckBox4ItemStateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Utils.playAlertSound();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        jButton1.doClick();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        signOut();
    }//GEN-LAST:event_jButton2ActionPerformed

    public void cancel() {
        if (this.testEmailSwingWorker != null) {
            this.testEmailSwingWorker.cancel(true);
        }
    }

    @Override
    public void set(JStockOptions jStockOptions) {
        jCheckBox1.setSelected(jStockOptions.isPopupMessage());
        jCheckBox2.setSelected(jStockOptions.isSendEmail());
        jCheckBox4.setSelected(jStockOptions.isSoundEnabled());
        jTextField1.setText(Utils.decrypt(jStockOptions.getCCEmail()));
        
        updateGUIState();
    }

    @Override
    public boolean apply(JStockOptions jStockOptions) {
        if (jCheckBox2.isSelected()) {           
        }

        jStockOptions.setSoundEnabled(jCheckBox4.isSelected());
        jStockOptions.setPopupMessage(jCheckBox1.isSelected());
        jStockOptions.setSendEmail(jCheckBox2.isSelected());
        jStockOptions.setCCEmail(Utils.encrypt(jTextField1.getText().trim()));

        return true;
    }

    /**
     * Update GUI components enable/disable state, according to current
     * selection.
     */
    private void updateGUIState() {
        final boolean isTestEmailDone = (testEmailSwingWorker == null || testEmailSwingWorker.isDone());

        final boolean soundState = jCheckBox4.isSelected();
        final boolean emailState = jCheckBox2.isSelected();

        jButton3.setEnabled(soundState);

        jButton1.setEnabled(emailState && isTestEmailDone && this.credentialEx != null);
        jButton2.setEnabled(emailState && isTestEmailDone && this.credentialEx != null);
        jCheckBox2.setEnabled(isTestEmailDone);
        jLabel7.setVisible(!isTestEmailDone);
        
        jLabel11.setEnabled(emailState && this.credentialEx != null);
        jLabel12.setEnabled(emailState && this.credentialEx != null);
        jTextField1.setEnabled(emailState && isTestEmailDone && this.credentialEx != null);                      
        
        if (this.credentialEx == null) {
            jLabel1.setText("?");
        } else {
            jLabel1.setText(credentialEx.second);
        }  
    }
    
    /**
     * Get Swing worker thread which performs email testing task.
     * @return Swing wroker thread which performs email testing task
     */
    private SwingWorker getTestEmailSwingWorker() {
        SwingWorker worker = new SwingWorker<Boolean, Void>() {
            @Override
            public Boolean doInBackground() {
                boolean status = true;

                Pair<Credential, String> credentialEx = OptionsAlertJPanel.this.credentialEx;
                if (credentialEx == null) {
                    status = false;
                } else {
                    final String ccEmail = jTextField1.getText().trim();
                    final String title = MessagesBundle.getString("info_message_congratulation_email_alert_system_is_working");
                    final String message = MessagesBundle.getString("info_message_congratulation_email_alert_system_is_working");
                    
                    try {
                        GoogleMail.Send(ccEmail, title, message);
                    } catch (Exception ex) {
                        log.error(null, ex);
                        status = false;
                    }
                }
                return status;
            }

            @Override
            public void done() {
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

                Boolean status = null;
                try {
                    status = get();
                } catch (InterruptedException ex) {
                    log.error(null, ex);
                } catch (ExecutionException ex) {
                    log.error(null, ex);
                } catch (CancellationException ex) {
                    // Some developers suggest to catch this exception, instead of
                    // checking on isCancelled. As I am not confident by merely
                    // isCancelled check can prevent CancellationException (What
                    // if cancellation is happen just after isCancelled check?),
                    // I will apply both techniques. 
                    log.error(null, ex);
                }

                OptionsAlertJPanel.this.updateGUIState();

                if (status == null || status == false)
                {
                    JOptionPane.showMessageDialog(OptionsAlertJPanel.this, MessagesBundle.getString("error_message_error_in_sending_email"), MessagesBundle.getString("error_title_error_in_sending_email"), JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(OptionsAlertJPanel.this, MessagesBundle.getString("info_message_email_alert_system_is_working"), MessagesBundle.getString("info_title_email_alert_system_is_working"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };

        return worker;
    }
    
    private Pair<Credential, String> credentialEx;
    
    private volatile SwingWorker testEmailSwingWorker = null;

    private static final Log log = LogFactory.getLog(OptionsAlertJPanel.class);
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.swingx.JXHeader jXHeader1;
    // End of variables declaration//GEN-END:variables
    
}
