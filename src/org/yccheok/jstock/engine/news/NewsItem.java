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

package org.yccheok.jstock.engine.news;

import java.net.URL;

/**
 *
 * @author schern
 */
public class NewsItem {
    private String guid=null;
    private String pubDate=null;
    private String title=null;
    private URL link = null;
    private String description=null;

    public  String getGuid() {
        return guid;
    }

 
    public  void setGuid(String guid) {
        this.guid = guid;
    }

    public  String getPubDate() {
        return pubDate;
    }

    public  String gettitle() {
        return title;
    }

    public  URL getlink() {
        return link;
    }

  
    public  void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    
    public  void settitle(String title) {
        this.title = title;
    }


    public  void setlink(URL link) {
        this.link = link;
    }

    public  String getDescription() {
        return description;
    }


    public  void setDescription(String description) {
        this.description = description;
    }
}
