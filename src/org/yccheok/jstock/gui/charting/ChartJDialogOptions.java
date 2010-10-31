/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng Cheok <yccheok@yahoo.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.yccheok.jstock.gui.charting;

import java.util.ArrayList;
import java.util.List;

/**
 * ChartJDialogOptions is used to hold the GUI settings for ChartJDialog.
 * 
 * @author yccheok
 */
public class ChartJDialogOptions {
    /**
     * Technical analysis information options.
     */
    private final List<ChartJDialog.TAEx> TAExs = new ArrayList<ChartJDialog.TAEx>();
    /**
     * Price volume chart or candlestick chart.
     */
    private ChartJDialog.Type type = ChartJDialog.Type.PriceVolume;
    /**
     * Zoom options.
     */
    private ChartJDialog.Zoom zoom = ChartJDialog.Zoom.All;
    /**
     * Interval options.
     */
    private ChartJDialog.Interval interval = ChartJDialog.Interval.Daily;

    private Object readResolve() {
        if (type == null) {
            type = ChartJDialog.Type.PriceVolume;
        }
        if (zoom == null) {
            zoom = ChartJDialog.Zoom.All;
        }
        if (interval == null) {
            interval = ChartJDialog.Interval.Daily;
        }
        return this;
    }
    
    /**
     * Removes all TAExs from this options.
     */
    public void clear() {
        this.TAExs.clear();
    }

    /**
     * Removes the first occurrence of the specified TAEx from this options, if it is present
     * @param taEx TAEx to be removed from this options
     * @return <tt>true</tt> if this list contained the specified element 
     */
    public boolean remove(ChartJDialog.TAEx taEx) {
        return this.TAExs.remove(taEx);
    }

    /**
     * Appends the specified TAEx to the end of this options. No duplicated TAEx
     * will be allowed.
     * @param taEx TAEx to be appended to this options
     * @return <tt>false</tt> if duplicated TAEx found
     */
    public boolean add(ChartJDialog.TAEx taEx) {
        if (this.TAExs.contains(taEx)) {
            return false;
        }
        return this.TAExs.add(taEx);
    }

    /**
     * Returns the number of TAExs in this options.
     * @return the number of TAExs in this options
     */
    public int getTAExSize() {
        return this.TAExs.size();
    }

    /**
     * Returns the TAEx at the specified position this options.
     *
     * @param index index of the TAEx to return
     * @return the TAEx at the specified position in this options
     */
    public ChartJDialog.TAEx getTAEx(int index) {
        return this.TAExs.get(index);
    }

    /**
     * @return the type
     */
    public ChartJDialog.Type getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ChartJDialog.Type type) {
        this.type = type;
    }

    /**
     * @return the zoom
     */
    public ChartJDialog.Zoom getZoom() {
        return this.zoom;
    }

    /**
     * @param zoom the zoom to set
     */
    public void setZoom(ChartJDialog.Zoom zoom) {
        this.zoom = zoom;
    }

    /**
     * @return the interval
     */
    public ChartJDialog.Interval getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(ChartJDialog.Interval interval) {
        this.interval = interval;
    }
}
