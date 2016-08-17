/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.Locale;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import static javafx.geometry.Orientation.VERTICAL;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.yccheok.jstock.trading.AccountModel;
import org.yccheok.jstock.trading.OpenPosModel;
import org.yccheok.jstock.trading.OrderModel;
import org.yccheok.jstock.trading.PortfolioService;
import org.yccheok.jstock.trading.DriveWealthAPI;


/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    public Portfolio (DriveWealthAPI api) {
        this.api = api;
        startBackgroundService(api);
    }

    private void startBackgroundService (DriveWealthAPI api) {
        PortfolioService service = new PortfolioService(api);
        
        // start immediately
        service.setDelay(Duration.seconds(0));
        // run every 10 sec
        service.setPeriod(Duration.seconds(10));
        
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Map<String, Object> result = (Map<String, Object>) workerStateEvent.getSource().getValue();
                
                // First run calls account Blotter & get instruments
                // Following run just call get market data to get latest market price
                
                if (result.containsKey("accBlotter") && result.containsKey("instruments")) {
                    accBlotter = (Map<String, Object>) result.get("accBlotter");
                    instruments = (Map<String, Map>) result.get("instruments");

                    posTableBuilder.initData(accBlotter, instruments);
                    ordTableBuilder.initData(accBlotter, instruments);
                    
                    initAccData();
                } else if (result.containsKey("marketPrices")) {
                    marketPrices = (Map) result.get("marketPrices");
                    
                    posTableBuilder.updatePrices(marketPrices);
                    ordTableBuilder.updatePrices(marketPrices);
                    
                    updateAccData();
                }
            }
        });
        
        service.start();
    }

    private void initAccData () {
        this.acc = new AccountModel(this.accBlotter, this.posTableBuilder.getPosList());

        Locale locale  = new Locale("en", "US");
        this.shareAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.equity));
        this.profitAmount.textProperty().bind(Bindings.format(locale, "$%,.2f (%,.2f%%)", this.acc.totalUnrealizedPL, this.acc.totalUnrealizedPLPercent));
        this.cashAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.cashForTrade));
        this.totalAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.accountTotal));

        this.profitAmount.getStyleClass().add(this.acc.unrealizedPLCss());
        this.cashAmount.getStyleClass().add(this.acc.cashForTradeCss());
        this.totalAmount.getStyleClass().add(this.acc.accountTotalCss());
        this.shareAmount.getStyleClass().add(this.acc.equityValueCss());
    }
    
    private void updateAccData () {
        this.acc.update(this.posTableBuilder.getPosList());

        this.profitAmount.getStyleClass().clear();
        this.profitAmount.getStyleClass().add(acc.unrealizedPLCss());
    }
    
    public Tab createTab() {
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5, 10, 5, 10));  // Insets: top, right, bottom, left
        vBox.setPrefWidth(1000);

        // Account Summary
        initAccSummary();
        vBox.getChildren().add(this.accBorderPane);
        
        // Open Positions
        final TableView posTable = this.posTableBuilder.build();
        
        VBox vboxOpenPos = new VBox(5);
        vboxOpenPos.setPadding(new Insets(5, 5, 5, 5));  // Insets: top, right, bottom, left

        final Label posLabel = new Label("Current Investments");
        vboxOpenPos.getChildren().addAll(posLabel, posTable);

        // Pending orders
        final TableView ordTable = this.ordTableBuilder.build();
        
        VBox vboxOrder = new VBox(5);
        vboxOrder.setPadding(new Insets(5, 5, 5, 5));  // Insets: top, right, bottom, left

        final Label ordLabel = new Label("Pending Orders");
        vboxOrder.getChildren().addAll(ordLabel, ordTable);

        // Up Down partition
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(VERTICAL);
        splitPane.setDividerPositions(0.6);
        splitPane.getItems().addAll(vboxOpenPos, vboxOrder);
        splitPane.setPrefHeight(500);
        vBox.getChildren().add(splitPane);
        
        vboxOpenPos.prefWidthProperty().bind(splitPane.widthProperty());
        vboxOrder.prefWidthProperty().bind(splitPane.widthProperty());

        // add Portfolio tab
        this.accTab.setText("Portfolio (Practice Account)");
        this.accTab.setClosable(false);
        this.accTab.setContent(vBox);

        return this.accTab;
    }
    
    private void initAccSummary () {
        // Left content
        HBox leftHbox = new HBox(8);
        
        // Stocks on hand value
        Label shareText = new Label("Share:");
        
        // Unrealized PL
        Label profitText = new Label("Paper Profit:");
        profitText.setPadding(new Insets(0, 0, 0, 10));

        leftHbox.getChildren().addAll(shareText, this.shareAmount, profitText, this.profitAmount);
        
        // Right content
        HBox rightHbox = new HBox(8);
        
        // Cash for trading
        Label cashText = new Label("Cash to Invest:");

        // Total: Cash balance + Stocks
        Label totalText = new Label("Total:");
        totalText.setPadding(new Insets(0, 0, 0, 10));
        
        rightHbox.getChildren().addAll(cashText, this.cashAmount, totalText, this.totalAmount);
        
        this.accBorderPane.setPadding(new Insets(5, 0, 10, 0));    // Insets: top, right, bottom, left
        this.accBorderPane.setLeft(leftHbox);
        this.accBorderPane.setRight(rightHbox);
        this.accBorderPane.setId("accBorderPane");
    }

    private final DriveWealthAPI api;
    
    private Map<String, Object> accBlotter;
    private Map<String, Map> instruments;
    private Map<String, Double> marketPrices;

    private AccountModel acc;

    public  final Tab accTab  = new Tab();
    
    private final BorderPane accBorderPane = new BorderPane();
    private final Label shareAmount = new Label();
    private final Label profitAmount = new Label();
    private final Label cashAmount = new Label();
    private final Label totalAmount = new Label();
    
    private final PositionsTableBuilder posTableBuilder = new PositionsTableBuilder();
    private final OrdersTableBuilder ordTableBuilder = new OrdersTableBuilder();
    
    public static final double tableCellSize = 25;
}
    