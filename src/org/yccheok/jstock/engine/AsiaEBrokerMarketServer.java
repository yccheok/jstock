/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.portfolio.Builder;

/**
 *
 * @author yccheok
 */
public class AsiaEBrokerMarketServer implements MarketServer {

    private static final class AsiaEBrokerMarketBuilder implements Builder<AsiaEBrokerMarket> {
        private int up = 0;
        private int down = 0;
        private int unchange = 0;
        private long volume = 0;
        private double value = 0.0;
        private Map<Index, Double> index_value = new HashMap<Index, Double>();
        private Map<Index, Double> index_change = new HashMap<Index, Double>();

        public AsiaEBrokerMarketBuilder up(int up) {
            this.up = up;
            return this;
        }

        public AsiaEBrokerMarketBuilder down(int down) {
            this.down = down;
            return this;
        }

        public AsiaEBrokerMarketBuilder unchange(int unchange) {
            this.unchange = unchange;
            return this;
        }

        public AsiaEBrokerMarketBuilder volume(long volume) {
            this.volume = volume;
            return this;
        }

        public AsiaEBrokerMarketBuilder value(double value) {
            this.value = value;
            return this;
        }

        public AsiaEBrokerMarketBuilder index(Index index, double value) {
            this.index_value.put(index, value);
            return this;
        }

        public AsiaEBrokerMarketBuilder change(Index index, double change) {
            this.index_change.put(index, change);
            return this;
        }

        @Override
        public AsiaEBrokerMarket build() {
            return new AsiaEBrokerMarket(this);
        }

    }

    private static final class AsiaEBrokerMarket implements Market {
        private final int up;
        private final int down;
        private final int unchange;
        private final long volume;
        private final double value;
        private final Map<Index, Double> index_value;
        private final Map<Index, Double> index_change;

        public AsiaEBrokerMarket(AsiaEBrokerMarketBuilder builder) {
            this.up = builder.up;
            this.down = builder.down;
            this.unchange = builder.unchange;
            this.volume = builder.volume;
            this.value = builder.value;
            index_value = new HashMap<Index, Double>(builder.index_value);
            index_change = new HashMap<Index, Double>(builder.index_change);
        }

        @Override
        public double getIndex(Index index) {
            final Double v = this.index_value.get(index);
            if (v == null) {
                return 0.0;
            }
            return v;
        }

        @Override
        public double getChange(Index index) {
            final Double v = this.index_change.get(index);
            if (v == null) {
                return 0.0;
            }
            return v;
        }

        @Override
        public int getNumOfStockChange(ChangeType type) {
            switch(type) {
                case Up:
                    return this.up;
                case Down:
                    return this.down;
                case Unchange:
                    return this.unchange;
                default:
                    assert(false);
            }
            return 0;
        }

        @Override
        public long getVolume() {
            return this.volume;
        }

        @Override
        public double getValue() {
            return this.value;
        }

        @Override
        public Country getCountry() {
            return Country.Malaysia;
        }
    }

    @Override
    public Market getMarket() {
        this.initServers();
        
        final AsiaEBrokerMarketBuilder builder = new AsiaEBrokerMarketBuilder();
        boolean summary_ok = false;
        boolean index_ok = false;
        int summary_sever_index = -1;
        int index_sever_index = -1;

        summary:
        for (String server : servers) {
            ++summary_sever_index;

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            final String summary = org.yccheok.jstock.gui.Utils.getPOSTResponseBodyAsStringBasedOnProxyAuthOption(server + SUMMARY_RESOURCE, SUMMARY_FORM_DATA);
            if (summary == null) {
                continue;
            }
            final String[] tokens = summary.split("\r\n|\r|\n");
            for (String token : tokens) {
                String[] token_elements = token.split(",");
                if (token_elements.length <= MAX_SUMMARY_TOKEN_INDEX) {
                    continue;
                }

                int up = 0;
                int down = 0;
                int unchange = 0;
                long volume = 0;
                double value = 0.0;
                try {
                    up = Integer.parseInt(token_elements[UP_TOKEN_INDEX]);
                    down = Integer.parseInt(token_elements[DOWN_TOKEN_INDEX]);
                    unchange = Integer.parseInt(token_elements[UNCHANGED_TOKEN_INDEX]);
                    volume = Long.parseLong(token_elements[VOLUME_TOKEN_INDEX]);
                    value = Double.parseDouble(token_elements[VALUE_TOKEN_INDEX]);
                }
                catch (NumberFormatException exp) {
                    log.error(null, exp);
                    continue;
                }
                builder.up(up).down(down).unchange(unchange).volume(volume).value(value);
                summary_ok = true;
                break summary;
            }
        }

        index:
        for (String server : servers) {
            ++index_sever_index;

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            final String index = org.yccheok.jstock.gui.Utils.getPOSTResponseBodyAsStringBasedOnProxyAuthOption(server + INDEX_RESOURCE, INDEX_FORM_DATA);
            if (index == null) {
                continue;
            }
            final String[] tokens = index.split("\r\n|\r|\n");
            for (String token : tokens) {
                if (false == token.startsWith("020000000.KL")) {
                    continue;
                }
                String[] token_elements = token.split(",");
                if (token_elements.length <= MAX_INDEX_TOKEN_INDEX) {
                    continue;
                }

                double current_index = 0;
                double opening_index = 0;
                try {
                    current_index = Double.parseDouble(token_elements[CURRENT_INDEX_TOKEN_INDEX]);
                    opening_index = Double.parseDouble(token_elements[OPENING_INDEX_TOKEN_INDEX]);
                }
                catch (NumberFormatException exp) {
                    log.error(null, exp);
                    continue;
                }
                builder.index(Index.KLSE, current_index).change(Index.KLSE, current_index - opening_index);
                index_ok = true;
                break index;
            }
        }

        if (summary_ok && index_ok) {
            // Sort the best server.
            if (bestServerAlreadySorted == false) {
                synchronized(servers_lock) {
                    if (bestServerAlreadySorted == false) {
                        bestServerAlreadySorted = true;
                        assert(summary_sever_index >= 0);
                        assert(index_sever_index >= 0);
                        final int best_index = Math.max(summary_sever_index, summary_sever_index);
                        String tmp = servers.get(0);
                        servers.set(0, servers.get(best_index));
                        servers.set(best_index, tmp);
                    }
                }
            }
            return builder.build();
        }
        return null;
    }

    private void initServers() {
        // Already initialized. Return early.
        if (this.servers != null) {
            return;
        }
        synchronized(servers_lock) {
            // Already initialized. Return early.
            if (this.servers != null) {
                return;
            }

            this.servers = Utils.getAsiaEBrokerMarketServers();
        }
    }

    private static final String SUMMARY_RESOURCE = "/%5bvUpJYKw4QvGRMBmhATUxRwv4JrU9aDnwNEuangVyy6OuHxi2YiY=%5dSector?";
    private static final String SUMMARY_FORM_DATA = "[SUMMARY]=KL";
    private static final String INDEX_RESOURCE = "/%5bvUpJYKw4QvGRMBmhATUxRwv4JrU9aDnwNEuangVyy6OuHxi2YiY=%5dImage?";
    private static final String INDEX_FORM_DATA = "[FAST]=KL&[SECTOR]=2000&[FIELD]=33,38,51,98";

    private static final int VOLUME_TOKEN_INDEX = 7;
    private static final int VALUE_TOKEN_INDEX = 8;
    private static final int UP_TOKEN_INDEX = 2;
    private static final int DOWN_TOKEN_INDEX = 3;
    private static final int UNCHANGED_TOKEN_INDEX = 4;
    private static final int MAX_SUMMARY_TOKEN_INDEX = Math.max(VOLUME_TOKEN_INDEX, Math.max(VALUE_TOKEN_INDEX, Math.max(UP_TOKEN_INDEX, Math.max(DOWN_TOKEN_INDEX, UNCHANGED_TOKEN_INDEX))));

    private static final int CURRENT_INDEX_TOKEN_INDEX = 3;
    private static final int OPENING_INDEX_TOKEN_INDEX = 2;
    private static final int MAX_INDEX_TOKEN_INDEX = Math.max(CURRENT_INDEX_TOKEN_INDEX, OPENING_INDEX_TOKEN_INDEX);

    // Do not initialize servers in constructor. Initialization will be time
    // consuming since we need to connect to sourceforge to retrieve server
    // information. If most of the time taken up in constructor, our GUI will
    // be slow to show up.
    // Only initialize it when we need it.
    private List<String> servers;
    private final Object servers_lock = new Object();
    // We had already discover the best server. Please take note that,
    // synchronized is required during best server sorting. Hence, we will
    // use this flag to help us only perform sorting once.
    private volatile boolean bestServerAlreadySorted = false;

    private static final Log log = LogFactory.getLog(AsiaEBrokerMarketServer.class);
}
