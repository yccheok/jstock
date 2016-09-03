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
    
    public Map<String, Object> buildBuyParam (String symbol, String instrumentID) {
        Map<String, Object> params = new HashMap<>();

        SessionManager.User user = DriveWealth.getUser();
        SessionManager.Account acc = user.getActiveAccount();
        
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
        this.posTable.setRowFactory(new Callback<TableView<PositionModel>, TableRow<PositionModel>>() {
                @Override
                public TableRow<PositionModel> call(TableView<PositionModel> tableView) {
                    final TableRow<PositionModel> row = new TableRow<>();
                    final ContextMenu rowMenu = new ContextMenu();
                    
                    final MenuItem buyItem = new MenuItem("Buy");
                    buyItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String symbol = row.getItem().getSymbol();
                            String name = row.getItem().getName();
                            String instrumentID = row.getItem().getInstrumentID();
                            
                            /////////////
                            // TODO: add ASK price to Model, this Last Price is WRONG
                            Double askPrice = row.getItem().getMarketPrice();

                            System.out.println("Buy button pressed, symbol: " + symbol + ", instrumentID: " + instrumentID);
                            
                            // TODO: BUY Dialog => should add scheduled service background - keep updating ASK / BID price

                            SessionManager.User user = DriveWealth.getUser();
                            SessionManager.Account acc = user.getActiveAccount();

                            String userID = user.getUserID();
                            String accountID = acc.getAccountID();
                            String accountNo = acc.getAccountNo();

                            Map<String, Object> params = new HashMap<>();
                                    
                            params.put("symbol", symbol);
                            params.put("instrumentID", instrumentID);
                            params.put("name", name);
                            params.put("askPrice", askPrice);
                            
                            params.put("accountID", accountID);
                            params.put("accountNo", accountNo);
                            params.put("userID", userID);                    
                            
                            showBuyDialog(params);
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
    
    private class BuyData {
        private Map<String, Object> params;
        
        public BuyData (Map<String, Object> params) {
            this.params = params;
            System.out.println("BUY MARKET ORDER Dialog - Instantiating BuyData when BUY button pressed");
        }
        
        public Map<String, Object> getParams () {
            return this.params;
        }
    }
    
    
    public void showBuyDialog (Map<String, Object> params) {

        String symbol       = params.get("symbol").toString();
        String name         = params.get("name").toString();
        String instrumentID = params.get("instrumentID").toString();
        Double askPrice     = (Double) params.get("askPrice");

        String accountID = params.get("accountID").toString();
        String accountNo = params.get("accountNo").toString();
        String userID = params.get("userID").toString();

        
        // Create the custom dialog.
        Dialog<BuyData> dialog = new Dialog<>();
        dialog.setTitle("Buy Order");
        dialog.setHeaderText("Buy " + symbol + " - " + name);

        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        // Set the button types.
        ButtonType buyButtonType = new ButtonType("Buy", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, cancelButtonType);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Symbol
        TextField symbolText = new TextField(symbol);
        symbolText.setEditable(false);
        
        // Order Type: Market, Stop, Limit
        ChoiceBox<String> orderType = new ChoiceBox<>();
        orderType.getItems().addAll("Market", "Stop", "Limit");

        // Ask price
        TextField askText = new TextField(askPrice.toString());
        askText.setPromptText("Ask Price");
        askText.setEditable(false);

        // Qty
        TextField qtyText = new TextField();
        qtyText.setPromptText("Units");
        
        grid.add(new Label("Stock:"), 0, 0);
        grid.add(symbolText, 1, 0);
        
        grid.add(new Label("Order Type:"), 0, 1);
        grid.add(orderType, 1, 1);

        grid.add(new Label("Ask Price:"), 0, 2);
        grid.add(askText, 1, 2);

        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(qtyText, 1, 3);

        // Enable/Disable login button depending on whether a username was entered.
        Node buyButton = dialog.getDialogPane().lookupButton(buyButtonType);
        buyButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        qtyText.textProperty().addListener((observable, oldValue, newValue) -> {
            buyButton.setDisable(Double.parseDouble(newValue) <= 0);
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> qtyText.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buyButtonType) {
                
                Map<String, Object> _params = new HashMap<>();
                
                _params.put("symbol", symbolText.getText());
                _params.put("instrumentID", instrumentID);
                _params.put("accountID", accountID);
                _params.put("accountNo", accountNo);
                _params.put("userID", userID);

                // 1: practice a/c, 2: live a/c
                _params.put("accountType", 1);
                // 1: market order
                _params.put("ordType", 1);
                _params.put("side", "B");
                _params.put("orderQty", Double.parseDouble(qtyText.getText()));                
                _params.put("comment", "SY Testing");
                
                BuyData buyData = new BuyData(_params);
                return buyData;
            }
            return null;
        });
        
        Optional<BuyData> result = dialog.showAndWait();

        result.ifPresent(buyData -> {
            Transaction.buy(Portfolio.portfolioService, buyData.getParams());
            
            System.out.println("Buy Order SYMBOL : " + buyData.getParams().get("symbol").toString());
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
