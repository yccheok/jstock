/*
 * OperatorIndicator.java
 *
 * Created on June 9, 2007, 5:16 PM
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
public class OperatorIndicator implements Indicator {
    
    /** Creates a new instance of OperatorIndicator */    
    public OperatorIndicator(String name) {
        this.name = name;
        this.stock = null;
    }
    
    public OperatorIndicator() {
        this("null");
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public void add(Operator operator) {
        operators.add(operator);

		// Whenever there is a new operator being added, we required to
		// perform history calculation again.
        stockHistoryCalculationDone = false;
    }
    
    public Operator get(int index) {
        return operators.get(index);
    }
    
    public int size() {
        return operators.size();
    }
    
    public Stock getStock() {
        return stock;
    }
    
    public void setStock(Stock stock)
    {
        if((sharesIssued == -1) || (marketCapital == -1)) {
            this.stock = stock;
        }
        else {
            this.stock = new StockEx(stock, marketCapital, sharesIssued);
        }
        
        for(Operator operator : operators) {
            if(operator instanceof StockOperator) {
                ((StockOperator)operator).setStock(stock);
            }
        }
    }
    
    @Override
    public void setStockHistoryServer(StockHistoryServer stockHistoryServer)
    {
        for(Operator operator : operators) {
            if(operator instanceof StockHistoryOperator) {
                /* Time consuming */
                ((StockHistoryOperator)operator).calculate(stockHistoryServer);
            }
            else if(operator instanceof StockRelativeHistoryOperator) {
                /* Time consuming */
                ((StockRelativeHistoryOperator)operator).calculate(stockHistoryServer);                
            }
        }

		// Indicate history calculation is done.
        stockHistoryCalculationDone = true;

        sharesIssued = stockHistoryServer.getSharesIssued();
        marketCapital = stockHistoryServer.getMarketCapital();
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        int numOfSinkOperator = 0;
        SinkOperator sinkOperator = null;
        
        for(Operator operator : operators) {
            if(operator instanceof SinkOperator) {                                
                numOfSinkOperator++;
                
                if(numOfSinkOperator != 1) return false;
                
                sinkOperator = (SinkOperator)operator;
            }
        }
        
        if(numOfSinkOperator != 1)
            return false;
        
        return (sinkOperator.getNumOfInputConnection() == 1 && sinkOperator.getNumOfOutputConnection() == 0);
    }
    
    public void preCalculate() {
        for(Operator operator : operators) {
            operator.clear();
        }
        
        for(Operator operator : operators) {
            operator.pull();
        }        
    }
    
    public boolean isStockHistoryServerNeeded() {
        for(Operator operator : operators) {
            if((operator instanceof StockHistoryOperator) || (operator instanceof StockRelativeHistoryOperator)) {
                return true;
            }
        }        
        
        return false;
    }
    
    public boolean isTriggered()
    {        
        for(Operator operator : operators) {
            if(operator instanceof StockOperator) {
                operator.pull();
            }
        }
        
        for(Operator operator : operators) {
            if(operator instanceof SinkOperator) {
                Object value = ((SinkOperator)operator).getValue();
                
                if(value instanceof Boolean) {
                    return ((Boolean)value).booleanValue();
                }
                else {
                    log.error(name + " " + stock.getSymbol() + " Sink operator should return boolean result.");			
                }
            }
        }
        
        log.error(name + " " + stock.getSymbol() + " No sink operator had been found. Invalid indicator.");        
        
        return false;
    }


    @Override
    public Duration getNeededStockHistoryDuration() {
        if (isStockHistoryServerNeeded() == false)
        {
			// Returns 0 day duration, if there are no history information needed.
            return Duration.getTodayDurationByDays(0);
        }

        Duration duration = Duration.getTodayDurationByDays(0);

        for (Operator operator : operators) {
            if (operator instanceof StockHistoryOperator) {
                final StockHistoryOperator stockHistoryOperator = (StockHistoryOperator)operator;
                Date start = stockHistoryOperator.getStartDate();
                Date end = stockHistoryOperator.getEndDate();
                duration = duration.getUnionDuration(new Duration(start, end));
            }
            else if (operator instanceof StockRelativeHistoryOperator) {
                final StockRelativeHistoryOperator stockRelativeHistoryOperator = (StockRelativeHistoryOperator)operator;
                int days = stockRelativeHistoryOperator.getDay();

                // Sometimes, there are no stock information during holidays. We will double up
                // the days, so that we really able to obtain n days data.
                duration = duration.getUnionDuration(Duration.getTodayDurationByDays(days * 2));
            }
        }

        return duration;
    }

    @Override
    public boolean isStockHistoryCalculationDone() {
        if (isStockHistoryServerNeeded() == false)
        {
            return true;
        }

        return stockHistoryCalculationDone;
    }

    private List<Operator> operators = new ArrayList<Operator>();
    private String name;
    private Stock stock;
    private volatile boolean stockHistoryCalculationDone = false;

    // So that we are able to convert Stock to StockEx.
    private long sharesIssued = -1;
    private long marketCapital = -1;
    
    private static final Log log = LogFactory.getLog(OperatorIndicator.class);
}
