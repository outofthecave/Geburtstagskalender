package com.example.outofthecave.geburtstagskalender;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.outofthecave.geburtstagskalender.model.Birthday;
import com.example.outofthecave.geburtstagskalender.room.AppDatabase;
import com.example.outofthecave.geburtstagskalender.room.AsyncAddBirthdayAndGetAllBirthdaysTask;
import com.example.outofthecave.geburtstagskalender.room.AsyncAddBirthdayTask;
import com.example.outofthecave.geburtstagskalender.room.AsyncGetAllBirthdaysTask;

import java.util.List;
import java.util.Locale;

public class TimelineActivity extends AppCompatActivity implements AsyncGetAllBirthdaysTask.Callbacks {
    public static final String EXTRA_BIRTHDAY_TO_ADD = "com.example.outofthecave.geburtstagskalender.BIRTHDAY_TO_ADD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase database = AppDatabase.getInstance(this);

        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Birthday birthdayToAdd = intent.getParcelableExtra(EXTRA_BIRTHDAY_TO_ADD);
        if (birthdayToAdd != null) {
            new AsyncAddBirthdayAndGetAllBirthdaysTask(database, this).execute(birthdayToAdd);
        } else {
            new AsyncGetAllBirthdaysTask(database, this).execute();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final TimelineActivity self = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(self, AddBirthdayActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBirthdayListLoaded(List<Birthday> birthdays) {
        LinearLayout birthdayList = (LinearLayout) findViewById(R.id.birthdayList);
        for (Birthday birthday : birthdays) {
            TextView birthdayLine = new TextView(this);
            String yearString = "";
            if (birthday.year != null) {
                yearString = birthday.year.toString();
            }
            birthdayLine.setText(String.format(Locale.ROOT, "%d.%d.%s %s", birthday.day, birthday.month, yearString, birthday.name));
            birthdayList.addView(birthdayLine);
        }
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
