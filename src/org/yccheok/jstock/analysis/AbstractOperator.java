/*
 * AbstactOperand.java
 *
 * Created on May 9, 2007, 11:18 PM
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
public abstract class AbstractOperator extends AbstractBean implements Operator {
    
    /** Creates a new instance of AbstactOperand */
    public AbstractOperator() {
        outputs = new Connector[getNumOfOutputConnector()];
        inputs = new Connector[getNumOfInputConnector()];
        outputConnections = new Connection[getNumOfOutputConnector()];
        inputConnections = new Connection[getNumOfInputConnector()];
        outputConnectionSize = 0;
        inputConnectionSize = 0;
        
        for (int i = 0; i < getNumOfOutputConnector(); i++) {
            outputs[i] = new Connector(this, i);
            outputConnections[i] = null;
        }

        for (int i = 0; i < getNumOfInputConnector(); i++) {
            inputs[i] = new Connector(this, i);
            // We will be interested in all the input event.
            inputs[i].addConnectorValueChangeListener(this);
            inputConnections[i] = null;
        }
        
        loopbackflag = false;
    }
    
    @Override
    public void connectorValueChange(ConnectorEvent evt)
    {
        if (isInputReady() == false) {
            // The input is not valid. Update the output to relect on this fact.
            for(Connector output : outputs) {
                Object old = output.getValue();
                output.setValue(null);
                
                if(Utils.equals(old, output.getValue()) == false)
                    this.firePropertyChange("value", old, output.getValue());
            }
            return;
        }
        
        Object newValue = this.calculate();
        
        // All the inputs are valid. Perform calculation and forward the result
        // to output.
        for (Connector output : outputs) {
            Object old = output.getValue();
            output.setValue(newValue);
            
            if (Utils.equals(old, output.getValue()) == false) {
                this.firePropertyChange("value", old, output.getValue());
            }
        }
    }
    
    @Override
    public int getNumOfOutputConnector() { return 1; }
    @Override
    public int getNumOfOutputConnection() { return outputConnectionSize; }
    @Override
    public int getNumOfInputConnection() { return inputConnectionSize; }
    
    @Override
    public boolean addInputConnection(Connection connection, int index) {
        if (index >= inputConnections.length)
            return false;
        
        if (inputConnections[index] != null)
            return false;
        
        inputConnections[index] = connection;
        
        inputConnections[index].setOutputConnector(inputs[index]);
        
        inputConnectionSize++;
        
        return true;
    }
    
    @Override
    public boolean removeInputConnection(int index) {
        inputConnections[index].setOutputConnector(null);
        inputConnections[index] = null;
        inputConnectionSize--;
        
        return true;
    }
    
    @Override
    public boolean removeOutputConnection(int index) {
        outputConnections[index].setInputConnector(null);
        outputConnections[index] = null;
        outputConnectionSize--;
        // Don't forget to remove the listener as well.
        outputs[index].removeConnectorValueChangeListener(outputConnections[index]);
        return true;        
    }
    
    @Override
    public boolean removeInputConnection(Connection connection) {
        final int length = inputConnections.length;
        
        for(int i = 0; i < length; i++) {
            if(inputConnections[i] == connection) {
                return removeInputConnection(i);
            }
        }
        
        return false;
    }
    
    @Override
    public boolean addOutputConnection(Connection connection, int index)
    {
        if(index >= outputConnections.length)
            return false;
        
        if(outputConnections[index] != null)
            return false;
        
        outputConnections[index] = connection;
        
        outputConnections[index].setInputConnector(outputs[index]);
        
        outputConnectionSize++;
        
        // Connection will be interested in the output event.
        outputs[index].addConnectorValueChangeListener(outputConnections[index]);
        
        return true;        
    }
    
    @Override
    public boolean removeOutputConnection(Connection connection)
    {        
        final int length = outputConnections.length;
        
        for(int i = 0; i < length; i++) {
            if (outputConnections[i] == connection) {
                return removeOutputConnection(i);
            }
        }
        
        return false;
    }
    
    // Try to put value into input connectors.
    @Override
    public boolean pull()
    {
        // Avoid loop back connection.
        if (loopbackflag)
            return false;
        
        if (inputs.length == 0) {
            outputs[0].setValue(this.calculate());
            return true;
        }
        
        final int length = inputs.length;
     
        boolean status = true;
        
        // First, ensure our input connectors are fill with value.
        for (int i = 0; i < length; i++) {
            // No connection. Nothing we can do. Just raise a false flag.
            if(inputConnections[i] == null)  {
                status = false;
                break;
            }
            
            // We have connection. But no input connector source. Just raise a false
            // flag.
            if (inputConnections[i].getInputConnector() == null) {
                status = false;
                break;              
            }
            
            loopbackflag = true;
            if (inputConnections[i].getInputConnector().getOperator().pull() == false)
            {
                status = false;
                break;
            }                    
        }
                
        loopbackflag = false;
        
        return status;
    }
    
    @Override
    public void clear()
    {
        for(Connector input : inputs) {
            input.setValue(null);
        }
        
        for(Connector output : outputs) {
            output.setValue(null);
        }        
    }
    
    private boolean isInputReady() {
        for(Connector input : inputs)
            if(input.getValue() == null)
                return false;
        
        return true;
    }
    
    @Override
    public String toString() {
        final java.lang.StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getName() + " [ input = ");
        for(Connector input : inputs) {
            builder.append(input.getValue() + " ");
        }
        builder.append("] [ output = ");
        for(Connector output : outputs) {
            builder.append(output.getValue() + " ");
        }    
        builder.append("] [ inputConnectionSize = " + inputConnectionSize + " ]");
        builder.append(" [ outputConnectionSize = " + outputConnectionSize + " ]");
        
        return builder.toString();
    }
    
    // This function is expecting all the input value during that time is valid.
    protected abstract Object calculate();
    
    protected Connector[] outputs;
    protected Connector[] inputs;
    protected Connection[] outputConnections;
    protected Connection[] inputConnections;
    protected int outputConnectionSize;
    protected int inputConnectionSize;
    private boolean loopbackflag;
}
