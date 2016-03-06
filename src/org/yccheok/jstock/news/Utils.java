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
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
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

    private static final Map<Country, NewsSource> defaultNewsSources = new EnumMap<>(Country.class);
    private static final Map<Class<? extends NewsServer>, NewsSource> classToNewsSourceMap = new HashMap<>();
    private static final List<String> paidNewsUrls = new ArrayList<String>();

    static {
        defaultNewsSources.put(Country.Australia, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Austria, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Belgium, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Brazil, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Canada, NewsSource.Yahoo);
        defaultNewsSources.put(Country.China, NewsSource.Yahoo);
        //defaultNewsSources.put(Country.Czech, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Denmark, NewsSource.Yahoo);
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
        defaultNewsSources.put(Country.Singapore, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Spain, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Sweden, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Switzerland, NewsSource.Yahoo);
        defaultNewsSources.put(Country.Taiwan, NewsSource.Yahoo);
        defaultNewsSources.put(Country.UnitedKingdom, NewsSource.Yahoo);
        defaultNewsSources.put(Country.UnitedState, NewsSource.Yahoo);

        classToNewsSourceMap.put(GoogleFinanceNewsServer.class, NewsSource.Google);
        classToNewsSourceMap.put(GoogleSearchNewsServer.class, NewsSource.GoogleSearch);
        classToNewsSourceMap.put(YahooFinanceNewsServer.class, NewsSource.Yahoo);

        paidNewsUrls.add("www.ft.com");
    }
    
    // explicitly set pubDate for FeedItem, as feed4j failed to handle in it.sauronsoftware.feed4j.TypeRSS_2_0
    public static Date getProperPubDate (FeedItem message) {
        for (int j = 0; j < message.getNodeCount(); j++) {
            RawNode node = message.getNode(j);
            if (node instanceof RawElement) {
                RawElement element = (RawElement) node;
                String name = element.getName();
                String value = element.getValue();

                if (name.equals("pubDate") && value != null) {
                    try {
                        return new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'z", Locale.US).parse(value);
                    } catch (ParseException ex) {
                        log.error(null, ex);
                    }
                }
            }
        }
        return null;
    }

    public static String getPubDateDiff (Date pubDate) {
        Date now = new java.util.Date();
        String pubDateDiff;
        
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(pubDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(now);

        if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            pubDateDiff = formatter.format(pubDate);
        } else {
            long diffInMillies = now.getTime() - pubDate.getTime();
            long hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (hours <= 1) {
                pubDateDiff = "1 hour ago";
            } else if (hours < 24) {
                pubDateDiff = hours + " hours ago";
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
                pubDateDiff = formatter.format(pubDate);
            }
        }
        
        return pubDateDiff;
    }
    
    private static final Log log = LogFactory.getLog(Utils.class);
}
