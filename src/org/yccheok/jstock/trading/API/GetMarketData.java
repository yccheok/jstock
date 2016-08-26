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
public class GetMarketData {

    private final String url;
    private final List<MarketData> marketDataList = new ArrayList<>();
    
    private static final List<String> OUTPUT_FIELDS = new ArrayList<>(Arrays.asList(
        "symbol",
        "bid",
        "ask",
        "lastTrade"
    ));

    public class MarketData {
        private final String symbol;
        private double bid;
        private double ask;
        private double lastTrade;

        public MarketData (Map<String, Object> params) {
            this.symbol = params.get("symbol").toString();
            
            if (params.containsKey("bid")) {
                this.bid = (Double) params.get("bid");
            }
            
            if (params.containsKey("ask")) {
                this.ask = (Double) params.get("ask");
            }
            
            if (params.containsKey("lastTrade")) {
                this.lastTrade = (Double) params.get("lastTrade");
            }
        }
        
        public String getSymbol () {
            return this.symbol;
        }
        
        public double getBid () {
            return this.bid;
        }
        
        public double getAsk () {
            return this.ask;
        }
        
        public double getLastTrade () {
            return this.lastTrade;
        }
    }
    
    
    public GetMarketData (ArrayList<String> symbolList, boolean lastTradeOnly) {
        String symbols = String.join(",", symbolList);
        System.out.println("symbols: " + symbols);

        this.url = "quotes?symbols=" + symbols;
        // get last Traded price only, no bid & ask return
        if (lastTradeOnly == true) {
            this.url.concat("&lastTrade=true");
        }
        
        // no Session Key is required, consider as unauthorized call
        Map<String, Object> respondMap = DriveWealth.executeGet(this.url, null);

        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = new Gson().fromJson(respondMap.get("respond").toString(), ArrayList.class);

            for (Map<String, Object> i : result) {
                Map<String, Object> params = new HashMap<>();
                for (String k: OUTPUT_FIELDS) {
                    if (i.containsKey(k)) {
                        Object v = i.get(k);
                        params.put(k, v);
                        //System.out.println("key: " + k + ", value: " + v);
                    }
                }
                
                this.marketDataList.add(new MarketData(params));
            }
        }
    }

    public List<MarketData> getMarketDataList () {
        return this.marketDataList;
    }
    
}
