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

package org.yccheok.jstock.portfolio;

import org.yccheok.jstock.engine.*;

/**
 *
 * @author Owner
 */
public class Contract {

    public Stock getStock() {
        return stock;
    }

    public SimpleDate getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
    
    public double getTotal() {
        return total;
    }
    
    public enum Type
    {
        Buy,
        Sell
    }
    
    public Contract(Stock stock, SimpleDate date, Type type, int quantity, double price)
    {
        this.stock = stock;
        this.date = date;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        
        this.total = price * quantity;
    }
    
    public Contract(Contract contract) {
        stock = new Stock(contract.stock);
        date = new SimpleDate(contract.date);
        type = contract.type;
        quantity = contract.quantity;
        price = contract.price;
        total = contract.total;
    }
    
    private Stock stock;
    private SimpleDate date;
    private Type type;
    private int quantity;
    private double price;    
    private double total;
}
