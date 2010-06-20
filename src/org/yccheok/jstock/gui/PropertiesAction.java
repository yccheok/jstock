/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui;

import org.yccheok.jstock.gui.analysis.ObjectInspectorJFrame;
import org.yccheok.jstock.gui.analysis.OperatorFigure;
import org.jhotdraw.util.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.draw.*;

/**
 * GroupAction.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2006-07-12 Changed to support any CompositeFigure.
 * <br>1.0.1 2006-07-09 Fixed enabled state.
 * <br>1.0 24. November 2003  Created.
 */
public class PropertiesAction extends AbstractSelectedAction {
    public final static String ID = "selectionProperties";
    private CompositeFigure prototype;
    private ObjectInspectorJFrame objectInspectorJFrame = null;
    
    /** Creates a new instance. */
    public PropertiesAction(DrawingEditor editor) {
        this(editor, new GroupFigure());
    }
    public PropertiesAction(DrawingEditor editor, CompositeFigure prototype) {
        super(editor);
        this.prototype = prototype;
        ResourceBundleUtil _labels =
			ResourceBundleUtil.getBundle("org.yccheok.jstock.data.Labels");        
        _labels.configureAction(this, ID);
    }
    
    @Override protected void updateEnabledState() {
        if (getView() != null) {
            if (getView().getSelectionCount() == 1) {
                this.setEnabled(getView().getSelectedFigures().iterator().next() instanceof OperatorFigure);
            }
            else {
                this.setEnabled(false);
            }
        }
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        java.util.Set<Figure> figures = this.getView().getSelectedFigures();
        for (Figure figure : figures) {
            if (figure instanceof OperatorFigure) {
                final OperatorFigure operatorFigure = (OperatorFigure)figure;
                if (objectInspectorJFrame == null) {
                    objectInspectorJFrame = new ObjectInspectorJFrame(operatorFigure.getOperator());
                }
                else {
                    objectInspectorJFrame.setBean(operatorFigure.getOperator());
                }

                objectInspectorJFrame.setVisible(true);
                objectInspectorJFrame.setState(java.awt.Frame.NORMAL);
                break;
            }
        }
    }
}