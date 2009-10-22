/*
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
public class DiodeOperator extends AbstractOperator {
    @Override
    protected Object calculate() {
        // The main characteristic of diode. Output will always same as input.
        return inputs[0].getValue();
    }

    @Override
    public int getNumOfInputConnector() {
        return 1;
    }

    @Override
    public Class getInputClass(int index) {
        synchronized(loopbackflag_monitor) {
            // If we connect in this way
            // Diode -> Diode
            // We will get stack overflow exception due to endless recuirsive call
            // Use loopbackflag to prevent this situation from happening.
            if (loopbackflag) {
                loopbackflag = false;
                return Object.class;
            }

            loopbackflag = true;

            final Connection inputConnection = this.inputConnections[index];
            final Connection outputConnection = this.outputConnections[index];
            Class c0 = Object.class;
            Class c1 = Object.class;
            if (inputConnection != null) {
                final Connector connector = inputConnection.getInputConnector();
                c0 = connector.getOperator().getOutputClass(connector.getIndex());
            }
            if (outputConnection != null) {
                final Connector connector = outputConnection.getOutputConnector();
                c1 = connector.getOperator().getInputClass(connector.getIndex());
            }

            // Object means can connect to anyone.
            this.loopbackflag = false;
            return c0.equals(Object.class) ? c1 : c0;
        }
    }

    @Override
    public Class getOutputClass(int index) {
        // The main characteristic of diode. Output will always same as input.
        return this.getInputClass(index);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        String old = this.description;
        this.description = description;
        if (old.equals(this.description) == false) {
            this.firePropertyChange("attribute", old, this.description);
        }
    }

    private boolean loopbackflag = false;
    private final Object loopbackflag_monitor = new Object();

    private String description = "Description";
}
