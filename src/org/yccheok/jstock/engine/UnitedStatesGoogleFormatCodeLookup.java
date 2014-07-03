/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.engine;

import java.util.Map;

/**
 *
 * @author yccheok
 */
public enum UnitedStatesGoogleFormatCodeLookup {
    INSTANCE;
    
    public String put(Code code, String googleFormatCode) {
        return map.put(code, googleFormatCode);
    }
    
    public String get(Code code) {
        return map.get(code);
    }
    
    private static final Map<Code, String> map = new java.util.concurrent.ConcurrentHashMap<Code, String>();
}
