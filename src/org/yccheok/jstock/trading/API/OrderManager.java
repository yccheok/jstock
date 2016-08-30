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
public class OrderManager {

    // avoid being instantiated as object instance
    private OrderManager () {}

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

    public static class Order {
        private final String orderID;
        private final String instrumentID;
        private final Double leavesQty;
        private final String ordType;
        private final String side;
        private final String orderNo;

        // Below fields only available for "Order Status" return output, not for "Create Order"
        private Double cumQty = null;
        private Double grossTradeAmt = null;
        private String ordStatus = null;
        private String execType = null;
        private String ordRejReason = null;
        private Double accountType = null;
        private Double orderQty = null;
        private Double commission = null;
        // enum for ordStatus
        private OrdStatus ordStatusEnum = null;

        public Order (Map<String, Object> params) {
            this.orderID        = params.get("orderID").toString();
            this.instrumentID   = params.get("instrumentID").toString();
            this.leavesQty      = (Double) params.get("leavesQty");
            this.ordType        = params.get("ordType").toString();
            this.side           = params.get("side").toString();
            this.orderNo        = params.get("orderNo").toString();
            
            if (params.containsKey("cumQty")) {
                this.cumQty = (Double) params.get("cumQty");
            }
            
            if (params.containsKey("grossTradeAmt")) {
                this.grossTradeAmt = (Double) params.get("grossTradeAmt");
            }
            
            if (params.containsKey("ordStatus")) {
                this.ordStatus = params.get("ordStatus").toString();
            }
            
            if (params.containsKey("execType")) {
                this.execType = params.get("execType").toString();
            }
            
            if (params.containsKey("ordRejReason")) {
                this.ordRejReason = params.get("ordRejReason").toString();
            }
            
            if (params.containsKey("accountType")) {
                this.accountType = (Double) params.get("accountType");
            }
            
            if (params.containsKey("orderQty")) {
                this.orderQty = (Double) params.get("orderQty");
            }
            
            if (params.containsKey("commission")) {
                this.commission = (Double) params.get("commission");
            }
            
            if (params.containsKey("ordStatusEnum")) {
                this.ordStatusEnum = (OrdStatus) params.get("ordStatusEnum");
            }
        }
        
        public String getOrderID () {
            return this.orderID;
        }
        
        public String getInstrumentID () {
            return this.instrumentID;
        }

        public Double getLeavesQty () {
            return this.leavesQty;
        }
        
        public String getOrdType () {
            return this.ordType;
        }
        
        public String getSide () {
            return this.side;
        }
        
        public Double getCumQty () {
            return this.cumQty;
        }
        
        public Double getGrossTradeAmt () {
            return this.grossTradeAmt;
        }
        
        public String getOrdStatus () {
            return this.ordStatus;
        }
        
        public String getExecType () {
            return this.execType;
        }
        
        public String getOrdRejReason () {
            return this.ordRejReason;
        }
        
        public String getOrderNo () {
            return this.orderNo;
        }
        
        public Double getAccountType () {
            return this.accountType;
        }
        
        public Double getOrderQty () {
            return this.orderQty;
        }
        
        public Double getCommission () {
            return this.commission;
        }

        public OrdStatus getOrdStatusEnum () {
            return this.ordStatusEnum;
        }
    }

    
    public static Pair<Order, String> create (OrderSide orderSide, OrderType orderType, Map<String, Object> params) {
        String url = "orders";

        List<String> INPUT_FIELDS = new ArrayList<>(Arrays.asList(
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

        List<String> RESULT_FIELDS = new ArrayList<>(Arrays.asList(
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

        Map<String, Object> ordParams = new HashMap<>();
        
        for (String k: INPUT_FIELDS) {
            if (params.containsKey(k)) {
                Object v = params.get(k);
                ordParams.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        ordParams.put("ordType", orderType.getValue());
        ordParams.put("side", orderSide.getValue());
        
        Pair<Boolean, String> validate;
        if (orderSide == OrderSide.BUY) {
            validate = validateBuy(orderType, ordParams);
        } else {
            validate = validateSell(orderType, ordParams);
        }

        // validation error
        if (validate.first == false) {
            return new Pair<>(null, validate.second);
        }

        // NOTE For LIMIT order:
        //  BUY will execute in the market when the market ask price is at or below the order limit price
        //  If the price entered is above the current ask price, order will be immediately executed

        //  SELL will execute in the market when the market bid price is at or above the order limit price
        //  If the price entered is below the current ask price, order will be immediately executed
        

        // create order
        Map<String, Object> respondMap = DriveWealth.executePost(url, params, DriveWealth.getSessionKey());
        String respond = respondMap.get("respond").toString();
        Map<String, Object> result = new Gson().fromJson(respond, HashMap.class);

        // debuging only
        for (String k: RESULT_FIELDS) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                System.out.println("key: " + k + ", value: " + v);
            }
        }

        Order order = new Order(result);
        return new Pair<>(order, null);
    }
    
    private static Pair<Boolean, String> validateBuy (OrderType orderType, Map<String, Object> params) {
        System.out.println("\n validate Buy Order - " + orderType);
        
        String accountID  = params.get("accountID").toString();
        String symbol     = params.get("symbol").toString();
        double orderQty   = Double.parseDouble(params.get("orderQty").toString());
        double commission = DriveWealth.getUser().getCommissionRate();

        // get market price (use "Ask Price" for Buy)
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(symbol));
        List<MarketDataManager.MarketData> dataList = MarketDataManager.get(symbols, false);
        double askPrice = dataList.get(0).getAsk();

        double price = 0;
        if (orderType == OrderType.MARKET) {
            price = askPrice;
        } else if (orderType == OrderType.STOP) {
            price = Double.parseDouble(params.get("price").toString());

            // Check price >= ask price + 0.05
            if (price < (askPrice + 0.05)) {
                String error = "Stop price must be >= ask price + 0.05, Stop price: " + price + ", Ask Price: " + askPrice;
                System.out.println(error);
                
                return new Pair<>(false, error);
            }
        } else if (orderType == OrderType.LIMIT) {
            // BUY will execute in the market when the market ask price is at or below the order limit price
            // If the price entered is above the current ask price, order will be immediately executed

            // SELL will execute in the market when the market bid price is at or above the order limit price
            // If the price entered is below the current ask price, order will be immediately executed
            price = Double.parseDouble(params.get("limitPrice").toString());
        }

        // get balance
        AccountManager.AccountBlotter accBlotter = AccountManager.blotter(DriveWealth.getUser().getUserID(), accountID);
        double balance = accBlotter.getTradingBalance();

        // check for insufficient balance
        double amount = orderQty * price + commission;

        System.out.println("Check balance: amount: " + amount + ", balance: " + balance
                + ", market price (Ask): " + price + ", Qty: " + orderQty
                + ", commission: " + commission);

        if (balance < amount) {
            String error = "Insufficient balance";
            System.out.println(error);

            return new Pair<>(false, error);
        }

        return new Pair<>(true, null);
    }

    private static Pair<Boolean, String> validateSell (OrderType orderType, Map<String, Object> params) {
        System.out.println("\n validate Sell order - " + orderType);
        
        String accountID    = params.get("accountID").toString();
        String symbol       = params.get("symbol").toString();
        String instrumentID = params.get("instrumentID").toString();
        double orderQty     = Double.parseDouble(params.get("orderQty").toString());

        if (orderType == OrderType.STOP) {
            // get market price (use "Bid Price" for Sell)
            ArrayList<String> symbols = new ArrayList<>(Arrays.asList(symbol));
            List<MarketDataManager.MarketData> dataList = MarketDataManager.get(symbols, false);
            double bidPrice = dataList.get(0).getBid();
            
            double price = Double.parseDouble(params.get("price").toString());

            // Check Stop Price <= Bid Price - 0.05
            if (price > (bidPrice - 0.05)) {
                String error = "Stop price must be <= Bid Price - 0.05, Stop price: " + price + ", Bid Price: " + bidPrice;
                System.out.println(error);
                
                return new Pair<>(false, error);
            }
        }
        
        // get available trading Qty
        AccountManager.AccountBlotter accBlotter = AccountManager.blotter(DriveWealth.getUser().getUserID(), accountID);
        double availQty = accBlotter.getAvailableTradingQty(instrumentID);

        System.out.println("availableForTradingQty: " + availQty + ", orderQty: " + orderQty);

        // check insufficient Qty for sell
        if (availQty < orderQty) {
            String error = "Insufficient Qty for sell";
            System.out.println(error);

            return new Pair<>(false, error);
        }
        
        return new Pair<>(true, null);
    }
    
    public static enum OrdStatus {
        NEW("0", "NEW"),
        PARTIAL_FILLED("1", "PARTIAL_FILLED"),
        FILLED("2", "FILLED"),
        CANCELLED("4", "CANCELLED"),
        REJECTED("8", "REJECTED");

        private final String value;
        private final String name;
        
        private OrdStatus (String value, String name) {
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

    public static Order status (String orderID) {
        String url = "orders/" + orderID;
        
        List<String> RESULT_FIELDS = new ArrayList<>(Arrays.asList(
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

        
        Map<String, Object> respondMap = DriveWealth.executeGet(url, DriveWealth.getSessionKey());
        Map<String, Object> result = new Gson().fromJson(respondMap.get("respond").toString(), HashMap.class);

        // debugging only
        for (String k: RESULT_FIELDS) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                System.out.println("key: " + k + ", value: " + v);
            }
        }

        double cumQty       = (double) result.get("cumQty");
        double leavesQty    = (double) result.get("leavesQty");
        double orderQty     = (double) result.get("orderQty");
        String execType     = result.get("execType").toString();
        String ordStatus    = result.get("ordStatus").toString();

        String ordRejReason     = null;
        OrdStatus ordStatusEnum = null;

        // New / Accepted
        if (    orderQty == leavesQty
                && execType.equals("0")
                && ordStatus.equals("0")
        ) {
            ordStatusEnum = OrdStatus.NEW;
        }
        // partially filled
        else if (  orderQty > cumQty
                && execType.equals("1")
                && ordStatus.equals("1")
        ) {
            ordStatusEnum = OrdStatus.PARTIAL_FILLED;
        }
        // filled
        else if (  orderQty == cumQty
                && execType.equals("2")
                && ordStatus.equals("2")
        ) {
            ordStatusEnum = OrdStatus.FILLED;
        }
        // Cancelled
        else if (  orderQty == leavesQty
                && execType.equals("4")
                && ordStatus.equals("4")
        ) {
            ordStatusEnum = OrdStatus.CANCELLED;
        }
        // Rejected
        else if (  leavesQty == 0
                && execType.equals("8")
                && ordStatus.equals("8")
        ) {
            ordStatusEnum = OrdStatus.REJECTED;
            ordRejReason = result.get("ordRejReason").toString();
        }
        
        String reason = (ordRejReason != null)? ", Reason: " + ordRejReason : "";
        System.out.println("Order: " + orderID + ", status: " + ordStatusEnum.getName() + reason);

        result.put("ordStatusEnum", ordStatusEnum);
        return new Order(result);
    }

    
}
