package org.yccheok.jstock.news;

import java.util.List;

import it.sauronsoftware.feed4j.bean.FeedItem;
import org.yccheok.jstock.engine.StockInfo;

public interface NewsServer {
    public List<FeedItem> getMessages(StockInfo stockInfo);
}
