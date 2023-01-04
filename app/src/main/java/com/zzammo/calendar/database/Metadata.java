package com.zzammo.calendar.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Metadata implements Comparable{
    @PrimaryKey(autoGenerate = true)
    public int key;
    public String name;
    public String data;

    public Metadata() {
    }

    public Metadata(String name, String data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public int compareTo(Object o) {
        //나중에 만들기
        return 0;
    }
}
