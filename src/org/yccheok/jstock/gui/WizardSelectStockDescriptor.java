/*
 * WizardSelectStockDescriptor.java
 *
 * Created on June 16, 2007, 12:58 AM
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
import java.awt.event.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.StockCodeAndSymbolDatabase;

/**
 *
 * @author yccheok
 */
public class WizardSelectStockDescriptor extends WizardPanelDescriptor implements ActionListener {
    
    public static final String IDENTIFIER = "SELECT_STOCK_PANEL";
    
    private WizardSelectStockJPanel wizardSelectStockJPanel;
    
    public WizardSelectStockDescriptor(StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase) {
        super(IDENTIFIER, new WizardSelectStockJPanel(stockCodeAndSymbolDatabase));
        
        wizardSelectStockJPanel = (WizardSelectStockJPanel)getPanelComponent();
        wizardSelectStockJPanel.installActionListenerToAllComponents(this);
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        getWizard().setNextFinishButtonEnabled(wizardSelectStockJPanel.isSelectionValid());
    }
    
    public Object getNextPanelDescriptor() {
         return FINISH;
    }
    
    public void aboutToDisplayPanel() {
        wizardSelectStockJPanel.updateRadioBoxState();
    }
    
    @Override
    public void aboutToHidePanel() {
		// Not sure why. When I click finish button, this function isn't being called.
    }
    
    public Object getBackPanelDescriptor() {
        return WizardSelectIndicatorDescriptor.IDENTIFIER;
    }      
    
    private static final Log log = LogFactory.getLog(WizardSelectStockJPanel.class);
}
