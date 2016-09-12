/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.Map;
import javafx.concurrent.Task;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.trading.API.OrderManager;


/**
 *
 * @author shuwnyuan
 */
public class Transaction {

    private Transaction () {}

    public static Task startBuyThread (OrderManager.OrderType orderType, Map<String, Object> params) {
        System.out.println("Start Buy order thread ....");

        Task buyTask = createBuyTask(orderType, params);
        Thread buyThread = new Thread(buyTask);
        buyThread.start();
        
        return buyTask;
    }

    private static Task createBuyTask (OrderManager.OrderType orderType, final Map<String, Object> params) {
        Task buyTask = new Task<Pair<OrderManager.Order, String>>() {
            @Override protected Pair<OrderManager.Order, String> call() throws Exception {
                System.out.println("BuyTask call create order .....");

                // Create Order
                Pair<OrderManager.Order, String> createOrder = OrderManager.create(OrderManager.OrderSide.BUY, orderType, params);
                OrderManager.Order order = createOrder.first;
                String error = createOrder.second;

                if (error != null) {
                    String ordName = orderType.getName();
                    
                    System.out.println("BUY " + ordName + " order failed....");
                    updateMessage("Create " + ordName + " Order Status FAILED !!");

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
        
}
