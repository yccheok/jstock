/*
 * @(#)DependencyFigure.java  1.0  18. Juni 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.yccheok.jstock.gui.analysis;

import org.yccheok.jstock.gui.*;
import java.awt.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.*;

/**
 * DependencyFigure.
 *
 * @author Werner Randelshofer.
 * @version 1.0 18. Juni 2006 Created.
 */
public class DependencyFigure extends LineConnectionFigure {
    /** Creates a new instance. */
    public DependencyFigure() {
        STROKE_COLOR.set(this, new Color(0x000099));
        STROKE_WIDTH.set(this, 1d);
        END_DECORATION.set(this, new ArrowTip());
        
        setAttributeEnabled(END_DECORATION, false);
        setAttributeEnabled(START_DECORATION, false);
        setAttributeEnabled(STROKE_DASHES, false);
        setAttributeEnabled(FONT_ITALIC, false);
        setAttributeEnabled(FONT_UNDERLINE, false);
    }
        
    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     */
    protected void handleDisconnect(Connector start, Connector end) {        
        final IndexedConnector indexedStart = (IndexedConnector)start;
        final IndexedConnector indexedEnd = (IndexedConnector)end;
        indexedStart.setNumOfConnection(0);
        indexedEnd.setNumOfConnection(0);

        // We will have problem if we keep track the of connection using the following methods
        // Imagine we connect a figure, serialize it, deserialize it, disconnect again
        // 1) Connect a figure, the number of connection for the connector is 1.
        // 2) Serialization. The number of connector is saved as 1.
        // 3) Deserialization. The number of connector is restored as 1.
        // 4) However, handleConnect event will be called. The number of connector is increased to 2. <-- Is handleConnect guarantee to be called?
        // 
        // Currently, we will just assume the maximum connection per connector is 1.
        // 
        // indexedStart.setNumOfConnection(indexedStart.getNumOfConnection() - 1);
        // indexedEnd.setNumOfConnection(indexedEnd.getNumOfConnection() - 1);

        if(start instanceof InputConnector) {
            System.out.println("DependencyFigure handleDisconnect start InputConnector indexed " + ((InputConnector)start).getIndex());
        }
        else {
            System.out.println("DependencyFigure handleDisconnect start OutputConnector indexed " + ((OutputConnector)start).getIndex());            
        }

        if(end instanceof InputConnector) {
            System.out.println("DependencyFigure handleDisconnect end InputConnector indexed " + ((InputConnector)end).getIndex());
        }
        else {
            System.out.println("DependencyFigure handleDisconnect end OutputConnector indexed " + ((OutputConnector)end).getIndex());            
        } 
        
        final OperatorFigure startOperatorFigure = (OperatorFigure)start.getOwner();
        final OperatorFigure endOperatorFigure = (OperatorFigure)end.getOwner();

        startOperatorFigure.getOperator().removeOutputConnection(indexedStart.getIndex());
        endOperatorFigure.getOperator().removeInputConnection(indexedEnd.getIndex());
    }
    
    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     */
    protected void handleConnect(Connector start, Connector end) {
        final IndexedConnector indexedStart = (IndexedConnector)start;
        final IndexedConnector indexedEnd = (IndexedConnector)end;
        indexedStart.setNumOfConnection(1);
        indexedEnd.setNumOfConnection(1);
        
        // We will have problem if we keep track the of connection using the following methods
        // Imagine we connect a figure, serialize it, deserialize it, disconnect again
        // 1) Connect a figure, the number of connection for the connector is 1.
        // 2) Serialization. The number of connector is saved as 1.
        // 3) Deserialization. The number of connector is restored as 1.
        // 4) However, handleConnect event will be called. The number of connector is increased to 2. <-- Is handleConnect guarantee to be called?
        // 
        // Currently, we will just assume the maximum connection per connector is 1.
        // 
        // indexedStart.setNumOfConnection(indexedStart.getNumOfConnection() + 1);
        // indexedEnd.setNumOfConnection(indexedEnd.getNumOfConnection() + 1);
        
        if(start instanceof InputConnector) {
            System.out.println("DependencyFigure handleConnect start InputConnector indexed " + ((InputConnector)start).getIndex());
        }
        else {
            System.out.println("DependencyFigure handleConnect start OutputConnector indexed " + ((OutputConnector)start).getIndex());            
        }

        if(end instanceof InputConnector) {
            System.out.println("DependencyFigure handleConnect end InputConnector indexed " + ((InputConnector)end).getIndex());
        }
        else {
            System.out.println("DependencyFigure handleConnect end OutputConnector indexed " + ((OutputConnector)end).getIndex());            
        }
        
        final OperatorFigure startOperatorFigure = (OperatorFigure)start.getOwner();
        final OperatorFigure endOperatorFigure = (OperatorFigure)end.getOwner();
        
        org.yccheok.jstock.analysis.Connection connection = new org.yccheok.jstock.analysis.Connection();
        startOperatorFigure.getOperator().addOutputConnection(connection, indexedStart.getIndex());
        endOperatorFigure.getOperator().addInputConnection(connection, indexedEnd.getIndex());
    }
    
    @Override
    public DependencyFigure clone() {
        DependencyFigure that = (DependencyFigure) super.clone();
        
        return that;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean canConnect(Connector start, Connector end) {
        if (start instanceof InputConnector || end instanceof OutputConnector) {
            return false;
        }        

        if (start instanceof IndexedConnector == false || end instanceof IndexedConnector == false) {
            return false;
        }
        
        final IndexedConnector indexedStart = (IndexedConnector)start;
        final IndexedConnector indexedEnd = (IndexedConnector)end;

        /*
        Fix on the following issues : When try to connect an output connector to input
        connector of a figure A. Then try to move the connection line to figure A's
        output connector, success. However, we shall not allow output connector to be
        connected to another output connector.
        */
        if (indexedStart.getNumOfConnection() > 0) {
            if (this.getStartConnector() != indexedStart) {
                return false;
            }
        }

        if (indexedEnd.getNumOfConnection() > 0) {
            if (this.getEndConnector() != indexedEnd) {
                return false;
            }
        }
        
        Figure startFigure = start.getOwner();
        Figure endFigure = end.getOwner();
        
        if (((startFigure instanceof OperatorFigure) == false) || ((endFigure instanceof OperatorFigure) == false))
        {
            return false;
        }
        
        OperatorFigure startOperatorFigure = (OperatorFigure)startFigure;
        OperatorFigure endOperatorFigure = (OperatorFigure)endFigure;
        final int startIndex = indexedStart.getIndex();
        final int endIndex = indexedEnd.getIndex();
        final Class outputClass = startOperatorFigure.getOperator().getOutputClass(startIndex);
        final Class inputClass = endOperatorFigure.getOperator().getInputClass(endIndex);

        // Ensure there is no casting expection.
        //      class A {}
        //      class B extends A {}
        //      class C extends B {}
        //      class D {}
        //      Class a0 = A.class;
        //      Class c0 = C.class;
        //      Class d0 = D.class;
        //
        //      System.out.println(c0.isAssignableFrom(a0));    // print false.
        //      System.out.println(a0.isAssignableFrom(c0));    // print true.
        //
        //      System.out.println(d0.isAssignableFrom(a0));    // print false.
        //      System.out.println(a0.isAssignableFrom(d0));    // print false.
        return (outputClass.isAssignableFrom(inputClass) || inputClass.isAssignableFrom(outputClass));
    }
    
    @Override
    public int getLayer() {
        return 1;
    }
    
    @Override public void removeNotify(Drawing d) {
        super.removeNotify(d);
    }
}
