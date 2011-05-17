/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.yccheok.jstock.gui;

import java.awt.event.MouseListener;
import javax.swing.*;
import org.jdesktop.swingx.*;

/**
 *
 * @author yccheok
 */
public class MyJXStatusBar extends JXStatusBar {
    
    /** Creates a new instance of MyJStatusBar */
    public MyJXStatusBar() {
        super();
        
        mainLabel = new JLabel();
        progressBar = new JProgressBar();
        progressBar.setStringPainted(false);
        exchangeRateLabel = new JLabel();
        countryLabel = new JLabel();
        imageLabel = new JLabel();

        exchangeRateLabel.setHorizontalAlignment(JLabel.CENTER);
        countryLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        JXStatusBar.Constraint c1 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
        JXStatusBar.Constraint c2 = new JXStatusBar.Constraint(100);
        JXStatusBar.Constraint c3 = new JXStatusBar.Constraint(50);
        JXStatusBar.Constraint c4 = new JXStatusBar.Constraint(50);
        JXStatusBar.Constraint c5 = new JXStatusBar.Constraint(50);
        
        this.add(mainLabel, c1);
        this.add(progressBar, c2);
        this.add(exchangeRateLabel, c3);
        this.add(countryLabel, c4);
        this.add(imageLabel, c5);
    }

    public MyJXStatusBar setMainMessage(String mainMessage) {
        mainLabel.setText(mainMessage);
        return this;
    }

    public MyJXStatusBar setImageIcon(ImageIcon imageIcon, String imageIconToolTipText) {
        imageLabel.setIcon(imageIcon);        
        imageLabel.setToolTipText(imageIconToolTipText);
        return this;
    }    

    public MyJXStatusBar setCountryIcon(ImageIcon imageIcon, String imageIconToolTipText) {
        countryLabel.setIcon(imageIcon);        
        countryLabel.setToolTipText(imageIconToolTipText);
        return this;
    }
    
    public void addImageLabelMouseListener(MouseListener l) {
        imageLabel.addMouseListener(l);
    }

    public void addCountryLabelMouseListener(MouseListener l) {
        countryLabel.addMouseListener(l);
    }

    /**
     * Add mouse listener to the exchange rate label.
     * 
     * @param l the mouse listener
     */
    public void addExchangeRateLabelMouseListener(MouseListener l) {
        exchangeRateLabel.addMouseListener(l);
    }

    public MyJXStatusBar setProgressBar(boolean newValue) {
        progressBar.setIndeterminate(newValue);
        progressBar.setVisible(newValue);
        return this;
    }

    /**
     * Set the tool tip text on exchange rate label.
     *
     * @param text the tool tip text
     * @return this status bar
     */
    public MyJXStatusBar setExchangeRateToolTipText(String text) {
        exchangeRateLabel.setToolTipText(text);
        return this;
    }

    /**
     * Set the visibility of exchange rate label.
     *
     * @param visible true to make exchange rate label visible. Else false
     * @return this status bar
     */
    public MyJXStatusBar setExchangeRateVisible(final boolean visible) {
        if (visible) {
            // We want to make exchange rate label visible. Only do something
            // if the label is not visible yet.
            if (false == exchangeRateLabel.isVisible()) {
                JXStatusBar.Constraint c = new JXStatusBar.Constraint(50);
                this.add(exchangeRateLabel, c, 2);
                exchangeRateLabel.setVisible(true);
                // Call revalidate followed by repaint to yield a refresh view.
                this.revalidate();
                this.repaint();
            }
        } else {
            // We want to make exchange rate label invisible. Only do something
            // if the label is visible yet.
            if (exchangeRateLabel.isVisible()) {
                this.remove(exchangeRateLabel);
                exchangeRateLabel.setVisible(false);
                // Call revalidate followed by repaint to yield a refresh view.
                this.revalidate();
                this.repaint();
            }
        }
        return this;
    }

    /**
     * Set the exchange rate value.
     *
     * @param exchangeRate the exchange rate value. null to reset
     * @return this status bar
     */
    public MyJXStatusBar setExchangeRate(Double exchangeRate) {
        
        if (exchangeRate == null) {
            exchangeRateLabel.setText("");
            exchangeRateLabel.setIcon(null);            
        } else {
            exchangeRateLabel.setText(org.yccheok.jstock.portfolio.Utils.toExchangeRate(exchangeRate));
            // Determine which icon should be used.
            if (prevExchangeRate != null) {
                final boolean reverse = org.yccheok.jstock.engine.Utils.isFallBelowAndRiseAboveColorReverse();
                if (exchangeRate > prevExchangeRate) {
                    if (reverse) {
                        exchangeRateLabel.setIcon(Icons.RED_UP);
                    } else {
                        exchangeRateLabel.setIcon(Icons.GREEN_UP);
                    }
                } else if (exchangeRate < prevExchangeRate) {
                    if (reverse) {
                        exchangeRateLabel.setIcon(Icons.GREEN_DOWN);
                    } else {
                        exchangeRateLabel.setIcon(Icons.RED_DOWN);
                    }
                } else {
                    // Make no change on current icon display.
                }
            }
        }
        prevExchangeRate = exchangeRate;
        return this;
    }

    // Previous last updated exchange rate. We need this to determine type of
    // icon.
    private Double prevExchangeRate = null;

    private final JLabel mainLabel;
    private final JLabel exchangeRateLabel;
    private final JLabel countryLabel;
    private final JLabel imageLabel;
    private final JProgressBar progressBar;
}
