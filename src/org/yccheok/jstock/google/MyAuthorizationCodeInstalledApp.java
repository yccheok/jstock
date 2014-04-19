/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 *
 * @author yccheok
 */
public class MyAuthorizationCodeInstalledApp {

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
    public Credential authorize(String userId) throws IOException {
        try {
            Credential credential = flow.loadCredential(userId);
            if (credential != null
                && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() > 60)) {
                return credential;
            }
            // open in browser
            redirectUri = receiver.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl =
            flow.newAuthorizationUrl().setRedirectUri(redirectUri);
            onAuthorization(authorizationUrl);
            // receive authorization code and exchange it for an access token
            String code = receiver.waitForCode();
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            // store credential and return it
            return flow.createAndStoreCredential(response, userId);
        } finally {        
            receiver.stop();
            SimpleSwingBrowser _browser = this.browser;
            
            if (_browser != null) {
                _browser.setVisible(false);
                _browser.dispose();
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
                SimpleSwingBrowser _browser = new SimpleSwingBrowser();
                
                _browser.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
                _browser.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent evt) {

                        if (redirectUri == null) {
                            return;
                        }
                        
                        String url = redirectUri + "?error=windowClosed";
                        org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(url);
                    }
                    
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent evt) {
                        SimpleSwingBrowser _browser = browser;
                        if (_browser != null) {
                            _browser.setVisible(false);
                            _browser.dispose();
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
                
                _browser.setVisible(true);
                _browser.loadURL(url);
                browser = _browser;
           }     
       });       
       
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
