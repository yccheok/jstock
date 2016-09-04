/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.Utils;
import org.yccheok.jstock.trading.Transaction;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.InstrumentManager;
import org.yccheok.jstock.trading.API.OrderManager;
import org.yccheok.jstock.trading.API.SessionManager;

/**
 *
 * @author shuwnyuan
 */
public class PositionsTableBuilder {
    private final TableView posTable = new TableView();
    private final ObservableList<PositionModel> posList = FXCollections.observableArrayList();

    
    public PositionsTableBuilder () {}
    
    public ObservableList<PositionModel> getPosList () {
        return this.posList;
    }
    
    private class FormatNumberCell extends TableCell<PositionModel, Number> {
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
    
    private void setRowContextMenu () {
        this.posTable.setRowFactory(new Callback<TableView<PositionModel>, TableRow<PositionModel>>() {
                @Override
                public TableRow<PositionModel> call(TableView<PositionModel> tableView) {
                    final TableRow<PositionModel> row = new TableRow<>();
                    final ContextMenu rowMenu = new ContextMenu();
                    
                    final MenuItem buyItem = new MenuItem("Buy");
                    buyItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            PositionModel pos = row.getItem();
                            
                            System.out.println("Buy button pressed, symbol: " + pos.getSymbol()
                                    + ", instrumentID: " + pos.getInstrumentID());
                            
                            showBuyDialog(pos);
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
    
    public void showBuyDialog (PositionModel pos) {
        String symbol       = pos.getSymbol();
        String name         = pos.getName();
        String instrumentID = pos.getInstrumentID();
        
        // TODO: Scheduled service to get ASK price => get market Data / get instrument
        Double askPrice = null;

        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Buy Order");
        dialog.setHeaderText("Buy " + symbol + " - " + name);

        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        ButtonType buyButtonType    = new ButtonType("Buy", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Symbol
        TextField symbolText = new TextField(symbol);
        symbolText.setPromptText("Symbol");
        symbolText.setEditable(false);

        // Order Type: Market, Stop, Limit
        ChoiceBox<OrderManager.OrderType> orderChoice = new ChoiceBox<>();
        orderChoice.getItems().setAll( OrderManager.OrderType.values() );
        orderChoice.setValue(OrderManager.OrderType.MARKET);

        // Ask price
        // TODO: bind value to Task - update ask price
        TextField askText = new TextField();
        askText.setPromptText("Ask Price");
        askText.setEditable(false);

        // Qty
        // TODO: get Instrument gives: orderSizeMax, orderSizeMin, orderSizeStep
        TextField qtyText = new TextField();
        qtyText.setPromptText("Units");
        
        grid.add(new Label("Stock:"), 0, 0);
        grid.add(symbolText, 1, 0);
        
        grid.add(new Label("Order Type:"), 0, 1);
        grid.add(orderChoice, 1, 1);

        grid.add(new Label("Ask Price:"), 0, 2);
        grid.add(askText, 1, 2);

        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(qtyText, 1, 3);

        // Enable/Disable buy button depending on whether qty was entered.
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

        dialog.getDialogPane().setContent(grid);

        // Request focus on the Qty field by default.
        Platform.runLater(() -> qtyText.requestFocus());

        // Convert the result when the BUY button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buyButtonType) {

                SessionManager.User user   = DriveWealth.getUser();
                SessionManager.Account acc = user.getActiveAccount();

                String userID    = user.getUserID();
                String accountID = acc.getAccountID();
                String accountNo = acc.getAccountNo();
                
                Map<String, Object> params = new HashMap<>();

                params.put("symbol",        symbolText.getText());
                params.put("instrumentID",  instrumentID);
                params.put("accountID",     accountID);
                params.put("accountNo",     accountNo);
                params.put("userID",        userID);

                // 1: practice a/c
                // 2: live a/c
                params.put("accountType", acc.getAccountType().getValue());
                
                // 1: Market order
                // 2: Limit order
                // 3: Stop order
                params.put("ordType", orderChoice.getValue().getValue());
                
                params.put("side", "B");
                params.put("orderQty", Double.parseDouble(qtyText.getText()));                
                params.put("comment", "SY Sunday Test - Market Order BUY");

                return params;
            }
            return null;
        });
        
        Optional<Map<String, Object>> result = dialog.showAndWait();

        result.ifPresent(buyParams -> {
            Transaction.buy(Portfolio.portfolioService, buyParams);
            
            System.out.println("Buy Order SYMBOL : " + buyParams.get("symbol").toString());
        });
        
    }

    public TableView build () {
        // Open Positions table
        TableColumn<PositionModel, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));
        symbolCol.getStyleClass().add("left");

        TableColumn<PositionModel, String> nameCol = new TableColumn<>("Company");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.getStyleClass().add("left");

        TableColumn<PositionModel, Number> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(cellData -> cellData.getValue().openQtyProperty());
        unitsCol.setCellFactory((TableColumn<PositionModel, Number> col) -> new FormatNumberCell(false));
        unitsCol.getStyleClass().add("right");

        TableColumn<PositionModel, Number> avgPriceCol = new TableColumn<>("Average Purchase Price");
        avgPriceCol.setCellValueFactory(cellData -> cellData.getValue().averagePriceProperty());
        avgPriceCol.setCellFactory((TableColumn<PositionModel, Number> col) -> new FormatNumberCell(false));
        avgPriceCol.getStyleClass().add("right");

        TableColumn<PositionModel, Number> mktPriceCol = new TableColumn<>("Current Price");
        mktPriceCol.setCellValueFactory(cellData -> cellData.getValue().marketPriceProperty());
        mktPriceCol.setCellFactory((TableColumn<PositionModel, Number> col) -> new FormatNumberCell(false));
        mktPriceCol.getStyleClass().add("right");

        TableColumn<PositionModel, Number> costCol = new TableColumn<>("Purchase Value");
        costCol.setCellValueFactory(cellData -> cellData.getValue().costBasisProperty());
        costCol.setCellFactory((TableColumn<PositionModel, Number> col) -> new FormatNumberCell(false));
        costCol.getStyleClass().add("right");

        TableColumn<PositionModel, Number> mktValueCol = new TableColumn<>("Current Value");
        mktValueCol.setCellValueFactory(cellData -> cellData.getValue().marketValueProperty());
        mktValueCol.setCellFactory((TableColumn<PositionModel, Number> col) -> new FormatNumberCell(false));
        mktValueCol.getStyleClass().add("right");
        
        TableColumn<PositionModel, Number> plCol = new TableColumn<>("Gain/Loss Value");
        plCol.setCellValueFactory(cellData -> cellData.getValue().unrealizedPLProperty());
        plCol.setCellFactory((TableColumn<PositionModel, Number> col) -> new FormatNumberCell(true));
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
        
        // set Data Model
        this.posTable.setItems(this.posList);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));
        
        return this.posTable;
    }
    
    public void initData (List<PositionModel> positions, Map<String, InstrumentManager.Instrument> instruments) {
        this.posList.clear();
        this.posList.addAll(positions);

        if (! instruments.isEmpty()) {
            updateStocksName(instruments);
        }
    }

    public void updateStocksName (Map<String, InstrumentManager.Instrument> instruments) {
        for (PositionModel pos : this.posList) {
            final String symbol = pos.getSymbol();
            
            if (instruments.containsKey(symbol)) {
                InstrumentManager.Instrument ins = instruments.get(symbol);
                pos.setName(ins.getName());
            }
        }
    }
    
    public void updatePrices (Map<String, Double> marketPrices) {
        for (PositionModel pos : this.posList) {
            final String symbol = pos.getSymbol();
            
            if (marketPrices.containsKey(symbol)) {
                Double price = marketPrices.get(symbol);
                pos.setMarketPrice(price);
            }
        }
    }
}
