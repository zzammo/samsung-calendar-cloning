package com.zzammo.calendar.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zzammo.calendar.database.Metadata;

@Dao
public interface MetadataDao {
    @Insert
    void insertAll(Metadata... metadata);

    @Delete
    void delete(Metadata metadata);

    @Update
    public void updateMetadata(Metadata... metadata);

    @Query("SELECT * FROM metadata WHERE name LIKE :keyword")
    Metadata getMetadata(String keyword);
}
