package com.deskmanager.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.deskmanager.app.data.entities.ReservationEntity;

public class ReminderScheduler {

    private static final long DAY_MS = 24 * 60 * 60 * 1000L;

    public static void schedule(Context ctx, ReservationEntity reservation, String roomNumber) {
        long startReminder = reservation.getStartDate() - DAY_MS;
        long endReminder   = reservation.getEndDate()   - DAY_MS;
        long now = System.currentTimeMillis();

        if (startReminder > now) {
            arm(ctx, reservation.getId() * 10,
                    ReminderReceiver.ACTION_REMINDER_START,
                    roomNumber,
                    DateUtils.format(reservation.getStartDate()),
                    startReminder);
        }
        if (endReminder > now) {
            arm(ctx, reservation.getId() * 10 + 1,
                    ReminderReceiver.ACTION_REMINDER_END,
                    roomNumber,
                    DateUtils.format(reservation.getEndDate()),
                    endReminder);
        }
    }

    public static void cancel(Context ctx, int reservationId) {
        cancel(ctx, reservationId * 10,     ReminderReceiver.ACTION_REMINDER_START);
        cancel(ctx, reservationId * 10 + 1, ReminderReceiver.ACTION_REMINDER_END);
    }

    private static void arm(Context ctx, int requestCode, String action,
                            String room, String date, long triggerAtMs) {
        Intent intent = new Intent(ctx, ReminderReceiver.class);
        intent.setAction(action);
        intent.putExtra(ReminderReceiver.EXTRA_ROOM, room);
        intent.putExtra(ReminderReceiver.EXTRA_DATE, date);

        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pi = PendingIntent.getBroadcast(ctx, requestCode, intent, flags);
        AlarmManager am  = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMs, pi);
        }
    }

    private static void cancel(Context ctx, int requestCode, String action) {
        Intent intent = new Intent(ctx, ReminderReceiver.class);
        intent.setAction(action);
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE
                : PendingIntent.FLAG_NO_CREATE;
        PendingIntent pi = PendingIntent.getBroadcast(ctx, requestCode, intent, flags);
        if (pi == null) return;
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(pi);
    }
}
