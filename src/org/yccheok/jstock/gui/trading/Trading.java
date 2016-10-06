/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.yccheok.jstock.trading.API.SessionManager;
import static org.yccheok.jstock.trading.API.SessionManager.Account;
import static org.yccheok.jstock.trading.API.SessionManager.User;

/**
 *
 * @author shuwnyuan
 */
public class Trading {

    private Trading () {}

    private static final Trading INSTANCE = new Trading();

    public static Trading getInstance () {
        return INSTANCE;
    }
    
    public VBox show () {
        // switch a/c DropDown
        User user = SessionManager.getInstance().getUser();

        ComboBox<Account> accCombo = new ComboBox<>();
        ObservableList<Account> data = FXCollections.observableArrayList(user.getAccounts());
        accCombo.setItems(data);

        // a/c change listener
        accCombo.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Account> arg0, Account oldVal, Account newVal) -> {
            if (newVal != null) {
                System.out.println("Acc changed: " + newVal.toString());

                user.setActiveAccount(newVal);
                Portfolio.getInstance().initPortfolioServ();
            }
        });

        HBox accHBox = new HBox();
        accHBox.setAlignment(Pos.BASELINE_CENTER);
        accHBox.setPadding(new Insets(10, 0, 10, 0));    // Top Right Bottom Left
        accHBox.setSpacing(5);
        accHBox.getChildren().addAll(new Label("Active Account: "), accCombo);

        VBox mainBox = new VBox();
        mainBox.getChildren().add(accHBox);

        Account acc = user.getActiveAccount();

        // Portfolio
        if (acc != null) {
            accCombo.getSelectionModel().select(acc);

            VBox portfolio = Portfolio.getInstance().show();
            mainBox.getChildren().add(portfolio);
        }
        
        return mainBox;
    }
    
    
}

