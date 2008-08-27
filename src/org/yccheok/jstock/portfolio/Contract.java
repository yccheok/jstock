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

    public static class ContractBuilder implements Builder<Contract> {
        private final Stock stock;
        private final SimpleDate date;
        
        // Optional parameters - initialized to default values
        private Type type = Type.Buy;
        private int quantity = 0;
        private double price = 0.0;
        private double referencePrice = 0.0;
        
        public ContractBuilder(Stock stock, SimpleDate date) {
            this.stock = stock;
            this.date = date;
        }
        
        public ContractBuilder type(Type val) {
            this.type = val;
            return this;
        }

        public ContractBuilder quantity(int val) {
            this.quantity = val;
            return this;
        }
        
        public ContractBuilder price(double val) {
            this.price = val;
            return this;
        }
        
        public ContractBuilder referencePrice(double val) {
            this.referencePrice = val;
            return this;
        }
        
        public Contract build() {
            return new Contract(this);
        }
        
    }
    
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
    
    public double getReferencePrice() {
        return referencePrice;
    }
    
    public double getTotal() {
        return total;
    }
    
    public double getReferenceTotal() {
        return referenceTotal;
    }
    
    public enum Type
    {
        Buy,
        Sell
    }
    
    private Contract(ContractBuilder builder)
    {
        this.stock = builder.stock;
        this.date = builder.date;
        this.type = builder.type;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.referencePrice = builder.referencePrice;
        
        this.total = price * quantity;
        this.referenceTotal = referencePrice * quantity;
    }
    
    public Contract(Contract contract) {
        stock = new Stock(contract.stock);
        date = new SimpleDate(contract.date);
        type = contract.type;
        quantity = contract.quantity;
        price = contract.price;
        referencePrice = contract.referencePrice;
        total = contract.total;
        referenceTotal = contract.referenceTotal;
    }
    
    private final Stock stock;
    private final SimpleDate date;
    private final Type type;
    private final int quantity;
    private final double price;
    private final double referencePrice;
    private final double total;
    // Reference price for the contract. Only for selling type contract usage.
    // It means cost for owning a stock.
    private final double referenceTotal;
}
