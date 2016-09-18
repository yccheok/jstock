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
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.PositionModel.SymbolUrl;

import org.yccheok.jstock.trading.Utils;
import org.yccheok.jstock.trading.API.InstrumentManager;
import org.yccheok.jstock.trading.API.OrderManager;
import org.yccheok.jstock.trading.API.OrderManager.OrderSide;

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

    
    private class SymbolCell extends TableCell<PositionModel, SymbolUrl> {
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
                }
            }
        }
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
                            
                            //System.out.println("Buy button pressed, symbol: " + pos.getSymbol()
                            //        + ", instrumentID: " + pos.getInstrumentID());

                            OrderDialog.newOrderDlg(pos, OrderSide.BUY);
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
        
        // Symbol column: show symbol name + stock's icon 
        TableColumn<PositionModel, SymbolUrl> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbolObj"));
        symbolCol.setCellFactory((TableColumn<PositionModel, SymbolUrl> col) -> new SymbolCell());
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

        // set all columns having equal width
        this.posTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Right click on row, show Menu
        setRowContextMenu();

        // display empty message when table is empty
        this.posTable.setPlaceholder(new Label());
        
        // set Data Model
        this.posTable.setItems(this.posList);
        
        // limit Table height, based on row number
        this.posTable.setFixedCellSize(Portfolio.TABLE_CELL_SIZE);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));
        
        return this.posTable;
    }
    
    public void initData (List<PositionModel> positions, Map<String, InstrumentManager.Instrument> instruments) {
        this.posList.clear();
        this.posList.addAll(positions);

        if (! instruments.isEmpty()) {
            updateNameURL(instruments);
        }
    }

    public void updateNameURL (Map<String, InstrumentManager.Instrument> instruments) {
        for (PositionModel pos : this.posList) {
            final String symbol = pos.getSymbol();
            
            if (instruments.containsKey(symbol)) {
                InstrumentManager.Instrument ins = instruments.get(symbol);
                pos.setName(ins.getName());
                
                // update Stock's icon URL
                pos.setUrlImage(ins.getUrlImage());
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
