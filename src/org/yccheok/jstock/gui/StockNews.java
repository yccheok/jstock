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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
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
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.news.NewsServer;
import org.yccheok.jstock.news.NewsServerFactory;


public class StockNews extends JFrame {
    StockInfo stockInfo;
    java.util.List<NewsServer> newsServers;
    int loadedServer = 0;

    private StockNewsContent newsTab;
    private final JFXPanel jfxPanel = new JFXPanel();
    Scene scene;
    VBox vbox;
    final int width = 700;
    final int height = 700;
    final int sceneWidth = width - 50;
    final int sceneHeight = height - 50;

    java.util.List<FeedItem> messages = new ArrayList<>();
    ObservableList<FeedItem> messages_o;
    ListView<FeedItem> newsListView;

    public StockNews(StockInfo stockInfo, String title) {
        super(title);
        this.stockInfo = stockInfo;
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

                messages_o = FXCollections.observableArrayList ();
                
                //messages_o.addListener(new ListChangeListener() {
                //    @Override
                //    public void onChanged(ListChangeListener.Change change) {
                //    }
                //});

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
                            
                            if (msg != null) {
                                URL link = msg.getLink();

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (newsTab == null) {
                                            newsTab = new StockNewsContent();
                                            newsTab.setVisible(true);
                                        }
                                        newsTab.addNewsTab(link);
                                        newsTab.toFront();
                                    }
                                });
                            }
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
                if (newsTab != null) {
                    newsTab.dispose();
                }
            }
        });
    }

    void retrieveNewsInBackground () {
        SwingWorker swingWorker = new SwingWorker<java.util.List<FeedItem>, Void>() {

            @Override
            protected java.util.List<FeedItem> doInBackground() throws Exception {
                Country country = org.yccheok.jstock.engine.Utils.toCountry(stockInfo.code);
                newsServers = NewsServerFactory.getNewsServers(country);

                // Only retrieve news from 1st news server, during initial load
                java.util.List<FeedItem> messages = newsServers.get(0).getMessages(stockInfo);
                return messages;
            }

            @Override
            public void done() {
                try {
                    StockNews.this.messages = this.get();
                    StockNews.this.updateList(StockNews.this.messages);
                    StockNews.this.loadedServer = 1;
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(JStock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        swingWorker.execute();
    }

    void retrieveMoreNews () {
        if (loadedServer >= newsServers.size()) {
            return;
        }
 
        SwingWorker swingWorker = new SwingWorker<java.util.List<FeedItem>, Void>() {

            @Override
            protected java.util.List<FeedItem> doInBackground() throws Exception {
                // retrieve more news from next news Server
                java.util.List<FeedItem> moreMessages = newsServers.get(loadedServer++).getMessages(stockInfo);
                return moreMessages;
            }

            @Override
            public void done() {
                try {
                    java.util.List<FeedItem> moreMessages = this.get();
                    StockNews.this.updateList(moreMessages);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(JStock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        swingWorker.execute();
    }
    
    void updateList (java.util.List<FeedItem> moreMessages) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < moreMessages.size(); i++) {
                    messages_o.add(moreMessages.get(i));
                }
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
