/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.Optional;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.trading.API.OrderManager;
import org.yccheok.jstock.trading.OrderModel;
import org.yccheok.jstock.trading.Utils;
import static org.yccheok.jstock.trading.API.OrderManager.OrderType;
import static org.yccheok.jstock.trading.API.OrderManager.OrderSide;

/**
 *
 * @author shuwnyuan
 */

public class CancelOrderDlg {
    private final Alert cancelDlg           = new Alert(Alert.AlertType.CONFIRMATION);
    private final ButtonType yesButtonType  = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType noButtonType   = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

    private final OrderModel ord;

    
    public CancelOrderDlg (OrderModel ord) {
        this.ord = ord;
    }

    private String getOrderMsg () {
        String ordType = this.ord.getType();
        String side = this.ord.getSide();

        String price = "";
        Double priceD = this.ord.getOrderPrice();
        if (priceD != null) price = Utils.monetaryFormat(priceD);

        String qty = Utils.formatNumber(this.ord.getUnits(), 4);
        String msg;
        
        if (side.equals(OrderSide.BUY.getName())) {
            if (ordType.equals(OrderType.LIMIT.getName())) {
                msg = String.format("Buy %1$s shares at or below %2$s.", qty, price);
            } else if (ordType.equals(OrderType.STOP.getName())) {
                msg = String.format("Buy %1$s shares at or above %2$s.", qty, price);
            } else {
                msg = String.format("Buy %1$s shares at market price", qty);
            }
        } else {
            if (ordType.equals(OrderType.LIMIT.getName())) {
                msg = String.format("Sell %1$s shares at or above %2$s.", qty, price);                
            } else if (ordType.equals(OrderType.STOP.getName())) {
                msg = String.format("Sell %1$s shares at or below %2$s.", qty, price);
            } else {
                msg = String.format("Sell %1$s shares at market price", qty);
            }
        }

        return msg;
    }

    public void initDlgAndWait () {
        String title = String.format("Cancel %1$s Order", ord.getType());
        String header = String.format("%1$s - %2$s", ord.getSymbol(), ord.getName());
        CreateOrderDlg.setDlgTitleHeader(cancelDlg, title, header, ord.getUrlImage());

        // Order details
        Label orderMsg = new Label(getOrderMsg());
        orderMsg.setId("order-msg");
        
        double width = 450;
        orderMsg.setPrefWidth(width);

        // Confirm msg
        Label confirmMsg = new Label("Are you sure you wish to cancel this order?");

        GridPane grid = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints(width);
        grid.getColumnConstraints().add(col1);

        grid.setHgap(10);
        grid.setVgap(20);
        grid.add(orderMsg, 0, 0);
        grid.add(confirmMsg, 0, 1);
        cancelDlg.getDialogPane().setContent(grid);

        // YES / NO button
        cancelDlg.getButtonTypes().setAll(yesButtonType, noButtonType);
        cancelOrdHandler();

        Optional<ButtonType> result = cancelDlg.showAndWait();
        ButtonType buttonType = result.get();

        if (buttonType == yesButtonType) {
            System.out.println("YES btn Pressed ......");
        } else {
            System.out.println("NO btn Pressed ......");
        }
    }
 
    private void cancelOrdHandler () {
        Node yesButton = cancelDlg.getDialogPane().lookupButton(yesButtonType);

        yesButton.addEventHandler(ActionEvent.ACTION, event -> {
            // temporary cancel / suspend Portfolio Scheduled Service
            Portfolio.getInstance().cancelPortfolioServ();

            // Dialog to show Cancelling Order
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            String header = String.format("%1$s - %2$s", ord.getSymbol(), ord.getName());
            CreateOrderDlg.setDlgTitleHeader(alert, "Cancel Order", header, ord.getUrlImage());

            alert.getDialogPane().setPrefSize(450, 200);
            alert.setContentText("Cancelling order....");

            // disable OK button
            alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            alert.show();

            // Cancel Order API call
            Task task = new Task<Pair<Boolean, String>>() {
                @Override
                protected Pair<Boolean, String> call() throws Exception {
                    String orderID = ord.getOrderID();
                    System.out.println("Cancel OrderID :" + orderID);

                    Pair<Boolean, String> result = OrderManager.cancel(orderID);
                    System.out.println("Cancel Order success : " + result.first + "....\n\n");

                    return result;
                }
            };
            
            Thread thread = new Thread(task);
            thread.start();

            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(final WorkerStateEvent workerStateEvent) {
                    Pair<Boolean, String> result = (Pair<Boolean, String>) workerStateEvent.getSource().getValue();

                    Boolean success = result.first;
                    String error = result.second;
                    
                    String content;
                    if (success == true) content = "Order cancelled successfully.";
                    else content = "Cancel order failed! Reason: " + error;

                    alert.setContentText(content);
                    alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                    
                    Portfolio.getInstance().restartPortfolioServ();
                }
            });
        });
    }
    
}
