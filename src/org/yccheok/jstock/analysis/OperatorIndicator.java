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

import java.util.*;
import org.yccheok.jstock.engine.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class OperatorIndicator implements Indicator {
    public enum Type {
        AlertIndicator,     // With 1 and only 1 sink operator. The input of sink operator
                            // must be connected. The output of sink operator must leave
                            // unconnected.
        ModuleIndicator,    // Must not contain any sink operator. Must contain 0 to many diode
                            // operators, with their outputs connected, inputs leave unconnected. Must contain 1 and only
                            // 1 diode operator, with its input connected, output leave unconnected.
        InvalidIndicator    // Other of the above is considered as invalid.
    }

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
    
    @Override
    public Stock getStock() {
        // Should we allow the possibility to return null stock, or shall we just
        // return empty stock?
        return stock;
    }

    @Override
    public void setStock(Stock stock)
    {
        if ((sharesIssued == -1) || (marketCapital == -1)) {
            this.stock = stock;
        }
        else {
            this.stock = new StockEx(stock, marketCapital, sharesIssued);
        }
        
        for (Operator operator : operators) {
            if(operator instanceof StockOperator) {
                ((StockOperator)operator).setStock(stock);
            }
        }
    }
    
    @Override
    public void setStockHistoryServer(StockHistoryServer stockHistoryServer)
    {
        for (Operator operator : operators) {
            if (operator instanceof StockHistoryOperator) {
                /* Time consuming */
                ((StockHistoryOperator)operator).calculate(stockHistoryServer);
            }
            else if (operator instanceof StockRelativeHistoryOperator) {
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
    
    public Type getType() {
        // Check whether there is any AlertIndicator.
        int numOfSinkOperator = 0;
        SinkOperator sinkOperator = null;
        for (Operator operator : operators) {
            if (operator instanceof SinkOperator) {
                numOfSinkOperator++;
                if (numOfSinkOperator != 1) {
                    return Type.InvalidIndicator;
                }
                sinkOperator = (SinkOperator)operator;
            }
        }

        if (numOfSinkOperator == 1) {
            if (sinkOperator.getNumOfInputConnection() == 1 && sinkOperator.getNumOfOutputConnection() == 0) {
                return Type.AlertIndicator;
            }
            return Type.InvalidIndicator;
        }
        
        final List <DiodeOperator> inputDiodeOperators = new ArrayList<DiodeOperator>();
        final List <DiodeOperator> outputDiodeOperators = new ArrayList<DiodeOperator>();

        for (Operator operator : operators) {
            if (operator instanceof DiodeOperator) {
                final DiodeOperator diodeOperator = (DiodeOperator)operator;
                if (diodeOperator.getNumOfInputConnection() == 1 && diodeOperator.getNumOfOutputConnection() == 0) {
                    outputDiodeOperators.add(diodeOperator);
                }
                else if (diodeOperator.getNumOfInputConnection() == 0 && diodeOperator.getNumOfOutputConnection() == 1) {
                    inputDiodeOperators.add(diodeOperator);
                }
                else {
                    // Shall I return as Type.InvalidIndicator?
                }
            }
        }
        if (inputDiodeOperators.size() >= 0 && outputDiodeOperators.size() == 1) {
            return Type.ModuleIndicator;
        }
        return Type.InvalidIndicator;
    }
    
    public void preCalculate() {
        for (Operator operator : operators) {
            operator.clear();
        }
        
        for (Operator operator : operators) {
            operator.pull();
        }        
    }

    public boolean isStockNeeded() {
        for (Operator operator : operators) {
            if (operator instanceof StockOperator) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isStockHistoryServerNeeded() {
        for (Operator operator : operators) {
            if ((operator instanceof StockHistoryOperator) || (operator instanceof StockRelativeHistoryOperator)) {
                return true;
            }
        }        
        return false;
    }
    
    @Override
    public boolean isTriggered()
    {        
        for (Operator operator : operators) {
            if (operator instanceof StockOperator) {
                operator.pull();
            }
        }        
        for (Operator operator : operators) {
            if(operator instanceof SinkOperator) {
                Object value = ((SinkOperator)operator).getValue();
                
                if( value instanceof Boolean) {
                    return ((Boolean)value).booleanValue();
                }
                else {
                    log.error("Sink operator should return boolean result.");
                }
            }
        }
        // No sink operator found. Just return false.
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
                long d = new Duration(start, end).getDurationInDays();
                // TODO:
                // Loss of precision?!
                int days = stockHistoryOperator.getRequiredHistorySize((int)d);
                duration = duration.getUnionDuration(Duration.getDurationByDays(end, days));
            } else if (operator instanceof StockRelativeHistoryOperator) {
                final StockRelativeHistoryOperator stockRelativeHistoryOperator = (StockRelativeHistoryOperator)operator;
                int days = stockRelativeHistoryOperator.getRequiredHistorySize() + stockRelativeHistoryOperator.getSkipDay();

                // Sometimes, there are no stock information during holidays. We will double up
                // the days, so that we really able to obtain n days data.
                // If the simulation runs on Monday, we must a least have 1 last Friday data.
                duration = duration.getUnionDuration(Duration.getTodayDurationByDays(Math.max(3, days * 2)));
            }
        }
        
        /* times 2, for technical analysis usage. */
        return duration.getUnionDuration(Duration.getTodayDurationByDays((int)duration.getDurationInDays() * 2));
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
