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
            final Code code = ((Transaction)transactionSummary.getChildAt(0)).getStock().code;
            final int count = transactionSummary.getChildCount();
            final List<Balance> tmps = new ArrayList<Balance>();

            // BUILD TMPS.
            {
                for (int i = 0; i < count; i++) {
                    Object o = transactionSummary.getChildAt(i);
    
                    assert(o instanceof Transaction);
    
                    final Transaction transaction = (Transaction)o;
                    if (transaction.getType() == Contract.Type.Buy) {
                        Balance balance = Balance.newInstance(transaction.getQuantity(), transaction.getDate());
                        tmps.add(balance);
                    } else {
                        assert(transaction.getType() == Contract.Type.Sell);
                        Balance balance0 = Balance.newInstance(transaction.getQuantity(), transaction.getReferenceDate());
                        Balance balance1 = Balance.newInstance(-transaction.getQuantity(), transaction.getDate());
                        tmps.add(balance0);
                        tmps.add(balance1);
                    }
                }
                
                // Sorting ascending
                Collections.sort(tmps, new Comparator<Balance>() {
                    @Override
                    public int compare(Balance o1, Balance o2) {
                        return o1.date.compareTo(o2.date);
                    }
                });
            }
            // BUILD TMPS.

            assert(tmps.isEmpty() == false);
            
            final List<Balance> mergedTmps = new ArrayList<Balance>();
            
            // MERGE TMPS.
            {
                Balance balance = null;
                for (int j = 0, ej = tmps.size() - 1; j < ej; j++) {
                    Balance tmp0 = tmps.get(j);
                    Balance tmp1 = tmps.get(j + 1);
                    if (tmp0.date.equals(tmp1.date)) {
                        if (balance == null) {
                            balance = tmp0;
                        } else {
                            balance = Balance.newInstance(balance.quantity + tmp0.quantity, tmp0.date);
                        }
                    } else {
                        if (balance == null) {
                            mergedTmps.add(tmp0);
                        } else {
                            balance = Balance.newInstance(balance.quantity + tmp0.quantity, tmp0.date);
                            mergedTmps.add(balance);
                            balance = null;
                        }
                    }
                }
                
                Balance lastTmp = tmps.get(tmps.size() - 1);
                if (balance == null) {
                    mergedTmps.add(lastTmp);
                } else {
                    balance = Balance.newInstance(balance.quantity + lastTmp.quantity, lastTmp.date);
                    mergedTmps.add(balance);
                    balance = null;
                }
            }
            // MERGE TMPS.

            final List<Balance> balances = new ArrayList<Balance>();
            double quantity = 0;
            for (Balance mergeTmp : mergedTmps) {
                quantity += mergeTmp.quantity;
                Balance balance = Balance.newInstance(quantity, mergeTmp.date);
                balances.add(balance);
            }
            
            // Sorting descending
            Collections.sort(balances, new Comparator<Balance>() {
                @Override
                public int compare(Balance o1, Balance o2) {
                    return -o1.date.compareTo(o2.date);
                }
            });
            
            balancesMap.put(code, balances);
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
