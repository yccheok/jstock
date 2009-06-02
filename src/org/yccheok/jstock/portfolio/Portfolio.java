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
public class Portfolio extends DefaultMutableTreeTableNode implements Commentable {

    public double getNetTotal() {
        double result = 0.0;
        
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getNetTotal();
        }
        
        return result;                
    }
    
    public double getTotal() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getTotal();
        }
        
        return result;        
    }

    public double getReferenceTotal() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getReferenceTotal();
        }
        
        return result;        
    }
    
    public double getCalculatedBroker() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getCalculatedBroker();
        }
        
        return result;                
    }

    public double getCalculatedClearingFee() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getCalculatdClearingFee();
        }
        
        return result;                
    }
    
    public double getCalculatedStampDuty() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getCalculatedStampDuty();
        }
        
        return result;                
    }
    
    @Override
    public String toString() {
        return "Portfolio";
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    private Object readResolve() {
        /* For backward compatible */
        if(comment == null) {
            comment = "";
        }

        return this;
    }

    private String comment = "";
}
