package com.example.outofthecave.geburtstagskalender.room;

import android.content.Context;
import android.os.AsyncTask;

import com.example.outofthecave.geburtstagskalender.model.Birthday;
import com.example.outofthecave.geburtstagskalender.model.BirthdayUpdate;

import java.util.ArrayList;
import java.util.List;

public final class AsyncAddEditDeleteBirthdayAndGetAllBirthdaysTask extends AsyncTask<BirthdayUpdate, Void, List<Birthday>> {
    private final AsyncDeleteBirthdayTask deleteBirthdayTask;
    private final AsyncAddBirthdayTask addBirthdayTask;
    private final AsyncGetAllBirthdaysTask getAllBirthdaysTask;

    public AsyncAddEditDeleteBirthdayAndGetAllBirthdaysTask(Context context, AppDatabase database, AsyncGetAllBirthdaysTask.Callbacks callbacks) {
        this.deleteBirthdayTask = new AsyncDeleteBirthdayTask(database);
        this.addBirthdayTask = new AsyncAddBirthdayTask(database);
        this.getAllBirthdaysTask = new AsyncGetAllBirthdaysTask(context, database, callbacks);
    }

    @Override
    protected List<Birthday> doInBackground(BirthdayUpdate... updates) {
        List<Birthday> deletions = new ArrayList<>();
        List<Birthday> additions = new ArrayList<>();
        for (BirthdayUpdate update : updates) {
            if (isCancelled()) {
                return null;
            }
            if (update.birthdayToReplace != null) {
                deletions.add(update.birthdayToReplace);
            }
            if (update.birthdayToAdd != null) {
                additions.add(update.birthdayToAdd);
            }
        }

        if (!deletions.isEmpty()) {
            deleteBirthdayTask.doInBackgroundImpl(deletions.toArray(new Birthday[deletions.size()]));
            if (isCancelled()) {
                return null;
            }
        }

        if (!additions.isEmpty()) {
            addBirthdayTask.doInBackgroundImpl(additions.toArray(new Birthday[additions.size()]));
            if (isCancelled()) {
                return null;
            }
        }

        return getAllBirthdaysTask.doInBackgroundImpl();
    }

    @Override
    protected void onPostExecute(List<Birthday> birthdays) {
        getAllBirthdaysTask.onPostExecuteImpl(birthdays);
    }
}
