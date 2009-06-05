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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yccheok
 */
public class DepositSummary {
    public DepositSummary() {
    }

    public DepositSummary(DepositSummary depositSummary) {
        for (Deposit deposit : depositSummary.deposits) {
            this.deposits.add(new Deposit(deposit));
        }
        this.needEvaluation = depositSummary.needEvaluation;
        this.total = depositSummary.total;
    }

    public boolean add(Deposit deposit) {
        this.needEvaluation = true;
        return deposits.add(deposit);

    }

    public Deposit remove(int index) {
        this.needEvaluation = true;
        return deposits.remove(index);
    }

    public boolean remove(Deposit deposit) {
        this.needEvaluation = true;
        return deposits.remove(deposit);
    }

    public int size() {
        return deposits.size();
    }

    public Deposit get(int index) {
        return deposits.get(index);
    }

    // Can we have lazy evaluation?
    public double getTotal() {
        if (this.needEvaluation) {
            double tmp = 0.0;
            for (Deposit deposit : deposits) {
                tmp += deposit.getAmount();
            }
            total = tmp;
            this.needEvaluation = false;
        }
        return total;
    }

    private double total = 0.0;
    private volatile boolean needEvaluation = false;
    private List<Deposit> deposits = new ArrayList<Deposit>();
}
