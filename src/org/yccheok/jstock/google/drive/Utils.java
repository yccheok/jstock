/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class DriveUtils {
    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the JSON factory. */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  
    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    private static final Log log = LogFactory.getLog(DriveUtils.class);
    
    static {
        try {
            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            
            // initialize the data store factory
            dataStoreFactory = new FileDataStoreFactory(getDataDirectory());
        } catch (IOException ex) {
            log.error(null, ex);
        } catch (GeneralSecurityException ex) {
            log.error(null, ex);
        }
    }
    
    public static File getDataDirectory() {
        return new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "authentication");
    }
    
    /** Authorizes the installed application to access user's protected data. */
    public static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(DriveUtils.JSON_FACTORY,
            new InputStreamReader(DriveUtils.class.getResourceAsStream("/resources/drive_client_secrets.json")));
        // Set up authorization code flow.
        // Ask for only the permissions you need. Asking for more permissions will
        // reduce the number of users who finish the process for giving you access
        // to their accounts. It will also increase the amount of effort you will
        // have to spend explaining to users what you are doing with their data.
        // Here we are listing all of the available scopes. You should remove scopes
        // that you are not actually using.
        Set<String> scopes = new HashSet<String>();
        scopes.add(DriveScopes.DRIVE_APPDATA);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, scopes)
            .setDataStoreFactory(dataStoreFactory)
            .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
