package com.zzammo.calendar.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Schedule implements Comparable{
    @PrimaryKey(autoGenerate = true)
    public int key;

    public String title;
    public String location;
    public Double Lat;
    public Double Lng;
    public Long timeMillis;
    public String memo;

    public Schedule() {
    }

    public Schedule(String title, String location, Long timeMillis) {
        this.title = title;
        this.location = location;
        this.timeMillis = timeMillis;
    }

    public Schedule(String title, String location, Double lat, Double lng, Long timeMillis, String memo) {
        this.title = title;
        this.location = location;
        Lat = lat;
        Lng = lng;
        this.timeMillis = timeMillis;
        this.memo = memo;
    }

    @Override
    public int compareTo(Object o) {
        return this.timeMillis.compareTo(((Schedule)o).timeMillis);
    }

    @NonNull
    @Override
    public String toString() {
        String string = "title : "+title+
                ", location : "+location+
                ", Lat : "+Lat+", Lng : "+Lng+
                ", timeMillis : "+timeMillis+
                ", memo : "+memo;
        return string;
    }
}
