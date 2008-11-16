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
 * Copyright (C) 2008 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import javax.swing.ImageIcon;

/**
 *
 * @author yccheok
 */
public enum Country {    
    Denmark("/images/16x16/dk.png"),
    France("/images/16x16/fr.png"),
    Germany("/images/16x16/de.png"),
    Italy("/images/16x16/it.png"),
    Malaysia("/images/16x16/my.png"),    
    Norway("/images/16x16/no.png"),
    Spain("/images/16x16/es.png"),            
    Sweden("/images/16x16/se.png"),
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
