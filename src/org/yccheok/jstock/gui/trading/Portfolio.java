/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.yccheok.jstock.trading.OpenPos;
import org.yccheok.jstock.trading.AccountSummary;

/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    public Portfolio (Map<String, Object> accBlotter, Map<String, Map> instruments) {
        this.accBlotter = accBlotter;
        this.instruments = instruments;
    }

    private String formatNumber(Double number) {
        return new DecimalFormat("0.00").format(number);
    }

    public void getOpenPositions () {
        LinkedTreeMap<String, Object> equity = (LinkedTreeMap) this.accBlotter.get("equity");
        List<LinkedTreeMap<String, Object>> result = (List) equity.get("equityPositions");

        int cnt = 0;
        for (LinkedTreeMap<String, Object> a : result) {
            String name = this.instruments.get(a.get("symbol").toString()).get("name").toString();
            OpenPos pos = new OpenPos(a, name);
            this.positions.add(pos);

            System.out.println("[" + cnt + "] Position: symbol: " + pos.symbol
                    + ", instrumentID: " + pos.instrumentID
                    + ", openQty: "      + pos.units
                    + ", costBasis: "    + pos.costBasis
            );
            cnt++;
        }
    }
    
    public Tab createTab() {
        // Account Summary
        initAccTable();
        
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().add(this.accTable);
        vBox.setPrefWidth(500);

        // Open Positions
        initOpenPosTable();

        final Label posLabel = new Label("Current Investments");
        vBox.getChildren().addAll(posLabel, this.posTable);

        // add account summary tab
        accTab.setText("Portfolio (Practice Account)");
        accTab.setClosable(false);
        accTab.setContent(vBox);

        return accTab;
    }

    public class AccData {
        private final SimpleStringProperty field;
        private final SimpleStringProperty value;

        private AccData(String f, String v) {
            this.field = new SimpleStringProperty(f);
            this.value = new SimpleStringProperty(v);
        }

        public String getField() {
            return field.get();
        }
        public void setField(String v) {
            field.set(v);
        }

        public String getValue() {
            return value.get();
        }
        public void setValue(String v) {
            value.set(v);
        }
    }
    
    public void initAccTable () {
        AccountSummary acc = new AccountSummary(accBlotter);

        final ObservableList<AccData> accTableData = FXCollections.observableArrayList(
            new AccData("Cash Available For Trading",      formatNumber(acc.cashForTrade) ),
            new AccData("Cash Available For Withdrawal",   formatNumber(acc.cashForWithdraw) ),
            new AccData("Total Cash Balance",              formatNumber(acc.cashBalance) ),
            new AccData("Total Positions Market Value",    formatNumber(acc.positionsValue) ),
            new AccData("Total Account Value",             formatNumber(acc.accountTotal) )
        );

        // Account Summary Table
        TableColumn fieldCol = new TableColumn("Account Summary");
        fieldCol.setCellValueFactory(new PropertyValueFactory("field"));

        TableColumn valueCol = new TableColumn();
        valueCol.setCellValueFactory(new PropertyValueFactory("value"));
        valueCol.getStyleClass().add("right-align");

        fieldCol.setSortable(false);
        valueCol.setSortable(false);

        this.accTable.setEditable(false);
        this.accTable.setItems(accTableData);
        this.accTable.getColumns().setAll(fieldCol, valueCol);

        // limit Table height, based on row number
        this.accTable.setFixedCellSize(30);
        this.accTable.prefHeightProperty().bind(Bindings.size(accTable.getItems()).multiply(accTable.getFixedCellSize()).add(30));

        // manually fix table width
        this.accTable.setMaxWidth(400);
        this.accTable.setPrefWidth(400);
        this.accTable.setMinWidth(400);
        // set all columns having equal width
        this.accTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public class PosData {
        private final SimpleStringProperty name;
        private final SimpleStringProperty units;
        private final SimpleStringProperty averagePrice;
        private final SimpleStringProperty costBasis;
        private final SimpleStringProperty marketPrice;
        private final SimpleStringProperty marketValue;
        private final SimpleStringProperty unrealizedPL;

        private PosData(OpenPos pos) {
            this.name           = new SimpleStringProperty(pos.name);
            this.units          = new SimpleStringProperty(formatNumber(pos.units));
            this.averagePrice   = new SimpleStringProperty(formatNumber(pos.averagePrice));
            this.costBasis      = new SimpleStringProperty(formatNumber(pos.costBasis));
            this.marketPrice    = new SimpleStringProperty(formatNumber(pos.marketPrice));
            this.marketValue    = new SimpleStringProperty(formatNumber(pos.marketValue));
            this.unrealizedPL   = new SimpleStringProperty(formatNumber(pos.unrealizedPL));
        }

        public String getName() {
            return name.get();
        }
        public void setName(String v) {
            name.set(v);
        }

        public String getUnits() {
            return units.get();
        }
        public void setUnits(String v) {
            units.set(v);
        }

        public String getAveragePrice() {
            return averagePrice.get();
        }
        public void setAveragePrice(String v) {
            averagePrice.set(v);
        }

        public String getCostBasis() {
            return costBasis.get();
        }
        public void setCostBasis(String v) {
            costBasis.set(v);
        }

        public String getMarketPrice() {
            return marketPrice.get();
        }
        public void setMarketPrice(String v) {
            marketPrice.set(v);
        }

        public String getMarketValue() {
            return marketValue.get();
        }
        public void setMarketValue(String v) {
            marketValue.set(v);
        }

        public String getUnrealizedPL() {
            return unrealizedPL.get();
        }
        public void setUnrealizedPL(String v) {
            unrealizedPL.set(v);
        }
    }

    public void initOpenPosTable () {
        getOpenPositions();
        
        // Open Positions table
        TableColumn nameCol = new TableColumn("Stock");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));

        TableColumn unitsCol = new TableColumn("Units");
        unitsCol.setCellValueFactory(new PropertyValueFactory("units"));
        unitsCol.getStyleClass().add("right-align");

        TableColumn avgPriceCol = new TableColumn("Average Purchase Price");
        avgPriceCol.setCellValueFactory(new PropertyValueFactory("averagePrice"));
        avgPriceCol.getStyleClass().add("right-align");

        TableColumn mktPriceCol = new TableColumn("Current Price");
        mktPriceCol.setCellValueFactory(new PropertyValueFactory("marketPrice"));
        mktPriceCol.getStyleClass().add("right-align");

        TableColumn costCol = new TableColumn("Purchase Value");
        costCol.setCellValueFactory(new PropertyValueFactory("costBasis"));
        costCol.getStyleClass().add("right-align");

        TableColumn mktValueCol = new TableColumn("Current Value");
        mktValueCol.setCellValueFactory(new PropertyValueFactory("marketValue"));
        mktValueCol.getStyleClass().add("right-align");
        
        TableColumn plCol = new TableColumn("Gain/Loss Value");
        plCol.setCellValueFactory(new PropertyValueFactory("unrealizedPL"));
        plCol.getStyleClass().add("right-align");

        nameCol.setSortable(false);
        unitsCol.setSortable(false);
        avgPriceCol.setSortable(false);
        mktPriceCol.setSortable(false);
        costCol.setSortable(false);
        mktValueCol.setSortable(false);
        plCol.setSortable(false);

        this.posTable.setEditable(false);

        final ObservableList<PosData> posTableData = FXCollections.observableArrayList();
        for (OpenPos pos : this.positions) {
            posTableData.add(new PosData(pos));
        }

        this.posTable.setItems(posTableData);
        this.posTable.getColumns().setAll(nameCol, unitsCol, avgPriceCol, costCol, mktPriceCol, mktValueCol, plCol);

        // limit Table height, based on row number
        this.posTable.setFixedCellSize(30);
        this.posTable.prefHeightProperty().bind(Bindings.size(this.posTable.getItems()).multiply(this.posTable.getFixedCellSize()).add(30));

        // manually fix table width
        //posTable.setMaxWidth(900);
        //posTable.setPrefWidth(900);
        //posTable.setMinWidth(900);

        // set all columns having equal width
        this.posTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private final Map<String, Object> accBlotter;
    private final Map<String, Map> instruments;
    
    private final List<OpenPos> positions = new ArrayList<>();

    public  final Tab accTab  = new Tab();
    private final TableView accTable = new TableView();
    private final TableView posTable = new TableView();
}
    