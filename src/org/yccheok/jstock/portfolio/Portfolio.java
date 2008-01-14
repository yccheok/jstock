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

import java.util.List;
import java.util.ArrayList;
import org.yccheok.jstock.gui.treetable.AbstractTreeTableable;
import org.yccheok.jstock.gui.treetable.TreeTableable;

/**
 *
 * @author Owner
 */
public class Portfolio extends AbstractTreeTableable {
    
    public int getSize() {
        return transactionSummaries.size();        
    }
    
    public TransactionSummary getTransactionSummary(int index) {
        return transactionSummaries.get(index);
    }
    
    public boolean removeTransaction(Transaction transaction) {
        for(TransactionSummary transactionSummary : transactionSummaries) {            
            boolean status = transactionSummary.removeTransaction(transaction);
            
            if(status) {
                if(transactionSummary.getSize() == 0) {
                    transactionSummaries.remove(transactionSummary);
                }
                
                return status;
            }
        }
        
        return false;
    }
    
    public boolean addTransaction(Transaction transaction) {
        for(TransactionSummary transactionSummary : transactionSummaries) {
            if(transactionSummary.getSize() == 0) {
                assert(false);
                continue;
            }
                
            
            final Transaction t0 = transactionSummary.getTransaction(0);
            if(Utils.isTransactionWithEqualStockCode(t0, transaction)) {
                return transactionSummary.addTransaction(transaction);
            }
        }
        
        TransactionSummary transactionSummary = new TransactionSummary(this);
        transactionSummary.addTransaction(transaction);
        
        return this.transactionSummaries.add(transactionSummary);
    }
        
    public double getNetTotal() {
        double result = 0;
        
        for(TransactionSummary transactionSummary : transactionSummaries) {
            result += transactionSummary.getNetTotal();
        }
        
        return result;                
    }
    
    public double getTotal() {
        double result = 0.0;
        
        for(TransactionSummary transactionSummary : transactionSummaries) {
            result += transactionSummary.getTotal();
        }
        
        return result;        
    }
    
    private List<TransactionSummary> transactionSummaries = new ArrayList<TransactionSummary>();

    public TreeTableable getParent() {
        return null;
    }

    public TreeTableable getChild(int index) {
        return getTransactionSummary(index);
    }
    
    public String toString() {
        return "Portfolio";
    }
}
