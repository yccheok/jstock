/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.trading;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author shuwnyuan
 */
public class TradingView {
    private final StackPane stackPane = new StackPane();
    private static final TradingView INSTANCE = new TradingView();
    private GridPane signInGrid = null;
    private StackPane portfolioStack = null;

    
    private TradingView () {}

    public static TradingView getInstance () {
        return INSTANCE;
    }

    public StackPane getStackPane () {
        return this.stackPane;
    }

    public void showLogin () {
        // first time show
        if (signInGrid == null) {
            signInGrid = SignIn.getInstance().show();
            stackPane.getChildren().add(signInGrid);
            return;
        }

        if (portfolioStack != null) portfolioStack.setVisible(false);
        signInGrid.setVisible(true);
    }

    public void showPortfolio () {
        portfolioStack = Portfolio.getInstance().show();

        signInGrid.setVisible(false);
        stackPane.getChildren().add(portfolioStack);
    }

}
