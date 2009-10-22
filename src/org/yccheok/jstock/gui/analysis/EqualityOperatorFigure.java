/*
 * LogicalOperatorFigure.java
 *
 * Created on May 26, 2007, 2:03 PM
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
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui.analysis;

import org.yccheok.jstock.analysis.*;

/**
 *
 * @author yccheok
 */
public class EqualityOperatorFigure extends OperatorFigure {
    
    /** Creates a new instance of StockOperatorFigure */
    public EqualityOperatorFigure() {
        super(new EqualityOperator());
        this.setName("Equality");
        this.setAttribute(((EqualityOperator)this.getOperator()).getEquality().toString());
        this.setValue("");        
    }
    
}
