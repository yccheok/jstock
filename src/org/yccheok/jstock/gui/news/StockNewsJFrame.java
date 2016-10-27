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

import it.sauronsoftware.feed4j.bean.FeedItem;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javax.swing.*;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
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


public class StockNewsJFrame extends JFrame implements WindowListener {
    
    public StockNewsJFrame(java.awt.Frame parent, StockInfo stockInfo, String title) {
        super(title);

        this.parent = parent;
        this.parent.addWindowListener(this);

        this.setIconImage(parent.getIconImage());
        
        this.stockInfo = stockInfo;
        final Country country = org.yccheok.jstock.engine.Utils.toCountry(this.stockInfo.code);
        this.newsServers = NewsServerFactory.getNewsServers(country);
        
        fullSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        sceneWidth = fullSize.width / 2;
        sceneHeight = fullSize.height;
        this.setSize((int)sceneWidth, (int)sceneHeight);

        initComponents();
    }

    public void windowActivated(WindowEvent e) {
       // this only works becuase AutoRequestFocus is false, so this stays on
       // top, but looses focus 
       this.toFront();
    }
    public void windowDeactivated(WindowEvent e) {
       // JFrame is set to AlwaysOnTop = true at design time. So this is only
       // useful on first deactivation. After that it is meaningless. But without
       // initial AlwaysOnTop, it would not receive focus because autoRequestFocus
       // is false.
       this.setAlwaysOnTop(false);
    }
    @Override
    public void windowIconified(WindowEvent e) {
       // when main app goes away, this child window should also go away 
       this.setVisible(false);
    }
    @Override
    public void windowDeiconified(WindowEvent e) {
       // when main app comes back, this child window should also come back
       this.setVisible(true);
    }
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e) {}
    @Override
    public void windowOpened(WindowEvent e) {}

    private void formWindowClosing(java.awt.event.WindowEvent evt) {  
        NewsTask task = StockNewsJFrame.this.newsTask;
        if (task != null) {
            task.cancel(true);
        }
                
        // To avoid memory leak.
        parent.removeWindowListener(this);
        this.dispose();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                NewsTask task = StockNewsJFrame.this.newsTask;
                if (task != null) {
                    task.cancel(true);
                }
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
            }
        });
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // JFXPanel => Scene => SplitPane:
                //      Left  (news List)       => StackPane => ListView / ProgressIndicator
                //      Right (HTML content)    => TabPane => Tab => StackPane => WebView / ProgressBar

                splitPane = new SplitPane();
                scene = new Scene(splitPane);

                scene.getStylesheets().add(StockNewsJFrame.class.getResource("StockNewsJFrame.css").toExternalForm()); 
                jfxPanel.setScene(scene);

                // Left component: News List
                messages_o = FXCollections.observableArrayList();
                newsListView = new ListView<>(messages_o); 
                newsListView.setId("news-listview");

                stackPane.setId("parent-stackPane");
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
                            if (msg == null) {
                                return;
                            }
                            
                            final URL link = msg.getLink();
                            if (link == null || link.getHost() == null) {
                                return;
                            }

                            if (stockNewsContent == null) {
                                stackPane.setPrefWidth(stackPane.getWidth());
                                stackPane.setMaxWidth(stackPane.getWidth());
                                stackPane.setMinWidth(stackPane.getWidth() / 5);
                                    
                                try {
                                    SwingUtilities.invokeAndWait(new Runnable() {
                                        @Override
                                        public void run() {
                                            // resize JFrame first
                                            StockNewsJFrame.this.setSize(fullSize.width, fullSize.height);

                                            java.awt.Insets in = StockNewsJFrame.this.getInsets();
                                            jfxPanel.setSize(StockNewsJFrame.this.getWidth() - in.left - in.right, jfxPanel.getHeight());
                                            
                                            java.awt.Insets in2 = jfxPanel.getInsets();
                                            
                                            // calculate width & height, but not resize in AWT event dispatching thread
                                            // javafx.scene.control.SplitPane should only be accessed from JavaFX Application Thread
                                            splitPaneWidth = jfxPanel.getWidth() - in2.left - in2.right;
                                            splitPaneHeight = jfxPanel.getHeight() - in2.top - in2.bottom;
                                        }
                                    });
                                } catch (InterruptedException | InvocationTargetException ex) {
                                    log.error(null, ex);
                                }
                                
                                // resize to full screen size of jfxPanel
                                splitPane.resize(splitPaneWidth, splitPaneHeight);
                                
                                stockNewsContent = new StockNewsContent();
                                splitPane.getItems().add(stockNewsContent.tabPane);
                                splitPane.setDividerPositions(0.5f);

                                stockNewsContent.tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                                        int i = stockNewsContent.tabPane.getSelectionModel().getSelectedIndex();
                                        if (i < 0) {
                                            return;
                                        }

                                        jFrameTitle = stockNewsContent.tabsInfo.get(i).second;

                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                StockNewsJFrame.this.setTitle(jFrameTitle);
                                            }
                                        });
                                    }
                                });
                            }
                            stockNewsContent.addNewsTab(link, StringEscapeUtils.unescapeHtml(msg.getTitle()));
                        }
                    }
                });
                
                retrieveNewsInBackground();
            }
        });

        this.add(jfxPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    private class DisplayNewsCard extends ListCell<FeedItem> {
        @Override
        public void updateItem(FeedItem item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                final BorderPane newsBox = new BorderPane();
                newsBox.setMaxWidth(sceneWidth - 20);
                newsBox.getStyleClass().add("item-border-pane");

                // News Title
                final String msgTitle = StringEscapeUtils.unescapeHtml(item.getTitle());
                final Text firstText = new Text(msgTitle.substring(0, 1));
                final Text secondText = new Text(msgTitle.substring(1));

                firstText.getStyleClass().add("item-title-text-1"); 
                secondText.getStyleClass().add("item-title-text-2");

                final TextFlow titleTextFlow = new TextFlow(firstText, secondText);
                titleTextFlow.setMaxWidth(sceneWidth - 60);

                newsBox.setTop(titleTextFlow);
                
                // News description
                final Text descText;
                if (item.getDescriptionAsText() != null) {
                    descText = new Text(item.getDescriptionAsText());
                    descText.setWrappingWidth(sceneWidth - 60);
                    descText.getStyleClass().add("item-desc-text");
                    
                    BorderPane.setMargin(descText, new javafx.geometry.Insets(10, 0, 0, 0));
                    BorderPane.setAlignment(descText, Pos.CENTER_LEFT);

                    newsBox.setCenter(descText);
                }

                // News published date
                final String pubDateDiff = toHumanReadableDate(item.getPubDate());
                final Label pubDate = new Label(pubDateDiff);
                pubDate.getStyleClass().add("item-date-label");
                newsBox.setBottom(pubDate);

                BorderPane.setAlignment(pubDate, Pos.BOTTOM_RIGHT);

                setGraphic(newsBox);
            }
        }
    }

    private class NewsTask extends Task< java.util.List<FeedItem> > {
        private final java.util.List<NewsServer> newsServers;

        public NewsTask (java.util.List<NewsServer> servers) {
            this.newsServers = servers;
        }

        @Override protected java.util.List<FeedItem> call() {
            java.util.List<FeedItem> allMessages = new java.util.ArrayList<FeedItem>();
            int serverCnt = 0;

            // load news from all available news servers, asynchrounusly
            while (serverCnt < this.newsServers.size()) {
                final java.util.List<FeedItem> newMessages = this.newsServers.get(serverCnt++).getMessages(stockInfo);

                if (isCancelled()) {
                    return null;
                }
                if (newMessages.isEmpty()) {
                    continue;
                }
                allMessages.addAll(newMessages);
            }

            if (isCancelled()) {
                return null;
            }

            // sort news in DESC order
            Collections.sort(allMessages, new Comparator<FeedItem>() {
                @Override
                public int compare(FeedItem lhs, FeedItem rhs) {
                    return -lhs.getPubDate().compareTo(rhs.getPubDate());
                }
            });

            return allMessages;
        }
    }
    
    public void retrieveNewsInBackground () {
        if (newsServers == null) {
            return;
        }
        if (newsTask != null) {
            throw new java.lang.RuntimeException("Being called more than once");
        }

        newsTask = new NewsTask(newsServers);
        
        // on newsTask successfully executed
        newsTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                java.util.List<FeedItem> allMessages = newsTask.getValue();

                messages_o.addAll(allMessages);
                stackPane.getChildren().remove(progressIn);
            }
        });
        
        new Thread(newsTask).start();
    }

    private boolean isSameDay(Date date0, Date date1) {
        return date0.getDate() == date1.getDate() && date0.getMonth() == date1.getMonth() && date0.getYear() == date1.getYear(); 
    }

    private boolean isSameYear(Date date0, Date date1) {
        return date0.getYear() == date1.getYear(); 
    }

    private String toHumanReadableDate(Date date) {
        Date today = new Date();
        if (isSameDay(today, date)) {
            // Check the differences in timestamp.
            long timestampDiff = today.getTime() - date.getTime();
            if (timestampDiff <= 0) {
                return org.yccheok.jstock.internationalization.GUIBundle.getString("StockNewsJFrame_Now");
            } else if (timestampDiff < 60 * 60 * 1000) {
                int minutesAgo = (int)(timestampDiff / 60.0 / 1000.0);
                if (minutesAgo > 1) {
                    return MessageFormat.format(org.yccheok.jstock.internationalization.GUIBundle.getString("StockNewsJFrame_MinutesAgo_template"), minutesAgo);
                } else {
                    return MessageFormat.format(org.yccheok.jstock.internationalization.GUIBundle.getString("StockNewsJFrame_MinuteAgo_template"), minutesAgo);
                }
            } else if (timestampDiff < 24 * 60 * 60 * 1000) {
                int hoursAgo = (int)(timestampDiff / 60.0 / 60.0 / 1000.0);
                if (hoursAgo > 1) {
                    return MessageFormat.format(org.yccheok.jstock.internationalization.GUIBundle.getString("StockNewsJFrame_HoursAgo_template"), hoursAgo);
                } else {
                    return MessageFormat.format(org.yccheok.jstock.internationalization.GUIBundle.getString("StockNewsJFrame_HourAgo_template"), hoursAgo);
                }                
            }
        } else if (isSameYear(today, date)) {
            return formatter.get().format(date);
        }
        return formatterWithYear.get().format(date);
    }


    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <SimpleDateFormat> formatterWithYear = new ThreadLocal <SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MMM dd, yyyy");
        }
    };

    private static final ThreadLocal <SimpleDateFormat> formatter = new ThreadLocal <SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MMM dd");
        }
    };
    
    public StockNewsContent stockNewsContent;

    private final StockInfo stockInfo;
    private final java.util.List<NewsServer> newsServers;

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

    private NewsTask newsTask;
    
    /* To avoid memory leak */
    private java.awt.Frame parent;

    private double splitPaneWidth;
    private double splitPaneHeight;
    
    private String jFrameTitle;
    
    private final Log log = LogFactory.getLog(StockNewsJFrame.class);    
}
