/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.Map;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import static javafx.geometry.Orientation.VERTICAL;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.yccheok.jstock.trading.PortfolioService;
import org.yccheok.jstock.trading.DriveWealthAPI;


/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    public Portfolio (DriveWealthAPI api) {
        this.api = api;
        startBackgroundService(this.api);
    }

    private void startBackgroundService (DriveWealthAPI api) {
        portfolioService = new PortfolioService(api);
        
        // start immediately
        portfolioService.setDelay(Duration.seconds(0));
        // run every 10 sec
        portfolioService.setPeriod(Duration.seconds(10));
        
        portfolioService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Map<String, Object> result = (Map<String, Object>) workerStateEvent.getSource().getValue();
                
                // 1st run calls account Blotter & get latest quotes
                // 2nd run calls get instruments & get quotes
                // Following run just call get quotes

                if (result.containsKey("marketPrices")) {
                    marketPrices = (Map) result.get("marketPrices");
                }

                if (result.containsKey("accBlotter")) {
                    accBlotter = (Map<String, Object>) result.get("accBlotter");

                    posTableBuilder.initData(accBlotter, instruments);
                    ordTableBuilder.initData(accBlotter, instruments, marketPrices);
                    accSummaryBuilder.initData(accBlotter, posTableBuilder.getPosList());
                } else {
                    if (result.containsKey("instruments")) {
                        instruments = (Map<String, Map>) result.get("instruments");
                        
                        // new instruments added from last call
                        if ((boolean) result.get("updated") == true) {
                            posTableBuilder.updateStocksName(instruments);
                            ordTableBuilder.updateStocksName(instruments);
                        }
                    }
                    
                    posTableBuilder.updatePrices(marketPrices);
                    ordTableBuilder.updatePrices(marketPrices);
                    accSummaryBuilder.update(posTableBuilder.getPosList());
                }
            }
        });
        
        portfolioService.start();
    }

    public Tab createTab() {
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5, 10, 5, 10));  // Insets: top, right, bottom, left
        vBox.setPrefWidth(1000);

        // Account Summary
        final BorderPane accBorderPane = accSummaryBuilder.build();
        vBox.getChildren().add(accBorderPane);
        
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
    
    private final DriveWealthAPI api;
    
    private Map<String, Object> accBlotter;
    private Map<String, Map> instruments;
    private Map<String, Double> marketPrices;

    public  final Tab accTab  = new Tab();
    
    private final PositionsTableBuilder posTableBuilder = new PositionsTableBuilder();
    private final OrdersTableBuilder ordTableBuilder = new OrdersTableBuilder();
    private final AccountSummaryBuilder accSummaryBuilder = new AccountSummaryBuilder();
    
    public static final double tableCellSize = 25;
    
    public PortfolioService portfolioService;
}
    