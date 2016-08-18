/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.yccheok.jstock.trading.OrderModel;
import org.yccheok.jstock.trading.Utils;

/**
 *
 * @author shuwnyuan
 */
public class OrdersTableBuilder {
    public final TableView ordTable = new TableView();
    private final ObservableList<OrderModel> ordList = FXCollections.observableArrayList();
    
    public OrdersTableBuilder () {}
    
    public ObservableList<OrderModel> getOrdList () {
        return this.ordList;
    }
    
    private class FormatNumberCell extends TableCell<OrderModel, Number> {
        public FormatNumberCell() {}

        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : Utils.monetaryFormat((Double) item));
        }
    }
    
    private FormatNumberCell getNumberCell () {
        final FormatNumberCell cell = new FormatNumberCell();
        
        cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Order Table UNITS col clicked...");

                }
            }
        });
        return cell;
    }

    private TableCell<OrderModel, String> getStringCell () {
        final TableCell<OrderModel, String> cell = new TableCell<>();
        cell.textProperty().bind(cell.itemProperty());
        
        cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Order Table UNITS col clicked...");

                }
            }
        });
        return cell;
    }
    
    public TableView build () {
        // Pending Orders table
        TableColumn<OrderModel, String> symbolCol = new TableColumn("Stock");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));
        symbolCol.setCellFactory((TableColumn<OrderModel, String> col) -> getStringCell());
        symbolCol.getStyleClass().add("left");

        TableColumn<OrderModel, String> nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.setCellFactory((TableColumn<OrderModel, String> col) -> getStringCell());
        nameCol.getStyleClass().add("left");

        TableColumn<OrderModel, Number> unitsCol = new TableColumn("Units");
        unitsCol.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        unitsCol.setCellFactory((TableColumn<OrderModel, Number> col) -> getNumberCell());
        unitsCol.getStyleClass().add("right");

        TableColumn<OrderModel, Number> mktPriceCol = new TableColumn("Current Price");
        mktPriceCol.setCellValueFactory(cellData -> cellData.getValue().marketPriceProperty());
        mktPriceCol.setCellFactory((TableColumn<OrderModel, Number> col) -> getNumberCell());
        mktPriceCol.getStyleClass().add("right");
        
        TableColumn<OrderModel, String> typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
        typeCol.setCellFactory((TableColumn<OrderModel, String> col) -> getStringCell());
        typeCol.getStyleClass().add("left");

        TableColumn<OrderModel, String> sideCol = new TableColumn("Side");
        sideCol.setCellValueFactory(new PropertyValueFactory("side"));
        sideCol.setCellFactory((TableColumn<OrderModel, String> col) -> getStringCell());
        sideCol.getStyleClass().add("left");
        
        TableColumn<OrderModel, Number> limitCol = new TableColumn("Limit Price");
        limitCol.setCellValueFactory(cellData -> cellData.getValue().limitPriceProperty());
        limitCol.setCellFactory((TableColumn<OrderModel, Number> col) -> getNumberCell());
        limitCol.getStyleClass().add("right");

        TableColumn<OrderModel, Number> stopCol = new TableColumn("Stop Price");
        stopCol.setCellValueFactory(cellData -> cellData.getValue().stopPriceProperty());
        stopCol.setCellFactory((TableColumn<OrderModel, Number> col) -> getNumberCell());
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
        
        // limit Table height, based on row number
        this.ordTable.setFixedCellSize(Portfolio.tableCellSize);
        this.ordTable.prefHeightProperty().bind(Bindings.size(this.ordTable.getItems()).multiply(this.ordTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.ordTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        return this.ordTable;
    }
    
    public void initData (Map<String, Object> accBlotter, Map<String, Double> marketPrices) {
        List<LinkedTreeMap<String, Object>> orders = (List) accBlotter.get("orders");

        for (LinkedTreeMap<String, Object> ord : orders) {
            String symbol = ord.get("symbol").toString();
            
            Map<String, Object> data = new HashMap<>();
            data.put("name",        "");
            data.put("marketPrice", marketPrices.get(symbol));
            data.put("symbol",      symbol);
            data.put("units",       ord.get("orderQty"));
            data.put("side",        ord.get("side"));
            data.put("orderType",   ord.get("orderType"));

            if (ord.containsKey("limitPrice")) {
                data.put("limitPrice", ord.get("limitPrice"));
            }
            if (ord.containsKey("stopPrice")) {
                data.put("stopPrice", ord.get("stopPrice"));
            }
            
            this.ordList.add(new OrderModel(data));
        }
        
        this.ordTable.setItems(this.ordList);
        this.ordTable.prefHeightProperty().bind(Bindings.size(this.ordTable.getItems()).multiply(this.ordTable.getFixedCellSize()).add(30));
    }
    
    public void updateStocksName (Map<String, Map> instruments) {
        for (OrderModel ord : this.ordList) {
            final String symbol = ord.getSymbol();
            
            if (instruments.containsKey(symbol)) {
                Map<String, Object> ins = instruments.get(symbol);
                ord.updateStockName(ins.get("name").toString());
            }
        }
    }
    
    public void updatePrices (Map<String, Double> marketPrices) {
        for (OrderModel ord : this.ordList) {
            final String symbol = ord.getSymbol();
            
            if (marketPrices.containsKey(symbol)) {
                Double price = marketPrices.get(symbol);
                ord.updateMarketPrice(price);
            }
        }
    }
}
