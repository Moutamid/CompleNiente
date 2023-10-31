package com.moutamid.calenderapp.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.moutamid.calenderapp.models.TaskModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Notification {
    public static void scheduleEventNotifications(Context context, List<TaskModel> eventList) {
        for (TaskModel event : eventList) {
            Date eventDate = event.getDate().getDate();
            long eventStartTime = event.getStartTime();
            Date currentTime = new Date();

            // Calculate the time difference in milliseconds
            long timeDifference = eventDate.getTime() + eventStartTime - currentTime.getTime();

            if (timeDifference > 0) {
                ArrayList<NotiModel> arrayList = Data.getData();
                // Schedule notification for the exact event time
                scheduleNotification(context, event, timeDifference, arrayList.get(0).title, arrayList.get(0).message);

                // Schedule notification 15 minutes before the event
                scheduleNotification(context, event, timeDifference - 15 * 60 * 1000, arrayList.get(1).title, arrayList.get(1).message);
            }
        }
    }

    public static void scheduleNotification(Context context, TaskModel event, long timeDifference, String title, String message) {

        Intent notificationIntent = new Intent(context, NotificationSchdule.class);
        notificationIntent.putExtra("event_title", event.getName() + title);
        notificationIntent.putExtra("event_description", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                new Random().nextInt(100), // Use a unique ID for each event to avoid overwriting previous alarms
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long notificationTime = System.currentTimeMillis() + timeDifference;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

}
