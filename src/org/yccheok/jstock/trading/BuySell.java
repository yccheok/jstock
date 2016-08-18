/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.HashMap;
import java.util.Map;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 *
 * @author shuwnyuan
 */
public class BuySell {
    private final DriveWealthAPI api;
    private final PortfolioService portfolioService;
    
    public BuySell (DriveWealthAPI api, PortfolioService portfolioService) {
        this.api = api;
        this.portfolioService = portfolioService;
    }

    public void buy (Map<String, Object> params) {
        BuyTask buyTask = new BuyTask(this.api, params);
        Thread thead = new Thread(buyTask);
        thead.start();
        
        buyTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Map<String, Object> result = (Map<String, Object>) workerStateEvent.getSource().getValue();
                
                if (result.containsKey("ordStatus")) {
                    DriveWealthAPI.OrderStatus ordStatus = (DriveWealthAPI.OrderStatus) result.get("ordStatus");
                    
                    if (ordStatus == DriveWealthAPI.OrderStatus.ERROR) {
                        System.out.println("BUY market order ERROR....");
                    } else if (ordStatus == DriveWealthAPI.OrderStatus.REJECTED) {
                        System.out.println("BUY market order REJECTED....");
                    } else {
                        // status: ACCEPTED, FILLED, PARTIALFILLED, CANCELLED
                        // trigger PortfolioService to refresh Portfolio
                        portfolioService.setRefresh();
                    }
                }
            }
        });
    }
    
    public class BuyTask extends Task<Map<String, Object>> {
        private final DriveWealthAPI api;
        private final Map<String, Object> params;
        private Map<String, Object> order;

        
        public BuyTask (DriveWealthAPI api, Map<String, Object> params) {
            this.api = api;
            this.params = params;
        }
        
        @Override protected Map<String, Object> call() throws Exception {
            order = api.createOrder("buy", "market", params);

            Map<String, Object> result = new HashMap<>();
            
            if (!order.containsKey("orderID")) {
                System.out.println("BUY market order failed....");
                result.put("ordStatus", DriveWealthAPI.OrderStatus.ERROR);
            }
            
            String orderID = order.get("orderID").toString();

            // check order status: Decide how many times / how long to check status in LOOP
            
            // 1) call get instrument, if now is not trading time, order is accepted for next trading day
            //      Once status = ACCEPTED, return TRUE
            // 2) If now is trading time, should loop through
            
            Map<String, Object> order = api.orderStatus(orderID);
            DriveWealthAPI.OrderStatus ordStatus = (DriveWealthAPI.OrderStatus) order.get("ordStatus");

            updateMessage("Market Order Status: " + ordStatus);
            result.put("order", order);
            result.put("ordStatus", ordStatus);
            
            return result;
        }
    }

}
