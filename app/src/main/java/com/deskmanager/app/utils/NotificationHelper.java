package com.deskmanager.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.deskmanager.app.R;
import com.deskmanager.app.presentation.main.MainActivity;

public class NotificationHelper {

    public static final String CHANNEL_RESERVATIONS = "channel_reservations";
    public static final String CHANNEL_ADMIN        = "channel_admin";
    public static final String CHANNEL_REMINDERS    = "channel_reminders";

    private static final int ID_WELCOME        = 1001;
    private static final int ID_ADMIN_CANCEL   = 1002;
    private static final int ID_REMINDER_START = 1003;
    private static final int ID_REMINDER_END   = 1004;
    private static final int ID_NEW_DESK       = 1005;
    private static final int ID_RESERVATION_OK = 1006;
    private static final int ID_CANCEL_BASE    = 2000;
    private static final int ID_TEST           = 9999;

    public static void createChannels(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager nm = ctx.getSystemService(NotificationManager.class);

        nm.createNotificationChannel(new NotificationChannel(
                CHANNEL_RESERVATIONS,
                ctx.getString(R.string.notif_channel_reservations),
                NotificationManager.IMPORTANCE_DEFAULT));

        nm.createNotificationChannel(new NotificationChannel(
                CHANNEL_ADMIN,
                ctx.getString(R.string.notif_channel_admin),
                NotificationManager.IMPORTANCE_HIGH));

        nm.createNotificationChannel(new NotificationChannel(
                CHANNEL_REMINDERS,
                ctx.getString(R.string.notif_channel_reminders),
                NotificationManager.IMPORTANCE_DEFAULT));
    }

    public static void notifyWelcome(Context ctx, String name, int activeCount) {
        String title = ctx.getString(R.string.notif_welcome_title, name);
        String body  = activeCount > 0
                ? ctx.getString(R.string.notif_welcome_body_reservations, activeCount)
                : ctx.getString(R.string.notif_welcome_body_none);
        send(ctx, CHANNEL_RESERVATIONS, ID_WELCOME, title, body);
    }

    public static void notifyReservationConfirmed(Context ctx, String room, String startDate) {
        String title = ctx.getString(R.string.notif_reservation_confirmed_title);
        String body  = ctx.getString(R.string.notif_reservation_confirmed_body, room, startDate);
        send(ctx, CHANNEL_RESERVATIONS, ID_RESERVATION_OK, title, body);
    }

    public static void notifyAdminCancelled(Context ctx, String room, int notifId) {
        String title = ctx.getString(R.string.notif_admin_cancel_title);
        String body  = ctx.getString(R.string.notif_admin_cancel_body, room);
        send(ctx, CHANNEL_ADMIN, ID_CANCEL_BASE + notifId, title, body);
    }

    public static void notifyReminderStart(Context ctx, String room, String date) {
        String title = ctx.getString(R.string.notif_reminder_start_title);
        String body  = ctx.getString(R.string.notif_reminder_start_body, room, date);
        send(ctx, CHANNEL_REMINDERS, ID_REMINDER_START, title, body);
    }

    public static void notifyReminderEnd(Context ctx, String room, String date) {
        String title = ctx.getString(R.string.notif_reminder_end_title);
        String body  = ctx.getString(R.string.notif_reminder_end_body, room, date);
        send(ctx, CHANNEL_REMINDERS, ID_REMINDER_END, title, body);
    }

    public static void notifyNewDesk(Context ctx, String room, int floor) {
        String title = ctx.getString(R.string.notif_new_desk_title);
        String body  = ctx.getString(R.string.notif_new_desk_body, room, floor);
        send(ctx, CHANNEL_ADMIN, ID_NEW_DESK, title, body);
    }

    public static void notifyTest(Context ctx) {
        String title = ctx.getString(R.string.notif_test_title);
        String body  = ctx.getString(R.string.notif_test_body);
        send(ctx, CHANNEL_RESERVATIONS, ID_TEST,     title, body);
        send(ctx, CHANNEL_ADMIN,        ID_TEST + 1, ctx.getString(R.string.notif_admin_cancel_title),
                ctx.getString(R.string.notif_admin_cancel_body, "A-101"));
        send(ctx, CHANNEL_REMINDERS,    ID_TEST + 2, ctx.getString(R.string.notif_reminder_start_title),
                ctx.getString(R.string.notif_reminder_start_body, "B-202", "02.05.2026"));
    }

    private static void send(Context ctx, String channel, int id, String title, String body) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pi = PendingIntent.getActivity(ctx, id, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channel)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi);

        try {
            NotificationManagerCompat.from(ctx).notify(id, builder.build());
        } catch (SecurityException ignored) {
        }
    }
}
