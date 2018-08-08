/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.gui;

import java.beans.*;

/**
 *
 * @author yccheok
 */
public class MutableStockBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.yccheok.jstock.gui.MutableStock.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
        // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_board = 0;
    private static final int PROPERTY_buyPrice = 1;
    private static final int PROPERTY_buyQuantity = 2;
    private static final int PROPERTY_changePrice = 3;
    private static final int PROPERTY_changePricePercentage = 4;
    private static final int PROPERTY_code = 5;
    private static final int PROPERTY_highPrice = 6;
    private static final int PROPERTY_industry = 7;
    private static final int PROPERTY_lastPrice = 8;
    private static final int PROPERTY_lastVolume = 9;
    private static final int PROPERTY_lowPrice = 10;
    private static final int PROPERTY_name = 11;
    private static final int PROPERTY_openPrice = 12;
    private static final int PROPERTY_prevPrice = 13;
    private static final int PROPERTY_sellPrice = 14;
    private static final int PROPERTY_sellQuantity = 15;
    private static final int PROPERTY_stock = 16;
    private static final int PROPERTY_symbol = 17;
    private static final int PROPERTY_volume = 18;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[19];
    
        try {
            properties[PROPERTY_board] = new PropertyDescriptor ( "board", org.yccheok.jstock.gui.MutableStock.class, "getBoard", null ); // NOI18N
            properties[PROPERTY_buyPrice] = new PropertyDescriptor ( "buyPrice", org.yccheok.jstock.gui.MutableStock.class, "getBuyPrice", "setBuyPrice" ); // NOI18N
            properties[PROPERTY_buyQuantity] = new PropertyDescriptor ( "buyQuantity", org.yccheok.jstock.gui.MutableStock.class, "getBuyQuantity", "setBuyQuantity" ); // NOI18N
            properties[PROPERTY_changePrice] = new PropertyDescriptor ( "changePrice", org.yccheok.jstock.gui.MutableStock.class, "getChangePrice", "setChangePrice" ); // NOI18N
            properties[PROPERTY_changePricePercentage] = new PropertyDescriptor ( "changePricePercentage", org.yccheok.jstock.gui.MutableStock.class, "getChangePricePercentage", "setChangePricePercentage" ); // NOI18N
            properties[PROPERTY_code] = new PropertyDescriptor ( "code", org.yccheok.jstock.gui.MutableStock.class, "getCode", null ); // NOI18N
            properties[PROPERTY_highPrice] = new PropertyDescriptor ( "highPrice", org.yccheok.jstock.gui.MutableStock.class, "getHighPrice", "setHighPrice" ); // NOI18N
            properties[PROPERTY_industry] = new PropertyDescriptor ( "industry", org.yccheok.jstock.gui.MutableStock.class, "getIndustry", null ); // NOI18N
            properties[PROPERTY_lastPrice] = new PropertyDescriptor ( "lastPrice", org.yccheok.jstock.gui.MutableStock.class, "getLastPrice", "setLastPrice" ); // NOI18N
            properties[PROPERTY_lastVolume] = new PropertyDescriptor ( "lastVolume", org.yccheok.jstock.gui.MutableStock.class, "getLastVolume", "setLastVolume" ); // NOI18N
            properties[PROPERTY_lowPrice] = new PropertyDescriptor ( "lowPrice", org.yccheok.jstock.gui.MutableStock.class, "getLowPrice", "setLowPrice" ); // NOI18N
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", org.yccheok.jstock.gui.MutableStock.class, "getName", null ); // NOI18N
            properties[PROPERTY_openPrice] = new PropertyDescriptor ( "openPrice", org.yccheok.jstock.gui.MutableStock.class, "getOpenPrice", "setOpenPrice" ); // NOI18N
            properties[PROPERTY_prevPrice] = new PropertyDescriptor ( "prevPrice", org.yccheok.jstock.gui.MutableStock.class, "getPrevPrice", "setPrevPrice" ); // NOI18N
            properties[PROPERTY_sellPrice] = new PropertyDescriptor ( "sellPrice", org.yccheok.jstock.gui.MutableStock.class, "getSellPrice", "setSellPrice" ); // NOI18N
            properties[PROPERTY_sellQuantity] = new PropertyDescriptor ( "sellQuantity", org.yccheok.jstock.gui.MutableStock.class, "getSellQuantity", "setSellQuantity" ); // NOI18N
            properties[PROPERTY_stock] = new PropertyDescriptor ( "stock", org.yccheok.jstock.gui.MutableStock.class, "getStock", null ); // NOI18N
            properties[PROPERTY_symbol] = new PropertyDescriptor ( "symbol", org.yccheok.jstock.gui.MutableStock.class, "getSymbol", null ); // NOI18N
            properties[PROPERTY_volume] = new PropertyDescriptor ( "volume", org.yccheok.jstock.gui.MutableStock.class, "getVolume", "setVolume" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
        // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[0];//GEN-HEADEREND:Events
        // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_toString0 = 0;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[1];
    
        try {
            methods[METHOD_toString0] = new MethodDescriptor(org.yccheok.jstock.gui.MutableStock.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString0].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
        // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null;//GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null;//GEN-END:IconsDef
    private static String iconNameC16 = null;//GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null;//GEN-END:Icons

    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


//GEN-FIRST:Superclass
    // Here you can add code for customizing the Superclass BeanInfo.

//GEN-LAST:Superclass
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable properties of this bean.
     * May return null if the information should be obtained by automatic
     * analysis.
     */
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean. May return null if the information
     * should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will belong
     * to the IndexedPropertyDescriptor subclass of PropertyDescriptor. A client
     * of getPropertyDescriptors can use "instanceof" to check if a given
     * PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return An array of EventSetDescriptors describing the kinds of events
     * fired by this bean. May return null if the information should be obtained
     * by automatic analysis.
     */
    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return An array of MethodDescriptors describing the methods implemented
     * by this bean. May return null if the information should be obtained by
     * automatic analysis.
     */
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     *
     * @return Index of default property in the PropertyDescriptor array
     * returned by getPropertyDescriptors.
     * <P>
     * Returns -1 if there is no default property.
     */
    @Override
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will mostly
     * commonly be used by human's when using the bean.
     *
     * @return Index of default event in the EventSetDescriptor array returned
     * by getEventSetDescriptors.
     * <P>
     * Returns -1 if there is no default event.
     */
    @Override
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    /**
     * This method returns an image object that can be used to represent the
     * bean in toolboxes, toolbars, etc. Icon images will typically be GIFs, but
     * may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from this
     * method.
     * <p>
     * There are four possible flavors of icons (16x16 color, 32x32 color, 16x16
     * mono, 32x32 mono). If a bean choses to only support a single icon we
     * recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background so they can be
     * rendered onto an existing background.
     *
     * @param iconKind The kind of icon requested. This should be one of the
     * constant values ICON_COLOR_16x16, ICON_COLOR_32x32, ICON_MONO_16x16, or
     * ICON_MONO_32x32.
     * @return An image object representing the requested icon. May return null
     * if no suitable icon is available.
     */
    @Override
    public java.awt.Image getIcon(int iconKind) {
        switch (iconKind) {
            case ICON_COLOR_16x16:
                if (iconNameC16 == null) {
                    return null;
                } else {
                    if (iconColor16 == null) {
                        iconColor16 = loadImage(iconNameC16);
                    }
                    return iconColor16;
                }
            case ICON_COLOR_32x32:
                if (iconNameC32 == null) {
                    return null;
                } else {
                    if (iconColor32 == null) {
                        iconColor32 = loadImage(iconNameC32);
                    }
                    return iconColor32;
                }
            case ICON_MONO_16x16:
                if (iconNameM16 == null) {
                    return null;
                } else {
                    if (iconMono16 == null) {
                        iconMono16 = loadImage(iconNameM16);
                    }
                    return iconMono16;
                }
            case ICON_MONO_32x32:
                if (iconNameM32 == null) {
                    return null;
                } else {
                    if (iconMono32 == null) {
                        iconMono32 = loadImage(iconNameM32);
                    }
                    return iconMono32;
                }
            default:
                return null;
        }
    }
    
}
