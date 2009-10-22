/*
 * SinkOperator.java
 *
 * Created on May 10, 2007, 10:49 PM
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

package org.yccheok.jstock.analysis;

/**
 *
 * @author yccheok
 */
public class SinkOperator extends AbstractOperator {
    
    /** Creates a new instance of SinkOperator */
    public SinkOperator() {
    }
    
    protected Object calculate()
    {
        return this.inputs[0].getValue();
    }
    
    public Object getValue() {
		return this.outputs[0].getValue();
        //return this.inputs[0].getValue();
    }
    
    @Override
    public int getNumOfInputConnector() {
        return 1;
    }

	// It would be best, if SinkOperator can be designed, to have 0 output.
	// As SinkOperator is used to indicator the 'end' of an indicator.
	// However, current design doesn't allow us to do so.
	// Operator figure is designed, to update its displayed value based on
	// property change event. Property change event will only be fired, 
	// when there is an change in the output connector value. Please refer
	// to "connectorValueChange" in AbstractOperator.
	// If we make SinkOperator to have 0 output connector, SinkOperatorFigure
	// will never update its display.
    //@Override
    //public int getNumOfOutputConnector() {
    //    return 0;
    //}

    @Override
    public Class getInputClass(int index) {
        return Boolean.class;
    }

    @Override
    public Class getOutputClass(int index) {
        return Boolean.class;
    }
}
