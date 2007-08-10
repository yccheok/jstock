/*
 * StockHistoryOperatorBeanInfo.java
 *
 * Created on May 25, 2007, 10:53 PM
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
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.analysis;

import java.beans.*;

/**
 * @author yccheok
 */
public class StockHistoryOperatorBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.yccheok.jstock.analysis.StockHistoryOperator.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_endDate = 0;
    private static final int PROPERTY_function = 1;
    private static final int PROPERTY_startDate = 2;
    private static final int PROPERTY_type = 3;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[4];
    
        try {
            properties[PROPERTY_endDate] = new PropertyDescriptor ( "endDate", org.yccheok.jstock.analysis.StockHistoryOperator.class, "getEndDate", "setEndDate" ); // NOI18N
            properties[PROPERTY_endDate].setShortDescription ( "Ending date of this stock history." );
            properties[PROPERTY_function] = new PropertyDescriptor ( "function", org.yccheok.jstock.analysis.StockHistoryOperator.class, "getFunction", "setFunction" ); // NOI18N
            properties[PROPERTY_function].setShortDescription ( "Analysis function on the stock history information." );
            properties[PROPERTY_startDate] = new PropertyDescriptor ( "startDate", org.yccheok.jstock.analysis.StockHistoryOperator.class, "getStartDate", "setStartDate" ); // NOI18N
            properties[PROPERTY_startDate].setShortDescription ( "Starting date of this stock history." );
            properties[PROPERTY_type] = new PropertyDescriptor ( "type", org.yccheok.jstock.analysis.StockHistoryOperator.class, "getType", "setType" ); // NOI18N
            properties[PROPERTY_type].setShortDescription ( "Type of information to be obtained from stock history." );
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
        
        // Here you can add code for customizing the properties array.
        
        return properties;     }//GEN-LAST:Properties
    
    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_propertyChangeListener = 0;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[1];
    
        try {
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;     }//GEN-LAST:Events
    
    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_addInputConnection0 = 0;
    private static final int METHOD_addOutputConnection1 = 1;
    private static final int METHOD_addPropertyChangeListener2 = 2;
    private static final int METHOD_calculate3 = 3;
    private static final int METHOD_clear4 = 4;
    private static final int METHOD_connectorValueChange5 = 5;
    private static final int METHOD_pull6 = 6;
    private static final int METHOD_read7 = 7;
    private static final int METHOD_removeInputConnection8 = 8;
    private static final int METHOD_removeOutputConnection9 = 9;
    private static final int METHOD_removePropertyChangeListener10 = 10;
    private static final int METHOD_toString11 = 11;
    private static final int METHOD_write12 = 12;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[13];
    
        try {
            methods[METHOD_addInputConnection0] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("addInputConnection", new Class[] {org.yccheok.jstock.analysis.Connection.class, Integer.TYPE})); // NOI18N
            methods[METHOD_addInputConnection0].setDisplayName ( "" );
            methods[METHOD_addOutputConnection1] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("addOutputConnection", new Class[] {org.yccheok.jstock.analysis.Connection.class, Integer.TYPE})); // NOI18N
            methods[METHOD_addOutputConnection1].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener2] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener2].setDisplayName ( "" );
            methods[METHOD_calculate3] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("calculate", new Class[] {org.yccheok.jstock.engine.StockHistoryServer.class})); // NOI18N
            methods[METHOD_calculate3].setDisplayName ( "" );
            methods[METHOD_clear4] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("clear", new Class[] {})); // NOI18N
            methods[METHOD_clear4].setDisplayName ( "" );
            methods[METHOD_connectorValueChange5] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("connectorValueChange", new Class[] {org.yccheok.jstock.analysis.ConnectorEvent.class})); // NOI18N
            methods[METHOD_connectorValueChange5].setDisplayName ( "" );
            methods[METHOD_pull6] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("pull", new Class[] {})); // NOI18N
            methods[METHOD_pull6].setDisplayName ( "" );
            methods[METHOD_read7] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("read", new Class[] {org.jhotdraw.xml.DOMInput.class})); // NOI18N
            methods[METHOD_read7].setDisplayName ( "" );
            methods[METHOD_removeInputConnection8] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("removeInputConnection", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_removeInputConnection8].setDisplayName ( "" );
            methods[METHOD_removeOutputConnection9] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("removeOutputConnection", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_removeOutputConnection9].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener10] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener10].setDisplayName ( "" );
            methods[METHOD_toString11] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString11].setDisplayName ( "" );
            methods[METHOD_write12] = new MethodDescriptor ( org.yccheok.jstock.analysis.StockHistoryOperator.class.getMethod("write", new Class[] {org.jhotdraw.xml.DOMOutput.class})); // NOI18N
            methods[METHOD_write12].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;     }//GEN-LAST:Methods
    
    
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
    
    
//GEN-FIRST:Superclass
    
    // Here you can add code for customizing the Superclass BeanInfo.
    
//GEN-LAST:Superclass
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
}

