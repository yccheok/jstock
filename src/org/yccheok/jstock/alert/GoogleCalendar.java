/*
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
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.alert;

import org.yccheok.jstock.gui.*;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.Reminder.Method;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class GoogleCalendar {
    public static boolean SMS(String username, String password, String message) {
        final CalendarService calendarService = new CalendarService("JStock");
        try {
            calendarService.setUserCredentials(username, password);
        }
        catch (com.google.gdata.util.AuthenticationException exp) {
            log.error(null, exp);
            return false;
        }

        // Try to search, whether we are having a calendar titled "JStock".
        CalendarEntry calendarEntry = null;
        CalendarFeed calendarFeed;
        try {
            calendarFeed = calendarService.getFeed(feedUrl, CalendarFeed.class);
        } catch (IOException exp) {
            log.error(null, exp);
            return false;
        } catch (ServiceException exp) {
            log.error(null, exp);
            return false;
        }

        final List<CalendarEntry> calendarEntries = calendarFeed.getEntries();
        for (CalendarEntry ce : calendarEntries) {
            final String title = ce.getTitle().getPlainText();
            if (title.equalsIgnoreCase("JStock")) {
                calendarEntry = ce;
                break;
            }
        }

        // Previous JStock calendar not found. Let's create a new one.
        if (calendarEntry == null) {
            // Create the calendar
            calendarEntry = new CalendarEntry();
            calendarEntry.setTitle(new PlainTextConstruct("JStock"));
            calendarEntry.setSummary(new PlainTextConstruct("For JStock SMS Alert Usage."));

            // Insert the calendar
            try {
                calendarEntry = calendarService.insert(feedUrl, calendarEntry);
            } catch (IOException exp) {
                log.error(null, exp);
                return false;
            } catch (ServiceException exp) {
                log.error(null, exp);
                return false;
            }
        }   // if (calendarEntry == null)

        String postUrlString = calendarEntry.getLink("alternate", "application/atom+xml").getHref();
        URL postUrl;
        try {
            postUrl = new URL(postUrlString);
        } catch (MalformedURLException exp) {
            log.error(null, exp);
            return false;
        }

        // We will make the following assumption.
        // (1) It takes less than 30 seconds, from sending SMS request to Google Calendar till
        // Google calendar process the SMS request.
        // (2) The smallest resolution for Google Calendar is minute. This means if we try to
        // set the calendar with starting time as 2:00:59, Google Calendar will process it as
        // 2:00:00.
        final Calendar cstart = Calendar.getInstance();
        Date now = Utils.getNTPDate();
        if (now == null) {
            // Unable to retrieve server time. Try our own local machine time.
            // Not accurate. But better than none.
            now = new Date();
        }

        // Consider the following edge case.
        // now is 2:00:59.
        // Due to our assumption (1) and (2), we need to tell Google Calendar start
        // is 2:02. If We try to tell Google Calendar start is 2:01:59, Google Calendar
        // will interpret start time as 2:01:00 due to (2).
        Date start = new Date(now.getTime() + 60000L);        
        cstart.setTime(start);

        // If we are having start time 2:01:29, leave it.
        // If we are having start time 2:01:30, leave it.
        // If we are having start time 2:01:31, we need to convert it to 2:02:00.
        // If we are having start time 2:01:59, we need to convert it to 2:02:00.
        final int second = cstart.get(Calendar.SECOND);
        if (60 - second < 30) {
            cstart.add(Calendar.SECOND, 60 - second);
            start = cstart.getTime();
        }
        final Date end = new Date(start.getTime() + 60000L);
        final DateTime startTime = new DateTime(start.getTime());
        final DateTime endTime = new DateTime(end.getTime());
        final When when = new When();
        when.setStartTime(startTime);
        when.setEndTime(endTime);

        CalendarEventEntry calendarEventEntry = new CalendarEventEntry();
        calendarEventEntry.setTitle(new PlainTextConstruct(message));
        calendarEventEntry.setContent(new PlainTextConstruct(message));
        calendarEventEntry.addTime(when);

        // Send the request and receive the response:
        try {
            calendarEventEntry = calendarService.insert(postUrl, calendarEventEntry);
        }
        catch(IOException exp) {
            log.error(null, exp);
            return false;
        } catch (ServiceException exp) {
            log.error(null, exp);
            return false;
        }

        // 0 minute reminder
        Reminder reminder = new Reminder();
        reminder.setMinutes(0);
        reminder.setMethod(Method.SMS);
        List<Reminder> reminders = calendarEventEntry.getReminder();
        reminders.add(reminder);
        try {
            calendarEventEntry.update();
        } catch (IOException exp) {
            log.error(null, exp);
            return false;
        } catch (ServiceException exp) {
            log.error(null, exp);
            return false;
        }

        return true;
    }

    private static URL feedUrl = null;
    static {
        try {
            feedUrl = new URL("http://www.google.com/calendar/feeds/default/owncalendars/full");
        }
        catch(MalformedURLException exp) {
        }
    }

    private static final Log log = LogFactory.getLog(GoogleCalendar.class);
}
