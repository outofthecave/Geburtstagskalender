package com.outofthecave.geburtstagskalender.ui;

import android.app.Application;

import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.room.AppDatabase;
import com.outofthecave.geburtstagskalender.room.BirthdayDao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import needle.Needle;

public class TimelineViewModel extends AndroidViewModel {
    private final BirthdayDao birthdayDao;
    private final LiveData<List<Birthday>> birthdays;

    public TimelineViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        birthdayDao = database.birthdayDao();
        birthdays = birthdayDao.getAllLive();
    }

    public LiveData<List<Birthday>> getBirthdays() {
        return birthdays;
    }

    public void add(final Birthday birthday) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                birthdayDao.add(birthday);
            }
        });
    }

    public void delete(final Birthday birthday) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                birthdayDao.delete(birthday);
            }
        });
    }

    /**
     * Replace one birthday with another. This is different from calling {@link #delete(Birthday)}
     * and {@link #add(Birthday)} in that it guarantees that the deletion is executed before the
     * addition.
     *
     * @param birthdayToReplace The old birthday to delete.
     * @param birthdayToAdd The new birthday to add.
     */
    public void replace(@Nullable final Birthday birthdayToReplace, @Nullable final Birthday birthdayToAdd) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                if (birthdayToReplace != null) {
                    birthdayDao.delete(birthdayToReplace);
                }
                if (birthdayToAdd != null) {
                    birthdayDao.add(birthdayToAdd);
                }
            }
        });
    }
}
