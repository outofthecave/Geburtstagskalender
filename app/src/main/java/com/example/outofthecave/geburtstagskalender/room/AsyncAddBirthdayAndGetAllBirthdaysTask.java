package com.example.outofthecave.geburtstagskalender.room;

import android.os.AsyncTask;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

import java.util.List;

public final class AsyncAddBirthdayAndGetAllBirthdaysTask extends AsyncTask<Birthday, Void, List<Birthday>> {
    private final AsyncAddBirthdayTask addBirthdayTask;
    private final AsyncGetAllBirthdaysTask getAllBirthdaysTask;

    public AsyncAddBirthdayAndGetAllBirthdaysTask(AppDatabase database, AsyncGetAllBirthdaysTask.Callbacks callbacks) {
        this.addBirthdayTask = new AsyncAddBirthdayTask(database);
        this.getAllBirthdaysTask = new AsyncGetAllBirthdaysTask(database, callbacks);
    }

    @Override
    protected List<Birthday> doInBackground(Birthday... birthdays) {
        addBirthdayTask.doInBackgroundImpl(birthdays);
        return getAllBirthdaysTask.doInBackgroundImpl();
    }

    @Override
    protected void onPostExecute(List<Birthday> birthdays) {
        getAllBirthdaysTask.onPostExecuteImpl(birthdays);
    }
}
