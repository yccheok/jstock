/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui.analysis;

import com.nexes.wizard.WizardPanelDescriptor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author yccheok
 */
public class WizardSelectInstallIndicatorMethodDescriptor extends WizardPanelDescriptor implements ActionListener, DocumentListener {
    public static final String IDENTIFIER = "WizardSelectInstallIndicatorMethodDescriptor";

    /** Creates a new instance of WizardSelectIndicatorDescriptor */
    public WizardSelectInstallIndicatorMethodDescriptor() {
        super(IDENTIFIER, new WizardSelectInstallIndicatorMethodJPanel());

        this.wizardSelectInstallIndicatorMethodJPanel = (WizardSelectInstallIndicatorMethodJPanel)getPanelComponent();
        this.wizardSelectInstallIndicatorMethodJPanel.addActionListenerForAllComponents(this);
        this.wizardSelectInstallIndicatorMethodJPanel.addDocumentListenerForAllComponents(this);
    }

    @Override
    public Object getNextPanelDescriptor() {
        if (this.wizardSelectInstallIndicatorMethodJPanel.isLocalFileSelected())
        {
            return FINISH;
        }
        else
        {
            return WizardSelectIndicatorDescriptor.IDENTIFIER;
        }
    }

    @Override
    public Object getBackPanelDescriptor() {
        return null;
    }

    @Override
    public void aboutToDisplayPanel() {
    }

    @Override
    public void displayingPanel() {
    }

    @Override
    public void aboutToHidePanel() {
    }

    private final WizardSelectInstallIndicatorMethodJPanel wizardSelectInstallIndicatorMethodJPanel;

    @Override
    public void actionPerformed(ActionEvent e) {
        this.updateGUIState();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.updateGUIState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.updateGUIState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.updateGUIState();
    }

    private void updateGUIState() {
        getWizard().setNextFinishButtonEnabled(wizardSelectInstallIndicatorMethodJPanel.isNextFinishButtonEnabled());
        if (wizardSelectInstallIndicatorMethodJPanel.isNextFinishButtonEnabled()) {
            // Update button to determine whether we shall go for next or finish.
            getWizard().getController().resetButtonsToPanelRules();
        }
    }
}
