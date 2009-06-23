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
import org.yccheok.jstock.engine.Stock;

/**
 *
 * @author yccheok
 */
public class DividendSummary extends AbstractSummary<Dividend> {
    public DividendSummary() {
    }

    public DividendSummary(DividendSummary dividendSummary) {
        final int size = dividendSummary.size();
        for (int i = 0; i < size; i++) {
            this.add(new Dividend(dividendSummary.get(i)));
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

    public double getTotal(Stock stock) {
        double tmp = 0.0;
        final int size = size();
        for (int i = 0; i < size; i++) {
            final Dividend dividend = this.get(i);
            if (dividend.getStock().getCode().equals(stock.getCode())) {
                tmp += dividend.getAmount();
            }
        }
        return tmp;
    }

    @Override
    protected Object readResolve() {
        super.readResolve();

        /* For backward compatible */
        for (Dividend dividen : dividends) {
            this.add(dividen);
        }
        return this;
    }
    
    /* total shall be removed. It is still here and marked as transient, for xstream backward compatible purpose. */
    private transient double total = 0.0;
    /* needEvaluation shall be removed. It is still here and marked as transient, for xstream backward compatible purpose. */
    private transient volatile boolean needEvaluation = false;
    private List<Dividend> dividends = new ArrayList<Dividend>();
}
