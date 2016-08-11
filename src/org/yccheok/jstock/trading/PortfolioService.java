/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import com.google.gson.internal.LinkedTreeMap;
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

    private final DriveWealthAPI api;

    public PortfolioService (DriveWealthAPI api) {
        this.api = api;
    }

    @Override
    protected Task<Map<String, Object>> createTask() {
        
        final Task<Map<String, Object>> portfolioTask = new Task<Map<String, Object>>() {
            @Override
            protected Map<String, Object> call() throws Exception {
                Map<String, Object> result = new HashMap<>();

                String userID = api.user.userID;
                String accountID = api.user.practiceAccount.accountID;
                if (userID != null && accountID != null) {
                    Map<String, Object> accBlotter = api.accountBlotter(userID, accountID);              
                    result.put("accBlotter", accBlotter);
                    System.out.println("calling account Blotter DONE...");

                    // loop through the below, call "get instrument" to get symbol long name
                    //      a) open positions
                    //      b) pending orders
                    Map<String, Map> instruments = new HashMap<>();
                    LinkedTreeMap<String, Object> equity = (LinkedTreeMap) accBlotter.get("equity");
                    List<LinkedTreeMap<String, Object>> posList = (List) equity.get("equityPositions");

                    for (LinkedTreeMap<String, Object> pos : posList) {
                        Map<String, Object> ins = api.getInstrument(pos.get("instrumentID").toString());
                        instruments.put(ins.get("symbol").toString(), ins);
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

                    result.put("instruments", instruments);
                    System.out.println("calling get instruments open positions DONE...");
                }

                return result;
            }
        };
        
        return portfolioTask;
    }
}



