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

package org.yccheok.jstock.gui.trading;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

/**
 *
 * @author  Owner
 */
public class TradingJPanel extends javax.swing.JPanel {
    
    public TradingJPanel() {
        initComponents();
    }
    
    private void initComponents() {
        // Javafx login form example: 
        // http://docs.oracle.com/javafx/2/get_started/form.htm
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Tab Pane
                tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);

                // create Sign In Tab & license Tab
                SignIn.createTab(tabPane);
                
                scene = new Scene(tabPane);
                scene.getStylesheets().add(TradingJPanel.class.getResource("trading.css").toExternalForm());
                jfxPanel.setScene(scene);
                jfxPanel.setPreferredSize(new Dimension(500, 500));
            }
        });

        jScrollPane.getViewport().add(jfxPanel);        
        jScrollPane.setPreferredSize(new Dimension(500, 500));

        this.setLayout(new java.awt.GridLayout(0, 1, 5, 5));
        this.add(this.jScrollPane);
        
        this.setVisible(true);
    }

    private final JScrollPane jScrollPane = new javax.swing.JScrollPane();
    private final JFXPanel jfxPanel = new JFXPanel();
    private Scene scene;
    private TabPane tabPane;
}
