/*
 * WizardDownloadHistoryProgressDescriptor.java
 *
 * Created on June 16, 2007, 6:51 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import com.nexes.wizard.*;
import org.yccheok.jstock.engine.Code;

/**
 *
 * @author yccheok
 */
public class WizardDownloadHistoryProgressDescriptor extends WizardPanelDescriptor implements java.beans.PropertyChangeListener {
    
    public static final String IDENTIFIER = "DOWNLOAD_HISTORY_PANEL";
    
    private WizardDownloadHistoryProgressJPanel wizardDownloadHistoryProgressJPanel;
    
    /** Creates a new instance of WizardDownloadHistoryProgressDescriptor */
    public WizardDownloadHistoryProgressDescriptor() {
        super(IDENTIFIER, new WizardDownloadHistoryProgressJPanel());
        
        wizardDownloadHistoryProgressJPanel = (WizardDownloadHistoryProgressJPanel)getPanelComponent();
        wizardDownloadHistoryProgressJPanel.addMyPropertyChangeListener(this);
    }
    
    public Object getNextPanelDescriptor() {
        return FINISH;
    }
    
    public Object getBackPanelDescriptor() {
        return WizardSelectStockDescriptor.IDENTIFIER;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt)
    {
        // The one and only one case where the property change event will be fired.
        getWizard().setNextFinishButtonEnabled(true);
    }
    
    //  Override this method in the subclass if you wish it to be called
    //  just before the panel is displayed.
    
    /**
     * Override this method to provide functionality that will be performed just before
     * the panel is to be displayed.
     */    
    public void aboutToDisplayPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        
        Wizard wizard = this.getWizard();
        WizardModel wizardModel = wizard.getModel();
        
        WizardPanelDescriptor wizardPanelDescriptor = wizardModel.getPanelDescriptor(WizardSelectStockDescriptor.IDENTIFIER);
        WizardSelectStockJPanel wizardSelectStockJPanel = (WizardSelectStockJPanel)wizardPanelDescriptor.getPanelComponent();
        
        wizardPanelDescriptor = wizardModel.getPanelDescriptor(WizardSelectIndicatorDescriptor.IDENTIFIER);
        WizardSelectIndicatorJPanel wizardSelectIndicatorJPanel = (WizardSelectIndicatorJPanel)wizardPanelDescriptor.getPanelComponent();
        
        java.util.List<String> projects = wizardSelectIndicatorJPanel.getSelectedProjects();
        java.util.List<Code> codes = wizardSelectStockJPanel.getSelectedCodes();

        wizardDownloadHistoryProgressJPanel.startDownload(projects, codes);
    }
 
    //  Override this method in the subclass if you wish to do something
    //  while the panel is displaying.
    
    /**
     * Override this method to perform functionality when the panel itself is displayed.
     */    
    public void displayingPanel() {

    }
 
    //  Override this method in the subclass if you wish it to be called
    //  just before the panel is switched to another or finished.
    
    /**
     * Override this method to perform functionality just before the panel is to be
     * hidden.
     */    
    public void aboutToHidePanel() {
        wizardDownloadHistoryProgressJPanel.stopDownload();
    }    
}
