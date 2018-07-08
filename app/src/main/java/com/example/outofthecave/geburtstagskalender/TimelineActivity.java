package com.example.outofthecave.geburtstagskalender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.outofthecave.geburtstagskalender.model.Birthday;
import com.example.outofthecave.geburtstagskalender.model.YearlyRecurringBirthdayComparator;
import com.example.outofthecave.geburtstagskalender.room.AppDatabase;
import com.example.outofthecave.geburtstagskalender.room.AsyncAddBirthdayAndGetAllBirthdaysTask;
import com.example.outofthecave.geburtstagskalender.room.AsyncGetAllBirthdaysTask;
import com.example.outofthecave.geburtstagskalender.ui.TimelineRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements AsyncGetAllBirthdaysTask.Callbacks {
    public static final String EXTRA_BIRTHDAY_TO_ADD = "com.example.outofthecave.geburtstagskalender.BIRTHDAY_TO_ADD";
    private static final int ADD_BIRTHDAY_REQUEST_CODE = 1;

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
        this.recyclerViewAdapter = new TimelineRecyclerViewAdapter(Collections.<Birthday>emptyList());
        recyclerView.setAdapter(recyclerViewAdapter);

        AppDatabase database = AppDatabase.getInstance(context);
        new AsyncGetAllBirthdaysTask(context, database, this).execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddBirthdayActivity.class);
                startActivityForResult(intent, ADD_BIRTHDAY_REQUEST_CODE);
            }
        });

        BirthdayNotifier.registerNotificationChannel(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != ADD_BIRTHDAY_REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }

        AppDatabase database = AppDatabase.getInstance(this);
        Birthday birthdayToAdd = intent.getParcelableExtra(EXTRA_BIRTHDAY_TO_ADD);
        if (birthdayToAdd == null) {
            return;
        }
        new AsyncAddBirthdayAndGetAllBirthdaysTask(this, database, this).execute(birthdayToAdd);
    }

    @Override
    public void onBirthdayListLoaded(Context context, List<Birthday> birthdays) {
        // Make a shallow copy so we can sort without changing the parameter.
        birthdays = new ArrayList<>(birthdays);
        Collections.sort(birthdays, YearlyRecurringBirthdayComparator.forReferenceDateToday());

        recyclerViewAdapter.setBirthdays(birthdays);

        BirthdayNotificationScheduler.scheduleNextNotification(context, birthdays);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
