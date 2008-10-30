/*
 * JStockOptions.java
 *
 * Created on June 19, 2007, 11:00 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
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
    
    /** Creates a new instance of JStockOptions */
    public JStockOptions() {
        setPopupMessage(true);
        setSendEmail(false);
        setEmail("");
        setEmailPassword("");
        setPasswordProtectedIndicator(false);
        setSingleIndicatorAlert(true);
        setIndicatorPassword("");
        setProxyServer("");
        setProxyPort(-1);
        setScanningSpeed(1000);
        setAlertSpeed(5);
        setLookNFeel("");
        setCountry(Country.Malaysia);
        
        this.setNormalTextForegroundColor(DEFAULT_NORMAL_TEXT_FOREGROUND_COLOR);
        this.setHigherNumericalValueForegroundColor(DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR);
        this.setLowerNumericalValueForegroundColor(DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR);
        
        setFirstRowBackgroundColor(DEFAULT_FIRST_ROW_BACKGROUND_COLOR);
        setSecondRowBackgroundColor(DEFAULT_SECOND_ROW_BACKGROUND_COLOR);
                
        this.setEnableColorChange(false);
        this.setAutoUpdateForegroundColor(DEFAULT_AUTO_UPDATE_FOREGROUND_COLOR);
        this.setAutoUpdateBackgroundColor(DEFAULT_AUTO_UPDATE_BACKGROUND_COLOR);        
    }
    
    private boolean singleIndicatorAlert;
    private boolean popupMessage;
    private boolean sendEmail;
    private String email;
    private String emailPassword;
    private boolean passwordProtectedIndicator;
    private String indicatorPassword;
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
    private boolean enableColorChange;
    
    private List<BrokingFirm> brokingFirms = new ArrayList<BrokingFirm>();
    private int selectedBrokingFirmIndex = -1;
    private boolean isAutoBrokerFeeCalculationEnabled = false;
    
    private double expectedProfitPercentage = 10.0;
    
    private Country country;
        
    public boolean isAutoBrokerFeeCalculationEnabled() {
        return this.isAutoBrokerFeeCalculationEnabled;
    }
    
    public void setAutoBrokerFeeCalculationEnabled(boolean isAutoBrokerFeeCalculationEnabled) {
        this.isAutoBrokerFeeCalculationEnabled = isAutoBrokerFeeCalculationEnabled;
    }
    
    private Object readResolve() {
        /* For backward compatible */
        if(brokingFirms == null) {
            brokingFirms = new ArrayList<BrokingFirm>();
        }
        
		/* For backward compatible */
        if(country == null) {
            country = Country.Malaysia;
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

    public boolean isPasswordProtectedIndicator() {
        return passwordProtectedIndicator;
    }

    public void setPasswordProtectedIndicator(boolean passwordProtectedIndicator) {
        this.passwordProtectedIndicator = passwordProtectedIndicator;
    }

    public String getIndicatorPassword() {
        return indicatorPassword;
    }

    public void setIndicatorPassword(String indicatorPassword) {
        this.indicatorPassword = indicatorPassword;
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
}
