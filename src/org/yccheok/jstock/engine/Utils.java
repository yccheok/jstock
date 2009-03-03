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

    private static final List<Index> denmarkIndices = new ArrayList<Index>();
    private static final List<Index> franceIndices = new ArrayList<Index>();
    private static final List<Index> germanyIndices = new ArrayList<Index>();
    private static final List<Index> indiaIndices = new ArrayList<Index>();
    private static final List<Index> italyIndices = new ArrayList<Index>();
    private static final List<Index> malaysiaIndices = new ArrayList<Index>();
    private static final List<Index> norwayIndices = new ArrayList<Index>();
    private static final List<Index> singaporeIndices = new ArrayList<Index>();
    private static final List<Index> spainIndices = new ArrayList<Index>();
    private static final List<Index> swedenIndices = new ArrayList<Index>();
    private static final List<Index> unitedKingdomIndices = new ArrayList<Index>();
    private static final List<Index> unitedStateIndices = new ArrayList<Index>();
    
    static
    {        
        denmarkIndices.add(Index.OMXC20CO);
        franceIndices.add(Index.FCHI);  
        germanyIndices.add(Index.DAX);
        indiaIndices.add(Index.BSESN);
        indiaIndices.add(Index.NSEI);
        italyIndices.add(Index.SPMIB);
        malaysiaIndices.add(Index.KLSE);
        malaysiaIndices.add(Index.Second);
        malaysiaIndices.add(Index.Mesdaq);
        norwayIndices.add(Index.OSEAX);
        singaporeIndices.add(Index.STI);
        spainIndices.add(Index.SMSI);
        swedenIndices.add(Index.OMXSPI);
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

        if(country == Country.Malaysia)
        {
            String _code = code.toString();
            if(_code.endsWith(".KL") == false)
            {
                result = Code.newInstance(_code + ".KL");
            }
        }
        
        return result;
    }

    public static List<Index> getStockIndices(Country country) {
        switch (country)
        {
            case Denmark:
                return java.util.Collections.unmodifiableList(Utils.denmarkIndices);
            case France:
                return java.util.Collections.unmodifiableList(Utils.franceIndices);
            case Germany:
                return java.util.Collections.unmodifiableList(Utils.germanyIndices);
            case India:
                return java.util.Collections.unmodifiableList(Utils.indiaIndices);
            case Italy:
                return java.util.Collections.unmodifiableList(Utils.italyIndices);
            case Malaysia:
                return java.util.Collections.unmodifiableList(Utils.malaysiaIndices);
            case Norway:
                return java.util.Collections.unmodifiableList(Utils.norwayIndices);
            case Singapore:
                return java.util.Collections.unmodifiableList(Utils.singaporeIndices);
            case Spain:
                return java.util.Collections.unmodifiableList(Utils.spainIndices);
            case Sweden:
                return java.util.Collections.unmodifiableList(Utils.swedenIndices);
            case UnitedKingdom:
                return java.util.Collections.unmodifiableList(Utils.unitedKingdomIndices);                
            case UnitedState:
                return java.util.Collections.unmodifiableList(Utils.unitedStateIndices);
        }
        
        return java.util.Collections.emptyList();
    }    
}
