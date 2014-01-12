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

import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class FunctionOperator extends AbstractOperator {
    public enum Function
    {
        Absolute,
        Square,
    }
    
    public FunctionOperator() {
        super();
        function = Function.Absolute;
    }
    
    @Override
    protected Object calculate() {
        Double result = null;
        switch (function) 
        {
        case Absolute:
            result = absolute();
            break;    
        case Square:
            result = square();
            break;  
        default:
            assert(false);
        }
        
        return result;            
    }

    private Double absolute() {
        Object object0 = inputs[0].getValue();
        
        try {
            BigDecimal d0 = new BigDecimal(object0.toString());
            
            return d0.abs().doubleValue();
        } catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        
        return null;
    }
    
    private Double square() {
        Object object0 = inputs[0].getValue();
        
        try {
            BigDecimal d0 = new BigDecimal(object0.toString());
            
            return d0.pow(2).doubleValue();
        } catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        
        return null;
    }
    
    public void setFunction(Function function) {
        Function old = this.function;
        this.function = function;
        
        if(old != this.function) {
            this.firePropertyChange("attribute", old, this.function);
        }        
    }
    
    public FunctionOperator.Function getFunction() {
        return function;
    }
    
    @Override
    public int getNumOfInputConnector() {
        return 1;
    }

    @Override
    public Class getInputClass(int index) {
        return Double.class;
    }

    @Override
    public Class getOutputClass(int index) {
        return Double.class;
    }
    
    private FunctionOperator.Function function;
    private static final Log log = LogFactory.getLog(FunctionOperator.class);    
    
}
