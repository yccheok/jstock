/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.yccheok.jstock.trading.API.InstrumentManager;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.OrderModel;
import org.yccheok.jstock.trading.Utils;
import org.yccheok.jstock.trading.PositionModel.SymbolUrl;
/**
 *
 * @author shuwnyuan
 */
public class OrdersTableBuilder {
    private final TableView ordTable = new TableView();
    private final ObservableList<OrderModel> ordList = FXCollections.observableArrayList();
    
    public OrdersTableBuilder () {}
    
    public ObservableList<OrderModel> getOrdList () {
        return this.ordList;
    }
    
    
    private class SymbolCell extends TableCell<OrderModel, SymbolUrl> {
        private final HBox hbox = new HBox();
        private final Label symLabel = new Label();
        private final ImageView imageView = new ImageView();

        public SymbolCell () {
            // So symbol icon aligns vertically for each row
            symLabel.setPrefWidth(70);

            // Set image height = table cell height
            // defaul image size: 125x125. Set width = height so image scale proportionally
            imageView.setFitHeight(Portfolio.TABLE_CELL_SIZE);
            imageView.setFitWidth(Portfolio.TABLE_CELL_SIZE);
            
            hbox.setSpacing(10) ;
            // align text vertically center
            hbox.setAlignment(Pos.CENTER_LEFT);

            hbox.getChildren().addAll(symLabel, imageView);
            setGraphic(hbox);
        }
        
        @Override
        protected void updateItem (SymbolUrl item, boolean empty) {
            if (item != null) {
                String symbol = item.getSymbol();
                String url = item.getUrl();

                symLabel.setText(symbol);

                if (url != null && ! url.isEmpty()) {
                    // use background loading:  public Image(String url, boolean backgroundLoading)
                    Image icon = new Image(url, true);
                    imageView.setImage(icon);
                } else {
                    imageView.setImage(null);
                }
            }
        }
    }
    
    private class FormatNumberCell extends TableCell<OrderModel, Number> {
        private final int decimal;
        
        public FormatNumberCell () {
            this.decimal = 2;
        }
        
        public FormatNumberCell (int decimal) {
            this.decimal = decimal;
        }

        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            
            if (this.decimal == 2) {
                setText(item == null ? "" : Utils.monetaryFormat((Double) item));
            } else {
                setText(item == null ? "" : Utils.formatNumber((Double) item, this.decimal));    
            }
        }
    }

    private void setRowContextMenu () {
        this.ordTable.setRowFactory(new Callback<TableView<OrderModel>, TableRow<OrderModel>>() {
            @Override
            public TableRow<OrderModel> call(TableView<OrderModel> tableView) {
                final TableRow<OrderModel> row = new TableRow<>();
                final ContextMenu rowMenu = new ContextMenu();

                final MenuItem cancelItem = new MenuItem("Cancel");
                cancelItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        CancelOrderDlg dlg = new CancelOrderDlg(row.getItem());
                        dlg.initDlgAndWait();
                    }
                });

                final MenuItem chartItem = new MenuItem("History Chart");
                chartItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //posTable.getItems().remove(row.getItem());
                    }
                });

                rowMenu.getItems().addAll(cancelItem, new SeparatorMenuItem(), chartItem);

                // only display context menu for non-null items:
                row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu)null));

                return row;
            }
        });
    }

    public TableView build () {
        // Pending Orders table
        
        // Symbol column: show symbol name + stock's icon 
        TableColumn<OrderModel, SymbolUrl> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbolObj"));
        symbolCol.setCellFactory((TableColumn<OrderModel, SymbolUrl> col) -> new SymbolCell());
        symbolCol.getStyleClass().add("left");

        TableColumn<OrderModel, String> nameCol = new TableColumn("Company");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.getStyleClass().add("left");

        TableColumn<OrderModel, Number> unitsCol = new TableColumn("Units");
        unitsCol.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        unitsCol.setCellFactory((TableColumn<OrderModel, Number> col) -> new FormatNumberCell(4));
        unitsCol.getStyleClass().add("right");

        TableColumn<OrderModel, Number> mktPriceCol = new TableColumn("Current Price");
        mktPriceCol.setCellValueFactory(cellData -> cellData.getValue().marketPriceProperty());
        mktPriceCol.setCellFactory((TableColumn<OrderModel, Number> col) -> new FormatNumberCell());
        mktPriceCol.getStyleClass().add("right");
        
        TableColumn<OrderModel, String> typeCol = new TableColumn("Order Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
        typeCol.getStyleClass().add("left");

        TableColumn<OrderModel, String> sideCol = new TableColumn("Action");
        sideCol.setCellValueFactory(new PropertyValueFactory("side"));
        sideCol.getStyleClass().add("left");

        TableColumn<OrderModel, Number> ordPriceCol = new TableColumn("Order Price");
        ordPriceCol.setCellValueFactory(cellData -> cellData.getValue().orderPriceProperty());
        ordPriceCol.setCellFactory((TableColumn<OrderModel, Number> col) -> new FormatNumberCell());
        ordPriceCol.getStyleClass().add("right");

        // Cancel Order Button
        TableColumn<OrderModel, OrderModel> cancelCol = new TableColumn("Cancel Order");
        ordPriceCol.getStyleClass().add("right");

        cancelCol.setCellFactory((TableColumn<OrderModel, OrderModel> col) -> new TableCell<OrderModel, OrderModel>() {
            Button cancelBtn = new Button("Cancel");

            @Override
            public void updateItem(OrderModel item, boolean empty) {
                super.updateItem(item, empty);
                
                if (!isEmpty()) {
                    setGraphic(cancelBtn);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        /////////
        //
        //  TODO
        //      Cancel Order BUTTON handler
        //
        ////////
        
        /*
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle (ActionEvent e) {



                CancelOrderDlg dlg = new CancelOrderDlg(item);
                dlg.initDlgAndWait();
            }
        });
        */
        
        
        
        symbolCol.setSortable(false);
        nameCol.setSortable(false);
        unitsCol.setSortable(false);
        mktPriceCol.setSortable(false);
        typeCol.setSortable(false);
        sideCol.setSortable(false);
        ordPriceCol.setSortable(false);
        cancelCol.setSortable(false);

        this.ordTable.getColumns().setAll(symbolCol, nameCol, unitsCol, mktPriceCol, typeCol, sideCol, ordPriceCol, cancelCol);

        this.ordTable.setEditable(false);
        
        // limit Table height, based on row number
        this.ordTable.setFixedCellSize(Portfolio.TABLE_CELL_SIZE);
        this.ordTable.prefHeightProperty().bind(Bindings.size(this.ordTable.getItems()).multiply(this.ordTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.ordTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Right clicked on row, show menu
        setRowContextMenu();
        
        // display empty message when table is empty
        this.ordTable.setPlaceholder(new Label());
        
        // set Data Model
        this.ordTable.setItems(this.ordList);
        this.ordTable.prefHeightProperty().bind(Bindings.size(this.ordTable.getItems()).multiply(this.ordTable.getFixedCellSize()).add(30));
        
        return this.ordTable;
    }
    
    public void initData (List<OrderModel> orders, Map<String, InstrumentManager.Instrument> instruments, Map<String, Double> marketPrices) {
        this.ordList.clear();
        this.ordList.addAll(orders);
        
        if (! instruments.isEmpty()) {
            updateNameURL(instruments);
        }
        
        updatePrices(marketPrices);
    }
    
    public void updateNameURL (Map<String, InstrumentManager.Instrument> instruments) {
        for (OrderModel ord : this.ordList) {
            final String symbol = ord.getSymbol();
            
            if (instruments.containsKey(symbol)) {
                InstrumentManager.Instrument ins = instruments.get(symbol);
                ord.setName(ins.getName());
                
                // update Stock's icon URL
                ord.setUrlImage(ins.getUrlImage());
            }
        }
    }
    
    public void updatePrices (Map<String, Double> marketPrices) {
        for (OrderModel ord : this.ordList) {
            final String symbol = ord.getSymbol();
            
            if (marketPrices.containsKey(symbol)) {
                Double price = marketPrices.get(symbol);
                ord.setMarketPrice(price);
            }
        }
    }
}
