/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;

import java.util.Map;

/**
 *
 * @author shuwnyuan
 */
public class Account {

    public Account (Map<String, Object> acc) {
        this.accountID      = acc.get("accountID").toString();
        this.accountNo      = acc.get("accountNo").toString();
        this.accountType    = (double) acc.get("accountType");
        this.cash           = (double) acc.get("cash");
        this.nickName       = acc.get("nickname").toString();
    }

    public String getAccountID () {
        return this.accountID;
    }
    
    public String getAccountNo () {
        return this.accountNo;
    }
    
    public String getNickName () {
        return this.nickName;
    }
    
    public double getAccountType () {
        return this.accountType;
    }
    
    public double getCash () {
        return this.cash;
    }

    private final String accountID;
    private final String accountNo;
    private final double accountType;
    private final double cash;
    private final String nickName;
}
