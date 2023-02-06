package com.zzammo.calendar.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zzammo.calendar.database.Metadata;

@Database(entities = {Metadata.class}, version = 1)
public abstract class MetadataDatabase extends RoomDatabase {
    public abstract MetadataDao metadataDao();

    private static MetadataDatabase instance = null;

    public static MetadataDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MetadataDatabase.class,
                    "metadata-database"
            ).allowMainThreadQueries().build();
        }

        return instance;
    }
}
