package com.outofthecave.geburtstagskalender;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.model.CalendarUtil;
import com.outofthecave.geburtstagskalender.model.YearlyRecurringBirthdayComparator;
import com.outofthecave.geburtstagskalender.room.AppDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * Schedules notifications for upcoming birthdays.
 */
public class BirthdayNotificationScheduler extends BroadcastReceiver {
    /** The timestamp at which the last notification was shown to the user. */
    private static long lastTriggeredTimestamp = 0L;

    public static void setLastTriggeredTimestamp(long lastTriggeredTimestamp) {
        BirthdayNotificationScheduler.lastTriggeredTimestamp = lastTriggeredTimestamp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            scheduleNextNotification(context);
        }
    }

    public static void scheduleNextNotification(final Context context) {
        final AppDatabase database = AppDatabase.getInstance(context);
        Needle.onBackgroundThread().execute(new UiRelatedTask<List<Birthday>>() {
            @Override
            protected List<Birthday> doWork() {
                return database.birthdayDao().getAll();
            }

            @Override
            protected void thenDoUiRelatedWork(List<Birthday> birthdays) {
                scheduleNextNotification(context, birthdays);
            }
        });
    }

    public static void scheduleNextNotification(Context context, List<Birthday> birthdays) {
        if (birthdays.isEmpty()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent notifierIntent = new Intent(context, BirthdayNotifier.class);
            PendingIntent pendingNotifierIntent = getPendingNotifierIntent(context, notifierIntent);
            alarmManager.cancel(pendingNotifierIntent);
            setAutoSchedulingOnReboot(context, false);
            return;
        }

        // If we've already triggered a notification today, we don't want to trigger another one on
        // the same day, so only consider birthdays starting tomorrow.
        Calendar referenceDate = Calendar.getInstance();
        Calendar lastTriggeredDate = Calendar.getInstance();
        lastTriggeredDate.setTimeInMillis(lastTriggeredTimestamp);
        if (CalendarUtil.isSameDay(lastTriggeredDate, referenceDate)) {
            referenceDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        int referenceMonth = CalendarUtil.getOneBasedMonth(referenceDate);
        int referenceDay = referenceDate.get(Calendar.DAY_OF_MONTH);
        Comparator<Birthday> comparator = YearlyRecurringBirthdayComparator.forReferenceDate(referenceMonth, referenceDay);

        // Android only allows us to set one alarm triggering the
        // BirthdayNotifier, so we only set an alarm for the closest upcoming
        // birthdays (in case there are multiple birthdays on the same date).
        ArrayList<Birthday> upcomingBirthdays = new ArrayList<>(1);
        upcomingBirthdays.add(birthdays.get(0));
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
        scheduleNotification(context, triggerTimestamp, upcomingBirthdays);

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

    /**
     * Schedule a notification for a specific time.
     *
     * @param context The current context.
     * @param triggerTimestamp When to trigger the notification, in milliseconds since the Unix Epoch.
     * @param birthdaysToNotifyAbout The birthdays to mention in the notification.
     */
    public static void scheduleNotification(Context context, long triggerTimestamp, @Nullable ArrayList<Birthday> birthdaysToNotifyAbout) {
        Log.d("BirthdayNotifScheduler", "Scheduling a birthday notification for epoch time: " + triggerTimestamp);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notifierIntent = new Intent(context, BirthdayNotifier.class);

        notifierIntent.putParcelableArrayListExtra(BirthdayNotifier.EXTRA_BIRTHDAYS, birthdaysToNotifyAbout);
        PendingIntent pendingNotifierIntent = getPendingNotifierIntent(context, notifierIntent);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimestamp, pendingNotifierIntent);
    }
}
