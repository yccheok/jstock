/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2016 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.net.URL;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.event.EventHandler;
import javafx.beans.value.ObservableValue; 
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.yccheok.jstock.internationalization.GUIBundle;


public class StockNewsContent {

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

        final Tab tab = new Tab();

        tab.setTooltip(new Tooltip(title));
        
        final ProgressIndicator progressIn = new ProgressIndicator();
        progressIn.setMaxSize(15, 15);
        
        tab.setGraphic(progressIn);
        
        final WebView webView = new WebView();
                
        Image image = new Image(getClass().getResource("/images/32x32/browser.png").toString());
        ImageView imageView = new ImageView(image);
        Button button = new Button(GUIBundle.getString("SimpleSwingBrowser_OpenInBrowser"), imageView);
        button.setOnAction(event -> {
            org.yccheok.jstock.gui.Utils.launchWebBrowser(link);
        });
        
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(8, 8, 8, 8));
        hbox.getChildren().addAll(button);
          
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hbox);
        borderPane.setCenter(webView);
        
        tab.setContent(borderPane);
        
        tab.setOnCloseRequest(new EventHandler<javafx.event.Event>() {
            public void handle(javafx.event.Event e) {
                tabsInfo.remove(tabPane.getSelectionModel().getSelectedIndex());
                webView.getEngine().load(null);
            }
        });

        tabPane.getTabs().add(tab);
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(link.toString());

        webEngine.getLoadWorker().stateProperty().addListener(
            new javafx.beans.value.ChangeListener<Worker.State>() {
                public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                    if (newState == SUCCEEDED || newState == FAILED) {
                        if (progressIn.isVisible() == true) {
                            progressIn.setVisible(false);
                        }
                        
                        if (tab.getGraphic() != null) {
                            tab.setGraphic(null);
                        }
                    }
                }
            });

        // Tab title: display progress Indicator + first 3 words of news title
        final String[] result = title.split(" ", 4);
        String shortTitle = "";
        for (int i=0; i<3; i++) {
            shortTitle = shortTitle + " " + result[i];
        }
        shortTitle = shortTitle + "....";
        
        tab.setText(shortTitle);
        
        // remove ProgressIndicator (loading icon on tab) after 15s
        final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(15), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (progressIn.isVisible() == true) {
                    progressIn.setVisible(false);
                }

                if (tab.getGraphic() != null) {
                    tab.setGraphic(null);
                }
            }
        }));
        timeline.setCycleCount(1);
        timeline.play();
        
        tabPane.getSelectionModel().select(tab);
    }

    public final TabPane tabPane = new TabPane();
    public final ArrayList<Pair<URL, String>> tabsInfo = new ArrayList();
}
