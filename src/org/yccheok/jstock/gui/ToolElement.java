/*
 * ToolElement.java
 *
 * Created on 26 marzo 2001, 0.02
 */

package org.yccheok.jstock.gui;

import java.util.*;
/**
 *
 * @author  pmarrone
 */
public class ToolElement {
    
    protected Map<Object, Object> params; 
    protected String type;
    
    /** Creates new ToolElement */
    public ToolElement() {
        params = new HashMap<Object, Object>();
    }
    public ToolElement(String newType)
    {
        this();
        type = newType;
    }
    
    public Map getParams() {
        return params;
    }
    
    public void setParam(Object key, Object value) {
        if (!params.containsKey(key))
            params.put(key, value);
    }
    
    public Object getParam(Object key) {
        return params.get(key);
    }
    
    // This will determine, whether it is an  1 input 1 output, 0 input
    // 1 output, 1 input 0 output ...
    public String getType() {
        return type;
    }
    
    public void setType(String newType) {
        type = newType;
    }
    
}
