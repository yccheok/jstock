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
        EMA
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
    
    public void setDay(int day) {
        if (day < 0) return;

        int oldDay = this.day;
        this.day = day;

        if(oldDay != this.day) {
            this.firePropertyChange("attribute", oldDay + "d " + this.function + " " + this.type, this.day + "d " + this.function + " " + this.type);
        }
    }
    
    public void setFunction(Function function) {
        Function oldFunction = this.function;
        this.function = function;                
                
        if(Utils.equals(oldFunction, this.function) == false) {
            this.firePropertyChange("attribute", this.day + "d " + oldFunction + " " + this.type, this.day + "d " + this.function + " " + this.type);
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
            this.firePropertyChange("attribute", this.day + "d " + this.function + " " + oldType, this.day + "d " + this.function + " " + this.type);
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

    private Double EMACalculation(java.util.List<Double> values, java.util.List<Double> prevValues) {
        final int size = values.size();

        if (size == 0) {
            return null;
        }

        double previous = prevValues.size() == 0 ? values.get(0) : average(prevValues);
        double smoothing_factor = 2.0 / (1.0 + size);

        for (Double current : values) {
            final double EMA = ((current - previous) * smoothing_factor) + previous;
            previous = EMA;
        }

        return previous;
    }

    private Double RSICalculation(java.util.List<Double> values, java.util.List<Double> prevValues) {
        if (values.size() == 0) {
            return null;
        }
        double prevValue = prevValues.size() == 0 ? Double.MAX_VALUE : prevValues.get(0);

        double up = 0.0;
        double down = 0.0;
        for (Double v : values) {
            double change = prevValue == Double.MAX_VALUE ? 0 : v - prevValue;
            if (change > 0.0) {
                up = up + change;
            }
            else if (change < 0.0) {
                down = down + change;
            }
            prevValue = v;
        }
        // Remove -ve sign.
        down = Math.abs(down);

        // But, what if up and down are 0.0 during the same time?
        // If there is no price changes during the period, RSI = 100.
        final double RS = down == 0.0 ? Double.MAX_VALUE : up / down;
        final double RSI = 100.0 - (100.0 / (1.0 + RS));

        return new Double(RSI);
    }

    public void calculate(StockHistoryServer stockHistoryServer)
    {
        if (day == 0) {
            Object oldValue = this.value;
            this.value = null;
            if (Utils.equals(oldValue, value) == false) {
                this.firePropertyChange("value", oldValue, this.value);
            }
            return;
        }

        java.util.List<Stock> stocks = new java.util.ArrayList<Stock>();
        /* Will be used for EMA and RSI. */
        java.util.List<Stock> prevStocks = new java.util.ArrayList<Stock>();
        java.util.List<Double> values = new java.util.ArrayList<Double>();
        /* Will be used for EMA and RSI. */
        java.util.List<Double> prevValues = new java.util.ArrayList<Double>();
        
        final int numOfCalendar = stockHistoryServer.getNumOfCalendar();
        /* Fill up stocks. */
        final int start = (numOfCalendar - day) >= 0 ? numOfCalendar - day : 0;
        for (int i = start ; i < numOfCalendar; i++) {
            final Calendar calendar = stockHistoryServer.getCalendar(i);
            final Stock stock = stockHistoryServer.getStock(calendar);
            stocks.add(stock);
        }

        /* Fill up prevStocks. */
        if (this.function == Function.RSI || this.function == Function.EMA) {
            final int requiredSize = this.function == Function.RSI ? 1 : stocks.size();
            int index = start - 1;
            while (true) {
                final Calendar calendar = stockHistoryServer.getCalendar(index);
                Stock stock = stockHistoryServer.getStock(calendar);
                if(stock != null) {
                    prevStocks.add(stock);
                }
                if (prevStocks.size() >= requiredSize) {
                    break;
                }
                index--;
            }
        }

        switch(type)
        {
            case PrevPrice:
                for (Stock stock : stocks) {
                    values.add(stock.getPrevPrice());
                }
                for (Stock stock : prevStocks) {
                    prevValues.add(stock.getPrevPrice());
                }
                break;

            case OpenPrice:
                for (Stock stock : stocks) {
                    values.add(stock.getOpenPrice());
                }
                for (Stock stock : prevStocks) {
                    prevValues.add(stock.getOpenPrice());
                }
                break;

            case HighPrice:
                for (Stock stock : stocks) {
                    values.add(stock.getHighPrice());
                }
                for (Stock stock : prevStocks) {
                    prevValues.add(stock.getHighPrice());
                }
                break;

            case LowPrice:
                for (Stock stock : stocks) {
                    values.add(stock.getLowPrice());
                }
                for (Stock stock : prevStocks) {
                    prevValues.add(stock.getLowPrice());
                }
                break;

            case LastPrice:
                for (Stock stock : stocks) {
                    values.add(stock.getLastPrice());
                }
                for (Stock stock : prevStocks) {
                    prevValues.add(stock.getLastPrice());
                }
                break;

            case TypicalPrice:
                for (Stock stock : stocks) {
                    values.add(TechnicalAnalysis.getTypicalPrice(stock));
                }
                for (Stock stock : prevStocks) {
                    prevValues.add(TechnicalAnalysis.getTypicalPrice(stock));
                }
                break;

            case Volume:
                // ???
                for (Stock stock : stocks) {
                    values.add(new Double(stock.getVolume()));
                }
                for (Stock stock : prevStocks) {
                    prevValues.add(new Double(stock.getVolume()));
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
                for (Double _value : values) {
                    v = Math.max(v, _value);
                }
                break;
                
            case Min:
                for (Double _value : values) {
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
                v = RSICalculation(values, prevValues);
                break;

            case EMA:
                v = EMACalculation(values, prevValues);
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
    
    @Override
    public Class getInputClass(int index) {
        return null;
    }

    @Override
    public Class getOutputClass(int index) {
        return Double.class;
    }

    private Object value;
    private Function function;
    private Type type;    
    private int day;   
}

