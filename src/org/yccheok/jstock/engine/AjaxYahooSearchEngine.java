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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class is used to suggest a list of items, which will be similar to a
 * given string prefix. The string will be sent to Yahoo server through Ajax.
 * Yahoo server will process the given string and returns a list of suggestion
 * stocks.
 */
public class AjaxYahooSearchEngine implements SearchEngine<AjaxYahooSearchEngine.ResultType> {

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
            final Holder value = mapper.readValue(json, Holder.class);
            // Shall I check value.ResultSet.Query against prefix?
            return Collections.unmodifiableList(value.ResultSet.Result);
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

    /**
     * Type used to hold user query and list of ResultType from Yahoo server.
     */
    public static class ResultSetType {
        /**
         * User query.
         */
        public final String Query;
        /**
         * List of ResultType from Yahoo server.
         */
        public final List<ResultType> Result;

        /**
         * Default constructor. Must have, in order for jackson to work
         * properly.
         */
        public ResultSetType() {
            Query = null;
            Result = null;
        }

        private ResultSetType(String Query, List<ResultType> Result) {
            this.Query = Query;
            this.Result = Result;
        }

        /**
         * Constructs an instance of ResultSetType.
         *
         * @param Query user query
         * @param Result List of ResultType from Yahoo server
         * @return an instance of ResultSetType
         */
        public static ResultSetType newInstance(String Query, List<ResultType> Result) {
            return new ResultSetType(Query, Result);
        }
    }

    /**
     * Type used to hold result from Yahoo server.
     */
    public static class ResultType {
        /**
         * The symbol.
         */
        public final String symbol;
        /**
         * The name.
         */
        public final String name;
        /**
         * The stock exchange.
         */
        public final String exch;
        /**
         * The stock type.
         */
        public final String type;
        /**
         * The stock exchange displayed name.
         */
        public final String exchDisp;
        /**
         * The stock type displayed name.
         */
        public final String typeDisp;

        /**
         * Creates a new instance of ResultType, with the specified symbol and
         * name.
         *
         * @param symbol The symbol
         * @param name The name
         */
        public ResultType(String symbol, String name) {
            this(symbol, name, null, null, null, null);
        }

        /**
         * Default constructor. Must have, in order for jackson to work
         * properly.
         */
        public ResultType() {
            this(null, null, null, null, null, null);
        }

        private ResultType(String symbol, String name, String exch, String type, String exchDisp, String typeDisp) {
            this.symbol = symbol;
            this.name = name;
            this.exch = exch;
            this.type = type;
            this.exchDisp = exchDisp;
            this.typeDisp = typeDisp;
        }

        public ResultType deriveWithSymbol(String symbol) {
            return new ResultType(symbol, this.name, this.exch, this.type, this.exchDisp, this.typeDisp);
        }

        public ResultType deriveWithName(String name) {
            return new ResultType(this.symbol, name, this.exch, this.type, this.exchDisp, this.typeDisp);
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    // Will it be better if we make this as static?
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(AjaxYahooSearchEngine.class);
}
