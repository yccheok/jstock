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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to suggest a list of items, based on given Pinyin prefix.
 * The suggested list will be in non-Pinyin. If the item is unable to be
 * translated to Pinyin, its behavior will be similar to TSTSearchEngine. However,
 * all the non-alphabetical characters will be ignored.
 *
 * For example, in PinyinTSTSearchEngine :
 *  database    : hel@lo
 *  input       : hello
 *  output      : hel@lo
 *
 * In TSTSearchEngine :
 *  database    : hel@lo
 *  input       : hel@lo
 *  output      : hel@lo
 *
 * The searching mechanism is case insensitive.
 */
public class PinyinTSTSearchEngine<E> implements SearchEngine<E> {
    /**
     * Initializes a newly created {@code PinyinTSTSearchEngine} with a given list of
     * elements.
     *
     * @param sources List of elements used to fill up {@code PinyinTSTSearchEngine}
     */
    public PinyinTSTSearchEngine(List<E> sources) {
        for (E source : sources) {
            put(source);
        }
    }

    /**
     * Initializes a newly created {@code PinyinTSTSearchEngine} with empty element.
     */
    public PinyinTSTSearchEngine() {
    }
    
    /**
     * Returns a list of elements, which will be similar to a given pinyin prefix.
     * The searching mechanism is case insensitive.
     * 
     * @param prefix Pinyin prefix to match against elements
     * @return A list of elements, which will be similar to a given pinyin prefix.
     * Returns empty list if no match found.
     */
    @Override
    public List<E> searchAll(String pinyinPrefix) {
        List<String> pinyins = searchEngine.searchAll(pinyinPrefix);
        List<E> list = new ArrayList<E>();
        for (String pinyin : pinyins) {
            // map.get(pinyin) must be non-null.
            list.addAll(pinyinMap.get(pinyin));
        }
        return list;
    }

    /**
     * Returns an element, which will be most similar to a given pinyin prefix.
     * The searching mechanism is case insensitive.
     *
     * @param prefix String prefix to match against elements
     * @return An element, which will be most similar to a given string prefix.
     * Returns <code>null</code> if no match found.
     */
    @Override
    public E search(String pinyinPrefix) {
        List<String> pinyins = searchEngine.searchAll(pinyinPrefix);
        if (pinyins.isEmpty() == false) {
            final String pinyin = pinyins.get(0);
            // pinyin must be non-null.
            Set<E> s = pinyinMap.get(pinyin);
            return s.isEmpty() == false ? s.iterator().next() : null;
        }
        return null;
    }

    /**
     * Insert an element into this search engine.
     * @param value Element to be inserted
     */
    public final void put(E value) {
        final List<String> pinyins = org.yccheok.jstock.gui.Utils.toHanyuPinyin(value.toString());
        for (String pinyin : pinyins) {
            searchEngine.put(pinyin);

            Set<E> set = pinyinMap.get(pinyin);
            if (set == null) {
                set = new HashSet<E>();
                pinyinMap.put(pinyin, set);
            }
            set.add(value);
        }
    }

    /**
     * Removes an element from this search engine.
     * @param value Element to be removed
     */
    public void remove(E value) {
        final List<String> pinyins = org.yccheok.jstock.gui.Utils.toHanyuPinyin(value.toString());
        for (String pinyin : pinyins) {
            searchEngine.remove(pinyin);
            final Set set = pinyinMap.get(pinyin);
            set.remove(value);
            if (set.isEmpty()) {
                pinyinMap.remove(pinyin);
            }
        }
    }

    // Re-use TSTSearchEngine, as we just like its case-insensitive behavior.
    private final TSTSearchEngine<String> searchEngine = new TSTSearchEngine<String>();
    // Translate Pinyin to list of items.
    private Map<String, Set<E>> pinyinMap = new HashMap<String, Set<E>>();
}
