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

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class AjaxGoogleSearchEngine implements SearchEngine<MatchType> {

    private static final class Holder {
        List<MatchType> matches;
    }
    
    @Override
    public List<MatchType> searchAll(String prefix) {
        final StringBuilder builder = new StringBuilder("https://www.google.com/finance/match?matchtype=matchall&q=");
        try {
            // Exception will be thrown from apache httpclient, if we do not
            // perform URL encoding.
            builder.append(java.net.URLEncoder.encode(prefix, "UTF-8"));

            final String location = builder.toString();

            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
            
            final Holder value = gson.fromJson(respond, Holder.class);
            
            if (value != null) {
                // Shall I check value.ResultSet.Query against prefix?
                return Collections.unmodifiableList(value.matches);       
            }
        } catch (UnsupportedEncodingException ex) {
            log.error(null, ex);
        } catch (Exception ex) {
            // Jackson library may cause runtime exception if there is error
            // in the JSON string.
            log.error(null, ex);
        }        
        
        return java.util.Collections.emptyList();
    }

    @Override
    public MatchType search(String prefix) {
        final List<MatchType> list = searchAll(prefix);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }    
    
    // Will it be better if we make this as static?
    private final Gson gson = new Gson();
    
    private static final Log log = LogFactory.getLog(AjaxGoogleSearchEngine.class);
}
