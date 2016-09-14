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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.MarketDataManager;
import org.yccheok.jstock.trading.API.OrderManager;
import org.yccheok.jstock.trading.API.OrderManager.OrdStatus;
import org.yccheok.jstock.trading.API.SessionManager;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.Transaction;
import static org.yccheok.jstock.trading.API.OrderManager.OrderType;
import org.yccheok.jstock.trading.Utils;

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
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Buy Order");
        dialog.setHeaderText("Buy " + symbol + " - " + name);

        ButtonType buyButtonType    = new ButtonType("Submit Order", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ColumnConstraints col1 = new ColumnConstraints(120);
        ColumnConstraints col2 = new ColumnConstraints(150);
        grid.getColumnConstraints().add(col1);
        grid.getColumnConstraints().add(col2);

        
        // Symbol
        TextField symbolText = new TextField(symbol);
        symbolText.setPromptText("Symbol");
        symbolText.setEditable(false);

        // Ask price
        Label askLabel = new Label();
        
        // Order Type: Market, Stop, Limit
        ChoiceBox<OrderType> orderChoice = new ChoiceBox<>();
        orderChoice.getItems().setAll(OrderType.values());
        orderChoice.setValue(OrderType.MARKET);

        // Qty
        // TODO: get Instrument gives: orderSizeMax, orderSizeMin, orderSizeStep
        TextField qtyText = new TextField();
        qtyText.setPromptText("Units");
        
        // Limit / Stop price
        Label priceLabel = new Label();
        TextField priceText = new TextField();

        // set to MARKET order by default, so hide Stop / Limit Price field
        priceLabel.setVisible(false);
        priceText.setVisible(false);
        
        // Limit / Stop price Note
        Label priceNote = new Label();
        priceNote.setVisible(false);
        
        orderChoice.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends OrderType> observable, OrderType oldValue, OrderType newValue) -> {
                System.out.println("Order Type changed: " + newValue.getName());

                if (newValue == OrderType.LIMIT) {
                    priceLabel.setText("Limit Price");
                    priceNote.setText("Enter limit price > " + askLabel.getText().trim());

                    priceLabel.setVisible(true);
                    priceText.setVisible(true);
                    priceNote.setVisible(true);
                } else if (newValue == OrderType.STOP) {
                    priceLabel.setText("Stop Price");
                    
                    Double stopPrice = Double.parseDouble(askLabel.getText().trim()) + 0.05;
                    priceNote.setText("Enter stop price > " + stopPrice);
                    
                    priceLabel.setVisible(true);
                    priceText.setVisible(true);
                } else if (newValue == OrderType.MARKET) {
                    // hide price field
                    priceLabel.setVisible(false);
                    priceText.setVisible(false);
                    priceNote.setVisible(false);
                }
            });

        
        grid.add(new Label("Stock:"), 0, 0);
        grid.add(symbolText, 1, 0);

        
        /*
        SimpleStringProperty url = new SimpleStringProperty("http://syscdn.drivewealth.net/images/symbols/mcd.png");
        ImageView img = new ImageView();
        img.imageProperty().bind(Bindings.createObjectBinding(() -> {
            return new Image(url.getValue());
        }, url));
        */

        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        String url = pos.getUrlImage();

        if (url != null && ! url.isEmpty()) {
            // use background loading
            Image icon = new Image(url, true);
            imageView.setImage(icon);
        }
        
        grid.add(imageView, 2, 0);
        
        grid.add(new Label("Ask Price:"), 0, 1);
        grid.add(askLabel, 1, 1);
        
        grid.add(new Label("Order Type:"), 0, 2);
        grid.add(orderChoice, 1, 2);

        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(qtyText, 1, 3);
        
        grid.add(priceLabel, 0, 4);
        grid.add(priceText, 1, 4);

        grid.add(priceNote, 1, 5);
        GridPane.setColumnSpan(priceNote, 2);
        
        // Scheduled service - get Ask price with Get Market Data / Quote API
        final ScheduledService marketDataSrv = getMarketDataService(symbol);
        marketDataSrv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                MarketDataManager.MarketData result = (MarketDataManager.MarketData) workerStateEvent.getSource().getValue();
        
                /*
                System.out.println("BUY dialog: Get Market Data Service, Updated price for symbol: " + symbol
                        + ", Bid: " + result.getBid()
                        + ", Ask: " + result.getAsk());
                */

                // Task's event handler is handled in JavaFX Application / UI Thread, so is ok to update UI
                askLabel.setText(result.getAsk().toString());
            }
        });
        marketDataSrv.start();

        
        /*
        ** Buy button
        */
        
        // Enable/Disable button depends on qty entered.
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

        // BUY button event handler
        buyButton.addEventHandler(ActionEvent.ACTION, event -> {
            // prepare BUY ORDER params
            SessionManager.User user   = DriveWealth.getUser();
            SessionManager.Account acc = user.getActiveAccount();

            String userID    = user.getUserID();
            String accountID = acc.getAccountID();
            String accountNo = acc.getAccountNo();

            Map<String, Object> params = new HashMap<>();

            // remove leading / trailing white space
            String sym = symbolText.getText().trim();

            params.put("symbol",        sym);
            params.put("instrumentID",  instrumentID);
            params.put("accountID",     accountID);
            params.put("accountNo",     accountNo);
            params.put("userID",        userID);

            // 1: practice a/c,  2: live a/c
            params.put("accountType",   acc.getAccountType().getValue());
            params.put("side",          "B");
            params.put("orderQty",      Double.parseDouble(qtyText.getText().trim()));                

            // 1: Market order,  2: Limit order,  3: Stop order
            OrderType ordType = orderChoice.getValue();
            params.put("ordType", ordType.getValue());

            // Stop order
            if (ordType == OrderType.STOP) {
                params.put("price", priceText.getText().trim());
            }
            // Limit Order
            if (ordType == OrderType.LIMIT) {
                params.put("limitPrice", priceText.getText().trim());
            }


            // show Dialog: Forwarding Order in Progress ....
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Forwarding Order");
            alert.setHeaderText("Forwarding your order");
            alert.setContentText("Forwarding your order, please wait ....");
            
            // disable OK button, until BUY ORDER finish
            alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            alert.show();

            
            // Execute BUY Order
            Task buyTask = Transaction.startBuyThread(orderChoice.getValue(), params);
            String ordName = orderChoice.getValue().getName();
            
            buyTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(final WorkerStateEvent workerStateEvent) {
                    Pair<OrderManager.Order, String> result = (Pair) workerStateEvent.getSource().getValue();

                    OrderManager.Order order = result.first;
                    String error = result.second;

                    System.out.println("Buy Task Succeed Handler ....");
                    String msg;
                    String details;

                    if (error != null) {
                        msg = "Buy " + ordName + " order failed.";
                        details = "Error : " + error;
                    } else {
                        OrdStatus ordStatus     = order.getOrdStatusEnum();
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
                        String rejReason        = order.getOrdRejReason();

                        if (ordStatus == OrdStatus.REJECTED) {
                            msg = "Buy " + ordName + " order rejected.";
                            details = "Reason: " + rejReason;
                        } else {
                            msg = "Buy " + ordName + " order successful.";
                            
                            // Product: symbol, stock name, logo
                            // Action: Buy
                            // Quantity
                            // Price
                            // Subtotal
                            // commission
                            // Total
                            
                            String unitPrice = Utils.formatNumber(grossTradeAmt / orderQty, 2);
                            String total = Utils.formatNumber(grossTradeAmt + commission, 2);
                            
                            details =   " Product : " + sym +
                                        "\n Action : Buy" +
                                        "\n Quantity : " + orderQty +
                                        "\n Price : " + unitPrice +
                                        "\n Subtotal : " + grossTradeAmt +
                                        "\n Commission : " + commission +
                                        "\n Total : " + total;
                        }
                    }
                    
                    System.out.println(msg + details);
                    alert.setContentText(msg);
                    
                    // Show Buy Success / Error message
                    TextArea buyText = new TextArea(details);
                    buyText.setEditable(false);
                    buyText.setWrapText(true);

                    buyText.setMaxWidth(Double.MAX_VALUE);
                    buyText.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setVgrow(buyText, Priority.ALWAYS);
                    GridPane.setHgrow(buyText, Priority.ALWAYS);

                    GridPane buyGrid = new GridPane();
                    buyGrid.setMaxWidth(Double.MAX_VALUE);
                    buyGrid.add(buyText, 0, 0);

                    alert.getDialogPane().setExpandableContent(buyGrid);
                    alert.getDialogPane().setExpanded(true);
                    
                    // enable Dialog - OK / Close  buton
                    alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                    
                    // refresh Portfolio
                    Portfolio.portfolioService._restart();
                }
            });
        });

        
        dialog.getDialogPane().setContent(grid);

        // Request focus on the Qty field by default.
        Platform.runLater(() -> qtyText.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();

        ButtonType buttonType = result.get();
        if (buttonType == buyButtonType) {
            System.out.println("AFTER BUY Button Pressed ......");
        } else if (buttonType == cancelButtonType) {
            System.out.println("AFTER CANCEL Button Pressed ......");
        }
        
        // cancel get market data scheduled service
        marketDataSrv.cancel();
    }

    
    public static ScheduledService getMarketDataService (String symbol) {
        ScheduledService<MarketDataManager.MarketData> svc = new ScheduledService<MarketDataManager.MarketData>() {
            @Override
            protected Task<MarketDataManager.MarketData> createTask() {
                return new Task<MarketDataManager.MarketData>() {
                    protected MarketDataManager.MarketData call() {
                        ArrayList<String> symbols = new ArrayList<>();
                        symbols.add(symbol);

                        //System.out.println("BUY DIALOG - Scheduled Task to get market data bid/ask");

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
