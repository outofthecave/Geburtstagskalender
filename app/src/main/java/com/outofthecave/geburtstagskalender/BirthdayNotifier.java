package com.outofthecave.geburtstagskalender;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import needle.Needle;
import needle.UiRelatedTask;

import android.util.Log;

import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.model.CalendarUtil;
import com.outofthecave.geburtstagskalender.room.AppDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Notifies the user of a birthday.
 */
public class BirthdayNotifier extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "birthday";

    public static final String EXTRA_BIRTHDAYS = "com.outofthecave.geburtstagskalender.EXTRA_BIRTHDAYS";

    private static final Set<Listener> LISTENERS = new LinkedHashSet<>();

    /**
     * Implementations of this interface get callbacks when certain things happen with a
     * {@code BirthdayNotifier} instance.
     */
    public interface Listener {
        /**
         * Called when the {@code BirthdayNotifier} wakes up because its alarm was triggered.
         *
         * @param birthdaysFromParcel The birthdays that were in the parcel that came with the
         *                            wake up call.
         */
        default void alarmTriggered(@Nullable List<Birthday> birthdaysFromParcel) {}

        /**
         * Called if and after the birthdays have been retrieved from the database.
         *
         * @param retrievedBirthdays The birthdays from the database.
         */
        default void afterRetrievedBirthdaysFromDatabase(@NonNull List<Birthday> retrievedBirthdays) {}

        /**
         * Called after a notification was shown (or would have been shown).
         *
         * @param context The current context.
         * @param wasNotificationShown Whether the notification was actually shown.
         * @param todaysBirthdays The birthdays mentioned in the notification.
         */
        default void afterNotification(Context context, boolean wasNotificationShown, @NonNull List<Birthday> todaysBirthdays) {}
    }

    public static void addListener(Listener listener) {
        synchronized (LISTENERS) {
            LISTENERS.add(listener);
        }
    }

    public static void removeListener(Listener listener) {
        synchronized (LISTENERS) {
            LISTENERS.remove(listener);
        }
    }

    private static List<Listener> getListeners() {
        synchronized (LISTENERS) {
            return new ArrayList<>(LISTENERS);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        List<Birthday> todaysBirthdays = intent.getParcelableArrayListExtra(EXTRA_BIRTHDAYS);
        Log.d("BirthdayNotifier", "Triggered a scheduled notification for " + (todaysBirthdays == null ? null : todaysBirthdays.size()) + " birthday(s).");
        for (Listener listener : getListeners()) {
            listener.alarmTriggered(todaysBirthdays);
        }

        if (todaysBirthdays != null) {
            showNotification(context, todaysBirthdays);
        } else {
            final AppDatabase database = AppDatabase.getInstance(context);
            Needle.onBackgroundThread().execute(new UiRelatedTask<List<Birthday>>() {
                @Override
                protected List<Birthday> doWork() {
                    Calendar now = Calendar.getInstance();
                    int currentMonth = CalendarUtil.getOneBasedMonth(now);
                    int currentDay = now.get(Calendar.DAY_OF_MONTH);
                    return database.birthdayDao().getByDayMonth(currentDay, currentMonth);
                }

                @Override
                protected void thenDoUiRelatedWork(@NonNull List<Birthday> todaysBirthdays) {
                    Log.d("BirthdayNotifier", "Retrieved " + todaysBirthdays.size() + " birthday(s).");
                    for (Listener listener : getListeners()) {
                        listener.afterRetrievedBirthdaysFromDatabase(todaysBirthdays);
                    }

                    showNotification(context, todaysBirthdays);
                }
            });
        }
    }

    private void showNotification(Context context, @NonNull List<Birthday> todaysBirthdays) {
        boolean wasNotificationShown = false;
        if (!todaysBirthdays.isEmpty()) {
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
            wasNotificationShown = true;

            Calendar now = Calendar.getInstance();
            BirthdayNotificationScheduler.setLastTriggeredTimestamp(now.getTimeInMillis());
        }

        BirthdayNotificationScheduler.scheduleNextNotification(context);

        for (Listener listener : getListeners()) {
            listener.afterNotification(context, wasNotificationShown, todaysBirthdays);
        }
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
