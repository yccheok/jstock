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

package org.yccheok.jstock.gui.table;

/**
 * This is a table cell editor which allow both negative and positive double
 * value, but not empty value.
 *
 * @author yccheok
 */
public class NonEmptyDoubleEditor extends GenericEditor {
    /**
     * Returns true always.
     *
     * @param value value to be validated
     * @return true always
     */
    @Override
    public boolean validate(Object value) {
        return true;
    }

    /**
     * Returns false as we do not allow empty value.
     *
     * @return false as we do not allow empty value
     */
    @Override
    public boolean isEmptyAllowed() {
        return false;
    }
}
