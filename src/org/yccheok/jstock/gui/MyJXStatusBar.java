/*
 * MyJStatusBar.java
 *
 * Created on April 29, 2007, 3:18 AM
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

import java.awt.*;
import javax.swing.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.plaf.basic.*;

/**
 *
 * @author yccheok
 */
public class MyJXStatusBar extends JXStatusBar {
    
    /** Creates a new instance of MyJStatusBar */
    public MyJXStatusBar(String mainMessage, ImageIcon imageIcon, String imageIconToolTipText) {
        super();
        
        mainLabel = new JLabel(mainMessage);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(false);
        imageLabel = new JLabel(imageIcon);
        imageLabel.setToolTipText(imageIconToolTipText);

        JXStatusBar.Constraint c1 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
        JXStatusBar.Constraint c2 = new JXStatusBar.Constraint(100);
        JXStatusBar.Constraint c3 = new JXStatusBar.Constraint(50);
        
        this.add(mainLabel, c1);
        this.add(progressBar, c2);
        this.add(imageLabel, c3);
    }

    public void setMainMessage(String mainMessage) {
        mainLabel.setText(mainMessage);
    }

    public void setImageIcon(ImageIcon imageIcon, String imageIconToolTipText) {
        imageLabel.setIcon(imageIcon);
        imageLabel.setToolTipText(imageIconToolTipText);
    }    
    
    public void setProgressBar(boolean newValue) {
        progressBar.setIndeterminate(newValue);
        progressBar.setVisible(newValue);
    }
    
    public JLabel getImageLabel() {
        return imageLabel;
    }
    
    private JLabel mainLabel;
    private JLabel imageLabel;
    private JProgressBar progressBar;    
}
