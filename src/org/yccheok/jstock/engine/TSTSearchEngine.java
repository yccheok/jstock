/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to suggest a list of items, which will be similar to a
 * given string prefix. Tenary Search Tree will be the primary data structure
 * to hold the complete list of E items. The searching mechanism is case insensitive.
 */
public class TSTSearchEngine<E> implements SearchEngine<E> {
    
    /**
     * Initializes a newly created {@code TSTSearchEngine} with a given list of
     * elements.
     * 
     * @param sources List of elements used to fill up {@code TSTSearchEngine}
     */
    public TSTSearchEngine(List<E> sources) {
        for (E source : sources) {
            put(source);
        }
    }

    /**
     * Initializes a newly created {@code TSTSearchEngine} with empty element.
     */
    public TSTSearchEngine() {
    }
    
    /**
     * Returns a list of elements, which will be similar to a given string prefix.
     * The searching mechanism is case insensitive.
     * 
     * @param prefix String prefix to match against elements
     * @return A list of elements, which will be similar to a given string prefix.
     * Returns empty list if no match found.
     */
    @Override
    public List<E> searchAll(String prefix) {
        final String mapKey = prefix.toUpperCase();
        final List<String> keys = tst.matchPrefix(mapKey);
        final List<E> list = new ArrayList<E>();
        for (String key : keys) {
            // map.get(key) must be non-null.
            list.addAll(map.get(key));
        }
        return list;
    }

    /**
     * Returns an element, which will be most similar to a given string prefix.
     * The searching mechanism is case insensitive.
     *
     * @param prefix String prefix to match against elements
     * @return An element, which will be most similar to a given string prefix.
     * Returns <code>null</code> if no match found.
     */
    @Override
    public E search(String prefix) {
        final String mapKey = prefix.toUpperCase();
        final List<String> keys = tst.matchPrefix(mapKey);
        if (keys.isEmpty() == false) {
            final String key = keys.get(0);
            // key must be non-null.
            final List<E> l = map.get(key);
            return l.isEmpty() == false ? l.get(0) : null;
        }
        return null;
    }
    
    /**
     * Insert an element into this search engine.
     * @param value Element to be inserted
     */
    public final void put(E value) {
        final String mapKey = value.toString().toUpperCase();
        tst.put(mapKey, mapKey);

        List<E> list = map.get(mapKey);
        if (list == null) {
            list = new ArrayList<E>();
            map.put(mapKey, list);
        }
        // Avoid duplication. Don't use Set, as order is
        // important for us.
        if (false == list.contains(value)) {
            list.add(value);
        }
    }
    
    /**
     * Removes an element from this search engine.
     * @param value Element to be removed
     */
    public void remove(E value) {
        final String mapKey = value.toString().toUpperCase();
        
        List<E> list = map.get(mapKey);
        if (list == null) {
            return;
        }
        
        list.remove(value);
        
        if (list.isEmpty()) {
            tst.remove(mapKey);
            map.remove(mapKey);
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
    //
    // There should be no duplicated item in List. Don't use Set, as order
    // is important for us.
    private final Map<String, List<E>> map = new HashMap<String, List<E>>();
}