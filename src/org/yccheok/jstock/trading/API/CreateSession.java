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

/**
 *
 * @author shuwnyuan
 */
public class CreateSession {
    private final String url = "userSessions";
    private Map<String, Object> resultMap = new HashMap<>();
    
    private static final List<String> INPUT_FIELDS = new ArrayList<>(Arrays.asList(
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

    private static final List<String> OUTPUT_FIELDS = new ArrayList<>(Arrays.asList(
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

    
    public CreateSession(Map<String, String> args) {
        Map<String, Object> params = new HashMap<>();
        
        for (String k: INPUT_FIELDS) {
            if (args.containsKey(k)) {
                String v = args.get(k);
                params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        Map<String, Object> respondMap = DriveWealth.executePost(this.url, params, null);
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result  = new Gson().fromJson(respond, HashMap.class);

        if ((int) respondMap.get("code") == 200) {
            for (String k: OUTPUT_FIELDS) {
                if (result.containsKey(k)) {
                    Object v = result.get(k);
                    this.resultMap.put(k, v);
                    //System.out.println("key: " + k + ", value: " + v);
                }
            }
        } else {
            this.resultMap = DriveWealth.getError(result);
        }
    }

    public Map<String, Object> getResultMap () {
        return this.resultMap;
    }
}
