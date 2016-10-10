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
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.yccheok.jstock.trading.API.InstrumentManager;
import org.yccheok.jstock.trading.API.SessionManager;
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

    private static final Portfolio INSTANCE = new Portfolio();

    public static Portfolio getInstance () {
        return INSTANCE;
    }

    public VBox show () {
        initUI();
        initPortfolioServ();

        return this.vBox;
    }

    private void initUI () {
        vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5, 10, 5, 10));  // Insets: top, right, bottom, left
        vBox.setPrefWidth(1000);

        // Account Summary
        final HBox accHBox = this.accSummaryBuilder.build();
        vBox.getChildren().add(accHBox);

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
    }

    public void cancelPortfolioServ () {
        portfolioServ._cancel();
    }

    public void restartPortfolioServ () {
        portfolioServ._restart();
    }

    public void initPortfolioServ () {
        // Cancels any currently running task and stops this scheduled service, such that no additional iterations will occur.
        if (portfolioServ != null) portfolioServ.cancel();

        SessionManager.User user = SessionManager.getInstance().getUser();
        portfolioServ = new PortfolioService(user.getUserID(), user.getActiveAccount().getAccountID());

        setSucceedHandler(portfolioServ);
        setFailedHandler(portfolioServ);

        portfolioServ.start();
    }

    private void setSucceedHandler (PortfolioService portfolioService) {
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
                        Map<String, InstrumentManager.Instrument> ins = (Map<String, InstrumentManager.Instrument>) result.get("instruments");
                        instruments.putAll(ins);

                        posTableBuilder.updateNameURL(instruments);
                        ordTableBuilder.updateNameURL(instruments);
                    }
                    
                    posTableBuilder.updatePrices(marketPrices);
                    ordTableBuilder.updatePrices(marketPrices);
                    accSummaryBuilder.update(posTableBuilder.getPosList());
                }
            }
        });
    }
    
    public void setFailedHandler (PortfolioService portfolioService) {
        portfolioService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("Portfolio Service - Error in call : "
                        + event.getSource().getException().getMessage());
            }
        });
    }

    // cache Stock icon for Position & Order table
    public Image getIcon (String url) {
        if (! icons.containsKey(url)) {
            // use background loading:  public Image(String url, boolean backgroundLoading)
            Image icon = new Image(url, true);
            icons.put(url, icon);
        }
        return icons.get(url);
    }

    public Map<String, InstrumentManager.Instrument> getInstruments () {
        return this.instruments;
    }

    
    private Map<String, InstrumentManager.Instrument> instruments = new HashMap<>();
    private Map<String, Double> marketPrices;
    private PortfolioService portfolioServ = null;

    private final PositionsTableBuilder posTableBuilder   = new PositionsTableBuilder();
    private final OrdersTableBuilder ordTableBuilder      = new OrdersTableBuilder();
    private final AccountSummaryBuilder accSummaryBuilder = new AccountSummaryBuilder();

    private VBox vBox;
    private final Map<String, Image> icons = new HashMap<>();
    
    public static final double TABLE_CELL_SIZE = 30;
    public static final double ICON_SIZE = TABLE_CELL_SIZE - 5;
}
    