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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author schern
 */
public class channel {
    private String title;
    private String copyright;
    private String link;
    private String description;
    private String language;
    private String lastBuildDate;
    
    List<NewsImage> Nimage = new ArrayList<NewsImage>();
    
    List<NewsItem> Ni = new ArrayList<NewsItem>();

    public String getTitle() {
        return title;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

 
    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name="image")
    public List<NewsImage> getNimage() {
        return Nimage;
    }

    public void setNimage(List<NewsImage> Nimage) {
        this.Nimage = Nimage;
    }
    
    
    @XmlElement(name="item")
    public List<NewsItem> getNi() {
        return Ni;
    }

    public void setNi(List<NewsItem> ni) {
        this.Ni = ni;
    }
}
