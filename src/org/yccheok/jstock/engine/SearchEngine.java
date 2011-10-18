/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.util.*;

/**
 * This class is used to suggest a list of items, which will be similar to a
 * given string prefix.
 */
public interface SearchEngine<E> {
    /**
     * Returns a list of elements, which will be similar to a given string prefix.
     *
     * @param prefix String prefix to match against elements
     * @return A list of E elements, which will be similar to a given string prefix.
     * Returns empty list if no match found.
     */
    public List<E> searchAll(String prefix);
    /**
     * Returns an element, which will be most similar to a given string prefix.
     *
     * @param prefix String prefix to match against elements
     * @return An element, which will be most similar to a given string prefix.
     * Returns <code>null</code> if no match found.
     */
    public E search(String prefix);
}
