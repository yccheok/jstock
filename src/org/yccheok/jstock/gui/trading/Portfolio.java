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
import org.yccheok.jstock.trading.OpenPos;
import org.yccheok.jstock.trading.Order;
import org.yccheok.jstock.trading.AccountSummary;
import org.yccheok.jstock.trading.OpenPosModel;
import org.yccheok.jstock.trading.OrderModel;
import org.yccheok.jstock.trading.Utils;

/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    public Portfolio (Map<String, Object> accBlotter, Map<String, Map> instruments) {
        this.accBlotter = accBlotter;
        this.instruments = instruments;
    }

    public void getOpenPositions () {
        LinkedTreeMap<String, Object> equity = (LinkedTreeMap) this.accBlotter.get("equity");
        List<LinkedTreeMap<String, Object>> result = (List) equity.get("equityPositions");

        for (LinkedTreeMap<String, Object> a : result) {
            Map<String, Object> ins = this.instruments.get(a.get("symbol").toString());
            OpenPos pos = new OpenPos(a, ins);
            this.positions.add(pos);
        }
    }
    
    public void getPendingOrders () {
        List<LinkedTreeMap<String, Object>> result = (List) this.accBlotter.get("orders");

        for (LinkedTreeMap<String, Object> a : result) {
            Map<String, Object> ins = this.instruments.get(a.get("symbol").toString());
            Order order = new Order(a, ins);
            this.orders.add(order);
        }
    }
    
    public Tab createTab() {
        getOpenPositions();
        getPendingOrders();

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
        vboxOpenPos.setPrefWidth(1000);

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

        // add account summary tab
        this.accTab.setText("Portfolio (Practice Account)");
        this.accTab.setClosable(false);
        this.accTab.setContent(vBox);

        return this.accTab;
    }

    public void initAccSummary () {
        AccountSummary acc = new AccountSummary(this.accBlotter, this.positions);

        // Left content
        HBox leftHbox = new HBox(8);
        
        // Total Open positions value
        Label shareText = new Label("Share:");
        Label shareAmount = new Label(Utils.monetaryFormat(acc.equityValue, true));
        shareAmount.getStyleClass().add("profit");
        
        // Total unrealized PL
        Label profitText = new Label("Paper Profit:");
        profitText.setPadding(new Insets(0, 0, 0, 10));

        String amountStr = Utils.monetaryFormat(acc.totalUnrealizedPL, true) + " (" + Utils.monetaryFormat(acc.totalUnrealizedPLPercent) + "%)";
        Label profitAmount = new Label(amountStr);
        profitAmount.getStyleClass().add((acc.totalUnrealizedPL > 0) ? "profit" : "loss");

        leftHbox.getChildren().addAll(shareText, shareAmount, profitText, profitAmount);
        
        // Right content
        HBox rightHbox = new HBox(8);
        
        // Cash for trading
        Label cashText = new Label("Cash to Invest:");
        Label cashAmount = new Label(Utils.monetaryFormat(acc.cashForTrade, true));
        cashAmount.getStyleClass().add((acc.cashForTrade > 0) ? "profit" : "loss");

        // Total
        Label totalText = new Label("Total:");
        totalText.setPadding(new Insets(0, 0, 0, 10));
        Label totalAmount = new Label(Utils.monetaryFormat(acc.accountTotal, true));
        totalAmount.getStyleClass().add((acc.accountTotal > 0) ? "profit" : "loss");

        rightHbox.getChildren().addAll(cashText, cashAmount, totalText, totalAmount);
        
        this.accBorderPane.setPadding(new Insets(5, 0, 10, 0));    // Insets: top, right, bottom, left
        this.accBorderPane.setLeft(leftHbox);
        this.accBorderPane.setRight(rightHbox);
        this.accBorderPane.setId("accBorderPane");
    }
    
    public void initOpenPosTable () {
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

        this.posTable.setEditable(false);

        final ObservableList<OpenPosModel> posList = FXCollections.observableArrayList();
        for (OpenPos pos : this.positions) {
            posList.add(new OpenPosModel(pos));
        }

        this.posTable.setItems(posList);
        this.posTable.getColumns().setAll(symbolCol, nameCol, unitsCol, avgPriceCol, costCol, mktPriceCol, mktValueCol, plCol);

        // limit Table height, based on row number
        this.posTable.setFixedCellSize(20);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.posTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    public void initOrderTable () {
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

        this.ordTable.setEditable(false);

        final ObservableList<OrderModel> ordList = FXCollections.observableArrayList();
        for (Order ord : this.orders) {
            ordList.add(new OrderModel(ord));
        }

        this.ordTable.setItems(ordList);
        this.ordTable.getColumns().setAll(symbolCol, nameCol, unitsCol, mktPriceCol, typeCol, sideCol, limitCol, stopCol);

        // limit Table height, based on row number
        this.ordTable.setFixedCellSize(20);
        this.ordTable.prefHeightProperty().bind(Bindings.size(this.ordTable.getItems()).multiply(this.ordTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.ordTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private final Map<String, Object> accBlotter;
    private final Map<String, Map> instruments;
    
    private final List<OpenPos> positions = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    
    public  final Tab accTab  = new Tab();
    private final BorderPane accBorderPane = new BorderPane();
    private final TableView posTable = new TableView();
    private final TableView ordTable = new TableView();
}
    