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
        this.symbol = order.get("symbol").toString();
        this.units = (Double) order.get("orderQty");
        this.side = order.get("side").toString();
        this.type = order.get("orderType").toString();

        // limit order
        if (this.type.equals("2")) {
            this.limitPrice = (Double) order.get("limitPrice");
        }
        // stop order
        if (this.type.equals("3")) {
            this.stopPrice = (Double) order.get("stopPrice");
        }

        this.name = instrument.get("name").toString();
        this.marketPrice = (Double) instrument.get("lastTrade");
    }
    
    public String symbol;
    public String name;
    public Double units;
    public String type;
    public String side;
    public Double limitPrice;
    public Double stopPrice;
    public Double marketPrice;
}
