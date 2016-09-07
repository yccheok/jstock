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
import org.yccheok.jstock.trading.API.InstrumentManager;
import org.yccheok.jstock.trading.AccountSummaryModel;
import org.yccheok.jstock.trading.PortfolioService;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.OrderModel;


/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    
    private Portfolio () {}

    public static Tab createTab () {
        initTab();
        startPortfolioService();

        return Portfolio.PortfolioTab;
    }
    
    private static void initTab () {
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5, 10, 5, 10));  // Insets: top, right, bottom, left
        vBox.setPrefWidth(1000);

        // Account Summary
        final BorderPane accBorderPane = accSummaryBuilder.build();
        vBox.getChildren().add(accBorderPane);
        
        // Open Positions
        final TableView posTable = Portfolio.posTableBuilder.build();
        
        VBox vboxOpenPos = new VBox(5);
        vboxOpenPos.setPadding(new Insets(5, 5, 5, 5));  // Insets: top, right, bottom, left

        final Label posLabel = new Label("Current Investments");
        vboxOpenPos.getChildren().addAll(posLabel, posTable);

        // Pending orders
        final TableView ordTable = Portfolio.ordTableBuilder.build();
        
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
        Portfolio.PortfolioTab.setText("Portfolio (Practice Account)");
        Portfolio.PortfolioTab.setClosable(false);
        Portfolio.PortfolioTab.setContent(vBox);
    }
    
    private static void startPortfolioService () {
        Portfolio.portfolioService = new PortfolioService();
        
        setSucceedHandler(portfolioService);
        setFailedHandler(portfolioService);
        
        portfolioService.start();
    }

    private static void setSucceedHandler (PortfolioService portfolioService) {
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
                    List<PositionModel> posList = (List) result.get("posList");
                    List<OrderModel> ordList = (List) result.get("ordList");
                    AccountSummaryModel accModel = (AccountSummaryModel) result.get("accModel");
                    
                    posTableBuilder.initData(posList, instruments);
                    ordTableBuilder.initData(ordList, instruments, marketPrices);
                    accSummaryBuilder.initData(accModel);
                } else {
                    if (state.equals("INSTRUMENTS")) {
                        instruments = (Map<String, InstrumentManager.Instrument>) result.get("instruments");
                        
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
    } 
    
    public static void setFailedHandler (PortfolioService portfolioService) {
        portfolioService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("Portfolio Service - Error in call : "
                        + event.getSource().getException().getMessage());
            }
        });
    }
    

    private static Map<String, InstrumentManager.Instrument> instruments = new HashMap<>();
    private static Map<String, Double> marketPrices;

    private static final PositionsTableBuilder posTableBuilder   = new PositionsTableBuilder();
    private static final OrdersTableBuilder ordTableBuilder      = new OrdersTableBuilder();
    private static final AccountSummaryBuilder accSummaryBuilder = new AccountSummaryBuilder();

    private static final Tab PortfolioTab = new Tab();
    
    public static final double TABLE_CELL_SIZE = 25;
    public static PortfolioService portfolioService;
}
    