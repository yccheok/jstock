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

import java.util.*;

/**
 *
 * @author Owner
 */
public class TransactionSummary {
    public TransactionSummary() {
        
    }

    public int getSize() {
        return transactions.size();
    }
    
    public Transaction getTransaction(int index) {
        return transactions.get(index);
    }
    
    public boolean addTransaction(Transaction transaction) {
        return transactions.add(transaction);
    }
    
    public boolean removeTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }
    
    public int getQuantity() {
        int result = 0;
        
        for(Transaction transaction : transactions) {
            int quantity = transaction.getQuantity();
            
            if(transaction.getContract().getType() == Contract.Type.Buy) {
                result += quantity;
            }
            else {
                result -= quantity;
            }
        }
        
        return result;        
    }
    
    /*
    public double getRealizedProfit() {
        double result = 0.0;
        
        for(Transaction transaction : transactions) {
            double total = transaction.getTotal();
            
            if(transaction.getContract().getType() == Contract.Type.Buy) {
                result -= total;
            }
            else {
                result += total;
            }
        }
        
        return result;
    }
    */
    
    public double getCalculatedBroker() {
        double result = 0.0;
        
        for(Transaction transaction : transactions) {
            double calculatedBroker = transaction.getCalculatedBroker();
            
            result += calculatedBroker;
        }
        
        return result;        
    }

    public double getCalculatedStampDuty() {
        double result = 0.0;
        
        for(Transaction transaction : transactions) {
            double calculatedStampDuty = transaction.getCalculatedStampDuty();
            
            result += calculatedStampDuty;
        }
        
        return result;        
    }

    public double getCalculatdClearingFee() {
        double result = 0.0;
        
        for(Transaction transaction : transactions) {
            double calculatedClearingFee = transaction.getCalculatdClearingFee();
            
            result += calculatedClearingFee;
        }
        
        return result;        
    }
    
    public double getTotal() {
        double result = 0.0;
        
        for(Transaction transaction : transactions) {
            double total = transaction.getTotal();
            
            result += total;
        }
        
        return result;
    }
    
    public double getNetTotal() {
        double result = 0.0;
        
        for(Transaction transaction : transactions) {
            double netTotal = transaction.getNetTotal();
            
            result += netTotal;
        }
        
        return result;        
    }
    
    private List<Transaction> transactions = new ArrayList<Transaction>();
}
