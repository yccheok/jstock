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
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.analysis;

import java.util.*;
import org.yccheok.jstock.engine.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class StockRelativeHistoryOperator extends AbstractOperator {
    
    public enum Function
    {
        Max,
        Min,
        Average
    }
    
    public enum Type
    {
        OpenPrice,
        HighPrice,
        LowPrice,
        LastPrice,
        Volume
    }
    
    /** Creates a new instance of StockHistoryOperator */
    public StockRelativeHistoryOperator() {
        day = 1;
        value = null;
        this.function = Function.Max;
        this.type = Type.OpenPrice;
    }
    
    protected Object calculate() {
        return value;
    }
    
    public int getDay() {
        return day;
    }
    
    public void setDay(int day) {
        if(day < 0) return;
        
        this.day = day;
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
            this.firePropertyChange("attribute", this.function + " " + oldType, this.function + " " + this.type);
        }         
    }
      
    public void calculate(StockHistoryServer stockHistoryServer)
    {   
        java.util.List<Stock> stocks = new java.util.ArrayList<Stock>();
        java.util.List<Double> values = new java.util.ArrayList<Double>();
        
        final int numOfCalendar = stockHistoryServer.getNumOfCalendar();
        for(int i = 0; i < day; i++) {
            final int index = numOfCalendar - i - 1;
            if(index < 0) break;
            
            final Calendar calendar = stockHistoryServer.getCalendar(index);
            final Stock stock = stockHistoryServer.getStock(calendar);
            stocks.add(stock);
        }
        
        switch(type)
        {
            case OpenPrice:
                for(Stock stock : stocks)
                    values.add(stock.getOpenPrice());
                break;
                
            case HighPrice:
                for(Stock stock : stocks)
                    values.add(stock.getHighPrice());
                break;

            case LowPrice:
                for(Stock stock : stocks)
                    values.add(stock.getLowPrice());
                break;

            case LastPrice:
                for(Stock stock : stocks)
                    values.add(stock.getLastPrice());
                break;

            case Volume:
                // ???
                for(Stock stock : stocks)
                    values.add(new Double(stock.getVolume()));
                break;
                
            default:
                assert(false);
        }
        
        double v = 0.0;
        final int dataSize = values.size();
        
        if(dataSize == 0) {
            value = null;
            return;
        }
        
        if(function == Function.Min)
            v = Double.MAX_VALUE;
        
        switch(function)
        {
            case Max:
                for(Double value : values)
                    v = Math.max(v, value);
                break;
                
            case Min:
                for(Double value : values)
                    v = Math.min(v, value);
                break;

            case Average:
                for(Double value : values)
                    v = v + value;
                    
                v = v / (double)dataSize;
                            
                break;
                
            default:
                assert(false);
        }                 
        
        Object oldValue = this.value;
        
        this.value = new Double(v);
        
        if(Utils.equals(oldValue, value) == false)
            this.firePropertyChange("value", oldValue, this.value);
    }
    
    private Object value;
    private Function function;
    private Type type;    
    private int day;

    private static final Log log = LogFactory.getLog(StockRelativeHistoryOperator.class);        
}

