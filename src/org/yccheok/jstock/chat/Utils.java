/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.chat;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Country;
import org.yccheok.jstock.gui.MainFrame;

/**
 *
 * @author yccheok
 */
public class Utils {
    private Utils() {
    }

    public enum Sound
    {
        ALERT,
        LOGIN,
        LOGOUT,
        RECEIVE,
        SEND
    };

    public static String getXMPPServer()
    {
        final String defaultServer = "jabber.org";

        final String _server = org.yccheok.jstock.gui.Utils.getUUIDValue(org.yccheok.jstock.engine.Utils.getJStockStaticServer() + "servers_information/chat_server.txt", "server");

        if (_server == null) {
            log.info("Server not found");
            return defaultServer;
        }

        final String server = org.yccheok.jstock.gui.Utils.decrypt(_server);
        if (server.length() <= 0) {
            return defaultServer;
        }

		log.info("Google App Engine suggests us to use " + server);
		return server;
    }

    public static String getEmotesIconsDirectory() {
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "chat" + File.separator + "emotes" + File.separator + "default" + File.separator;
    }

    public static String getSoundsDirectory() {
        return org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "chat" + File.separator + "sounds" + File.separator + "purple" + File.separator;
    }

    public static String getRoomName(Country country) {
        return country + "_jstock";
    }

    private static String buildEmotionalTag(String src) {
        /*
        :)	-	smile.png
        :O	-	shock.png
        :">	-	embarrassed.png
        :D	-	smile-big.png
        ;)	-	wink.png
        :((	-	crying.png
        :(	-	sad.png
        :P	-	tongue.png
        :|	-	neutral.png
         */

        if (icons.size() == 0) {
            try {
                icons.put(Pattern.compile(":\\)", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "smile.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(":O", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "shock.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(":\"&gt;", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "embarrassed.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(":D", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "smile-big.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(";\\)", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "wink.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(":\\(\\(", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "crying.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(":\\(", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "sad.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(":P", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "tongue.png").toURI().toURL().toString() + "\"/>");
                icons.put(Pattern.compile(":\\|", Pattern.CASE_INSENSITIVE), "<img src=\"" + new File(Utils.getEmotesIconsDirectory() + "neutral.png").toURI().toURL().toString() + "\"/>");
            } catch (MalformedURLException ex) {
                // Unlikely.
                log.error(null, ex);
            }
        }

        final Set<Pattern> patterns = icons.keySet();
        for (Pattern pattern : patterns) {
            src = pattern.matcher(src).replaceAll(icons.get(pattern));
        }

        return src;
    }

    private static String buildHyperLinkTag(String src) {
        final Matcher matcher = URLPattern.matcher(src);
        return matcher.replaceAll("<a href=\"$0\">$0</a>");
    }

    // We should make use of StringEscapeUtils utility methods from apache common.
    private static String escapeHTMLEntities(String src) {
        return src.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    public static String getJEditorPaneEmptyHeader() {
        return "<html><head></head><body style=\"font-size: 9px; font-family: Tahoma;\"></body></html>";
    }

    public static String getHTMLAccordingToMessageMode(String who, String msg, ChatJPanel.Message.Mode mode)
    {
        final StringBuilder stringBuilder = new StringBuilder("<p>");

        switch(mode)
        {
        case Mine:
            stringBuilder.append("<span style=\"color:#");
            stringBuilder.append(Utils.toCSSHTML(MainFrame.getInstance().getJStockOptions().getChatOwnMessageColor()));
            stringBuilder.append(";font-weight:bold\">");
            stringBuilder.append(who);
            stringBuilder.append(": </span>");
            stringBuilder.append(buildEmotionalTag(buildHyperLinkTag(escapeHTMLEntities(msg))));
            break;

        case Other:
            stringBuilder.append("<span style=\"color:#");
            stringBuilder.append(Utils.toCSSHTML(MainFrame.getInstance().getJStockOptions().getChatOtherMessageColor()));
            stringBuilder.append(";font-weight:bold\">");
            stringBuilder.append(who);
            stringBuilder.append(": </span>");
            stringBuilder.append(buildEmotionalTag(buildHyperLinkTag(escapeHTMLEntities(msg))));
            break;

        case System:
            // Get today's date
            Date date = new Date();
            Format formatter = new SimpleDateFormat("h:mm:ss a");
            stringBuilder.append("<span style=\"color:#");
            stringBuilder.append(Utils.toCSSHTML(MainFrame.getInstance().getJStockOptions().getChatSystemMessageColor()));
            stringBuilder.append(";font-weight: bold\">");
            stringBuilder.append('(');
            stringBuilder.append(formatter.format(date));
            stringBuilder.append(") ");
            stringBuilder.append(buildEmotionalTag(buildHyperLinkTag(escapeHTMLEntities(msg))));
            stringBuilder.append("</span>");
            break;
        }

        stringBuilder.append("</p>");

        return stringBuilder.toString();
    }

    private static String toCSSHTML(Color color) {
        return Integer.toHexString( color.getRGB() & 0x00ffffff );
    }

    public static void playSound(final Sound sound) {
        if (sounds.size() == 0) {
            for (Sound s : Sound.values()) {
                AudioInputStream stream = null;
                Clip clip = null;

                try {
                    switch (s)
                    {
                    case ALERT:
                        stream = AudioSystem.getAudioInputStream(new File(Utils.getSoundsDirectory() + "alert.wav"));
                        break;
                    case LOGIN:
                        stream = AudioSystem.getAudioInputStream(new File(Utils.getSoundsDirectory() + "login.wav"));
                        break;
                    case LOGOUT:
                        stream = AudioSystem.getAudioInputStream(new File(Utils.getSoundsDirectory() + "logout.wav"));
                        break;
                    case RECEIVE:
                        stream = AudioSystem.getAudioInputStream(new File(Utils.getSoundsDirectory() + "receive.wav"));
                        break;
                    case SEND:
                        stream = AudioSystem.getAudioInputStream(new File(Utils.getSoundsDirectory() + "send.wav"));
                        break;
                    default:
                        throw new java.lang.IllegalArgumentException("Missing case " + sound);
                    }

                    // At present, ALAW and ULAW encodings must be converted
                    // to PCM_SIGNED before it can be played
                    AudioFormat format = stream.getFormat();
                    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                        format = new AudioFormat(
                                AudioFormat.Encoding.PCM_SIGNED,
                                format.getSampleRate(),
                                format.getSampleSizeInBits()*2,
                                format.getChannels(),
                                format.getFrameSize()*2,
                                format.getFrameRate(),
                                true);        // big endian
                        stream = AudioSystem.getAudioInputStream(format, stream);
                    }

                    // Create the clip
                    DataLine.Info info = new DataLine.Info(
                        Clip.class, stream.getFormat(), ((int)stream.getFrameLength()*format.getFrameSize()));
                    clip = (Clip) AudioSystem.getLine(info);

                    // This method does not return until the audio file is completely loaded
                    clip.open(stream);
                    clip.drain();
                    sounds.put(s, clip);
                } catch (MalformedURLException e) {
                    log.error(null, e);
                } catch (IOException e) {
                    log.error(null, e);
                } catch (LineUnavailableException e) {
                    log.error(null, e);
                } catch (UnsupportedAudioFileException e) {
                    log.error(null, e);
                }
                finally {
                }
            }

        }
        soundPool.execute(new Runnable() {
            @Override
            public void run() {
                Clip clip = sounds.get(sound);

                if (clip == null) {
                    return;
                }
                
                clip.stop();
                clip.flush();
                clip.setFramePosition(0);
                clip.loop(0);
                // Wait for the sound to finish.
                //while (clip.isRunning()) {
                //    try {
                //        Thread.sleep(1);
                //    } catch (InterruptedException ex) {
                //        log.error(null, ex);
                //    }
                //}
            }
        });
    }
    
    private static final Log log = LogFactory.getLog(Utils.class);
    // Sequence are important. For example, we wish to parse :(( before :(
    private static final Map<Pattern, String> icons = new LinkedHashMap<Pattern, String>();

    private static Executor soundPool = Executors.newFixedThreadPool(Utils.NUM_OF_THREADS_SOUND_POOL);
    private static final Map<Sound, Clip> sounds = new HashMap<Sound, Clip>();
    private static final int NUM_OF_THREADS_SOUND_POOL = 1;
    
    private static final Pattern URLPattern = Pattern.compile("\\b(?:http://|https://|www.|ftp://|file:/|mailto:)\\S+\\b");
}
