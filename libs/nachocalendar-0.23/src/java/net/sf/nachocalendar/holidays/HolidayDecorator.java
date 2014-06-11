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
 * 2005-01-09   Cleanups
 * 
 * -------
 *
 * HolidayDecorator.java
 * 
 * Created on 21/12/2004
 *
 */
package net.sf.nachocalendar.holidays;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;

/**
 * @author Ignacio Merani
 *
 */
public class HolidayDecorator implements DayRenderer {
    private DayRenderer renderer;
    
    
    /**
     * Default constructor.
     * @param renderer renderer to decore.
     */
    public HolidayDecorator(DayRenderer renderer) {
        super();
        this.renderer = renderer;
    }
    
    /**
     * @see net.sf.nachocalendar.components.DayRenderer#getDayRenderer(net.sf.nachocalendar.components.DayPanel, java.util.Date, java.lang.Object, boolean, boolean, boolean)
     */
    public Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled) {
        Component retorno = renderer.getDayRenderer(daypanel, day, data, selected, working, enabled);
        if (!enabled) return retorno;
        if (data != null) {
            retorno.setForeground(Color.RED);
            if (data instanceof HoliDay) {
                HoliDay h = (HoliDay) data;
                daypanel.setToolTipText(h.getName());
            }
        }
        return retorno;
    }

}
