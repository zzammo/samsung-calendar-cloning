package com.zzammo.calendar.custom_calendar.teest.data;

import java.util.ArrayList;
import java.util.Calendar;

public class PageData {
    Calendar month;
    ArrayList<CalendarDate> days;

    public PageData() {
    }

    public PageData(Calendar month, ArrayList<CalendarDate> days) {
        this.month = month;
        this.days = days;
    }

    public Calendar getMonth() {
        return month;
    }

    public ArrayList<CalendarDate> getDays() {
        return days;
    }

    public void setMonth(Calendar month) {
        this.month = month;
    }

    public void setDays(ArrayList<CalendarDate> days) {
        this.days = days;
    }
}
