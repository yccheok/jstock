/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.MarketDataManager;
import org.yccheok.jstock.trading.API.OrderManager;
import org.yccheok.jstock.trading.API.SessionManager;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.Transaction;

/**
 *
 * @author shuwnyuan
 */
public class BuyDialog {
    
    private BuyDialog () {}
    
    public static void showBuyDialog (PositionModel pos) {
        
        // temporary cancel / suspend Portfolio Scheduled Service
        Portfolio.portfolioService._cancel();
        
        String symbol       = pos.getSymbol();
        String name         = pos.getName();
        String instrumentID = pos.getInstrumentID();
        
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Buy Order");
        dialog.setHeaderText("Buy " + symbol + " - " + name);

        ButtonType buyButtonType    = new ButtonType("Buy", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Symbol
        TextField symbolText = new TextField(symbol);
        symbolText.setPromptText("Symbol");
        symbolText.setEditable(false);

        // Order Type: Market, Stop, Limit
        ChoiceBox<OrderManager.OrderType> orderChoice = new ChoiceBox<>();
        orderChoice.getItems().setAll( OrderManager.OrderType.values() );
        orderChoice.setValue(OrderManager.OrderType.MARKET);

        //  box.getSelectionModel()
//    .selectedItemProperty()
//    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> System.out.println(newValue) );

        
        
        
        
        
        
        // Ask price
        TextField askText = new TextField();
        askText.setPromptText("Ask Price");
        askText.setEditable(false);

        // Qty
        // TODO: get Instrument gives: orderSizeMax, orderSizeMin, orderSizeStep
        TextField qtyText = new TextField();
        qtyText.setPromptText("Units");
        
        grid.add(new Label("Stock:"), 0, 0);
        grid.add(symbolText, 1, 0);
        
        grid.add(new Label("Order Type:"), 0, 1);
        grid.add(orderChoice, 1, 1);

        grid.add(new Label("Ask Price:"), 0, 2);
        grid.add(askText, 1, 2);

        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(qtyText, 1, 3);

        // Scheduled service - get Ask price with Get Market Data / Quote API
        final ScheduledService marketDatasrv = getMarketDataService(symbol);
        marketDatasrv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                MarketDataManager.MarketData result = (MarketDataManager.MarketData) workerStateEvent.getSource().getValue();
                
                System.out.println("BUY dialog: Get Market Data Service, Updated price for symbol: " + symbol
                        + ", Bid: " + result.getBid()
                        + ", Ask: " + result.getAsk());

                // Task's event handler is handled in JavaFX Application / UI Thread, so is ok to update UI
                askText.setText(result.getAsk().toString());
            }
        });
        marketDatasrv.start();
        
        // Buy button: Enable/Disable button depends on qty entered.
        Node buyButton = dialog.getDialogPane().lookupButton(buyButtonType);
        buyButton.setDisable(true);

        // Validate Qty is number format
        qtyText.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            boolean disable = true;
            String newQty = newValue.trim();
            if (!newQty.isEmpty()) {
                try {
                    if (Double.parseDouble(newQty) > 0) {
                        disable = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Qty is not number format: " + newQty);
                }
            }
            buyButton.setDisable(disable);
        });

        // Cancel button
        Node cancelButton = dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
            System.out.println("Cancel was definitely pressed");
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the Qty field by default.
        Platform.runLater(() -> qtyText.requestFocus());

        // Convert the result when the BUY button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buyButtonType) {
                SessionManager.User user   = DriveWealth.getUser();
                SessionManager.Account acc = user.getActiveAccount();

                String userID    = user.getUserID();
                String accountID = acc.getAccountID();
                String accountNo = acc.getAccountNo();
                
                Map<String, Object> params = new HashMap<>();

                // remove leading / trailing white space
                params.put("symbol",        symbolText.getText().trim());
                
                params.put("instrumentID",  instrumentID);
                params.put("accountID",     accountID);
                params.put("accountNo",     accountNo);
                params.put("userID",        userID);

                // 1: practice a/c,  2: live a/c
                params.put("accountType",   acc.getAccountType().getValue());
                
                // 1: Market order,  2: Limit order,  3: Stop order
                params.put("ordType",       orderChoice.getValue().getValue());
                
                params.put("side",          "B");
                params.put("orderQty",      Double.parseDouble(qtyText.getText().trim()));                
                params.put("comment",       "SY Sunday Test - Market Order BUY");

                return params;
            }
            return null;
        });
        
        Optional<Map<String, Object>> result = dialog.showAndWait();

        if (result.isPresent()) {
            Map<String, Object> buyParams = result.get();
            
            Transaction.buy(Portfolio.portfolioService, orderChoice.getValue(), buyParams);
            System.out.println("Buy Order SYMBOL : " + buyParams.get("symbol").toString());
            
            // cancel get market data shceduled service
            marketDatasrv.cancel();
        } else {
            // No result, probably Cancel button or Close has been pressed
            
            // resume Portfolio Scheduled Service
            Portfolio.portfolioService._restart();
            
            // cancel get market data shceduled service
            marketDatasrv.cancel();
        }
    }

    
    public static ScheduledService getMarketDataService (String symbol) {
        ScheduledService<MarketDataManager.MarketData> svc = new ScheduledService<MarketDataManager.MarketData>() {
            @Override
            protected Task<MarketDataManager.MarketData> createTask() {
                return new Task<MarketDataManager.MarketData>() {
                    protected MarketDataManager.MarketData call() {
                        ArrayList<String> symbols = new ArrayList<>();
                        symbols.add(symbol);

                        System.out.println("BUY DIALOG - Scheduled Task to get market data bid/ask");

                        // call Get market Data API => bid + ask
                        List<MarketDataManager.MarketData> dataList = MarketDataManager.get(symbols, false);
                        MarketDataManager.MarketData marketData = null;

                        if (!dataList.isEmpty()) {
                            marketData = dataList.get(0);
                        }
                        return marketData;
                    }
                };
            }
        };
        
        svc.setPeriod(Duration.seconds(1));
        return svc;
    }
    
}
