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

import it.sauronsoftware.feed4j.bean.FeedItem;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.news.NewsServer;
import org.yccheok.jstock.news.NewsServerFactory;


public class StockNews extends JFrame {
    public StockNews(StockInfo stockInfo, String title) {
        super(title);
        this.stockInfo = stockInfo;
        initComponents();
        
        final Country country = org.yccheok.jstock.engine.Utils.toCountry(stockInfo.code);
        this.newsServers = NewsServerFactory.getNewsServers(country);
    }

    private void initComponents() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox = new VBox();
                scene = new Scene(vbox, StockNews.sceneWidth, StockNews.sceneHeight);
                scene.getStylesheets().add(StockNews.class.getResource("StockNews.css").toExternalForm()); 
                jfxPanel.setScene(scene);

                messages_o = FXCollections.observableArrayList();
                
                //messages_o.addListener(new ListChangeListener() {
                //    @Override
                //    public void onChanged(ListChangeListener.Change change) {
                //    }
                //});

                newsListView = new ListView<>(messages_o); 
                newsListView.setId("news-listview");
                vbox.setId("parent-vbox"); 
                VBox.setVgrow(newsListView, Priority.ALWAYS);
                vbox.getChildren().addAll(newsListView);

                newsListView.setCellFactory(new Callback<ListView<FeedItem>, 
                    ListCell<FeedItem>>() {
                        @Override 
                        public ListCell<FeedItem> call(ListView<FeedItem> list) {
                            return new DisplayNewsCard();
                        }
                    }
                );

                newsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            final FeedItem msg = newsListView.getSelectionModel().getSelectedItem();
                            if (msg == null)
                                return;

                            final URL link = msg.getLink();
                            if (link == null)
                                return;

                            // Tab title: display first 2 words of news title
                            final String[] result = msg.getTitle().split(" ", 3);
                            final String title = String.join(" ", result[0], result[1]) + "...";

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (newsTab == null) {
                                        newsTab = new StockNewsContent();
                                        newsTab.setVisible(true);
                                    }
                                    newsTab.addNewsTab(link, title);
                                    newsTab.toFront();
                                }
                            });
                        }
                    }
                });
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

    public void retrieveNewsInBackground () {
        if (this.newsServers == null || this.loadedServerCnt >= this.newsServers.size())
            return;

        SwingWorker swingWorker = new SwingWorker<java.util.List<FeedItem>, Void>() {

            @Override
            protected java.util.List<FeedItem> doInBackground() throws Exception {
                // Retrieve news from next available news server
                final java.util.List<FeedItem> newMessages = newsServers.get(loadedServerCnt).getMessages(stockInfo);
                return newMessages;
            }

            @Override
            public void done() {
                try {
                    final java.util.List<FeedItem> newMessages = this.get();
                    messages.addAll(newMessages);
                    loadedServerCnt++;
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for (FeedItem message : messages) {
                                messages_o.add(message);
                            }
                        }
                    });
                } catch (InterruptedException | ExecutionException ex) {
                    log.error(null, ex);
                }
            }
        };
        swingWorker.execute();
    }

    static class DisplayNewsCard extends ListCell<FeedItem> {
        @Override
        public void updateItem(FeedItem item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                final BorderPane newsBox = new BorderPane();
                newsBox.setMaxWidth(sceneWidth - 20);
                newsBox.getStyleClass().add("item-border-pane");

                final String msgTitle = item.getTitle();
                final Text firstText = new Text(msgTitle.substring(0, 1));
                final Text secondText = new Text(msgTitle.substring(1));

                firstText.getStyleClass().add("item-title-text-1"); 
                secondText.getStyleClass().add("item-title-text-2");
                
                final TextFlow titleTextFlow = new TextFlow(firstText, secondText);
                titleTextFlow.getStyleClass().add("item-title-textflow");
                titleTextFlow.setMaxWidth(sceneWidth - 60);
                
                newsBox.setTop(titleTextFlow);
                
                final VBox descVBox;
                final Text descText;
                if (item.getDescriptionAsText() != null) {
                    descText = new Text(item.getDescriptionAsText());
                    descText.setWrappingWidth(sceneWidth - 60);

                    descVBox = new VBox();
                    descVBox.getChildren().addAll(descText);
                    descVBox.getStyleClass().add("item-desc-vbox");
                    
                    newsBox.setCenter(descVBox);
                    BorderPane.setAlignment(descText, Pos.CENTER_LEFT);
                }
                
                final String pubDateDiff = org.yccheok.jstock.news.Utils.getPubDateDiff(item.getPubDate());
                final Label pubDate = new Label(pubDateDiff);
                pubDate.getStyleClass().add("item-date-label");
                newsBox.setBottom(pubDate);
                
                BorderPane.setAlignment(pubDate, Pos.BOTTOM_RIGHT);
                
                setGraphic(newsBox);
            }
        }
    }
    
    private final StockInfo stockInfo;
    private final java.util.List<NewsServer> newsServers;
    private int loadedServerCnt = 0;

    private StockNewsContent newsTab;
    private final JFXPanel jfxPanel = new JFXPanel();
    private Scene scene;
    private VBox vbox;
    private static final int width = 700;
    private static final int height = 700;
    private static final int sceneWidth = width;
    private static final int sceneHeight = height;

    private java.util.List<FeedItem> messages = new ArrayList<>();
    private ObservableList<FeedItem> messages_o;
    private ListView<FeedItem> newsListView;
    
    private static final Log log = LogFactory.getLog(StockNews.class);
}
