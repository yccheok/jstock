/*
 *  NachoCalendar
 *
 * Project Info:  http://nachocalendar.sf.net
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * Changes
 * -------
 *
 *  2004-10-01   Checked with checkstyle
 *
 * -------
 *
 * DefaultHeaderRenderer.java
 *
 * Created on August 15, 2004, 7:27 PM
 */

package net.sf.nachocalendar.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Default implementation of HeaderRenderer.
 * @author Ignacio Merani
 */
public class DefaultHeaderRenderer extends JLabel implements HeaderRenderer {
    
    private static final Color BACKGROUND_COLOR = new Color(230, 230, 230);
    /** Creates a new instance of DefaultHeaderRenderer. */
    public DefaultHeaderRenderer() {
        super();
        setOpaque(true);
        Font f = getFont();
        Font n = f.deriveFont(f.getStyle() | Font.BOLD | Font.ITALIC);
        setFont(n);
        setBackground(BACKGROUND_COLOR);
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    /**
     * Returns the component used to render the header.
     * @return Component to be used
     * @param isHeader true if is used for header, false if used for week number
     * @param isWorking true if it's a working day
     * @param value value to be show
     * @param panel panel where this component is showed
     */    
    public Component getHeaderRenderer(HeaderPanel panel, Object value, boolean isHeader, boolean isWorking) {
        // Have some space around, so that "Mon/Tue..." won't stick together.
        setText(" " + value.toString() + " ");
        return this;
    }
    
}
