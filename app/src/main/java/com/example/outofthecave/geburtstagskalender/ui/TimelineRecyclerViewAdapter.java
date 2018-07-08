package com.example.outofthecave.geburtstagskalender.ui;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.outofthecave.geburtstagskalender.R;
import com.example.outofthecave.geburtstagskalender.model.Birthday;
import com.example.outofthecave.geburtstagskalender.model.YearlyRecurringBirthdayComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TimelineRecyclerViewAdapter extends RecyclerView.Adapter<TimelineRecyclerViewAdapter.ViewHolder> {
    private List<Birthday> birthdays;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ConstraintLayout layout;

        ViewHolder(ConstraintLayout layout) {
            super(layout);
            this.layout = layout;
        }
    }

    public TimelineRecyclerViewAdapter(List<Birthday> birthdays) {
        this.birthdays = birthdays;
    }

    public TimelineRecyclerViewAdapter setBirthdays(List<Birthday> birthdays) {
        this.birthdays = birthdays;
        // TODO Update only the items that actually changed, see https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter
        notifyDataSetChanged();
        return this;
    }

    @Override
    public TimelineRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Birthday birthday = birthdays.get(position);

        String yearString = "";
        if (birthday.year != null) {
            yearString = birthday.year.toString();
        }
        String text = String.format(Locale.ROOT, "%d.%d.%s %s", birthday.day, birthday.month, yearString, birthday.name);

        TextView textView = holder.layout.findViewById(R.id.timelineItemTextView);
        textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return birthdays.size();
    }
}
