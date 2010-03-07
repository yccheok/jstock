/*
 * StockHistoryOperator.java
 *
 * Created on May 25, 2007, 9:12 PM
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
import org.yccheok.jstock.engine.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.charting.TechnicalAnalysis;

/**
 *
 * @author yccheok
 */
public class StockHistoryOperator extends AbstractOperator {
        
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
    public StockHistoryOperator() {
        setStartDate(new Date());
        setEndDate(new Date());
        value = null;
        this.function = Function.Max;
        this.type = Type.OpenPrice;
    }
    
    @Override
    protected Object calculate() {
        return value;
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
        boolean valid = true;
        if (startDate != null && this.endDate != null) {
            if (startDate.after(endDate)) {
                valid = false;
                log.error("startDate " + startDate + " should not after endDate " + endDate);
            }
        }
        else {
            log.error("startDate or endDate cannot be null");
            valid = false;
        }

        if (valid == false) {
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

        java.util.Calendar startCalendar = new java.util.GregorianCalendar();
        startCalendar.setTime(startDate);
        java.util.Calendar endCalendar = new java.util.GregorianCalendar();
        endCalendar.setTime(endDate);

        // I am not sure MFI is depending on previous day data???
        if (this.function == Function.RSI || this.function == Function.EMA || this.function == Function.MFI) {
            java.util.Calendar oldCalendar = new java.util.GregorianCalendar();
            oldCalendar.setTime(startDate);
            oldCalendar.add(Calendar.DAY_OF_MONTH, -(int)org.yccheok.jstock.engine.Utils.getDifferenceInDays(endCalendar, startCalendar));
            while (true) {
                Stock stock = stockHistoryServer.getStock(oldCalendar);
                if (stock != null) {
                    stocks.add(stock);
                }
                oldCalendar.add(Calendar.DAY_OF_MONTH, 1);
                if(
                        startCalendar.get(Calendar.YEAR) == oldCalendar.get(Calendar.YEAR) &&
                        startCalendar.get(Calendar.MONTH) == oldCalendar.get(Calendar.MONTH) &&
                        startCalendar.get(Calendar.DATE) == oldCalendar.get(Calendar.DATE)
                    )
                {
                    break;
                }
            }
        }

        int day = 0;

        /* Fill up stocks. */
        while(true) {
            Stock stock = stockHistoryServer.getStock(startCalendar);
            if (stock != null) {
                stocks.add(stock);
                day++;
            }
            
            if(
                    startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) && 
                    startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) && 
                    startCalendar.get(Calendar.DATE) == endCalendar.get(Calendar.DATE)
                )
            {
                break;
            }
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
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
            switch(this.type)
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
        } // if (this.function == Function.MFI)
        
        double v = function == Function.Min ? Double.MAX_VALUE : 0.0;
        final int dataSize = values.size();
        
        if (dataSize == 0) {
            Object oldValue = this.value;
            this.value = null;
            if (Utils.equals(oldValue, value) == false) {
                this.firePropertyChange("value", oldValue, this.value);
            }
            return;
        }
        
        switch(function)
        {
            case Max:
                for(Double _value : values) {
                    v = Math.max(v, _value);
                }
                break;
                
            case Min:
                for(Double _value : values) {
                    v = Math.min(v, _value);
                }
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
                    v = v + Math.abs(_value - average);
                }
                v = v / (double)dataSize;
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
        
        this.value = new Double(v);
        
        if (Utils.equals(oldValue, value) == false) {
            this.firePropertyChange("value", oldValue, this.value);
        }
    }    
    
    public void setFunction(Function function) {
        Function oldFunction = this.function;
        this.function = function;                
                
        if(Utils.equals(oldFunction, this.function) == false) {
            this.firePropertyChange("attribute", oldFunction + " " + this.type, this.function + " " + this.type);
        }         
        
    }
    
    public Function getFunction() {
        return this.function;
    }
    
    @Override
    public int getNumOfInputConnector() {
        return 0;
    }    
  
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {

        
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if(startDate != null && this.endDate != null) {
            if(startDate.after(endDate)) {
                log.error("endDate " + endDate + " should not before startDate " + startDate);
                // Should we notify some GUIs so that they can pop up warning to warn user?
                return;
            }
        }
        
        this.endDate = endDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        Type oldType = this.type;
        this.type = type;
                
        if(Utils.equals(oldType, this.type) == false) {
            this.firePropertyChange("attribute", this.function + " " + oldType, this.function + " " + this.type);
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
        
    private Date startDate;
    private Date endDate;    
    private Object value;
    private Function function;
    private Type type;    
    
    private static final Log log = LogFactory.getLog(StockHistoryOperator.class);
}
