/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author shuwnyuan
 */

public class PortfolioService extends ScheduledService<Map<String, Object>> {
    
    private DriveWealthAPI api;
    private Map<String, Object> accBlotter = new HashMap<>();
    private final Map<String, Map> instruments = new HashMap<>();
    private boolean needFullRefresh = true;

    
    public PortfolioService (DriveWealthAPI api) {
        this.api = api;
    }

    public class PortfolioTask extends Task<Map<String, Object>> {
        
        public PortfolioTask() {
        }

        @Override
        protected Map<String, Object> call() throws Exception {
            Map<String, Object> result = new HashMap<>();

            String userID = api.user.userID;
            String accountID = api.user.practiceAccount.accountID;
            if (userID != null && accountID != null) {
                // only call account Blotter & get instruments during first run
                
                System.out.println("1111111111   needFullRefresh: " + needFullRefresh);
                
                if (needFullRefresh == true) {
                    needFullRefresh = false;

                    System.out.println("22222222   needFullRefresh: " + needFullRefresh);
                
                    
                    accBlotter = api.accountBlotter(userID, accountID);              
                    System.out.println("calling account Blotter DONE...");

                    // LOOP: call "get instrument" to get stock name for open positions + pending orders
                    LinkedTreeMap<String, Object> equity = (LinkedTreeMap) accBlotter.get("equity");
                    List<LinkedTreeMap<String, Object>> posList = (List) equity.get("equityPositions");

                    for (LinkedTreeMap<String, Object> pos : posList) {
                        String symbol = pos.get("symbol").toString();
                        if (instruments.containsKey(symbol)) {
                            continue;
                        }

                        Map<String, Object> ins = api.getInstrument(pos.get("instrumentID").toString());
                        instruments.put(symbol, ins);
                    }

                    List<LinkedTreeMap<String, Object>> orders = (List) accBlotter.get("orders");
                    for (LinkedTreeMap<String, Object> ord : orders) {
                        String symbol = ord.get("symbol").toString();
                        if (instruments.containsKey(symbol)) {
                            continue;
                        }

                        Map<String, String> param = new HashMap<>();
                        param.put("symbol", symbol);
                        List<Map<String, Object>> insList = api.searchInstruments(param);

                        for (Map<String, Object> ins : insList) {
                            if (symbol.equals( ins.get("symbol").toString() )) {
                                instruments.put(symbol, ins);
                                break;
                            }
                        }
                    }
                    result.put("accBlotter", accBlotter);
                    result.put("instruments", instruments);

                    System.out.println("DONE calling accBlotter & get instruments for positions / orders...");
                    
                    System.out.println("3333333   needFullRefresh: " + needFullRefresh);
                } else {
                    
                    System.out.println("44444444   needFullRefresh: " + needFullRefresh);

                    
                    // get latest prices for all symbols
                    ArrayList<String> symbols = new ArrayList<>(instruments.keySet());
                    List<Map<String, Object>> priceList = api.getMarketData(symbols, true);

                    Map<String, Double> prices = new HashMap<>();
                    for (Map<String, Object> price : priceList) {
                        prices.put(price.get("symbol").toString(), (Double) price.get("lastTrade"));
                    }

                    result.put("marketPrices", prices);
                    System.out.println("DONE calling get market data for positions / orders...");
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



