package com.outofthecave.geburtstagskalender.room;

import android.os.AsyncTask;

import com.outofthecave.geburtstagskalender.model.Birthday;

public final class AsyncAddBirthdayTask extends AsyncTask<Birthday, Void, Void> {
    private final AppDatabase database;

    public AsyncAddBirthdayTask(AppDatabase database) {
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
            birthdayDao.add(birthday);
        }
        return null;
    }
}
