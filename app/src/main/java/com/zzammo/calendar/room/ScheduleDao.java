package com.zzammo.calendar.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insertAll(Schedule... schedules);

    @Delete
    void delete(Schedule schedule);

    @Query("SELECT * FROM schedule")
    List<Schedule> getAll();

    @Query("SELECT * FROM schedule WHERE timeMillis BETWEEN :begin AND :end")
    public Schedule[] loadAllScheduleDuring(Long begin, Long end);
}
