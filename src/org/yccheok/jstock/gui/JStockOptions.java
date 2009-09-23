/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.yccheok.jstock.portfolio.BrokingFirm;
import org.yccheok.jstock.engine.Country;

/**
 *
 * @author yccheok
 */
public class JStockOptions {
    
    public static final java.awt.Color DEFAULT_NORMAL_TEXT_FOREGROUND_COLOR = Color.BLACK;
    public static final java.awt.Color DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR = new java.awt.Color(50, 150, 0);
    public static final java.awt.Color DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR = new java.awt.Color(200, 0, 50);
    public static final java.awt.Color DEFAULT_FIRST_ROW_BACKGROUND_COLOR = Color.WHITE;
    public static final java.awt.Color DEFAULT_SECOND_ROW_BACKGROUND_COLOR = new java.awt.Color(255, 255, 204);
    public static final java.awt.Color DEFAULT_AUTO_UPDATE_FOREGROUND_COLOR = Color.BLACK;
    public static final java.awt.Color DEFAULT_AUTO_UPDATE_BACKGROUND_COLOR = Color.RED;
    public static final java.awt.Color DEFAULT_ALERT_FOREGROUND_COLOR = Color.YELLOW;
    public static final java.awt.Color DEFAULT_ALERT_BACKGROUND_COLOR = Color.BLACK;

    public static final java.awt.Color DEFAULT_CHAT_SYSTEM_MESSAGE_COLOR = Color.RED;
    public static final java.awt.Color DEFAULT_CHAT_OWN_MESSAGE_COLOR = new java.awt.Color(169, 169, 169);
    public static final java.awt.Color DEFAULT_CHAT_OTHER_MESSAGE_COLOR = new java.awt.Color(100, 149, 237);

    private static final int DEFAULT_HISTORY_DURATION =  10;

    /** Creates a new instance of JStockOptions */
    public JStockOptions() {
        setPopupMessage(true);
        setSendEmail(false);
        setEmail("");
        setEmailPassword("");
        setSMSEnabled(false);
        setGoogleCalendarUsername("");
        setGoogleCalendarPassword("");
        setSingleIndicatorAlert(true);
        setProxyServer("");
        setProxyPort(-1);
        setScanningSpeed(1000);
        setAlertSpeed(5);
        setLookNFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        setCountry(Country.Malaysia);
        
        this.setNormalTextForegroundColor(DEFAULT_NORMAL_TEXT_FOREGROUND_COLOR);
        this.setHigherNumericalValueForegroundColor(DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR);
        this.setLowerNumericalValueForegroundColor(DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR);
        
        setFirstRowBackgroundColor(DEFAULT_FIRST_ROW_BACKGROUND_COLOR);
        setSecondRowBackgroundColor(DEFAULT_SECOND_ROW_BACKGROUND_COLOR);
                
        this.setEnableColorChange(false);
        this.setAutoUpdateForegroundColor(DEFAULT_AUTO_UPDATE_FOREGROUND_COLOR);
        this.setAutoUpdateBackgroundColor(DEFAULT_AUTO_UPDATE_BACKGROUND_COLOR);

        this.setEnableColorAlert(false);
        this.setAlertForegroundColor(DEFAULT_ALERT_FOREGROUND_COLOR);
        this.setAlertBackgroundColor(DEFAULT_ALERT_BACKGROUND_COLOR);

        this.setAutoUpdateNewsEnabled(true);
        this.setNewsVersion(0);

        this.setHistoryDuration(DEFAULT_HISTORY_DURATION);

        this.setChatEnabled(false);
        this.setChatUsername("");
        this.setChatPassword("");
        this.setChatFlashNotificationEnabled(true);
        this.setChatSoundNotificationEnabled(true);

        this.setChatSystemMessageColor(DEFAULT_CHAT_SYSTEM_MESSAGE_COLOR);
        this.setChatOwnMessageColor(DEFAULT_CHAT_OWN_MESSAGE_COLOR);
        this.setChatOtherMessageColor(DEFAULT_CHAT_OTHER_MESSAGE_COLOR);
    }
    
    private boolean singleIndicatorAlert;
    private boolean popupMessage;
    private boolean sendEmail;
    private String email;
    private String emailPassword;
    private String googleCalendarUsername;
    private String googleCalendarPassword;
    private boolean SMSEnabled;
    @Deprecated
    private volatile boolean passwordProtectedIndicator;
    @Deprecated
    private volatile String indicatorPassword;
    private String proxyServer;
    private int proxyPort;
    private int scanningSpeed;  /* In second. */
    private int alertSpeed;
    private String looknFeel;
    
    private Color normalTextForegroundColor;
    private Color lowerNumericalValueForegroundColor;
    private Color higherNumericalValueForegroundColor;
    private Color firstRowBackgroundColor;
    private Color secondRowBackgroundColor;
    private Color autoUpdateForegroundColor;
    private Color autoUpdateBackgroundColor;
    private Color alertForegroundColor;
    private Color alertBackgroundColor;
    private boolean enableColorChange;
    private boolean enableColorAlert;

    private List<BrokingFirm> brokingFirms = new ArrayList<BrokingFirm>();
    private int selectedBrokingFirmIndex = -1;
    private boolean isAutoBrokerFeeCalculationEnabled = false;
    
    private double expectedProfitPercentage = 10.0;

    private Country country;

    private boolean isAutoUpdateNewsEnabled;
    private long newsVersion;

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

    // Remember where we save/open the last file.
    private String lastFileIODirectory = System.getProperty("user.home");
    private String lastFileNameExtensionDescription = "CSV Documents (*.csv)";

    private Map<Country, Class> primaryStockServerFactoryClasses = new HashMap<Country, Class>();

    // Remember the last view page.
    private int lastSelectedPageIndex = 0;

    private String portfolioName = "My Portfolio";
    
    public boolean isAutoBrokerFeeCalculationEnabled() {
        return this.isAutoBrokerFeeCalculationEnabled;
    }
    
    public void setAutoBrokerFeeCalculationEnabled(boolean isAutoBrokerFeeCalculationEnabled) {
        this.isAutoBrokerFeeCalculationEnabled = isAutoBrokerFeeCalculationEnabled;
    }
    
    private Object readResolve() {
        /* For backward compatible */
        if (lastSelectedPageIndex < 0) {
            lastSelectedPageIndex = 0;
        }

        /* For backward compatible */
        if (brokingFirms == null) {
            brokingFirms = new ArrayList<BrokingFirm>();
        }
        
        /* For backward compatible */
        if (country == null) {
            country = Country.Malaysia;
        }
        
        /* For backward compatible */
        if (newsVersion < 0) {
            newsVersion = 0;
        }

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

        if (this.getAlertForegroundColor() == null) {
            this.setAlertForegroundColor(DEFAULT_ALERT_FOREGROUND_COLOR);
        }

        if (this.getAlertBackgroundColor() == null) {
            this.setAlertBackgroundColor(DEFAULT_ALERT_BACKGROUND_COLOR);    
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

        if (this.getGoogleCalendarUsername() == null) {
            this.setGoogleCalendarUsername("");
            this.setSMSEnabled(false);
        }

        if (this.getGoogleCalendarPassword() == null) {
            setGoogleCalendarPassword("");
            this.setSMSEnabled(false);
        }

        if (this.primaryStockServerFactoryClasses == null) {
            primaryStockServerFactoryClasses = new HashMap<Country, Class>();
        }

        if (this.portfolioName == null) {
            this.portfolioName = "My Portfolio";
        }
        
        return this;
    }    
    
    public int getBrokingFirmSize() {        
        return brokingFirms.size();
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
        if((this.selectedBrokingFirmIndex < 0) || (this.selectedBrokingFirmIndex >= this.getBrokingFirmSize()))
            return null;
        
        return this.brokingFirms.get(this.selectedBrokingFirmIndex);
    }
    
    public String getLooknFeel() {
        return looknFeel;
    }
    
    public void setLookNFeel(String looknFeel) {
        this.looknFeel = looknFeel;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
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

    public int getAlertSpeed() {
        return alertSpeed;
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

    public void setNewsVersion(long newsVersion)
    {
        this.newsVersion = newsVersion;
    }

    public long getNewsVersion()
    {
        return this.newsVersion;
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
     * @return the alertForegroundColor
     */
    public Color getAlertForegroundColor() {
        return alertForegroundColor;
    }

    /**
     * @param alertForegroundColor the alertForegroundColor to set
     */
    public void setAlertForegroundColor(Color alertForegroundColor) {
        this.alertForegroundColor = alertForegroundColor;
    }

    /**
     * @return the alertBackgroundColor
     */
    public Color getAlertBackgroundColor() {
        return alertBackgroundColor;
    }

    /**
     * @param alertBackgroundColor the alertBackgroundColor to set
     */
    public void setAlertBackgroundColor(Color alertBackgroundColor) {
        this.alertBackgroundColor = alertBackgroundColor;
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
     * @return the googleCalendarUsername
     */
    public String getGoogleCalendarUsername() {
        return googleCalendarUsername;
    }

    /**
     * @param googleCalendarUsername the googleCalendarUsername to set
     */
    public void setGoogleCalendarUsername(String googleCalendarUsername) {
        this.googleCalendarUsername = googleCalendarUsername;
    }

    /**
     * @return the googleCalendarPassword
     */
    public String getGoogleCalendarPassword() {
        return googleCalendarPassword;
    }

    /**
     * @param googleCalendarPassword the googleCalendarPassword to set
     */
    public void setGoogleCalendarPassword(String googleCalendarPassword) {
        this.googleCalendarPassword = googleCalendarPassword;
    }

    /**
     * @return the SMSEnabled
     */
    public boolean isSMSEnabled() {
        return SMSEnabled;
    }

    /**
     * @param SMSEnabled the SMSEnabled to set
     */
    public void setSMSEnabled(boolean SMSEnabled) {
        this.SMSEnabled = SMSEnabled;
    }

    public Class addPrimaryStockServerFactoryClass(Country country, Class c) {
        return primaryStockServerFactoryClasses.put(country, c);
    }

    public Class getPrimaryStockServerFactoryClass(Country country) {
        return primaryStockServerFactoryClasses.get(country);
    }

    /**
     * @return the portfolioName
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * @param portfolioName the portfolioName to set
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }
}
