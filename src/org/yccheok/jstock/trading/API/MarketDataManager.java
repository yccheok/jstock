/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shuwnyuan
 */
public class MarketDataManager {

    private MarketDataManager () {}
    
    public static class MarketData {
        private final String symbol;
        private Double bid = null;
        private Double ask = null;
        private Double lastTrade = null;

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
        
        public Double getBid () {
            return this.bid;
        }
        
        public Double getAsk () {
            return this.ask;
        }
        
        public Double getLastTrade () {
            return this.lastTrade;
        }
    }
    
    
    public static List<MarketData> get (ArrayList<String> symbolList, boolean lastTradeOnly) {
        List<String> OUTPUT_FIELDS = new ArrayList<>(Arrays.asList(
            "symbol",
            "bid",
            "ask",
            "lastTrade"
        ));

        String symbols = String.join(",", symbolList);
        //System.out.println("symbols: " + symbols);

        // get last Traded price only, no bid & ask return
        String lastTrade = "";
        if (lastTradeOnly == true) {
            lastTrade = "&lastTrade=true";
        }
        
        String url = "quotes?symbols=" + symbols + lastTrade;

        // no Session Key is required, consider as unauthorized call
        Map<String, Object> respondMap = Http.get(url, null);

        List<MarketData> marketDataList = new ArrayList<>();

        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = new Gson().fromJson(respondMap.get("respond").toString(), ArrayList.class);

            for (Map<String, Object> data : result) {
                
                // debugging only
                for (String k: OUTPUT_FIELDS) {
                    if (data.containsKey(k)) {
                        Object v = data.get(k);
                        //System.out.println("key: " + k + ", value: " + v);
                    }
                }

                marketDataList.add(new MarketData(data));
            }
        }
        
        return marketDataList;
    }

    
    
}
