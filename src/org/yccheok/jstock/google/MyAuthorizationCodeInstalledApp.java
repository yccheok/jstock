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

package org.yccheok.jstock.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.model.Userinfoplus;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Pair;
import static org.yccheok.jstock.gui.trading.GUIUtils.openURLInBrowser;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;

/**
 *
 * @author yccheok
 */
public class MyAuthorizationCodeInstalledApp {

    private static final Log log = LogFactory.getLog(MyAuthorizationCodeInstalledApp.class);
    
    private static final String CODE = "org.yccheok.jstock.google.MyAuthorizationCodeInstalledApp";
    
    private SimpleSwingBrowser browser;  
    private String redirectUri;
    
    /** Authorization code flow. */
    private final AuthorizationCodeFlow flow;

    /** Verification code receiver. */
    private final VerificationCodeReceiver receiver;
  
    /**
     * @param flow authorization code flow
     * @param receiver verification code receiver
     */
    public MyAuthorizationCodeInstalledApp(
        AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
      this.flow = Preconditions.checkNotNull(flow);
      this.receiver = Preconditions.checkNotNull(receiver);
    }

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param userId user ID or {@code null} if not using a persisted credential store
     * @return credential
     */
    public Pair<Pair<Credential, String>, Boolean> authorize(String userId) throws IOException {
        try {
            Credential credential = flow.loadCredential(userId);
            if (credential != null && credential.getRefreshToken() != null) {
                boolean success = false;
                if (credential.getExpiresInSeconds() <= 60) {
                    if (credential.refreshToken()) {
                        success = true;
                    }
                } else {
                    success = true;
                }
                
                if (success) {
                    FileDataStoreFactory fileDataStoreFactory = (FileDataStoreFactory)flow.getCredentialDataStore().getDataStoreFactory();
                    String email = Utils.loadEmail(fileDataStoreFactory.getDataDirectory());
                    if (email == null) {
                        Userinfoplus userinfoplus = org.yccheok.jstock.google.Utils.getUserInfo(credential);
                        email = userinfoplus.getEmail();
                    }                    
                    return new Pair<Pair<Credential, String>, Boolean>(new Pair<Credential, String>(credential, email), true);
                }
            }
            
            // open in browser
            redirectUri = receiver.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl =
            flow.newAuthorizationUrl().setRedirectUri(redirectUri);
            onAuthorization(authorizationUrl);
            // receive authorization code and exchange it for an access token
            String code = receiver.waitForCode();
            if (code.equals(CODE)) {
                return null;
            }
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            // store credential and return it
            credential = flow.createAndStoreCredential(response, userId);
            Userinfoplus userinfoplus = org.yccheok.jstock.google.Utils.getUserInfo(credential);
            String email = userinfoplus.getEmail();
            FileDataStoreFactory fileDataStoreFactory = (FileDataStoreFactory)flow.getCredentialDataStore().getDataStoreFactory();
            Utils.saveEmail(fileDataStoreFactory.getDataDirectory(), email);
            return new Pair<Pair<Credential, String>, Boolean>(new Pair<Credential, String>(credential, email), false);
        } finally {        
            receiver.stop();
            SimpleSwingBrowser _browser = this.browser;
            
            if (_browser != null) {
                _browser.setVisible(false);
                try {
                    // Possible cause random exception
                    // java.lang.NullPointerException
                    //     at javafx.embed.swing.JFXPanel.getInputMethodRequests(JFXPanel.java:810)
                    //
                    // If such exception happens, there's no chance to execute windowClosed.
                    _browser.dispose();
                } catch (Exception ex) {
                    performRedirectUri();
                    log.error(null, ex);
                }
                this.browser = null;
            }      
        }
    }

    /**
     * Handles user authorization by redirecting to the OAuth 2.0 authorization server.
     *
     * <p>
     * Default implementation is to call {@code browse(authorizationUrl.build())}. Subclasses may
     * override to provide optional parameters such as the recommended state parameter. Sample
     * implementation:
     * </p>
     *
     * <pre>
    &#64;Override
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
      authorizationUrl.setState("xyz");
      super.onAuthorization(authorizationUrl);
    }
     * </pre>
     *
     * @param authorizationUrl authorization URL
     * @throws IOException I/O exception
     */
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        final String url = authorizationUrl.build();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
               if (!launchJavaFxBrowser(url)) {
                   launchDesktopBrowser(url);
               }
           }     
       });       
       
    }

    private void launchDesktopBrowser(String url) {
        try {
            if (openURLInBrowser(url)) {
                // Show dialog, to prevent performRedirectUri from being executed
                // immediately.
                JOptionPane.showMessageDialog(
                        null, 
                        MessagesBundle.getString("info_message_press_ok_after_browser_is_closed"),
                        GUIBundle.getString("MainFrame_Application_Title"),
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        null, 
                        MessagesBundle.getString("error_message_fail_to_launch_browser"), 
                        MessagesBundle.getString("error_title_fail_to_launch_browser"),
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } finally {
            performRedirectUri();
        }
    }
    
    private boolean launchJavaFxBrowser(String url) {
        try {
            SimpleSwingBrowser _browser = new SimpleSwingBrowser();

           _browser.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
           _browser.addWindowListener(new java.awt.event.WindowAdapter() {
               @Override
               public void windowClosed(java.awt.event.WindowEvent evt) {
                   performRedirectUri();
               }

               @Override
               public void windowClosing(java.awt.event.WindowEvent evt) {                        
                   SimpleSwingBrowser _browser = browser;
                   if (_browser != null) {
                       _browser.setVisible(false);
                       try {
                           // Possible cause random exception
                           // java.lang.NullPointerException
                           //     at javafx.embed.swing.JFXPanel.getInputMethodRequests(JFXPanel.java:810)
                           //
                           // If such exception happens, there's no chance to execute windowClosed.
                           _browser.dispose();
                       } catch (Exception ex) {
                           performRedirectUri();
                           log.error(null, ex);
                       }
                       browser = null;
                   }
               }
               @Override
               public void windowDeiconified(java.awt.event.WindowEvent evt) {
               }
               @Override
               public void windowIconified(java.awt.event.WindowEvent evt) {
               }
           });

           browser = _browser;
           _browser.loadURL(url);
           _browser.setVisible(true);
        } catch (Exception e) {
            log.error(null, e);
            return false;
        }
        
        return true;
    }
    
    private void performRedirectUri() {
        String uri = redirectUri;

        if (uri == null) {
            return;
        }

        // Avoid redirectUri being called twice.
        // http://stackoverflow.com/questions/23319579/why-formwindowclosed-is-being-triggered-twice-in-jdialog-after-dispose
        redirectUri = null;

        final String url = uri + "?code=" + CODE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(url);
            }

        }).start();
    }
    
    /** Returns the authorization code flow. */
    public final AuthorizationCodeFlow getFlow() {
        return flow;
    }

    /** Returns the verification code receiver. */
    public final VerificationCodeReceiver getReceiver() {
        return receiver;
    }
}
