/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import javafx.application.Platform;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
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
import static org.yccheok.jstock.trading.API.SessionManager.User;

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
        signInHandler();
        licenceHandler();
    }

    public void showLogin () {
        if (portfolio != null) portfolio.setVisible(false);
        signInGrid.setVisible(true);
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

        // progress indicator
        progressIn.setVisible(false);
        final VBox progressBox = new VBox();
        progressBox.setPrefHeight(5);
        progressBox.getChildren().addAll(progressIn);

        signInGrid.add(progressBox, 0, 5);
        
        // Licence
        HBox licenceHBox = new HBox(0);
        Label licenceLabel = new Label("By signing in you agree to ");

        licenceLabel.setPrefHeight(30);
        licenceLink.setPrefHeight(30);
        HBox.setHgrow(licenceLabel, Priority.ALWAYS);
        HBox.setHgrow(licenceLink, Priority.ALWAYS);

        licenceHBox.getChildren().addAll(licenceLabel, licenceLink);
        // HBox max size will follow max size of all children nodes
        licenceHBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE );
        signInGrid.add(licenceHBox, 0, 6);
        GridPane.setHalignment(licenceHBox, HPos.CENTER);

        
        // make components auto resize
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        cc.setHgrow(Priority.ALWAYS);
        signInGrid.getColumnConstraints().setAll(cc);

        RowConstraints rr = new RowConstraints();
        rr.setVgrow(Priority.ALWAYS);
        signInGrid.getRowConstraints().setAll(rr, rr, rr, rr, rr, rr, rr);

        stack.getChildren().add(signInGrid);
        
        // focus on username
        Platform.runLater(() -> userField.requestFocus());
    }
    
    private void signInHandler () {
        // Sign In Button action
        signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                errorText.setVisible(false);
                String userName = userField.getText();
                String password = pwdField.getText();

                if (userName.isEmpty() || password.isEmpty()) {
                    String errorStr = "Please enter username and password.";
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

                // progress indicator
                progressIn.setVisible(true);
            }
        });
    }

    private void licenceHandler () {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        Dialog licenceDlg = new Dialog<>();
        StackPane dlgStack = new StackPane();
        ProgressBar progressBar = new ProgressBar();
        Label error = new Label("Failed to load page.");

        licenceDlg.setResizable(true);

        ButtonType closeBtn = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
        licenceDlg.getDialogPane().getButtonTypes().add(closeBtn);

        // updating progress bar using binding
        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());

        dlgStack.getChildren().addAll(error, webView, progressBar);
        licenceDlg.getDialogPane().setContent(dlgStack);

        
        licenceLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {            
                // Load DriveWealth T&C in Dialog - WebView
                webEngine.load("https://drivewealth.com/terms-of-use/");
                licenceLink.setDisable(true);

                error.setVisible(false);
                progressBar.setVisible(true);
                webView.setVisible(true);

                licenceDlg.showAndWait();

                // Close btn is clicked, stop webEngine load
                // http://stackoverflow.com/questions/22436498/how-to-stop-webengine-after-closing-stage-javafx
                webEngine.load(null);
                licenceLink.setDisable(false);
            }
        });

        webEngine.getLoadWorker().stateProperty().addListener(new javafx.beans.value.ChangeListener<Worker.State>() {
                public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                    System.out.println("Loading licence page status: " + newState);

                    if (newState == SUCCEEDED) {
                        // hide progress bar
                        progressBar.setVisible(false);
                    } else if (newState == FAILED) {
                        progressBar.setVisible(false);
                        webView.setVisible(false);
                        error.setVisible(true);
                    }
                }
            }
        );
    }
    
    private Task createLoginTask (String userName, String password) {
        
        Task< Pair<String, DriveWealth.Error> > loginTask = new Task< Pair<String, DriveWealth.Error> >() {
            @Override protected Pair<String, DriveWealth.Error> call() throws Exception {
                Pair<String, DriveWealth.Error> login = SessionManager.getInstance().login(userName, password);
                return login;
            }
        };

        loginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                // Enable "Sign In" Btn
                signInBtn.setDisable(false);
                // Hide progress Indicator
                progressIn.setVisible(false);

                Pair<String, DriveWealth.Error> login = loginTask.getValue();
                String sessionKey       = login.first;
                DriveWealth.Error error = login.second;

                if (error != null) {
                    errorText.setText("Sign In failed: " + error.getMessage());
                    errorText.setTextFill(Color.FIREBRICK);
                    errorText.setVisible(true);
                    return;
                }

                // clear fields, so that when user Logout & reLogin, username + pwd are not set
                userField.clear();
                pwdField.clear();

                User user = SessionManager.getInstance().getUser();
                System.out.println("Successfully Sign In, userID: " + user.getUserID());

                portfolio = Portfolio.getInstance().show();

                signInGrid.setVisible(false);
                stack.getChildren().add(portfolio);
            }
        });

        return loginTask;
    }

    private final Hyperlink licenceLink = new Hyperlink("Drive Wealth's Terms of Use");
    
    private final Button signInBtn = new Button("Sign in");
    private final TextField userField = new TextField();
    private final PasswordField pwdField = new PasswordField();
    private final Label errorText = new Label();
    private final ProgressIndicator progressIn = new ProgressIndicator();
    
    private StackPane stack;
    private final GridPane signInGrid = new GridPane();
    private StackPane portfolio = null;
}
