/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import javafx.beans.property.SimpleStringProperty;
import java.util.Map;

/**
 *
 * @author shuwnyuan
 */
public class OrderModel {
    private final SimpleStringProperty symbol;
    private final SimpleStringProperty name;
    private final SimpleStringProperty type;
    private final SimpleStringProperty side;
    private final SimpleStringProperty units;
    private final SimpleStringProperty marketPrice;
    private final SimpleStringProperty limitPrice;
    private final SimpleStringProperty stopPrice;
    
    public OrderModel(Map<String, Object> ord) {
        this.symbol = new SimpleStringProperty(ord.get("symbol").toString());
        this.name   = new SimpleStringProperty(ord.get("name").toString());
        
        Double stopPriceD = 0.0;
        Double limitPriceD = 0.0;
        String typeStr = ord.get("orderType").toString();
        
        if (typeStr.equals("1")) {
            typeStr = "Market";
        } else if (typeStr.equals("2")) {
            typeStr = "Limit";
            limitPriceD = (Double) ord.get("limitPrice");
        } else if (typeStr.equals("3")) {
            typeStr = "Stop";
            stopPriceD = (Double) ord.get("stopPrice");
        }
        
        this.type = new SimpleStringProperty(typeStr);
        this.limitPrice = new SimpleStringProperty(Utils.monetaryFormat(limitPriceD));
        this.stopPrice = new SimpleStringProperty(Utils.monetaryFormat(stopPriceD));
        
        this.side           = new SimpleStringProperty( ord.get("side").toString().equals("B") ? "buy" : "sell" ) ;
        this.units          = new SimpleStringProperty(Utils.formatNumber((Double)ord.get("units"), 2));
        this.marketPrice    = new SimpleStringProperty(Utils.monetaryFormat((Double)ord.get("marketPrice")));
    }

    public void updateMarketPrice (Double price) {
        this.setMarketPrice(Utils.monetaryFormat(price));
    }
    
    public final String getSymbol() {
        return symbol.get();
    }
    public final void setSymbol(String v) {
        symbol.set(v);
    }
    public SimpleStringProperty symbolProperty () {
        return symbol;
    }
    
    public final String getName() {
        return name.get();
    }
    public final void setName(String v) {
        name.set(v);
    }
    public SimpleStringProperty nameProperty () {
        return name;
    }
    
    public final String getType() {
        return type.get();
    }
    public final void setType(String v) {
        type.set(v);
    }
    public SimpleStringProperty typeProperty () {
        return type;
    }
    
    public final String getSide() {
        return side.get();
    }
    public final void setSide(String v) {
        side.set(v);
    }
    public SimpleStringProperty sideProperty () {
        return side;
    }
    
    public final String getUnits() {
        return units.get();
    }
    public final void setUnits(String v) {
        units.set(v);
    }
    public SimpleStringProperty unitsProperty () {
        return units;
    }
    
    public final String getMarketPrice() {
        return marketPrice.get();
    }
    public final void setMarketPrice(String v) {
        marketPrice.set(v);
    }
    public SimpleStringProperty marketPriceProperty () {
        return marketPrice;
    }

    public final String getLimitPrice() {
        return limitPrice.get();
    }
    public final void setLimitPrice(String v) {
        limitPrice.set(v);
    }
    public SimpleStringProperty limitPriceProperty () {
        return limitPrice;
    }
    
    public final String getStopPrice() {
        return stopPrice.get();
    }
    public final void setStopPrice(String v) {
        stopPrice.set(v);
    }
    public SimpleStringProperty stopPriceProperty () {
        return stopPrice;
    }
}
