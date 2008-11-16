/*
 * TSTSearchEngine.java
 *
 * Created on April 28, 2007, 1:39 AM
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

/**
 *
 * @author yccheok
 */
public class TSTSearchEngine<E> implements SearchEngine<E> {
    
    /** Creates a new instance of TSTSearchEngine */
    public TSTSearchEngine(List<E> sources) {
        for(E source : sources) {
            tst.put(source.toString(), source);
        }
    }
    
    @Override
    public List<E> searchAll(String prefix) {
        return tst.matchPrefix(prefix);
    }

    @Override
    public E search(String prefix) {
        List<E> l = tst.matchPrefix(prefix, 1);
        return l.size() > 0 ? l.get(0) : null;
    }
    
    public void put(String key, E value) {
        tst.put(key, value);
    }
    
    public void remove(String key) {
        tst.remove(key);
    }
    
    private final TernarySearchTree<E> tst = new TernarySearchTree<E>();
}