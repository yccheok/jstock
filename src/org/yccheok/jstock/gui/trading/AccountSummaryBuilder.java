/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.Locale;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.yccheok.jstock.trading.AccountModel;
import org.yccheok.jstock.trading.OpenPosModel;

/**
 *
 * @author shuwnyuan
 */
public class AccountSummaryBuilder {
    
    private final BorderPane accBorderPane = new BorderPane();
    private final Label shareAmount = new Label();
    private final Label profitAmount = new Label();
    private final Label cashAmount = new Label();
    private final Label totalAmount = new Label();
    
    private AccountModel acc;

    
    public AccountSummaryBuilder() {}
    
    public BorderPane build () {
        // Left content
        HBox leftHbox = new HBox(8);
        
        // Stocks on hand value
        Label shareText = new Label("Share:");
        
        // Unrealized PL
        Label profitText = new Label("Paper Profit:");
        profitText.setPadding(new Insets(0, 0, 0, 10));

        leftHbox.getChildren().addAll(shareText, this.shareAmount, profitText, this.profitAmount);
        
        // Right content
        HBox rightHbox = new HBox(8);
        
        // Cash for trading
        Label cashText = new Label("Cash to Invest:");

        // Total: Cash balance + Stocks
        Label totalText = new Label("Total:");
        totalText.setPadding(new Insets(0, 0, 0, 10));
        
        rightHbox.getChildren().addAll(cashText, this.cashAmount, totalText, this.totalAmount);
        
        this.accBorderPane.setPadding(new Insets(5, 0, 10, 0));    // Insets: top, right, bottom, left
        this.accBorderPane.setLeft(leftHbox);
        this.accBorderPane.setRight(rightHbox);
        this.accBorderPane.setId("accBorderPane");
        
        return this.accBorderPane;
    }

    public void initData (Map<String, Object> accBlotter, ObservableList<OpenPosModel> posList) {
        this.acc = new AccountModel(accBlotter, posList);

        Locale locale  = new Locale("en", "US");
        this.shareAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.equity));
        this.profitAmount.textProperty().bind(Bindings.format(locale, "$%,.2f (%,.2f%%)", this.acc.totalUnrealizedPL, this.acc.totalUnrealizedPLPercent));
        this.cashAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.cashForTrade));
        this.totalAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.accountTotal));

        this.profitAmount.getStyleClass().add(this.acc.unrealizedPLCss());
        this.cashAmount.getStyleClass().add(this.acc.cashForTradeCss());
        this.totalAmount.getStyleClass().add(this.acc.accountTotalCss());
        this.shareAmount.getStyleClass().add(this.acc.equityValueCss());
    }

    public void update (ObservableList<OpenPosModel> posList) {
        this.acc.update(posList);

        this.profitAmount.getStyleClass().clear();
        this.profitAmount.getStyleClass().add(acc.unrealizedPLCss());
    }
}
