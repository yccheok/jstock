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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    private static final SignIn INSTANCE = new SignIn();

    public static SignIn getInstance () {
        return INSTANCE;
    }

    public static SignIn build (StackPane stack) {
        INSTANCE.stack = stack;
        return INSTANCE;
    }

    public void show () {
        initUI();
        initEventHandler();
        
        // Drive Wealth Terms & Condition
        //initLicenceTab();
    }

    private void initUI () {
        signInGrid.setAlignment(Pos.CENTER);
        signInGrid.setHgap(10);
        signInGrid.setVgap(15);
        signInGrid.setPadding(new Insets(25, 25, 25, 25));
        signInGrid.setId("grid");

        // Drive Wealth Logo as Title
        Label titleLabel = new Label();
        
        Image image = new Image(SignIn.class.getResourceAsStream("drivewealth-logo.png"));
        
        titleLabel.setGraphic(new ImageView(image));
        titleLabel.setAlignment(Pos.CENTER);
        signInGrid.add(titleLabel, 0, 0);
        GridPane.setHalignment(titleLabel, HPos.CENTER);

        // username field
        userField.setPromptText("Username");
        signInGrid.add(userField, 0, 1);

        // password field
        pwdField.setPromptText("Password");
        signInGrid.add(pwdField, 0, 2);

        // Error msg
        errorText.setWrapText(true);
        errorText.setVisible(false);
        signInGrid.add(errorText, 0, 3);

        // Sign In button
        signInBtn.setId("green");
        signInBtn.setMaxWidth(Double.MAX_VALUE);
        signInBtn.setMaxHeight(Double.MAX_VALUE);
        signInBtn.setTextAlignment(TextAlignment.CENTER);
        
        signInGrid.add(signInBtn, 0, 4);
        
        GridPane.setHalignment(signInBtn, HPos.CENTER);

        
        /*
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
        */

        
        // make components auto resize
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        cc.setHgrow(Priority.ALWAYS);
        signInGrid.getColumnConstraints().setAll(cc);

        RowConstraints rr = new RowConstraints();
        rr.setVgrow(Priority.ALWAYS);
        signInGrid.getRowConstraints().setAll(rr, rr, rr, rr, rr, rr, rr);

        stack.getChildren().add(signInGrid);
    }
    
    private void initEventHandler () {
        // Sign In Button action
        signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                errorText.setVisible(false);
                String userName = userField.getText();
                String password = pwdField.getText();

                if (userName.isEmpty() || password.isEmpty()) {
                    String errorStr = "Please enter username and password";
                    errorText.setTextFill(Color.FIREBRICK);
                    errorText.setText(errorStr);
                    errorText.setVisible(true);

                    System.out.println(errorStr);
                    return;
                }

                Task loginTask = createLoginTask(userName, password);
                new Thread(loginTask).start();

                // disable "Sign In" button
                signInBtn.setDisable(true);
            }
        });
    }

    private Task createLoginTask (String userName, String password) {
        
        Task< Pair<SessionManager.Session, DriveWealth.Error> > loginTask = new Task< Pair<SessionManager.Session, DriveWealth.Error> >() {
            @Override protected Pair<SessionManager.Session, DriveWealth.Error> call() throws Exception {
                Pair<SessionManager.Session, DriveWealth.Error> login = DriveWealth.login(userName, password);
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
                    errorText.setText("Sign In failed: " + error.getMessage());
                    errorText.setTextFill(Color.FIREBRICK);
                    errorText.setVisible(true);

                    // reenable "Sign In" button
                    signInBtn.setDisable(false);
                    return;
                }

                SessionManager.User user = session.getUser();
                System.out.println("Successfully Sign In, userID: " + user.getUserID());

                // Login will set practice acc as active account
                SessionManager.Account acc = user.getActiveAccount();

                // create Portfolio Tab
                if (acc != null) {
                    VBox portfolio = Portfolio.getInstance().show();
                    stack.getChildren().remove(signInGrid);
                    stack.getChildren().add(portfolio);
                }
            }
        });

        return loginTask;
    }

    /*  
    private void initLicenceTab () {
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
    */
    
    
    //private final Hyperlink licenceLink = new Hyperlink("Drive Wealth's Terms of Use");
    
    private final Button signInBtn = new Button("Sign in");
    private final TextField userField = new TextField();
    private final PasswordField pwdField = new PasswordField();
    private final Label errorText = new Label();
    
    //private final Tab licenceTab = new Tab();

    private StackPane stack;
    private final GridPane signInGrid = new GridPane();
}
