/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;


import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author shuwnyuan
 */
public class AccountBlotter {
    
    private final DriveWealthAPI api;
    private final String userID;
    private final String accountID;
    private final String url;
    private final Map<String, Object> resultMap = new HashMap<>();
    
    private final List<OpenPosModel> posList = new ArrayList();
    private final List<OrderModel> ordList = new ArrayList();
    private AccountModel accModel;
    
    private final Set symbolsSet = new HashSet();

    private static final List<String> resultFields = new ArrayList<>(Arrays.asList(
        "accountID",
        "accountNo",
        "equity",
        "cash",
        "orders",
        "transactions"
    ));
    
    
    public AccountBlotter (DriveWealthAPI api, String userID, String accountID) {
        this.api = api;
        this.userID = userID;
        this.accountID = accountID;
        this.url = "users/" + this.userID + "/accountSummary/" + this.accountID;
        
        Map<String, Object> respondMap = DriveWealthAPI.executeGet(this.url, this.api.getSessionKey());
        Map<String, Object> result = new Gson().fromJson(respondMap.get("respond").toString(), HashMap.class);

        for (String k: resultFields) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                this.resultMap.put(k, v);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
    }
    
    public Map<String, Object> getResultMap () {
        return this.resultMap;
    }
    
    public List<OpenPosModel> getPositions () {
        if (! this.posList.isEmpty()) {
            return this.posList;
        }

        LinkedTreeMap<String, Object> equity = (LinkedTreeMap) this.resultMap.get("equity");
        List<LinkedTreeMap<String, Object>> positions = (List) equity.get("equityPositions");
        
        for (LinkedTreeMap<String, Object> pos : positions) {
            final String symbol = pos.get("symbol").toString();
            
            Map<String, Object> data = new HashMap<>();
            
            // position only has symbol, but no stock name
            data.put("name",            "");
            data.put("symbol",          symbol);
            data.put("instrumentID",    pos.get("instrumentID"));
            data.put("openQty",         pos.get("openQty"));
            data.put("tradingQty",      pos.get("availableForTradingQty"));
            data.put("averagePrice",    pos.get("avgPrice"));
            data.put("costBasis",       pos.get("costBasis"));
            data.put("marketPrice",     pos.get("mktPrice"));
            data.put("marketValue",     pos.get("marketValue"));
            data.put("unrealizedPL",    pos.get("unrealizedPL"));
            
            this.posList.add(new OpenPosModel(data));
        }
        
        return this.posList;
    }

    public List<OrderModel> getOrders () {
        if (! this.ordList.isEmpty()) {
            return this.ordList;
        }

        List<LinkedTreeMap<String, Object>> orders = (List) this.resultMap.get("orders");
        
        for (LinkedTreeMap<String, Object> ord : orders) {
            final String symbol = ord.get("symbol").toString();
            
            Map<String, Object> data = new HashMap<>();
            
            // orders from accBlotter don't have stock name & market price
            data.put("name",        "");
            data.put("marketPrice", 0.0);
            
            data.put("symbol",      symbol);
            data.put("units",       ord.get("orderQty"));
            
            // side: BUY, SELL
            final CreateOrder.OrderSide ordSide;
            String side = ord.get("side").toString();

            if (side.equals("B")) {
                ordSide = CreateOrder.OrderSide.BUY;
            } else {
                ordSide = CreateOrder.OrderSide.SELL;
            }
            data.put("side", ordSide);
            
            // Order Type: Market, Limit, Stop
            final CreateOrder.OrderType ordType;
            String type = ord.get("orderType").toString();

            if (type.equals("2")) {
                ordType = CreateOrder.OrderType.LIMIT;
                data.put("limitPrice", ord.get("limitPrice"));
            } else if (type.equals("3")) {
                ordType = CreateOrder.OrderType.STOP;
                data.put("stopPrice", ord.get("stopPrice"));
            } else {
                ordType = CreateOrder.OrderType.MARKET;
            }
            data.put("orderType", ordType);

            this.ordList.add(new OrderModel(data));
        }
        
        return this.ordList;
    }
    
    public AccountModel getAccount () {
        LinkedTreeMap<String, Object> equity  = (LinkedTreeMap) this.resultMap.get("equity");
        LinkedTreeMap<String, Object> balance = (LinkedTreeMap) this.resultMap.get("cash");

        Map<String, Object> params = new HashMap<>();
        
        params.put("equity",            equity.get("equityValue"));
        params.put("cashBalance",       balance.get("cashBalance"));
        params.put("cashForTrade",      balance.get("cashAvailableForTrade"));
        params.put("cashForWithdraw",   balance.get("cashAvailableForWithdrawal"));
        
        double totalUnrealizedPL = 0.0;
        for (OpenPosModel pos : getPositions()) {
            totalUnrealizedPL += pos.getUnrealizedPL();
        }
        params.put("totalUnrealizedPL", totalUnrealizedPL);
        
        this.accModel = new AccountModel(params);
        return this.accModel;
    }
    
    public Set getSymbols () {
        if (! this.symbolsSet.isEmpty()) {
            return this.symbolsSet;
        }

        for (OpenPosModel pos : getPositions()) {
            symbolsSet.add(pos.getSymbol());
        }
        for (OrderModel ord : getOrders()) {
            symbolsSet.add(ord.getSymbol());
        }

        return this.symbolsSet;
    }

    public double getAvailableTradingQty (String instrumentID) {
        double qty = 0;
        for (OpenPosModel pos : getPositions()) {
            if (pos.getInstrumentID().equals(instrumentID)) {
                qty = (double) pos.getTradingQty();
                break;
            }
        }
        return qty;
    }
    
    public double getTradingBalance () {
        LinkedTreeMap<String, Object> cash = (LinkedTreeMap) this.resultMap.get("cash");
        double balance = (double) cash.get("cashAvailableForTrade");
        return balance;
    }
    
}
