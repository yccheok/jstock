/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.HashMap;
import java.util.List;
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
import org.yccheok.jstock.trading.AccountModel;
import org.yccheok.jstock.trading.PortfolioService;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.OpenPosModel;
import org.yccheok.jstock.trading.OrderModel;


/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    
    public Portfolio (DriveWealth api) {
        Portfolio.api = api;
        startPortfolioService();
    }

    public static DriveWealth getAPI () {
        return Portfolio.api;
    }
    
    private void startPortfolioService () {
        Portfolio.portfolioService = new PortfolioService(Portfolio.api);
        
        // The initial delay between when the ScheduledService is first started, and when it will begin operation.
        // This is the amount of time the ScheduledService will remain in the SCHEDULED state, before entering the RUNNING state,
        // following a fresh invocation of Service.start() or Service.restart().
        portfolioService.setDelay(this.delay);
        
        // The minimum amount of time to allow between the start of the last run and the start of the next run.
        portfolioService.setPeriod(this.period);
        
        portfolioService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Map<String, Object> result = (Map<String, Object>) workerStateEvent.getSource().getValue();
                
                // 1st run calls account Blotter & get latest quotes
                // 2nd run calls get instruments & get quotes
                // Following run just call get quotes

                String state = result.get("state").toString();
                
                if (result.containsKey("marketPrices")) {
                    marketPrices = (Map) result.get("marketPrices");
                }

                if (state.equals("ACC_BLOTTER")) {
                    List<OpenPosModel> posList = (List) result.get("posList");
                    List<OrderModel> ordList = (List) result.get("ordList");
                    AccountModel accModel = (AccountModel) result.get("accModel");
                    
                    posTableBuilder.initData(posList, instruments);
                    ordTableBuilder.initData(ordList, instruments, marketPrices);
                    accSummaryBuilder.initData(accModel);
                } else {
                    if (state.equals("INSTRUMENTS")) {
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
        this.PortfolioTab.setText("Portfolio (Practice Account)");
        this.PortfolioTab.setClosable(false);
        this.PortfolioTab.setContent(vBox);

        return this.PortfolioTab;
    }
    

    private static DriveWealth api;
    
    private Map<String, Map> instruments = new HashMap<>();
    private Map<String, Double> marketPrices;

    private final PositionsTableBuilder posTableBuilder = new PositionsTableBuilder();
    private final OrdersTableBuilder ordTableBuilder = new OrdersTableBuilder();
    private final AccountSummaryBuilder accSummaryBuilder = new AccountSummaryBuilder();

    private final Tab PortfolioTab = new Tab();
    
    private final Duration delay = new Duration(0);
    private final Duration period = Duration.seconds(10);
    
    public static final double TABLE_CELL_SIZE = 25;
    public static PortfolioService portfolioService;
}
    