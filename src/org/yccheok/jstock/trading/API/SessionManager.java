/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yccheok.jstock.engine.Pair;

/**
 *
 * @author shuwnyuan
 */
public class SessionManager {

    // avoid being instantiated as object instance
    private SessionManager () {}

    public static class Session {
        private final String sessionKey;
        private final User user;

        public Session (Map<String, Object> params) {
            this.sessionKey = params.get("sessionKey").toString();
            this.user = new User(params);
        }
        
        public String getSessionKey () {
            return this.sessionKey;
        }
        
        public User getUser () {
            return this.user;
        }
    }
    
    public static class User {
        private final String username;
        private final String password;
        private final String userID;
        private final Double commissionRate;
        private final List<Account> accounts = new ArrayList<>();
        private Account activeAccount = null;
        
        
        public User (Map<String, Object> params) {
            this.username       = params.get("username").toString();
            this.password       = params.get("password").toString();
            this.userID         = params.get("userID").toString(); 
            this.commissionRate = (Double) params.get("commissionRate");
            
            // populate user accounts
            List<Map<String, Object>> accs = (ArrayList) params.get("accounts");
            for (Map<String, Object> accMap : accs) {

                System.out.println("Create session, class of accountType: "
                        + accMap.get("accountType").getClass());

                Account acc = new Account(accMap);
                this.accounts.add(acc);
            }
        }

        public String getUserName () {
            return this.username;
        }

        public String getPassword () {
            return this.password;
        }

        public String getUserID () {
            return this.userID;
        }

        public Double getCommissionRate () {
            return this.commissionRate;
        }

        public List<Account> getAccounts () {
            return this.accounts;
        }
        
        public List<Account> getPracticeAccounts () {
            return getAccountsByType(AccountType.PRACTICE);
        }

        public List<Account> getLiveAccounts () {
            return getAccountsByType(AccountType.LIVE);
        }

        private List<Account> getAccountsByType (AccountType accType) {
            List<Account> accounts = new ArrayList<>();
            
            for (Account acc: this.accounts) {
                if (acc.getAccountType() == accType) {
                    accounts.add(acc);
                }
            }
            return accounts;
        }
        
        public void setActiveAccount (Account account) {
            this.activeAccount = account;
        }
        
        public Account getActiveAccount () {
            return this.activeAccount;
        }
    }
    
    public static enum AccountType {
        PRACTICE(1, "PRACTICE"),
        LIVE(2, "LIVE");

        private final Integer value;
        private final String name;

        private AccountType(Integer value, String name) {
           this.value = value;
           this.name = name;
        }

        public Integer getValue () {
            return this.value;
        }

        public String getName () {
            return this.name;
        }
    }

    public static class Account {
        
        public Account (Map<String, Object> acc) {
            this.accountID      = acc.get("accountID").toString();
            this.accountNo      = acc.get("accountNo").toString();
            this.userID         = acc.get("userID").toString();
            this.cash           = (Double) acc.get("cash");

            Double type = (Double) acc.get("accountType");
            if (type == 1) {
                this.accountType = AccountType.PRACTICE;
            } else {
                this.accountType = AccountType.LIVE;
            }
        }

        public String getAccountID () {
            return this.accountID;
        }

        public String getAccountNo () {
            return this.accountNo;
        }

        public String getUserID () {
            return this.userID;
        }

        public AccountType getAccountType () {
            return this.accountType;
        }

        public Double getCash () {
            return this.cash;
        }

        private final String accountID;
        private final String accountNo;
        private final String userID;
        private final AccountType accountType;
        private final Double cash;
    }
    
    
    public static Pair<Session, DriveWealth.Error> create (String userName, String password) {
        String url = "userSessions";

        List<String> INPUT_FIELDS = new ArrayList<>(Arrays.asList(
            "username",
            "password",
            "appTypeID",
            "appVersion",
            "languageID",
            "osType",
            "osVersion",
            "scrRes",
            "ipAddress"
        ));

        List<String> OUTPUT_FIELDS = new ArrayList<>(Arrays.asList(
            "appTypeID",
            "commissionRate",
            "loginState",
            "referralCode",
            "sessionKey",
            "userID",
            "wlpID",
            "accounts",
            "instruments"
        ));
        
        Map<String, Object> params = new HashMap<>();
        params.put("username",      userName);
        params.put("password",      password);

        // temporary hardcoded
        params.put("appTypeID",     "26");
        params.put("appVersion",    "0.1");
        params.put("languageID",    "en_US");
        params.put("osType",        "iOS");
        params.put("osVersion",     "iOS 9.1");
        params.put("scrRes",        "1920x1080");
        params.put("ipAddress",     "1.1.1.1");
        
        // debug only
        for (String k: INPUT_FIELDS) {
            if (params.containsKey(k)) {
                String v = params.get(k).toString();
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        Map<String, Object> respondMap = DriveWealth.executePost(url, params, null);
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = new Gson().fromJson(respond, HashMap.class);

        Session session = null;
        DriveWealth.Error error = null;

        if ((int) respondMap.get("code") == 200) {
            // debugging only
            for (String k: OUTPUT_FIELDS) {
                if (result.containsKey(k)) {
                    Object v = result.get(k);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }

            result.put("username", userName);
            result.put("password", password);
            
            session = new Session(result);
        } else {
            error = DriveWealth.getError(result);
        }

        return new Pair<>(session, error);
    }

}
