package com.zzammo.calendar.custom_calendar.teest.data;

import java.util.ArrayList;

public class PageData {
    Long month;
    ArrayList<CalendarDate> days;

    public PageData() {
    }

    public PageData(Long month, ArrayList<CalendarDate> days) {
        this.month = month;
        this.days = days;
    }

    public Long getMonth() {
        return month;
    }

    public ArrayList<CalendarDate> getDays() {
        return days;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public void setDays(ArrayList<CalendarDate> days) {
        this.days = days;
    }
}
