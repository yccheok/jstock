/*
 * WizardSelectIndicatorDescriptor.java
 *
 * Created on June 17, 2007, 1:34 AM
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
public class WizardSelectIndicatorDescriptor extends WizardPanelDescriptor implements javax.swing.event.ListSelectionListener {
    
    public static final String IDENTIFIER = "SELECT_INDICATOR_PANEL";
    
    /** Creates a new instance of WizardSelectIndicatorDescriptor */
    public WizardSelectIndicatorDescriptor() {
        super(IDENTIFIER, new WizardSelectIndicatorJPanel());
        
        wizardSelectIndicatorJPanel = (WizardSelectIndicatorJPanel)getPanelComponent();
        wizardSelectIndicatorJPanel.addListSelectionListener(this);                
    }
    
    public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        getWizard().setNextFinishButtonEnabled(wizardSelectIndicatorJPanel.getSelectedProjects().size() > 0);
    }
    
    @Override
    public Object getNextPanelDescriptor() {
        return WizardSelectStockDescriptor.IDENTIFIER;
    }
    
    @Override
    public Object getBackPanelDescriptor() {
        return null;
    }
    
    @Override
    public void aboutToDisplayPanel() {        
        wizardSelectIndicatorJPanel.updateAlertIndicatorProjectManager();
        getWizard().setNextFinishButtonEnabled(wizardSelectIndicatorJPanel.getSelectedProjects().size() > 0);
    }
   
    @Override
    public void displayingPanel() {
    }
   
    @Override
    public void aboutToHidePanel() {
    }
    
    private WizardSelectIndicatorJPanel wizardSelectIndicatorJPanel;
}
