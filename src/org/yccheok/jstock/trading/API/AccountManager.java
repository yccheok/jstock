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
import org.yccheok.jstock.trading.AccountSummaryModel;
import static org.yccheok.jstock.trading.API.OrderManager.OrderSide;
import static org.yccheok.jstock.trading.API.OrderManager.OrderType;


/**
 *
 * @author shuwnyuan
 */
public final class AccountManager {

    public static class AccountBlotter {
        private final Map<String, Object> resultMap;
    
        private final List<PositionModel> posList = new ArrayList();
        private final List<OrderModel> ordList = new ArrayList();
        private AccountSummaryModel accModel;
    
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

                // position has NO stock name, icon URL. Will get this via "Search Instrument" in PortfolioService
                data.put("name",            "");
                data.put("urlImage",        "");

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
                // skip if order status:  4 - Cancelled,    8 - Rejected
                String orderStatus = ord.get("orderStatus").toString();
                if (orderStatus.equals("4") || orderStatus.equals("8")) {
                    continue;
                }
                
                Map<String, Object> data = new HashMap<>();

                // orders from accBlotter don't have stock name, icon URL & market price
                data.put("name",        "");
                data.put("urlImage",    "");
                data.put("marketPrice", 0.0);

                data.put("orderID",     ord.get("orderID").toString());
                data.put("symbol",      ord.get("symbol").toString());
                data.put("units",       ord.get("orderQty"));

                // side: BUY, SELL
                final OrderSide ordSide;
                String side = ord.get("side").toString();

                if (side.equals("B")) {
                    ordSide = OrderSide.BUY;
                } else {
                    ordSide = OrderSide.SELL;
                }
                data.put("side", ordSide);

                // Order Type: Market, Limit, Stop
                final OrderType ordType;
                String type = ord.get("orderType").toString();

                switch (type) {
                    case "2":
                        ordType = OrderType.LIMIT;
                        data.put("limitPrice", ord.get("limitPrice"));
                        break;
                    case "3":
                        ordType = OrderType.STOP;
                        data.put("stopPrice", ord.get("stopPrice"));
                        break;
                    default:
                        ordType = OrderType.MARKET;
                        break;
                }
                data.put("orderType", ordType);

                
                
                // debug
                String sym = ord.get("symbol").toString();
                Double qty = (Double) ord.get("orderQty");
                String buysell = ord.get("side").toString();

                Double limit = 0.0;
                Double stop = 0.0;
                
                if (ord.containsKey("limitPrice")) {
                    limit = (Double) ord.get("limitPrice");
                }
                if (ord.containsKey("stopPrice")) {
                    limit = (Double) ord.get("stopPrice");
                }
                
                String typ = ord.get("orderType").toString();

                String st = ord.get("orderStatus").toString();
                
                // 0 - New
                // 1 - partial fill
                // 2 - filled
                // 4 - Cancelled
                // 8 - Rejected

                String status = "";
                switch (st) {
                    case "0":
                        status = "new";
                        break;
                    case "1":
                        status = "partially filled";
                        break;
                    case "2":
                        status = "filled";
                        break;
                    case "4":
                        status = "cancelled";
                        break;
                    case "8":
                        status = "rejected";
                        break;
                    default:
                        break;
                }
                
                System.out.println(
                        
String.format("Pending order: %1$s, %2$s, %3$s, %4$s, %5$s, %6$s, %7$s", status, sym, buysell, typ, qty, limit, stop)

                );
                
                
                
                
                this.ordList.add(new OrderModel(data));
            }

            return this.ordList;
        }
    
        public AccountSummaryModel getAccount () {
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

            this.accModel = new AccountSummaryModel(params);
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

        Map<String, Object> respondMap = Http.get(url, DriveWealth.getSessionKey());
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
