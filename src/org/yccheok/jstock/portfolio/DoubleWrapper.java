/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.portfolio;

/**
 *
 * @author yccheok
 */
public final class DoubleWrapper {
    public final Double value;
    public final DecimalPlace decimalPlace;
    
    public DoubleWrapper(DecimalPlace decimalPlace, Double value) {
        this.value = value;
        this.decimalPlace = decimalPlace;
    }
}

