package com.outofthecave.geburtstagskalender.room;

import android.content.Context;

import com.outofthecave.geburtstagskalender.model.Birthday;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Birthday.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            "geburtstagskalender-database").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract BirthdayDao birthdayDao();
}
