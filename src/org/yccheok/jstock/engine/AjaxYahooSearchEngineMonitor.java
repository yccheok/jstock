/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides asynchronous search based on AjaxYahooSearchEngine.
 *
 * @author yccheok
 */
public class AjaxYahooSearchEngineMonitor extends Subject<AjaxYahooSearchEngineMonitor, ResultSetType> {
    /**
     * Creates an instance of AjaxYahooSearchEngineMonitor.
     */
    public AjaxYahooSearchEngineMonitor() {
        executor.submit(new SearchTask());
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
                    List<ResultType> results = searchEngine.searchAll(string);
                    final ResultSetType resultSet = ResultSetType.newInstance(string, results);
                    // Notify all observers.
                    AjaxYahooSearchEngineMonitor.this.notify(AjaxYahooSearchEngineMonitor.this, resultSet);
                } catch (InterruptedException ex) {
                    log.error(null, ex);
                    // Error occurs. Stop immediately.
                    break;
                }                
            }
        }
    }
    private final SearchEngine<ResultType> searchEngine = new AjaxYahooSearchEngine();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // 128 is just a magic number.
    private final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(128);

    private static final Log log = LogFactory.getLog(AjaxYahooSearchEngineMonitor.class);

}
