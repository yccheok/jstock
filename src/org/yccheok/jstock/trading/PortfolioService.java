/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author shuwnyuan
 */

public class PortfolioService extends ScheduledService<Map<String, Object>> {
    
    private final DriveWealthAPI api;
    
    private List<OpenPosModel> posList = new ArrayList<>();
    private List<OrderModel> ordList = new ArrayList<>();
    private AccountModel accModel;
    
    private final Map<String, Map> instruments = new HashMap<>();
    private Set symbolsSet;
    private TaskState taskState = TaskState.ACC_BLOTTER;
    private boolean refresh = false;

    public static enum TaskState {
        ACC_BLOTTER,
        INSTRUMENTS,
        PRICES;
    }
    
    // Check for condition to trigger accBlotter => Refresh Portfolio - positions + order table + acc summary
    // should be done by another task? Timer task?
    // should this class keep a list of pending orders? Check periodically for order Status change
    // Class OrdersBuilder (accBlotter) => HashMap <orderID, order>
    // order status => HashMap <orderID, status>
    // a boolean flag => changed
    // collection of Orders
    // rebuild on each accBlotter call

    
    
    public synchronized void setRefresh () {
        this.refresh = true;
    }
    
    public synchronized void resetRefresh() {
        this.refresh = false;
    }
    
    public PortfolioService (DriveWealthAPI api) {
        this.api = api;
    }

    public class PortfolioTask extends Task<Map<String, Object>> {
        
        public PortfolioTask() {}

        private void getAccBlotter (String userID, String accountID) {
            AccountBlotter accBlot = api.accountBlotter(userID, accountID);
            
            // List of positions (OpenPosModel) & pending oders (OrderModel)
            posList = accBlot.getPositions();
            ordList = accBlot.getOrders();
            accModel = accBlot.getAccount();
            symbolsSet = accBlot.getSymbols();
            
            System.out.println("calling account Blotter DONE...");
        }
        
        public boolean getInstruments () {
            // call "search instrument" to get stocks name for all symbols
            boolean updated = false;
            Iterator<String> itr = symbolsSet.iterator();

            while (itr.hasNext()) {
                String symbol = itr.next();
                
                if (instruments.containsKey(symbol)) {
                    continue;
                }
                
                Map<String, String> param = new HashMap<>();
                // only search for exact symbol match
                param.put("symbols", symbol);
                List<Map<String, Object>> insList = api.searchInstruments(param);

                if (insList.size() > 0) {
                    Map<String, Object> ins = insList.get(0);
                    instruments.put(symbol, ins);
                    updated = true;
                }
            }
            
            return updated;
        }
        
        private Map<String, Double> getMarketPrices () {
            // get latest prices for all symbols
            ArrayList<String> symbols = new ArrayList<>(symbolsSet);
            List<Map<String, Object>> priceList = api.getMarketData(symbols, true);

            Map<String, Double> prices = new HashMap<>();
            for (Map<String, Object> price : priceList) {
                prices.put(price.get("symbol").toString(), (Double) price.get("lastTrade"));
            }
            
            return prices;
        }
        
        private void updateTaskState () {
            if (taskState == TaskState.PRICES) {
                return;
            }

            if (taskState == TaskState.ACC_BLOTTER) {
                taskState = TaskState.INSTRUMENTS;
            } else if (taskState == TaskState.INSTRUMENTS) {
                taskState = TaskState.PRICES;
            }
        }
        
        private void checkResetState () {
            // Force Portfolio Refresh, by changing state => AccBlotter. Eg: After create order successfully
            if (refresh == true) {
                taskState = TaskState.ACC_BLOTTER;
                resetRefresh();
            }
        }
        
        @Override
        protected Map<String, Object> call() throws Exception {
            checkResetState();
            
            Map<String, Object> result = new HashMap<>();

            String userID = api.user.userID;
            String accountID = api.user.practiceAccount.accountID;
            
            if (userID != null && accountID != null) {
                result.put("state", taskState);
                
                // Not calling Account Blotter & get instruments on every iteration
                if (taskState == TaskState.ACC_BLOTTER) {
                    getAccBlotter(userID, accountID);

                    result.put("posList", posList);
                    result.put("ordList", ordList);
                    result.put("accModel", accModel);

                    System.out.println("DONE calling accBlotter...");
                } else if (taskState == TaskState.INSTRUMENTS) {
                    final boolean updated = getInstruments();
                    
                    result.put("instruments", instruments);
                    result.put("updated", updated);

                    System.out.println("DONE calling get instruments...");
                }
                
                // always get latest prices
                Map<String, Double> prices = getMarketPrices();
                result.put("marketPrices", prices);

                updateTaskState();
                
                System.out.println("DONE calling get market prices...");
            }
            return result;
        }
    }
    
    @Override
    protected Task<Map<String, Object>> createTask() {
        final PortfolioTask portfolioTask = new PortfolioTask();
        return portfolioTask;
    }

}



