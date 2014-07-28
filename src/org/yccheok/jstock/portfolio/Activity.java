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

import java.util.EnumMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.yccheok.jstock.engine.Stock;

/**
 *
 * @author yccheok
 */
public class Activity {
    public enum Param {
        StockInfo,
        Quantity        
    }

    public enum Type {
        Deposit,
        Buy,
        Sell,
        Dividend;
    }

    public static class Builder {
        // Required parameters
        private final Type type;
        private final double amount;
        private Map<Param, Object> paramToObject = new EnumMap<Param, Object>(Param.class);

        public Builder(Type type, double amount) {
            this.type = type;
            this.amount = amount;
        }

        public Builder put(Param param, Object value) {
            paramToObject.put(param, value);
            return this;
        }

        public Activity build() {
            return new Activity(this);
        }
    }

    private Activity(Builder builder) {
        this.type = builder.type;
        this.amount = builder.amount;
        this.paramToObject = builder.paramToObject;
    }

    public Object get(Param param) {
        return paramToObject.get(param);
    }

    public Type getType() {
        return this.type;
    }

    public double getAmount() {
        return this.amount;
    }

    private final Type type;
    private final double amount;
    private Map<Param, Object> paramToObject = new EnumMap<Param, Object>(Param.class);
}
