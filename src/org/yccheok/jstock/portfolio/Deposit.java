/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.portfolio;

import org.yccheok.jstock.engine.SimpleDate;

/**
 * This class shall be renamed as Cash instead of Deposit, as it carries both
 * deposit and withdraw information. It remains as Deposit, because our xstream
 * already store its name as this is.
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
    public Deposit setAmount(double amount) {
        Deposit d = new Deposit(amount, this.date);
        d.setComment(this.comment);
        return d;
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
    public Deposit setDate(SimpleDate date) {
        Deposit d = new Deposit(this.amount, date);
        d.setComment(this.comment);
        return d;
    }

    private final double amount;
    private final SimpleDate date;
    private String comment = "";
}
