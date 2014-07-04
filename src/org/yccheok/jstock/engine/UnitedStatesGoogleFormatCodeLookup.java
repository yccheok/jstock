/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2014 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.Map;

/**
 *
 * @author yccheok
 */
public enum UnitedStatesGoogleFormatCodeLookup {
    INSTANCE;
    
    public String put(Code code, String googleFormatCode) {
        return map.put(code, googleFormatCode);
    }
    
    public String get(Code code) {
        return map.get(code);
    }
    
    private static final Map<Code, String> map = new java.util.concurrent.ConcurrentHashMap<Code, String>();
}
