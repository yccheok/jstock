/*
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
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yccheok
 */
public class GUIOptions {
    private final List<JTableOptions> jTableOptionsList = new ArrayList<JTableOptions>();

    public static class JTableOptions
    {
        private final List<String> columnNames = new ArrayList<String>();

        public int getColumnSize()
        {
            return columnNames.size();
        }

        public String getColumnName(int index)
        {
            return columnNames.get(index);
        }

        public boolean contains(String name)
        {
            return columnNames.contains(name);
        }

        public boolean addColumnName(String name)
        {
            /* We do not allow duplication. */
            if (columnNames.contains(name))
            {
                return false;
            }

            columnNames.add(name);
            return true;
        }
    }

    private static class UnmodifiableJTableOptions extends JTableOptions {
        private final JTableOptions jTableOptions;

        public UnmodifiableJTableOptions(JTableOptions jTableOptions)
        {
            this.jTableOptions = jTableOptions;
        }

        @Override
        public int getColumnSize()
        {
            return jTableOptions.getColumnSize();
        }

        @Override
        public String getColumnName(int index)
        {
            return jTableOptions.getColumnName(index);
        }

        @Override
        public boolean contains(String name)
        {
            return jTableOptions.contains(name);
        }

        @Override
        public boolean addColumnName(String name)
        {
            throw new java.lang.UnsupportedOperationException();
        }
    }

    public int getJTableOptionsSize() {
        return jTableOptionsList.size();
    }

    public JTableOptions getJTableOptions(int index)
    {
		/* Returns immutable version. */
        return new UnmodifiableJTableOptions(jTableOptionsList.get(index));
    }

    public void addJTableOptions(JTableOptions jTableOptions) {
        this.jTableOptionsList.add(jTableOptions);
    }
}
