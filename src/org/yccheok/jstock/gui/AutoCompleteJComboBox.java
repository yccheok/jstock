/*
 * AutoCompleteJComboBox.java
 *
 * Created on May 29, 2007, 10:37 PM
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

import javax.swing.*;
import java.awt.event.*;
import org.yccheok.jstock.engine.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class AutoCompleteJComboBox extends JComboBox {
    
    /** Creates a new instance of AutoCompleteJComboBox */
    public AutoCompleteJComboBox() {
        this.stockCodeAndSymbolDatabase = null;
        
        this.setEditable(true);
        
        keyAdapter = this.getEditorComponentKeyAdapter();
        
        this.getEditor().getEditorComponent().addKeyListener(keyAdapter);
    }
    
    public void setStockCodeAndSymbolDatabase(StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase) {
        this.stockCodeAndSymbolDatabase = stockCodeAndSymbolDatabase;
        
        KeyListener[] listeners = this.getEditor().getEditorComponent().getKeyListeners();
        
        for(KeyListener listener : listeners) {
            if(listener == keyAdapter) {
                return;
            }
        }
        
        // Bug in Java 6. Most probably this listener had been removed during look n feel updating, reassign!
        this.getEditor().getEditorComponent().addKeyListener(keyAdapter);
        log.info("Reassign key adapter to combo box");        
    }
    
    // We should make this powerful combo box shared amoing different classes.
    private KeyAdapter getEditorComponentKeyAdapter() {
        
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(!e.isActionKey()) {
                    String string = AutoCompleteJComboBox.this.getEditor().getItem().toString();
                    
                    AutoCompleteJComboBox.this.hidePopup();                                        
                    
                    if(KeyEvent.VK_ENTER == e.getKeyCode()) {
                        if(AutoCompleteJComboBox.this.getItemCount() > 0) {
                            int index = AutoCompleteJComboBox.this.getSelectedIndex();
                            if(index == -1) {
                                lastEnteredString = (String)AutoCompleteJComboBox.this.getItemAt(0);
                                AutoCompleteJComboBox.this.getEditor().setItem(lastEnteredString);
                            }
                            else {
                                lastEnteredString = (String)AutoCompleteJComboBox.this.getItemAt(index);
                                AutoCompleteJComboBox.this.getEditor().setItem(lastEnteredString);
                            }
                        }
                        else {
                            final Object object = AutoCompleteJComboBox.this.getEditor().getItem();
                            
                            if(object instanceof String) {
                                lastEnteredString = (String)object;
                            }
                            else {
                                lastEnteredString = "";
                            }
                            
                            AutoCompleteJComboBox.this.getEditor().setItem(null);
                        }
                        
                        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                AutoCompleteJComboBox.this.removeAllItems();
                            }
                        });
                                                                        
                        return;
                    }   /* if(KeyEvent.VK_ENTER == e.getKeyCode()) */                                               
                    
                    AutoCompleteJComboBox.this.removeAllItems();
                    
                    if(string.length() > 0) {
                        if(AutoCompleteJComboBox.this.stockCodeAndSymbolDatabase != null) {
                            final String upperCaseString = string.toUpperCase();
                            
                            java.util.List<Symbol> symbols = stockCodeAndSymbolDatabase.searchStockSymbols(upperCaseString);
                            java.util.List<Code> codes = null;
                            
                            if(symbols.size() == 0 && Character.isDigit(string.charAt(0))) {
                                codes = stockCodeAndSymbolDatabase.searchStockCodes(string);
                            }
                            
                            if(codes != null) {
                                if(codes.size() > 0) {
                                    for(Code c : codes)
                                        AutoCompleteJComboBox.this.addItem(c.toString());

                                    AutoCompleteJComboBox.this.showPopup();
                                }                                
                            }
                            else {
                                if(symbols.size() > 0) {
                                    for(Symbol s : symbols)
                                        AutoCompleteJComboBox.this.addItem(s.toString());

                                    AutoCompleteJComboBox.this.showPopup();
                                }
                            }
                        }
                    }
                    
                    AutoCompleteJComboBox.this.getEditor().setItem(string);
                    
                    /* When we are in windows look n feel, the text will always be selected. We do not want that. */
                    JTextField jTextField = (JTextField)AutoCompleteJComboBox.this.getEditor().getEditorComponent();
                    jTextField.setSelectionStart(jTextField.getText().length());
                    jTextField.setSelectionEnd(jTextField.getText().length());
                    jTextField.setCaretPosition(jTextField.getText().length());
                    
                }   /* if(!e.isActionKey()) */
            }   /* public void keyReleased(KeyEvent e) */
        };
    }    
    
    public String getLastEnteredString() {
        return this.lastEnteredString;
    }
    
    private StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase;
    private KeyAdapter keyAdapter;
    private volatile String lastEnteredString = "";
    
    private static final Log log = LogFactory.getLog(AutoCompleteJComboBox.class);
}
