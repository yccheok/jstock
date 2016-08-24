/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import javafx.beans.property.SimpleDoubleProperty;
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
    
    private final SimpleDoubleProperty units;
    private final SimpleDoubleProperty marketPrice;
    private final SimpleDoubleProperty limitPrice;
    private final SimpleDoubleProperty stopPrice;
    
    public OrderModel(Map<String, Object> ord) {
        this.symbol = new SimpleStringProperty(ord.get("symbol").toString());
        this.name   = new SimpleStringProperty(ord.get("name").toString());
        
        Double _stopPrice = 0.0;
        Double _limitPrice = 0.0;
        String typeStr = ord.get("orderType").toString();
        
        if (typeStr.equals("1")) {
            typeStr = "Market";
        } else if (typeStr.equals("2")) {
            typeStr = "Limit";
            _limitPrice = (Double) ord.get("limitPrice");
        } else if (typeStr.equals("3")) {
            typeStr = "Stop";
            _stopPrice = (Double) ord.get("stopPrice");
        }
        
        this.type = new SimpleStringProperty(typeStr);
        this.side = new SimpleStringProperty( ord.get("side").toString().equals("B") ? "buy" : "sell" ) ;

        this.units       = new SimpleDoubleProperty((Double) ord.get("units"));
        this.marketPrice = new SimpleDoubleProperty((Double) ord.get("marketPrice"));
        this.limitPrice  = new SimpleDoubleProperty(_limitPrice);
        this.stopPrice   = new SimpleDoubleProperty(_stopPrice);
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
    
    public final Double getUnits() {
        return units.get();
    }
    public final void setUnits(Double v) {
        units.set(v);
    }
    public SimpleDoubleProperty unitsProperty () {
        return units;
    }
    
    public final Double getMarketPrice() {
        return marketPrice.get();
    }
    public final void setMarketPrice(Double v) {
        marketPrice.set(v);
    }
    public SimpleDoubleProperty marketPriceProperty () {
        return marketPrice;
    }

    public final Double getLimitPrice() {
        return limitPrice.get();
    }
    public final void setLimitPrice(Double v) {
        limitPrice.set(v);
    }
    public SimpleDoubleProperty limitPriceProperty () {
        return limitPrice;
    }
    
    public final Double getStopPrice() {
        return stopPrice.get();
    }
    public final void setStopPrice(Double v) {
        stopPrice.set(v);
    }
    public SimpleDoubleProperty stopPriceProperty () {
        return stopPrice;
    }
}
