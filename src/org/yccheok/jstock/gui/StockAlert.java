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

package org.yccheok.jstock.gui;

import java.util.Objects;

/**
 *
 * @author yccheok
 */
public class StockAlert {
    public final Double fallBelow;
    public final Double riseAbove;

    public StockAlert setRiseAbove(Double riseAbove) {
        return new StockAlert(this.fallBelow, riseAbove);
    }

    public StockAlert setFallBelow(Double fallBelow) {
        return new StockAlert(fallBelow, this.riseAbove);
    }

    public StockAlert() {
        this.fallBelow = null;
        this.riseAbove = null;
    }

    public StockAlert(Double fallBelow, Double riseAbove) {
        this.fallBelow = fallBelow;
        this.riseAbove = riseAbove;
    }

    public StockAlert(StockAlert stockAlert) {
        this.fallBelow = stockAlert.fallBelow;
        this.riseAbove = stockAlert.riseAbove;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (fallBelow != null ? fallBelow.hashCode() : 0);
        result = 31 * result + (riseAbove != null ? riseAbove.hashCode() : 0);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if(!(o instanceof StockAlert))
            return false;

        final StockAlert stockAlert = (StockAlert)o;

        boolean result0 = Objects.equals(fallBelow, stockAlert.fallBelow) &&
                          Objects.equals(riseAbove, stockAlert.riseAbove);

        return result0;
    }

    @Override
    public String toString() {
        return "Alert with fallBelow=" + fallBelow + " and riseAbove=" + riseAbove;
    }
}
