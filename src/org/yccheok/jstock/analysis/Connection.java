/*
 * Connection.java
 *
 * Created on May 9, 2007, 10:40 PM
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
public class Connection implements ConnectorValueChangeListener, org.jhotdraw.xml.DOMStorable {
    
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
    
    public void connectorValueChange(ConnectorEvent evt)
    {
        if(output != null)
            output.setValue(evt.getSource().getValue());
    }
    
    public void write(org.jhotdraw.xml.DOMOutput out) throws java.io.IOException {
        out.openElement("input");
        out.writeObject(input);
        out.closeElement();
        
        out.openElement("output");
        out.writeObject(output);
        out.closeElement(); 
    }

    public void read(org.jhotdraw.xml.DOMInput in) throws java.io.IOException {
        in.openElement("input");
        input = (Connector)in.readObject();
        in.closeElement();
        
        in.openElement("output");
        output = (Connector)in.readObject();
        in.closeElement();        
    }
    
    private Connector input;
    private Connector output;
}
