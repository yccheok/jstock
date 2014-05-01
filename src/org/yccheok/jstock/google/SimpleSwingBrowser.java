/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.*;
import org.yccheok.jstock.gui.MainFrame;
  
public class SimpleSwingBrowser extends JDialog {
 
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JPanel panel = new JPanel(new BorderLayout());
 
    public SimpleSwingBrowser() {
        super(MainFrame.getInstance(), JDialog.ModalityType.APPLICATION_MODAL);
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
 
                engine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {

                    public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                        if (engine.getLoadWorker().getState() == FAILED) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override public void run() {
                                    JOptionPane.showMessageDialog(
                                        panel,
                                        (value != null) ?
                                        engine.getLocation() + "\n" + value.getMessage() :
                                        engine.getLocation() + "\nUnexpected error.",
                                        "Loading error...",
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            });
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
