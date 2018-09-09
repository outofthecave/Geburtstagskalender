package com.example.outofthecave.geburtstagskalender.room;

import android.content.Context;
import android.os.AsyncTask;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

import java.util.List;

public final class AsyncGetAllBirthdaysTask extends AsyncTask<Void, Void, List<Birthday>> {
    private final Context context;
    private final AppDatabase database;
    private final Callbacks callbacks;

    public AsyncGetAllBirthdaysTask(Context context, AppDatabase database, Callbacks callbacks) {
        this.context = context;
        this.database = database;
        this.callbacks = callbacks;
    }

    @Override
    protected List<Birthday> doInBackground(Void... voids) {
        return doInBackgroundImpl(voids);
    }

    List<Birthday> doInBackgroundImpl(Void... voids) {
        return database.birthdayDao().getAll();
    }

    @Override
    protected void onPostExecute(List<Birthday> birthdays) {
        onPostExecuteImpl(birthdays);
    }

    void onPostExecuteImpl(List<Birthday> birthdays) {
        callbacks.onBirthdayListLoaded(context, birthdays);
    }

    public interface Callbacks {
        void onBirthdayListLoaded(Context context, List<Birthday> birthdays);
    }
}
