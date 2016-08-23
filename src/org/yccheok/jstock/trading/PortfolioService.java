/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private Map<String, Object> accBlotter = new HashMap<>();
    private final Map<String, Map> instruments = new HashMap<>();
    private Set symbolsSet;
    private TaskState taskState = TaskState.ACCBLOTTER;
    private boolean refresh = false;

    private static enum TaskState {
        ACCBLOTTER,
        INSTRUMENTS,
        PRICES;
    }
    
    public synchronized void setRefresh () {
        this.refresh = true;
    }
    
    public synchronized void resetRefresh() {
        this.refresh = false;
    }
    
    public synchronized boolean needRefresh () {
        return (this.refresh == true);
    }
    
    public PortfolioService (DriveWealthAPI api) {
        this.api = api;
    }

    public class PortfolioTask extends Task<Map<String, Object>> {
        
        public PortfolioTask() {}

        private void getAccBlotter (String userID, String accountID) {
            accBlotter = api.accountBlotter(userID, accountID);              
            LinkedTreeMap<String, Object> equity = (LinkedTreeMap) accBlotter.get("equity");
            List<LinkedTreeMap<String, Object>> posList = (List) equity.get("equityPositions");

            symbolsSet = new HashSet();
            for (LinkedTreeMap<String, Object> pos : posList) {
                String symbol = pos.get("symbol").toString();
                symbolsSet.add(symbol);
            }
            
            List<LinkedTreeMap<String, Object>> orders = (List) accBlotter.get("orders");
            for (LinkedTreeMap<String, Object> ord : orders) {
                String symbol = ord.get("symbol").toString();
                symbolsSet.add(symbol);
            }
            
            System.out.println("calling account Blotter DONE...");
        }
        
        public boolean getInstruments () {
            // call "search instrument" to get stocks' name for all symbols
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
        
        @Override
        protected Map<String, Object> call() throws Exception {
            Map<String, Object> result = new HashMap<>();

            String userID = api.user.userID;
            String accountID = api.user.practiceAccount.accountID;
            if (userID != null && accountID != null) {
                // only call account Blotter & get instruments during first run
                if (taskState == TaskState.ACCBLOTTER) {
                    getAccBlotter(userID, accountID);
                    result.put("accBlotter", accBlotter);
                    taskState = TaskState.INSTRUMENTS;
                    
                    System.out.println("DONE calling accBlotter...");
                } else if (taskState == TaskState.INSTRUMENTS) {
                    final boolean updated = getInstruments();
                    result.put("instruments", instruments);
                    result.put("updated", updated);
                    taskState = TaskState.PRICES;

                    System.out.println("DONE calling get instruments for positions & orders...");
                }
                
                // always get latest prices
                Map<String, Double> prices = getMarketPrices();
                result.put("marketPrices", prices);

                System.out.println("DONE calling get market data for positions / orders...");
                
                // This is set to TRUE after Create Order
                if (needRefresh()) {
                    taskState = TaskState.ACCBLOTTER;
                    resetRefresh();
                }
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



