/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.Map;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;

/**
 *
 * @author shuwnyuan
 */
public class AccountSummaryModel {
    private final SimpleDoubleProperty equity;
    private final SimpleDoubleProperty cashBalance;
    private final SimpleDoubleProperty cashForTrade;
    private final SimpleDoubleProperty cashForWithdraw;
    private final SimpleDoubleProperty accountTotal;
    private final SimpleDoubleProperty totalUnrealizedPL;
    private final SimpleDoubleProperty totalUnrealizedPLPercent;

    
    public AccountSummaryModel (Map<String, Object> acc) {
        this.equity             = new SimpleDoubleProperty((Double) acc.get("equity"));
        this.cashBalance        = new SimpleDoubleProperty((Double) acc.get("cashBalance"));
        this.cashForTrade       = new SimpleDoubleProperty((Double) acc.get("cashForTrade"));
        this.cashForWithdraw    = new SimpleDoubleProperty((Double) acc.get("cashForWithdraw"));
        this.totalUnrealizedPL  = new SimpleDoubleProperty((Double) acc.get("totalUnrealizedPL"));

        this.accountTotal = new SimpleDoubleProperty();
        this.accountTotal.bind(Bindings.add(this.cashBalance, this.equity));
        
        this.totalUnrealizedPLPercent = new SimpleDoubleProperty();
        this.totalUnrealizedPLPercent.bind(
                Bindings.when(this.equity.greaterThan(0)).then(
                    this.totalUnrealizedPL.divide(this.equity.subtract(this.totalUnrealizedPL)).multiply(100)
                ).otherwise(0)
            );
    }

    public void update (ObservableList<PositionModel> posList) {
        Double _equity = 0.0;
        Double pl = 0.0;

        for (PositionModel pos : posList) {
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
