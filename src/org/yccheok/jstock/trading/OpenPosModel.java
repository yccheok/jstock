/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import javafx.beans.property.SimpleStringProperty;
import java.util.Map;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author shuwnyuan
 */

public class OpenPosModel {
    private final SimpleStringProperty symbol;
    private final SimpleStringProperty name;
    private final SimpleStringProperty units;
    private final SimpleStringProperty averagePrice;
    
    //private final SimpleStringProperty costBasis;
    private final SimpleDoubleProperty costBasis;
    
    private final SimpleStringProperty marketPrice;
    private final SimpleStringProperty marketValue;
    private final SimpleStringProperty unrealizedPL;

    private final Double unitsD;
    private final Double averagePriceD;
    private Double marketPriceD;
    public Double marketValueD;
    public Double unrealizedPLD;
    
    public OpenPosModel(Map<String, Object> pos) {
        this.unitsD         = (Double) pos.get("units");
        this.averagePriceD  = (Double) pos.get("averagePrice");
        this.marketPriceD   = (Double) pos.get("marketPrice");
        this.marketValueD   = (Double) pos.get("marketValue");
        this.unrealizedPLD  = (Double) pos.get("unrealizedPL");

        this.symbol         = new SimpleStringProperty(pos.get("symbol").toString());
        this.name           = new SimpleStringProperty(pos.get("name").toString());
        
        //this.costBasis      = new SimpleStringProperty(Utils.monetaryFormat( (Double)pos.get("costBasis") ));
        this.costBasis      = new SimpleDoubleProperty((Double)pos.get("costBasis"));
        
        this.units          = new SimpleStringProperty(Utils.formatNumber(this.unitsD, 2));
        this.averagePrice   = new SimpleStringProperty(Utils.monetaryFormat(this.averagePriceD));
        this.marketPrice    = new SimpleStringProperty(Utils.monetaryFormat(this.marketPriceD));
        this.marketValue    = new SimpleStringProperty(Utils.monetaryFormat(this.marketValueD));
        this.unrealizedPL   = new SimpleStringProperty(Utils.monetaryFormat(this.unrealizedPLD));
    }

    public void updateMarketPrice (Double price) {
        this.marketPriceD  = price;
        this.marketValueD  = this.unitsD * price;
        this.unrealizedPLD = this.unitsD * (price - this.averagePriceD);

        this.setMarketPrice(Utils.monetaryFormat(this.marketPriceD));
        this.setMarketValue(Utils.monetaryFormat(this.marketValueD));
        this.setUnrealizedPL(Utils.monetaryFormat(this.unrealizedPLD));
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

    public final String getUnits() {
        return units.get();
    }
    public final void setUnits(String v) {
        units.set(v);
    }
    public SimpleStringProperty unitsProperty() {
        return units;
    }

    public final String getAveragePrice() {
        return averagePrice.get();
    }
    public final void setAveragePrice(String v) {
        averagePrice.set(v);
    }
    public SimpleStringProperty averagePriceProperty() {
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
    
    public final String getMarketPrice() {
        return marketPrice.get();
    }
    public final void setMarketPrice(String v) {
        marketPrice.set(v);
    }
    public SimpleStringProperty marketPriceProperty() {
        return marketPrice;
    }

    public final String getMarketValue() {
        return marketValue.get();
    }
    public final void setMarketValue(String v) {
        marketValue.set(v);
    }
    public SimpleStringProperty marketValueProperty() {
        return marketValue;
    }

    public final String getUnrealizedPL() {
        return unrealizedPL.get();
    }
    public final void setUnrealizedPL(String v) {
        unrealizedPL.set(v);
    }
    public SimpleStringProperty unrealizedPLProperty() {
        return unrealizedPL;
    }

}


