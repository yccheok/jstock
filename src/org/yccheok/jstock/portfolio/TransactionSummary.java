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

import org.jdesktop.swingx.treetable.*;

/**
 *
 * @author Owner
 */
public class TransactionSummary extends DefaultMutableTreeTableNode {

    public int getQuantity() {
        int result = 0;
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof Transaction);
            
            final Transaction transaction = (Transaction)o;
            
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
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof Transaction);
            
            final Transaction transaction = (Transaction)o;
            
            double calculatedBroker = transaction.getCalculatedBroker();
            
            result += calculatedBroker;
        }

        return result;        
    }

    public double getCalculatedStampDuty() {
        double result = 0.0;
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof Transaction);
            
            final Transaction transaction = (Transaction)o;
            
            double calculatedStampDuty = transaction.getCalculatedStampDuty();
            
            result += calculatedStampDuty;
        }
        
        return result;        
    }

    public double getCalculatdClearingFee() {
        double result = 0.0;
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof Transaction);
            
            final Transaction transaction = (Transaction)o;
            
            double calculatedClearingFee = transaction.getCalculatdClearingFee();
            
            result += calculatedClearingFee;
        }
        
        return result;        
    }
    
    public double getTotal() {
        double result = 0.0;
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof Transaction);
            
            final Transaction transaction = (Transaction)o;
            
            double total = transaction.getTotal();
            
            result += total;
        }
        
        return result;
    }
    
    public double getReferenceTotal() {
        double result = 0.0;
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof Transaction);
            
            final Transaction transaction = (Transaction)o;
            
            double total = transaction.getReferenceTotal();
            
            result += total;
        }
        
        return result;
    }
    
    public double getNetTotal() {
        double result = 0.0;
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof Transaction);
            
            final Transaction transaction = (Transaction)o;
            
            double netTotal = transaction.getNetTotal();
            
            result += netTotal;
        }
        
        return result;        
    }
       
    @Override
    public String toString() {
        if(this.getChildCount() > 0) {
            return this.getChildAt(0).toString();
        }
        
        return "";
    }
}
