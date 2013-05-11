/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui.table;

import org.yccheok.jstock.engine.StockInfo;

/**
 *
 * @author yccheok
 */
public class StockInfoRenderer extends GenericRenderer {
    @Override
    public void setValue(Object value) {
        setText((value == null) ? "" : ((StockInfo)value).symbol.toString());
    }
}
