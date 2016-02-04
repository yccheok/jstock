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

package org.yccheok.jstock.gui.news;

import org.yccheok.jstock.engine.Pair;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import static javafx.concurrent.Worker.State.FAILED;


public class StockNewsContent extends JFrame {
    public StockNewsContent() {
        super("Stock News");
        initComponents();
    }

    private void initComponents() {
        // JFrame => mainJPanel => tabbedPane
        this.add(mainJPanel, BorderLayout.CENTER);
        mainJPanel.add(tabbedPane);

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int index = tabbedPane.getSelectedIndex();
                final String frameTitle = tabsInfo.get(index).second;
                StockNewsContent.this.setTitle(frameTitle);
            }
        });
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - width)/2, (screenSize.height - height)/2, width, height);
    }

    public void addNewsTab (URL link, String title) {
        if (!tabsInfo.isEmpty()) {
            // URL already open in tab, just make tab active
            for (int i = 0; i < tabsInfo.size(); i++) {
                if (link.equals(tabsInfo.get(i).first)) {
                    tabbedPane.setSelectedIndex(i);
                    return;
                }
            }
        }
        tabsInfo.add(new Pair(link, title));

        // Each tab content: JPanel => JFXPanel => Scene => WebView
        JComponent panel = new JPanel(false);
        JFXPanel jfxPanel = new JFXPanel();
        panel.add(jfxPanel);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView browser = new WebView();
                jfxPanel.setScene(new Scene(browser));
                WebEngine webEngine = browser.getEngine();

                webEngine.getLoadWorker().stateProperty().addListener(
                    new javafx.beans.value.ChangeListener<State>() {
                        public void changed(ObservableValue ov, State oldState, State newState) {
                            if (newState == State.SUCCEEDED) {
                                //SwingUtilities.invokeLater(new Runnable() {
                                //    @Override 
                                //    public void run() {
                                //        StockNewsContent.this.setTitle(webEngine.getLocation());
                                //    }
                                //});
                            } else if (newState == FAILED) {
                                // handle failed
                            }
                        }
                    });

                webEngine.load(link.toString());
            }
        });

        // Tab title: display first 2 words of news title
        final String[] result = title.split(" ", 3);
        final String shortTitle = String.join(" ", result[0], result[1]) + "...";

        tabbedPane.addTab(shortTitle, panel);
        tabbedPane.setSelectedIndex(tabsInfo.size() - 1);
    }

    //pair.first = URL
    //pair.second = title
    
    private final JPanel mainJPanel = new JPanel(new GridLayout(1, 1));
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final ArrayList<Pair<URL, String>> tabsInfo = new ArrayList();
    private static final int width = 800;
    private static final int height = 800;
}
    