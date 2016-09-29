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
import static org.yccheok.jstock.trading.API.OrderManager.OrderSide;
import static org.yccheok.jstock.trading.API.OrderManager.OrderType;


/**
 *
 * @author shuwnyuan
 */
public class Transaction {

    private Transaction () {}

    public static Task buySellThread (OrderSide orderSide, OrderType orderType, Map<String, Object> params) {
        System.out.println("Start " + orderSide.getName() + " order thread ....");

        Task buySellTask = buySellTask(orderSide, orderType, params);
        Thread buySellThread = new Thread(buySellTask);
        buySellThread.start();

        return buySellTask;
    }

    private static Task buySellTask (OrderSide orderSide, OrderType orderType, final Map<String, Object> params) {
        Task buySellTask = new Task<Pair<OrderManager.Order, String>>() {
            @Override protected Pair<OrderManager.Order, String> call() throws Exception {
                System.out.println("BuySellTask call create order .....");

                // Create Order
                Pair<OrderManager.Order, String> createOrder = OrderManager.create(orderSide, orderType, params);
                OrderManager.Order order = createOrder.first;
                String error = createOrder.second;
                
                String orderStr = orderSide.getName() + " " + orderType.getName();
                
                if (error != null) {
                    System.out.println(orderStr + " order failed....");
                    updateMessage("Create " + orderStr + " Order Status FAILED !!");

                    return new Pair<>(null, error);
                }

                String orderID = order.getOrderID();

                // Get Order Status
                order = OrderManager.status(orderID);
                OrderManager.OrdStatus ordStatus = order.getOrdStatusEnum();

                updateMessage(orderStr + " Order Status: " + ordStatus.getValue() + " - " + ordStatus.getName());                
                System.out.println("BuySellTask call get order status DONE, order status: "
                        + ordStatus.getValue() + " - " + ordStatus.getName());

                return new Pair<>(order, null);
            }
        };

        return buySellTask;
    }
        
}
