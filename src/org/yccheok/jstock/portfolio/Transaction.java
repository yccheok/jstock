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
import org.yccheok.jstock.gui.treetable.DefaulSortabletMutableTreeTableNode;

/**
 *
 * @author Owner
 */
public class Transaction extends DefaulSortabletMutableTreeTableNode implements Commentable {
    public Transaction(Contract contract, Broker broker, StampDuty stampDuty, ClearingFee clearingFee)
    {
        this.contract = contract;
        this.broker = broker;
        this.stampDuty = stampDuty;
        this.clearingFee = clearingFee;

        this.calculatedBroker = broker.calculate(contract);
        this.calculatedStampDuty = stampDuty.calculate(contract);
        this.calculatdClearingFee = clearingFee.calculate(contract);
     
        if (contract.getType() == Contract.Type.Buy) {
            netTotal = this.contract.getTotal() + this.calculatedBroker + this.calculatedStampDuty + this.calculatdClearingFee;
        }
        else {
            netTotal = this.contract.getTotal() - this.calculatedBroker - this.calculatedStampDuty - this.calculatdClearingFee;
        }
    }
    
    private Contract contract;
    private Broker broker;
    private StampDuty stampDuty;
    private ClearingFee clearingFee;
    
    private double calculatedBroker;
    private double calculatedStampDuty;
    private double calculatdClearingFee;
    private double netTotal;
    
    public void copyFrom(Transaction transaction) {
        contract = new Contract(transaction.contract);
        broker = new SimpleBroker((SimpleBroker)transaction.broker);
        stampDuty = new SimpleStampDuty((SimpleStampDuty)transaction.stampDuty);
        clearingFee = new SimpleClearingFee((SimpleClearingFee)transaction.clearingFee);
        this.calculatedBroker = transaction.calculatedBroker;
        this.calculatedStampDuty = transaction.calculatedStampDuty;
        this.calculatdClearingFee = transaction.calculatdClearingFee;
        this.netTotal = transaction.netTotal;
    }
    
    /**
     * Derives a transaction with new quantity from this transaction.
     * 
     * @param quantity new quantity
     * @return a transaction with new quantity derived from this transaction
     */
    public Transaction deriveWithQuantity(double quantity) {
        return new Transaction(contract.deriveWithQuantity(quantity),
                new SimpleBroker((SimpleBroker)broker), 
                new SimpleStampDuty((SimpleStampDuty)stampDuty),
                new SimpleClearingFee((SimpleClearingFee)clearingFee));
    }

    /**
     * Derives a transaction with new price from this transaction.
     * 
     * @param price new price
     * @return a transaction with new price derived from this transaction
     */
    public Transaction deriveWithPrice(double price) {
        return new Transaction(contract.deriveWithPrice(price),
                new SimpleBroker((SimpleBroker)broker),
                new SimpleStampDuty((SimpleStampDuty)stampDuty),
                new SimpleClearingFee((SimpleClearingFee)clearingFee));
    }

    public Contract getContract() {
        return contract;
    }

    public Broker getBroker() {
        return broker;
    }

    public StampDuty getStampDuty() {
        return stampDuty;
    }

    public ClearingFee getClearingFee() {
        return clearingFee;
    }

    public double getCalculatedBroker() {
        return calculatedBroker;
    }

    public double getCalculatedStampDuty() {
        return calculatedStampDuty;
    }

    public double getCalculatdClearingFee() {
        return calculatdClearingFee;
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
    
    public double getReferenceTotal() {
        return contract.getReferenceTotal();
    }
    
    public double getNetTotal() {
        return netTotal;
    }
    
    @Override
    public String toString() {
        return contract.getStock().getSymbol().toString();
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
