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
public class DividendSummary {
    public DividendSummary() {
    }

    public DividendSummary(DividendSummary dividendSummary) {
        for (Dividend dividend : dividendSummary.dividends) {
            this.dividends.add(new Dividend(dividend));
        }
        this.needEvaluation = dividendSummary.needEvaluation;
        this.total = dividendSummary.total;
    }

    public boolean add(Dividend dividend) {
        this.needEvaluation = true;
        return dividends.add(dividend);

    }

    public Dividend remove(int index) {
        this.needEvaluation = true;
        return dividends.remove(index);
    }

    public boolean remove(Dividend dividend) {
        this.needEvaluation = true;
        return dividends.remove(dividend);
    }

    public int size() {
        return dividends.size();
    }

    public Dividend get(int index) {
        return dividends.get(index);
    }

    // Can we have lazy evaluation?
    public double getTotal() {
        if (this.needEvaluation) {
            double tmp = 0.0;
            for (Dividend dividend : dividends) {
                tmp += dividend.getAmount();
            }
            total = tmp;
            this.needEvaluation = false;
        }
        return total;
    }

    public double getTotal(Stock stock) {
        double tmp = 0.0;
        for (Dividend dividend : dividends) {
            if (stock.getCode().equals(dividend.getStock().getCode())) {
                tmp = tmp + dividend.getAmount();
            }
        }
        return tmp;
    }
    
    public void add(int index, Dividend dividend) {
        dividends.add(index, dividend);
    }

    private double total = 0.0;
    private volatile boolean needEvaluation = false;
    private List<Dividend> dividends = new ArrayList<Dividend>();
}
