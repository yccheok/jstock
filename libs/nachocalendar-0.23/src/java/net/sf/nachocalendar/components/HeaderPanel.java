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
 * 2004-12-21   Added antialias support
 * 2004-10-01   Checked with checkstyle
 *
 * -------
 * HeaderPanel.java
 *
 * Created on August 11, 2004, 10:40 AM
 */

package net.sf.nachocalendar.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/**
 * Panel used to render headers and week numbers.
 * @author Ignacio Merani
 */
public class HeaderPanel extends JComponent {
    private boolean antiAliased;
    /**
     * Holds value of property renderer.
     */
    private HeaderRenderer renderer;
    
    /**
     * Holds value of property value.
     */
    private Object value;
    
    /**
     * Holds value of property header.
     */
    private boolean header;
    
    /**
     * Holds value of property working.
     */
    private boolean working;
    
    /**
     * Creates a new instance of HeaderPanel.
     * @param renderer renderer to be used
     */
    
    public HeaderPanel(HeaderRenderer renderer) {
        this.renderer = renderer;
        setLayout(new BorderLayout());
        value = "";
    }
    
    /**
     * Getter for property renderer.
     * @return Value of property renderer.
     */
    public HeaderRenderer getRenderer() {
        return this.renderer;
    }
    
    /**
     * Setter for property renderer.
     * @param renderer New value of property renderer.
     */
    public void setRenderer(HeaderRenderer renderer) {
        this.renderer = renderer;
    }
    
    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public Object getValue() {
        return this.value;
    }
    
    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(Object value) {
        this.value = value;
        repaint();
    }
    
    /**
     * Getter for property header.
     * @return Value of property header.
     */
    public boolean isHeader() {
        return this.header;
    }
    
    /**
     * Setter for property header.
     * @param header New value of property header.
     */
    public void setHeader(boolean header) {
        this.header = header;
    }
    
    /**
     * Getter for property working.
     * @return Value of property working.
     */
    public boolean isWorking() {
        return this.working;
    }
    
    /**
     * Setter for property working.
     * @param working New value of property working.
     */
    public void setWorking(boolean working) {
        this.working = working;
    }
    
    /**
     * Invoked by Swing to draw components.
     * Applications should not invoke <code>paint</code> directly,
     * but should instead use the <code>repaint</code> method to
     * schedule the component for redrawing.
     * <p>
     * This method actually delegates the work of painting to three
     * protected methods: <code>paintComponent</code>,
     * <code>paintBorder</code>,
     * and <code>paintChildren</code>.  They're called in the order
     * listed to ensure that children appear on top of component itself.
     * Generally speaking, the component and its children should not
     * paint in the insets area allocated to the border. Subclasses can
     * just override this method, as always.  A subclass that just
     * wants to specialize the UI (look and feel) delegate's
     * <code>paint</code> method should just override
     * <code>paintComponent</code>.
     *
     * @param g  the <code>Graphics</code> context in which to paint
     */
    public void paint(Graphics g) {
        Component comp = renderer.getHeaderRenderer(this, value, header, working);
        comp.setBounds(getBounds());
        Graphics2D g2 = (Graphics2D) g;
        if (isAntiAliased()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        comp.paint(g);
    }
    
    /**
     * If the <code>preferredSize</code> has been set to a
     * non-<code>null</code> value just returns it.
     * If the UI delegate's <code>getPreferredSize</code>
     * method returns a non <code>null</code> value then return that;
     * otherwise defer to the component's layout manager.
     *
     * @return the value of the <code>preferredSize</code> property
     */
    public Dimension getPreferredSize() {
        final Component comp = renderer.getHeaderRenderer(this, value, header, working);
        return comp.getPreferredSize();
    }
    /**
     * @return Returns the antiAliased.
     */
    public boolean isAntiAliased() {
        return antiAliased;
    }
    /**
     * @param antiAliased The antiAliased to set.
     */
    public void setAntiAliased(boolean antiAliased) {
        this.antiAliased = antiAliased;
    }
}
