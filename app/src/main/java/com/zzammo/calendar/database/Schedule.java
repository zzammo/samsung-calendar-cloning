package com.zzammo.calendar.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity
public class Schedule implements Comparable {
    @PrimaryKey(autoGenerate = true)
    public int key;

    public static final int PUBLIC = 0;
    public static final int WALK = 1;
    public static final int CAR = 2;

    public String serverId;

    public String title;
    public boolean isAllDay;
    public boolean departAlarm;

    public String begin_loc;
    public Double begin_lat;
    public Double begin_lng;

    public String end_loc;
    public Double end_lat;

    public int need_hour;
    public int need_minute;
    public int need_second;

    public int means;

    public Double end_lng;

    public Long begin_ms;
    public Long end_ms;

    public String alarm;

    public String memo;

    public boolean isHoliday;

    public Schedule() {
    }

    public Schedule(String title, String begin_loc, Long begin_ms) {
        this.title = title;
        this.begin_loc = begin_loc;
        this.begin_ms = begin_ms;
        isHoliday = false;
    }

    public Schedule(String title, String begin_loc, Long begin_ms, Long end_ms) {
        this.title = title;
        this.begin_loc = begin_loc;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        isHoliday = false;
    }
    public Schedule(String title, String begin_loc, String end_loc, Long begin_ms, Long end_ms) {
        this.title = title;
        this.begin_loc = begin_loc;
        this.end_loc = end_loc;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        isHoliday = false;
    }
    public Schedule(String title, String begin_loc, String end_loc, Long begin_ms, Long end_ms, String memo) {
        this.title = title;
        this.begin_loc = begin_loc;
        this.end_loc = end_loc;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        this.memo = memo;
        isHoliday = false;
    }

    public Schedule(String title,
                    String begin_loc, Double begin_lat, Double begin_lng,
                    String end_loc, Double end_lat, Double end_lng,
                    Long begin_ms, Long end_ms,
                    String memo) {
        this.title = title;
        this.begin_loc = begin_loc;
        this.begin_lat = begin_lat;
        this.begin_lng = begin_lng;
        this.end_loc = end_loc;
        this.end_lat = end_lat;
        this.end_lng = end_lng;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        this.memo = memo;
        isHoliday = false;
    }

    public Schedule(String title, boolean isAllDay, boolean departAlarm, Long begin_ms, Long end_ms, String alarm, String memo) {
        this.title = title;
        this.isAllDay = isAllDay;
        this.departAlarm = departAlarm;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        this.alarm = alarm;
        this.memo = memo;
        isHoliday = false;
    }

    public Schedule(String title, boolean isAllDay, boolean departAlarm, String begin_loc, Double begin_lat, Double begin_lng, String end_loc, Double end_lat, Double end_lng, int need_hour, int need_minute, int need_second, int means, Long begin_ms, Long end_ms, String alarm, String memo) {
        this.title = title;
        this.isAllDay = isAllDay;
        this.departAlarm = departAlarm;
        this.begin_loc = begin_loc;
        this.begin_lat = begin_lat;
        this.begin_lng = begin_lng;
        this.end_loc = end_loc;
        this.end_lat = end_lat;
        this.end_lng = end_lng;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        this.alarm = alarm;
        this.memo = memo;
        this.need_hour = need_hour;
        this.need_minute = need_minute;
        this.need_second = need_second;
        this.means = means;
        isHoliday = false;
    }

    public Schedule(String title, boolean isAllDay, Long begin_ms, Long end_ms, boolean isHoliday) {
        this.title = title;
        this.isAllDay = isAllDay;
        this.begin_ms = begin_ms;
        this.end_ms = end_ms;
        this.isHoliday = isHoliday;
    }

    public Schedule(Map<String, Object> map) {
        key = (int) map.get("key");
        title = (String) map.get("title");

        begin_loc = (String) map.get("begin_loc");
        begin_lat = (Double) map.get("begin_lat");
        begin_lng = (Double) map.get("begin_lng");

        end_loc = (String) map.get("end_loc");
        end_lat = (Double) map.get("end_lat");
        end_lng = (Double) map.get("end_lng");

        begin_ms = (Long) map.get("begin_ms");
        end_ms = (Long) map.get("end_ms");

        memo = (String) map.get("memo");
        isHoliday = false;
    }

    public String getTitle() {
        return title;
    }

    public String getBegin_loc() {
        return begin_loc;
    }

    public Double getBegin_lat() {
        return begin_lat;
    }

    public Double getBegin_lng() {
        return begin_lng;
    }

    public String getEnd_loc() {
        return end_loc;
    }

    public Double getEnd_lat() {
        return end_lat;
    }

    public Double getEnd_lng() {
        return end_lng;
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

    public boolean isAllDay() {
        return isAllDay;
    }

    public boolean isDepartAlarm() {
        return departAlarm;
    }

    public int getNeed_hour() {
        return need_hour;
    }

    public int getNeed_minute() {
        return need_minute;
    }

    public int getNeed_second() {
        return need_second;
    }

    public String getAlarm() {
        return alarm;
    }

    public int getMeans() {
        return means;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public int compareTo(Object o) {
        return this.begin_ms.compareTo(((Schedule)o).begin_ms);
    }

    @NonNull
    @Override
    public String toString() {
        String string = "key : "+key+", id : "+ serverId +
                ", title : "+title+
                ", begin_loc : "+begin_loc+
                "\n, begin_Lat : "+ begin_lat +", begin_Lng : "+ begin_lng +
                ", end_loc : "+end_loc+
                "\n, end_Lat : "+ end_lat +", end_Lng : "+ end_lng +
                ", begin_ms : "+ begin_ms +
                ", end_ms : "+ end_ms +
                "\n, memo : "+memo;
        return string;
    }

}
