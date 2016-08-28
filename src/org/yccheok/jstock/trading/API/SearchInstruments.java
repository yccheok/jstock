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
public class SearchInstruments {

    private final String url = "instruments?";
    private final DriveWealth api;
    private final List<Map<String, Object>> instrumentList = new ArrayList<>();

    private static final List<String> INPUT_FIELDS = new ArrayList<>(Arrays.asList(
        "symbol",
        "symbols",
        "name",
        "tag"
    ));

    private static final List<String> OUTPUT_FIELDS = new ArrayList<>(Arrays.asList(
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
        "longOnly",
        "lastTrade"
    ));
    
    
    //public List<Map<String, Object>> searchInstruments (Map<String, String> args) {
    public SearchInstruments (DriveWealth api, Map<String, String> args) {
        System.out.println("\n[searchInstruments]");

        this.api = api;

        // 1) For exact symbol match (only return 1 symbol)
        //      https://api.drivewealth.io/v1/instruments?symbols=SCS
        // 2) search by symbol pattern (return >= 1 symbols)
        //      https://api.drivewealth.io/v1/instruments?symbol=SCS
        
        String params = null;
        
        for (String k: INPUT_FIELDS) {
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

        Map<String, Object> respondMap = DriveWealth.executeGet("instruments?" + params, this.api.getSessionKey());
        
        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = new Gson().fromJson(respondMap.get("respond").toString(), ArrayList.class);

            int cnt = 0;
            for (Map<String, Object> i : result) {
                Map<String, Object> instrument = new HashMap<>();
                for (String k: OUTPUT_FIELDS) {
                    if (i.containsKey(k)) {
                        Object v = i.get(k);
                        instrument.put(k, v);
                        //System.out.println(cnt + ": key: " + k + ", value: " + v);
                    }
                }
                this.instrumentList.add(instrument);
                cnt++;
            }
        }
    }
    
    public List<Map<String, Object>> getInstrumentList () {
        return this.instrumentList;
    }
    
}
