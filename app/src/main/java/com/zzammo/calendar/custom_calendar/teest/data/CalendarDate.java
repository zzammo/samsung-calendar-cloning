package com.zzammo.calendar.custom_calendar.teest.data;

import com.zzammo.calendar.database.Holiday;
import com.zzammo.calendar.database.Schedule;

import java.util.ArrayList;

public class CalendarDate {
    public Long date;
    public ArrayList<Schedule> schedules;
    public ArrayList<Holiday> holidays;
    public boolean thisMonth;

    public CalendarDate() {
    }

    public CalendarDate(Long date, boolean thisMonth) {
        this.date = date;
        this.thisMonth = thisMonth;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public ArrayList<Holiday> getHolidays() {
        return holidays;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public void setHolidays(ArrayList<Holiday> holidays) {
        this.holidays = holidays;
    }
}
