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
 * Copyright (C) 2008 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import org.yccheok.jstock.engine.Stock.Board;

/**
 *
 * @author yccheok
 */
public class USMarket implements Market {

    @Override
    public double getIndex(Board board) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getChange(Board board) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNumOfStockChange(ChangeType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getVolume() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Country getCountry() {
        return Country.UnitedState;
    }

}
