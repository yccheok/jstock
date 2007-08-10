/*
 * GradientJLabel.java
 *
 * Created on May 1, 2007, 8:41 PM
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

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author yccheok
 */
public class GradientJLabel extends JLabel {
    
    /** Creates a new instance of GradientJLabel */
    public GradientJLabel(Color topColor, Color bottomColor, float ratio) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.ratio = ratio;
    }
    
   protected void paintComponent(Graphics g) {       
        Rectangle  rect  = this.getVisibleRect();
        
        GradientPaint gradient = new GradientPaint((float) rect.getMinX(), (float) rect.getMinY(),  topColor, (float) rect.getMinX(), (float) rect.getMaxY() * ratio, bottomColor);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setPaint(gradient);
        g2d.fill(rect);
        
        super.paintComponent(g);        
    }
   
   private Color topColor;
   private Color bottomColor;
   private float ratio;
}
