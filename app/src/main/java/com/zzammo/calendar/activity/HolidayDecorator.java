package com.zzammo.calendar.activity;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class HolidayDecorator implements DayViewDecorator {

    private Calendar calendar = Calendar.getInstance();
    private ArrayList<CalendarDay> holiDates;
    private ArrayList<String> holiNames;

    public HolidayDecorator(ArrayList<CalendarDay> holiDates, ArrayList<String> holiNames) {
        this.holiDates = holiDates; this.holiNames = holiNames;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return holiDates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}