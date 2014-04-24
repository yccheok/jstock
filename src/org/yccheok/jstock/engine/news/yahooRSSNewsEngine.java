/*
* JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.engine.news;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class makes yahoo news request and read RSS reply.
 * @author schern
 */
public class yahooRSSNewsEngine {
    public List<NewsItem> urlToBean(String ticker) throws XMLStreamException, MalformedURLException{
       
       try{
           
          JAXBContext jc = JAXBContext.newInstance(rss.class);
          Unmarshaller um = jc.createUnmarshaller();
          um.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
          StringBuilder sb = new StringBuilder();
          sb.append(yahooNewsURL);
          sb.append(ticker);
          URL url = new URL(sb.toString());
          rss r = (rss)um.unmarshal(url);
          
          channel c = r.getCh();
          
          listItems = c.getNi();
           
         
       }catch(JAXBException e){
           log.error(null, e);
       }
       
       return listItems;
   }
    
    List<NewsItem> listItems = new ArrayList<NewsItem>(); 
    private final String yahooNewsURL="http://finance.yahoo.com/rss/headline?s=";
    
    private static final Log log = LogFactory.getLog(yahooRSSNewsEngine.class);
}
