/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.file;

/**
 *
 * @author yccheok
 */
public class Atom {
    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    public Atom(Object value, String type) {
        if (value == null || type == null) {
            throw new IllegalArgumentException("Method arguments cannot be null");
        }
        this.value = value;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Atom)) {
            return false;
        }
        Atom atom = (Atom)o;
        return  (this.value == null ? atom.value == null : this.value.equals(atom.value)) &&
                (this.type == null ? atom.type == null : this.type.equals(atom.type));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    private final Object value;
    private final String type;
}
