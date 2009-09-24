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


import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class CIMBMarketServer implements MarketServer {
    
    /** Creates a new instance of CIMBMarketServer */
    public CIMBMarketServer() {
        // Empty username and password.
        this("", "");
    }

    public CIMBMarketServer(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Override
    public Market getMarket()
    {
        initServers();

        final int numOfServer = servers.size();
        final Thread currentThread = Thread.currentThread();                

        for (int i = 0; (i < numOfServer) && (!currentThread.isInterrupted()); i++) {
            final String request = servers.get(i) + "rtQuote.dll?GetInitInfo";

            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(request);
            if (respond == null) {
                continue;
            }
            String infos = Utils.subString(respond, "--_BeginMS_", "--_EndMS_").trim();

            if (infos.length() != 0) {
                String[] infoFields = infos.split("\\|");

                try {
                    double mainBoardIndex = Double.parseDouble(infoFields[MAIN_BOARD_INDEX_TOKEN_INDEX]);
                    double mainBoardChange = Double.parseDouble(infoFields[MAIN_BOARD_CHANGE_TOKEN_INDEX]);
                    double secondBoardIndex = Double.parseDouble(infoFields[SECOND_BOARD_INDEX_TOKEN_INDEX]);
                    double secondBoardChange = Double.parseDouble(infoFields[SECOND_BOARD_CHANGE_TOKEN_INDEX]);
                    // double mesdaqIndex = Double.parseDouble(infoFields[MESDAQ_INDEX_TOKEN_INDEX]);
                    // double mesdaqChange = Double.parseDouble(infoFields[MESDAQ_INDEX_CHANGE_INDEX]);
                    double mesdaqIndex = 0.0;
                    double mesdaqChange = 0.0;
                    int up = Integer.parseInt(infoFields[UP_TOKEN_INDEX]);
                    int down = Integer.parseInt(infoFields[DOWN_TOKEN_INDEX]);
                    int unchange = Integer.parseInt(infoFields[UNCHANGE_TOKEN_INDEX]);
                    long volume = Long.parseLong(infoFields[VOLUME_TOKEN_INDEX]);
                    double value = Double.parseDouble(infoFields[VALUE_TOKEN_INDEX]);

                    // Sometimes, CIMB just returns 0.
                    if (volume != 0) {
                        // Sort the best server.
                        if (bestServerAlreadySorted == false) {
                            synchronized(servers_lock) {
                                if (bestServerAlreadySorted == false) {
                                    bestServerAlreadySorted = true;
                                    String tmp = servers.get(0);
                                    servers.set(0, servers.get(i));
                                    servers.set(i, tmp);
                                }
                            }
                        }

                        return new MalaysiaMarket(mainBoardIndex, mainBoardChange, secondBoardIndex, secondBoardChange,
                            mesdaqIndex, mesdaqChange, up, down, unchange, volume, value);
                    }
                }
                catch(NumberFormatException exp) {
                    log.error("", exp);
                    continue;
                }
            }   // if (infos.length() != 0)
        }   // for
            
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

            this.servers = Utils.getCIMBMarketServers();
        }
    }

    private final String username;
    private final String password;

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

    private static final int UP_TOKEN_INDEX = 0;   
    private static final int DOWN_TOKEN_INDEX = 1;
    private static final int UNCHANGE_TOKEN_INDEX = 2;    
    private static final int MAIN_BOARD_INDEX_TOKEN_INDEX = 4;   
    private static final int MAIN_BOARD_CHANGE_TOKEN_INDEX = 5;
    // private static final int MESDAQ_INDEX_TOKEN_INDEX = 6;
    // private static final int MESDAQ_INDEX_CHANGE_INDEX = 7;
    private static final int SECOND_BOARD_INDEX_TOKEN_INDEX = 8;
    private static final int SECOND_BOARD_CHANGE_TOKEN_INDEX = 9; 
    private static final int VOLUME_TOKEN_INDEX = 10; 
    private static final int VALUE_TOKEN_INDEX = 11;

    private static final Log log = LogFactory.getLog(CIMBMarketServer.class);    
}
