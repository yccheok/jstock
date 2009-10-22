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
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.yccheok.jstock.gui.IndicatorProjectManager;

/**
 *
 * @author yccheok
 */
public class WizardDownloadIndicatorDescriptor extends WizardPanelDescriptor implements Observer {
    public static final String IDENTIFIER = "WizardDownloadIndicatorDescriptor";

    public WizardDownloadIndicatorDescriptor(IndicatorProjectManager indicatorProjectManager) {
        super(IDENTIFIER, new WizardDownloadIndicatorJPanel(indicatorProjectManager));
        this.wizardDownloadIndicatorJPanel = (WizardDownloadIndicatorJPanel)getPanelComponent();
        this.wizardDownloadIndicatorJPanel.addObserver(this);
    }

    @Override
    public Object getNextPanelDescriptor() {
        return FINISH;
    }

    @Override
    public Object getBackPanelDescriptor() {
        return WizardSelectIndicatorDescriptor.IDENTIFIER;
    }

    @Override
    public void aboutToDisplayPanel() {
    }

    @Override
    public void displayingPanel() {        
        final WizardSelectIndicatorJPanel wizardSelectIndicatorJPanel = (WizardSelectIndicatorJPanel)this.getWizardModel().getPanelDescriptor(WizardSelectIndicatorDescriptor.IDENTIFIER).getPanelComponent();
        final List<IndicatorDownloadManager.Info> infos = wizardSelectIndicatorJPanel.getPlannedInstallIndicatorDownloadInfos();

        wizardDownloadIndicatorJPanel.setIndicatorDownloadInfos(infos);
        wizardDownloadIndicatorJPanel.start();
        this.updateGUIState();
    }

    @Override
    public void aboutToHidePanel() {
        wizardDownloadIndicatorJPanel.cancel();
    }

    private void updateGUIState() {
        final boolean flag = wizardDownloadIndicatorJPanel.isNextFinishButtonEnabled();
        getWizard().setNextFinishButtonEnabled(flag);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.updateGUIState();
    }

    private final WizardDownloadIndicatorJPanel wizardDownloadIndicatorJPanel;
}
