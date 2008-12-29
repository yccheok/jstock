/*
 * Subject.java
 *
 * Created on April 28, 2007, 1:26 AM
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
import java.util.concurrent.*;

/**
 *
 * @author yccheok
 */
public class Subject<S, A> {
    
    public void attach(Observer<S, A> observer) {
        if(observers.contains(observer) == false)
            observers.add(observer);
    }

    public void dettach(Observer<S, A> observer) {
        observers.remove(observer);
    }
    
    public void dettachAll() {
        observers.clear();
    }
    
    protected void notify(S subject, A arg) {
        for (Observer<S, A> obs : observers) {
            obs.update(subject, arg);
        }
    }

    private List<Observer<S, A>> observers = new CopyOnWriteArrayList<Observer<S, A>>();    
}
