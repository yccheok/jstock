/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.Map;

/**
 *
 * @author shuwnyuan
 */
public class Order {
    
    public Order (Map<String, Object> order, Map<String, Object> instrument) {
        this.symbol      = order.get("symbol").toString();
        this.name        = instrument.get("name").toString();
        this.units       = (Double) order.get("orderQty");
        this.side        = (order.get("side").toString().equals("B")) ? "buy" : "sell";
        this.marketPrice = (Double) instrument.get("lastTrade");

        this.limitPrice  = 0.00;
        this.stopPrice   = 0.00;
        String type      = order.get("orderType").toString();
        
        if (type.equals("1")) {
            this.type = "Market";
        } else if (type.equals("2")) {
            this.type = "Limit";
            this.limitPrice = (Double) order.get("limitPrice");
        } else if (type.equals("3")) {
            this.type = "Stop";
            this.stopPrice = (Double) order.get("stopPrice");
        } else {
            System.out.println("Invalid order type: " + type);
        }
    }

    public String symbol;
    public String name;
    public Double units;
    public String type;
    public String side;
    public Double marketPrice;
    public Double limitPrice;
    public Double stopPrice;
}
