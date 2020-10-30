package com.outofthecave.geburtstagskalender.ui;

import android.app.Application;

import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.room.AppDatabase;
import com.outofthecave.geburtstagskalender.room.BirthdayDao;

import java.util.List;

import androidx.annotation.NonNull;
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
}
