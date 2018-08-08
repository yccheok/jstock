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
import org.yccheok.jstock.engine.Code;

import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.yahoo.quote.QuoteResponse;
import org.yccheok.jstock.engine.yahoo.quote.QuoteResponse_;
import retrofit2.Call;


public class YahooFinanceNewsServer implements NewsServer {

    @Override
    public List<FeedItem> getMessages(StockInfo stockInfo) {
        final Code code = stockInfo.code;

        String query = org.yccheok.jstock.engine.Utils.toYahooFormat(code);

        if (org.yccheok.jstock.engine.Utils.needToResolveUnderlyingCode(code)) {
            Call<QuoteResponse> c = org.yccheok.jstock.engine.Utils.getYahooFinanceApi().quote(query);
            try {
                QuoteResponse quoteResponse = c.execute().body();
                QuoteResponse_ quoteResponse_ = quoteResponse.getQuoteResponse();
                List<org.yccheok.jstock.engine.yahoo.quote.Result> results = quoteResponse_.getResult();
                query = results.get(0).getUnderlyingSymbol();
            } catch (Exception e) {
                log.error(null, e);
            }
        }

        // http://feeds.finance.yahoo.com/rss/2.0/headline?s=0005.HK&region=US&lang=en-US
        final String feedUrl = "https://feeds.finance.yahoo.com/rss/2.0/headline?s=" + query + "&region=US&lang=en-US";
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
