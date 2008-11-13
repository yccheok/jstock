/*
 * CIMBMarketServer.java
 *
 * Created on May 6, 2007, 4:15 AM
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

package org.yccheok.jstock.engine;

import java.io.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
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
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        
        this.username = username;
        this.password = password;        
    }
    
    public Market getMarket()
    {
        final int numOfServer = servers.length;
        final Thread currentThread = Thread.currentThread();
        
        for(int i = 0; (i < numOfServer) && (!currentThread.isInterrupted()); i++) {
            HttpMethod method = new GetMethod(servers[i] + "rtQuote.dll?GetInitInfo");

            try {
                Utils.setHttpClientProxyFromSystemProperties(httpClient);
                httpClient.executeMethod(method);
                final String responde = method.getResponseBodyAsString();

                String infos = Utils.subString(responde, "--_BeginMS_", "--_EndMS_").trim();
        
                if(infos.length() != 0) {
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
                        
                        return new MalaysiaMarket(mainBoardIndex, mainBoardChange, secondBoardIndex, secondBoardChange, 
                                mesdaqIndex, mesdaqChange, up, down, unchange, volume, value);
                    }
                    catch(NumberFormatException exp) {
                        log.error("", exp);
                        continue;
                    }
                }
            
            }
            catch(HttpException exp) {
                log.error("", exp);
                continue;
            }
            catch(IOException exp) {
                log.error("", exp);
                continue;
            }
            finally {
                method.releaseConnection();
            }
        }
            
        return null;
    }
    
    private final HttpClient httpClient;
    private final String username;
    private final String password;
    
    private final String[] servers = new String[] {
        "http://n2ntbfd01.itradecimb.com/",
        "http://n2ntbfd02.itradecimb.com/",
        "http://n2ntbfd03.itradecimb.com/",
        "http://n2ntbfd04.itradecimb.com/",
        "http://n2ntbfd05.itradecimb.com/",
        "http://n2ntbfd06.itradecimb.com/",
        "http://n2ntbfd07.itradecimb.com/",
        "http://n2ntbfd08.itradecimb.com/",
        "http://n2ntbfd09.itradecimb.com/",
        "http://n2ntbfd10.itradecimb.com/"
    };

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
