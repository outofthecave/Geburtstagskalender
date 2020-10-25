package com.outofthecave.geburtstagskalender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.model.BirthdayUpdate;
import com.outofthecave.geburtstagskalender.model.YearlyRecurringBirthdayComparator;
import com.outofthecave.geburtstagskalender.room.AppDatabase;
import com.outofthecave.geburtstagskalender.room.AsyncAddEditDeleteBirthdayAndGetAllBirthdaysTask;
import com.outofthecave.geburtstagskalender.room.AsyncGetAllBirthdaysTask;
import com.outofthecave.geburtstagskalender.ui.TimelineRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements AsyncGetAllBirthdaysTask.Callbacks {
    public static final String EXTRA_BIRTHDAY_TO_ADD = "com.outofthecave.geburtstagskalender.BIRTHDAY_TO_ADD";
    public static final String EXTRA_BIRTHDAY_TO_REPLACE = "com.outofthecave.geburtstagskalender.BIRTHDAY_TO_REPLACE";

    private TimelineRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;

        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.birthdayRecycler);
        // Improve performance because changes in content do not change the layout size of the RecyclerView.
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // The list will be filled once we get the data from the database.
        this.recyclerViewAdapter = new TimelineRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        AppDatabase database = AppDatabase.getInstance(context);
        new AsyncGetAllBirthdaysTask(context, database, this).execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddEditDeleteBirthdayActivity.class);
                startActivityForResult(intent, AddEditDeleteBirthdayActivity.REQUEST_CODE);
            }
        });

        BirthdayNotifier.registerNotificationChannel(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != AddEditDeleteBirthdayActivity.REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }

        Birthday birthdayToReplace = intent.getParcelableExtra(EXTRA_BIRTHDAY_TO_REPLACE);
        Birthday birthdayToAdd = intent.getParcelableExtra(EXTRA_BIRTHDAY_TO_ADD);
        if (birthdayToReplace == null && birthdayToAdd == null) {
            return;
        }

        BirthdayUpdate update = new BirthdayUpdate();
        update.birthdayToReplace = birthdayToReplace;
        update.birthdayToAdd = birthdayToAdd;

        AppDatabase database = AppDatabase.getInstance(this);
        new AsyncAddEditDeleteBirthdayAndGetAllBirthdaysTask(this, database, this).execute(update);
    }

    @Override
    public void onBirthdayListLoaded(Context context, List<Birthday> birthdays) {
        // Make a shallow copy so we can sort without changing the parameter.
        birthdays = new ArrayList<>(birthdays);
        Collections.sort(birthdays, YearlyRecurringBirthdayComparator.forReferenceDateToday());

        recyclerViewAdapter.setBirthdays(birthdays);

        BirthdayNotificationScheduler.scheduleNextNotification(context, birthdays);
    }
}
