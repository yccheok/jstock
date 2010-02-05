/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockHistoryServer;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.internationalization.GUIBundle;

/**
 * Shows a magnification glass on top of a component.
 */
public class CrossHairUI<V extends javax.swing.JComponent> extends AbstractLayerUI<V> {

    private Point2D point = null;
    private int pointIndex = -1;
    private Rectangle2D plotArea = null;
    private static final Color COLOR_BALL = new Color(85, 85, 255);
    private static final Color COLOR_BACKGROUND = new Color(255, 255, 153);
    private static final Color COLOR_BORDER = new Color(255, 204, 0);
    private final StockHistoryServer stockHistoryServer;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static final DecimalFormat integerFormat = new DecimalFormat("###,###");
    private static final List<String> params = Collections.unmodifiableList(
            Arrays.asList(
            GUIBundle.getString("MainFrame_Prev"),
            GUIBundle.getString("MainFrame_Last"),
            GUIBundle.getString("MainFrame_High"),
            GUIBundle.getString("MainFrame_Low"),
            GUIBundle.getString("MainFrame_Vol")
            ));

    public CrossHairUI(StockHistoryServer stockHistoryServer) {
        this.stockHistoryServer = stockHistoryServer;
    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }


    private void drawInformationBox(Graphics2D g2, JXLayer layer) {
        final Font oldFont = g2.getFont();
        final Font paramFont = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize());
        final FontMetrics paramFontMetrics = g2.getFontMetrics(paramFont);
        final Font valueFont = new Font(oldFont.getFontName(), oldFont.getStyle() + Font.BOLD, oldFont.getSize() + 1);
        final FontMetrics valueFontMetrics = g2.getFontMetrics(valueFont);
        final Font dateFont = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize() - 1);
        final FontMetrics dateFontMetrics = g2.getFontMetrics(dateFont);

        List<String> values = new ArrayList<String>();
        final Stock stock = this.stockHistoryServer.getStock(this.stockHistoryServer.getCalendar(pointIndex));

        values.add(CrossHairUI.decimalFormat.format(stock.getPrevPrice()));
        values.add(CrossHairUI.decimalFormat.format(stock.getLastPrice()));
        values.add(CrossHairUI.decimalFormat.format(stock.getHighPrice()));
        values.add(CrossHairUI.decimalFormat.format(stock.getLowPrice()));
        values.add(CrossHairUI.integerFormat.format(stock.getVolume()));

        assert(values.size() == params.size());
        int index = 0;
        final int paramValueWidthMargin = 10;
        final int paramValueHeightMargin = 0;
        int maxInfoWidth = -1;
        // paramFontMetrics will always "smaller" than valueFontMetrics.
        int totalInfoHeight = Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight()) * values.size() + paramValueHeightMargin * (values.size() - 1);
        for (String param : params) {
            final String value = values.get(index++);
            final int paramStringWidth = paramFontMetrics.stringWidth(param + ":") + paramValueWidthMargin + valueFontMetrics.stringWidth(value);
            if (maxInfoWidth < paramStringWidth) {
                maxInfoWidth = paramStringWidth;
            }
        }

        final Date date =  this.stockHistoryServer.getCalendar(this.pointIndex).getTime();
        final String dateString = simpleDateFormat.format(date);
        final int dateStringWidth = dateFontMetrics.stringWidth(dateString);
        final int dateStringHeight = dateFontMetrics.getHeight();
        final int maxStringWidth = Math.max(dateStringWidth, maxInfoWidth);
        final int dateInfoHeightMargin = 5;
        final int maxStringHeight = dateStringHeight + dateInfoHeightMargin + totalInfoHeight;
        
        final int padding = 5;
        final int boxPointMargin = 8;
        final int width = maxStringWidth + (padding << 1);
        final int height = maxStringHeight + (padding << 1);
        final int x = point.getX() - width > plotArea.getX() ? (int)(point.getX() - width - boxPointMargin) : (int)(point.getX() + boxPointMargin);
        final int y = point.getY() - (height >> 1) > plotArea.getY() ? (int)(point.getY() - (height >> 1)) : (int)(plotArea.getY() + boxPointMargin);

        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Composite oldComposite = g2.getComposite();
        final Color oldColor = g2.getColor();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_BORDER);
        g2.drawRoundRect(x, y, width, height, 20, 20);
        g2.setColor(COLOR_BACKGROUND);
        g2.setComposite(makeComposite(0.75f));
        g2.fillRoundRect(x + 1, y + 1, width - 1, height - 1, 20, 20);
        g2.setComposite(oldComposite);
        g2.setColor(oldColor);
                
        int yy = y + dateFontMetrics.getHeight() + padding;
        g2.setFont(dateFont);
        g2.setColor(COLOR_BALL);
        g2.drawString(dateString,
                ((width - dateFontMetrics.stringWidth(dateString)) >> 1) + x,
                yy);

        index = 0;
        yy += dateInfoHeightMargin + valueFontMetrics.getHeight();
        final String LAST_STR = GUIBundle.getString("MainFrame_Last");
        for (String param : params) {
            final String value = values.get(index++);
            g2.setColor(Color.BLACK);
            if (param.equals(LAST_STR)) {
                if (stock.getChangePrice() > 0.0) {
                    g2.setColor(JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR);
                }
                else if (stock.getChangePrice() < 0.0) {
                    g2.setColor(JStockOptions.DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR);
                }
            }
            g2.setFont(paramFont);
            g2.drawString(param + ":",
                padding + x,
                yy);
            g2.setFont(valueFont);
            g2.drawString(value,
                width - padding - valueFontMetrics.stringWidth(value) + x,
                yy);
            yy += paramValueHeightMargin + valueFontMetrics.getHeight();
        }
        g2.setColor(oldColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        g2.setFont(oldFont);
    }
    
    /**
     * Paints the magnifying glass.
     *
     * @param g2  the graphics device.
     * @param layer  the layer.
     */
    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends V> layer) {
        super.paintLayer(g2, layer);
        
        if (this.point == null) {
            return;
        }

        final int radius = 8;
        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Color oldColor = g2.getColor();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_BALL);
        g2.fillOval((int)(this.point.getX() - (radius >> 1) + 0.5), (int)(this.point.getY() - (radius >> 1) + 0.5), radius, radius);
        g2.setColor(oldColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        this.drawInformationBox(g2, layer);
    }

    /**
     * Updates the glass location in response to mouse events.
     *
     * @param e  the event.
     * @param layer  the layer.
     */
    @Override
    protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> layer) {
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
    protected void processMouseMotionEvent(MouseEvent e, JXLayer<? extends V> layer) {
        super.processMouseMotionEvent(e, layer);
        this.processEvent(e, layer);
    }

    private void processTimeSeriesCollectionEvent(MouseEvent e, JXLayer layer) {
        final Component component = e.getComponent();
        final ChartPanel chartPanel = (ChartPanel)component;
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        // Top most plot.
        final XYPlot plot = (XYPlot) cplot.getSubplots().get(0);
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
        // 0 are the main chart. 1, 2, 3... are TA.
        final TimeSeries timeSeries = timeSeriesCollection.getSeries(0);

        final Point mousePoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), layer);
        // I also not sure why. This is what are being done in Mouse Listener Demo 4.
        final Point2D mousePoint2 = chartPanel.translateScreenToJava2D(mousePoint);
        final Rectangle2D _plotArea = chartPanel.getScreenDataArea();

        final ValueAxis domainAxis = plot.getDomainAxis();
        final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        final ValueAxis rangeAxis = plot.getRangeAxis();
        final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        double coordinateX = domainAxis.java2DToValue(mousePoint2.getX(), _plotArea,
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
        final double xJava2D = domainAxis.valueToJava2D(xValue, _plotArea, domainAxisEdge);
        final double yJava2D = rangeAxis.valueToJava2D(yValue, _plotArea, rangeAxisEdge);

        this.pointIndex = bestMid;
        // this.point = new Point2D.Double(xJava2D, yJava2D);
        this.point = chartPanel.translateJava2DToScreen(new Point2D.Double(xJava2D, yJava2D));
        this.plotArea = _plotArea;

        if (_plotArea.contains(this.point)) {
            setDirty(true);
        }
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
        else {
            this.point = null;
            this.setDirty(true);
        }
    }
}

