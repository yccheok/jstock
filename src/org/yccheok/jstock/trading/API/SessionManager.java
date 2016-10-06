/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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

    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance () {
        return INSTANCE;
    }

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
    
    public static class Commission {
        private final Double baseRate;
        private final Double baseShares;
        private final Double excessRate;
        private final Double fractionalRate;
        
        public Commission (Double baseRate, Double baseShares, Double excessRate, Double fractionalRate) {
            this.baseRate = baseRate;
            this.baseShares = baseShares;
            this.excessRate = excessRate;
            this.fractionalRate = fractionalRate;
        }
        
        public Double getBaseRate () {
            return this.baseRate;
        }
        
        public Double getBaseShares () {
            return this.baseShares;
        }
        
        public Double getExcessRate () {
            return this.excessRate;
        }
        
        public Double getFractionalRate () {
            return this.fractionalRate;
        }
        
        public static Double calcCommission (Double qty, Commission rate) {
            Double commission;
            
            if (qty < 1) {
                commission = rate.getFractionalRate();
            } else if (qty < rate.getBaseShares()) {
                commission = rate.getBaseRate();
            } else {
                commission = qty * rate.getExcessRate();
            }

            return commission;
        }
    }
    
    public static class User {
        private final String userID;
        private final List<Account> accounts = new ArrayList<>();
        private Account activeAccount = null;
        private Commission commission = null;

        
        public User (Map<String, Object> params) {
            this.userID = params.get("userID").toString(); 

            // populate user accounts
            List<Map<String, Object>> accs = (ArrayList) params.get("accounts");
            for (Map<String, Object> accMap : accs) {

                System.out.println("Create session, class of accountType: "
                        + accMap.get("accountType").getClass());

                Account acc = new Account(accMap);
                this.accounts.add(acc);

                if (this.commission == null) {
                    LinkedTreeMap<String, Object> comm = (LinkedTreeMap) accMap.get("commissionSchedule");
                    this.commission = new Commission((Double) comm.get("baseRate"), (Double) comm.get("baseShares"),
                            (Double) comm.get("excessRate"), (Double) comm.get("fractionalRate"));
                }
            }
        }

        public String getUserID () {
            return this.userID;
        }

        public Commission getCommission () {
            return this.commission;
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
        PRACTICE(1, "Practice"),
        LIVE(2, "Live");

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
            this.nickname       = acc.get("nickname").toString();
            this.cash           = (Double) acc.get("cash");

            Double type = (Double) acc.get("accountType");
            if (type == 1) {
                this.accountType = AccountType.PRACTICE;
            } else {
                this.accountType = AccountType.LIVE;
            }
        }

        // override this for Switch a/c comboBox
        @Override
        public String toString() {
            return String.format("%1$s Account : %2$s", this.accountType.getName(), this.accountNo);
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

        public String getNickname () {
            return this.nickname;
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
        private final String nickname;
        private final AccountType accountType;
        private final Double cash;
    }


    public Pair<Session, DriveWealth.Error> login (String userName, String password) {
        Pair<Session, DriveWealth.Error> createSession = create(userName, password);

        if (this.session != null && this.user != null) {
            // default to live a/c if available
            Account active = null;
            if (! user.getLiveAccounts().isEmpty()) {
                active = user.getLiveAccounts().get(0);
            } else if (! user.getPracticeAccounts().isEmpty()) {
                active = user.getPracticeAccounts().get(0);
            }

            if (active != null) user.setActiveAccount(active);
        }
        
        return createSession;
    }

    public Pair<Session, DriveWealth.Error> relogin () {
        return login(this.userName, this.password);
    }

    private Pair<Session, DriveWealth.Error> create (String userName, String password) {
        this.userName = userName;
        this.password = password;

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

        Map<String, Object> respondMap = Http.post(url, params, null);
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = new Gson().fromJson(respond, HashMap.class);

        DriveWealth.Error error = null;

        if ((int) respondMap.get("code") == 200) {
            // debugging only
            for (String k: OUTPUT_FIELDS) {
                if (result.containsKey(k)) {
                    Object v = result.get(k);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }

            this.session = new Session(result);
            this.user    = this.session.getUser();
        } else {
            error = DriveWealth.getError(result);
        }

        return new Pair<>(this.session, error);
    }

    public User getUser () {
        return this.user;
    }
    
    public Session getSession () {
        return this.session;
    }
    
    public String getSessionKey () {
        if (this.session == null) return null;
        
        return this.session.getSessionKey();
    }

    private User user = null;
    private Session session = null;

    private String userName;
    private String password;
}
