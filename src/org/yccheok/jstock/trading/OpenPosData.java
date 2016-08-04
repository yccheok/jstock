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

public class OpenPosData {
    private final SimpleStringProperty symbol;
    private final SimpleStringProperty name;
    private final SimpleStringProperty units;
    private final SimpleStringProperty averagePrice;
    private final SimpleStringProperty costBasis;
    private final SimpleStringProperty marketPrice;
    private final SimpleStringProperty marketValue;
    private final SimpleStringProperty unrealizedPL;

    public OpenPosData(OpenPos pos) {
        this.symbol         = new SimpleStringProperty(pos.symbol);
        this.name           = new SimpleStringProperty(pos.name);
        this.units          = new SimpleStringProperty(Utility.formatNumber(pos.units));
        this.averagePrice   = new SimpleStringProperty(Utility.formatNumber(pos.averagePrice));
        this.costBasis      = new SimpleStringProperty(Utility.formatNumber(pos.costBasis));
        this.marketPrice    = new SimpleStringProperty(Utility.formatNumber(pos.marketPrice));
        this.marketValue    = new SimpleStringProperty(Utility.formatNumber(pos.marketValue));
        this.unrealizedPL   = new SimpleStringProperty(Utility.formatNumber(pos.unrealizedPL));
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

    public String getAveragePrice() {
        return averagePrice.get();
    }
    public void setAveragePrice(String v) {
        averagePrice.set(v);
    }

    public String getCostBasis() {
        return costBasis.get();
    }
    public void setCostBasis(String v) {
        costBasis.set(v);
    }

    public String getMarketPrice() {
        return marketPrice.get();
    }
    public void setMarketPrice(String v) {
        marketPrice.set(v);
    }

    public String getMarketValue() {
        return marketValue.get();
    }
    public void setMarketValue(String v) {
        marketValue.set(v);
    }

    public String getUnrealizedPL() {
        return unrealizedPL.get();
    }
    public void setUnrealizedPL(String v) {
        unrealizedPL.set(v);
    }
}


