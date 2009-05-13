/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui.dynamicchart;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author yccheok
 */
public class DynamicChart {
    /** Creates new form DynamicChart */
    public DynamicChart() {
        this.price = new TimeSeries("Price", Second.class);
        // Sets the maximumItemAge attribute, which specifies the maximum age of data items in the series
        // (in terms of the RegularTimePeriod type used by this series). Whenever a new data value is
        // added, any data items that are older than the limit specified by maximumItemAge are automatically
        // discarded
        // Maximum 2 hours.
        this.price.setMaximumItemAge(2 * 60 * 60);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(this.price);

        JFreeChart freeChart = ChartFactory.createTimeSeriesChart(
            null,
            null,
            null,
            dataset,
            false,
            true,
            false
        );
        // Due to limited spacing, we remove legend.
        freeChart.removeLegend();
        freeChart.setAntiAlias(true);
        while (freeChart.getSubtitleCount() > 0)
        {
            freeChart.removeSubtitle(freeChart.getSubtitle(0));
        }

        // Due to limited spacing, we remove all information regarding x and y axis
        // as well.
        XYPlot plot = freeChart.getXYPlot();
        plot.getRangeAxis().setVisible(false);
        plot.getDomainAxis().setVisible(false);

        XYItemRenderer renderer1 = plot.getRenderer();
        renderer1.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("h:mm:ss a"), new DecimalFormat("0.00")
            )
        );

        // Disable zoom.
        chartPanel = new ChartPanel(freeChart, true, true, true, false, true);
        chartPanel.setMouseZoomable(false);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    private void addPriceObservation(Date date, double y) {
        // There must be at least an item in price. If not, ArrayIndexOutOfBoundsException
        // will be thrown, when getNextTimePeriod is being called.
        final int count = this.price.getItemCount();

        if (count > 0) {
            final Date nextStartDate = this.price.getTimePeriod(count - 1).getStart();            
            // 10 seconds interval for every point.
            if ((date.getTime() - nextStartDate.getTime()) < 10000) {
                return;
            }
        }

        Second second = new Second(date);

        try {
            this.price.add(second, y, notify);
        }
        catch (SeriesException exp) {
            log.error(null, exp);
        }
    }

    private final TimeSeries price;
    private final ChartPanel chartPanel;
    private static final Log log = LogFactory.getLog(DynamicChart.class);
}
