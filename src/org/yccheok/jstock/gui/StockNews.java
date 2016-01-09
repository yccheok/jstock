/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui;

import java.awt.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javax.swing.*;


public class StockNews extends JFrame {
    private final JFXPanel jfxPanel = new JFXPanel();
    //private final JPanel panel = new JPanel(new BorderLayout());
    
    Scene scene;
    StackPane stack;
    Text hello;
 
    public StockNews(String title) {
        super(title);
        initComponents();
    }
    
    private void initComponents() {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                stack = new StackPane();
                scene = new Scene(stack, 500, 500);
                hello = new Text("Hello Shuwn Yuan! :D");
                
                scene.setFill(Color.BLACK);
                hello.setFill(Color.WHEAT);
                hello.setEffect(new Reflection());
                
                jfxPanel.setScene(scene);
                stack.getChildren().add(hello);
            }
        });
        
        this.add(jfxPanel, BorderLayout.CENTER);
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-460)/2, (screenSize.height-680)/2, 460, 680);
        this.setVisible(true);
    }
}
