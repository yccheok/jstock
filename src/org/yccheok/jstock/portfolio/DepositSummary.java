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

import java.util.ArrayList;
import java.util.List;

/**
 * This class shall be renamed as CashSummary instead of DepositSummary, as it
 * carries both deposit and withdraw information. It remains as DepositSummary,
 * because our xstream already store its name as this is.
 *
 * @author yccheok
 */
public class DepositSummary extends AbstractSummary<Deposit> {
    public DepositSummary() {
    }

    public DepositSummary(DepositSummary depositSummary) {
        final int size = depositSummary.size();
        for (int i = 0; i < size; i++) {
            this.add(new Deposit(depositSummary.get(i)));
        }
    }

    public double getTotal() {
        double tmp = 0.0;
        final int size = size();
        for (int i = 0; i < size; i++) {
            tmp += this.get(i).getAmount();
        }
        return tmp;
    }
    
    @Override
    protected Object readResolve() {
        super.readResolve();

        /* For backward compatible */
        for (Deposit deposit : deposits) {
            this.add(deposit);
        }
        return this;
    }
    
    /* total shall be removed. It is still here and marked as transient, for xstream backward compatible purpose. */
    private transient double total = 0.0;
    /* needEvaluation shall be removed. It is still here and marked as transient, for xstream backward compatible purpose. */
    private transient volatile boolean needEvaluation = false;
    private List<Deposit> deposits = new ArrayList<Deposit>();    
}
