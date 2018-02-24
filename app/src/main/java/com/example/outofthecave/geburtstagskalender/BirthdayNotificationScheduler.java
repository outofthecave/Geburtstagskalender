package com.example.outofthecave.geburtstagskalender;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Schedules notifications for upcoming birthdays.
 */
public class BirthdayNotificationScheduler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO show notification
        scheduleNotifications(context);
    }

    public static void scheduleNotifications(Context context) {
        Log.d("BirthdayNotifScheduler", "scheduling notifications");
        // TODO compute time when to schedule notification
        long triggerTimestamp = System.currentTimeMillis() + 5000;
        Intent notifierIntent = new Intent(context, BirthdayNotifier.class);
        PendingIntent pendingNotifierIntent = PendingIntent.getBroadcast(context, 0, notifierIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimestamp, pendingNotifierIntent);

        setAutoSchedulingOnReboot(context, true);
        Log.d("BirthdayNotifScheduler", "scheduled notifications");
    }

    /**
     * Enable or disable auto-scheduling the notifications again when the device is rebooted.
     *
     * @param context The current context.
     * @param enabled Whether to enable the automatic scheduling. If this is
     *                {@code false}, the automatic scheduling will be disabled.
     */
    public static void setAutoSchedulingOnReboot(Context context, boolean enabled) {
        ComponentName receiver = new ComponentName(context, BirthdayNotificationScheduler.class);
        PackageManager packageManager = context.getPackageManager();
        int state;
        if (enabled) {
            state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        packageManager.setComponentEnabledSetting(receiver, state, PackageManager.DONT_KILL_APP);
    }
}
