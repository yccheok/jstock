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

/**
 * This class is an utility for network related activities.
 * @author yccheok
 */
public class Utils {
    public enum Type {
        X_MARKET_SERVERS_TXT,
        X_STOCK_SERVERS_TXT,
        CIMB_MARKET_SERVERS_TXT,
        CIMB_STOCK_SERVERS_TXT,
        CIMB_HISTORY_SERVERS_TXT,
        NEWS_INFORMATION_TXT,
        CHAT_SERVER_TXT,
        NTP_SERVER_TXT,
        VERSION_INFORMATION_TXT,
        CURRENCY_CODE_TXT,
        MODULE_INDICATOR_DOWNLOAD_MANAGER_XML,
        ALERT_INDICATOR_DOWNLOAD_MANAGER_XML,
        HELP_STOCK_DATABASE_HTML,
        PRIVACY_HTML,
        HELP_HTML,
        MA_INDICATOR_HTML,
    }

    private Utils() {
    }

    /**
     * URL based on current locale language.
     * @param type The URL type
     * @return URL based on current locale language
     */
    public static String getURL(Type type) {
        if (Locale.getDefault().getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage())) {
            return zh_map.get(type);
        }
        return map.get(type);
    }

    /**
     * @return the JStock static server URL
     */
    public static String getJStockStaticServer() {
        return JSTOCK_STATIC_SERVER;
    }
    
    private static final Map<Type, String> map = new EnumMap<Type, String>(Type.class);
    private static final Map<Type, String> zh_map = new EnumMap<Type, String>(Type.class);
    private static final String JSTOCK_STATIC_SERVER = "http://jstock-static.appspot.com/";

    static {
        map.put(Type.X_MARKET_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/x_market_servers.txt");
        map.put(Type.X_STOCK_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/x_stock_servers.txt");
        map.put(Type.CIMB_MARKET_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/cimb_market_servers.txt");
        map.put(Type.CIMB_STOCK_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/cimb_stock_servers.txt");
        map.put(Type.CIMB_HISTORY_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/cimb_history_servers.txt");
        map.put(Type.CHAT_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/chat_server.txt");
        map.put(Type.NTP_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/ntp_server.txt");
        map.put(Type.NEWS_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "news_information/index.txt");
        map.put(Type.VERSION_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "version_information/index.txt");
        map.put(Type.CURRENCY_CODE_TXT, JSTOCK_STATIC_SERVER + "currency_information/currency_code.txt");
        map.put(Type.MODULE_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "module_indicators/indicator_download_manager.xml");
        map.put(Type.ALERT_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "alert_indicators/indicator_download_manager.xml");
        map.put(Type.HELP_STOCK_DATABASE_HTML, "http://jstock.sourceforge.net/help_stock_database.html?utm_source=jstock&utm_medium=database_dialog");
        map.put(Type.PRIVACY_HTML, "http://jstock.sourceforge.net/privacy.html");
        map.put(Type.HELP_HTML, "http://jstock.sourceforge.net/help.html?utm_source=jstock&utm_medium=help_menu");
        map.put(Type.MA_INDICATOR_HTML, "http://jstock.sourceforge.net/ma_indicator.html?utm_source=jstock&utm_medium=chart_dialog");

        zh_map.put(Type.X_MARKET_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/x_market_servers.txt");
        zh_map.put(Type.X_STOCK_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/x_stock_servers.txt");
        zh_map.put(Type.CIMB_MARKET_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/cimb_market_servers.txt");
        zh_map.put(Type.CIMB_STOCK_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/cimb_stock_servers.txt");
        zh_map.put(Type.CIMB_HISTORY_SERVERS_TXT, JSTOCK_STATIC_SERVER + "servers_information/cimb_history_servers.txt");
        zh_map.put(Type.CHAT_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/chat_server.txt");
        zh_map.put(Type.NTP_SERVER_TXT, JSTOCK_STATIC_SERVER + "servers_information/ntp_server.txt");
        zh_map.put(Type.NEWS_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "news_information/zh/index.txt");
        zh_map.put(Type.VERSION_INFORMATION_TXT, JSTOCK_STATIC_SERVER + "version_information/zh/index.txt");
        zh_map.put(Type.CURRENCY_CODE_TXT, JSTOCK_STATIC_SERVER + "currency_information/currency_code.txt");
        zh_map.put(Type.MODULE_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "module_indicators/zh/indicator_download_manager.xml");
        zh_map.put(Type.ALERT_INDICATOR_DOWNLOAD_MANAGER_XML, JSTOCK_STATIC_SERVER + "alert_indicators/zh/indicator_download_manager.xml");
        zh_map.put(Type.HELP_STOCK_DATABASE_HTML, "http://jstock.sourceforge.net/zh/help_stock_database.html?utm_source=jstock&utm_medium=database_dialog");
        zh_map.put(Type.PRIVACY_HTML, "http://jstock.sourceforge.net/zh/privacy.html");
        zh_map.put(Type.HELP_HTML, "http://jstock.sourceforge.net/zh/help.html?utm_source=jstock&utm_medium=help_menu");
        zh_map.put(Type.MA_INDICATOR_HTML, "http://jstock.sourceforge.net/zh/ma_indicator.html?utm_source=jstock&utm_medium=chart_dialog");

        assert(map.size() == Type.values().length);
        assert(zh_map.size() == Type.values().length);
    }
}
