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

    
    public static Pair<User, DriveWealth.Error> create (String userName, String password) {
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

        Map<String, Object> resultMap = new HashMap<>();
        User user = null;
        DriveWealth.Error error = null;

        if ((int) respondMap.get("code") == 200) {
            for (String k: OUTPUT_FIELDS) {
                if (result.containsKey(k)) {
                    Object v = result.get(k);
                    resultMap.put(k, v);
                    System.out.println("key: " + k + ", value: " + v);
                }
            }

            // initialise User Object & set accounts
            user = initUser(userName, password, resultMap);
        } else {
            error = DriveWealth.getError(result);
        }
        
        return new Pair<>(user, error);
    }

    private static User initUser (String userName, String password, Map<String, Object> resultMap) {
        Map<String, Object> params = new HashMap<>();

        params.put("username",       userName);
        params.put("password",       password);
        params.put("sessionKey",     resultMap.get("sessionKey"));
        params.put("userID",         resultMap.get("userID"));
        params.put("commissionRate", resultMap.get("commissionRate"));

        User user = new User(params);
        
        List<Map<String, Object>> accounts = (ArrayList) resultMap.get("accounts");
        for (Map<String, Object> acc : accounts) {
            double accountType = (double) acc.get("accountType");
            Account account = new Account(acc);

            if (accountType == 1) {
                user.setPracticeAccount(account);
            } else if (accountType == 2) {
                user.setLiveAccount(account);
            }
        }
        return user;
    }    
}
