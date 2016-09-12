/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.AccountManager;
import org.yccheok.jstock.trading.API.SessionManager;
import org.yccheok.jstock.trading.API.MarketDataManager;
import org.yccheok.jstock.trading.API.InstrumentManager;
import org.yccheok.jstock.trading.API.InstrumentManager.Instrument;

/**
 *
 * @author shuwnyuan
 */

public class PortfolioService extends ScheduledService<Map<String, Object>> {
    
    private List<PositionModel> posList = new ArrayList<>();
    private List<OrderModel> ordList = new ArrayList<>();
    private AccountSummaryModel accModel;
    
    private Map<String, Instrument> instruments = new HashMap<>();
    private Set symbolsSet;
    private TaskState taskState = TaskState.ACC_BLOTTER;
    private boolean refresh = false;

    public static enum TaskState {
        ACC_BLOTTER("ACC_BLOTTER"),
        INSTRUMENTS("INSTRUMENTS"),
        PRICES("PRICES");
        
        private final String value;
        
        private TaskState (String value) {
            this.value = value;
        }
        
        public String getValue () {
            return this.value;
        }
    }
    
    
    // Flow of Service run:
    //  1st run: AccBlotter => get open positions + orders
    //  2nd run: Get Instruments for all symbols => get stock's name
    //  following run: get market prices => update PL
    // This is control by taskState
    //
    // Initially, delay=0. So after calling AccBlotter, immediately go to next iteration to call Get Instrument.
    // Then delay is set to longer (10sec), to update prices via Market Data / Quote API
    //
    // After BUY ORDER, taskState is set to "ACCBLOTTER", to refresh portfolio
    

    ////////////////
    // TODO: after BUY ORDER (especially Limit / STOP), order might be pending.
    // How to monitor order status & reset Service accordingly ??
    ////////////////
    
    public PortfolioService () {
        // The minimum amount of time to allow between the start of the last run and the start of the next run.
        this.setPeriod(Duration.ZERO);
        
        // The initial delay between when the ScheduledService is first started, and when it will begin operation.
        // This is the amount of time the ScheduledService will remain in the SCHEDULED state, before entering the RUNNING state,
        // following a fresh invocation of Service.start() or Service.restart().
        this.setDelay(Duration.ZERO);
    }
    
    public synchronized void _setRefresh () {
        this.refresh = true;
    }
    
    private synchronized void _resetRefresh () {
        this.refresh = false;
    }
    
    public void _cancel () {
        this.cancel();
    }

    public void _restart () {
        this._setRefresh();
        this.setPeriod(Duration.ZERO);

        // Service restart, reset, start => should only be called from JavaFX Application Thread
        Platform.runLater(new Runnable() {
            @Override public void run() {
                restart();
            }
        });
    }
    
    private class PortfolioTask extends Task<Map<String, Object>> {
        
        public PortfolioTask() {}

        private void getAccBlotter (String userID, String accountID) {
            AccountManager.AccountBlotter accBlot = AccountManager.blotter(userID, accountID);

            // List of positions (PositionModel) & pending oders (OrderModel)
            posList = accBlot.getPositions();
            ordList = accBlot.getOrders();
            accModel = accBlot.getAccount();
            symbolsSet = accBlot.getSymbols();

            System.out.println("calling account Blotter DONE...");
        }
        
        private void getInstruments () {
            // call "search instrument" to get stocks name for all symbols
            Iterator<String> itr = symbolsSet.iterator();

            while (itr.hasNext()) {
                String symbol = itr.next();

                if (instruments.containsKey(symbol)) {
                    // avoid Get Instrument call if already did in previous iteration
                    continue;
                }

                Map<String, String> param = new HashMap<>();
                // only search for exact symbol match
                param.put("symbols", symbol);
                List<Instrument> insList = InstrumentManager.search(param);

                if (insList.size() > 0) {
                    Instrument ins = insList.get(0);
                    instruments.put(symbol, ins);
                }
            }
        }
        
        private Map<String, Double> getMarketPrices () {
            // get latest prices for all symbols
            ArrayList<String> symbols = new ArrayList<>(symbolsSet);
            List<MarketDataManager.MarketData> dataList = MarketDataManager.get(symbols, true);

            Map<String, Double> prices = new HashMap<>();
            for (MarketDataManager.MarketData marketData : dataList) {
                prices.put(marketData.getSymbol(), marketData.getLastTrade());
            }
            
            return prices;
        }
        
        private void updateTaskState () {
            if (taskState == TaskState.PRICES) {
                return;
            }

            if (taskState == TaskState.ACC_BLOTTER) {
                taskState = TaskState.INSTRUMENTS;
            } else if (taskState == TaskState.INSTRUMENTS) {
                taskState = TaskState.PRICES;

                // update prices every 10 sec
                setPeriod(Duration.seconds(10));
            }
        }
        
        private void checkResetState () {
            // Force Portfolio Refresh, by changing state => AccBlotter. Eg: After create order successfully
            if (refresh == true) {
                taskState = TaskState.ACC_BLOTTER;
                _resetRefresh();
            }
        }
        
        @Override
        protected Map<String, Object> call() throws Exception {
            SessionManager.User user = DriveWealth.getUser();
            String userID = user.getUserID();
            String accountID = user.getActiveAccount().getAccountID();
            
            if (userID == null || accountID == null) {
                return null;
            }
            
            checkResetState();
            
            Map<String, Object> result = new HashMap<>();
            result.put("state", taskState.getValue());

            // Not calling Account Blotter & get instruments on every iteration
            if (taskState == TaskState.ACC_BLOTTER) {
                getAccBlotter(userID, accountID);

                result.put("posList", posList);
                result.put("ordList", ordList);
                result.put("accModel", accModel);

                System.out.println("DONE calling accBlotter...");
            } else if (taskState == TaskState.INSTRUMENTS) {
                getInstruments();
                result.put("instruments", instruments);

                System.out.println("DONE calling get instruments...");
            }

            // always get latest prices
            Map<String, Double> prices = getMarketPrices();
            result.put("marketPrices", prices);
            System.out.println("DONE calling get market prices...");
            
            updateTaskState();

            return result;
        }
    }
    
    @Override
    protected Task<Map<String, Object>> createTask() {
        final PortfolioTask portfolioTask = new PortfolioTask();
        return portfolioTask;
    }

}



