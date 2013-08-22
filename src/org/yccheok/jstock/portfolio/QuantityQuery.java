/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.SimpleDate;

/**
 *
 * @author yccheok
 */
public class QuantityQuery {
    private static final class Balance implements Comparable<Balance> {
        public final double quantity;
        public final SimpleDate date;
        
        private Balance(double quantity, SimpleDate date) {
            this.quantity = quantity;
            this.date = date;
        }
        
        public static Balance newInstance(double quantity, SimpleDate date) {
            return new Balance(quantity, date);
        }

        @Override
        public int compareTo(Balance o) {
            return this.date.compareTo(o.date);
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + this.date.hashCode();
            final long f = Double.doubleToLongBits(quantity);
            result = 31 * result + (int) (f ^ (f >>> 32));

            return result;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;

            if (!(o instanceof Balance))
                return false;

            Balance balance = (Balance)o;
            return this.quantity == balance.quantity && this.date.equals(balance.date);
        }
    }
    
    public QuantityQuery(List<TransactionSummary> transactionSummaries) {
        for (TransactionSummary transactionSummary : transactionSummaries) {
            final int count = transactionSummary.getChildCount();
            List<Transaction> transactions = new ArrayList<Transaction>();
            
            for (int i = 0; i < count; i++) {
                Object o = transactionSummary.getChildAt(i);

                assert(o instanceof Transaction);

                final Transaction transaction = (Transaction)o;
                transactions.add(transaction);
            }
            
            // Sorting ascending
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction o1, Transaction o2) {
                    return o1.getDate().compareTo(o1.getDate());
                }
            });
            
            assert(transactions.isEmpty() == false);
            
            final List<Balance> balances = new ArrayList<Balance>();
            double quantity = 0;
            for (Transaction transaction : transactions) {
                if (transaction.getType() == Contract.Type.Buy) {
                    quantity += transaction.getQuantity();
                } else {
                    assert(transaction.getType() == Contract.Type.Sell);
                    quantity -= transaction.getQuantity();
                }           

                Balance balance = Balance.newInstance(quantity, transaction.getDate());
                balances.add(balance);
            }
            
            // Sorting descending
            Collections.sort(balances, new Comparator<Balance>() {
                @Override
                public int compare(Balance o1, Balance o2) {
                    return o2.date.compareTo(o1.date);
                }
            });
            
            balancesMap.put(transactions.get(0).getStock().code, balances);
        }
    }
    
    public double getBalance(Code code, SimpleDate date) {
        List<Balance> balances = balancesMap.get(code);
        if (balances == null) {
            return 0;
        }
        
        for (Balance balance : balances) {
            if (date.compareTo(balance.date) >= 0) {
                return balance.quantity;
            }
        }
        
        return 0;
    }
    
    private final Map<Code, List<Balance>> balancesMap = new HashMap<Code, List<Balance>>();
}
