package com.example.outofthecave.geburtstagskalender.room;

import android.os.AsyncTask;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

public final class AsyncDeleteBirthdayTask extends AsyncTask<Birthday, Void, Void> {
    private final AppDatabase database;

    public AsyncDeleteBirthdayTask(AppDatabase database) {
        this.database = database;
    }

    @Override
    protected Void doInBackground(Birthday... birthdays) {
        return doInBackgroundImpl(birthdays);
    }

    Void doInBackgroundImpl(Birthday... birthdays) {
        BirthdayDao birthdayDao = database.birthdayDao();
        for (Birthday birthday : birthdays) {
            if (isCancelled()) {
                break;
            }
            birthdayDao.delete(birthday);
        }
        return null;
    }
}