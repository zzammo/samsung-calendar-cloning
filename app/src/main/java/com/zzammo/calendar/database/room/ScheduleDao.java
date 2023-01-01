package com.zzammo.calendar.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.google.firebase.firestore.auth.User;
import com.zzammo.calendar.database.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insertAll(Schedule... schedules);

    @Delete
    void delete(Schedule schedule);

    @Update
    public void updateSchedules(Schedule... schedules);

    @Query("SELECT * FROM schedule")
    List<Schedule> getAll();

    @Query("SELECT * FROM schedule WHERE begin_ms BETWEEN :begin AND :end")
    Schedule[] loadAllScheduleDuring(Long begin, Long end);
}
