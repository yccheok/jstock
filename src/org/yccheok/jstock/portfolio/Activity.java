/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.portfolio;

import java.text.NumberFormat;
import javax.swing.ImageIcon;

/**
 *
 * @author yccheok
 */
public class Activity {
    public enum Type {
        Deposit("/images/16x16/money.png"),
        Buy("/images/16x16/inbox.png"),
        Sell("/images/16x16/outbox.png"),
        Dividend("/images/16x16/money2.png");

        Type(String fileName) {
            this.icon = new javax.swing.ImageIcon(this.getClass().getResource(fileName));
        }

        public ImageIcon getIcon() {
            return icon;
        }

        private ImageIcon icon;
    }

    public Activity(String who, Type type, double amount) {
        this.type = type;
        this.amount = amount;
        this.who = who;
    }

    public Type getType() {
        return this.type;
    }

    public String getWho() {
        return who;
    }

    public double getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        if (who.length() > 1) {
            return who + " " + type.toString().toLowerCase() + " " + currencyFormat.format(amount);
        }

        return type.toString().toLowerCase() + " " + currencyFormat.format(amount);
    }

    private static final NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance();
    //static {
    //    currencyFormat.setMaximumFractionDigits(2);
    //    currencyFormat.setMinimumFractionDigits(2);
    //}
    private final String who;
    private final Type type;
    private final double amount;
}
