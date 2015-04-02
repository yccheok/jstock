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
public class Board implements Comparable<Board> {
    private Board(String board) {
        this.board = board;
    }
    
    public static Board valueOf(String board) {
        if (board == null) {
            throw new java.lang.IllegalArgumentException("board cannot be null");
        }
        
        board = board.trim();
        
        if (board.isEmpty()) {
            throw new java.lang.IllegalArgumentException("board cannot be empty");
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
    public int compareTo(Board o) {
        return this.board.compareTo(o.board);
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
    
    public String name() {
        return board;
    }
    
    private final String board;
    
    // Avoid using interface. We want it to be fast!
    private static final ConcurrentHashMap<String, Board> map = new ConcurrentHashMap<>();
    
    // Common used board.
    public static final Board Unknown = Board.valueOf("Unknown");
    public static final Board UserDefined = Board.valueOf("UserDefined");
}
