/*
 * Operand.java
 *
 * Created on May 8, 2007, 10:00 PM
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
public interface Operator extends ConnectorValueChangeListener {
    public int getNumOfInputConnector();
    public int getNumOfOutputConnector();
    public int getNumOfInputConnection();
    public int getNumOfOutputConnection();
    
    public boolean addInputConnection(Connection connection, int index);
    public boolean removeInputConnection(Connection connection);
    public boolean addOutputConnection(Connection connection, int index);
    public boolean removeOutputConnection(Connection connection);
    public boolean removeInputConnection(int index);
    public boolean removeOutputConnection(int index);

    public Class getInputClass(int index);
    public Class getOutputClass(int index);

    // Try to refresh the input connectors. Once the input connectors
    // are refreshed, ConnectorValueChangeListeners will be triggered. The
    // changes will be propagated through the affected network.
    public boolean pull();
    
    // Try to clear both the input and output connectors. Once the input 
    // and output connectors are cleared, ConnectorValueChangeListeners will be 
    // triggered. The changes will be propagated through the whole network.    
    public void clear();    
}
