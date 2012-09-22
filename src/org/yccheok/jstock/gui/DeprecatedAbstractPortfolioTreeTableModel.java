/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.gui;

import javax.swing.tree.TreePath;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;

/**
 *
 * @author yccheok
 */
@Deprecated
public abstract class DeprecatedAbstractPortfolioTreeTableModel extends DefaultTreeTableModel {
    public DeprecatedAbstractPortfolioTreeTableModel() {
        super(new Portfolio()) ;
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
        
        final Code code = transaction.getStock().getCode();
        
        TransactionSummary transactionSummary = null;
        
        for (int i = 0; i < size; i++) {
            TransactionSummary t = (TransactionSummary)portfolio.getChildAt(i);
            
            if (((Transaction)t.getChildAt(0)).getStock().getCode().equals(code)) {
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
    
    public void addTransaction(Transaction transaction) {
        if (isValidTransaction(transaction) == false) return;
        
        final Portfolio portfolio = (Portfolio)this.getRoot();
        
        final int size = portfolio.getChildCount();
        
        final Code code = transaction.getStock().getCode();
        
        TransactionSummary transactionSummary = null;
        
        for (int i = 0; i < size; i++) {
            TransactionSummary t = (TransactionSummary)portfolio.getChildAt(i);
            
            if (((Transaction)t.getChildAt(0)).getStock().getCode().equals(code)) {
                transactionSummary = t;
                break;
            }
        }
        
        if (transactionSummary == null) {
            transactionSummary = new TransactionSummary();
            this.insertNodeInto(transactionSummary, portfolio, portfolio.getChildCount());
        }
       
        this.insertNodeInto(transaction, transactionSummary, transactionSummary.getChildCount());
        
        // Workaround to solve root is not being updated when children are not 
        // being collapse.
        fireTreeTableNodeChanged(getRoot());
    }
}
