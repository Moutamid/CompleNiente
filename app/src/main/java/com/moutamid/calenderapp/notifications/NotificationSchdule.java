package com.moutamid.calenderapp.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fxn.stash.Stash;
import com.moutamid.calenderapp.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class NotificationSchdule  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("event_title");
        String description = intent.getStringExtra("event_description");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendHighPriorityNotification(title, description, MainActivity.class);

    }

}
