/*
 *  NachoCalendar
 *
 * Project Info:  http://nachocalendar.sf.net
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Changes
 * -------
 *
 * -------
 *
 * Customizer.java
 *
 * Created on Dec 17, 2005
 */

package net.sf.nachocalendar.customizer;

import java.util.Set;

/**
 * Interface that must implement any class
 * used to read a configuration file to
 * get property values.
 * 
 * @author Ignacio Merani
 *
 *
 */
public interface Customizer {

    /**
     * Returns an int related to the key. 
     * 
     * @param key
     * @return
     */
    public int getInteger(String key);

    /**
     * Returns a boolean related to the key.
     *  
     * @param key
     * @return
     */
    public boolean getBoolean(String key);

    /**
     * Returns a String related to the key. 
     * 
     * @param key
     * @return
     */
    public String getString(String key);

    /**
     * Returns a long related to the key.
     *  
     * @param key
     * @return
     */
    public long getLong(String key);

    /**
     * Returns a float related to the key.
     *  
     * @param key
     * @return
     */
    public float getFloat(String key);

    /**
     * Returns a double related to the key. 
     * 
     * @param key
     * @return
     */
    public double getDouble(String key);

    /**
     * Returns a Set with the properties names.
     * @return
     */
    public Set keySet();

}