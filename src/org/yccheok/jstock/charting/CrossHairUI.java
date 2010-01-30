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
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
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

    private int radius = 80;

    private double magnifyingFactor = 4.0;

    private Point2D point = new Point2D.Double();

    /**
     * Returns the magnifying scale factor.
     *
     * @return The magnifying scale factor.
     */
    public double getMagnifyingFactor() {
        return this.magnifyingFactor;
    }

    /**
     * Sets the magnifying scale factor.
     *
     * @param factor  the new scale factor.
     */
    public void setMagnifyingFactor(double factor) {
        this.magnifyingFactor = factor;
    }

    /**
     * Returns the radius of the magnifying glass, in Java2D units.
     *
     * @return The radius.
     */
    public int getRadius() {
        return this.radius;
    }

    /**
     * Sets the radius of the magnifying glass.
     *
     * @param radius  the new radius (in Java2D units).
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

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
        Point2D _point = getPoint();
        double scale = getMagnifyingFactor();
        double baseRadius = getRadius();
        double scaledRadius = (baseRadius / scale);
        double strokeAdjust = 0.5;
        double drawSize = 2 * (baseRadius + strokeAdjust);
        double clipSize = 2 * scaledRadius;
        Ellipse2D drawGlass = new Ellipse2D.Double(-strokeAdjust,
                -strokeAdjust, drawSize, drawSize);
        Ellipse2D clipGlass = new Ellipse2D.Double(0, 0, clipSize, clipSize);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.translate(_point.getX() - baseRadius, _point.getY() - baseRadius);
                Color oldColor = g2.getColor();
        g2.setPaint(createPaint(drawGlass, false));
        g2.fill(drawGlass);
        g2.setColor(oldColor);
        g2.draw(drawGlass);
        AffineTransform oldTransform = g2.getTransform();
        Shape oldClip = g2.getClip();
        g2.scale(scale, scale);
        g2.clip(clipGlass);
        g2.translate(scaledRadius - _point.getX(), scaledRadius - _point.getY());
        layer.paint(g2);
        g2.setTransform(oldTransform);
        g2.setClip(oldClip);
        g2.setPaint(createPaint(drawGlass, true));
        g2.fill(drawGlass);
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

    private void processEvent(MouseEvent e, JXLayer layer) {
        final Component component = e.getComponent();
        final ChartPanel chartPanel = (ChartPanel)component;
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        final XYPlot plot = (XYPlot) cplot.getSubplots().get(0);
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
        double coordinateY = rangeAxis.java2DToValue(mousePoint2.getY(), plotArea,
                rangeAxisEdge);
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
        final TimeSeries timeSeries = timeSeriesCollection.getSeries(0);

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
            // ???
            final long target = (day.getFirstMillisecond() + day.getLastMillisecond()) >>> 1;
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

        System.out.println("BEST = " + timeSeries.getDataItem(bestMid).getPeriod());

        setDirty(true);
    }

    /**
     * A utility method to create the paint for the glass.
     *
     * @param glass  the glass shape.
     * @param transparent  transparent?
     *
     * @return The paint.
     */
    private Paint createPaint(Ellipse2D glass, boolean transparent) {
        Point2D center = new Point2D.Double(glass.getCenterX(),
                glass.getCenterY());
        float _radius = (float) (glass.getCenterX() - glass.getX());
        Point2D focus = new Point2D.Double(center.getX() - 0.5 * _radius,
                center.getY() - 0.5 * _radius);
        Color[] colors = new Color[] {
                transparent ? new Color(255, 255, 255, 128) : Color.WHITE,
                transparent ? new Color(0, 255, 255, 32) : Color.CYAN };
        float[] fractions = new float[] { 0f, 1f };
        RadialGradientPaint paint = new RadialGradientPaint(center, _radius,
                focus, fractions, colors,
                MultipleGradientPaint.CycleMethod.NO_CYCLE);
        return paint;
    }

}

