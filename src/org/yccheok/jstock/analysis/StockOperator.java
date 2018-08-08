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

package org.yccheok.jstock.analysis;

import org.yccheok.jstock.charting.TechnicalAnalysis;
import org.yccheok.jstock.engine.*;

/**
 *
 * @author yccheok
 */
public class StockOperator extends AbstractOperator {

    public enum Type
    {
        PrevPrice,
        OpenPrice,
        LastPrice,
        HighPrice,
        LowPrice,
        TypicalPrice,
        Volume,
        ChangePrice,
        ChagePricePercentage,
        LastVolume,
        BuyPrice,
        BuyQuantity,
        SellPrice,
        SellQuantity
    }
        
    /** Creates a new instance of StockOperator */
    public StockOperator() {
        this.stock = null;
        this.type = Type.LastPrice;
        this.cache = null;
    }
    
    @Override
    protected Object calculate()
    {
        return this.cache;
    }
    
    @Override
    public int getNumOfInputConnector() {
        return 0;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type)
    {
        Type oldType = this.type;
        this.type = type;
                
        if(Utils.equals(oldType, this.type) == false) {
            this.firePropertyChange("attribute", oldType, this.type);
        } 
    }

    public void setStock(Stock stock)
    {
        Object old = this.cache;
        this.stock = stock;
        this.cache = getValue(stock);
        
        if(Utils.equals(old, cache) == false) {
            this.firePropertyChange("value", old, this.cache);
        }
    }
    
    private Object getValue(Stock stock)
    {
        if (stock == null) return null;
        
        switch(type)
        {
            case PrevPrice:
                return new Double(stock.getPrevPrice());
            case OpenPrice:
                return new Double(stock.getOpenPrice());
            case LastPrice:
                return new Double(stock.getLastPrice());
            case HighPrice:
                return new Double(stock.getHighPrice());
            case LowPrice:
                return new Double(stock.getLowPrice());
            case TypicalPrice:
                return new Double(TechnicalAnalysis.getTypicalPrice(stock));
            case Volume:
                // TODO: CRITICAL LONG BUG REVISED NEEDED.
                return new Long(stock.getVolume());
            case ChangePrice:
                return new Double(stock.getChangePrice());
            case ChagePricePercentage:
                return new Double(stock.getChangePricePercentage());
            case LastVolume:
                return new Integer(stock.getLastVolume());
            case BuyPrice:
                return new Double(stock.getBuyPrice());
            case BuyQuantity:
                return new Integer(stock.getBuyQuantity());
            case SellPrice:
                return new Double(stock.getSellPrice());
            case SellQuantity:
                return new Integer(stock.getSellQuantity());
        }
        
        return null;
    }    
    
    @Override
    public Class getInputClass(int index) {
        return null;
    }

    @Override
    public Class getOutputClass(int index) {
        return Double.class;
    }

    private Stock stock;
    private Type type;
    private Object cache;
}
