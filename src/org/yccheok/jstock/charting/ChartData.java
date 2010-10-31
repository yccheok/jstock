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

package org.yccheok.jstock.charting;

/**
 * Data structure to hold charting point.
 *
 * @author yccheok
 */
public class ChartData {
    private final double prevPrice;
    private final double openPrice;
    private final double lastPrice;
    private final double highPrice;
    private final double lowPrice;
    private final long volume;
    // We choose not to use either Calendar or Joda DateTime, as we feel is too
    // heavy weight. We do not need time zone information.
    private final long timestamp;

    private ChartData(double prevPrice, double openPrice, double lastPrice, double highPrice, double lowPrice, long volume, long timestamp) {
        this.prevPrice = prevPrice;
        this.openPrice = openPrice;
        this.lastPrice = lastPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    /**
     * Creates an instance of chart data.
     *
     * @param prevPrice previous price
     * @param openPrice open price
     * @param lastPrice last price
     * @param highPrice high price
     * @param lowPrice low price
     * @param volume the volume
     * @param timestamp the timestamp
     * @return an instance of chart data
     */
    public static ChartData newInstance(double prevPrice, double openPrice, double lastPrice, double highPrice, double lowPrice, long volume, long timestamp) {
        return new ChartData(prevPrice, openPrice, lastPrice, highPrice, lowPrice, volume, timestamp);
    }
    
    /**
     * @return the prevPrice
     */
    public double getPrevPrice() {
        return prevPrice;
    }

    /**
     * @return the openPrice
     */
    public double getOpenPrice() {
        return openPrice;
    }

    /**
     * @return the lastPrice
     */
    public double getLastPrice() {
        return lastPrice;
    }

    /**
     * @return the highPrice
     */
    public double getHighPrice() {
        return highPrice;
    }

    /**
     * @return the lowPrice
     */
    public double getLowPrice() {
        return lowPrice;
    }

    /**
     * @return the volume
     */
    public long getVolume() {
        return volume;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
}
