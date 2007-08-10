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

/**
 *
 * @author yccheok
 */
public class JStockOptions {
    
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
        setScanningSpeed(0);
        setAlertSpeed(5);
        setLookNFeel("");
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
}
