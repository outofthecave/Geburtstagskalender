package com.outofthecave.geburtstagskalender;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.model.CalendarUtil;
import com.outofthecave.geburtstagskalender.room.AppDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import needle.Needle;
import needle.UiRelatedTask;

public class SettingsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 2;

    public static boolean isTestNotificationScheduled = false;

    private final BirthdayNotifier.Listener listener = new BirthdayNotifier.Listener() {
        @Override
        public void alarmTriggered(@Nullable List<Birthday> birthdaysFromParcel) {
            TextView log = (TextView) SettingsActivity.this.findViewById(R.id.testNotificationLogTextView);
            log.append("\nAndroid hat uns an etwas erinnert. Was war das noch?");
            if (birthdaysFromParcel != null) {
                log.append(String.format("\nJa, genau: %s Geburtstag(e).", birthdaysFromParcel.size()));
            } else {
                log.append("\nSteht nichts drin, also müssen wir selber nachschlagen...");
            }
        }

        @Override
        public void afterRetrievedBirthdaysFromDatabase(@NonNull List<Birthday> retrievedBirthdays) {
            TextView log = (TextView) SettingsActivity.this.findViewById(R.id.testNotificationLogTextView);
            log.append(String.format("\nGeburtstagsdaten nachgeschlagen: %s Geburtstag(e) gefunden.", retrievedBirthdays.size()));
        }

        @Override
        public void afterNotification(Context context, boolean wasNotificationShown, @NonNull List<Birthday> todaysBirthdays) {
            isTestNotificationScheduled = false;

            TextView log = (TextView) SettingsActivity.this.findViewById(R.id.testNotificationLogTextView);
            if (wasNotificationShown) {
                log.append(String.format("\nBenachrichtigung müsste jetzt angezeigt worden sein für %s Geburtstag(e).", todaysBirthdays.size()));
            } else {
                log.append(String.format("\nBenachrichtigung wäre jetzt angezeigt worden für %s Geburtstag(e).", todaysBirthdays.size()));
            }
        }
    };

    private static class DatabaseCleanUpListener implements BirthdayNotifier.Listener {
        private final Birthday fakeBirthdayInDatabase;

        DatabaseCleanUpListener(Birthday fakeBirthdayInDatabase) {
            this.fakeBirthdayInDatabase = fakeBirthdayInDatabase;
        }

        @Override
        public void afterNotification(Context context, boolean wasNotificationShown, @NonNull List<Birthday> todaysBirthdays) {
            final AppDatabase database = AppDatabase.getInstance(context);
            Needle.onBackgroundThread().execute(new Runnable() {
                @Override
                public void run() {
                    database.birthdayDao().delete(fakeBirthdayInDatabase);
                    BirthdayNotifier.removeListener(DatabaseCleanUpListener.this);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();

        BirthdayNotifier.addListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        BirthdayNotifier.removeListener(listener);
    }

    public void onTestNotificationButtonClick(View view) {
        TextView log = (TextView) findViewById(R.id.testNotificationLogTextView);
        log.setText("Test hat begonnen.");

        Calendar now = Calendar.getInstance();
        final long triggerTimestamp = now.getTimeInMillis();

        log.append("\nBeispiel-Geburtstag zum testen wird erstellt und gespeichert. (Wird später auch wieder gelöscht.)");
        final Birthday fakeBirthday = createFakeBirthday(now);
        final ArrayList<Birthday> fakeBirthdays = new ArrayList<>(1);
        fakeBirthdays.add(fakeBirthday);

        final Context context = this;
        final AppDatabase database = AppDatabase.getInstance(context);
        Needle.onBackgroundThread().execute(new UiRelatedTask<Void>() {
            @Override
            protected Void doWork() {
                database.birthdayDao().add(fakeBirthday);
                BirthdayNotifier.addListener(new DatabaseCleanUpListener(fakeBirthday));
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(Void unused) {
                isTestNotificationScheduled = true;
                BirthdayNotificationScheduler.scheduleNotification(context, triggerTimestamp, fakeBirthdays);
                log.append("\nErinnerung wurde bei Android beantragt - und zwar sofort.");
            }
        });
    }

    private Birthday createFakeBirthday(Calendar calendar) {
        Birthday fakeBirthday = new Birthday();
        fakeBirthday.name = "Benachrichtigungstest";
        fakeBirthday.day = calendar.get(Calendar.DAY_OF_MONTH);
        fakeBirthday.month = CalendarUtil.getOneBasedMonth(calendar);
        return fakeBirthday;
    }
}
