/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.Map;
import javafx.concurrent.Task;

/**
 *
 * @author shuwnyuan
 */
public class BuySell {
    
    public class BuyTask extends Task<Boolean> {
        private final DriveWealthAPI api;
        private final Map<String, Object> params;
        private Map<String, Object> order;
        private boolean done = false;
        
        public BuyTask (DriveWealthAPI api, Map<String, Object> params) {
            this.api = api;
            this.params = params;
        }
        
        @Override protected Boolean call() throws Exception {
            order = api.createOrder("buy", "market", params);
            String orderID = order.get("orderID").toString();
            
            while (true) {
                // check order status
                Map<String, Object> status = api.orderStatus(orderID);

                // Decide how many times / how long to check status in LOOP
                
                DriveWealthAPI.OrderStatus ordStatus = (DriveWealthAPI.OrderStatus) status.get("ordStatus");
                
                switch (ordStatus) {
                    case ACCEPTED:
                        break;
                    case FILLED:
                        done = true;
                        break;
                    case PARTIALFILLED:
                        break;
                    case CANCELLED:
                        done = true;
                        break;
                    case REJECTED:
                        done = true;
                        break;
                }
                
                if (done == true) {
                    // how to call PortfolioService.setFullRefresh() ??
                    break;
                }
            }
            
            return true;
        }
    }

}
