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

package org.yccheok.jstock.gui;

/**
 * This class is equivalent to C++ std::pair class. It is usually used to hold
 * Code and Symbol pair.
 * @author yccheok
 * @param <A> first element
 * @param <B> second element
 */
public final class Pair<A, B> {
    private final A first;
    private final B second;

    /**
     * Construct a pair for the given first and second elements.
     * @param first first element
     * @param second second element
     */
    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 31 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (false == obj instanceof Pair) {
            return false;
        }
        final Pair<A, B> other = (Pair<A, B>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
           return "(" + first + ", " + second + ")";
    }

    /**
     * Returns first element.
     * @return first element
     */
    public A getFirst() {
        return first;
    }

    /**
     * Returns second element.
     * @return second element
     */
    public B getSecond() {
        return second;
    }
}
