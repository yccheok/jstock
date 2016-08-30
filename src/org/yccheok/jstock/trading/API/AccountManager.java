/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;


import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.OrderModel;
import org.yccheok.jstock.trading.AccountModel;


/**
 *
 * @author shuwnyuan
 */
public final class AccountManager {

    public static class AccountBlotter {
        private final Map<String, Object> resultMap;
    
        private final List<PositionModel> posList = new ArrayList();
        private final List<OrderModel> ordList = new ArrayList();
        private AccountModel accModel;
    
        private final Set symbolsSet = new HashSet();

    
        public AccountBlotter (Map<String, Object> params) {
            this.resultMap = params;
        }
        
        public List<PositionModel> getPositions () {
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

                this.posList.add(new PositionModel(data));
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
                final OrderManager.OrderSide ordSide;
                String side = ord.get("side").toString();

                if (side.equals("B")) {
                    ordSide = OrderManager.OrderSide.BUY;
                } else {
                    ordSide = OrderManager.OrderSide.SELL;
                }
                data.put("side", ordSide);

                // Order Type: Market, Limit, Stop
                final OrderManager.OrderType ordType;
                String type = ord.get("orderType").toString();

                if (type.equals("2")) {
                    ordType = OrderManager.OrderType.LIMIT;
                    data.put("limitPrice", ord.get("limitPrice"));
                } else if (type.equals("3")) {
                    ordType = OrderManager.OrderType.STOP;
                    data.put("stopPrice", ord.get("stopPrice"));
                } else {
                    ordType = OrderManager.OrderType.MARKET;
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
            for (PositionModel pos : getPositions()) {
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

            for (PositionModel pos : getPositions()) {
                symbolsSet.add(pos.getSymbol());
            }
            for (OrderModel ord : getOrders()) {
                symbolsSet.add(ord.getSymbol());
            }

            return this.symbolsSet;
        }

        public double getAvailableTradingQty (String instrumentID) {
            double qty = 0;
            for (PositionModel pos : getPositions()) {
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

    public static AccountBlotter blotter (String userID, String accountID) {
        String url = "users/" + userID + "/accountSummary/" + accountID;
        
        List<String> RESULT_FIELDS = new ArrayList<>(Arrays.asList(
            "accountID",
            "accountNo",
            "equity",
            "cash",
            "orders",
            "transactions"
        ));

        Map<String, Object> respondMap = DriveWealth.executeGet(url, DriveWealth.getSessionKey());
        Map<String, Object> result = new Gson().fromJson(respondMap.get("respond").toString(), HashMap.class);

        // debugging only
        for (String k: RESULT_FIELDS) {
            if (result.containsKey(k)) {
                Object v = result.get(k);
                //System.out.println("key: " + k + ", value: " + v);
            }
        }
        
        return new AccountBlotter(result);
    }

    
}
