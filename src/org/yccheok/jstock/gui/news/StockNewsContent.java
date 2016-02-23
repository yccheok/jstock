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

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.SingleSelectionModel;


public class StockNewsContent {

    public StockNewsContent(int width, int height) {
        this.width = width;
        this.height = height;
        jfxPanel = new JFXPanel();

        tabPane = new TabPane();
        tabPane.setPrefWidth(StockNewsContent.this.width);
        tabPane.setPrefWidth(StockNewsContent.this.height);
        //tabPane.setTabMaxWidth(StockNewsContent.this.width);
        jfxPanel.setScene(new Scene(tabPane));
    }
    
    public void addNewsTab (URL link, String title) {
        if (!tabsInfo.isEmpty()) {
            // URL already open in tab, just select tab
            for (int i = 0; i < tabsInfo.size(); i++) {
                if (link.equals(tabsInfo.get(i).first)) {
                    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                    selectionModel.select(i);
                    return;
                }
            }
        }
        tabsInfo.add(new Pair(link, title));

        // Each tab content: JPanel => JFXPanel => Scene => WebView

        final Tab tab = new Tab();
        final StackPane stackPane = new StackPane();
        final ProgressBar progress = new ProgressBar();
        
        final WebView webView = new WebView();
        stackPane.getChildren().addAll(webView, progress);

        tab.setContent(stackPane);
        tabPane.getTabs().add(tab);
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(link.toString());

        // update progress bar using binding
        progress.progressProperty().bind(webEngine.getLoadWorker().progressProperty());

        webEngine.getLoadWorker().stateProperty().addListener(
            new javafx.beans.value.ChangeListener<Worker.State>() {
                public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                    if (newState == Worker.State.SUCCEEDED) {
                        progress.setVisible(false);
                    } else if (newState == FAILED) {
                        // handle failed
                    }
                }
            });
                
        // Tab title: display first 2 words of news title
        final String[] result = title.split(" ", 3);
        final String shortTitle = String.join(" ", result[0], result[1]) + "...";
        tab.setText(shortTitle);
        
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    public final JFXPanel jfxPanel;
    private TabPane tabPane;

    private final ArrayList<Pair<URL, String>> tabsInfo = new ArrayList();
    private final int width;
    private final int height;
}