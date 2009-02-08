/*
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
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import org.yccheok.jstock.analysis.Indicator;
import org.yccheok.jstock.engine.Subject;

/**
 * For a stock which is under an indicator, every time we receive a stock tick,
 * there will be two states :-
 * (1) The stock hits the indicator.
 * (2) The stock doesn't hit the indicator.
 *
 * Most of the time, when a stock hits an indicator in n ticks, it will still hit
 * the indicator in n+1 ticks, with same parameters (e.g. same last price). If we try to
 * perform alert based on the condition :-
 * (1) When a stock hits an indicator in every tick.
 *
 * We will be getting far too many duplicated alert messages, which will not be client's
 * interest. The alert should be perform based on the following condition :-
 * (1) When a stock hits an indicator in every tick AND
 * (2) There is no alert for the particular stock and indicator at previous tick.
 *
 * AlertStateManager will be used to keep track the alert state, and inform its observer,
 * when a stock should be alerted.
 *
 * @author yccheok
 */
public class AlertStateManager extends Subject<AlertStateManager, Indicator> {
    /* Start from the initial state. */
    public void init()
    {
    }

    public void alert(Indicator indicator)
    {
    }

    public void dealert(Indicator indicator)
    {
    }

    private final java.util.List<String> alertRecords = new java.util.ArrayList<String>();
}
