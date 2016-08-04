/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import com.google.gson.internal.LinkedTreeMap;

/**
 *
 * @author shuwnyuan
 */
public class OpenPos {
    public OpenPos (LinkedTreeMap<String, Object> pos, String stockName) {
        this.name           = stockName;
        this.symbol         = pos.get("symbol").toString();
        this.instrumentID   = pos.get("instrumentID").toString();
        this.units          = (Double) pos.get("availableForTradingQty");
        this.averagePrice   = (Double) pos.get("avgPrice");
        this.costBasis      = (Double) pos.get("costBasis");
        this.marketPrice    = (Double) pos.get("mktPrice");
        this.marketValue    = (Double) pos.get("marketValue");
        this.unrealizedPL   = (Double) pos.get("unrealizedPL");
    }

    public String symbol;
    public String name;
    public String instrumentID;
    public Double units;
    public Double averagePrice;
    public Double costBasis;
    public Double marketPrice;
    public Double marketValue;
    public Double unrealizedPL;
}
