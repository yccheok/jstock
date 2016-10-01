/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.trading.API.DriveWealth;
import org.yccheok.jstock.trading.API.MarketDataManager;
import org.yccheok.jstock.trading.API.OrderManager;
import org.yccheok.jstock.trading.API.OrderManager.OrdStatus;
import org.yccheok.jstock.trading.API.OrderManager.OrderSide;
import org.yccheok.jstock.trading.API.SessionManager;
import org.yccheok.jstock.trading.PositionModel;
import org.yccheok.jstock.trading.Transaction;
import org.yccheok.jstock.trading.Utils;
import static org.yccheok.jstock.trading.API.OrderManager.OrderType;
import static org.yccheok.jstock.trading.API.SessionManager.Commission;

/**
 *
 * @author shuwnyuan
 */
public class CreateOrderDlg {
    
    private final PositionModel pos;
    private final OrderSide side;
    private final String symbol;
    private final String name;
    private final String instrumentID;

    // UI components
    private final Dialog<ButtonType> newOrdDlg      = new Dialog<>();
    private final TextField symbolText              = new TextField();
    private final Label bidAskLabel                 = new Label();
    private final ChoiceBox<OrderType> orderChoice  = new ChoiceBox<>();

    private final Label shareLabel                  = new Label();
    private final ChoiceBox<ShareCash> shareChoice  = new ChoiceBox<>();

    // for SELL only: available Qty & Sell All btn
    private final Label availQtyLabel               = new Label();
    private final Button sellAllBtn                 = new Button();

    private final TextField qtyText                 = new TextField();
    private final Label cashLabel                   = new Label();
    private final TextField cashText                = new TextField();
    
    private final Label priceLabel                  = new Label();
    private final TextField priceText               = new TextField();
    private final Label priceNote                   = new Label();
    private final ButtonType reviewButtonType       = new ButtonType("Review Order", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType cancelButtonType       = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    
    // input fields validator
    private BooleanBinding qtyValid;
    private BooleanBinding priceValid;
    private BooleanBinding reviewEnabled;

    // Service to update bid ask
    private ScheduledService<MarketDataManager.MarketData> marketDataSrv;
    private Double bidAskPrice = null;
    private final String availQty;

    public CreateOrderDlg (PositionModel pos, OrderSide side) {
        this.pos            = pos;
        this.side           = side;
        this.symbol         = pos.getSymbol();
        this.name           = pos.getName();
        this.instrumentID   = pos.getInstrumentID();
        this.availQty       = Utils.formatNumberNoComma(pos.getOpenQty(), 4);
    }

    
    public static enum ShareCash {
        SHARE(),
        CASH();
    }
    
    public static class OrdSummary {
        private final SimpleStringProperty symbol;
        private final SimpleStringProperty action;
        private final SimpleStringProperty ordName;
        private final SimpleStringProperty qty;
        private final SimpleStringProperty price;
        private final SimpleStringProperty subtotal;
        private final SimpleStringProperty commission;
        private final SimpleStringProperty total;
        
        private final boolean estimated;
        private final OrderType ordType;
        private final OrderSide ordSide;
        private final String name;
        private final String instrumentID;
        private final ShareCash shareCash;
        private final String logoURL;


        public OrdSummary (boolean estimated, ShareCash shareCash,
                String symbol, String name, String logoURL, String instrumentID, 
                OrderSide side, OrderType ordType, Double qty,
                Double price, Double subtotal, Double commission, Double total) {

            this.estimated    = estimated;
            this.shareCash    = shareCash;

            this.ordType      = ordType;
            this.ordSide      = side;
            this.name         = name;
            this.logoURL      = logoURL;
            this.instrumentID = instrumentID;

            this.symbol     = new SimpleStringProperty(symbol);
            this.action     = new SimpleStringProperty(side.getName());
            this.ordName    = new SimpleStringProperty(ordType.getName());

            // convert Double to String, avoid display 8.55 as 8.5499999
            this.qty        = new SimpleStringProperty(Utils.formatNumber(qty, 4));
            this.price      = new SimpleStringProperty(Utils.monetaryFormat(price));
            this.subtotal   = new SimpleStringProperty(Utils.monetaryFormat(subtotal));
            this.commission = new SimpleStringProperty(Utils.monetaryFormat(commission));
            this.total      = new SimpleStringProperty(Utils.monetaryFormat(total));
        }

        public boolean isEstimated () {
            return estimated;
        }

        public ShareCash getShareCash () {
            return shareCash;
        }
        
        public String getLogoURL () {
            return logoURL;
        }
        
        public OrderType getOrdType () {
            return ordType;
        }
        
        public OrderSide getOrdSide () {
            return ordSide;
        }
        
        public String getName () {
            return name;
        }
        
        public String getInstrumentID () {
            return instrumentID;
        }

        // normal property member variables
        public String getSymbol () {
            return symbol.get();
        }
        public void setSymbol (String v) {
            symbol.set(v);
        }
        public SimpleStringProperty symbolProperty() {
            return symbol;
        }
        
        public String getAction () {
            return action.get();
        }
        public void setAction (String v) {
            action.set(v);
        }
        public SimpleStringProperty actionProperty() {
            return action;
        }
        
        public String getOrdName () {
            return ordName.get();
        }
        public void setOrdName (String v) {
            ordName.set(v);
        }
        public SimpleStringProperty ordNameProperty() {
            return ordName;
        }
        
        public String getQty () {
            return qty.get();
        }
        public void setQty (String v) {
            qty.set(v);
        }
        public SimpleStringProperty qtyProperty() {
            return qty;
        }
        
        public String getPrice () {
            return price.get();
        }
        public void setPrice (String v) {
            price.set(v);
        }
        public SimpleStringProperty priceProperty() {
            return price;
        }
        
        public String getSubtotal () {
            return subtotal.get();
        }
        public void setSubtotal (String v) {
            subtotal.set(v);
        }
        public SimpleStringProperty subtotalProperty() {
            return subtotal;
        }
        
        public String getCommission () {
            return commission.get();
        }
        public void setCommission (String v) {
            commission.set(v);
        }
        public SimpleStringProperty commissionProperty() {
            return commission;
        }
        
        public String getTotal () {
            return total.get();
        }
        public void setTotal (String v) {
            total.set(v);
        }
        public SimpleStringProperty totalProperty() {
            return total;
        }

        public static OrdSummary buildFromDialog (String symbol, String name,
                String logoURL, String instrumentID,
                ShareCash shareCash, Double share, Double cash,
                Double price, OrderType ordType, OrderSide side) {

            final Double qty;
            final Double subtotal;

            // default is Share Qty
            if (shareCash == null || shareCash == ShareCash.SHARE) {
                qty = share;
                subtotal = share * price;
            } else {
                subtotal = cash;
                qty = cash / price;
            }

            // calc commission
            Commission rate = DriveWealth.getUser().getCommission();
            Double commission = Commission.calcCommission(qty, rate);

            Double total;
            if (side == OrderSide.BUY) {
                total = subtotal + commission;
            } else {
                // SELL should deduct commission
                total = subtotal - commission;
            }

            return new OrdSummary(true, shareCash,
                    symbol, name, logoURL, instrumentID, side,
                    ordType, qty, price, subtotal, commission, total);
        }
        
        public static OrdSummary buildFromOrder (OrdSummary summary, OrderManager.Order order) {
            
            Double qty        = order.getOrderQty();
            Double subtotal   = order.getGrossTradeAmt();
            Double commission = order.getCommission();
            Double price      = subtotal / qty;
            
            Double total;
            if (summary.getOrdSide() == OrderSide.BUY) {
                total = subtotal + commission;
            } else {
                total = subtotal - commission;
            }

            return new OrdSummary(false, null,
                    summary.getSymbol(), summary.getName(), summary.getLogoURL(),
                    summary.getInstrumentID(), summary.getOrdSide(), summary.getOrdType(),
                    qty, price, subtotal, commission, total);
        }
    }

    private static TableView OrdSummaryTable (OrdSummary ordSummary) {
        String est = "";
        String qtyTitle = "Quantity";
        String subtotalTitle = "Subtotal";

        if (ordSummary.isEstimated() == true) {
            est = "Est. ";

            if (ordSummary.getShareCash() == null || ordSummary.getShareCash() == ShareCash.SHARE) {
                subtotalTitle = est + subtotalTitle;
            } else {
                qtyTitle = est + qtyTitle;
            }
        }
            
        // table columns
        TableColumn<OrdSummary, String> productCol  = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory("symbol"));

        TableColumn<OrdSummary, String> actionCol   = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory("action"));

        TableColumn<OrdSummary, String> ordTypeCol   = new TableColumn<>("Order Type");
        ordTypeCol.setCellValueFactory(new PropertyValueFactory("ordName"));

        TableColumn<OrdSummary, String> qtyCol      = new TableColumn<>(qtyTitle);
        qtyCol.setCellValueFactory(new PropertyValueFactory("qty"));

        TableColumn<OrdSummary, String> priceCol    = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));

        TableColumn<OrdSummary, String> subtotalCol = new TableColumn<>(subtotalTitle);
        subtotalCol.setCellValueFactory(new PropertyValueFactory("subtotal"));

        TableColumn<OrdSummary, String> commCol     = new TableColumn<>(est + "Commission");
        commCol.setCellValueFactory(new PropertyValueFactory("commission"));

        TableColumn<OrdSummary, String> totalCol    = new TableColumn<>(est + "Total");
        totalCol.setCellValueFactory(new PropertyValueFactory("total"));

        // add columns to table
        TableView table = new TableView();
        table.getColumns().addAll(productCol, actionCol, ordTypeCol, qtyCol, priceCol, subtotalCol, commCol, totalCol);
        table.setEditable(false);

        // set table data
        ObservableList<OrdSummary> resultList = FXCollections.observableArrayList(ordSummary);
        table.setItems(resultList);

        // set all columns having equal width
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // set table cell height
        table.setFixedCellSize(30);

        // limit table height to show 1 row, avoid showing empty rows
        table.setPrefHeight(80);
        table.setMaxHeight(80);

        table.setPrefWidth(900);
        table.setMaxWidth(900);
        
        return table;
    }

    private static class PriceValidator {
        private final OrderSide side;
        private final Double bidAsk;
        private final Double stopBarrier;

        // For Buy, bidAsk = Ask. For Sell, bidAsk = Bid
        public PriceValidator (Double bidAsk, OrderSide side) {
            this.side = side;
            this.bidAsk = bidAsk;

            if (side == OrderSide.BUY) {
                // User must enter a price that's $0.05 above the current ask price.
                this.stopBarrier = bidAsk + 0.05;
            } else {
                // User must enter a price that's $0.05 below the current bid price.
                this.stopBarrier = bidAsk - 0.05;
            }
        }

        public Double getStopBarrier () {
            return this.stopBarrier;
        }

        public String getLimitTxt () {
            if (this.side == OrderSide.BUY) {
                // If the price entered is above the current ask price, the limit will be immediately executed (unless after market hours in live).
                return "Enter Limit Price <= " + Utils.monetaryFormat(this.bidAsk);
            } else {
                // If the price entered is below the current bid price, the limit will be immediately executed (unless after market hours in live).
                return "Enter Limit Price >= " + Utils.monetaryFormat(this.bidAsk);
            }
        }
        
        public String getStopTxt () {
            if (this.side == OrderSide.BUY) {
                return "Enter Stop Price > " + Utils.monetaryFormat(this.stopBarrier);
            } else {
                return "Enter Stop Price < " + Utils.monetaryFormat(this.stopBarrier);
            }
        }

        public boolean validateStop (String price) {
            if (price == null || price.isEmpty()) {
                return false;
            }

            boolean valid = false;
            try {
                if ((this.side == OrderSide.BUY  && Double.parseDouble(price) > this.stopBarrier) ||
                    (this.side == OrderSide.SELL && Double.parseDouble(price) < this.stopBarrier)) {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("[validateStop]  CATCH price NOT NUMBER: " + price);
            }
            
            return valid;
        }
    }
    
    
    public void initDlgAndWait () {
        // temporary cancel / suspend Portfolio Scheduled Service
        Portfolio.portfolioService._cancel();

        initDlgUI();
        qtyPriceValidator();
        orderChangeListener();
        shareCashChangeListener();

        if (side == OrderSide.SELL) sellAllBtnListener();
        
        // Market order: calc Qty <-> Cash
        qtyListener();
        cashListener();
        
        reviewBtnHandler();
        startMarketDataSrv();

        Optional<ButtonType> result = newOrdDlg.showAndWait();
        ButtonType buttonType = result.get();
        
        if (buttonType == reviewButtonType) {
            System.out.println("AFTER Review Order Button Pressed ......");
        } else {
            Portfolio.portfolioService._restart();
            System.out.println("AFTER CANCEL Button Pressed ......");
        }

        // cancel get market data scheduled service
        marketDataSrv.cancel();
    }

    public static void setDlgTitleHeader (Dialog dlg, String title, String header, String logoURL) {
        // load css file
        dlg.getDialogPane().getScene().getStylesheets().add(CancelOrderDlg.class.getResource("trading.css").toExternalForm()); 
        
        dlg.setTitle(title);
        dlg.setHeaderText(header);

        // Stock logo
        ImageView stockLogo = new ImageView();
        stockLogo.setFitWidth(50);
        stockLogo.setFitHeight(50);
        stockLogo.setImage(null);

        if (logoURL != null && ! logoURL.isEmpty()) {
            // use background loading
            Image logo = new Image(logoURL, true);
            stockLogo.setImage(logo);
        }
        // show stock logo in Header - Right
        dlg.setGraphic(stockLogo);
    }
    
    private void initDlgUI () {
        // UI components
        String header = symbol + " - " + name;
        setDlgTitleHeader(newOrdDlg, side.getName(), header, pos.getUrlImage());

        newOrdDlg.getDialogPane().getButtonTypes().addAll(reviewButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ColumnConstraints col1 = new ColumnConstraints(150);
        ColumnConstraints col2 = new ColumnConstraints(200);
        grid.getColumnConstraints().add(col1);
        grid.getColumnConstraints().add(col2);

        // Symbol
        symbolText.setPromptText("Symbol");
        symbolText.setText(symbol);
        symbolText.setEditable(false);

        // default to Market Order
        orderChoice.getItems().setAll(OrderType.values());
        orderChoice.setValue(OrderType.MARKET);

        shareLabel.setText("Share / Cash Type");
        shareChoice.getItems().setAll(ShareCash.SHARE, ShareCash.CASH);
        shareChoice.setValue(ShareCash.CASH);
        
        // Since default to MARKET + Cash, so disable Qty
        // Qty TODO: Suggest : orderSizeMax, orderSizeMin, orderSizeStep (get Instrument)
        qtyText.setPromptText("Units");
        qtyText.setDisable(true);
        
        cashLabel.setText("Cash ($)");
        cashText.setPromptText("$ Amount");

        // default to MARKET order, so hide Stop / Limit Price field & note
        priceLabel.setVisible(false);
        priceText.setVisible(false);
        priceNote.setVisible(false);

        // add all to grid
        grid.add(new Label("Stock:"), 0, 0);
        grid.add(symbolText, 1, 0);

        String bidAsk = (side == OrderSide.BUY) ? "Ask" : "Bid";
        grid.add(new Label(bidAsk + " Price ($):"), 0, 1);
        grid.add(bidAskLabel, 1, 1);

        String buySell = side.getName();
        grid.add(new Label(buySell + " Order:"), 0, 2);
        grid.add(orderChoice, 1, 2);

        grid.add(shareLabel, 0, 3);
        grid.add(shareChoice, 1, 3);
        
        if (side == OrderSide.SELL) {
            availQtyLabel.setText("Available Qty: " + availQty);
            sellAllBtn.setText("Sell All");

            grid.add(availQtyLabel, 2, 3);
            grid.add(sellAllBtn, 3, 3);
        }

        grid.add(new Label("Shares:"), 0, 4);
        grid.add(qtyText, 1, 4);

        // Market Order: by $ amount (support fractional share where Qty < 1)
        grid.add(cashLabel, 2, 4);
        grid.add(cashText, 3, 4);
        
        grid.add(priceLabel, 0, 5);
        grid.add(priceText, 1, 5);

        grid.add(priceNote, 1, 6);

        newOrdDlg.getDialogPane().setContent(grid);

        // disable Review button by default
        Node reviewButton = newOrdDlg.getDialogPane().lookupButton(reviewButtonType);
        reviewButton.setDisable(true);
        
        // focus on Cash
        Platform.runLater(() -> cashText.requestFocus());
    }

    private void sellAllBtnListener () {
        sellAllBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle (ActionEvent e) {

                // Market Order: select Shares
                if (orderChoice.getValue() == OrderType.MARKET) {
                    shareChoice.setValue(ShareCash.SHARE);
                }

                // Fill in Shares & set focus
                qtyText.setText(availQty);
                Platform.runLater(() -> qtyText.requestFocus());
            }
        });
    }

    private void qtyPriceValidator () {
        // validate Qty
        qtyValid = Bindings.createBooleanBinding(() -> {
            
            String qtyS = qtyText.getText().trim();
            if (! Utils.validateNumber(qtyS)) return false;

            if (side == OrderSide.BUY) return true;
            
            // for sell: Qty <= available Qty
            return (Double.parseDouble(qtyS) <= pos.getOpenQty());
        }, qtyText.textProperty());

        
        // validate LIMIT / STOP price
        priceValid = Bindings.createBooleanBinding(() -> {
            String bidAskS = bidAskLabel.getText().trim();

            // No bidAsk price yet, disable "Review Order" button
            if (! Utils.validateNumber(bidAskS)) {
                return false;
            }

            OrderType ordType = orderChoice.getValue();
            String price = priceText.getText().trim();

            boolean valid = false;
            
            if (null != ordType) switch (ordType) {
                case MARKET:
                    // Market Order doesn't specify price
                    valid = true;
                    break;
                case LIMIT:
                    // LIMIT price just suggestion, not enforcing price limit. So just validate number is valid
                    valid = Utils.validateNumber(price);
                    break;
                case STOP:
                    if (bidAskPrice != null) {
                        PriceValidator validator = new PriceValidator (bidAskPrice, side);
                        valid = validator.validateStop(price);
                    }
                    break;
                default:
                    break;
            }

            return valid;
        }, priceText.textProperty());

        // only enable "Review Order" button if Qty & price valid
        reviewEnabled = orderChoice.valueProperty().isNotNull().and(qtyValid).and(priceValid);

        Node reviewButton = newOrdDlg.getDialogPane().lookupButton(reviewButtonType);
        reviewButton.disableProperty().bind(reviewEnabled.not());
    }

    private void showPriceField (boolean show, String labelTxt, String noteTxt) {
        if (show == true) {
            priceLabel.setText(labelTxt);
            priceNote.setText(noteTxt);

            priceLabel.setVisible(true);
            priceText.setVisible(true);
            priceNote.setVisible(true);
        } else {
            // clear price, so not submitted
            priceText.clear();

            priceLabel.setVisible(false);
            priceText.setVisible(false);
            priceNote.setVisible(false);
        }
    }

    private void showShareCashChoice (boolean show) {
        if (show == true) {
            // for Market Order
            shareLabel.setVisible(true);
            shareChoice.setVisible(true);
            shareChoice.setValue(ShareCash.CASH);

            cashLabel.setVisible(true);
            cashText.setVisible(true);
            qtyText.clear();

            enableShareOrCash(ShareCash.CASH);
        } else {
            // Stop / Limit order, only allow Share / Unit
            shareLabel.setVisible(false);
            shareChoice.setVisible(false);

            cashText.clear();
            cashLabel.setVisible(false);
            cashText.setVisible(false);
            
            qtyText.setDisable(false);
        }
    }
    
    private void orderChangeListener () {
        orderChoice.valueProperty().addListener((ObservableValue<? extends OrderType> observable, OrderType oldVal, OrderType newVal) -> {
            System.out.println("Order Type changed: " + newVal.getName());

            PriceValidator validator = null;
            String limitHint = "";
            String stopHint = "";
            if (bidAskPrice != null) {
                validator = new PriceValidator (bidAskPrice, side);
                limitHint = validator.getLimitTxt();
                stopHint  = validator.getStopTxt();
            }

            switch (newVal) {
                case LIMIT:
                    showPriceField(true, "Limit Price ($)", limitHint);
                    showShareCashChoice(false);
                    
                    break;
                case STOP:
                    showPriceField(true, "Stop Price ($)", stopHint);
                    showShareCashChoice(false);

                    break;
                case MARKET:
                    // hide price input field
                    if (validator != null) showPriceField(false, null, null);
                    // specify Share by Qty or Amount ($), support fractional share
                    showShareCashChoice(true);

                    break;
                default:
                    break;
            }

            // invalidate all, to recalculate BUY button disable property
            reviewEnabled.invalidate();
            qtyValid.invalidate();
            priceValid.invalidate();
        });
    }

    private void enableShareOrCash (ShareCash type) {
        if (type == ShareCash.SHARE) {
            // enable Qty, disable Cash
            qtyText.setDisable(false);
            cashText.setDisable(true);

            // set focus on Shares
            Platform.runLater(() -> qtyText.requestFocus());
        } else {
            // disable Qty, enable Cash
            qtyText.setDisable(true);
            cashText.setDisable(false);
            
            // set focus on Cash
            Platform.runLater(() -> cashText.requestFocus());
        }
    }

    private void shareCashChangeListener () {
        shareChoice.valueProperty().addListener((ObservableValue<? extends ShareCash> observable, ShareCash oldVal, ShareCash newVal) -> {
            if (orderChoice.getValue() == OrderType.MARKET) {
                System.out.println("MARKET order : Change Share / Cash Type: " + newVal);
                enableShareOrCash(newVal);
            }
        });
    }
    
    private void qtyListener () {
        qtyText.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // convert Share Qty => Cash amount
            if (orderChoice.getValue() == OrderType.MARKET
                && shareChoice.getValue() == ShareCash.SHARE
                && bidAskPrice != null && bidAskPrice > 0) {

                if (! Utils.validateNumber(newValue)) {
                    cashText.clear();
                    return;
                }

                Double cash = Double.parseDouble(newValue) * bidAskPrice;
                String cashS = Utils.formatNumberNoComma(cash, 2);
                cashText.setText(cashS);
            }
        });
    }

    private void cashListener () {    
        cashText.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // convert Cash Amount => Share Qty
            if (orderChoice.getValue() == OrderType.MARKET
                && shareChoice.getValue() == ShareCash.CASH
                && bidAskPrice != null && bidAskPrice > 0) {

                if (! Utils.validateNumber(newValue)) {
                    qtyText.clear();
                    return;
                }

                Double qty = Double.parseDouble(newValue) / bidAskPrice;
                // Qty up to 4 decimal
                String qtyS = Utils.formatNumberNoComma(qty, 4);
                qtyText.setText(qtyS);
            }
        });
    }
    
    private void startMarketDataSrv () {
        // Scheduled service - get Ask price with Get Market Data / Quote API
        marketDataSrv = new ScheduledService<MarketDataManager.MarketData>() {
            @Override
            protected Task<MarketDataManager.MarketData> createTask() {
                return new Task<MarketDataManager.MarketData>() {
                    protected MarketDataManager.MarketData call() {
                        ArrayList<String> symbols = new ArrayList<>();
                        symbols.add(symbol);

                        //System.out.println("BUY / SELL DIALOG - Scheduled Task to get market data bid/ask");

                        // call Get market Data API => Bid + Ask
                        List<MarketDataManager.MarketData> dataList = MarketDataManager.get(symbols, false);
                        MarketDataManager.MarketData marketData = null;

                        if (!dataList.isEmpty()) {
                            marketData = dataList.get(0);
                        }
                        return marketData;
                    }
                };
            }
        };
        marketDataSrv.setPeriod(Duration.seconds(1));
        
        marketDataSrv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent workerStateEvent) {
                MarketDataManager.MarketData result = (MarketDataManager.MarketData) workerStateEvent.getSource().getValue();
        
                /*
                System.out.println("BUY / SELL dialog: Get Market Data Service, Updated price for symbol: " + symbol
                        + ", Bid: " + result.getBid()
                        + ", Ask: " + result.getAsk());
                */

                if (side == OrderSide.BUY) {
                    bidAskPrice = result.getAsk();
                } else {
                    bidAskPrice = result.getBid();
                }

                // Task's event handler is handled in JavaFX Application / UI Thread, so is ok to update UI
                bidAskLabel.setText(bidAskPrice.toString());

                // invalidate all, to recalculate BUY button disable property
                priceValid.invalidate();
                qtyValid.invalidate();
                reviewEnabled.invalidate();
            }
        });
        marketDataSrv.start();
    }

    private static void setDlgContent (Dialog dlg, TableView orderTable, String headerNote, String footerNote) {
        Label headerLabel = new Label();
        headerLabel.getStyleClass().add("bold-italic");
        if (headerNote != null) headerLabel.setText("* " + headerNote);

        Label footerLabel = new Label();
        footerLabel.getStyleClass().add("bold-italic");
        if (footerNote != null) footerLabel.setText("* " + footerNote);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(headerLabel, 0, 0);
        grid.add(orderTable, 0, 1);
        grid.add(footerLabel, 0, 2);

        dlg.getDialogPane().setContent(grid);
    }
    
    private void reviewBtnHandler () {
        // review button event handler
        Node reviewButton = newOrdDlg.getDialogPane().lookupButton(reviewButtonType);

        reviewButton.addEventHandler(ActionEvent.ACTION, event -> {
            OrderType ordType = orderChoice.getValue();

            String priceS  = priceText.getText().trim();
            Double price   = (ordType == OrderType.MARKET) ? bidAskPrice : Double.parseDouble(priceS);

            ShareCash shareCash = null;
            Double qty = null;
            Double cash = null;

            if (ordType == OrderType.MARKET) {
                shareCash = shareChoice.getValue();
            }

            if (ordType != OrderType.MARKET || shareCash == ShareCash.SHARE) {
                qty = Double.parseDouble(qtyText.getText().trim());
            } else {
                cash = Double.parseDouble(cashText.getText().trim());
            }
            
            // Review Order Dialog
            Alert reviewDlg = new Alert(AlertType.CONFIRMATION);

            String logoURL = pos.getUrlImage();
            String header = symbol + " - " + name;
            setDlgTitleHeader(reviewDlg, "Review Order", header, logoURL);

            // Review Order Summary Table
            OrdSummary summary = OrdSummary.buildFromDialog(
                    symbol, name, logoURL, instrumentID,
                    shareCash, qty, cash, price, ordType, side);

            TableView orderTable = OrdSummaryTable(summary);
            String footerNote = (side == OrderSide.SELL) ? "SEC and TAF fees not included in estimated commission." : null;
            setDlgContent(reviewDlg, orderTable, null, footerNote);

            ButtonType submitButtonType = new ButtonType("Submit Order", ButtonData.OK_DONE);
            reviewDlg.getButtonTypes().setAll(submitButtonType, ButtonType.CANCEL);

            submitOrderHandler(reviewDlg, submitButtonType);

            reviewDlg.show();
        });
    }

    private static void submitOrderHandler (Alert reviewDlg, ButtonType submitButtonType) {
        Node submitButton = reviewDlg.getDialogPane().lookupButton(submitButtonType);

        // Confirm order, execute Create Order
        submitButton.addEventHandler(ActionEvent.ACTION, event -> {

            GridPane grid = (GridPane) reviewDlg.getDialogPane().getContent();
            // Grid children:  1) Label headerNote   2) TableView   3) Label footerNote
            TableView ordTable = (TableView) grid.getChildren().get(1);

            OrdSummary summary = (OrdSummary) ordTable.getItems().get(0);
            
            // Submit order dialog
            Alert submitDlg = new Alert(AlertType.INFORMATION);

            String header = summary.getSymbol() + " - " + summary.getName();
            setDlgTitleHeader(submitDlg, summary.getAction(), header, summary.getLogoURL());

            submitDlg.setContentText("Submitting " + summary.getAction() + " " + summary.getOrdName() + " Order ....");

            // disable OK button
            submitDlg.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            submitDlg.show();

            // CREATE ORDER params
            SessionManager.User user   = DriveWealth.getUser();
            SessionManager.Account acc = user.getActiveAccount();

            String userID    = user.getUserID();
            String accountID = acc.getAccountID();
            String accountNo = acc.getAccountNo();

            OrderType ordType = summary.getOrdType();
            OrderSide ordSide = summary.getOrdSide();

            Map<String, Object> params = new HashMap<>();
            params.put("symbol",        summary.getSymbol());
            params.put("instrumentID",  summary.getInstrumentID());
            params.put("accountID",     accountID);
            params.put("accountNo",     accountNo);
            params.put("userID",        userID);
            params.put("accountType",   acc.getAccountType().getValue());
            params.put("side",          ordSide.getValue());
            params.put("ordType",       summary.getOrdType().getValue());

            
            // Stop / Limit price
            if (ordType == OrderType.LIMIT || ordType == OrderType.STOP) {
                String key = (ordType == OrderType.STOP) ? "price" : "limitPrice";
                params.put(key, summary.getPrice());
            }

            // LIMIT / STOP Order only by Shares Qty
            // MARKET order support both Shares Qty & Cash ($ Amount)
            if (ordType != OrderType.MARKET || summary.getShareCash() == ShareCash.SHARE) {
                Double qty = Utils.formattedNumtoDouble(summary.getQty());
                params.put("orderQty", qty);
            } else {
                Double subtotal = Utils.formattedNumtoDouble(summary.getSubtotal());
                params.put("amountCash", subtotal);
            }

            // Create Order
            Task buySellTask = Transaction.buySellThread(ordSide, ordType, params);

            buySellTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(final WorkerStateEvent workerStateEvent) {
                    System.out.println(ordSide.getName() + " Task Succeed Handler ....");

                    Pair<OrderManager.Order, String> result = (Pair) workerStateEvent.getSource().getValue();
                    OrderManager.Order order = result.first;
                    String error = result.second;

                    // show result Dialog
                    Alert resultDlg = new Alert(AlertType.INFORMATION);

                    // TextArea - show Create Order Error message
                    TextArea contentText = new TextArea();
                    contentText.setEditable(false);
                    contentText.setWrapText(true);

                    String header = summary.getAction() + " " + summary.getOrdName() + " order ";
                    
                    if (error != null) {
                        header = header + "failed.";

                        contentText.setText("Error : " + error);
                        resultDlg.getDialogPane().setContent(contentText);
                    } else {
                        OrdStatus ordStatus = order.getOrdStatusEnum();

                        System.out.println("order status: " + ordStatus + " ....\n\n\n");

                        if (null != ordStatus) switch (ordStatus) {
                            case REJECTED:
                                header = header + " rejected.";
                                contentText.setText("Reason: " + order.getOrdRejReason());
                                resultDlg.getDialogPane().setContent(contentText);
                                break;
                                
                            case FILLED:
                                header = "Your order was successfully filled. We emailed you a trade notification.";
                                OrdSummary ordSummary = OrdSummary.buildFromOrder(summary, order);
                                TableView resultTable = OrdSummaryTable(ordSummary);
                                setDlgContent(resultDlg, resultTable, null, null);
                                break;

                            case NEW:
                            case PARTIAL_FILLED:
                                header = "Your order was successfully entered. View the status under \"Pending Orders\" in Portfolio.";
                                resultTable = OrdSummaryTable(summary);

                                // Note for pending orders
                                String headerNote = "Estimated values for order if executed.";
                                
                                // LIMIT order: show auto expire time
                                String footerNote = null;
                                if (OrderType.LIMIT.getValue() == Integer.parseInt(order.getOrdType())) {
                                    String expiryUTC = order.getIsoTimeRestingOrderExpires();
                                    String expiryTime = Utils.utcDateToLocal(expiryUTC);

                                    if (expiryTime != null) footerNote = "Unless executed, this order will expire on " + expiryTime;
                                }

                                setDlgContent(resultDlg, resultTable, headerNote, footerNote);
                                break;
                                
                            default:
                                break;
                        }
                    }

                    setDlgTitleHeader(resultDlg, "Order Status", header, summary.getLogoURL());
                    resultDlg.show();

                    // close Submitting Order Dialog
                    submitDlg.close();

                    // refresh Portfolio
                    Portfolio.portfolioService._restart();
                }
            });
        });
    }
}
