/*
 * Connector.java
 *
 * Created on May 9, 2007, 11:07 PM
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
public class Connector {
    
    /* For serialization purpose. */
    public Connector() {
        this.operator = null;
        this.value = null;
        this.listeners = new java.util.ArrayList<ConnectorValueChangeListener>();
        this.index = -1;
    }
    
    /** Creates a new instance of Connector */
    public Connector(Operator operator, int index) {
        this.operator = operator;
        this.value = null;
        this.listeners = new java.util.ArrayList<ConnectorValueChangeListener>();
        this.index = index;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        
        // Special case when the value is null.
        if (oldValue == null) {
            if (value != null) {
                ConnectorEvent event = new ConnectorEvent(this);

                for(ConnectorValueChangeListener listerner : listeners)
                    listerner.connectorValueChange(event);                
            }
            
            return;
        }
        
        if(oldValue.equals(value) == false)
        {
            ConnectorEvent event = new ConnectorEvent(this);
            
            for(ConnectorValueChangeListener listerner : listeners)
                listerner.connectorValueChange(event);
        }
    }
    
    public Operator getOperator() {
        return operator;
    }

    public void addConnectorValueChangeListener(ConnectorValueChangeListener connectorValueChangeLister) {
        if(listeners.contains(connectorValueChangeLister))
            return;
        
        listeners.add(connectorValueChangeLister);
    }
    
    public void removeConnectorValueChangeListener(ConnectorValueChangeListener connectorValueChangeLister) {
        listeners.remove(connectorValueChangeLister);
    }

    public int getIndex() {
        return this.index;
    }
    
    private final Operator operator;
    private Object value;
    private final int index;    // The index associated with the operator.
    private java.util.List<ConnectorValueChangeListener> listeners;
}
