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
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.yccheok.jstock.trade.DriveWealthAPI;

/**
 *
 * @author  Owner
 */
public class TradingJPanel extends javax.swing.JPanel {
    
    /** Creates new form TradingJPanel */
    public TradingJPanel() {  // Rectangle rec) {
        this.height = 500;  // rec.height;
        this.width = 500;   // rec.width;
        
        System.out.println("width: " + this.width + ", height: " + this.height);

        //this.setBounds(rec);
        initComponents();
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
                grid.setVgap(30);
                grid.setPadding(new Insets(25, 25, 25, 25));

                ColumnConstraints column1 = new ColumnConstraints();
                column1.setPercentWidth(80);
                grid.getColumnConstraints().addAll(column1);

                scene = new Scene(grid, width, height);
                scene.getStylesheets().add(TradingJPanel.class.getResource("trading.css").toExternalForm());
                jfxPanel.setScene(scene);

                grid.setId("grid");
                
                Label titleLabel = new Label();
                Image image = new Image(getClass().getResourceAsStream("drivewealth-logo.png"));
                titleLabel.setGraphic(new ImageView(image));
                titleLabel.setAlignment(Pos.CENTER);
                grid.add(titleLabel, 0, 0);
                GridPane.setHalignment(titleLabel, HPos.CENTER);

                // username
                TextField username = new TextField();
                username.setPromptText("Username");
                grid.add(username, 0, 1);

                // password
                PasswordField password = new PasswordField();
                password.setPromptText("Password");
                grid.add(password, 0, 2);

                /*
                Button signInBtn = new Button("Sign in");
                signInBtn.setId("green");
                HBox hbBtn = new HBox(10);

                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.getChildren().add(signInBtn);
                grid.add(hbBtn, 0, 4);
                */
                
                
                Button signInBtn = new Button("Sign in");
                signInBtn.setId("green");
                signInBtn.setMaxWidth(Double.MAX_VALUE);
                signInBtn.setMaxHeight(Double.MAX_VALUE);
                signInBtn.setTextAlignment(TextAlignment.CENTER);
                
                // rowspan = 2
                grid.add(signInBtn, 0, 4, 1, 2);
                GridPane.setHalignment(signInBtn, HPos.CENTER);
                        
                // Sign In successful msg
                final Label successText = new Label();
                successText.setWrapText(true);
                grid.add(successText, 0, 6);
                
                signInBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username.getText());
                        params.put("password", password.getText());
                        
                        Task<DriveWealthAPI> task = new Task<DriveWealthAPI>() {
                            @Override protected DriveWealthAPI call() throws Exception {
                                System.out.println("Drive Wealth User Sign In....\n\n ");

                                DriveWealthAPI _api = new DriveWealthAPI(params);
                                DriveWealthAPI.User user = _api.user;

                                System.out.println("DriveWealth: username: " + username.getText()
                                                    + ", pwd: " + password.getText()
                                                    + ", sessionKey: " + user.sessionKey
                                                    + ", userID: " + user.userID
                                                    + ", commission: " + user.commissionRate);
                                return _api;
                            }
                        };
                        
                        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent t) {
                                api = task.getValue();
                                System.out.println("Successfully Sign In, userID: " + api.user.userID);
                                
                                // reenable "Sign In" button
                                signInBtn.setDisable(false);
                                
                                DriveWealthAPI.Account acc = api.user.practiceAccount;
                                String accountNo =  acc.accountNo;
                                String nickname = acc.nickname;
                                Double cash = acc.cash;

                                String welcomeStr = "Start trading now with " + acc.nickname + ".\n AccountNo: " + acc.accountNo
                                        + "\n AccountID: " + acc.accountID + "\n Balance: " + acc.cash;
                                
                                successText.setTextFill(Color.FIREBRICK);
                                successText.setText(welcomeStr);
                            }
                        });
                        
                        new Thread(task).start();
                        
                        // disable "Sign In" button
                        signInBtn.setDisable(true);
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
    DriveWealthAPI api;

    int width;
    int height;
    
    //private final Rectangle fullSize;
    //private final double sceneWidth;
    //private final double sceneHeight;

}
