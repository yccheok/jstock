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

import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;

/**
 *
 * @author yccheok
 */
public class Dividend implements Commentable {
    public Dividend(Stock stock, double amount, SimpleDate date) {
        this.stock = stock;
        this.amount = amount;
        this.date = date;
    }

    public Dividend(Dividend dividend) {
        this.stock = dividend.stock;
        this.amount = dividend.amount;
        this.date = dividend.date;
        this.comment = dividend.comment;
    }

    public Dividend setStock(Stock stock) {
        Dividend dividend = new Dividend(stock,this.getAmount(), this.getDate());
        dividend.setComment(this.comment);
        return dividend;
    }

    public Dividend setAmount(double amount) {
        Dividend dividend = new Dividend(this.getStock(), amount, this.getDate());
        dividend.setComment(this.comment);
        return dividend;
    }

    public Dividend setDate(SimpleDate date) {
        Dividend dividend = new Dividend(this.getStock(), this.getAmount(), date);
        dividend.setComment(this.comment);
        return dividend;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * @return the stock
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return the date
     */
    public SimpleDate getDate() {
        return date;
    }

    private final Stock stock;
    private final double amount;
    private final SimpleDate date;
    private String comment = "";
}
