/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

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
import org.yccheok.jstock.trading.API.SessionManager;
import org.yccheok.jstock.engine.Pair;

/**
 *
 * @author shuwnyuan
 */
public class SignIn {
    private SignIn () {}

    public static void createTab (TabPane tabPane) {
        SignIn.tabPane = tabPane;

        initUI();
        initEventHandler();
        
        // Drive Wealth Terms & Condition
        initLicenceTab();
    }

    private static void initUI () {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setId("grid");

        // Drive Wealth Logo as Title
        Label titleLabel = new Label();
        
        Image image = new Image(SignIn.class.getResourceAsStream("drivewealth-logo.png"));
        
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
        SignIn.licenceLink.setPrefHeight(30);
        HBox.setHgrow(licenceLabel, Priority.ALWAYS);
        HBox.setHgrow(SignIn.licenceLink, Priority.ALWAYS);

        licenceHBox.getChildren().addAll(licenceLabel, SignIn.licenceLink);
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

        SignIn.signInTab.setTooltip(new Tooltip("Sign In"));
        SignIn.signInTab.setText("Sign In");
        SignIn.signInTab.setContent(grid);
        SignIn.signInTab.setClosable(false);
        SignIn.tabPane.getTabs().add(SignIn.signInTab);
    }
    
    private static void initEventHandler () {
        // Sign In Button action
        signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                successText.setVisible(false);
                String userName = userField.getText();
                String password = pwdField.getText();

                if (userName.isEmpty() || password.isEmpty()) {
                    String welcomeStr = "Please enter username and password";
                    successText.setTextFill(Color.FIREBRICK);
                    successText.setText(welcomeStr);
                    successText.setVisible(true);

                    System.out.println(welcomeStr);
                    return;
                }

                Task loginTask = createLoginTask(userName, password);
                new Thread(loginTask).start();

                // disable "Sign In" button
                signInBtn.setDisable(true);
            }
        });
    }
    
    private static Task createLoginTask (String userName, String password) {
        
        Task< Pair<SessionManager.Session, DriveWealth.Error> > loginTask = new Task< Pair<SessionManager.Session, DriveWealth.Error> >() {
            @Override protected Pair<SessionManager.Session, DriveWealth.Error> call() throws Exception {
                System.out.println("Drive Wealth User Sign In....\n\n ");

                Pair<SessionManager.Session, DriveWealth.Error> login = DriveWealth.login(userName, password);

                SessionManager.Session session  = login.first;
                DriveWealth.Error error         = login.second;
                SessionManager.User user        = session.getUser();

                if (error == null) {
                    System.out.println("DriveWealth: username: "    + userName
                                        + ", pwd: "                 + password
                                        + ", sessionKey: "          + session.getSessionKey()
                                        + ", userID: "              + user.getUserID()
                                        + ", commission: "          + user.getCommissionRate());
                }

                return login;
            }
        };

        loginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                Pair<SessionManager.Session, DriveWealth.Error> login = loginTask.getValue();

                SessionManager.Session session  = login.first;
                DriveWealth.Error error         = login.second;

                if (error != null) {
                    System.out.println("Sign In failed, code: " + error.getMessage());
                    successText.setText("Sign In failed: " + error.getMessage());
                } else {
                    SessionManager.User user = session.getUser();

                    System.out.println("Successfully Sign In, userID: " + user.getUserID());

                    // Login will set practice acc as active account
                    SessionManager.Account acc = user.getActiveAccount();
                    String welcomeStr;

                    if (acc == null && user.getPracticeAccounts().isEmpty()) {
                        System.out.println("No practice account, prompt for creating ??");
                        welcomeStr = "Successfully Sign In, please create practice account to start trading";
                    } else {
                        welcomeStr = "Start trading with Practice Account "
                            + ".\n AccountNo: " + acc.getAccountNo()
                            + "\n AccountID: " + acc.getAccountID()
                            + "\n Balance: " + acc.getCash();
                    }
                    successText.setText(welcomeStr);

                    // create Portfolio Tab
                    if (acc != null) {
                        Tab portfolioTab = Portfolio.createTab();
                        tabPane.getTabs().add(portfolioTab);
                        // select tab
                        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                        selectionModel.select(portfolioTab);

                        tabPane.getTabs().remove(signInTab);
                        System.out.println("Init Portfolio tab DONE....");
                    }
                }

                successText.setTextFill(Color.FIREBRICK);
                successText.setVisible(true);
                // reenable "Sign In" button
                signInBtn.setDisable(false);
            }
        });

        return loginTask;
    }
    
    private static void initLicenceTab () {
        // Load Drive Wealth's licence in new Tab with browser
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        SignIn.licenceTab.setText("Drive Wealth's Terms of Use");
        SignIn.licenceTab.setContent(browser);
        SignIn.licenceTab.setClosable(true);

        final ProgressIndicator progressIn = new ProgressIndicator();
        progressIn.setMaxSize(15, 15);
        SignIn.licenceTab.setGraphic(progressIn);

        SignIn.licenceLink.setOnAction(new EventHandler<ActionEvent>() {
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

    private static TabPane tabPane;

    private static final Hyperlink licenceLink = new Hyperlink("Drive Wealth's Terms of Use");
    
    private static final Button signInBtn = new Button("Sign in");;
    private static final TextField userField = new TextField();;
    private static final PasswordField pwdField = new PasswordField();
    private static final Label successText = new Label();
    
    private static final Tab signInTab  = new Tab();
    private static final Tab licenceTab = new Tab();
}
