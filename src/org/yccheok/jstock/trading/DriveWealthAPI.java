/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;


/**
 *
 * @author shuwnyuan
 */
public class DriveWealthAPI {

    public DriveWealthAPI(Map<String, String>params) {
        _initUserSession(params.get("username"), params.get("password"));
    }

    public DriveWealthAPI() {
        this.user = new User();
    }

    private void _initUserSession(String username, String password) {
        this.user = new User(username, password);
        this.getSessionKey();
    }
    
    /********************
     * API Functions
     ********************/

    static final List<String> accountBlotterFields = new ArrayList<>(Arrays.asList(
        "accountID",
        "accountNo",
        "equity",
        "cash",
        "orders",
        "transactions"
    ));
    
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

    static final List<String> createSessionFields = new ArrayList<>(Arrays.asList(
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

    static final List<String> sessionFields = new ArrayList<>(Arrays.asList(
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
        "ratePrecision"
    ));

    static final List<String> searchInstrumentFields = new ArrayList<>(Arrays.asList(
        "instrumentID",
        "name",
        "category",
        "currencyID",
        "exchangeID",
        "limitStatus",
        "instrumentTypeID",
        "isLongOnly",
        "marginCurrencyID",
        "orderSizeMax",
        "orderSizeMin",
        "orderSizeStep",
        "rateAsk",
        "rateBid",
        "ratePrecision",
        "symbol",
        "tags",
        "tradeStatus",
        "tradingHours",
        "uom",
        "urlImage",
        "urlInvestor",
        "sector",
        "longOnly"
    ));
    
    static final List<String> createOrderFields = new ArrayList<>(Arrays.asList(
        "symbol",
        "instrumentID",
        "accountID",
        "accountNo",
        "userID",
        "accountType",
        "ordType",
        "side",
        "orderQty",
        "comment",

        // for Market Order only
        "amountCash",
        "autoStop",

        // for Stop Order only
        "price",

        // for Limit Order only
        "limitPrice"
    ));
    
    static final List<String> orderFields = new ArrayList<>(Arrays.asList(
        "execID",
        "orderID",
        "cumQty",
        "instrumentID",
        "execType",
        "grossTradeAmt",
        "leavesQty",
        "ordType",
        "side",
        "ordStatus",
        "limitPrice",
        "timeInForce",
        "expireTimestamp",
        "statusPath"
    ));
    
    static final List<String> orderStatusFields = new ArrayList<>(Arrays.asList(
        "orderID",
        "accountID",
        "userID",
        "cumQty",
        "accountNo",
        "comment",
        "commission",
        "createdByID",
        "createdWhen",
        "executedWhen",
        "execType",
        "grossTradeAmt",
        "instrumentID",
        "leavesQty",
        "orderNo",
        "orderQty",
        "ordStatus",
        "ordType",
        "side",
        "accountType",
        "autoStop",
        "ordRejReason",

        // for Stop Order only
        "price",

        // for Limit Order only
        "limitPrice",
            
        // a) Market & Limit order are DAY order, not persist and are cancelled if not filled at end of exchange trading day.
        //      => timeInForce = 0 / null
        //
        // b) Stop order persists across trading days, are GOOD UNTIL CANCEL.
        //      => timeInforce = 1
        "timeInForce"
    ));
    
    static final List<String> marketDataFields = new ArrayList<>(Arrays.asList(
        "symbol",
        "bid",
        "ask"
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

    public Map<String, Object> accountBlotter(String userID, String accountID) {
        System.out.println("\n[Account Blotter] userID:" + userID + ", accountID:" + accountID);
        
        Map<String, Object> respondMap = executeGet("users/" + userID + "/accountSummary/" + accountID, this.getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> account = new HashMap<>();
        for (String k: this.accountBlotterFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                account.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        
/*
        LinkedTreeMap<String, Object> equity = (LinkedTreeMap) account.get("equity");
        
        System.out.println("Equity: equityValue: " + equity.get("equityValue")
                    + ", equityPositions: " + equity.get("equityPositions"));
        
        List<LinkedTreeMap<String, Object>> positions = (List) equity.get("equityPositions");
        int cnt = 0;
        for (LinkedTreeMap<String, Object> a : positions) {
            System.out.println("[" + cnt + "] Position: symbol: " + a.get("symbol")
                    + ", instrumentID: " + a.get("instrumentID")
                    + ", openQty: " + a.get("openQty")
                    + ", costBasis: " + a.get("costBasis"));
            cnt++;
        }

        LinkedTreeMap<String, Object> balance = (LinkedTreeMap) account.get("balance");
        System.out.println("balance: cashBalance: " + balance.get("cashBalance")
                    + ", cashAvailableForTrade: " + balance.get("cashAvailableForTrade"));
*/
        
        return account;
    }
            
    public Map<String, Object> getAccount(String userID, String accountID) {
        System.out.println("\n[getAccount] " + accountID);
        
        Map<String, Object> respondMap = executeGet("users/" + userID + "/accounts/" + accountID, this.getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> account = new HashMap<>();
        for (String k: this.accountFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                account.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return account;
    }
    
    public List<Map<String, Object>> listAllAccounts(String userID) {
        System.out.println("\n[listAllAccounts]");
        
        Map<String, Object> respondMap = executeGet("users/" + userID + "/accounts", this.getSessionKey());
        List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);
        List<Map<String, Object>> accounts = new ArrayList<>();
        
        for (Map<String, Object> a : result) {
            Map<String, Object> account = new HashMap<>();
            for (String k: this.accountFields) {
                if (a.containsKey(k)) {
                    Object v = a.get(k);
                    account.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
            accounts.add(account);
            
            String accountID = account.get("accountID").toString();
            String accountNo = account.get("accountNo").toString();
            Double accountTypeID = (Double) account.get("accountType");
            Double cash = (Double) account.get("cash");

            // accountTypeID: 1 => Practice a/c, 2 => Live a/c
            String accountType = (accountTypeID == 1) ? "Practice" : "Live";

            Account acc = this.new Account(account);
            if (accountTypeID == 1) {
                this.user.practiceAccount = acc;
            } else if (accountTypeID == 2) {
                this.user.liveAccount = acc;
            }
            
            if (!this.user.accountsMap.containsKey(accountID)) {
                this.user.accountsMap.put(accountID, acc);
            }

            
            System.out.println("accountID: " + accountID + ", accountNo: " + accountNo + ", accountType: " + accountType + ", cash" + cash);
        }
        return accounts;
    }

    /* API endpoint: "signups/practice"
        if user not exists, POST creates user + practice a/c
        if user exist but no practice a/c, POST with userID creates practice a/c
        if user & practice account exist, POST with userID returns existing accountID
    */

    public Account createPracticeAccount(Map<String, Object> args) {
        System.out.println("\n[create Practice Account]");
        String api = "signups/practice";
        
        if (args.containsKey("userID")) {
            // user already exist, create practice a/c
            String userID = args.get("userID").toString();
            
            // existing user requires SignIn: to create SessionKey
            if (this.user == null || !this.user.userID.equals(userID)) {
                System.out.println("Please Sign In, userID: " + userID);
                return null;
            }

            // practice acc exists
            if (this.user.practiceAccount != null) {
                Account acc = this.user.practiceAccount;
                System.out.println("Practice a/c exists: accountID: " + acc.accountID + ", accountNo: " + acc.accountNo);
                return acc;
            }

            // create practice account
            Map<String, Object> params = new HashMap<>();
            params.put("userID", userID);

            Map<String, Object> respondMap = executePost(api, params, this.getSessionKey());
            String respond = respondMap.get("respond").toString();
            Map<String, Object> result = gson.fromJson(respond, HashMap.class);

            String accountID = result.get("accountID").toString();
            System.out.println("user already exist, created practice accountID: " + accountID);

            this._initUserSession(this.user.username, this.user.password);
            
            return this.user.practiceAccount;
        } else {
            // create new user + practice a/c
            System.out.println("create User + Practice a/c + funded");

            Map<String, Object> params = new HashMap<>();
            for (String k: this.userFields) {
                if (args.containsKey(k)) {
                    Object v = args.get(k);
                    params.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
            Map<String, Object> respondMap = executePost(api, params, null);

            String respond = respondMap.get("respond").toString();
            Map<String, Object> result = gson.fromJson(respond, HashMap.class);
            int statusCode = (int) respondMap.get("code");

            Account acc = null;
            // status code: 400 => duplicate username, 200 => OK
            if (statusCode == 200) {
                this._initUserSession(result.get("username").toString(), result.get("password").toString());
                acc = this.user.practiceAccount;
                
                System.out.println("New user + practice a/c created");
            } else if (statusCode == 400) {
                System.out.println("User already exists, error: " + result.get("message").toString());
            }

            return acc;
        }
    }

    public Map<String, Object> createLiveAccount(Map<String, Object> args) {
        // user already exist, create live account
        System.out.println("\n[Create Live Account]");

        Map<String, Object> params = new HashMap<>();
        for (String k: this.liveAccountFields) {
            if (args.containsKey(k)) {
                Object v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        Map<String, Object> respondMap = executePost("signups/live", params, this.getSessionKey());
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = gson.fromJson(respond, HashMap.class);
        
        Map<String, Object> liveAc = new HashMap<>();
        liveAc.put("useID", result.get("userID"));
        liveAc.put("username", result.get("username"));
        
        return liveAc;
    }

    public boolean addDocument (Map<String, Object> args) {
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
            Logger.getLogger(DriveWealthAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        PostMethod postMethod = new PostMethod(hostURL + "documents");
        postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
        postMethod.addRequestHeader("x-mysolomeo-session-key", getSessionKey());
        Map<String, Object> result = executeHttpCall(postMethod);
        
        return (int) result.get("code") == 200;
    }
    
    /********************
     * API: Users
     ********************/

    public Map<String, Object> createUser(Map<String, String> args) {
        System.out.println("\n[create User Only]");

        Map<String, Object> params = new HashMap<>();
        for (String k: this.userFields) {
            if (k.equals("tranAmount")) {
                continue;
            }
            
            if (args.containsKey(k)) {
                Object v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        
        Map<String, Object> respondMap = executePost("signups/live", params, null);
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = gson.fromJson(respond, HashMap.class);
        Double code = (Double) result.get("code");
        
        Map<String, Object> user = new HashMap<>();
        
        if (code == 200) {
            user.put("password", result.get("password").toString());
            user.put("userID", result.get("userID").toString());
            user.put("username", result.get("username").toString());
        } else if (code == 400) {
            System.out.println("create user, error: " + result.get("message").toString());
        }
        return user;
    }
    
    public Map<String, Object> getUser(String userID) {
        System.out.println("\n[getUser]");
        
        Map<String, Object> respondMap = executeGet("users/" + userID, this.getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> user = new HashMap<>();
        for (String k: this.getUserFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                user.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return user;
    }
    
    public boolean checkUserNameAvailability(String username) {
        System.out.println("\n[checkUserNameAvailability]");

        Map<String, Object> respondMap = executeGet("users?username=" + username, null);
        int statusCode  = (int) respondMap.get("code");

        // 404 : username not found => available
        // 200 : username found => unavailable
        return statusCode == 404;
    }
    
    public Map<String, Object> userStatus(String userID) {
        System.out.println("\n[userStatus]");
        
        Map<String, Object> respondMap = executeGet("users/" + userID + "/status", this.getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> status = new HashMap<>();
        for (String k: this.userStatusFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                status.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        // approved: accountStatus, addressProofStatus, idStatus, kycStatus = 2
        Double accountStatus        = (Double) result.get("accountStatus");
        Double addressProofStatus   = (Double) result.get("addressProofStatus");
        Double idStatus             = (Double) result.get("idStatus");
        Double kycStatus            = (Double) result.get("kycStatus");

        boolean approved = false;
        if (accountStatus == 2 && addressProofStatus == 2 && idStatus == 2 && kycStatus == 2) {
            approved = true;
        }
        System.out.println("\n[User Status] Approved status: " + approved + ", accountStatus: " + accountStatus
            + ", addressProofStatus: " + addressProofStatus + ", idStatus: " + idStatus
            + ", kycStatus: " + kycStatus);

        return status;
    }

    public boolean updateUser(Map<String, String> args) {
        System.out.println("\n[updateUser]");
        
        String userID = args.get("userID").toString();

        Map<String, Object> params = new HashMap<>();
        for (String k: this.updateUserFields) {
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
        
        Map<String, Object> respondMap = executePut("users/" + userID, params, this.getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        return (int) respondMap.get("code") == 200;
    }
    
    public String forgotPassword(String username) {
        System.out.println("\n[Forgot password]");
        
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        Map<String, Object> respondMap = executePost("users/passwords", params, null);
        
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = gson.fromJson(respond, HashMap.class);
        
        String passwordResetID = null;
        if ((int) respondMap.get("code") == 200) {
            passwordResetID = result.get("passwordResetID").toString();
            System.out.println("Forgot password, passwordResetID: " + passwordResetID);
        }
        return passwordResetID;
    }
    
    public boolean resetPassword (Map<String, String> args) {
        System.out.println("\n[Reset password]");

        String passwordResetID = args.get("passwordResetID");
        Map<String, Object> params = new HashMap<>();
        for (String k: this.resetPasswordFields) {
            if (args.containsKey(k)) {
                String v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        Map<String, Object> respondMap = executePut("users/passwords/" + passwordResetID, params, null);
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        return (int) respondMap.get("code") == 200;
    }

    /********************
     * API: Session
     ********************/
    
    public Map<String, Object> getSession(String sessionKey) {
        System.out.println("\n[Get Session]");

        Map<String, Object> respondMap = executeGet("userSessions/" + sessionKey, this.getSessionKey());
        Map<String, Object> result  = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);
        
        Double loginState = (Double) result.get("loginState");
        boolean login = false;
        if (loginState == 1) {
            login = true;
        }
        System.out.println("Get Session, login state: " + login);

        Map<String, Object> session = new HashMap<>();
        for (String k: this.getSessionFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                session.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return session;
    }
    
    public Map<String, Object> createSession(Map<String, String> args) {
        System.out.println("\n[Create Session]");
 
        Map<String, Object> params = new HashMap<>();
        for (String k: this.createSessionFields) {
            if (args.containsKey(k)) {
                String v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        Map<String, Object> respondMap = executePost("userSessions", params, null);
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = gson.fromJson(respond, HashMap.class);

        Map<String, Object> session = new HashMap<>();
        if ((int) respondMap.get("code") == 200) {
            for (String k: this.sessionFields) {
                if (result.containsKey(k)) {
                    Object v = result.get(k);
                    session.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
        } else {
            session = this.getError(respondMap);
        }
        return session;
    }

    public boolean cancelSession(String sessionKey) {
        System.out.println("\n[Cancel Session] sessionKey: " + sessionKey);

        Map<String, Object> result = executeDelete("userSessions/" + sessionKey, this.getSessionKey());
        int statusCode = (int) result.get("code");

        if (statusCode == 200) {
            this.user.sessionKey = null;
            return true;
        }
        return false;
    }
    
    /********************
     * API: Instruments
     ********************/
    
    public List<Map<String, Object>> listAllInstruments() {
        System.out.println("\n[listAllInstruments]");

        Map<String, Object> respondMap = executeGet("instruments?tradeStatus=1", this.getSessionKey());
        List<Map<String, Object>> instruments = new ArrayList<>();
        
        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);

            for (Map<String, Object> i : result) {
                Map<String, Object> instrument = new HashMap<>();
                for (String k: this.instrumentFields) {
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

    public Map<String, Object> getInstrument (String instrumentID) {
        System.out.println("\n[getInstrument]");

        Map<String, Object> respondMap = executeGet("instruments/" + instrumentID + "?options=F", this.getSessionKey());
        Map<String, Object> instrument = new HashMap<>();
        
        if ((int) respondMap.get("code") == 200) {
            Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

            for (String k: this.getInstrumentFields) {
                if (result.containsKey(k)) {
                    Object v = result.get(k);
                    instrument.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
        }
        return instrument;
    }

    public List<Map<String, Object>> searchInstruments (Map<String, String> args) {
        System.out.println("\n[searchInstruments]");

        String params = null;
        final List<String> searchFields = new ArrayList<>(Arrays.asList(
            "symbol",
            "symbols",
            "name",
            "tag"
        ));

        for (String k: searchFields) {
            if (args.containsKey(k)) {
                String kv = k + "=" + args.get(k);
                if (params == null) {
                    params = kv;
                } else {
                    params += "&" + kv;
                }
            }
        }
        //System.out.println(params);

        Map<String, Object> respondMap = executeGet("instruments?" + params, this.getSessionKey());
        List<Map<String, Object>> instruments = new ArrayList<>();
        
        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);

            int cnt = 0;
            for (Map<String, Object> i : result) {
                Map<String, Object> instrument = new HashMap<>();
                for (String k: this.searchInstrumentFields) {
                    if (i.containsKey(k)) {
                        Object v = i.get(k);
                        instrument.put(k, v);
                        //System.out.println(cnt + ": key: " + k + ", value: " + v);
                    }
                }
                instruments.add(instrument);
                cnt++;
            }
        }
        return instruments;
    }

    /********************
     * API: Orders
     ********************/

    public Map<String, Object> createOrder (String action, String OrderType, Map<String, Object> args) {
        System.out.println("\n[create order]: " + action + ", " + OrderType);

        Map<String, Object> params = new HashMap<>();
        for (String k: this.createOrderFields) {
            if (args.containsKey(k)) {
                Object v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        final String instrumentID = args.get("instrumentID").toString();
        final Double orderQty = (Double) params.get("orderQty");
        final Double commission = this.user.commissionRate;
        
        // get Account for: balance, commission
        Map<String, Object> account = this.accountBlotter(this.user.userID, params.get("accountID").toString());
        
        // get Market Data for: bid, ask
        ArrayList<String> symbols = new ArrayList<>( Arrays.asList(params.get("symbol").toString()) );
        List<Map<String, Object>> dataArray = this.getMarketData(symbols);
        Map<String, Object> marketData = dataArray.get(0);
        
        Double rateAsk = (Double) marketData.get("ask");
        Double rateBid = (Double) marketData.get("bid");
        Double price = null;
        
        if (OrderType.equals("market")) {
            params.put("ordType", 1);
            
            if (action.equals("buy")) {
                price = rateAsk;
            } else {
                price = rateBid;
            }
        } else if (OrderType.equals("stop")) {
            params.put("ordType", 3);
            price = (Double) params.get("price");
            
            if (action.equals("buy")) {
                // Check price >= ask price + 0.05
                if (price.compareTo(rateAsk + 0.05) < 0) {
                    System.out.println("price must be >= ask price + 0.05, price[" + price + "], ask[" + rateAsk + "]");
                    return null;
                }
            } else {
                // Check price <= bid price - 0.05
                if (price.compareTo(rateBid - 0.05) > 0) {
                    System.out.println("price must be <= bid price - 0.05, price[" + price + "], bid[" + rateBid + "]");
                    return null;
                }
            }
        } else if (OrderType.equals("limit")) {
            // BUY will execute in the market when the market ask price is at or below the order limit price
            // If the price entered is above the current ask price, order will be immediately executed

            // SELL will execute in the market when the market bid price is at or above the order limit price
            // If the price entered is below the current ask price, order will be immediately executed
            params.put("ordType", 2);
            price = (Double) params.get("limitPrice");
        } else {
            System.out.println("invalid order type: " + OrderType);
            return null;
        }
        
        Map<String, Object> order = new HashMap<>();
        
        if (action.equals("buy")) {
            params.put("side", "B");
            
            Double amount = orderQty * price + commission;
            
            LinkedTreeMap<String, Object> cashObj = (LinkedTreeMap) account.get("cash");
            Double balance = (Double) cashObj.get("cashAvailableForTrade");

            System.out.println("cash: Balance: " + cashObj.get("cashBalance")
                        + ", AvailableForTrade: " + cashObj.get("cashAvailableForTrade"));

            // Check for insufficient balance
            System.out.println("Check balance: amount: " + amount + ", balance: " + balance
                    + ", price: " + price + ", Qty: " + params.get("orderQty")
                    + ", ask: " + rateAsk + ", bid: " + rateBid
                    + ", commission: " + commission);

            if (balance.compareTo(amount) < 0) {
                System.out.println("Insufficient balance");
                return null;
            }
        } else if (action.equals("sell")) {
            params.put("side", "S");
            
            // check available Qty
            List<LinkedTreeMap<String, Object>> positions = (List) ((LinkedTreeMap) account.get("equity")).get("equityPositions");

            Double availQty = null;
            for (LinkedTreeMap<String, Object> i : positions) {
                if (i.get("instrumentID").toString().equals(instrumentID)) {
                    availQty = (Double) i.get("availableForTradingQty");
                    break;
                }
            }

            System.out.println("price: " + price + ", ask: " + rateAsk
                    + ", bid: " + rateBid + ", availableForTradingQty: " + availQty
                    + ", orderQty: " + orderQty);

            if (availQty.compareTo(orderQty) < 0) {
                System.out.println("Insufficient Qty to sell");
                return null;
            }
        } else {
            System.out.println("invalid action: " + action);
            return null;
        }
        
        // create order
        Map<String, Object> respondMap = executePost("orders", params, this.getSessionKey());
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = gson.fromJson(respond, HashMap.class);

        for (String k: this.orderFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                order.put(k, v);
                System.out.println("key: " + k + ", value: " + v);
            }
        }

        return order;
    }
    
    public boolean cancelOrder (String orderID) {
        System.out.println("\n[Cancel order]: " + orderID);

        Map<String, Object> result = executeDelete("orders/" + orderID, this.getSessionKey());
        int statusCode = (int) result.get("code");

        return statusCode == 200;
    }

    
    public Map<String, Object> orderStatus (String orderID) {
        System.out.println("\n[Order Status]");

        Map<String, Object> respondMap = executeGet("orders/" + orderID, this.getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);
        Map<String, Object> status = new HashMap<>();

        for (String k: this.orderStatusFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                status.put(k, v);
                System.out.println("key: " + k + ", value: " + v);
            }
        }
        
        final Double cumQty     = (Double) status.get("cumQty");
        final Double leavesQty  = (Double) status.get("leavesQty");
        final Double orderQty   = (Double) status.get("orderQty");

        // accepted
        if (    orderQty.compareTo(leavesQty) == 0
                && status.get("execType").equals("0")
                && status.get("ordStatus").toString().equals("0")
        ) {
            System.out.println("Order accepted: " + orderID);
        }
        // filled
        else if (  orderQty.compareTo(cumQty) == 0
                && status.get("execType").equals("2")
                && status.get("ordStatus").toString().equals("2")
        ) {
            System.out.println("Order filled: " + orderID);
        }
        // partially filled
        else if (  orderQty.compareTo(cumQty) < 0
                && status.get("execType").equals("1")
                && status.get("ordStatus").toString().equals("1")
        ) {
            System.out.println("Order partially filled: " + orderID);
        }
        // Cancelled
        else if (  orderQty.compareTo(leavesQty) == 0
                && status.get("execType").equals("4")
                && status.get("ordStatus").toString().equals("4")
                
        ) {
            System.out.println("Order cancelled: " + orderID);
        }
        // Rejected
        else if (  leavesQty.compareTo(0.0) == 0
                && status.get("execType").equals("8")
                && status.get("ordStatus").toString().equals("8")
        ) {
            System.out.println("Order cancelled: " + orderID + ", reason: "
                    + status.get("ordRejReason").toString());
        }

        return status;
    }
    
    /************************
     * Market Data
     ************************/
    
    public List<Map<String, Object>> getMarketData (ArrayList<String> args) {
        System.out.println("\n[Get Market Data]");

        String symbols = null;
        for (int i = 0; i < args.size(); i++) {
	    String symbol = args.get(i);
            
            if (i == 0) {
                symbols = symbol;
            } else {
                symbols = symbols + "," + symbol;
            }
	    System.out.println(i + ": symbol: " + symbol);
	}
        System.out.println("symbols: " + symbols);
        
        Map<String, Object> respondMap = executeGet("quotes?symbols=" + symbols, this.getSessionKey());
        List<Map<String, Object>> marketData = new ArrayList<>();

        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);

            for (Map<String, Object> i : result) {
                Map<String, Object> data = new HashMap<>();
                for (String k: this.marketDataFields) {
                    if (i.containsKey(k)) {
                        Object v = i.get(k);
                        data.put(k, v);
                        //System.out.println("key: " + k + ", value: " + v);
                    }
                }
                marketData.add(data);
            }
        }
        return marketData;
    }
    
    /********************
     * API: Settings
     ********************/

    public Map<String, Object> getSetting (String key) {
        System.out.println("\n[Get Setting]");

        Map<String, Object> respondMap = executeGet("users/" + this.user.userID + "/settings/" + key, this.getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);
        Map<String, Object> setting = new HashMap<>();

        for (String k: this.settingFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                setting.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return setting;
    }
    
    public List<Map<String, Object>> listAllSettings () {
        System.out.println("\n[List all Settings]");

        Map<String, Object> respondMap = executeGet("users/" + this.user.userID + "/settings", this.getSessionKey());
        List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);
        List<Map<String, Object>> settings = new ArrayList<>();
        
        for (Map<String, Object> a : result) {
            Map<String, Object> setting = new HashMap<>();
            for (String k: this.settingFields) {
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

    public Map<String, Object> createSetting (Map<String, String> args) {
        System.out.println("\n[Create Setting]");

        Map<String, Object> params = new HashMap<>();
        params.put("userID", this.user.userID);
        params.put("key", args.get("key"));
        params.put("value", args.get("value"));
        
        Map<String, Object> respondMap = executePost("users/" + this.user.userID + "/settings", params, this.getSessionKey());
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = gson.fromJson(respond, HashMap.class);

        Map<String, Object> setting = new HashMap<>();
            
        for (String k: this.settingFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                setting.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        return setting;
    }
    
    public boolean deleteSetting (String key) {
        System.out.println("\n[Delete Setting]");

        Map<String, Object> result = executeDelete("users/" + this.user.userID + "/settings/" + key, this.getSessionKey());
        int statusCode = (int) result.get("code");

        return statusCode == 200;
    }
    
    /********************
     * API: Charts
     ********************/

    public static enum ChartCompression {
        Daily(0),
        OneMinute(1),
        TwoMinute(2),
        ThreeMinute(3),
        FiveMinute(4),
        TenMinute(5),
        FifteenMinute(6),
        TwentyMinute(7),
        ThirtyMinute(8),
        OneHour(9),
        TwoHour(102),
        FourHour(14),
        EightHour(108),
        Weekly(10),
        Monthly(11),
        Yearly(12);
        
        public final int value;

        private ChartCompression(int value) {
           this.value = value;
        }
    }

    public List<String[]> getCharts (Map<String, Object> args) {
        System.out.println("\n[get Charts]");

        // required fields: instrumentID, compression
        // if tradingDays set, dateStart & dateEnd are ignored
        
        String instrumentID = args.get("instrumentID").toString();
        int compression = ( (ChartCompression) args.get("compression") ).value;
        String url = "bars?instrumentID=" + instrumentID + "&compression=" + compression;

        if (args.containsKey("dateStart") && args.containsKey("dateEnd")) {
            String dateStart = args.get("dateStart").toString();
            String dateEnd = args.get("dateEnd").toString();
            url = url + "&dateStart=" + dateStart + "&dateEnd=" + dateEnd;
        } else if (args.containsKey("tradingDays")) {
            url = url + "&tradingDays=" + args.get("tradingDays").toString();
        }
        System.out.println("\n url: " + url);
        
        Map<String, Object> respondMap = executeGet(url, this.getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        List<String[]> data = new ArrayList<String[]>();
        
        if (   result.get("instrumentID").toString().equals(instrumentID)
            && (Double) result.get("compression") == compression)
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
        FinTrans("FinTrans"),
        OrderTrans("OrderTrans"),
        PositionRestingOrder("PositionRestingOrder"),
        Instrument("Instrument"),
        ReferralSummaryPerformance("ReferralSummaryPerformance");

        public final String name;

        private ReportName(String name) {
           this.name = name;
        }
    }

    /*
        "transactionReport" covers 3 types:

        1) Financial Transaction
            This report allows users to generate all of their financial transactions for their specified account.
        2) Order Transaction
            This report allows users to generate order related transactions for the specified account number
            over the specified date interval. These transactions can be also filtered by symbol if desired.
        3) Open Positions and Resting Orders
            Provides the user their current open positions and resting orders.
    */
    
    public List<Map<String, Object>> transactionReport (String reportType, Map<String, String> args) {
        System.out.println("\n[Transaction Report] - " + reportType);

        String AccountNumber = args.get("AccountNumber");
        String reportName = null;
        String DateStart = "";
        String DateEnd = "";
        String symbol = "";
        List<String> txnFields;

        switch (reportType) {
            case "financial":
                reportName = ReportName.FinTrans.name;
                DateStart = "&DateStart=" + args.get("DateStart");
                DateEnd = "&DateEnd=" + args.get("DateEnd");

                txnFields = this.financialTxnFields;
                break;
            case "order":
                reportName = ReportName.OrderTrans.name;
                DateStart = "&DateStart=" + args.get("DateStart");
                DateEnd = "&DateEnd=" + args.get("DateEnd");

                // for Order Transaction, "symbol" is optional
                if (args.containsKey("symbol")) {
                    symbol = "&symbol=" + args.get("symbol");
                }

                txnFields = this.orderTxnFields;
                break;
            case "openPos":
                reportName = ReportName.PositionRestingOrder.name;
                txnFields = this.openPosFields;
                break;
            default:
                System.out.println("unknown reportType: " + reportType);
                return null;
        }
        
        String url = "DriveWealth?ReportFormat=JSON&wlpID=DW&LanguageID=en_US"
                + "&ReportName="    + reportName
                + "&sessionKey="    + this.getSessionKey()
                + "&AccountNumber=" + AccountNumber
                + DateStart
                + DateEnd
                + symbol;

        System.out.println("URL: " + url);
        
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> respondMap = executePost(url, params, this.getSessionKey(), "reportServer");

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
        if (reportType.equals("openPos")) {
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
    public List<Map<String, Object>> stocksReport (Map<String, String> args) {
        System.out.println("\n[Stocks, ETFs and ADRs Report]");

        String url = "DriveWealth?ReportFormat=JSON&wlpID=DW&LanguageID=en_US"
                + "&ReportName="    + ReportName.Instrument.name
                + "&sessionKey="    + this.getSessionKey()
                + "&AccountNumber=" + args.get("AccountNumber")
                + "&DateStart="     + args.get("DateStart")
                + "&DateEnd="       + args.get("DateEnd")
                // All: -1, Inactive: 0, Active: 1
                + "&TradeStatus=-1"
                // All: -1, Stocks: 6, ETFs: 7
                + "&InstrumentType=-1";

        System.out.println("URL: " + url);

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> respondMap = executePost(url, params, this.getSessionKey(), "reportServer");

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
            for (String k: this.stockFields) {
                if (a.containsKey(k)) {
                    Object v = a.get(k);
                    stock.put(k, v);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }
            
            // convert instrument from LinkedTreeMap -> HashMap
            Map<String, Object> ins = (Map<String, Object>) stock.get("instrument");
            Map<String, Object> instrument = new HashMap<>();

            for (String k: this.stockInstrumentFields) {
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

    public List<Map<String, Object>> listStatements (String type, Map<String, String> args) {
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

        Map<String, Object> respondMap = executeGet(url, this.getSessionKey());
        List<Map<String, Object>> result = gson.fromJson(respondMap.get("respond").toString(), ArrayList.class);
        List<Map<String, Object>> statements = new ArrayList<>();
        
        for (Map<String, Object> a : result) {
            Map<String, Object> statement = new HashMap<>();
            for (String k: this.listStatementFields) {
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
    
    public Map<String, Object> getStatement (String accountID, String fileKey) {
        String url = "statements/" + accountID + "/" + fileKey;
        System.out.println("URL: " + url);

        Map<String, Object> respondMap = executeGet(url, this.getSessionKey());
        Map<String, Object> result = gson.fromJson(respondMap.get("respond").toString(), HashMap.class);

        Map<String, Object> statement = new HashMap<>();
        for (String k: this.getStatementFields) {
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
    
    public String getSessionKey() {
        Map<String, Object> session = new HashMap<>();
        
        if (this.user.sessionKey == null) {
            Map<String, String> args = new HashMap<>();
            
            args.put("username", this.user.username);
            args.put("password", this.user.password);
            args.put("appTypeID", "26");
            args.put("appVersion", "0.1");
            args.put("languageID", "en_US");
            args.put("osType", "iOS");
            args.put("osVersion", "iOS 9.1");
            args.put("scrRes", "1920x1080");
            args.put("ipAddress", "1.1.1.1");
            
            session = createSession(args);
            
            // error create session
            if (session.containsKey("code") && session.containsKey("message")) {
                return null;
            }
            
            this.user.sessionKey = session.get("sessionKey").toString();
            this.user.userID = session.get("userID").toString();
            this.user.commissionRate = (Double) session.get("commissionRate");
            
            List<Map<String, Object>> accounts = (ArrayList) session.get("accounts");

            for (Map<String, Object> a : accounts) {
                String accountID   = a.get("accountID").toString();
                String accountNo   = a.get("accountNo").toString();
                Double accountType = (Double) a.get("accountType");
                Double cash        = (Double) a.get("cash");
                
                final Account account = new Account(a);

                if (accountType == 1) {
                    this.user.practiceAccount = account;
                } else if (accountType == 2) {
                    this.user.liveAccount = account;
                }

                if (!this.user.accountsMap.containsKey(accountID)) {
                    this.user.accountsMap.put(accountID, account);
                }
            }                    
        }
        return this.user.sessionKey;
    }
    
    public void setUser (Map<String, Object> params) {
        this.user = new User(params.get("username").toString(), params.get("password").toString());
    }
    
    /************************
     * Http Helper functions
     ************************/

    private static HttpMethod setJsonHeader (HttpMethod httpMethod, String sessionKey) {
        httpMethod.addRequestHeader("Accept", "application/json");
        httpMethod.addRequestHeader("Content-Type", "application/json");

        if (sessionKey != null) {
            httpMethod.addRequestHeader("x-mysolomeo-session-key", sessionKey);
        }
        return httpMethod;
    }

    private static Map<String, Object> executeHttpCall (HttpMethod httpMethod) {
        HttpClient httpClient = new HttpClient();
        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(httpMethod);
            System.out.println("status code: " + statusCode);
        } catch (IOException ex) {
            Logger.getLogger(DriveWealthAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String respond = null;
        try {
            StringBuilder resultStr = new StringBuilder();
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(httpMethod.getResponseBodyAsStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    resultStr.append(line);
                }
            }
            respond = resultStr.toString();
            httpMethod.releaseConnection();
        } catch (IOException ex) {
            Logger.getLogger(DriveWealthAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", statusCode);
        result.put("respond", respond);
        
        System.out.println("respond: " + respond);
        
        return result;
    }

    private static Map<String, Object> executeGet(String url, String sessionKey) {
        GetMethod getMethod = new GetMethod(hostURL + url);
        getMethod = (GetMethod) setJsonHeader(getMethod, sessionKey);
        return executeHttpCall(getMethod);
    }

    private static Map<String, Object> executePost(String url, Map<String, Object> params, String sessionKey, String server) {
        Gson gson = new Gson();
        String paramsJson = gson.toJson(params);

        StringRequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(
                    paramsJson,
                    "application/json",
                    "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DriveWealthAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        String host = null;
        if (server.equals("apiServer")) {
            host = hostURL;
        } else if (server.equals("reportServer")) {
            host = reportURL;
        } else {
            System.out.println("[executePost] unknown hostType: " + server);
            return null;
        }
        
        PostMethod postMethod = new PostMethod(host + url);
        postMethod.setRequestEntity(requestEntity);

        postMethod = (PostMethod) setJsonHeader(postMethod, sessionKey);
        return executeHttpCall(postMethod);
    }
    
    private static Map<String, Object> executePost(String url, Map<String, Object> params, String sessionKey) {
        return executePost(url, params, sessionKey, "apiServer");
    }
    
    private static Map<String, Object> executePut(String url, Map<String, Object> params, String sessionKey) {
        Gson gson = new Gson();
        String paramsJson = gson.toJson(params);

        StringRequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(
                    paramsJson,
                    "application/json",
                    "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DriveWealthAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        PutMethod putMethod = new PutMethod(hostURL + url);
        putMethod.setRequestEntity(requestEntity);

        putMethod = (PutMethod) setJsonHeader(putMethod, sessionKey);
        return executeHttpCall(putMethod);
    }

    private static Map<String, Object> executeDelete(String url, String sessionKey) {
        DeleteMethod deleteMethod = new DeleteMethod(hostURL + url);
        deleteMethod = (DeleteMethod) setJsonHeader(deleteMethod, sessionKey);
        return executeHttpCall(deleteMethod);
    }
    
    private static Map<String, Object> getError (Map<String, Object> result) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", result.get("code"));
        error.put("message", result.get("message"));
        return error;
    }
    
    /************************
     * inner class, Variables
     ************************/

    public class User {
        public User () {}

        public User (String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String username;
        public String password;
        public String sessionKey;
        public String userID;
        public Double commissionRate;
        
        public Account practiceAccount;
        public Account liveAccount;
        public Map<String, Account> accountsMap = new HashMap<>();
    }
    
    public class Account {
        public Account (Map<String, Object> account) {
            String accountID = account.get("accountID").toString();
            String accountNo = account.get("accountNo").toString();
            Double accountTypeID = (Double) account.get("accountType");
            Double cash = (Double) account.get("cash");
            String nickname = account.get("nickname").toString();
            
            this.accountID = accountID;
            this.accountNo = accountNo;
            this.accountType = accountTypeID;
            this.cash = cash;
            this.nickname = nickname;
        }

        public String accountID;
        public String accountNo;
        public Double accountType;
        public Double cash;
        public String nickname;
    }
    
    private final Gson gson = new Gson();
    public static String hostURL = "https://api.drivewealth.io/v1/";
    public static String reportURL = "http://reports.drivewealth.io/";
    
    public User user;
}
