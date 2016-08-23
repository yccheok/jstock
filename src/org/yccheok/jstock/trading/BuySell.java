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
        System.out.println("BuySell construtor...");
        
        this.api = api;
        this.portfolioService = portfolioService;
    }

    public void buy (Map<String, Object> params) {
        System.out.println("Calling buy...");

        BuyTask buyTask = new BuyTask(this.api, params);
        Thread buyThread = new Thread(buyTask);
        buyThread.start();
        
        buyTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Map<String, Object> result = (Map) workerStateEvent.getSource().getValue();

                DriveWealthAPI.OrderStatus ordStatus = (DriveWealthAPI.OrderStatus) result.get("ordStatus");
                
                System.out.println("buy Task succeeded....  ordStatus: " + ordStatus);
                
                if (ordStatus == DriveWealthAPI.OrderStatus.ERROR) {
                    System.out.println("BUY market order ERROR....");
                } else if (ordStatus == DriveWealthAPI.OrderStatus.REJECTED) {
                    System.out.println("BUY market order REJECTED....");
                } else {
                    // status: ACCEPTED, FILLED, PARTIALFILLED, CANCELLED
                    // trigger PortfolioService to refresh => call accBlotter

                    Map<String, Object> order = (Map) result.get("order");

                    String instrumentID = order.get("instrumentID").toString();
                    String orderID = order.get("orderID").toString();
                    String orderQty = order.get("orderQty").toString();
                    String comment = order.get("comment").toString();
                    Double commission = (Double) order.get("commission");
                    Double grossTradeAmt = (Double) order.get("grossTradeAmt");
                    String orderNo = order.get("orderNo").toString();
                    String status = order.get("ordStatus").toString();
                    String ordType = order.get("ordType").toString();
                    String side = order.get("side").toString();
                    double accountType = (double) order.get("accountType");

                    System.out.println("Successfully create market order, " +
                        " instrumentID: " + instrumentID + 
                        " orderID: " + orderID +
                        " orderNo: " + orderNo +
                        " orderQty: " + orderQty + 
                        " orderType: " + ordType +
                        " side: " + side +
                        " accountType: " + accountType + 
                        " comment: " + comment + 
                        " grossTradeAmt: " + grossTradeAmt +
                        " commission: " + commission + 
                        " status: " + status
                    );

                    portfolioService.setRefresh();
                    portfolioService.cancel();
                    portfolioService.restart();
                    
                }
            }
        });
    }
    
    public class BuyTask extends Task<Map<String, Object>> {
        private final DriveWealthAPI api;
        private final Map<String, Object> params;
        private Map<String, Object> order;

        
        public BuyTask (DriveWealthAPI api, Map<String, Object> params) {
            System.out.println("BuyTask constructor.....");
            
            this.api = api;
            this.params = params;
        }
        
        @Override protected Map<String, Object> call() throws Exception {
            
            System.out.println("BuyTask call create order .....");
            
            order = api.createOrder("buy", "market", params);

            Map<String, Object> result = new HashMap<>();
            
            if (!order.containsKey("orderID")) {
                System.out.println("BUY market order failed....");
                updateMessage("Create Market Order Status FAILED !!");
                
                result.put("ordStatus", DriveWealthAPI.OrderStatus.ERROR);
                return result;
            }
            
            String orderID = order.get("orderID").toString();
            
            System.out.println("BuyTask call get order status, orderID: " + orderID);
            
            order = api.orderStatus(orderID);
            DriveWealthAPI.OrderStatus ordStatus = (DriveWealthAPI.OrderStatus) order.get("ordStatus");

            
            System.out.println("BuyTask call get order status DONE, order status: " + ordStatus);
            

            updateMessage("Market Order Status: " + ordStatus);
            result.put("order", order);
            result.put("ordStatus", ordStatus);
            
            return result;
        }
    }
}
