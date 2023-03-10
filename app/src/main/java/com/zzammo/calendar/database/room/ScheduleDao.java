package com.zzammo.calendar.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zzammo.calendar.database.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insertAll(Schedule... schedules);

    @Delete
    void delete(Schedule schedule);

    @Update
    void updateSchedules(Schedule... schedules);

    @Query("SELECT * FROM schedule")
    List<Schedule> getAll();

    @Query("SELECT * FROM schedule WHERE begin_ms BETWEEN :begin AND :end")
    Schedule[] loadAllScheduleDuring(Long begin, Long end);

    @Query("SELECT * FROM schedule WHERE begin_ms >= :begin")
    Schedule[] loadAllScheduleStartedAt(Long begin);

    @Query("SELECT * FROM schedule WHERE serverId IS NULL")
    List<Schedule> getNotSynced();

    @Query("SELECT * FROM schedule WHERE title LIKE :keyword")
    List<Schedule> searchRecords(String keyword);
}
