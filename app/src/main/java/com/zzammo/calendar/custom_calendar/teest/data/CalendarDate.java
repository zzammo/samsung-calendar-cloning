package com.zzammo.calendar.custom_calendar.teest.data;

import com.zzammo.calendar.database.Holiday;
import com.zzammo.calendar.database.Schedule;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarDate {
    public Calendar calendar;
    public ArrayList<Schedule> schedules;
    public ArrayList<Holiday> holidays;

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

    public ArrayList<Holiday> getHolidays() {
        return holidays;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public void setHolidays(ArrayList<Holiday> holidays) {
        this.holidays = holidays;
    }
}
