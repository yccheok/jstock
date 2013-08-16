/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.portfolio;

/**
 *
 * @author yccheok
 */
public final class DoubleWrapper implements Comparable<DoubleWrapper> {
    public final Double value;
    public final DecimalPlaces decimalPlace;
    
    public DoubleWrapper(DecimalPlaces decimalPlace, Double value) {
        this.value = value;
        this.decimalPlace = decimalPlace;
    }

    @Override
    public int compareTo(DoubleWrapper o) {
        return value.compareTo(o.value);
    }

}

