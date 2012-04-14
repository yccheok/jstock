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
public class LogicalOperator extends AbstractOperator {
    
    public enum Logical
    {
        And,
        Or
    }
    
    /** Creates a new instance of LogicalOperator */
    public LogicalOperator() {
        this.logical = Logical.And;
    }
    
    @Override
    protected Object calculate()
    {
        Object object0 = inputs[0].getValue();
        Object object1 = inputs[1].getValue();
        
        if(
                ((object0 instanceof Boolean) == false) ||
                ((object1 instanceof Boolean) == false)
        )
            return null;
        
        boolean b0 = (Boolean)object0;
        boolean b1 = (Boolean)object1;

        switch(logical) 
        {
            case And:
                return Boolean.valueOf(b0 && b1);

            case Or:
                return Boolean.valueOf(b0 || b1);
                
            default:
                assert(false);
        }
        
        return null; 
    }
        
    @Override
    public int getNumOfInputConnector() {
        return 2;
    }    
    
    public Logical getLogical() {
        return logical;
    }
    
    public void setLogical(Logical logical) {
        Logical old = this.logical;
        this.logical = logical;
        
        if(old != this.logical) {
            this.firePropertyChange("attribute", old, this.logical);
        }        
    }
    
    @Override
    public Class getInputClass(int index) {
        return Boolean.class;
    }

    @Override
    public Class getOutputClass(int index) {
        return Boolean.class;
    }

    private Logical logical;
    private static final Log log = LogFactory.getLog(LogicalOperator.class);    
}