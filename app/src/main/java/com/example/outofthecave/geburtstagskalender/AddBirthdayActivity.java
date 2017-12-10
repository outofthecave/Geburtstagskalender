package com.example.outofthecave.geburtstagskalender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

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
        birthday.name = nameTextField.getText().toString();

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        birthday.day = datePicker.getDayOfMonth();
        // Returned months are zero-based.
        birthday.month = datePicker.getMonth() + 1;

        // TODO year

        intent.putExtra(TimelineActivity.EXTRA_BIRTHDAY_TO_ADD, birthday);

        startActivity(intent);
    }
}
