package com.outofthecave.geburtstagskalender.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.outofthecave.geburtstagskalender.model.Birthday;

import java.util.List;

@Dao
public interface BirthdayDao {
    @Query("SELECT * FROM Birthday")
    List<Birthday> getAll();

    @Query("SELECT * FROM Birthday WHERE day = :day AND month = :month")
    List<Birthday> getByDayMonth(int day, int month);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(Birthday birthday);

    @Delete
    void delete(Birthday birthday);
}
