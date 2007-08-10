/*
 * WizardSelectHistoryDescriptor.java
 *
 * Created on June 15, 2007, 11:16 PM
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

/**
 *
 * @author yccheok
 */
public class WizardSelectHistoryDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "SELECT_HISTORY_PANEL";
    
    private WizardSelectHistoryJPanel wizardSelectHistoryJPanel;
    
    public WizardSelectHistoryDescriptor() {
        super(IDENTIFIER, new WizardSelectHistoryJPanel());
        
        wizardSelectHistoryJPanel = (WizardSelectHistoryJPanel)getPanelComponent();        
    }
    
    public Object getNextPanelDescriptor() {
        if(wizardSelectHistoryJPanel.isDownloadLatestHistoryNeeded())
            return WizardDownloadHistoryProgressDescriptor.IDENTIFIER;
        else {
            if(wizardSelectHistoryJPanel.isVerificationNeeded())
                return WizardVerifyDatabaseDescriptor.IDENTIFIER;
            else
                return WizardIndicatorConstructionDescriptor.IDENTIFIER;
        }
    }
    
    public Object getBackPanelDescriptor() {
        return WizardSelectStockDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() {
        Wizard wizard = this.getWizard();
        WizardModel wizardModel = wizard.getModel();
        
        WizardPanelDescriptor wizardPanelDescriptor = wizardModel.getPanelDescriptor(WizardSelectStockDescriptor.IDENTIFIER);
        WizardSelectStockJPanel wizardSelectStockJPanel = (WizardSelectStockJPanel)wizardPanelDescriptor.getPanelComponent();
        
        getWizard().setNextFinishButtonEnabled(wizardSelectStockJPanel.getSelectedCodes().size() != 0);
        
        wizardSelectHistoryJPanel.updateNumberOfStockSelected(wizardSelectStockJPanel.getSelectedCodes().size());
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

    }        
}
