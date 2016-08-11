/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author shuwnyuan
 */
public class AccountSummary {
    public AccountSummary () {
    }
    
    public void update (Map<String, Object> accBlotter, List<OpenPos> openPos) {
        LinkedTreeMap<String, Object> equity  = (LinkedTreeMap) accBlotter.get("equity");
        LinkedTreeMap<String, Object> balance = (LinkedTreeMap) accBlotter.get("cash");

        this.equityValue     = (Double) equity.get("equityValue");
        this.cashBalance     = (Double) balance.get("cashBalance");
        this.cashForTrade    = (Double) balance.get("cashAvailableForTrade");
        this.cashForWithdraw = (Double) balance.get("cashAvailableForWithdrawal");
        this.accountTotal    = cashBalance + equityValue;
        
        for (OpenPos pos : openPos) {
            this.totalUnrealizedPL += pos.unrealizedPL;
        }

        if (this.equityValue > 0) {
            this.totalUnrealizedPLPercent = (totalUnrealizedPL / (equityValue - totalUnrealizedPL)) * 100;
        }
        
        this.equityProperty.setValue(Utils.monetaryFormat(equityValue, true));
        this.cashBalanceProperty.setValue(Utils.monetaryFormat(cashBalance, true));
        this.cashForTradeProperty.setValue(Utils.monetaryFormat(cashForTrade, true));
        this.cashForWithdrawProperty.setValue(Utils.monetaryFormat(cashForWithdraw, true));
        this.accountTotalProperty.setValue(Utils.monetaryFormat(accountTotal, true));
        
        String PLStr = Utils.monetaryFormat(totalUnrealizedPL, true) + " (" + Utils.monetaryFormat(totalUnrealizedPLPercent) + "%)";
        this.totalUnrealizedPLProperty.setValue(PLStr);
    }

    public Double equityValue;
    public Double cashBalance;
    public Double cashForTrade;
    public Double cashForWithdraw;
    public Double accountTotal;
    public Double totalUnrealizedPL = 0.0;
    public Double totalUnrealizedPLPercent = 0.0;
    
    public final StringProperty equityProperty = new SimpleStringProperty();
    public final StringProperty cashBalanceProperty = new SimpleStringProperty();
    public final StringProperty cashForTradeProperty = new SimpleStringProperty();
    public final StringProperty cashForWithdrawProperty = new SimpleStringProperty();
    public final StringProperty accountTotalProperty = new SimpleStringProperty();
    public final StringProperty totalUnrealizedPLProperty = new SimpleStringProperty();
}
