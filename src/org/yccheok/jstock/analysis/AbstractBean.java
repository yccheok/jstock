/*
 * AbstractBean.java
 *
 * Created on May 22, 2007, 11:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.yccheok.jstock.analysis;

import java.beans.*;

/**
 *
 * @author yccheok
 */
public class AbstractBean {
    
    // We will not perform serialization on PropertyChangeSupport. This is because it may
    // hold JHotDraw drawing as listener. Currently, XStream is having problem in serialization
    // with JHotDraw. In case, we will have a workaround, by register the listener manually
    // during restore.
    protected transient PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    
    /* initialize a transient field at deserialization? */
    private Object readResolve() {
        propertySupport = new PropertyChangeSupport(this);
        return this;
    }
  
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener( propertyName, listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }
    
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }    
}
