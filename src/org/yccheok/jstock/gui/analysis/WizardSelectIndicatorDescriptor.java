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
import java.util.Observable;
import java.util.Observer;
import org.yccheok.jstock.gui.IndicatorProjectManager;

/**
 *
 * @author yccheok
 */
public class WizardSelectIndicatorDescriptor extends WizardPanelDescriptor implements Observer {
    public static final String IDENTIFIER = "WizardSelectIndicatorDescriptor";

    public WizardSelectIndicatorDescriptor(IndicatorProjectManager indicatorProjectManager) {
        super(IDENTIFIER, new WizardSelectIndicatorJPanel(indicatorProjectManager));

        this.wizardSelectIndicatorJPanel = (WizardSelectIndicatorJPanel)getPanelComponent();
        this.wizardSelectIndicatorJPanel.addObserver(this);
    }

    @Override
    public Object getNextPanelDescriptor() {
        if (this.wizardSelectIndicatorJPanel.useFinishButton())
        {
            return FINISH;
        }
        return WizardDownloadIndicatorDescriptor.IDENTIFIER;
    }

    @Override
    public Object getBackPanelDescriptor() {
        return WizardSelectInstallIndicatorMethodDescriptor.IDENTIFIER;
    }

    @Override
    public void aboutToDisplayPanel() {
    }

    @Override
    public void displayingPanel() {
        this.updateGUIState();
        wizardSelectIndicatorJPanel.start();
    }

    @Override
    public void aboutToHidePanel() {
        wizardSelectIndicatorJPanel.cancel();
    }

    private void updateGUIState() {
        getWizard().setNextFinishButtonEnabled(wizardSelectIndicatorJPanel.isNextFinishButtonEnabled());
        if (wizardSelectIndicatorJPanel.isNextFinishButtonEnabled()) {
            // Update button to determine whether we shall go for next or finish.
            getWizard().getController().resetButtonsToPanelRules();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.updateGUIState();
    }
    
    private final WizardSelectIndicatorJPanel wizardSelectIndicatorJPanel;
}
