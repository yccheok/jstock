/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.yccheok.jstock.trading.OpenPosModel;
import org.yccheok.jstock.trading.Utils;
import org.yccheok.jstock.trading.BuySell;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.User;
import org.yccheok.jstock.trading.API.Account;

/**
 *
 * @author shuwnyuan
 */
public class PositionsTableBuilder {
    private final TableView posTable = new TableView();
    private final ObservableList<OpenPosModel> posList = FXCollections.observableArrayList();

    
    public PositionsTableBuilder () {}
    
    public ObservableList<OpenPosModel> getPosList () {
        return this.posList;
    }
    
    private class FormatNumberCell extends TableCell<OpenPosModel, Number> {
        private final boolean style;

        public FormatNumberCell (boolean style) {
            this.style = style;
        }

        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);

            // 1000.56 => 1,000.56, -9999.80 => -9,999.80
            setText(item == null ? "" : Utils.monetaryFormat((Double) item));

            // show profit as GREEN, loss as RED
            if (this.style == true && item != null) {
                double value = item.doubleValue();
                setTextFill(value < 0 ? Color.RED : Color.GREEN);
            }
        }
    }
    
    public Map<String, Object> buildBuyParam (String symbol, String instrumentID) {
        Map<String, Object> params = new HashMap<>();

        User user = Portfolio.getAPI().getUser();
        Account acc = user.getPracticeAccount();
        
        String userID = user.getUserID();
        String accountID = acc.getAccountID();
        String accountNo = acc.getAccountNo();

        // get instrumentID
        params.put("symbol", symbol);
        params.put("instrumentID", instrumentID);
        params.put("accountID", accountID);
        params.put("accountNo", accountNo);
        params.put("userID", userID);
        
        // 1: practice a/c, 2: live a/c
        params.put("accountType", 1);
        // 1: market order
        params.put("ordType", 1);
        params.put("side", "B");
        params.put("orderQty", 1.0);
        params.put("comment", "SY Testing");

        return params;
    }
    
    private void setRowContextMenu () {
        this.posTable.setRowFactory(
            new Callback<TableView<OpenPosModel>, TableRow<OpenPosModel>>() {
                @Override
                public TableRow<OpenPosModel> call(TableView<OpenPosModel> tableView) {
                    final TableRow<OpenPosModel> row = new TableRow<>();
                    final ContextMenu rowMenu = new ContextMenu();
                    
                    final MenuItem buyItem = new MenuItem("Buy");
                    buyItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String symbol = row.getItem().getSymbol();
                            String instrumentID = row.getItem().getInstrumentID();

                            
                            System.out.println("Buy button pressed, symbol: " + symbol + "instrumentID: " + instrumentID);

                            
                            BuySell buySell = new BuySell(Portfolio.getAPI(), Portfolio.portfolioService);
                            Map<String, Object> params = buildBuyParam(symbol, instrumentID);
                            buySell.buy(params);
                        }
                    });
                    
                    final MenuItem sellItem = new MenuItem("Sell");
                    sellItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            //posTable.getItems().remove(row.getItem());
                        }
                    });
                    
                    final MenuItem chartItem = new MenuItem("History Chart");
                    chartItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            //posTable.getItems().remove(row.getItem());
                        }
                    });
                    
                    rowMenu.getItems().addAll(buyItem, sellItem, new SeparatorMenuItem(), chartItem);

                    // only display context menu for non-null items:
                    row.contextMenuProperty().bind(
                        Bindings.when(Bindings.isNotNull(row.itemProperty()))
                        .then(rowMenu)
                        .otherwise((ContextMenu)null));
                    
                    return row;
                }
            }
        );
    }
    
    public TableView build () {
        // Open Positions table
        TableColumn<OpenPosModel, String> symbolCol = new TableColumn<>("Stock");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));
        symbolCol.getStyleClass().add("left");

        TableColumn<OpenPosModel, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.getStyleClass().add("left");

        TableColumn<OpenPosModel, Number> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(cellData -> cellData.getValue().openQtyProperty());
        unitsCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> new FormatNumberCell(false));
        unitsCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> avgPriceCol = new TableColumn<>("Average Purchase Price");
        avgPriceCol.setCellValueFactory(cellData -> cellData.getValue().averagePriceProperty());
        avgPriceCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> new FormatNumberCell(false));
        avgPriceCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> mktPriceCol = new TableColumn<>("Current Price");
        mktPriceCol.setCellValueFactory(cellData -> cellData.getValue().marketPriceProperty());
        mktPriceCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> new FormatNumberCell(false));
        mktPriceCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> costCol = new TableColumn<>("Purchase Value");
        costCol.setCellValueFactory(cellData -> cellData.getValue().costBasisProperty());
        costCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> new FormatNumberCell(false));
        costCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> mktValueCol = new TableColumn<>("Current Value");
        mktValueCol.setCellValueFactory(cellData -> cellData.getValue().marketValueProperty());
        mktValueCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> new FormatNumberCell(false));
        mktValueCol.getStyleClass().add("right");
        
        TableColumn<OpenPosModel, Number> plCol = new TableColumn<>("Gain/Loss Value");
        plCol.setCellValueFactory(cellData -> cellData.getValue().unrealizedPLProperty());
        plCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> new FormatNumberCell(true));
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

        // limit Table height, based on row number
        this.posTable.setFixedCellSize(Portfolio.TABLE_CELL_SIZE);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.posTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Right click on row, show Menu
        setRowContextMenu();

        // display empty message when table is empty
        this.posTable.setPlaceholder(new Label());
        
        return this.posTable;
    }
    
    public void initData (List<OpenPosModel> positions, Map<String, Map> instruments) {
        this.posList.clear();
        this.posList.addAll(positions);

        if (! instruments.isEmpty()) {
            updateStocksName(instruments);
        }
        
        this.posTable.setItems(this.posList);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));
    }

    public void updateStocksName (Map<String, Map> instruments) {
        for (OpenPosModel pos : this.posList) {
            final String symbol = pos.getSymbol();
            
            if (instruments.containsKey(symbol)) {
                Map<String, Object> ins = instruments.get(symbol);
                pos.setName(ins.get("name").toString());
            }
        }
    }
    
    public void updatePrices (Map<String, Double> marketPrices) {
        for (OpenPosModel pos : this.posList) {
            final String symbol = pos.getSymbol();
            
            if (marketPrices.containsKey(symbol)) {
                Double price = marketPrices.get(symbol);
                pos.setMarketPrice(price);
            }
        }
    }
}
