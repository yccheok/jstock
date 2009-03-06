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
        for (E source : sources) {
            put(source);
        }
    }

    public TSTSearchEngine() {
    }
    
    @Override
    public List<E> searchAll(String prefix) {
        final String mapKey = prefix.toUpperCase();
        List<String> keys = tst.matchPrefix(mapKey);
        List<E> list = new ArrayList<E>();
        for (String key : keys) {
            // map.get(key) must be non-null.
            list.addAll(map.get(key));
        }
        return list;
    }

    @Override
    public E search(String prefix) {
        final String mapKey = prefix.toUpperCase();
        List<String> keys = tst.matchPrefix(mapKey);
        if (keys.size() > 0) {
            final String key = keys.get(0);
            // key must be non-null.
            List<E> l = map.get(key);
            return l.size() > 0 ? l.get(0) : null;
        }
        return null;
    }
    
    public void put(E value) {
        final String mapKey = value.toString().toUpperCase();
        tst.put(mapKey, mapKey);

        List<E> list = map.get(mapKey);
        if (list == null) {
            list = new ArrayList<E>();
            map.put(mapKey, list);
        }
        list.add(value);
    }
    
    public void remove(E value) {
        final String mapKey = value.toString().toUpperCase();
        final String key = tst.remove(mapKey);
        if (key != null) {
            map.remove(key);
        }
    }
    
    private final TernarySearchTree<String> tst = new TernarySearchTree<String>();
    // We need to have this map, so that we are able to retrieve E value
    // in a case insensitive way. This is just a pseudo way for us to
    // achieve this purpose. We should really build this case insensitive
    // capability into TernarySearchTree itself. Once TernarySearchTree
    // has the capability to handle case insensitive, it should be holding
    // E value instead of String (String will be used as the key to access
    // map).
    private final Map<String, List<E>> map = new HashMap<String, List<E>>();
}