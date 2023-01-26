package com.zzammo.calendar.custom_calendar.bindingAdapter;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.zzammo.calendar.custom_calendar.utils.DateFormat;
import com.zzammo.calendar.database.Schedule;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TextBindingAdapter {
    @BindingAdapter({"setCalendarHeaderText"})
    public static void setCalendarHeaderText(TextView view, Long date) {
        try {
            if (date != null) {
                view.setText(DateFormat.getDate(date, DateFormat.CALENDAR_HEADER_FORMAT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"setScheduleText"})
    public static void setScheduleText(TextView view, Schedule x){
        try {
            if (x != null) {
                view.setText(x.title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"setDayText"})
    public static void setDayText(TextView view, Calendar calendar) {
        try {
            if (calendar != null) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                view.setText(DateFormat.getDate(gregorianCalendar.getTimeInMillis(), DateFormat.DAY_FORMAT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}