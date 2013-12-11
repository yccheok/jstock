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
 * 2005-01-08   Cleanups
 * 2004-12-11   Fixed colors in other lnf's
 * 2004-10-09   setOpaque moved to constructor, local var "retorno" removed.
 * 2004-10-01   Checked with checkstyle.
 * -------
 *
 * DefaultDayRenderer.java
 *
 * Created on August 15, 2004, 11:15 AM
 */

package net.sf.nachocalendar.components;
import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Default implementation of the DayRenderer interface.
 * @author Ignacio Merani
 */
public class DefaultDayRenderer extends JLabel implements DayRenderer {
    private Calendar cal;
    private Color selectedbg, unselectedbg, selectedfg, unselectedfg, notworking;
    
    /** Creates a new instance of DefaultDayRenderer. */
    public DefaultDayRenderer() {
        cal = Calendar.getInstance();
        
        // Fake solution: by now steal colors from a JList
        JList jl = new JList();
        unselectedbg = Color.white;
        unselectedfg = jl.getForeground();
        selectedbg = jl.getSelectionBackground();
        selectedfg = jl.getSelectionForeground();
        setVerticalAlignment(CENTER);
        setHorizontalAlignment(CENTER);
        setOpaque(true);
        notworking = new Color(240, 240, 255);
    }
    
    /**
     * Returns a component configured to render the Day.
     * @return component to be used
     * @param daypanel Daypanel to be renderer
     * @param day current day
     * @param data current data
     * @param selected true if it's selected
     * @param working true if it's a working day
     * @param enabled true if it's enabled
     */    
    public Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled) {
        if (selected) {
            setBackground(selectedbg);
        } else {
            if (working) {
                setBackground(unselectedbg);
            } else setBackground(notworking);
        }
        
        if (working) {
            if (selected) {
                setForeground(selectedfg);
            } else {
                setForeground(unselectedfg);
            }
        } else {
            if (selected) {
                setForeground(selectedfg);
            } else {
                setForeground(Color.GRAY);
            }
        }
        cal.setTime(day);
        setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));

        if (!enabled) {
            setForeground(Color.lightGray);
            return this;
        }
        if (data != null) {
            setForeground(Color.RED);
        } else {
            daypanel.setToolTipText(null);
        }
        return this;
    }
}
