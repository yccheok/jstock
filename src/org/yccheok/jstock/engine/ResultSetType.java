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

import java.util.List;

/**
 *
 * @author yccheok
 */
public class ResultSetType {
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
