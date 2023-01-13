package com.zzammo.calendar.custom_calendar.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.zzammo.calendar.custom_calendar.data.TSLiveData;

public class CalendarHeaderViewModel extends ViewModel {
    public TSLiveData<Long> mHeaderDate = new TSLiveData<>();

    public void setHeaderDate(long headerDate) {
        this.mHeaderDate.setValue(headerDate);
    }
}
