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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.httpclient.*;

/**
 *
 * @author yccheok
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }

    // Use Copy On Write ArrayList, so that we can perform sorting.
    private static volatile List<String> CIMBMarketServers = null;
    private static volatile List<String> CIMBStockServers = null;
    private static volatile List<String> CIMBHistoryServers = null;

    private static volatile List<String> AsiaEBrokerMarketServers = null;
    private static volatile List<String> AsiaEBrokerStockServers = null;

    private static final String[] DEFAULT_CIMB_SERVERS = new String[] {
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

    private static final String[] DEFAULT_ASIAEBROKER_SERVERS = new String[] {
        "http://n2ntbfd03.asiaebroker.com:20000",
        "http://n2ntbfd065.asiaebroker.com:20000",
        "http://n2ntbfd01.asiaebroker.com:20000"
    };

    public static List<String> getAsiaEBrokerMarketServers() {
        List<String> servers = Utils.AsiaEBrokerMarketServers;
        if (servers != null) {
            // We already have the server list.
            return new CopyOnWriteArrayList<String>(servers);
        }

        final String server = org.yccheok.jstock.gui.Utils.getUUIDValue("http://jstock.sourceforge.net/server/asiaebrokermarketservers.txt", "server");
        if (server != null) {
            String[] s = server.split(",");
            if (s.length > 0) {
                List<String> me = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(s));
                // Save it! So that we need not to ask for server list again next time.
                Utils.AsiaEBrokerMarketServers = me;
                return new CopyOnWriteArrayList<String>(me);
            }
        }
        servers = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(DEFAULT_ASIAEBROKER_SERVERS));
        // Save it! So that we need not to ask for server list again next time.
        Utils.AsiaEBrokerMarketServers = servers;
        return new CopyOnWriteArrayList<String>(servers);
    }

    public static List<String> getCIMBMarketServers() {
        List<String> servers = Utils.CIMBMarketServers;
        if (servers != null) {
            // We already have the server list.
            return new CopyOnWriteArrayList<String>(servers);
        }

        final String server = org.yccheok.jstock.gui.Utils.getUUIDValue("http://jstock.sourceforge.net/server/cimbmarketservers.txt", "server");
        if (server != null) {
            String[] s = server.split(",");
            if (s.length > 0) {
                List<String> me = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(s));
                // Save it! So that we need not to ask for server list again next time.
                Utils.CIMBMarketServers = me;
                return new CopyOnWriteArrayList<String>(me);
            }
        }
        servers = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(DEFAULT_CIMB_SERVERS));
        // Save it! So that we need not to ask for server list again next time.
        Utils.CIMBMarketServers = servers;
        return new CopyOnWriteArrayList<String>(servers);
    }

    public static List<String> getAsiaEBrokerStockServers() {
        List<String> servers = Utils.AsiaEBrokerStockServers;
        if (servers != null) {
            // We already have the server list.
            return new CopyOnWriteArrayList<String>(servers);
        }

        final String server = org.yccheok.jstock.gui.Utils.getUUIDValue("http://jstock.sourceforge.net/server/asiaebrokerstockservers.txt", "server");
        if (server != null) {
            String[] s = server.split(",");
            if (s.length > 0) {
                List<String> me = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(s));
                // Save it! So that we need not to ask for server list again next time.
                Utils.AsiaEBrokerStockServers = me;
                return new CopyOnWriteArrayList<String>(me);
            }
        }
        servers = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(DEFAULT_ASIAEBROKER_SERVERS));
        // Save it! So that we need not to ask for server list again next time.
        Utils.AsiaEBrokerStockServers = servers;
        return new CopyOnWriteArrayList<String>(servers);
    }

    public static List<String> getCIMBStockServers() {
        List<String> servers = Utils.CIMBStockServers;
        if (servers != null) {
            // We already have the server list.
            return new CopyOnWriteArrayList<String>(servers);
        }

        final String server = org.yccheok.jstock.gui.Utils.getUUIDValue("http://jstock.sourceforge.net/server/cimbstockservers.txt", "server");
        if (server != null) {
            String[] s = server.split(",");
            if (s.length > 0) {
                List<String> me = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(s));
				// Save it! So that we need not to ask for server list again next time.
                Utils.CIMBStockServers = me;
                return new CopyOnWriteArrayList<String>(me);
            }
        }
        servers = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(DEFAULT_CIMB_SERVERS));
        // Save it! So that we need not to ask for server list again next time.
        Utils.CIMBStockServers = servers;
        return new CopyOnWriteArrayList<String>(servers);
    }

    public static List<String> getCIMBHistoryServers() {
        List<String> servers = Utils.CIMBHistoryServers;
        if (servers != null) {
            // We already have the server list.
            return new CopyOnWriteArrayList<String>(servers);
        }

        final String server = org.yccheok.jstock.gui.Utils.getUUIDValue("http://jstock.sourceforge.net/server/cimbhistoryservers.txt", "server");
        if (server != null) {
            String[] s = server.split(",");
            if (s.length > 0) {
                List<String> me = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(s));
                // Save it! So that we need not to ask for server list again next time.
                Utils.CIMBHistoryServers = me;
                return new CopyOnWriteArrayList<String>(me);
            }
        }
        servers = new CopyOnWriteArrayList<String>(java.util.Arrays.asList(DEFAULT_CIMB_SERVERS));
        // Save it! So that we need not to ask for server list again next time.
        Utils.CIMBHistoryServers = servers;
        return new CopyOnWriteArrayList<String>(servers);
    }

    public static void setHttpClientProxyFromSystemProperties(HttpClient httpClient) {
        final String httpproxyHost = System.getProperties().getProperty("http.proxyHost");
        final String httpproxyPort = System.getProperties().getProperty("http.proxyPort");
        
        if (null == httpproxyHost || null == httpproxyPort) {
            HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
            hostConfiguration.setProxyHost(null);
        }
        else {
            int port = -1;
            try {
                port = Integer.parseInt(httpproxyPort);
            }
            catch (NumberFormatException exp) {
            }     
            
            if (isValidPortNumber(port)) {
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

    private static final List<Index> australiaIndices = new ArrayList<Index>();
    private static final List<Index> austriaIndices = new ArrayList<Index>();
    private static final List<Index> belgiumIndices = new ArrayList<Index>();
    private static final List<Index> canadaIndices = new ArrayList<Index>();
    private static final List<Index> denmarkIndices = new ArrayList<Index>();
    private static final List<Index> franceIndices = new ArrayList<Index>();
    private static final List<Index> germanyIndices = new ArrayList<Index>();
    private static final List<Index> hongkongIndices = new ArrayList<Index>();
    private static final List<Index> indiaIndices = new ArrayList<Index>();
    private static final List<Index> indonesiaIndices = new ArrayList<Index>();
    private static final List<Index> italyIndices = new ArrayList<Index>();
    private static final List<Index> koreaIndices = new ArrayList<Index>();
    private static final List<Index> malaysiaIndices = new ArrayList<Index>();
    private static final List<Index> netherlandsIndices = new ArrayList<Index>();
    private static final List<Index> norwayIndices = new ArrayList<Index>();
    private static final List<Index> portugalIndices = new ArrayList<Index>();
    private static final List<Index> singaporeIndices = new ArrayList<Index>();
    private static final List<Index> spainIndices = new ArrayList<Index>();
    private static final List<Index> swedenIndices = new ArrayList<Index>();
    private static final List<Index> switzerlandIndices = new ArrayList<Index>();
    private static final List<Index> taiwanIndices = new ArrayList<Index>();
    private static final List<Index> unitedKingdomIndices = new ArrayList<Index>();
    private static final List<Index> unitedStateIndices = new ArrayList<Index>();
    
    static
    {
        austriaIndices.add(Index.ATX);
        australiaIndices.add(Index.AORD);
        belgiumIndices.add(Index.BFX);
        canadaIndices.add(Index.GSPTSE);
        denmarkIndices.add(Index.OMXC20CO);
        franceIndices.add(Index.FCHI);  
        germanyIndices.add(Index.DAX);
        hongkongIndices.add(Index.HSI);
        indiaIndices.add(Index.BSESN);
        indiaIndices.add(Index.NSEI);
        indonesiaIndices.add(Index.JKSE);
        italyIndices.add(Index.SPMIB);
        koreaIndices.add(Index.KS11);
        malaysiaIndices.add(Index.KLSE);
        malaysiaIndices.add(Index.Second);
        malaysiaIndices.add(Index.Mesdaq);
        netherlandsIndices.add(Index.AEX);
        norwayIndices.add(Index.OSEAX);
        portugalIndices.add(Index.PSI20);
        singaporeIndices.add(Index.STI);
        spainIndices.add(Index.SMSI);
        swedenIndices.add(Index.OMXSPI);
        switzerlandIndices.add(Index.SSMI);
        taiwanIndices.add(Index.TWII);
        unitedKingdomIndices.add(Index.FTSE);
        unitedStateIndices.add(Index.DJI);        
        unitedStateIndices.add(Index.IXIC);        
    }

    // Use to provide conversion between different server's database.
    public static Code toCIMBFormat(Code code, Country country)
    {
        if(code == null || country == null)
        {
            throw new java.lang.IllegalArgumentException("Method parameters cannot be null in toYahooFormat");
        }

        Code result = code;

        if(country == Country.Malaysia)
        {
            String _code = code.toString();
            if(_code.endsWith(".KL") == true)
            {
                if(_code.length() > ".KL".length())
                {
                    result = Code.newInstance(_code.substring(0, _code.length() - ".KL".length()));
                }
            }
        }
        
        return result;
    }

    public static Code toYahooFormat(Code code, Country country)
    {
        if(code == null || country == null)
        {
            throw new java.lang.IllegalArgumentException("Method parameters cannot be null in toYahooFormat");
        }

        Code result = code;

        if (country == Country.Malaysia)
        {
            String _code = code.toString();
            // Index's code start with ^. We will not intrude index's code.
            if(_code.startsWith("^") == false && _code.endsWith(".KL") == false)
            {
                // This is not index's code, and it does not end with .KL.
                // Let's intrude it!
                result = Code.newInstance(_code + ".KL");
            }
        }
        
        return result;
    }

    public static List<Index> getStockIndices(Country country) {
        switch (country)
        {
            case Australia:
                return java.util.Collections.unmodifiableList(Utils.australiaIndices);
            case Austria:
                return java.util.Collections.unmodifiableList(Utils.austriaIndices);
            case Belgium:
                return java.util.Collections.unmodifiableList(Utils.belgiumIndices);
            case Canada:
                return java.util.Collections.unmodifiableList(Utils.canadaIndices);
            case Denmark:
                return java.util.Collections.unmodifiableList(Utils.denmarkIndices);
            case France:
                return java.util.Collections.unmodifiableList(Utils.franceIndices);
            case Germany:
                return java.util.Collections.unmodifiableList(Utils.germanyIndices);
            case HongKong:
                return java.util.Collections.unmodifiableList(Utils.hongkongIndices);
            case India:
                return java.util.Collections.unmodifiableList(Utils.indiaIndices);
            case Indonesia:
                return java.util.Collections.unmodifiableList(Utils.indonesiaIndices);
            case Italy:
                return java.util.Collections.unmodifiableList(Utils.italyIndices);
            case Korea:
                return java.util.Collections.unmodifiableList(Utils.koreaIndices);
            case Malaysia:
                return java.util.Collections.unmodifiableList(Utils.malaysiaIndices);
            case Netherlands:
                return java.util.Collections.unmodifiableList(Utils.netherlandsIndices);
            case Norway:
                return java.util.Collections.unmodifiableList(Utils.norwayIndices);
            case Portugal:
                return java.util.Collections.unmodifiableList(Utils.portugalIndices);
            case Singapore:
                return java.util.Collections.unmodifiableList(Utils.singaporeIndices);
            case Spain:
                return java.util.Collections.unmodifiableList(Utils.spainIndices);
            case Sweden:
                return java.util.Collections.unmodifiableList(Utils.swedenIndices);
            case Switzerland:
                return java.util.Collections.unmodifiableList(Utils.switzerlandIndices);
            case Taiwan:
                return java.util.Collections.unmodifiableList(Utils.taiwanIndices);
            case UnitedKingdom:
                return java.util.Collections.unmodifiableList(Utils.unitedKingdomIndices);                
            case UnitedState:
                return java.util.Collections.unmodifiableList(Utils.unitedStateIndices);
        }
        
        return java.util.Collections.emptyList();
    }    
}
