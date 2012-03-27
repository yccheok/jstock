/*
 * StockRelativeHistoryOperator.java
 *
 * Created on June 18, 2007, 2:52 AM
 *
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

package org.yccheok.jstock.analysis;

import java.util.*;
import org.yccheok.jstock.charting.TechnicalAnalysis;
import org.yccheok.jstock.engine.*;

/**
 *
 * @author yccheok
 */
public class StockRelativeHistoryOperator extends AbstractOperator {
    
    public enum Function
    {
        Max,
        Min,
        Average,
        MeanDeviation,
        RSI,
        EMA,
        MFI
    }
    
    public enum Type
    {
        PrevPrice,
        OpenPrice,
        HighPrice,
        LowPrice,
        LastPrice,
        TypicalPrice,
        Volume,
        MarketCapital,
        SharesIssued
    }
    
    /** Creates a new instance of StockHistoryOperator */
    public StockRelativeHistoryOperator() {
        day = 1;
        skipDay = 0;
        value = null;
        this.function = Function.Max;
        this.type = Type.OpenPrice;
    }
    
    @Override
    protected Object calculate() {
        return value;
    }
    
    public int getDay() {
        return day;
    }
    
    public int getSkipDay() {
        return skipDay;
    }
    
    public void setDay(int day) {
        if (day < 0) return;

        int oldDay = this.day;
        this.day = day;

        if(oldDay != this.day) {
            this.firePropertyChange("attribute", oldDay + "d " + getSkipDayAsString(skipDay) + this.function + " " + this.type, this.day + "d " + getSkipDayAsString(skipDay) + this.function + " " + this.type);
        }
    }
    
    public void setSkipDay(int skipDay) {
        if (skipDay < 0) return;

        int oldSkipDay = this.skipDay;
        this.skipDay = skipDay;

        if(oldSkipDay != this.skipDay) {
            this.firePropertyChange("attribute", this.day + "d " + getSkipDayAsString(oldSkipDay) + this.function + " " + this.type, this.day + "d " + getSkipDayAsString(skipDay) + this.function + " " + this.type);
        }        
    }
    
    public void setFunction(Function function) {
        Function oldFunction = this.function;
        this.function = function;                
                
        if(Utils.equals(oldFunction, this.function) == false) {
            this.firePropertyChange("attribute", this.day + "d " + getSkipDayAsString(skipDay) + oldFunction + " " + this.type, this.day + "d " + getSkipDayAsString(skipDay) + this.function + " " + this.type);
        }         
        
    }
    
    public Function getFunction() {
        return this.function;
    }
    
    @Override
    public int getNumOfInputConnector() {
        return 0;
    }    
    
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        Type oldType = this.type;
        this.type = type;
                
        if(Utils.equals(oldType, this.type) == false) {
            this.firePropertyChange("attribute", this.day + "d " + getSkipDayAsString(skipDay) + this.function + " " + oldType, this.day + "d " + getSkipDayAsString(skipDay) + this.function + " " + this.type);
        }         
    }

    private double average(java.util.List<Double> values) {
        final int size = values.size();
        assert (size >= 0);
        Double sum = 0.0;
        for (Double v : values) {
            sum = sum + v;
        }
        return sum / size;
    }

    public void calculate(StockHistoryServer stockHistoryServer)
    {
        if (day <= 0 || skipDay < 0) {
            Object oldValue = this.value;
            this.value = null;
            if (Utils.equals(oldValue, value) == false) {
                this.firePropertyChange("value", oldValue, this.value);
            }
            return;
        }

        java.util.List<Stock> stocks = new java.util.ArrayList<Stock>();
        java.util.List<Double> values = new java.util.ArrayList<Double>();

        // For MFI usage.
        java.util.List<Double> highs = new java.util.ArrayList<Double>();
        java.util.List<Double> lows = new java.util.ArrayList<Double>();
        java.util.List<Double> closes = new java.util.ArrayList<Double>();
        // TODO: CRITICAL LONG BUG REVISED NEEDED.
        java.util.List<Long> volumes = new java.util.ArrayList<Long>();

        final int numOfCalendar = stockHistoryServer.getNumOfCalendar();
        /* Fill up stocks. */
        final int start;
        // I am not sure MFI is depending on previous day data???
        if (this.function == Function.EMA || function == Function.RSI || this.function == Function.MFI) {
            start = (numOfCalendar - (day << 1)) >= 0 ? numOfCalendar - (day << 1) : 0;
        }
        else {
            start = (numOfCalendar - day) >= 0 ? numOfCalendar - day : 0;
        }
        for (int i = Math.max(0, start - skipDay), ei = Math.max(0, numOfCalendar - skipDay); i < ei; i++) {
            final Calendar calendar = stockHistoryServer.getCalendar(i);
            final Stock stock = stockHistoryServer.getStock(calendar);
            stocks.add(stock);
        }

        if (this.function == Function.MFI) {
            for (Stock stock : stocks) {
                values.add(TechnicalAnalysis.getTypicalPrice(stock));
                highs.add(stock.getHighPrice());
                lows.add(stock.getLowPrice());
                closes.add(stock.getLastPrice());
                volumes.add(stock.getVolume());
            }
        }
        else {
            switch(type)
            {
                case PrevPrice:
                    for (Stock stock : stocks) {
                        values.add(stock.getPrevPrice());
                    }
                    break;

                case OpenPrice:
                    for (Stock stock : stocks) {
                        values.add(stock.getOpenPrice());
                    }
                    break;

                case HighPrice:
                    for (Stock stock : stocks) {
                        values.add(stock.getHighPrice());
                    }
                    break;

                case LowPrice:
                    for (Stock stock : stocks) {
                        values.add(stock.getLowPrice());
                    }
                    break;

                case LastPrice:
                    for (Stock stock : stocks) {
                        values.add(stock.getLastPrice());
                    }
                    break;

                case TypicalPrice:
                    for (Stock stock : stocks) {
                        values.add(TechnicalAnalysis.getTypicalPrice(stock));
                    }
                    break;

                case Volume:
                    // ???
                    for (Stock stock : stocks) {
                        values.add(new Double(stock.getVolume()));
                    }
                    break;

                case MarketCapital:
                    values.add(new Double(stockHistoryServer.getMarketCapital()));
                    break;

                case SharesIssued:
                    values.add(new Double(stockHistoryServer.getSharesIssued()));
                    break;

                default:
                    assert(false);
            }
        }   // if (this.function == Function.MFI)

        final int dataSize = values.size();
        
        if (dataSize == 0) {
            Object oldValue = this.value;
            this.value = null;
            if (Utils.equals(oldValue, value) == false) {
                this.firePropertyChange("value", oldValue, this.value);
            }
            return;
        }

        // Do not use primitive. As we do not want to perform auto unboxing
        // on TechnicalAnalysis's returned value. It might be null.
        Double v = function == Function.Min ? Double.MAX_VALUE : 0.0;
        // Use tmp_v to prevent frequent boxing/unboxing operation.
        double tmp_v = v;
        
        switch(function)
        {
            case Max:
                for (Double _value : values) {
                    tmp_v = Math.max(tmp_v, _value);
                }
                v = tmp_v;
                break;
                
            case Min:
                for (Double _value : values) {
                    tmp_v = Math.min(tmp_v, _value);
                }
                v = tmp_v;
                break;

            case Average:
                v = average(values);                          
                break;

            case MeanDeviation:
                double average = 0;
                for (Double _value : values) {
                    average = average + _value;
                }
                average = average / (double)dataSize;
                for (Double _value : values) {
                    tmp_v = tmp_v + Math.abs(_value - average);
                }
                tmp_v = tmp_v / (double)dataSize;
                v = tmp_v;
                break;

            case RSI:
                v = TechnicalAnalysis.createRSI(values, day);
                break;

            case EMA:
                v = TechnicalAnalysis.createEMA(values, day);
                break;

            case MFI:
                v = TechnicalAnalysis.createMFI(highs, lows, closes, volumes, day);
                break;
                
            default:
                assert(false);
        }                 
        
        Object oldValue = this.value;
        
        this.value = v;
        
        if (Utils.equals(oldValue, value) == false) {
            this.firePropertyChange("value", oldValue, this.value);
        }
    }
    
    @Override
    public Class getInputClass(int index) {
        return null;
    }

    @Override
    public Class getOutputClass(int index) {
        return Double.class;
    }

    private String getSkipDayAsString(int skipDay) {
        if (skipDay <= 0) {
            return "";
        }
        return "(-"+skipDay+") ";
    }
    private Object value;
    private Function function;
    private Type type;    
    private int day;
    private int skipDay;
}

