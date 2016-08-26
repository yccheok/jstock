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
public class CreateOrder {

    private final String url = "orders";
    private final DriveWealth api;
    private final OrderSide orderSide;
    private final OrderType orderType;
    private final Map<String, Object> params = new HashMap<>();
    private final Map<String, Object> orderMap = new HashMap<>();

    public static enum OrderSide {
        BUY("B", "buy"),
        SELL("S", "sell");
        
        private final String value;
        private final String name;

        private OrderSide(String value, String name) {
           this.value = value;
           this.name = name;
        }
        
        public String getValue () {
            return this.value;
        }
        
        public String getName () {
            return this.name;
        }
    }
    
    public static enum OrderType {
        MARKET(1, "market"),
        LIMIT(2, "limit"),
        STOP(3, "stop");
        
        private final int value;
        private final String name;

        private OrderType(int value, String name) {
           this.value = value;
           this.name = name;
        }
        
        public int getValue () {
            return this.value;
        }
        
        public String getName () {
            return this.name;
        }
    }

    private static final List<String> INPUT_FIELDS = new ArrayList<>(Arrays.asList(
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

    private static final List<String> RESULT_FIELDS = new ArrayList<>(Arrays.asList(
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
    
    public CreateOrder (DriveWealth api, OrderSide side, OrderType type, Map<String, Object> params) {
        this.api = api;
        this.orderSide = side;
        this.orderType = type;
        
        for (String k: INPUT_FIELDS) {
            if (params.containsKey(k)) {
                Object v = params.get(k);
                this.params.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }

        this.params.put("ordType", this.orderType.getValue());
        this.params.put("side", this.orderSide.getValue());
    }
    
    private class ValidationStatus {
        private final boolean status;
        private final String error;

        public ValidationStatus (boolean status, String error) {
            this.status = status;
            this.error = error;
        }

        public boolean getStatus () {
            return this.status;
        }
        
        public String getError () {
            return this.error;
        }
    }
    
    private ValidationStatus validateBuy () {
        System.out.println("\n validate Buy Order - " + this.orderType);
        
        String accountID  = this.params.get("accountID").toString();
        String symbol     = this.params.get("symbol").toString();
        double orderQty   = Double.parseDouble(this.params.get("orderQty").toString());
        double commission = this.api.getUser().getCommissionRate();
        
        // get market price (use "Ask Price" for Buy)
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(symbol));
        List<GetMarketData.MarketData> dataList = this.api.getMarketData(symbols, false);
        double askPrice = dataList.get(0).getAsk();

        double price = 0;
        Map<String, Object> status = new HashMap<>();

        if (this.orderType == OrderType.MARKET) {
            price = askPrice;
        } else if (this.orderType == OrderType.STOP) {
            price = Double.parseDouble(this.params.get("price").toString());

            // Check price >= ask price + 0.05
            if (price < (askPrice + 0.05)) {
                String error = "Stop price must be >= ask price + 0.05, Stop price: " + price + ", Ask Price: " + askPrice;
                System.out.println(error);
                
                return new ValidationStatus(false, error);
            }
        } else if (this.orderType == OrderType.LIMIT) {
            // BUY will execute in the market when the market ask price is at or below the order limit price
            // If the price entered is above the current ask price, order will be immediately executed

            // SELL will execute in the market when the market bid price is at or above the order limit price
            // If the price entered is below the current ask price, order will be immediately executed
            price = Double.parseDouble(this.params.get("limitPrice").toString());
        }

        // get balance
        double balance = this.api.accountBlotter(this.api.getUser().getUserID(), accountID).getTradingBalance();

        // check for insufficient balance
        double amount = orderQty * price + commission;

        System.out.println("Check balance: amount: " + amount + ", balance: " + balance
                + ", market price (Ask): " + price + ", Qty: " + orderQty
                + ", commission: " + commission);

        if (balance < amount) {
            String error = "Insufficient balance";
            System.out.println(error);

            return new ValidationStatus(false, error);
        }

        return new ValidationStatus(true, null);
    }

    private ValidationStatus validateSell () {
        System.out.println("\n validate Sell order - " + this.orderType);
        
        String accountID    = this.params.get("accountID").toString();
        String symbol       = this.params.get("symbol").toString();
        String instrumentID = this.params.get("instrumentID").toString();
        double orderQty     = Double.parseDouble(this.params.get("orderQty").toString());

        if (this.orderType == OrderType.STOP) {
            // get market price (use "Bid Price" for Sell)
            ArrayList<String> symbols = new ArrayList<>(Arrays.asList(symbol));
            List<GetMarketData.MarketData> dataList = this.api.getMarketData(symbols, false);
            double bidPrice = dataList.get(0).getBid();
            
            double price = Double.parseDouble(this.params.get("price").toString());

            // Check Stop Price <= Bid Price - 0.05
            if (price > (bidPrice - 0.05)) {
                String error = "Stop price must be <= Bid Price - 0.05, Stop price: " + price + ", Bid Price: " + bidPrice;
                System.out.println(error);
                
                return new ValidationStatus(false, error);
            }
        }
        
        // get available trading Qty
        double availQty = this.api.accountBlotter(this.api.getUser().getUserID(), accountID).getAvailableTradingQty(instrumentID);

        System.out.println("availableForTradingQty: " + availQty + ", orderQty: " + orderQty);

        // check insufficient Qty for sell
        if (availQty < orderQty) {
            String error = "Insufficient Qty for sell";
            System.out.println(error);

            return new ValidationStatus(false, error);
        }

        return new ValidationStatus(true, null);
    }
    
    public Map<String, Object> execute () {

        this.orderMap.clear();
        ValidationStatus validation;
        
        if (this.orderSide == OrderSide.BUY) {
            validation = validateBuy();
        } else {
            validation = validateSell();
        }

        if (validation.getStatus() == false) {
            this.orderMap.put("validationError", validation.getError());
            return this.orderMap;
        }

        // NOTE For LIMIT order:
        //  BUY will execute in the market when the market ask price is at or below the order limit price
        //  If the price entered is above the current ask price, order will be immediately executed

        //  SELL will execute in the market when the market bid price is at or above the order limit price
        //  If the price entered is below the current ask price, order will be immediately executed
        

        // create order
        Map<String, Object> respondMap = DriveWealth.executePost(this.url, this.params, this.api.getSessionKey());
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = new Gson().fromJson(respond, HashMap.class);

        for (String k: RESULT_FIELDS) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                this.orderMap.put(k, v);
                System.out.println("key: " + k + ", value: " + v);
            }
        }

        return this.orderMap;
    }
    
    public Map<String, Object> getOrderMap () {
        return this.orderMap;
    }
    
}
