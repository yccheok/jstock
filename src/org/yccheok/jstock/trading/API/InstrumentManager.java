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
public class InstrumentManager {

    private InstrumentManager () {}
    
    public static class Instrument {
        private final String instrumentID;
        private final String symbol;
        private final String name;

        private final Double lastTrade;
        private final Double priorClose;
        private final Double close;

        private final Double tradeStatus;
        private final String tradingHours;  // "Mon-Fri: 9:30am - 4:00pm ET"
        
        private final String category;      // Stock
        private final String currencyID;    // USD

        // only available for "search instrument", not "get instrument"
        private Double rateAsk = null;
        private Double rateBid = null;

        // available for certain symbols only, for both "search instrument" & "get instrument"
        private String urlImage = null;


        public Instrument (Map<String, Object> params) {
            this.instrumentID   = params.get("instrumentID").toString();
            this.symbol         = params.get("symbol").toString();
            this.name           = params.get("name").toString();

            this.lastTrade      = (Double) params.get("lastTrade");
            this.priorClose     = (Double) params.get("priorClose");
            this.close          = (Double) params.get("close");

            this.tradeStatus    = (Double) params.get("tradeStatus");
            this.tradingHours   = params.get("tradingHours").toString();

            this.category       = params.get("category").toString();
            this.currencyID     = params.get("currencyID").toString();

            // optional fields
            if (params.containsKey("rateAsk")) {
                this.rateAsk = (Double) params.get("rateAsk");
            }
            
            if (params.containsKey("rateBid")) {
                this.rateBid = (Double) params.get("rateBid");
            }

            if (params.containsKey("urlImage")) {
                this.urlImage = params.get("urlImage").toString();
            }
        }

        public String getInstrumentID () {
            return this.instrumentID;
        }
        
        public String getSymbol () {
            return this.symbol;
        }
        
        public String getName () {
            return this.name;
        }
        
        public Double getRateAsk () {
            return this.rateAsk;
        }
        
        public Double getRateBid () {
            return this.rateBid;
        }
        
        public Double getLastTrade () {
            return this.lastTrade;
        }

        public Double getClose () {
            return this.close;
        }
        
        public Double getPriorClose () {
            return this.priorClose;
        }
        
        // 0 - inactive
        // 1 - active
        // 2 - close positions only (no new position)
        public Double getTradeStatus () {
            return this.tradeStatus;
        }
        
        public String getTradingHours () {
            return this.tradingHours;
        }

        public String getCategory () {
            return this.category;
        }
        
        public String getCurrencyID () {
            return this.currencyID;
        }

        public String getUrlImage () {
            return this.urlImage;
        }
    }


    public static List<Instrument> search (Map<String, String> args) {
        System.out.println("\n[searchInstruments]");
        
        String url = "instruments?";

        List<String> INPUT_FIELDS = new ArrayList<>(Arrays.asList(
            "symbol",
            "symbols",
            "name",
            "tag"
        ));

        List<String> OUTPUT_FIELDS = new ArrayList<>(Arrays.asList(
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
            "rateHigh",
            "rateLow",
            "rateOpen",
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
            "lastTrade",
            "priorClose",
            "close"
        ));

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
        url = url.concat(params);
        
        Map<String, Object> respondMap = Http.get(url, DriveWealth.getSessionKey());
        
        List<Instrument> instrumentList = new ArrayList<>();
        
        if ((int) respondMap.get("code") == 200) {
            List<Map<String, Object>> result = new Gson().fromJson(respondMap.get("respond").toString(), ArrayList.class);

            int cnt = 0;
            for (Map<String, Object> ins : result) {
                
                // debugging only
                for (String k: OUTPUT_FIELDS) {
                    if (ins.containsKey(k)) {
                        Object v = ins.get(k);
                        //System.out.println(cnt + ": key: " + k + ", value: " + v);
                    }
                }

                Instrument instrument = new Instrument(ins);
                instrumentList.add(instrument);

                cnt++;
            }
        }
        
        return instrumentList;
    }
    
}
