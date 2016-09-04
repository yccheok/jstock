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

import org.yccheok.jstock.engine.Country;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.sauronsoftware.feed4j.bean.FeedItem;
import it.sauronsoftware.feed4j.bean.RawNode;
import it.sauronsoftware.feed4j.bean.RawElement;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
    

public class Utils {
    private Utils() {
    }
    
    public static List<String> getPaidNewsUrls() {
        return paidNewsUrls;
    }

    public static NewsSource getDefaultNewsSource(Country country) {
        assert(defaultNewsSources.containsKey(country));
        return defaultNewsSources.get(country);
    }

    public static Set<NewsSource> getSupportedNewsSources(Country country) {
        List<NewsServer> newsServers = NewsServerFactory.getNewsServers(country);
        Set<NewsSource> set = EnumSet.noneOf(NewsSource.class);
        for (NewsServer newsServer : newsServers) {
            NewsSource newsSource = classToNewsSourceMap.get(newsServer.getClass());
            if (newsSource != null) {
                set.add(newsSource);
            }
        }
        return set;
    }
    
    // explicitly set pubDate for FeedItem, as feed4j failed to handle in it.sauronsoftware.feed4j.TypeRSS_2_0
    public static Date getProperPubDate (FeedItem message) {
        for (int j = 0, ej = message.getNodeCount(); j < ej; j++) {
            RawNode node = message.getNode(j);
            if (node instanceof RawElement) {
                RawElement element = (RawElement) node;
                String name = element.getName();
                String value = element.getValue();               
                
                if (value != null && name.equals("pubDate")) {
                    try {
                        return FORMATTER.get().parse(value);
                    } catch (ParseException ex) {
                        log.error(null, ex);
                    }
                }
            }
        }
        return null;
    }
    
    private static final Map<Country, NewsSource> defaultNewsSources = new EnumMap<>(Country.class);
    private static final Map<Class<? extends NewsServer>, NewsSource> classToNewsSourceMap = new HashMap<>();
    private static final List<String> paidNewsUrls = new ArrayList<String>();

    static {
        defaultNewsSources.put(Country.Argentina, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Australia, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Austria, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Belgium, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Brazil, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Canada, NewsSource.Yahoo);
        defaultNewsSources.put(Country.China, NewsSource.Yahoo);
        //defaultNewsSources.put(Country.Czech, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Denmark, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Finland, NewsSource.Google);
        defaultNewsSources.put(Country.France, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Germany, NewsSource.Yahoo);
        defaultNewsSources.put(Country.HongKong, NewsSource.Yahoo);
        //defaultNewsSources.put(Country.Hungary, NewsSource.Yahoo);
        defaultNewsSources.put(Country.India, NewsSource.Google);
        defaultNewsSources.put(Country.Indonesia, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Israel, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Italy, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Japan, NewsSource.Google);
        defaultNewsSources.put(Country.Korea, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Malaysia, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Netherlands, NewsSource.Yahoo);
        defaultNewsSources.put(Country.NewZealand, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Norway, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Portugal, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Russia, NewsSource.Google);
        defaultNewsSources.put(Country.SaudiArabia, NewsSource.Google);
        defaultNewsSources.put(Country.Singapore, NewsSource.Yahoo);
        defaultNewsSources.put(Country.SouthAfrica, NewsSource.Google);
        defaultNewsSources.put(Country.Spain, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Sweden, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Switzerland, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Taiwan, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Thailand, NewsSource.Google);
        defaultNewsSources.put(Country.Turkey, NewsSource.Google);
        defaultNewsSources.put(Country.UnitedKingdom, NewsSource.Yahoo);
        defaultNewsSources.put(Country.UnitedState, NewsSource.Yahoo);

        classToNewsSourceMap.put(GoogleFinanceNewsServer.class, NewsSource.Google);
        classToNewsSourceMap.put(GoogleSearchNewsServer.class, NewsSource.GoogleSearch);
        classToNewsSourceMap.put(YahooFinanceNewsServer.class, NewsSource.Yahoo);

        paidNewsUrls.add("www.ft.com");
    }
    
    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <SimpleDateFormat> FORMATTER = new ThreadLocal <SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            // Having a fixed locale is important. If not, it will break when the device is in non-english locale.
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        }
    };
        
    private static final Log log = LogFactory.getLog(Utils.class);
}
