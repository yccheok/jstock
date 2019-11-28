/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.alert;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.gui.Utils;

/**
 *
 * @author doraemon
 */
public class GoogleMail {
    private GoogleMail() {
    }

    private static MimeMessage createEmail(String to, String cc, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress cAddress = Utils.isNullOrEmpty(cc) ? null : new InternetAddress(cc);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(fAddress);
        if (cAddress != null) {
            email.addRecipient(javax.mail.Message.RecipientType.CC, cAddress);
        }
        email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    private static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);        
        String encodedEmail = java.util.Base64.getUrlEncoder().encodeToString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
  
    public static void Send(Gmail service, String recipientEmail, String ccEmail, String fromEmail, String title, String message) throws IOException, MessagingException {
        Message m = createMessageWithEmail(createEmail(recipientEmail, ccEmail, fromEmail, title, message));
        service.users().messages().send("me", m).execute();
    }
    
    public static void Send(String ccEmail, String title, String message) throws Exception {
        final Pair<Credential, String> credentialEx  = org.yccheok.jstock.google.Utils.authorizeGmailOffline();        
        final Credential credential = credentialEx.first;
        final Gmail service = org.yccheok.jstock.google.Utils.getGmail(credential);                    
        final String recipientEmail = credentialEx.second;
        final String fromEmail = credentialEx.second;
        
        Send(service, recipientEmail, ccEmail, fromEmail, title, message);
    }
    
    private static final Log log = LogFactory.getLog(GoogleMail.class);
}

