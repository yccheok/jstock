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
 * 2004-12-11   Fixed colors in other lnf's
 * 2004-10-01   Checked with checkstyle
 *
 * -------
 *
 * TaskRenderer.java
 *
 * Created on August 18, 2004, 9:38 PM
 */

package net.sf.nachocalendar.tasks;

import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JList;

import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;

/**
 * Renderer customized to show dates with tasks assigned in
 * yellow background. It also creates a tooltip with the quantity
 * of tasks related to the Date.
 * @author Ignacio Merani
 * @deprecated As of version 0.20 replaced by net.sf.nachocalendar.tasks.TaskDecorator
 */
public class TaskRenderer extends JLabel implements DayRenderer {
    private Calendar cal;
    private Color selectedbg, unselectedbg, unselectedfg, selectedfg, notworking, taskBg;
    /** Creates a new instance of TaskRenderer. */
    public TaskRenderer() {
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
        taskBg = Color.yellow;
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
        if (!enabled) {
            setText("");
            return this;
        }
        cal.setTime(day);
        setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        daypanel.setToolTipText(null);
        
        if ((data != null) && (data instanceof Collection)) {
            if (selected) {
                setBackground(Color.magenta);
            } else {
                setBackground(taskBg);
            }
            Collection col = (Collection) data;
            daypanel.setToolTipText(Integer.toString(col.size()) + " tasks");
        } else {
            daypanel.setToolTipText(null);
        }
        
        return this;
    }
    /**
     * @return Returns the taskBg.
     */
    public Color getTaskBg() {
        return taskBg;
    }
    /**
     * @param taskBg The taskBg to set.
     */
    public void setTaskBg(Color taskBg) {
        this.taskBg = taskBg;
    }
    
}
