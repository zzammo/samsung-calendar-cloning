package com.zzammo.calendar.custom_calendar.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.zzammo.calendar.custom_calendar.data.TSLiveData;
import com.zzammo.calendar.database.Schedule;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarViewModel extends ViewModel {
    public TSLiveData<Calendar> mCalendar = new TSLiveData<>();
    public TSLiveData<ArrayList<Schedule>> mSchedule = new TSLiveData<>();
    public void setCalendar(Calendar calendar) {
        this.mCalendar.setValue(calendar);
        
    }
    public void setShedule(ArrayList<Schedule> schedule){this.mSchedule.setValue(schedule);}
}
