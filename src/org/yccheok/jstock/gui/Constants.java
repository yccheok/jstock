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

package org.yccheok.jstock.gui;

/**
 *
 * @author yccheok
 */
public class Constants {
    public static final int REAL_TIME_STOCK_MONITOR_MAX_THREAD = 4;
    public static final int REAL_TIME_STOCK_MONITOR_MAX_STOCK_SIZE_PER_SCAN = 20;
    
    // I know that entire stock indices shall never more than 2 x 20 = 40. But,
    // it really make no harm that we use 4 instead of 2 in such case.
    // RealTimeIndexMonitor is smart enough to handle such case.
    public static final int REAL_TIME_INDEX_MONITOR_MAX_THREAD = 4;
    public static final int REAL_TIME_INDEX_MONITOR_MAX_STOCK_SIZE_PER_SCAN = 20;
    
    public static final int EXCHANGE_RATE_MONITOR_MAX_THREAD = 4;
    public static final int EXCHANGE_RATE_MONITOR_MAX_STOCK_SIZE_PER_SCAN = 20;
    
    public static final double FONT_ENLARGE_FACTOR = 1.2;
}
