package com.zzammo.calendar.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Holiday implements Comparable{
    @PrimaryKey(autoGenerate = true)
    public int key;
    public String date;
    public String name;

    public Holiday() {
    }

    public Holiday(String date, String name) {
        this.date = date;
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        //나중에 만들기
        return 0;
    }
}
