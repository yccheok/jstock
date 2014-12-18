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
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Pair;

/**
 *
 * @author yccheok
 */
public class Utils {
    /** Global instance of the JSON factory. */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  
    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    private static final Log log = LogFactory.getLog(Utils.class);
    
    static {
        try {
            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            
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

    public static String loadEmail(File dataStoreDirectory)  {
        File file = new File(dataStoreDirectory, "email");
        try {
            return new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");
        } catch (IOException ex) {
            log.error(null, ex);
            return null;
        }
    }

    public static boolean saveEmail(File dataStoreDirectory, String email) {
        File file = new File(dataStoreDirectory, "email");
        try {
            //If the constructor throws an exception, the finally block will NOT execute
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            try {
                writer.write(email);
            } finally {
                writer.close();
            }
            return true;
        } catch (IOException ex){
            log.error(null, ex);
            return false;
        }
    }
    
    public static void logoutCalendar() {
        File credential = new File(getCalendarDataDirectory(), "StoredCredential");
        File email = new File(getCalendarDataDirectory(), "email");
        credential.delete();
        email.delete();
    }
    
    public static void logoutDrive() {
        File credential = new File(getDriveDataDirectory(), "StoredCredential");
        File email = new File(getDriveDataDirectory(), "email");
        credential.delete();
        email.delete();
    }

    public static Pair<Pair<Credential, String>, Boolean> authorizeCalendar() throws Exception {
        Set<String> scopes = new HashSet<String>();
        scopes.add("email");
        scopes.add("profile");
        scopes.add(CalendarScopes.CALENDAR);  

        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Utils.JSON_FACTORY,
            new InputStreamReader(Utils.class.getResourceAsStream("/assets/authentication/calendar/client_secrets.json")));
        
        return authorize(clientSecrets, scopes, getCalendarDataDirectory());
    }
    
    public static Pair<Pair<Credential, String>, Boolean> authorizeDrive() throws Exception {
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
        // Legacy. Shall be removed after a while...
        scopes.add(DriveScopes.DRIVE);
        
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Utils.JSON_FACTORY,
            new InputStreamReader(Utils.class.getResourceAsStream("/assets/authentication/drive/client_secrets.json")));
        
        return authorize(clientSecrets, scopes, getDriveDataDirectory());
    }
    
    public static Pair<Credential, String> authorizeCalendarOffline() throws Exception {
        Set<String> scopes = new HashSet<String>();
        scopes.add("email");
        scopes.add("profile");
        scopes.add(CalendarScopes.CALENDAR);  
        
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Utils.JSON_FACTORY,
            new InputStreamReader(Utils.class.getResourceAsStream("/assets/authentication/calendar/client_secrets.json")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, scopes)
            .setDataStoreFactory(new FileDataStoreFactory(getCalendarDataDirectory()))
            .build();
        
        Credential credential = flow.loadCredential("user");
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
                return new Pair<Credential, String>(credential, email);
            }
        }
        
        return null;
    }
    
    /** Authorizes the installed application to access user's protected data.
     * @return 
     * @throws java.lang.Exception */
    private static Pair<Pair<Credential, String>, Boolean> authorize(GoogleClientSecrets clientSecrets, Set<String> scopes, File dataStoreDirectory) throws Exception {
        // Set up authorization code flow.

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, scopes)
            .setDataStoreFactory(new FileDataStoreFactory(dataStoreDirectory))
            .build();
        // authorize
        return new MyAuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    
    public static Drive getDrive(Credential credential) {
        Drive service = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("JStock").build();
        return service;
    }
    
    public static Calendar getCalendar(Credential credential) {
        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("JStock").build();
        return service;
    }    
}
