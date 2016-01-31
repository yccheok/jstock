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

import it.sauronsoftware.feed4j.bean.FeedItem;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;


public class StockNews extends JFrame {
    private StockNewsContent newsTab = new StockNewsContent();
    private final JFXPanel jfxPanel = new JFXPanel();
    Scene scene;
    VBox vbox;
    final int width = 700;
    final int height = 700;
    final int sceneWidth = width - 50;
    final int sceneHeight = height - 50;
    
    java.util.List<FeedItem> messages;
    ListView<FeedItem> newsListView;
    ObservableList<FeedItem> messages_o;
    
    public StockNews(java.util.List<FeedItem> messages, String title) {
        super("News: " + title);
        this.messages = messages;
        initComponents();
    }
    
    private void initComponents() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox = new VBox();
                scene = new Scene(vbox, sceneWidth, sceneHeight);
                scene.getStylesheets().add(StockNews.class.getResource("StockNews.css").toExternalForm()); 
                jfxPanel.setScene(scene);

                messages_o = FXCollections.observableArrayList (messages);
                newsListView = new ListView<>(messages_o); 
                newsListView.setId("news-listview"); 
                
                newsListView.setCellFactory(new Callback<ListView<FeedItem>, 
                    ListCell<FeedItem>>() {
                        @Override 
                        public ListCell<FeedItem> call(ListView<FeedItem> list) {
                            return new DisplaySingleNews();
                        }
                    }
                );

                newsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            FeedItem msg = newsListView.getSelectionModel().getSelectedItem();
                            URL link = msg.getLink();

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    newsTab.addNewsTab(link);
                                    newsTab.setVisible(true);
                                }
                            });
                        }
                    }
                });
                
                vbox.setId("parent-vbox"); 
                VBox.setVgrow(newsListView, Priority.ALWAYS);
                vbox.getChildren().addAll(newsListView);
            }
        });
        
        this.add(jfxPanel, BorderLayout.CENTER);
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - width)/2, (screenSize.height - height)/2, width, height);
        this.setVisible(true);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                newsTab.dispose();
            }
        });
    }

    class DisplaySingleNews extends ListCell<FeedItem> {
        BorderPane newsBox;
        VBox descVBox;
        
        TextFlow titleTextFlow;
        Text firstText;
        Text secondText;
        
        Text titleText;
        Text descText;
        Label pubDate;

        public DisplaySingleNews() {
            super();
        }

        @Override
        public void updateItem(FeedItem item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                newsBox = new BorderPane();
                newsBox.setMaxWidth(sceneWidth - 20);
                newsBox.getStyleClass().add("item-border-pane");
                
                String msgTitle = item.getTitle();
                firstText = new Text(msgTitle.substring(0, 1));
                secondText = new Text(msgTitle.substring(1));
                
                firstText.getStyleClass().add("item-title-text-1"); 
                secondText.getStyleClass().add("item-title-text-2");
                
                titleTextFlow = new TextFlow(firstText, secondText);
                titleTextFlow.getStyleClass().add("item-title-textflow");
                titleTextFlow.setMaxWidth(sceneWidth - 60);
                
                newsBox.setTop(titleTextFlow);
                
                if (item.getDescriptionAsText() != null) {
                    descText = new Text(item.getDescriptionAsText());
                    descText.setWrappingWidth(sceneWidth - 60);


                    descVBox = new VBox();
                    descVBox.getChildren().addAll(descText);
                    descVBox.getStyleClass().add("item-desc-vbox");
                    
                    newsBox.setCenter(descVBox);
                    BorderPane.setAlignment(descText, Pos.CENTER_LEFT);
                }
                
                String pubDateDiff = org.yccheok.jstock.news.Utils.getPubDateDiff(item.getPubDate());
                pubDate = new Label(pubDateDiff);
                pubDate.getStyleClass().add("item-date-label");
                newsBox.setBottom(pubDate);
                
                BorderPane.setAlignment(pubDate, Pos.BOTTOM_RIGHT);
                
                setGraphic(newsBox);
            }
        }
    }
}
