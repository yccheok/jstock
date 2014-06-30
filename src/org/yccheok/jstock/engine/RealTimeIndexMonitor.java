/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2014 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yccheok
 */
public class RealTimeIndexMonitor extends Subject<RealTimeIndexMonitor, java.util.List<Market>> {
    /** Creates a new instance of RealTimeIndexMonitor */
    public RealTimeIndexMonitor(int maxThread, int maxBucketSize, long delay) {
        realTimeStockMonitor = new RealTimeStockMonitor(maxThread, maxBucketSize, delay);
        
        realTimeStockMonitor.attach(getRealTimeStockMonitorObserver());
    }
    
    private Observer<RealTimeStockMonitor, List<Stock>> getRealTimeStockMonitorObserver() {
        return new Observer<RealTimeStockMonitor, List<Stock>>() {

            @Override
            public void update(RealTimeStockMonitor subject, List<Stock> stocks) {
                
                List<Market> markets = new ArrayList<Market>();
                for (Stock stock : stocks) {
                    Index index = indexMapping.get(stock.code);
                    if (index != null) {
                        markets.add(Market.newInstance(stock, index));
                    }
                }
                
                if (false == markets.isEmpty()) {
                    RealTimeIndexMonitor.this.notify(RealTimeIndexMonitor.this, markets);
                }
            }
            
        };
    }
    
    public synchronized boolean addIndex(Index index) {
        if (realTimeStockMonitor.addStockCode(index.code)) {
            indexMapping.put(index.code, index);
            return true;
        }
        return false;
    }
    
    public synchronized boolean isEmpty() {
        return realTimeStockMonitor.isEmpty();
    }
    
    public synchronized boolean clearIndices() {
        indexMapping.clear();        
        return realTimeStockMonitor.clearStockCodes();
    }
    
    public synchronized boolean removeIndex(Index index) {
        indexMapping.remove(index.code);
        return realTimeStockMonitor.removeStockCode(index.code);
    } 
    
    public synchronized void resume() {
        realTimeStockMonitor.resume();
    }

    public synchronized void suspend() {
        realTimeStockMonitor.suspend();
    } 
    
    public synchronized void startNewThreadsIfNecessary() {
        realTimeStockMonitor.startNewThreadsIfNecessary();
    }
    
    public synchronized void rebuild() {
        realTimeStockMonitor.rebuild();
    }
    
    public synchronized void stop() {
        realTimeStockMonitor.stop();
    }
    
    public synchronized void refresh() {
        realTimeStockMonitor.refresh();
    }
    
    public synchronized long getDelay() {
        return realTimeStockMonitor.getDelay();
    }
    
    public synchronized void setDelay(int delay) {
        realTimeStockMonitor.setDelay(delay);
    }
    
    public int getTotalScanned() {
        return realTimeStockMonitor.getTotalScanned();
    }
    
    private final Map<Code, Index> indexMapping = new ConcurrentHashMap<Code, Index>();
    private final RealTimeStockMonitor realTimeStockMonitor;
}
