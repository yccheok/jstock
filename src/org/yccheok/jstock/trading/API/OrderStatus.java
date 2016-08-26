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
public class OrderStatus {
    
    private final DriveWealth api;
    private final String orderID;
    private final String url;
    private final Map<String, Object> statusMap = new HashMap<>();
    private final OrdStatus ordStatus;
    private String rejectedReason = null;

    
    public static enum OrdStatus {
        ACCEPTED("ACCEPTED"),
        FILLED("FILLED"),
        PARTIAL_FILLED("PARTIAL_FILLED"),
        CANCELLED("CANCELLED"),
        REJECTED("REJECTED"),
        
        // validation error, eg: insufficient balance
        VALIDATION_ERROR("VALIDATION_ERROR"),
        
        // Failed to call create order
        ERROR("ERROR");
        
        private final String value;
        
        private OrdStatus (String value) {
            this.value = value;
        }
        
        public String getValue () {
            return this.value;
        }
    }

    private static final List<String> RESULT_FIELDS = new ArrayList<>(Arrays.asList(
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
   
    
    public OrderStatus (DriveWealth api, String orderID) {
        this.api = api;
        this.orderID = orderID;
        this.url = "orders/" + this.orderID;

        Map<String, Object> respondMap = DriveWealth.executeGet(this.url, this.api.getSessionKey());
        Map<String, Object> result = new Gson().fromJson(respondMap.get("respond").toString(), HashMap.class);

        for (String k: RESULT_FIELDS) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                this.statusMap.put(k, v);
                System.out.println("key: " + k + ", value: " + v);
            }
        }

        double cumQty      = (double) this.statusMap.get("cumQty");
        double leavesQty   = (double) this.statusMap.get("leavesQty");
        double orderQty    = (double) this.statusMap.get("orderQty");
        String execType    = this.statusMap.get("execType").toString();
        String status      = this.statusMap.get("ordStatus").toString();

        OrdStatus _ordStatus = OrdStatus.ERROR;

        // accepted
        if (    orderQty == leavesQty
                && execType.equals("0")
                && status.equals("0")
        ) {
            _ordStatus = OrdStatus.ACCEPTED;
        }
        // filled
        else if (  orderQty == cumQty
                && execType.equals("2")
                && status.equals("2")
        ) {
            _ordStatus = OrdStatus.FILLED;
        }
        // partially filled
        else if (  orderQty > cumQty
                && execType.equals("1")
                && status.equals("1")
        ) {
            _ordStatus = OrdStatus.PARTIAL_FILLED;
        }
        // Cancelled
        else if (  orderQty == leavesQty
                && execType.equals("4")
                && status.equals("4")
        ) {
            _ordStatus = OrdStatus.CANCELLED;
        }
        // Rejected
        else if (  leavesQty == 0
                && execType.equals("8")
                && status.equals("8")
        ) {
            _ordStatus = OrdStatus.REJECTED;
            
            if (this.statusMap.containsKey("ordRejReason")) {
                this.rejectedReason = this.statusMap.get("ordRejReason").toString();
            }
        }
        
        this.ordStatus = _ordStatus;
        this.statusMap.put("ordStatus", this.ordStatus);

        String reason = (this.rejectedReason != null)? ", Reason: " + this.rejectedReason : "";

        System.out.println("Order " + this.ordStatus.getValue() + ": " + this.orderID + reason);
    }

    public OrdStatus getStatus () {
        return this.ordStatus;
    }
    
    public Map<String, Object> getStatusMap () {
        return this.statusMap;
    }
    
    public String getRejectedReason () {
        return this.rejectedReason;
    }
}
