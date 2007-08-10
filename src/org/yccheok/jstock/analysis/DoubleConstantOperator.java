/*
 * ConstantOperand.java
 *
 * Created on May 10, 2007, 12:19 AM
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

/**
 *
 * @author yccheok
 */
public class DoubleConstantOperator extends AbstractOperator implements Operator, org.jhotdraw.xml.DOMStorable {
    
    /** Creates a new instance of ConstantOperator */
    public DoubleConstantOperator() {
        super();
        constant = 0.0;
    }
    
    public void setConstant(Double constant) {
        Double old = this.constant;
        this.constant = constant;
        
        if(Utils.equals(old, constant) == false) {
            this.firePropertyChange("value", old, this.constant);
        }       
    }
    
    public Double getConstant() {
        return constant;
    }
    
    protected Object calculate()
    {
        return constant;
    }
    
    public int getNumOfInputConnector() {
        return 0;
    }
    
    public void write(org.jhotdraw.xml.DOMOutput out) throws java.io.IOException {
        super.write(out);
        
        out.openElement("constant");
        out.writeObject(constant);
        out.closeElement();
    }

    public void read(org.jhotdraw.xml.DOMInput in) throws java.io.IOException {
        super.read(in);
        
        in.openElement("constant");
        constant = (Double)in.readObject();
        in.closeElement();
    }
    
    private Double constant;
}
