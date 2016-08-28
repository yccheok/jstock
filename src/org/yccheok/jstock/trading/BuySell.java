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
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.OrderManager;


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

                OrderManager.OrdStatus ordStatus = (OrderManager.OrdStatus) result.get("ordStatus");
                
                System.out.println("buy Task succeeded....  ordStatus: " + ordStatus);
                
                if (ordStatus == OrderManager.OrdStatus.ERROR) {
                    String error = result.get("error").toString();
                    System.out.println("BUY market order ERROR: " + error);
                } else if (ordStatus == OrderManager.OrdStatus.REJECTED) {
                    System.out.println("BUY market order REJECTED....");
                } else {
                    // status: ACCEPTED, FILLED, PARTIAL_FILLED, CANCELLED
                    // trigger PortfolioService to refresh => call accBlotter

                    OrderManager.Order order = (OrderManager.Order) result.get("order");

                    String instrumentID     = order.getInstrumentID();
                    String orderID          = order.getOrderID();
                    Double grossTradeAmt    = order.getGrossTradeAmt();
                    String orderNo          = order.getOrderNo();
                    String status           = order.getOrdStatus();
                    String ordType          = order.getOrdType();
                    String side             = order.getSide();
                    Double accountType      = order.getAccountType();
                    Double orderQty         = order.getOrderQty();
                    Double commission       = order.getCommission();
                    
                    System.out.println("Successfully create market order, " +
                        " instrumentID: " + instrumentID + 
                        " orderID: " + orderID +
                        " orderNo: " + orderNo +
                        " orderQty: " + orderQty + 
                        " orderType: " + ordType +
                        " side: " + side +
                        " accountType: " + accountType + 
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
            Pair<OrderManager.Order, String> createOrder = OrderManager.create(api, OrderManager.OrderSide.BUY, OrderManager.OrderType.MARKET, params);
            OrderManager.Order order = createOrder.first;
            String error = createOrder.second;

            Map<String, Object> result = new HashMap<>();

            if (error != null) {
                System.out.println("BUY market order failed....");
                updateMessage("Create Market Order Status FAILED !!");

                result.put("ordStatus", OrderManager.OrdStatus.ERROR);
                result.put("error", error);
                
                return result;
            }

            String orderID = order.getOrderID();

            System.out.println("BuyTask call get order status, orderID: " + orderID);
            
            // Get Order Status
            Pair<OrderManager.Order, OrderManager.OrdStatus> orderStatus = OrderManager.status(api, orderID);

            order = orderStatus.first;
            OrderManager.OrdStatus ordStatus = orderStatus.second;
            
            updateMessage("Market Order Status: " + ordStatus);
            result.put("ordStatus", ordStatus);
            result.put("order", order);

            System.out.println("BuyTask call get order status DONE, order status: " + ordStatus);

            return result;
        }
    }
}
