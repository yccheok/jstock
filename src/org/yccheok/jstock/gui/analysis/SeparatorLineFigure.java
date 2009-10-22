/*
 * @(#)SeparatorLineFigure.java  1.0  2. Dezember 2003
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

import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.LineFigure;
import org.jhotdraw.geom.*;
/**
 * A horizontal line with a preferred size of 1,1.
 *
 * @author  Werner Randelshofer
 * @version 1.0 2. Dezember 2003  Created.
 */
public class SeparatorLineFigure 
extends LineFigure {
    
    /** Creates a new instance. */
    public SeparatorLineFigure() {
    }

    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        setPoint(0, 0, anchor);
        setPoint(getNodeCount() - 1, 0, new Point2D.Double(lead.x, anchor.y));
    }
    public Dimension2DDouble getPreferredSize() {
        double width = Math.ceil(STROKE_WIDTH.get(this));
        return new Dimension2DDouble(width, width);
    }
}
