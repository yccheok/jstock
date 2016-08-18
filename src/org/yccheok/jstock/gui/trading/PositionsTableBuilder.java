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
import javafx.scene.paint.Color;
import org.yccheok.jstock.trading.OpenPosModel;
import org.yccheok.jstock.trading.Utils;

/**
 *
 * @author shuwnyuan
 */
public class PositionsTableBuilder {
    public final TableView posTable = new TableView();
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
    
    private FormatNumberCell getNumberCell (boolean style) {
        final FormatNumberCell cell = new FormatNumberCell(style);
        
        cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Pos Table UNITS col clicked...");

                }
            }
        });
        return cell;
    }

    private TableCell<OpenPosModel, String> getStringCell () {
        final TableCell<OpenPosModel, String> cell = new TableCell<>();
        cell.textProperty().bind(cell.itemProperty());
        
        cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Pos Table UNITS col clicked...");

                }
            }
        });
        return cell;
    }
    
    public TableView build () {
        // Open Positions table
        TableColumn<OpenPosModel, String> symbolCol = new TableColumn<>("Stock");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));
        symbolCol.setCellFactory((TableColumn<OpenPosModel, String> col) -> getStringCell());
        symbolCol.getStyleClass().add("left");

        TableColumn<OpenPosModel, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.setCellFactory((TableColumn<OpenPosModel, String> col) -> getStringCell());
        nameCol.getStyleClass().add("left");

        TableColumn<OpenPosModel, Number> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        unitsCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> getNumberCell(false));
        unitsCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> avgPriceCol = new TableColumn<>("Average Purchase Price");
        avgPriceCol.setCellValueFactory(cellData -> cellData.getValue().averagePriceProperty());
        avgPriceCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> getNumberCell(false));
        avgPriceCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> mktPriceCol = new TableColumn<>("Current Price");
        mktPriceCol.setCellValueFactory(cellData -> cellData.getValue().marketPriceProperty());
        mktPriceCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> getNumberCell(false));
        mktPriceCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> costCol = new TableColumn<>("Purchase Value");
        costCol.setCellValueFactory(cellData -> cellData.getValue().costBasisProperty());
        costCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> getNumberCell(false));
        costCol.getStyleClass().add("right");

        TableColumn<OpenPosModel, Number> mktValueCol = new TableColumn<>("Current Value");
        mktValueCol.setCellValueFactory(cellData -> cellData.getValue().marketValueProperty());
        mktValueCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> getNumberCell(false));
        mktValueCol.getStyleClass().add("right");
        
        TableColumn<OpenPosModel, Number> plCol = new TableColumn<>("Gain/Loss Value");
        plCol.setCellValueFactory(cellData -> cellData.getValue().unrealizedPLProperty());
        plCol.setCellFactory((TableColumn<OpenPosModel, Number> col) -> getNumberCell(true));
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
        this.posTable.setFixedCellSize(Portfolio.tableCellSize);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));

        // set all columns having equal width
        this.posTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        return this.posTable;
    }
    
    public void initData (Map<String, Object> accBlotter) {
        LinkedTreeMap<String, Object> equity = (LinkedTreeMap) accBlotter.get("equity");
        List<LinkedTreeMap<String, Object>> positions = (List) equity.get("equityPositions");
        
        for (LinkedTreeMap<String, Object> pos : positions) {
            Map<String, Object> data = new HashMap<>();
            data.put("name",            "");
            data.put("symbol",          pos.get("symbol"));
            data.put("instrumentID",    pos.get("instrumentID"));
            data.put("units",           pos.get("availableForTradingQty"));
            data.put("averagePrice",    pos.get("avgPrice"));
            data.put("costBasis",       pos.get("costBasis"));
            data.put("marketPrice",     pos.get("mktPrice"));
            data.put("marketValue",     pos.get("marketValue"));
            data.put("unrealizedPL",    pos.get("unrealizedPL"));
            
            this.posList.add(new OpenPosModel(data));
        }

        this.posTable.setItems(this.posList);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));
    }

    public void updateStocksName (Map<String, Map> instruments) {
        for (OpenPosModel pos : this.posList) {
            final String symbol = pos.getSymbol();
            
            if (instruments.containsKey(symbol)) {
                Map<String, Object> ins = instruments.get(symbol);
                pos.updateStockName(ins.get("name").toString());
            }
        }
    }
    
    public void updatePrices (Map<String, Double> marketPrices) {
        for (OpenPosModel pos : this.posList) {
            final String symbol = pos.getSymbol();
            
            if (marketPrices.containsKey(symbol)) {
                Double price = marketPrices.get(symbol);
                pos.updateMarketPrice(price);
            }
        }
    }

    
}
