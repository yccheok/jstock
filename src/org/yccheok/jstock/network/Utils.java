/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.network;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import org.yccheok.jstock.engine.Country;

/**
 * This class is an utility for network related activities.
 * @author yccheok
 */
public class Utils {
    public enum Type {
        NEWS_INFORMATION_TXT,
        CHAT_SERVER_TXT,
        NTP_SERVER_TXT,
        VERSION_INFORMATION_TXT,
        MODULE_INDICATOR_DOWNLOAD_MANAGER_XML,
        ALERT_INDICATOR_DOWNLOAD_MANAGER_XML,
        HELP_STOCK_DATABASE_HTML,
        PRIVACY_HTML,
        HELP_HTML,
        MA_INDICATOR_HTML,
        GET_TIME,
        GET_IP,
        OPTIONS,
        DONATE_HTML,
        CONTRIBUTE_HTML,
        HELP_KEYBOARD_SHORTCUTS_HTML,
        ANDROID_HTML,
        WENOTE_HTML,
        STOCK_INFO_DATABASE_META,
        GOOGLE_CODE_DATABASE_META,
        GOOGLE_CODE_DATABASE,
        MISSING_STOCK_CODE_SERVICE,
        YAHOO_FINANCE_API,
        YAHOO_FINANCE_CHART_API_V8,
        IEX_API
    }

    private Utils() {
    }

    /**
     * URL based on current locale language.
     * @param type The URL type
     * @return URL based on current locale language
     */
    public static String getURL(Type type) {
        final Locale defaultLocale = Locale.getDefault();
        if (org.yccheok.jstock.gui.Utils.isSimplifiedChinese(defaultLocale)) {
            return zh_map.get(type);
        } else if (org.yccheok.jstock.gui.Utils.isTraditionalChinese(defaultLocale)) {
            return zh_Hant_map.get(type);
        }
        return map.get(type);
    }

    public static String getURL(Type type, Country country) {
        String url = getURL(type);
        return url.replace(COUNTRY_TEMPLATE, country.name().toLowerCase());
    }
    
    /**
     * @return the JStock static server URL
     */
    public static String getJStockStaticServer() {
        return JSTOCK_STATIC_SERVER;
    }
    
    private static final Map<Type, String> map = new EnumMap<>(Type.class);
    private static final Map<Type, String> zh_map = new EnumMap<>(Type.class);
    private static final Map<Type, String> zh_Hant_map = new EnumMap<>(Type.class);

    //private static final String JSTOCK_STATIC_SERVER = "http://jstock-static-hrd.appspot.com/";
    private static final String JSTOCK_STATIC_SERVER = "https://raw.githubusercontent.com/yccheok/jstock/master/appengine/jstock-static/war/";
    private static final String JSTOCK_WEBAPP_SERVER = "https://jstock-webapp.appspot.com/";

    private static final String COUNTRY_TEMPLATE = "{country}";
    
    static {
        map.put(Type.CHAT_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/chat_server.txt");
        map.put(Type.NTP_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/ntp_server.txt");
        map.put(Type.NEWS_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "news_information/index.txt");
        map.put(Type.VERSION_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "version_information/index.txt");
        map.put(Type.MODULE_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "module_indicators/indicator_download_manager.xml");
        map.put(Type.ALERT_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "alert_indicators/indicator_download_manager.xml");
        map.put(Type.HELP_STOCK_DATABASE_HTML, "http://www.jstock.org/help_real_time_info.html?utm_source=jstock&utm_medium=database_dialog#new-database");
        map.put(Type.PRIVACY_HTML, "http://jstock.org/privacy.html");
        map.put(Type.HELP_HTML, "http://jstock.org/help.html?utm_source=jstock&utm_medium=help_menu");
        map.put(Type.MA_INDICATOR_HTML, "http://jstock.org/ma_indicator.html?utm_source=jstock&utm_medium=chart_dialog");
        map.put(Type.GET_TIME, JSTOCK_WEBAPP_SERVER + "get-time.py");
        map.put(Type.GET_IP, JSTOCK_WEBAPP_SERVER + "get-ip.py");
        map.put(Type.OPTIONS, JSTOCK_STATIC_SERVER + "options_information/options.txt");
        map.put(Type.DONATE_HTML,"http://jstock.org/donation.html?utm_source=jstock&utm_medium=help_menu");
        // http://webmasters.stackexchange.com/questions/35413/unable-to-use-anchor-hash-tag-if-using-google-analytics-utm
        map.put(Type.CONTRIBUTE_HTML, "http://jstock.org/help_faq.html?utm_source=jstock&utm_medium=help_menu#contribution");
        map.put(Type.HELP_KEYBOARD_SHORTCUTS_HTML, "http://jstock.org/help_faq.html?utm_source=jstock&utm_medium=help_menu#keyboard-shortcuts");
        map.put(Type.ANDROID_HTML, "http://goo.gl/VAm6Mx");
        map.put(Type.WENOTE_HTML, "http://bit.ly/2JcBKKv");
        map.put(Type.STOCK_INFO_DATABASE_META, JSTOCK_STATIC_SERVER + "stocks_information/stock-info-database-meta.json");
        map.put(Type.GOOGLE_CODE_DATABASE_META, JSTOCK_STATIC_SERVER + "stocks_information/google-code-database-meta.json");
        map.put(Type.GOOGLE_CODE_DATABASE, JSTOCK_STATIC_SERVER + "stocks_information/" + COUNTRY_TEMPLATE + "/google-code-database.zip");
        map.put(Type.MISSING_STOCK_CODE_SERVICE, JSTOCK_WEBAPP_SERVER + "missing-stock-code/");
        map.put(Type.YAHOO_FINANCE_API, "https://mobile-query.finance.yahoo.com/v6/finance/");
        map.put(Type.YAHOO_FINANCE_CHART_API_V8, "https://query1.finance.yahoo.com/v8/finance/chart/");
        map.put(Type.IEX_API, "https://iex.jstock.co/stable/");

        zh_map.put(Type.CHAT_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/chat_server.txt");
        zh_map.put(Type.NTP_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/ntp_server.txt");
        zh_map.put(Type.NEWS_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "news_information/zh/index.txt");
        zh_map.put(Type.VERSION_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "version_information/zh/index.txt");
        zh_map.put(Type.MODULE_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "module_indicators/zh/indicator_download_manager.xml");
        zh_map.put(Type.ALERT_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "alert_indicators/zh/indicator_download_manager.xml");
        zh_map.put(Type.HELP_STOCK_DATABASE_HTML, "http://jstock.org/zh/help_stock_database.html?utm_source=jstock&utm_medium=database_dialog");
        zh_map.put(Type.PRIVACY_HTML, "http://jstock.org/zh/privacy.html");
        zh_map.put(Type.HELP_HTML, "http://jstock.org/zh/help.html?utm_source=jstock&utm_medium=help_menu");
        zh_map.put(Type.MA_INDICATOR_HTML, "http://jstock.org/zh/ma_indicator.html?utm_source=jstock&utm_medium=chart_dialog");
        zh_map.put(Type.GET_TIME, JSTOCK_WEBAPP_SERVER + "get-time.py");
        zh_map.put(Type.GET_IP, JSTOCK_WEBAPP_SERVER + "get-ip.py");
        zh_map.put(Type.OPTIONS, JSTOCK_STATIC_SERVER + "options_information/options.txt");
        zh_map.put(Type.DONATE_HTML,"http://jstock.org/zh/donation.html?utm_source=jstock&utm_medium=help_menu");
        // http://webmasters.stackexchange.com/questions/35413/unable-to-use-anchor-hash-tag-if-using-google-analytics-utm
        zh_map.put(Type.CONTRIBUTE_HTML, "http://jstock.org/zh/help_faq.html?utm_source=jstock&utm_medium=help_menu#contribution");
        zh_map.put(Type.HELP_KEYBOARD_SHORTCUTS_HTML, "http://jstock.org/zh/help_faq.html?utm_source=jstock&utm_medium=help_menu#keyboard-shortcuts");
        zh_map.put(Type.ANDROID_HTML, "http://goo.gl/VAm6Mx");
        zh_map.put(Type.WENOTE_HTML, "http://bit.ly/2JcBKKv");
        zh_map.put(Type.STOCK_INFO_DATABASE_META, JSTOCK_STATIC_SERVER + "stocks_information/stock-info-database-meta.json");
        zh_map.put(Type.GOOGLE_CODE_DATABASE_META, JSTOCK_STATIC_SERVER + "stocks_information/google-code-database-meta.json");
        zh_map.put(Type.GOOGLE_CODE_DATABASE, JSTOCK_STATIC_SERVER + "stocks_information/" + COUNTRY_TEMPLATE + "/google-code-database.zip");
        zh_map.put(Type.MISSING_STOCK_CODE_SERVICE, JSTOCK_WEBAPP_SERVER + "missing-stock-code/");
        zh_map.put(Type.YAHOO_FINANCE_API, "https://mobile-query.finance.yahoo.com/v6/finance/");
        zh_map.put(Type.YAHOO_FINANCE_CHART_API_V8, "https://query1.finance.yahoo.com/v8/finance/chart/");
        zh_map.put(Type.IEX_API, "https://iex.jstock.co/stable/");

        zh_Hant_map.put(Type.CHAT_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/chat_server.txt");
        zh_Hant_map.put(Type.NTP_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/ntp_server.txt");
        zh_Hant_map.put(Type.NEWS_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "news_information/zh/index.txt");
        zh_Hant_map.put(Type.VERSION_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "version_information/zh/index.txt");
        zh_Hant_map.put(Type.MODULE_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "module_indicators/zh/indicator_download_manager.xml");
        zh_Hant_map.put(Type.ALERT_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "alert_indicators/zh/indicator_download_manager.xml");
        zh_Hant_map.put(Type.HELP_STOCK_DATABASE_HTML, "http://jstock.org/zh/help_stock_database.html?utm_source=jstock&utm_medium=database_dialog");
        zh_Hant_map.put(Type.PRIVACY_HTML, "http://jstock.org/zh/privacy.html");
        zh_Hant_map.put(Type.HELP_HTML, "http://jstock.org/zh/help.html?utm_source=jstock&utm_medium=help_menu");
        zh_Hant_map.put(Type.MA_INDICATOR_HTML, "http://jstock.org/zh/ma_indicator.html?utm_source=jstock&utm_medium=chart_dialog");
        zh_Hant_map.put(Type.GET_TIME, JSTOCK_WEBAPP_SERVER + "get-time.py");
        zh_Hant_map.put(Type.GET_IP, JSTOCK_WEBAPP_SERVER + "get-ip.py");
        zh_Hant_map.put(Type.OPTIONS, JSTOCK_STATIC_SERVER + "options_information/options.txt");
        zh_Hant_map.put(Type.DONATE_HTML,"http://jstock.org/zh/donation.html?utm_source=jstock&utm_medium=help_menu");
        // http://webmasters.stackexchange.com/questions/35413/unable-to-use-anchor-hash-tag-if-using-google-analytics-utm
        zh_Hant_map.put(Type.CONTRIBUTE_HTML, "http://jstock.org/zh/help_faq.html?utm_source=jstock&utm_medium=help_menu#contribution");
        zh_Hant_map.put(Type.HELP_KEYBOARD_SHORTCUTS_HTML, "http://jstock.org/zh/help_faq.html?utm_source=jstock&utm_medium=help_menu#keyboard-shortcuts");
        zh_Hant_map.put(Type.ANDROID_HTML, "http://goo.gl/VAm6Mx");
        zh_Hant_map.put(Type.WENOTE_HTML, "http://bit.ly/2JcBKKv");
        zh_Hant_map.put(Type.STOCK_INFO_DATABASE_META, JSTOCK_STATIC_SERVER + "stocks_information/stock-info-database-meta.json");
        zh_Hant_map.put(Type.GOOGLE_CODE_DATABASE_META, JSTOCK_STATIC_SERVER + "stocks_information/google-code-database-meta.json");
        zh_Hant_map.put(Type.GOOGLE_CODE_DATABASE, JSTOCK_STATIC_SERVER + "stocks_information/" + COUNTRY_TEMPLATE + "/google-code-database.zip");
        zh_Hant_map.put(Type.MISSING_STOCK_CODE_SERVICE, JSTOCK_WEBAPP_SERVER + "missing-stock-code/");
        zh_Hant_map.put(Type.YAHOO_FINANCE_API, "https://mobile-query.finance.yahoo.com/v6/finance/");
        zh_Hant_map.put(Type.YAHOO_FINANCE_CHART_API_V8, "https://query1.finance.yahoo.com/v8/finance/chart/");
        zh_Hant_map.put(Type.IEX_API, "https://iex.jstock.co/stable/");

        assert(map.size() == Type.values().length);
        assert(zh_map.size() == Type.values().length);
    }
}
