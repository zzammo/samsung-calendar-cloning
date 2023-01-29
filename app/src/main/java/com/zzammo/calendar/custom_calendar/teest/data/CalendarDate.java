package com.zzammo.calendar.custom_calendar.teest.data;

import com.zzammo.calendar.database.Schedule;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarDate {
    public Calendar calendar;
    public ArrayList<Schedule> schedules;

    public CalendarDate() {
    }

    public CalendarDate(Calendar calendar) {
        this.calendar = calendar;
    }

    public CalendarDate(Calendar calendar, ArrayList<Schedule> schedules) {
        this.calendar = calendar;
        this.schedules = schedules;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }
}
