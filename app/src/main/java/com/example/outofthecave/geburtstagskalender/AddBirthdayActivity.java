package com.example.outofthecave.geburtstagskalender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

public class AddBirthdayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
    }

    public void onAddBirthdayButtonClick(View view) {
        Intent intent = new Intent(this, TimelineActivity.class);

        Birthday birthday = new Birthday();

        EditText nameTextField = (EditText) findViewById(R.id.nameTextField);
        birthday.name = nameTextField.getText().toString().trim();

        if (birthday.name.isEmpty()) {
            remindUserToEnterName();
            return;
        }

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        birthday.day = datePicker.getDayOfMonth();
        // Returned months are zero-based.
        birthday.month = datePicker.getMonth() + 1;

        Switch doSaveYear = (Switch) findViewById(R.id.doSaveYear);
        if (doSaveYear.isChecked()) {
            birthday.year = datePicker.getYear();
        }

        intent.putExtra(TimelineActivity.EXTRA_BIRTHDAY_TO_ADD, birthday);

        startActivity(intent);
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
