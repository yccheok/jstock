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

import java.awt.Dimension;
import javax.swing.JScrollPane;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.yccheok.jstock.trade.DriveWealthAPI;

/**
 *
 * @author  Owner
 */
public class TradingJPanel extends javax.swing.JPanel {
    
    public TradingJPanel() {
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
                grid.setId("grid");
                
                // Drive Wealth Logo as Title
                Label titleLabel = new Label();
                Image image = new Image(getClass().getResourceAsStream("drivewealth-logo.png"));
                titleLabel.setGraphic(new ImageView(image));
                titleLabel.setAlignment(Pos.CENTER);
                grid.add(titleLabel, 0, 0);
                GridPane.setHalignment(titleLabel, HPos.CENTER);

                // username field
                TextField userField = new TextField();
                userField.setPromptText("Username");
                grid.add(userField, 0, 1);

                // password field
                PasswordField pwdField = new PasswordField();
                pwdField.setPromptText("Password");
                grid.add(pwdField, 0, 2);

                // Sign In button
                Button signInBtn = new Button("Sign in");
                signInBtn.setId("green");
                signInBtn.setMaxWidth(Double.MAX_VALUE);
                signInBtn.setMaxHeight(Double.MAX_VALUE);
                signInBtn.setTextAlignment(TextAlignment.CENTER);
                // rowspan = 2
                grid.add(signInBtn, 0, 4, 1, 2);
                GridPane.setHalignment(signInBtn, HPos.CENTER);

                // Licence
                HBox licenceHBox = new HBox(0);
                Label licenceLabel = new Label("By signing in you agree to ");
                Hyperlink licenceLink = new Hyperlink("Drive Wealth's Terms of Use");
                
                licenceLabel.setPrefHeight(30);
                licenceLink.setPrefHeight(30);
                HBox.setHgrow(licenceLabel, Priority.ALWAYS);
                HBox.setHgrow(licenceLink, Priority.ALWAYS);

                licenceHBox.getChildren().addAll(licenceLabel, licenceLink);
                // HBox max size will follow max size of all children nodes
                licenceHBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE );
                grid.add(licenceHBox, 0, 6);
                GridPane.setHalignment(licenceHBox, HPos.CENTER);
                
                
                // Sign In successful msg
                final Label successText = new Label();
                successText.setWrapText(true);
                grid.add(successText, 0, 7);

                
                // make components auto resize
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(50);
                cc.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().setAll(cc);
                
                RowConstraints rr = new RowConstraints();
                rr.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().setAll(rr, rr, rr, rr, rr, rr, rr);
                


                final Tab tab = new Tab();
                tab.setTooltip(new Tooltip("Sign In"));
                tab.setText("Sign In");
                tab.setContent(grid);
                tab.setClosable(false);

                TabPane tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
                tabPane.getTabs().add(tab);
                
                scene = new Scene(tabPane);
                scene.getStylesheets().add(TradingJPanel.class.getResource("trading.css").toExternalForm());
                jfxPanel.setScene(scene);
                jfxPanel.setPreferredSize(new Dimension(500, 500));

                
                // Load Drive Wealth's licence in new Tab with browser
                final WebView browser = new WebView();
                final WebEngine webEngine = browser.getEngine();
                Tab licenceTab = new Tab();
                licenceTab.setText("Drive Wealth's Terms of Use");
                licenceTab.setContent(browser);
                licenceTab.setClosable(true);

                final ProgressIndicator progressIn = new ProgressIndicator();
                progressIn.setMaxSize(15, 15);
                licenceTab.setGraphic(progressIn);

                
                licenceLink.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        webEngine.load(licenceURL);
                        licenceLink.setDisable(true);
                        tabPane.getTabs().add(licenceTab);
                    }
                });

                webEngine.getLoadWorker().stateProperty().addListener(
                    new javafx.beans.value.ChangeListener<Worker.State>() {
                        public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                            if (newState == SUCCEEDED || newState == FAILED) {
                                licenceLink.setDisable(false);
                                
                                if (progressIn.isVisible() == true) {
                                    progressIn.setVisible(false);
                                }
                                if (licenceTab.getGraphic() != null) {
                                    licenceTab.setGraphic(null);
                                }
                                
                                if (newState == FAILED) {
                                    System.out.println("Failed loading licence page");
                                    return;
                                }
                                
                                SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                                selectionModel.select(licenceTab);
                            }
                            System.out.println("Loading licence page status: " + newState);
                        }
                    }
                );

                signInBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        Map<String, String> params = new HashMap<>();
                        
                        successText.setVisible(false);
                        String username = userField.getText();
                        String pwd = pwdField.getText();
                        
                        if (username.isEmpty() || pwd.isEmpty()) {
                            System.out.println("Please enter username and password.");
                            
                            String welcomeStr = "Please enter username and password";
                            successText.setTextFill(Color.FIREBRICK);
                            successText.setText(welcomeStr);
                            successText.setVisible(true);
                            
                            return;
                        }
                        
                        params.put("username", username);
                        params.put("password", pwd);
                        
                        Task<DriveWealthAPI> task = new Task<DriveWealthAPI>() {
                            @Override protected DriveWealthAPI call() throws Exception {
                                System.out.println("Drive Wealth User Sign In....\n\n ");

                                DriveWealthAPI _api = new DriveWealthAPI(params);
                                DriveWealthAPI.User user = _api.user;

                                System.out.println("DriveWealth: username: " + username
                                                    + ", pwd: " + pwd
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

                                if (api.user != null && api.getSessionKey() != null) {
                                    System.out.println("Successfully Sign In, userID: " + api.user.userID);

                                    DriveWealthAPI.Account acc = api.user.practiceAccount;
                                    String accountNo =  acc.accountNo;
                                    String nickname = acc.nickname;
                                    Double cash = acc.cash;

                                    String welcomeStr = "Start trading now with " + acc.nickname + ".\n AccountNo: " + acc.accountNo
                                            + "\n AccountID: " + acc.accountID + "\n Balance: " + acc.cash;

                                    successText.setText(welcomeStr);
                                } else {
                                    System.out.println("Sign In failed");
                                    successText.setText("Sign In failed");
                                }

                                successText.setTextFill(Color.FIREBRICK);
                                successText.setVisible(true);
                                
                                // reenable "Sign In" button
                                signInBtn.setDisable(false);
                            }
                        });
                        
                        new Thread(task).start();
                        
                        // disable "Sign In" button
                        signInBtn.setDisable(true);
                    }
                });
            }
        });

        jScrollPane.getViewport().add(jfxPanel);        
        jScrollPane.setPreferredSize(new Dimension(500, 500));

        //this.setLayout(new java.awt.BorderLayout(5, 5));
        //this.add(this.jScrollPane, BorderLayout.NORTH);
        
        this.setLayout(new java.awt.GridLayout(0, 1, 5, 5));
        this.add(this.jScrollPane);
        
        this.setVisible(true);
    }

    private final JScrollPane jScrollPane = new javax.swing.JScrollPane();
    private final JFXPanel jfxPanel = new JFXPanel();
    
    private GridPane grid;
    private Scene scene;
    
    public DriveWealthAPI api;
    
    static String licenceURL = "https://drivewealth.com/terms-of-use/";
}
