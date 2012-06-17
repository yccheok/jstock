/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui.analysis;

import com.l2fprod.common.swing.renderer.*;
import com.l2fprod.common.propertysheet.*;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import java.beans.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.table.TableCellEditor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.gui.EnumComboBoxPropertyEditor;

/**
 *
 * @author yccheok
 */
public class ObjectInspectorJPanel extends PropertySheetPanel {
    
    /** Creates a new instance of ObjectInspectorJPanel */
    public ObjectInspectorJPanel(Object bean) {
        this.bean = bean;
        
        BeanInfo beanInfo = null;

        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException exp) {
            log.error(null, exp);
        }
                
        this.getTable().setEditorFactory(new PropertyEditorRegistryEx());
        PropertyEditorRegistry editor = (PropertyEditorRegistry)this.getTable().getEditorFactory();
        PropertyRendererRegistry renderer = (PropertyRendererRegistry)this.getTable().getRendererFactory();

        editor.registerEditor(Enum.class, new EnumComboBoxPropertyEditor());
    
        DefaultCellRenderer r = new DefaultCellRenderer();
        r.setShowOddAndEvenRows(false);    
        renderer.registerRenderer(Enum.class, r);

        this.setMode(PropertySheet.VIEW_AS_FLAT_LIST);
        this.setToolBarVisible(false);
        this.setDescriptionVisible(false);    
        this.setToolBarVisible(true);
        this.setBeanInfo(beanInfo);        

        Property[] properties = this.getProperties();
        for (int i = 0, c = properties.length; i < c; i++) {
            properties[i].readFromObject(bean);            
        }
        
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Property prop = (Property)evt.getSource();
                prop.writeToObject(ObjectInspectorJPanel.this.bean);
            }
        };
        
        this.addPropertySheetChangeListener(listener);
    }
    

    private static class PropertyEditorRegistryEx extends PropertyEditorRegistry {
        // We will try to get the "nearest" super class.        
        @Override
        public synchronized PropertyEditor getEditor(Class type) {
            PropertyEditor editor = super.getEditor(type);

            Class c = type;
        
            while (editor == null) {
                c = c.getSuperclass();

                if(c == null)
                    return editor;

                editor = super.getEditor(c);
            }

            return editor;
        }
    }  
    
    public void setBean(Object bean) {
        this.bean = bean;
        
        BeanInfo beanInfo = null;

        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException exp) {
            log.error(null, exp);
        }
        
        this.setBeanInfo(beanInfo);        

        Property[] properties = this.getProperties();
        for (int i = 0, c = properties.length; i < c; i++) {
            properties[i].readFromObject(bean);            
        }
        
        //sheet.revalidate();
    }
    
    public Object getBean() {
        return bean;
    }
    
    /**
     * Tells the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped; this is useful for editors that validate
     * and can not accept invalid entries.
     *
     * @return	true if editing was stopped; false otherwise
     */
    public boolean stopCellEditing() {
        final TableCellEditor editor = this.getTable().getCellEditor();
        if (editor != null) {
            return editor.stopCellEditing();
        }
        return true;
    }

    private Object bean;
        
    private static final Log log = LogFactory.getLog(ObjectInspectorJPanel.class);    
}
