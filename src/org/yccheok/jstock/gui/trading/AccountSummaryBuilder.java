/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.Locale;
import javafx.beans.binding.Bindings;
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
import org.yccheok.jstock.trading.AccountSummaryModel;
import org.yccheok.jstock.trading.PositionModel;

/**
 *
 * @author shuwnyuan
 */
public class AccountSummaryBuilder {

    private final HBox headerBox = new HBox();

    private final Label shareAmount = new Label();
    private final Label profitAmount = new Label();
    private final Label cashAmount = new Label();
    private final Label totalAmount = new Label();
    
    private AccountSummaryModel acc;

    
    public AccountSummaryBuilder() {}
    

    public HBox build () {
        // switch a/c DropDown
        SessionManager.User user = SessionManager.getInstance().getUser();

        ComboBox<SessionManager.Account> accCombo = new ComboBox<>();
        ObservableList<SessionManager.Account> data = FXCollections.observableArrayList(user.getAccounts());
        accCombo.setItems(data);

        SessionManager.Account acc = user.getActiveAccount();
        if (acc != null) {
            accCombo.getSelectionModel().select(acc);
        }
        
        // a/c change listener
        accCombo.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends SessionManager.Account> arg0, SessionManager.Account oldVal, SessionManager.Account newVal) -> {
            if (newVal != null) {
                System.out.println("Acc changed: " + newVal.toString());

                user.setActiveAccount(newVal);
                Portfolio.getInstance().initPortfolioServ();
            }
        });
        
        Label dwLabel = new Label("DriveWealth");
        dwLabel.setId("dwLabel");
        //dwLabel.setAlignment(Pos.CENTER);
        HBox dwBox = new HBox();
        dwBox.setPrefWidth(350);
        dwBox.getChildren().add(dwLabel);
        dwBox.setAlignment(Pos.CENTER);

        // A/c summary
        shareAmount.getStyleClass().add("cash");
        profitAmount.getStyleClass().add("cash");
        cashAmount.getStyleClass().add("cash");
        totalAmount.getStyleClass().addAll("cash", "bold");

        VBox cashBox = new VBox();
        cashBox.setAlignment(Pos.CENTER_LEFT);
        cashBox.setSpacing(5);
        Label cashLabel = new Label("CASH TO INVEST");
        cashLabel.getStyleClass().add("headerTitle");
        cashBox.getChildren().addAll(cashLabel, this.cashAmount);
        
        VBox shareBox = new VBox();
        shareBox.setAlignment(Pos.CENTER_LEFT);
        shareBox.setSpacing(5);
        Label shareLabel = new Label("INVESTMENTS");
        shareLabel.getStyleClass().add("headerTitle");
        shareBox.getChildren().addAll(shareLabel, this.shareAmount);

        VBox totalBox = new VBox();
        totalBox.setAlignment(Pos.CENTER_LEFT);
        totalBox.setSpacing(5);
        Label totalLabel = new Label("TOTAL");
        totalLabel.getStyleClass().addAll("headerTitle", "bold");
        totalBox.getChildren().addAll(totalLabel, this.totalAmount);

        VBox profitBox = new VBox();
        profitBox.setPadding(new Insets(0, 0, 5, 0));
        profitBox.setAlignment(Pos.BOTTOM_LEFT);
        profitAmount.setPadding(new Insets(5, 5, 5, 5));
        profitBox.getChildren().addAll(this.profitAmount);

        HBox summaryBox = new HBox();
        summaryBox.setId("accSummaryBox");
        summaryBox.setPadding(new Insets(15, 10, 10, 15));
        summaryBox.setSpacing(15);
        //summaryBox.setPrefWidth(450);
        summaryBox.getChildren().addAll(cashBox, shareBox, totalBox, profitBox);

        // Header
        headerBox.setId("accHeader");
        headerBox.setPrefHeight(120);
        headerBox.getChildren().addAll(dwBox, summaryBox, accCombo);

        return headerBox;
    }

    public void initData (AccountSummaryModel accModel) {
        resetData();

        this.acc = accModel;
        Locale locale  = new Locale("en", "US");

        this.shareAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.equityProperty()));
        
        this.profitAmount.textProperty().bind(Bindings.format(locale, "$%,.2f (%,.2f%%)",
                this.acc.totalUnrealizedPLProperty(),
                this.acc.totalUnrealizedPLPercentProperty()));
        
        this.cashAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.cashForTradeProperty()));
        this.totalAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.accountTotalProperty()));

        this.profitAmount.getStyleClass().add(this.acc.unrealizedPLCss());
    }

    public void resetData () {
        // remove binding & css
        this.profitAmount.textProperty().unbind();
        this.profitAmount.getStyleClass().clear();
    }
    
    public void update (ObservableList<PositionModel> posList) {
        this.acc.update(posList);

        this.profitAmount.getStyleClass().clear();
        profitAmount.getStyleClass().addAll("header", acc.unrealizedPLCss());
    }
}
