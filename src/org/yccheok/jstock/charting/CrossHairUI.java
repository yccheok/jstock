/*
 * This file is based on code by Piet Blok, with amendments by David Gilbert.
 * For Piet's original code, see:
 *
 * http://www.pbjar.org/blogs/jxlayer/JXLayer_one.html
 *
 * Copyright (C) 2008 Piet Blok.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yccheok.jstock.charting;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import java.awt.geom.Rectangle2D;
import java.util.Date;
import javax.swing.SwingUtilities;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RectangleEdge;

/**
 * Shows a magnification glass on top of a component.
 */
public class CrossHairUI extends AbstractLayerUI {

    private Point2D point = new Point2D.Double();

    /**
     * Returns the current location of the magnifying glass.
     *
     * @return The current location.
     */
    public Point2D getPoint() {
        return this.point;
    }

    /**
     * Sets the location of the magnifying glass.
     *
     * @param point  the new location.
     */
    public void setPoint(Point2D point) {
        this.point.setLocation(point);
    }

    /**
     * Paints the magnifying glass.
     *
     * @param g2  the graphics device.
     * @param layer  the layer.
     */
    @Override
    protected void paintLayer(Graphics2D g2, JXLayer layer) {
        super.paintLayer(g2, layer);
        final Point2D _point = getPoint();
        final int radius = 8;
        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(85, 85, 255));
        g2.fillOval((int)(_point.getX() - (radius >> 1) + 0.5), (int)(_point.getY() - (radius >> 1) + 0.5), radius, radius);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
    }

    /**
     * Updates the glass location in response to mouse events.
     *
     * @param e  the event.
     * @param layer  the layer.
     */
    @Override
    protected void processMouseEvent(MouseEvent e, JXLayer layer) {
        super.processMouseEvent(e, layer);
        this.processEvent(e, layer);
    }

    /**
     * Updates the glass location in response to mouse events.
     *
     * @param e  the event.
     * @param layer  the layer.
     */
    @Override
    protected void processMouseMotionEvent(MouseEvent e,
        JXLayer layer) {
        super.processMouseMotionEvent(e, layer);
        this.processEvent(e, layer);
    }

    private void processTimeSeriesCollectionEvent(MouseEvent e, JXLayer layer) {
        final Component component = e.getComponent();
        final ChartPanel chartPanel = (ChartPanel)component;
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        final XYPlot plot = (XYPlot) cplot.getSubplots().get(0);
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
        final TimeSeries timeSeries = timeSeriesCollection.getSeries(0);

        final Point mousePoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), layer);
        // I also not sure why. This is what are being done in Mouse Listener Demo 4.
        final Point2D mousePoint2 = chartPanel.translateScreenToJava2D(mousePoint);
        final Rectangle2D plotArea = chartPanel.getScreenDataArea();
        final ValueAxis domainAxis = plot.getDomainAxis();
        final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        final ValueAxis rangeAxis = plot.getRangeAxis();
        final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        double coordinateX = domainAxis.java2DToValue(mousePoint2.getX(), plotArea,
                domainAxisEdge);
        //double coordinateY = rangeAxis.java2DToValue(mousePoint2.getY(), plotArea,
        //        rangeAxisEdge);

        int low = 0;
        int high = timeSeries.getItemCount() - 1;
        Date date = new Date((long)coordinateX);
        final long time = date.getTime();
        long bestDistance = Long.MAX_VALUE;
        int bestMid = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(mid);
            final Day day = (Day)timeSeriesDataItem.getPeriod();
            final long target = day.getFirstMillisecond();
            final long cmp = target - time;

            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                bestDistance = 0;
                bestMid = mid;
                break;
            }

            final long abs_cmp = Math.abs(cmp);
            if (abs_cmp < bestDistance) {
                bestDistance = abs_cmp;
                bestMid = mid;
            }
        }

        final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(bestMid);
        final double xValue = timeSeriesDataItem.getPeriod().getFirstMillisecond();
        final double yValue = timeSeriesDataItem.getValue().doubleValue();
        final double xJava2D = domainAxis.valueToJava2D(xValue, plotArea, domainAxisEdge);
        final double yJava2D = rangeAxis.valueToJava2D(yValue, plotArea, rangeAxisEdge);
        System.out.println(xJava2D + " " + yJava2D + " (" + xValue + " " + yValue + ")");

        this.setPoint(new Point2D.Double(xJava2D, yJava2D));

        setDirty(true);
    }

    private void processEvent(MouseEvent e, JXLayer layer) {
        if (MouseEvent.MOUSE_DRAGGED == e.getID()) {
            return;
        }
        
        final Component component = e.getComponent();
        final ChartPanel chartPanel = (ChartPanel)component;
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        final XYPlot plot = (XYPlot) cplot.getSubplots().get(0);
        if (plot.getDataset() instanceof TimeSeriesCollection) {
            this.processTimeSeriesCollectionEvent(e, layer);
        }
    }
}

