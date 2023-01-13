package com.zzammo.calendar.custom_calendar.ui.viewmodel;
import java.util.Calendar;
import androidx.lifecycle.ViewModel;
import com.zzammo.calendar.custom_calendar.data.TSLiveData;
public class CalendarViewModel extends ViewModel {
    public TSLiveData<Calendar> mCalendar = new TSLiveData<>();

    public void setCalendar(Calendar calendar) {
        this.mCalendar.setValue(calendar);
    }

}