/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author shuwnyuan
 */

public class PositionModel {
    private final SimpleStringProperty symbol;
    private final SimpleStringProperty name;
    private final SimpleStringProperty instrumentID;
    
    private final SimpleDoubleProperty openQty;
    private final SimpleDoubleProperty tradingQty;
    private final SimpleDoubleProperty averagePrice;
    private final SimpleDoubleProperty costBasis;
    private final SimpleDoubleProperty marketPrice;
    private final SimpleDoubleProperty marketValue;
    private final SimpleDoubleProperty unrealizedPL;

    
    public PositionModel(Map<String, Object> pos) {
        this.symbol         = new SimpleStringProperty(pos.get("symbol").toString());
        this.name           = new SimpleStringProperty(pos.get("name").toString());
        this.instrumentID   = new SimpleStringProperty(pos.get("instrumentID").toString());
        
        this.costBasis      = new SimpleDoubleProperty((Double) pos.get("costBasis"));
        this.openQty        = new SimpleDoubleProperty((Double) pos.get("openQty"));
        this.tradingQty     = new SimpleDoubleProperty((Double) pos.get("tradingQty"));

        this.averagePrice   = new SimpleDoubleProperty((Double) pos.get("averagePrice"));
        this.marketPrice    = new SimpleDoubleProperty((Double) pos.get("marketPrice"));
        
        this.marketValue    = new SimpleDoubleProperty();
        this.marketValue.bind(this.openQty.multiply(this.marketPrice));
        
        this.unrealizedPL   = new SimpleDoubleProperty();
        this.unrealizedPL.bind(this.openQty.multiply(this.marketPrice.subtract(this.averagePrice)));
    }

    public final String getSymbol() {
        return symbol.get();
    }
    public final void setSymbol(String v) {
        symbol.set(v);
    }
    public SimpleStringProperty symbolProperty() {
        return symbol;
    }
    
    public final String getName() {
        return name.get();
    }
    public final void setName(String v) {
        name.set(v);
    }
    public SimpleStringProperty nameProperty() {
        return name;
    }

    public final String getInstrumentID() {
        return instrumentID.get();
    }
    public final void setInstrumentID(String v) {
        instrumentID.set(v);
    }
    public SimpleStringProperty instrumentIDProperty() {
        return instrumentID;
    }
    
    public final Double getOpenQty() {
        return openQty.get();
    }
    public final void setOpenQty(Double v) {
        openQty.set(v);
    }
    public SimpleDoubleProperty openQtyProperty() {
        return openQty;
    }

    public final Double getTradingQty() {
        return tradingQty.get();
    }
    public final void setTradingQty(Double v) {
        tradingQty.set(v);
    }
    public SimpleDoubleProperty tradingQtyProperty() {
        return tradingQty;
    }

    public final Double getAveragePrice() {
        return averagePrice.get();
    }
    public final void setAveragePrice(Double v) {
        averagePrice.set(v);
    }
    public SimpleDoubleProperty averagePriceProperty() {
        return averagePrice;
    }

    public final Double getCostBasis() {
        return costBasis.get();
    }
    public final void setCostBasis(Double v) {
        costBasis.set(v);
    }
    public SimpleDoubleProperty costBasisProperty() {
        return costBasis;
    }
    
    public final Double getMarketPrice() {
        return marketPrice.get();
    }
    public final void setMarketPrice(Double v) {
        marketPrice.set(v);
    }
    public SimpleDoubleProperty marketPriceProperty() {
        return marketPrice;
    }

    public final Double getMarketValue() {
        return marketValue.get();
    }
    public final void setMarketValue(Double v) {
        marketValue.set(v);
    }
    public SimpleDoubleProperty marketValueProperty() {
        return marketValue;
    }

    public final Double getUnrealizedPL() {
        return unrealizedPL.get();
    }
    public final void setUnrealizedPL(Double v) {
        unrealizedPL.set(v);
    }
    public SimpleDoubleProperty unrealizedPLProperty() {
        return unrealizedPL;
    }

}

