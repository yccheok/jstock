/*
 * OperatorFigureCreationTool.java
 *
 * Created on May 19, 2007, 4:37 PM
 *
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

package org.yccheok.jstock.gui.analysis;

import org.jhotdraw.draw.*;
import java.util.*;

/**
 *
 * @author yccheok
 */
public class OperatorFigureCreationTool extends org.jhotdraw.draw.CreationTool {
    
    /** Creates a new instance of OperatorFigureCreationTool */
    public OperatorFigureCreationTool(java.lang.String prototypeClassName, java.util.Map<AttributeKey,java.lang.Object> attributes) {
        super(prototypeClassName, attributes);
        
        this.prototypeClassName = prototypeClassName;
        this.prototypeAttributes = attributes;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Figure createFigure() {
        Figure f = null;
        
        try {
            f = (Figure) Class.forName(prototypeClassName).newInstance();
        } catch (Exception e) {
            InternalError error = new InternalError("Unable to create Figure from "+prototypeClassName);
            error.initCause(e);
            throw error;
        }
        
        for (Map.Entry<AttributeKey, Object> entry : prototypeAttributes.entrySet()) {
            f.setAttribute(entry.getKey(), entry.getValue());
        } 
        
        return f;

    }
    
    private String prototypeClassName;
    private Map<AttributeKey, Object> prototypeAttributes;
}
