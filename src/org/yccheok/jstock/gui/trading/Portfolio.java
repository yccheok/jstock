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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.yccheok.jstock.trading.OpenPos;
import org.yccheok.jstock.trading.AccountSummary;
import org.yccheok.jstock.trading.OpenPosModel;
import org.yccheok.jstock.trading.Utils;

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
        getOpenPositions();

        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 20, 0, 20));  // Insets: top, right, bottom, left
        vBox.setPrefWidth(500);

        // Account Summary
        initAccSummary();
        vBox.getChildren().add(this.accBorderPane);
        
        // Open Positions
        initOpenPosTable();

        final Label posLabel = new Label("Current Investments");
        vBox.getChildren().addAll(posLabel, this.posTable);

        // add account summary tab
        this.accTab.setText("Portfolio (Practice Account)");
        this.accTab.setClosable(false);
        this.accTab.setContent(vBox);

        return this.accTab;
    }

    public void initAccSummary () {
        AccountSummary acc = new AccountSummary(this.accBlotter, this.positions);

        // Left content
        HBox leftHbox = new HBox(8);
        
        // Total Open positions value
        Label shareText = new Label("Share:");
        Label shareAmount = new Label(Utils.monetaryFormat(acc.equityValue));
        shareAmount.getStyleClass().add("profit");
        
        // Total unrealized PL
        Label profitText = new Label("Paper Profit:");
        profitText.setPadding(new Insets(0, 0, 0, 10));

        String amountStr = Utils.monetaryFormat(acc.totalUnrealizedPL) + " (" + Utils.formatNumber(acc.totalUnrealizedPLPercent) + "%)";
        Label profitAmount = new Label(amountStr);
        profitAmount.getStyleClass().add((acc.totalUnrealizedPL > 0) ? "profit" : "loss");

        leftHbox.getChildren().addAll(shareText, shareAmount, profitText, profitAmount);
        
        // Right content
        HBox rightHbox = new HBox(8);
        
        // Cash for trading
        Label cashText = new Label("Cash to Invest:");
        Label cashAmount = new Label(Utils.monetaryFormat(acc.cashForTrade));
        cashAmount.getStyleClass().add((acc.cashForTrade > 0) ? "profit" : "loss");

        // Total
        Label totalText = new Label("Total:");
        totalText.setPadding(new Insets(0, 0, 0, 10));
        Label totalAmount = new Label(Utils.monetaryFormat(acc.accountTotal));
        totalAmount.getStyleClass().add((acc.accountTotal > 0) ? "profit" : "loss");

        rightHbox.getChildren().addAll(cashText, cashAmount, totalText, totalAmount);
        
        this.accBorderPane.setPadding(new Insets(10, 0, 30, 0));    // Insets: top, right, bottom, left
        this.accBorderPane.setLeft(leftHbox);
        this.accBorderPane.setRight(rightHbox);
        this.accBorderPane.setId("accBorderPane");
    }
    
    public void initOpenPosTable () {
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

        final ObservableList<OpenPosModel> posTableData = FXCollections.observableArrayList();
        for (OpenPos pos : this.positions) {
            posTableData.add(new OpenPosModel(pos));
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
    private final BorderPane accBorderPane = new BorderPane();
    private final TableView posTable = new TableView();
}
    