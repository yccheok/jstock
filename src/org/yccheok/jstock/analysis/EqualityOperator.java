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

package org.yccheok.jstock.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author yccheok
 */
public class EqualityOperator extends AbstractOperator {
    
    public enum Equality
    {
        Equal,
        Greater,
        Lesser,
        GreaterOrEqual,
        LesserOrEqual        
    }
    
    /** Creates a new instance of LogicalOperator */
    public EqualityOperator() {
        this.equality = Equality.Equal;
    }
    
    @Override
    protected Object calculate()
    {
        Object object0 = inputs[0].getValue();
        Object object1 = inputs[1].getValue();
        
        try {
            Double d0 = Double.parseDouble(object0.toString());
            Double d1 = Double.parseDouble(object1.toString());

            int result = d0.compareTo(d1);
            
            switch(equality) 
            {
                case Equal:
                    return Boolean.valueOf(result == 0);
                    
                case Greater:
                    return Boolean.valueOf(result > 0);
                    
                case Lesser:
                    return Boolean.valueOf(result < 0);
                    
                case GreaterOrEqual:
                    return Boolean.valueOf(result >= 0);
                    
                case LesserOrEqual:
                    return Boolean.valueOf(result <= 0);
                    
                default:
                    assert(false);
            }

        }
        catch (NumberFormatException exp) {
            log.error(null, exp);
        }
        
        return null; 
    }
        
    @Override
    public int getNumOfInputConnector() {
        return 2;
    }    
    
    public Equality getEquality() {
        return equality;
    }
    
    public void setEquality(Equality equality) {
        Equality old = this.equality;
        this.equality = equality;
        
        if(old != this.equality) {
            this.firePropertyChange("attribute", old, this.equality);
        }        
    }
    
    @Override
    public Class getInputClass(int index) {
        return Double.class;
    }

    @Override
    public Class getOutputClass(int index) {
        return Boolean.class;
    }

    private Equality equality;
    private static final Log log = LogFactory.getLog(EqualityOperator.class);    
}
