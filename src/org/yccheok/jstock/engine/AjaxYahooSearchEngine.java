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

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to suggest a list of items, which will be similar to a
 * given string prefix. The string will be sent to Yahoo server through Ajax.
 * Yahoo server will process the given string and returns a list of suggestion
 * stocks.
 */
public class AjaxYahooSearchEngine implements SearchEngine<ResultType> {

    /**
     * Returns a list of ResultType, which will be similar to a given prefix.
     * The searching mechanism based on the logic behind Yahoo server.
     *
     * @param prefix prefix to match against a list of ResultType
     * @return a list of ResultType, which will be similar to a given prefix.
     * Returns empty list if no match found
     */
    @Override
    public List<ResultType> searchAll(String prefix) {
        final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(getURL(prefix));
        final String json = Utils.YahooRespondToJSON(respond);
        try {
            final Holder value = gson.fromJson(json, Holder.class);
            
            if (value != null) {
                // Shall I check value.ResultSet.Query against prefix?
                return Collections.unmodifiableList(value.ResultSet.Result);
            }
        } catch (Exception ex) {
            log.error(null, ex);
        }
        return java.util.Collections.emptyList();
    }

    /**
     * Returns a ResultType, which will be similar to a given prefix.
     * The searching mechanism based on the logic behind Yahoo server.
     *
     * @param prefix prefix to match against ResultType
     * @return a ResultType, which will be similar to a given prefix.
     * Returns <code>null</null> if no match found
     */
    @Override
    public ResultType search(String prefix) {
        final List<ResultType> list = searchAll(prefix);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private String getURL(String prefix) {
        try {
            final String ePrefix = java.net.URLEncoder.encode(prefix, "UTF-8");
            final String URL = "http://d.yimg.com/aq/autoc?query=" + ePrefix + "&region=US&lang=en-US&callback=YAHOO.util.ScriptNodeDataSource.callbacks";
            return URL;
        } catch (UnsupportedEncodingException ex) {
            log.error(null, ex);
        }
        return "";
    }

    private static class Holder {
        public final ResultSetType ResultSet = null;
    }

    // Will it be better if we make this as static?
    private final Gson gson = new Gson();
    private static final Log log = LogFactory.getLog(AjaxYahooSearchEngine.class);
}
