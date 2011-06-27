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

package org.yccheok.jstock.portfolio;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yccheok
 */
public class AbstractSummary<E> implements Summary<E> {
    @Override
    public boolean add(E element) {
        return list.add(element);
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public boolean remove(E element) {
        return list.remove(element);
    }

    @Override
    public int size() {
        return list.size();
    }

    // Take note on the protected access level. This is must have, in order for
    // child classes to behave correctly.
    protected Object readResolve() {
        /* For backward compatible */
        if (list == null) {
            list = new ArrayList<E>();
        }
        return this;
    }

    private List<E> list = new ArrayList<E>();
}
