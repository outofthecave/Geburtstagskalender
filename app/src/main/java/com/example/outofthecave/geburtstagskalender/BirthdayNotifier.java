package com.example.outofthecave.geburtstagskalender;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Notifies the user of a birthday.
 */
public class BirthdayNotifier extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "birthday";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BirthdayNotifier", "triggering notification");

        // TODO generate title and text
        String title = "Geburtstag von Beispiel";
        String text = "Beispiel hat heute Geburtstag. Herzlichen Glückwunsch!";

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
        // TODO generate ID
        notificationManager.notify(35264751, notificationBuilder.build());

        Log.d("BirthdayNotifier", "triggered notification");
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
