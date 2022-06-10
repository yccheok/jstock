/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui.treetable;

import java.util.List;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.portfolio.Dividend;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;

/**
 *
 * @author yccheok
 */
public abstract class AbstractPortfolioTreeTableModelEx extends SortableTreeTableModel {
    public AbstractPortfolioTreeTableModelEx(List<?> columnNames) {
        // SortableTreeTableModel is using columnIdentifiers, which is the 
        // inherited member of DefaultTreeTableModel. In order for 
        // SortableTreeTableModel to work correct, we must call its constructor
        // SortableTreeTableModel(TreeTableNode root, List<?> columnNames) so 
        // that columnIdentifiers will be initialized with correct value.        
        super(new Portfolio(), columnNames);
        // Hacking. Pass myself to portfolio, so that sorting would work.
        ((Portfolio)this.getRoot()).setTreeTableModel(this);
    }

    public void fireTreeTableNodeChanged(TreeTableNode node) {
        TreeTableNode[] nodes = new TreeTableNode[] { node };
        this.modelSupport.firePathChanged(new TreePath(nodes));        
    }
    
    @Override
    public abstract int getColumnCount();

    @Override
    @SuppressWarnings("static-access")
    public abstract Class getColumnClass(int column);
    
    @Override
    public abstract String getColumnName(int column);    
    
    @Override
    public abstract Object getValueAt(Object node, int column);

    public abstract boolean isValidTransaction(Transaction transaction);

    public int getTransactionSize() {
        return ((Portfolio)getRoot()).getChildCount();
    }
    
    // Please take note that, after we edit with newTransaction, the resultant
    // transaction will not equal to newTransaction. We just copy it by value.
    //
    public void editTransaction(Transaction newTransaction, Transaction oldTransaction) {
        if (isValidTransaction(newTransaction) == false) return;
        
        oldTransaction.copyFrom(newTransaction);
        fireTreeTableNodeChanged(oldTransaction);
        fireTreeTableNodeChanged(oldTransaction.getParent());
        fireTreeTableNodeChanged(getRoot());
    }
    
    public void removeTransactionSummary(TransactionSummary transactionSummary) {
        if (transactionSummary == null) {
            return;
        }
        
        this.removeNodeFromParent(transactionSummary);
        
        // Workaround to solve root is not being updated when children are not 
        // being collapse.
        fireTreeTableNodeChanged(getRoot());        
    }
    
    public void removeTransaction(Transaction transaction) {
        if (isValidTransaction(transaction) == false) return;
        
        final Portfolio portfolio = (Portfolio)this.getRoot();
        
        final int size = portfolio.getChildCount();
        
        final Code code = transaction.getStockInfo().code;
        
        TransactionSummary transactionSummary = null;
        
        for (int i = 0; i < size; i++) {
            TransactionSummary t = (TransactionSummary)portfolio.getChildAt(i);
            
            if (((Transaction)t.getChildAt(0)).getStockInfo().code.equals(code)) {
                transactionSummary = t;
                break;
            }
        }
        
        if (transactionSummary == null) {
            return;
        }
        
        this.removeNodeFromParent(transaction);
        if (transactionSummary.getChildCount() <= 0) {
            this.removeNodeFromParent(transactionSummary);
        }
        
        // Workaround to solve root is not being updated when children are not 
        // being collapse.
        fireTreeTableNodeChanged(getRoot());        
    }
    
    public TransactionSummary addTransaction(Transaction transaction) {
        if (isValidTransaction(transaction) == false) {
            return null;
        }
        
        final Portfolio portfolio = (Portfolio)this.getRoot();
        
        final int size = portfolio.getChildCount();
        
        final Code code = transaction.getStockInfo().code;
        
        TransactionSummary transactionSummary = null;
        
        for (int i = 0; i < size; i++) {
            TransactionSummary t = (TransactionSummary)portfolio.getChildAt(i);
            
            if (((Transaction)t.getChildAt(0)).getStockInfo().code.equals(code)) {
                transactionSummary = t;
                break;
            }
        }
        
        if (transactionSummary == null) {
            transactionSummary = new TransactionSummary();
            // Hacking. Pass myself to portfolio, so that sorting would work.
            transactionSummary.setTreeTableModel(this);
            this.insertNodeInto(transactionSummary, portfolio, portfolio.getChildCount());
        }
        
        // Hacking. Pass myself to portfolio, so that sorting would work.
        transaction.setTreeTableModel(this);       
        this.insertNodeInto(transaction, transactionSummary, transactionSummary.getChildCount());
        
        // Workaround to solve root is not being updated when children are not 
        // being collapse.
        fireTreeTableNodeChanged(getRoot());
        
        return transactionSummary;
    }
        
    public void rename(StockInfo newStockInfo, StockInfo oldStockInfo) {
        final Portfolio portfolio = (Portfolio)this.getRoot();
        
        final int size = portfolio.getChildCount();
        
        final Code oldCode = oldStockInfo.code;        
        
        TransactionSummary transactionSummary = null;
        
        for (int i = 0; i < size; i++) {
            TransactionSummary t = (TransactionSummary)portfolio.getChildAt(i);
            
            final Code code = ((Transaction)t.getChildAt(0)).getStockInfo().code;
                    
            if (code.equals(oldCode)) {
                transactionSummary = t;
                break;
            }
        }
        
        if (transactionSummary == null) {
            return;
        }
        
        for (int i = 0, ei = transactionSummary.getChildCount(); i < ei; i++) {
            Transaction oldTransaction = (Transaction)transactionSummary.getChildAt(i);
            Transaction newTransaction = oldTransaction.deriveWithStockInfo(newStockInfo);
            editTransaction(newTransaction, oldTransaction);
        }
    }
}
