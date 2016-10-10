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

/**
 *
 * @author yccheok
 */
public class BrokingFirm {
    public BrokingFirm(String name) {
        this.name = name;
        this.broker = new SimpleBroker(0.0, 0.0, 0.0);
        this.clearingFee = new SimpleClearingFee(0.0, 0.0, 0.0);
        this.stampDuty = new SimpleStampDuty(0.0, 1.0, 0.0);
    }
    
    public BrokingFirm(BrokingFirm brokingFirm) {
        this.name = brokingFirm.getName();
        this.broker = new SimpleBroker((SimpleBroker)brokingFirm.broker);
        this.clearingFee = new SimpleClearingFee((SimpleClearingFee)brokingFirm.clearingFee);
        this.stampDuty = new SimpleStampDuty((SimpleStampDuty)brokingFirm.stampDuty);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    private String name;
    private Broker broker;
    private ClearingFee clearingFee;
    private StampDuty stampDuty;

    public String getName() {
        return name;
    }

    public void setBrokerMaximumRate(double maximumRate) {
        SimpleBroker simpleBroker = (SimpleBroker)broker;
        simpleBroker.setMaximumRate(maximumRate);
    }

    public void setBrokerMinimumRate(double minimumRate) {
        SimpleBroker simpleBroker = (SimpleBroker)broker;
        simpleBroker.setMinimumRate(minimumRate);
    }

    public void setBrokerRate(double rate) {
        SimpleBroker simpleBroker = (SimpleBroker)broker;
        simpleBroker.setRate(rate);
    }    

    public double getBrokerMaximumRate() {
        SimpleBroker simpleBroker = (SimpleBroker)broker;
        return simpleBroker.getMaximumRate();
    }

    public double getBrokerMinimumRate() {
        SimpleBroker simpleBroker = (SimpleBroker)broker;
        return simpleBroker.getMinimumRate();
    }

    public double getBrokerRate() {
        SimpleBroker simpleBroker = (SimpleBroker)broker;
        return simpleBroker.getRate();
    }    
    
    public double brokerCalculate(Contract contract) {
        return broker.calculate(contract);
    }
    
    public void setClearingFeeMaximumRate(double maximumRate) {
        SimpleClearingFee simpleClearingFee = (SimpleClearingFee)clearingFee;
        simpleClearingFee.setMaximumRate(maximumRate);
    }

    public void setClearingFeeMinimumRate(double minimumRate) {
        SimpleClearingFee simpleClearingFee = (SimpleClearingFee)clearingFee;
        simpleClearingFee.setMinimumRate(minimumRate);
    }

    public void setClearingFeeRate(double rate) {
        SimpleClearingFee simpleClearingFee = (SimpleClearingFee)clearingFee;
        simpleClearingFee.setRate(rate);
    }    

    public double getClearingFeeMaximumRate() {
        SimpleClearingFee simpleClearingFee = (SimpleClearingFee)clearingFee;
        return simpleClearingFee.getMaximumRate();
    }

    public double getClearingFeeMinimumRate() {
        SimpleClearingFee simpleClearingFee = (SimpleClearingFee)clearingFee;
        return simpleClearingFee.getMinimumRate();
    }

    public double getClearingFeeRate() {
        SimpleClearingFee simpleClearingFee = (SimpleClearingFee)clearingFee;
        return simpleClearingFee.getRate();
    }    
    
    public double clearingFeeCalculate(Contract contract) {
        return clearingFee.calculate(contract);
    }
    
    public void setStampDutyMaximumRate(double maximumRate) {
        SimpleStampDuty simpleStampDuty = (SimpleStampDuty)stampDuty;
        simpleStampDuty.setMaximumRate(maximumRate);
    }

    public void setStampDutyFraction(double fraction) {
        SimpleStampDuty simpleStampDuty = (SimpleStampDuty)stampDuty;
        simpleStampDuty.setFraction(fraction);
    }

    public void setStampDutyRate(double rate) {
        SimpleStampDuty simpleStampDuty = (SimpleStampDuty)stampDuty;
        simpleStampDuty.setRate(rate);
    }    

    public double getStampDutyMaximumRate() {
        SimpleStampDuty simpleStampDuty = (SimpleStampDuty)stampDuty;
        return simpleStampDuty.getMaximumRate();
    }

    public double getStampDutyFraction() {
        SimpleStampDuty simpleStampDuty = (SimpleStampDuty)stampDuty;
        return simpleStampDuty.getFraction();
    }

    public double getStampDutyRate() {
        SimpleStampDuty simpleStampDuty = (SimpleStampDuty)stampDuty;
        return simpleStampDuty.getRate();
    }
    
    public double stampDutyCalculate(Contract contract) {
        return this.stampDuty.calculate(contract);
    } 
    
    public Broker getBroker() {
        SimpleBroker simpleBroker = new SimpleBroker((SimpleBroker)broker);
        return simpleBroker;
    }
    
    public StampDuty getStampDuty() {
        SimpleStampDuty simpleStampDuty = new SimpleStampDuty((SimpleStampDuty)stampDuty);
        return simpleStampDuty;
    }
    
    public ClearingFee getClearingFee() {
        SimpleClearingFee simpleClearingFee = new SimpleClearingFee((SimpleClearingFee)clearingFee);
        return simpleClearingFee;
    }
}
