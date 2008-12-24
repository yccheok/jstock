/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.chat;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

/**
 *
 * @author yccheok
 */
public class Main {
    public static void main(String[] args) throws InterruptedException
    {
        XMPPConnection connection = new XMPPConnection("programmer-art.org");

        try {
            connection.connect();
            connection.login("yccheok", "yccheok");
        } catch (XMPPException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            AccountManager accountManager = connection.getAccountManager();
            try {
                accountManager.createAccount("yccheok", "yccheok");
            } catch (XMPPException ex1) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        System.out.println("Connected...");
        try {
            Collection<String> serviceNames = MultiUserChat.getServiceNames(connection);
            for (String service : serviceNames) {
                System.out.println("service = " + service);
            }
        } catch (XMPPException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        MultiUserChat muc = new MultiUserChat(connection, "jstock");
        // Gets all the available rooms from the server
        Collection<HostedRoom> hostedRooms = null;
        try {
            hostedRooms = MultiUserChat.getHostedRooms(connection, "conference.programmer-art.org");
        } catch (XMPPException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (HostedRoom hostedRoom : hostedRooms) {
            // Get more detailed information about each room
            RoomInfo info;
            try {
                info = MultiUserChat.getRoomInfo(connection, hostedRoom.getJid());
                // Extract whatever information you want about each room
                info.getSubject();
            } catch (XMPPException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /*
        Chat chat = connection.getChatManager().createChat("yccheok@jabber.org", new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                System.out.println("Received message: " + message);
            }
        });

        try {
            chat.sendMessage("Howdy!");
        } catch (XMPPException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("sleep");
        Thread.sleep(1000000);
        */
    }
}
