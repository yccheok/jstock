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

package org.yccheok.jstock.portfolio;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.yccheok.jstock.engine.*;
import org.yccheok.jstock.gui.treetable.DefaultSortableMutableTreeTableNode;
import org.yccheok.jstock.portfolio.Contract.Type;

/**
 *
 * @author Owner
 */
public class Transaction extends DefaultSortableMutableTreeTableNode implements Commentable {
    public Transaction(Contract contract, double broker, double stampDuty, double clearingFee)
    {
        this.contract = contract;
        this.broker = null;
        this.stampDuty = null;
        this.clearingFee = null;

        this.calculatedBroker = broker;
        this.calculatedStampDuty = stampDuty;
        this.calculatdClearingFee = clearingFee;
     
        if (contract.getType() == Contract.Type.Buy) {
            netTotal = this.contract.getTotal() + this.calculatedBroker + this.calculatedStampDuty + this.calculatdClearingFee;
        } else {
            netTotal = this.contract.getTotal() - this.calculatedBroker - this.calculatedStampDuty - this.calculatdClearingFee;
        }
    }
    
    private Contract contract;
    
    /* They shall be removed. They are still here and marked as transient, for xstream backward compatible purpose. */
    private transient Broker broker;
    private transient StampDuty stampDuty;
    private transient ClearingFee clearingFee;
    
    private double calculatedBroker;
    private double calculatedStampDuty;
    private double calculatdClearingFee;
    private double netTotal;
    
    public void copyFrom(Transaction transaction) {
        contract = new Contract(transaction.contract);
        // No longer used. We already marked them as transient.
        //broker = new SimpleBroker((SimpleBroker)transaction.broker);
        //stampDuty = new SimpleStampDuty((SimpleStampDuty)transaction.stampDuty);
        //clearingFee = new SimpleClearingFee((SimpleClearingFee)transaction.clearingFee);
        this.calculatedBroker = transaction.calculatedBroker;
        this.calculatedStampDuty = transaction.calculatedStampDuty;
        this.calculatdClearingFee = transaction.calculatdClearingFee;
        this.netTotal = transaction.netTotal;
        this.comment = transaction.comment;
    }
    
    /**
     * Derives a transaction with new quantity from this transaction.
     * 
     * @param quantity new quantity
     * @return a transaction with new quantity derived from this transaction
     */
    public Transaction deriveWithQuantity(double quantity) {
        return new Transaction(contract.deriveWithQuantity(quantity),
                calculatedBroker, 
                calculatedStampDuty,
                calculatdClearingFee);
    }

    /**
     * Derives a transaction with new price from this transaction.
     * 
     * @param price new price
     * @return a transaction with new price derived from this transaction
     */
    public Transaction deriveWithPrice(double price) {
        return new Transaction(contract.deriveWithPrice(price),
                calculatedBroker,
                calculatedStampDuty,
                calculatdClearingFee);
    }

    public Transaction deriveWithBroker(double broker) {
        return new Transaction(contract,
                broker,
                calculatedStampDuty,
                calculatdClearingFee);
    }

    public Transaction deriveWithStampDuty(double stampDuty) {
        return new Transaction(contract,
                calculatedBroker,
                stampDuty,
                calculatdClearingFee);
    }
    
    public Transaction deriveWithClearingFee(double clearingFee) {
        return new Transaction(contract,
                calculatedBroker,
                calculatedStampDuty,
                clearingFee);
    }

    public double getBroker() {
        return calculatedBroker;
    }

    public double getStampDuty() {
        return calculatedStampDuty;
    }

    public double getClearingFee() {
        return calculatdClearingFee;
    }
      
    public SimpleDate getReferenceDate() {
        return contract.getReferenceDate();
    }
    
    public Type getType() {
        return contract.getType();
    }
    
    public Stock getStock() {
        return contract.getStock();
    }
    
    public double getReferencePrice() {
        return contract.getReferencePrice();
    }
    
    public double getReferenceTotal() {
        return contract.getReferenceTotal();
    }
    
    public Contract getContract() {
        return contract;
    }

    public double getReferenceBroker() {
        return contract.getReferenceBroker();
    }

    public double getReferenceClearingFee() {
        return contract.getReferenceClearingFee();
    }
    
    public double getReferenceStampDuty() {
        return contract.getReferenceStampDuty();
    }
    
    public double getPrice() {
        return contract.getPrice();
    }
    
    public double getTotal() {
        return contract.getTotal();
    }
    
    public double getQuantity() {
        return contract.getQuantity();
    }
    
    public SimpleDate getDate() {
        return contract.getDate();
    }
        
    public double getNetReferenceTotal() {
        return contract.getReferenceTotal() + contract.getReferenceBroker() + contract.getReferenceClearingFee() + contract.getReferenceStampDuty();
    }
    
    public double getNetPrice() {
        return netTotal / contract.getQuantity();
    }
    
    public double getNetTotal() {
        return netTotal;
    }
    
    @Override
    public String toString() {
        return contract.getStock().symbol.toString();
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

        // If this Transaction is read from obsolete XML file, sortable value will
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
    }
    
    @Override
    public Object getValueAt(int column) {
        return treeTableModel.getValueAt(this, column);
    }    
}
