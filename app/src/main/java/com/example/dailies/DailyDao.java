package com.example.dailies;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DailyDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Daily daily);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Daily daily);

    @Query("DELETE FROM daily_table")
    void deleteAll();

    @Query("SELECT * FROM daily_table")
    LiveData<List<Daily>> getAllDailies();

    @Query("SELECT * FROM daily_table LIMIT 1")
    Daily[] getAnyDaily();

    @Delete
    void deleteDaily(Daily daily);
}
