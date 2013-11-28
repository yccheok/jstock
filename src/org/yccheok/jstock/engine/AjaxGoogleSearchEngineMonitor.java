/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class AjaxGoogleSearchEngineMonitor extends Subject<AjaxGoogleSearchEngineMonitor, MatchSetType> {
    /**
     * Creates an instance of AjaxYahooSearchEngineMonitor.
     */
    public AjaxGoogleSearchEngineMonitor() {
        executor.submit(new AjaxGoogleSearchEngineMonitor.SearchTask());
    }

    /**
     * Removes all previous elements, and inserts the specified element into
     * this queue, waiting if necessary for space to become available.
     * 
     * @param string the searched string
     * @throws RuntimeException if <code>stop</code> has been called before
     */
    public synchronized void clearAndPut(String string) {
        if (executor.isShutdown()) {
            throw new RuntimeException("Executor is shutdown.");
        }
        blockingQueue.clear();
        try {
            blockingQueue.put(string);
        } catch (InterruptedException ex) {
            log.error(null, ex);
        }
    }

    /**
     * Stop this monitor from running. After stopping, this monitor can no
     * longer be reused.
     */
    public void stop() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            log.error(null, ex);
        }
    }

    private class SearchTask implements Runnable {
        @Override
        public void run() {
            String string;
            while (!executor.isShutdown()) {
                try {
                    string = blockingQueue.take();
                    
                    List<String> queries = new ArrayList<String>();
                    queries.add(string);
                    
                    Set<String> exchSet = new HashSet<String>();
                    Set<String> codeSet = new HashSet<String>();
                    List<MatchType> matchTypes = new ArrayList<MatchType>();
                    
                    for (String exch : exchs) {
                        queries.add(exch + ":" + string);
                        exchSet.add(exch);
                    }
                    
                    for (String query : queries) {
                        List<MatchType> results = searchEngine.searchAll(query);
                        for (MatchType matchType : results) {
                            if (false == exchSet.contains(matchType.e)) {
                                continue;
                            }
                            
                            String code = matchType.e + ":" + matchType.t;
                            if (codeSet.contains(code)) {
                                continue;
                            }
                            
                            codeSet.add(code);  
                            matchTypes.add(matchType);
                        }
                    }
                    
                    // Sorting.
                    java.util.Collections.sort(matchTypes, new Comparator<MatchType>() {
                        @Override
                        public int compare(MatchType o1, MatchType o2) {
                            return o1.t.compareTo(o2.t);
                        }
                    });
                    
                    final MatchSetType matchSet = MatchSetType.newInstance(string, matchTypes);
                    // Notify all observers.
                    AjaxGoogleSearchEngineMonitor.this.notify(AjaxGoogleSearchEngineMonitor.this, matchSet);
                } catch (InterruptedException ex) {
                    log.error(null, ex);
                    // Error occurs. Stop immediately.
                    break;
                }                
            }
        }
    }
    
    public void setExchs(List<String> exchs) {
        this.exchs = exchs;
    }
    
    private List<String> exchs;
    
    private final SearchEngine<MatchType> searchEngine = new AjaxGoogleSearchEngine();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // 128 is just a magic number.
    private final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(128);

    private static final Log log = LogFactory.getLog(AjaxGoogleSearchEngineMonitor.class);
}
