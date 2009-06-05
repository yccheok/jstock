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

/**
 *
 * @author yccheok
 */
public class Deposit implements Commentable {
    public Deposit(double amount, SimpleDate date) {
        this.amount = amount;
        this.date = date;
    }

    public Deposit(Deposit deposit) {
        this.amount = deposit.amount;
        // SimpleDate is immutable class.
        this.date = deposit.date;
        this.comment = deposit.comment;
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
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return the date
     */
    public SimpleDate getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(SimpleDate date) {
        this.date = date;
    }

    private double amount;
    private SimpleDate date;
    private String comment = "";
}
