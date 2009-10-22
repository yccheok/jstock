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

import java.util.Calendar;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockHistoryServer;

/**
 *
 * @author yccheok
 */
public class TechnicalAnalysis {
    public static TimeSeries createEMA(StockHistoryServer stockHistoryServer, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 1");
        }

        final TimeSeries series = new TimeSeries(name, Day.class);
        final int num = stockHistoryServer.getNumOfCalendar();

        if (period > num) {
            return series;
        }

        double previous = 0.0;
        int n = 0;
        for (int i = 0; i < period; i++) {
            previous = previous + stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getLastPrice();
            n++;
        }
        previous = previous / n;

        final double smoothing_factor = 2.0 / (1.0 + period);

        for (int i = period; i < num; i++) {
            final Calendar calendar = stockHistoryServer.getCalendar(i);
            final double current = stockHistoryServer.getStock(calendar).getLastPrice();
            final double EMA = ((current - previous) * smoothing_factor) + previous;
            series.add(new Day(calendar.getTime()), EMA);
            previous = EMA;
        }
        return series;
    }

    public static XYDataset createCCI(StockHistoryServer stockHistoryServer, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 1");
        }

        final TimeSeries series = new TimeSeries(name, Day.class);
        final int num = stockHistoryServer.getNumOfCalendar();

        for (int i = period - 1; i < num; i++) {
            double averageTypicalPrice = 0.0;
            double meanDevTypicalPrice = 0.0;
            final Calendar calendar = stockHistoryServer.getCalendar(i);
            final double typicalPrice = TechnicalAnalysis.getTypicalPrice(stockHistoryServer.getStock(calendar));

            double tmp = 0.0;
            for (int j = i; j > i - period; j--) {
                final Calendar c = stockHistoryServer.getCalendar(j);
                final Stock stock = stockHistoryServer.getStock(c);
                final double tp = TechnicalAnalysis.getTypicalPrice(stock);
                tmp = tmp + tp;
            }            
            averageTypicalPrice = tmp / period;

            tmp = 0.0;
            for (int j = i; j > i - period; j--) {
                final Calendar c = stockHistoryServer.getCalendar(j);
                final Stock stock = stockHistoryServer.getStock(c);
                final double tp = TechnicalAnalysis.getTypicalPrice(stock);
                tmp = tmp + Math.abs(tp - averageTypicalPrice);
            }
            meanDevTypicalPrice = tmp / period;

            final double CCI = meanDevTypicalPrice <= 0.0 ? 0.0 : (typicalPrice - averageTypicalPrice) / (0.015 * meanDevTypicalPrice);

            series.add(new Day(calendar.getTime()), CCI);
        }
        return new TimeSeriesCollection(series);
    }

    public static XYDataset createRSI(StockHistoryServer stockHistoryServer, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 1");
        }

        final TimeSeries series = new TimeSeries(name, Day.class);
        final int num = stockHistoryServer.getNumOfCalendar();

        for (int i = period - 1; i < num; i++) {
            double up = 0.0;
            double down = 0.0;

            for (int j = i; j > i - period; j--) {
                final Calendar c = stockHistoryServer.getCalendar(j);
                final Stock stock = stockHistoryServer.getStock(c);
                if (stock.getChangePrice() > 0.0) {
                    up = up + stock.getChangePrice();
                }
                else if (stock.getChangePrice() < 0.0) {
                    down = down + stock.getChangePrice();
                }
            }

            // Remove -ve sign.
            down = Math.abs(down);

            // But, what if up and down are 0.0 during the same time?
            // If there is no price changes during the period, RSI = 100.
            final double RS = down == 0.0 ? Double.MAX_VALUE : up / down;
            final double RSI = 100.0 - (100.0 / (1.0 + RS));
            series.add(new Day(stockHistoryServer.getCalendar(i).getTime()), RSI);
        }
        return new TimeSeriesCollection(series);
    }

    public static double getTypicalPrice(Stock stock) {
        return (stock.getHighPrice() + stock.getLowPrice() + stock.getLastPrice()) / 3.0;
    }
}
