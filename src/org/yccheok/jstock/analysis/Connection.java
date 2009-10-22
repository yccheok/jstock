/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

/**
 *
 * @author yccheok
 */
public class Connection implements ConnectorValueChangeListener {
    
    /** Creates a new instance of Connection */
    public Connection() {
        input = null;
        output = null;
    }
    
    public void setInputConnector(Connector connector)
    {
        input = connector;                
    }
    
    public void setOutputConnector(Connector connector)
    {
        output = connector;
    }
    
    public Connector getInputConnector() {
        return input;
    }

    public Connector getOutputConnector() {
        return output;
    }
    
    @Override
    public void connectorValueChange(ConnectorEvent evt)
    {
        if (output != null)
            output.setValue(evt.getSource().getValue());
    }
    
    private Connector input;
    private Connector output;
}
