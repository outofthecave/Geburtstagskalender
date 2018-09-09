package com.example.outofthecave.geburtstagskalender.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

import java.util.List;

@Dao
public interface BirthdayDao {
    @Query("SELECT * FROM Birthday")
    List<Birthday> getAll();

    @Insert
    void add(Birthday birthday);

    @Delete
    void delete(Birthday birthday);
}
