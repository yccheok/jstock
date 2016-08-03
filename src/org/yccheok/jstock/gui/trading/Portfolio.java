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
        return new DecimalFormat("#.00").format(number);
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
        valueCol.getStyleClass().add( "right-align");

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

    public void initOpenPosTable () {
        getOpenPositions();
        
        // Open Positions table
        TableColumn symbolCol = new TableColumn("Stock");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));

        TableColumn qtyCol = new TableColumn("Units");
        qtyCol.setCellValueFactory(new PropertyValueFactory("qty"));

        TableColumn avgPriceCol = new TableColumn("Average Purchase Price");
        avgPriceCol.setCellValueFactory(new PropertyValueFactory("avgPrice"));

        TableColumn mktPriceCol = new TableColumn("Current Price");
        mktPriceCol.setCellValueFactory(new PropertyValueFactory("mktPrice"));

        TableColumn costCol = new TableColumn("Purchase Value");
        costCol.setCellValueFactory(new PropertyValueFactory("costBasis"));

        TableColumn mktValueCol = new TableColumn("Current Value");
        mktValueCol.setCellValueFactory(new PropertyValueFactory("marketValue"));

        TableColumn plCol = new TableColumn("Gain/Loss Value");
        plCol.setCellValueFactory(new PropertyValueFactory("pl"));

        symbolCol.setSortable(false);
        qtyCol.setSortable(false);
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
        this.posTable.getColumns().setAll(symbolCol, qtyCol, avgPriceCol, costCol, mktPriceCol, mktValueCol, plCol);

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

    public class PosData {
        private final SimpleStringProperty symbol;
        private final SimpleStringProperty qty;
        private final SimpleStringProperty avgPrice;
        private final SimpleStringProperty costBasis;
        private final SimpleStringProperty mktPrice;
        private final SimpleStringProperty marketValue;
        private final SimpleStringProperty pl;

        private PosData(OpenPos pos) {
            this.symbol         = new SimpleStringProperty(pos.name);
            this.qty            = new SimpleStringProperty(pos.units.toString());
            this.avgPrice       = new SimpleStringProperty(pos.averagePrice.toString());
            this.costBasis      = new SimpleStringProperty(pos.costBasis.toString());
            this.mktPrice       = new SimpleStringProperty(pos.marketPrice.toString());
            this.marketValue    = new SimpleStringProperty(pos.marketValue.toString());
            this.pl             = new SimpleStringProperty(pos.unrealizedPL.toString());
        }

        public String getSymbol() {
            return symbol.get();
        }
        public void setSymbol(String v) {
            symbol.set(v);
        }

        public String getQty() {
            return qty.get();
        }
        public void setQty(String v) {
            qty.set(v);
        }

        public String getAvgPrice() {
            return avgPrice.get();
        }
        public void setAvgPrice(String v) {
            avgPrice.set(v);
        }

        public String getCostBasis() {
            return costBasis.get();
        }
        public void setCostBasis(String v) {
            costBasis.set(v);
        }

        public String getMktPrice() {
            return mktPrice.get();
        }
        public void setMktPrice(String v) {
            mktPrice.set(v);
        }

        public String getMarketValue() {
            return marketValue.get();
        }
        public void setMarketValue(String v) {
            marketValue.set(v);
        }

        public String getPl() {
            return pl.get();
        }
        public void setPl(String v) {
            pl.set(v);
        }
    }

    private final Map<String, Object> accBlotter;
    private final Map<String, Map> instruments;
    
    private final List<OpenPos> positions = new ArrayList<>();

    public  final Tab accTab  = new Tab();
    private final TableView accTable = new TableView();
    private final TableView posTable = new TableView();
}
    