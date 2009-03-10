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

package org.yccheok.jstock.engine;

import javax.swing.ImageIcon;

/**
 *
 * @author yccheok
 */
public enum Country {
    Australia("/images/16x16/au.png"),
    Austria("/images/16x16/at.png"),
    Belgium("/images/16x16/be.png"),
    Canada("/images/16x16/ca.png"),
    Denmark("/images/16x16/dk.png"),
    France("/images/16x16/fr.png"),
    Germany("/images/16x16/de.png"),
    HongKong("/images/16x16/hk.png"),
    India("/images/16x16/in.png"),
    Indonesia("/images/16x16/id.png"),
    Italy("/images/16x16/it.png"),
    Korea("/images/16x16/kr.png"),
    Malaysia("/images/16x16/my.png"),
    Netherlands("/images/16x16/nl.png"),
    Norway("/images/16x16/no.png"),
    Portugal("/images/16x16/pt.png"),
    Singapore("/images/16x16/sg.png"),
    Spain("/images/16x16/es.png"),            
    Sweden("/images/16x16/se.png"),
    Switzerland("/images/16x16/ch.png"),
    Taiwan("/images/16x16/tw.png"),
    UnitedKingdom("/images/16x16/gb.png"),
    UnitedState("/images/16x16/us.png");
            
    Country(String fileName) {
        this.icon = new javax.swing.ImageIcon(this.getClass().getResource(fileName));
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    private ImageIcon icon;
}
