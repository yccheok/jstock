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

package org.yccheok.jstock.google;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.*;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.internationalization.MessagesBundle;
  
public class SimpleSwingBrowser extends JDialog {
 
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private String loadedURL = null;
    private final JPanel panel = new JPanel(new BorderLayout());
 
    public SimpleSwingBrowser() {
        super(JStock.instance(), JDialog.ModalityType.APPLICATION_MODAL);
        initComponents();
    }

    
    private void initComponents() {
        createScene();
  
        // http://stackoverflow.com/questions/11269632/javafx-hmtleditor-doesnt-react-on-return-key
        jfxPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 10) {
                    e.setKeyChar((char) 13);
                    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
                }
            }
        });

        panel.add(jfxPanel, BorderLayout.CENTER);
        
        getContentPane().add(panel);
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-460)/2, (screenSize.height-680)/2, 460, 680);
    }
    
    private void createScene() {
 
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
 
                // Clear the session/cache/cookie in the JavaFX WebView
                // http://stackoverflow.com/questions/23409138/clear-the-session-cache-cookie-in-the-javafx-webview
                java.net.CookieHandler.setDefault(new java.net.CookieManager());
                
                final WebView view = new WebView();
                engine = view.getEngine();
 
                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
                                SimpleSwingBrowser.this.setTitle(newValue);
                            }
                        });
                    }
                });
 
                engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

                    @Override
                    public void changed(ObservableValue<? extends State> observable, State oldValue, final State newValue) {
                        if (newValue == FAILED) {
                            final int result = JOptionPane.showConfirmDialog(
                                panel,
                                MessagesBundle.getString("error_message_unable_connect_to_internet"),
                                MessagesBundle.getString("error_title_unable_connect_to_internet"),
                                JOptionPane.YES_NO_OPTION);
                            
                            if (result == JOptionPane.YES_OPTION) {
                                if (loadedURL != null) {
                                    engine.load(loadedURL);
                                }
                            }
                        }
                    }
                });
                
                // http://stackoverflow.com/questions/11206942/how-to-hide-scrollbars-in-the-javafx-webview
                // hide webview scrollbars whenever they appear.
                view.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
                    @Override 
                    public void onChanged(Change<? extends Node> change) {
                        Set<Node> deadSeaScrolls = view.lookupAll(".scroll-bar");
                        for (Node scroll : deadSeaScrolls) {
                            scroll.setVisible(false);
                        }
                    }
                });
                
                jfxPanel.setScene(new Scene(view));
            }
        });
    }
 
    public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                String tmp = toURL(url);
                
                if (tmp == null) {
                    tmp = toURL("http://" + url);
                }
 
                loadedURL = tmp;
                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }
}
