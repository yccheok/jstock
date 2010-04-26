/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import org.apache.commons.lang.ArrayUtils;
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
    public static Double createEMA(java.util.List<Double> values, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }
        final int size = values.size();
        final Core core = new Core();
        final int allocationSize = size - core.emaLookback(period);
        if (allocationSize <= 0) {
            return null;
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();
        double[] _values = ArrayUtils.toPrimitive(values.toArray(new Double[0]));
        core.ema(0, values.size() - 1, _values, period, outBegIdx, outNbElement, output);

        return output[outNbElement.value - 1];
    }

    public static Double createMFI(java.util.List<Double> highs,
            java.util.List<Double> lows,
            java.util.List<Double> closes,
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            java.util.List<Long> volumes, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }
        if (highs.size() != lows.size() || highs.size() != closes.size() || highs.size() != volumes.size()) {
            throw new java.lang.IllegalArgumentException("input list must be same size");
        }

        final int size = highs.size();
        final Core core = new Core();
        final int allocationSize = size - core.mfiLookback(period);
        if (allocationSize <= 0) {
            return null;
        }
        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();
        double[] _highs = ArrayUtils.toPrimitive(highs.toArray(new Double[0]));
        double[] _lows = ArrayUtils.toPrimitive(lows.toArray(new Double[0]));
        double[] _closes = ArrayUtils.toPrimitive(closes.toArray(new Double[0]));
        long[] _volumes = ArrayUtils.toPrimitive(volumes.toArray(new Long[0]));

        double[] dv = new double[_volumes.length];
        for (int i = 0; i < dv.length; i++) {
            dv[i] = _volumes[i];
        }
        core.mfi(0, _highs.length - 1, _highs, _lows, _closes, dv,  period, outBegIdx, outNbElement, output);

        return output[outNbElement.value - 1];
    }

    public static Double createRSI(java.util.List<Double> values, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }
        final int size = values.size();
        final Core core = new Core();
        final int allocationSize = size - core.rsiLookback(period);
        if (allocationSize <= 0) {
            return null;
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();
        double[] _values = ArrayUtils.toPrimitive(values.toArray(new Double[0]));
        core.rsi(0, values.size() - 1, _values, period, outBegIdx, outNbElement, output);

        return output[outNbElement.value - 1];
    }

    public static TimeSeries createEMA(StockHistoryServer stockHistoryServer, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = stockHistoryServer.getNumOfCalendar();

        final Core core = new Core();
        final int allocationSize = num - core.emaLookback(period);
        if (allocationSize <= 0) {
            return series;
        }
        final double[] last = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            last[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getLastPrice();
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.ema(0, last.length - 1, last, period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(stockHistoryServer.getCalendar(i + outBegIdx.value).getTime()), output[i]);
        }

        return series;
    }

    public static XYDataset createCCI(StockHistoryServer stockHistoryServer, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = stockHistoryServer.getNumOfCalendar();

        final Core core = new Core();
        final int allocationSize = num - core.cciLookback(period);
        if (allocationSize <= 0) {
            return new TimeSeriesCollection(series);
        }

        final double[] high = new double[num];
        final double[] low = new double[num];
        final double[] close = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            high[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getHighPrice();
            low[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getLowPrice();
            close[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getLastPrice();
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.cci(0, num - 1, high, low, close, period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(stockHistoryServer.getCalendar(i + outBegIdx.value).getTime()), output[i]);
        }

        return new TimeSeriesCollection(series);
    }

    public static XYDataset createRSI(StockHistoryServer stockHistoryServer, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = stockHistoryServer.getNumOfCalendar();

        final Core core = new Core();
        final int allocationSize = num - core.rsiLookback(period);
        if (allocationSize <= 0) {
            return new TimeSeriesCollection(series);
        }

        final double[] last = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            last[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getLastPrice();
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.rsi(0, last.length - 1, last, period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(stockHistoryServer.getCalendar(i + outBegIdx.value).getTime()), output[i]);
        }

        return new TimeSeriesCollection(series);
    }

    public static XYDataset createMFI(StockHistoryServer stockHistoryServer, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = stockHistoryServer.getNumOfCalendar();

        final Core core = new Core();
        final int allocationSize = num - core.mfiLookback(period);
        if (allocationSize <= 0) {
            return new TimeSeriesCollection(series);
        }

        final double[] high = new double[num];
        final double[] low = new double[num];
        final double[] close = new double[num];
        final double[] volume = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            high[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getHighPrice();
            low[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getLowPrice();
            close[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getLastPrice();
            volume[i] = stockHistoryServer.getStock(stockHistoryServer.getCalendar(i)).getVolume();
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.mfi(0, num - 1, high, low, close, volume,  period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(stockHistoryServer.getCalendar(i + outBegIdx.value).getTime()), output[i]);
        }

        return new TimeSeriesCollection(series);
    }

    public static double getTypicalPrice(Stock stock) {
        return (stock.getHighPrice() + stock.getLowPrice() + stock.getLastPrice()) / 3.0;
    }
}
