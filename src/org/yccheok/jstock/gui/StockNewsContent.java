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

package org.yccheok.jstock.gui;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;


public class StockNewsContent extends JDialog {
    private final JFXPanel jfxPanel = new JFXPanel();
    Scene scene;
    URL link;
    
    public StockNewsContent(Frame parent, URL link) {
        super(parent, "Loading News....");
        
        this.link = link;
        initComponents();
    }
    
    private void initComponents() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView browser = new WebView();
                jfxPanel.setScene(new Scene(browser));

                WebEngine webEngine = browser.getEngine();

                webEngine.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<State>() {
                        public void changed(ObservableValue ov, State oldState, State newState) {
                            if (newState == State.SUCCEEDED) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override 
                                    public void run() {
                                        StockNewsContent.this.setTitle(webEngine.getLocation());
                                    }
                                });
                            }
                        }
                    });

                webEngine.load(link.toString());
            }
        });
        
        this.add(jfxPanel, BorderLayout.CENTER);
        this.setBounds(200, 200, 500, 500);
        this.setVisible(true);
    }
}
    