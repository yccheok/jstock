/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2016 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.news;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.URL;
import java.net.MalformedURLException;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;
import it.sauronsoftware.feed4j.FeedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.StockInfo;


public class GoogleSearchNewsServer implements NewsServer {

    @Override
    public List<FeedItem> getMessages(StockInfo stockInfo) {
        final String feedUrl = googleSearchNewsFeedUrl(stockInfo);
        final List<FeedItem> messages = new ArrayList<>();
        
        if (feedUrl == null) {
            return messages; 
        }
        
        final Set<String> titles = new HashSet<>();

        try {
            final URL url = new URL(feedUrl);
            final Feed feed = FeedParser.parse(url);
            final int items = feed.getItemCount();
        
            messages:
            for (int i = 0; i < items; i++) {
                FeedItem message = feed.getItem(i);
                message.setPubDate(Utils.getProperPubDate(message));
                
                if (message.getLink() == null || message.getTitle() == null || message.getTitle().isEmpty() || message.getPubDate() == null) {
                    continue;
                }

                for (String paidNewsUrl : Utils.getPaidNewsUrls()) {
                    if (message.getLink().toString().contains(paidNewsUrl)) {
                        // We only want free news.
                        continue messages;
                    }
                }
                
                final String title = message.getTitle(); 
                if (false == titles.add(title)) {
                    continue;
                }
                
                messages.add(message);
            }
        } catch (MalformedURLException | FeedException ex) {
            log.error(null, ex);
        }
        
        return messages;
    }

    // Possible return null.
    private String googleSearchNewsFeedUrl(StockInfo stockInfo) {
        // https://news.google.com/news/feeds?output=rss&q=public+bank+berhad+malaysia
        
        Country c = org.yccheok.jstock.engine.Utils.toCountry(stockInfo.code);

        if (c == Country.UnitedState) {
            // Most probably is United States. We will not specific anything
            // in Google's query.
            try {
                String query = java.net.URLEncoder.encode(stockInfo.symbol.toString(), "UTF-8");
                String url = "https://news.google.com/news/feeds?output=rss&q=" + query;
                return url;
            } catch (UnsupportedEncodingException ex) {
                log.error(null, ex);
            }                
        } else {
            try {
                String location = java.net.URLEncoder.encode(c.humanString, "UTF-8");
                String query = java.net.URLEncoder.encode(stockInfo.symbol.toString() + " " + location, "UTF-8");
                String url = "https://news.google.com/news/feeds?output=rss&q=" + query + "&as_nloc=" + location;
                return url;
            } catch (UnsupportedEncodingException ex) {
                log.error(null, ex);
            }
        }
        return null;
    }    
    
    private static final String TAG = GoogleSearchNewsServer.class.getSimpleName();
    private static final Log log = LogFactory.getLog(GoogleSearchNewsServer.class);
}
