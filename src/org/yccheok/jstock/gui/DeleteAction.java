/*
 * @(#)GroupAction.java  1.1  2006-07-12
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

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.FigureSelectionEvent;
import org.jhotdraw.draw.FigureSelectionListener;
import javax.swing.*;
import java.beans.*;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
import java.util.*;
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
public class DeleteAction extends AbstractSelectedAction {
    public final static String ID = "selectionDelete";
    private CompositeFigure prototype;
    
    /** Creates a new instance. */
    public DeleteAction(DrawingEditor editor) {
        this(editor, new GroupFigure());
    }
    public DeleteAction(DrawingEditor editor, CompositeFigure prototype) {
        super(editor);
        this.prototype = prototype;
        ResourceBundleUtil labels =
            ResourceBundleUtil.getBundle("org.yccheok.jstock.data.Labels");        
        labels.configureAction(this, ID);
    }
    
    @Override protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(canDelete());
        } else {
            setEnabled(false);
        }
    }
    
    protected boolean canDelete() {
        return getView().getSelectionCount() >= 1;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (canDelete()) {
            this.getEditor().getTool().editDelete();
        }
    }
}