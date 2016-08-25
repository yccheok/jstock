/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shuwnyuan
 */
public class User {

    public User (Map<String, Object> params) {
        this.username = params.get("username").toString();
        this.password = params.get("password").toString();
        this.userID = params.get("userID").toString(); 
        this.commissionRate = (Double) params.get("commissionRate");
        
        this.sessionKey = params.get("sessionKey").toString();
    }

    public String getUserName () {
        return this.username;
    }
    
    public String getPassword () {
        return this.password;
    }
    
    public String getSessionKey () {
        return this.sessionKey;
    }
    
    public void setSessionKey (String sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    public String getUserID () {
        return this.userID;
    }
    
    public double getCommissionRate () {
        return this.commissionRate;
    }
    
    public void setPracticeAccount (Account acc) {
        this.practiceAccount = acc;
        addAccount(acc);
    }

    public void setLiveAccount (Account acc) {
        this.liveAccount = acc;
        addAccount(acc);
    }

    public Account getPracticeAccount () {
        return this.practiceAccount;
    }

    public Account getLiveAccount () {
        return this.liveAccount;
    }
    
    private boolean addAccount (Account acc) {
        String accountID = acc.getAccountID();
        
        if (!this.accountsMap.containsKey(accountID)) {
            this.accountsMap.put(accountID, acc);
            return true;
        }
        
        // no acc added
        return false;
    }

    
    private final String username;
    private final String password;
    private final String userID;
    private final double commissionRate;
    
    private String sessionKey;
    
    private Account practiceAccount;
    private Account liveAccount;
    private final Map<String, Account> accountsMap = new HashMap<>();
}


