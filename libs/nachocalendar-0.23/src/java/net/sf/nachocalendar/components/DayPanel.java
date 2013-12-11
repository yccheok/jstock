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
 *  2005-03-25   Added Moonphase painting
 *  2005-01-09   Cleanups
 *  2005-01-02   Fixed today bug 
 *  2004-12-21   Added antialiasing support
 *  2004-12-21   Today now rendered with an oval
 *  2004-12-14   DayPanel now extends JComponent
 *  2004-12-12   Modified to support multiple selection
 *  2004-10-22   Added auxiliar var componentEnabled
 *  2004-10-01   Checked with checkstyle
 *
 * -------
 *
 * DayPanel.java
 *
 * Created on August 11, 2004, 9:25 AM
 */

package net.sf.nachocalendar.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Date;

import javax.swing.JComponent;
//import javax.swing.UIManager;


/**
 * Component used to display a day. It has to had a renderer to paint itself
 * @author Ignacio Merani
 */
public class DayPanel extends JComponent {
    private Date date;
//    private Color cselected, cunselected;
    private Object data;
    javax.swing.JTable d;
    private boolean antiAliased;
    
    /**
     * Holds value of property renderer.
     */
    private DayRenderer renderer;
    
    /**
     * Holds value of property working.
     */
    private boolean working;
    
    /**
     * Holds value of property componentEnabled.
     */
    private boolean componentEnabled;
    
    /**
     * Holds value of property enabled.
     */
    private boolean enabled;
    
    /**
     * Holds value of property selected.
     */
    private boolean selected;
    
    /**
     * Holds value of property index.
     */
    private int index;
    
    /**
     * Holds the Calculator for the Moon
     */
    private MoonCalculation moonCalculator;
    
    /**
     * size of the moon
     */
    
    private int moonSize=7;
    
    /**
     * specifies if we shall print the moon signs or not
     */
    private boolean printMoon;
    
    /**
     * Creates a new instance of DayPanel.
     * @param renderer renderer used to paint
     */
    public DayPanel(DayRenderer renderer, int index) {
        this.renderer = renderer;
        this.index = index;
        componentEnabled = true;
        setBorder(null);
        setOpaque(false);
        setLayout(new BorderLayout());
        date = new Date();
//        cunselected = UIManager.getLookAndFeel().getDefaults().getColor("text");
//        cselected = UIManager.getLookAndFeel().getDefaults().getColor("textHighlight");
        setFocusable(true);
        moonCalculator = new MoonCalculation();
    }
    
    /**
     * Sets the displaying date.
     * @param d new date
     */    
    public void setDate(Date d) {
        date = d;
        repaint();
    }
    
    /**
     * Return the displaying date.
     * @return the Date
     */    
    public Date getDate() {
        return date;
    }
    
    /**
     * Sets the current data.
     * @param d the data
     */    
    public void setData(Object d) {
        data = d;
        repaint();
    }
    
    /**
     * Returns the current data.
     * @return current data
     */    
    public Object getData() {
        return data;
    }
    
    /**
     * Getter for property renderer.
     * @return Value of property renderer.
     */
    public DayRenderer getRenderer() {
        return this.renderer;
    }    
    
    /**
     * Setter for property renderer.
     * @param renderer New value of property renderer.
     */
    public void setRenderer(DayRenderer renderer) {
        this.renderer = renderer;
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
        Component comp = renderer.getDayRenderer(this, date, data, isSelected(), working, (isEnabled() && (isComponentEnabled())));
        comp.setBounds(getBounds());
        Graphics2D g2 = (Graphics2D) g;
        if (isAntiAliased()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        
        comp.paint(g);
        if (isEnabled()) {
          if (CalendarUtils.isToday(date)) {
            g.setColor(Color.red);
            g.drawOval(1,1, getWidth() - 2, getHeight() - 2);
          }

            //MoonPainting within minimal size
          if (printMoon && getWidth() > moonSize && getHeight() > moonSize) {
            g = moonCalculator.drawMoon(g, date, getWidth()-moonSize-1, 0,moonSize);
          }
        }
    }
    
    /**
     * Getter for property selected.
     * @return Value of property selected.
     */
    public boolean isSelected() {
        return this.selected;
    }    

    /**
     * Setter for property selected.
     * @param selected New value of property selected.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /**
     * Getter for property index.
     * @return Value of property index.
     */
    public int getIndex() {
        return this.index;
    }
    
    /**
     * Setter for property index.
     * @param index New value of property index.
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * Getter for property componentEnabled.
     * @return Value of property componentEnabled.
     */
    public boolean isComponentEnabled() {
        return this.componentEnabled;
    }
    
    /**
     * Setter for property componentEnabled.
     * @param componentEnabled New value of property componentEnabled.
     */
    public void setComponentEnabled(boolean componentEnabled) {
        this.componentEnabled = componentEnabled;
        super.setEnabled(enabled && componentEnabled);
    }
    
    /**
     * Getter for property enabled.
     * @return Value of property enabled.
     */
    public boolean isEnabled() {
        return this.enabled;
    }
    
    /**
     * Setter for property enabled.
     * @param enabled New value of property enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled && componentEnabled);
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
    /**
     * @return Returns the printMoon.
     */
    public boolean isPrintMoon() {
      return printMoon;
    }
    /**
     * @param printMoon The printMoon to set.
     */
    public void setPrintMoon(boolean printMoon) {
      this.printMoon = printMoon;
    }
}
