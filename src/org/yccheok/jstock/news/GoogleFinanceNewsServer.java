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

import org.yccheok.jstock.engine.StockInfo;


public class GoogleFinanceNewsServer implements NewsServer {

    @Override
    public List<FeedItem> getMessages(StockInfo stockInfo) {
        final String feedUrl = googleFinanceNewsFeedUrl(stockInfo);
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
        } catch (MalformedURLException | FeedException e) {
        }
        
        return messages;
    }

    // Possible return null.
    private String googleFinanceNewsFeedUrl(StockInfo stockInfo) {
        // https://www.google.com/finance/company_news?q=NYSE:SAN&output=rss
        
        final String googleFormat = org.yccheok.jstock.engine.Utils.toGoogleFormat(stockInfo.code);
        
        try {
            String query = java.net.URLEncoder.encode(googleFormat, "UTF-8");
            String url = "https://www.google.com/finance/company_news?output=rss&q=" + query;
            return url;
        } catch (UnsupportedEncodingException ex) {
        }                
        return null;
    }    
    
    private static final String TAG = GoogleFinanceNewsServer.class.getSimpleName();
}
