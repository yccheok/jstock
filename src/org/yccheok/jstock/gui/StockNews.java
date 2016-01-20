/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui;

import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

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
import javafx.geometry.Insets;


public class StockNews extends JFrame {
    private final JFXPanel jfxPanel = new JFXPanel();
    
    Scene scene;
    //Text hello;
    //Label label;
    VBox vbox;
    ListView<String> newsList;
    ArrayList<String> news;
    
    public StockNews(ArrayList<String> news) {
        super("Stock News");
        this.news = news;
        initComponents();
    }
    
    private void initComponents() {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                vbox = new VBox();
                scene = new Scene(vbox, 200, 200);
                //scene.setFill(Color.BLACK);
                //scene.getStylesheets().add("stockNews.css");
                
                //label = new Label("Stock News");
                //hello = new Text(stockInfo.symbol.toString() + " (" + stockInfo.code.toString() + ")");
                //hello.setFill(Color.CHOCOLATE);
                
                //DropShadow dropShadow = new DropShadow();
                //dropShadow.setRadius(5.0);
                //dropShadow.setOffsetX(3.0);
                //dropShadow.setOffsetY(3.0);
                //dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
                //hello.setEffect(dropShadow);
                
                jfxPanel.setScene(scene);
                
                newsList = new ListView<>();
                ObservableList<String> FxNews = FXCollections.observableArrayList (news);
                
                newsList.setItems(FxNews);
                newsList.setPrefWidth(50);
                newsList.setPrefHeight(50);
                
                vbox.setPadding(new Insets(10));
                //vbox.setSpacing(100);
                
                vbox.setStyle("-fx-border-color: #2e8b57; -fx-border-width: 2px;");
                vbox.setAlignment(Pos.CENTER);
                VBox.setVgrow(newsList, Priority.ALWAYS);
                
                vbox.getChildren().addAll(newsList);
                
                //VBox.setMargin(label, new Insets(10));
                //VBox.setMargin(hello, new Insets(20));
                //VBox.setMargin(newsList, new Insets(30));
            }
        });
        
        this.add(jfxPanel, BorderLayout.CENTER);
        
        //java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        //setBounds((screenSize.width-460)/2, (screenSize.height-680)/2, 460, 680);
        this.setBounds(200, 200, 400, 400);
        
        this.setVisible(true);
    }
}
