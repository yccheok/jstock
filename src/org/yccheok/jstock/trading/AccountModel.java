/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.Map;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.beans.binding.Bindings;

/**
 *
 * @author shuwnyuan
 */
public class AccountModel {
    public final SimpleDoubleProperty equity;
    public final SimpleDoubleProperty cashBalance;
    public final SimpleDoubleProperty cashForTrade;
    public final SimpleDoubleProperty cashForWithdraw;
    public final SimpleDoubleProperty accountTotal;
    public final SimpleDoubleProperty totalUnrealizedPL;
    public final SimpleDoubleProperty totalUnrealizedPLPercent;

    
    public AccountModel (Map<String, Object> accBlotter, ObservableList<OpenPosModel> posList) {
        LinkedTreeMap<String, Object> _equity  = (LinkedTreeMap) accBlotter.get("equity");
        LinkedTreeMap<String, Object> balance = (LinkedTreeMap) accBlotter.get("cash");

        this.equity          = new SimpleDoubleProperty((Double) _equity.get("equityValue"));
        this.cashBalance     = new SimpleDoubleProperty((Double) balance.get("cashBalance"));
        this.cashForTrade    = new SimpleDoubleProperty((Double) balance.get("cashAvailableForTrade"));
        this.cashForWithdraw = new SimpleDoubleProperty((Double) balance.get("cashAvailableForWithdrawal"));

        Double totalUnrealizedPLD = 0.0;
        for (OpenPosModel pos : posList) {
            totalUnrealizedPLD += pos.getUnrealizedPL();
        }
        this.totalUnrealizedPL = new SimpleDoubleProperty(totalUnrealizedPLD);
        
        this.accountTotal = new SimpleDoubleProperty();
        this.accountTotal.bind(Bindings.add(this.cashBalance, this.equity));
        
        this.totalUnrealizedPLPercent = new SimpleDoubleProperty();
        this.totalUnrealizedPLPercent.bind(this.totalUnrealizedPL.divide(this.equity.subtract(this.totalUnrealizedPL)).multiply(100));
    }

    public void update (ObservableList<OpenPosModel> posList) {
        Double _equity = 0.0;
        Double pl = 0.0;

        for (OpenPosModel pos : posList) {
            _equity += pos.getMarketValue();
            pl += pos.getUnrealizedPL();
        }

        this.setEquity(_equity);
        this.setTotalUnrealizedPL(pl);
    }
    
    public final Double getEquity() {
        return equity.get();
    }
    public final void setEquity(Double v) {
        equity.set(v);
    }
    public DoubleProperty equityProperty() {
        return equity;
    }

    public final Double getCashBalance() {
        return cashBalance.get();
    }
    public final void setCashBalance(Double v) {
        cashBalance.set(v);
    }
    public DoubleProperty cashBalanceProperty() {
        return cashBalance;
    }
    
    public final Double getCashForTrade() {
        return cashForTrade.get();
    }
    public final void setCashForTrade(Double v) {
        cashForTrade.set(v);
    }
    public DoubleProperty cashForTradeProperty() {
        return cashForTrade;
    }

    public final Double getCashForWithdraw() {
        return cashForWithdraw.get();
    }
    public final void setCashForWithdraw(Double v) {
        cashForWithdraw.set(v);
    }
    public DoubleProperty cashForWithdrawProperty() {
        return cashForWithdraw;
    }

    public final Double getTotalUnrealizedPL() {
        return totalUnrealizedPL.get();
    }
    public final void setTotalUnrealizedPL(Double v) {
        totalUnrealizedPL.set(v);
    }
    public DoubleProperty totalUnrealizedPLProperty() {
        return totalUnrealizedPL;
    }
    
    public final Double getTotalUnrealizedPLPercent() {
        return totalUnrealizedPLPercent.get();
    }
    public final void setTotalUnrealizedPLPercent(Double v) {
        totalUnrealizedPLPercent.set(v);
    }
    public DoubleProperty totalUnrealizedPLPercentProperty() {
        return totalUnrealizedPLPercent;
    }

    public final Double getAccountTotal() {
        return accountTotal.get();
    }
    public final void setAccountTotal(Double v) {
        accountTotal.set(v);
    }
    public DoubleProperty accountTotalProperty() {
        return accountTotal;
    }

    // Css class: display profit in green, loss in red
    public String equityValueCss () {
        return "profit";
    }
    
    public String unrealizedPLCss () {
        return cssClass(this.getTotalUnrealizedPL());
    }
    
    public String cashForTradeCss () {
        return cssClass(this.getCashForTrade());
    }
    
    public String accountTotalCss () {
        return cssClass(this.getAccountTotal());
    }
    
    private String cssClass (Double value) {
        return (value > 0) ? "profit" : "loss";
    }
    
}
