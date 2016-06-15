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

package org.yccheok.jstock.gui.trading;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.yccheok.jstock.trade.DriveWealthAPI;

/**
 *
 * @author  Owner
 */
public class TradingJPanel extends javax.swing.JPanel {
    
    /** Creates new form TradingJPanel */
    public TradingJPanel() {
        initComponents();
        
//        this.initPortfolio();
    }
    
    public void initComponents() {
        // Javafx login form example: 
        // http://docs.oracle.com/javafx/2/get_started/form.htm
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(25, 25, 25, 25));

                scene = new Scene(grid, 300, 275);
                jfxPanel.setScene(scene);

                Text scenetitle = new Text("Drive Wealth Login");
                scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                grid.add(scenetitle, 0, 0, 2, 1);

                Label userName = new Label("User Name:");
                grid.add(userName, 0, 1);

                TextField userTextField = new TextField();
                grid.add(userTextField, 1, 1);

                Label pw = new Label("Password:");
                grid.add(pw, 0, 2);

                PasswordField pwBox = new PasswordField();
                grid.add(pwBox, 1, 2);

                Button btn = new Button("Sign in");
                HBox hbBtn = new HBox(10);

                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.getChildren().add(btn);
                grid.add(hbBtn, 1, 4);
                
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", userTextField.getText());
                        params.put("password", pwBox.getText());
                        
                        DriveWealthAPI api = new DriveWealthAPI(params);
                        DriveWealthAPI.User user = api.user;

                        System.out.println("DriveWealth: username: " + userTextField.getText()
                                            + ", pwd: " + pwBox.getText()
                                            + ", sessionKey: " + user.sessionKey
                                            + ", userID: " + user.userID
                                            + ", commission: " + user.commissionRate);
                    }
                });
            }
        });

        this.add(jfxPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }
    
    private final JFXPanel jfxPanel = new JFXPanel();
    private GridPane grid = new GridPane();
    private Scene scene;
}
