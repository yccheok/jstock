/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.charting;

import java.awt.Color;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

/**
 *
 * @author yccheok
 */
public class Utils {
    public static void applyChartTheme(JFreeChart chart) {
        final StandardChartTheme chartTheme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();
        chartTheme.setXYBarPainter(barPainter);
        chartTheme.setShadowVisible(false);
        chartTheme.setPlotBackgroundPaint(Color.WHITE);
        chartTheme.setDomainGridlinePaint(Color.LIGHT_GRAY);
        chartTheme.setRangeGridlinePaint(Color.LIGHT_GRAY);
        chartTheme.setPlotOutlinePaint(Color.LIGHT_GRAY);

        if (chart.getPlot() instanceof CombinedDomainXYPlot) {
            @SuppressWarnings("unchecked")
            List<Plot> plots = ((CombinedDomainXYPlot)chart.getPlot()).getSubplots();
            for (Plot plot : plots) {
                final int domainAxisCount = ((XYPlot)plot).getDomainAxisCount();
                final int rangeAxisCount = ((XYPlot)plot).getRangeAxisCount();
                for (int i = 0; i < domainAxisCount; i++) {
                    ((XYPlot)plot).getDomainAxis(i).setAxisLinePaint(Color.LIGHT_GRAY);
                    ((XYPlot)plot).getDomainAxis(i).setTickMarkPaint(Color.LIGHT_GRAY);
                }
                for (int i = 0; i < rangeAxisCount; i++) {
                    ((XYPlot)plot).getRangeAxis(i).setAxisLinePaint(Color.LIGHT_GRAY);
                    ((XYPlot)plot).getRangeAxis(i).setTickMarkPaint(Color.LIGHT_GRAY);
                }
            }
        }
        else {
            final Plot plot = chart.getPlot();
            if (plot instanceof XYPlot) {            
                final org.jfree.chart.plot.XYPlot xyPlot = (org.jfree.chart.plot.XYPlot)plot;
                final int domainAxisCount = xyPlot.getDomainAxisCount();
                final int rangeAxisCount = xyPlot.getRangeAxisCount();
                for (int i = 0; i < domainAxisCount; i++) {
                    xyPlot.getDomainAxis(i).setAxisLinePaint(Color.LIGHT_GRAY);
                    xyPlot.getDomainAxis(i).setTickMarkPaint(Color.LIGHT_GRAY);
                }
                for (int i = 0; i < rangeAxisCount; i++) {
                    xyPlot.getRangeAxis(i).setAxisLinePaint(Color.LIGHT_GRAY);
                    xyPlot.getRangeAxis(i).setTickMarkPaint(Color.LIGHT_GRAY);
                }                
            }
            //else if (plot instanceof org.jfree.chart.plot.PiePlot) {
            //    final org.jfree.chart.plot.PiePlot piePlot = (org.jfree.chart.plot.PiePlot)plot;
            //    
            //}
        }

        chartTheme.apply(chart);
    }

    private static final org.jfree.chart.renderer.xy.StandardXYBarPainter barPainter = new org.jfree.chart.renderer.xy.StandardXYBarPainter();
}
