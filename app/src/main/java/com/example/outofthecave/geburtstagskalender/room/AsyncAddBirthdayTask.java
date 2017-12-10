package com.example.outofthecave.geburtstagskalender.room;

import android.os.AsyncTask;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

public final class AsyncAddBirthdayTask extends AsyncTask<Birthday, Void, Void> {
    private final AppDatabase database;

    public AsyncAddBirthdayTask(AppDatabase database) {
        this.database = database;
    }

    @Override
    protected Void doInBackground(Birthday... birthdays) {
        BirthdayDao birthdayDao = database.birthdayDao();
        for (Birthday birthday : birthdays) {
            birthdayDao.add(birthday);
        }
        return null;
    }
}
