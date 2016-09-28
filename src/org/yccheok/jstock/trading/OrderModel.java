/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;
import org.yccheok.jstock.trading.API.OrderManager;
import static org.yccheok.jstock.trading.PositionModel.SymbolUrl;

/**
 *
 * @author shuwnyuan
 */
public class OrderModel {
    private final SimpleObjectProperty symbolObj;
    private final SimpleStringProperty name;
    private final SimpleStringProperty type;
    private final SimpleStringProperty side;
    
    private final SimpleDoubleProperty units;
    private final SimpleDoubleProperty marketPrice;
    private final SimpleDoubleProperty limitPrice;
    private final SimpleDoubleProperty stopPrice;
    
    public OrderModel(Map<String, Object> ord) {
        this.name   = new SimpleStringProperty(ord.get("name").toString());
        
        Double _stopPrice = 0.0;
        Double _limitPrice = 0.0;

        OrderManager.OrderType ordType = (OrderManager.OrderType) ord.get("orderType");
        if (ordType == OrderManager.OrderType.LIMIT) {
            _limitPrice = (Double) ord.get("limitPrice");
        } else if (ordType == OrderManager.OrderType.STOP) {
            _stopPrice = (Double) ord.get("stopPrice");
        }
        this.type = new SimpleStringProperty(ordType.getName());

        OrderManager.OrderSide ordSide = (OrderManager.OrderSide) ord.get("side");
        this.side = new SimpleStringProperty(ordSide.getName());

        this.units       = new SimpleDoubleProperty((Double) ord.get("units"));
        this.marketPrice = new SimpleDoubleProperty((Double) ord.get("marketPrice"));
        this.limitPrice  = new SimpleDoubleProperty(_limitPrice);
        this.stopPrice   = new SimpleDoubleProperty(_stopPrice);
        
        // symbolObj to represent symbol + icon URL
        String symbolStr    = ord.get("symbol").toString();
        String urlStr       = ord.get("urlImage").toString();
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
    public final String getSymbol() {
        SymbolUrl symbolUrl = (SymbolUrl) this.symbolObj.get();
        return symbolUrl.getSymbol();
    }
    
    // Icon's URL is updated, after Get Instrument call. Reinitialize symbolObj with new URL
    public final void setUrlImage (String v) {
        SymbolUrl symbolUrl = new SymbolUrl(getSymbol(), v);
        this.symbolObj.set(symbolUrl);
    }

    public final String getUrlImage (String v) {
        SymbolUrl symbolUrl = (SymbolUrl) this.symbolObj.get();
        return symbolUrl.getUrl();
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
