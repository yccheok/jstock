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
 * TaskQuantityRenderer.java
 *
 * Created on August 28, 2004, 11:17 AM
 */

package net.sf.nachocalendar.tasks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
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
 * of tasks related to the Date
 * @author Ignacio Merani
 */
public class TaskQuantityRenderer extends JLabel implements DayRenderer {
    private Calendar cal;
    private Color selectedbg, unselectedbg, unselectedfg, selectedfg, notworking, taskBg, taskColor;
    private int taskq;
    
    /** Creates a new instance of TaskRenderer. */
    public TaskQuantityRenderer() {
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
        taskColor = Color.RED;
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
        taskq = 0;
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
            taskq = col.size();
            daypanel.setToolTipText(Integer.toString(taskq) + " tasks");
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
    
    /** Draws the component.
     * @param g Graphics Object
     */
    public void paint(Graphics g) {
        super.paint(g);
        if (taskq > 0) {
            int x = getWidth() / 4;
            int y = getHeight() / 4;
            g.setColor(taskColor);
            g.fillOval(x - 1, y - 1, 2, 2);
            if (taskq > 1) {
                g.fillOval(x - 1, (y * 3) - 1, 2, 2);
            }
            if (taskq > 2) {
                g.fillOval((x * 3) - 1, y - 1, 2, 2);
            }
            if (taskq > 3) {
                g.fillOval((x * 3) - 1, (y * 3) - 1, 2, 2);
            }
        }
    }
    
}
