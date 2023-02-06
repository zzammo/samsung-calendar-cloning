package com.zzammo.calendar.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zzammo.calendar.database.Holiday;

@Database(entities = {Holiday.class}, version = 1)
public abstract class HolidayDatabase extends RoomDatabase {
    public abstract HolidayDao holidayDao();

    private static HolidayDatabase instance = null;

    public static HolidayDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    HolidayDatabase.class,
                    "holiday-database"
            ).allowMainThreadQueries().build();
        }

        return instance;
    }
}
