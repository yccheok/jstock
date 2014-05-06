/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng Cheok <yccheok@yahoo.com>
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
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.google.Utils;
import org.yccheok.jstock.gui.Pair;

/**
 *
 * @author yccheok
 */
public class GoogleCalendar {
    
    public static boolean SMS(String message) {
        if (credential == null) {
            final Pair<Credential, String> pair;
            try {
                pair = org.yccheok.jstock.google.Utils.authorizeCalendarOffline();
            } catch (Exception ex) {
                Logger.getLogger(GoogleCalendar.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            if (pair != null) {
                credential = pair.first;
            } else {
                return false;
            }
        }
        
        boolean success = false;
        if (credential.getExpiresInSeconds() <= 60) {
            try {
                if (credential.refreshToken()) {
                    success = true;
                }
            } catch (IOException ex) {
                log.error(null, ex);
            }
        } else {
            success = true;
        } 
        
        if (success == false) {
            return false;
        }
        
        Calendar service = Utils.getCalendar(credential);
        
        // Try to search, whether we are having a calendar titled "JStock".
        CalendarListEntry calendarListEntry = null;
        
        String pageToken = null;
        do {
            com.google.api.services.calendar.model.CalendarList calendarList;
            try {
                calendarList = service.calendarList().list().setPageToken(pageToken).execute();
                List<CalendarListEntry> items = calendarList.getItems();

                for (CalendarListEntry c : items) {
                    if (c.getSummary().equals("JStock")) {
                        calendarListEntry = c;
                        break;
                    }
                }
                pageToken = calendarList.getNextPageToken();
            } catch (IOException ex) {
                log.error(null, ex);
                break;
            }
        } while (pageToken != null);
                
        // Previous JStock calendar not found. Let's create a new one.
        if (calendarListEntry == null) {
            com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
            calendar.setSummary("JStock");
            try {
                calendar = service.calendars().insert(calendar).execute();
            } catch (IOException ex) {
                log.error(null, ex);
                return false;
            }

            // http://stackoverflow.com/questions/17865302/error-404-when-creating-a-calendar-with-google-calendar-api-v3-using-c-sharp-ne
            // Insert the ID of the calendar created in step 1 and insert it into the calendarList:
            calendarListEntry = new CalendarListEntry();
            calendarListEntry.setSummary("JStock");
            calendarListEntry.setId(calendar.getId());
            
            try {
                calendarListEntry = service.calendarList().insert(calendarListEntry).execute();
            } catch (IOException ex) {
                log.error(null, ex);
                return false;
            }
        }
        
        // We will make the following assumption.
        // (1) It takes less than 30 seconds, from sending SMS request to Google Calendar till
        // Google calendar process the SMS request.
        // (2) The smallest resolution for Google Calendar is minute. This means if we try to
        // set the calendar with starting time as 2:00:59, Google Calendar will process it as
        // 2:00:00.
        final java.util.Calendar cstart = java.util.Calendar.getInstance();
        long now = org.yccheok.jstock.gui.Utils.getGoogleServerTimestamp();

        if (now == 0) {
            // Unable to retrieve server time. Try our own local machine time.
            // Not accurate. But better than none.
            now = System.currentTimeMillis();
        }

        // Consider the following edge case.
        // now is 2:00:59.
        // Due to our assumption (1) and (2), we need to tell Google Calendar start
        // is 2:02. If We try to tell Google Calendar start is 2:01:59, Google Calendar
        // will interpret start time as 2:01:00 due to (2).
        java.util.Date start = new java.util.Date(now + 60000L);        
        cstart.setTime(start);

        // If we are having start time 2:01:29, leave it.
        // If we are having start time 2:01:30, leave it.
        // If we are having start time 2:01:31, we need to convert it to 2:02:00.
        // If we are having start time 2:01:59, we need to convert it to 2:02:00.
        final int second = cstart.get(java.util.Calendar.SECOND);
        if (60 - second < 30) {
            cstart.add(java.util.Calendar.SECOND, 60 - second);
            start = cstart.getTime();
        }
        
        final java.util.Date end = new java.util.Date(start.getTime() + 60000L);
        final DateTime startTime = new DateTime(start.getTime());
        final DateTime endTime = new DateTime(end.getTime());
        final EventDateTime startEventDateTime = new EventDateTime();
        startEventDateTime.setDateTime(startTime);
        final EventDateTime endEventDateTime = new EventDateTime();
        endEventDateTime.setDateTime(endTime);
        
        // Create a new event.
        Event event = new Event();
        event.setSummary(message);
        event.setStart(startEventDateTime);
        event.setEnd(endEventDateTime);
                
        // 0 minute reminder
        EventReminder reminder = new EventReminder();
        reminder.setMinutes(0);
        reminder.setMethod("sms");

        // http://stackoverflow.com/questions/23444238/set-google-calendar-reminder-migrate-from-api-v2-to-api-v3
        Event.Reminders reminders = new Event.Reminders();
        List<EventReminder> listEventReminder = new ArrayList<EventReminder>();
        listEventReminder.add(reminder);
        reminders.setUseDefault(false);
        reminders.setOverrides(listEventReminder);
        event.setReminders(reminders);
        
        try {
            event = service.events().insert(calendarListEntry.getId(), event).execute();
        } catch (IOException ex) {
            log.error(null, ex);
            return false;
        }
        
        return true;
    }
    
    private static Credential credential = null;
    private static final Log log = LogFactory.getLog(GoogleCalendar.class);
    
}
