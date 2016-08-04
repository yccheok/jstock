/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
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
import org.yccheok.jstock.trading.OpenPosData;
import org.yccheok.jstock.trading.AccData;

/**
 *
 * @author shuwnyuan
 */
public class Portfolio {
    public Portfolio (Map<String, Object> accBlotter, Map<String, Map> instruments) {
        this.accBlotter = accBlotter;
        this.instruments = instruments;
    }

    public void getOpenPositions () {
        LinkedTreeMap<String, Object> equity = (LinkedTreeMap) this.accBlotter.get("equity");
        List<LinkedTreeMap<String, Object>> result = (List) equity.get("equityPositions");

        for (LinkedTreeMap<String, Object> a : result) {
            String stockName = this.instruments.get(a.get("symbol").toString()).get("name").toString();
            OpenPos pos = new OpenPos(a, stockName);
            this.positions.add(pos);
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
            new AccData("Cash Available For Trading",      acc.cashForTrade),
            new AccData("Cash Available For Withdrawal",   acc.cashForWithdraw),
            new AccData("Total Cash Balance",              acc.cashBalance),
            new AccData("Total Positions Market Value",    acc.equityValue),
            new AccData("Total Account Value",             acc.accountTotal)
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

    public void initOpenPosTable () {
        getOpenPositions();
        
        // Open Positions table
        TableColumn symbolCol = new TableColumn("Stock");
        symbolCol.setCellValueFactory(new PropertyValueFactory("symbol"));

        TableColumn nameCol = new TableColumn("Name");
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

        symbolCol.setSortable(false);
        nameCol.setSortable(false);
        unitsCol.setSortable(false);
        avgPriceCol.setSortable(false);
        mktPriceCol.setSortable(false);
        costCol.setSortable(false);
        mktValueCol.setSortable(false);
        plCol.setSortable(false);

        this.posTable.setEditable(false);

        final ObservableList<OpenPosData> posTableData = FXCollections.observableArrayList();
        for (OpenPos pos : this.positions) {
            posTableData.add(new OpenPosData(pos));
        }

        this.posTable.setItems(posTableData);
        this.posTable.getColumns().setAll(symbolCol, nameCol, unitsCol, avgPriceCol, costCol, mktPriceCol, mktValueCol, plCol);

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
    