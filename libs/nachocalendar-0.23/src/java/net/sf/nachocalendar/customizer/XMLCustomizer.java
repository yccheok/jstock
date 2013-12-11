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
 * XMLCustomizer.java
 *
 * Created on Dec 28, 2005
 */
package net.sf.nachocalendar.customizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class reads an xml file to get properties values.
 * @author Ignacio Merani
 *
 * 
 */
public class XMLCustomizer extends DefaultHandler implements Customizer {
    private Properties properties;
    private String name;
    private StringBuffer value;
    
    public XMLCustomizer(InputStream config) throws SAXException, IOException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(config, this);
    }
    
    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.Customizer#getInteger(java.lang.String)
     */
    public int getInteger(String key) {
        return PropertiesConverter.getInteger(properties.getProperty(key));
    }

    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.Customizer#getBoolean(java.lang.String)
     */
    public boolean getBoolean(String key) {
        return PropertiesConverter.getBoolean(properties.getProperty(key));
    }
    
    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.Customizer#getString(java.lang.String)
     */
    public String getString(String key) {
        return properties.getProperty(key);
    }
    
    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.Customizer#getLong(java.lang.String)
     */
    public long getLong(String key) {
        return PropertiesConverter.getLong(properties.getProperty(key));
    }
    
    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.Customizer#getFloat(java.lang.String)
     */
    public float getFloat(String key) {
        return PropertiesConverter.getFloat(properties.getProperty(key));
    }
    
    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.Customizer#getDouble(java.lang.String)
     */
    public double getDouble(String key) {
        return PropertiesConverter.getDouble(properties.getProperty(key));
    }
    
    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.Customizer#keySet()
     */
    public Set keySet() {
        return properties.keySet();
    }

    public void startDocument() throws SAXException {
        properties = new Properties();
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        value.append(ch, start, length);
        
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (qName.equals("property"))
            properties.put(name, value.toString());
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (qName.equals("property")) {
            name = atts.getValue("name");
            value = new StringBuffer();
        }
    }

}
