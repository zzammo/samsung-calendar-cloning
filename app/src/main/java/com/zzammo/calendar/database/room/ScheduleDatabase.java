package com.zzammo.calendar.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zzammo.calendar.database.Schedule;

@Database(entities = {Schedule.class}, version = 1)
public abstract class ScheduleDatabase extends RoomDatabase {
    public abstract ScheduleDao scheduleDao();

    private static ScheduleDatabase instance = null;

    public static ScheduleDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ScheduleDatabase.class,
                            "schedule-database"
                    ).allowMainThreadQueries().build();
        }

        return instance;
    }
}
