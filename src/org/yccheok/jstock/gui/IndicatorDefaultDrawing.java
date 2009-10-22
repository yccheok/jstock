/*
 * AlertDefaultDrawing.java
 *
 * Created on May 20, 2007, 1:29 PM
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

package org.yccheok.jstock.gui;

import org.yccheok.jstock.gui.analysis.OperatorFigure;
import org.jhotdraw.draw.*;
import java.util.*;
import java.io.*;
import org.jhotdraw.undo.UndoRedoManager;
import org.yccheok.jstock.analysis.*;


/**
 *
 * @author yccheok
 */
public class IndicatorDefaultDrawing extends org.jhotdraw.draw.DefaultDrawing {
    /* Used to detect unsaved changes. */
    private final UndoRedoManager undoRedoManager = new UndoRedoManager();

    /** Creates a new instance of AlertDefaultDrawing */
    public IndicatorDefaultDrawing() {
        DOMStorableInputOutputFormat ioFormat =
                new DOMStorableInputOutputFormat(new IndicatorDOMFactory());
        LinkedList<InputFormat> inputFormats = new LinkedList<InputFormat>();
        inputFormats.add(ioFormat);
        this.setInputFormats(inputFormats);
        LinkedList<OutputFormat> outputFormats = new LinkedList<OutputFormat>();
        outputFormats.add(ioFormat);
        this.setOutputFormats(outputFormats);

        // To detect unsaved changes.
        this.addUndoableEditListener(undoRedoManager);
    }
    
    // Return an OperatorIndicator with name "null".
    public OperatorIndicator getOperatorIndicator() {
        OperatorIndicator operatorIndicator = new OperatorIndicator();

        List<Figure> figures = this.getChildren();
        for (Figure figure : figures) {
            if(figure instanceof OperatorFigure) {
                OperatorFigure operatorFigure = (OperatorFigure)figure;
                operatorIndicator.add(operatorFigure.getOperator());
            }
        }
        
        return operatorIndicator;
    }
    
    /**
     * Writes the project to the specified file.
     */
    public void write(String projectName, String jHotDrawFilename, String operatorIndicatorFilename) throws IOException {
        File jHotdrawFile = new File(jHotDrawFilename);
        
        OperatorIndicator operatorIndicator = getOperatorIndicator();
        operatorIndicator.setName(projectName);
        
        OutputFormat outputFormat = this.getOutputFormats().get(0);
        outputFormat.write(jHotdrawFile, this);

        org.yccheok.jstock.gui.Utils.toXML(operatorIndicator, operatorIndicatorFilename);

        // To flag no more unsaved changes in this drawing.
        undoRedoManager.setHasSignificantEdits(false);
    }

    public boolean hasSignificantEdits() {
        return undoRedoManager.hasSignificantEdits();
    }

    /**
     * Reads the project from the specified file.
     */
    public void read(String jHotDrawFilename, String operatorIndicatorFilename) throws IOException {
        File jHotdrawFile = new File(jHotDrawFilename);
        File xStreamFile = new File(operatorIndicatorFilename);
        
        InputFormat inputFormat = this.getInputFormats().get(0);
        inputFormat.read(jHotdrawFile, this);

        OperatorIndicator operatorIndicator = org.yccheok.jstock.gui.Utils.fromXML(OperatorIndicator.class, xStreamFile);
        if (operatorIndicator == null) {
            throw new IOException();
        }
        
        List<Figure> figures = this.getChildren();
        int counter = 0;
        for (Figure f : figures) {
            if (f instanceof OperatorFigure) {
                final Operator operator = operatorIndicator.get(counter);
                OperatorFigure operatorFigure = (OperatorFigure)f;
                operatorFigure.setOperator(operator);
                
                // Property listener are not being serialized. We need to restore them manually.
                ((AbstractOperator)operator).addPropertyChangeListener(operatorFigure);
                
                counter++;
            }
        }                
    }    
}
