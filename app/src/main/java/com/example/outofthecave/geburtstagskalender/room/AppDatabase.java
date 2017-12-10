package com.example.outofthecave.geburtstagskalender.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

@Database(entities = {Birthday.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    "geburtstagskalender-database").build();
        }
        return INSTANCE;
    }

    public abstract BirthdayDao birthdayDao();
}
