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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author yccheok
 */
public class GUIOptions {
    private final List<JTableOptions> jTableOptionsList = new ArrayList<JTableOptions>();
    private List<Integer> dividerLocationList = new ArrayList<Integer>();

    public static class JTableOptions
    {

        /**
         * @return the locale
         */
        public Locale getLocale() {
            return this.locale;
        }

        /**
         * @param locale the locale to set
         */
        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public static class ColumnOption {
            private String columnName;
            private final int columnWidth;

            private ColumnOption(String columnName, int columnWidth) {
                this.columnName = columnName;
                this.columnWidth = columnWidth;
            }

            public static ColumnOption newInstance(String columnName, int columnWidth) {
                return new ColumnOption(columnName, columnWidth);
            }

            public String getColumnName() {
                return columnName;
            }

            public int getColumnWidth() {
                return columnWidth;
            }

            // hashCode and equals, only perform computation on columnName.
            @Override
            public int hashCode() {
                int result = 17;
                if (null != columnName) {
                    result = 31 * result + columnName.hashCode();
                }
                return result;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this)
                    return true;

                if(!(o instanceof ColumnOption))
                    return false;

                if (this.columnName == null) {
                    return null == ((ColumnOption)o).columnName;
                }

                return this.columnName.equals(((ColumnOption)o).columnName);
            }
        }
		
        private final List<ColumnOption> columnOptions = new ArrayList<ColumnOption>();
        private Locale locale = Locale.getDefault();

        public int getColumnSize()
        {
            return columnOptions.size();
        }

        public String getColumnName(int index)
        {
            return columnOptions.get(index).getColumnName();
        }

        private Object readResolve() {
            /* For backward compatible */
            if (getLocale() == null) {
                setLocale(Locale.ENGLISH);
            }            
            return this;
        }

        public boolean contains(String name)
        {
            // Create a dummy ColumnOption for comparison purpose.
            return columnOptions.contains(ColumnOption.newInstance(name, 0));
        }

        public int getColumnWidth(int index)
        {
            return columnOptions.get(index).getColumnWidth();
        }

        public boolean addColumnOption(ColumnOption option) {
            /* We do not allow duplication. */
            if (columnOptions.contains(option))
            {
                return false;
            }
            columnOptions.add(option);
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
        public int getColumnWidth(int index)
        {
            return jTableOptions.getColumnWidth(index);
        }

        @Override
        public boolean addColumnOption(ColumnOption option)
        {
            throw new java.lang.UnsupportedOperationException();
        }
        
        @Override
        public Locale getLocale() {
            return jTableOptions.getLocale();
        }
    }

    public int getJTableOptionsSize() {
        return jTableOptionsList.size();
    }

    public int getDividerLocationSize() {
        return dividerLocationList.size();
    }

    public JTableOptions getJTableOptions(int index)
    {
        /* Returns immutable version. */
        return new UnmodifiableJTableOptions(jTableOptionsList.get(index));
    }

    public int getDividerLocation(int index) {
        return dividerLocationList.get(index);

    }

    public void addJTableOptions(JTableOptions jTableOptions) {
        this.jTableOptionsList.add(jTableOptions);
    }

    public void addDividerLocation(int dividerLocation) {
        this.dividerLocationList.add(dividerLocation);
    }

    private Object readResolve() {
        /* For backward compatible */
        if (dividerLocationList == null) {
            dividerLocationList = new ArrayList<Integer>();
        }
        return this;
    }
}
