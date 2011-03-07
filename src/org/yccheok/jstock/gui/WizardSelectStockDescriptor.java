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

import com.nexes.wizard.*;
import java.awt.event.*;

import org.yccheok.jstock.engine.StockInfoDatabase;

/**
 *
 * @author yccheok
 */
public class WizardSelectStockDescriptor extends WizardPanelDescriptor implements ActionListener {
    
    public static final String IDENTIFIER = "SELECT_STOCK_PANEL";
    
    private WizardSelectStockJPanel wizardSelectStockJPanel;
    
    public WizardSelectStockDescriptor(StockInfoDatabase stockInfoDatabase) {
        super(IDENTIFIER, new WizardSelectStockJPanel(stockInfoDatabase));
        
        wizardSelectStockJPanel = (WizardSelectStockJPanel)getPanelComponent();
        wizardSelectStockJPanel.addActionListenerForAllComponents(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        getWizard().setNextFinishButtonEnabled(wizardSelectStockJPanel.isNextFinishButtonEnabled());
    }
    
    @Override
    public Object getNextPanelDescriptor() {
         return FINISH;
    }
    
    @Override
    public void aboutToDisplayPanel() {
        wizardSelectStockJPanel.updateRadioBoxState();
    }
    
    @Override
    public void aboutToHidePanel() {
        // 14/10/09 Update : This function works after Cheok performs hacking on Wizard.java
        // Not sure why. When I click finish button, this function isn't being called.
    }
    
    @Override
    public Object getBackPanelDescriptor() {
        return WizardSelectIndicatorDescriptor.IDENTIFIER;
    }
}
