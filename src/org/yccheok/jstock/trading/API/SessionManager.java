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
    private User user = null;
    private String sessionKey = null;

    private String username;
    private String password;
    

    // avoid being instantiated as object instance
    private SessionManager () {}

    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance () {
        return INSTANCE;
    }

    public User getUser () {
        return this.user;
    }
    
    public String getSessionKey () {
        return this.sessionKey;
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

        private final String avatarUrl;
        private final String emailAddress;
        private final String displayName;
        private final String firstName;
        private final String lastName;

        
        public User (Map<String, Object> session, Map<String, Object> userParams) {
            this.userID = session.get("userID").toString();

            // populate user accounts
            List<Map<String, Object>> accs = (ArrayList) session.get("accounts");
            for (Map<String, Object> accMap : accs) {
                Account acc = new Account(accMap);
                this.accounts.add(acc);

                if (this.commission == null) {
                    LinkedTreeMap<String, Object> comm = (LinkedTreeMap) accMap.get("commissionSchedule");
                    this.commission = new Commission((Double) comm.get("baseRate"), (Double) comm.get("baseShares"),
                            (Double) comm.get("excessRate"), (Double) comm.get("fractionalRate"));
                }
            }

            this.avatarUrl    = userParams.get("avatarUrl").toString();
            this.emailAddress = userParams.get("emailAddress").toString();
            this.displayName  = userParams.get("displayName").toString();
            this.firstName    = userParams.get("firstName").toString();
            this.lastName     = userParams.get("lastName").toString();
        }

        public String getUserID () {
            return this.userID;
        }

        public String getAvatarUrl () {
            return this.avatarUrl;
        }
        
        public String getEmailAddress () {
            return this.emailAddress;
        }
        
        public String getDisplayName () {
            return this.displayName;
        }
        
        public String getFirstName () {
            return this.firstName;
        }
        
        public String getLastName () {
            return this.lastName;
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
            return this.accountNo;
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


    public Pair<String, DriveWealth.Error> login (String username, String password) {
        this.username = username;
        this.password = password;

        Pair< Map<String, Object>, DriveWealth.Error> result = create(username, password);
        Map<String, Object> session = result.first;
        DriveWealth.Error error = result.second;

        // create session failed
        if (error != null) {
            this.sessionKey = null;
            return new Pair<>(null, error);
        }
        
        this.sessionKey = session.get("sessionKey").toString();
        
        // call Get User API: get AvatarURL, first + last name, email, etc
        Map<String, Object> userParams = getUserAPI(session.get("userID").toString());
        this.user = new User(session, userParams);

        // set active a/c: default to live a/c
        Account active = null;
        if (! user.getLiveAccounts().isEmpty()) {
            active = user.getLiveAccounts().get(0);
        } else if (! user.getPracticeAccounts().isEmpty()) {
            active = user.getPracticeAccounts().get(0);
        }
        if (active != null) user.setActiveAccount(active);

        return new Pair<>(this.sessionKey, null);
    }

    public Pair<String, DriveWealth.Error> relogin () {
        return login(this.username, this.password);
    }

    public static Pair< Map<String, Object>, DriveWealth.Error> create (String userName, String password) {
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
        
        /*
        for (String k: INPUT_FIELDS) {
            if (params.containsKey(k)) {
                String v = params.get(k).toString();
                System.out.println("key: " + k + ", value: " + v);
            }
        }
        */

        Map<String, Object> respondMap = Http.post(url, params, null);
        String respond = respondMap.get("respond").toString();
        Map<String, Object> session  = new Gson().fromJson(respond, HashMap.class);

        DriveWealth.Error error = null;
        if ((int) respondMap.get("code") != 200) {
            error = DriveWealth.getError(session);
        }

        return new Pair<>(session, error);
    }

    public static boolean cancelSession () {
        String sessionKey = SessionManager.getInstance().getSessionKey();

        System.out.println("\n[Cancel Session] sessionKey: " + sessionKey);

        Map<String, Object> result = Http.delete("userSessions/" + sessionKey, sessionKey);
        int statusCode = (int) result.get("code");

        return statusCode == 200;
    }
    
    
    public static Map<String, Object> getUserAPI (String userID) {
        System.out.println("\n[getUser API]");
 
        List<String> OUTPUT_FIELDS = new ArrayList<>(Arrays.asList(
            "commissionRate",
            "emailAddress1",
            "languageID",
            "referralCode",
            "sessionKey",
            "username",
            "wlpID",
            "status",
            "lastLoginWhen",
            "ackSignedWhen",
            "addressLine1",
            "avatarUrl",
            "city",
            "coinBalance",
            "countryID",
            "displayName",
            "firstName",
            "lastName",
            "gender",
            "phoneHome",
            "stateProvince",
            "userID",
            "zipPostalCode",
            "usCitizen",
            "updatedWhen",
            "rebateCfdValue",
            "rebateEquityValue",
            "rebateFxValue",
            "brandAmbassador",
            "employerBusiness",
            "employmentStatus",
            "statementPrint",
            "confirmPrint",
            "citizenship",
            "createdWhen",
            "addressProofReviewWhen",
            "approvedWhen",
            "approvedBy",
            "kycWhen",
            "pictureReviewBy",
            "pictureReviewWhen",
            "annualIncome",
            "userAttributes"
        ));     

        Map<String, Object> respondMap = Http.get("users/" + userID, SessionManager.getInstance().getSessionKey());
        Map<String, Object> result  = new Gson().fromJson(respondMap.get("respond").toString(), HashMap.class);

        /*
        for (String k: OUTPUT_FIELDS) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        */

        return result;
    }
    
}
