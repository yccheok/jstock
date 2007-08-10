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
public class StockHistoryOperator extends AbstractOperator {
        
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
    public StockHistoryOperator() {
        setStartDate(new Date());
        setEndDate(new Date());
        value = null;
        this.function = Function.Max;
        this.type = Type.OpenPrice;
    }
    
    protected Object calculate() {
        return value;
    }
    
    public void calculate(StockHistoryServer stockHistoryServer)
    {   
        java.util.List<Stock> stocks = new java.util.ArrayList<Stock>();
        java.util.List<Double> values = new java.util.ArrayList<Double>();

        java.util.Calendar startCalendar = new java.util.GregorianCalendar();
        startCalendar.setTime(startDate);
        java.util.Calendar endCalendar = new java.util.GregorianCalendar();
        endCalendar.setTime(endDate);
        
        while(true) {
            Stock stock = stockHistoryServer.getStock(startCalendar);
            if(stock != null)
                stocks.add(stock);
            
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
  
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if(startDate != null && this.endDate != null) {
            if(startDate.after(endDate)) {
                log.error("startDate " + startDate + " should not after endDate " + endDate);
                // Should we notify some GUIs so that they can pop up warning to warn user?
                return;
            }
        }
        
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if(startDate != null && this.endDate != null) {
            if(startDate.after(endDate)) {
                log.error("endDate " + endDate + " should not after startDate " + startDate);
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
        
    private Date startDate;
    private Date endDate;    
    private Object value;
    private Function function;
    private Type type;    
    
    private static final Log log = LogFactory.getLog(StockHistoryOperator.class);
}
