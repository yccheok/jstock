/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.trading.API;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 *
 * @author shuwnyuan
 */
public class Http {

    private Http () {}
    
    private static HttpMethod setJsonHeader (HttpMethod httpMethod, String sessionKey) {
        httpMethod.addRequestHeader("Accept", "application/json");
        httpMethod.addRequestHeader("Content-Type", "application/json");

        if (sessionKey != null) {
            httpMethod.addRequestHeader("x-mysolomeo-session-key", sessionKey);
        }
        return httpMethod;
    }

    public static Map<String, Object> executeCall (HttpMethod httpMethod) {
        HttpClient httpClient = new HttpClient();
        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(httpMethod);
            //System.out.println("status code: " + statusCode);
        } catch (IOException ex) {
            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String respond = null;
        try {
            StringBuilder resultStr = new StringBuilder();
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(httpMethod.getResponseBodyAsStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    resultStr.append(line);
                }
            }
            respond = resultStr.toString();
            httpMethod.releaseConnection();
        } catch (IOException ex) {
            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", statusCode);
        result.put("respond", respond);
        
        //System.out.println("respond: " + respond);
        
        return result;
    }

    public static Map<String, Object> get (String url, String sessionKey) {
        GetMethod getMethod = new GetMethod(DriveWealth.hostURL + url);
        getMethod = (GetMethod) setJsonHeader(getMethod, sessionKey);
        return executeCall(getMethod);
    }

    public static Map<String, Object> post (String url, Map<String, Object> params, String sessionKey, String server) {
        Gson gson = new Gson();
        String paramsJson = gson.toJson(params);

        StringRequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(
                    paramsJson,
                    "application/json",
                    "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
        }

        String host;
        switch (server) {
            case "apiServer":
                host = DriveWealth.hostURL;
                break;
            case "reportServer":
                host = DriveWealth.reportURL;
                break;
            default:
                //System.out.println("[executePost] unknown hostType: " + server);
                return null;
        }

        PostMethod postMethod = new PostMethod(host + url);
        postMethod.setRequestEntity(requestEntity);

        postMethod = (PostMethod) setJsonHeader(postMethod, sessionKey);
        return executeCall(postMethod);
    }
    
    public static Map<String, Object> post (String url, Map<String, Object> params, String sessionKey) {
        return post(url, params, sessionKey, "apiServer");
    }
    
    public static Map<String, Object> put (String url, Map<String, Object> params, String sessionKey) {
        Gson gson = new Gson();
        String paramsJson = gson.toJson(params);

        StringRequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(
                    paramsJson,
                    "application/json",
                    "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
        }

        PutMethod putMethod = new PutMethod(DriveWealth.hostURL + url);
        putMethod.setRequestEntity(requestEntity);

        putMethod = (PutMethod) setJsonHeader(putMethod, sessionKey);
        return executeCall(putMethod);
    }

    public static Map<String, Object> delete (String url, String sessionKey) {
        DeleteMethod deleteMethod = new DeleteMethod(DriveWealth.hostURL + url);
        deleteMethod = (DeleteMethod) setJsonHeader(deleteMethod, sessionKey);
        return executeCall(deleteMethod);
    }
    
}
