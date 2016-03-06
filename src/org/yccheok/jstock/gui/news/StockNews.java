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
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.*;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.news.NewsServer;
import org.yccheok.jstock.news.NewsServerFactory;


public class StockNews extends JFrame {
    
    public StockNews(StockInfo stockInfo, String title) {
        super(title);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.stockInfo = stockInfo;
        final Country country = org.yccheok.jstock.engine.Utils.toCountry(this.stockInfo.code);
        this.newsServers = NewsServerFactory.getNewsServers(country);
        
        fullSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        sceneWidth = fullSize.width / 2;
        sceneHeight = fullSize.height;

        initComponents();
    }

    private void initComponents() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // JFXPanel => Scene => SplitPane:
                //      Left  (news List)       => StackPane => ViewView / ProgressIndicator
                //      Right (HTML content)    => TabPane => Tab => StackPane => WebView / ProgressBar

                splitPane = new SplitPane();
                scene = new Scene(splitPane);

                scene.getStylesheets().add(StockNews.class.getResource("StockNews.css").toExternalForm()); 
                jfxPanel.setScene(scene);
                jfxPanel.setPreferredSize(new Dimension((int)sceneWidth, (int)sceneHeight));

                // Left component: News List
                messages_o = FXCollections.observableArrayList();
                newsListView = new ListView<>(messages_o); 
                newsListView.setId("news-listview");

                stackPane.setId("parent-stackPane");
                stackPane.setPrefWidth(sceneWidth);
                stackPane.setMaxWidth(sceneWidth);

                stackPane.getChildren().addAll(newsListView, progressIn);

                splitPane.getItems().add(stackPane);
                SplitPane.setResizableWithParent(stackPane, Boolean.FALSE);
                
                // show progress indicator when loading
                progressIn.setMaxWidth(100);
                progressIn.setMaxHeight(100);
                progressIn.setVisible(true);
                newsListView.setVisible(true);

                newsListView.setCellFactory(new Callback<ListView<FeedItem>, 
                    ListCell<FeedItem>>() {
                        @Override 
                        public ListCell<FeedItem> call(ListView<FeedItem> list) {
                            return new DisplayNewsCard();
                        }
                    }
                );
                
                // register event listener: add tab for news HTML content
                newsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() >= 1) {
                            final FeedItem msg = newsListView.getSelectionModel().getSelectedItem();
                            if (msg == null)
                                return;

                            final URL link = msg.getLink();
                            if (link == null || link.getHost() == null)
                                return;

                            if (stockNewsContent == null) {
                                // also minus divider width = 2px. Refer css: .split-pane > .split-pane-divider
                                final double rightWidth = fullSize.width - sceneWidth - 2;
                                stockNewsContent = new StockNewsContent(rightWidth, sceneHeight);

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        // resize JFrame first
                                        jfxPanel.setPreferredSize(new Dimension(fullSize.width, fullSize.height));
                                        StockNews.this.pack();

                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                // resize splitPane
                                                splitPane.setMinWidth(fullSize.width);
                                                splitPane.setPrefWidth(fullSize.width);
                                                splitPane.resize(fullSize.width, fullSize.height);

                                                splitPane.getItems().add(stockNewsContent.tabPane);
                                                //A position of 1.0 will place the divider at the right/bottom most edge of the SplitPane minus the minimum size of the node.
                                                splitPane.setDividerPositions(1.0f);
                                            }
                                        });
                                    }
                                });

                                stockNewsContent.tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                                        int i = stockNewsContent.tabPane.getSelectionModel().getSelectedIndex();
                                        final String jFrameTitle = stockNewsContent.tabsInfo.get(i).second;
                                        
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                StockNews.this.setTitle(jFrameTitle);
                                            }
                                        });
                                    }
                                });
                            }
                            stockNewsContent.addNewsTab(link, StringEscapeUtils.unescapeHtml(msg.getTitle()));
                        }
                    }
                });
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StockNews.this.pack();
                        StockNews.this.setVisible(true);
                    }
                });
            }
        });

        this.add(jfxPanel, BorderLayout.CENTER);
    }

    private class DisplayNewsCard extends ListCell<FeedItem> {
        @Override
        public void updateItem(FeedItem item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                final BorderPane newsBox = new BorderPane();
                newsBox.setMaxWidth(sceneWidth - 20);
                newsBox.getStyleClass().add("item-border-pane");

                // last cell - has padding bottom
                if(getIndex() == (getListView().getItems().size() - 1)) {
                    this.getStyleClass().add("listcell-last");
                }
                
                final String msgTitle = StringEscapeUtils.unescapeHtml(item.getTitle());
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
                    descText.getStyleClass().add("item-desc-text");
                    
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

    public void retrieveNewsInBackground () {
        if (this.newsServers == null || this.serverCnt >= this.newsServers.size())
            return;

        // Retrieve news from next available news server
        Task task = new Task<Void>() {
            @Override public Void call() {
                // load news from all available news servers, asynchrounusly
                while (serverCnt < newsServers.size()) {
                    final java.util.List<FeedItem> newMessages = newsServers.get(serverCnt++).getMessages(stockInfo);
                    if (newMessages.isEmpty())
                        continue;
                    
                    // Latest news come first.
                    Collections.sort(newMessages, new Comparator<FeedItem>() {
                        @Override
                        public int compare(FeedItem lhs, FeedItem rhs) {
                            return -lhs.getPubDate().compareTo(rhs.getPubDate());
                        }
                    });
                    
                    messages_o.addAll(newMessages);
                }
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stackPane.getChildren().remove(progressIn);
                    }
                });

                return null;
            }
        };
        new Thread(task).start();
    }
    
    public StockNewsContent stockNewsContent;

    private final StockInfo stockInfo;
    private final java.util.List<NewsServer> newsServers;
    private int serverCnt = 0;

    private final Rectangle fullSize;
    private final double sceneWidth;
    private final double sceneHeight;

    private final JFXPanel jfxPanel = new JFXPanel();
    private SplitPane splitPane;
    private Scene scene;

    private final StackPane stackPane = new StackPane();
    private final ProgressIndicator progressIn = new ProgressIndicator();
    
    private ObservableList<FeedItem> messages_o;
    private ListView<FeedItem> newsListView;

    private final Log log = LogFactory.getLog(StockNews.class);    
}
