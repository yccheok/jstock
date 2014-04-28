/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2014 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.AbstractTableModel;
import javax.xml.stream.XMLStreamException;
import org.yccheok.jstock.engine.news.*;
import org.yccheok.jstock.internationalization.GUIBundle;

/**
 *
 * @author schern
 */
public class NewsTModel extends AbstractTableModel{

    public NewsTModel() {
        for(int i=0; i < columnNames.length; i++){
            columnNameMapping.put(columnNames[i], i);
        }
        
    }
    
   public void add(List<Object> nData){
       NewsModel.add(nData);
       fireTableRowsInserted(NewsModel.size()-1, NewsModel.size()-1);
   }

    @Override
    public int getRowCount() {
            return NewsModel.size();
    }

    @Override
    public int getColumnCount() {
            return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        List<Object> newsInfo = NewsModel.get(row);
        return newsInfo.get(col);
        
    }

    
    @Override
    public int findColumn(String columnName) {
        return columnNameMapping.get(columnName);
    }
    
           

    @Override
    public String getColumnName(int col) {
        return columnNames[col]; 
    }
    

    
    public void getCoNews(String ticker){
        try{
        yahooRSSNewsEngine yrne = new yahooRSSNewsEngine();
        List<NewsItem> NIList = yrne.urlToBean(ticker);
        int i=0;
        for(NewsItem ni : NIList){
            NewsModel.add(newsToList(ni));
            fireTableRowsInserted(i,i);
            i++;
        }
        }catch(XMLStreamException xe){
            //do something later
        }catch(MalformedURLException me){
            //do something later
        }
        
    }
    
    // extract only needed info from object
    private List<Object> newsToList(NewsItem newsitem){
        List<Object> list = new ArrayList<Object>();
        list.add(newsitem.getPubDate());
        list.add(newsitem.gettitle());
        list.add(newsitem.getlink());
        return list;
        
    }
    private final static String[] columnNames;
    
    //3rd column is hidden, not setting proper text at this moment
    static {
        final String[] tmp={
        GUIBundle.getString("News_Time"),
        GUIBundle.getString("News_Title"),
        GUIBundle.getString("News_Title")
        };
        
        columnNames = tmp;
    }
    
    
   
    private final List<List <Object>> NewsModel = new ArrayList<List <Object>>();
    private final Map<String, Integer> columnNameMapping = new ConcurrentHashMap<String, Integer>();
   
}
