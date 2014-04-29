/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.google;



import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.gui.Pair;

/**
 *
 * @author yccheok
 */
public class Utils {
    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory driveDataStoreFactory;
    private static FileDataStoreFactory calendarDataStoreFactory;

    /** Global instance of the JSON factory. */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  
    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    private static final Log log = LogFactory.getLog(Utils.class);
    
    static {
        try {
            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            
            // initialize the data store factory
            driveDataStoreFactory = new FileDataStoreFactory(getDriveDataDirectory());
            calendarDataStoreFactory = new FileDataStoreFactory(getCalendarDataDirectory());
        } catch (IOException ex) {
            log.error(null, ex);
        } catch (GeneralSecurityException ex) {
            log.error(null, ex);
        }
    }
    
    private static File getDriveDataDirectory() {
        return new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "authentication" + File.separator + "drive");
    }

    private static File getCalendarDataDirectory() {
        return new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "authentication" + File.separator + "calendar");
    }
    
    /**
     * Send a request to the UserInfo API to retrieve the user's information.
     *
     * @param credentials OAuth 2.0 credentials to authorize the request.
     * @return User's information.
     * @throws java.io.IOException
     */
    public static Userinfoplus getUserInfo(Credential credentials) throws IOException
    {
        Oauth2 userInfoService =
            new Oauth2.Builder(httpTransport, JSON_FACTORY, credentials).setApplicationName("JStock").build();
        Userinfoplus userInfo  = userInfoService.userinfo().get().execute();
        return userInfo;
    }
  
    public static Pair<Credential, Userinfoplus> authorizeDrive() throws Exception {
        // Ask for only the permissions you need. Asking for more permissions will
        // reduce the number of users who finish the process for giving you access
        // to their accounts. It will also increase the amount of effort you will
        // have to spend explaining to users what you are doing with their data.
        // Here we are listing all of the available scopes. You should remove scopes
        // that you are not actually using.
        Set<String> scopes = new HashSet<String>();
        scopes.add("email");
        scopes.add("profile");
        scopes.add(DriveScopes.DRIVE_APPDATA);  
        
        return authorize(scopes, driveDataStoreFactory);
    }
    
    /** Authorizes the installed application to access user's protected data.
     * @return 
     * @throws java.lang.Exception */
    private static Pair<Credential, Userinfoplus> authorize(Set<String> scopes, FileDataStoreFactory dataStoreFactory) throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Utils.JSON_FACTORY,
            new InputStreamReader(Utils.class.getResourceAsStream("/resources/drive_client_secrets.json")));
        // Set up authorization code flow.

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, scopes)
            .setDataStoreFactory(dataStoreFactory)
            .build();
        // authorize
        return new MyAuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    
    public static Drive getDrive(Credential credential) {
        Drive service = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("JStock").build();
        return service;
    }
}
