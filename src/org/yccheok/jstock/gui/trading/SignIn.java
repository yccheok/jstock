/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import org.yccheok.jstock.trading.API.DriveWealth;

/**
 *
 * @author shuwnyuan
 */
public class SignIn {
    public SignIn (TabPane tabPane) {
        this.tabPane = tabPane;
    }
    
    public void createTab () {
        initUI();
        initEventHandler();
        
        // Drive Wealth Terms & Condition
        initLicenceTab();
    }

    private void initUI () {
        GridPane grid = new GridPane();
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
        userField.setPromptText("Username");
        grid.add(userField, 0, 1);

        // password field
        pwdField.setPromptText("Password");
        grid.add(pwdField, 0, 2);

        // Sign In button
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

        licenceLabel.setPrefHeight(30);
        this.licenceLink.setPrefHeight(30);
        HBox.setHgrow(licenceLabel, Priority.ALWAYS);
        HBox.setHgrow(this.licenceLink, Priority.ALWAYS);

        licenceHBox.getChildren().addAll(licenceLabel, this.licenceLink);
        // HBox max size will follow max size of all children nodes
        licenceHBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE );
        grid.add(licenceHBox, 0, 6);
        GridPane.setHalignment(licenceHBox, HPos.CENTER);

        // Sign In successful msg
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

        this.signInTab.setTooltip(new Tooltip("Sign In"));
        this.signInTab.setText("Sign In");
        this.signInTab.setContent(grid);
        this.signInTab.setClosable(false);
        tabPane.getTabs().add(this.signInTab);
    }
    
    private void initEventHandler () {
        // Sign In Button action
        signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                successText.setVisible(false);
                String username = userField.getText();
                String pwd = pwdField.getText();

                if (username.isEmpty() || pwd.isEmpty()) {
                    String welcomeStr = "Please enter username and password";
                    successText.setTextFill(Color.FIREBRICK);
                    successText.setText(welcomeStr);
                    successText.setVisible(true);

                    System.out.println(welcomeStr);
                    return;
                }

                Task< Map<String, Object> > task = new Task< Map<String, Object> >() {
                    @Override protected Map<String, Object> call() throws Exception {
                        System.out.println("Drive Wealth User Sign In....\n\n ");

                        final DriveWealth _api = new DriveWealth();
                        final Map<String, Object> login = _api.login(username, pwd);
                        final Map<String, Object> result = new HashMap<>();
                        
                        if (login.containsKey("code") && login.containsKey("message")) {
                            result.put("error", login.get("message"));
                            return result;
                        }

                        DriveWealth.User user = _api.user;

                        System.out.println("DriveWealth: username: " + username
                                            + ", pwd: " + pwd
                                            + ", sessionKey: " + user.sessionKey
                                            + ", userID: " + user.userID
                                            + ", commission: " + user.commissionRate);

                        result.put("api", _api);
                        return result;
                    }
                };

                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        Map<String, Object> result = task.getValue();

                        if (result.containsKey("error")) {
                            System.out.println("Sign In failed, code: " + result.get("error"));
                            successText.setText("Sign In failed: " + result.get("error"));
                        } else {
                            api = (DriveWealth) result.get("api");

                            if (api.user != null && api.getSessionKey() != null) {
                                System.out.println("Successfully Sign In, userID: " + api.user.userID);

                                DriveWealth.Account acc = api.user.practiceAccount;
                                String welcomeStr;

                                if (acc == null) {
                                    System.out.println("No practice account, prompt for creating ??");
                                    welcomeStr = "Successfully Sign In, please create practice account to start trading";

                                    /*
                                    Map<String, Object> params = new HashMap<>();
                                    params.put("userID", api.user.userID);
                                    acc = api.createPracticeAccount(params);
                                    */
                                } else {
                                    welcomeStr = "Start trading now with " + acc.nickname + ".\n AccountNo: " + acc.accountNo
                                        + "\n AccountID: " + acc.accountID + "\n Balance: " + acc.cash;
                                }
                                successText.setText(welcomeStr);
                                
                                // create Portfolio Tab
                                if (acc != null) {
                                    Portfolio portfolio = new Portfolio(api);
                                    Tab portfolioTab = portfolio.createTab();
                                    tabPane.getTabs().add(portfolioTab);
                                    // select tab
                                    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                                    selectionModel.select(portfolioTab);

                                    tabPane.getTabs().remove(signInTab);
                                    System.out.println("Init Portfolio tab DONE....");
                                }
                            } else {
                                System.out.println("Sign In failed");
                                successText.setText("Sign In failed");
                            }
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
    
    private void initLicenceTab () {
        // Load Drive Wealth's licence in new Tab with browser
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        this.licenceTab.setText("Drive Wealth's Terms of Use");
        this.licenceTab.setContent(browser);
        this.licenceTab.setClosable(true);

        final ProgressIndicator progressIn = new ProgressIndicator();
        progressIn.setMaxSize(15, 15);
        licenceTab.setGraphic(progressIn);


        this.licenceLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                webEngine.load("https://drivewealth.com/terms-of-use/");
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
    }

    private DriveWealth api;
    private final TabPane tabPane;

    private final Hyperlink licenceLink = new Hyperlink("Drive Wealth's Terms of Use");
    
    private final Button signInBtn = new Button("Sign in");;
    private final TextField userField = new TextField();;
    private final PasswordField pwdField = new PasswordField();
    private final Label successText = new Label();
    
    private final Tab signInTab  = new Tab();
    private final Tab licenceTab = new Tab();
}
