/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.gui.treetable.DefaultSortableMutableTreeTableNode;

/**
 *
 * @author Owner
 */
public class Portfolio extends DefaultSortableMutableTreeTableNode implements Commentable {
    // Avoid NPE.
    private PortfolioRealTimeInfo portfolioRealTimeInfo = new PortfolioRealTimeInfo();
    
    public void bind(PortfolioRealTimeInfo portfolioRealTimeInfo) {
        this.portfolioRealTimeInfo = portfolioRealTimeInfo;
    }
    
    public double getNetTotal(Currency localCurrency) {
        double result = 0.0;

        final int count = this.getChildCount();
        
        for (int i = 0; i < count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
            final Code code = transaction.getStockInfo().code;
            
            final double exchangeRate = org.yccheok.jstock.portfolio.Utils.getExchangeRate(portfolioRealTimeInfo, localCurrency, code);

            result += (transactionSummary.getNetTotal() * exchangeRate);
        }
        
        return result;                
    }
    
    public double getTotal(Currency localCurrency) {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for (int i = 0; i < count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
            final Code code = transaction.getStockInfo().code;
            
            final double exchangeRate = org.yccheok.jstock.portfolio.Utils.getExchangeRate(portfolioRealTimeInfo, localCurrency, code);

            result += (transactionSummary.getTotal() * exchangeRate);
        }
        
        return result;        
    }

    public double getNetReferenceTotal(Currency localCurrency) {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for (int i = 0; i < count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
            final Code code = transaction.getStockInfo().code;
            
            final double exchangeRate = org.yccheok.jstock.portfolio.Utils.getExchangeRate(portfolioRealTimeInfo, localCurrency, code);

            result += (transactionSummary.getNetReferenceTotal() * exchangeRate);
        }
        
        return result;        
    }
    
    public double getReferenceTotal(Currency localCurrency) {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for (int i = 0; i < count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            
            final Transaction transaction = (Transaction)transactionSummary.getChildAt(0);
        
            final Code code = transaction.getStockInfo().code;
            
            final double exchangeRate = org.yccheok.jstock.portfolio.Utils.getExchangeRate(portfolioRealTimeInfo, localCurrency, code);

            result += (transactionSummary.getReferenceTotal() * exchangeRate);
        }
        
        return result;        
    }
    
    public double getBroker() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getBroker();
        }
        
        return result;                
    }

    public double getClearingFee() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getClearingFee();
        }
        
        return result;                
    }
    
    public double getStampDuty() {
        double result = 0.0;
     
        final int count = this.getChildCount();
        
        for(int i=0; i<count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            result += transactionSummary.getStampDuty();
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
        if (comment == null) {
            comment = "";
        }

        // If this Portfolio is read from obsolete XML file, sortable value will
        // be false. We need it to be true all the time.
        this.setSortable(true);
        
        return this;
    }
    
    private String comment = "";
    
    // **************** HACKING CODE SO THAT SORTING COULD WORK ****************
    
    // Use transient, so that we still can load portfolio from obsolete XML file.
    private transient TreeTableModel treeTableModel = null;
    // In order for sorting to work, Portfolio must implement getValueAt correctly.
    // In order for getValueAt to work correctly, it must obtain information from
    // TreeTableModel.
    public void setTreeTableModel(TreeTableModel treeTableModel) {
        this.treeTableModel = treeTableModel;
        
        // Propogate TreeTableModel information to its children.
        final int count = this.getChildCount();
        
        for (int i = 0; i < count; i++) {
            Object o = this.getChildAt(i);
            
            assert(o instanceof TransactionSummary);
            
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            transactionSummary.setTreeTableModel(treeTableModel);
        }        
    }
    
    @Override
    public Object getValueAt(int column) {
        return treeTableModel.getValueAt(this, column);
    }    
}
