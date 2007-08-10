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
}
