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
    
    // readResolve will no longer be triggered any more after upgrading from
    // XStream 1.4.2 to 1.4.11.1 (Because we want to support Java 11)
    // The correct solution is that, all affected classes need to implement 
    // Serializable. However, if we do so, all previous written XML files are no
    // longer valid - https://github.com/x-stream/xstream/issues/179
    //
    // As such, we will abandon usage of readResolve, with our very own hacked
    // workaround.
    //
    /* initialize a transient field at deserialization? */
    private Object readResolve() {
        propertySupport = new PropertyChangeSupport(this);
        return this;
    }
  
    private void ensurePropertySupportIsNotNull() {
        if (propertySupport == null) {
            propertySupport = new PropertyChangeSupport(this);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        ensurePropertySupportIsNotNull();
        propertySupport.addPropertyChangeListener(listener);
    }
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        ensurePropertySupportIsNotNull();
        propertySupport.addPropertyChangeListener( propertyName, listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        ensurePropertySupportIsNotNull();
        propertySupport.removePropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        ensurePropertySupportIsNotNull();
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }
    
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        ensurePropertySupportIsNotNull();
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        ensurePropertySupportIsNotNull();
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        ensurePropertySupportIsNotNull();
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }    
}
