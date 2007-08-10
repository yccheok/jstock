/*
 * OperatorConnectionValidator.java
 *
 * Created on June 11, 2007, 4:06 AM
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

/**
 *
 * @author yccheok
 */
public class OperatorConnectionValidator {
    
    /* First element in every row is from, the rest is to. */
    private static final Class[][] table = {
        {SinkOperator.class},        
        {LogicalOperator.class, LogicalOperator.class, SinkOperator.class},
        {ArithmeticOperator.class, ArithmeticOperator.class, EqualityOperator.class},        
        {DoubleConstantOperator.class, ArithmeticOperator.class, EqualityOperator.class},
        {EqualityOperator.class, LogicalOperator.class, SinkOperator.class},        
        {StockHistoryOperator.class, ArithmeticOperator.class, EqualityOperator.class},
        {StockRelativeHistoryOperator.class, ArithmeticOperator.class, EqualityOperator.class},
        {StockOperator.class, ArithmeticOperator.class, EqualityOperator.class}
    };
      
    private static Map<Class, List<Class>> m = new HashMap<Class, List<Class>>();
    
    /** Creates a new instance of OperatorConnectionValidator */
    public OperatorConnectionValidator() {
    }
    
    public static boolean canConnect(Operator from, Operator to) {
        if(m.size() == 0) {
            for(int row = 0; row < table.length; row++) {
                
                List<Class> l = new ArrayList<Class>();
                
                for(int col = 1; col < table[row].length; col++) {
                    l.add(table[row][col]);
                }
                
                m.put(table[row][0], l);
            }
        }
        
        List<Class> l = m.get(from.getClass());
        if(l == null) return false;
        
        if(l.contains(to.getClass()))
            return true;
        
        return false;
    }
}
