/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.engine.PriceSource;
import org.yccheok.jstock.gui.trading.CreateOrderDlg.AmountQty;
import org.yccheok.jstock.portfolio.BrokingFirm;
import org.yccheok.jstock.portfolio.DecimalPlace;
import org.yccheok.jstock.trading.api.OrderManager.OrderType;

/**
 *
 * @author yccheok
 */
public class JStockOptions {
    public static class DriveWealthBuySellOption {
        public final OrderType orderType;
        public final AmountQty amountQty;
        
        public DriveWealthBuySellOption(OrderType orderType, AmountQty amountQty) {
            this.orderType = orderType;
            this.amountQty = amountQty;
        }
    }

    /**
     * Data structure to carry location, size and state of a JFrame.
     */
    public static class BoundsEx {
        /**
         * Location and size of JFrame.
         */
        public final Rectangle bounds;
        /**
         * JFrame extended state.
         */
        public final int extendedState;

        /**
         * Constructs a data structure to carry location, size and state of a
         * JFrame.
         * 
         * @param bounds location and size of JFrame
         * @param extendedState JFrame extended state
         */
        public BoundsEx(Rectangle bounds, int extendedState) {
            this.bounds = bounds;
            this.extendedState = extendedState;
        }
    }

    /**
     * Option to let user chooses whether a single column stock information will
     * be displayed, or double columns stock information will be displayed,
     * while he is inputing to auto complete combo box.
     */
    public enum StockInputSuggestionListOption {
        OneColumn,
        TwoColumns
    }

    /**
     * GUI option to determine the behavior of history chart's yellow
     * information box.
     */
    public enum YellowInformationBoxOption {
        Stay,
        Follow,
        Hide
    }

    public enum ChartTheme {
        Light,
        Dark
    }
    
    public static final java.awt.Color DEFAULT_NORMAL_TEXT_FOREGROUND_COLOR = Color.BLACK;
    public static final java.awt.Color DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR = new java.awt.Color(50, 150, 0);
    public static final java.awt.Color DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR = new java.awt.Color(200, 0, 50);
    public static final java.awt.Color DEFAULT_FIRST_ROW_BACKGROUND_COLOR = Color.WHITE;
    public static final java.awt.Color DEFAULT_SECOND_ROW_BACKGROUND_COLOR = new java.awt.Color(255, 255, 204);
    public static final java.awt.Color DEFAULT_AUTO_UPDATE_FOREGROUND_COLOR = Color.BLACK;
    public static final java.awt.Color DEFAULT_AUTO_UPDATE_BACKGROUND_COLOR = Color.RED;

    public static final java.awt.Color DEFAULT_FALL_BELOW_ALERT_FOREGROUND_COLOR = Color.YELLOW;
    public static final java.awt.Color DEFAULT_FALL_BELOW_ALERT_BACKGROUND_COLOR = Color.BLACK;
    public static final java.awt.Color DEFAULT_RISE_ABOVE_ALERT_FOREGROUND_COLOR = Color.YELLOW;
    public static final java.awt.Color DEFAULT_RISE_ABOVE_ALERT_BACKGROUND_COLOR = Color.BLACK;

    public static final java.awt.Color DEFAULT_CHAT_SYSTEM_MESSAGE_COLOR = Color.RED;
    public static final java.awt.Color DEFAULT_CHAT_OWN_MESSAGE_COLOR = new java.awt.Color(169, 169, 169);
    public static final java.awt.Color DEFAULT_CHAT_OTHER_MESSAGE_COLOR = new java.awt.Color(100, 149, 237);

    private static final YellowInformationBoxOption DEFAULT_YELLOW_INFORMATION_BOX_OPTION = YellowInformationBoxOption.Follow;
    private static final StockInputSuggestionListOption DEFAULT_STOCK_INPUT_SUGGESTION_LIST_OPTION = StockInputSuggestionListOption.TwoColumns;

    private static final int DEFAULT_HISTORY_DURATION =  10;

    private static final int DEFAULT_RECENT_COUNTRY_SIZE = 5;
    
    /** Creates a new instance of JStockOptions */
    public JStockOptions() {
        this.popupMessage = true;
        this.sendEmail = false;
        this.CCEmail = "";
        this.singleIndicatorAlert = true;
        this.proxyServer = "";
        this.proxyPort = -1;
        // In milliseconds.
        this.scanningSpeed = 1*60*1000;
        this.indicatorScanningSpeed = 30*1000;
        // In seconds.
        this.alertSpeed = 5;
        this.looknFeel = null;
        this.alwaysOnTop = false;
        this.country = Country.UnitedState;
        this.soundEnabled = false;

        this.normalTextForegroundColor = DEFAULT_NORMAL_TEXT_FOREGROUND_COLOR;
        this.higherNumericalValueForegroundColor = DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR;
        this.lowerNumericalValueForegroundColor = DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR;
        
        this.firstRowBackgroundColor = DEFAULT_FIRST_ROW_BACKGROUND_COLOR;
        this.secondRowBackgroundColor = DEFAULT_SECOND_ROW_BACKGROUND_COLOR;
                
        this.enableColorChange = false;
        this.autoUpdateForegroundColor = DEFAULT_AUTO_UPDATE_FOREGROUND_COLOR;
        this.autoUpdateBackgroundColor = DEFAULT_AUTO_UPDATE_BACKGROUND_COLOR;

        this.enableColorAlert = false;
        this.fallBelowAlertForegroundColor = DEFAULT_FALL_BELOW_ALERT_FOREGROUND_COLOR;
        this.fallBelowAlertBackgroundColor = DEFAULT_FALL_BELOW_ALERT_BACKGROUND_COLOR;
        this.riseAboveAlertForegroundColor = DEFAULT_RISE_ABOVE_ALERT_FOREGROUND_COLOR;
        this.riseAboveAlertBackgroundColor = DEFAULT_RISE_ABOVE_ALERT_BACKGROUND_COLOR;

        this.isAutoUpdateNewsEnabled = true;
        this.newsID = "";

        this.historyDuration = DEFAULT_HISTORY_DURATION;

        this.isChatEnabled = false;
        this.chatUsername = "";
        this.chatPassword = "";
        this.isChatFlashNotificationEnabled = true;
        this.isChatSoundNotificationEnabled = true;

        this.chatSystemMessageColor = DEFAULT_CHAT_SYSTEM_MESSAGE_COLOR;
        this.chatOwnMessageColor = DEFAULT_CHAT_OWN_MESSAGE_COLOR;
        this.chatOtherMessageColor = DEFAULT_CHAT_OTHER_MESSAGE_COLOR;

        this.yellowInformationBoxOption = DEFAULT_YELLOW_INFORMATION_BOX_OPTION;
        this.stockInputSuggestionListOption = DEFAULT_STOCK_INPUT_SUGGESTION_LIST_OPTION;

        this.locale = Locale.getDefault();

        this.boundsEx = null;
        
        initRecentCountries();
    }

    private boolean soundEnabled;
    private boolean singleIndicatorAlert;
    private boolean popupMessage;
    private boolean sendEmail;
    
    @Deprecated
    private transient String email;
    @Deprecated
    private transient String emailPassword;
    
    private String CCEmail;
    @Deprecated
    private transient String googleCalendarUsername;
    @Deprecated
    private transient String googleCalendarPassword;
    @Deprecated
    private transient boolean SMSEnabled;
    @Deprecated
    private transient boolean passwordProtectedIndicator;
    @Deprecated
    private transient String indicatorPassword;
    private String proxyServer;
    private int proxyPort;
    
    // In milliseconds.
    private int scanningSpeed;
    // In milliseconds.
    private int indicatorScanningSpeed;
    
    // In seconds.
    private int alertSpeed;
    // Opps! Spelling mistake (Should be lookNFeel). However, due to XML
    // serialization compatibility, we decide not to fix it.
    private String looknFeel;
    private boolean alwaysOnTop;

    private Color normalTextForegroundColor;
    private Color lowerNumericalValueForegroundColor;
    private Color higherNumericalValueForegroundColor;
    private Color firstRowBackgroundColor;
    private Color secondRowBackgroundColor;
    private Color autoUpdateForegroundColor;
    private Color autoUpdateBackgroundColor;
    @Deprecated
    private transient Color alertForegroundColor;
    @Deprecated
    private transient Color alertBackgroundColor;
    // As replacement for alertForegroundColor and alertBackgroundColor.
    private Color fallBelowAlertForegroundColor;
    private Color fallBelowAlertBackgroundColor;
    private Color riseAboveAlertForegroundColor;
    private Color riseAboveAlertBackgroundColor;

    private boolean enableColorChange;
    private boolean enableColorAlert;

    private List<BrokingFirm> brokingFirms = new ArrayList<BrokingFirm>();
    private int selectedBrokingFirmIndex = -1;
    @Deprecated
    private transient boolean isAutoBrokerFeeCalculationEnabled = false;
    
    private double expectedProfitPercentage = 10.0;

    private Country country;

    private boolean isAutoUpdateNewsEnabled;
    @Deprecated
    private transient long newsVersion;
    private String newsID;

    private int historyDuration; /* In years */

    private boolean isChatEnabled = false;
    private String chatUsername = "";
    private String chatPassword = "";
    private boolean isChatSoundNotificationEnabled = true;
    private boolean isChatFlashNotificationEnabled = true;
    private Color chatSystemMessageColor;
    private Color chatOwnMessageColor;
    private Color chatOtherMessageColor;

    // We want to avoid from having too frequent credentials creation during
    // runtime. We will immediately contruct credentials, once we load the
    // JStockOptions from disk.
    private transient Credentials credentials = null;
    private String proxyAuthPassword = "";
    private String proxyAuthUserName = "";
    private boolean isProxyAuthEnabled = false;

    private boolean driveWealthRememberLogin = false;
    private String driveWealthSessionKey = null;
    private Map<String, Long> driveWealthLastTxnTimestamp = new HashMap<>();
    private DriveWealthBuySellOption driveWealthBuyOption = null;
    private DriveWealthBuySellOption driveWealthSellOption = null;
    
    @Deprecated
    private transient boolean rememberGoogleAccountEnabled = false;
    @Deprecated
    private transient String googleUsername = "";
    @Deprecated
    private transient String googlePassword = "";

    // Remember where we save/open the last file.
    private String lastFileIODirectory = System.getProperty("user.home");
    private String lastFileNameExtensionDescription = "CSV Documents (*.csv)";

    @Deprecated
    private transient Map<Country, Class> primaryStockServerFactoryClasses = new EnumMap<Country, Class>(Country.class);
    @Deprecated
    private transient Boolean primaryStockServerFactoryClassesIsValidForMalaysia = true;
    
    // Remember the last view page.
    private int lastSelectedPageIndex = 0;
    private int lastSelectedSellPortfolioChartIndex = 0;
    private int lastSelectedBuyPortfolioChartIndex = 0;
    
    // Use -1 to indicate unlimited SMS per day.
    @Deprecated
    private transient int maxSMSPerDay = -1;

    @Deprecated
    private transient String portfolioName = org.yccheok.jstock.portfolio.Utils.getDefaultPortfolioName();
    private Map<Country, String> portfolioNames = new EnumMap<Country, String>(Country.class);

    @Deprecated
    private transient String watchlistName = org.yccheok.jstock.watchlist.Utils.getDefaultWatchlistName();
    private Map<Country, String> watchlistNames = new EnumMap<Country, String>(Country.class);
    
    private YellowInformationBoxOption yellowInformationBoxOption = YellowInformationBoxOption.Follow;

    private StockInputSuggestionListOption stockInputSuggestionListOption = StockInputSuggestionListOption.OneColumn;
    
    private Locale locale = Locale.getDefault();

    // Possile be null in entire application life cycle.
    private BoundsEx boundsEx;

    private Map<Country, Long> googleCodeDatabaseMeta = new EnumMap<>(Country.class);
    
    private Map<Country, PriceSource> priceSources = new EnumMap<Country, PriceSource>(Country.class);
    
    private Map<Country, String> currencies = new EnumMap<Country, String>(Country.class);

    private Map<Country, Boolean> currencyExchangeEnable = new EnumMap<Country, Boolean>(Country.class);

    private Map<Country, Country> localCurrencyCountries = new EnumMap<Country, Country>(Country.class);

    private Map<Country, Boolean> preferLongNames = new EnumMap<Country, Boolean>(Country.class);
    
    @Deprecated
    private transient Map<Country, Boolean> penceToPoundConversionEnabled = new EnumMap<Country, Boolean>(Country.class);
    
    @Deprecated
    private transient Map<Country, Boolean> fourDecimalPlacesEnabled = new EnumMap<Country, Boolean>(Country.class);
    
    private Map<Country, DecimalPlace> decimalPlaces = new EnumMap<>(Country.class);
    
    // So that in later time we know that, which version of JStock, is used to
    // save this options.xml.
    private int applicationVersionID = Utils.getApplicationVersionID();
    
    private ChartTheme chartTheme = ChartTheme.Light;
    
    private boolean isFeeCalculationEnabled = false;
        
    private boolean useLargeFont = false;
    
    private boolean isDynamicChartVisible = false;
    
    private List<Country> recentCountries = new ArrayList<>();    

    private long iexStockInfoDBMeta = 0;
    
    // Will be used by LoadFromCloudDialog.
    public void insensitiveCopy(JStockOptions jStockOptions) {
        this.singleIndicatorAlert = jStockOptions.singleIndicatorAlert;
        this.popupMessage = jStockOptions.popupMessage;

        //this.sendEmail = jStockOptions.sendEmail;
        //this.email = jStockOptions.email;
        //this.CCEmail = jStockOptions.CCEmail;
        //this.emailPassword = jStockOptions.emailPassword;
        //this.googleCalendarUsername = jStockOptions.googleCalendarUsername;
        //this.googleCalendarPassword = jStockOptions.googleCalendarPassword;

        // Don't store proxy. Home and office proxy environment are most probably different.
        //this.proxyServer = jStockOptions.proxyServer;
        //this.proxyPort = jStockOptions.proxyPort;
        this.scanningSpeed = jStockOptions.scanningSpeed;
        this.indicatorScanningSpeed = jStockOptions.indicatorScanningSpeed;
        this.alertSpeed = jStockOptions.alertSpeed;
        this.looknFeel = jStockOptions.looknFeel;
        this.alwaysOnTop = jStockOptions.alwaysOnTop;

        this.normalTextForegroundColor = jStockOptions.normalTextForegroundColor;
        this.lowerNumericalValueForegroundColor = jStockOptions.lowerNumericalValueForegroundColor;
        this.higherNumericalValueForegroundColor = jStockOptions.higherNumericalValueForegroundColor;
        this.firstRowBackgroundColor = jStockOptions.firstRowBackgroundColor;
        this.secondRowBackgroundColor = jStockOptions.secondRowBackgroundColor;
        this.autoUpdateForegroundColor = jStockOptions.autoUpdateForegroundColor;
        this.autoUpdateBackgroundColor = jStockOptions.autoUpdateBackgroundColor;
        this.fallBelowAlertForegroundColor = jStockOptions.fallBelowAlertForegroundColor;
        this.fallBelowAlertBackgroundColor = jStockOptions.fallBelowAlertBackgroundColor;
        this.riseAboveAlertForegroundColor = jStockOptions.riseAboveAlertForegroundColor;
        this.riseAboveAlertBackgroundColor = jStockOptions.riseAboveAlertBackgroundColor;
        this.enableColorChange = jStockOptions.enableColorChange;
        this.enableColorAlert = jStockOptions.enableColorAlert;

        this.brokingFirms = jStockOptions.brokingFirms;
        this.selectedBrokingFirmIndex = jStockOptions.selectedBrokingFirmIndex;

        this.expectedProfitPercentage = jStockOptions.expectedProfitPercentage;

        this.country = jStockOptions.country;

        this.isAutoUpdateNewsEnabled = jStockOptions.isAutoUpdateNewsEnabled;

        //this.newsID = jStockOptions.newsID;

        this.historyDuration = jStockOptions.historyDuration;

        //this.isChatEnabled = jStockOptions.isChatEnabled;
        //this.chatUsername = jStockOptions.chatUsername;
        //this.chatPassword = jStockOptions.chatPassword;
        this.isChatSoundNotificationEnabled = jStockOptions.isChatSoundNotificationEnabled;
        this.isChatFlashNotificationEnabled = jStockOptions.isChatFlashNotificationEnabled;
        this.chatSystemMessageColor = jStockOptions.chatSystemMessageColor;
        this.chatOwnMessageColor = jStockOptions.chatOwnMessageColor;
        this.chatOtherMessageColor = jStockOptions.chatOtherMessageColor;

        // Don't store proxy. Home and office proxy environment are most probably different.
        //
        // We want to avoid from having too frequent credentials creation during
        // runtime. We will immediately contruct credentials, once we load the
        // JStockOptions from disk.
        //this.credentials = jStockOptions.credentials;
        //this.proxyAuthPassword = jStockOptions.proxyAuthPassword;
        //this.proxyAuthUserName = jStockOptions.proxyAuthUserName;
        //this.isProxyAuthEnabled = jStockOptions.isProxyAuthEnabled;

        //this.driveWealthSessionKey = jStockOptions.driveWealthSessionKey;
        this.driveWealthRememberLogin = jStockOptions.driveWealthRememberLogin;
        this.driveWealthLastTxnTimestamp = jStockOptions.driveWealthLastTxnTimestamp;
        this.driveWealthBuyOption = jStockOptions.driveWealthBuyOption;
        this.driveWealthSellOption = jStockOptions.driveWealthSellOption;

        /* For UK client. */
        //this.penceToPoundConversionEnabled = jStockOptions.penceToPoundConversionEnabled;
        
        this.decimalPlaces = jStockOptions.decimalPlaces;
        
        //this.rememberGoogleAccountEnabled = jStockOptions.rememberGoogleAccountEnabled;
        //this.googleUsername = jStockOptions.googleUsername;
        //this.googlePassword = jStockOptions.googlePassword;

        // Don't save file location. Different machines may have different file location.
        //
        // Remember where we save/open the last file.
        //this.lastFileIODirectory = jStockOptions.lastFileIODirectory;
        //this.lastFileNameExtensionDescription = jStockOptions.lastFileNameExtensionDescription;

        // Remember the last view page.
        this.lastSelectedPageIndex = jStockOptions.lastSelectedPageIndex;
        this.lastSelectedSellPortfolioChartIndex = jStockOptions.lastSelectedSellPortfolioChartIndex;
        this.lastSelectedBuyPortfolioChartIndex = jStockOptions.lastSelectedBuyPortfolioChartIndex;

        this.portfolioNames = new EnumMap<Country, String>(jStockOptions.portfolioNames);

        this.watchlistNames = new EnumMap<Country, String>(jStockOptions.watchlistNames);

        this.yellowInformationBoxOption = jStockOptions.yellowInformationBoxOption;

        this.stockInputSuggestionListOption = jStockOptions.stockInputSuggestionListOption;

        // We won't save locale into cloud. As to have effect on new locale,
        // restarting the entire application is required. We do not want user
        // to restart the application after loading from cloud.
        //this.locale = jStockOptions.locale;

        // We are not interested in transfering MainFrame size from cloud to
        // local.
        //this.boundsEx = jStockOptions.boundsEx;

        //this.googleCodeDatabaseMeta = new EnumMap<>(jStockOptions.googleCodeDatabaseMeta);
        this.priceSources = new EnumMap<Country, PriceSource>(jStockOptions.priceSources);
        this.currencies = new EnumMap<Country, String>(jStockOptions.currencies);
        this.currencyExchangeEnable = new EnumMap<Country, Boolean>(jStockOptions.currencyExchangeEnable);
        this.localCurrencyCountries = new EnumMap<Country, Country>(jStockOptions.localCurrencyCountries);
        this.preferLongNames = new EnumMap<Country, Boolean>(preferLongNames);
        
        //this.penceToPoundConversionEnabled = new EnumMap<Country, Boolean>(jStockOptions.penceToPoundConversionEnabled);
        this.decimalPlaces = new EnumMap<Country, DecimalPlace>(jStockOptions.decimalPlaces);
        
        this.chartTheme = jStockOptions.getChartTheme();
        
        this.isFeeCalculationEnabled = jStockOptions.isFeeCalculationEnabled;
        
        this.useLargeFont = jStockOptions.useLargeFont;
        
        this.isDynamicChartVisible = jStockOptions.isDynamicChartVisible;
        
        //this.iexStockInfoDBMeta = jStockOptions.iexStockInfoDBMeta;
    }

    // User may not trust us to store their password in cloud server. To avoid
    // from getting trouble, we will clone another copy of JStockOptions, which
    // doesn't contain sensitive data.
    public JStockOptions insensitiveClone() {
        final JStockOptions jStockOptions = new JStockOptions();
        jStockOptions.insensitiveCopy(this);
        return jStockOptions;
    }

    private void initRecentCountries() {
        if (this.recentCountries == null) {
            this.recentCountries = new ArrayList<>();            
        }
        
        Country[] countries = {
            Country.UnitedState, 
            Country.Canada,
            Country.Malaysia,
            Country.UnitedKingdom,
            Country.Singapore
        };
    
        if (recentCountries.size() != DEFAULT_RECENT_COUNTRY_SIZE) {
            if (recentCountries.size() < DEFAULT_RECENT_COUNTRY_SIZE) {
                for (Country country : countries) {
                    recentCountries.add(country);
                }
            }
            Set<Country> c = new HashSet<>(recentCountries);
            recentCountries = new ArrayList<>(c);
            int k = recentCountries.size();
            if (k > DEFAULT_RECENT_COUNTRY_SIZE) {
                recentCountries.subList(DEFAULT_RECENT_COUNTRY_SIZE, k).clear();
            }
        }
    }
    
    private Object readResolve() {
        /* For backward compatible */
        if (lastSelectedPageIndex < 0) {
            lastSelectedPageIndex = 0;
        }

        if (lastSelectedSellPortfolioChartIndex < 0) {
            lastSelectedSellPortfolioChartIndex = 0;
        }
        
        if (lastSelectedBuyPortfolioChartIndex < 0) {
            lastSelectedBuyPortfolioChartIndex = 0;
        }
        
        /* For backward compatible */
        if (brokingFirms == null) {
            brokingFirms = new ArrayList<>();
        }
        
        /* For backward compatible */
        if (country == null) {
            country = Country.UnitedState;
        } else {
            List<Country> countries = Utils.getSupportedStockMarketCountries();
            if (!countries.contains(country)) {
                country = Country.UnitedState;
            }
        }

        this.initRecentCountries();
        
        if (historyDuration <= 0) {
            historyDuration = DEFAULT_HISTORY_DURATION;
        }

        if (getChatUsername() == null) {
            setChatUsername("");
        }

        if (getChatPassword() == null) {
            setChatPassword("");
        }

        if (this.getChatSystemMessageColor() == null) {
            this.setChatSystemMessageColor(DEFAULT_CHAT_SYSTEM_MESSAGE_COLOR);
        }

        if (this.getChatOwnMessageColor() == null) {
            this.setChatOwnMessageColor(DEFAULT_CHAT_OWN_MESSAGE_COLOR);
        }

        if (this.getChatOtherMessageColor() == null) {
            this.setChatOtherMessageColor(DEFAULT_CHAT_OTHER_MESSAGE_COLOR);
        }

        if (this.getFallBelowAlertForegroundColor() == null) {
            this.setFallBelowAlertForegroundColor(DEFAULT_FALL_BELOW_ALERT_FOREGROUND_COLOR);
        }

        if (this.getFallBelowAlertBackgroundColor() == null) {
            this.setFallBelowAlertBackgroundColor(DEFAULT_FALL_BELOW_ALERT_BACKGROUND_COLOR);    
        }

        if (this.getRiseAboveAlertForegroundColor() == null) {
            this.setRiseAboveAlertForegroundColor(DEFAULT_RISE_ABOVE_ALERT_FOREGROUND_COLOR);
        }

        if (this.getRiseAboveAlertBackgroundColor() == null) {
            this.setRiseAboveAlertBackgroundColor(DEFAULT_RISE_ABOVE_ALERT_BACKGROUND_COLOR);    
        }

        if (this.proxyAuthUserName == null) {
            this.proxyAuthUserName = "";
        }

        if (this.proxyAuthPassword == null) {
            this.proxyAuthPassword = "";
        }
    
        setCredentials(new NTCredentials(this.proxyAuthUserName, Utils.decrypt(this.proxyAuthPassword), "", ""));

        if (this.getLastFileIODirectory() == null) {
            this.setLastFileIODirectory("");
        }

        if (this.getLastSavedFileNameExtensionDescription() == null) {
            this.setLastFileNameExtensionDescription("CSV Documents (*.csv)");
        }

        if (this.portfolioNames == null) {
            this.portfolioNames = new EnumMap<Country, String>(Country.class);
        }

        if (this.watchlistNames == null) {
            this.watchlistNames = new EnumMap<Country, String>(Country.class);
        }

        if (this.getNewsID() == null) {
            this.setNewsID("");
        }

        if (this.yellowInformationBoxOption == null) {
            this.yellowInformationBoxOption = YellowInformationBoxOption.Follow;
        }

        if (this.stockInputSuggestionListOption == null) {
            this.stockInputSuggestionListOption = StockInputSuggestionListOption.OneColumn;
        }

        if (this.getCCEmail() == null) {
            this.setCCEmail("");
        }

        if (this.getLocale() == null) {
            this.setLocale(Locale.getDefault());
        }
        
        if (this.googleCodeDatabaseMeta == null) {
            this.googleCodeDatabaseMeta = new EnumMap<>(Country.class);
        }
        
        if (this.priceSources == null) {
            this.priceSources = new EnumMap<>(Country.class);
        } else {
            // Still here for xstream backward compatible. Shall be removed
            // after a while.
            if (this.priceSources.get(Country.Malaysia) == PriceSource.KLSEInfo) {
                this.priceSources.put(Country.Malaysia, PriceSource.Yahoo);
            }
        }
        
        if (this.currencies == null) {
            this.currencies = new EnumMap<Country, String>(Country.class);
        }

        if (this.currencyExchangeEnable == null) {
            this.currencyExchangeEnable = new EnumMap<Country, Boolean>(Country.class);
        }

        if (this.localCurrencyCountries == null) {
            this.localCurrencyCountries = new EnumMap<Country, Country>(Country.class);
        }

        if (this.preferLongNames == null) {
            this.preferLongNames = new EnumMap<Country, Boolean>(Country.class);
        }
        
        //if (this.penceToPoundConversionEnabled == null) {
        //    this.penceToPoundConversionEnabled = new EnumMap<Country, Boolean>(Country.class);
        //}
        
        //if (false == this.penceToPoundConversionEnabled.containsKey(Country.UnitedKingdom)) {
        //    this.penceToPoundConversionEnabled.put(Country.UnitedKingdom, true);
        //}
        
        if (this.decimalPlaces == null) {
            this.decimalPlaces = new EnumMap<Country, DecimalPlace>(Country.class);
        }
        
        // Bug caused by change language menu method. We rectify it, after we 
        // fix the change language menu method.
        if (this.locale.getCountry().equals(Locale.FRANCE.getCountry()) && this.locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
            this.locale = Locale.FRANCE;
        }
        
        if (this.chartTheme == null) {
            this.chartTheme = ChartTheme.Light;
        }
        
        if (this.scanningSpeed <= 1000) {
            // In previous version, it is possible for scanningSpeed <= 1000.
            // This is some how wasting CPU and network resource. Let's go green.
            this.scanningSpeed = 10000;
        }
        
        if (this.indicatorScanningSpeed <= 0) {
            // In previous version, it is possible for scanningSpeed <= 1000.
            // This is some how wasting CPU and network resource. Let's go green.
            this.indicatorScanningSpeed = 30*1000;
        }

        if (this.driveWealthLastTxnTimestamp == null) {
            this.driveWealthLastTxnTimestamp = new HashMap<>();
        }

        return this;
    }    
    
    public int getBrokingFirmSize() {        
        return brokingFirms.size();
    }
    
    public List<BrokingFirm> getBrokingFirms() {
        return java.util.Collections.unmodifiableList(brokingFirms);
    }
    
    public BrokingFirm getBrokingFirm(int index) {
        return brokingFirms.get(index);
    }
    
    public int getSelectedBrokingFirmIndex() {
        return selectedBrokingFirmIndex;
    }
    
    public void setSelectedBrokingFirmIndex(int selectedBrokingFirmIndex) {
        this.selectedBrokingFirmIndex = selectedBrokingFirmIndex;
    }
    
    public void clearBrokingFirms() {
        this.brokingFirms.clear();
    }
    
    public void addBrokingFirm(BrokingFirm brokingFirm) {
        this.brokingFirms.add(brokingFirm);
    }
    
    public BrokingFirm getSelectedBrokingFirm() {
        if ((this.selectedBrokingFirmIndex < 0) || (this.selectedBrokingFirmIndex >= this.getBrokingFirmSize()))
            return null;
        
        return this.brokingFirms.get(this.selectedBrokingFirmIndex);
    }
    
    public String getLooknFeel() {
        return looknFeel;
    }
    
    public void setLooknFeel(String looknFeel) {
        this.looknFeel = looknFeel;
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }

    public boolean isSingleIndicatorAlert() {
        return singleIndicatorAlert;
    }
    
    public void setSingleIndicatorAlert(boolean singleIndicatorAlert) {
        this.singleIndicatorAlert = singleIndicatorAlert;
    }
    
    public boolean isPopupMessage() {
        return popupMessage;
    }

    public void setPopupMessage(boolean popupMessage) {
        this.popupMessage = popupMessage;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getProxyServer() {
        return proxyServer;
    }

    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public int getScanningSpeed() {
        return scanningSpeed;
    }

    public int getIndicatorScanningSpeed() {
        return indicatorScanningSpeed;
    }
    
    public int getAlertSpeed() {
        return alertSpeed;
    }

    public void setIndicatorScanningSpeed(int indicatorScanningSpeed) {
        this.indicatorScanningSpeed = indicatorScanningSpeed;
    }
    
    public void setScanningSpeed(int scanningSpeed) {
        this.scanningSpeed = scanningSpeed;
    }
    
    public void setAlertSpeed(int alertSpeed) {
        this.alertSpeed = alertSpeed;
    }    

    public Color getNormalTextForegroundColor() {
        return normalTextForegroundColor;
    }

    public void setNormalTextForegroundColor(Color normalTextForegroundColor) {
        this.normalTextForegroundColor = normalTextForegroundColor;
    }

    public Color getLowerNumericalValueForegroundColor() {
        return lowerNumericalValueForegroundColor;
    }

    public void setLowerNumericalValueForegroundColor(Color lowerNumericalValueForegroundColor) {
        this.lowerNumericalValueForegroundColor = lowerNumericalValueForegroundColor;
    }

    public Color getHigherNumericalValueForegroundColor() {
        return higherNumericalValueForegroundColor;
    }

    public void setHigherNumericalValueForegroundColor(Color higherNumericalValueForegroundColor) {
        this.higherNumericalValueForegroundColor = higherNumericalValueForegroundColor;
    }

    public Color getFirstRowBackgroundColor() {
        return firstRowBackgroundColor;
    }

    public void setFirstRowBackgroundColor(Color firstRowBackgroundColor) {
        this.firstRowBackgroundColor = firstRowBackgroundColor;
    }

    public Color getSecondRowBackgroundColor() {
        return secondRowBackgroundColor;
    }

    public void setSecondRowBackgroundColor(Color secondRowBackgroundColor) {
        this.secondRowBackgroundColor = secondRowBackgroundColor;
    }

    public Color getAutoUpdateForegroundColor() {
        return autoUpdateForegroundColor;
    }

    public void setAutoUpdateForegroundColor(Color autoUpdateForegroundColor) {
        this.autoUpdateForegroundColor = autoUpdateForegroundColor;
    }

    public Color getAutoUpdateBackgroundColor() {
        return autoUpdateBackgroundColor;
    }

    public void setAutoUpdateBackgroundColor(Color autoUpdateBackgroundColor) {
        this.autoUpdateBackgroundColor = autoUpdateBackgroundColor;
    }

    public boolean isEnableColorChange() {
        return enableColorChange;
    }

    public void setEnableColorChange(boolean enableColorChange) {
        this.enableColorChange = enableColorChange;
    }
    
    public boolean isAutoUpdateNewsEnabled()
    {
        return this.isAutoUpdateNewsEnabled;
    }

    public boolean isChatEnabled() {
        return this.isChatEnabled;
    }

    public boolean isChatSoundNotificationEnabled() {
        return this.isChatSoundNotificationEnabled;
    }

    public boolean isChatFlashNotificationEnabled() {
        return this.isChatFlashNotificationEnabled;
    }
    
    public void setAutoUpdateNewsEnabled(boolean isAutoUpdateNewsEnabled)
    {
        this.isAutoUpdateNewsEnabled = isAutoUpdateNewsEnabled;
    }

    public void setChatEnabled(boolean isChatEnabled) {
        this.isChatEnabled = isChatEnabled;
    }

    public void setChatFlashNotificationEnabled(boolean isChatFlashNotificationEnabled) {
        this.isChatFlashNotificationEnabled = isChatFlashNotificationEnabled;
    }

    public void setChatSoundNotificationEnabled(boolean isChatSoundNotificationEnabled) {
        this.isChatSoundNotificationEnabled = isChatSoundNotificationEnabled;
    }

    public double getExpectedProfitPercentage() {
        return expectedProfitPercentage;
    }
    
    public void setExpectedProfitPercentage(double expectedProfitPercentage) {
        this.expectedProfitPercentage = expectedProfitPercentage;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @return the historyDuration
     */
    public int getHistoryDuration() {
        return historyDuration;
    }

    /**
     * @param historyDuration the historyDuration to set
     */
    public void setHistoryDuration(int historyDuration) {
        this.historyDuration = historyDuration;
    }

    /**
     * @return the chatUsername
     */
    public String getChatUsername() {
        return chatUsername;
    }

    /**
     * @param chatUsername the chatUsername to set
     */
    public void setChatUsername(String chatUsername) {
        this.chatUsername = chatUsername;
    }

    /**
     * @return the chatSystemMessageColor
     */
    public Color getChatSystemMessageColor() {
        return chatSystemMessageColor;
    }

    /**
     * @param chatSystemMessageColor the chatSystemMessageColor to set
     */
    public void setChatSystemMessageColor(Color chatSystemMessageColor) {
        this.chatSystemMessageColor = chatSystemMessageColor;
    }

    /**
     * @return the chatOwnMessageColor
     */
    public Color getChatOwnMessageColor() {
        return chatOwnMessageColor;
    }

    /**
     * @param chatOwnMessageColor the chatOwnMessageColor to set
     */
    public void setChatOwnMessageColor(Color chatOwnMessageColor) {
        this.chatOwnMessageColor = chatOwnMessageColor;
    }

    /**
     * @return the chatOtherMessageColor
     */
    public Color getChatOtherMessageColor() {
        return chatOtherMessageColor;
    }

    /**
     * @param chatOtherMessageColor the chatOtherMessageColor to set
     */
    public void setChatOtherMessageColor(Color chatOtherMessageColor) {
        this.chatOtherMessageColor = chatOtherMessageColor;
    }

    /**
     * @return the chatPassword
     */
    public String getChatPassword() {
        return chatPassword;
    }

    /**
     * @param chatPassword the chatPassword to set
     */
    public void setChatPassword(String chatPassword) {
        this.chatPassword = chatPassword;
    }

    /**
     * @return the enableColorAlert
     */
    public boolean isEnableColorAlert() {
        return enableColorAlert;
    }

    /**
     * @param enableColorAlert the enableColorAlert to set
     */
    public void setEnableColorAlert(boolean enableColorAlert) {
        this.enableColorAlert = enableColorAlert;
    }

    public String getDriveWealthSessionKey() {
        return this.driveWealthSessionKey;
    }
    
    public void setDriveWealthSessionKey(String driveWealthSessionKey) {
        this.driveWealthSessionKey = driveWealthSessionKey;
    }
    
    public boolean isDriveWealthRememberLogin () {
        return this.driveWealthRememberLogin;
    }
    
    public void setDriveWealthRememberLogin (boolean driveWealthRememberLogin) {
        this.driveWealthRememberLogin = driveWealthRememberLogin;
    }

    public Long getDriveWealthLastTxnTimestamp(String accountID) {
        return this.driveWealthLastTxnTimestamp.get(accountID);
    }

    public void setDriveWealthLastTxnTimestamp (String accountID, long timestamp) {
        this.driveWealthLastTxnTimestamp.put(accountID, timestamp);
    }

    public DriveWealthBuySellOption getDriveWealthBuyOption () {
        return this.driveWealthBuyOption;
    } 

    public void setDriveWealthBuyOption (OrderType ordType, AmountQty amtQty) {
        this.driveWealthBuyOption = new DriveWealthBuySellOption(ordType, amtQty);
    } 

    public DriveWealthBuySellOption getDriveWealthSellOption () {
        return this.driveWealthSellOption;        
    } 

    public void setDriveWealthSellOption (OrderType ordType, AmountQty amtQty) {
        this.driveWealthSellOption = new DriveWealthBuySellOption(ordType, amtQty);
    } 

    /**
     * @return the proxyAuthPassword
     */
    public String getProxyAuthPassword() {
        return proxyAuthPassword;
    }

    /**
     * @param proxyAuthPassword the proxyAuthPassword to set
     */
    public void setProxyAuthPassword(String proxyAuthPassword) {
        this.proxyAuthPassword = proxyAuthPassword;
        // Update credentials as well.
        setCredentials(new NTCredentials(this.proxyAuthUserName, Utils.decrypt(this.proxyAuthPassword), "", ""));
    }

    /**
     * @return the proxyAuthUserName
     */
    public String getProxyAuthUserName() {
        return proxyAuthUserName;
    }

    /**
     * @param proxyAuthUserName the proxyAuthUserName to set
     */
    public void setProxyAuthUserName(String proxyAuthUserName) {
        this.proxyAuthUserName = proxyAuthUserName;
        // Update credentials as well.
        setCredentials(new NTCredentials(this.proxyAuthUserName, Utils.decrypt(this.proxyAuthPassword), "", ""));
    }

    /**
     * @return the isProxyAuthEnabled
     */
    public boolean isProxyAuthEnabled() {
        return isProxyAuthEnabled;
    }

    /**
     * @param isProxyAuthEnabled the isProxyAuthEnabled to set
     */
    public void setIsProxyAuthEnabled(boolean isProxyAuthEnabled) {
        this.isProxyAuthEnabled = isProxyAuthEnabled;
    }

    /**
     * @return the credentials
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * @param credentials the credentials to set
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * @return the lastFileIODirectory
     */
    public String getLastFileIODirectory() {
        return lastFileIODirectory;
    }

    /**
     * @param lastFileIODirectory the lastFileIODirectory to set
     */
    public void setLastFileIODirectory(String lastFileIODirectory) {
        this.lastFileIODirectory = lastFileIODirectory;
    }

    /**
     * @return the lastFileNameExtensionDescription
     */
    public String getLastSavedFileNameExtensionDescription() {
        return lastFileNameExtensionDescription;
    }

    /**
     * @param lastFileNameExtensionDescription the lastFileNameExtensionDescription to set
     */
    public void setLastFileNameExtensionDescription(String lastFileNameExtensionDescription) {
        this.lastFileNameExtensionDescription = lastFileNameExtensionDescription;
    }

    /**
     * @return the lastSelectedPageIndex
     */
    public int getLastSelectedPageIndex() {
        return lastSelectedPageIndex;
    }

    /**
     * @param lastSelectedPageIndex the lastSelectedPageIndex to set
     */
    public void setLastSelectedPageIndex(int lastSelectedPageIndex) {
        this.lastSelectedPageIndex = lastSelectedPageIndex;
    }

    /**
     * @return the lastSelectedBuyPortfolioChartIndex
     */
    public int getLastSelectedBuyPortfolioChartIndex() {
        return lastSelectedBuyPortfolioChartIndex;
    }

    /**
     * @param lastSelectedBuyPortfolioChartIndex the lastSelectedBuyPortfolioChartIndex to set
     */
    public void setLastSelectedBuyPortfolioChartIndex(int lastSelectedBuyPortfolioChartIndex) {
        this.lastSelectedBuyPortfolioChartIndex = lastSelectedBuyPortfolioChartIndex;
    }
    
    /**
     * @return the lastSelectedSellPortfolioChartIndex
     */
    public int getLastSelectedSellPortfolioChartIndex() {
        return lastSelectedSellPortfolioChartIndex;
    }

    /**
     * @param lastSelectedSellPortfolioChartIndex the lastSelectedSellPortfolioChartIndex to set
     */
    public void setLastSelectedSellPortfolioChartIndex(int lastSelectedSellPortfolioChartIndex) {
        this.lastSelectedSellPortfolioChartIndex = lastSelectedSellPortfolioChartIndex;
    }

    /**
     * Returns the watchlist name for current country. If there is no watchlist 
     * name for current country, a default watchlist name will be returned.
     * @return the watchlist name for current country
     */
    public String getWatchlistName() {
        final String p = this.watchlistNames.get(this.country);
        if (p == null) {
            // Not found. Returns default watchlist name.
            return org.yccheok.jstock.watchlist.Utils.getDefaultWatchlistName();
        }
        return p;
    }
    
    /**
     * Returns the portfolio name for current country. If there is no portfolio 
     * name for current country, a default portfolio name will be returned.
     * @return the portfolio name for current country
     */
    public String getPortfolioName() {
        final String p = this.portfolioNames.get(this.country);
        if (p == null) {
            // Not found. Returns default portfolio name.
            return org.yccheok.jstock.portfolio.Utils.getDefaultPortfolioName();
        }
        return p;
    }

    /**
     * @param p the portfolio name to set
     */
    public void setPortfolioName(String p) {
        this.portfolioNames.put(this.country, p);
    }

    /**
     * @param watchlistName the watchlistName to set
     */
    public void setWatchlistName(String watchlistName) {
        this.watchlistNames.put(this.country, watchlistName);
    }

    /**
     * @return the newsID
     */
    public String getNewsID() {
        return newsID;
    }

    /**
     * @param newsID the newsID to set
     */
    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public DecimalPlace getDecimalPlace(/*Country country*/) {
        DecimalPlace decimalPlace = this.decimalPlaces.get(this.country);
        if (decimalPlace == null) {
            return DecimalPlace.Two;
        }
        return decimalPlace;
    }

    // Do we need country as parameter?
    public void setDecimalPlace(/*Country country, */DecimalPlace decimalPlace) {
        this.decimalPlaces.put(this.country, decimalPlace);
    }

    public Long getGoogleCodeDatabaseMetaTimestamp(Country country) {
        return this.googleCodeDatabaseMeta.get(country);
    }

    public void setGoogleCodeDatabaseMetaTimestamp(Country country, long timestamp) {
        this.googleCodeDatabaseMeta.put(country, timestamp);
    }
    
    public PriceSource getPriceSource(Country country) {
        final PriceSource priceSource = this.priceSources.get(country);
        if (priceSource == null) {
            return org.yccheok.jstock.engine.Utils.getDefaultPriceSource(country);
        }
        return priceSource;
    }
    
    public void setPriceSource(Country country, PriceSource priceSource) {
        this.priceSources.put(country, priceSource);
    }
    
    /**
     * @return the soundEnabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * @param soundEnabled the soundEnabled to set
     */
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    /**
     * @param c the country to get
     * @return the currency symbol. If not currency symbol for the country, a
     * default symbol will be returned
     */
    public String getCurrencySymbol(Country c) {
        final String currecySymbol = this.currencies.get(c);
        if (currecySymbol == null) {
            // Not found. Returns default currency symbol.
            return Utils.getDefaultCurrencySymbol();
        }
        return currecySymbol;
    }

    /**
     * @param c the country to set
     * @param s the currency symbol to set
     */
    public void setCurrencySymbol(Country c, String s) {
        this.currencies.put(c, s);
    }

    /**
     * Returns true if currency exchange feature is enabled for country.
     *
     * @param c the country to get
     * @return true if currency exchange feature is enabled for the country
     */
    public boolean isCurrencyExchangeEnable(Country c) {
        Boolean flag = this.currencyExchangeEnable.get(c);
        if (flag != null) {
            return flag;
        }
        return false;
    }

    /**
     * Enables currency exchange feature for the country.
     *
     * @param country the country to set
     * @param enable true to enable
     */
    public void setCurrencyExchangeEnable(Country country, boolean enable) {
        this.currencyExchangeEnable.put(country, enable);
    }
    
    /**
     * Returns country of local currency used to purchase foreign stocks. If no
     * country of local currency found for the country of foreign stocks,
     * country of foreign stocks itself will be returned. So that we can get
     * 1:1 exchange ratio.
     * 
     * @param country country of foreign stocks
     * @return country of local currency used to purchase foreign stocks
     */
    public Country getLocalCurrencyCountry(Country country) {
        Country localCountry = this.localCurrencyCountries.get(country);
        if (localCountry != null) {
            return localCountry;
        }
        return country;
    }
        
    /**
     * Set the country of local currency used to purchase foreign stocks.
     * 
     * @param country country of foreign stocks
     * @param localCurrencyCountry country of local currency used to purchase
     * foreign stocks
     */
    public void setLocalCurrencyCountry(Country country, Country localCurrencyCountry) {
        this.localCurrencyCountries.put(country, localCurrencyCountry);
    }


    public boolean isPreferLongName(Country country) {
        Boolean prefer = this.preferLongNames.get(country);
        if (prefer != null) {
            return prefer;
        }
        // Default is true.
        return true;
    }
    
    public void setPreferLongName(Country country, boolean prefer) {
        this.preferLongNames.put(country, prefer);
    }
    
    /**
     * @return the yellowInformationBoxOption
     */
    public YellowInformationBoxOption getYellowInformationBoxOption() {
        return yellowInformationBoxOption;
    }

    /**
     * @param yellowInformationBoxOption the yellowInformationBoxOption to set
     */
    public void setYellowInformationBoxOption(YellowInformationBoxOption yellowInformationBoxOption) {
        this.yellowInformationBoxOption = yellowInformationBoxOption;
    }

    /**
     * @return the CCEmail
     */
    public String getCCEmail() {
        return CCEmail;
    }

    /**
     * @param CCEmail the CCEmail to set
     */
    public void setCCEmail(String CCEmail) {
        this.CCEmail = CCEmail;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * @return the boundsEx
     */
    public BoundsEx getBoundsEx() {
        return boundsEx;
    }

    /**
     * @param boundsEx the boundsEx to set
     */
    public void setBoundsEx(BoundsEx boundsEx) {
        this.boundsEx = boundsEx;
    }
    
    /**
     * @return the fallBelowAlertForegroundColor
     */
    public Color getFallBelowAlertForegroundColor() {
        return fallBelowAlertForegroundColor;
    }

    /**
     * @param fallBelowAlertForegroundColor the fallBelowAlertForegroundColor to set
     */
    public void setFallBelowAlertForegroundColor(Color fallBelowAlertForegroundColor) {
        this.fallBelowAlertForegroundColor = fallBelowAlertForegroundColor;
    }

    /**
     * @return the fallBelowAlertBackgroundColor
     */
    public Color getFallBelowAlertBackgroundColor() {
        return fallBelowAlertBackgroundColor;
    }

    /**
     * @param fallBelowAlertBackgroundColor the fallBelowAlertBackgroundColor to set
     */
    public void setFallBelowAlertBackgroundColor(Color fallBelowAlertBackgroundColor) {
        this.fallBelowAlertBackgroundColor = fallBelowAlertBackgroundColor;
    }

    /**
     * @return the riseAboveAlertForegroundColor
     */
    public Color getRiseAboveAlertForegroundColor() {
        return riseAboveAlertForegroundColor;
    }

    /**
     * @param riseAboveAlertForegroundColor the riseAboveAlertForegroundColor to set
     */
    public void setRiseAboveAlertForegroundColor(Color riseAboveAlertForegroundColor) {
        this.riseAboveAlertForegroundColor = riseAboveAlertForegroundColor;
    }

    /**
     * @return the riseAboveAlertBackgroundColor
     */
    public Color getRiseAboveAlertBackgroundColor() {
        return riseAboveAlertBackgroundColor;
    }

    /**
     * @param riseAboveAlertBackgroundColor the riseAboveAlertBackgroundColor to set
     */
    public void setRiseAboveAlertBackgroundColor(Color riseAboveAlertBackgroundColor) {
        this.riseAboveAlertBackgroundColor = riseAboveAlertBackgroundColor;
    }

    /**
     * @return the stockInputSuggestionListOption
     */
    public StockInputSuggestionListOption getStockInputSuggestionListOption() {
        return stockInputSuggestionListOption;
    }

    /**
     * @param stockInputSuggestionListOption the stockInputSuggestionListOption to set
     */
    public void setStockInputSuggestionListOption(StockInputSuggestionListOption stockInputSuggestionListOption) {
        this.stockInputSuggestionListOption = stockInputSuggestionListOption;
    }
    
    /**
     * @return the applicationVersionID
     */
    public int getApplicationVersionID() {
        return applicationVersionID;
    }
    
    /**
     * @param applicationVersionID the applicationVersionID to set
     */
    public void setApplicationVersionID(int applicationVersionID) {
        this.applicationVersionID = applicationVersionID;
    }

    /**
     * @return the chartTheme
     */
    public ChartTheme getChartTheme() {
        return chartTheme;
    }

    /**
     * @param chartTheme the chartTheme to set
     */
    public void setChartTheme(ChartTheme chartTheme) {
        this.chartTheme = chartTheme;
    }  
    
    /**
     * @return the isFeeCalculationEnabled
     */
    public boolean isFeeCalculationEnabled() {
        return isFeeCalculationEnabled;
    }

    public boolean useLargeFont() {
        return useLargeFont;
    }
    
    /**
     * @return the isDynamicChartVisible
     */    
    public boolean isDynamicChartVisible() {
        return isDynamicChartVisible;
    }
    
    /**
     * @param isFeeCalculationEnabled the isFeeCalculationEnabled to set
     */
    public void setFeeCalculationEnabled(boolean isFeeCalculationEnabled) {
        this.isFeeCalculationEnabled = isFeeCalculationEnabled;
    } 

    public void setUseLargeFont(boolean useLargeFont) {
        this.useLargeFont = useLargeFont;
    }
    
    /**
     * @param isDynamicChartVisible the isDynamicChartVisible to set
     */    
    public void setDynamicChartVisible(boolean isDynamicChartVisible) {
        this.isDynamicChartVisible = isDynamicChartVisible;
    }
    
    public List<Country> getRecentCountries() {
        List<Country> countries = new ArrayList<>(recentCountries);
        java.util.Collections.sort(countries);
        return countries;
    }
    
    public void addRecentCountry(Country country) {
        if (recentCountries.contains(country)) {
            return;
        }
        
        if (recentCountries.size() >= DEFAULT_RECENT_COUNTRY_SIZE) {
            recentCountries.remove(0);
        }
        
        recentCountries.add(country);
    }

    /**
     * @return the iexStockInfoDBMeta
     */
    public long getIEXStockInfoDBMeta() {
        return iexStockInfoDBMeta;
    }

    /**
     * @param iexStockInfoDBMeta the iexStockInfoDBMeta to set
     */
    public void setIEXStockInfoDBMeta(long iexStockInfoDBMeta) {
        this.iexStockInfoDBMeta = iexStockInfoDBMeta;
    }    
}
