/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;


import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.yccheok.jstock.engine.Pair;

import static org.yccheok.jstock.trading.API.SessionManager.Session;
import static org.yccheok.jstock.trading.API.SessionManager.User;
import static org.yccheok.jstock.trading.API.SessionManager.Account;


/**
 *
 * @author shuwnyuan
 */
public final class DriveWealth {

    private DriveWealth() {}

    /********************
     * API Functions
     ********************/

    static final List<String> accountFields = new ArrayList<>(Arrays.asList(
        "accountID",
        "accountNo",
        "userID",
        "currencyID",
        "accountType",
        "cash",
        "freeTradeBalance",
        "goodFaithViolations",
        "interestFree",
        "orders",
        "patternDayTrades",
        "positions",
        "status",
        "tradingType",
        "bodMoneyMarket",
        "bodEquityValue",
        "bodCashAvailForWith",
        "bodCashAvailForTrading",
        "rtCashAvailForTrading",
        "rtCashAvailForWith",
        "accountMgmtType",
        "longOnly"
    ));
    
    static final List<String> userFields = new ArrayList<>(Arrays.asList(
        "emailAddress1",
        "firstName",
        "lastName",
        "username",
        "password",
        "languageID",
        "tranAmount",
        "referralCode",
        "wlpID",
        "utm_campaign",
        "utm_content",
        "utm_medium",
        "utm_source",
        "utm_term"
    ));     

    static final List<String> getUserFields = new ArrayList<>(Arrays.asList(
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
    
    static final List<String> liveAccountFields = new ArrayList<>(Arrays.asList(
        "ownershipType",
        "userID",
        "emailAddress1",
        "emailAddress2",
        "firstName",
        "lastName",
        "username",
        "password",
        "gender",
        "languageID",
        "phoneHome",
        "phoneWork",
        "phoneMobile",
        "addressLine1",
        "addressLine2",
        "city",
        "countryID",
        "stateProvince",
        "wlpID",
        "zipPostalCode",
        "dob",
        "maritalStatus",
        "idNo",
        "usCitizen",
        "referralCode",
        "tradingType",
        "citizenship",
        "utm_campaign",
        "utm_content",
        "utm_medium",
        "utm_source",
        "utm_term",
        "employerBusiness",
        "employerCompany",
        "employerAddressLine1",
        "employerAddressLine2",
        "employerCity",
        "employerStateProvince",
        "employerZipPostalCode",
        "employerCountryID",
        "employerIsBroker",
        "employmentPosition",
        "employmentStatus",
        "employmentYears",
        "annualIncome",
        "investmentObjectives",
        "investmentExperience",
        "networthLiquid",
        "networthTotal",
        "director",
        "directorOf",
        "politicallyExposed",
        "politicallyExposedNames",
        "riskTolerance",
        "disclosureAck",
        "disclosureRule14b",
        "ackCustomerAgreement",
        "ackSweep",
        "ackFindersFee",
        "ackForeignFindersFee",
        "ackMarketData",
        "ackSignedBy",
        "ackSignedWhen",
        "ackSigned",
        "accountMgmtType"
    ));
    
    static final List<String> userStatusFields = new ArrayList<>(Arrays.asList(
        "accountStatus",
        "accountReasonID",
        "accountReason",
        "accountApprovedWhen",
        "addressProofReviewWhen",
        "addressProofStatus",
        "addressProofReasonID",
        "addressProofReason",
        "idReviewWhen",
        "idStatus",
        "idReasonID",
        "idReason",
        "kycWhen",
        "kycStatus"
    ));
    
    static final List<String> updateUserFields = new ArrayList<>(Arrays.asList(
        "userID",
        "addressLine1",
        "addressLine2",
        "city",
        "countryID",
        "emailAddress1",
        "firstName",
        "lastName",
        "languageID",
        "phoneHome",
        "phoneWork",
        "phoneMobile",
        "stateProvince",
        "zipPostalCode"
    ));    
    
    static final List<String> resetPasswordFields = new ArrayList<>(Arrays.asList(
        "code",
        "passwordResetID",
        "password"
    ));

    static final List<String> getSessionFields = new ArrayList<>(Arrays.asList(
        "appTypeID",
        "appVersion",
        "commissionRate",
        "heartbeatWhen",
        "ipAddress",
        "loginState",
        "languageID",
        "loginWhen",
        "osType",
        "osVersion",
        "referralCode",
        "scrRes",
        "sessionKey",
        "userID",
        "wlpID",
        "accounts",
        "instruments"
    ));

    static final List<String> instrumentFields = new ArrayList<>(Arrays.asList(
        "name",
        "instrumentID",
        "currencyID",
        "exchangeID",
        "instrumentTypeID",
        "isLongOnly",
        "orderSizeMax",
        "orderSizeMin",
        "orderSizeStep",
        "rateAsk",
        "rateBid",
        "ratePrecision",
        "symbol",
        "tags",
        "tradeSatus",
        "tradingHours",
        "longOnly"
    ));

    static final List<String> getInstrumentFields = new ArrayList<>(Arrays.asList(
        "name",
        "instrumentID",
        "currencyID",
        "description",
        "exchangeID",
        "orderSizeMax",
        "orderSizeMin",
        "orderSizeStep",
        "rateAsk",
        "rateBid",
        "symbol",
        "tags",
        "tradeStatus",
        "tradingHours",
        "urlImage",
        "urlInvestor",
        "fundamentalDataModel",
        "sector",
        "longOnly",
        "instrumentTypeID",
        "ratePrecision",
        "lastTrade"
    ));

    static final List<String> settingFields = new ArrayList<>(Arrays.asList(
        "userID",
        "key",
        // only for create setting, list all settings
        "value"
    ));

    static final List<String> chartFields = new ArrayList<>(Arrays.asList(
        "instrumentID",
        "compression",
        "dateStart",
        "dateEnd",
        "tradingDay"
    ));

    static final List<String> financialTxnFields = new ArrayList<>(Arrays.asList(
        "accountAmount",
        "accountBalance",
        "comment",
        "currencyID",
        "finTranID",
        "finTranTypeID",
        "orderID",
        "orderNo",
        "systemAmount",
        "tranAmount",
        "tranWhen",
        "wlpAmount",
        "execID",
        "dnb"
    ));
    
    static final List<String> orderTxnFields = new ArrayList<>(Arrays.asList(
        "orderNo",
        "transactTime",
        "execType",
        "ordStatus",
        "ordType",
        "side",
        "symbol",
        "lastShares",
        "lastPx",
        "cumQty",
        "leavesQty"
    ));

    static final List<String> openPosFields = new ArrayList<>(Arrays.asList(
        "costBasis",
        "initQty",
        "side",
        "openQty",
        "instrument",
        "price",
        "mtm",
        "mtmPL"
    ));
    
    static final List<String> stockFields = new ArrayList<>(Arrays.asList(
        "instrument",
        "instrumentTypeDescr",
        "tradeStatusDescr",
        "tagDescr",
        "linkable10K"
    ));

    static final List<String> stockInstrumentFields = new ArrayList<>(Arrays.asList(
        "currencyID",
        "instrumentID",
        "limitStatus",
        "instrumentTypeID",
        "marginCurrencyID",
        "name",
        "orderSizeMax",
        "orderSizeMin",
        "orderSizeStep",
        "symbol",
        "tradeStatus",
        "urlInvestor",
        "priorClose",
        "marketState",
        "minTic",
        "pipMultiplier",
        "rebateSpread"
    ));
    
    static final List<String> listStatementFields = new ArrayList<>(Arrays.asList(
        "displayName",  // Display name of the file
        "fileKey"       // Key to access the file via "Get Statements"
    ));
    
    static final List<String> getStatementFields = new ArrayList<>(Arrays.asList(
        "accountID",
        "url"           // URL to download statement directly. Note: this url will expire in 30 minutes.
    ));
    
    /********************
     * API: Accounts
     ********************/

    
    
    public static Map<String, Object> getAccount(String userID, String accountID) {
        System.out.println("\n[getAccount] " + accountID);
        
        Map<String, Object> respondMap = Http.get("users/" + userID + "/accounts/" + accountID, getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> account = new HashMap<>();
        for (String k: accountFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                account.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return account;
    }
    
    public static List<Map<String, Object>> listAllAccounts(String userID) {
        System.out.println("\n[listAllAccounts]");
        
        Map<String, Object> respondMap = Http.get("users/" + userID + "/accounts", getSessionKey());
        List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);
        List<Map<String, Object>> accounts = new ArrayList<>();
        
        for (Map<String, Object> a : result) {
            Map<String, Object> accMap = new HashMap<>();
            for (String k: accountFields) {
                if (a.containsKey(k)) {
                    Object v = a.get(k);
                    accMap.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
            accounts.add(accMap);
            
            String accountID = accMap.get("accountID").toString();
            String accountNo = accMap.get("accountNo").toString();
            double accountTypeID = (double) accMap.get("accountType");
            double cash = (double) accMap.get("cash");

            // accountTypeID: 1 => Practice a/c, 2 => Live a/c
            String accountType = (accountTypeID == 1) ? "Practice" : "Live";

            System.out.println("accountID: " + accountID + ", accountNo: " + accountNo + ", accountType: " + accountType + ", cash" + cash);
        }
        return accounts;
    }

    /* API endpoint: "signups/practice"
        if userMap not exists, POST creates userMap + practice a/c
        if userMap exist but no practice a/c, POST with userID creates practice a/c
        if userMap & practice accMap exist, POST with userID returns existing accountID
    */

    public static Account createPracticeAccount(Map<String, Object> args) {
        System.out.println("\n[create Practice Account]");
        String url = "signups/practice";
        
        if (args.containsKey("userID")) {
            // userMap already exist, create practice a/c
            String userID = args.get("userID").toString();
            
            // existing userMap requires SignIn: to create SessionKey
            if (SessionManager.getInstance().getSession() == null) {
                System.out.println("Please Sign In, userID: " + userID);
                return null;
            }

            // practice acc exists
            List<Account> accs = SessionManager.getInstance().getUser().getPracticeAccounts();
            Account acc = accs.get(0);
            
            if (acc != null) {
                System.out.println("Practice a/c exists: accountID: " + acc.getAccountID() + ", accountNo: " + acc.getAccountNo());
                return acc;
            }

            // create practice accMap
            Map<String, Object> params = new HashMap<>();
            params.put("userID", userID);

            Map<String, Object> respondMap = Http.post(url, params, getSessionKey());
            String respond = respondMap.get("respond").toString();
            Map<String, Object> result = gson.fromJson(respond, HashMap.class);

            String accountID = result.get("accountID").toString();
            System.out.println("user already exist, created practice accountID: " + accountID);

            // Login to create sessionMap
            Pair<Session, Error> login = SessionManager.getInstance().relogin();

            if (login.second != null) {
                return null;
            }

            return SessionManager.getInstance().getUser().getPracticeAccounts().get(0);
        }

        // create new userMap + practice a/c
        System.out.println("create User + Practice a/c + funded");

        Map<String, Object> params = new HashMap<>();
        for (String k: userFields) {
            if (args.containsKey(k)) {
                Object v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        Map<String, Object> respondMap = Http.post(url, params, null);

        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = gson.fromJson(respond, HashMap.class);
        int statusCode = (int) respondMap.get("code");

        Account acc = null;
        
        // status code: 400 => duplicate username, 200 => OK
        if (statusCode == 200) {
            System.out.println("New user + practice a/c created");
            
            String userName = result.get("username").toString();
            String password = result.get("password").toString();
            
            // Create sessionMap for new User
            Pair<Session, Error> login = SessionManager.getInstance().login(userName, password);
            // error
            if (login.second != null) {
                return null;
            }

            acc = SessionManager.getInstance().getUser().getPracticeAccounts().get(0);
        } else {
            Error error = getError(result);
            Double code = error.getCode();
            String message = error.getMessage();

            System.out.println("ERROR createPracticeAccount, code: " + code + ", message: " + message);
        }

        return acc;
    }

    public static Map<String, Object> createLiveAccount(Map<String, Object> args) {
        // userMap already exist, create live accMap
        System.out.println("\n[Create Live Account]");

        Map<String, Object> params = new HashMap<>();
        for (String k: liveAccountFields) {
            if (args.containsKey(k)) {
                Object v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        Map<String, Object> respondMap = Http.post("signups/live", params, getSessionKey());
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = gson.fromJson(respond, HashMap.class);
        
        Map<String, Object> liveAc = new HashMap<>();
        liveAc.put("useID", result.get("userID"));
        liveAc.put("username", result.get("username"));
        
        return liveAc;
    }

    public static boolean addDocument (Map<String, Object> args) {
        System.out.println("\n[add Document]\n\n\n");

        String userID = args.get("userID").toString();
        String documentType = args.get("documentType").toString();
        File file = (File) args.get("file");
        String fileFormat = args.get("fileFormat").toString();

        Part[] parts = new Part[3];
        parts[0] = new StringPart("token", userID);
        parts[1] = new StringPart("documentType", documentType);

        try {
            parts[2] = new FilePart("documentImage", file, fileFormat, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DriveWealth.class.getName()).log(Level.SEVERE, null, ex);
        }

        PostMethod postMethod = new PostMethod(hostURL + "documents");
        postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
        postMethod.addRequestHeader("x-mysolomeo-session-key", getSessionKey());
        Map<String, Object> result = Http.executeCall(postMethod);
        
        return (int) result.get("code") == 200;
    }
    
    /********************
     * API: Users
     ********************/

    public static Map<String, Object> createUser(Map<String, String> args) {
        System.out.println("\n[create User Only]");

        Map<String, Object> params = new HashMap<>();
        for (String k: userFields) {
            if (k.equals("tranAmount")) {
                continue;
            }
            
            if (args.containsKey(k)) {
                Object v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        
        Map<String, Object> respondMap = Http.post("signups/live", params, null);
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = gson.fromJson(respond, HashMap.class);
        int code = (int) result.get("code");
        
        Map<String, Object> userMap = new HashMap<>();
        
        if (code == 200) {
            userMap.put("password", result.get("password").toString());
            userMap.put("userID", result.get("userID").toString());
            userMap.put("username", result.get("username").toString());
        } else if (code == 400) {
            System.out.println("create user, error: " + result.get("message").toString());
        }
        return userMap;
    }
    
    // NOTE:
    // temporary rename to "getUserAPI" as method name clash with "getUser" which return API.User Object
    public static Map<String, Object> getUserAPI(String userID) {
        System.out.println("\n[getUser]");
        
        Map<String, Object> respondMap = Http.get("users/" + userID, getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> userMap = new HashMap<>();
        for (String k: getUserFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                userMap.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return userMap;
    }
    
    public static boolean checkUserNameAvailability(String username) {
        System.out.println("\n[checkUserNameAvailability]");

        Map<String, Object> respondMap = Http.get("users?username=" + username, null);
        int statusCode  = (int) respondMap.get("code");

        // 404 : username not found => available
        // 200 : username found => unavailable
        return statusCode == 404;
    }
    
    public static Map<String, Object> userStatus(String userID) {
        System.out.println("\n[userStatus]");
        
        Map<String, Object> respondMap = Http.get("users/" + userID + "/status", getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> status = new HashMap<>();
        for (String k: userStatusFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                status.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        // approved: accountStatus, addressProofStatus, idStatus, kycStatus = 2
        double accountStatus        = (double) result.get("accountStatus");
        double addressProofStatus   = (double) result.get("addressProofStatus");
        double idStatus             = (double) result.get("idStatus");
        double kycStatus            = (double) result.get("kycStatus");

        boolean approved = false;
        if (accountStatus == 2 && addressProofStatus == 2 && idStatus == 2 && kycStatus == 2) {
            approved = true;
        }
        System.out.println("\n[User Status] Approved status: " + approved + ", accountStatus: " + accountStatus
            + ", addressProofStatus: " + addressProofStatus + ", idStatus: " + idStatus
            + ", kycStatus: " + kycStatus);

        return status;
    }

    public static boolean updateUser(Map<String, String> args) {
        System.out.println("\n[updateUser]");
        
        String userID = args.get("userID");

        Map<String, Object> params = new HashMap<>();
        for (String k: updateUserFields) {
            if (args.containsKey(k)) {
                String v = args.get(k);
                
                if (k.equals("languageID")) {
                    List<String> languages = new ArrayList<>(Arrays.asList("en_US", "zh_CN", "es_ES", "pt_BR", "ja_JP"));

                    // default to en_US, for invalid languages set
                    if (!languages.contains(v)) {
                        v = "en_US";
                    }
                }
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        
        Map<String, Object> respondMap = Http.put("users/" + userID, params, getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        return (int) respondMap.get("code") == 200;
    }
    
    public static String forgotPassword(String username) {
        System.out.println("\n[Forgot password]");
        
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        Map<String, Object> respondMap = Http.post("users/passwords", params, null);
        
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = gson.fromJson(respond, HashMap.class);
        
        String passwordResetID = null;
        if ((int) respondMap.get("code") == 200) {
            passwordResetID = result.get("passwordResetID").toString();
            System.out.println("Forgot password, passwordResetID: " + passwordResetID);
        }
        return passwordResetID;
    }
    
    public static boolean resetPassword (Map<String, String> args) {
        System.out.println("\n[Reset password]");

        String passwordResetID = args.get("passwordResetID");
        Map<String, Object> params = new HashMap<>();
        for (String k: resetPasswordFields) {
            if (args.containsKey(k)) {
                String v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        Map<String, Object> respondMap = Http.put("users/passwords/" + passwordResetID, params, null);
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        return (int) respondMap.get("code") == 200;
    }

    /********************
     * API: Session
     ********************/
    
    public static Map<String, Object> getSession(String sessionKey) {
        System.out.println("\n[Get Session]");

        Map<String, Object> respondMap = Http.get("userSessions/" + sessionKey, getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);
        
        double loginState = (double) result.get("loginState");
        boolean login = false;
        if (loginState == 1) {
            login = true;
        }
        System.out.println("Get Session, login state: " + login);

        Map<String, Object> sessionMap = new HashMap<>();
        for (String k: getSessionFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                sessionMap.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return sessionMap;
    }
    
    public static boolean cancelSession(String sessionKey) {
        System.out.println("\n[Cancel Session] sessionKey: " + sessionKey);

        Map<String, Object> result = Http.delete("userSessions/" + sessionKey, getSessionKey());
        int statusCode = (int) result.get("code");

        return statusCode == 200;
    }
    
    /********************
     * API: Instruments
     ********************/
    
    public static List<Map<String, Object>> listAllInstruments() {
        System.out.println("\n[listAllInstruments]");

        Map<String, Object> respondMap = Http.get("instruments?tradeStatus=1", getSessionKey());
        List<Map<String, Object>> instruments = new ArrayList<>();
        
        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);

            for (Map<String, Object> i : result) {
                Map<String, Object> instrument = new HashMap<>();
                for (String k: instrumentFields) {
                    if (i.containsKey(k)) {
                        Object v = i.get(k);
                        instrument.put(k, v);
                        //System.out.println("key: " + k + ", value: " + v);
                    }
                }
                instruments.add(instrument);
            }
        }
        return instruments;
    }

    public static Map<String, Object> getInstrument (String instrumentID) {
        System.out.println("\n[getInstrument]");

        Map<String, Object> respondMap = Http.get("instruments/" + instrumentID + "?options=F", getSessionKey());
        Map<String, Object> instrument = new HashMap<>();
        
        if ((int) respondMap.get("code") == 200) {
            Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

            for (String k: getInstrumentFields) {
                if (result.containsKey(k)) {
                    Object v = result.get(k);
                    instrument.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
        }
        return instrument;
    }

    /********************
     * API: Orders
     ********************/

    public static boolean cancelOrder (String orderID) {
        System.out.println("\n[Cancel order]: " + orderID);

        Map<String, Object> result = Http.delete("orders/" + orderID, getSessionKey());
        int statusCode = (int) result.get("code");

        return statusCode == 200;
    }

    /********************
     * API: Settings
     ********************/

    public static Map<String, Object> getSetting (String key) {
        System.out.println("\n[Get Setting]");

        String url = "users/" + SessionManager.getInstance().getUser().getUserID() + "/settings/" + key;
        
        Map<String, Object> respondMap = Http.get(url, getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);
        Map<String, Object> setting = new HashMap<>();

        for (String k: settingFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                setting.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return setting;
    }
    
    public static List<Map<String, Object>> listAllSettings () {
        System.out.println("\n[List all Settings]");

        String url = "users/" + SessionManager.getInstance().getUser().getUserID() + "/settings";
        
        Map<String, Object> respondMap = Http.get(url, getSessionKey());
        List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);
        List<Map<String, Object>> settings = new ArrayList<>();
        
        for (Map<String, Object> a : result) {
            Map<String, Object> setting = new HashMap<>();
            for (String k: settingFields) {
                if (a.containsKey(k)) {
                    Object v = a.get(k);
                    setting.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
            settings.add(setting);
        }
        return settings;
    }

    public static Map<String, Object> createSetting (Map<String, String> args) {
        System.out.println("\n[Create Setting]");

        String userID = SessionManager.getInstance().getUser().getUserID();
        String url = "users/" + userID + "/settings";
        
        Map<String, Object> params = new HashMap<>();
        params.put("userID", userID);
        params.put("key", args.get("key"));
        params.put("value", args.get("value"));
        
        Map<String, Object> respondMap = Http.post(url, params, getSessionKey());
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = gson.fromJson(respond, HashMap.class);

        Map<String, Object> setting = new HashMap<>();
            
        for (String k: settingFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                setting.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return setting;
    }
    
    public static boolean deleteSetting (String key) {
        System.out.println("\n[Delete Setting]");

        String url = "users/" + SessionManager.getInstance().getUser().getUserID() + "/settings/";
        Map<String, Object> result = Http.delete(url + key, getSessionKey());
        int statusCode = (int) result.get("code");

        return statusCode == 200;
    }
    
    /********************
     * API: Charts
     ********************/

    public static enum ChartCompression {
        DAILY(0),
        ONE_MINUTE(1),
        TWO_MINUTE(2),
        THREE_MINUTE(3),
        FIVE_MINUTE(4),
        TEN_MINUTE(5),
        FIFTEEN_MINUTE(6),
        TWENTY_MINUTE(7),
        THIRTY_MINUTE(8),
        ONE_HOUR(9),
        TWO_HOUR(102),
        FOUR_HOUR(14),
        EIGHT_HOUR(108),
        WEEKLY(10),
        MONTHLY(11),
        YEARLY(12);
        
        private final int value;

        private ChartCompression(int value) {
           this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }

    public static List<String[]> getCharts (ChartCompression chartCompression, Map<String, Object> args) {
        System.out.println("\n[get Charts]");

        // required fields: instrumentID, compression
        // if tradingDays set, dateStart & dateEnd are ignored
        
        int compression = chartCompression.getValue();
        String instrumentID = args.get("instrumentID").toString();
        
        String url = "bars?instrumentID=" + instrumentID + "&compression=" + compression;

        if (args.containsKey("dateStart") && args.containsKey("dateEnd")) {
            String dateStart = args.get("dateStart").toString();
            String dateEnd = args.get("dateEnd").toString();
            url = url + "&dateStart=" + dateStart + "&dateEnd=" + dateEnd;
        } else if (args.containsKey("tradingDays")) {
            url = url + "&tradingDays=" + args.get("tradingDays").toString();
        }
        System.out.println("\n url: " + url);
        
        Map<String, Object> respondMap = Http.get(url, getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        List<String[]> data = new ArrayList<>();
        
        if (   result.get("instrumentID").toString().equals(instrumentID)
            && (int) result.get("compression") == compression)
        {
            // Format: date_time, open, high, low, close, volume
            // 2016-01-11T14:49:00Z,7.34,7.34,7.34,7.34,100|2016-01-11T15:05:00Z,7.33,7.33,7.33,7.33,100
            String line = result.get("data").toString();
            System.out.println("data: " + line);

            String[] items = line.split("\\|");
            
            for (int i=0; i<items.length; i++) {
                String[] ohlc = items[i].split(",");
                data.add(ohlc);
            }
        }
        return data;
    }
    
    /********************
     * API: Reports
     ********************/

    // Reports TODO: Referral Summary
    
    public static enum ReportName {
        FIN_TRANS("FinTrans"),
        ORDER_TRANS("OrderTrans"),
        POSITION_RESTING_ORDER("PositionRestingOrder"),
        INSTRUMENT("Instrument"),
        REFERRAL_SUMMARY_PERFORMANCE("ReferralSummaryPerformance");

        private final String value;

        private ReportName(String value) {
           this.value = value;
        }
        
        public String getValue () {
            return this.value;
        }
    }

    /*
        "transactionReport" covers 3 types:

        1) Financial Transaction
            This report allows users to generate all of their financial transactions for their specified accMap.
        2) Order Transaction
            This report allows users to generate order related transactions for the specified accMap number
            over the specified date interval. These transactions can be also filtered by symbol if desired.
        3) Open Positions and Resting Orders
            Provides the userMap their current open positions and resting orders.
    */
    
    public static List<Map<String, Object>> transactionReport (ReportName reportName, Map<String, String> args) {
        System.out.println("\n[Transaction Report] - " + reportName);

        String AccountNumber = args.get("AccountNumber");
        String DateStart = "";
        String DateEnd = "";
        String symbol = "";
        List<String> txnFields;

        switch (reportName) {
            case FIN_TRANS:
                DateStart = "&DateStart=" + args.get("DateStart");
                DateEnd = "&DateEnd=" + args.get("DateEnd");

                txnFields = financialTxnFields;
                break;
            case ORDER_TRANS:
                DateStart = "&DateStart=" + args.get("DateStart");
                DateEnd = "&DateEnd=" + args.get("DateEnd");

                // for Order Transaction, "symbol" is optional
                if (args.containsKey("symbol")) {
                    symbol = "&symbol=" + args.get("symbol");
                }

                txnFields = orderTxnFields;
                break;
            case POSITION_RESTING_ORDER:
                txnFields = openPosFields;
                break;
            default:
                System.out.println("Unsupported reportType: " + reportName.getValue());
                return null;
        }
        
        String url = "DriveWealth?ReportFormat=JSON&wlpID=DW&LanguageID=en_US"
                + "&ReportName="    + reportName.getValue()
                + "&sessionKey="    + getSessionKey()
                + "&AccountNumber=" + AccountNumber
                + DateStart
                + DateEnd
                + symbol;

        System.out.println("URL: " + url);
        
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> respondMap = Http.post(url, params, getSessionKey(), "reportServer");

        int statusCode = (int) respondMap.get("code");
        if (statusCode != 200) {
            System.out.println("Error status code: " + statusCode);
            return null;
        }

        String respond = respondMap.get("respond").toString();
        Map<String, Object> result;
        try {
            result = gson.fromJson(respond, HashMap.class);
        } catch(com.google.gson.JsonSyntaxException ex) { 
            System.out.println("Error respond: " + respond);
            return null;
        }
        
        String accountNo = result.get("accountNo").toString();
        String accountID = result.get("accountID").toString();
        String dateRange = null;
        String accountType = null;
        
        if (result.containsKey("accountType")) {
            accountType = result.get("accountType").toString();
        }
        if (result.containsKey("dateRange")) {
            dateRange = result.get("dateRange").toString();
        }
        
        System.out.println("accountNo: " + accountNo + ", accountID: " + accountID
                + ", accountType: " + accountType + ", dateRange: " + dateRange);


        List<Map<String, Object>> resultTxn;
        if (reportName == ReportName.POSITION_RESTING_ORDER) {
            resultTxn = (ArrayList) result.get("positions");
        } else {
            resultTxn = (ArrayList) result.get("transaction");
        }

        // convert List node from LinkedTreeMap -> HashMap
        List<Map<String, Object>> txns = new ArrayList<>();
        int cnt = 0;
        for (Map<String, Object> a : resultTxn) {
            System.out.println("\n\n txn [" + cnt++ + "]\n\n");

            Map<String, Object> txn = new HashMap<>();
            for (String k: txnFields) {
                if (a.containsKey(k)) {
                    Object v = a.get(k);
                    txn.put(k, v);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }
            txns.add(txn);
        }
        return txns;
    }

    // View all instruments available on the DriveWealth platform
    public static List<Map<String, Object>> stocksReport (Map<String, String> args) {
        System.out.println("\n[Stocks, ETFs and ADRs Report]");

        String url = "DriveWealth?ReportFormat=JSON&wlpID=DW&LanguageID=en_US"
                + "&ReportName="    + ReportName.INSTRUMENT.getValue()
                + "&sessionKey="    + getSessionKey()
                + "&AccountNumber=" + args.get("AccountNumber")
                + "&DateStart="     + args.get("DateStart")
                + "&DateEnd="       + args.get("DateEnd")
                // All: -1, Inactive: 0, Active: 1
                + "&TradeStatus=-1"
                // All: -1, Stocks: 6, ETFs: 7
                + "&InstrumentType=-1";

        System.out.println("URL: " + url);

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> respondMap = Http.post(url, params, getSessionKey(), "reportServer");

        int statusCode = (int) respondMap.get("code");
        if (statusCode != 200) {
            System.out.println("Error status code: " + statusCode);
            return null;
        }

        String respond = respondMap.get("respond").toString();
        List<Map<String, Object>> result;
        try {
            result = (ArrayList) gson.fromJson(respond, HashMap.class).get("instruments");
        } catch(com.google.gson.JsonSyntaxException ex) { 
            System.out.println("Error respond: " + respond);
            return null;
        }

        List<Map<String, Object>> stocks = new ArrayList<>();
        int cnt = 0;
        for (Map<String, Object> a : result) {
            System.out.println("\n\n stock [" + cnt++ + "]\n\n");

            Map<String, Object> stock = new HashMap<>();
            for (String k: stockFields) {
                if (a.containsKey(k)) {
                    Object v = a.get(k);
                    stock.put(k, v);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }
            
            // convert instrument from LinkedTreeMap -> HashMap
            Map<String, Object> ins = (Map<String, Object>) stock.get("instrument");
            Map<String, Object> instrument = new HashMap<>();

            for (String k: stockInstrumentFields) {
                if (ins.containsKey(k)) {
                    Object v = ins.get(k);
                    instrument.put(k, v);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }
            stock.put("instrument", instrument);
            stocks.add(stock);
        }
        return stocks;
    }

    /********************
     * API: Statements
     ********************/

    public static List<Map<String, Object>> listStatements (String type, Map<String, String> args) {
        switch (type) {
            // List trade confirms
            case "trade":
                type = "01";
                break;
            // List statements
            case "statement":
                type = "02";
                break;
            // List 1099-B Documents
            case "doc":
                type = "03";
                break;
            default:
                System.out.println("Unknown listStatements - type: " + type);
                return null;
        }

        String url = "statements?accountID=" + args.get("accountID")
                + "&startDate=" + args.get("startDate")
                + "&endDate="   + args.get("endDate")
                + "&type="      + type;

        System.out.println("URL: " + url);

        Map<String, Object> respondMap = Http.get(url, getSessionKey());
        List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);
        List<Map<String, Object>> statements = new ArrayList<>();
        
        for (Map<String, Object> a : result) {
            Map<String, Object> statement = new HashMap<>();
            for (String k: listStatementFields) {
                if (a.containsKey(k)) {
                    Object v = a.get(k);
                    statement.put(k, v);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }
            statements.add(statement);
        }
        return statements;
    }
    
    public static Map<String, Object> getStatement (String accountID, String fileKey) {
        String url = "statements/" + accountID + "/" + fileKey;
        System.out.println("URL: " + url);

        Map<String, Object> respondMap = Http.get(url, getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> statement = new HashMap<>();
        for (String k: getStatementFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                statement.put(k, v);
                System.out.println("key: " + k + ", value: " + v);
            }
        }                
        return statement;
    }
    
    
    
    /************************
     * API Utility Functions
     ************************/
    
    public static String getSessionKey() {
        return SessionManager.getInstance().getSessionKey();
    }
    
    /************************
     * Http Helper functions
     ************************/

    public static class Error {
        private final Double code;
        private final String message;
        
        public Error (Double code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public Double getCode () {
            return this.code;
        }
        
        public String getMessage () {
            return this.message;
        }
    }
    
    public static Error getError (Map<String, Object> result) {
        if (result.containsKey("code") && result.containsKey("message")) {
            return new Error(Double.parseDouble(result.get("code").toString()), result.get("message").toString());
        }
        return null;
    }
    
    /*****************
     * Variables
     *****************/

    private static final Gson gson = new Gson();
    public static String hostURL = "https://api.drivewealth.io/v1/";
    public static String reportURL = "http://reports.drivewealth.io/";
}
