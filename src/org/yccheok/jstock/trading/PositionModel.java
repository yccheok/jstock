/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author shuwnyuan
 */

public class PositionModel {
    
    public static class SymbolUrl {
        private String symbol;
        private String url;
        
        public SymbolUrl (String symbol, String url) {
            this.symbol = symbol;
            this.url = url;
        }

        public String getSymbol () {
            return symbol;
        }
        public void setSymbol (String symbol) {
            this.symbol = symbol;
        }
        
        public String getUrl () {
            return url;
        }
        public void setUrl (String url) {
            this.url = url;
        }
    }
    
    private final SimpleObjectProperty symbolObj;
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

        String symbolStr    = pos.get("symbol").toString();
        String urlStr       = pos.get("urlImage").toString();
        this.symbolObj      = new SimpleObjectProperty();
        symbolObj.set(new SymbolUrl(symbolStr, urlStr));
    }

    // symbol + Image URL
    public void setSymbolObj (SymbolUrl sym) {
        symbolObj.set(sym);
    }

    public Object getSymbolObj () {
        return symbolObj.get();
    }

    public SimpleObjectProperty symbolObjProperty () {
        return symbolObj;
    }

    // get symbol from symbolObj
    public final String getSymbol () {
        SymbolUrl symbolUrl = (SymbolUrl) getSymbolObj();
        return symbolUrl.getSymbol();
    }

    // Icon's URL is updated, after Get Instrument call. Reinitialize symbolObj with new URL
    public final void setUrlImage (String v) {
        SymbolUrl symbolUrl = new SymbolUrl(getSymbol(), v);
        setSymbolObj(symbolUrl);
    }
    
    public final String getUrlImage () {
        SymbolUrl symbolUrl = (SymbolUrl) this.symbolObj.get();
        return symbolUrl.getUrl();
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


