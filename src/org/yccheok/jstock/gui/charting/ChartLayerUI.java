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

import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
import org.yccheok.jstock.gui.Utils;
import org.yccheok.jstock.internationalization.GUIBundle;

public class ChartLayerUI<V extends javax.swing.JComponent> extends AbstractLayerUI<V> {

    private static class TraceInfo {
        private final Point2D point;
        private final int dataIndex;
        private final int plotIndex;
        private final int seriesIndex;

        private TraceInfo(Point2D point, int plotIndex, int seriesIndex, int dataIndex) {
            this.point = point;
            this.dataIndex = dataIndex;
            this.plotIndex = plotIndex;
            this.seriesIndex = seriesIndex;
        }

        public static TraceInfo newInstance(Point2D point, int plotIndex, int seriesIndex, int dataIndex) {
            return new TraceInfo(point, plotIndex, seriesIndex, dataIndex);
        }
        
        /**
         * @return the point
         */
        public Point2D getPoint() {
            return point;
        }

        /**
         * @return the dataIndex
         */
        public int getDataIndex() {
            return dataIndex;
        }

        /**
         * @return the plotIndex
         */
        public int getPlotIndex() {
            return plotIndex;
        }

        /**
         * @return the seriesIndex
         */
        public int getSeriesIndex() {
            return seriesIndex;
        }
    }

    // Major tracing information for main chart.
    private TraceInfo mainTraceInfo = TraceInfo.newInstance(null, 0, 0, 0);
    // Tracing information for all indicators.
    private final List<TraceInfo> indicatorTraceInfos = new ArrayList<TraceInfo>();
            
    private final Rectangle2D mainDrawArea = new Rectangle();

    private static final Color COLOR_BLUE = new Color(85, 85, 255);
    private static final Color COLOR_BACKGROUND = new Color(255, 255, 153);
    private static final Color COLOR_BORDER = new Color(255, 204, 0);
    private final ChartJDialog chartJDialog;

    private static final List<String> params = Collections.unmodifiableList(
            Arrays.asList(
            GUIBundle.getString("MainFrame_Prev"),
            GUIBundle.getString("MainFrame_Last"),
            GUIBundle.getString("MainFrame_High"),
            GUIBundle.getString("MainFrame_Low"),
            GUIBundle.getString("MainFrame_Vol")
            ));

    public ChartLayerUI(ChartJDialog chartJDialog) {
        this.chartJDialog = chartJDialog;
    }

    private void drawInformationBox(Graphics2D g2, JXLayer<? extends V> layer) {
        final Font oldFont = g2.getFont();
        final Font paramFont = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize());
        final FontMetrics paramFontMetrics = g2.getFontMetrics(paramFont);
        final Font valueFont = new Font(oldFont.getFontName(), oldFont.getStyle() + Font.BOLD, oldFont.getSize() + 1);
        final FontMetrics valueFontMetrics = g2.getFontMetrics(valueFont);
        final Font dateFont = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize() - 1);
        final FontMetrics dateFontMetrics = g2.getFontMetrics(dateFont);

        final StockHistoryServer stockHistoryServer = this.chartJDialog.getStockHistoryServer();
        List<String> values = new ArrayList<String>();
        final Stock stock = stockHistoryServer.getStock(stockHistoryServer.getCalendar(this.mainTraceInfo.getDataIndex()));

        // Number formats are generally not synchronized. It is recommended to create separate format instances for each thread. 
        // If multiple threads access a format concurrently, it must be synchronized externally.
        // http://stackoverflow.com/questions/2213410/usage-of-decimalformat-for-the-following-case
        final DecimalFormat integerFormat = new DecimalFormat("###,###");
        values.add(org.yccheok.jstock.gui.Utils.stockPriceDecimalFormat(stock.getPrevPrice()));
        values.add(org.yccheok.jstock.gui.Utils.stockPriceDecimalFormat(stock.getLastPrice()));
        values.add(org.yccheok.jstock.gui.Utils.stockPriceDecimalFormat(stock.getHighPrice()));
        values.add(org.yccheok.jstock.gui.Utils.stockPriceDecimalFormat(stock.getLowPrice()));
        values.add(integerFormat.format(stock.getVolume()));

        final List<String> indicatorParams = new ArrayList<String>();
        final List<String> indicatorValues = new ArrayList<String>();
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        for (TraceInfo indicatorTraceInfo : this.indicatorTraceInfos) {
            final int plotIndex = indicatorTraceInfo.getPlotIndex();
            final int seriesIndex = indicatorTraceInfo.getSeriesIndex();
            final int dataIndex = indicatorTraceInfo.getDataIndex();
            final String name = this.getLegendName(plotIndex, seriesIndex);
            final Number value = this.getValue(plotIndex, seriesIndex, dataIndex);
            if (name == null || value == null) {
                continue;
            }
            indicatorParams.add(name);
            indicatorValues.add(decimalFormat.format(value));
        }

        assert(values.size() == params.size());
        int index = 0;
        final int paramValueWidthMargin = 10;
        final int paramValueHeightMargin = 0;
        // Slightly larger than dateInfoHeightMargin, as font for indicator is
        // larger than date's.
        final int infoIndicatorHeightMargin = 8;
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

        if (indicatorValues.size() > 0) {
            totalInfoHeight += infoIndicatorHeightMargin;
            totalInfoHeight += Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight()) * indicatorValues.size() + paramValueHeightMargin * (indicatorValues.size() - 1);
            index = 0;
            for (String indicatorParam : indicatorParams) {
                final String indicatorValue = indicatorValues.get(index++);
                final int paramStringWidth = paramFontMetrics.stringWidth(indicatorParam + ":") + paramValueWidthMargin + valueFontMetrics.stringWidth(indicatorValue);
                if (maxInfoWidth < paramStringWidth) {
                    maxInfoWidth = paramStringWidth;
                }
            }
        }

        final Date date =  stockHistoryServer.getCalendar(this.mainTraceInfo.getDataIndex()).getTime();
        
        // Date formats are not synchronized. It is recommended to create separate format instances for each thread.
        // If multiple threads access a format concurrently, it must be synchronized externally.
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
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

        /* Get Border Rect Information. */
        /*
            fillRect(1, 1, 1, 1);   // O is rect pixel

            xxx
            xOx
            xxx

            drawRect(0, 0, 2, 2);   // O is rect pixel

            OOO
            OxO
            OOO
         */
        final int borderWidth = width + 2;
        final int borderHeight = height + 2;
        // On left side of the ball.
        final double suggestedBorderX = this.mainTraceInfo.getPoint().getX() - borderWidth - boxPointMargin;
        final double suggestedBorderY = this.mainTraceInfo.getPoint().getY() - (borderHeight >> 1);
        final double bestBorderX = suggestedBorderX > this.mainDrawArea.getX() ?
                        (suggestedBorderX + borderWidth) < (this.mainDrawArea.getX() + this.mainDrawArea.getWidth()) ? suggestedBorderX : this.mainDrawArea.getX() + this.mainDrawArea.getWidth() - borderWidth - boxPointMargin :
                        this.mainTraceInfo.getPoint().getX() + boxPointMargin;
        final double bestBorderY = suggestedBorderY > this.mainDrawArea.getY() ?
                        (suggestedBorderY + borderHeight) < (this.mainDrawArea.getY() + this.mainDrawArea.getHeight()) ? suggestedBorderY : this.mainDrawArea.getY() + this.mainDrawArea.getHeight() - borderHeight - boxPointMargin :
                        this.mainDrawArea.getY() + boxPointMargin;
        
        final double x = bestBorderX + 1;
        final double y = bestBorderY + 1;

        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Composite oldComposite = g2.getComposite();
        final Color oldColor = g2.getColor();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_BORDER);
        g2.drawRoundRect((int)(bestBorderX + 0.5), (int)(bestBorderY + 0.5), borderWidth - 1, borderHeight - 1, 15, 15);
        g2.setColor(COLOR_BACKGROUND);
        g2.setComposite(Utils.makeComposite(0.75f));
        g2.fillRoundRect((int)(x + 0.5), (int)(y + 0.5), width, height, 15, 15);
        g2.setComposite(oldComposite);
        g2.setColor(oldColor);
                
        int yy = (int)(y  + padding + dateFontMetrics.getAscent() + 0.5);
        g2.setFont(dateFont);
        g2.setColor(COLOR_BLUE);
        g2.drawString(dateString,
                (int)(((width - dateFontMetrics.stringWidth(dateString)) >> 1) + x + 0.5),
                yy);

        index = 0;
        yy += dateFontMetrics.getDescent() + dateInfoHeightMargin + valueFontMetrics.getAscent();
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
                (int)(padding + x + 0.5),
                yy);
            g2.setFont(valueFont);
            g2.drawString(value,
                (int)(width - padding - valueFontMetrics.stringWidth(value) + x + 0.5),
                yy);
            // Same as yy += valueFontMetrics.getDescent() + paramValueHeightMargin + valueFontMetrics.getAscent()
            yy += paramValueHeightMargin + valueFontMetrics.getHeight();
        }

        g2.setColor(Color.BLACK);
        yy -= paramValueHeightMargin;
        yy += infoIndicatorHeightMargin;

        index = 0;
        for (String indicatorParam : indicatorParams) {
            final String indicatorValue = indicatorValues.get(index++);
            g2.setFont(paramFont);
            g2.drawString(indicatorParam + ":",
                (int)(padding + x + 0.5),
                yy);
            g2.setFont(valueFont);
            g2.drawString(indicatorValue,
                (int)(width - padding - valueFontMetrics.stringWidth(indicatorValue) + x + 0.5),
                yy);
            // Same as yy += valueFontMetrics.getDescent() + paramValueHeightMargin + valueFontMetrics.getAscent()
            yy += paramValueHeightMargin + valueFontMetrics.getHeight();
        }

        g2.setColor(oldColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        g2.setFont(oldFont);
    }
    
    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends V> layer) {
        super.paintLayer(g2, layer);
        
        if (this.mainTraceInfo.getPoint() == null) {
            return;
        }

        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Color oldColor = g2.getColor();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_BLUE);
        final int BALL_RADIUS = 8;
        g2.fillOval((int)(this.mainTraceInfo.getPoint().getX() - (BALL_RADIUS >> 1) + 0.5), (int)(this.mainTraceInfo.getPoint().getY() - (BALL_RADIUS >> 1) + 0.5), BALL_RADIUS, BALL_RADIUS);

        for (TraceInfo indicatorTraceInfo : this.indicatorTraceInfos) {
            final Point2D point = indicatorTraceInfo.getPoint();
            if (null == point) {
                continue;
            }
            g2.fillOval((int)(point.getX() - (BALL_RADIUS >> 1) + 0.5), (int)(point.getY() - (BALL_RADIUS >> 1) + 0.5), BALL_RADIUS, BALL_RADIUS);
        }

        g2.setColor(oldColor);
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        this.drawInformationBox(g2, layer);
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> layer) {
        super.processMouseEvent(e, layer);
        this.processEvent(e, layer);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JXLayer<? extends V> layer) {
        super.processMouseMotionEvent(e, layer);
        this.processEvent(e, layer);
    }

    // Indicator tracing information will always be determined by main tracing
    // information. Not by mouse coordinate. Their x-location, must be exactly
    // same as mainTraceInfo's point x-location.
    //
    // Their behavior are diference from mainTraceInfo. For mainTraceInfo, if we
    // move our mouse cursor to a location where ball cannot be drawn, previous
    // ball will remain in its old location.
    //
    // However, in order to avoid confusion from users, "why CCI ball is not 
    // x-location tally with main ball", if an indicator ball unable to be drawn
    // x-parallel with main ball, it will be removed.
    //
    // If particular indicator point is not within mainDrawArea, it will be removed
    // immediately. Their index, however will be updated, in order to access
    // time series data, which will be shown in information box.
    private void updateIndicatorTraceInfos(int mainPointIndex) {
        this.indicatorTraceInfos.clear();
        if (this.mainTraceInfo == null) {
            return;
        }

        final ChartPanel chartPanel = this.chartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();

        // Get the date.
        final Day day = (Day)((TimeSeriesCollection)((XYPlot)cplot.getSubplots().get(0)).getDataset()).getSeries(0).getDataItem(mainPointIndex).getPeriod();

        // Interate through main plot's series.
        {
            // 0 means main plot.
            final XYPlot plot = (XYPlot) cplot.getSubplots().get(0);
            final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
            // Start with 1. We are not interested in main series.
            for (int j = 1, size = timeSeriesCollection.getSeriesCount(); j < size; j++) {
                final TimeSeries timeSeries = timeSeriesCollection.getSeries(j);
                /* Time consuming. */
                final int dataIndex = dataIndex(timeSeries, day);

                if (dataIndex < 0) {
                    continue;
                }

                final Point2D point = this.getPoint(0, j, dataIndex);
                final String name = this.getLegendName(0, j);
                final Number value = this.getValue(0, j, dataIndex);
                
                if (point == null || name == null || value == null) {
                    continue;
                }

                // We will never draw ball for SMA, EMA...
                this.indicatorTraceInfos.add(TraceInfo.newInstance(null, 0, j, dataIndex));
            }
        }

        // Begin with 1. 0 is main plot.
        for (int i = 1, size = cplot.getSubplots().size(); i < size; i++) {
            final XYPlot plot = (XYPlot) cplot.getSubplots().get(i);
            final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
            // So far, for subplot, each of them only have 1 series.
            assert(1 == timeSeriesCollection.getSeriesCount());
            for (int j = 0, size2 = timeSeriesCollection.getSeriesCount(); j < size2; j++) {
                final TimeSeries timeSeries = timeSeriesCollection.getSeries(j);
                /* Time consuming. */
                final int dataIndex = dataIndex(timeSeries, day);

                if (dataIndex < 0) {
                    continue;
                }
                
                final Point2D point = this.getPoint(i, j, dataIndex);
                final String name = this.getLegendName(i, j);
                final Number value = this.getValue(i, j, dataIndex);

                if (point == null || name == null || value == null) {
                    continue;
                }

                final Rectangle2D plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getSubplotInfo(i).getDataArea();
                if (plotArea.contains(point)) {
                    this.indicatorTraceInfos.add(TraceInfo.newInstance(point, i, j, dataIndex));
                }
                else {
                    this.indicatorTraceInfos.add(TraceInfo.newInstance(null, i, j, dataIndex));
                }
            }
        }
    }

    // Possible null, if the chart had been updated, but the drawing is not being
    // rendered properly yet.
    private String getLegendName(int plotIndex, int seriesIndex) {
        final ChartPanel chartPanel = this.chartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();

        if (plotIndex >= chartPanel.getChartRenderingInfo().getPlotInfo().getSubplotCount()) {
            /* Not ready yet. */
            return null;
        }

        final XYPlot plot = (XYPlot)cplot.getSubplots().get(plotIndex);
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();

        if (seriesIndex >= timeSeriesCollection.getSeriesCount()) {
            /* Not ready yet. */
            return null;
        }

        final TimeSeries timeSeries = timeSeriesCollection.getSeries(seriesIndex);
        return (String)timeSeries.getKey();
    }

    // Possible null, if the chart had been updated, but the drawing is not being
    // rendered properly yet.
    private Point2D getPoint(int plotIndex, int seriesIndex, int dataIndex) {
        final ChartPanel chartPanel = this.chartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();

        if (plotIndex >= chartPanel.getChartRenderingInfo().getPlotInfo().getSubplotCount()) {
            /* Not ready yet. */
            return null;
        }

        if (plotIndex >= cplot.getSubplots().size()) {
            /* Not ready yet. */
            return null;
        }

        final XYPlot plot = (XYPlot) cplot.getSubplots().get(plotIndex);
        final ValueAxis domainAxis = plot.getDomainAxis();
        final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        final ValueAxis rangeAxis = plot.getRangeAxis();
        final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();

        if (seriesIndex >= timeSeriesCollection.getSeriesCount()) {
            /* Not ready yet. */
            return null;
        }

        final TimeSeries timeSeries = timeSeriesCollection.getSeries(seriesIndex);

        if (dataIndex >= timeSeries.getItemCount()) {
            /* Not ready yet. */
            return null;
        }
        
        final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(dataIndex);
        final double xValue = timeSeriesDataItem.getPeriod().getFirstMillisecond();
        final double yValue = timeSeriesDataItem.getValue().doubleValue();
        final Rectangle2D plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getSubplotInfo(plotIndex).getDataArea();
        final double xJava2D = domainAxis.valueToJava2D(xValue, plotArea, domainAxisEdge);
        final double yJava2D = rangeAxis.valueToJava2D(yValue, plotArea, rangeAxisEdge);

        return new Point((int)xJava2D, (int)yJava2D);
    }

    // Possible null, if the chart had been updated, but the drawing is not being
    // rendered properly yet.
    private Number getValue(int plotIndex, int seriesIndex, int dataIndex) {
        final ChartPanel chartPanel = this.chartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();

        if (plotIndex >= cplot.getSubplots().size()) {
            /* Not ready yet. */
            return null;
        }
        
        final XYPlot plot = (XYPlot)cplot.getSubplots().get(plotIndex);
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();

        if (seriesIndex >= timeSeriesCollection.getSeriesCount()) {
            /* Not ready yet. */
            return null;
        }

        final TimeSeries timeSeries = timeSeriesCollection.getSeries(seriesIndex);

        if (dataIndex >= timeSeries.getItemCount()) {
            /* Not ready yet. */
            return null;
        }

        return timeSeries.getDataItem(dataIndex).getValue();
    }


    // Use binary search to search for index.
    // Return -1 if failed.
    private int dataIndex(TimeSeries timeSeries, Day targetDay) {
        int low = 0;
        int high = timeSeries.getItemCount() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(mid);
            final Day searchDay = (Day)timeSeriesDataItem.getPeriod();
            final long cmp = searchDay.compareTo(targetDay);

            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                return mid;
            }
        }
        return -1;
    }

    public void updateTraceInfos() {
        this.updateMainTraceInfo();
        // updateIndicatorTraceInfos must always be called only after updateMainTraceInfo.
        this.updateIndicatorTraceInfos(this.mainTraceInfo.getDataIndex());
        this.setDirty(true);
    }

    private void updateMainTraceInfo() {
        if (this.updateMainTraceInfo(this.mainTraceInfo.getPoint()) == false) {
            /* Clear the mainTraceInfo. */
            this.mainTraceInfo = TraceInfo.newInstance(null, 0, 0, 0);
        }
    }

    // Update this.mainDrawArea, this.mainTraceInfo
    // If traceInfo is not within this.mainDrawArea, this.mainTraceInfo will not be
    // updated. However, this.mainDrawArea will always be updated.
    private boolean updateMainTraceInfo(Point2D point) {
        if (point == null) {
            return false;
        }

        final ChartPanel chartPanel = this.chartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final CombinedDomainXYPlot cplot = (CombinedDomainXYPlot) chart.getPlot();
        // Top most plot.
        final XYPlot plot = (XYPlot) cplot.getSubplots().get(0);
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
        // 0 are the main chart. 1, 2, 3... are TA.
        final TimeSeries timeSeries = timeSeriesCollection.getSeries(0);
        
        // I also not sure why. This is what are being done in Mouse Listener Demo 4.
        final Point2D p2 = chartPanel.translateScreenToJava2D((Point)point);

        /* Try to get correct main chart area. */
        final Rectangle2D _plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getSubplotInfo(0).getDataArea();

        final ValueAxis domainAxis = plot.getDomainAxis();
        final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        final ValueAxis rangeAxis = plot.getRangeAxis();
        final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        final double coordinateX = domainAxis.java2DToValue(p2.getX(), _plotArea,
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
            final long search = day.getFirstMillisecond();
            final long cmp = search - time;

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

        final int tmpIndex = bestMid;
        // translateJava2DToScreen will internally convert Point2D.Double to Point.
        final Point2D tmpPoint = chartPanel.translateJava2DToScreen(new Point2D.Double(xJava2D, yJava2D));
        this.mainDrawArea.setRect(_plotArea);

        if (this.mainDrawArea.contains(tmpPoint)) {
            // 0 indicates main plot.
            this.mainTraceInfo = TraceInfo.newInstance(tmpPoint, 0, 0, tmpIndex);
            return true;
        }
        return false;
    }

    private void processTimeSeriesCollectionEvent(MouseEvent e, JXLayer layer) {
        final Point mousePoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), layer);
        if (this.updateMainTraceInfo(mousePoint)) {
            this.updateIndicatorTraceInfos(this.mainTraceInfo.getDataIndex());
            this.setDirty(true);
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
            this.mainTraceInfo = TraceInfo.newInstance(null, 0, 0, 0);
            this.setDirty(true);
        }
    }
}

