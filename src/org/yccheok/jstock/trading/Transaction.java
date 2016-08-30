/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.Map;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.trading.API.OrderManager;


/**
 *
 * @author shuwnyuan
 */
public class Transaction {

    private Transaction () {}

    public static void buy (PortfolioService portfolioService, Map<String, Object> params) {
        System.out.println("Buy market order...");

        // temporary stop Portfolio Scheduled Service
        portfolioService.cancel();

        Task buyTask = createBuyTask(params);
        setSucceedHandler(buyTask, portfolioService);

        Thread buyThread = new Thread(buyTask);
        buyThread.start();
    }

    private static Task createBuyTask (final Map<String, Object> params) {
        Task buyTask = new Task<Pair<OrderManager.Order, String>>() {
            @Override protected Pair<OrderManager.Order, String> call() throws Exception {
                System.out.println("BuyTask call create order .....");

                // Create Order
                Pair<OrderManager.Order, String> createOrder = OrderManager.create(OrderManager.OrderSide.BUY, OrderManager.OrderType.MARKET, params);
                OrderManager.Order order = createOrder.first;
                String error = createOrder.second;

                if (error != null) {
                    System.out.println("BUY market order failed....");
                    updateMessage("Create Market Order Status FAILED !!");

                    return new Pair<>(null, error);
                }

                String orderID = order.getOrderID();

                // Get Order Status
                order = OrderManager.status(orderID);
                OrderManager.OrdStatus ordStatus = order.getOrdStatusEnum();
                
                updateMessage("Market Order Status: " + ordStatus.getValue() + " - " + ordStatus.getName());                
                System.out.println("BuyTask call get order status DONE, order status: "
                        + ordStatus.getValue() + " - " + ordStatus.getName());

                return new Pair<>(order, null);
            }
        };

        return buyTask;
    }
    
    
    private static void setSucceedHandler (Task buyTask, PortfolioService portfolioService) {
        buyTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Pair<OrderManager.Order, String> result = (Pair) workerStateEvent.getSource().getValue();

                OrderManager.Order order = result.first;
                String error = result.second;
                
                System.out.println("buy Task succeeded....");
                
                if (error != null) {
                    System.out.println("BUY market order ERROR: " + error);
                } else {
                    OrderManager.OrdStatus ordStatus = order.getOrdStatusEnum();
                    
                    if (ordStatus == OrderManager.OrdStatus.REJECTED) {
                        System.out.println("BUY market order REJECTED....");
                    }
                    
                    // status: NEW, PARTIAL_FILLED, FILLED, CANCELLED
                    // trigger PortfolioService to refresh => call accBlotter
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

                    System.out.println("Successfully BUY MARKET order, " +
                        " instrumentID: "   + instrumentID + 
                        " orderID: "        + orderID +
                        " orderNo: "        + orderNo +
                        " orderQty: "       + orderQty + 
                        " orderType: "      + ordType +
                        " side: "           + side +
                        " accountType: "    + accountType + 
                        " grossTradeAmt: "  + grossTradeAmt +
                        " commission: "     + commission + 
                        " status: "         + status
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
}
