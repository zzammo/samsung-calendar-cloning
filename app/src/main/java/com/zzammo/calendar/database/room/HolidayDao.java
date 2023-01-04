package com.zzammo.calendar.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zzammo.calendar.database.Holiday;

import java.util.List;
@Dao
public interface HolidayDao {
    @Insert
    void insertAll(Holiday... holidays);

    @Delete
    void delete(Holiday holiday);

    @Update
    public void updateHolidays(Holiday... holidays);

    @Query("SELECT * FROM holiday")
    List<Holiday> getAll();

//    @Query("SELECT * FROM holiday WHERE date BETWEEN :begin AND :end")
//    Holiday[] loadAllHolidayDuring(Long begin, Long end);

    @Query("SELECT * FROM holiday WHERE date LIKE :keyword")
    List<Holiday> searchHolidayByDate(String keyword);

    @Query("SELECT * FROM holiday WHERE name LIKE :keyword")
    List<Holiday> searchHolidayByName(String keyword);
}
