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
 * @author Owner
 */
public class SimpleStampDuty implements StampDuty {
    
    public SimpleStampDuty(double maximumRate, double fraction, double rate) { 
        this.maximumRate = maximumRate;
        this.fraction = fraction;
        this.rate = rate;        
    }
    
    @Deprecated
    public SimpleStampDuty(String name, double maximumRate, double fraction, double rate) {        
        this.name = name;
        this.maximumRate = maximumRate;
        this.fraction = fraction;
        this.rate = rate;
    }
    
    public SimpleStampDuty(SimpleStampDuty simpleStampDuty) {
        this.name = simpleStampDuty.getName();
        this.maximumRate = simpleStampDuty.getMaximumRate();
        this.fraction = simpleStampDuty.getFraction();
        this.rate = simpleStampDuty.getRate();
    }
    
    @Override
    public double calculate(Contract contract) {
        if (fraction <= 0.0) {
            return 0.0;
        }
        
        int numOfFraction = (int)(contract.getTotal() / fraction);
        double remainder = contract.getTotal() - (numOfFraction * fraction);
        
        double total = rate * (double)numOfFraction;
        if (remainder > 0.0) {
            total += (double)rate;
        }
        
        // 0 in maximum rate means ignore.
        if (false == Utils.essentiallyEqual(this.maximumRate, 0.0)) {
            if (total > this.maximumRate) {
                return this.maximumRate;
            }
        }
        
        return total;
    }

    private double maximumRate;
    private double fraction;
    private double rate;
    @Deprecated
    private transient String name;
    
    public double getMaximumRate() {
        return maximumRate;
    }

    public double getFraction() {
        return fraction;
    }

    public double getRate() {
        return rate;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    public void setMaximumRate(double maximumRate) {
        this.maximumRate = maximumRate;
    }


    public void setFraction(double fraction) {
        this.fraction = fraction;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
