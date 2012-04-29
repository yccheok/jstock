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

import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author yccheok
 */
public class MACD {

    public static class Period {
        public final int fastPeriod;
        public final int slowPeriod;
        public final int period;
        
        private Period(int fastPeriod, int slowPeriod, int period) {
            this.fastPeriod = fastPeriod;
            this.slowPeriod = slowPeriod;
            this.period = period;                    
        }
        
        public static Period newInstance(int fastPeriod, int slowPeriod, int period) {
            return new Period(fastPeriod, slowPeriod, period);
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + fastPeriod;
            result = 31 * result + slowPeriod;
            result = 31 * result + period;            
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Period)) {
                return false;
            }

            Period _period = (Period)o;
            return this.fastPeriod == _period.fastPeriod && this.slowPeriod == _period.slowPeriod && this.period == _period.period;
        }        
    }
    
    public static class ChartResult {
        public final XYDataset outMACD;
        public final XYDataset outMACDSignal;
        public final XYDataset outMACDHist;
        
        private ChartResult(XYDataset outMACD, XYDataset outMACDSignal, XYDataset outMACDHist) {
            this.outMACD = outMACD;
            this.outMACDSignal = outMACDSignal;
            this.outMACDHist = outMACDHist;            
        }
        
        public static ChartResult newInstance(XYDataset outMACD, XYDataset outMACDSignal, XYDataset outMACDHist) {
            return new ChartResult(outMACD, outMACDSignal, outMACDHist);
        }
    }
    
    public static class Result {
        public final double outMACD;
        public final double outMACDSignal;
        public final double outMACDHist;
        
        private Result(double outMACD, double outMACDSignal, double outMACDHist) {
            this.outMACD = outMACD;
            this.outMACDSignal = outMACDSignal;
            this.outMACDHist = outMACDHist;
        }
        
        public static Result newInstance(double outMACD, double outMACDSignal, double outMACDHist) {
            return new Result(outMACD, outMACDSignal, outMACDHist);
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            long _outMACD = Double.doubleToLongBits(outMACD);
            long _outMACDSignal = Double.doubleToLongBits(outMACDSignal);
            long _outMACDHist = Double.doubleToLongBits(outMACDHist);
            result = 31 * result + (int)(_outMACD ^ (_outMACD >>> 32));
            result = 31 * result + (int)(_outMACDSignal ^ (_outMACDSignal >>> 32));
            result = 31 * result + (int)(_outMACDHist ^ (_outMACDHist >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Result)) {
                return false;
            }

            Result macdResult = (Result)o;
            return this.outMACD == macdResult.outMACD && this.outMACDSignal == macdResult.outMACDSignal && this.outMACDHist == macdResult.outMACDHist;
        }        
    }    
}
