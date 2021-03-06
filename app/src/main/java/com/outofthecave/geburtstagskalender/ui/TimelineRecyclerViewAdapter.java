package com.outofthecave.geburtstagskalender.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import needle.Needle;
import needle.UiRelatedTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.outofthecave.geburtstagskalender.AddEditDeleteBirthdayActivity;
import com.outofthecave.geburtstagskalender.R;
import com.outofthecave.geburtstagskalender.TimelineActivity;
import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.room.AppDatabase;
import com.outofthecave.geburtstagskalender.room.BirthdayDao;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TimelineRecyclerViewAdapter extends RecyclerView.Adapter<TimelineRecyclerViewAdapter.ViewHolder> {
    private final TimelineActivity activity;
    private List<Birthday> birthdays;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ConstraintLayout layout;

        ViewHolder(ConstraintLayout layout) {
            super(layout);
            this.layout = layout;
        }
    }

    public TimelineRecyclerViewAdapter(TimelineActivity activity) {
        this.activity = activity;
        this.birthdays = Collections.emptyList();
    }

    public TimelineRecyclerViewAdapter setBirthdays(List<Birthday> birthdays) {
        this.birthdays = birthdays;
        // TODO Update only the items that actually changed, see https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter
        notifyDataSetChanged();
        return this;
    }

    @NonNull
    @Override
    public TimelineRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Birthday birthday = birthdays.get(position);

        final String yearString;
        if (birthday.year != null) {
            yearString = birthday.year.toString();
        } else {
            yearString = "";
        }
        String text = String.format(Locale.ROOT, "%d.%d.%s %s", birthday.day, birthday.month, yearString, birthday.name);

        TextView textView = holder.layout.findViewById(R.id.timelineItemTextView);
        textView.setText(text);

        ImageButton editButton = holder.layout.findViewById(R.id.timelineItemEditButton);
        editButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AddEditDeleteBirthdayActivity.class);
                intent.putExtra(TimelineActivity.EXTRA_BIRTHDAY_TO_REPLACE, birthday);
                activity.startActivityForResult(intent, AddEditDeleteBirthdayActivity.REQUEST_CODE);
            }
        });

        ImageButton deleteButton = holder.layout.findViewById(R.id.timelineItemDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                String msg = String.format(Locale.ROOT, "Geburtstag von %s am %d.%d.%s wirklich löschen?", birthday.name, birthday.day, birthday.month, yearString);
                new AlertDialog.Builder(activity)
                        .setMessage(msg)
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AppDatabase database = AppDatabase.getInstance(activity);
                                Needle.onBackgroundThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        BirthdayDao birthdayDao = database.birthdayDao();
                                        birthdayDao.delete(birthday);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Behalten", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return birthdays.size();
    }
}
