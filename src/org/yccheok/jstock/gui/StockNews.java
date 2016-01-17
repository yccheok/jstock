/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui;

import java.util.List;
import java.util.Iterator;
import java.awt.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.swing.*;
import javafx.geometry.Insets;

import it.sauronsoftware.feed4j.bean.FeedItem;

import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.news.NewsServer;
import org.yccheok.jstock.news.NewsServerFactory;


public class StockNews extends JFrame {
    private final JFXPanel jfxPanel = new JFXPanel();
    
    Scene scene;
    Text hello;
    Label label;
    VBox vbox;
    ListView<String> myList;
    StockInfo stockInfo;
    Country country;
    
    public StockNews(String title, Stock stock) {
        super(title);
        this.stockInfo = StockInfo.newInstance(stock.code, stock.symbol);
        this.country = org.yccheok.jstock.engine.Utils.toCountry(stock.code);
                
        initComponents();
    }
    
    private void initComponents() {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                vbox = new VBox();
                
                scene = new Scene(vbox, 200, 200);
                scene.setFill(Color.BLACK);
                //scene.getStylesheets().add("stockNews.css");
                
                label = new Label("Stock News");
                hello = new Text(stockInfo.symbol.toString() + " (" + stockInfo.code.toString() + ")");
                hello.setFill(Color.CHOCOLATE);
                
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(5.0);
                dropShadow.setOffsetX(3.0);
                dropShadow.setOffsetY(3.0);
                dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
                hello.setEffect(dropShadow);
                
                jfxPanel.setScene(scene);
                
                myList = new ListView<String>();
                ObservableList<String> news = FXCollections.observableArrayList ();
                
                final List<NewsServer> newsServers = NewsServerFactory.getNewsServers(country);
                final NewsServer server = newsServers.get(0);
                List<FeedItem> messages = server.getMessages(stockInfo);
                
                Iterator<FeedItem> messagesIterator = messages.iterator();
                while (messagesIterator.hasNext()) {
                    FeedItem msg = messagesIterator.next();

                    String datetime = "";
                    if (msg.getPubDate() != null) {
                        datetime = msg.getPubDate().toString();
                    }
                    
                    news.add(msg.getTitle() + " [Date: " + datetime + "]");
                    news.add(msg.getDescriptionAsText());
		}
                
                myList.setItems(news);
                myList.setPrefWidth(50);
                myList.setPrefHeight(50);
                
                vbox.setPadding(new Insets(10));
                vbox.setSpacing(8);
                
                vbox.setStyle("-fx-border-color: #2e8b57; -fx-border-width: 2px;");
                vbox.setAlignment(Pos.CENTER);
                VBox.setVgrow(myList, Priority.ALWAYS);
                
                vbox.getChildren().addAll(label, hello, myList);
                
                VBox.setMargin(label, new Insets(10));
                VBox.setMargin(hello, new Insets(20));
                VBox.setMargin(myList, new Insets(30));
            }
        });
        
        this.add(jfxPanel, BorderLayout.CENTER);
        
        //java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        //setBounds((screenSize.width-460)/2, (screenSize.height-680)/2, 460, 680);
        this.setBounds(200, 200, 400, 400);
        
        this.setVisible(true);
    }
}
