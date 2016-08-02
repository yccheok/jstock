/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    public Portfolio (Map<String, Object> accBlotter, List<Map<String, Object>> openTxns) {
        this.accBlotter = accBlotter;
        this.openTxns = openTxns;
    }

    private String formatNumber(Double number) {
        return new DecimalFormat("#.00").format(number);
    }

    private void getOpenPositions() {
    }
    
    public Tab createTab() {
        LinkedTreeMap<String, Object> equity    = (LinkedTreeMap) accBlotter.get("equity");
        LinkedTreeMap<String, Object> balance   = (LinkedTreeMap) accBlotter.get("cash");

        Double positionsValue   = (Double) equity.get("equityValue");
        Double cashBalance      = (Double) balance.get("cashBalance");
        Double cashForTrade     = (Double) balance.get("cashAvailableForTrade");
        Double cashForWithdraw  = (Double) balance.get("cashAvailableForWithdrawal");
        Double accountTotal     = (Double) cashBalance + (Double) positionsValue;

        final ObservableList<AccData> accTableData = FXCollections.observableArrayList(
            new AccData("Cash Available For Trading",      formatNumber(cashForTrade) ),
            new AccData("Cash Available For Withdrawal",   formatNumber(cashForWithdraw) ),
            new AccData("Total Cash Balance",              formatNumber(cashBalance) ),
            new AccData("Total Positions Market Value",    formatNumber(positionsValue) ),
            new AccData("Total Account Value",             formatNumber(accountTotal) )
        );

        // get open positions
        List<LinkedTreeMap<String, Object>> result = (List) equity.get("equityPositions");
        int cnt = 0;
        for (LinkedTreeMap<String, Object> a : result) {
            String symbol       = a.get("symbol").toString();
            Double costBasis    = (Double) a.get("costBasis");
            // average buy price
            Double averagePrice = (Double) a.get("avgPrice");
            Double tradingQty   = (Double) a.get("availableForTradingQty");
            // spot price
            Double marketPrice  = (Double) a.get("mktPrice");
            // spot price * qty
            Double marketValue  = (Double) a.get("marketValue");
            Double PL           = (Double) a.get("unrealizedPL");
            Double dayPL        = (Double) a.get("unrealizedDayPL");
            Double dayPLPercent = (Double) a.get("unrealizedDayPLPercent");

            Map<String, Object> p = new HashMap<>();
            p.put("symbol",                 symbol);
            p.put("availableForTradingQty", tradingQty);
            p.put("avgPrice",               averagePrice);
            p.put("costBasis",              costBasis);
            p.put("mktPrice",               marketPrice);
            p.put("marketValue",            marketValue);
            p.put("unrealizedPL",           PL);
            p.put("unrealizedDayPL",        dayPL);
            p.put("unrealizedDayPLPercent", dayPLPercent);

            this.positions.add(p);

            System.out.println("[" + cnt + "] Position: symbol: " + a.get("symbol")
                    + ", instrumentID: " + a.get("instrumentID")
                    + ", openQty: " + a.get("openQty")
                    + ", costBasis: " + a.get("costBasis")
                    + ", trading Qty: " + tradingQty
            );
            cnt++;
        }

        // Account Summary Table
        TableColumn fieldCol = new TableColumn("Account Summary");
        fieldCol.setCellValueFactory(new PropertyValueFactory("field"));

        TableColumn valueCol = new TableColumn();
        valueCol.setCellValueFactory(new PropertyValueFactory("value"));
        valueCol.getStyleClass().add( "right-align");

        fieldCol.setSortable(false);
        valueCol.setSortable(false);

        accTable.setEditable(false);
        accTable.setItems(accTableData);
        accTable.getColumns().setAll(fieldCol, valueCol);

        // limit Table height, based on row number
        accTable.setFixedCellSize(30);
        accTable.prefHeightProperty().bind(Bindings.size(accTable.getItems()).multiply(accTable.getFixedCellSize()).add(30));

        // manually fix table width
        accTable.setMaxWidth(400);
        accTable.setPrefWidth(400);
        accTable.setMinWidth(400);
        // set all columns having equal width
        accTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().add(accTable);
        vBox.setPrefWidth(500);


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

        TableColumn dayPlCol = new TableColumn("Day Gain/Loss Value");
        dayPlCol.setCellValueFactory(new PropertyValueFactory("dayPl"));

        TableColumn dayPlPercentCol = new TableColumn("Day Gain/Loss %");
        dayPlPercentCol.setCellValueFactory(new PropertyValueFactory("dayPlPercent"));

        symbolCol.setSortable(false);
        qtyCol.setSortable(false);
        avgPriceCol.setSortable(false);
        mktPriceCol.setSortable(false);
        costCol.setSortable(false);
        mktValueCol.setSortable(false);
        plCol.setSortable(false);
        dayPlCol.setSortable(false);
        dayPlPercentCol.setSortable(false);

        posTable.setEditable(false);

        final ObservableList<PosData> posTableData = FXCollections.observableArrayList();
        for (Map<String, Object> pos : this.positions) {
            posTableData.add(new PosData(pos));
        }

        posTable.setItems(posTableData);
        posTable.getColumns().setAll(symbolCol, qtyCol, avgPriceCol, costCol, mktPriceCol, mktValueCol, plCol, dayPlCol, dayPlPercentCol);

        // limit Table height, based on row number
        posTable.setFixedCellSize(30);
        posTable.prefHeightProperty().bind(Bindings.size(posTable.getItems()).multiply(posTable.getFixedCellSize()).add(30));

        // manually fix table width
        //posTable.setMaxWidth(900);
        //posTable.setPrefWidth(900);
        //posTable.setMinWidth(900);

        // set all columns having equal width
        posTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        final Label posLabel = new Label("Current Investments");
        vBox.getChildren().addAll(posLabel, posTable);

        // add account summary tab
        accTab.setText("Portfolio (Practice Account)");
        accTab.setClosable(false);
        accTab.setContent(vBox);

        return accTab;

        /*
        tabPane.getTabs().add(accTab);

        // select tab
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(accTab);
        */
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
        private final SimpleStringProperty dayPl;
        private final SimpleStringProperty dayPlPercent;

        private PosData(Map<String, Object> pos) {
            this.symbol         = new SimpleStringProperty(pos.get("symbol").toString());
            this.qty            = new SimpleStringProperty(pos.get("availableForTradingQty").toString());
            this.avgPrice       = new SimpleStringProperty(pos.get("avgPrice").toString());
            this.costBasis      = new SimpleStringProperty(pos.get("costBasis").toString());
            this.mktPrice       = new SimpleStringProperty(pos.get("mktPrice").toString());
            this.marketValue    = new SimpleStringProperty(pos.get("marketValue").toString());
            this.pl             = new SimpleStringProperty(pos.get("unrealizedPL").toString());
            this.dayPl          = new SimpleStringProperty(pos.get("unrealizedDayPL").toString());
            this.dayPlPercent   = new SimpleStringProperty(pos.get("unrealizedDayPLPercent").toString());
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

        public String getDayPl() {
            return dayPl.get();
        }
        public void setDayPl(String v) {
            dayPl.set(v);
        }

        public String getDayPlPercent() {
            return dayPlPercent.get();
        }
        public void setDayPlPercent(String v) {
            dayPlPercent.set(v);
        }
    }

    private final Map<String, Object> accBlotter;
    private final List<Map<String, Object>> openTxns;
    private final List<Map<String, Object>> positions = new ArrayList<>();

    public  final Tab accTab  = new Tab();
    private final TableView accTable = new TableView();
    private final TableView posTable = new TableView();
}
    