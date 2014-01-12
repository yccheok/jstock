/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2014 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author yccheok
 */
public class ArithmeticOperator extends AbstractOperator {

    public enum Arithmetic
    {
        Addition,
        Subtraction,
        Multiplication,
        Division
    }
    
    /** Creates a new instance of ConstantOperand */
    public ArithmeticOperator() {
        super();
        arithmetic = Arithmetic.Addition;
    }
        
    @Override
    protected Object calculate()
    {
        Double result = null;
        
        switch (arithmetic) 
        {
        case Addition:
            result = addition();
            break;    
        case Subtraction:
            result = subtraction();
            break; 
        case Multiplication:
            result = multiplication();
            break; 
        case Division:
            result = division();
            break; 
        default:
            assert(false);
        }
        
        return result;
    }
    
    private Double addition() {
        Object object0 = inputs[0].getValue();
        Object object1 = inputs[1].getValue();
        
        try {
            BigDecimal d0 = new BigDecimal(object0.toString());
            BigDecimal d1 = new BigDecimal(object1.toString());
            
            BigDecimal result = d0.add(d1);
            return result.doubleValue();
        }
        catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        
        return null;
    }
    
    private Double subtraction() {
        Object object0 = inputs[0].getValue();
        Object object1 = inputs[1].getValue();
        
        try {
            BigDecimal d0 = new BigDecimal(object0.toString());
            BigDecimal d1 = new BigDecimal(object1.toString());
            
            BigDecimal result = d0.subtract(d1);
            return result.doubleValue();
        } catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        
        return null;        
    }
    
    private Double multiplication() {
        Object object0 = inputs[0].getValue();
        Object object1 = inputs[1].getValue();
        
        try {
            BigDecimal d0 = new BigDecimal(object0.toString());
            BigDecimal d1 = new BigDecimal(object1.toString());
            
            BigDecimal result = d0.multiply(d1);
            return result.doubleValue();
        } catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        
        return null;        
    }
    
    private Double division() {
        Object object0 = inputs[0].getValue();
        Object object1 = inputs[1].getValue();
        
        try {
            BigDecimal d0 = new BigDecimal(object0.toString());
            BigDecimal d1 = new BigDecimal(object1.toString());
            
            final double d1Value = d1.doubleValue();
            if(d1Value != 0.0) {
                BigDecimal result = d0.divide(d1, MathContext.DECIMAL64);
                return result.doubleValue();
            }
        } catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        
        return null;        
    }    

    public void setArithmetic(Arithmetic arithmetic) {
        Arithmetic old = this.arithmetic;
        this.arithmetic = arithmetic;
        
        if(old != this.arithmetic) {
            this.firePropertyChange("attribute", old, this.arithmetic);
        }        
    }
    
    public Arithmetic getArithmetic() {
        return arithmetic;
    }
    
    @Override
    public int getNumOfInputConnector() {
        return 2;
    }
    
    @Override
    public Class getInputClass(int index) {
        return Double.class;
    }

    @Override
    public Class getOutputClass(int index) {
        return Double.class;
    }

    private Arithmetic arithmetic;
    private static final Log log = LogFactory.getLog(ArithmeticOperator.class);    
}
