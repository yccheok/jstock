/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.engine;

import java.util.*;

/**
 *
 * @author yccheok
 */
public class MalaysiaMarket implements Market {
    
    /** Creates a new instance of MalaysiaMarket */
    public MalaysiaMarket(
            double mainBoardIndex, double mainBoardChange, 
            double secondBoardIndex, double secondBoardChange,
            double mesdaqIndex, double mesdaqChange,
            int up, int down, int unchange, long volume,
            double value
            ) 
    {
        indexMap.put(Index.KLSE, mainBoardIndex);
        indexMap.put(Index.Second, secondBoardIndex);
        indexMap.put(Index.Mesdaq, mesdaqIndex);

        changeMap.put(Index.KLSE, mainBoardChange);
        changeMap.put(Index.Second, secondBoardChange);
        changeMap.put(Index.Mesdaq, mesdaqChange);
        
        numMap.put(ChangeType.Up, up);
        numMap.put(ChangeType.Down, down);
        numMap.put(ChangeType.Unchange, unchange);    
        
        this.volume = volume;
        this.value = value;
    }
    
    @Override
    public double getIndex(Index index)
    {
        Double d = indexMap.get(index);
        if (d == null) return 0.0;
        return d;
    }
    
    @Override
    public double getChange(Index index)
    {
        Double d = changeMap.get(index);
        if (d == null) return 0.0;
        return d;
    }
    
    @Override
    public int getNumOfStockChange(ChangeType type)
    {
        Integer i = numMap.get(type);
        if (i == null) return 0;
        return i;
    }

    @Override
    public long getVolume()
    {
        return volume;
    }
    
    @Override
    public double getValue() {
        return value;
    }
    
    private Map<Index, Double> indexMap = new HashMap<Index, Double>();
    private Map<Index, Double> changeMap = new HashMap<Index, Double>();
    private Map<ChangeType, Integer> numMap = new HashMap<ChangeType, Integer>();
    private long volume;
    private double value;

    @Override
    public Country getCountry() {
        return Country.Malaysia;
    }
}
