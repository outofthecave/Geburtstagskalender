package com.example.outofthecave.geburtstagskalender.room;

import android.os.AsyncTask;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

import java.util.List;

public final class AsyncGetAllBirthdaysTask extends AsyncTask<Void, Void, List<Birthday>> {
    private final AppDatabase database;
    private final Callbacks callbacks;

    public AsyncGetAllBirthdaysTask(AppDatabase database, Callbacks callbacks) {
        this.database = database;
        this.callbacks = callbacks;
    }

    @Override
    protected List<Birthday> doInBackground(Void... voids) {
        return database.birthdayDao().getAll();
    }

    @Override
    protected void onPostExecute(List<Birthday> birthdays) {
        callbacks.onBirthdayListLoaded(birthdays);
    }

    public interface Callbacks {
        void onBirthdayListLoaded(List<Birthday> birthdays);
    }
}
