/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.yccheok.jstock.engine.Stock;

/**
 *
 * @author yccheok
 */
public class TechnicalAnalysis {

    /**
     * Returns the latest EMA.
     *
     * @param values list of raw data input
     * @param period the duration period
     * @return the latest EMA
     */
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

    /**
     * Returns the latest MFI.
     *
     * @param highs list of high price
     * @param lows list of low price
     * @param closes list of close price
     * @param volumes list of volume
     * @param period the duration period
     * @return the latest MFI
     */
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
        // Do not use System.arraycopy.
        // It will cause java.lang.ArrayStoreException, due to type mismatch.
        for (int i = 0; i < dv.length; i++) {
            dv[i] = _volumes[i];
        }
        core.mfi(0, _highs.length - 1, _highs, _lows, _closes, dv,  period, outBegIdx, outNbElement, output);

        return output[outNbElement.value - 1];
    }

    /**
     * Returns the latest RSI.
     *
     * @param values list of raw data input
     * @param period the duration period
     * @return the latest RSI
     */
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

    // Moving Average Convergence/Divergence Fix 12/26
    public static MACD.Result createMACDFix(List<Double> values, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }
        final int size = values.size();
        final Core core = new Core();
        final int allocationSize = size - core.macdFixLookback(period);
        if (allocationSize <= 0) {
            return null;
        }

        final double[] outMACD = new double[allocationSize];
        final double[] outMACDSignal = new double[allocationSize];
        final double[] outMACDHist = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();
        double[] _values = ArrayUtils.toPrimitive(values.toArray(new Double[0]));
        
        core.macdFix(0, values.size() - 1, _values, period, outBegIdx, outNbElement, outMACD, outMACDSignal, outMACDHist);
        
        return MACD.Result.newInstance(outMACD[outNbElement.value - 1], outMACDSignal[outNbElement.value - 1], outMACDHist[outNbElement.value - 1]);
    }
    
    /**
     * Returns SMA time series for charting purpose.
     *
     * @param chartDatas list of chart data
     * @param name name for the time series
     * @param period the duration period
     * @return SMA time series for charting purpose
     */
    public static TimeSeries createSMA(List<ChartData> chartDatas, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = chartDatas.size();

        final Core core = new Core();
        final int allocationSize = num - core.smaLookback(period);
        if (allocationSize <= 0) {
            return series;
        }
        final double[] last = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            last[i] = chartDatas.get(i).lastPrice;
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.sma(0, last.length - 1, last, period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(new Date(chartDatas.get(i + outBegIdx.value).timestamp)), output[i]);
        }

        return series;
    }

    public static MACD.ChartResult createMACD(List<ChartData> chartDatas, String name, MACD.Period period) {
        final int num = chartDatas.size();
        final Core core = new Core();
        final int allocationSize = num - core.macdLookback(period.fastPeriod, period.slowPeriod, period.period);
        if (allocationSize <= 0) {
            return null;
        }
        
        final double[] last = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            last[i] = chartDatas.get(i).lastPrice;
        }
        
        final double[] outMACD = new double[allocationSize];
        final double[] outMACDSignal = new double[allocationSize];
        final double[] outMACDHist = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.macd(0, last.length - 1, last, period.fastPeriod, period.slowPeriod, period.period, outBegIdx, outNbElement, outMACD, outMACDSignal, outMACDHist);
        
        final TimeSeries macdTimeSeries = new TimeSeries(name);
        final TimeSeries macdSignalTimeSeries = new TimeSeries(name);
        final TimeSeries macdHistTimeSeries = new TimeSeries(name);
        
        for (int i = 0; i < outNbElement.value; i++) {
            Day day = new Day(new Date(chartDatas.get(i + outBegIdx.value).timestamp));
            macdTimeSeries.add(day, outMACD[i]);
            macdSignalTimeSeries.add(day, outMACDSignal[i]);
            macdHistTimeSeries.add(day, outMACDHist[i]);
        }
        
        return MACD.ChartResult.newInstance(
                new TimeSeriesCollection(macdTimeSeries), 
                new TimeSeriesCollection(macdSignalTimeSeries), 
                new TimeSeriesCollection(macdHistTimeSeries));
    }
    
    /**
     * Returns EMA time series for charting purpose.
     *
     * @param chartDatas list of chart data
     * @param name name for the time series
     * @param period the duration period
     * @return EMA time series for charting purpose
     */
    public static TimeSeries createEMA(List<ChartData> chartDatas, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = chartDatas.size();

        final Core core = new Core();
        final int allocationSize = num - core.emaLookback(period);
        if (allocationSize <= 0) {
            return series;
        }
        final double[] last = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            last[i] = chartDatas.get(i).lastPrice;
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.ema(0, last.length - 1, last, period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(new Date(chartDatas.get(i + outBegIdx.value).timestamp)), output[i]);
        }

        return series;
    }

    /**
     * Returns CCI XYDataset for charting purpose.
     *
     * @param chartDatas list of chart data
     * @param name name for the XYDataset
     * @param period the duration period
     * @return CCI XYDataset for charting purpose
     */
    public static XYDataset createCCI(List<ChartData> chartDatas, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = chartDatas.size();

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
            high[i] = chartDatas.get(i).highPrice;
            low[i] = chartDatas.get(i).lowPrice;
            close[i] = chartDatas.get(i).lastPrice;
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.cci(0, num - 1, high, low, close, period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(new Date(chartDatas.get(i + outBegIdx.value).timestamp)), output[i]);
        }

        return new TimeSeriesCollection(series);
    }

    /**
     * Returns RSI XYDataset for charting purpose.
     *
     * @param chartDatas list of chart data
     * @param name name for the XYDataset
     * @param period the duration period
     * @return RSI XYDataset for charting purpose
     */
    public static XYDataset createRSI(List<ChartData> chartDatas, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = chartDatas.size();

        final Core core = new Core();
        final int allocationSize = num - core.rsiLookback(period);
        if (allocationSize <= 0) {
            return new TimeSeriesCollection(series);
        }

        final double[] last = new double[num];
        // Fill up last array.
        for (int i = 0; i < num; i++) {
            last[i] = chartDatas.get(i).lastPrice;
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.rsi(0, last.length - 1, last, period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(new Date(chartDatas.get(i + outBegIdx.value).timestamp)), output[i]);
        }

        return new TimeSeriesCollection(series);
    }

    /**
     * Returns MFI XYDataset for charting purpose.
     *
     * @param chartDatas list of chart data
     * @param name name for the XYDataset
     * @param period the duration period
     * @return MFI XYDataset for charting purpose
     */
    public static XYDataset createMFI(List<ChartData> chartDatas, String name, int period) {
        if (period <= 0) {
            throw new java.lang.IllegalArgumentException("period must be greater than 0");
        }

        final TimeSeries series = new TimeSeries(name);
        final int num = chartDatas.size();

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
            high[i] = chartDatas.get(i).highPrice;
            low[i] = chartDatas.get(i).lowPrice;
            close[i] = chartDatas.get(i).lastPrice;
            volume[i] = chartDatas.get(i).volume;
        }

        final double[] output = new double[allocationSize];
        final MInteger outBegIdx = new MInteger();
        final MInteger outNbElement = new MInteger();

        core.mfi(0, num - 1, high, low, close, volume,  period, outBegIdx, outNbElement, output);

        for (int i = 0; i < outNbElement.value; i++) {
            series.add(new Day(new Date(chartDatas.get(i + outBegIdx.value).timestamp)), output[i]);
        }

        return new TimeSeriesCollection(series);
    }

    /**
     * Returns typical price of the stock.
     * 
     * @param stock the stock
     * @return typical price of the stock
     */
    public static double getTypicalPrice(Stock stock) {
        return (stock.getHighPrice() + stock.getLowPrice() + stock.getLastPrice()) / 3.0;
    }
}
