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
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.CreateOrder;
import org.yccheok.jstock.trading.API.OrderStatus;



/**
 *
 * @author shuwnyuan
 */
public class BuySell {
    private final DriveWealth api;
    private final PortfolioService portfolioService;
    
    public BuySell (DriveWealth api, PortfolioService portfolioService) {
        System.out.println("BuySell construtor...");
        
        this.api = api;
        this.portfolioService = portfolioService;
    }

    public void buy (Map<String, Object> params) {
        System.out.println("Calling buy...");

        // temporary stop Portfolio Scheduled Service
        portfolioService.cancel();
        
        BuyTask buyTask = new BuyTask(this.api, params);
        Thread buyThread = new Thread(buyTask);
        buyThread.start();
        
        buyTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Map<String, Object> result = (Map) workerStateEvent.getSource().getValue();

                OrderStatus.OrdStatus ordStatus = (OrderStatus.OrdStatus) result.get("ordStatus");
                
                System.out.println("buy Task succeeded....  ordStatus: " + ordStatus);
                
                if (ordStatus == OrderStatus.OrdStatus.VALIDATION_ERROR) {
                    String error = result.get("error").toString();
                    System.out.println("BUY market order VALIDATION ERROR: " + error);
                } else if (ordStatus == OrderStatus.OrdStatus.ERROR) {
                    System.out.println("BUY market order ERROR: UNKNOWN");
                } else if (ordStatus == OrderStatus.OrdStatus.REJECTED) {
                    System.out.println("BUY market order REJECTED....");
                } else {
                    // status: ACCEPTED, FILLED, PARTIAL_FILLED, CANCELLED
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

                    // resume service, reset to AccBlotter state to refresh Portfolio, as new position / order has been added
                    portfolioService.setRefresh();

                    // service restart must be called in FX application thread. Since task event handler occurs in JavaFX application thread
                    // so no need to wrap in Platfrom.runLater()
                    
                    // http://stackoverflow.com/questions/39100941/javafx-task-eventhandler-handled-in-which-thread/39101113#39101113
                    
                    // Because the Task is designed for use with JavaFX GUI applications, it ensures that every change
                    // to its public properties, as well as change notifications for state, errors, and for event handlers,
                    // all occur on the main JavaFX application thread
                    
                    portfolioService.restart();
                }
            }
        });
    }
    
    private class BuyTask extends Task<Map<String, Object>> {
        private final DriveWealth api;
        private final Map<String, Object> params;

        public BuyTask (DriveWealth api, Map<String, Object> params) {
            System.out.println("BuyTask constructor.....");
            
            this.api = api;
            this.params = params;
        }
        
        @Override protected Map<String, Object> call() throws Exception {
            System.out.println("BuyTask call create order .....");

            // Create Order
            Map<String, Object> order = api.createOrder(CreateOrder.OrderSide.BUY, CreateOrder.OrderType.MARKET, params);

            Map<String, Object> result = new HashMap<>();
            OrderStatus.OrdStatus ordStatus;
            
            if (! order.containsKey("orderID")) {
                System.out.println("BUY market order failed....");
                updateMessage("Create Market Order Status FAILED !!");

                String error = null;
                if (order.containsKey("validationError")) {
                    // validation error
                    ordStatus = OrderStatus.OrdStatus.VALIDATION_ERROR;
                    error = order.get("validationError").toString();
                } else {
                    // Unknown error, failed to create order
                    ordStatus = OrderStatus.OrdStatus.ERROR;
                }

                result.put("ordStatus", ordStatus);
                result.put("error", error);
                
                return result;
            }

            String orderID = order.get("orderID").toString();

            System.out.println("BuyTask call get order status, orderID: " + orderID);

            
            // Get Order Status
            order = api.orderStatus(orderID);
            ordStatus = (OrderStatus.OrdStatus) order.get("ordStatus");
            
            
            System.out.println("BuyTask call get order status DONE, order status: " + ordStatus);

            
            updateMessage("Market Order Status: " + ordStatus);
            result.put("order", order);
            result.put("ordStatus", ordStatus);
            
            return result;
        }
    }
}
