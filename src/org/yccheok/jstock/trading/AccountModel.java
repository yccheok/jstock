/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author shuwnyuan
 */
public class AccountModel {
    private Double equityD;
    private Double cashBalanceD;
    private Double cashForTradeD;
    private Double cashForWithdrawD;
    private Double accountTotalD;
    private Double totalUnrealizedPLD = 0.0;
    private Double totalUnrealizedPLPercentD = 0.0;

    public final SimpleStringProperty equity;
    public final SimpleStringProperty cashBalance;
    public final SimpleStringProperty cashForTrade;
    public final SimpleStringProperty cashForWithdraw;
    public final SimpleStringProperty accountTotal;
    public final SimpleStringProperty unrealizedPL;

    public AccountModel (Map<String, Object> accBlotter, ObservableList<OpenPosModel> posList) {
        LinkedTreeMap<String, Object> equity  = (LinkedTreeMap) accBlotter.get("equity");
        LinkedTreeMap<String, Object> balance = (LinkedTreeMap) accBlotter.get("cash");

        this.equityD          = (Double) equity.get("equityValue");
        this.cashBalanceD     = (Double) balance.get("cashBalance");
        this.cashForTradeD    = (Double) balance.get("cashAvailableForTrade");
        this.cashForWithdrawD = (Double) balance.get("cashAvailableForWithdrawal");
        this.accountTotalD    = this.cashBalanceD + this.equityD;
        
        for (OpenPosModel pos : posList) {
            this.totalUnrealizedPLD += pos.unrealizedPLD;
        }
        this.totalUnrealizedPLPercentD = (this.totalUnrealizedPLD / (this.equityD - this.totalUnrealizedPLD)) * 100;
        
        this.equity          = new SimpleStringProperty(Utils.monetaryFormat(this.equityD, true));
        this.cashBalance     = new SimpleStringProperty(Utils.monetaryFormat(this.cashBalanceD, true));
        this.cashForTrade    = new SimpleStringProperty(Utils.monetaryFormat(this.cashForTradeD, true));
        this.cashForWithdraw = new SimpleStringProperty(Utils.monetaryFormat(this.cashForWithdrawD, true));
        this.accountTotal    = new SimpleStringProperty(Utils.monetaryFormat(this.accountTotalD, true));

        String PLStr = Utils.monetaryFormat(this.totalUnrealizedPLD, true) + " (" + Utils.monetaryFormat(this.totalUnrealizedPLPercentD) + "%)";
        this.unrealizedPL = new SimpleStringProperty(PLStr);
    }

    public void update (ObservableList<OpenPosModel> posList) {
        this.equityD = 0.0;
        this.totalUnrealizedPLD = 0.0;
        this.totalUnrealizedPLPercentD = 0.0;
        this.accountTotalD = 0.0;
        
        for (OpenPosModel pos : posList) {
            this.equityD += pos.marketValueD;
            this.totalUnrealizedPLD += pos.unrealizedPLD;
        }
        this.totalUnrealizedPLPercentD = (this.totalUnrealizedPLD / (this.equityD - this.totalUnrealizedPLD)) * 100;
        this.accountTotalD = this.cashBalanceD + this.equityD;
        
        this.setEquity(Utils.monetaryFormat(this.equityD, true));
        this.setAccountTotal(Utils.monetaryFormat(this.accountTotalD, true));

        String PLStr = Utils.monetaryFormat(this.totalUnrealizedPLD, true) + " (" + Utils.monetaryFormat(this.totalUnrealizedPLPercentD) + "%)";
        this.setUnrealizedPL(PLStr);
    }
    
    public String getEquity() {
        return equity.get();
    }
    public void setEquity(String v) {
        equity.set(v);
    }
    public StringProperty equityProperty() {
        return equity;
    }

    public String getCashBalance() {
        return cashBalance.get();
    }
    public void setCashBalance(String v) {
        cashBalance.set(v);
    }
    public StringProperty cashBalanceProperty() {
        return cashBalance;
    }
    
    public String getCashForTrade() {
        return cashForTrade.get();
    }
    public void setCashForTrade(String v) {
        cashForTrade.set(v);
    }
    public StringProperty cashForTradeProperty() {
        return cashForTrade;
    }

    public String getCashForWithdraw() {
        return cashForWithdraw.get();
    }
    public void setCashForWithdraw(String v) {
        cashForWithdraw.set(v);
    }
    public StringProperty cashForWithdrawProperty() {
        return cashForWithdraw;
    }

    public String getAccountTotal() {
        return accountTotal.get();
    }
    public void setAccountTotal(String v) {
        accountTotal.set(v);
    }
    public StringProperty accountTotalProperty() {
        return accountTotal;
    }

    public String getUnrealizedPL() {
        return unrealizedPL.get();
    }
    public void setUnrealizedPL(String v) {
        unrealizedPL.set(v);
    }
    public StringProperty unrealizedPLProperty() {
        return unrealizedPL;
    }

    // Css class: display profit in green, loss in red
    public String equityValueCss () {
        return "profit";
    }
    
    public String unrealizedPLCss () {
        return cssClass(this.totalUnrealizedPLD);
    }
    
    public String cashForTradeCss () {
        return cssClass(this.cashForTradeD);
    }
    
    public String accountTotalCss () {
        return cssClass(this.accountTotalD);
    }
    
    private String cssClass (Double value) {
        return (value > 0) ? "profit" : "loss";
    }
    
}
