package com.zzammo.calendar.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity
public class Schedule implements Comparable{
    @PrimaryKey(autoGenerate = true)
    public int key;

    public String title;
    public String location;
    public Double lat;
    public Double lng;
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
        this.lat = lat;
        this.lng = lng;
        this.timeMillis = timeMillis;
        this.memo = memo;
    }

    public Schedule(Map<String, Object> map) {
        key = (int) map.get("key");
        title = (String) map.get("title");
        location = (String) map.get("location");
        lat = (Double) map.get("lat");
        lng = (Double) map.get("lng");
        timeMillis = (Long) map.get("timeMillis");
        memo = (String) map.get("memo");
    }

    public int getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Long getTimeMillis() {
        return timeMillis;
    }

    public String getMemo() {
        return memo;
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
                ", Lat : "+ lat +", Lng : "+ lng +
                ", timeMillis : "+timeMillis+
                ", memo : "+memo;
        return string;
    }

}
