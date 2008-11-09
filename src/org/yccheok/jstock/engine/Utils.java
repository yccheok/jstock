/*
 * Utils.java
 *
 * Created on April 27, 2007, 12:31 AM
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

import java.util.*;
import org.apache.commons.httpclient.*;

/**
 *
 * @author yccheok
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    public static void setHttpClientProxyFromSystemProperties(HttpClient httpClient) {
        final String httpproxyHost = System.getProperties().getProperty("http.proxyHost");
        final String httpproxyPort = System.getProperties().getProperty("http.proxyPort");
        
        if(null == httpproxyHost || null == httpproxyPort) {
            HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
            hostConfiguration.setProxyHost(null);
        }
        else {
            int port = -1;
            try {
                port = Integer.parseInt(httpproxyPort);
            }
            catch(NumberFormatException exp) {
            }     
            
            if(isValidPortNumber(port)) {
                HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
                hostConfiguration.setProxy(httpproxyHost, port);                
            }
            else {
                HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
                hostConfiguration.setProxyHost(null);
            }
        }
    } 
    
    // Refer to http://www.exampledepot.com/egs/java.util/CompDates.html
    public static long getDifferenceInDays(Calendar calendar0, Calendar calendar1) {
        // Determine which is earlier
        boolean b = calendar0.after(calendar1);
        
        long diffMillis = 0;
        
        if(b) {
            // Get difference in milliseconds
            diffMillis = calendar0.getTimeInMillis()-calendar1.getTimeInMillis();
        }
        else {
            diffMillis = calendar1.getTimeInMillis()-calendar0.getTimeInMillis();
        }
        
        // Get difference in days
        long diffDays = diffMillis/(24*60*60*1000);

        return diffDays;
    }
    
    public static String subString(String source, String begin, String end) {
        assert(begin.length() > 0 && end.length() > 0);
        
        int beginIndex = source.indexOf(begin);
        if(beginIndex == -1) return "";
        
        beginIndex += begin.length();
        
        int endIndex = source.indexOf(end);
        
        if(beginIndex > endIndex) return "";
        
        return source.substring(beginIndex, endIndex);
    } 
    
    public static boolean isValidPortNumber(int portNumber) {
        return (portNumber >= 0) && (portNumber <= 65534);
    }
    
    public static boolean isValidPortNumber(String portNumber) {
        int port = -1;
        try {
            port = Integer.parseInt(portNumber);
        }
        catch(NumberFormatException exp) {
        }
        
        return isValidPortNumber(port);
    }
    
    private static final List<Stock.Board> denmarkBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> franceBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> germanyBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> italyBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> malaysiaBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> norwayBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> spainBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> sweedenBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> unitedKingdomBoards = new ArrayList<Stock.Board>();
    private static final List<Stock.Board> unitedStateBoards = new ArrayList<Stock.Board>();

    private static final List<Index> denmarkIndices = new ArrayList<Index>();
    private static final List<Index> franceIndices = new ArrayList<Index>();
    private static final List<Index> germanyIndices = new ArrayList<Index>();
    private static final List<Index> italyIndices = new ArrayList<Index>();
    private static final List<Index> malaysiaIndices = new ArrayList<Index>();
    private static final List<Index> norwayIndices = new ArrayList<Index>();
    private static final List<Index> spainIndices = new ArrayList<Index>();
    private static final List<Index> sweedenIndices = new ArrayList<Index>();
    private static final List<Index> unitedKingdomIndices = new ArrayList<Index>();
    private static final List<Index> unitedStateIndices = new ArrayList<Index>();
    
    static
    {
        denmarkBoards.add(Stock.Board.OMXC20CO);
        franceBoards.add(Stock.Board.FCHI);        
        germanyBoards.add(Stock.Board.DAX);
        italyBoards.add(Stock.Board.SPMIB);
        malaysiaBoards.add(Stock.Board.Main);
        malaysiaBoards.add(Stock.Board.Second);
        malaysiaBoards.add(Stock.Board.Mesdaq);
        malaysiaBoards.add(Stock.Board.CallWarrant);
        norwayBoards.add(Stock.Board.OSEAX);
        spainBoards.add(Stock.Board.SMSI);
        sweedenBoards.add(Stock.Board.OMXSPI);
        unitedKingdomBoards.add(Stock.Board.FTSE);
        unitedStateBoards.add(Stock.Board.DHI);        
        unitedStateBoards.add(Stock.Board.IXIC);
        
        denmarkIndices.add(Index.OMXC20CO);
        franceIndices.add(Index.FCHI);        
        germanyIndices.add(Index.DAX);
        italyIndices.add(Index.SPMIB);
        malaysiaIndices.add(Index.KLSE);
        malaysiaIndices.add(Index.Second);
        malaysiaIndices.add(Index.Mesdaq);
        norwayIndices.add(Index.OSEAX);
        spainIndices.add(Index.SMSI);
        sweedenIndices.add(Index.OMXSPI);
        unitedKingdomIndices.add(Index.FTSE);
        unitedStateIndices.add(Index.DHI);        
        unitedStateIndices.add(Index.IXIC);        
    }

    public static List<Index> getStockIndices(Country country) {
        switch(country)
        {
            case Denmark:
                return java.util.Collections.unmodifiableList(Utils.denmarkIndices);
            case France:
                return java.util.Collections.unmodifiableList(Utils.franceIndices);
            case Germany:
                return java.util.Collections.unmodifiableList(Utils.germanyIndices);                
            case Italy:
                return java.util.Collections.unmodifiableList(Utils.italyIndices);
            case Malaysia:
                return java.util.Collections.unmodifiableList(Utils.malaysiaIndices);
            case Norway:
                return java.util.Collections.unmodifiableList(Utils.norwayIndices);
            case Spain:
                return java.util.Collections.unmodifiableList(Utils.spainIndices);
            case Sweeden:
                return java.util.Collections.unmodifiableList(Utils.sweedenIndices);
            case UnitedKingdom:
                return java.util.Collections.unmodifiableList(Utils.unitedKingdomIndices);                
            case UnitedState:
                return java.util.Collections.unmodifiableList(Utils.unitedStateIndices);
        }
        
        return java.util.Collections.emptyList();
    }
    
    public static List<Stock.Board> getStockBoards(Country country) {
        switch(country)
        {
            case Denmark:
                return java.util.Collections.unmodifiableList(Utils.denmarkBoards);
            case France:
                return java.util.Collections.unmodifiableList(Utils.franceBoards);
            case Germany:
                return java.util.Collections.unmodifiableList(Utils.germanyBoards);                
            case Italy:
                return java.util.Collections.unmodifiableList(Utils.italyBoards);
            case Malaysia:
                return java.util.Collections.unmodifiableList(Utils.malaysiaBoards);
            case Norway:
                return java.util.Collections.unmodifiableList(Utils.norwayBoards);
            case Spain:
                return java.util.Collections.unmodifiableList(Utils.spainBoards);
            case Sweeden:
                return java.util.Collections.unmodifiableList(Utils.sweedenBoards);
            case UnitedKingdom:
                return java.util.Collections.unmodifiableList(Utils.unitedKingdomBoards);                
            case UnitedState:
                return java.util.Collections.unmodifiableList(Utils.unitedStateBoards);
        }
        
        return java.util.Collections.emptyList();
    }
    
}
