/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import java.util.Locale;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.yccheok.jstock.trading.API.SessionManager;
import org.yccheok.jstock.trading.API.SessionManager.User;
import org.yccheok.jstock.trading.API.SessionManager.Account;
import org.yccheok.jstock.trading.AccountSummaryModel;
import org.yccheok.jstock.trading.PositionModel;
import javafx.scene.paint.ImagePattern;
import javafx.util.Callback;

/**
 *
 * @author shuwnyuan
 */
public class AccountSummaryBuilder {

    private final Label activeAccLbl = new Label();
    private final HBox headerBox = new HBox();
    private final Label shareAmount = new Label();
    private final Label profitAmount = new Label();
    private final Label cashAmount = new Label();
    private final Label totalAmount = new Label();
    
    private AccountSummaryModel acc;

    
    public AccountSummaryBuilder() {}
    

    public HBox build () {
        // switch a/c DropDown
        ComboBox accCombo = getAccCombo();
        
        Label dwLabel = new Label("DriveWealth");
        dwLabel.setId("dwLabel");
        //dwLabel.setAlignment(Pos.CENTER);
        HBox dwBox = new HBox();
        dwBox.setPrefWidth(450);
        dwBox.getChildren().add(dwLabel);
        dwBox.setAlignment(Pos.CENTER);

        // A/c summary
        shareAmount.getStyleClass().add("cash");
        profitAmount.getStyleClass().add("cash");
        cashAmount.getStyleClass().add("cash");
        totalAmount.getStyleClass().addAll("cash", "bold");

        VBox cashBox = new VBox();
        cashBox.setAlignment(Pos.CENTER_LEFT);
        cashBox.setSpacing(5);
        Label cashLabel = new Label("CASH TO INVEST");
        cashLabel.getStyleClass().add("headerTitle");
        cashBox.getChildren().addAll(cashLabel, this.cashAmount);
        
        VBox shareBox = new VBox();
        shareBox.setAlignment(Pos.CENTER_LEFT);
        shareBox.setSpacing(5);
        Label shareLabel = new Label("INVESTMENTS");
        shareLabel.getStyleClass().add("headerTitle");
        shareBox.getChildren().addAll(shareLabel, this.shareAmount);

        VBox totalBox = new VBox();
        totalBox.setAlignment(Pos.CENTER_LEFT);
        totalBox.setSpacing(5);
        Label totalLabel = new Label("TOTAL");
        totalLabel.getStyleClass().addAll("headerTitle", "bold");
        totalBox.getChildren().addAll(totalLabel, this.totalAmount);

        VBox profitBox = new VBox();
        profitBox.setPadding(new Insets(0, 0, 5, 0));
        profitBox.setAlignment(Pos.BOTTOM_LEFT);
        profitAmount.setPadding(new Insets(5, 5, 5, 5));
        profitBox.getChildren().addAll(this.profitAmount);

        HBox summaryBox = new HBox();
        summaryBox.setId("accSummaryBox");
        summaryBox.setPadding(new Insets(15, 10, 10, 15));
        summaryBox.setSpacing(15);
        summaryBox.getChildren().addAll(cashBox, shareBox, totalBox, profitBox);

        // Avator
        User user = SessionManager.getInstance().getUser();
        Circle circle = new Circle(30);
        // can't load img in background, as ImagePattern requires img already loaded
        Image img = new Image(user.getAvatarUrl(), false);
        ImagePattern pattern = new ImagePattern(img);
        circle.setFill(pattern);

        HBox userBox = new HBox();
        userBox.setAlignment(Pos.CENTER);
        userBox.setSpacing(15);
        userBox.setPadding(new Insets(10, 10, 10, 10));
        
        // Show Active acc, Acc DrowDown, Profile, Logout
        VBox accBox = new VBox();
        accBox.setSpacing(5);
        activeAccLbl.setText(user.getActiveAccount().getNickname());
        accBox.getChildren().addAll(activeAccLbl, accCombo);
        userBox.getChildren().addAll(circle, accBox);

        // Header
        headerBox.setId("accHeader");
        headerBox.setPrefHeight(120);
        headerBox.getChildren().addAll(dwBox, summaryBox, userBox);

        return headerBox;
    }

    public ComboBox getAccCombo () {
        User user = SessionManager.getInstance().getUser();

        ComboBox<Object> accCombo = new ComboBox<>();
        ObservableList<Object> options = FXCollections.observableArrayList(user.getAccounts());
        accCombo.setItems(options);

        options.add("Profile");
        options.add("Logout");

        accCombo.setCellFactory(
            new Callback<ListView<Object>, ListCell<Object>>() {
                @Override
                public ListCell<Object> call(ListView<Object> p) {
                    ListCell cell = new ListCell<Object>() {
                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText("");
                            } else {
                                if (item instanceof Account) {
                                    Account ac = (Account) item;

                                    Label acType = new Label(ac.getAccountType().getName() + " Account");
                                    acType.getStyleClass().add("accType");

                                    Label acNo = new Label(ac.getAccountNo());
                                    acNo.getStyleClass().add("accNo");

                                    VBox accBox = new VBox();
                                    accBox.setSpacing(5);
                                    accBox.getChildren().addAll(acType, acNo);

                                    setGraphic(accBox);
                                } else {
                                    VBox vBox = new VBox();
                                    vBox.setPadding(new Insets(5, 0, 5, 0));  // top right bottom left
                                    Label itemLabel = new Label((String) item);
                                    vBox.getChildren().add(itemLabel);
                                    setGraphic(vBox);
                                }
                            }
                        }
                    };
                    return cell;
                }
            });
        
        
        SessionManager.Account active = user.getActiveAccount();
        if (active != null) {
            accCombo.getSelectionModel().select(active);
        }

        // a/c change listener
        accCombo.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Object> arg0, Object oldVal, Object newVal) -> {
            if (newVal != null) {
                //System.out.println("Acc changed: " + newVal.toString());

                if (newVal instanceof Account) {
                    System.out.println("Acc changed is ACCOUNT --------");
                    
                    user.setActiveAccount((Account) newVal);
                    String accDisplay = SessionManager.getInstance().getUser().getActiveAccount().getNickname();
                    activeAccLbl.setText(accDisplay);
                    Portfolio.getInstance().initPortfolioServ();
                    return;
                }

                if (newVal instanceof String) {
                    System.out.println("Acc changed is STRING --------");
                    
                    String action = (String) newVal;
                    
                    if (action.equals("Profile")) {
                        System.out.println("Profile ------ No profile yet");
                    } else if (action.equals("Logout")) {
                        // Cancel session
                        // show login page
                        System.out.println("Signout ------ ");
                    }
                }
            }
        });

        return accCombo;
    }
    
    
    public void initData (AccountSummaryModel accModel) {
        resetData();

        this.acc = accModel;
        Locale locale  = new Locale("en", "US");

        this.shareAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.equityProperty()));
        
        this.profitAmount.textProperty().bind(Bindings.format(locale, "$%,.2f (%,.2f%%)",
                this.acc.totalUnrealizedPLProperty(),
                this.acc.totalUnrealizedPLPercentProperty()));
        
        this.cashAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.cashForTradeProperty()));
        this.totalAmount.textProperty().bind(Bindings.format(locale, "$%,.2f", this.acc.accountTotalProperty()));

        this.profitAmount.getStyleClass().add(this.acc.unrealizedPLCss());
    }

    public void resetData () {
        // remove binding & css
        this.profitAmount.textProperty().unbind();
        this.profitAmount.getStyleClass().clear();
    }
    
    public void update (ObservableList<PositionModel> posList) {
        this.acc.update(posList);

        this.profitAmount.getStyleClass().clear();
        profitAmount.getStyleClass().addAll("header", acc.unrealizedPLCss());
    }
}
