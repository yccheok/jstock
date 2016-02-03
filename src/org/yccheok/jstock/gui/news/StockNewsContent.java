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

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;


public class StockNewsContent extends JFrame {
    public StockNewsContent() {
        super("Stock News");
        initComponents();
    }

    private void initComponents() {
        // JFrame => mainJPanel => tabbedPane
        this.add(mainJPanel, BorderLayout.CENTER);
        mainJPanel.add(tabbedPane);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - width)/2, (screenSize.height - height)/2, width, height);
    }

    public void addNewsTab (URL link, String title) {
        if (!links.isEmpty()) {
            // URL already open in tab, just make tab active
            for (int i = 0; i < links.size(); i++) {
                if (link.equals(links.get(i))) {
                    tabbedPane.setSelectedIndex(i);
                    return;
                }
            }
        }
        links.add(link);

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

        tabbedPane.addTab(title, panel);
        tabbedPane.setSelectedIndex(links.size() - 1);
    }

    private final JPanel mainJPanel = new JPanel(new GridLayout(1, 1));
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final ArrayList<URL> links = new ArrayList();
    private static final int width = 800;
    private static final int height = 800;
}
    