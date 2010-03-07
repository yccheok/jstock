/*
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

package org.yccheok.jstock.gui;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 *
 * Having reflection call on HSSFCell's setCellValue is complicated enough
 * for us to spawn another utility class to handle this.
 *
 * @author yccheok
 */
public class POIUtils {
    // Prevent from instantiation.
    private POIUtils() {}

    public static void invokeSetCellValue(HSSFCell cell, Object value) {
        Method method = findMethodToInvoke(value);
        if (method == null) {
            // Cannot find any matching method. But we do not want to convert Integer
            // to String. Convert Integer to Double to try first.
            if (value.getClass().equals(Integer.class))
            {
                final Double doubleInteger = new Double(((Integer)value).doubleValue());
                Method method2 = findMethodToInvoke(doubleInteger);
                if (method2 != null) {
                    try {
                        method2.invoke(cell, doubleInteger);
                    } catch (Exception e) {
                        log.error(null, e);
                    }

                    return;
                }
            }
            
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            if (value.getClass().equals(Long.class))
            {
                final Double doubleLong = new Double(((Long)value).doubleValue());
                Method method2 = findMethodToInvoke(doubleLong);
                if (method2 != null) {
                    try {
                        method2.invoke(cell, doubleLong);
                    } catch (Exception e) {
                        log.error(null, e);
                    }

                    return;
                }
            }

            // Cannot find any matching method. Convert to String and try again.
            final String string = value.toString();

            Method method3 = findMethodToInvoke(string);
            if (method3 == null) {
                throw new RuntimeException("Nothing found for " + value.getClass());
            }

            try {
                method3.invoke(cell, string);
            } catch (Exception e) {
                log.error(null, e);
            }

            return;
        }

        try {
            method.invoke(cell, value);
        } catch (Exception e) {
            log.error(null, e);
        }
    }

    private static Method findMethodToInvoke(Object value) {
        Method method = parameterTypeMap.get(value.getClass());
        if (method != null) {
            return method;
        }

        // Look for superclasses
        Class<?> x = value.getClass().getSuperclass();
        while (x != null && x != Object.class) {
            method = parameterTypeMap.get(x);
            if (method != null) {
                return method;
            }
            x = x.getSuperclass();
        }

        // Look for interfaces
        for (Class<?> i : value.getClass().getInterfaces()) {
            method = parameterTypeMap.get(i);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    // See http://java.sun.com/javase/6/docs/api/java/lang/Class.html#isPrimitive()
    private static void handlePrimitive(Method method, Class<?> clazz) {
        if (clazz == Boolean.TYPE) {
            parameterTypeMap.put(Boolean.class, method);
        }
        else if (clazz == Character.TYPE) {
            parameterTypeMap.put(Character.class, method);
        }
        else if (clazz == Byte.TYPE) {
            parameterTypeMap.put(Byte.class, method);
        }
        else if (clazz == Short.TYPE) {
            parameterTypeMap.put(Short.class, method);
        }
        else if (clazz == Integer.TYPE) {
            parameterTypeMap.put(Integer.class, method);
        }
        else if (clazz == Long.TYPE) {
            parameterTypeMap.put(Long.class, method);
        }
        else if (clazz == Float.TYPE) {
            parameterTypeMap.put(Float.class, method);
        }
        else if (clazz == Double.TYPE) {
            parameterTypeMap.put(Double.class, method);
        } // ... and so on for the other six primitive types (void doesn't matter)
    }

    private static final Map<Object, Method> parameterTypeMap = new HashMap<Object, Method>();
    private static final Log log = LogFactory.getLog(POIUtils.class);

    static {
        Method[] methods = HSSFCell.class.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("setCellValue")) {
                Class<?>[] clazzes = method.getParameterTypes();
                if (clazzes.length != 1) {
                    continue;
                }
                if (clazzes[0].isPrimitive()) {
                    handlePrimitive(method, clazzes[0]);
                    // findMethodToInvoke will never have primitive type.
                    // Hence, there is need not to have primitive key in
                    // parameterTypeMap.
                    continue;
                }
                parameterTypeMap.put(clazzes[0], method);
            }
        }
    }
}
