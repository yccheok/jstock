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
 * Copyright (C) 2008 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

/**
 *
 * @author yccheok
 */
public class Code {
    private Code(String code) {
        this.code = code;
    }
    
    public static Code newInstance(String code) {
        if(code == null) {
            throw new java.lang.IllegalArgumentException("code cannot be null");
        }
        
        return new Code(code);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + code.hashCode();
        
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if(!(o instanceof Code))
            return false;
        
        return this.code.equals(((Code)o).code);
    }
    
    @Override
    public String toString() {
        return code;
    }
    
    private String code;
}
