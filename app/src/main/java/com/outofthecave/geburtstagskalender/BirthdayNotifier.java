package com.outofthecave.geburtstagskalender;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.outofthecave.geburtstagskalender.model.Birthday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Notifies the user of a birthday.
 */
public class BirthdayNotifier extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "birthday";

    public static final String EXTRA_BIRTHDAYS = "com.outofthecave.geburtstagskalender.EXTRA_BIRTHDAYS";

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Birthday> todaysBirthdays = intent.getParcelableArrayListExtra(EXTRA_BIRTHDAYS);
        Log.d("BirthdayNotifier", "Triggered a scheduled notification for " + (todaysBirthdays == null ? null : todaysBirthdays.size()) + " birthdays.");
        if (todaysBirthdays != null && !todaysBirthdays.isEmpty()) {
            String joinedNames = joinNames(todaysBirthdays, "und", false);
            String title = String.format("Geburtstag von %s", joinedNames);

            String have;
            if (todaysBirthdays.size() == 1) {
                have = "hat";
            } else {
                have = "haben";
            }
            String text = String.format("%s %s heute Geburtstag. Herzlichen Glückwunsch!", joinedNames, have);

            Intent notificationTapIntent = new Intent(context, TimelineActivity.class);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_cake)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(text))
                    .setContentIntent(PendingIntent.getActivity(context, 0, notificationTapIntent, 0))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, notificationBuilder.build());

            Calendar now = Calendar.getInstance();
            BirthdayNotificationScheduler.setLastTriggeredTimestamp(now.getTimeInMillis());
        }

        new BirthdayNotificationScheduler().scheduleNextNotification(context);
    }

    @VisibleForTesting
    static String joinNames(List<Birthday> birthdays, String conjunction, boolean useOxfordComma) {
        if (birthdays.size() == 1) {
            return birthdays.get(0).name;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < birthdays.size(); ++i) {
            if (i == birthdays.size() - 1) {
                if (useOxfordComma && birthdays.size() > 2) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(conjunction);
                sb.append(" ");
            } else if (i != 0) {
                sb.append(", ");
            }
            sb.append(birthdays.get(i).name);
        }
        return sb.toString();
    }

    public static void registerNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Geburtstag";
            String description = "Benachrichtigung, wenn jemand Geburtstag hat";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
