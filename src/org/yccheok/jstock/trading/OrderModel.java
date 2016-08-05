/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author shuwnyuan
 */
public class OrderModel {
    private final SimpleStringProperty symbol;
    private final SimpleStringProperty name;
    private final SimpleStringProperty units;
    private final SimpleStringProperty marketPrice;
    private final SimpleStringProperty type;
    private final SimpleStringProperty side;
    private final SimpleStringProperty limitPrice;
    private final SimpleStringProperty stopPrice;
    
    public OrderModel(Order ord) {
        this.symbol         = new SimpleStringProperty(ord.symbol);
        this.name           = new SimpleStringProperty(ord.name);
        this.type           = new SimpleStringProperty(ord.type);
        this.side           = new SimpleStringProperty(ord.side);

        this.units          = new SimpleStringProperty(Utils.formatNumber(ord.units));
        this.marketPrice    = new SimpleStringProperty(Utils.formatNumber(ord.marketPrice));
        this.limitPrice     = new SimpleStringProperty(Utils.formatNumber(ord.limitPrice));
        this.stopPrice      = new SimpleStringProperty(Utils.formatNumber(ord.stopPrice));
    }

    public String getSymbol() {
        return symbol.get();
    }
    public void setSymbol(String v) {
        symbol.set(v);
    }

    public String getName() {
        return name.get();
    }
    public void setName(String v) {
        name.set(v);
    }

    public String getUnits() {
        return units.get();
    }
    public void setUnits(String v) {
        units.set(v);
    }

    public String getMarketPrice() {
        return marketPrice.get();
    }
    public void setMarketPrice(String v) {
        marketPrice.set(v);
    }

    public String getType() {
        return type.get();
    }
    public void setType(String v) {
        type.set(v);
    }
    
    public String getSide() {
        return side.get();
    }
    public void setSide(String v) {
        side.set(v);
    }
    
    public String getLimitPrice() {
        return limitPrice.get();
    }
    public void setLimitPrice(String v) {
        limitPrice.set(v);
    }
    
    public String getStopPrice() {
        return stopPrice.get();
    }
    public void setStopPrice(String v) {
        stopPrice.set(v);
    }
}
