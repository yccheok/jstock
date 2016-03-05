package org.yccheok.jstock.news;

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

import org.yccheok.jstock.engine.StockInfo;


public class YahooFinanceNewsServer implements NewsServer {

    @Override
    public List<FeedItem> getMessages(StockInfo stockInfo) {
        // http://feeds.finance.yahoo.com/rss/2.0/headline?s=0005.HK&region=US&lang=en-US
        final String feedUrl = "https://feeds.finance.yahoo.com/rss/2.0/headline?s=" + stockInfo.code + "&region=US&lang=en-US";
        final List<FeedItem> messages = new ArrayList<>();
        final Set<String> titles = new HashSet<>();
            
        try {
            final URL url = new URL(feedUrl);
            final Feed feed = FeedParser.parse(url);
            final int items = feed.getItemCount();
            
            messages:
            for (int i = 0; i < items; i++) {
                FeedItem message = feed.getItem(i);
                message.setPubDate(Utils.getProperPubDate(message));
                
                if (message.getLink() == null || message.getTitle() == null || message.getTitle().isEmpty() ||  message.getPubDate() == null) {
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
    
    private static final Log log = LogFactory.getLog(YahooFinanceNewsServer.class);
}
