/*
 * LogicalOperator.java
 *
 * Created on May 26, 2007, 2:20 PM
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
                return new Boolean(b0 && b1);

            case Or:
                return new Boolean(b0 || b1);
                
            default:
                assert(false);
        }
        
        return null; 
    }
        
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
    
    private Logical logical;
    private static final Log log = LogFactory.getLog(LogicalOperator.class);    
}