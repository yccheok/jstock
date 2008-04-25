/*
 * PlusOperand.java
 *
 * Created on May 10, 2007, 12:24 AM
 *
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
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.analysis;

import java.math.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
        
    protected Object calculate()
    {
        Double result = null;
        
        switch(arithmetic) 
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
        catch(NumberFormatException exp) {
            exp.printStackTrace();
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
        }
        catch(NumberFormatException exp) {
            exp.printStackTrace();
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
        }
        catch(NumberFormatException exp) {
            exp.printStackTrace();
        }
        
        return null;        
    }
    
    private Double division() {
        Object object0 = inputs[0].getValue();
        Object object1 = inputs[1].getValue();
        
        try {
            BigDecimal d0 = new BigDecimal(object0.toString());
            BigDecimal d1 = new BigDecimal(object1.toString());
            
            BigDecimal result = null;
            
            final double d1Value = d1.doubleValue();
            if(d1Value != 0.0) {
                result = d0.divide(d1, MathContext.DECIMAL64);
            }
            return result.doubleValue();
        }
        catch(NumberFormatException exp) {
            log.error("", exp);
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
    
    public int getNumOfInputConnector() {
        return 2;
    }
    
    public void write(org.jhotdraw.xml.DOMOutput out) throws java.io.IOException {
        super.write(out);
        
        out.openElement("Arithmetic");
        out.writeObject(arithmetic);
        out.closeElement();
    }

    public void read(org.jhotdraw.xml.DOMInput in) throws java.io.IOException {
        super.read(in);
        
        in.openElement("Arithmetic");
        arithmetic = (Arithmetic)in.readObject();
        in.closeElement();
    }
    
    private Arithmetic arithmetic;
    private static final Log log = LogFactory.getLog(ArithmeticOperator.class);    
}
