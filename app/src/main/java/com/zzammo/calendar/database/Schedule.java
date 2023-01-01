package com.zzammo.calendar.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity
public class Schedule implements Comparable{
    @PrimaryKey(autoGenerate = true)
    public int key;
    @Ignore
    public String id;

    public String title;
    public String location;
    public Double lat;
    public Double lng;
    public Long begin_ms;
    public Long end_ms;
    public String memo;

    public Schedule() {
    }

    public Schedule(String title, String location, Long begin_ms) {
        this.title = title;
        this.location = location;
        this.begin_ms = begin_ms;
    }

    public Schedule(String title, String location, Long begin_ms, Long end_ms) {
        this.title = title;
        this.location = location;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
    }

    public Schedule(String title, String location, Double lat, Double lng, Long begin_ms, Long end_ms, String memo) {
        this.title = title;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        this.memo = memo;
    }

    public Schedule(Map<String, Object> map) {
        key = (int) map.get("key");
        title = (String) map.get("title");
        location = (String) map.get("location");
        lat = (Double) map.get("lat");
        lng = (Double) map.get("lng");
        begin_ms = (Long) map.get("begin_ms");
        end_ms = (Long) map.get("end_ms");
        memo = (String) map.get("memo");
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

    public Long getBegin_ms() {
        return begin_ms;
    }

    public Long getEnd_ms() {
        return end_ms;
    }

    public String getMemo() {
        return memo;
    }

    @Override
    public int compareTo(Object o) {
        return this.begin_ms.compareTo(((Schedule)o).begin_ms);
    }

    @NonNull
    @Override
    public String toString() {
        String string = "key : "+key+", id : "+id+
                ", title : "+title+
                ", location : "+location+
                "\n, Lat : "+ lat +", Lng : "+ lng +
                ", begin_ms : "+ begin_ms +
                ", end_ms : "+ end_ms +
                "\n, memo : "+memo;
        return string;
    }

}
