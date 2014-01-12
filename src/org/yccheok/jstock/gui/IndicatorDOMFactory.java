/*
 * @(#)PertFactory.java  1.0  2006-01-18
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
package org.yccheok.jstock.gui;

import org.yccheok.jstock.gui.analysis.StockOperatorFigure;
import org.yccheok.jstock.gui.analysis.EqualityOperatorFigure;
import org.yccheok.jstock.gui.analysis.StockHistoryOperatorFigure;
import org.yccheok.jstock.gui.analysis.LogicalOperatorFigure;
import org.yccheok.jstock.gui.analysis.StockRelativeHistoryOperatorFigure;
import org.yccheok.jstock.gui.analysis.DoubleConstantOperatorFigure;
import org.yccheok.jstock.gui.analysis.SeparatorLineFigure;
import org.yccheok.jstock.gui.analysis.SinkOperatorFigure;
import org.yccheok.jstock.gui.analysis.DependencyFigure;
import org.yccheok.jstock.gui.analysis.ArithmeticOperatorFigure;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;
import org.yccheok.jstock.analysis.*;
import org.yccheok.jstock.gui.analysis.DiodeOperatorFigure;
import org.yccheok.jstock.gui.analysis.FunctionOperatorFigure;

/**
 * PertFactory.
 * 
 * @author Werner Randelshofer
 * @version 2006-01-18 Created.
 */
public class IndicatorDOMFactory extends DefaultDOMFactory {
    private final static Object[][] classTagArray = {
        { DefaultDrawing.class, "PertDiagram" },
        { StockOperatorFigure.class, "stockOperatorFigure" },
        { StockHistoryOperatorFigure.class, "stockHistoryOperatorFigure" }, 
        { StockRelativeHistoryOperatorFigure.class, "stockRelativeHistoryOperatorFigure" },
        { SinkOperatorFigure.class, "sinkOperatorFigure" },
        { ArithmeticOperatorFigure.class, "arithmeticOperatorFigure" },
        { FunctionOperatorFigure.class, "functionOperatorFigure" },
        { DoubleConstantOperatorFigure.class, "doubleConstantOperatorFigure" },
        { LogicalOperatorFigure.class, "logicalOperatorFigure" },
        { EqualityOperatorFigure.class, "equalityOperatorFigure" },
        { DiodeOperatorFigure.class, "diodeOperatorFigure" },
        { SinkOperator.class, "sinkOperator" },    
        { org.yccheok.jstock.analysis.Connector.class, "analysis.connector" },
        { org.yccheok.jstock.analysis.Connection.class, "analysis.Connection" },
        { ArithmeticOperator.class, "arithmeticOperator" },
        { DependencyFigure.class, "dep" },
        { ListFigure.class, "list" },
        { TextFigure.class, "text" },
        { GroupFigure.class, "g" },
        { TextAreaFigure.class, "ta" },
        { SeparatorLineFigure.class, "separator" },
        { OutputConnector.class, "outputConnector" },
        { InputConnector.class, "inputConnector" },
        { RelativeLocator.class, "relativeLocator" },
        { ChopRectangleConnector.class, "rectConnector" },
        { ArrowTip.class, "arrowTip" },
        { IndicatorDefaultDrawing.class, "alertDefaultDrawing" },     
        { DoubleConstantOperator.class, "doubleConstantOperator" }        
    };
    
    /*
    private final static Class[] enumTagArray = {
        ArithmeticOperator.Arithmetic.class
    };
    */
    
    private final static Object[][] enumTagArray = {
        { ArithmeticOperator.Arithmetic.class, "arithmeticEnum" }
    };
    
    /** Creates a new instance. */
    public IndicatorDOMFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
        
        for (Object[] o : enumTagArray) {
            this.addEnumClass((String) o[1], (Class) o[0]);
        }
        
        /*
        for (Class c : enumTagArray) {
            java.util.EnumSet set = java.util.EnumSet.allOf(c);
            
            for(Object o : set) {
                Enum e = (Enum)o;
                this.addEnum(e.toString(), e);
            }
        }
        */
    }
}
