package com.deskmanager.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String ACTION_REMINDER_START = "com.deskmanager.REMINDER_START";
    public static final String ACTION_REMINDER_END   = "com.deskmanager.REMINDER_END";
    public static final String EXTRA_ROOM  = "extra_room";
    public static final String EXTRA_DATE  = "extra_date";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String room   = intent.getStringExtra(EXTRA_ROOM);
        String date   = intent.getStringExtra(EXTRA_DATE);

        if (ACTION_REMINDER_START.equals(action)) {
            NotificationHelper.notifyReminderStart(context, room, date);
        } else if (ACTION_REMINDER_END.equals(action)) {
            NotificationHelper.notifyReminderEnd(context, room, date);
        }
    }
}
