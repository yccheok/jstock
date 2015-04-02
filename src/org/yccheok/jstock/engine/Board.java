/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yccheok
 */
public class Board {
    private Board(String board) {
        this.board = board;
    }
    
    public static Board newInstance(String board) {
        if (board == null) {
            throw new java.lang.IllegalArgumentException("board cannot be null");
        }
        
        Board result = map.get(board);
        if (result == null) {
            final Board instance = new Board(board);
            result = map.putIfAbsent(board, instance);
            if (result == null) {
                return instance;
            }
        }
        
        assert(result != null);
        return result;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + board.hashCode();
        
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Board)) {
            return false;
        }
        
        return this.board.equals(((Board)o).board);
    }
    
    @Override
    public String toString() {
        return board;
    }
    
    // Avoid using interface. We want it to be fast!
    private static final ConcurrentHashMap<String, Board> map = new ConcurrentHashMap<>();
    private final String board;
}
