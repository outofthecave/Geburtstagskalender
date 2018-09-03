package com.example.outofthecave.geburtstagskalender;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.outofthecave.geburtstagskalender.model.Birthday;
import com.example.outofthecave.geburtstagskalender.model.CalendarUtil;
import com.example.outofthecave.geburtstagskalender.model.YearlyRecurringBirthdayComparator;
import com.example.outofthecave.geburtstagskalender.room.AppDatabase;
import com.example.outofthecave.geburtstagskalender.room.AsyncGetAllBirthdaysTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * Schedules notifications for upcoming birthdays.
 */
public class BirthdayNotificationScheduler extends BroadcastReceiver implements AsyncGetAllBirthdaysTask.Callbacks {
    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleNextNotification(context);
    }

    public void scheduleNextNotification(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        new AsyncGetAllBirthdaysTask(context, database, this).execute();
    }

    @Override
    public void onBirthdayListLoaded(Context context, List<Birthday> birthdays) {
        scheduleNextNotification(context, birthdays);
    }

    public static void scheduleNextNotification(Context context, List<Birthday> birthdays) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notifierIntent = new Intent(context, BirthdayNotifier.class);

        if (birthdays.isEmpty()) {
            PendingIntent pendingNotifierIntent = getPendingNotifierIntent(context, notifierIntent);
            alarmManager.cancel(pendingNotifierIntent);
            setAutoSchedulingOnReboot(context, false);
            return;
        }

        // Android only allows us to set one alarm triggering the
        // BirthdayNotifier, so we only set an alarm for the closest upcoming
        // birthdays (in case there are multiple birthdays on the same date).
        ArrayList<Birthday> upcomingBirthdays = new ArrayList<>(1);
        upcomingBirthdays.add(birthdays.get(0));
        Comparator<Birthday> comparator = YearlyRecurringBirthdayComparator.forReferenceDateToday();
        for (int i = 1; i < birthdays.size(); ++i) {
            Birthday birthday = birthdays.get(i);
            int cmp = comparator.compare(birthday, upcomingBirthdays.get(0));
            if (cmp < 0) {
                upcomingBirthdays = new ArrayList<>(1);
                upcomingBirthdays.add(birthday);
            } else if (cmp == 0) {
                upcomingBirthdays.add(birthday);
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, CalendarUtil.getMonthForCalendar(upcomingBirthdays.get(0)));
        calendar.set(Calendar.DAY_OF_MONTH, upcomingBirthdays.get(0).day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.roll(Calendar.YEAR, 1);
        }
        long triggerTimestamp = calendar.getTimeInMillis();
        Log.d("BirthdayNotifScheduler", "Scheduling a birthday notification for epoch time: " + triggerTimestamp);

        notifierIntent.putParcelableArrayListExtra(BirthdayNotifier.EXTRA_BIRTHDAYS, upcomingBirthdays);
        PendingIntent pendingNotifierIntent = getPendingNotifierIntent(context, notifierIntent);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimestamp, pendingNotifierIntent);

        setAutoSchedulingOnReboot(context, true);
    }

    private static PendingIntent getPendingNotifierIntent(Context context, Intent notifierIntent) {
        return PendingIntent.getBroadcast(context, 0, notifierIntent, 0);
    }

    /**
     * Enable or disable auto-scheduling the notifications again when the device is rebooted.
     *
     * @param context The current context.
     * @param enabled Whether to enable the automatic scheduling. If this is
     *                {@code false}, the automatic scheduling will be disabled.
     */
    private static void setAutoSchedulingOnReboot(Context context, boolean enabled) {
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
