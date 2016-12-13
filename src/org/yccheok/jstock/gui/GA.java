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

package org.yccheok.jstock.gui;

import com.boxysystems.jgoogleanalytics.FocusPoint;
import com.boxysystems.jgoogleanalytics.JGoogleAnalyticsTracker;

/**
 *
 * @author yccheok
 */
public class GA {
    public static void trackAsynchronously(String name) {
        //Google analytics tracking code for Library Finder
        JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker("JStock", Utils.getApplicationVersionString(), trackingCode);
        FocusPoint focusPoint = new FocusPoint(name);
        tracker.trackAsynchronously(focusPoint);
    }
    
    private static final String trackingCode = "UA-74773703-1";
}
