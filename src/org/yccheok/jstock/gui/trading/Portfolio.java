/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import static javafx.geometry.Orientation.VERTICAL;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.yccheok.jstock.trading.OpenPos;
import org.yccheok.jstock.trading.Order;
import org.yccheok.jstock.trading.AccountSummary;
import org.yccheok.jstock.trading.DriveWealthAPI;
import org.yccheok.jstock.trading.OpenPosModel;
import org.yccheok.jstock.trading.OrderModel;
import org.yccheok.jstock.trading.Utils;
import org.yccheok.jstock.trading.PortfolioService;

/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    public Portfolio (DriveWealthAPI api) {
        this.api = api;
        startBackgroundService();
    }

    private void startBackgroundService () {
        PortfolioService service = new PortfolioService(api);
        
        // start immediately
        service.setDelay(new Duration(0));
        // run every 30 sec
        service.setPeriod(new Duration(30000));
        
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                Map<String, Object> result = (Map<String, Object>) workerStateEvent.getSource().getValue();
                
                accBlotter = (Map<String, Object>) result.get("accBlotter");
                instruments = (Map<String, Map>) result.get("instruments");
                
                getOpenPositions();
                getPendingOrders();
                
                updateAccSummary();
                updateOpenPosTableData();
                updateOrderTableData();
            }
        });
        
        service.start();
    }
    
    private void getOpenPositions () {
        LinkedTreeMap<String, Object> equity = (LinkedTreeMap) this.accBlotter.get("equity");
        List<LinkedTreeMap<String, Object>> result = (List) equity.get("equityPositions");
        
        this.positions = new ArrayList<>();
        
        for (LinkedTreeMap<String, Object> a : result) {
            Map<String, Object> ins = this.instruments.get(a.get("symbol").toString());
            OpenPos pos = new OpenPos(a, ins);
            this.positions.add(pos);
        }
    }
    
    private void getPendingOrders () {
        List<LinkedTreeMap<String, Object>> result = (List) this.accBlotter.get("orders");
        
        this.orders = new ArrayList<>();
        
        for (LinkedTreeMap<String, Object> a : result) {
            Map<String, Object> ins = this.instruments.get(a.get("symbol").toString());
            Order order = new Order(a, ins);
            this.orders.add(order);
        }
    }
    
    private void updateOpenPosTableData () {
        final ObservableList<OpenPosModel> list = FXCollections.observableArrayList();
        for (OpenPos pos : this.positions) {
            list.add(new OpenPosModel(pos));
        }
        
        this.posList = list;
        this.posTable.setItems(this.posList);
        
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));
    }
    
    private void updateOrderTableData () {
        final ObservableList<OrderModel> list = FXCollections.observableArrayList();
        for (Order ord : this.orders) {
            list.add(new OrderModel(ord));
        }

        this.ordList = list;
        this.ordTable.setItems(this.ordList);
        
        this.ordTable.prefHeightProperty().bind(Bindings.size(this.ordTable.getItems()).multiply(this.ordTable.getFixedCellSize()).add(30));
    }
    
    private void updateAccSummary () {
        this.acc.update(this.accBlotter, this.positions);

        this.profitAmount.getStyleClass().add((acc.totalUnrealizedPL > 0) ? "profit" : "loss");
        this.cashAmount.getStyleClass().add((acc.cashForTrade > 0) ? "profit" : "loss");
        this.totalAmount.getStyleClass().add((acc.accountTotal > 0) ? "profit" : "loss");
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
        initOpenPosTable();
        
        VBox vboxOpenPos = new VBox(5);
        vboxOpenPos.setPadding(new Insets(5, 5, 5, 5));  // Insets: top, right, bottom, left

        final Label posLabel = new Label("Current Investments");
        vboxOpenPos.getChildren().addAll(posLabel, this.posTable);

        // Pending orders
        initOrderTable();
        
        VBox vboxOrder = new VBox(5);
        vboxOrder.setPadding(new Insets(5, 5, 5, 5));  // Insets: top, right, bottom, left

        final Label ordLabel = new Label("Pending Orders");
        vboxOrder.getChildren().addAll(ordLabel, this.ordTable);

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
        acc = new AccountSummary();
        
        // Left content
        HBox leftHbox = new HBox(8);
        
        // Total Open positions value
        Label shareText = new Label("Share:");
        this.shareAmount.getStyleClass().add("profit");
        
        // Total unrealized PL
        Label profitText = new Label("Paper Profit:");
        profitText.setPadding(new Insets(0, 0, 0, 10));

        leftHbox.getChildren().addAll(shareText, this.shareAmount, profitText, this.profitAmount);
        
        // Right content
        HBox rightHbox = new HBox(8);
        
        // Cash for trading
        Label cashText = new Label("Cash to Invest:");

        // Total
        Label totalText = new Label("Total:");
        totalText.setPadding(new Insets(0, 0, 0, 10));
        
        rightHbox.getChildren().addAll(cashText, this.cashAmount, totalText, this.totalAmount);
        
        this.accBorderPane.setPadding(new Insets(5, 0, 10, 0));    // Insets: top, right, bottom, left
        this.accBorderPane.setLeft(leftHbox);
        this.accBorderPane.setRight(rightHbox);
        this.accBorderPane.setId("accBorderPane");
        
        this.shareAmount.textProperty().bind(acc.equityProperty);
        this.profitAmount.textProperty().bind(acc.totalUnrealizedPLProperty);
        this.cashAmount.textProperty().bind(acc.cashForTradeProperty);
        this.totalAmount.textProperty().bind(acc.accountTotalProperty);
    }

    private void initOpenPosTable () {
        // Open Positions table
        TableColumn symbolCol = new TableColumn("Stock");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));
        symbolCol.getStyleClass().add("left");

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.getStyleClass().add("left");

        TableColumn unitsCol = new TableColumn("Units");
        unitsCol.setCellValueFactory(new PropertyValueFactory("units"));
        unitsCol.getStyleClass().add("right");

        TableColumn avgPriceCol = new TableColumn("Average Purchase Price");
        avgPriceCol.setCellValueFactory(new PropertyValueFactory("averagePrice"));
        avgPriceCol.getStyleClass().add("right");

        TableColumn mktPriceCol = new TableColumn("Current Price");
        mktPriceCol.setCellValueFactory(new PropertyValueFactory("marketPrice"));
        mktPriceCol.getStyleClass().add("right");

        TableColumn costCol = new TableColumn("Purchase Value");
        costCol.setCellValueFactory(new PropertyValueFactory("costBasis"));
        costCol.getStyleClass().add("right");

        TableColumn mktValueCol = new TableColumn("Current Value");
        mktValueCol.setCellValueFactory(new PropertyValueFactory("marketValue"));
        mktValueCol.getStyleClass().add("right");
        
        TableColumn plCol = new TableColumn("Gain/Loss Value");
        plCol.setCellValueFactory(new PropertyValueFactory("unrealizedPL"));
        plCol.getStyleClass().add("right");

        symbolCol.setSortable(false);
        nameCol.setSortable(false);
        unitsCol.setSortable(false);
        avgPriceCol.setSortable(false);
        mktPriceCol.setSortable(false);
        costCol.setSortable(false);
        mktValueCol.setSortable(false);
        plCol.setSortable(false);
        
        this.posTable.getColumns().setAll(symbolCol, nameCol, unitsCol, avgPriceCol, costCol, mktPriceCol, mktValueCol, plCol);

        this.posTable.setEditable(false);
        this.posTable.setItems(this.posList);

        // limit Table height, based on row number
        this.posTable.setFixedCellSize(20);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.posTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void initOrderTable () {
        // Pending Orders table
        TableColumn symbolCol = new TableColumn("Stock");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));
        symbolCol.getStyleClass().add("left");

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.getStyleClass().add("left");

        TableColumn unitsCol = new TableColumn("Units");
        unitsCol.setCellValueFactory(new PropertyValueFactory("units"));
        unitsCol.getStyleClass().add("right");

        TableColumn mktPriceCol = new TableColumn("Current Price");
        mktPriceCol.setCellValueFactory(new PropertyValueFactory("marketPrice"));
        mktPriceCol.getStyleClass().add("right");
        
        TableColumn typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
        typeCol.getStyleClass().add("left");

        TableColumn sideCol = new TableColumn("Side");
        sideCol.setCellValueFactory(new PropertyValueFactory("side"));
        sideCol.getStyleClass().add("left");
        
        TableColumn limitCol = new TableColumn("Limit Price");
        limitCol.setCellValueFactory(new PropertyValueFactory("limitPrice"));
        limitCol.getStyleClass().add("right");

        TableColumn stopCol = new TableColumn("Stop Price");
        stopCol.setCellValueFactory(new PropertyValueFactory("stopPrice"));
        stopCol.getStyleClass().add("right");
        
        symbolCol.setSortable(false);
        nameCol.setSortable(false);
        unitsCol.setSortable(false);
        mktPriceCol.setSortable(false);
        typeCol.setSortable(false);
        sideCol.setSortable(false);
        limitCol.setSortable(false);
        stopCol.setSortable(false);

        this.ordTable.getColumns().setAll(symbolCol, nameCol, unitsCol, mktPriceCol, typeCol, sideCol, limitCol, stopCol);

        this.ordTable.setEditable(false);
        this.ordTable.setItems(this.ordList);
        
        // limit Table height, based on row number
        this.ordTable.setFixedCellSize(20);
        this.ordTable.prefHeightProperty().bind(Bindings.size(this.ordTable.getItems()).multiply(this.ordTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.ordTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private DriveWealthAPI api;
    private Map<String, Object> accBlotter;
    private Map<String, Map> instruments;

    private List<OpenPos> positions = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    
    private ObservableList<OpenPosModel> posList = FXCollections.observableArrayList();
    private ObservableList<OrderModel> ordList = FXCollections.observableArrayList();
    
    public  final Tab accTab  = new Tab();
    
    private final BorderPane accBorderPane = new BorderPane();
    private final Label shareAmount = new Label();
    private final Label profitAmount = new Label();
    private final Label cashAmount = new Label();
    private final Label totalAmount = new Label();
    
    private final TableView posTable = new TableView();
    private final TableView ordTable = new TableView();
    
    private AccountSummary acc;
}
    