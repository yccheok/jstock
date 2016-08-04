/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author shuwnyuan
 */

public class AccData {
    private final SimpleStringProperty field;
    private final SimpleStringProperty value;

    public AccData(String f, Double v) {
        this.field = new SimpleStringProperty(f);
        this.value = new SimpleStringProperty(Utility.formatNumber(v));
    }

    public String getField() {
        return field.get();
    }
    public void setField(String v) {
        field.set(v);
    }

    public String getValue() {
        return value.get();
    }
    public void setValue(String v) {
        value.set(v);
    }
}
    

