package com.outofthecave.geburtstagskalender;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.outofthecave.geburtstagskalender.model.Birthday;
import com.outofthecave.geburtstagskalender.model.CalendarUtil;

import java.util.Calendar;

public class AddEditDeleteBirthdayActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    @Nullable
    private Birthday birthdayToReplace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);

        Intent intent = getIntent();
        this.birthdayToReplace = intent.getParcelableExtra(TimelineActivity.EXTRA_BIRTHDAY_TO_REPLACE);

        DatePicker datePicker = findViewById(R.id.datePicker);
        View yearPicker = getYearPicker(datePicker);
        if (birthdayToReplace != null) {
            EditText nameTextField = findViewById(R.id.nameTextField);
            nameTextField.setText(birthdayToReplace.name);

            Integer year = birthdayToReplace.year;
            if (year == null) {
                Calendar now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                yearPicker.setVisibility(View.GONE);
            }
            datePicker.updateDate(year, CalendarUtil.getMonthForCalendar(birthdayToReplace), birthdayToReplace.day);

            SwitchCompat doSaveYear = findViewById(R.id.doSaveYear);
            doSaveYear.setChecked(birthdayToReplace.year != null);

        } else {
            yearPicker.setVisibility(View.GONE);

            Button deleteButton = findViewById(R.id.deleteBirthdayButton);
            deleteButton.setVisibility(View.GONE);
        }
    }

    public void onDoSaveYearClick(View view) {
        SwitchCompat doSaveYear = findViewById(R.id.doSaveYear);
        DatePicker datePicker = findViewById(R.id.datePicker);
        View yearPicker = getYearPicker(datePicker);
        if (doSaveYear.isChecked()) {
            yearPicker.setVisibility(View.VISIBLE);
        } else {
            yearPicker.setVisibility(View.GONE);
        }
    }

    private View getYearPicker(DatePicker datePicker) {
        return datePicker.findViewById(getResources().getIdentifier("year", "id", "android"));
    }

    public void onSaveBirthdayButtonClick(View view) {
        onButtonClickImpl(view, true);
    }

    public void onDeleteBirthdayButtonClick(View view) {
        onButtonClickImpl(view, false);
    }

    private void onButtonClickImpl(View view, boolean doAddNewBirthday) {
        Intent intent = new Intent(this, TimelineActivity.class);

        if (birthdayToReplace != null) {
            intent.putExtra(TimelineActivity.EXTRA_BIRTHDAY_TO_REPLACE, birthdayToReplace);
        }

        if (doAddNewBirthday) {
            Birthday birthday = new Birthday();

            EditText nameTextField = findViewById(R.id.nameTextField);
            birthday.name = nameTextField.getText().toString().trim();

            if (birthday.name.isEmpty()) {
                remindUserToEnterName();
                return;
            }

            DatePicker datePicker = findViewById(R.id.datePicker);
            birthday.day = datePicker.getDayOfMonth();
            // Returned months are zero-based.
            birthday.month = datePicker.getMonth() + 1;

            SwitchCompat doSaveYear = findViewById(R.id.doSaveYear);
            if (doSaveYear.isChecked()) {
                birthday.year = datePicker.getYear();
            }

            intent.putExtra(TimelineActivity.EXTRA_BIRTHDAY_TO_ADD, birthday);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    private void remindUserToEnterName() {
        TextView nameLabel = (TextView) findViewById(R.id.nameLabel);
        nameLabel.setText("Bitte einen Namen eingeben:");

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(0, nameLabel.getTop());

        // Flash the TextView to make the user notice that the text changed.
        Animation flashingAnimation = new AlphaAnimation(1, 0);
        flashingAnimation.setDuration(500);
        flashingAnimation.setRepeatCount(1);
        flashingAnimation.setRepeatMode(Animation.REVERSE);
        nameLabel.startAnimation(flashingAnimation);
    }
}
