package org.yccheok.jstock.news;

import org.yccheok.jstock.engine.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class NewsServerFactory {
    public static List<NewsServer> getNewsServers(Country country) {
        return map.get(country);
    }

    public static void updateNewsSource(Country country, final NewsSource newsSource) {
        final Set<Class<? extends NewsServer>> classes = newsSourceMap.get(newsSource);

        if (classes == null) {
            return;
        }

        final List<NewsServer> newsServers = map.get(country);

        if (null == newsServers) {
            return;
        }

        // It isn't possible to perform sorting directly on CopyOnWriteArrayList.
        // at java.util.concurrent.CopyOnWriteArrayList$COWIterator.set(CopyOnWriteArrayList.java:1049)
        final List<NewsServer> tmp = new ArrayList<>(newsServers);

        Collections.sort(tmp, new Comparator<NewsServer>() {

            @Override
            public int compare(NewsServer o1, NewsServer o2) {
                final boolean result1 = classes.contains(o1.getClass());
                final boolean result2 = classes.contains(o2.getClass());
                if (result1 && !result2) {
                    return -1;
                }

                if (!result1 && result2) {
                    return 1;
                }

                return 0;
            }
        });

        // Copy back to CopyOnWriteArrayList.
        for (int i = 0, ei = tmp.size(); i < ei; i++) {
            NewsServer f = tmp.get(i);
            if (f != newsServers.get(i)) {
                newsServers.set(i, f);
            }
        }
    }

    private static final Map<NewsSource, Set<Class<? extends NewsServer>>> newsSourceMap = new EnumMap<>(NewsSource.class);

    private static final Map<Country, List<NewsServer>> map = new EnumMap<>(Country.class);
    
    static {
        final Set<Class<? extends NewsServer>> googleSet = new HashSet<>();
        final Set<Class<? extends NewsServer>> googleSearchSet = new HashSet<>();
        final Set<Class<? extends NewsServer>> yahooSet = new HashSet<>();

        final List<NewsServer> argentinaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> australiaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> austriaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> belgiumList = new CopyOnWriteArrayList<>();
        final List<NewsServer> brazilList = new CopyOnWriteArrayList<>();
        final List<NewsServer> canadaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> chinaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> denmarkList = new CopyOnWriteArrayList<>();
        final List<NewsServer> finlandList = new CopyOnWriteArrayList<>();
        final List<NewsServer> franceList = new CopyOnWriteArrayList<>();
        final List<NewsServer> germanyList = new CopyOnWriteArrayList<>();
        final List<NewsServer> hongkongList = new CopyOnWriteArrayList<>();
        final List<NewsServer> indiaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> indonesiaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> israelList = new CopyOnWriteArrayList<>();
        final List<NewsServer> italyList = new CopyOnWriteArrayList<>();
        final List<NewsServer> japanList = new CopyOnWriteArrayList<>();
        final List<NewsServer> koreaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> malaysiaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> netherlandsList = new CopyOnWriteArrayList<>();
        final List<NewsServer> newZealandList = new CopyOnWriteArrayList<>();
        final List<NewsServer> norwayList = new CopyOnWriteArrayList<>();
        final List<NewsServer> polandList = new CopyOnWriteArrayList<>();
        final List<NewsServer> portugalList = new CopyOnWriteArrayList<>();
        final List<NewsServer> russiaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> saudiArabiaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> singaporeList = new CopyOnWriteArrayList<>();
        final List<NewsServer> southAfricaList = new CopyOnWriteArrayList<>();
        final List<NewsServer> spainList = new CopyOnWriteArrayList<>();
        final List<NewsServer> swedenList = new CopyOnWriteArrayList<>();
        final List<NewsServer> switzerlandList = new CopyOnWriteArrayList<>();
        final List<NewsServer> taiwanList = new CopyOnWriteArrayList<>();
        final List<NewsServer> thailandList = new CopyOnWriteArrayList<>();
        final List<NewsServer> turkeyList = new CopyOnWriteArrayList<>();
        final List<NewsServer> unitedKingdomList = new CopyOnWriteArrayList<>();
        final List<NewsServer> unitedStateList = new CopyOnWriteArrayList<>();

        googleSet.add(GoogleFinanceNewsServer.class);
        googleSearchSet.add(GoogleSearchNewsServer.class);
        yahooSet.add(YahooFinanceNewsServer.class);

        argentinaList.add(new YahooFinanceNewsServer());
        argentinaList.add(new GoogleFinanceNewsServer());
        australiaList.add(new YahooFinanceNewsServer());
        australiaList.add(new GoogleFinanceNewsServer());
        austriaList.add(new YahooFinanceNewsServer());
        austriaList.add(new GoogleFinanceNewsServer());
        belgiumList.add(new YahooFinanceNewsServer());
        belgiumList.add(new GoogleFinanceNewsServer());
        brazilList.add(new YahooFinanceNewsServer());
        brazilList.add(new GoogleFinanceNewsServer());
        canadaList.add(new YahooFinanceNewsServer());
        canadaList.add(new GoogleFinanceNewsServer());
        chinaList.add(new YahooFinanceNewsServer());
        chinaList.add(new GoogleFinanceNewsServer());
        denmarkList.add(new YahooFinanceNewsServer());
        denmarkList.add(new GoogleFinanceNewsServer());
        finlandList.add(new GoogleFinanceNewsServer());
        finlandList.add(new YahooFinanceNewsServer());
        franceList.add(new YahooFinanceNewsServer());
        franceList.add(new GoogleFinanceNewsServer());
        germanyList.add(new YahooFinanceNewsServer());
        germanyList.add(new GoogleSearchNewsServer());
        hongkongList.add(new YahooFinanceNewsServer());
        hongkongList.add(new GoogleFinanceNewsServer());
        indiaList.add(new YahooFinanceNewsServer());
        indiaList.add(new GoogleFinanceNewsServer());
        indonesiaList.add(new YahooFinanceNewsServer());
        indonesiaList.add(new GoogleFinanceNewsServer());
        israelList.add(new YahooFinanceNewsServer());
        israelList.add(new GoogleFinanceNewsServer());
        italyList.add(new YahooFinanceNewsServer());
        italyList.add(new GoogleFinanceNewsServer());
        japanList.add(new GoogleFinanceNewsServer());
        koreaList.add(new YahooFinanceNewsServer());
        koreaList.add(new GoogleFinanceNewsServer());
        malaysiaList.add(new YahooFinanceNewsServer());
        malaysiaList.add(new GoogleFinanceNewsServer());
        netherlandsList.add(new YahooFinanceNewsServer());
        netherlandsList.add(new GoogleFinanceNewsServer());
        newZealandList.add(new YahooFinanceNewsServer());
        newZealandList.add(new GoogleFinanceNewsServer());
        norwayList.add(new YahooFinanceNewsServer());
        norwayList.add(new GoogleSearchNewsServer());
        polandList.add(new GoogleFinanceNewsServer());
        portugalList.add(new YahooFinanceNewsServer());
        portugalList.add(new GoogleFinanceNewsServer());
        russiaList.add(new GoogleFinanceNewsServer());
        russiaList.add(new YahooFinanceNewsServer());
        saudiArabiaList.add(new GoogleFinanceNewsServer());
        singaporeList.add(new YahooFinanceNewsServer());
        singaporeList.add(new GoogleFinanceNewsServer());
        southAfricaList.add(new GoogleFinanceNewsServer());
        spainList.add(new GoogleFinanceNewsServer());
        swedenList.add(new YahooFinanceNewsServer());
        swedenList.add(new GoogleFinanceNewsServer());
        switzerlandList.add(new YahooFinanceNewsServer());
        switzerlandList.add(new GoogleSearchNewsServer());
        taiwanList.add(new YahooFinanceNewsServer());
        taiwanList.add(new GoogleFinanceNewsServer());
        thailandList.add(new GoogleFinanceNewsServer());
        turkeyList.add(new GoogleFinanceNewsServer());
        unitedKingdomList.add(new YahooFinanceNewsServer());
        unitedKingdomList.add(new GoogleFinanceNewsServer());
        unitedStateList.add(new YahooFinanceNewsServer());
        unitedStateList.add(new GoogleFinanceNewsServer());

        newsSourceMap.put(NewsSource.Google, googleSet);
        newsSourceMap.put(NewsSource.GoogleSearch, googleSearchSet);
        newsSourceMap.put(NewsSource.Yahoo, yahooSet);

        map.put(Country.Argentina, argentinaList);
        map.put(Country.Australia, australiaList);
        map.put(Country.Austria, austriaList);
        map.put(Country.Belgium, belgiumList);
        map.put(Country.Brazil, brazilList);
        map.put(Country.Canada, canadaList);
        map.put(Country.China, chinaList);
        map.put(Country.Denmark, denmarkList);
        map.put(Country.Finland, finlandList);
        map.put(Country.France, franceList);
        map.put(Country.Germany, germanyList);
        map.put(Country.HongKong, hongkongList);
        map.put(Country.India, indiaList);
        map.put(Country.Indonesia, indonesiaList);
        map.put(Country.Israel, israelList);
        map.put(Country.Italy, italyList);
        map.put(Country.Japan, japanList);
        map.put(Country.Korea, koreaList);
        map.put(Country.Malaysia, malaysiaList);
        map.put(Country.Netherlands, netherlandsList);
        map.put(Country.NewZealand, newZealandList);
        map.put(Country.Norway, norwayList);
        map.put(Country.Poland, polandList);
        map.put(Country.Portugal, portugalList);
        map.put(Country.Russia, russiaList);
        map.put(Country.SaudiArabia, saudiArabiaList);
        map.put(Country.Singapore, singaporeList);
        map.put(Country.SouthAfrica, southAfricaList);
        map.put(Country.Spain, spainList);
        map.put(Country.Sweden, swedenList);
        map.put(Country.Switzerland, switzerlandList);
        map.put(Country.Taiwan, taiwanList);
        map.put(Country.Thailand, thailandList);
        map.put(Country.Turkey, turkeyList);
        map.put(Country.UnitedKingdom, unitedKingdomList);
        map.put(Country.UnitedState, unitedStateList);
    }
}
